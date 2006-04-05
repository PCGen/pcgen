/*
 * Globals.java
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSlot;
import pcgen.core.character.WieldCategory;
import pcgen.core.money.DenominationList;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.persistence.PersistenceManager;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Globals
{
	/** These are changed during normal operation */
	private static PlayerCharacter currentPC;
	private static List            pcList      = new ArrayList();
	/** Race, a s_EMPTYRACE */
	public static  Race            s_EMPTYRACE;

	/** This is true when the campaign data structures are sorted. */
	private static boolean d_sorted;

	/** These are system constants */
	public static final String javaVersion      = System.getProperty("java.version");
	/** Java Version Major */
	public static final int    javaVersionMajor =
		Integer.valueOf(javaVersion.substring(0,
				javaVersion.indexOf('.'))).intValue();
	/** Java Version Minor */
	public static final int    javaVersionMinor =
		Integer.valueOf(javaVersion.substring(javaVersion.indexOf('.') + 1,
				javaVersion.lastIndexOf('.'))).intValue();

	/** NOTE: The defaultPath is duplicated in LstSystemLoader. */
	private static final String defaultPath    = System.getProperty("user.dir");
	private static final String defaultPcgPath = getDefaultPath() + File.separator + "characters";
	private static final String backupPcgPath = "";
	private static final int[]  dieSizes       = new int[]{ 1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000 };

	/** These are for the Internationalization project. */
	private static String     language        = "en";
	private static String     country         = "US";

	/** The BioSet used for age calculations */
	private static BioSet     bioSet          = new BioSet();
	private static final List custColumnWidth = new ArrayList();
	private static int        sourceDisplay   = Constants.SOURCELONG;
	private static int        selectedPaper   = -1;

	private static CategorisableStore abilityStore = new CategorisableStore();

	/** we need maps for efficient lookups */
	private static Map        campaignMap     = new HashMap();
	private static Map        domainMap       = new TreeMap();
	private static SortedMap  raceMap         = new TreeMap();
	private static Map        spellMap        = new TreeMap();
	private static Map        eqSlotMap       = new HashMap();
	private static Map        visionMap       = new HashMap();

	/** We use lists for efficient iteration */
	private static List armorProfList         = new ArrayList();
	private static List campaignList          = new ArrayList(85);
	private static List classList             = new ArrayList(380);
	private static List companionModList      = new ArrayList();
	private static List deityList             = new ArrayList(275);
	private static List domainList            = new ArrayList(100);
	private static List kitList               = new ArrayList();
	private static List languageList          = new ArrayList(200);

	//any TYPE added to pcClassTypeList is assumed be pre-tokenized
	private static List             pcClassTypeList = new ArrayList();
	private static List             skillList       = new ArrayList(400);
	private static List             templateList    = new ArrayList(350);
	private static DenominationList denomList       = DenominationList.getInstance(); // derived from ArrayList
	private static SortedSet        saSet           = new TreeSet();

	private static Map sponsors = new HashMap();
	private static List sponsorList = new ArrayList();

	/** Weapon proficiency Data storage */
	private static final WeaponProfDataStore weaponProfs = new WeaponProfDataStore();

	/** this is used by the random selection tools */
	private static final Random random = new Random(System.currentTimeMillis());

	/**
	 * The following sets are for efficient filter creation:
	 * <ul>
	 * <li>pantheonsSet</li>
	 * <li>raceTypesSet</li>
	 * <li>subschoolsSet</li>
	 * <li>weaponTypes</li>
	 * </ul>
	 * The following sets are for efficient filter creation as
	 * well as quick loading of the spell editor:
	 * <ul>
	 * <li>castingTimesSet</li>
	 * <li>componentSet</li>
	 * <li>descriptorSet</li>
	 * <li>durationSet</li>
	 * <li>typeForSpellsSet</li>
	 * <li>rangesSet</li>
	 * <li>saveInfoSet</li>
	 * <li>srSet</li>
	 * <li>statSet</li>
	 * <li>targetSet</li>
	 * </ul>
	 */
	private static SortedSet pantheonsSet     = new TreeSet();
	private static SortedSet raceTypesSet     = new TreeSet();
	private static SortedSet subschoolsSet    = new TreeSet();
	private static SortedSet weaponTypes      = new TreeSet();

	private static SortedSet castingTimesSet  = new TreeSet();
	private static SortedSet componentSet     = new TreeSet();
	private static SortedSet descriptorSet    = new TreeSet();
	private static SortedSet durationSet      = new TreeSet();
	private static SortedSet typeForSpellsSet = new TreeSet();
	private static SortedSet rangesSet        = new TreeSet();
	private static SortedSet saveInfoSet      = new TreeSet();
	private static SortedSet srSet            = new TreeSet();
	private static SortedSet statSet          = new TreeSet();
	private static SortedSet targetSet        = new TreeSet();

	// end of filter creation sets
	private static JFrame rootFrame;
	private static JFrame currentFrame;
	private static final StringBuffer section15 = new StringBuffer(30000);
	private static final String spellPoints = "0";

	/** whether or not the GUI is used (false for command line) */
	private static boolean useGUI = true;


	private static final Comparator pObjectComp = new Comparator()
		{
			public int compare(final Object o1, final Object o2)
			{
				return ((PObject) o1).getKeyName().compareToIgnoreCase(((PObject) o2).getKeyName());
			}
		};

	private static final Comparator pObjectNameComp = new Comparator()
		{
			public int compare(final Object o1, final Object o2)
			{
				return ((PObject) o1).getName().compareToIgnoreCase(((PObject) o2).getName());
			}
		};

	// Optimizations used by any code needing empty arrays.  All empty arrays
	// of the same type are idempotent.
	/** EMPTY_CLASS_ARRAY*/
	public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
	/** EMPTY_DOUBLE_ARRAY*/
	public static final Double[] EMPTY_DOUBLE_ARRAY = new Double[0];
	/** EMPTY_OBJECT_ARRAY*/
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	/** EMPTY_STRING_ARRAY*/
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	/** Name of the default innate spell book. */
	public static final String INNATE_SPELL_BOOK_NAME = "Innate";

	static
	{
		ResourceBundle globalProperties;

		try
		{
			globalProperties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			globalProperties.getString("VersionNumber");
		}
		catch (MissingResourceException mrex)
		{
			Logging.errorPrint("Can't find the VersionNumber property.", mrex);
		}
		finally
		{
			globalProperties = null; // TODO: value never used
		}
	}

	/**
	 * Get all weapon proficiencies of a type
	 * @param type
	 * @return all weapon proficiencies of a type
	 */
	public static Collection getAllWeaponProfsOfType(final String type)
	{
		return weaponProfs.getAllOfType(type);
	}

	/**
	 * Retrieve a set of the possible types of weapon proficiencies.
	 * @return Set of the names of the weapon proficiencey types.
	 */
	public static Set getWeaponProfTypes()
	{
		return weaponProfs.getTypes();
	}

	/**
	 * Get a list of the allowed game modes
	 * @return list of the allowed game modes
	 */
	public static List getAllowedGameModes()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getAllowedModes();
		}

		return new ArrayList();
	}

	/**
	 * Set the BioSet
	 * @param aBioSet
	 */
	public static void setBioSet(final BioSet aBioSet)
	{
		bioSet = aBioSet;
	}

	/**
	 * Get the Bio Set
	 * @return the Bio Set
	 */
	public static BioSet getBioSet()
	{
		return bioSet;
	}

	/**
	 * Get the bonus spell map
	 * @return bonus spell map
	 */
	public static Map getBonusSpellMap()
	{
		return SettingsHandler.getGame().getBonusSpellMap();
	}

	/**
	 * Get the campaign by file name
	 * @param aName
	 * @return Campaign
	 */
	public static Campaign getCampaignByFilename(final String aName)
	{
		return getCampaignByFilename(aName, true);
	}

	/**
	 * This method is used to locate a Campaign object based on its file
	 * name (in URL syntax).
	 *
	 * @param aName           String name of file, in URL.toString() format
	 * @param complainOnError boolean true to log an error if the campaign
	 *                        cannot be found
	 * @return Campaign loaded from the given filename, or null if it
	 *         cannot be found
	 */
	public static Campaign getCampaignByFilename(final String aName, final boolean complainOnError)
	{
		final Campaign campaign = (Campaign) campaignMap.get(aName);

		if ((campaign == null) && complainOnError)
		{
			Logging.errorPrint("Could not find campaign by filename: " + aName);
		}

		return campaign;
	}

	/**
	 * Get campaign list
	 * @return campaign list
	 */
	public static List getCampaignList()
	{
		return campaignList;
	}

	/**
	 * Get campaign by name
	 * @param aName
	 * @return Campaign
	 */
	public static Campaign getCampaignNamed(final String aName)
	{
		Campaign currCampaign;
		final Iterator e = getCampaignList().iterator();

		while (e.hasNext())
		{
			currCampaign = (Campaign) e.next();

			if (currCampaign.getName().equalsIgnoreCase(aName))
			{
				return currCampaign;
			}
		}

		Logging.errorPrint("Could not find campaign: " + aName);

		return null;
	}

	/**
	 * Get casting times set
	 * @return casting times set
	 */
	public static SortedSet getCastingTimesSet()
	{
		return castingTimesSet;
	}

	/**
	 * Get class by key
	 * @param aKey
	 * @return Class
	 */
	public static PCClass getClassKeyed(final String aKey)
	{
		return (PCClass) searchPObjectList(getClassList(), aKey);
	}

	/**
	 * Get class list
	 * @return List
	 */
	public static List getClassList()
	{
		return classList;
	}

	/**
	 * Get class by name
	 * @param aName
	 * @return Class
	 */
	public static PCClass getClassNamed(final String aName)
	{
		return getClassNamed(aName, getClassList());
	}

	/**
	 * Get class by name
	 * @param aName
	 * @param aList
	 * @return class by name
	 */
	public static PCClass getClassNamed(final String aName, final List aList)
	{
		PCClass currClass;
		final Iterator e = aList.iterator();

		while (e.hasNext())
		{
			currClass = (PCClass) e.next();

			if (currClass.getName().equalsIgnoreCase(aName))
			{
				return currClass;
			}
		}

		return null;
	}

	/**
	 * Get companion modifier
	 * @param aString
	 * @return companion modifie
	 */
	public static CompanionMod getCompanionMod(final String aString)
	{
		if (aString.length() <= 0)
		{
			return null;
		}

		StringTokenizer aTok = new StringTokenizer(aString.substring(9), "=", false);
		final String classes = aTok.nextToken();
		final int level = Integer.parseInt(aTok.nextToken());
		final Iterator e = getCompanionModList().iterator();

		while (e.hasNext())
		{
			final CompanionMod aComp = (CompanionMod) e.next();
			aTok = new StringTokenizer(classes, ",", false);

			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();

				if (aComp.getLevel(cString) == level)
				{
					return aComp;
				}
			}
		}

		return null;
	}

	/**
	 * Get companion mod list
	 * @return companion mod list
	 */
	public static List getCompanionModList()
	{
		return companionModList;
	}

	/**
	 * Get component set
	 * @return component set
	 */
	public static SortedSet getComponentSet()
	{
		return componentSet;
	}

	/**
	 * Set Country
	 * @param aString
	 */
	public static void setCountry(final String aString)
	{
		country = aString;
	}

	/**
	 * Get country
	 * @return country
	 */
	public static String getCountry()
	{
		return country;
	}

	/**
	 * Returns the string to use for displaying (standard) currency.
	 * @return currency display
	 */
	public static String getCurrencyDisplay()
	{
		return SettingsHandler.getGame().getCurrencyDisplay();
	}

	/**
	 * Set the current PC
	 * @param aCurrentPC
	 */
	public static void setCurrentPC(final PlayerCharacter aCurrentPC)
	{
		currentPC = aCurrentPC;
	}

	/**
	 * @param fromTab
	 * @param col
	 * @param value
	 */
	public static void setCustColumnWidth(final String fromTab, final int col, final int value)
	{
		boolean found = false;
		final String cName = fromTab.concat(Integer.toString(col));
		final String addMe = cName.concat("|").concat(Integer.toString(value));

		if (getCustColumnWidth().isEmpty())
		{
			getCustColumnWidth().add(addMe);
		}

		final int loopMax = getCustColumnWidth().size();

		for (int i = 0; i < loopMax; ++i)
		{
			final StringTokenizer tTok = new StringTokenizer((String) getCustColumnWidth().get(i), "|", false);
			final String tabName = tTok.nextToken();

			if (cName.equals(tabName))
			{
				getCustColumnWidth().set(i, addMe);
				found = true;
			}
		}

		if (!found)
		{
			getCustColumnWidth().add(addMe);
		}
	}

	/**
	 * Get custom column width
	 * @param fromTab
	 * @param col
	 * @return custom column width
	 */
	public static int getCustColumnWidth(final String fromTab, final int col)
	{
		int colSize = 0;
		final String cName = fromTab.concat(Integer.toString(col));

		final int loopMax = getCustColumnWidth().size();

		for (int i = 0; i < loopMax; ++i)
		{
			final StringTokenizer tTok = new StringTokenizer((String) getCustColumnWidth().get(i), "|", false);
			if (tTok.hasMoreTokens())
			{
				final String tabName = tTok.nextToken();

				if (tabName.equals(cName))
				{
					colSize = Integer.parseInt(tTok.nextToken());
				}
			}
		}

		return colSize;
	}
	// END Game Modes Section.

	/**
	 * Get the default path
	 * @return default path
	 */
	public static String getDefaultPath()
	{
		return expandRelativePath(defaultPath);
	}

	/**
	 * Get the backup pcg path
	 * @return backup pcg path
	 */
	public static String getBackupPcgPath()
	{
		return expandRelativePath(backupPcgPath);
	}

	/**
	 * Returns the name of the Default Spell Book, or null if there is no default spell book.
	 * @return default spellbook
	 */
	public static String getDefaultSpellBook()
	{
		String book = null;

		if (SettingsHandler.getGame() != null)
		{
			book = SettingsHandler.getGame().getDefaultSpellBook();
		}

		return book;
	}

	/**
	 * Get the deity by key
	 * @param aKey
	 * @return Deity
	 */
	public static Deity getDeityKeyed(final String aKey)
	{
		return (Deity) searchPObjectList(getDeityList(), aKey);
	}

	/**
	 * Get deity list
	 * @return deity list
	 */
	public static List getDeityList()
	{
		return deityList;
	}

	/**
	 * Get deity by name
	 * @param name
	 * @return Deity
	 */
	public static Deity getDeityNamed(final String name)
	{
		return getDeityNamed(name, getDeityList());
	}

	/**
	 * Get Deity by nmae in list
	 * @param name
	 * @param aList
	 * @return Deity
	 */
	public static Deity getDeityNamed(final String name, final List aList)
	{
		Deity currDeity;
		final Iterator e = aList.iterator();

		while (e.hasNext())
		{
			currDeity = (Deity) e.next();

			if (currDeity.getName().equalsIgnoreCase(name))
			{
				return currDeity;
			}
		}

		return null;
	}

	/**
	 * Get denomination list
	 * @return denomination list
	 */
	public static DenominationList getDenominationList()
	{
		return denomList;
	}

	/**
	 * Get descriptor set
	 * @return descriptor set
	 */
	public static SortedSet getDescriptorSet()
	{
		return descriptorSet;
	}

	/**
	 * Get domain by key
	 * @param aKey
	 * @return Domain
	 */
	public static Domain getDomainKeyed(final String aKey)
	{
		return (Domain) domainMap.get(aKey);
	}

	/**
	 * Get domain list
	 * @return domain list
	 */
	public static List getDomainList()
	{
		return domainList;
	}

	/**
	 * Get domain map
	 * @return domain map
	 */
	public static Map getDomainMap()
	{
		return domainMap;
	}

	/**
	 * Get domain by name
	 * @param name
	 * @return Domain
	 */
	public static Domain getDomainNamed(final String name)
	{
		return (Domain) domainMap.get(name);
	}

	/**
	 * Get duration set
	 * @return duration set
	 */
	public static SortedSet getDurationSet()
	{
		return durationSet;
	}

	/**
	 * Get equipment slot by name
	 * @param aName
	 * @return equipment slot
	 */
	public static EquipSlot getEquipSlotByName(final String aName)
	{
		for (Iterator eI = SystemCollections.getUnmodifiableEquipSlotList().iterator(); eI.hasNext();)
		{
			final EquipSlot es = (EquipSlot) eI.next();

			if (es.getSlotName().equals(aName))
			{
				return es;
			}
		}

		return null;
	}

	/**
	 * Get equipment slot map
	 * @return equipment slot map
	 */
	public static Map getEquipSlotMap()
	{
		return eqSlotMap;
	}

	/**
	 * Set equipment slot type count
	 * @param aString
	 * @param aNum
	 */
	public static void setEquipSlotTypeCount(final String aString, final String aNum)
	{
		getEquipSlotMap().put(aString, aNum);
	}

	/**
	 * returns the # of slots for an equipmentslots Type
	 * The number of slots is define by the NUMSLOTS: line
	 * in the game mode file equipmentslots.lst
	 * @param aType
	 * @return equipment slot type count
	 */
	public static int getEquipSlotTypeCount(final String aType)
	{
		final String aNum = (String) getEquipSlotMap().get(aType);

		if (aNum != null)
		{
			return Integer.parseInt(aNum);
		}
		return 0;
	}

	/**
	 * Add an ability to the system
	 * @param anAbility the abiilty to add
	 * @return true or false
	 */
	public static boolean addAbility (final Ability anAbility)
	{
		return abilityStore.addCategorisable(anAbility);
	}

	/**
	 * Remove the Ability object whose Key matches the String passed in.
	 * @param category
	 * @param aKey The key of the Ability to remove
	 * @return true or false
	 */
	public static boolean removeAbilityKeyed (final String category, final String aKey)
	{
		return abilityStore.removeKeyed(category, aKey);
	}

	/**
	 * Remove the Ability object whose Name matches the String passed in.
	 * @param category
	 * @param aName The key of the Ability to remove
	 * @return a boolean representing whether the ability was removed.  If
	 *         the ability was never there, this will return false (since
	 *         it was not removed).
	 */
	public static boolean removeAbilityNamed (final String category, String aName)
	{
		return abilityStore.removeNamed(category, aName);
	}

	/**
	 * Get the Ability whose Key matches the String passed in
	 * @param category
	 * @param aKey the KEY of the Ability to return
	 * @return Ability
	 */
	public static Ability getAbilityKeyed (final String category, final String aKey)
	{
		return (Ability) abilityStore.getKeyed(category, aKey);
	}

	/**
	 * Get the Ability whose Name matches the String passed in
	 * @param category
	 * @param aName the Name of the Ability to return
	 * @return Ability
	 */
	public static Ability getAbilityNamed (final String category, String aName)
	{
		return (Ability) abilityStore.getNamed(category, aName);
	}

	/**
	 * Get an iterator for the Abilities in the chosen category.  If
	 * passed the string "ALL", will construct an iterator for all abilites
	 * in the system.  The abilites will be sorted in Key order.
	 * @param aCategory the Category of the Abilities to return an iterator for
	 * @return An Iterator
	 */
	public static Iterator getAbilityKeyIterator (String aCategory)
	{
		return abilityStore.getKeyIterator(aCategory);
	}

	/**
	 * Get an iterator for the Abilities in the chosen category.  If
	 * passed the string "ALL", will construct an iterator for all abilites
	 * in the system.  The abilites will be sorted in Name order.
	 * @param aCategory the Category of the Abilities to return an iterator for
	 * @return An Iterator
	 */
	public static Iterator getAbilityNameIterator (String aCategory)
	{
		return abilityStore.getNameIterator(aCategory);
	}

	/**
	 * For the rare method that does actually need a list of Ability
	 * objects rather than an iterator.
	 * @param aCategory the category of object to return
	 * @return an unmodifiable list of the Ability objects currently loaded
	 */
	public static List getUnmodifiableAbilityList(String aCategory)
	{
		return abilityStore.getUnmodifiableList(aCategory);
	}

	/**
	 * Test to see if a weapon is Finesseable or not
	 * @param eq
	 * @param aPC
	 * @return TRUE if finessable
	 **/
	public static boolean isFinessable(final Equipment eq, final PlayerCharacter aPC)
	{
		if (eq.isType("Finesseable"))
		{
			return true;
		}
		else if (eq.hasWield())
		{
			final WieldCategory wCat = effectiveWieldCategory(aPC, eq);

			if (wCat != null)
			{
				return (wCat.isFinessable());
			}
		}

		return false;
	}

	/**
	 * Get game mode AC abbreviation
	 * @return game mode AC abbreviation
	 */
	public static String getGameModeACAbbrev()
	{
		return SettingsHandler.getGame().getACAbbrev();
	}

	/**
	 * Get game mode AC text
	 * @return game mode AC tex
	 */
	public static String getGameModeACText()
	{
		return SettingsHandler.getGame().getACText();
	}

	/**
	 * Get game mode align text
	 * @return game mode align text
	 */
	public static String getGameModeAlignmentText()
	{
		return SettingsHandler.getGame().getAlignmentText();
	}

	/**
	 * Get Game mode alternative HP abbreviation
	 * @return Game mode alternative HP abbreviation
	 */
	public static String getGameModeAltHPAbbrev()
	{
		return SettingsHandler.getGame().getAltHPAbbrev();
	}

	/**
	 * Get game mode alternative HP Text
	 * @return game mode alternative HP Text
	 */
	public static String getGameModeAltHPText()
	{
		return SettingsHandler.getGame().getAltHPText();
	}

	/**
	 * Get game mode base spell DC
	 * @return game mode base spell DC
	 */
	public static String getGameModeBaseSpellDC()
	{
		return SettingsHandler.getGame().getSpellBaseDC();
	}

	/**
	 * Get game mode damage resistance text
	 * @return game mode damage resistance text
	 */
	public static String getGameModeDamageResistanceText()
	{
		return SettingsHandler.getGame().getDamageResistanceText();
	}

	/**
	 * Get game mode non proficiency penalty
	 * @return game mode non proficiency penalty
	 */
	public static int getGameModeNonProfPenalty()
	{
		return SettingsHandler.getGame().getNonProfPenalty();
	}

	/**
	 * Get the game mode point pool name
	 * @return game mode point pool name
	 */
	public static String getGameModePointPoolName()
	{
		return SettingsHandler.getGame().getPointPoolName();
	}

	/**
	 * TRUE if the game mode has a point pool
	 * @return TRUE if the game mode has a point pool
	 */
	public static boolean getGameModeHasPointPool()
	{
		return getGameModePointPoolName().length() != 0;
	}

	/**
	 * Get the abbreviation to be used for hit points.
	 *
	 * @return String
	 */
	public static String getGameModeHPAbbrev()
	{
		return SettingsHandler.getGame().getHPAbbrev();
	}

	/**
	 * Get the game mode HP Formula
	 * @return game mode HP Formula
	 */
	public static String getGameModeHPFormula()
	{
		return SettingsHandler.getGame().getHPFormula();
	}

	/**
	 * Get the long definition for hit points.
	 *
	 * @return String
	 */
	public static String getGameModeHitPointText()
	{
		return SettingsHandler.getGame().getHPText();
	}

	/**
	 * Get the game mode rank mod formula
	 * @return game mode rank mod formula
	 */
	public static String getGameModeRankModFormula()
	{
		return SettingsHandler.getGame().getRankModFormula();
	}

	/**
	 * Return TRUE if game mode shows class defense
	 * @return TRUE if game mode shows class defense
	 */
	public static boolean getGameModeShowClassDefense()
	{
		return SettingsHandler.getGame().getShowClassDefense();
	}

	/**
	 * TRUE if game mode shows spell tab
	 * @return TRUE if game mode shows spell tab
	 */
	public static boolean getGameModeShowSpellTab()
	{
		return SettingsHandler.getGame().getTabShown(Constants.TAB_SPELLS);
	}

	/**
	 * Get game mode skill cost for class
	 * @return game modeskill cost for class
	 */
	public static int getGameModeSkillCost_Class()
	{
		return SettingsHandler.getGame().getSkillCost_Class();
	}

	/**
	 * Get game mode skill cost for cross-class
	 * @return game modeskill cost for cross-class
	 */
	public static int getGameModeSkillCost_CrossClass()
	{
		return SettingsHandler.getGame().getSkillCost_CrossClass();
	}

	/**
	 * Get game mode skill cost for exclusive
	 * @return game modeskill cost for exclusive
	 */
	public static int getGameModeSkillCost_Exclusive()
	{
		return SettingsHandler.getGame().getSkillCost_Exclusive();
	}

	/**
	 * Get game mode spell range formula
	 * @param aRange
	 * @return game mode spell range formula
	 */
	public static String getGameModeSpellRangeFormula(final String aRange)
	{
		return SettingsHandler.getGame().getSpellRangeFormula(aRange);
	}

	/**
	 * Get game mode unit set
	 * @return game mode unit set
	 */
	public static UnitSet getGameModeUnitSet()
	{
		return SettingsHandler.getGame().getUnitSet();
	}

	/**
	 * Get game mode variable display (2) name
	 * @return game mode variable display (2) name
	 */
	public static String getGameModeVariableDisplay2Name()
	{
		return SettingsHandler.getGame().getVariableDisplay2Name();
	}

	/**
	 * Get game mode variable display (2) text
	 * @return game mode variable display (2) text
	 */
	public static String getGameModeVariableDisplay2Text()
	{
		return SettingsHandler.getGame().getVariableDisplay2Text();
	}

	/**
	 * Get game mode variable display (3) name
	 * @return game mode variable display (3) name
	 */
	public static String getGameModeVariableDisplay3Name()
	{
		return SettingsHandler.getGame().getVariableDisplay3Name();
	}

	/**
	 * Get game mode variable display (2) text
	 * @return game mode variable display (2) text
	 */
	public static String getGameModeVariableDisplay3Text()
	{
		return SettingsHandler.getGame().getVariableDisplay3Text();
	}

	/**
	 * Get game mode variable display name
	 * @return game mode variable display name
	 */
	public static String getGameModeVariableDisplayName()
	{
		return SettingsHandler.getGame().getVariableDisplayName();
	}

	/**
	 * Gets the information for Displaying a Variable.
	 *
	 * @return String
	 */
	public static String getGameModeVariableDisplayText()
	{
		return SettingsHandler.getGame().getVariableDisplayText();
	}

	/**
	 * Get global deity list
	 * @return global deity lis
	 */
	public static List getGlobalDeityList()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getDeityList();
		}

		return new ArrayList();
	}

	/**
	 * Get game mode square size
	 * @return game mode square size
	 */
	public static double getGameModeSquareSize()
	{
		return SettingsHandler.getGame().getSquareSize();
	}

	/**
	 * Return TRUE if in a particular game mode
	 * @param gameMode
	 * @return TRUE if in a particular game mode
	 */
	public static boolean isInGameMode(final String gameMode)
	{
		if ((gameMode.length() == 0)
			|| ((SettingsHandler.getGame() != null) && gameMode.equalsIgnoreCase(SettingsHandler.getGame().getName())))
		{
			return true;
		}

		return false;
	}

	/**
	 * Get kit info
	 * @return kit info
	 */
	public static List getKitInfo()
	{
		return kitList;
	}

	/**
	 * Get kit by key
	 * @param aKey
	 * @return Kit
	 */
	public static Kit getKitKeyed(final String aKey)
	{
		final Iterator e = kitList.iterator();

		while (e.hasNext())
		{
			final Kit aKit = (Kit) e.next();

			if (aKit.getKeyName().equals(aKey))
			{
				return aKit;
			}
		}

		return null;
	}

	/**
	 * Set language
	 * @param aString
	 */
	public static void setLanguage(final String aString)
	{
		language = aString;
	}

	/**
	 * Get language
	 * @return language
	 */
	public static String getLanguage()
	{
		return language;
	}

	/**
	 * Get language list
	 * @return language list
	 */
	public static List getLanguageList()
	{
		return languageList;
	}

	/**
	 * Retrieve a language object based on the name. Matches on name
	 * and type are case insensitive.
	 *
	 * @param name The name of the language to be found.
	 * @return The matching language, or null if none exists.
	 */
	public static Language getLanguageNamed(final String name)
	{
		return getLanguageNamed(name, null);
	}

	/**
	 * Retrieve a language object based on the name and type. Matches on name
	 * and type are case insensitive. If there is no type restriction, then
	 * null should be used for the type.
	 *
	 * @param name The name of the language to be found.
	 * @param langType The type of the language to be found.
	 * @return The matching language, or null if none exists.
	 */
	public static Language getLanguageNamed(final String name, final String langType)
	{
		if (name.equalsIgnoreCase("ALL") || name.equalsIgnoreCase("ANY"))
		{
			return Language.getAllLanguage();
		}

		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();

			if (aLang.getName().equalsIgnoreCase(name))
			{
				if (langType == null || aLang.isType(langType))
				{
					return aLang;
				}
			}
		}

		return null;
	}

	/**
	 * returns a HashMap of LevelInfo objects
	 * @return Map
	 */
	public static Map getLevelInfo()
	{
		return SettingsHandler.getGame().getLevelInfo();
	}

	/**
	 * Get load strings
	 * @return List of Strings
	 */
	public static List getLoadStrings()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getLoadStrings();
		}

		return new ArrayList();
	}

	/**
	 * Get long currency display
	 * @return long currency display
	 */
	public static String getLongCurrencyDisplay()
	{
		return SettingsHandler.getGame().getLongCurrencyDisplay();
	}

	/**
	 * Get PCC class type list
	 * @return PCC class type list
	 */
	public static List getPCClassTypeList()
	{
		return pcClassTypeList;
	}

	/**
	 * Set PC List
	 * @param argPcList
	 */
	public static void setPCList(final List argPcList)
	{
		pcList = argPcList;
	}

	/**
	 * Get PC List
	 * @return List of Pcs
	 */
	public static List getPCList()
	{
		return pcList;
	}

	/**
	 * Get pantheons
	 * @return Sorted set of pantheons
	 */
	public static SortedSet getPantheons()
	{
		return getPantheonsSet();
	}

	/**
	 * Get paper count
	 * @return paper count
	 */
	public static int getPaperCount()
	{
		return SystemCollections.getUnmodifiablePaperInfo().size();
	}

	/**
	 * Get paper info
	 * @param infoType
	 * @return paper info
	 */
	public static String getPaperInfo(final int infoType)
	{
		return getPaperInfo(getSelectedPaper(), infoType);
	}

	/**
	 * Get paper info
	 * @param idx
	 * @param infoType
	 * @return paper info
	 */
	public static String getPaperInfo(final int idx, final int infoType)
	{
		if ((idx < 0) || (idx >= SystemCollections.getUnmodifiablePaperInfo().size()))
		{
			return null;
		}

		final PaperInfo pi = (PaperInfo) SystemCollections.getUnmodifiablePaperInfo().get(idx);

		return pi.getPaperInfo(infoType);
	}

	/**
	 * Get's Race from raceMap() based on aKey
	 * @param aKey
	 * @return keyed Race
	 */
	public static Race getRaceKeyed(final String aKey)
	{
		return (Race) getRaceMap().get(aKey);
	}

	/**
	 * This method gets the race map
	 * @return race map
	 */
	public static Map getRaceMap()
	{
		return raceMap;
	}

	/**
	 * Get's Race from raceMap() based on aName
	 * @param aName
	 * @return named race
	 */
	public static Race getRaceNamed(final String aName)
	{
		return (Race) getRaceMap().get(aName);
	}

	/**
	 * This method gets the available race types as a set.
	 * @return race types
	 */
	public static SortedSet getRaceTypes()
	{
		return raceTypesSet;
	}

	/**
	 * Get a random int
	 * @return random int
	 */
	public static int getRandomInt()
	{
		return getRandom().nextInt();
	}

	/**
	 * Get ranges set
	 * @return set of ranges
	 */
	public static SortedSet getRangesSet()
	{
		return rangesSet;
	}

	/**
	 * Sets the root frame
	 * The root frame is the container in which all
	 * other panels, frame etc are placed.
	 *
	 * @param frame the <code>PCGen_Frame1</code> which is to be root
	 */
	public static void setRootFrame(final JFrame frame)
	{
		rootFrame = frame;
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return the <code>rootFrame</code> property
	 */
	public static JFrame getRootFrame()
	{
		return rootFrame;
	}

	/**
	 * Set the current frame
	 * @param frame
	 */
	public static void setCurrentFrame(final JFrame frame)
	{
		currentFrame = frame;
	}

	/**
	 * Get the current frame
	 * @return current frame
	 */
	public static JFrame getCurrentFrame()
	{
		if (currentFrame == null)
		{
			return rootFrame;
		}
		return currentFrame;
	}

	/**
	 * Return an <b>unmodifiable</b> version of the saSet.
	 *
	 * @return SortedSet
	 */
	public static SortedSet getSASet()
	{
		return saSet;
	}

	/**
	 * Get the save info set
	 * @return save info set
	 */
	public static SortedSet getSaveInfoSet()
	{
		return saveInfoSet;
	}

	/**
	 * Get the section 15
	 * @return section 15
	 */
	public static StringBuffer getSection15()
	{
		return section15;
	}

	/**
	 * Get selected paper
	 * @return selected paper
	 */
	public static int getSelectedPaper()
	{
		return selectedPaper;
	}

	/**
	 * Get skill by key
	 * @param aKey
	 * @return Skill
	 */
	public static Skill getSkillKeyed(final String aKey)
	{
		return (Skill) searchPObjectList(getSkillList(), aKey);
	}

	/**
	 * Get list of skills
	 * @return list of skills
	 */
	public static List getSkillList()
	{
		return skillList;
	}

	/**
	 * Retrieve those skills in the global skill list that match the
	 * supplied visibility level.
	 *
	 * @param visibility What level of visibility skills are desired.
	 * @return A list of the skills matching the visibility criteria.
	 */
	public static List getPartialSkillList(final int visibility)
	{
		// Now select the required set of skills, based on their visibility.
		ArrayList aList = new ArrayList();
		for (Iterator iter = getSkillList().iterator(); iter.hasNext();)
		{
			final Skill aSkill = (Skill) iter.next();
			final int skillVis = aSkill.isVisible();

			if (visibility == Skill.VISIBILITY_DEFAULT
				|| skillVis == Skill.VISIBILITY_DEFAULT
				|| skillVis == visibility)
			{
				aList.add(aSkill);
			}

		}
		return aList;
	}

	/**
	 * Get skill by name
	 * @param name
	 * @return Skill
	 */
	public static Skill getSkillNamed(final String name)
	{
		Skill currSkill;

		for (Iterator skillIter = getSkillList().iterator(); skillIter.hasNext();)
		{
			currSkill = (Skill) skillIter.next();

			if (currSkill.getName().equalsIgnoreCase(name))
			{
				return currSkill;
			}
		}

		return null;
	}

	/**
	 * Return TRUE if the equipment type is hidden
	 * @param aType
	 * @return TRUE if the equipment type is hidden
	 */
	public static boolean isEquipmentTypeHidden(final String aType)
	{
		return SettingsHandler.getGame().isEquipmentTypeHidden(aType);
	}

	/**
	 * Return TRUE if the ability type is hidden
	 * @param aType
	 * @return TRUE if the ability type is hidden
	 */
	public static boolean isAbilityTypeHidden(final String aType)
	{
		return SettingsHandler.getGame().isAbilityTypeHidden(aType);
	}

	/**
	 * Return TRUE if the skill type is hidden
	 * @param aType
	 * @return TRUE if the skill type is hidden
	 */
	public static boolean isSkillTypeHidden(final String aType)
	{
		return SettingsHandler.getGame().isSkillTypeHidden(aType);
	}

	/**
	 * Set sroted flag
	 * @param sorted
	 */
	public static void setSorted(final boolean sorted)
	{
		setD_sorted(sorted);
	}

	/**
	 * Set source display
	 * @param sourceType
	 */
	public static void setSourceDisplay(final int sourceType)
	{
		sourceDisplay = sourceType;
	}

	/**
	 * Get source display
	 * @return source display
	 */
	public static int getSourceDisplay()
	{
		return sourceDisplay;
	}

	/**
	 * Get spell by key
	 * @param aKey
	 * @return Spell
	 */
	public static Spell getSpellKeyed(final String aKey)
	{
		final Object obj = getSpellMap().get(aKey);

		if (obj != null)
		{
			if (obj instanceof Spell)
			{
				return (Spell) getSpellMap().get(aKey);
			}

			if (obj instanceof ArrayList)
			{
				return (Spell) ((ArrayList) obj).get(0);
			}
		}

		return null;
	}

	/**
	 * Get spell map
	 * @return spell map
	 */
	public static Map getSpellMap()
	{
		return spellMap;
	}

	/**
	 * Get spell by name
	 * @param name
	 * @return spell
	 */
	public static Spell getSpellNamed(final String name)
	{
		return getSpellKeyed(name);
	}

	/**
	 * Get spell points
	 * @return spell points
	 */
	public static String getSpellPoints()
	{
		return spellPoints;
	}

	/**
	 * Returns a List of Spell with following criteria:
	 *
	 * @param level      (optional, ignored if < 0),
	 * @param className  (optional, ignored if "")
	 * @param domainName (optional, ignored if "")
	 *                   at least one of className and domainName must not be ""
	 * @return a List of Spell
	 */
	public static List getSpellsIn(final int level, final String className, final String domainName)
	{
		final List aList = new ArrayList();
		final StringBuffer aBuf = new StringBuffer();
		String spellType = "";

		if (className.length() > 0)
		{
			final PCClass aClass;

			if (className.indexOf('|') < 0)
			{
				aClass = getClassNamed(className);
				aBuf.append("CLASS|").append(className);
			}
			else
			{
				aClass = getClassNamed(className.substring(className.indexOf("|") + 1));
				aBuf.append(className);
			}

			if (aClass != null)
			{
				spellType = aClass.getSpellType();
			}
		}

		if (domainName.length() > 0)
		{
			if (aBuf.length() > 0)
			{
				aBuf.append('|');
			}

			if (domainName.indexOf('|') < 0)
			{
				aBuf.append("DOMAIN|").append(domainName);
			}
			else
			{
				aBuf.append(domainName);
			}

			spellType = "DIVINE";
		}

		for (Iterator i = spellMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = (String) i.next();
			final Object obj = spellMap.get(aKey);

			if (obj instanceof ArrayList)
			{
				for (Iterator j = ((ArrayList) obj).iterator(); j.hasNext();)
				{
					final Spell aSpell = (Spell) j.next();

					if (aSpell.levelForKeyContains(aBuf.toString(), level, currentPC)
						&& (aSpell.getType().indexOf(spellType.toUpperCase()) >= 0))
					{
						aList.add(aSpell);
					}
				}
			}
			else if (obj instanceof Spell)
			{
				final Spell aSpell = (Spell) obj;

				if (aSpell.levelForKeyContains(aBuf.toString(), level, currentPC))
				{
					aList.add(aSpell);
				}
			}
		}

		return aList;
	}

	/**
	 * Get Sr Set
	 * @return Sr Set
	 */
	public static SortedSet getSrSet()
	{
		return srSet;
	}

	/**
	 * Get stat set
	 * @return stat set
	 */
	public static SortedSet getStatSet()
	{
		return statSet;
	}

	/**
	 * Get sub schools
	 * @return sub schools
	 */
	public static SortedSet getSubschools()
	{
		return getSubschoolsSet();
	}

	/**
	 * Get Target set
	 * @return target set
	 */
	public static SortedSet getTargetSet()
	{
		return targetSet;
	}

	/**
	 * Get template by key
	 * @param aKey
	 * @return Template
	 */
	public static PCTemplate getTemplateKeyed(final String aKey)
	{
		return (PCTemplate) searchPObjectList(getTemplateList(), aKey);
	}

	/**
	 * Get the template list
	 * @return list of tempaltes
	 */
	public static List getTemplateList()
	{
		return templateList;
	}

	/**
	 * Get a template by name
	 * @param name
	 * @return Template
	 */
	public static PCTemplate getTemplateNamed(final String name)
	{
		PCTemplate currTemp;

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			currTemp = (PCTemplate) e.next();

			if (currTemp.getName().equalsIgnoreCase(name))
			{
				return currTemp;
			}
		}

		return null;
	}

	/**
	 * Get type for spells set
	 * @return type for spells set
	 */
	public static SortedSet getTypeForSpells()
	{
		return typeForSpellsSet;
	}

	/**
	 * Add to the type for spells set
	 * @param arg
	 */
	public static void addTypeForSpells(final String arg)
	{
		typeForSpellsSet.add(arg);
	}

	/**
	 * Sets whether to use the GUI or not
	 * @param aBool
	 */
	public static void setUseGUI(final boolean aBool)
	{
		useGUI = aBool;
	}

	/**
	 * TRUE if using UI
	 * @return TRUE if using UI
	 */
	public static boolean getUseGUI()
	{
		return isUseGUI();
	}

	/**
	 * Get map of VISIONs
	 * @return map of VISIONs
	 */
	public static Map getVisionMap()
	{
		return visionMap;
	}

	/**
	 * @param pc
	 * @param weapon
	 * @return true if the weapon is light for the specified pc
	 */
	public static boolean isWeaponLightForPC(final PlayerCharacter pc, final Equipment weapon)
	{
		if ((pc == null) || (weapon == null))
		{
			return false;
		}

		if (weapon.hasWield())
		{
			if (Globals.checkRule(RuleConstants.SIZECAT))
			{
				// In 3.5, a 'Light' weapon is light
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					return (wCat.getName().equals("Light"));
				}
			}
			else if (Globals.checkRule(RuleConstants.SIZEOBJ))
			{
				// Use Object Size to determin if weapon light
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					// use 3.5 code to get Object Size
					return (pc.sizeInt() > wCat.getObjectSizeInt(weapon));
				}
				// Must be in 3.0 mode or something
				return (pc.sizeInt() > weapon.sizeInt());
			}
		}
		else
		{
			// Old Weapon code
			// if a PC is a size category larger than
			// the weapon it's considered light
			return (pc.sizeInt() > weapon.sizeInt());
		}

		return false;
	}

	/**
	 * @param pc
	 * @param weapon
	 * @param wp
	 * @return true if the weapon is one-handed for the specified pc
	 */
	public static boolean isWeaponOneHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		return isWeaponOneHanded(pc, weapon, wp, false);
	}

	/**
	 * Returns TRUE if weapon is one handed
	 * @param pc
	 * @param weapon
	 * @param wp
	 * @param baseOnly
	 * @return TRUE if weapon is one handed
	 */
	public static boolean isWeaponOneHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp,
		final boolean baseOnly)
	{
		if ((pc == null) || (weapon == null) || (wp == null))
		{
			return false;
		}

		if (handsRequired(pc, weapon, wp) == 1)
		{
			if (weapon.hasWield())
			{
				if (Globals.checkRule(RuleConstants.SIZECAT))
				{
					// Check was done in handsRequired()
					return true;
				}
				else if (Globals.checkRule(RuleConstants.SIZEOBJ))
				{
					// Use Object Size
					final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

					if (wCat != null)
					{
						// compare Object Sizes
						return (pc.sizeInt() >= wCat.getObjectSizeInt(weapon));
					}
				}
			}
			else
			{
				// Old Code
				int pcSize = pc.sizeInt();

				if (!baseOnly)
				{
					pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE");
				}

				return (pcSize >= weapon.sizeInt());
			}
		}

		return false;
	}

	/**
	 * @param pc
	 * @param weapon
	 * @return true if the weapon is too large or to small for PC
	 **/
	public static boolean isWeaponOutsizedForPC(final PlayerCharacter pc, final Equipment weapon)
	{
		if ((pc == null) || (weapon == null))
		{
			return true;
		}

		final int overSize = pc.sizeInt() + 1;
		final int underSize = pc.sizeInt() - 1;

		if (weapon.hasWield())
		{
			if (Globals.checkRule(RuleConstants.SIZECAT))
			{
				// 3.5 rules
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					return (wCat.getHands() > 2);
				}
			}
			else if (Globals.checkRule(RuleConstants.SIZEOBJ))
			{
				// Use Object Size
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					// use 3.5 code to get Object Size
					if (wCat.getObjectSizeInt(weapon) > overSize)
					{
						return true;
					}
					else if (wCat.getObjectSizeInt(weapon) < underSize)
					{
						return true;
					}
				}
				else
				{
					// Must be in 3.0 mode or something
					if (weapon.sizeInt() > overSize)
					{
						return true;
					}
					else if (weapon.sizeInt() < underSize)
					{
						return true;
					}
				}
			}
		}
		else
		{
			// 3.0 Rules
			if (weapon.sizeInt() > overSize)
			{
				return true;
			}
			else if (weapon.sizeInt() < (underSize - 1))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Get copy of weapon prof array
	 * @return copy of weapon prof array
	 */
	public static List getWeaponProfArrayCopy()
	{
		return weaponProfs.getArrayCopy();
	}

	/**
	 * Searches for an exact key match.
	 *
	 * @param aKey
	 * @return an exact match or null
	 */
	public static WeaponProf getWeaponProfKeyed(final String aKey)
	{
		return weaponProfs.getKeyed(aKey);
	}

	/**
	 * Searches for an exact name match.
	 *
	 * @param name
	 * @return an exact match or null
	 */
	public static WeaponProf getWeaponProfNamed(final String name)
	{
		return weaponProfs.getNamed(name);
	}

	/**
	 * Get weapon prof names
	 * @param delim
	 * @param addArrayMarkers
	 * @return weapon prof names
	 */
	public static String getWeaponProfNames(final String delim, final boolean addArrayMarkers)
	{
		return weaponProfs.getNames(delim, addArrayMarkers);
	}

	/**
	 * Get the weapon proficiency size
	 * @return weapon proficiency size
	 */
	public static int getWeaponProfSize()
	{
		return weaponProfs.size();
	}

	/**
	 * @param pc
	 * @param weapon
	 * @return true if the weapon is too large for the specified pc.
	 **/
	public static boolean isWeaponTooLargeForPC(final PlayerCharacter pc, final Equipment weapon)
	{
		if ((pc == null) || (weapon == null))
		{
			return false;
		}

		if (weapon.hasWield())
		{
			if (Globals.checkRule(RuleConstants.SIZECAT))
			{
				// 3.5 rules
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					return (wCat.getHands() > 2);
				}
			}
			else if (Globals.checkRule(RuleConstants.SIZEOBJ))
			{
				// Use Object Size
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);
				final int overSize = pc.sizeInt() + 1;

				if (wCat != null)
				{
					// use 3.5 code to get Object Size
					return (wCat.getObjectSizeInt(weapon) > overSize);
				}
				// Must be in 3.0 mode or something
				return (weapon.sizeInt() > overSize);
			}
		}
		else
		{
			// 3.0 Rules
			return (weapon.sizeInt() > (pc.sizeInt() + 1));
		}

		return false;
	}

	/**
	 * @param pc
	 * @param weapon
	 * @param wp
	 * @return true if the weapon is two-handed for the specified pc
	 */
	public static boolean isWeaponTwoHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		return isWeaponTwoHanded(pc, weapon, wp, false);
	}

	/**
	 * Returns an Iterator over the weapontypes
	 *
	 * @return The iterator of weapon types
	 */
	public static Iterator getWeaponTypesIterator()
	{
		return weaponTypes.iterator();
	}

	/**
	 * This method is called by the persistence layer to
	 * add a campaign that it has located to the globl campaign list.
	 *
	 * @param campaign Campaign loaded from persistence to add to the
	 *                 Global campaign list
	 */
	public static void addCampaign(final Campaign campaign)
	{
		campaignMap.put(campaign.getSourceFile(), campaign);
		campaignList.add(campaign);
	}

	/**
	 * Add domain to map and list
	 * @param nextDomain
	 */
	public static void addDomain(final Domain nextDomain)
	{
		domainMap.put(nextDomain.getKeyName(), nextDomain);
		domainList.add(nextDomain);
	}

	/**
	 * Add duration to set
	 * @param aString
	 */
	public static void addDurationSet(final String aString)
	{
		durationSet.add(aString);
	}

	// Spell info section

	/**
	 * Add to the spell casting times set
	 * @param aString
	 */
	public static void addSpellCastingTimesSet(final String aString)
	{
		castingTimesSet.add(aString);
	}

	/**
	 * Add to the spell components set
	 * @param aString
	 */
	public static void addSpellComponentSet(final String aString)
	{
		componentSet.add(aString);
	}

	/**
	 * Add to the spell descriptors set
	 * @param aString
	 */
	public static void addSpellDescriptorSet(final String aString)
	{
		descriptorSet.add(aString);
	}

	/**
	 * Add to spell ranges set
	 * @param aString
	 */
	public static void addSpellRangesSet(final String aString)
	{
		rangesSet.add(aString);
	}

	/**
	 * add to spell save info set
	 * @param aString
	 */
	public static void addSpellSaveInfoSet(final String aString)
	{
		saveInfoSet.add(aString);
	}

	/**
	 * Add to spell sr set
	 * @param aString
	 */
	public static void addSpellSrSet(final String aString)
	{
		srSet.add(aString);
	}

	/**
	 * Add to spell stat set
	 * @param aString
	 */
	public static void addSpellStatSet(final String aString)
	{
		statSet.add(aString);
	}

	/**
	 * Add to spell target set
	 * @param aString
	 */
	public static void addSpellTargetSet(final String aString)
	{
		targetSet.add(aString);
	}

	// Special Abilities List

	/**
	 * Add to the saList.
	 *
	 * @param sa
	 */
	public static void addToSASet(final SpecialAbility sa)
	{
		saSet.add(sa);
	}

	/**
	 * Add to list of unique weapon profs (Strings)
	 * @param dest
	 */
	public static void addUniqueWeaponProfsAsStringTo(final List dest)
	{
		weaponProfs.addUniqueAsStringTo(dest);
	}

	/**
	 * Add a weapon proficiency
	 * @param wp
	 */
	public static void addWeaponProf(final WeaponProf wp)
	{
		weaponProfs.add(wp);
	}

	/**
	 * Adjust damage
	 * @param aDamage
	 * @param baseSize
	 * @param finalSize
	 * @return adjusted damage
	 */
	public static String adjustDamage(final String aDamage, int baseSize, final int finalSize)
	{
		String result = aDamage;
		int multiplier = 1;

		if (baseSize < finalSize)
		{
			String upString = getDamageUpKey(aDamage);

			if (upString != null)
			{
				StringTokenizer aTok = new StringTokenizer(upString, "|");
				multiplier = Integer.parseInt(aTok.nextToken());
				upString = aTok.nextToken();
				aTok = new StringTokenizer(upString, ",");

				while ((baseSize < finalSize) && aTok.hasMoreTokens())
				{
					result = aTok.nextToken();
					baseSize++;
				}
			}
		}
		else
		{
			if (baseSize > finalSize)
			{
				String downString = getDamageDownKey(aDamage);

				if (downString != null)
				{
					StringTokenizer aTok = new StringTokenizer(downString, "|");
					multiplier = Integer.parseInt(aTok.nextToken());
					downString = aTok.nextToken();
					aTok = new StringTokenizer(downString, ",");

					while ((baseSize > finalSize) && aTok.hasMoreTokens())
					{
						result = aTok.nextToken();
						baseSize--;
					}
				}
			}
		}

		if (multiplier > 1)
		{
			final RollInfo aRollInfo = new RollInfo(result);
			aRollInfo.times *= multiplier;
			result = aRollInfo.toString();
		}

		return result;
	}

	/**
	 * Return true if resizing the equipment will have any "noticable" effect
	 * checks for cost modification, armor bonus, weight, capacity
	 * @param aPC
	 *
	 * @param aEq
	 * @param typeList
	 * @return TRUE or FALSE
	 */
	public static boolean canResizeHaveEffect(final PlayerCharacter aPC, final Equipment aEq, List typeList)
	{
		// cycle through typeList and see if it matches one in the BONUS:ITEMCOST|TYPE=etc on sizeadjustment
		if (typeList == null)
		{
			typeList = aEq.typeList();
		}

		for (int iSize = 0; iSize < SettingsHandler.getGame().getSizeAdjustmentListSize(); ++iSize)
		{
			final SizeAdjustment sadj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize);

			if ((!CoreUtility.doublesEqual(sadj.getBonusTo(aPC, "ITEMCOST", typeList, 1.0), 1.0))
				|| (aEq.isArmor()
				|| (aEq.isShield() && !CoreUtility.doublesEqual(sadj.getBonusTo(aPC, "ACVALUE", typeList, 1.0), 1.0)))
				|| (!CoreUtility.doublesEqual(aEq.getWeightAsDouble(aPC), 0.0)
				&& !CoreUtility.doublesEqual(sadj.getBonusTo(aPC, "ITEMWEIGHT", typeList, 1.0), 1.0))
				|| (aEq.isContainer() && !CoreUtility.doublesEqual(sadj.getBonusTo(aPC, "ITEMCAPACITY", typeList, 1.0), 1.0)))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Find out the state of a PRERULE check
	 * @param aKey
	 * @return true or false
	 */
	public static boolean checkRule(final String aKey)
	{
		boolean aBool = false;
		final GameMode gameMode = SettingsHandler.getGame();

		if (gameMode.hasRuleCheck(aKey))
		{
			aBool = gameMode.getRuleCheck(aKey);
			if (SettingsHandler.hasRuleCheck(aKey))
			{
				aBool = SettingsHandler.getRuleCheck(aKey);
			}
		}


		return aBool;
	}

	/**
	 * Choose from a list
	 * @param title
	 * @param choiceList
	 * @param selectedList
	 * @param pool
	 * @return a Choice
	 */
	public static String chooseFromList(final String title, final List choiceList, final List selectedList,
			final int pool)
	{
		return chooseFromList(title, choiceList, selectedList, pool, false);
	}

	/**
	 * Choose from a list
	 * @param title
	 * @param choiceList
	 * @param selectedList
	 * @param pool
	 * @param forceChoice
	 * @return chosen item
	 */
	public static String chooseFromList(final String title, final List choiceList, final List selectedList,
		final int pool, final boolean forceChoice)
	{
		final List justSelectedList = getChoiceFromList(title, choiceList, selectedList, pool, forceChoice);

		if (justSelectedList.size() != 0)
		{
			return (String) justSelectedList.get(0);
		}

		return null;
	}

	/**
	 * This method is called by the persistence layer to
	 * clear the global campaigns for a refresh.
	 */
	public static void clearCampaignsForRefresh()
	{
		emptyLists();
		campaignMap.clear();
		campaignList.clear();
	}

	/**
	 * Clear out the SA list.
	 */
	public static void clearSASet()
	{
		saSet.clear();
	}

	/**
	 * Dsiplay happy message that lsts are loaded
	 * @return true or false
	 */
	public static boolean displayListsHappy()
	{
		Logging.debugPrint("Number of objects loaded. The following should all be greater than 0:");
		Logging.debugPrint("Races=" + getRaceMap().size());
		Logging.debugPrint("Classes=" + getClassList().size());
		Logging.debugPrint("Skills=" + getSkillList().size());
		Logging.debugPrint("Feats=" + getUnmodifiableAbilityList("FEAT").size());
		Logging.debugPrint("Equipment=" + EquipmentList.size());
		Logging.debugPrint("WeaponProfs=" + getWeaponProfSize());
		Logging.debugPrint("Kits=" + kitList.size());
		Logging.debugPrint("Templates=" + templateList.size());

		//
		// NOTE: If you add something here be sure to update the debug output in pcgen.gui.MainSource in loadCampaigns_actionPerformed
		//
		if ((getRaceMap().size() == 0) || (getClassList().size() == 0) || (getSkillList().size() == 0)
			|| (getUnmodifiableAbilityList("FEAT").size() == 0) || (EquipmentList.size() == 0)
			|| (getWeaponProfSize() == 0))
		{
			return false;
		}

		return true;
	}

	/**
	 * Return WieldCategory based on PC vs Equipment size
	 * @param aPC
	 * @param eq
	 * @return WeildCategory
	 */
	public static WieldCategory effectiveWieldCategory(final PlayerCharacter aPC, final Equipment eq)
	{
		// Get this equipments WieldCategory from gameMode
		WieldCategory wCat = SettingsHandler.getGame().getWieldCategory(eq.getWield());

		if (wCat == null)
		{
			return null;
		}

		// Get the starting effective wield category
		String ewName = wCat.getWieldCategory(aPC, eq);
		wCat = SettingsHandler.getGame().getWieldCategory(ewName);

		// Change the effective Wield Category based on bonuses
		WieldCategory bonusCat = wCat;

		final String valString = SettingsHandler.getGame().getWCStepsFormula();
		final String eqVar = "EQ:" + eq.getNonHeadedName();
		final int sizeDiff = eq.getVariableValue(valString, eqVar, aPC).intValue();

		int aBump = 0;

		// See if there is a bonus associated with just this weapon
		// Make sure this is profName(0) else you'll be sorry!
		aBump += (int) aPC.getTotalBonusTo("WEAPONPROF=" + eq.profName(0, aPC), "WIELDCATEGORY");

		// or a bonus from the weapon itself
		//aBump += (int) eq.bonusTo("WEAPON", "WIELDCATEGORY");
		aBump += (int) eq.bonusTo(aPC, "WEAPON", "WIELDCATEGORY", true);

		// if the Equipment is not same Size category as PC then we
		// need to compute bonuses that might change Wield Category
		if (aPC.sizeInt() != eq.sizeInt())
		{
			aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", ewName);
			aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", "ALL");
		}
		// See if the equip has the [Hands] modifier.
		boolean hands = false;
		if (eq.getProfName().indexOf("[Hands]") > 0) {
			hands = true;
			//aBump--;
		}

		if (bonusCat != null)
		{
			ewName = bonusCat.getWieldCategoryStep(aBump, eq.getWield(), sizeDiff, hands);
			bonusCat = SettingsHandler.getGame().getWieldCategory(ewName);
		}

		if (bonusCat != null)
		{
			// return whichever one has the least number of hands
			if (bonusCat.getHands() <= wCat.getHands())
			{
				return bonusCat;
			}
		}

		return wCat;
	}

	/**
	 * Clears all lists of game data.
	 */
	public static void emptyLists()
	{
		// These lists do not need cleared; they are tied to game mode
		// alignmentList
		// checkList
		// gameModeList
		// campaignList
		// statList
		// All other lists should be cleared!!!
		//////////////////////////////////////
		// DO NOT CLEAR THESE HERE!!!
		// They only get loaded once.
		//
		//birthplaceList.clear();
		//bonusStackList.clear();
		//cityList.clear();
		//hairStyleList.clear();
		//helpContextFileList.clear();
		//interestsList.clear();
		//locationList.clear();
		//paperInfo.clear();
		//phobiaList.clear();
		//phraseList.clear();
		//schoolsList.clear();
		//sizeAdjustmentList.clear();
		//specialsList.clear();
		//speechList.clear();
		//traitList.clear();
		//unitSet.clear();
		//////////////////////////////////////
		armorProfList = new ArrayList();
		classList = new ArrayList();
		companionModList = new ArrayList();
		deityList = new ArrayList();
		domainList = new ArrayList();
		EquipmentList.clearEquipmentMap();
		kitList = new ArrayList();
		languageList = new ArrayList();
		EquipmentList.clearModifierList();
		pcClassTypeList = new ArrayList();
		skillList = new ArrayList();
		templateList = new ArrayList();
		saSet = new TreeSet();

		clearWeaponProfs();

		// Clear Maps (not strictly necessary, but done for consistency)
//		bonusSpellMap = new HashMap();
		domainMap = new HashMap();
		raceMap = new TreeMap();
		spellMap = new HashMap();
		visionMap = new HashMap();

		// Clear Sets (not strictly necessary, but done for consistency)
		clearSpellSets();
		pantheonsSet = new TreeSet();
		raceTypesSet = new TreeSet();
		subschoolsSet = new TreeSet();
		weaponTypes = new TreeSet();

		// Perform other special cleanup
		createEmptyRace();
		Equipment.clearEquipmentTypes();
		PersistenceManager.getInstance().emptyLists();
	}

	/**
	 * Execute post export commands for standard files
	 * @param fileName
	 */
	public static void executePostExportCommandStandard(final String fileName)
	{
		String postExportCommand = SettingsHandler.getPostExportCommandStandard();
		executePostExportCommand(fileName, postExportCommand);
	}

	/**
	 * Execute post export commands for PDF
	 * @param fileName
	 */
	public static void executePostExportCommandPDF(final String fileName)
	{
		String postExportCommand = SettingsHandler.getPostExportCommandPDF();
		executePostExportCommand(fileName, postExportCommand);
	}

	/**
	 * Execute any post export commands
	 * @param fileName
	 * @param postExportCommand
	 */
	public static void executePostExportCommand(final String fileName, String postExportCommand)
	{
		int x = 100;

		while (postExportCommand.indexOf("%") >= 0)
		{
			final String beforeString = postExportCommand.substring(0, postExportCommand.indexOf("%"));
			final String afterString = postExportCommand.substring(postExportCommand.indexOf("%") + 1);
			postExportCommand = beforeString + fileName + afterString;

			if (--x <= 0)
			{
				break;
			}
		}

		if (!"".equals(postExportCommand))
		{
			try
			{
				Runtime.getRuntime().exec(postExportCommand);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not execute " + postExportCommand + " after exporting " + fileName, ex);
			}
		}
	}

	/**
	 * Get the minimum number of hands required to wield a weapon
	 * @param pc
	 * @param weapon
	 * @param wp
	 * @return int
	 **/
	public static int handsRequired(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		if (wp == null)
		{
			 return 1;
		}

		int iHands = wp.getHands();

		if (iHands == WeaponProf.HANDS_SIZEDEPENDENT)
		{
			if (pc.sizeInt() > weapon.sizeInt())
			{
				iHands = 1;
			}
			else
			{
				iHands = 2;
			}
		}

		if (Globals.checkRule(RuleConstants.SIZECAT) && weapon.hasWield())
		{
			// 3.5 Wield Category rules
			final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

			if (wCat != null)
			{

				if (wp.getHands() >= wCat.getHands() || pc.sizeInt() < weapon.sizeInt())
				{
					iHands = wCat.getHands();
				}
			}
		}

		return iHands;
	}

	/**
	 * Return TRUE if the weapon profs have a variable named x
	 * @param collectionOfNames
	 * @param variableString
	 * @return TRUE if the weapon profs have a variable named x
	 */
	public static boolean hasWeaponProfVariableNamed(final Collection collectionOfNames, final String variableString)
	{
		return weaponProfs.hasVariableNamed(collectionOfNames, variableString);
	}

	/**
	 * load attribute names
	 */
	public static void loadAttributeNames()
	{
		createEmptyRace();
	}

	/**
	 * @param loadScoreValue
	 * @param weight
	 * @param aPC
	 * @return 0 = light, 1 = medium, 2 = heavy, 3 = overload
	 */
	public static int loadTypeForLoadScore(int loadScoreValue, final Float weight, final PlayerCharacter aPC)
	{
		if (loadScoreValue < 0)
		{
			loadScoreValue = Constants.LIGHT_LOAD;
		}

		final double dbl = weight.doubleValue() / maxLoadForLoadScore(loadScoreValue, aPC).doubleValue();

		if (SystemCollections.getLoadInfo().getLoadMultiplier("LIGHT") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("LIGHT").doubleValue() )
		{
			return Constants.LIGHT_LOAD;
		}

		if (SystemCollections.getLoadInfo().getLoadMultiplier("MEDIUM") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("MEDIUM").doubleValue() )
		{
			return Constants.MEDIUM_LOAD;
		}

		if (SystemCollections.getLoadInfo().getLoadMultiplier("HEAVY") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("HEAVY").doubleValue() )
		{
			return Constants.HEAVY_LOAD;
		}

		return Constants.OVER_LOAD;
	}

	/**
	 * Size is taken into account for the currentPC.
	 * @param loadScore
	 * @param aPC
	 * @return Float
	 */
	public static Float maxLoadForLoadScore(final int loadScore, final PlayerCharacter aPC)
	{
		Float loadValue = SystemCollections.getLoadInfo().getLoadScoreValue(loadScore);
		String formula = SystemCollections.getLoadInfo().getLoadModifierFormula();
		if (formula.length() != 0)
		{
			formula = CoreUtility.replaceAll(formula, "$$SCORE$$", new Float(loadValue.doubleValue() * getLoadMultForSize(aPC)).toString());
			return new Float(aPC.getVariableValue(formula, "").intValue());
		}
		return new Float(loadValue.doubleValue() * getLoadMultForSize(aPC));
	}

	/**
	 * @param loadScore
	 * @param aPC
	 * @param mult
	 * @return Float
	 */
	public static Float maxLoadForLoadScore(final int loadScore, final PlayerCharacter aPC, Float mult)
	{
		Float loadValue = SystemCollections.getLoadInfo().getLoadScoreValue(loadScore);
		String formula = SystemCollections.getLoadInfo().getLoadModifierFormula();
		if (formula.length() != 0)
		{
			formula = CoreUtility.replaceAll(formula, "$$SCORE$$", new Float(loadValue.doubleValue() * mult.doubleValue() * getLoadMultForSize(aPC)).toString());
			return new Float(aPC.getVariableValue(formula, "").intValue());
		}
		return new Float(loadValue.doubleValue() * mult.doubleValue() * getLoadMultForSize(aPC));
	}

	/**
	 * Get the minimum level for a spell level
	 * @param castingClass
	 * @param spellLevel
	 * @param allowBonus
	 * @return minimum level for a spell level
	 */
	public static int minLevelForSpellLevel(final PCClass castingClass, final int spellLevel, final boolean allowBonus)
	{
		int minLevel = Constants.INVALID_LEVEL;
		final Map castMap = castingClass.getCastMap();

		int loopMax = castMap.keySet().size();
		for (int i = 0; i < loopMax; i++)
		{
			final String aLevel = Integer.toString(i);
			final String castPerDay = (String) castMap.get(aLevel);

			if ((castPerDay == null) || (castPerDay.length() <= 0))
			{
				continue;
			}

			final StringTokenizer bTok = new StringTokenizer(castPerDay, ",");
			int maxCastable = -1;

			if (allowBonus)
			{
				maxCastable = bTok.countTokens() - 1;
			}
			else
			{
				int j = 0;

				while (bTok.hasMoreTokens())
				{
					try
					{
						if (Integer.parseInt(bTok.nextToken()) != 0)
						{
							maxCastable = j;
						}
					}
					catch (NumberFormatException ignore)
					{
						// ignore
					}

					j++;
				}
			}

			if (maxCastable >= spellLevel)
			{
				minLevel = i;

				break;
			}
		}

		if (minLevel < Constants.INVALID_LEVEL)
		{
			return minLevel;
		}

		final List knownList = castingClass.getKnownList();

		loopMax = knownList.size();

		for (int i = 0; i < loopMax; ++i)
		{
			final String knownSpells = knownList.get(i).toString();

			if ("0".equals(knownSpells))
			{
				continue;
			}

			final StringTokenizer bTok = new StringTokenizer(knownSpells, ",");
			int maxCastable = -1;

			if (allowBonus)
			{
				maxCastable = bTok.countTokens() - 1;
			}
			else
			{
				int j = 0;

				while (bTok.hasMoreTokens())
				{
					try
					{
						if (Integer.parseInt(bTok.nextToken()) != 0)
						{
							maxCastable = j;
						}
					}
					catch (NumberFormatException e)
					{
						//TODO: Should this really be ignored?
						Logging.errorPrint("", e);
					}

					j += 1;
				}
			}

			if (maxCastable >= spellLevel)
			{
				minLevel = i + 1;

				break;
			}
		}

		return minLevel;
	}

	/**
	 * Store a list of all vision types (such as Darkvision)
	 * @param aKey
	 */
	public static void putVisionMap(final String aKey)
	{
		visionMap.put(aKey, "0");
	}

	/**
	 * Remove a weapon prof by name
	 * @param name
	 */
	public static void removeWeaponProfNamed(final String name)
	{
		weaponProfs.removeNamed(name);
	}

	/**
	 * roll HP
	 * @param min
	 * @param max
	 * @param name
	 * @param level
	 * @return HP
	 */
	public static int rollHP(final int min, final int max, final String name, final int level)
	{
		int roll;

		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_USERROLLED:
				roll = -1;

				break;

			case Constants.HP_AVERAGE:

				// (n+1)/2
				// average roll on a die with an  odd # of sides works out exactly
				// average roll on a die with an even # of sides will have an extra 0.5
				roll = max - min;

				if (((level & 0x01) == 0) && ((roll & 0x01) != 0))
				{
					++roll;
				}

				roll = min + (roll / 2);

				break;

			case Constants.HP_AUTOMAX:
				roll = max;

				break;

			case Constants.HP_PERCENTAGE:
				roll = min - 1 + (int) ((SettingsHandler.getHPPct() * (max - min + 1)) / 100.0);

				break;

			case Constants.HP_STANDARD:default:
				roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;

				break;

			//TODO: Can we put these back now? XXX
//			case Constants.s_HP_LIVING_GREYHAWK:
//				if (totalLevels == 1)
//					roll = max;
//				else
//					roll = (int)Math.floor((max + min) / 2) + 1;
//				break;
//			case Constants.s_HP_LIVING_CITY:
//				if (totalLevels == 1 || totalLevels == 2)
//					roll = max;
//				else
//				{
//					roll = (int)Math.floor(3 * max / 4);
//					// In the bizarre case a class has a max of 1, need to fix that Floor will make that 0 instead.
//					if (roll < min) roll = min;
//				}
//				break;
		}

		if (SettingsHandler.getShowHPDialogAtLevelUp())
		{
			final Object[] rollChoices = new Object[max - min + 2];
			rollChoices[0] = Constants.s_NONESELECTED;

			for (int i = min; i <= max; ++i)
			{
				rollChoices[i - min + 1] = new Integer(i);
			}

			while (min <= max)
			{
				//TODO: This must be refactored away. Core shouldn't know about gui.
				final InputInterface ii = InputFactory.getInputInstance();
				final Object selectedValue = ii.showInputDialog(Globals.getRootFrame(),
					"Randomly generate a number between " + min + " and " + max + "." + Constants.s_LINE_SEP
					+ "Select it from the box below.",
					Globals.getGameModeHitPointText() + " for " + CoreUtility.ordinal(level) + " level of " + name,
					MessageType.INFORMATION, rollChoices, new Integer(roll));

				if ((selectedValue != null) && (selectedValue instanceof Integer))
				{
					roll = ((Integer) selectedValue).intValue();

					break;
				}
			}
		}

		return roll;
	}

	/**
	 * Select the paper
	 * @param paperName
	 * @return TRUE if OK
	 */
	public static boolean selectPaper(final String paperName)
	{
		for (int i = 0; i < SystemCollections.getUnmodifiablePaperInfo().size(); ++i)
		{
			final PaperInfo pi = (PaperInfo) SystemCollections.getUnmodifiablePaperInfo().get(i);

			if (pi.getName().equals(paperName))
			{
				setSelectedPaper(i);

				return true;
			}
		}

		setSelectedPaper(-1);

		return false;
	}

	/**
	 * Get size as an int
	 * @param aSize
	 * @return size as an int
	 */
	public static int sizeInt(final String aSize)
	{
		return sizeInt(aSize, 0);
	}

	/**
	 * Get size as an integer
	 * @param aSize
	 * @param defaultValue
	 * @return size as an int
	 */
	public static int sizeInt(final String aSize, final int defaultValue)
	{
		for (int iSize = 0; iSize <= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1); ++iSize)
		{
			if (aSize.startsWith(SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize).getAbbreviation()))
			{
				return iSize;
			}
		}

		return defaultValue;
	}

	/**
	 * Sort campaigns
	 */
	public static void sortCampaigns()
	{
		sortPObjectList(getClassList());
		sortPObjectList(getSkillList());
		// sortPObjectList(getFeatList()); Obsolete data structure
		sortPObjectList(getDeityList());
		sortPObjectList(getDomainList());
		sortPObjectList(getArmorProfList());
		sortPObjectList(getTemplateList());
		sortPObjectList(EquipmentList.getModifierList());
		sortPObjectList(getLanguageList());
		setD_sorted(true);
	}

	/**
	 * Sorts chooser lists using the appropriate method, based on the type of the first item in either list.
	 * Not pretty, but it works.
	 *
	 * @param availableList
	 * @param selectedList
	 */
	public static void sortChooserLists(final List availableList, final List selectedList)
	{
		final boolean stringsInList;

		if (availableList.size() > 0)
		{
			stringsInList = availableList.get(0) instanceof String;
		}
		else if (selectedList.size() > 0)
		{
			stringsInList = selectedList.get(0) instanceof String;
		}
		else
		{
			stringsInList = false;
		}

		if (stringsInList)
		{
			Collections.sort(availableList);
			Collections.sort(selectedList);
		}
		else
		{
			Globals.sortPObjectList(availableList);
			Globals.sortPObjectList(selectedList);
		}
	}

	/**
	 * Sort Pcgen Object list
	 * @param aList
	 * @return Sorted list of Pcgen Objects
	 */
	public static List sortPObjectList(final List aList)
	{
		Collections.sort(aList, pObjectComp);

		return aList;
	}

	/**
	 * Sort Pcgen Object list by name
	 * @param aList
	 * @return Sorted list of Pcgen Objects
	 */
	public static List sortPObjectListByName(final List aList)
	{
		Collections.sort(aList, pObjectNameComp);

		return aList;
	}

	/**
	 * Find PObject by key name in a sorted list of PObjects
	 * The list must be sorted by key name
	 *
	 * @param aList   a list of PObject objects.
	 * @param keyName the keyname being sought.
	 * @return a <code>null</code> value indicates the search failed.
	 */
	protected static PObject searchPObjectList(final List aList, final String keyName)
	{
		if ((keyName == null) || (keyName.length() <= 0))
		{
			return null;
		}

		if (isD_sorted())
		{
			return binarySearchPObject(aList, keyName);
		}
		final Object[] pobjArray = aList.toArray();
		final int upper = pobjArray.length;

		// not presently sorted
		PObject obj;

		for (int i = upper - 1; i >= 0; --i)
		{
			obj = (PObject) pobjArray[i];

			if (keyName.equals(obj.getKeyName()))
			{
				return obj;
			}
		}

		return null;
	}

	static int getBonusFeatsForLevel(final int level)
	{
		int num = 0;

		for (Iterator i = SettingsHandler.getGame().getBonusFeatLevels().iterator(); i.hasNext();)
		{
			num = bonusParsing(i, level, num);
		}

		return num;
	}

	static int getBonusStatsForLevel(final int level)
	{
		int num = 0;

		for (Iterator i = SettingsHandler.getGame().getBonusStatLevels().iterator(); i.hasNext();)
		{
			num = bonusParsing(i, level, num);
		}

		return num;
	}

	/**
	 * Geta choice from a list
	 * @param title
	 * @param choiceList
	 * @param selectedList
	 * @param pool
	 * @return a choice
	 */
	public static List getChoiceFromList(final String title, final List choiceList, final List selectedList, final int pool)
	{
		return getChoiceFromList(title, choiceList, selectedList, pool, false);
	}

	static List getChoiceFromList(final String title, final List choiceList, final List selectedList, final int pool, final boolean forceChoice)
	{
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setPool(pool);
		c.setPoolFlag(forceChoice);
		c.setAllowsDups(false);
		c.setTitle(title);
		c.setAvailableList(choiceList);

		if (selectedList != null)
		{
			c.setSelectedList(selectedList);
		}

		c.setVisible(true);

		return c.getSelectedList();
	}

	static List getCustColumnWidth()
	{
		return custColumnWidth;
	}

	static String getDefaultPcgPath()
	{
		return expandRelativePath(defaultPcgPath);
	}

	static int[] getDieSizes()
	{
		return dieSizes;
	}

	/**
	 * returns the location of the "filter.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 * @return filter path
	 */
	static String getFilterPath()
	{
		String aPath;

		// first see if it was specified on the command line
		aPath = System.getProperty("pcgen.filter");

		if (aPath == null)
		{
			aPath = getFilePath("filter.ini");
		}

		return expandRelativePath(aPath);
	}

	static Kit getKitNamed(final String aName)
	{
		final Iterator e = kitList.iterator();

		while (e.hasNext())
		{
			final Kit aKit = (Kit) e.next();

			if (aKit.getName().equals(aName))
			{
				return aKit;
			}
		}

		return null;
	}

	static List getLanguagesFromListOfType(final List langList, final String aType)
	{
		final List retSet = new ArrayList();

		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();

			if ((aLang != null)
				&& (aLang.isType(aType) || ((aType.length() > 0) && (aType.charAt(0) == '!') && !aLang.isType(aType))))
			{
				retSet.add(aLang);
			}
		}

		return retSet;
	}

	/**
	 * returns the location of the "options.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 * @return option path
	 */
	static String getOptionsPath()
	{
		String aPath;

		// first see if it was specified on the command line
		aPath = System.getProperty("pcgen.options");

		if (aPath == null)
		{
			aPath = getFilePath("options.ini");
		}

		return expandRelativePath(aPath);
	}

	/**
	 * Get a random integer between 0 (inclusive) and the given value (exclusive)
	 * @param high
	 * @return random int
	 */
	public static int getRandomInt(final int high)
	{
		//
		// Sanity check. If 'high' is <= 0, a IllegalArgumentException will be thrown
		//
		if (high <= 0)
		{
			return 0;
		}
		return getRandom().nextInt(high);
	}

	static int getSkillMultiplierForLevel(final int level)
	{
		final List sml = SettingsHandler.getGame().getSkillMultiplierLevels();

		if ((level > sml.size()) || (level <= 0))
		{
			return 1;
		}

		return Integer.parseInt(sml.get(level - 1).toString());
	}

	/**
	 * Get the Weapon Profs
	 * @param type
	 * @param aPC
	 * @return List of Weapon Profs
	 */
	public static List getWeaponProfs(final String type, final PlayerCharacter aPC)
	{
		final List aList = new ArrayList();
		final List bList = new ArrayList();
		String aString;
		StringTokenizer aTok;
		WeaponProf tempProf;

		for (Iterator e = aPC.getChangeProfList().iterator(); e.hasNext();)
		{
			aString = (String) e.next();
			aTok = new StringTokenizer(aString, "|");

			//
			// aString is format: WeapProf|ProfCategory
			// eg: Greataxe|Simple
			// eg: Sword (Bastard/Exotic)|Martial
			//
			final String eqName = aTok.nextToken();
			final String wpType = aTok.nextToken();
			tempProf = getWeaponProfNamed(eqName);

			if (tempProf == null)
			{
				continue;
			}

			if (wpType.equalsIgnoreCase(type))
			{
				aList.add(tempProf);
			}
			else
			{
				bList.add(tempProf);
			}
		}

		WeaponProf tempProf2;
		final Collection weaponProfsOfType = getAllWeaponProfsOfType(type);

		if (weaponProfsOfType == null)
		{
			return aList;
		}

		for (Iterator e = weaponProfsOfType.iterator(); e.hasNext();)
		{
			tempProf2 = (WeaponProf) e.next();

			if (bList.contains(tempProf2))
			{
				continue;
			}

			aList.add(tempProf2);
		}

		return aList;
	}

	/**
	 * Adds a weapon type
	 *
	 * @param weaponType The weapon type to add
	 */
	public static void addWeaponType(final String weaponType)
	{
		weaponTypes.add(weaponType.toUpperCase());
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 * @param aDamage
	 * @param sBaseSize
	 * @param sNewSize
	 * @return String
	 */
	static String adjustDamage(final String aDamage, final String sBaseSize, final String sNewSize)
	{
		if (aDamage.length() == 0)
		{
			return aDamage;
		}

		return adjustDamage(aDamage, sizeInt(sBaseSize), sizeInt(sNewSize));
	}

	static PObject binarySearchPObject(final List aList, final String keyName)
	{
		if ((keyName == null) || (keyName.length() <= 0))
		{
			return null;
		}

		final Object[] pobjArray = aList.toArray();
		int lower = 0;
		int upper = pobjArray.length;

		// always one past last possible match
		while (lower < upper)
		{
			final int mid = (lower + upper) / 2;
			final PObject obj = (PObject) pobjArray[mid];
			final int cmp = keyName.compareToIgnoreCase(obj.getKeyName());

			if (cmp == 0)
			{
				return obj;
			}
			else if (cmp > 0)
			{
				lower = mid + 1;
			}
			else
			{
				upper = mid;
			}
		}

		return null;
	}

	static double calcEncumberedMove(final int load, final double moveInt, final boolean checkLoad)
	{
		return calcEncumberedMove(load, moveInt, checkLoad, null);
	}

	/**
	 * Get the load String (how loaded a characere)
	 * @param load
	 * @return load string
	 */
	public static String getLoadString(final int load)
	{
		switch (load)
		{
			case Constants.LIGHT_LOAD:
				return Constants.s_LOAD_LIGHT;

			case Constants.MEDIUM_LOAD:
				return Constants.s_LOAD_MEDIUM;

			case Constants.HEAVY_LOAD:
				return Constants.s_LOAD_HEAVY;

			// case Constants.OVER_LOAD:
			default:
				return Constants.s_LOAD_OVERLOAD;

		}
	}

	/**
	 * Works for dnd according to the method noted in the faq.
	 * (NOTE: The table in the dnd faq is wrong for speeds 80 and 90)
	 * Not as sure it works for all other d20 games.
	 *
	 * @param load  (0 = light, 1 = medium, 2 = heavy, 3 = overload)
	 * @param unencumberedMove the unencumbered move value
	 * @param checkLoad
	 * @param aPC
	 * @return encumbered move as an integer
	 */
	static double calcEncumberedMove(final int load, final double unencumberedMove, final boolean checkLoad, final PlayerCharacter aPC)
	{
		double encumberedMove;

		if (checkLoad)
		{
			//
			// Can we ignore any encumberance for this type? If we can, then there's no
			// need to do any more calculations.
			//
			if ((aPC != null) && (aPC.ignoreEncumberedLoadMove(load)))
			{
				encumberedMove = unencumberedMove;
			}
			else
			{
				if (aPC != null)
				{
					String formula = SystemCollections.getLoadInfo().getLoadMoveFormula(getLoadString(load));
					if (formula.length() != 0)
					{
						formula = CoreUtility.replaceAll(formula, "$$MOVE$$", new Float(Math.floor(unencumberedMove)).toString());
						return aPC.getVariableValue(formula, "").doubleValue();
					}
				}

				switch (load)
				{
					case Constants.LIGHT_LOAD:
						encumberedMove = unencumberedMove;

						break;

					case Constants.MEDIUM_LOAD:
					case Constants.HEAVY_LOAD:

						if (CoreUtility.doublesEqual(unencumberedMove,5))
						{
							encumberedMove = 5;
						}
						else if (CoreUtility.doublesEqual(unencumberedMove,10))
						{
							encumberedMove = 5;
						}
						else
						{
							encumberedMove = (Math.floor(unencumberedMove / 15) * 10) + (((int)unencumberedMove) % 15);
						}

						break;

					case Constants.OVER_LOAD:
						encumberedMove = 0;

						break;

					default:
						Logging.errorPrint("The load " + load + " is not possible.");
						encumberedMove = 0;

						break;
				}
			}
		}
		else
		{
			encumberedMove = unencumberedMove;
		}

		return encumberedMove;
	}

	// Methods
	static String chooseFromList(final String title, final String choiceList, final List selectedList, final int pool)
	{
		final StringTokenizer tokens = new StringTokenizer(choiceList, "|");

		if (tokens.countTokens() != 0)
		{
			final List choices = new ArrayList();

			while (tokens.hasMoreTokens())
			{
				choices.add(tokens.nextToken());
			}

			return chooseFromList(title, choices, selectedList, pool);
		}

		return null;
	}

	/**
	 * Takes a SortedSet of language names and extracts the cases
	 * of ALL and TYPE=x and
	 * returns a larger SortedSet of Strings (language names)
	 * @param langNames
	 * @return SortedSet
	 */
	static SortedSet extractLanguageListNames(final SortedSet langNames)
	{
		final SortedSet newSet = new TreeSet();

		for (Iterator bI = langNames.iterator(); bI.hasNext();)
		{
			final String aLang = (String) bI.next();

			if ("ALL".equals(aLang))
			{
				newSet.addAll(getLanguageSetNames());
			}
			else if (aLang.startsWith("TYPE=") || aLang.startsWith("TYPE."))
			{
				newSet.addAll(getLanguageNamesFromListOfType(getLanguageList(), aLang.substring(5)));
			}
			else
			{
				newSet.add(aLang);
			}
		}

		return newSet;
	}

	static void initCustColumnWidth(final List l)
	{
		getCustColumnWidth().clear();
		getCustColumnWidth().addAll(l);
	}

	/**
	 * Checks if the weapon types include a certain weapon type
	 *
	 * @param weaponType The weapon type to look for
	 * @return True if the weapon type exists among the weapon types, otherwise false
	 */
	public static boolean weaponTypesContains(final String weaponType)
	{
		return weaponTypes.contains(weaponType.toUpperCase());
	}

	/**
	 * Gets the list of armor profs
	 *
	 * @return The list of armor profs
	 */
	private static List getArmorProfList()
	{
		return armorProfList;
	}

	private static void setD_sorted(final boolean argD_sorted)
	{
		Globals.d_sorted = argD_sorted;
	}

	private static boolean isD_sorted()
	{
		return d_sorted;
	}

	private static String getDamageDownKey(final String aDamage)
	{
		if (SettingsHandler.getGame().getDamageDownMap().containsKey(aDamage))
		{
			return "1|" + (String) SettingsHandler.getGame().getDamageDownMap().get(aDamage);
		}

		final RollInfo aRollInfo = new RollInfo(aDamage);
		final String baseDice = "1d" + Integer.toString(aRollInfo.sides);

		if (SettingsHandler.getGame().getDamageDownMap().containsKey(baseDice))
		{
			return Integer.toString(aRollInfo.times) + "|"
			+ (String) SettingsHandler.getGame().getDamageDownMap().get(baseDice);
		}

		return null;
	}

	/**
	 * How to change damage as weapon size changes is Contained in a Map
	 * @param aDamage
	 * @return String
	 */
	private static String getDamageUpKey(final String aDamage)
	{
		if (SettingsHandler.getGame().getDamageUpMap().containsKey(aDamage))
		{
			return "1|" + (String) SettingsHandler.getGame().getDamageUpMap().get(aDamage);
		}

		final RollInfo aRollInfo = new RollInfo(aDamage);
		final String baseDice = "1d" + Integer.toString(aRollInfo.sides);

		if (SettingsHandler.getGame().getDamageUpMap().containsKey(baseDice))
		{
			return Integer.toString(aRollInfo.times) + "|"
			+ (String) SettingsHandler.getGame().getDamageUpMap().get(baseDice);
		}

		return null;
	}

	/**
	 * Get a writable path for storing files
	 * First check to see if it's been set in-program
	 * Then check user home directory
	 * Else use directory pcgen started from
	 * @param aString
	 * @return file path
	 */
	private static String getFilePath(final String aString)
	{
		final String fType = SettingsHandler.getFilePaths();

		if ((fType == null) || fType.equals("pcgen"))
		{
			// we are either running PCGen for the first
			// time or user wants default file locations
			return System.getProperty("user.dir") + File.separator + aString;
		}
		else if (fType.equals("user"))
		{
			// use the users "home" directory + .pcgen
			return System.getProperty("user.home") + File.separator + ".pcgen" + File.separator + aString;
		}
		else
		{
			// use the specified directory
			return fType + File.separator + aString;
		}
	}

	private static List getLanguageNamesFromListOfType(final List langList, final String aType)
	{
		final List retSet = new ArrayList();

		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();

			if ((aLang != null)
				&& (((aType.length() > 0) && (aType.charAt(0) == '!') && !aLang.isType(aType)) || aLang.isType(aType)))
			{
				retSet.add(aLang.getName());
			}
		}

		return retSet;
	}

	private static List getLanguageSetNames()
	{
		final List aList = new ArrayList();

		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();
			aList.add(aLang.getName());
		}

		return aList;
	}

	private static double getLoadMultForSize(final PlayerCharacter aPC)
	{
		double mult = 1.0;
		final String size = aPC.getSize();
		final Float value = SystemCollections.getLoadInfo().getSizeAdjustment(size);
		if (value != null)
		{
			mult = value.doubleValue();
		}

		SizeAdjustment sadj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(sizeInt(size));
		if (sadj == null)
		{
			sadj = SettingsHandler.getGame().getDefaultSizeAdjustment();
		}

		if (sadj != null)
		{
			mult += sadj.bonusTo("LOADMULT", "TYPE=SIZE", aPC, aPC);
		}
		return mult;
	}

	private static SortedSet getPantheonsSet()
	{
		return pantheonsSet;
	}

	private static Random getRandom()
	{
		return random;
	}

	private static void setSelectedPaper(final int argSelectedPaper)
	{
		Globals.selectedPaper = argSelectedPaper;
	}

	private static SortedSet getSubschoolsSet()
	{
		return subschoolsSet;
	}

	private static boolean isUseGUI()
	{
		return useGUI;
	}

	private static boolean isWeaponTwoHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp,
		final boolean baseOnly)
	{
		if ((pc == null) || (weapon == null) || (wp == null))
		{
			return false;
		}

		int pcSize = pc.sizeInt();

		if (weapon.hasWield())
		{
			if (Globals.checkRule(RuleConstants.SIZECAT))
			{
				// 3.5 Wield Category
				if (handsRequired(pc, weapon, wp) == 2)
				{
					return true;
				}
			}
			else if (Globals.checkRule(RuleConstants.SIZEOBJ))
			{
				// Use Object Size
				final WieldCategory wCat = effectiveWieldCategory(pc, weapon);

				if (wCat != null)
				{
					// use 3.5 code to get Object Size
					return (wCat.getObjectSizeInt(weapon) > pcSize);
				}
			}
		}
		else
		{
			// Original Code
			if (!baseOnly)
			{
				pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE");
			}

			// Check to see if it's a two handed weapon
			if (handsRequired(pc, weapon, wp) == 2)
			{
				return true;
			}

			// If weapon is larger size than PC, it is two handed
			if (weapon.sizeInt() > pcSize)
			{
				return true;
			}
		}

		return false;
	}

	private static int bonusParsing(final Iterator i, final int level, int num)
	{
		// should be in format levelnum,rangenum
		final String l = i.next().toString();
		final StringTokenizer aTok = new StringTokenizer(l, "|", false);
		final int startLevel = Integer.parseInt(aTok.nextToken());
		final int rangeLevel = Integer.parseInt(aTok.nextToken());

		if ((level == startLevel)
			|| ((level > startLevel) && (rangeLevel > 0) && (((level - startLevel) % rangeLevel) == 0)))
		{
			++num;
		}

		return num;
	}

	private static void clearSpellSets()
	{
		castingTimesSet.clear();
		componentSet.clear();
		descriptorSet.clear();
		durationSet.clear();
		rangesSet.clear();
		saveInfoSet.clear();
		srSet.clear();
		statSet.clear();
		targetSet.clear();
	}

	/**
	 * Clear the global weaponProf list
	 **/
	private static void clearWeaponProfs()
	{
		weaponProfs.clear();
	}

	private static void createEmptyRace()
	{
		if (s_EMPTYRACE == null)
		{
			s_EMPTYRACE = new Race();
			s_EMPTYRACE.setName(Constants.s_NONESELECTED);
			s_EMPTYRACE.setTypeInfo("HUMANOID");
		}

		getRaceMap().put(Constants.s_NONESELECTED, s_EMPTYRACE);
	}

	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@"))
		{
			path = System.getProperty("user.dir") + File.separator + path.substring(1);
		}

		return path;
	}

	/**
	 * Add a sponsor, e.g. Silven Publishing
	 * @param sponsor
	 */
	public static void addSponsor(Map sponsor) {
		sponsors.put(sponsor.get("SPONSOR"), sponsor);
		sponsorList.add(sponsor);
	}

	/**
	 * Get a list of sponsors of PCGen
	 * @return list of sponsors of PCGen
	 */
	public static List getSponsors() {
		return sponsorList;
	}

	/**
	 * Get a sponsor
	 * @param name
	 * @return sponsor
	 */
	public static Map getSponsor(String name) {
		return (Map)sponsors.get(name);
	}
}
