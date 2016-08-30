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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.apache.commons.lang.SystemUtils;

import pcgen.base.util.RandomUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.content.BaseDice;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.character.EquipSlot;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.VisionType;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 *
 * @author Bryan McRoberts &lt;merton_monk@users.sourceforge.net&gt;
 * @author boomer70 &lt;boomer70@yahoo.com&gt;
 * @version $Revision$
 */
public final class Globals
{
	/** These are changed during normal operation */
	private static List<PlayerCharacter>            pcList      = new ArrayList<>();
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
	private static final String defaultPcgPath = Globals.getUserFilesPath() + File.separator + "characters"; //$NON-NLS-1$
	
	private static final List<String> custColumnWidth = new ArrayList<>();
	private static SourceFormat sourceDisplay = SourceFormat.LONG;
	private static int        selectedPaper   = -1;

	/** we need maps for efficient lookups */
	private static Map<URI, Campaign>    campaignMap     = new HashMap<>();
	private static Map<String, Campaign> campaignNameMap = new HashMap<>();
	private static Map<String, Spell>    spellMap        = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private static Map<String, String>   eqSlotMap       = new HashMap<>();

	/** We use lists for efficient iteration */
	private static List<Campaign> campaignList          = new ArrayList<>(85);

	// end of filter creation sets
	private static JFrame rootFrame;
	private static final StringBuilder section15 = new StringBuilder(30000);

	/** whether or not the GUI is used (false for command line) */
	private static boolean useGUI = true;

	/** whether or not we are running on a Mac */
	public static final boolean isMacPlatform = SystemUtils.IS_OS_MAC;
	/** default location for options.ini on a Mac */
	public static final String defaultMacOptionsPath = System.getProperty("user.home") + "/Library/Preferences/pcgen";

	private static final Comparator<CDOMObject> pObjectComp = new Comparator<CDOMObject>()
		{
        @Override
			public int compare(final CDOMObject o1, final CDOMObject o2)
			{
				return o1.getKeyName().compareToIgnoreCase(o2.getKeyName());
			}
		};

	public static final Comparator<CDOMObject> pObjectNameComp = new Comparator<CDOMObject>()
		{
        @Override
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
				if (!o1.getDisplayName().equals(o2.getDisplayName()))
				{
					return collator.compare(o1.getDisplayName(), o2.getDisplayName());
				}
				// Fall back to keyname if the displayname is the same
				return collator.compare(o1.getKeyName(), o2.getKeyName());
			}
		};

	// Optimizations used by any code needing empty arrays.  All empty arrays
	// of the same type are idempotent.
	/** EMPTY_STRING_ARRAY*/
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

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

		return new ArrayList<>();
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
	 * Finds all PObjects that match the passed in type.  All the types listed
	 * in aType must match for the object to be returned.
	 * @param aPObjectList List of PObjects to search
	 * @param aType A "." separated list of TYPEs to match
	 * @return List of PObjects matching all TYPEs
	 */
	public static <T extends CDOMObject> List<T> getPObjectsOfType(final Collection<T> aPObjectList, final String aType)
	{
		final ArrayList<T> ret = new ArrayList<>(aPObjectList.size());

		List<String> typeList = new ArrayList<>();
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
		return ConfigurationSettings.getUserDir();
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
	 * Get game mode align text
	 * @return game mode align text
	 */
	public static String getGameModeAlignmentText()
	{
		return SettingsHandler.getGame().getAlignmentText();
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
	 * Get global deity list
	 * @return global deity lis
	 */
	public static List<String> getGlobalDeityList()
	{
		if (SettingsHandler.getGame() != null)
		{
			return SettingsHandler.getGame().getDeityList();
		}

		return new ArrayList<>();
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
		return SettingsHandler.getGame().getModeContext().getReferenceContext()
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
				|| (idx >= SettingsHandler.getGame().getModeContext().getReferenceContext()
						.getConstructedObjectCount(PaperInfo.class)))
		{
			return null;
		}

		final PaperInfo pi = SettingsHandler.getGame().getModeContext().getReferenceContext()
				.getItemInOrder(PaperInfo.class, idx);

		return pi.getPaperInfo(infoType);
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
	 * Get the section 15
	 * @return section 15
	 */
	public static StringBuilder getSection15()
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
		return getSpellMap().get(aKey);
	}

	/**
	 * Get spell map
	 * @return spell map
	 */
	public static Map<String, Spell> getSpellMap()
	{
		return Collections.unmodifiableMap(spellMap);
	}

	/**
	 * Add spell to the spell map.
	 * 
	 * @param key The key the object is associated with.
	 * @param spell The object to be added to the map.
	 */
	public static void addToSpellMap(final String key, final Spell spell)
	{
		spellMap.put(key, spell);
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
		Campaign oldCampaign = campaignNameMap.put(campaign.getName(), campaign);
		if (oldCampaign != null)
		{
			if (oldCampaign.getSourceURI().toString()
				.equalsIgnoreCase(campaign.getSourceURI().toString()))
			{
				Logging.errorPrint("The campaign ("
					+ campaign.getName()
					+ ") was referenced with the incorrect case: "
					+ oldCampaign.getSourceURI() + " vs "
					+ campaign.getSourceURI());
			}
			else
			{
				Logging.errorPrint("Loaded Campaigns with matching names ("
					+ campaign.getName() + ") at different Locations: "
					+ oldCampaign.getSourceURI() + " "
					+ campaign.getSourceURI());
			}
		}
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
		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
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
		List<String> upperTypeList = new ArrayList<>(typeList.size());
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
		RuleCheck rule = SettingsHandler.getGame().getModeContext().getReferenceContext()
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
		if (Logging.isLoggable(logLevel))
		{
			Logging.log(logLevel, "Number of objects loaded. The following should "
				+ "all be greater than 0:");
			Logging.log(logLevel, "Races=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Race.class).size());
			Logging.log(logLevel, "Classes=" + getContext().getReferenceContext().getConstructedCDOMObjects(PCClass.class).size());
			Logging.log(logLevel, "Skills=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class).size());
			Logging.log(logLevel, "Feats="
					+ Globals.getContext().getReferenceContext().getManufacturer(Ability.class,
					AbilityCategory.FEAT).getConstructedObjectCount());
			Logging.log(logLevel, "Equipment=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class).size());
			Logging.log(logLevel, "ArmorProfs=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(ArmorProf.class).size());
			Logging.log(logLevel, "ShieldProfs=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(ShieldProf.class).size());
			Logging.log(logLevel, "WeaponProfs=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(WeaponProf.class).size());
			Logging.log(logLevel, "Kits=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Kit.class).size());
			Logging.log(logLevel, "Templates=" + Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCTemplate.class).size());
		}
		return listsHappy;
	}

	/**
	 * Check if enough data has been loaded to support character creation.
	 * @return true or false
	 */
	public static boolean checkListsHappy()
	{
		// NOTE: If you add something here be sure to update the log output in displayListsHappy above
		boolean listsHappy = !((Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Race.class).size() == 0)
				|| (getContext().getReferenceContext().getConstructedCDOMObjects(PCClass.class).size() == 0)
//				|| (Globals.getContext().ref.getConstructedCDOMObjects(Skill.class).size() == 0)
//				|| (Globals.getContext().ref.getManufacturer(
//						Ability.class, AbilityCategory.FEAT).getConstructedObjectCount() == 0)
				|| (Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class).size() == 0)
				|| (Globals.getContext().getReferenceContext().getConstructedCDOMObjects(WeaponProf.class).size() == 0));
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

		// Clear Maps (not strictly necessary, but done for consistency)
		spellMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		VisionType.clearConstants();

		// Perform other special cleanup
		Equipment.clearEquipmentTypes();
		SettingsHandler.getGame().clearLoadContext();

		RaceType.clearConstants();
		createEmptyRace();
		CNAbilityFactory.reset();
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
		ArrayList<String> aList = new ArrayList<>();
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
	 * Roll the hitpoints for a single level.
	 *
	 * @param min the minimum number on the die
	 * @param max the maximum number on the die
	 * @param name the PC's name (used for a message to the user)
	 * @param level the level the hit points are being rolled for (used for a message to the user)
	 * @param totalLevel the level the hitpoints are being rolled for (used in maths)
	 * @return the hitpoints for the given level.
	 */
	public static int rollHP(
		final int min,
		final int max,
		final String name,
		final int level,
		final int totalLevel)
	{
		int roll;

		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_USER_ROLLED:
				roll = -1;

				break;

			case Constants.HP_AVERAGE:

				roll = max - min;

				// (n+1)/2
				// average roll on a die with an  odd # of sides works out exactly
				// average roll on a die with an even # of sides will have an extra 0.5

				if (((totalLevel & 0x01) == 0) && ((roll & 0x01) != 0))
				{
					++roll;
				}

				roll = min + (roll / 2);

				break;

			case Constants.HP_AUTO_MAX:
				roll = max;

				break;

			case Constants.HP_PERCENTAGE:
				roll = min - 1 + (int) ((SettingsHandler.getHPPercent() * (max - min + 1)) / 100.0);

				break;

			case Constants.HP_AVERAGE_ROUNDED_UP:
				roll = (int)Math.ceil((min + max)/2.0);

				break;

			case Constants.HP_STANDARD:default:
				roll = Math.abs(RandomUtil.getRandomInt(max - min + 1)) + min;

				break;
		}

//		if (SettingsHandler.getShowHPDialogAtLevelUp())
//		{
//			final Object[] rollChoices = new Object[max - min + 2];
//			rollChoices[0] = Constants.NONESELECTED;
//
//			for (int i = min; i <= max; ++i)
//			{
//				rollChoices[i - min + 1] = i;
//			}
//
//			while (min <= max)
//			{
//				//TODO: This must be refactored away. Core shouldn't know about gui.
//				final InputInterface ii = InputFactory.getInputInstance();
//				final Object selectedValue = ii.showInputDialog(Globals.getRootFrame(),
//					"Randomly generate a number between " + min + " and " + max
//						+ "." + Constants.LINE_SEPARATOR
//						+ "Select it from the box below.",
//					SettingsHandler.getGame().getHPText() + " for "
//						+ CoreUtility.ordinal(level) + " level of " + name,
//					MessageType.INFORMATION,
//					rollChoices, roll);
//
//				if ((selectedValue != null) && (selectedValue instanceof Integer))
//				{
//					roll = (Integer) selectedValue;
//
//					break;
//				}
//			}
//		}

		return roll;
	}

	/**
	 * Select the paper
	 * @param paperName
	 * @return TRUE if OK
	 */
	public static boolean selectPaper(final String paperName)
	{
		for (int i = 0; i < SettingsHandler.getGame().getModeContext().getReferenceContext()
				.getConstructedObjectCount(PaperInfo.class); ++i)
		{
			final PaperInfo pi = SettingsHandler.getGame().getModeContext().getReferenceContext()
					.getItemInOrder(PaperInfo.class, i);

			if (pi.getName().equals(paperName))
			{
				setSelectedPaper(i);
				PCGenSettings.getInstance().setProperty(PCGenSettings.PAPERSIZE,
					paperName);

				return true;
			}
		}

		setSelectedPaper(-1);

		return false;
	}

	/**
	 * Apply the user's preferences to the initial state of the Globals.  
	 */
	public static void initPreferences()
	{
		if (selectedPaper != -1)
		{
			// Already initialized
			return;
		}
		String papersize = PCGenSettings.getInstance().initProperty(PCGenSettings.PAPERSIZE,
			"A4");
		selectPaper(papersize);
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
			nonPObjectInList = ! (availableList.get(0) instanceof CDOMObject);
		}
		else if (selectedList.size() > 0)
		{
			nonPObjectInList = ! (selectedList.get(0) instanceof CDOMObject);
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
	public static List<? extends CDOMObject> sortPObjectList(final List<? extends CDOMObject> aList)
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
	
	static String getBonusFeatString() 
	{
		List<String> bonusFeatLevels = SettingsHandler.getGame().getBonusFeatLevels();
		if (bonusFeatLevels == null || bonusFeatLevels.isEmpty())
		{
			// Default to no bonus feats.
			return "9999|0";
		}
		return bonusFeatLevels.get(0);
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
	 * Get a choice from a list
	 * @param title The title of the chooser dialog.
	 * @param choiceList The list of possible choices.
	 * @param selectedList The values already selected (none of which should be in the available list).
	 * @param pool The number of choices the user can make.
	 * @param pc The character the choice is being made for.
	 * @return a choice
	 */
	public static <T> List<T> getChoiceFromList(final String title,
		final List<T> choiceList, final List<T> selectedList, final int pool,
		PlayerCharacter pc)
	{
		return getChoiceFromList(title, choiceList, selectedList, pool, false, false, pc);
	}

	/**
	 * Ask the user for a choice from a list.
	 * @param title The title of the chooser dialog.
	 * @param choiceList The list of possible choices.
	 * @param selectedList The values already selected (none of which should be in the available list).
	 * @param pool The number of choices the user can make.
	 * @param forceChoice true if the user will be forced to make all choices.
	 * @param preferRadioSelection true if this would be better presented as a radio button list
	 * @param pc The character the choice is being made for.
	 * @return The list of choices made by the user.
	 */
	public static <T> List<T> getChoiceFromList(final String title,
		final List<T> choiceList, final List<T> selectedList, final int pool,
		final boolean forceChoice, final boolean preferRadioSelection, PlayerCharacter pc)
	{
		List<T> startingSelectedList = new ArrayList<>();
		if (selectedList != null)
		{
			startingSelectedList = selectedList;
		}

		CDOMChooserFacadeImpl<T> chooserFacade =
                new CDOMChooserFacadeImpl<>(title,
                        choiceList,
                        startingSelectedList, pool);
		chooserFacade.setAllowsDups(false);
		chooserFacade.setRequireCompleteSelection(forceChoice);
		chooserFacade.setInfoFactory(new Gui2InfoFactory(pc));
		chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
		chooserFacade.setPreferRadioSelection(preferRadioSelection);
		ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
		
		return chooserFacade.getFinalSelected();
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
	 * @param baseSize
	 * @param newSize
	 * @return String
	 */
	public static String adjustDamage(String aDamage, SizeAdjustment baseSize, SizeAdjustment newSize)
	{
		if (aDamage.length() == 0)
		{
			return aDamage;
		}
		int baseIndex = baseSize.get(IntegerKey.SIZEORDER);
		int newIndex =  newSize.get(IntegerKey.SIZEORDER);
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

	private static void setSelectedPaper(final int argSelectedPaper)
	{
		Globals.selectedPaper = argSelectedPaper;
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

	public static void createEmptyRace()
	{
		if (s_EMPTYRACE == null)
		{
			s_EMPTYRACE = new Race();
			s_EMPTYRACE.setName(Constants.NONESELECTED);
			s_EMPTYRACE.addToListFor(ListKey.TYPE, Type.HUMANOID);
		}

		getContext().getReferenceContext().importObject(s_EMPTYRACE);
	}

	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@"))
		{
			path = System.getProperty("user.dir") + File.separator + path.substring(1);
		}

		return path;
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
}
