/*
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
 */
package pcgen.cdom.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * This interface holds all global constants.
 *
 * Grandfathered as an interface. Not to be implemented.
 */
@SuppressWarnings("PMD.ConstantsInInterface")
public interface Constants
{

	/********************************************************************
	 * Static definitions of Equipment location strings
	 ********************************************************************/

	/** Equipment location string - both hands. */
	String EQUIP_LOCATION_BOTH = "Both Hands"; //$NON-NLS-1$

	/** Equipment location string - carried. */
	String EQUIP_LOCATION_CARRIED = "Carried"; //$NON-NLS-1$

	/** Equipment location string - double weapon. */
	String EQUIP_LOCATION_DOUBLE = "Double Weapon"; //$NON-NLS-1$

	/** Equipment location string - equipped. */
	String EQUIP_LOCATION_EQUIPPED = "Equipped"; //$NON-NLS-1$

	/** Equipment location string - natural-primary. */
	String EQUIP_LOCATION_NATURAL_PRIMARY = "Natural-Primary"; //$NON-NLS-1$

	/** Equipment location string - natural-secondary. */
	String EQUIP_LOCATION_NATURAL_SECONDARY = "Natural-Secondary"; //$NON-NLS-1$

	/** Equipment location string - not carried. */
	String EQUIP_LOCATION_NOTCARRIED = "Not Carried"; //$NON-NLS-1$

	/** Equipment location string - primary hand. */
	String EQUIP_LOCATION_PRIMARY = "Primary Hand"; //$NON-NLS-1$

	/** Equipment location string - secondary hand. */
	String EQUIP_LOCATION_SECONDARY = "Secondary Hand"; //$NON-NLS-1$

	/** Equipment location string - shield. */
	String EQUIP_LOCATION_SHIELD = "Shield"; //$NON-NLS-1$

	/** Equipment location string - two Weapons. */
	String EQUIP_LOCATION_TWOWEAPONS = "Two Weapons"; //$NON-NLS-1$

	/** Equipment location string - unarmed. */
	String EQUIP_LOCATION_UNARMED = "Unarmed"; //$NON-NLS-1$

	/********************************************************************
	 * Game mode constants
	 ********************************************************************/

	/** None selected, wrapped in html tags. */
	String WRAPPED_NONE_SELECTED = "<html>none selected</html>"; //$NON-NLS-1$

	/** What to display as the application's name. */
	String APPLICATION_NAME = "PCGen"; //$NON-NLS-1$

	/** The prefix to add to an Automatically resized piece of equipment's Key. */
	String AUTO_RESIZE_PREFIX = "AUTOSIZE"; //$NON-NLS-1$

	/********************************************************************
	 * Output sheet related
	 ********************************************************************/

	/** The prefix of a character template file name. */
	String CHARACTER_TEMPLATE_PREFIX = "csheet"; //$NON-NLS-1$

	/** The prefix of a party template file name. */
	String PARTY_TEMPLATE_PREFIX = "psheet"; //$NON-NLS-1$

	/** A constant string representing the type custom. */
	String TYPE_CUSTOM = "Custom"; //$NON-NLS-1$

	/** Default */
	String DEFAULT = "Default"; //$NON-NLS-1$

	/** Generic Item. */
	String GENERIC_ITEM = "GENERIC ITEM"; //$NON-NLS-1$

	/** The name of the internal Armour EQMOD. */
	String INTERNAL_EQMOD_ARMOR = "PCGENi_ARMOR"; //$NON-NLS-1$

	/** The name of the internal Weapon EQMOD. */
	String INTERNAL_EQMOD_WEAPON = "PCGENi_WEAPON"; //$NON-NLS-1$

	/** The name of the internal Weapon Proficiency ability. */
	String INTERNAL_WEAPON_PROF = "PCGENi_WEAPON_PROFICIENCY"; //$NON-NLS-1$

	/** Line Separator. */
	String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

	/** The constant string "None". */
	String NONE = "None"; //$NON-NLS-1$

	/** The extension for a campaign file. */
	String EXTENSION_CAMPAIGN_FILE = ".pcc"; //$NON-NLS-1$

	/** The extension for a character file. */
	String EXTENSION_CHARACTER_FILE = ".pcg"; //$NON-NLS-1$

	/** The extension for a list file. */
	String EXTENSION_LIST_FILE = ".lst"; //$NON-NLS-1$

	/** The extension for a party file. */
	String EXTENSION_PARTY_FILE = ".pcp"; //$NON-NLS-1$

	/** The temporary file name of the current PC used during output. */
	String TEMPORARY_FILE_NAME = "currentPC"; //$NON-NLS-1$

	/** A constant string representing the type spellbook. */
	String TYPE_SPELLBOOK = "SPELLBOOK"; //$NON-NLS-1$

	/********************************************************************
	 * Units of measurement
	 ********************************************************************/

	/** The name of the Standard Unit Set. */
	String STANDARD_UNITSET_NAME = "Imperial"; //$NON-NLS-1$

	/**
	 * The unit of height in the standard Unit set.  ftin is hardcoded to translate
	 * to feet and inches (x'y"). */
	String STANDARD_UNITSET_HEIGHT_UNIT = "ftin"; //$NON-NLS-1$

	/** The multiplier used to convert height in inches into the standard unit set's Height Unit. */
	BigDecimal STANDARD_UNITSET_HEIGHT_FACTOR = BigDecimal.ONE;

	/** The height display pattern. */
	DecimalFormat STANDARD_UNITSET_HEIGHT_DISPLAY_PATTERN = new DecimalFormat("#.#"); //$NON-NLS-1$

	/** The suffix appended to distances for the standard unit set.  The use of a tilde (~)
	 * as the first character means the unit name is appended without a leading space. */
	String STANDARD_UNITSET_DISTANCE_UNIT = "~'"; //$NON-NLS-1$

	/**
	 * The multiplier used to convert distance in feet into distance in the distance units
	 * of the standard unit set.  Which is feet, giving a multiplier of 1.0 */
	BigDecimal STANDARD_UNITSET_DISTANCE_FACTOR = BigDecimal.ONE;

	/** The decimal number display pattern. */
	DecimalFormat STANDARD_UNITSET_DISTANCE_DISPLAY_PATTERN = new DecimalFormat("#"); //$NON-NLS-1$

	/** The unit of weight in the standard Unit set. */
	String STANDARD_UNITSET_WEIGHT_UNIT = "lbs."; //$NON-NLS-1$

	/** The multiplier used to convert weight in pounds into weight in the weight units
	 * of the standard unit set. */
	BigDecimal STANDARD_UNITSET_WEIGHT_FACTOR = BigDecimal.ONE;

	/** The weight display pattern. */
	DecimalFormat STANDARD_UNITSET_WEIGHT_DISPLAY_PATTERN = new DecimalFormat("#.###"); //$NON-NLS-1$

	/********************************************************************
	 * Character stat generation methods
	 ********************************************************************/

	/** A constant used to select the method of rolling stats. */
	int CHARACTER_STAT_METHOD_USER = 0;

	/** A constant used to select the method of rolling stats. */
	int CHARACTER_STAT_METHOD_ALL_THE_SAME = 1;

	/** A constant used to select the method of rolling stats. */
	int CHARACTER_STAT_METHOD_PURCHASE = 2;

	/** A constant used to select the method of rolling stats. */
	int CHARACTER_STAT_METHOD_ROLLED = 3;

	/********************************************************************
	 *  Character panel tab constants
	 ********************************************************************/

	/**
	 * A constant defining the behaviour of a chooser when there is only one
	 * valid choice available - do nothing. */
	int CHOOSER_SINGLE_CHOICE_METHOD_NONE = 0;

	/**
	 * A constant defining the behaviour of a chooser when there is only one
	 * valid choice available  - add single choice to selected list. */
	//int CHOOSER_SINGLE_CHOICE_METHOD_SELECT = 1;

	/**
	 * A constant defining the behaviour of a chooser when there is only one
	 * valid choice available - add single choice to selected list and then
	 * close. */
	int CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT = 2;

	/********************************************************************
	 * How to roll hitpoints
	 ********************************************************************/

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option simply rolls the die. */
	int HP_STANDARD = 0;

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option simply gives the maximum available on the die. */
	int HP_AUTO_MAX = 1;

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option simply gives the average roll the die, adjusted
	 * so that for dice with an even number of sides, every other level gives
	 * and extra point. The extra point is given on even numbered levels. */
	int HP_AVERAGE = 2;

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option gives a defined percentage of the maximum available. */
	int HP_PERCENTAGE = 3;

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option means that the user must enter a value for each level. */
	int HP_USER_ROLLED = 4;

	/**
	 * A constant used to define the way that hitpoints will be calculated for
	 * this PC.  This option simply gives the average roll the die, adjusted
	 * so that for dice with an even number of sides, every other level gives
	 * and extra point. The extra point is given on odd numbered levels. */
	int HP_AVERAGE_ROUNDED_UP = 5;

	/**
	 * The highest possible maxDex value. */
	int MAX_MAXDEX = 100;

	/**
	 * The highest spell level we are catering for. */
	int MAX_SPELL_LEVEL = 25;

	/********************************************************************
	 * merge of like equipment constants
	 ********************************************************************/

	/**
	 * A constant defining how to merge like bits of equipment.
	 * merge all of them, regardless of location. */
	int MERGE_ALL = 0;

	/**
	 * A constant defining how to merge like bits of equipment.
	 * Don't merge any of them, regardless of location. */
	int MERGE_NONE = 1;

	/**
	 * A constant defining how to merge like bits of equipment.
	 * Merge those items located together. */
	int MERGE_LOCATION = 2;

	// TODO: this is broken in some sad way whereby if you change the case
	// of FEAT to Feat, the pre-req tree fails on the Feats tab.
	/** For accessing {@code CategorisableStore}. */
	String FEAT_CATEGORY = "FEAT"; //$NON-NLS-1$

	/** An empty string. */
	String EMPTY_STRING = ""; //$NON-NLS-1$

	/********************************************************************
	 * Various parsing token constants
	 ********************************************************************/

	/** Colon ':' character as a string. */
	String COLON = ":";

	/** Comma ',' character as a string. */
	String COMMA = ","; //$NON-NLS-1$

	/** Semicolon ',' character as a string. */
	String SEMICOLON = ";"; //$NON-NLS-1$

	/** Dot '.' character as a string. */
	String DOT = ".";

	/** Equals '=' character as a string. */
	String EQUALS = "=";

	/** Percent '%' character as a string. */
	String PERCENT = "%";

	/** Pipe '|' character as a string. */
	String PIPE = "|"; //$NON-NLS-1$

	/** Tab '*' character as a string. */
	String TAB = "\t";

	/** Asterisk '*' character as a constant. */
	char CHAR_ASTERISK = '*';

	/** A snippet of List code as a constant. */
	String LST_DOT_CLEAR = ".CLEAR";

	/** A snippet of List code as a constant. */
	String LST_DOT_CLEAR_ALL = ".CLEARALL";

	/** A snippet of List code as a constant. */
	String LST_DOT_CLEAR_DOT = ".CLEAR.";

	/** A snippet of List code as a constant. */
	String LST_SEMI_LEVEL_DOT = ";LEVEL.";

	/** A snippet of List code as a constant. */
	String LST_SEMI_LEVEL_EQUAL = ";LEVEL=";

	/** A snippet of List code as a constant. */
	String LST_CLASS_DOT = "CLASS.";

	/** A snippet of List code as a constant. */
	String LST_CLASS_EQUAL = "CLASS=";

	/** A snippet of List code as a constant. */
	String LST_TYPE_DOT = "TYPE.";

	/** A snippet of List code as a constant. */
	String LST_TYPE_EQUAL = "TYPE=";

	/** A snippet of List code as a constant. */
	String LST_NOT_TYPE_DOT = "!TYPE.";

	/** A snippet of List code as a constant. */
	String LST_NOT_TYPE_EQUAL = "!TYPE=";

	/** A snippet of List code as a constant. */
	String LST_PERCENT_CHOICE = "%CHOICE";

	/** A snippet of List code as a constant. */
	String LST_PERCENT_LIST = "%LIST";

	/** A snippet of List code as a constant. */
	String LST_CHOOSE_COLON = "CHOOSE:";

	/** A snippet of List code as a constant. */
	String LST_ALL = "ALL";

	/** A snippet of List code as a constant. */
	String LST_ANY = "ANY";

	/** A snippet of List code as a constant. */
	String LST_CROSS_CLASS = "CROSSCLASSSKILLS";

	/** A snippet of List code as a constant. */
	String LST_CLASS = "CLASS";

	/** A snippet of List code as a constant. */
	String LST_EXCLUSIVE = "EXCLUSIVE";

	/** A snippet of List code as a constant. */
	String LST_LIST = "LIST";

	/** A snippet of List code as a constant. */
	String LST_NONE = "NONE";

	/** A snippet of List code as a constant. */
	String LST_NONEXCLUSIVE = "NONEXCLUSIVE";

	/** A snippet of List code as a constant. */
	String LST_TRAINED = "TRAINED";

	/** A snippet of List code as a constant. */
	String LST_UNTRAINED = "UNTRAINED";

	/** A constant used in List parsing of Favoured Class. */
	String HIGHEST_LEVEL_CLASS = "HIGHESTLEVELCLASS";

	/** A constant used in the wield code. */
	int HANDS_SIZE_DEPENDENT = -1;

	/** A constant used in the control of whether a particular class has a level limit. */
	int NO_LEVEL_LIMIT = -1;

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

	/** The default percentage of hit points to grant when granting a percentage of hitpoints. */
	int DEFAULT_HP_PERCENT = 100;

	/** The default percentage of an item's worth a character receives when selling
	 * an item in the equipment tab. */
	int DEFAULT_GEAR_TAB_SELL_RATE = 50;

	/** The default percentage of an item's worth a character must pay when buying
	 * an item in the equipment tab. */
	int DEFAULT_GEAR_TAB_BUY_RATE = 100;

	/** A constant used to define an array of age sets. */
	int NUMBER_OF_AGESET_KIT_SELECTIONS = 10;

	/** If an ID path is longer than this, then the item is contained in something. */
	int ID_PATH_LENGTH_FOR_NON_CONTAINED = 3;

	/**
	 * Used when sorting skills to move some items right to the end of the
	 * sorted list. The moved items are sorted in their new position.  */
	int ARBITRARY_END_SKILL_INDEX = 999;

	/**
	 * The length of various LST constants that need to stripped from the front of a string during parsing.  */
	int SUBSTRING_LENGTH_FIVE = 5;

	/**
	 * The length of various LST constants that need to stripped from the front of a string during parsing.  */
	int SUBSTRING_LENGTH_SIX = 6;

	/**
	 * The length of various LST constants that need to stripped from the front of a string during parsing.  */
	int SUBSTRING_LENGTH_SEVEN = 7;

	/**
	 * The default for whether to print the weapon proficiencies.
	 * */
	boolean DEFAULT_PRINTOUT_WEAPONPROF = true;

	/** The ID component for the Root equip set. */
	String EQUIP_SET_ROOT_ID = "0"; //$NON-NLS-1$

	/** The character to used to separate path components. */
	String EQUIP_SET_PATH_SEPARATOR = "."; //$NON-NLS-1$

	/** Name of the default innate spell book. */
	public static final String INNATE_SPELL_BOOK_NAME = "Innate";

	/** The size (in pixels) of a side of the square thumbnail image */
	public static final int THUMBNAIL_SIZE = 100;

	/** Type that signifies the modifier marks what the equipment item is made from. */
	public static final String EQMOD_TYPE_BASEMATERIAL = "BaseMaterial";
}
