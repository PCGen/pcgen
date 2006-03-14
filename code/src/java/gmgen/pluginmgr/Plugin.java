/*
 *  Plugin.java - Abstract class all plugins must implement
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999, 2000
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

import pcgen.core.SettingsHandler;

import java.io.File;
import java.util.Comparator;
import java.util.Vector;

/**
 *  The interface between GMGen and a plugin.  All plugins to be loaded should
 *  extend this class, and should be named *Plugin
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public abstract class Plugin
{
	// private members
	private Plugin.JAR jar;

	/**
	 *  Returns the plugin's class name.
	 *
	 *@return    The Class Name
	 *@since        GMGen 3.3
	 */
	public String getClassName()
	{
		return getClass().getName();
	}

	/**
	 *  Gets the name of the data directory for Plugin object
	 *
	 *@return    The data directory name
	 *@since        GMGen 3.3
	 */
	public String getDataDir()
	{
		String pluginDirectory = SettingsHandler.getGmgenPluginDir().toString();

		return pluginDirectory + File.separator + getName();
	}

	/**
	 *  Returns the JAR file containing this plugin.
	 *
	 *@return    The JAR
	 *@since        GMGen 3.3
	 */
	public Plugin.JAR getJAR()
	{
		return jar;
	}

	/**
	 *  Gets the name attribute of the Plugin object
	 *
	 *@return    The name value
	 *@since        GMGen 3.3
	 */
	public String getName()
	{
		return null;
	}

	/**
	 *  Gets the version attribute of the Plugin object
	 *
	 *@return    The version
	 *@since        GMGen 3.3
	 */
	public String getVersion()
	{
		return null;
	}

	/**
	 *  Method called by GMGen to initialize the plugin. Any gui creation, and
	 *  properties should be set here. Any GMBus calls can be made here as well.
	 *@since        GMGen 3.3
	 */
	public void start()
	{
	    // TODO This method currently does nothing?
	}

	/**
	 *  Method called by GMGen before exiting. Usually, nothing needs to be done
	 *  here.
	 *@since        GMGen 3.3
	 */
	public void stop()
	{
	    // TODO This method currently does nothing?
	}

	public abstract String getPluginSystem();

	public abstract int getPluginLoadOrder();

	/**
	 *  A placeholder for a plugin that didn't load.
	 *
	 *@author     Soulcatcher
	 *@since        GMGen 3.3
	 */
	public static class Broken extends Plugin
	{
		// private members
		private String clazz;

		// package-private members
		Broken(String clazz)
		{
			this.clazz = clazz;
		}

		/**
		 *  Gets the className attribute of the Broken object
		 *
		 *@return    The className value
		 *@since        GMGen 3.3
		 */
		public String getClassName()
		{
			return clazz;
		}

		public String getPluginSystem()
		{
			return "Broken";
		}

		public int getPluginLoadOrder() {
			return 0;
		}
	}

	/**
	 *  A JAR file.
	 *
	 *@author     Soulcatcher
	 *@since        GMGen 3.3
	 */
	public static class JAR
	{
		private JARClassLoader classLoader;

		// private members
		private String path;
		private Vector plugins;

		/**
		 *  Constructor for the JAR object
		 *
		 *@param  path         path to the file
		 *@param  classLoader  classloader to use to load it
		 *@since        GMGen 3.3
		 */
		public JAR(String path, JARClassLoader classLoader)
		{
			this.path = path;
			this.classLoader = classLoader;
			plugins = new Vector();
		}

		/**
		 *  Gets the Class Loader used for the JAR object
		 *
		 *@return    The Class Loader
		 *@since        GMGen 3.3
		 */
		public JARClassLoader getClassLoader()
		{
			return classLoader;
		}

		/**
		 *  Returns the path of the JAR object
		 *
		 *@return    The path
		 *@since        GMGen 3.3
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 *  Gets all the plugins of the JAR
		 *
		 *@return    The plugins
		 *@since        GMGen 3.3
		 */
		public Plugin[] getPlugins()
		{
			Plugin[] array = new Plugin[plugins.size()];
			plugins.copyInto(array);

			return array;
		}

		/**
		 *  Adds all the plugins in this jar to a vector
		 *
		 *@param  vector  Vector to add all the plugins to.
		 *@since        GMGen 3.3
		 */
		public void getPlugins(Vector vector)
		{
			for (int i = 0; i < plugins.size(); i++)
			{
				vector.addElement(plugins.elementAt(i));
			}
		}

		/**
		 *  Adds a Plugin to the JAR object
		 *
		 *@param  plugin  The plugin to be added
		 *@since        GMGen 3.3
		 */
		public void addPlugin(Plugin plugin)
		{
			plugin.jar = JAR.this;

			// must be before the below two so that if an error
			// occurs during start, the plugin is not listed as
			// being active
			plugin.start();

			if (plugin instanceof GMBPlugin)
			{
				GMBus.addToBus((GMBPlugin) plugin);
			}

			plugins.addElement(plugin);
		}
	}

	public static class PluginComperator implements Comparator
	{
		/**
		 *  Description of the Method
		 *
		 *@param  o1  Object 1 to compare
		 *@param  o2  Object 2 to compare
		 *@return     the comparion between the two (in java.util.Comperator format)
		 */
		public int compare(Object o1, Object o2)
		{
			Plugin p1 = (Plugin) o1;
			Plugin p2 = (Plugin) o2;
			Integer load1 = new Integer(p1.getPluginLoadOrder());
			Integer load2 = new Integer(p2.getPluginLoadOrder());
			System.out.println(p1.getName() + ": " + p1.getPluginLoadOrder() + " - " + p2.getName() + ": " + p2.getPluginLoadOrder());

			return load2.compareTo(load1);
		}
	}
}
