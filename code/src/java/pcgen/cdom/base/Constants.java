/*
 * Constants.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * $Id$
 */
package pcgen.cdom.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * This interface holds all global constants.
 *
 * (The reason for an interface rather than a class
 * is that an interface uses a little less memory.)
 *
 * @author     Jonas Karlsson
 * @version    $Revision$
 */
public interface Constants
{

	/********************************************************************
	 * Static definitions of Equipment location strings
	 ********************************************************************/

	/** Equipment location string - both hands. */
	String S_BOTH = "Both Hands"; //$NON-NLS-1$

	/** Equipment location string - carried. */
	String S_CARRIED = "Carried"; //$NON-NLS-1$

	/** Equipment location string - double weapon. */
	String S_DOUBLE = "Double Weapon"; //$NON-NLS-1$

	/** Equipment location string - equipped. */
	String S_EQUIPPED = "Equipped"; //$NON-NLS-1$

	/** Equipment location string - natural-primary. */
	String S_NATURAL_PRIMARY = "Natural-Primary"; //$NON-NLS-1$

	/** Equipment location string - natural-secondary. */
	String S_NATURAL_SECONDARY = "Natural-Secondary"; //$NON-NLS-1$

	/** Equipment location string - not carried. */
	String S_NOTCARRIED = "Not Carried"; //$NON-NLS-1$

	/** Equipment location string - primary hand. */
	String S_PRIMARY = "Primary Hand"; //$NON-NLS-1$

	/** Equipment location string - secondary hand. */
	String S_SECONDARY = "Secondary Hand"; //$NON-NLS-1$

	/** Equipment location string - shield. */
	String S_SHIELD = "Shield"; //$NON-NLS-1$

	/** Equipment location string - two Weapons. */
	String S_TWOWEAPONS = "Two Weapons"; //$NON-NLS-1$

	/** Equipment location string - unarmed. */
	String S_UNARMED = "Unarmed"; //$NON-NLS-1$

	/********************************************************************
	 * Game mode constants
	 ********************************************************************/

	/** Gamemode identification string - Third edition. */
	String GAMEMODE_EDITION_THREE_POINT_ZERO = "3e"; //$NON-NLS-1$

	/** Gamemode identification string - edition three point five. */
	String GAMEMODE_EDITION_THREE_POINT_FIVE = "35e"; //$NON-NLS-1$

	/** <html>none selected</html> */
	String html_NONESELECTED = "<html>none selected</html>"; //$NON-NLS-1$

	/** What to display as the application's name */
	String s_APPNAME = "PCGen"; //$NON-NLS-1$

	/** The prefix to add to an Automatically resized piece of equipment's Key */
	String s_AUTO_RESIZE = "AUTOSIZE"; //$NON-NLS-1$


	/********************************************************************
	 * Output sheet related
	 ********************************************************************/

	/** What a character template file name starts with */
	String s_CHARACTER_TEMPLATE_START = "csheet"; //$NON-NLS-1$

	/** CUSTOM */
	String s_CUSTOM = "Custom"; //$NON-NLS-1$

	/** Custom Equipment */
	String s_CUSTOMSOURCE = "Custom Equipment"; //$NON-NLS-1$

	/** Default */
	String s_DEFAULT = "Default"; //$NON-NLS-1$

	/** What a character template file name starts with. */
	String s_EQSET_TEMPLATE_START = "eqsheet"; //$NON-NLS-1$
	/** Generic Item */
	String s_GENERIC_ITEM = "Generic Item"; //$NON-NLS-1$

	/** PCGENi_ARMOR */
	String s_INTERNAL_EQMOD_ARMOR = "PCGENi_ARMOR"; //$NON-NLS-1$

	/** PCGENi_WEAPON */
	String s_INTERNAL_EQMOD_WEAPON = "PCGENi_WEAPON"; //$NON-NLS-1$

	/** PCGENi_WEAPON_PROFICIENCY */
	String s_INTERNAL_WEAPON_PROF = "PCGENi_WEAPON_PROFICIENCY"; //$NON-NLS-1$

	/** Line Seperator */
	String s_LINE_SEP = System.getProperty("line.separator"); //$NON-NLS-1$

	/** None */
	String s_NONE = "None"; //$NON-NLS-1$

	/** <none selected> */
	String s_NONESELECTED = "<none selected>"; //$NON-NLS-1$

	/** What a party template file name starts with. */
	String s_PARTY_TEMPLATE_START = "psheet"; //$NON-NLS-1$

	/** The extension for a campaign file   */
	String s_PCGEN_CAMPAIGN_EXTENSION = ".pcc"; //$NON-NLS-1$

	/** The extension for a character file   */
	String s_PCGEN_CHARACTER_EXTENSION = ".pcg"; //$NON-NLS-1$

	/** The extension for a party file   */
	String s_PCGEN_PARTY_EXTENSION = ".pcp"; //$NON-NLS-1$

	/** The extension for a list file */
	String s_PCGEN_LIST_EXTENSION = ".lst"; //$NON-NLS-1$

	/** Name of Shield Proficiency Feat */
	String s_ShieldProficiency = "Shield Proficiency"; //$NON-NLS-1$

	/** TYPE: */
	String s_TAG_TYPE = "TYPE:"; //$NON-NLS-1$

	/** currentPC */
	String s_TempFileName = "currentPC"; //$NON-NLS-1$

	/** Tower Shield Proficiency */
	String s_TowerShieldProficiency = "Tower Shield Proficiency"; //$NON-NLS-1$

	/** pdf */
	String s_pdf_outputsheet_directory = "pdf"; //$NON-NLS-1$

	/** htmlxml */
	String s_standard_outputsheet_directory = "htmlxml"; //$NON-NLS-1$

	/** Type: SPELLBOOK */
	String s_TYPE_SPELLBOOK = "SPELLBOOK"; //$NON-NLS-1$

	// Units of measurement
	/** Imperial */
	String s_STANDARD_UNITSET_NAME = "Imperial"; //$NON-NLS-1$
	/** ftin is harcoded to translate to x'y */
	String s_STANDARD_UNITSET_HEIGHTUNIT = "ftin"; //$NON-NLS-1$
	/** 1.0 */
	BigDecimal s_STANDARD_UNITSET_HEIGHTFACTOR = BigDecimal.ONE;
	/** The height display pattern */
	DecimalFormat s_STANDARD_UNITSET_HEIGHTDISPLAYPATTERN = new DecimalFormat("#.#"); //$NON-NLS-1$
	/**
	 * Use of "~" as first character means the unit
	 * name is appended without a leading space
	 */
	String s_STANDARD_UNITSET_DISTANCEUNIT = "~'"; //$NON-NLS-1$
	/** 1.0 */
	BigDecimal s_STANDARD_UNITSET_DISTANCEFACTOR = BigDecimal.ONE;
	/** # */
	DecimalFormat s_STANDARD_UNITSET_DISTANCEDISPLAYPATTERN = new DecimalFormat("#"); //$NON-NLS-1$
	/** lbs. */
	String s_STANDARD_UNITSET_WEIGHTUNIT = "lbs."; //$NON-NLS-1$
	/** 1.0 */
	BigDecimal s_STANDARD_UNITSET_WEIGHTFACTOR = BigDecimal.ONE;
	/** The unit set weight display pattern */
	DecimalFormat s_STANDARD_UNITSET_WEIGHTDISPLAYPATTERN = new DecimalFormat("#.###"); //$NON-NLS-1$

	// Systems for plug-ins
	/** PCGen */
	String s_SYSTEM_PCGEN = "PCGen"; //$NON-NLS-1$
	/** Tokens */
	String s_SYSTEM_TOKENS = "Tokens"; //$NON-NLS-1$
	/** GMGen */
	String s_SYSTEM_GMGEN = "GMGen"; //$NON-NLS-1$

	// Attack
	/** true */
	boolean PRINTOUT_WEAPONPROF = true;
	/** 0 */
	int ATTACKSTRING_MELEE = 0;
	/** 1 */
	int ATTACKSTRING_RANGED = 1;
	/** 2 */
	int ATTACKSTRING_UNARMED = 2;
	/** 4 */
	int AUTOGEN_EXOTICMATERIAL = 4;
	/** 3 */
	int AUTOGEN_MAGIC = 3;
	/** 2 */
	int AUTOGEN_MASTERWORK = 2;

	// What equipment to auto generate
	/** 1 */
	int AUTOGEN_RACIAL = 1;

	// Character stat generation methods
	/** 0 */
	int CHARACTERSTATMETHOD_USER = 0;
	/** 1 */
	int CHARACTERSTATMETHOD_ALLSAME = 1;
	/** 2 */
	int CHARACTERSTATMETHOD_PURCHASE = 2;
	/** 3 */
	int CHARACTERSTATMETHOD_ROLLED = 3;

	// Character panel tab constants
	/** 0 */
	int CHARACTER_TAB_SUMMARY = 0;
	/** 0 - do nothing*/
	int CHOOSER_SINGLECHOICEMETHOD_NONE = 0;
	/** 1 - add single choice to selected list*/
	int CHOOSER_SINGLECHOICEMETHOD_SELECT = 1;
	/** 2 - add single choice to selected list and then close */
	int CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT = 2;

	// HackMaster attributes
	/** 6 */
	int COMELINESS = 6;
	/** 0 */
	int DISPLAY_STYLE_NAME = 0;
	/** 1 */
	int DISPLAY_STYLE_NAME_CLASS = 1;
	/** 5 */
	int DISPLAY_STYLE_NAME_CUSTOM = 5;
	/** 4 */
	int DISPLAY_STYLE_NAME_FULL = 4;
	/** 2 */
	int DISPLAY_STYLE_NAME_RACE = 2;
	/** 3 */
	int DISPLAY_STYLE_NAME_RACE_CLASS = 3;
	/** 7 */
	int HONOR = 7;
	/** 1 */
	int HP_AUTOMAX = 1;
	/** 2 */
	int HP_AVERAGE = 2;
	/** 3 */
	int HP_PERCENTAGE = 3;

	// How to roll hitpoints
	/** 0 */
	int HP_STANDARD = 0;
	/** 4 */
	int HP_USERROLLED = 4;
	/** 5 */
	int HP_AVERAGE_ROUNDED_UP = 5;
	/** 9999 */
	int INVALID_LEVEL = 9999;

	/** The highest possible maxDex value. */
	Integer MAX_MAXDEX = Integer.valueOf(100);
	/** 5 */
	int MAX_OPEN_RECENT_ENTRIES = 5;
	/** The highest spell level we are catering for. */
	int MAX_SPELL_LEVEL = 25;

	// merge of like equipment constants
	/** 0 */
	int MERGE_ALL = 0;
	/** 2 */
	int MERGE_LOCATION = 2;
	/** 1 */
	int MERGE_NONE = 1;

	// SOURCE Display options
	/** 0 */
	int SOURCELONG = 0;
	/** 2 */
	int SOURCEPAGE = 2;
	/** 1 */
	int SOURCESHORT = 1;
	/** 3 */
	int SOURCEWEB = 3;
	/** 4 */
	int SOURCEMEDIUM = 4;
	/** 4 */
	int SOURCEDATE = 5;

	/** For accessing <code>CategorisableStore</code>. */
	String ALL_CATEGORIES = "ALL"; //$NON-NLS-1$
	// TODO: this is broken in some sad way whereby if you change the case
	// of FEAT to Feat, the pre-req tree fails on the Feats tab.
	/** For accessing <code>CategorisableStore</code>. */
	String FEAT_CATEGORY = "FEAT"; //$NON-NLS-1$

	/** For Tokens that need to know they're dealing with a .fo file */
	String XSL_FO_EXTENSION = "fo"; //$NON-NLS-1$

	/** An empty string. */
	String EMPTY_STRING = ""; //$NON-NLS-1$

	// Various parsing token constants
	/** Pipe '|' character as a string */
	String PIPE = "|"; //$NON-NLS-1$

	/** Comma ',' character as a string */
	String COMMA = ","; //$NON-NLS-1$
	String LST_DOT_CLEAR = ".CLEAR";
	String LST_TYPE_OLD = "TYPE.";
	String LST_NOT_TYPE_OLD = "!TYPE.";
	String LST_TYPE = "TYPE=";
	String LST_NOT_TYPE = "!TYPE=";
	int HANDS_SIZEDEPENDENT = -1;
	String LST_ALL = "ALL";
	String LST_ANY = "ANY";
	String LST_DOT_CLEAR_DOT = ".CLEAR.";
	String ALLREF_LST = "ALL";
	String DOT = ".";
	String LST_LIST = "LIST";
	String LST_PATTERN = "%";
	String LST_PERCENTLIST = "%LIST";
	String COLON = ":";
	String LST_NONE = "NONE";
	String LST_DOT_CLEARALL = ".CLEARALL";
	String EQUALS = "=";
	String LST_CHOOSE = "CHOOSE:";
	String LST_ADDCHOICE = "ADDCHOICE:";
	char CHAR_ASTERISK = '*';
	String LST_SHIELDTYPE = "SHIELDTYPE=";
	String LST_SHIELDTYPE_OLD = "SHIELDTYPE.";
	String LST_ARMORTYPE = "ARMORTYPE=";
	String LST_ARMORTYPE_OLD = "ARMORTYPE.";
	String LST_UNTRAINED = "UNTRAINED";
	String LST_TRAINED = "TRAINED";
	String LST_EXCLUSIVE = "EXCLUSIVE";
	String LST_NONEXCLUSIVE = "NONEXCLUSIVE";
	String LST_CROSSCLASS = "CROSSCLASSSKILLS";
	char PERCENT = '%';
	String LST_CHOICE = "CHOICE";
	String LST_PERCENT_CHOICE = "%CHOICE";
	String HIGHESTLEVELCLASS = "HIGHESTLEVELCLASS";
	String TAB = "\t";
	public static final int NO_LEVEL_LIMIT = -1;

	/* define some constants, so we can avoid magic numbers for the default values
	   in the settings handler */

	/**
	 * The default maximum level of a spell that may be put into a potion. This is used
	 * to initialise the value in the settings handler if the user has not overridden it.
	 */
	int DEFAULT_MAX_POTION_SPELL_LEVEL = 3;

	/**
	 * The default maximum level of a spell that may be put into a wand. This is used
	 * to initialise the value in the settings handler if the user has not overridden it.
	 */
	int DEFAULT_MAX_WAND_SPELL_LEVEL = 4;

	/**
	 * The default colour of automatic feats in the GUI. 0xB2B200 is dark yellow. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_FEAT_AUTO_COLOUR = 0xB2B200;

	/**
	 * The default colour of virtual feats in the GUI. 0xFF00FF is magenta. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_FEAT_VIRTUAL_COLOUR = 0xFF00FF;

	/**
	 * The default colour of released sources in the GUI. 0x000000 is black. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_SOURCE_STATUS_RELEASE_COLOUR = 0x000000;

	/**
	 * The default colour of alpha sources in the GUI. 0xFF0000 is red. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_SOURCE_STATUS_ALPHA_COLOUR = 0xFF0000; // red

	/**
	 * The default colour of beta sources in the GUI. 0x800000 is maroon. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_SOURCE_STATUS_BETA_COLOUR = 0x800000;

	/**
	 * The default colour of test sources in the GUI. 0xFF00FF is magenta. This
	 * is used to initialise the value in the settings handler if the user has not
	 * overridden it.
	 */
	int DEFAULT_SOURCE_STATUS_TEST_COLOUR = 0xFF00FF;

	int DEFAULT_HPPCT = 100;
	int DEFAULT_GEARTAB_SELL_RATE = 50;
	int DEFAULT_GEARTAB_BUY_RATE = 100;
	int DEFAULT_PREREQ_QUALIFY_COLOUR = 0x000000; // 0x000000 = black
	int DEFAULT_PREREQ_FAIL_COLOUR = 0xFF0000; // 0xFF0000 = red

	int NUMBER_OF_AGESET_KIT_SELECTIONS = 3;
	int ID_PATH_LENGTH_FOR_NONCONTAINDED = 3;
	int ARBITRARY_END_SKILL_INDEX = 3;
}
