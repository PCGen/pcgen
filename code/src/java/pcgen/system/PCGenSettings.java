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

/**
 * This stores some of the properties that pcgen uses.
 * This class is mainly intended to be used to store non-ui related
 * properties
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenSettings extends PropertyContext
{

	private static final PCGenSettings instance = new PCGenSettings();
	/**
	 * This is the PropertyContext for the pcgen options, all keys that are used with
	 * this context have a key name starting with 'OPTION'
	 */
	public static final PropertyContext OPTIONS_CONTEXT = instance.createChildContext("pcgen.options");
	public static final String OPTION_SAVE_CUSTOM_EQUIPMENT = "saveCustomInLst";
	public static final String OPTION_ALLOWED_IN_SOURCES = "optionAllowedInSources";
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

}
