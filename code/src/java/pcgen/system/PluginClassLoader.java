/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.system;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipFile;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

class PluginClassLoader extends PCGenTask
{
	private final File pluginDir;
	private final MapToList<Class<?>, PluginLoader> loaderMap;
	private final ExecutorService dispatcher = Executors.newSingleThreadExecutor(
			runnable -> Thread.ofPlatform().name("Plugin-loading-thread").daemon().unstarted(runnable));
	private final LinkedList<File> jarFiles = new LinkedList<>();
	private int progress = 0;

	PluginClassLoader(File pluginDir)
	{
		this.loaderMap = new HashMapToList<>();
		this.pluginDir = pluginDir;
	}

	@Override
	public String getMessage()
	{
		return LanguageBundle.getString("in_taskLoadPlugins"); //$NON-NLS-1$
	}

	void addPluginLoader(PluginLoader loader)
	{
		for (final Class<?> clazz : loader.getPluginClasses())
		{
			loaderMap.addToListFor(clazz, loader);
		}
	}

	private void loadClasses(final File pluginJar) throws IOException
	{
		try (JarClassLoader loader = new JarClassLoader(pluginJar.toURI().toURL());
				ZipFile file = new ZipFile(pluginJar))
		{
			var jarClasses = file.stream()
					.filter(entry -> entry.getName().endsWith(".class"));
			final Collection<String> classList = new LinkedList<>();
			jarClasses.forEach(entry -> {
				String name = StringUtils.removeEnd(entry.getName(), ".class").replace('/', '.');


				try (var in = file.getInputStream(entry))
				{
					byte[] buffer = in.readAllBytes();
					loader.storeClassDef(name, buffer);
					classList.add(name);
				} catch (IOException e)
				{
					Logging.errorPrint("Error occurred while extracting a class file " + name + " from JAR: " + pluginJar, e);
				}
			});

			/*
			 * Loading files and loading classes can both be lengthy processes. This splits the tasks
			 * so that class loading occurs in another thread, thus allowing both processes to
			 * operate at the same time.
			 */
			dispatcher.execute(() ->
			{
				boolean pluginFound = false;
				for (final String string : classList)
				{
					try
					{
						pluginFound |= processClass(Class.forName(string, true, loader));
					}
					catch (ClassNotFoundException | NoClassDefFoundError ex)
					{
						Logging.errorPrint("Error occurred while loading plugin: " + pluginJar.getName(), ex);
					}
				}
				if (!pluginFound)
				{
					Logging.log(Logging.WARNING, "Plugin not found in " + pluginJar.getName());
				}
				progress++;
				setProgress(progress);
			});
		}
	}

	private boolean processClass(Class<?> clazz)
	{
		boolean loaded = false;
		for (final Class<?> key : loaderMap.getKeySet())
		{
			if ((key != null) && !key.isAssignableFrom(clazz))
			{
				continue;
			}
			for (final PluginLoader loader : loaderMap.getListFor(key))
			{
				try
				{
					loader.loadPlugin(clazz);
				}
				catch (final Exception ex)
				{
					Logging.errorPrint("Error occurred while loading a plugin class: " + clazz.getName(), ex);
				}
				finally
				{
					loaded = true;
				}
			}
		}
		return loaded;
	}

	@Override
	public void run()
	{
		loadPlugins();
	}

	public void loadPlugins()
	{
		findJarFiles(pluginDir);
		setMaximum(jarFiles.size());
		loadClasses();
		Future<?> future = dispatcher.submit(dispatcher::shutdown);
		try
		{
			// This is done to cause this thread to wait until the shutdown task has been executed.
			future.get();
		}
		catch (ExecutionException | InterruptedException ex)
		{
			Logging.errorPrint("Exception during shutdown", ex);
		}
	}

	private void findJarFiles(File pluginDir)
	{
		if (!pluginDir.isDirectory())
		{
			return;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginDir.toPath(), "*plugins.jar"))
		{
			stream.forEach(path -> jarFiles.add(path.toFile()));
		}
		catch (IOException e)
		{
			Logging.errorPrint("Couldn't process the ./plugins/ folder.", e);
		}
	}

	private void loadClasses()
	{
		jarFiles.forEach(file -> {
			try
			{
				loadClasses(file);
			}
			catch (final IOException ex)
			{
				Logging.errorPrint("Could not load classes from a file: " + file.getAbsolutePath(), ex);
			}
		});
		jarFiles.clear();
	}

	private static final class JarClassLoader extends URLClassLoader
	{
		private final Map<String, byte[]> classDefinitions = new HashMap<>();

		private JarClassLoader(URL url)
		{
			super(new URL[]{url});
		}

		private void storeClassDef(String name, byte[] bytes)
		{
			classDefinitions.put(name, bytes);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException
		{
			byte[] bytes = classDefinitions.remove(name);
			if (bytes == null)
			{
				throw new ClassNotFoundException("The class with the name cannot be found: " + name);
			}
			return defineClass(name, bytes, 0, bytes.length);
		}

	}

}
