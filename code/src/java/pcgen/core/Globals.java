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
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.persistence.PersistenceManager;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.Collator;
import java.util.*;
import java.util.regex.Pattern;

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
	private static List<PlayerCharacter>            pcList      = new ArrayList<PlayerCharacter>();
	/** Race, a s_EMPTYRACE */
	public static  Race            s_EMPTYRACE;

	/** This is true when the campaign data structures are sorted. */
	private static boolean d_sorted;

	/** These are system constants */
	public static final String javaVersion      = System.getProperty("java.version"); //$NON-NLS-1$
	/** Java Version Major */
	public static final int    javaVersionMajor =
		Integer.valueOf(javaVersion.substring(0,
				javaVersion.indexOf('.'))).intValue();
	/** Java Version Minor */
	public static final int    javaVersionMinor =
		Integer.valueOf(javaVersion.substring(javaVersion.indexOf('.') + 1,
				javaVersion.lastIndexOf('.'))).intValue();

	/** NOTE: The defaultPath is duplicated in LstSystemLoader. */
	private static final String defaultPath    = System.getProperty("user.dir"); //$NON-NLS-1$
	private static final String defaultPcgPath = getDefaultPath() + File.separator + "characters"; //$NON-NLS-1$
	private static final String backupPcgPath = Constants.EMPTY_STRING;
	private static final int[]  dieSizes       = new int[]{ 1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000 };

	/** These are for the Internationalization project. */
	private static String     language        = "en"; //$NON-NLS-1$
	private static String     country         = "US"; //$NON-NLS-1$

	/** The BioSet used for age calculations */
	private static BioSet     bioSet          = new BioSet();
	private static final List<String> custColumnWidth = new ArrayList<String>();
	private static SourceEntry.SourceFormat sourceDisplay = SourceEntry.SourceFormat.LONG;
	private static int        selectedPaper   = -1;

	private static CategorisableStore abilityStore = new CategorisableStore();

	/** we need maps for efficient lookups */
	private static Map<URI, Campaign>        campaignMap     = new HashMap<URI, Campaign>();
	private static Map<String, Domain>        domainMap       = new TreeMap<String, Domain>();
	private static SortedMap<String, Race>  raceMap         = new TreeMap<String, Race>();
	/** TODO Why can spellMap contain both Spell and List<Spell>? Change to always contain List<Spell> (it is possible said list only has one member, but that's ok.)
	 * Does  need to be sorted? If not, change to HashMap.*/
	private static Map<String, Object>        spellMap        = new TreeMap<String, Object>();
	private static Map<String, String>        eqSlotMap       = new HashMap<String, String>();
	private static Map<String, List<CompanionMod>>  companionModMap = new TreeMap<String, List<CompanionMod>>();

	/** We use lists for efficient iteration */
	private static List<String> armorProfList         = new ArrayList<String>();
	private static List<Campaign> campaignList          = new ArrayList<Campaign>(85);
	private static List<PCClass> classList             = new ArrayList<PCClass>(380);
//	private static List<CompanionMod> companionModList      = new ArrayList<CompanionMod>();
	private static List<Deity> deityList             = new ArrayList<Deity>(275);
	private static List<Domain> domainList            = new ArrayList<Domain>(100);
	private static Map<String, Kit> kitMap               = new HashMap<String, Kit>();
	private static List<Language> languageList          = new ArrayList<Language>(200);

	//any TYPE added to pcClassTypeList is assumed be pre-tokenized
	private static List<String>             pcClassTypeList = new ArrayList<String>();
	private static List<Skill>             skillList       = new ArrayList<Skill>(400);
	private static List<PCTemplate>             templateList    = new ArrayList<PCTemplate>(350);
	private static SortedSet<SpecialAbility>        saSet           = new TreeSet<SpecialAbility>();

	private static Map<String, Map<String, String>> sponsors = new HashMap<String, Map<String, String>>();
	private static List<Map<String, String>> sponsorList = new ArrayList<Map<String, String>>();

	/** Weapon proficiency Data storage */
	private static final PObjectDataStore<WeaponProf> weaponProfs = new PObjectDataStore<WeaponProf>("WeaponProf");

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
	private static SortedSet<String> pantheonsSet     = new TreeSet<String>();
	private static SortedSet<String> raceTypesSet     = new TreeSet<String>();
	private static SortedSet<String> subschoolsSet    = new TreeSet<String>();
	private static SortedSet<String> weaponTypes      = new TreeSet<String>();

	private static SortedSet<String> castingTimesSet  = new TreeSet<String>();
	private static SortedSet<String> componentSet     = new TreeSet<String>();
	private static SortedSet<String> descriptorSet    = new TreeSet<String>();
	private static SortedSet<String> durationSet      = new TreeSet<String>();
	private static SortedSet<String> typeForSpellsSet = new TreeSet<String>();
	private static SortedSet<String> rangesSet        = new TreeSet<String>();
	private static SortedSet<String> saveInfoSet      = new TreeSet<String>();
	private static SortedSet<String> srSet            = new TreeSet<String>();
	private static SortedSet<String> statSet          = new TreeSet<String>();
	private static SortedSet<String> targetSet        = new TreeSet<String>();

	// end of filter creation sets
	private static JFrame rootFrame;
	private static JFrame currentFrame;
	private static final StringBuffer section15 = new StringBuffer(30000);
	private static final String spellPoints = "0";

	
	/** whether or not the GUI is used (false for command line) */
	private static boolean useGUI = true;

	private static final Comparator<PObject> pObjectComp = new Comparator<PObject>()
		{
			public int compare(final PObject o1, final PObject o2)
			{
				return o1.getKeyName().compareToIgnoreCase(o2.getKeyName());
			}
		};

	private static final Comparator<PObject> pObjectNameComp = new Comparator<PObject>()
		{
			public int compare(final PObject o1, final PObject o2)
			{
				final Collator collator = Collator.getInstance();
				return collator.compare(o1.getDisplayName(), o2.getDisplayName());
			}
		};

	private static final Comparator<Object> pObjectStringComp = new Comparator<Object>()
		{
			public int compare(final Object o1, final Object o2)
			{
				final String key1;
				final String key2;
				if ( o1 instanceof PObject )
				{
					key1 = ((PObject)o1).getKeyName();
				}
				else
				{
					key1 = o1.toString();
				}
				if ( o2 instanceof PObject )
				{
					key2 = ((PObject)o2).getKeyName();
				}
				else
				{
					key2 = o2.toString();
				}
				return key1.compareToIgnoreCase(key2);
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
			globalProperties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp"); //$NON-NLS-1$
			globalProperties.getString("VersionNumber"); //$NON-NLS-1$
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
	public static Collection<WeaponProf> getAllWeaponProfsOfType(final String type)
	{
		return weaponProfs.getAllOfType(type);
	}

	/**
	 * Retrieve a set of the possible types of weapon proficiencies.
	 * @return Set of the names of the weapon proficiencey types.
	 */
	public static Set<String> getWeaponProfTypes()
	{
		return weaponProfs.getTypes();
	}

	/**
	 * Returns all weapon proficiencies registered in the system.
	 * 
	 * @return Collection of <tt>WeaponProf</tt>
	 */
	public static Collection<WeaponProf> getAllWeaponProfs()
	{
		return Collections.unmodifiableCollection(weaponProfs.getAll());
	}

	/**
	 * Get a list of the allowed game modes
	 * @return list of the allowed game modes
	 */
	public static List<String> getAllowedGameModes()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getAllowedModes();
		}

		return new ArrayList<String>();
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
	public static Map<String, String> getBonusSpellMap()
	{
		return SettingsHandler.getGame().getBonusSpellMap();
	}

	/**
	 * Get the campaign by file name
	 * @param aName
	 * @return Campaign
	 */
	public static Campaign getCampaignByURI(final URI aName)
	{
		return getCampaignByURI(aName, true);
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
	public static Campaign getCampaignByURI(final URI aName, final boolean complainOnError)
	{
		final Campaign campaign = campaignMap.get(aName);

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
	public static List<Campaign> getCampaignList()
	{
		return campaignList;
	}

	/**
	 * Get campaign by key
	 * @param aKey
	 * @return Campaign
	 */
	public static Campaign getCampaignKeyed(final String aKey)
	{
		for ( Campaign campaign : getCampaignList() )
		{
			if (campaign.getKeyName().equalsIgnoreCase(aKey))
			{
				return campaign;
			}
		}

		Logging.errorPrint("Could not find campaign: " + aKey);

		return null;
	}

	/**
	 * Get casting times set
	 * @return casting times set
	 */
	public static SortedSet<String> getCastingTimesSet()
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
		return searchPObjectList(getClassList(), aKey);
	}

	/**
	 * Get class list
	 * @return List
	 */
	public static List<PCClass> getClassList()
	{
		return classList;
	}

	/**
	 * Finds all PObjects that match the passed in type.  All the types listed
	 * in aType must match for the object to be returned.
	 * @param aPObjectList List of PObjects to search
	 * @param aType A "." separated list of TYPEs to match
	 * @return List of PObjects matching all TYPEs
	 */
	private static <T extends PObject> List<T> getPObjectsOfType(final List<T> aPObjectList, final String aType)
	{
		final ArrayList<T> ret = new ArrayList<T>(aPObjectList.size());

		List<String> typeList = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(aType, ".");
		while (tok.hasMoreTokens())
		{
			typeList.add(tok.nextToken());
		}

		for (T anObject : aPObjectList)
		{
			boolean match = false;
			for (String type : typeList)
			{
				final boolean sense = !(type.charAt(0) == '!');
				if (anObject.isType(type) == sense)
				{
					match = true;
				}
				else
				{
					match = false;
					break;
				}
			}
			if (match)
			{
				ret.add(anObject);
			}
		}
		ret.trimToSize();
		return ret;
	}

	/**
	 * Returns a list of classes matching the specified type
	 * @param aType TYPE string
	 * @return List of Classes
	 */
	public static List<PCClass> getClassesByType(final String aType)
	{
		return getPObjectsOfType(getClassList(), aType);
	}

	/**
	 * Adds a <tt>CompanionMod</tt> to the global list of companion mods
	 * registered in the system.
	 * 
	 * @param aMod A <tt>CompanionMod</tt> to add.
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	public static void addCompanionMod( final CompanionMod aMod )
	{
		final String type = aMod.getType().toUpperCase();
		List<CompanionMod> mods = companionModMap.get( type );
		if ( mods == null )
		{
			mods = new ArrayList<CompanionMod>();
			companionModMap.put( type, mods );
		}
		mods.add( aMod );
	}

	/**
	 * Removes a <tt>CompanionMod</tt> from the system registry.
	 * 
	 * <p>This method is used by the .FORGET logic to remove a CompanionMod
	 * previously loaded by another set.
	 * 
	 * @param aMod A <tt>CompanionMod</tt> to remove
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	public static void removeCompanionMod( final CompanionMod aMod )
	{
		final Collection<List<CompanionMod>> allMods = companionModMap.values();
		for ( Iterator<List<CompanionMod>> i = allMods.iterator(); i.hasNext(); )
		{
			final List<CompanionMod> mods = i.next();
			final boolean removed = mods.remove( aMod );
			if ( removed )
			{
				if ( mods.size() == 0 )
				{
					i.remove();
					return;
				}
			}
		}
	}

	/**
	 * Returns all types of followers for which a <code>CompanionMod</code>
	 * has been defined.
	 * 
	 * @return Collection of Follower types.
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	public static Collection<String> getFollowerTypes()
	{
		return Collections.unmodifiableSet( companionModMap.keySet() );
	}

	/**
	 * Gets all the <code>CompanionMod</code>s for the specified type of 
	 * follower.
	 * 
	 * @param aType The type of Follower to get mods for.
	 * @return An unmodifiable Collection of COMPANIONMODs or an EMPTY_LIST
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	public static Collection<CompanionMod> getCompanionMods( final String aType )
	{
		final String type = aType.toUpperCase();
		final List<CompanionMod> cMods = companionModMap.get( type );
		if ( cMods == null )
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList( companionModMap.get( type ) );
	}
	
	/**
	 * Get companion modifier.
	 * @param aString
	 * @return companion mod
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
		for ( CompanionMod cMod : getAllCompanionMods() )
		{
			aTok = new StringTokenizer(classes, ",", false);

			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();

				if (cMod.getLevel(cString) == level)
				{
					return cMod;
				}
			}
		}

		return null;
	}

	/**
	 * Gets a <tt>Collection</tt> of all <tt>CompanionMod</tt>s registered in
	 * the system.
	 * 
	 * @return An unmodifiable collection of <tt>CompanionMod</tt>s
	 */
	public static Collection<CompanionMod> getAllCompanionMods()
	{
		final List<CompanionMod> ret = new ArrayList<CompanionMod>();
		final Collection<List<CompanionMod>> values = companionModMap.values();
		for ( final List<CompanionMod> cMods : values )
		{
			ret.addAll( cMods );
		}
		return Collections.unmodifiableCollection( ret );
	}

	/**
	 * Get component set
	 * @return component set
	 */
	public static SortedSet<String> getComponentSet()
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
		final String addMe = cName.concat(Constants.PIPE).concat(Integer.toString(value));

		if (getCustColumnWidth().isEmpty())
		{
			getCustColumnWidth().add(addMe);
		}

		final int loopMax = getCustColumnWidth().size();

		for (int i = 0; i < loopMax; ++i)
		{
			final String colWidth = getCustColumnWidth().get(i);
			if (colWidth == null || colWidth.length() == 0)
			{
				continue;
			}
			final StringTokenizer tTok = new StringTokenizer(colWidth, Constants.PIPE, false);
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
			final StringTokenizer tTok = new StringTokenizer(getCustColumnWidth().get(i), Constants.PIPE, false);
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
		return searchPObjectList(getDeityList(), aKey);
	}

	/**
	 * Get deity list
	 * @return deity list
	 */
	public static List<Deity> getDeityList()
	{
		return deityList;
	}

	/**
	 * Get Deity by key in list
	 * @param aKey
	 * @param aList
	 * @return Deity
	 */
	public static Deity getDeityKeyed(final String aKey, final List<Deity> aList)
	{
		for ( Deity deity : aList )
		{
			if (deity.getKeyName().equalsIgnoreCase(aKey))
			{
				return deity;
			}
		}

		return null;
	}

	/**
	 * Get descriptor set
	 * @return descriptor set
	 */
	public static SortedSet<String> getDescriptorSet()
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
		return domainMap.get(aKey);
	}

	/**
	 * Get domain list
	 * @return domain list
	 */
	public static List<Domain> getDomainList()
	{
		return domainList;
	}

	/**
	 * Get domain map
	 * @return domain map
	 */
	public static Map<String, Domain> getDomainMap()
	{
		return domainMap;
	}

	/**
	 * Get duration set
	 * @return duration set
	 */
	public static SortedSet<String> getDurationSet()
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
		for (EquipSlot es : SystemCollections.getUnmodifiableEquipSlotList())
		{
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
	public static Map<String, String> getEquipSlotMap()
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
		final String aNum = getEquipSlotMap().get(aType);

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
	 * 
	 * @param aCategory
	 * @param aKey The key of the Ability to remove
	 * @return true or false
	 */
	public static boolean removeAbilityKeyed( final AbilityCategory aCategory, final String aKey )
	{
		return abilityStore.removeKeyed(aCategory.getKeyName(), aKey);
	}

	// TODO - Remove this version
	public static boolean removeAbilityKeyed( final String aCategory, final String aKey )
	{
		return abilityStore.removeKeyed(aCategory, aKey);
	}
	
	/**
	 * Get the Ability whose Key matches the String passed in.
	 * 
	 * @param aCategory The category of the Ability to return.
	 * @param aKey The KEY of the Ability to return
	 * @return Ability
	 */
	public static Ability getAbilityKeyed( final AbilityCategory aCategory, final String aKey )
	{
		return (Ability)abilityStore.getKeyed(aCategory.getAbilityCategory(), aKey);
	}

	// TODO - Remove this version
	public static Ability getAbilityKeyed( final String aCategory, final String aKey )
	{
		return (Ability)abilityStore.getKeyed(aCategory, aKey);
	}

	/**
	 * Get an iterator for the Abilities in the chosen category.  If
	 * passed the string "ALL", will construct an iterator for all abilites
	 * in the system.  The abilites will be sorted in Key order.
	 * @param aCategory the Category of the Abilities to return an iterator for
	 * @return An Iterator
	 */
	public static Iterator<Categorisable> getAbilityKeyIterator (String aCategory)
	{
		String catKey = aCategory;
		if (!aCategory.equals("ALL"))
		{
			AbilityCategory cat = AbilityUtilities.getAbilityCategory(aCategory);
			if (cat != null)
			{
				catKey = cat.getAbilityCategory();
			}
		}
		return abilityStore.getKeyIterator(catKey);
	}

	/**
	 * Get an iterator for the Abilities in the chosen category.  If
	 * passed the string "ALL", will construct an iterator for all abilites
	 * in the system.  The abilites will be sorted in Name order.
	 * @param aCategory the Category of the Abilities to return an iterator for
	 * @return An Iterator
	 */
	public static Iterator<? extends Categorisable> getAbilityNameIterator (String aCategory)
	{
		String catKey = aCategory;
		if (!aCategory.equals("ALL"))
		{
			AbilityCategory cat = AbilityUtilities.getAbilityCategory(aCategory);
			if (cat != null)
			{
				catKey = cat.getAbilityCategory();
			}
		}
		return abilityStore.getNameIterator(catKey);
	}

	/**
	 * Returns a list of abilities of the specified category.
	 * 
	 * @param aCategory The category of Ability to return
	 * 
	 * @return An <b>unmodifiable</b> list of the Ability objects currently 
	 * loaded
	 */
	public static List<Ability> getAbilityList( final AbilityCategory aCategory )
	{
		final List<? extends Categorisable> abilities = abilityStore.getUnmodifiableList( aCategory.getAbilityCategory() );
		final List<Ability> ret = new ArrayList<Ability>(abilities.size());
		for ( final Categorisable ab : abilities )
		{
			if ( ab instanceof Ability )
			{
				final Ability ability = (Ability)ab;
				if ( aCategory.getAbilityTypes().size() > 0 )
				{
					for ( final String type : aCategory.getAbilityTypes() )
					{
						if ( ability.isType(type) )
						{
							ret.add( ability );
							break;
						}
					}
				}
				else
				{
					ret.add( ability );
				}
			}
		}
		return Collections.unmodifiableList(ret);
	}
	
	/**
	 * For the rare method that does actually need a list of Ability
	 * objects rather than an iterator.
	 * @param aCategory the category of object to return
	 * @return an unmodifiable list of the Ability objects currently loaded
	 */
	public static List<? extends Categorisable> getUnmodifiableAbilityList(String aCategory)
	{
		String catKey = aCategory;
		if (!aCategory.equals("ALL"))
		{
			AbilityCategory cat = AbilityUtilities.getAbilityCategory(aCategory);
			if (cat != null)
			{
				catKey = cat.getAbilityCategory();
			}
		}
		return abilityStore.getUnmodifiableList(catKey);
	}

	/**
	 * Returns a list of Abilities of a specified category and type
	 * @param aCategory The Category of Ability e.g. "FEAT"
	 * @param aType a TYPE String
	 * @return List of Abilities
	 */
	public static List<Ability> getAbilitiesByType(final String aCategory, final String aType)
	{
		List<Ability> abilityList = new ArrayList<Ability>();
		for ( Categorisable c : getUnmodifiableAbilityList(aCategory) )
		{
			if ( c instanceof Ability )
			{
				abilityList.add( (Ability)c );
			}
			else if ( c instanceof AbilityInfo )
			{
				abilityList.add( ((AbilityInfo)c).getAbility() );
			}
		}
		return getPObjectsOfType(abilityList, aType);
	}

	public static Collection<String> getAbilityTypes(final AbilityCategory aCategory, final boolean visibleOnly)
	{
		final Set<String> typeList = new TreeSet<String>();
		for ( final Categorisable c : getUnmodifiableAbilityList(aCategory.getKeyName()) )
		{
			Ability ability = null;
			if ( c instanceof Ability )
			{
				ability = (Ability)c;
			}
			else if ( c instanceof AbilityInfo )
			{
				ability = ((AbilityInfo)c).getAbility();
			}
			typeList.addAll( ability.getTypeList(visibleOnly) );
		}
		return Collections.unmodifiableSet(typeList);
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
		return SettingsHandler.getGame().getTabShown(Tab.SPELLS);
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
	public static List<String> getGlobalDeityList()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getDeityList();
		}

		return new ArrayList<String>();
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
	public static Map<String, Kit> getKitInfo()
	{
		return kitMap;
	}

	/**
	 * Get kit by key
	 * @param aKey
	 * @return Kit
	 */
	public static Kit getKitKeyed(final String aKey)
	{
		return kitMap.get(aKey);
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
	public static List<Language> getLanguageList()
	{
		return languageList;
	}

	/**
	 * Returns the <tt>Language</tt> for the specified key.
	 * 
	 * <p>If the key is either &quot;ALL&quot; or &quot;ANY&quot; the special
	 * Language object representing all languages is returned.
	 * 
	 * @param aKey A key to retrieve a Language for
	 * @return The Language matching the key or null if not found.
	 */
	public static Language getLanguageKeyed(final String aKey)
	{
		// TODO - Fix this.
		if (aKey.equalsIgnoreCase("ALL") || aKey.equalsIgnoreCase("ANY"))
		{
			return Language.getAllLanguage();
		}
		for (Language aLang : getLanguageList())
		{
			if (aLang.getKeyName().equalsIgnoreCase(aKey))
			{
				return aLang;
			}
		}
		return null;
	}

	/**
	 * returns a HashMap of LevelInfo objects
	 * @return Map
	 */
	public static Map<String, LevelInfo> getLevelInfo()
	{
		return SettingsHandler.getGame().getLevelInfo();
	}

	/**
	 * Get load strings
	 * @return List of Strings
	 */
	public static List<String> getLoadStrings()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getLoadStrings();
		}

		return new ArrayList<String>();
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
	public static List<String> getPCClassTypeList()
	{
		return pcClassTypeList;
	}

	/**
	 * Set PC List
	 * @param argPcList
	 */
	public static void setPCList(final List<PlayerCharacter> argPcList)
	{
		pcList = argPcList;
	}

	/**
	 * Get PC List
	 * @return List of Pcs
	 */
	public static List<PlayerCharacter> getPCList()
	{
		return pcList;
	}

	/**
	 * Get pantheons
	 * @return Sorted set of pantheons
	 */
	public static SortedSet<String> getPantheons()
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

		final PaperInfo pi = SystemCollections.getUnmodifiablePaperInfo().get(idx);

		return pi.getPaperInfo(infoType);
	}

	/**
	 * This method gets the available race types as a set.
	 * @return race types
	 */
	public static SortedSet<String> getRaceTypes()
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
	public static SortedSet<String> getRangesSet()
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
	public static SortedSet<SpecialAbility> getSASet()
	{
		return saSet;
	}

	/**
	 * Get the save info set
	 * @return save info set
	 */
	public static SortedSet<String> getSaveInfoSet()
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
		return searchPObjectList(getSkillList(), aKey);
	}

	/**
	 * Get list of skills
	 * @return list of skills
	 */
	public static List<Skill> getSkillList()
	{
		return skillList;
	}

	/**
	 * Retrieve those skills in the global skill list that match the
	 * supplied visibility level.
	 *
	 * @param vis What level of visibility skills are desired.
	 * @return A list of the skills matching the visibility criteria.
	 */
	public static List<Skill> getPartialSkillList(final Visibility vis)
	{
		// Now select the required set of skills, based on their visibility.
		ArrayList<Skill> aList = new ArrayList<Skill>();
		for ( Skill skill : getSkillList() )
		{
			final Visibility skillVis = skill.getVisibility();

			if (vis == Visibility.DEFAULT
				|| skillVis == Visibility.DEFAULT
				|| skillVis == vis)
			{
				aList.add(skill);
			}

		}
		return aList;
	}

	/**
	 * Returns a list of skills matching the specified type
	 * @param aType A TYPE String
	 * @return List of Skills
	 */
	public static List<Skill> getSkillsByType(final String aType)
	{
		return getPObjectsOfType(getSkillList(), aType);
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
	public static void setSourceDisplay(final SourceEntry.SourceFormat sourceType)
	{
		sourceDisplay = sourceType;
	}

	/**
	 * Get source display
	 * @return source display
	 */
	public static SourceEntry.SourceFormat getSourceDisplay()
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
				return (Spell)obj;
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
	public static Map<String, ?> getSpellMap()
	{
		return Collections.unmodifiableMap(spellMap);
	}

	/**
	 * Add an item to the spell map. generally this will either 
	 * be a Spell object or a list of Spells
	 * 
	 * @param key The key the object is associated with.
	 * @param anObject The object to be added to the map.
	 */
	public static void addToSpellMap(final String key, final Object anObject)
	{
		spellMap.put(key, anObject);
	}

	/**
	 * Remove the item with the listed key from the global spell map.
	 * 
	 * @param key The key of the item to be removed.
	 * @return Previous value associated with specified key, or null 
	 * if there was no mapping for key.
	 */
	public static Object removeFromSpellMap(final String key)
	{
		return spellMap.remove(key);
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
	 * @param classKey  (optional, ignored if "")
	 * @param domainKey (optional, ignored if "")
	 *                   at least one of classKey and domainKey must not be ""
	 * @return a List of Spell
	 */
	public static List<Spell> getSpellsIn(final int level, final String classKey, final String domainKey)
	{
		final List<Spell> aList = new ArrayList<Spell>();
		final StringBuffer aBuf = new StringBuffer();
		String spellType = Constants.EMPTY_STRING;

		if (classKey.length() > 0)
		{
			final PCClass aClass;

			if (classKey.indexOf('|') < 0)
			{
				aClass = getClassKeyed(classKey);
				aBuf.append("CLASS|").append(classKey);
			}
			else
			{
				aClass = getClassKeyed(classKey.substring(classKey.indexOf(Constants.PIPE) + 1));
				aBuf.append(classKey);
			}

			if (aClass != null)
			{
				spellType = aClass.getSpellType();
			}
		}

		if (domainKey.length() > 0)
		{
			if (aBuf.length() > 0)
			{
				aBuf.append('|');
			}

			if (domainKey.indexOf('|') < 0)
			{
				aBuf.append("DOMAIN|").append(domainKey);
			}
			else
			{
				aBuf.append(domainKey);
			}

			spellType = "DIVINE";
		}

		for (String aKey : spellMap.keySet())
		{
			final Object obj = spellMap.get(aKey);

			if (obj instanceof ArrayList)
			{
				for (Spell aSpell : (ArrayList<Spell>)obj)
				{
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
	public static SortedSet<String> getSrSet()
	{
		return srSet;
	}

	/**
	 * Get stat set
	 * @return stat set
	 */
	public static SortedSet<String> getStatSet()
	{
		return statSet;
	}

	/**
	 * Get sub schools
	 * @return sub schools
	 */
	public static SortedSet<String> getSubschools()
	{
		return getSubschoolsSet();
	}

	/**
	 * Get Target set
	 * @return target set
	 */
	public static SortedSet<String> getTargetSet()
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
		return searchPObjectList(getTemplateList(), aKey);
	}

	/**
	 * Get the template list
	 * @return list of tempaltes
	 */
	public static List<PCTemplate> getTemplateList()
	{
		return templateList;
	}

	/**
	 * Get type for spells set
	 * @return type for spells set
	 */
	public static SortedSet<String> getTypeForSpells()
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
	 * Get copy of weapon prof array
	 * @return copy of weapon prof array
	 */
	public static List<WeaponProf> getWeaponProfArrayCopy()
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
	 * Returns a List of weapontypes
	 *
	 * @return The list of weapon types
	 */
	public static SortedSet<String> getWeaponTypeList()
	{
		return weaponTypes;
	}
	
	public static String getWeaponReachForumla ()
	{
		return SettingsHandler.getGame().getWeaponReachFormula();
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
		campaignMap.put(campaign.getSourceURI(), campaign);
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
	public static void addUniqueWeaponProfsAsStringTo(final List<String> dest)
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

		int direction = 0;
		String parseString = Constants.EMPTY_STRING;
		if (baseSize < finalSize)
		{
			parseString = getDamageUpKey(aDamage);
			direction = 1;
		}
		else if ( baseSize > finalSize )
		{
			parseString = getDamageDownKey(aDamage);
			direction = -1;
		}
		
		if ( direction != 0 && parseString != null )
		{
			StringTokenizer aTok = new StringTokenizer(parseString, Constants.PIPE);
			multiplier = Integer.parseInt(aTok.nextToken());
			parseString = aTok.nextToken();
			aTok = new StringTokenizer(parseString, ",");

			while ((baseSize != finalSize) && aTok.hasMoreTokens())
			{
				result = aTok.nextToken();
				baseSize += direction;
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
	public static boolean canResizeHaveEffect(final PlayerCharacter aPC, final Equipment aEq, List<String> typeList)
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
		Logging.debugPrint("Races=" + raceMap.size());
		Logging.debugPrint("Classes=" + getClassList().size());
		Logging.debugPrint("Skills=" + getSkillList().size());
		Logging.debugPrint("Feats=" + getUnmodifiableAbilityList("FEAT").size());
		Logging.debugPrint("Equipment=" + EquipmentList.size());
		Logging.debugPrint("WeaponProfs=" + getWeaponProfSize());
		Logging.debugPrint("Kits=" + kitMap.size());
		Logging.debugPrint("Templates=" + templateList.size());

		//
		// NOTE: If you add something here be sure to update the debug output in pcgen.gui.MainSource in loadCampaigns_actionPerformed
		//
		if ((raceMap.size() == 0) || (getClassList().size() == 0) || (getSkillList().size() == 0)
			|| (getUnmodifiableAbilityList("FEAT").size() == 0) || (EquipmentList.size() == 0)
			|| (getWeaponProfSize() == 0))
		{
			return false;
		}

		return true;
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
		abilityStore = new CategorisableStore();
		armorProfList = new ArrayList<String>();
		classList = new ArrayList<PCClass>();
		companionModMap = new TreeMap<String, List<CompanionMod>>();
		deityList = new ArrayList<Deity>();
		domainList = new ArrayList<Domain>();
		EquipmentList.clearEquipmentMap();
		kitMap = new HashMap<String, Kit>();
		languageList = new ArrayList<Language>();
		EquipmentList.clearModifierList();
		pcClassTypeList = new ArrayList<String>();
		skillList = new ArrayList<Skill>();
		templateList = new ArrayList<PCTemplate>();
		saSet = new TreeSet<SpecialAbility>();

		clearWeaponProfs();

		// Clear Maps (not strictly necessary, but done for consistency)
//		bonusSpellMap = new HashMap();
		domainMap = new HashMap<String, Domain>();
		raceMap = new TreeMap<String, Race>();
		spellMap = new HashMap<String, Object>();
		VisionType.clearConstants();

		// Clear Sets (not strictly necessary, but done for consistency)
		clearSpellSets();
		pantheonsSet = new TreeSet<String>();
		raceTypesSet = new TreeSet<String>();
		subschoolsSet = new TreeSet<String>();
		weaponTypes = new TreeSet<String>();

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
		ArrayList<String> aList = new ArrayList<String>();
		StringTokenizer aTok = new StringTokenizer(postExportCommand, " ");
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		String[] cmdArray = new String[aList.size()];
		for (int idx = 0; idx < aList.size(); idx++)
		{
			final String s = aList.get(idx);
			if (s.indexOf("%") > -1)
			{
				final String beforeString = s.substring(0, s.indexOf("%"));
				final String afterString = s.substring(s.indexOf("%") + 1);
				cmdArray[idx] = beforeString + fileName + afterString;
			}
			else
			{
				cmdArray[idx] = s;
			}
		}

		if (cmdArray.length > 0)
		{
			try
			{
				Runtime.getRuntime().exec(cmdArray);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not execute " + postExportCommand + " after exporting " + fileName, ex);
			}
		}
	}

	/**
	 * Return TRUE if the weapon profs have a variable named x
	 * @param collectionOfProfs
	 * @param variableString
	 * @return TRUE if the weapon profs have a variable named x
	 */
	public static boolean hasWeaponProfVariableNamed(final Collection<WeaponProf> collectionOfProfs, final String variableString)
	{
		return weaponProfs.hasVariableNamed(collectionOfProfs, variableString);
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
	 */
	public static Load loadTypeForLoadScore(int loadScoreValue, final Float weight, final PlayerCharacter aPC)
	{
		final double dbl = weight.doubleValue() / maxLoadForLoadScore(loadScoreValue, aPC).doubleValue();

		if (SystemCollections.getLoadInfo().getLoadMultiplier("LIGHT") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("LIGHT").doubleValue() )
		{
			return Load.LIGHT;
		}

		if (SystemCollections.getLoadInfo().getLoadMultiplier("MEDIUM") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("MEDIUM").doubleValue() )
		{
			return Load.MEDIUM;
		}

		if (SystemCollections.getLoadInfo().getLoadMultiplier("HEAVY") != null &&
				dbl <= SystemCollections.getLoadInfo().getLoadMultiplier("HEAVY").doubleValue() )
		{
			return Load.HEAVY;
		}

		return Load.OVERLOAD;
	}

	/**
	 * Size is taken into account for the currentPC.
	 * @param loadScore
	 * @param aPC
	 * @return Float
	 */
	public static Float maxLoadForLoadScore(final int loadScore, final PlayerCharacter aPC)
	{
		return maxLoadForLoadScore(loadScore, aPC, new Float(1.0));
	}

	/**
	 * @param loadScore
	 * @param aPC
	 * @param mult
	 * @return Float
	 */
	public static Float maxLoadForLoadScore(
			final int loadScore,
			final PlayerCharacter aPC,
			final Float mult)
	{
		final Float loadValue = SystemCollections.getLoadInfo().getLoadScoreValue(loadScore);
		String formula = SystemCollections.getLoadInfo().getLoadModifierFormula();
		if (formula.length() != 0)
		{
			formula = formula.replaceAll(Pattern.quote("$$SCORE$$"),
			                             Double.toString(loadValue.doubleValue() * 
			                                             mult.doubleValue() * 
			                                             getLoadMultForSize(aPC)));
			return (float) aPC.getVariableValue(formula, "").intValue();
		}
		return new Float(loadValue.doubleValue() * mult.doubleValue() * getLoadMultForSize(aPC));
	}

	/**
	 * Remove a weapon prof by key
	 * @param aKey
	 */
	public static void removeWeaponProfKeyed(final String aKey)
	{
		weaponProfs.removeNamed(aKey);
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

			case Constants.HP_AVERAGE_ROUNDED_UP:
				roll = (int)Math.ceil((min + max)/2.0);

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
				rollChoices[i - min + 1] = Integer.valueOf(i);
			}

			while (min <= max)
			{
				//TODO: This must be refactored away. Core shouldn't know about gui.
				final InputInterface ii = InputFactory.getInputInstance();
				final Object selectedValue = ii.showInputDialog(Globals.getRootFrame(),
					"Randomly generate a number between " + min + " and " + max + "." + Constants.s_LINE_SEP
					+ "Select it from the box below.",
					Globals.getGameModeHitPointText() + " for " + CoreUtility.ordinal(level) + " level of " + name,
					MessageType.INFORMATION, rollChoices, Integer.valueOf(roll));

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
			final PaperInfo pi = SystemCollections.getUnmodifiablePaperInfo().get(i);

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
	 * Sort campaign data using the order they will be searched by.
	 */
	public static void sortCampaigns()
	{
		sortPObjectListByKey(getClassList());
		sortPObjectListByKey(getSkillList());
		// sortPObjectList(getFeatList()); Obsolete data structure
		sortPObjectListByKey(getDeityList());
		sortPObjectListByKey(getDomainList());
		Collections.sort(getArmorProfList());
		sortPObjectListByKey(getTemplateList());
		sortPObjectListByKey(getLanguageList());
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
			// NOCHOICE feats add nulls to the selectedList
			if ( selectedList.size() > 0 && selectedList.get(0) != null )
			{
				Collections.sort(selectedList);
			}
		}
		else
		{
			Globals.sortPObjectListByName(availableList);
			Globals.sortPObjectListByName(selectedList);
		}
	}

	/**
	 * Sort Pcgen Object list
	 * @param aList
	 * @return Sorted list of Pcgen Objects
	 */
	public static List<? extends PObject> sortPObjectList(final List<? extends PObject> aList)
	{
		Collections.sort(aList, pObjectComp);

		return aList;
	}
	
	/**
	 * Sort Pcgen Object list by name
	 * @param <T> 
	 * 
	 * @param aList
	 * @return Sorted list of Pcgen Objects
	 */
	public static <T extends PObject> List<T> sortPObjectListByName(final List<T> aList)
	{
		Collections.sort(aList, pObjectNameComp);

		return aList;
	}
	
	/**
	 * Sort Pcgen Object list by key
	 * 
	 * @param aList The list to be sorted.
	 * @return Sorted list of Pcgen Objects
	 */
	public static <T extends PObject> List<T> sortPObjectListByKey(final List<T> aList)
	{
		Collections.sort(aList, pObjectStringComp);

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
	protected static <T extends PObject> T searchPObjectList(final List<T> aList, final String keyName)
	{
		if ((keyName == null) || (keyName.length() <= 0))
		{
			return null;
		}
		if ( aList == null || aList.size() == 0 )
		{
			return null;
		}

		if (isD_sorted())
		{
			return binarySearchPObject(aList, keyName);
		}

		// not presently sorted
		for ( T pobj : aList )
		{
			if ( keyName.equalsIgnoreCase( pobj.getKeyName() ) )
			{
				return pobj;
			}
		}

		return null;
	}
	
	static String getBonusFeatString() {
		return SettingsHandler.getGame().getBonusFeatLevels().get(0);
	}

	static int getBonusFeatsForLevel(final int level)
	{
		int num = 0;

		for (String s : SettingsHandler.getGame().getBonusFeatLevels())
		{
			num = bonusParsing(s, level, num);
		}

		return num;
	}

	static int getBonusStatsForLevel(final int level)
	{
		int num = 0;

		for (String s : SettingsHandler.getGame().getBonusStatLevels())
		{
			num = bonusParsing(s, level, num);
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
	public static <T> List<T> getChoiceFromList(final String title, final List<T> choiceList, final List<T> selectedList, final int pool)
	{
		return getChoiceFromList(title, choiceList, selectedList, pool, false);
	}

	static <T> List<T> getChoiceFromList(final String title, final List<T> choiceList, final List<T> selectedList, final int pool, final boolean forceChoice)
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

	static List<String> getCustColumnWidth()
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
	 * returns the location of the "filepaths.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 * @return option path
	 */
	static String getFilepathsPath()
	{
		String aPath;

		// first see if it was specified on the command line
		aPath = System.getProperty("pcgen.filepaths"); //$NON-NLS-1$

		if (aPath == null)
		{
			aPath = System.getProperty("user.dir") + File.separator + "filepaths.ini"; //$NON-NLS-1$ //$NON-NLS-2$;
		}
		else
		{
			File testPath=new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "filepaths.ini"; //$NON-NLS-1$
				testPath=new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging
					.errorPrint("WARNING: The filepaths file you specified is not updatable. "
						+ "Filepath changes will not be saved. File is "
						+ testPath.getAbsolutePath());
			}
		}

		return expandRelativePath(aPath);
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
		else
		{
			File testPath=new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "filter.ini";
				testPath=new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging
					.errorPrint("WARNING: The filter file you specified is not updatable. "
						+ "Filter changes will not be saved. File is "
						+ testPath.getAbsolutePath());
			}
		}

		return expandRelativePath(aPath);
	}

	static List<Language> getLanguagesFromListOfType(final List<Language> langList, final String aType)
	{
		final List<Language> retSet = new ArrayList<Language>();

		for ( Language lang : langList )
		{
			if ((lang != null)
				&& (lang.isType(aType) || ((aType.length() > 0) && (aType.charAt(0) == '!') && !lang.isType(aType))))
			{
				retSet.add(lang);
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
		else
		{
			File testPath=new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "options.ini";
				testPath=new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging
					.errorPrint("WARNING: The options file you specified is not updatable. "
						+ "Settings changes will not be saved. File is "
						+ testPath.getAbsolutePath());
			}
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
		final int rand = getRandom().nextInt(high);
		Logging.debugPrint("Generated random number between 0 and " + high + ": " + rand);  //$NON-NLS-1$//$NON-NLS-2$
		return rand;
	}

	public static int getSkillMultiplierForLevel(final int level)
	{
		final List<String> sml = SettingsHandler.getGame().getSkillMultiplierLevels();

		if ((level > sml.size()) || (level <= 0))
		{
			return 1;
		}

		return Integer.parseInt(sml.get(level - 1));
	}

	/**
	 * Get the Weapon Profs
	 * @param type
	 * @param aPC
	 * @return List of Weapon Profs
	 */
	public static List<WeaponProf> getWeaponProfs(final String type, final PlayerCharacter aPC)
	{
		final List<WeaponProf> aList = new ArrayList<WeaponProf>();
		final List<WeaponProf> bList = new ArrayList<WeaponProf>();
		StringTokenizer aTok;
		WeaponProf tempProf;

		for ( String aString : aPC.getChangeProfList() )
		{
			aTok = new StringTokenizer(aString, "|");

			//
			// aString is format: WeapProf|ProfCategory
			// eg: Greataxe|Simple
			// eg: Sword (Bastard/Exotic)|Martial
			//
			final String eqName = aTok.nextToken();
			final String wpType = aTok.nextToken();
			tempProf = getWeaponProfKeyed(eqName);

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

		final Collection<WeaponProf> weaponProfsOfType = getAllWeaponProfsOfType(type);

		if (weaponProfsOfType == null)
		{
			return aList;
		}

		for ( WeaponProf tempProf2 : weaponProfsOfType )
		{
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

	/**
	 * Efficiently search for a PObject in a list with a particular key. The list
	 * must be sorted by key, otherwise the search may fail find entries in the list.
	 *   
	 * @param aList The list to be searched, must be sorted by key.
	 * @param keyName The key to be found.
	 * @return The object found, or null if not found. 
	 */
	static <T extends PObject> T binarySearchPObject(final List<T> aList, final String keyName)
	{
		if ((keyName == null) || (keyName.length() <= 0))
		{
			return null;
		}

		int index = Collections.binarySearch(aList, keyName, pObjectStringComp);
		if ( index >= 0 )
		{
			return aList.get(index);
		}
		return null;
	}

	static double calcEncumberedMove(final Load load, final double moveInt, final boolean checkLoad)
	{
		return calcEncumberedMove(load, moveInt, checkLoad, null);
	}

	/**
	 * Works for dnd according to the method noted in the faq.
	 * (NOTE: The table in the dnd faq is wrong for speeds 80 and 90)
	 * Not as sure it works for all other d20 games.
	 *
	 * @param load
	 * @param unencumberedMove the unencumbered move value
	 * @param checkLoad
	 * @param aPC
	 * @return encumbered move as an integer
	 */
	static double calcEncumberedMove(final Load load, final double unencumberedMove, final boolean checkLoad, final PlayerCharacter aPC)
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
					String formula = SystemCollections.getLoadInfo().getLoadMoveFormula(load.toString());
					if (formula.length() != 0)
					{
						formula = formula.replaceAll(Pattern.quote("$$MOVE$$"),
						                             Double.toString(Math.floor(unencumberedMove)));
						return aPC.getVariableValue(formula, "").doubleValue();
					}
				}

				switch (load)
				{
					case LIGHT:
						encumberedMove = unencumberedMove;

						break;

					case MEDIUM:
					case HEAVY:

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

					case OVERLOAD:
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

	/**
	 * Returns a list of Language objects from a string of choices.  The method
	 * will expand "ALL" or "ANY" into all languages and TYPE= into all
	 * languages of that type
	 * @param stringList Pipe separated list of language choices
	 * @return Sorted list of Language objects
	 */
	public static SortedSet<Language> getLanguagesFromString(final String stringList)
	{
		SortedSet<Language> list = new TreeSet<Language>();

		final StringTokenizer tokens = new StringTokenizer(stringList,	"|", false);

		while (tokens.hasMoreTokens())
		{
			final String aLang = tokens.nextToken();
			if ("ALL".equals(aLang))
			{
				list.addAll(getLanguageList());
				return list;
			}
			else if (aLang.startsWith("TYPE=") || aLang.startsWith("TYPE."))
			{
				list.addAll(getLanguagesOfType(aLang.substring(5)));
			}
			else
			{
				Language languageKeyed = getLanguageKeyed(aLang);
				if (languageKeyed == null)
				{
					Logging.debugPrint("Someone expected Language: " + aLang + " to exist: it doesn't");
				}
				else
				{
					list.add(languageKeyed);
				}
			}
		}
		return list;
	}

	static void initCustColumnWidth(final List<String> l)
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
	private static List<String> getArmorProfList()
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
			return "1|" + SettingsHandler.getGame().getDamageDownMap().get(aDamage);
		}

		final RollInfo aRollInfo = new RollInfo(aDamage);
		final String baseDice = "1d" + Integer.toString(aRollInfo.sides);

		if (SettingsHandler.getGame().getDamageDownMap().containsKey(baseDice))
		{
			return Integer.toString(aRollInfo.times) + "|"
			+ SettingsHandler.getGame().getDamageDownMap().get(baseDice);
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
			return "1|" + SettingsHandler.getGame().getDamageUpMap().get(aDamage);
		}

		final RollInfo aRollInfo = new RollInfo(aDamage);
		final String baseDice = "1d" + Integer.toString(aRollInfo.sides);

		if (SettingsHandler.getGame().getDamageUpMap().containsKey(baseDice))
		{
			return Integer.toString(aRollInfo.times) + "|"
			+ SettingsHandler.getGame().getDamageUpMap().get(baseDice);
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

	public static List<Language> getLanguagesOfType(final String aType)
	{
		return getPObjectsOfType(getLanguageList(), aType);
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

	private static SortedSet<String> getPantheonsSet()
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

	private static SortedSet<String> getSubschoolsSet()
	{
		return subschoolsSet;
	}

	private static boolean isUseGUI()
	{
		return useGUI;
	}

	private static int bonusParsing(final String l, final int level, int num)
	{
		// should be in format levelnum,rangenum
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

		addRace(s_EMPTYRACE);
//		getRaceMap().put(Constants.s_NONESELECTED, s_EMPTYRACE);
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
	public static void addSponsor(Map<String, String> sponsor) {
		sponsors.put(sponsor.get("SPONSOR"), sponsor);
		sponsorList.add(sponsor);
	}

	/**
	 * Get a list of sponsors of PCGen
	 * @return list of sponsors of PCGen
	 */
	public static List<Map<String, String>> getSponsors() {
		return sponsorList;
	}

	/**
	 * Get a sponsor
	 * @param name
	 * @return sponsor
	 */
	public static Map<String, String> getSponsor(String name) {
		return sponsors.get(name);
	}

	/**
	 * Returns a list of default genders used by the system.
	 * @return List of gender strings
	 * TODO - Genders need to become objects.
	 */
	public static List<String> getAllGenders()
	{
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(PropertyFactory.getString("in_genderMale")); //$NON-NLS-1$
		ret.add(PropertyFactory.getString("in_genderFemale")); //$NON-NLS-1$
		ret.add(PropertyFactory.getString("in_genderNeuter")); //$NON-NLS-1$
		ret.add(PropertyFactory.getString("in_comboNone")); //$NON-NLS-1$
		ret.add(PropertyFactory.getString("in_comboOther")); //$NON-NLS-1$

		return ret;
	}
	
	/**
	 * Get's Race from raceMap() based on aKey
	 * @param aKey
	 * @return keyed Race
	 */
	public static Race getRaceKeyed(final String aKey)
	{
		return raceMap.get(aKey.toLowerCase());
	}

	public static void addRace(final Race aRace)
	{
		raceMap.put(aRace.getKeyName().toLowerCase(), aRace);
	}
	
	public static Collection<Race> getAllRaces()
	{
		return Collections.unmodifiableCollection(raceMap.values());
	}
	
	public static boolean removeRaceKeyed(final String aKey)
	{
		return raceMap.remove(aKey.toLowerCase()) != null;
	}
	
}
