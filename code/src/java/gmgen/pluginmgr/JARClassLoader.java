/*
 *  JARClassLoader.java - Loads classes from JAR files
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999, 2000, 2001, 2002
 *  Portions copyright (C) 1999 mike dillon
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;

import gmgen.GMGenSystem;
import gmgen.gui.PreferencesPluginsPanel;
import gmgen.util.MiscUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import pcgen.core.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 *  A class loader implementation that loads classes from JAR files.
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public class JARClassLoader extends ClassLoader
{
	// used to mark non-existent classes in class hash
	private static final Object NO_CLASS = new Object();
	private static Hashtable classHash = new Hashtable();
	private Plugin.JAR jar;
	private Vector pluginClasses = new Vector();
	private ZipFile zipFile;

	/**
	 *  This constructor creates a class loader for loading classes from all
	 *  plugins. For example BeanShell uses one of these so that scripts can use
	 *  plugin classes.
	 *@since        GMGen 3.3
	 */
	public JARClassLoader()
	{
	    // Empty Constructor
	}

	/**
	 *  Constructor for the JARClassLoader object
	 *
	 *@param  path             path to the jar file
	 *@exception  IOException  Exception for not being able to read the file
	 *@since        GMGen 3.3
	 */
	public JARClassLoader(String path) throws IOException
	{
		zipFile = new ZipFile(path);
		jar = new Plugin.JAR(path, this);

		Enumeration entries = zipFile.entries();

		while (entries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String name = entry.getName();
			String lname = name.toLowerCase();

			if (lname.endsWith(".props") || lname.endsWith(".properties"))
			{
				//MiscUtilities.loadProps(zipFile.getInputStream(entry));
			}
			else if (name.endsWith(".class"))
			{
				classHash.put(MiscUtilities.fileToClass(name), this);

				//if (name.endsWith("Plugin.class") || name.endsWith("Token.class") || name.endsWith("Lst.class"))
				//{
				pluginClasses.addElement(name);
				//}
			}
		}

		PluginLoader.addPluginJAR(jar);
	}

	/**
	 *  Returns the ZIP file associated with this class loader.
	 *
	 *@return    The Zip File
	 *@since        GMGen 3.3
	 */
	public ZipFile getZipFile()
	{
		return zipFile;
	}

	/**
	 *  Closes the ZIP file. This plugin will no longer be usable after this.
	 *@since        GMGen 3.3
	 */
	public void closeZipFile()
	{
		if (zipFile == null)
		{
			return;
		}

		try
		{
			zipFile.close();
		}
		catch (IOException io)
		{
			Logging.errorPrint(io.getMessage(), io);
		}

		zipFile = null;
	}

	/**
	 *  loads a class
	 *
	 *@param  clazz                       class name
	 *@param  resolveIt                   weather to link the class
	 *@return                             teh class that has been loaded
	 *@exception  ClassNotFoundException  Didn't find the class we were looking for
	 *@since        GMGen 3.3
	 */
	public Class loadClass(String clazz, boolean resolveIt)
		throws ClassNotFoundException
	{
		// see what JARClassLoader this class is in
		Object obj = classHash.get(clazz);

		if (obj == NO_CLASS)
		{
			// we remember which classes we don't exist
			// because BeanShell tries loading all possible
			// <imported prefix>.<class name> combinations
			throw new ClassNotFoundException(clazz);
		}
		else if (obj instanceof ClassLoader)
		{
			JARClassLoader classLoader = (JARClassLoader) obj;

			return classLoader._loadClass(clazz, resolveIt);
		}

		// if it's not in the class hash, and not marked as
		// non-existent, try loading it from the CLASSPATH
		try
		{
			Class cls;

			/*
			 *  Defer to whoever loaded us (such as JShell,
			 *  Echidna, etc)
			 */
			ClassLoader parentLoader = getClass().getClassLoader();

			if (parentLoader != null)
			{
				cls = parentLoader.loadClass(clazz);
			}
			else
			{
				cls = findSystemClass(clazz);
			}

			return cls;
		}
		catch (ClassNotFoundException cnf)
		{
			// remember that this class doesn't exist for
			// future reference
			classHash.put(clazz, NO_CLASS);

			throw cnf;
		}
	}

	/**
	 *  Starts all the plugins in this JARClassLoader object
	 * @param system
	 *@since        GMGen 3.3
	 */
	public void startAllPlugins(String system)
	{
		Vector plugins = new Vector();
		for (int i = 0; i < pluginClasses.size(); i++)
		{
			String name = (String) pluginClasses.elementAt(i);
			name = MiscUtilities.fileToClass(name);

			try
			{
				Plugin pl = loadPluginClass(name, system);
				if(pl != null)
				{
					plugins.add(pl);
				}
			}
			catch (Throwable t)
			{
				Logging.errorPrint("Error while starting plugin " + t.getMessage(), t);

				jar.addPlugin(new Plugin.Broken(name));

				String[] args = { t.toString() };
				PluginLoader.pluginError(jar.getPath(), "plugin-error.start-error", args);
			}
		}
		Collections.sort(plugins, new Plugin.PluginComperator());
		for(int i = 0; i < plugins.size(); i++)
		{
			Plugin pl = (Plugin)plugins.get(i);
			Logging.debugPrint("Starting " + system + " plugin " + pl.getName() + " (version "
					+ MiscUtilities.buildToVersion(pl.getVersion()) + ")");
			jar.addPlugin(pl);
		}
	}

	private Class _loadClass(String clazz, boolean resolveIt)
		throws ClassNotFoundException
	{
		Class cls = findLoadedClass(clazz);

		if (cls != null)
		{
			if (resolveIt)
			{
				resolveClass(cls);
			}

			return cls;
		}

		String name = MiscUtilities.classToFile(clazz);

		try
		{
			ZipEntry entry = zipFile.getEntry(name);

			if (entry == null)
			{
				throw new ClassNotFoundException(clazz);
			}

			InputStream in = zipFile.getInputStream(entry);

			int len = (int) entry.getSize();
			byte[] data = new byte[len];
			int success = 0;
			int offset = 0;

			while (success < len)
			{
				len -= success;
				offset += success;
				success = in.read(data, offset, len);

				if (success == -1)
				{
					Logging.debugPrint("Failed to load class " + clazz + " from " + zipFile.getName());
					throw new ClassNotFoundException(clazz);
				}
			}

			cls = defineClass(clazz, data, 0, data.length);

			if (resolveIt)
			{
				resolveClass(cls);
			}

			return cls;
		}
		catch (IOException io)
		{
			Logging.errorPrint(io.getMessage(), io);
			throw new ClassNotFoundException(clazz);
		}
	}

	private boolean checkDependencies(String name)
	{
		int i = 0;

		String dep;

		while ((dep = SettingsHandler.getGMGenOption("plugin." + name + ".depend." + i++, null)) != null)
		{
			int index = dep.indexOf(' ');

			if (index == -1)
			{
				Logging.debugPrint(name + " has an invalid" + " dependency: " + dep);

				return false;
			}

			String what = dep.substring(0, index);
			String arg = dep.substring(index + 1);

			if (what.equals("jdk"))
			{
				if (MiscUtilities.compareStrings(System.getProperty("java.version"), arg, false) < 0)
				{
					String[] args = { arg, System.getProperty("java.version") };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-jdk", args);

					return false;
				}
			}
			else if (what.equals("gmgen"))
			{
				if (arg.length() != 11)
				{
					Logging.debugPrint("Invalid GMGen version" + " number: " + arg);

					return false;
				}

				if (MiscUtilities.compareStrings(GMGenSystem.getBuild(), arg, false) < 0)
				{
					String needs = MiscUtilities.buildToVersion(arg);
					String[] args = { needs, GMGenSystem.getVersion() };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-jedit", args);

					return false;
				}
			}
			else if (what.equals("plugin"))
			{
				int index2 = arg.indexOf(' ');

				if (index2 == -1)
				{
					Logging.debugPrint(name + " has an invalid dependency: " + dep + " (version is missing)");

					return false;
				}

				String plugin = arg.substring(0, index2);
				String needVersion = arg.substring(index2 + 1);
				String currVersion = SettingsHandler.getGMGenOption("plugin." + plugin + ".version", null);

				if (currVersion == null)
				{
					String[] args = { needVersion, plugin };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-plugin.no-version", args);

					return false;
				}

				if (MiscUtilities.compareStrings(currVersion, needVersion, false) < 0)
				{
					String[] args = { needVersion, plugin, currVersion };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-plugin", args);

					return false;
				}

				if (PluginLoader.getPlugin(plugin) instanceof Plugin.Broken)
				{
					String[] args = { plugin };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-plugin.broken", args);

					return false;
				}
			}
			else if (what.equals("class"))
			{
				try
				{
					loadClass(arg, false);
				}
				catch (Exception e)
				{
					String[] args = { arg };
					PluginLoader.pluginError(jar.getPath(), "plugin-error.dep-class", args);

					return false;
				}
			}
			else
			{
				Logging.debugPrint(name + " has unknown" + " dependency: " + dep);

				return false;
			}
		}

		return true;
	}

	private Plugin loadPluginClass(String name, String system) throws Exception
	{
		// Check if a plugin with the same name is already loaded
		Plugin[] plugins = PluginLoader.getPlugins();

		for (int i = 0; i < plugins.length; i++)
		{
			if (plugins[i].getClass().getName().equals(name))
			{
				PluginLoader.pluginError(jar.getPath(), "plugin-error.already-loaded", null);

				return null;
			}
		}

		// Check dependencies
		if (!checkDependencies(name))
		{
			jar.addPlugin(new Plugin.Broken(name));

			return null;
		}

		// JDK 1.1.8 throws a GPF when we do an isAssignableFrom()
		// on an unresolved class
		Class clazz = loadClass(name, true);
		int modifiers = clazz.getModifiers();

		if(system.equals(Constants.s_SYSTEM_TOKENS)) {
			loadOutputTokenClass(clazz, modifiers);
			loadLstTokens(clazz, modifiers);
			loadBonusTokens(clazz, name, modifiers);
			loadPreTokens(clazz, modifiers);
		}
		return loadPluginClass(clazz, modifiers, name, system);
	}

	private Plugin loadPluginClass(Class clazz, int modifiers, String name, String system) throws Exception {
		if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers) && Plugin.class.isAssignableFrom(clazz))
		{
			Plugin pl = (Plugin) clazz.newInstance();

			boolean load = true;
			if(!RequiredPlugin.class.isAssignableFrom(clazz)) {
				load = addPreferencesPanel(clazz, pl);
			}

			if(load && pl.getPluginSystem().equals(system))
			{
				if ((pl.getVersion() == null) || (pl.getName() == null))
				{
					Logging.debugPrint("Plugin " + name + " needs" + " 'name' and 'version' properties.");
					jar.addPlugin(new Plugin.Broken(name));

					return null;
				}
				return pl;
			}
		}
		return null;
	}

	private void loadOutputTokenClass(Class clazz, int modifiers) throws Exception {
		if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers) && Token.class.isAssignableFrom(clazz))
		{
			Token pl = (Token) clazz.newInstance();
			ExportHandler.addToTokenMap(pl);
		}
	}

	private void loadLstTokens(Class clazz, int modifiers) throws Exception
	{
		if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers) && LstToken.class.isAssignableFrom(clazz))
		{
			LstToken pl = (LstToken) clazz.newInstance();
			TokenStore.inst().addToTokenMap(pl);
		}
	}
	
	private void loadBonusTokens(Class clazz, String name, int modifiers) throws Exception
	{
		if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers) && BonusObj.class.isAssignableFrom(clazz))
		{
			Bonus.addBonusClass(clazz, name);
		}
	}
	
	private void loadPreTokens(Class clazz, int modifiers) throws Exception
	{
		if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers))
		{
			if(PrerequisiteParserInterface.class.isAssignableFrom(clazz)) {
				PrerequisiteParserInterface parser = (PrerequisiteParserInterface) clazz.newInstance();
				PreParserFactory.register(parser);
			}
			else if(PrerequisiteTest.class.isAssignableFrom(clazz)) {
				PrerequisiteTest test = (PrerequisiteTest) clazz.newInstance();
				PrerequisiteTestFactory.register(test);
			}
			else if(PrerequisiteWriterInterface.class.isAssignableFrom(clazz)) {
				PrerequisiteWriterInterface writer = (PrerequisiteWriterInterface) clazz.newInstance();
				PrerequisiteWriterFactory.register(writer);
			}
		}
	}

	private boolean addPreferencesPanel(Class clazz, Plugin pl) {
		boolean load = true;
		try {
			Field f = clazz.getField("LOG_NAME");
			String logName = (String)f.get(pl);
			String plName = pl.getName();
			String plSystem = pl.getPluginSystem();
			PreferencesPluginsPanel.addPanel(logName, plName, plSystem);
			load = SettingsHandler.getGMGenOption(logName + ".Load", true);
		}
		catch(Exception e) {
			System.out.println(clazz.getName() + " does not have LOG_NAME defined, Plugin class implemented improperly");
		}
		return load;
	}
}
