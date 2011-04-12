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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.content.BaseDice;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.MasterSkillFacet;
import pcgen.cdom.list.CompanionList;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSlot;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.persistence.PersistenceManager;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

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
	private static List<PlayerCharacter>            pcList      = new ArrayList<PlayerCharacter>();
	/** Race, a s_EMPTYRACE */
	public static  Race            s_EMPTYRACE;

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
	private static final String defaultPcgPath = Globals.getUserFilesPath() + File.separator + "characters"; //$NON-NLS-1$
	private static final String backupPcgPath = Constants.EMPTY_STRING;
	
	/** These are for the Internationalization project. */
	private static String     language        = "en"; //$NON-NLS-1$
	private static String     country         = "US"; //$NON-NLS-1$

	/** The BioSet used for age calculations */
	private static final List<String> custColumnWidth = new ArrayList<String>();
	private static SourceFormat sourceDisplay = SourceFormat.LONG;
	private static int        selectedPaper   = -1;

	/** we need maps for efficient lookups */
	private static Map<URI, Campaign>        campaignMap     = new HashMap<URI, Campaign>();

	/** TODO Why can spellMap contain both Spell and List<Spell>? Change to always contain List<Spell> (it is possible said list only has one member, but that's ok.)
	 * Does  need to be sorted? If not, change to HashMap.*/
	private static Map<String, Object>        spellMap        = new TreeMap<String, Object>();
	private static Map<String, String>        eqSlotMap       = new HashMap<String, String>();
	private static Map<CompanionList, List<CompanionMod>>  companionModMap = new TreeMap<CompanionList, List<CompanionMod>>(CDOMObjectUtilities.CDOM_SORTER);

	/** We use lists for efficient iteration */
	private static List<Campaign> campaignList          = new ArrayList<Campaign>(85);

	private static SortedSet<SpecialAbility>        saSet           = new TreeSet<SpecialAbility>();

	/** this is used by the random selection tools */
	private static final Random random = new Random(System.currentTimeMillis());

	/**
	 * The following sets are for efficient filter creation:
	 * <ul>
	 * <li>subschoolsSet</li>
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
	private static SortedSet<String> subschoolsSet    = new TreeSet<String>();

	private static SortedSet<String> castingTimesSet  = new TreeSet<String>();
	private static SortedSet<String> componentSet     = new TreeSet<String>();
	private static SortedSet<String> descriptorSet    = new TreeSet<String>();
	private static SortedSet<String> durationSet      = new TreeSet<String>();
	private static SortedSet<String> typeForSpellsSet = new TreeSet<String>();
	private static SortedSet<String> rangesSet        = new TreeSet<String>();
	private static SortedSet<String> saveInfoSet      = new TreeSet<String>();
	private static SortedSet<String> srSet            = new TreeSet<String>();
	private static SortedSet<String> targetSet        = new TreeSet<String>();

	// end of filter creation sets
	private static JFrame rootFrame;
	private static JFrame currentFrame;
	private static final StringBuffer section15 = new StringBuffer(30000);
	private static final String spellPoints = "0";

	/** whether or not the GUI is used (false for command line) */
	private static boolean useGUI = true;

	/** whether or not we are running on a Mac */
	public static final boolean isMacPlatform = System.getProperty("os.name").equals("Mac OS X");
	/** default location for options.ini on a Mac */
	public static final String defaultMacOptionsPath = System.getProperty("user.home") + "/Library/Preferences/pcgen";

	private static final Comparator<PObject> pObjectComp = new Comparator<PObject>()
		{
			public int compare(final PObject o1, final PObject o2)
			{
				return o1.getKeyName().compareToIgnoreCase(o2.getKeyName());
			}
		};

	private static final Comparator<CDOMObject> pObjectNameComp = new Comparator<CDOMObject>()
		{
			public int compare(final CDOMObject o1, final CDOMObject o2)
			{
				final Collator collator = Collator.getInstance();
				
				// Check sort keys first
				String key1 = o1.get(StringKey.SORT_KEY);
				if (key1 == null)
				{
					key1 = o1.getDisplayName();
				}
				String key2 = o2.get(StringKey.SORT_KEY);
				if (key2 == null)
				{
					key2 = o2.getDisplayName();
				}
				if (!key1.equals(key2))
				{
					return collator.compare(key1, key2);
				}
				return collator.compare(o1.getDisplayName(), o2.getDisplayName());
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
	 * Get the Bio Set
	 * @return the Bio Set
	 */
	public static BioSet getBioSet()
	{
		return SettingsHandler.getGame().getBioSet();
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
		
		Campaign campaign = getCampaignKeyedSilently(aKey);
		if (campaign == null)
		{
			Logging.errorPrint("Could not find campaign: " + aKey);
		}

		return campaign;
	}

	/**
	 * Get campaign by key
	 * @param aKey
	 * @return Campaign
	 */
	public static Campaign getCampaignKeyedSilently(final String aKey)
	{
		for ( Campaign campaign : getCampaignList() )
		{
			if (campaign.getKeyName().equalsIgnoreCase(aKey))
			{
				return campaign;
			}
		}

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
	 * Finds all PObjects that match the passed in type.  All the types listed
	 * in aType must match for the object to be returned.
	 * @param aPObjectList List of PObjects to search
	 * @param aType A "." separated list of TYPEs to match
	 * @return List of PObjects matching all TYPEs
	 */
	public static <T extends PObject> List<T> getPObjectsOfType(final Collection<T> aPObjectList, final String aType)
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
	 * Adds a <tt>CompanionMod</tt> to the global list of companion mods
	 * registered in the system.
	 * 
	 * @param aMod A <tt>CompanionMod</tt> to add.
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	public static void addCompanionMod(final CompanionMod aMod)
	{
		String type = aMod.getType();
		if (type == null || type.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, "CompanionMod must have a TYPE:, "
					+ aMod.getKeyName() + " was not assigned a TYPE");
		}
		else
		{
			CompanionList cList = Globals.getContext().ref.constructNowIfNecessary(
					CompanionList.class, type);
			List<CompanionMod> mods = companionModMap.get(cList);
			if (mods == null)
			{
				mods = new ArrayList<CompanionMod>();
				companionModMap.put(cList, mods);
			}
			mods.add(aMod);
		}
	}

	/**
	 * Removes a <tt>CompanionMod</tt> from the system registry.
	 * 
	 * <p>
	 * This method is used by the .FORGET logic to remove a CompanionMod
	 * previously loaded by another set.
	 * 
	 * @param aMod
	 *            A <tt>CompanionMod</tt> to remove
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
	public static Collection<CompanionMod> getCompanionMods(
			final CompanionList cList)
	{
		final List<CompanionMod> cMods = companionModMap.get(cList);
		if (cMods == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(companionModMap.get(cList));
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
	 * Get the default path
	 * @return default path
	 */
	public static String getUserFilesPath()
	{
		return expandRelativePath(System.getProperty("user.home") + File.separator + ".pcgen");
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
	 * Get descriptor set
	 * @return descriptor set
	 */
	public static SortedSet<String> getDescriptorSet()
	{
		return descriptorSet;
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
	private static Map<String, String> getEquipSlotMap()
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
	 * Get long currency display
	 * @return long currency display
	 */
	public static String getLongCurrencyDisplay()
	{
		return SettingsHandler.getGame().getLongCurrencyDisplay();
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
	 * Get paper count
	 * @return paper count
	 */
	public static int getPaperCount()
	{
		return SettingsHandler.getGame().getModeContext().ref
				.getConstructedObjectCount(PaperInfo.class);
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
		if ((idx < 0)
				|| (idx >= SettingsHandler.getGame().getModeContext().ref
						.getConstructedObjectCount(PaperInfo.class)))
		{
			return null;
		}

		final PaperInfo pi = SettingsHandler.getGame().getModeContext().ref
				.getItemInOrder(PaperInfo.class, idx);

		return pi.getPaperInfo(infoType);
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
	 * Return TRUE if the skill type is hidden
	 * @param aType
	 * @return TRUE if the skill type is hidden
	 */
	public static boolean isSkillTypeHidden(final String aType)
	{
		return SettingsHandler.getGame().isTypeHidden(Skill.class, aType);
	}

	/**
	 * Set source display
	 * @param sourceType
	 */
	public static void setSourceDisplay(final SourceFormat sourceType)
	{
		sourceDisplay = sourceType;
	}

	/**
	 * Get source display
	 * @return source display
	 */
	public static SourceFormat getSourceDisplay()
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
	 * @param pc TODO
	 * @param classKey  (optional, ignored if "")
	 * @param domainKey (optional, ignored if "")
	 *                   at least one of classKey and domainKey must not be ""
	 * @return a List of Spell
	 */
	public static List<Spell> getSpellsIn(final int level, List<? extends CDOMList<Spell>> spellLists, PlayerCharacter pc)
	{
		MasterListInterface masterLists = Globals.getMasterLists();
		ArrayList<CDOMReference<CDOMList<Spell>>> useLists = new ArrayList<CDOMReference<CDOMList<Spell>>>();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			for (CDOMList<Spell> list : spellLists)
			{
				if (ref.contains(list))
				{
					useLists.add(ref);
					break;
				}
			}
		}
		boolean allLevels = level == -1;
		Set<Spell> spellList = new HashSet<Spell>();
		for (CDOMReference<CDOMList<Spell>> ref : useLists)
		{
			for (Spell spell : masterLists.getObjects(ref))
			{
				Collection<AssociatedPrereqObject> assoc = masterLists
						.getAssociations(ref, spell);
				for (AssociatedPrereqObject apo : assoc)
				{
					// TODO This null for source is incorrect!
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), pc,
							null))
					{
						int lvl = apo
								.getAssociation(AssociationKey.SPELL_LEVEL);
						if (allLevels || level == lvl)
						{
							spellList.add(spell);
							break;
						}
					}
				}
			}
			if (pc != null)
			{
				DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> dkmtl = pc
						.getPCBasedLevelInfo();
				for (Spell spell : dkmtl.getKeySet())
				{
					for (CDOMList<Spell> list : dkmtl.getSecondaryKeySet(spell))
					{
						if (spellLists.contains(list))
						{
							List<Integer> levels = dkmtl
									.getListFor(spell, list);
							if (levels != null
									&& (allLevels || levels.contains(level)))
							{
								spellList.add(spell);
								break;
							}
						}
					}
				}
			}
		}

		return new ArrayList<Spell>(spellList);
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
	 * Get type for spells set
	 * @return type for spells set
	 */
	public static SortedSet<String> getTypeForSpells()
	{
		return typeForSpellsSet;
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
	 * Add to spell target set
	 * @param aString
	 */
	public static void addSpellTargetSet(final String aString)
	{
		targetSet.add(aString);
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
		ReferenceContext ref = Globals.getContext().ref;
		BaseDice bd = ref.silentlyGetConstructedCDOMObject(BaseDice.class,
				aDamage);
		int multiplier = 0;
		List<RollInfo> steps = null;
		if (bd == null)
		{
			//Need to test for higher dice
			final RollInfo aRollInfo = new RollInfo(aDamage);
			final String baseDice = "1d" + Integer.toString(aRollInfo.sides);
			bd = ref.silentlyGetConstructedCDOMObject(BaseDice.class,
					baseDice);
			if (bd != null)
			{
				multiplier = aRollInfo.times;
			}
		}
		else
		{
			multiplier = 1;
		}
		RollInfo bi;
		if (bd == null)
		{
			bi = new RollInfo(aDamage);
		}
		else
		{
			if (baseSize < finalSize)
			{
				steps = bd.getUpSteps();
			}
			else if (baseSize > finalSize)
			{
				steps = bd.getDownSteps();
			}
			else
			{
				// Not a warning?
				return aDamage;
			}
			int difference = Math.abs(baseSize - finalSize);

			int index;
			if (steps.size() > difference)
			{
				index = difference - 1;
			}
			else
			{
				index = steps.size() - 1;
			}
			bi = steps.get(index);
		}
		if (multiplier > 1)
		{
			// Ugh, have to do this for "cloning" to avoid polluting the master
			// RollInfo
			bi = new RollInfo(bi.toString());
			bi.times *= multiplier;
		}
		return bi.toString();
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
		List<String> upperTypeList = new ArrayList<String>(typeList.size());
		for (String type : typeList)
		{
			upperTypeList.add(type.toUpperCase());
		}

		List<String> resizeTypeList = SettingsHandler.getGame().getResizableTypeList();
		for (String rType : resizeTypeList)
		{
			if (upperTypeList.contains(rType.toUpperCase()))
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
		RuleCheck rule = SettingsHandler.getGame().getModeContext().ref
				.silentlyGetConstructedCDOMObject(RuleCheck.class, aKey);
		if (rule == null)
		{
			return false;
		}
		if (SettingsHandler.hasRuleCheck(aKey))
		{
			return SettingsHandler.getRuleCheck(aKey);
		}
		else
		{
			return rule.getDefault();
		}
	}

	/**
	 * This method is called by the persistence layer to clear the global
	 * campaigns for a refresh.
	 */
	public static void clearCampaignsForRefresh()
	{
		emptyLists();
		campaignMap.clear();
		campaignList.clear();
		hasSpellPPCost = false;
	}

	/**
	 * Check if enough data has been loaded to support character creation.
	 * Will also report to the log the number of items of each of the 
	 * necessary types that are currently loaded.  
	 * @return true or false
	 */
	public static boolean displayListsHappy()
	{
		// NOTE: If you add something here be sure to update the log output below
		boolean listsHappy = checkListsHappy();

		Level logLevel = listsHappy ? Logging.DEBUG : Logging.WARNING;
		Logging.log(logLevel, "Number of objects loaded. The following should "
			+ "all be greater than 0:");
		Logging.log(logLevel, "Races=" + Globals.getContext().ref.getConstructedCDOMObjects(Race.class).size());
		Logging.log(logLevel, "Classes=" + getContext().ref.getConstructedCDOMObjects(PCClass.class).size());
		Logging.log(logLevel, "Skills=" + Globals.getContext().ref.getConstructedCDOMObjects(Skill.class).size());
		Logging.log(logLevel, "Feats="
				+ Globals.getContext().ref.getManufacturer(Ability.class,
						AbilityCategory.FEAT).getConstructedObjectCount());
		Logging.log(logLevel, "Equipment=" + Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class).size());
		Logging.log(logLevel, "ArmorProfs=" + Globals.getContext().ref.getConstructedCDOMObjects(ArmorProf.class).size());
		Logging.log(logLevel, "ShieldProfs=" + Globals.getContext().ref.getConstructedCDOMObjects(ShieldProf.class).size());
		Logging.log(logLevel, "WeaponProfs=" + Globals.getContext().ref.getConstructedCDOMObjects(WeaponProf.class).size());
		Logging.log(logLevel, "Kits=" + Globals.getContext().ref.getConstructedCDOMObjects(Kit.class).size());
		Logging.log(logLevel, "Templates=" + Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class).size());

		return listsHappy;
	}

	/**
	 * Check if enough data has been loaded to support character creation.
	 * @return true or false
	 */
	public static boolean checkListsHappy()
	{
		// NOTE: If you add something here be sure to update the log output in displayListsHappy above
		boolean listsHappy = !((Globals.getContext().ref.getConstructedCDOMObjects(Race.class).size() == 0)
				|| (getContext().ref.getConstructedCDOMObjects(PCClass.class).size() == 0)
				|| (Globals.getContext().ref.getConstructedCDOMObjects(Skill.class).size() == 0)
				|| (Globals.getContext().ref.getManufacturer(
						Ability.class, AbilityCategory.FEAT).getConstructedObjectCount() == 0)
				|| (Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class).size() == 0)
				|| (Globals.getContext().ref.getConstructedCDOMObjects(WeaponProf.class).size() == 0));
		return listsHappy;
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
		companionModMap = new TreeMap<CompanionList, List<CompanionMod>>(CDOMObjectUtilities.CDOM_SORTER);
		saSet = new TreeSet<SpecialAbility>();

		// Clear Maps (not strictly necessary, but done for consistency)
		spellMap = new HashMap<String, Object>();
		VisionType.clearConstants();

		// Clear Sets (not strictly necessary, but done for consistency)
		clearSpellSets();
		subschoolsSet = new TreeSet<String>();

		// Perform other special cleanup
		Equipment.clearEquipmentTypes();
		PersistenceManager.getInstance().emptyLists();
		SettingsHandler.getGame().clearLoadContext();
		FacetLibrary.getFacet(MasterSkillFacet.class).emptyLists();

		
		Pantheon.clearConstants();
		RaceType.clearConstants();
		createEmptyRace();
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
	private static void executePostExportCommand(final String fileName, String postExportCommand)
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
	 * roll HP
	 * @param min
	 * @param max
	 * @param name
	 * @param level
	 * @return HP
	 */
	public static int rollHP(final int min, final int max, final String name, final int level, final int totalLevel)
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

				if (((totalLevel & 0x01) == 0) && ((roll & 0x01) != 0))
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
		for (int i = 0; i < SettingsHandler.getGame().getModeContext().ref
				.getConstructedObjectCount(PaperInfo.class); ++i)
		{
			final PaperInfo pi = SettingsHandler.getGame().getModeContext().ref
					.getItemInOrder(PaperInfo.class, i);

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
	 * Sorts chooser lists using the appropriate method, based on the type of the first item in either list.
	 * Not pretty, but it works.
	 *
	 * @param availableList
	 * @param selectedList
	 */
	public static void sortChooserLists(final List availableList, final List selectedList)
	{
		final boolean nonPObjectInList;

		if (availableList.size() > 0)
		{
			nonPObjectInList = ! (availableList.get(0) instanceof PObject);
		}
		else if (selectedList.size() > 0)
		{
			nonPObjectInList = ! (selectedList.get(0) instanceof PObject);
		}
		else
		{
			nonPObjectInList = false;
		}

		if (nonPObjectInList)
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
	public static <T extends CDOMObject> List<T> sortPObjectListByName(final List<T> aList)
	{
		Collections.sort(aList, pObjectNameComp);

		return aList;
	}
	
	static String getBonusFeatString() {
		return SettingsHandler.getGame().getBonusFeatLevels().get(0);
	}

	static int getBonusStatsForLevel(final int level, final PlayerCharacter aPC)
	{
		int num = 0;

		for (String s : SettingsHandler.getGame().getBonusStatLevels())
		{
			num = bonusParsing(s, level, num, aPC);
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

	public static <T> List<T> getChoiceFromList(final String title, final List<T> choiceList, final List<T> selectedList, final int pool, final boolean forceChoice)
	{
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTotalChoicesAvail(pool);
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
	 * Reduce/increase damage for modified size as per DMG p.162
	 * @param aDamage
	 * @param sBaseSize
	 * @param sNewSize
	 * @return String
	 */
	public static String adjustDamage(String aDamage, SizeAdjustment baseSize, SizeAdjustment newSize)
	{
		if (aDamage.length() == 0)
		{
			return aDamage;
		}
		int baseIndex = SizeUtilities.sizeInt(baseSize);
		int newIndex =  SizeUtilities.sizeInt(newSize);
		return adjustDamage(aDamage, baseIndex, newIndex);
	}

	public static double calcEncumberedMove(final Load load,
			final double unencumberedMove)
	{
		double encumberedMove;

		switch (load)
		{
		case LIGHT:
			encumberedMove = unencumberedMove;

			break;

		case MEDIUM:
		case HEAVY:

			if (CoreUtility.doublesEqual(unencumberedMove, 5))
			{
				encumberedMove = 5;
			}
			else if (CoreUtility.doublesEqual(unencumberedMove, 10))
			{
				encumberedMove = 5;
			}
			else
			{
				encumberedMove = (Math.floor(unencumberedMove / 15) * 10)
						+ (((int) unencumberedMove) % 15);
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

		return encumberedMove;
	}

	/**
	 * Works for dnd according to the method noted in the faq. (NOTE: The table
	 * in the dnd faq is wrong for speeds 80 and 90) Not as sure it works for
	 * all other d20 games.
	 * 
	 * @param load
	 * @param unencumberedMove
	 *            the unencumbered move value
	 * @param checkLoad
	 * @param aPC
	 * @return encumbered move as an integer
	 */
	static double calcEncumberedMove(final Load load, final double unencumberedMove, final PlayerCharacter aPC)
	{
		double encumberedMove;

		//
		// Can we ignore any encumberance for this type? If we can, then there's
		// no need to do any more calculations.
		//
		if (aPC.ignoreEncumberedLoadMove(load))
		{
			encumberedMove = unencumberedMove;
		}
		else
		{
			String formula = SettingsHandler.getGame().getLoadInfo()
					.getLoadMoveFormula(load.toString());
			if (formula.length() != 0)
			{
				formula = formula.replaceAll(Pattern.quote("$$MOVE$$"), Double
						.toString(Math.floor(unencumberedMove)));
				return aPC.getVariableValue(formula, "").doubleValue();
			}

			encumberedMove = calcEncumberedMove(load, unencumberedMove);
		}

		return encumberedMove;
	}

	// Methods

	static void initCustColumnWidth(final List<String> l)
	{
		getCustColumnWidth().clear();
		getCustColumnWidth().addAll(l);
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
		else if (fType.equals("mac_user"))
		{
			// use the users "home" directory + standard Mac settings
			return System.getProperty("user.home") + "/Library/Preferences/pcgen" + File.separator + aString;
		}
		else
		{
			// use the specified directory
			return fType + File.separator + aString;
		}
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

	private static int bonusParsing(final String l, final int level, int num, final PlayerCharacter aPC)
	{
		// should be in format levelnum,rangenum[,numchoices] 
		final StringTokenizer aTok = new StringTokenizer(l, "|", false);
		final int startLevel = Integer.parseInt(aTok.nextToken());
		final String rangeLevelFormula = aTok.nextToken();
		final int rangeLevel = aPC.getVariableValue(rangeLevelFormula, "").intValue();
		int numChoices = 1;
		if (aTok.hasMoreTokens())
		{
			numChoices = Integer.parseInt(aTok.nextToken());
		}

		if ((level == startLevel)
			|| ((level > startLevel) && (rangeLevel > 0) && (((level - startLevel) % rangeLevel) == 0)))
		{
			num+=numChoices;
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
		targetSet.clear();
	}

	public static void createEmptyRace()
	{
		if (s_EMPTYRACE == null)
		{
			s_EMPTYRACE = new Race();
			s_EMPTYRACE.setName(Constants.s_NONESELECTED);
			s_EMPTYRACE.addToListFor(ListKey.TYPE, Type.HUMANOID);
		}

		getContext().ref.importObject(s_EMPTYRACE);
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
	 * Returns a list of default genders used by the system.
	 * @return List of gender strings
	 * TODO - Genders need to become objects.
	 */
	public static List<Gender> getAllGenders()
	{
		ArrayList<Gender> ret = new ArrayList<Gender>();
		ret.add(Gender.Male); 
		ret.add(Gender.Female); 
		ret.add(Gender.Neuter);
//		ret.add(PropertyFactory.getString("in_comboNone")); //$NON-NLS-1$
//		ret.add(PropertyFactory.getString("in_comboOther")); //$NON-NLS-1$

		return ret;
	}
	
	/**
	 * Get's current gamemodes DieSizes
	 * @return dieSizes array
	 */
	public static int[] getDieSizes()
	{
		return SettingsHandler.getGame().getDieSizes();
	}

	public static LoadContext getContext()
	{
		return SettingsHandler.getGame().getContext();
	}

	private static LoadContext globalContext = new RuntimeLoadContext(
			new RuntimeReferenceContext(), new ConsolidatedListCommitStrategy());

	public static LoadContext getGlobalContext()
	{
		return globalContext;
	}

	/**
	 * @return The class instance controlling the association lists for the current game mode.
	 */
	public static MasterListInterface getMasterLists()
	{
		return SettingsHandler.getGame().getMasterLists();
	}
	
	private static boolean hasSpellPPCost;
	
	public static boolean hasSpellPPCost()
	{
		return hasSpellPPCost;
	}
	
	public static <T extends PObject> List<T> getObjectsOfVisibility(Collection<T> c, Visibility v)
	{
		ArrayList<T> aList = new ArrayList<T>();
		for (T po : c)
		{
			Visibility poVis = po.getSafe(ObjectKey.VISIBILITY);
			if (v == Visibility.DEFAULT || poVis == Visibility.DEFAULT
					|| poVis == v)
			{
				aList.add(po);
			}
		}
		return aList;
	}

	/**
	 * Return the set of equipment type names as a sorted set of strings.
	 * 
	 * @return The equipmentTypes value
	 */
	public static Collection<String> getEquipmentTypes() {
		return getContext().getTypes(Equipment.class);
	}

}
