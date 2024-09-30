/*
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
 */
package pcgen.system;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public final class ConfigurationSettings extends PropertyContext
{
	private static final String USER_LANGUAGE = "language";
	private static final String USER_COUNTRY = "country";
	public static final String SETTINGS_FILES_PATH = "settingsPath";
	public static final String SYSTEMS_DIR = "systemsPath";
	public static final String OUTPUT_SHEETS_DIR = "osPath";
	private static final String PLUGINS_DIR = "pluginsPath";
	public static final String PREVIEW_DIR = "previewPath";
	public static final String VENDOR_DATA_DIR = "vendordataPath";
	public static final String HOMEBREW_DATA_DIR = "homebrewdataPath";
	public static final String DOCS_DIR = "docsPath";
	public static final String PCC_FILES_DIR = "pccFilesPath";
	public static final String CUSTOM_DATA_DIR = "customPath";
	private static ConfigurationSettings instance = null;
	/** APPLICATION directory name, used in <em>~/.&lt;APPLICATION&gt;</em>, etc. */
	private static final String APPLICATION = "pcgen"; // $NON-NLS-1$

	private ConfigurationSettings(String configFileName)
	{
		super(configFileName);
		//Initialize defaults
		setProperty(USER_LANGUAGE, SystemUtils.USER_LANGUAGE);
		setProperty(USER_COUNTRY, SystemUtils.USER_COUNTRY);
		setProperty(SYSTEMS_DIR, "@system");
		setProperty(OUTPUT_SHEETS_DIR, "@outputsheets");
		setProperty(PLUGINS_DIR, "@plugins");
		setProperty(PREVIEW_DIR, "@preview");
		setProperty(DOCS_DIR, "@docs");
		setProperty(PCC_FILES_DIR, "@data");
	}

	@Override
	protected void beforePropertiesSaved()
	{
		relativize(SYSTEMS_DIR);
		relativize(OUTPUT_SHEETS_DIR);
		relativize(PLUGINS_DIR);
		relativize(PREVIEW_DIR);
		relativize(DOCS_DIR);
		relativize(PCC_FILES_DIR);
	}

	public static String getLanguage()
	{
		return getSystemProperty(USER_LANGUAGE);
	}

	public static void setLanguage(String language)
	{
		setSystemProperty(USER_LANGUAGE, language);
	}

	static String getCountry()
	{
		return getSystemProperty(USER_COUNTRY);
	}

	public static void setCountry(String country)
	{
		setSystemProperty(USER_COUNTRY, country);
	}

	/**
	 * @return the current user directory
	 */
	public static String getUserDir()
	{
		return SystemUtils.USER_DIR;
	}

	public static ConfigurationSettings getInstance()
	{
		return getInstance(null);
	}

	public static ConfigurationSettings getInstance(String configFileName)
	{
		if (instance == null)
		{
			instance = new ConfigurationSettings(configFileName == null ? "config.ini" : configFileName);
		}
		return instance;
	}

	public static String getSystemsDir()
	{
		return getDirectory(SYSTEMS_DIR);
	}

	public static String getOutputSheetsDir()
	{
		return getDirectory(OUTPUT_SHEETS_DIR);
	}

	static String getPluginsDir()
	{
		return getDirectory(PLUGINS_DIR);
	}

	public static String getPreviewDir()
	{
		return getDirectory(PREVIEW_DIR);
	}

	public static String getDocsDir()
	{
		return getDirectory(DOCS_DIR);
	}

	public static String getPccFilesDir()
	{
		return getDirectory(PCC_FILES_DIR);
	}

	public static String getSettingsDir()
	{
		return getDirectory(SETTINGS_FILES_PATH);
	}

	public static String getSystemProperty(String key)
	{
		return getInstance().getProperty(key);
	}

	public static Object setSystemProperty(String key, String value)
	{
		return getInstance().setProperty(key, value);
	}

	private static String getDirectory(String key)
	{
		if (SETTINGS_FILES_PATH.equals(key))
		{
			return getSettingsDirFromFilePath(getSystemProperty(key));
		}
		return expandRelativePath(getSystemProperty(key));
	}

	private static String expandRelativePath(String path)
	{
		// TODO: a dirty hack for Mac bundles only
		if (SystemUtils.USER_DIR.endsWith("MacOS") && path.startsWith("@"))
		{
			path = SystemUtils.USER_DIR + File.separator + ".." + File.separator + "app" + File.separator + path.substring(1);
		}
		else if (path.startsWith("@"))
		{
			path = SystemUtils.USER_DIR + File.separator + path.substring(1);
		}
		return path;
	}

	private static String unexpandRelativePath(String path)
	{
		if (path.startsWith(SystemUtils.USER_DIR + File.separator))
		{
			path = '@' + path.substring(SystemUtils.USER_DIR.length() + 1);
		}
		return path;
	}

	private static void relativize(String property)
	{
		setSystemProperty(property, unexpandRelativePath(getSystemProperty(property)));
	}

	public enum SettingsFilesPath
	{

		/** User Directory */
		user,
		/** Indicates PCGen directory */
		pcgen,
		/** Freedesktop configuration directories */
		FD_USER,
		/** Indicate MAC specific directories */
		mac_user;

		public String getSettingsDir()
		{
			switch (this)
			{
				case user:
					return SystemUtils.USER_HOME + File.separator + '.' + APPLICATION; // $NON-NLS-1$
				case pcgen:
					return SystemUtils.USER_DIR + File.separator + "settings"; // $NON-NLS-1$
				case mac_user:
					return SystemUtils.USER_HOME + "/Library/Preferences/" + APPLICATION; // $NON-NLS-1$
				case FD_USER:
					String config = System.getenv("XDG_CONFIG_HOME"); // $NON-NLS-1$
					if ((config == null) || config.isEmpty())
					{
						config = SystemUtils.USER_HOME + File.separator + ".config"; // $NON-NLS-1$
					}
					return config + File.separator + APPLICATION;
				default:
					throw new InternalError();
			}
		}

	}

	public static String getSettingsDirFromFilePath(String fType)
	{
		if ((fType == null) || (fType.length() < 1))
		{
			// make sure we have a default
			fType = getDefaultSettingsFilesPath();
		}
		String path;
		try
		{
			//Check to see if this path is one of the standard path types
			path = SettingsFilesPath.valueOf(fType).getSettingsDir();
		}
		catch (IllegalArgumentException ex)
		{
			//It must be a custom filepath
			path = fType;
		}
		return path;
	}

	/**
	 * @return A default Settings Files Path value.
	 */
	public static String getDefaultSettingsFilesPath()
	{
		String fType;
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			fType = SettingsFilesPath.mac_user.name();
		}
		else if (SystemUtils.IS_OS_UNIX)
		{
			fType = SettingsFilesPath.FD_USER.name();
		}
		else
		{
			fType = SettingsFilesPath.user.name();
		}
		return fType;
	}

	/**
	 * @return "User Dir" dir Settings Files Path value.
	 */
	public static String getUserSettingsDirFromFilePath()
	{
		return getSettingsDirFromFilePath(getDefaultSettingsFilesPath());
	}

}
