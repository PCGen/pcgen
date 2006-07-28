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
package pcgen.core;

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
	// Static definitions of Equipment location strings
	/** Both Hands */
	String S_BOTH              = "Both Hands";
	/** Carried */
	String S_CARRIED           = "Carried";
	/** Double Weapon */
	String S_DOUBLE            = "Double Weapon";
	/** Equipped */
	String S_EQUIPPED          = "Equipped";
	/** Fingers */
	String S_FINGERS           = "Fingers,";
	/** Natural-Primary */
	String S_NATURAL_PRIMARY   = "Natural-Primary";
	/** Natural-Secondary */
	String S_NATURAL_SECONDARY = "Natural-Secondary";
	/** Not Carried */
	String S_NOTCARRIED        = "Not Carried";
	/** Primary Hand */
	String S_PRIMARY           = "Primary Hand";
	/** Rapid Shot */
	String S_RAPIDSHOT         = "Rapid Shot";
	/** Secondary Hand */
	String S_SECONDARY         = "Secondary Hand";
	/** Shield */
	String S_SHIELD            = "Shield";
	/** Two Weapons */
	String S_TWOWEAPONS        = "Two Weapons";
	/** Unarmed */
	String S_UNARMED           = "Unarmed";

	// Game mode constants
	/** 3e */
	String e3_MODE = "3e";
	/** 35e */
	String e35_MODE = "35e";
	/** <html>none selected</html> */
	String html_NONESELECTED = "<html>none selected</html>";

	/** What to display as the application's name */
	String s_APPNAME = "PCGen";

	/** The prefix to add to an Automatically resized piece of equipment's Key */
	String s_AUTO_RESIZE ="AUTOSIZE";

	// Output sheet related
	/** What a character template file name starts with */
	String s_CHARACTER_TEMPLATE_START = "csheet";
	/** CUSTOM */
	String s_CUSTOM = "CUSTOM";
	/** Custom Equipment */
	String s_CUSTOMSOURCE = "Custom Equipment";
	/** Default */
	String s_DEFAULT = "Default";
	/** What a character template file name starts with. */
	String s_EQSET_TEMPLATE_START  = "eqsheet";
	/** Generic Item */
	String s_GENERIC_ITEM          = "Generic Item";
	/** PCGENi_ARMOR */
	String s_INTERNAL_EQMOD_ARMOR  = "PCGENi_ARMOR";
	/** PCGENi_WEAPON */
	String s_INTERNAL_EQMOD_WEAPON = "PCGENi_WEAPON";
	/** PCGENi_WEAPON_PROFICIENCY */
	String s_INTERNAL_WEAPON_PROF  = "PCGENi_WEAPON_PROFICIENCY";
	/** Line Seperator */
	String s_LINE_SEP              = System.getProperty("line.separator");
	/** None */
	String s_NONE = "None";
	/** <none selected> */
	String s_NONESELECTED = "<none selected>";
	/** What a party template file name starts with. */
	String s_PARTY_TEMPLATE_START = "psheet";
	/** The extension for a campaign file   */
	String s_PCGEN_CAMPAIGN_EXTENSION = ".pcc";
	/** The extension for a character file   */
	String s_PCGEN_CHARACTER_EXTENSION = ".pcg";
	/** The extension for a party file   */
	String s_PCGEN_PARTY_EXTENSION = ".pcp";
	/** The extension for a list file */
	String s_PCGEN_LIST_EXTENSION = ".lst";
	/** Name of Shield Proficiency Feat */
	String s_ShieldProficiency = "Shield Proficiency";
	/** TYPE: */
	String s_TAG_TYPE                       = "TYPE:";
	/** currentPC */
	String s_TempFileName                   = "currentPC";
	/** Tower Shield Proficiency */
	String s_TowerShieldProficiency         = "Tower Shield Proficiency";
	/** pdf */
	String s_pdf_outputsheet_directory      = "pdf";
	/** htmlxml */
	String s_standard_outputsheet_directory = "htmlxml";

	/** Type: SPELLBOOK */
	String s_TYPE_SPELLBOOK= "SPELLBOOK";
	
	/** Tab names */
	String[] tabNames =
	{
		"Abilities",
		"Campaigns",
		"Class",
		"Description",
		"Domains",
		"Feats",
		"Inventory",
		"Race",
		"Skills",
		"Spells",
		"Summary",
		"Gear",
		"Equipping",
		"Resources",
		"TempMod",
		"NaturalWeapons",
		"Known",
		"Prepared",
		"Spellbooks",
		"Races",
		"Templates"
	};

	// Encumberence Constants (String)
	/** LIGHT */
	String s_LOAD_LIGHT    = "LIGHT";
	/** MEDIUM */
	String s_LOAD_MEDIUM   = "MEDIUM";
	/** HEAVY */
	String s_LOAD_HEAVY    = "HEAVY";
	/** OVERLOAD */
	String s_LOAD_OVERLOAD = "OVERLOAD";

	// Units of measurement
	/** Imperial */
	String s_STANDARD_UNITSET_NAME                   = "Imperial";
	/** ftin is harcoded to translate to x'y */
	String s_STANDARD_UNITSET_HEIGHTUNIT             = "ftin";
	/** 1.0 */
	double s_STANDARD_UNITSET_HEIGHTFACTOR           = 1.0;
	/** The height display pattern */
	String s_STANDARD_UNITSET_HEIGHTDISPLAYPATTERN   = "#.#";
	/**
	 * Use of "~" as first character means the unit
     * name is appended without a leading space
     */
	String s_STANDARD_UNITSET_DISTANCEUNIT           = "~'";
	/** 1.0 */
	double s_STANDARD_UNITSET_DISTANCEFACTOR         = 1.0;
	/** # */
	String s_STANDARD_UNITSET_DISTANCEDISPLAYPATTERN = "#";
	/** lbs. */
	String s_STANDARD_UNITSET_WEIGHTUNIT             = "lbs.";
	/** 1.0 */
	double s_STANDARD_UNITSET_WEIGHTFACTOR           = 1.0;
	/** The unit set weight display pattern */
	String s_STANDARD_UNITSET_WEIGHTDISPLAYPATTERN   = "#.###";

	// Systems for plugins
	/** PCGen */
	String s_SYSTEM_PCGEN  = "PCGen";
	/** Tokens */
	String s_SYSTEM_TOKENS = "Tokens";
	/** GMGen */
	String s_SYSTEM_GMGEN  = "GMGen";

	// Attack
	/** true */
	boolean PRINTOUT_WEAPONPROF = true;
	/** 0 */
	int ATTACKSTRING_MELEE      = 0;
	/** 1 */
	int ATTACKSTRING_RANGED     = 1;
	/** 2 */
	int ATTACKSTRING_UNARMED    = 2;
	/** 4 */
	int AUTOGEN_EXOTICMATERIAL  = 4;
	/** 3 */
	int AUTOGEN_MAGIC           = 3;
	/** 2 */
	int AUTOGEN_MASTERWORK      = 2;

	// What equipment to autogenerate
	/** 1 */
	int AUTOGEN_RACIAL            = 1;

	// Character stat generation methods
	/** 0 */
	int CHARACTERSTATMETHOD_USER      = 0;
	/** 1 */
	int CHARACTERSTATMETHOD_ALLSAME   = 1;
	/** 2 */
	int CHARACTERSTATMETHOD_PURCHASE  = 2;
	/** 3 */
	int CHARACTERSTATMETHOD_ROLLED    = 3;

	// Character panel tab constants
	/** 0 */
	int CHARACTER_TAB_SUMMARY                 = 0;
	/** 0 - do nothing*/
	int CHOOSER_SINGLECHOICEMETHOD_NONE       = 0;
	/** 1 - add single choice to selected list*/
	int CHOOSER_SINGLECHOICEMETHOD_SELECT     = 1;
	/** 2 - add single choice to selected list and then close */
	int CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT = 2;

	// HackMaster attributes
	/** 6 */
	int COMELINESS                    = 6;
	/** 0 */
	int DISPLAY_STYLE_NAME            = 0;
	/** 1 */
	int DISPLAY_STYLE_NAME_CLASS      = 1;
	/** 5 */
	int DISPLAY_STYLE_NAME_CUSTOM     = 5;
	/** 4 */
	int DISPLAY_STYLE_NAME_FULL       = 4;
	/** 2 */
	int DISPLAY_STYLE_NAME_RACE       = 2;
	/** 3 */
	int DISPLAY_STYLE_NAME_RACE_CLASS = 3;
	/** 7 */
	int HONOR                         = 7;
	/** 1 */
	int HP_AUTOMAX                    = 1;
	/** 2 */
	int HP_AVERAGE                    = 2;
	/** 3 */
	int HP_PERCENTAGE				  = 3;

	// How to roll hitpoints
	/** 0 */
	int HP_STANDARD   = 0;
	/** 4 */
	int HP_USERROLLED = 4;
	/** 9999 */
	int INVALID_LEVEL = 9999;

	// Encumbrance Constants
	/** 0 */
	int LIGHT_LOAD  = 0;
	/** 1 */
	int MEDIUM_LOAD = 1;
	/** 2 */
	int HEAVY_LOAD  = 2;
	/** 3 */
	int OVER_LOAD   = 3;

	/** The highest possible maxDex value. */
	int MAX_MAXDEX              = 100;
	/** 5 */
	int MAX_OPEN_RECENT_ENTRIES = 5;

	// merge of like equipment constants
	/** 0 */
	int MERGE_ALL      = 0;
	/** 2 */
	int MERGE_LOCATION = 2;
	/** 1 */
	int MERGE_NONE     = 1;

	// Paper info
	/** 4 */
	int PAPERINFO_BOTTOMMARGIN  = 4;
	/** 1 */
	int PAPERINFO_HEIGHT        = 1;
	/** 5 */
	int PAPERINFO_LEFTMARGIN    = 5;
	/** 0 */
	int PAPERINFO_NAME          = 0;
	/** 6 */
	int PAPERINFO_RIGHTMARGIN   = 6;
	/** 3 */
	int PAPERINFO_TOPMARGIN     = 3;
	/** 2 */
	int PAPERINFO_WIDTH         = 2;

	// SOURCE Display options
	/** 0 */
	int SOURCELONG      = 0;
	/** 2 */
	int SOURCEPAGE      = 2;
	/** 1 */
	int SOURCESHORT     = 1;
	/** 3 */
	int SOURCEWEB       = 3;
	/** 4 */
	int SOURCEMEDIUM    = 4;
	/** 4 */
	int SOURCEDATE      = 5;

	// Tabs
	/** -1 */
	int TAB_INVALID     = -1;
	/** 0 */
	int TAB_SABILITIES  = 0;
	/** 1 */
	int TAB_SOURCES     = 1;
	/** 2 */
	int TAB_CLASSES     = 2;
	/** 3 */
	int TAB_DESCRIPTION = 3;
	/** 4 */
	int TAB_DOMAINS     = 4;
	/** 5 */
	int TAB_ABILITIES   = 5;
	/** 6 */
	int TAB_INVENTORY   = 6;
	/** 7 */
	int TAB_RACE_MASTER = 7;
	/** 8 */
	int TAB_SKILLS      = 8;
	/** 9 */
	int TAB_SPELLS      = 9;
	/** 10 */
	int TAB_SUMMARY     = 10;
	/** 11 */
	int TAB_GEAR        = 11;
	/** 12 */
	int TAB_EQUIPPING   = 12;
	/** 13 */
	int TAB_RESOURCES   = 13;
	/** 14 */
	int TAB_TEMPBONUS   = 14;
	/** 15 */
	int TAB_NATWEAPONS  = 15;
	/** 16 */
	int TAB_KNOWN_SPELLS  = 16;
	/** 17 */
	int TAB_PREPARED_SPELLS  = 17;
	/** 18 */
	int TAB_SPELLBOOKS  = 18;
	/** 19 */
	int TAB_RACES       = 19;
	/** 20 */
	int TAB_TEMPLATES   = 20;


	/** For accessing <code>CategorisableStore</code>. */
	String ALL_CATEGORIES = "ALL";
	// TODO: this is broken in some sad way whereby if you change the case
	// of FEAT to Feat, the pre-req tree fails on the Feats tab.
	/** For accessing <code>CategorisableStore</code>. */
	String FEAT_CATEGORY = "FEAT";
	
	/** For Tokens that need to know they're dealing with a .fo file */
	String XSL_FO_EXTENSION = "fo";

	/** An empty string. */
	String EMPTY_STRING = ""; //$NON-NLS-1$
}
