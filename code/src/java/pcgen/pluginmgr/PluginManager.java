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
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import pcgen.base.lang.UnreachableError;
import pcgen.util.Logging;

public final class PluginManager implements pcgen.system.PluginLoader
{

    private static PluginManager instance;
    private final MessageHandlerManager msgHandlerMgr;

    private PluginManager()
    {
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
    private static final Comparator<InteractivePlugin> PLUGIN_PRIORITY_SORTER =
            Comparator.comparingInt(InteractivePlugin::getPriority);

    private static String getLogName(Class<?> clazz, InteractivePlugin pl)
    {
        String logName = null;
        try
        {
            Field f = clazz.getField("LOG_NAME");
            logName = (String) f.get(pl);
        } catch (SecurityException e)
        {
            throw new UnreachableError("Access to Class " + clazz + " should not be prohibited", e);
        } catch (IllegalAccessException e)
        {
            throw new UnreachableError("Access to Method LOG_NAME in Class " + clazz + " should not be prohibited", e);
        } catch (NoSuchFieldException e)
        {
            Logging.errorPrint(
                    clazz.getName() + " does not have LOG_NAME defined, " + "Plugin class implemented improperly");
        } catch (IllegalArgumentException e)
        {
            Logging.errorPrint(clazz.getName() + " does not have LOG_NAME defined to "
                    + "take a Plugin as the argument, " + "Plugin class implemented improperly");
        }
        return logName;
    }

    @Override
    public void loadPlugin(Class<?> clazz)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException
    {
        InteractivePlugin pl = (InteractivePlugin) clazz.getConstructor().newInstance();

        String logName = getLogName(clazz, pl);
        String plName = pl.getPluginName();

        if ((logName == null) || (plName == null))
        {
            Logging.log(Logging.WARNING, "Plugin " + clazz.getCanonicalName() + " needs" + " 'name' property.");
        }
    }

    @Override
    public Class<?>[] getPluginClasses()
    {
        return new Class[]{InteractivePlugin.class};
    }

    /**
     * @return the postbox used to distribute messages.
     */
    public PCGenMessageHandler getPostbox()
    {
        return msgHandlerMgr.getPostbox();
    }

}
