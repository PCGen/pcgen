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

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import pcgen.core.SettingsHandler;

/**
 *  The interface between GMGen and a plugin.  All plugins to be loaded should
 *  extend this class, and should be named *Plugin
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public abstract class Plugin
{

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
	 *  Gets the name attribute of the Plugin object.
	 *  This is used in the default implementation of {@link #getDataDir()}. 
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

	public static class PluginComperator implements Comparator<Plugin>, Serializable
	{
		/**
		 *  Description of the Method
		 *
		 *@param  p1  Object 1 to compare
		 *@param  p2  Object 2 to compare
		 *@return     the comparison between the two (in java.util.Comperator format)
		 */
        @Override
		public int compare(Plugin p1, Plugin p2)
		{
			Integer load1 = Integer.valueOf(p1.getPluginLoadOrder());
			Integer load2 = Integer.valueOf(p2.getPluginLoadOrder());
			return load2.compareTo(load1);
		}
	}
}
