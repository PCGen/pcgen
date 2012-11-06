/*
 * PluginManager.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Nov 5, 2011, 2:55:43 PM
 */
package gmgen.pluginmgr;

import gmgen.pluginmgr.Plugin.PluginComperator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pcgen.base.lang.UnreachableError;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PluginManager implements pcgen.system.PluginLoader
{

	private static PluginManager instance;
	private final Map<Plugin, Boolean> pluginMap;
	private final List<PluginInfo> infoList;

	private PluginManager()
	{
		pluginMap = new TreeMap<Plugin, Boolean>(new PluginComperator());
		infoList = new ArrayList<PluginInfo>();
	}

	public static PluginManager getInstance()
	{
		if (instance == null)
		{
			instance = new PluginManager();
		}
		return instance;
	}

	public List<PluginInfo> getPluginInfoList()
	{
		return new ArrayList<PluginInfo>(infoList);
	}

	public void startAllPlugins()
	{
		for(Plugin plugin : pluginMap.keySet())
		{
			if(pluginMap.get(plugin))
			{
				plugin.start();

				if (plugin instanceof GMBPlugin)
				{
					GMBus.addToBus((GMBPlugin) plugin);
				}
			}
		}
	}

	private String getLogName(Class<?> clazz, Plugin pl)
	{
		String logName = null;
		try
		{
			Field f = clazz.getField("LOG_NAME");
			logName = (String) f.get(pl);
		}
		catch (SecurityException e)
		{
			throw new UnreachableError("Access to Class " + clazz
					+ " should not be prohibited", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("Access to Method LOG_NAME in Class "
					+ clazz + " should not be prohibited", e);
		}
		catch (NoSuchFieldException e)
		{
			Logging.errorPrint(clazz.getName()
					+ " does not have LOG_NAME defined, "
					+ "Plugin class implemented improperly");
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(clazz.getName()
					+ " does not have LOG_NAME defined to "
					+ "take a Plugin as the argument, "
					+ "Plugin class implemented improperly");
		}
		return logName;
	}

    @Override
	public void loadPlugin(Class<?> clazz) throws Exception
	{
		Plugin pl = (Plugin) clazz.newInstance();

		String logName = getLogName(clazz, pl);
		String plName = pl.getName();
		String plSystem = pl.getPluginSystem();
		String plVersion = pl.getVersion();

		boolean required = RequiredPlugin.class.isAssignableFrom(clazz);
		boolean load = PCGenSettings.GMGEN_OPTIONS_CONTEXT.getBoolean(logName + ".Load", true);

		if ((logName == null || plVersion == null) || (plName == null))
		{
			Logging.log(Logging.WARNING, "Plugin " + clazz.getCanonicalName() + " needs"
					+ " 'name' and 'version' properties.");
			pluginMap.put(new Plugin.Broken(clazz.getCanonicalName()), Boolean.FALSE);
		}
		else
		{
			if (!required)
			{
				infoList.add(new PluginInfo(logName, plName, plSystem));
			}
			pluginMap.put(pl, load);
		}
	}

    @Override
	public Class[] getPluginClasses()
	{
		return new Class[]
				{
					Plugin.class
				};
	}

	public static class PluginInfo
	{

		public final String logName;
		public final String pluginName;
		public final String pluginSystem;

		private PluginInfo(String logName, String pluginName, String pluginSystem)
		{
			this.logName = logName;
			this.pluginName = pluginName;
			this.pluginSystem = pluginSystem;
		}

	}

}
