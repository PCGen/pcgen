/*
 * PluginClassLoader.java
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
 * Created on Aug 25, 2009, 3:14:40 PM
 */
package pcgen.system;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang.StringUtils;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PluginClassLoader extends PCGenTask
{

	private static FilenameFilter pluginFilter = new FilenameFilter()
	{

		public boolean accept(File dir, String name)
		{
			if (name.indexOf("plugin") > -1)
			{
				return true;
			}
			return StringUtils.endsWithIgnoreCase(name, ".jar");
		}

	};
	private final File pluginDir;
	private final MapToList<Class<?>, PluginLoader> loaderMap;
	private ExecutorService dispatcher = Executors.newSingleThreadExecutor(new ThreadFactory()
	{

		public Thread newThread(Runnable r)
		{
			Thread thread = new Thread(r, "Plugin-loading-thread");
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY);
			return thread;
		}

	});
	private LinkedList<File> jarFiles = new LinkedList<File>();
	private int progress = 0;

	public PluginClassLoader(File pluginDir)
	{
		this.loaderMap = new HashMapToList<Class<?>, PluginLoader>();
		this.pluginDir = pluginDir;
	}

	@Override
	public String getMessage()
	{
		return LanguageBundle.getString("in_taskLoadPlugins"); //$NON-NLS-1$
	}

	public void addPluginLoader(PluginLoader loader)
	{
		for (Class<?> clazz : loader.getPluginClasses())
		{
			loaderMap.addToListFor(clazz, loader);
		}
	}

	private void loadClasses(final File pluginJar) throws IOException
	{
		final JarClassLoader loader = new JarClassLoader(pluginJar.toURI().toURL());
		final List<String> classList = new LinkedList<String>();
		ZipFile file = new ZipFile(pluginJar);
		Enumeration<? extends ZipEntry> entries = file.entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			if (!name.endsWith(".class"))
			{
				continue;
			}
			name = StringUtils.removeEnd(name, ".class").replace('/', '.');
			int size = (int) entry.getSize();
			byte[] buffer = new byte[size];

			InputStream in = file.getInputStream(entry);
			int rb = 0;
			int chunk = 0;
			while ((size - rb) > 0)
			{
				chunk = in.read(buffer, rb, size - rb);
				if (chunk == -1)
				{
					break;
				}
				rb += chunk;
			}
			in.close();
			loader.storeClassDef(name, buffer);
			classList.add(name);
		}
		/*
		 * Loading files and loading classes can both be lengthy processes. This splits the tasks
		 * so that class loading occurs in another thread thus allowing both processes to
		 * operate at the same time.
		 */
		dispatcher.execute(new Runnable()
		{

			public void run()
			{
				boolean pluginFound = false;
				for (String string : classList)
				{
					try
					{
						pluginFound |= processClass(Class.forName(string, true, loader));
					}
					catch (ClassNotFoundException ex)
					{
						Logging.errorPrint("Error occured while loading plugin: " +
								pluginJar.getName(), ex);
					}
					catch (NoClassDefFoundError e)
					{
						Logging.errorPrint("Error occured while loading plugin: " +
								pluginJar.getName(), e);
					}
				}
				if (!pluginFound)
				{
					Logging.log(Logging.WARNING, "Plugin not found in " + pluginJar.getName());
				}
				progress++;
				setProgress(progress);
			}

		});
	}

	private boolean processClass(Class<?> clazz)
	{
		int modifiers = clazz.getModifiers();
		if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers))
		{
			return false;
		}

		boolean loaded = false;
		for (Class<?> key : loaderMap.getKeySet())
		{
			if (key != null && !key.isAssignableFrom(clazz))
			{
				continue;
			}
			for (PluginLoader loader : loaderMap.getListFor(key))
			{
				try
				{
					loader.loadPlugin(clazz);
				}
				catch (Exception ex)
				{
					Logging.errorPrint("Error occured while loading plugin class: " +
							clazz.getName(), ex);
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
	public void execute()
	{
		loadPlugins();
	}

	public void loadPlugins()
	{
		findJarFiles(pluginDir);
		setMaximum(jarFiles.size());
		loadClasses();
		Future<?> future = dispatcher.submit(new Runnable()
		{

			public void run()
			{
				dispatcher.shutdown();
			}

		});
		try
		{
			//This is done to cause this thread to wait until the shutdown task
			//has been executed.
			future.get();
		}
		catch (ExecutionException ex)
		{
			//Do nothing
		}
		catch (InterruptedException ex)
		{
			//Do nothing
		}
	}

	private void findJarFiles(File pluginDir)
	{
		if (!pluginDir.isDirectory())
		{
			return;
		}
		File[] pluginFiles = pluginDir.listFiles(pluginFilter);
		for (File file : pluginFiles)
		{
			if (file.isDirectory())
			{
				findJarFiles(file);
				continue;
			}
			jarFiles.add(file);
		}
	}

	private void loadClasses()
	{
		while (!jarFiles.isEmpty())
		{
			File file = jarFiles.poll();
			try
			{
				loadClasses(file);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not load classes from file: " + file.getAbsolutePath(), ex);
			}
		}
	}

	private static class JarClassLoader extends URLClassLoader
	{

		private Map<String, byte[]> classDefinitions = new HashMap<String, byte[]>();

		public JarClassLoader(URL url) throws MalformedURLException
		{
			super(new URL[]
					{
						url
					});
		}

		public void storeClassDef(String name, byte[] bytes)
		{
			classDefinitions.put(name, bytes);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException
		{
			byte[] bytes = classDefinitions.remove(name);
			if (bytes == null)
			{
				throw new ClassNotFoundException();
			}
			return defineClass(name, bytes, 0, bytes.length);
		}

	}

}
