/*
 * PropertyContextFactory.java
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
 * Created on Apr 4, 2010, 6:34:29 PM
 */
package pcgen.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PropertyContextFactory
{

	private static PropertyContextFactory DEFAULT_FACTORY;
	private final Map<String, PropertyContext> contextMap = new HashMap<String, PropertyContext>();
	private final String dir;

	PropertyContextFactory(String dir)
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

	void registerAndLoadPropertyContext(PropertyContext context)
	{
		registerPropertyContext(context);
		String filePath;
		if (dir == null)
		{
			filePath = ConfigurationSettings.getSettingsDir();
		}
		else
		{
			filePath = dir;
		}
		loadPropertyContext(new File(filePath, context.getName()));
	}

	private void loadPropertyContext(File file)
	{
		String name = file.getName();
		if (!file.exists())
		{
			Logging.debugPrint("No " + name + " file found, will create one when exiting.");
			return;
		}
		else if (!file.canWrite())
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
		FileInputStream in = null;
		boolean loaded = false;
		try
		{
			in = new FileInputStream(file);
			context.properties.load(in);
			loaded = true;
			context.afterPropertiesLoaded();
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Error occured while reading properties", ex);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint("Failed to close input stream for file: " + context.getName(), ex); //$NON-NLS-1$
			}
		}
		if (!loaded)
		{
			Logging.errorPrint(
					"Failed to load " + name + ", either the file is unreadable or it "
					+ "is corrupt. Possible solution is to delete the " + name
					+ " file and restart PCGen");
		}
	}

	void loadPropertyContexts()
	{
		File settingsDir;
		if (dir == null)
		{
			settingsDir = new File(ConfigurationSettings.getSettingsDir());
		}
		else
		{
			settingsDir = new File(dir);
		}
		File[] files = settingsDir.listFiles();
		if (files == null)
		{
			return;
		}
		for (File file : files)
		{
			if (!file.isDirectory())
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
		FileOutputStream out = null;
		try
		{
			context.beforePropertiesSaved();
			out = new FileOutputStream(file);
			context.properties.store(out, null);
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Error occured while storing properties", ex);
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint("Failed to close output stream for file: " + context.getName(), ex); //$NON-NLS-1$
			}
		}
	}

	void savePropertyContexts()
	{
		File settingsDir;
		if (dir == null)
		{
			settingsDir = new File(ConfigurationSettings.getSettingsDir());
		}
		else
		{
			settingsDir = new File(dir);
		}
		if (settingsDir.exists() || settingsDir.mkdirs())
		{
			for (PropertyContext context : contextMap.values())
			{
				savePropertyContext(settingsDir, context);
			}
		}
		else
		{
			Logging.errorPrint("Could not create directory to save settings files");
		}
	}

	/**
	 * Retrieves the PropertyContext with the given name.
	 * If one is not found then a new one will be created
	 * and returned.
	 * @param fileName the name of the PropertyContext
	 * @return a PropertyContext with given name
	 */
	public PropertyContext getPropertyContext(String fileName)
	{
		PropertyContext context = contextMap.get(fileName);
		if (context == null)
		{
			context = new PropertyContext(fileName);
			contextMap.put(fileName, context);
		}
		return context;
	}

	void registerPropertyContext(PropertyContext context)
	{
		contextMap.put(context.getName(), context);
	}

}
