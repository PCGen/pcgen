/*
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
 */
package pcgen.pluginmgr;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pcgen.base.lang.UnreachableError;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

public final class PluginManager implements pcgen.system.PluginLoader
{

	private static PluginManager instance;
	private final Map<InteractivePlugin, Boolean> pluginMap;
	private final List<PluginInfo> infoList;
	private final MessageHandlerManager msgHandlerMgr;

	private PluginManager()
	{
		pluginMap = new TreeMap<>(PLUGIN_PRIORITY_SORTER);
		infoList = new ArrayList<>();
		msgHandlerMgr = new MessageHandlerManager();
	}

	public static synchronized PluginManager getInstance()
	{
		if (instance == null)
		{
			instance = new PluginManager();
		}
		return instance;
	}

	/**
	 * A Comparator to sort interactive plugins by their priority.
	 */
	public static final Comparator<InteractivePlugin> PLUGIN_PRIORITY_SORTER = new Comparator<InteractivePlugin>()
	{
		@Override
		public int compare(InteractivePlugin arg0, InteractivePlugin arg1)
		{
			return Integer.compare(arg0.getPriority(), arg1.getPriority());
		}
	};

	public List<PluginInfo> getPluginInfoList()
	{
		return new ArrayList<>(infoList);
	}

	public void startAllPlugins()
	{
		PCGenMessageHandler dispatcher = msgHandlerMgr.getPostbox();
		for (InteractivePlugin plugin : pluginMap.keySet())
		{
			if (pluginMap.get(plugin))
			{
				plugin.start(dispatcher);
				msgHandlerMgr.addMember(plugin);
			}
		}
	}

	private String getLogName(Class<?> clazz, InteractivePlugin pl)
	{
		String logName = null;
		try
		{
			Field f = clazz.getField("LOG_NAME");
			logName = (String) f.get(pl);
		}
		catch (SecurityException e)
		{
			throw new UnreachableError("Access to Class " + clazz + " should not be prohibited", e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("Access to Method LOG_NAME in Class " + clazz + " should not be prohibited", e);
		}
		catch (NoSuchFieldException e)
		{
			Logging.errorPrint(
				clazz.getName() + " does not have LOG_NAME defined, " + "Plugin class implemented improperly");
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(clazz.getName() + " does not have LOG_NAME defined to "
				+ "take a Plugin as the argument, " + "Plugin class implemented improperly");
		}
		return logName;
	}

	@Override
	public void loadPlugin(Class<?> clazz) throws Exception
	{
		InteractivePlugin pl = (InteractivePlugin) clazz.newInstance();

		String logName = getLogName(clazz, pl);
		String plName = pl.getPluginName();

		boolean load = PCGenSettings.GMGEN_OPTIONS_CONTEXT.getBoolean(logName + ".Load", true);

		if ((logName == null) || (plName == null))
		{
			Logging.log(Logging.WARNING, "Plugin " + clazz.getCanonicalName() + " needs" + " 'name' property.");
		}
		else
		{
			infoList.add(new PluginInfo(logName, plName));
			pluginMap.put(pl, load);
		}
	}

	@Override
	public Class<?>[] getPluginClasses()
	{
		return new Class[]{InteractivePlugin.class};
	}

	/**
	 * Add a new handler to the list of message handlers for 
	 * GMGen and PCGen messages.
	 * @param handler The handler to be added.
	 */
	public void addMember(PCGenMessageHandler handler)
	{
		msgHandlerMgr.addMember(handler);
	}

	/**
	 * @return the postbox used to distribute messages.
	 */
	public PCGenMessageHandler getPostbox()
	{
		return msgHandlerMgr.getPostbox();
	}

	public static final class PluginInfo
	{

		public final String logName;
		public final String pluginName;

		private PluginInfo(String logName, String pluginName)
		{
			this.logName = logName;
			this.pluginName = pluginName;
		}

	}

	public static void clear()
	{
		instance = null;
	}
}
