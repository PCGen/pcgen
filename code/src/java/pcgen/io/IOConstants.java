/*
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
package pcgen.io;

/**
 * Set of constants for Tags. Not to be implemented.
 * Grandfathered as an interface. Not to be implemented.
 */
@SuppressWarnings("PMD.ConstantsInInterface")
interface IOConstants
{
	/** Unix-style line separator.  Used for cross platform compatability */
	String LINE_SEP = "\n";

	// General tag constants
	/** Y used for yes/no options */
	String VALUE_Y = "Y";

	/** The end of tag marker */
	String TAG_END = ":";

	/** The tag separator */
	String TAG_SEPARATOR = "|";

	/** Start of comment line */
	String TAG_COMMENT = "#";

	/**  ABILITY */
	String TAG_ABILITY = "ABILITY";

	String TAG_ADDTOKEN = "ADD";

	/** Tag for Follower ADJUSTMENT */
	String TAG_ADJUSTMENT = "ADJUSTMENT";

	/** Adventure */
	String TAG_ADVENTURE = "ADVENTURE";

	/** AGE tag - Used by PCGVer2Creator and Parser */
	String TAG_AGE = "AGE";

	/** AGE SET tag */
	String TAG_AGESET = "AGESET";

	/** ALIGNALLOW */
	String TAG_ALIGNALLOW = "ALIGNALLOW";

	/** ALIGN */
	String TAG_ALIGNMENT = "ALIGN";

	/** APPLIEDTO */
	String TAG_APPLIEDTO = "APPLIEDTO";

	/** ASSOCIATED DATA */
	String TAG_ASSOCIATEDDATA = "ASSOCIATEDDATA";

	/** AUTOSORTSKILLS */
	String TAG_AUTOSORTSKILLS = "AUTOSORTSKILLS";

	/** AUTOSPELLS */
	String TAG_AUTOSPELLS = "AUTOSPELLS";

	/** IGNORECOST */
	String TAG_IGNORECOST = "IGNORECOST";

	/** ALLOWDEBT */
	String TAG_ALLOWDEBT = "ALLOWDEBT";

	/** AUTORESIZEGEAR */
	String TAG_AUTORESIZEGEAR = "AUTORESIZEGEAR";

	/** AUTOADDKNOWN */
	String TAG_AUTOADDKNOWN = "AUTOADDKNOWN";

	/** BASEITEM */
	String TAG_BASEITEM = "BASEITEM";

	/** BIRTHDAY */
	String TAG_BIRTHDAY = "BIRTHDAY";

	/** BIRTHPLACE */
	String TAG_BIRTHPLACE = "BIRTHPLACE";

	/** BONUS */
	String TAG_BONUS = "BONUS";

	/** CALCEQUIPSET */
	String TAG_CALCEQUIPSET = "CALCEQUIPSET";

	/** CAMPAIGN - System Information */
	String TAG_CAMPAIGN = "CAMPAIGN";

	/** CANCASTPERDAY - character spells info */
	String TAG_CANCASTPERDAY = "CANCASTPERDAY";

	/** CATCHPHRASE */
	String TAG_CATCHPHRASE = "CATCHPHRASE";

	/** CATEGORY */
	String TAG_CATEGORY = "CATEGORY";

	/** CHARACTERASSET */
	String TAG_CHARACTERASSET = "CHARACTERASSET";

	/** CHARACTERBIO - Character description/bio/history */
	String TAG_CHARACTERBIO = "CHARACTERBIO";

	/** CHARACTERCOMP */
	String TAG_CHARACTERCOMP = "CHARACTERCOMP";

	/** CHARACTERDESC */
	String TAG_CHARACTERDESC = "CHARACTERDESC";

	/** CHARACTERMAGIC */
	String TAG_CHARACTERMAGIC = "CHARACTERMAGIC";

	/** CHARACTERGMNOTES */
	String TAG_CHARACTERDMNOTES = "CHARACTERDMNOTES";

	/** Character Name */
	String TAG_CHARACTERNAME = "CHARACTERNAME";

	/** Character Type */
	String TAG_CHARACTERTYPE = "CHARACTERTYPE";

	/** CHOICE */
	String TAG_CHOICE = "CHOICE";

	/** CHOSENFEAT */
	String TAG_CHOSENFEAT = "CHOSENFEAT";

	/** CHOSENTEMPLATE */
	String TAG_CHOSENTEMPLATE = "CHOSENTEMPLATE";

	/** CHRONICLE */
	String TAG_CHRONICLE = "CHRONICLE";
	/** CHRONICLE_ENTRY */
	String TAG_CHRONICLE_ENTRY = "CHRONICLEENTRY";

	/** CITY */
	String TAG_CITY = "CITY";

	/** Character class(es) */
	String TAG_CLASS = "CLASS";

	/** CLASSABILITIESLEVEL */
	String TAG_CLASSABILITIESLEVEL = "CLASSABILITIESLEVEL";

	/** CLASSBOUGHT - Character skills */
	String TAG_CLASSBOUGHT = "CLASSBOUGHT";

	/** CLASSSKILL */
	String TAG_CLASSSKILL = "CLASSSKILL";

	/** COST - Currently (20/03/2006) only being used by CMP data sources */
	String TAG_COST = "COST";

	/** CUSTOMIZATION */
	String TAG_CUSTOMIZATION = "CUSTOMIZATION";
	/** DATA */
	String TAG_DATA = "DATA";
	/** Date */
	String TAG_DATE = "DATE";
	/** DEFINED */
	String TAG_DEFINED = "DEFINED";

	/** Character deity/domain */
	String TAG_DEITY = "DEITY";
	/** DEITYALIGN */
	String TAG_DEITYALIGN = "DEITYALIGN";
	/** DEITYDOMAINS */
	String TAG_DEITYDOMAINS = "DEITYDOMAINS";
	/** DEITYFAVWEAP */
	String TAG_DEITYFAVWEAP = "DEITYFAVWEAP";

	/** DESC */
	String TAG_DESC = "DESC";

	/** DOMAIN */
	String TAG_DOMAIN = "DOMAIN";

	/** DOMAINGRANTS */
	String TAG_DOMAINGRANTS = "DOMAINGRANTS";

	/** EquipSet Temporary Bonuses */
	String TAG_EQSETBONUS = "EQSETBONUS";

	/** Character equipment */
	String TAG_EQUIPMENT = "EQUIPMENT";
	/** Character equipment name */
	String TAG_EQUIPNAME = "EQUIPNAME";

	/** EQUIPSET */
	String TAG_EQUIPSET = "EQUIPSET";

	/** ERROR */
	String TAG_ERROR = "ERROR";

	/** Character experience */
	String TAG_EXPERIENCE = "EXPERIENCE";
	String TAG_EXPERIENCETABLE = "EXPERIENCETABLE";

	/** EXPRESSION */
	String TAG_EXPRESSION = "EXPRESSION";
	/** EYECOLOR */
	String TAG_EYECOLOR = "EYECOLOR";

	/** Character feats */
	String TAG_FEAT = "FEAT";

	/** FAVORED CLASS */
	String TAG_FAVOREDCLASS = "FAVOREDCLASS";
	/** FEATLIST */
	String TAG_FEATLIST = "FEATLIST";
	/** FEATPOOL */
	String TAG_FEATPOOL = "FEATPOOL";
	/** FILE */
	String TAG_FILE = "FILE";

	/** Character follower */
	String TAG_FOLLOWER = "FOLLOWER";
	/** GAMEMODE */
	String TAG_GAMEMODE = "GAMEMODE";
	/** GENDER */
	String TAG_GENDER = "GENDER";
	/** Game master */
	String TAG_GM = "GM";
	/** HAIRCOLOR */
	String TAG_HAIRCOLOR = "HAIRCOLOR";
	/** HAIRSTYLE */
	String TAG_HAIRSTYLE = "HAIRSTYLE";
	/** HANDED */
	String TAG_HANDED = "HANDED";
	/** HEIGHT - Used by PCGVer2Creator &amp; Parser */
	String TAG_HEIGHT = "HEIGHT";
	/** HITDICE */
	String TAG_HITDICE = "HITDICE";
	/** HITPOINTS */
	String TAG_HITPOINTS = "HITPOINTS";

	/** Output Sheets */
	String TAG_HTMLOUTPUTSHEET = "OUTPUTSHEETHTML";
	/** ID */
	String TAG_ID = "ID";
	/** INTERESTS */
	String TAG_INTERESTS = "INTERESTS";

	/** Kits */
	String TAG_KIT = "KIT";

	/** Character languages */
	String TAG_LANGUAGE = "LANGUAGE";
	/** LEVEL */
	String TAG_LEVEL = "LEVEL";
	/** ABILITY */
	String TAG_LEVELABILITY = "ABILITY";
	/** LOADCOMPANIONS */
	String TAG_LOADCOMPANIONS = "LOADCOMPANIONS";
	/** LOCATION */
	String TAG_LOCATION = "LOCATION";
	/** KEY (for Java maps) */
	String TAG_MAPKEY = "KEY";
	/** VALUE (stored in map) */
	String TAG_MAPVALUE = "VALUE";
	/** MASTER */
	String TAG_MASTER = "MASTER";

	/** MONEY */
	String TAG_MONEY = "MONEY";
	/** NAME */
	String TAG_NAME = "NAME";
	/** NOTE */
	String TAG_NOTE = "NOTE";
	/** OUTPUTORDER */
	String TAG_OUTPUTORDER = "OUTPUTORDER";
	/** PARENTID */
	String TAG_PARENTID = "PARENTID";
	/** Party */
	String TAG_PARTY = "PARTY";
	/** PC */
	String TAG_PC = "PC";
	/** PCCLASS */
	String TAG_PCCLASS = "PCCLASS";
	/** PCGVERSION */
	String TAG_PCGVERSION = "PCGVERSION";
	/** OUTPUTSHEETPDF */
	String TAG_PDFOUTPUTSHEET = "OUTPUTSHEETPDF";
	/** PERSONALITYTRAIT1 */
	String TAG_PERSONALITYTRAIT1 = "PERSONALITYTRAIT1";
	/** PERSONALITYTRAIT2 */
	String TAG_PERSONALITYTRAIT2 = "PERSONALITYTRAIT2";
	/** PHOBIAS */
	String TAG_PHOBIAS = "PHOBIAS";
	/** PLAYERNAME */
	String TAG_PLAYERNAME = "PLAYERNAME";
	/** POOLPOINTS */
	String TAG_POOLPOINTS = "POOLPOINTS";
	/** POOLPOINTSAVAIL */
	String TAG_POOLPOINTSAVAIL = "POOLPOINTSAVAIL";
	/** PORTRAIT */
	String TAG_PORTRAIT = "PORTRAIT";
	/** PORTRAIT */
	String TAG_PORTRAIT_THUMBNAIL_RECT = "PORTRAITTHUMBNAILRECT";
	/** POSTSTAT */
	String TAG_POSTSTAT = "POSTSTAT";

	/** PRESTAT */
	String TAG_PRESTAT = "PRESTAT";

	/** Preview Sheet */
	String TAG_PREVIEWSHEET = "PREVIEWSHEET";

	/** PROHIBITED */
	String TAG_PROHIBITED = "PROHIBITED";
	/** PURCHASEPOINTS */
	String TAG_PURCHASEPOINTS = "PURCHASEPOINTS";
	/** QUANTITY */
	String TAG_QUANTITY = "QUANTITY";
	/** RACE */
	String TAG_RACE = "RACE";

	/** RANKS */
	String TAG_RANKS = "RANKS";
	/** REGION */
	String TAG_REGION = "REGION";
	/** RESIDENCE */
	String TAG_RESIDENCE = "RESIDENCE";
	/** ROLLMETHOD */
	String TAG_ROLLMETHOD = "ROLLMETHOD";
	/** SA */
	String TAG_SA = "SA";
	/** SAVE */
	String TAG_SAVE = "SAVE";
	/** SAVES */
	String TAG_SAVES = "SAVES";

	/** SCORE */
	String TAG_SCORE = "SCORE";
	/** SKILL */
	String TAG_SKILL = "SKILL";
	/** SKILLFILTER */
	String TAG_SKILLFILTER = "SKILLFILTER";
	/** SKILLSGAINED */
	String TAG_SKILLPOINTSGAINED = "SKILLSGAINED";
	/** SKILLSREMAINING */
	String TAG_SKILLPOINTSREMAINING = "SKILLSREMAINING";
	/** SKILLPOOL */
	String TAG_SKILLPOOL = "SKILLPOOL";
	/** SKILLSOUTPUTORDER */
	String TAG_SKILLSOUTPUTORDER = "SKILLSOUTPUTORDER";
	/** SKINCOLOR */
	String TAG_SKINCOLOR = "SKINCOLOR";
	/** SOURCE */
	String TAG_SOURCE = "SOURCE";
	/** SPECIALABILITIES */
	String TAG_SPECIALABILITIES = "SPECIALABILITIES";
	/** SPECIALTIES */
	String TAG_SPECIALTIES = "SPECIALTIES";
	/** SPECIALTY */
	String TAG_SPECIALTY = "SPECIALTY";
	/** SPEECHPATTERN */
	String TAG_SPEECHPATTERN = "SPEECHPATTERN";

	/** SPELLBASE */
	String TAG_SPELLBASE = "SPELLBASE";
	/** BOOK */
	String TAG_SPELL_BOOK = "BOOK";
	/** SPELL */
	String TAG_SPELL = "SPELL";
	/** SPELLBOOK */
	String TAG_SPELLBOOK = "SPELLBOOK";
	/** SPELLLEVEL */
	String TAG_SPELLLEVEL = "SPELLLEVEL";
	/** SPELLLIST */
	String TAG_SPELLLIST = "SPELLLIST";
	/** SPELLNAME */
	String TAG_SPELLNAME = "SPELLNAME";
	/** SPELLPPCOST (Psionics) */
	String TAG_SPELLPPCOST = "SPELLPPCOST";
	/** SPELLNUMPAGES */
	String TAG_SPELLNUMPAGES = "SPELLNUMPAGES";

	/** Character attributes */
	String TAG_STAT = "STAT";
	/** SUBCLASS */
	String TAG_SUBCLASS = "SUBCLASS";
	/** SUBSTITUTIONLEVEL */
	String TAG_SUBSTITUTIONLEVEL = "SUBSTITUTIONLEVEL";
	/** Suppressed biography fields */
	String TAG_SUPPRESS_BIO_FIELDS = "SUPPRESSBIOFIELDS";

	/** SYNERGY */
	String TAG_SYNERGY = "SYNERGY";
	/** TABNAME */
	String TAG_TABNAME = "TABNAME";

	/** Temporary Bonuses */
	String TAG_TEMPBONUS = "TEMPBONUS";
	/** TBBONUS */
	String TAG_TEMPBONUSBONUS = "TBBONUS";
	/** Is the temporary bonus to be initially applied? */
	String TAG_TEMPBONUSACTIVE = "TBACTIVE";

	/** TBTARGET */
	String TAG_TEMPBONUSTARGET = "TBTARGET";
	/** TEMPLATE */
	String TAG_TEMPLATE = "TEMPLATE";

	/** Character templates */
	String TAG_TEMPLATESAPPLIED = "TEMPLATESAPPLIED";
	/** TIMES */
	String TAG_TIMES = "TIMES";

	/** TYPE */
	String TAG_TYPE = "TYPE";

	/** USETEMPMODS */
	String TAG_USETEMPMODS = "USETEMPMODS";
	/** USEHIGHERKNOWN (SPELL) */
	String TAG_USEHIGHERKNOWN = "USEHIGHERKNOWN";
	/** USEHIGHERPREPPED (SPELL) */
	String TAG_USEHIGHERPREPPED = "USEHIGHERPREPPED";
	/** VALUE */
	String TAG_VALUE = "VALUE";
	/** VERSION */
	String TAG_VERSION = "VERSION";
	/** VFEAT */
	String TAG_VFEAT = "VFEAT";
	/** WEAPON */
	String TAG_WEAPON = "WEAPON";

	/** Character weapon proficiencies */
	String TAG_WEAPONPROF = "WEAPONPROF";
	/** WEIGHT - Used by PCGVer2 Parser and Creator */
	String TAG_WEIGHT = "WEIGHT";
	/** WT */
	String TAG_WT = "WT";

	/** USERPOOL - The amount the user has modified the pool by. */
	String TAG_USERPOOL = "USERPOOL";

	/** PREVIEWVAR - preview sheet variable,  allows preview input fields to be maintained*/
	String TAG_PREVIEWSHEETVAR = "PREVIEWVAR";
}
