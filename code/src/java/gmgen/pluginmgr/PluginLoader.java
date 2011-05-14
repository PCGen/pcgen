/*
 *  PluginLoader.java - Plugin infrastructure
 *  :noTabs=false:
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999, 2003
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

import gmgen.util.MiscUtilities;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.prereq.PreMultParser;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Loads plugins into gmgen.  looks dor any .jar files in ./plugins, and then
 *  searches their contents for any *Plugin.class files, which it attempts to load.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class PluginLoader
{
	private static List<Plugin.JAR> jars = new ArrayList<Plugin.JAR>();
	private static List<Plugin> pluginCache = null;
	private static List<ErrorListDialog.ErrorEntry> pluginErrors;

	private static PluginLoader inst;
	private static Map<String, String> loadedMap = new HashMap<String, String>();

	/**
	 *  Constructor for the PluginLoader object
	 *  Sets some properties, and creates some directories if they don't exist.
	 */
	protected PluginLoader()
	{
		if (SettingsHandler.getGmgenPluginDir().toString().equals(""))
		{
			SettingsHandler.setGmgenPluginDir(new File(Globals.getDefaultPath() + File.separator + "plugins"));
		}

		File _pluginDirectory = SettingsHandler.getGmgenPluginDir();

		if (!_pluginDirectory.exists())
		{
			_pluginDirectory.mkdirs();
		}

		loadPlugins(_pluginDirectory);
	}

	public static PluginLoader inst()
	{
		if(inst == null) {
			inst = new PluginLoader();
		}
		return inst;
	}

	public void startSystemPlugins(String system)
	{
		if(loadedMap.get(system) == null)
		{
			for ( Plugin.JAR jar : jars )
			{
				jar.getClassLoader().startAllPlugins(system);
			}
			loadedMap.put(system, system);
			if (system.equals(Constants.SYSTEM_TOKENS))
			{
				TokenLibrary.addToTokenMap(new PreMultParser());
			}
		}
	}

	/**
	 *  Returns the plugin with the specified class name.
	 *
	 *@param  name  Name of the class who's owner plugin you are looking for
	 *@return       The plugin
	 */
	public static Plugin getPlugin(String name)
	{
		for (Plugin pi : getPlugins())
		{
			if (pi.getClassName().equals(name))
			{
				return pi;
			}
		}

		return null;
	}

	/**
	 *  Returns an array of installed plugins.
	 *
	 *@return    The plugins
	 */
	public static List<Plugin> getPlugins()
	{
		if (pluginCache == null) {
			pluginCache = new ArrayList<Plugin>(500);

			for ( Plugin.JAR jar : jars )
			{
				jar.getPlugins(pluginCache);
			}
		}

		return pluginCache;
	}

	/**
	 *  Adds a plugin JAR to GMGen
	 *
	 *@param  plugin  The plugin
	 */
	public static void addPluginJAR(Plugin.JAR plugin)
	{
		jars.add(plugin);
		pluginCache = null;
	}

	static void pluginError(final String path, String messageProp, Object[] args)
	{
		if (pluginErrors == null)
		{
			pluginErrors = new ArrayList<ErrorListDialog.ErrorEntry>();
		}

		pluginErrors.add(new ErrorListDialog.ErrorEntry(path, messageProp, args));
	}

	/**
	 *  Loads all plugins in a directory.
	 *
	 *@param  directory  The directory
	 */
	private static void loadPlugins(File directory)
	{
		Logging.debugPrint("Loading plugins from " + directory);

		if (!(directory.exists() && directory.isDirectory()))
		{
			return;
		}

		String[] plugins = directory.list();

		if (plugins == null)
		{
			return;
		}

		//MiscUtilities.quicksort(plugins,new MiscUtilities.StringICaseCompare());
		for (int i = 0; i < plugins.length; i++)
		{
			String plugin = plugins[i];
			String path = MiscUtilities.constructPath(directory.toString(), plugin);
			File pluginFile = new File(path);

			if (plugin.indexOf("plugin") > -1 && pluginFile.isDirectory())
			{
				loadPlugins(pluginFile);
			}

			if (!plugin.regionMatches(true, plugin.length() - 4, ".jar", 0, 4))
			{
				continue;
			}

			try
			{
				Logging.debugPrint("Scanning JAR file: " + path);
				new JARClassLoader(path);
			}
			catch (IOException io)
			{
				Logging.errorPrint("Cannot load plugin " + plugin + ": " + io.getMessage(), io);

				String[] args = { io.toString() };
				pluginError(path, "plugin-error.load-error", args);
			}
		}
	}
}
