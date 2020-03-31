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
package pcgen.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.content.BaseDice;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.character.EquipSlot;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.facade.core.ChooserFacade;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.VisionType;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 */
public final class Globals
{
	/** These are changed during normal operation */
	private static final List<PlayerCharacter> PC_LIST = new ArrayList<>();

	/** NOTE: The defaultPath is duplicated in LstSystemLoader. */
	private static final String DEFAULT_PCG_PATH = getUserFilesPath() + File.separator + "characters"; //$NON-NLS-1$

	private static final List<String> CUST_COLUMN_WIDTH = new ArrayList<>();
	private static SourceFormat sourceDisplay = SourceFormat.LONG;
	private static int selectedPaper = -1;

	/** we need maps for efficient lookups */
	private static final Map<URI, Campaign> CAMPAIGN_MAP = new HashMap<>();
	private static final Map<String, Integer> EQ_SLOT_MAP = new HashMap<>();

	// end of filter creation sets
	private static JFrame rootFrame;

	/** whether or not the GUI is used (false for command line) */
	private static boolean useGUI = true;

	// Optimizations used by any code needing empty arrays.  All empty arrays
	// of the same type are idempotent.

	private Globals()
	{
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
		final Campaign campaign = CAMPAIGN_MAP.get(aName);

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
		return List.copyOf(CAMPAIGN_MAP.values());
	}

	/**
	 * Get campaign by key
	 * @param aKey
	 * @return Campaign
	 */
	public static Campaign getCampaignKeyed(final String aKey)
	{
		final Campaign campaign = getCampaignKeyedSilently(aKey);
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
		return getCampaignList().stream()
		                    .filter(campaign -> campaign.getKeyName().equalsIgnoreCase(aKey))
		                    .findFirst()
		                    .orElse(null);

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

		final List<String> typeList = new ArrayList<>();
		final StringTokenizer tok = new StringTokenizer(aType, ".");
		while (tok.hasMoreTokens())
		{
			typeList.add(tok.nextToken());
		}

		for (final T anObject : aPObjectList)
		{
			if (isMatch(typeList, anObject))
			{
				ret.add(anObject);
			}
		}
		ret.trimToSize();
		return ret;
	}

	private static <T extends CDOMObject> boolean isMatch(List<String> typeList, T anObject)
	{
		for (final String type : typeList)
		{
			final boolean sense = (type.charAt(0) != '!');
			if (anObject.isType(type) != sense)
			{
				return false;
			}
		}
		return true;
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
	private static String getUserFilesPath()
	{
		return expandRelativePath(System.getProperty("user.home") + File.separator + ".pcgen");
	}

	/**
	 * Returns the name of the Default Spell Book, or null if there is no default spell book.
	 * @return default spellbook
	 */
	public static String getDefaultSpellBook()
	{
		GameMode game = SettingsHandler.getGameAsProperty().get();
		if (game != null)
		{
			return game.getDefaultSpellBook();
		}

		return null;
	}

	/**
	 * Get equipment slot by name
	 * @param aName
	 * @return equipment slot
	 */
	public static EquipSlot getEquipSlotByName(final String aName)
	{
		return SystemCollections.getUnmodifiableEquipSlotList()
		                        .stream()
		                        .filter(es -> es.getSlotName().equals(aName))
		                        .findFirst()
		                        .orElse(null);
	}

	/**
	 * Set equipment slot type count
	 * @param aString
	 * @param aNum
	 */
	public static void setEquipSlotTypeCount(final String aString, final int aNum)
	{
		EQ_SLOT_MAP.put(aString, aNum);
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
		return EQ_SLOT_MAP.computeIfAbsent(aType, s -> 0);
	}

	/**
	 * Get the game mode point pool name
	 * @return game mode point pool name
	 */
	public static String getGameModePointPoolName()
	{
		return SettingsHandler.getGameAsProperty().get().getPointPoolName();
	}

	/**
	 * TRUE if the game mode has a point pool
	 * @return TRUE if the game mode has a point pool
	 */
	public static boolean getGameModeHasPointPool()
	{
		return !getGameModePointPoolName().isEmpty();
	}

	/**
	 * Get game mode unit set
	 * @return game mode unit set
	 */
	public static UnitSet getGameModeUnitSet()
	{
		return SettingsHandler.getGameAsProperty().get().getUnitSet();
	}

	/**
	 * Get PC List
	 * @return List of Pcs
	 */
	public static List<PlayerCharacter> getPCList()
	{
		return PC_LIST;
	}

	/**
	 * Get paper count
	 * @return paper count
	 */
	public static int getPaperCount()
	{
		return SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
			.getConstructedObjectCount(PaperInfo.class);
	}

	/**
	 * Get paper info
	 * @param infoType
	 * @return paper info
	 */
	public static String getPaperInfo(final int infoType)
	{
		return getPaperInfo(selectedPaper, infoType);
	}

	/**
	 * Get paper info
	 * @param idx
	 * @param infoType
	 * @return paper info
	 */
	public static String getPaperInfo(final int idx, final int infoType)
	{
		if ((idx < 0) || (idx >= getPaperCount()))
		{
			return null;
		}

		return getSortedPaperInfo().get(idx).getPaperInfo(infoType);
	}

	private static List<PaperInfo> getSortedPaperInfo()
	{
		List<PaperInfo> items = new ArrayList<>(SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
			.getConstructedCDOMObjects(PaperInfo.class));
		items.sort(Comparator.comparing(SortKeyRequired::getSortKey));
		return items;
	}

	/**
	 * Sets the root frame
	 * The root frame is the container in which all
	 * other panels, frame etc are placed.
	 *
	 * @param frame the {@code PCGen_Frame1} which is to be root
	 */
	public static void setRootFrame(final JFrame frame)
	{
		rootFrame = frame;
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return the {@code rootFrame} property
	 */
	public static JFrame getRootFrame()
	{
		return rootFrame;
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
	static boolean getUseGUI()
	{
		return useGUI;
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
		CAMPAIGN_MAP.put(campaign.getSourceURI(), campaign);
	}

	/**
	 * Adjust damage
	 * @param aDamage
	 * @return adjusted damage
	 */
	public static String adjustDamage(String aDamage, int sizeDiff)
	{
		if (aDamage.isEmpty())
		{
			return aDamage;
		}
		final AbstractReferenceContext ref = getContext().getReferenceContext();
		BaseDice bd = ref.silentlyGetConstructedCDOMObject(BaseDice.class, aDamage);
		int multiplier = 0;
		if (bd == null)
		{
			//Need to test for higher dice
			final RollInfo aRollInfo = new RollInfo(aDamage);
			final String baseDice = "1d" + Integer.toString(aRollInfo.sides);
			bd = ref.silentlyGetConstructedCDOMObject(BaseDice.class, baseDice);
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
			List<RollInfo> steps;
			if (sizeDiff > 0)
			{
				steps = bd.getUpSteps();
			}
			else if (sizeDiff < 0)
			{
				steps = bd.getDownSteps();
			}
			else
			{
				// Not a warning?
				return aDamage;
			}
			final int difference = Math.abs(sizeDiff);

			final int index;
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
	 * @param typeList
	 * @return TRUE or FALSE
	 */
	public static boolean canResizeHaveEffect(List<String> typeList)
	{
		final List<String> resizeTypeList = SettingsHandler.getGameAsProperty().get().getResizableTypeList().stream()
			.map(Type::toString).map(String::toUpperCase).collect(Collectors.toList());
		return typeList.stream().map(String::toUpperCase).anyMatch(resizeTypeList::contains);
	}

	/**
	 * Find out the state of a PRERULE check
	 * @param aKey
	 * @return true or false
	 */
	public static boolean checkRule(final String aKey)
	{
		final RuleCheck rule = SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
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
		CAMPAIGN_MAP.clear();
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
		final boolean listsHappy = checkListsHappy();

		final Level logLevel = listsHappy ? Logging.DEBUG : Logging.WARNING;
		if (Logging.isLoggable(logLevel))
		{
			Logging.log(logLevel, "Number of objects loaded. The following should " + "all be greater than 0:");
			AbstractReferenceContext referenceContext = getContext().getReferenceContext();
			Logging.log(logLevel, "Races=" + referenceContext.getConstructedCDOMObjects(Race.class).size());
			Logging.log(logLevel, "Classes=" + referenceContext.getConstructedCDOMObjects(PCClass.class).size());
			Logging.log(logLevel, "Skills=" + referenceContext.getConstructedCDOMObjects(Skill.class).size());
			AbilityCategory featCategory = referenceContext.get(AbilityCategory.class, "FEAT");
			Logging.log(logLevel,
				"Feats=" + referenceContext.getManufacturerId(featCategory).getConstructedObjectCount());
			Logging.log(logLevel, "Equipment=" + referenceContext.getConstructedCDOMObjects(Equipment.class).size());
			Logging.log(logLevel, "ArmorProfs=" + referenceContext.getConstructedCDOMObjects(ArmorProf.class).size());
			Logging.log(logLevel, "ShieldProfs=" + referenceContext.getConstructedCDOMObjects(ShieldProf.class).size());
			Logging.log(logLevel, "WeaponProfs=" + referenceContext.getConstructedCDOMObjects(WeaponProf.class).size());
			Logging.log(logLevel, "Kits=" + referenceContext.getConstructedCDOMObjects(Kit.class).size());
			Logging.log(logLevel, "Templates=" + referenceContext.getConstructedCDOMObjects(PCTemplate.class).size());
		}
		return listsHappy;
	}

	/**
	 * Check if enough data has been loaded to support character creation.
	 * @return true or false
	 */
	private static boolean checkListsHappy()
	{
		// NOTE: If you add something here be sure to update the log output in displayListsHappy above
		return !((getContext().getReferenceContext().getConstructedCDOMObjects(Race.class).isEmpty())
			|| (getContext().getReferenceContext().getConstructedCDOMObjects(PCClass.class).isEmpty())
			//				|| (getContext().ref.getConstructedCDOMObjects(Skill.class).size() == 0)
			//				|| (getContext().ref.getManufacturer(
			//						Ability.class, AbilityCategory.FEAT).getConstructedObjectCount() == 0)
			|| (getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class).isEmpty())
			|| (getContext().getReferenceContext().getConstructedCDOMObjects(WeaponProf.class).isEmpty()));
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
		VisionType.clearConstants();
		FactKey.clearConstants();
		FactSetKey.clearConstants();

		// Perform other special cleanup
		Equipment.clearEquipmentTypes();
		SettingsHandler.getGameAsProperty().get().clearLoadContext();

		RaceType.clearConstants();
		CNAbilityFactory.reset();
		MovementType.clearConstants();
	}

	/**
	 * Execute post export commands for standard files
	 * @param fileName
	 */
	public static void executePostExportCommandStandard(final String fileName)
	{
		final String postExportCommand = SettingsHandler.getPostExportCommandStandard();
		executePostExportCommand(fileName, postExportCommand);
	}

	/**
	 * Execute post export commands for PDF
	 * @param fileName
	 */
	public static void executePostExportCommandPDF(final String fileName)
	{
		final String postExportCommand = SettingsHandler.getPostExportCommandPDF();
		executePostExportCommand(fileName, postExportCommand);
	}

	/**
	 * Execute any post export commands
	 * @param fileName
	 * @param postExportCommand
	 */
	private static void executePostExportCommand(final String fileName, final String postExportCommand)
	{
		final List<String> aList = new ArrayList<>();
		final StringTokenizer aTok = new StringTokenizer(postExportCommand, " ");
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		final String[] cmdArray = new String[aList.size()];
		for (int idx = 0; idx < aList.size(); idx++)
		{
			final String s = aList.get(idx);
			if (s.contains("%"))
			{
				final String beforeString = s.substring(0, s.indexOf('%'));
				final String afterString = s.substring(s.indexOf('%') + 1);
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
			catch (final IOException ex)
			{
				Logging.errorPrint("Could not run " + postExportCommand + " after exporting " + fileName, ex);
			}
		}
	}

	/**
	 * Select the paper
	 * @param paperName
	 * @return TRUE if OK
	 */
	public static boolean selectPaper(final String paperName)
	{
		List<PaperInfo> paperInfoObjects = getSortedPaperInfo();
		for (int i = 0; i < paperInfoObjects.size(); i++)
		{
			final PaperInfo pi = paperInfoObjects.get(i);

			if (pi.getName().equals(paperName))
			{
				selectedPaper = i;
				PCGenSettings.getInstance().setProperty(PCGenSettings.PAPERSIZE, paperName);

				return true;
			}
		}

		selectedPaper = -1;

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
		final String papersize = PCGenSettings.getInstance().initProperty(PCGenSettings.PAPERSIZE, "A4");
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

		if (!availableList.isEmpty())
		{
			nonPObjectInList = !(availableList.get(0) instanceof CDOMObject);
		}
		else if (!selectedList.isEmpty())
		{
			nonPObjectInList = !(selectedList.get(0) instanceof CDOMObject);
		}
		else
		{
			nonPObjectInList = false;
		}

		if (nonPObjectInList)
		{
			Collections.sort(availableList);
			// NOCHOICE feats add nulls to the selectedList
			if ((!selectedList.isEmpty()) && (selectedList.get(0) != null))
			{
				Collections.sort(selectedList);
			}
		}
		else
		{
			sortPObjectListByName(availableList);
			sortPObjectListByName(selectedList);
		}
	}

	/**
	 * Sort Pcgen Object list
	 * @param aList
	 * @return Sorted list of Pcgen Objects
	 */
	static List<? extends CDOMObject> sortPObjectList(final List<? extends CDOMObject> aList)
	{
		aList.sort(CDOMObject.P_OBJECT_COMP);

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
		aList.sort(CDOMObject.P_OBJECT_NAME_COMP);

		return aList;
	}

	static String getBonusFeatString()
	{
		final List<String> bonusFeatLevels = SettingsHandler.getGameAsProperty().get().getBonusFeatLevels();
		if ((bonusFeatLevels == null) || bonusFeatLevels.isEmpty())
		{
			// Default to no bonus feats.
			return "9999|0";
		}
		return bonusFeatLevels.get(0);
	}

	static int getBonusStatsForLevel(final int level, final PlayerCharacter aPC)
	{
		int num = 0;

		for (final String s : SettingsHandler.getGameAsProperty().get().getBonusStatLevels())
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
	public static <T> List<T> getChoiceFromList(final String title, final List<T> choiceList,
		final List<T> selectedList, final int pool, final PlayerCharacter pc)
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
	public static <T> List<T> getChoiceFromList(final String title, final List<T> choiceList,
		final List<T> selectedList, final int pool, final boolean forceChoice, final boolean preferRadioSelection,
		final PlayerCharacter pc)
	{
		List<T> startingSelectedList = new ArrayList<>();
		if (selectedList != null)
		{
			startingSelectedList = selectedList;
		}

		final CDOMChooserFacadeImpl<T> chooserFacade =
				new CDOMChooserFacadeImpl<>(title, choiceList, startingSelectedList, pool);
		chooserFacade.setAllowsDups(false);
		chooserFacade.setRequireCompleteSelection(forceChoice);
		chooserFacade.setInfoFactory(new Gui2InfoFactory(pc));
		chooserFacade.setDefaultView(ChooserFacade.ChooserTreeViewType.NAME);
		chooserFacade.setPreferRadioSelection(preferRadioSelection);
		ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);

		return chooserFacade.getFinalSelected();
	}

	static List<String> getCustColumnWidth()
	{
		return CUST_COLUMN_WIDTH;
	}

	static String getDefaultPcgPath()
	{
		return expandRelativePath(DEFAULT_PCG_PATH);
	}

	/**
	 * returns the location of the "filepaths.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 * @return option path
	 */
	static String getFilepathsPath()
	{

		// first see if it was specified on the command line
		String aPath = System.getProperty("pcgen.filepaths"); //$NON-NLS-1$

		if (aPath == null)
		{
			aPath = System.getProperty("user.dir") + File.separator + "filepaths.ini"; //$NON-NLS-1$ //$NON-NLS-2$;
		}
		else
		{
			File testPath = new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "filepaths.ini"; //$NON-NLS-1$
				testPath = new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging.errorPrint("WARNING: The filepaths file you specified is not updatable. "
					+ "Filepath changes will not be saved. File is " + testPath.getAbsolutePath());
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

		// first see if it was specified on the command line
		String aPath = System.getProperty("pcgen.filter");

		if (aPath == null)
		{
			aPath = getFilePath("filter.ini");
		}
		else
		{
			File testPath = new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "filter.ini";
				testPath = new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging.errorPrint("WARNING: The filter file you specified is not updatable. "
					+ "Filter changes will not be saved. File is " + testPath.getAbsolutePath());
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

		// first see if it was specified on the command line
		String aPath = System.getProperty("pcgen.options");

		if (aPath == null)
		{
			aPath = getFilePath("options.ini");
		}
		else
		{
			File testPath = new File(expandRelativePath(aPath));
			if (testPath.exists() && testPath.isDirectory())
			{
				aPath = testPath.getAbsolutePath() + File.separator + "options.ini";
				testPath = new File(aPath);
			}
			if (testPath.exists() && !testPath.canWrite())
			{
				Logging.errorPrint("WARNING: The options file you specified is not updatable. "
					+ "Settings changes will not be saved. File is " + testPath.getAbsolutePath());
			}
		}

		return expandRelativePath(aPath);
	}

	public static int getSkillMultiplierForLevel(final int level)
	{
		final List<String> sml = SettingsHandler.getGameAsProperty().get().getSkillMultiplierLevels();

		if ((level > sml.size()) || (level <= 0))
		{
			return 1;
		}

		return Integer.parseInt(sml.get(level - 1));
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
			num += numChoices;
		}

		return num;
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
		return SettingsHandler.getGameAsProperty().get().getContext();
	}
}
