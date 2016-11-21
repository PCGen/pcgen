/*
 * PCGenSettings.java
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
 * Created on Apr 1, 2010, 8:19:31 PM
 */
package pcgen.system;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

import pcgen.output.publish.OutputDB;

/**
 * This stores some of the properties that pcgen uses.
 * This class is mainly intended to be used to store non-ui related
 * properties
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public final class PCGenSettings extends PropertyContext
{

	private static final PCGenSettings instance = new PCGenSettings();
	/**
	 * This is the PropertyContext for the pcgen options, all keys that are used with
	 * this context have a key name starting with 'OPTION'
	 */
	public static final PropertyContext OPTIONS_CONTEXT = instance.createChildContext("pcgen.options");
	public static final String OPTION_SAVE_CUSTOM_EQUIPMENT = "saveCustomInLst";
	public static final String OPTION_ALLOWED_IN_SOURCES = "optionAllowedInSources";
	public static final String OPTION_SOURCES_ALLOW_MULTI_LINE = "optionSourcesAllowMultiLine";
	public static final String OPTION_SHOW_LICENSE = "showLicense";
	public static final String OPTION_SHOW_MATURE_ON_LOAD = "showMatureOnLoad";
	public static final String OPTION_SHOW_SPONSORS_ON_LOAD = "showSponsorsOnLoad";
	public static final String OPTION_CREATE_PCG_BACKUP = "createPcgBackup";
	public static final String OPTION_SHOW_HP_DIALOG_AT_LEVELUP = "showHPDialogAtLevelUp";
	public static final String OPTION_SHOW_STAT_DIALOG_AT_LEVELUP = "showStatDialogAtLevelUp";
	public static final String OPTION_SHOW_WARNING_AT_FIRST_LEVEL_UP = "showWarningAtFirstLevelUp";
	public static final String OPTION_AUTO_RESIZE_EQUIP = "autoResizeEquip";
	public static final String OPTION_SHOW_SKILL_MOD_BREAKDOWN = "showSkillModBreakdown";
	public static final String OPTION_SHOW_SKILL_RANK_BREAKDOWN = "showSkillRankBreakdown";
	public static final String OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS = "showOutputNameForOtherItems";
	public static final String OPTION_AUTOLOAD_SOURCES_AT_START = "autoloadSourcesAtStart";
	public static final String OPTION_AUTOLOAD_SOURCES_WITH_PC = "autoloadSourcesWithPC";
	public static final String OPTION_AUTOCREATE_MW_MAGIC_EQUIP = "autoCreateMagicMwEquip";
	public static final String OPTION_ALLOW_OVERRIDE_DUPLICATES = "allowOverrideDuplicates";
	public static final String OPTION_SKILL_FILTER = "skillsOutputFilter";
	public static final String OPTION_GENERATE_TEMP_FILE_WITH_PDF = "generateTempFileWithPdf";
	public static final String BROWSER_PATH = "browserPath";
	/**
	 * The key for the path to the character files.
	 */
	public static final String PCG_SAVE_PATH = "pcgen.files.characters";
	public static final String PCP_SAVE_PATH = "pcgen.files.parties";
	public static final String CHAR_PORTRAITS_PATH = "pcgen.files.portaits";
	public static final String BACKUP_PCG_PATH = "pcgen.files.characters.backup";
	public static final String SELECTED_SPELL_SHEET_PATH = "pcgen.files.selectedSpellOutputSheet";
	public static final String RECENT_CHARACTERS = "recentCharacters";
	public static final String RECENT_PARTIES = "recentParties";
	public static final String LAST_LOADED_SOURCES = "lastLoadedSources";
	public static final String LAST_LOADED_GAME = "lastLoadedGame";
	public static final String PAPERSIZE = "papersize";

	public static final String VENDOR_DATA_DIR = "pcgen.files.vendordataPath";
	public static final String HOMEBREW_DATA_DIR = "pcgen.files.homebrewdataPath";
	public static final String CUSTOM_DATA_DIR = "pcgen.files.customPath";
	
	/* Data converter saved choices. */
	public static final String CONVERT_OUTPUT_SAVE_PATH = "pcgen.convert.outputPath";
	public static final String CONVERT_INPUT_PATH = "pcgen.convert.inputPath";
	public static final String CONVERT_GAMEMODE = "pcgen.convert.gamemode";
	public static final String CONVERT_SOURCES = "pcgen.convert.sources";
	public static final String CONVERT_DATA_LOG_FILE = "pcgen.convert.dataLogFile";
	
	public static final PropertyContext GMGEN_OPTIONS_CONTEXT = instance.createChildContext("gmgen.options");
	
	private PCGenSettings()
	{
		super("options.ini");
		setProperty(PCG_SAVE_PATH,
					(ConfigurationSettings.getUserDir() + "/characters").replace('/',
																				 File.separatorChar));
		setProperty(PCP_SAVE_PATH,
					(ConfigurationSettings.getUserDir() + "/characters").replace('/',
																				 File.separatorChar));
		setProperty(CHAR_PORTRAITS_PATH,
					(ConfigurationSettings.getUserDir() + "/characters").replace('/',
																				 File.separatorChar));
		setProperty(BACKUP_PCG_PATH,
					(ConfigurationSettings.getUserDir() + "/characters").replace('/',
																				 File.separatorChar));
		setProperty(VENDOR_DATA_DIR, "@vendordata");
		setProperty(HOMEBREW_DATA_DIR, "@homebrewdata");
		setProperty(CUSTOM_DATA_DIR, "@data/customsources".replace('/', File.separatorChar));
		OutputDB.registerBooleanPreference(
			OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS, false);
	}

	@Override
	protected void beforePropertiesSaved()
	{
		relativize(VENDOR_DATA_DIR);
		relativize(HOMEBREW_DATA_DIR);
		relativize(CUSTOM_DATA_DIR);
	}

	public static PCGenSettings getInstance()
	{
		return instance;
	}

	public static String getSelectedSpellSheet()
	{
		return getInstance().getProperty(SELECTED_SPELL_SHEET_PATH);
	}

	public static String getPcgDir()
	{
		return getInstance().getProperty(PCG_SAVE_PATH);
	}

	public static String getPortraitsDir()
	{
		return getInstance().getProperty(CHAR_PORTRAITS_PATH);
	}

	public static String getBackupPcgDir()
	{
		return getInstance().getProperty(BACKUP_PCG_PATH);
	}

	public static String getBrowserPath()
	{
		return OPTIONS_CONTEXT.getProperty(BROWSER_PATH);
	}

	public static boolean getCreatePcgBackup()
	{
		return OPTIONS_CONTEXT.initBoolean(
				PCGenSettings.OPTION_CREATE_PCG_BACKUP, true);
	}

	public static String getVendorDataDir()
	{
		return getDirectory(VENDOR_DATA_DIR);
	}

	public static String getHomebrewDataDir()
	{
		return getDirectory(HOMEBREW_DATA_DIR);
	}

	public static String getCustomDir()
	{
		return getDirectory(CUSTOM_DATA_DIR);
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
		return expandRelativePath(getSystemProperty(key));
	}

	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@"))
		{
			path = SystemUtils.USER_DIR + File.separator + path.substring(1);
		}
		return path;
	}

	private static String unexpandRelativePath(String path)
	{
		if (path.startsWith(SystemUtils.USER_DIR + File.separator))
		{
			path = "@" + path.substring(SystemUtils.USER_DIR.length() + 1);
		}
		return path;
	}

	private static void relativize(String property)
	{
		setSystemProperty(property, unexpandRelativePath(getSystemProperty(property)));
	}

}
