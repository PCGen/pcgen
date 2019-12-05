/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import pcgen.util.Logging;

public class PropertyContextFactory
{

    private static PropertyContextFactory DEFAULT_FACTORY;
    private final Map<String, PropertyContext> contextMap = new HashMap<>();
    private final String dir;

    public PropertyContextFactory(String dir)
    {
        this.dir = dir;
    }

    public static PropertyContextFactory getDefaultFactory()
    {
        return DEFAULT_FACTORY;
    }

    static void setDefaultFactory(String dir)
    {
        DEFAULT_FACTORY = new PropertyContextFactory(dir);
    }

    public void registerAndLoadPropertyContext(PropertyContext context)
    {
        registerPropertyContext(context);
        String filePath = (dir == null) ? ConfigurationSettings.getSettingsDir() : dir;
        loadPropertyContext(new File(filePath, context.getName()));
    }

    private void loadPropertyContext(File file)
    {
        String name = file.getName();
        if (!file.exists())
        {
            if (Logging.isDebugMode())
            {
                Logging.debugPrint("No " + name + " file found, will create one when exiting.");
            }
            return;
        } else if (!file.canWrite())
        {
            Logging.errorPrint("WARNING: The file you specified is not updatable. "
                    + "Settings changes will not be saved. File is " + file.getAbsolutePath());
        }

        PropertyContext context = contextMap.get(name);
        if (context == null)
        {
            context = new PropertyContext(name);
            contextMap.put(name, context);
        }
        boolean loaded = false;
        try (InputStream in = new FileInputStream(file))
        {
            context.properties.load(in);
            loaded = true;
            context.afterPropertiesLoaded();
        } catch (Exception ex)
        {
            Logging.errorPrint("Error occurred while reading properties", ex);
        }

        if (!loaded)
        {
            Logging.errorPrint("Failed to load " + name + ", either the file is unreadable or it "
                    + "is corrupt. Possible solution is to delete the " + name + " file and restart PCGen");
        }
    }

    void loadPropertyContexts()
    {
        File settingsDir = new File(dir == null ? ConfigurationSettings.getSettingsDir() : dir);
        File[] files = settingsDir.listFiles();
        if (files == null)
        {
            return;
        }
        for (final File file : files)
        {
            if (!file.isDirectory() && file.getName().endsWith(".ini")) //$NON-NLS-1$
            {
                loadPropertyContext(file);
            }
        }
    }

    private void savePropertyContext(File settingsDir, PropertyContext context)
    {
        File file = new File(settingsDir, context.getName());
        if (file.exists() && !file.canWrite())
        {
            Logging.errorPrint("WARNING: Could not update settings file: " + file.getAbsolutePath());
            return;
        }
        try (OutputStream out = new FileOutputStream(file))
        {
            context.beforePropertiesSaved();
            context.properties.store(out, null);
        } catch (IOException ex)
        {
            Logging.errorPrint("Error occurred while storing properties", ex);
        }
    }

    public void savePropertyContexts()
    {
        File settingsDir = new File((dir == null) ? ConfigurationSettings.getSettingsDir() : dir);
        if (settingsDir.exists() || settingsDir.mkdirs())
        {
            contextMap.values().forEach(context -> savePropertyContext(settingsDir, context));
        } else
        {
            Logging.errorPrint("Could not create directory to save settings files");
        }
    }

    void registerPropertyContext(PropertyContext context)
    {
        contextMap.put(context.getName(), context);
    }

}
