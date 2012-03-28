/*
 * SettingsHandler.java
 * Copyright 2001 (C) Jonas Karlsson
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
 * Created on July 10, 2002, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.core.utils.SortedProperties;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * This class contains all settings-related code moved from Globals.java
 *
 * Should be cleaned up more.
 * 
 * <b>NB: This class is being gradually replaced with use of 
 * {@link pcgen.system.PropertyContext} and its children.</b>   
 *
 * @author jujutsunerd
 * @version $Revision$
 **/
public final class SettingsHandler
{
	private static boolean abilitiesShownAsTab = false;
	private static boolean autoFeatsRefundable = false;
	private static boolean useFeatBenefits = true;
	private static boolean autogenExoticMaterial = false;
	private static boolean autogenMagic = false;
	private static boolean autogenMasterwork = false;
	private static boolean autogenRacial = false;
	private static boolean validateBonuses = false;

	//
	// For EqBuilder
	//
	private static int maxPotionSpellLevel = Constants.DEFAULT_MAX_POTION_SPELL_LEVEL;
	private static int maxWandSpellLevel   = Constants.DEFAULT_MAX_WAND_SPELL_LEVEL;
	private static boolean allowMetamagicInCustomizer = false;
	private static boolean spellMarketPriceAdjusted = false;

	// Map of RuleCheck keys and their settings
	private static Map<String, String> ruleCheckMap = new HashMap<String, String>();

	/** That browserPath is set to null is intentional. */
	private static String browserPath = null; //Intentional null

	/**
	 *  See @javax.swing.SwingConstants.
	 */
	private static int chaTabPlacement = SwingConstants.TOP;
	private static Dimension customizerDimension = null;
	private static Point customizerLeftUpperCorner = null;
	private static int customizerSplit1 = -1;
	private static int customizerSplit2 = -1;
	private static String dmNotes = ""; //$NON-NLS-1$
	private static boolean enforceSpendingBeforeLevelUp = false;
	private static int featAutoColor            = Constants.DEFAULT_FEAT_AUTO_COLOUR;
	private static int featVirtualColor         = Constants.DEFAULT_FEAT_VIRTUAL_COLOUR;
	private static int sourceStatusReleaseColor = Constants.DEFAULT_SOURCE_STATUS_RELEASE_COLOUR;
	private static int sourceStatusAlphaColor   = Constants.DEFAULT_SOURCE_STATUS_ALPHA_COLOUR;
	private static int sourceStatusBetaColor    = Constants.DEFAULT_SOURCE_STATUS_BETA_COLOUR;
	private static int sourceStatusTestColor    = Constants.DEFAULT_SOURCE_STATUS_TEST_COLOUR;
	private static final Properties FILTERSETTINGS = new Properties();
	private static GameMode game = new GameMode("default");
	private static boolean grimHPMode = false;
	private static boolean grittyACMode = false;
	private static Dimension kitSelectorDimension = null;
	private static Point kitSelectorLeftUpperCorner = null;
	private static boolean useWaitCursor = true;
	private static boolean showD20InfoAtStart = true;
	private static boolean loadURLs = false;
	private static boolean showOGLOnLoad = true;
	private static boolean showMatureOnLoad = true;
	private static boolean showSponsorsOnLoad = true;
	private static boolean hpMaxAtFirstLevel = true;
	private static boolean hpMaxAtFirstClassLevel = true;
	private static boolean hpMaxAtFirstPCClassLevelOnly = true;
	private static int hpRollMethod = Constants.HP_STANDARD;
	private static int hpPercent    = Constants.DEFAULT_HP_PERCENT;
	private static boolean ignoreMonsterHDCap = false;
	private static boolean debugFeats = false;

	public static final int INCLUDE_SKILLS_NONE = 0;
	public static final int INCLUDE_SKILLS_UNTRAINED = 1;
	public static final int INCLUDE_SKILLS_ALL = 2;
	public static final int INCLUDE_SKILLS_SKILLS_TAB = 3;

	private static int includeSkills = INCLUDE_SKILLS_SKILLS_TAB;
	private static int skillsTab_IncludeSkills = 1;
//	private static int intCrossClassSkillCost = 2;
	private static String invalidDmgText;
	private static String invalidToHitText;
	private static boolean gearTab_IgnoreCost = false;
	private static boolean gearTab_AutoResize = false;
	private static boolean gearTab_AllowDebt  = false;
	private static int gearTab_SellRate = Constants.DEFAULT_GEAR_TAB_SELL_RATE;
	private static int gearTab_BuyRate  = Constants.DEFAULT_GEAR_TAB_BUY_RATE;
	private static boolean isROG = false;
	private static Point leftUpperCorner = null;
	private static int windowState = Frame.NORMAL;
	private static boolean loadCampaignsAtStart = false;
	private static boolean loadCampaignsWithPC = false;
	private static int looknFeel = 1; // default to Java L&F
	private static boolean expertGUI = false; // default to System L&F
	private static boolean optionAllowedInSources = true;
	private static final SortedProperties options = new SortedProperties();
	private static final Properties filepaths = new Properties();
	private static final String fileLocation = Globals.getFilepathsPath();
	private static File pcgenFilesDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
	private static File pccFilesLocation = null;
	private static File pcgPath = new File(Globals.getDefaultPath());
	private static File lastUsedPcgPath = null; // NB: This is not saved to preferences 
	private static File backupPcgPath = null;
	private static boolean createPcgBackup = true;
	private static File portraitsPath = new File(Globals.getDefaultPath());
	private static File pcgenCustomDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "data" //$NON-NLS-1$
			+ File.separator + "customsources"); //$NON-NLS-1$
	private static File pcgenVendorDataDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "vendordata"); //$NON-NLS-1$
	private static File pcgenSponsorDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "system" //$NON-NLS-1$
			+ File.separator + "sponsors"); //$NON-NLS-1$
	private static File pcgenDocsDir = null;

	/**
	 * Where to load the system lst files from.
	 */
	private static File pcgenSystemDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "system"); //$NON-NLS-1$
	private static File pcgenThemePackDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "lib" //$NON-NLS-1$
			+ File.separator + "lnf" //$NON-NLS-1$
			+ File.separator + "themes"); //$NON-NLS-1$
	private static File pcgenOutputSheetDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "outputsheets"); //$NON-NLS-1$
	private static File gmgenPluginDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "plugins"); //$NON-NLS-1$
	private static int prereqQualifyColor = Constants.DEFAULT_PREREQ_QUALIFY_COLOUR;
	private static int prereqFailColor    = Constants.DEFAULT_PREREQ_FAIL_COLOUR;
	private static boolean previewTabShown = false;
	private static File pcgenPreviewDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "preview");//$NON-NLS-1$

/////////////////////////////////////////////////
	private static boolean ranStartingWizard = false;

	private static boolean saveCustomInLst = false;
	private static String selectedCharacterHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedCharacterPDFOutputSheet = ""; //$NON-NLS-1$
	private static boolean saveOutputSheetWithPC = false;
	private static boolean printSpellsWithPC = true;
	private static String selectedPartyHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedPartyPDFOutputSheet = ""; //$NON-NLS-1$
	private static String selectedEqSetTemplate = ""; //$NON-NLS-1$
	private static String selectedSpellSheet = ""; //$NON-NLS-1$
	private static boolean showFeatDialogAtLevelUp = true;
	private static boolean showHPDialogAtLevelUp = true;
	private static boolean showStatDialogAtLevelUp = true;
	private static boolean showToolBar = true;
	private static boolean showSkillModifier = false;
	private static boolean showSkillRanks = false;
	private static boolean showWarningAtFirstLevelUp = true;
	private static String skinLFThemePack = null;
	private static boolean summaryTabShown = false;
	private static int classTab_AvailableListMode = GuiConstants.INFOCLASS_VIEW_NAME;
	private static int classTab_SelectedListMode = GuiConstants.INFOCLASS_VIEW_NAME;
	private static int domainTab_ListMode = GuiConstants.INFODOMAIN_VIEW_NAME;
	private static int equipTab_AvailableListMode = GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED;
	private static int equipTab_SelectedListMode = GuiConstants.INFOEQUIPPING_VIEW_NAME;
	private static int featTab_AvailableListMode = GuiConstants.INFOFEATS_VIEW_PREREQTREE;
	private static int featTab_SelectedListMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;
	private static int gearTab_AvailableListMode = GuiConstants.INFOINVENTORY_VIEW_TYPE_NAME;
	private static int gearTab_SelectedListMode = GuiConstants.INFOINVENTORY_VIEW_NAME;
	private static int raceTab_ListMode = GuiConstants.INFORACE_VIEW_NAME;
	private static int templateTab_ListMode = GuiConstants.INFOTEMPLATE_VIEW_NAME;
	private static int templateSelTab_ListMode = GuiConstants.INFOTEMPLATE_VIEW_NAME;
	private static int skillsTab_AvailableListMode = GuiConstants.INFOSKILLS_VIEW_TYPE_NAME;
	private static int skillsTab_SelectedListMode = GuiConstants.INFOSKILLS_VIEW_NAME;
	private static int spellsTab_AvailableListMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	private static int spellsTab_SelectedListMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	private static boolean cleanupTempFiles = false;
	private static boolean alwaysOverwrite = false;
	private static boolean allowOverride = false;
	private static String defaultOSType = ""; //$NON-NLS-1$

	/**
	 *  See @javax.swing.SwingConstants
	 */
	private static int tabPlacement = SwingConstants.BOTTOM;
	private static final String tmpPath = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
	private static final File tempPath = new File(getTmpPath());
	private static boolean toolTipTextShown = true;
	private static boolean useHigherLevelSlotsDefault = false;
	private static boolean wantToLoadMasterworkAndMagic = false;
	private static int nameDisplayStyle = Constants.DISPLAY_STYLE_NAME;
	private static boolean weaponProfPrintout = Constants.DEFAULT_PRINTOUT_WEAPONPROF;
	private static String postExportCommandStandard = ""; //$NON-NLS-1$
	private static String postExportCommandPDF = ""; //$NON-NLS-1$
	private static boolean hideMonsterClasses = false;
	private static boolean guiUsesOutputNameEquipment = false;
	private static boolean guiUsesOutputNameSpells = false;
	private static int singleChoicePreference = Constants.CHOOSER_SINGLE_CHOICE_METHOD_NONE;
	private static int lastTipShown = -1;
	private static boolean showMemoryArea = false;
	private static boolean showImagePreview = true;
	private static boolean showTipOfTheDay = true;
	private static boolean isGMGen = false;
	private static boolean showSingleBoxPerBundle = false;
	private static boolean useAdvancedSourceSelect = false;
	
	//
	// Hide this tab from general consumption, until I get it working.
	// Then this routine can be removed and the tab will show always
	//
	private static boolean showNatWeaponTab = false;

	private static String hiddenSources = ""; //$NON-NLS-1$
	private static String quickLaunchSources = ""; //$NON-NLS-1$

	public static String getSelectedGenerators(String string)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public static void setSelectedGenerators(String prop, String generators)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public static void setAbilitiesShownAsATab(final boolean showAbilitiesAsTab)
	{
		abilitiesShownAsTab = showAbilitiesAsTab;
	}

	public static boolean isAbilitiesShownAsATab()
	{
		return abilitiesShownAsTab;
	}

	public static void setAllowOverride(final boolean aBool)
	{
		allowOverride = aBool;
	}

	public static boolean isAllowOverride()
	{
		return allowOverride;
	}

	public static void setAlwaysOverwrite(final boolean argAlwaysOverwrite)
	{
		alwaysOverwrite = argAlwaysOverwrite;
	}

	public static boolean getAlwaysOverwrite()
	{
		return alwaysOverwrite;
	}

	public static void setDefaultOSType(final String argDefaultOSType)
	{
		defaultOSType = argDefaultOSType;
	}

	public static String getDefaultOSType()
	{
		return defaultOSType;
	}

	public static void setAutogen(final int idx, final boolean bFlag)
	{
		switch (idx)
		{
			case Constants.AUTOGEN_RACIAL:
				setAutogenRacial(bFlag);

				break;

			case Constants.AUTOGEN_MASTERWORK:
				setAutogenMasterwork(bFlag);

				break;

			case Constants.AUTOGEN_MAGIC:
				setAutogenMagic(bFlag);

				break;

			case Constants.AUTOGEN_EXOTIC_MATERIAL:
				setAutogenExoticMaterial(bFlag);

				break;

			default:
				break;
		}
	}

	public static boolean getAutogen(final int idx)
	{
		if (!wantToLoadMasterworkAndMagic())
		{
			switch (idx)
			{
				case Constants.AUTOGEN_RACIAL:
					return isAutogenRacial();

				case Constants.AUTOGEN_MASTERWORK:
					return isAutogenMasterwork();

				case Constants.AUTOGEN_MAGIC:
					return isAutogenMagic();

				case Constants.AUTOGEN_EXOTIC_MATERIAL:
					return isAutogenExoticMaterial();

				default:
					break;
			}
		}

		return false;
	}

	/**
	 * Sets the path to the backup directory for character files.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setBackupPcgPath(final File path)
	{
		backupPcgPath = path;
	}

	/**
	 * Returns the path to the backup directory for character files.
	 *
	 * @return    the <code>backupPcgPath</code> property
	 */
	public static File getBackupPcgPath()
	{
		return backupPcgPath;
	}

	/**
	 * Sets the external browser path to use.
	 *
	 * @param  path  the <code>String</code> representing the path
	 **/
	public static void setBrowserPath(final String path)
	{
		browserPath = path;
	}

	/**
	 * Returns the external browser path to use.
	 *
	 * @return    the <code>browserPath</code> property
	 */
	public static String getBrowserPath()
	{
		return browserPath;
	}

	public static void setChaTabPlacement(final int argChaTabPlacement)
	{
		chaTabPlacement = argChaTabPlacement;
	}

	public static int getChaTabPlacement()
	{
		return chaTabPlacement;
	}

	public static void setClassTab_AvailableListMode(final int listMode)
	{
		classTab_AvailableListMode = listMode;
	}

	public static int getClassTab_AvailableListMode()
	{
		return classTab_AvailableListMode;
	}

	public static void setClassTab_SelectedListMode(final int listMode)
	{
		classTab_SelectedListMode = listMode;
	}

	public static int getClassTab_SelectedListMode()
	{
		return classTab_SelectedListMode;
	}

	/**
	 * @Deprecated Use UIPropertyContext.CLEANUP_TEMP_FILES instead.
	 */
	public static void setCleanupTempFiles(final boolean argDoCleanup)
	{
		cleanupTempFiles = argDoCleanup;
	}

	/**
	 * @Deprecated Use UIPropertyContext.CLEANUP_TEMP_FILES instead.
	 */
	public static boolean getCleanupTempFiles()
	{
		return cleanupTempFiles;
	}

	/**
	 * Sets the flag to determine whether PCGen should backup pcg files before saving
	 *
	 * @param  argCreatePcgBackup  the <code>flag</code>
	 */
	public static void setCreatePcgBackup(final boolean argCreatePcgBackup)
	{
		createPcgBackup = argCreatePcgBackup;
	}

	/**
	 * Returns the flag to determine whether PCGen should backup pcg files before saving
	 *
	 * @return    the <code>createPcgBackup</code> property
	 */
	public static boolean getCreatePcgBackup()
	{
		return createPcgBackup;
	}

	public static void setCustomizerDimension(final Dimension d)
	{
		customizerDimension = d;
	}

	public static Dimension getCustomizerDimension()
	{
		return customizerDimension;
	}

	public static void setCustomizerLeftUpperCorner(final Point argLeftUpperCorner)
	{
		customizerLeftUpperCorner = argLeftUpperCorner;
	}

	public static Point getCustomizerLeftUpperCorner()
	{
		return customizerLeftUpperCorner;
	}

	public static void setCustomizerSplit1(final int split)
	{
		customizerSplit1 = split;
	}

	public static int getCustomizerSplit1()
	{
		return customizerSplit1;
	}

	public static void setCustomizerSplit2(final int split)
	{
		customizerSplit2 = split;
	}

	public static int getCustomizerSplit2()
	{
		return customizerSplit2;
	}

	public static void setDmNotes(final String argDmNotes)
	{
		dmNotes = argDmNotes;
	}

	public static String getDmNotes()
	{
		return dmNotes;
	}

	public static void setDomainTab_ListMode(final int listMode)
	{
		domainTab_ListMode = listMode;
	}

	public static int getDomainTab_ListMode()
	{
		return domainTab_ListMode;
	}

	/**
	 * Sets whether PCgen will enforce the spending of all unallocated feats and skill points
	 * before allowing the character to level up.
	 * @param argEnforceSpendingBeforeLevelUp Should spending be enforced?
	 */
	public static void setEnforceSpendingBeforeLevelUp(final boolean argEnforceSpendingBeforeLevelUp)
	{
		enforceSpendingBeforeLevelUp = argEnforceSpendingBeforeLevelUp;
	}

	public static boolean getEnforceSpendingBeforeLevelUp()
	{
		return enforceSpendingBeforeLevelUp;
	}

	public static void setEquipTab_AvailableListMode(final int listMode)
	{
		equipTab_AvailableListMode = listMode;
	}

	public static int getEquipTab_AvailableListMode()
	{
		return equipTab_AvailableListMode;
	}

	public static void setEquipTab_SelectedListMode(final int listMode)
	{
		equipTab_SelectedListMode = listMode;
	}

	public static int getEquipTab_SelectedListMode()
	{
		return equipTab_SelectedListMode;
	}

//	public static void setExcSkillCost(final int argExcSkillCost)
//	{
//		excSkillCost = argExcSkillCost;
//	}
//
//	public static int getExcSkillCost()
//	{
//		return excSkillCost;
//	}

	public static void setExpertGUI(final boolean argExpertGUI)
	{
		SettingsHandler.expertGUI = argExpertGUI;
	}

	public static boolean isExpertGUI()
	{
		return expertGUI;
	}

	public static void setFeatAutoColor(final int newColor)
	{
		featAutoColor = newColor & 0x00FFFFFF;
	}

	public static int getFeatAutoColor()
	{
		return featAutoColor;
	}

	public static void setFeatTab_AvailableListMode(final int listMode)
	{
		featTab_AvailableListMode = listMode;
	}

	public static int getFeatTab_AvailableListMode()
	{
		return featTab_AvailableListMode;
	}

	public static void setFeatTab_SelectedListMode(final int listMode)
	{
		featTab_SelectedListMode = listMode;
	}

	public static int getFeatTab_SelectedListMode()
	{
		return featTab_SelectedListMode;
	}

	public static void setFeatVirtualColor(final int newColor)
	{
		featVirtualColor = newColor & 0x00FFFFFF;
	}

	public static int getFeatVirtualColor()
	{
		return featVirtualColor;
	}

	public static void setSourceStatusReleaseColor(final int newColor)
	{
		sourceStatusReleaseColor = newColor & 0x00FFFFFF;
	}

	public static int getSourceStatusReleaseColor()
	{
		return sourceStatusReleaseColor;
	}

	public static void setSourceStatusAlphaColor(final int newColor)
	{
		sourceStatusAlphaColor = newColor & 0x00FFFFFF;
	}

	public static int getSourceStatusAlphaColor()
	{
		return sourceStatusAlphaColor;
	}

	public static void setSourceStatusBetaColor(final int newColor)
	{
		sourceStatusBetaColor = newColor & 0x00FFFFFF;
	}

	public static int getSourceStatusBetaColor()
	{
		return sourceStatusBetaColor;
	}

	public static void setSourceStatusTestColor(final int newColor)
	{
		sourceStatusTestColor = newColor & 0x00FFFFFF;
	}

	public static int getSourceStatusTestColor()
	{
		return sourceStatusTestColor;
	}

	public static void setFilePaths(final String aString)
	{
		getFilepathProp().setProperty("pcgen.filepaths", aString); //$NON-NLS-1$
	}

	public static String getFilePaths()
	{
		String def_type = "user";
		if (Globals.isMacPlatform)
		{
			def_type = "mac_user";
		}
		return getFilepathProp().getProperty("pcgen.filepaths", def_type); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static Properties getFilepathProp()
	{
		return filepaths;
	}

	public static boolean getFirstRun()
	{
		// if filepaths.ini doesn't exist that means this is
		// the first time PCGen has been run
		final File aFile = new File(fileLocation);

		return !aFile.exists();

		}

	public static boolean isGMGen()
	{
		return isGMGen;
	}

	/**
	 * Puts all properties into the <code>Properties</code> object,
	 * (<code>options</code>). This is called by
	 * <code>writeOptionsProperties</code>, which then saves the
	 * <code>options</code> into a file.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 * @param optionName
	 * @param optionValue
	 */
	public static void setGMGenOption(final String optionName, final boolean optionValue)
	{
		setGMGenOption(optionName, optionValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setGMGenOption(final String optionName, final int optionValue)
	{
		setGMGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setGMGenOption(final String optionName, final double optionValue)
	{
		setGMGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setGMGenOption(final String optionName, final String optionValue)
	{
		getOptions().setProperty("gmgen.options." + optionName, optionValue); //$NON-NLS-1$
	}

	/**
	 * Set most of this objects static properties from the loaded <code>options</code>.
	 * Called by readOptionsProperties. Most of the static properties are
	 * set as a side effect, with the main screen size being returned.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 * @param optionName
	 * @param defaultValue
	 *
	 * @return the default <code>Dimension</code> to set the screen size to
	 */
	public static boolean getGMGenOption(final String optionName, final boolean defaultValue)
	{
		final String option = getGMGenOption(optionName, defaultValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	public static int getGMGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getGMGenOption(optionName, String.valueOf(defaultValue))).intValue();
	}

	public static Double getGMGenOption(final String optionName, final double defaultValue)
	{
		return new Double(getGMGenOption(optionName, Double.toString(defaultValue)));
	}

	public static String getGMGenOption(final String optionName, final String defaultValue)
	{
		return getOptions().getProperty("gmgen.options." + optionName, defaultValue); //$NON-NLS-1$
	}

	public static void setGUIUsesOutputNameEquipment(final boolean argUseOutputNameEquipment)
	{
		guiUsesOutputNameEquipment = argUseOutputNameEquipment;
	}

	public static void setGUIUsesOutputNameSpells(final boolean argUseOutputNameSpells)
	{
		guiUsesOutputNameSpells = argUseOutputNameSpells;
	}

	public static void setGame(final String g)
	{
		final GameMode newMode = SystemCollections.getGameModeNamed(g);

		if (newMode != null)
		{
			game = newMode;
		}
		String key = g;
		// new key for game mode specific options are pcgen.options.gameMode.X.optionName
		// but offer downward compatible support to read in old version for unitSet from 5.8.0
		String unitSetName = getOptions().getProperty("pcgen.options.gameMode." + key + ".unitSetName",
				getOptions().getProperty("pcgen.options.unitSetName." + key, game.getDefaultUnitSet()));
		if  (!game.selectUnitSet(unitSetName))
		{
			if  (!game.selectDefaultUnitSet())
			{
				game.selectUnitSet(Constants.STANDARD_UNITSET_NAME);
			}
		}
		game.setRollMethodExpressionByName(getPCGenOption("gameMode." + key + ".rollMethodExpression", ""));
		game.setPurchaseMethodName(getPCGenOption("gameMode." + key + ".purchaseMethodName", "")); //$NON-NLS-1$ //$NON-NLS-2$
		game.setAllStatsValue(getPCGenOption("gameMode." + key + ".allStatsValue", 10));
		game.setRollMethod(getPCGenOption("gameMode." + key + ".rollMethod", 0)); //$NON-NLS-1$

		game.setDefaultXPTableName(getPCGenOption("gameMode." + key + ".xpTableName", "")); //$NON-NLS-1$ //$NON-NLS-2$
		game.setDefaultCharacterType(getPCGenOption("gameMode." + key + ".characterType", "")); //$NON-NLS-1$ //$NON-NLS-2$
		
		getChosenCampaignFiles(game);
	}

	public static GameMode getGame()
	{
		return game;
	}

	public static void setGearTab_AllowDebt(final boolean allowDebt)
	{
		gearTab_AllowDebt = allowDebt;
	}

	public static boolean getGearTab_AllowDebt()
	{
		return gearTab_AllowDebt;
	}

	public static void setGearTab_AutoResize(final boolean autoResize)
	{
		gearTab_AutoResize = autoResize;
	}

	public static boolean getGearTab_AutoResize()
	{
		return gearTab_AutoResize;
	}

	public static void setGearTab_AvailableListMode(final int listMode)
	{
		gearTab_AvailableListMode = listMode;
	}

	public static int getGearTab_AvailableListMode()
	{
		return gearTab_AvailableListMode;
	}

	public static void setGearTab_BuyRate(final int argBuyRate)
	{
		gearTab_BuyRate = argBuyRate;
	}

	public static int getGearTab_BuyRate()
	{
		return gearTab_BuyRate;
	}

	public static void setGearTab_IgnoreCost(final boolean ignoreCost)
	{
		gearTab_IgnoreCost = ignoreCost;
	}

	public static boolean getGearTab_IgnoreCost()
	{
		return gearTab_IgnoreCost;
	}

	public static void setGearTab_SelectedListMode(final int listMode)
	{
		gearTab_SelectedListMode = listMode;
	}

	public static int getGearTab_SelectedListMode()
	{
		return gearTab_SelectedListMode;
	}

	public static void setGearTab_SellRate(final int argSellRate)
	{
		gearTab_SellRate = argSellRate;
	}

	public static int getGearTab_SellRate()
	{
		return gearTab_SellRate;
	}

	public static void setGmgenPluginDir(final File aFile)
	{
		gmgenPluginDir = aFile;
	}

	public static File getGmgenPluginDir()
	{
		return gmgenPluginDir;
	}

	public static void setHPMaxAtFirstLevel(final boolean aBool)
	{
		hpMaxAtFirstLevel = aBool;
	}

	public static boolean isHPMaxAtFirstLevel()
	{
		return hpMaxAtFirstLevel;
	}

	public static void setHPMaxAtFirstClassLevel(final boolean aBool)
	{
		hpMaxAtFirstClassLevel = aBool;
	}

	public static boolean isHPMaxAtFirstClassLevel()
	{
		return hpMaxAtFirstClassLevel;
	}

	public static void setHPMaxAtFirstPCClassLevelOnly(final boolean aBool)
	{
		hpMaxAtFirstPCClassLevelOnly = aBool;
	}

	public static boolean isHPMaxAtFirstPCClassLevelOnly()
	{
		return hpMaxAtFirstPCClassLevelOnly;
	}

	public static void setHPPercent(final int argHPPct)
	{
		hpPercent = argHPPct;
	}

	public static int getHPPercent()
	{
		return hpPercent;
	}

	public static void setHPRollMethod(final int aBool)
	{
		hpRollMethod = aBool;
	}

	public static int getHPRollMethod()
	{
		return hpRollMethod;
	}

	public static String getHTMLOutputSheetPath()
	{
		if ("".equals(selectedCharacterHTMLOutputSheet)) //$NON-NLS-1$
		{
			return getPcgenOutputSheetDir().toString();
		}

		return new File(selectedCharacterHTMLOutputSheet).getParentFile().getAbsolutePath();
	}

	public static void setHideMonsterClasses(final boolean argHideMonsterClasses)
	{
		hideMonsterClasses = argHideMonsterClasses;
	}

	public static void setIgnoreMonsterHDCap(final boolean argIgoreCap)
	{
		ignoreMonsterHDCap = argIgoreCap;
	}

	public static boolean isIgnoreMonsterHDCap()
	{
		return ignoreMonsterHDCap;
	}

	public static void setIncludeSkills(final int anInt)
	{
		includeSkills = anInt;
	}

	public static int getIncludeSkills()
	{
		return includeSkills;
	}

//	public static void setIntCrossClassSkillCost(final int anInt)
//	{
//		intCrossClassSkillCost = anInt;
//	}
//
//	public static int getIntCrossClassSkillCost()
//	{
//		return intCrossClassSkillCost;
//	}

	/**
	 * @param string The invalidDmgText to set.
	 */
	public static void setInvalidDmgText(final String string)
	{
		SettingsHandler.invalidDmgText = string;
	}

	public static String getInvalidDmgText()
	{
		return invalidDmgText;
	}
	/**
	 * @param string The invalidToHitText to set.
	 */
	public static void setInvalidToHitText(final String string)
	{
		SettingsHandler.invalidToHitText = string;
	}

	public static String getInvalidToHitText()
	{
		return invalidToHitText;
	}

	/**
	 * TODO: It's commented out in gmgen. Is it safe to remove?
	 * @param GMGen
	 */
	public static void setIsGMGen(final boolean GMGen)
	{
		isGMGen = GMGen;
	}

	public static void setKitSelectorDimension(final Dimension d)
	{
		kitSelectorDimension = d;
	}

	public static Dimension getKitSelectorDimension()
	{
		return kitSelectorDimension;
	}

	public static void setKitSelectorLeftUpperCorner(final Point argLeftUpperCorner)
	{
		kitSelectorLeftUpperCorner = argLeftUpperCorner;
	}

	public static Point getKitSelectorLeftUpperCorner()
	{
		return kitSelectorLeftUpperCorner;
	}

	public static void setLastTipShown(final int argLastTipShown)
	{
		lastTipShown = argLastTipShown;
	}

	public static int getLastTipShown()
	{
		return lastTipShown;
	}

	public static void setLeftUpperCorner(final Point argLeftUpperCorner)
	{
		leftUpperCorner = argLeftUpperCorner;
	}

	public static Point getLeftUpperCorner()
	{
		return leftUpperCorner;
	}

	public static void setLoadCampaignsAtStart(final boolean aBool)
	{
		loadCampaignsAtStart = aBool;
	}

	public static boolean isLoadCampaignsAtStart()
	{
		return loadCampaignsAtStart;
	}

	public static void setLoadCampaignsWithPC(final boolean aBool)
	{
		loadCampaignsWithPC = aBool;
	}

	public static boolean isLoadCampaignsWithPC()
	{
		return loadCampaignsWithPC;
	}

	public static void setLoadURLs(final boolean aBool)
	{
		loadURLs = aBool;
	}

	public static boolean isLoadURLs()
	{
		return loadURLs;
	}

	public static void setLookAndFeel(final int argLookAndFeel)
	{
		looknFeel = argLookAndFeel;
	}

	public static int getLookAndFeel()
	{
		return looknFeel;
	}

	public static void setMaxPotionSpellLevel(final int anInt)
	{
		maxPotionSpellLevel = anInt;
	}

	public static int getMaxPotionSpellLevel()
	{
		return maxPotionSpellLevel;
	}

	public static void setMaxWandSpellLevel(final int anInt)
	{
		maxWandSpellLevel = anInt;
	}

	public static int getMaxWandSpellLevel()
	{
		return maxWandSpellLevel;
	}

	public static void setMetamagicAllowedInEqBuilder(final boolean aBool)
	{
		allowMetamagicInCustomizer = aBool;
	}

	public static boolean isMetamagicAllowedInEqBuilder()
	{
		return allowMetamagicInCustomizer;
	}

	public static void setNameDisplayStyle(final int style)
	{
		nameDisplayStyle = style;

		final PCGen_Frame1 frame = PCGen_Frame1.getInst();

		// Guard against load order
		if (frame != null)
		{
			PCGen_Frame1.forceUpdate_PlayerTabs();
		}
	}

	public static int getNameDisplayStyle()
	{
		return nameDisplayStyle;
	}

	public static void setOptionAllowedInSources(final boolean aBool)
	{
		optionAllowedInSources = aBool;
	}

	public static boolean isOptionAllowedInSources()
	{
		return optionAllowedInSources;
	}

	public static SortedProperties getOptions()
	{
		return options;
	}

	public static Dimension getOptionsFromProperties(final PlayerCharacter aPC)
	{
		Dimension d = new Dimension(0, 0);

		final String tempBrowserPath = getPCGenOption("browserPath", ""); //$NON-NLS-1$ //$NON-NLS-2$

		if (!"".equals(tempBrowserPath)) //$NON-NLS-1$
		{
			setBrowserPath(tempBrowserPath);
		}
		else
		{
			setBrowserPath(null);
		}

		setLeftUpperCorner(new Point(getPCGenOption("windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("windowLeftUpperCorner.Y", -1.0).intValue())); //$NON-NLS-1$

		setWindowState(getPCGenOption("windowState",Frame.NORMAL)); //$NON-NLS-1$

		Double dw = getPCGenOption("windowWidth", 0.0); //$NON-NLS-1$
		Double dh = getPCGenOption("windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw.doubleValue(), 0.0) && !CoreUtility.doublesEqual(dh.doubleValue(), 0.0))
		{
			final int width = Integer.parseInt(dw.toString().substring(0,
						Math.min(dw.toString().length(), dw.toString().lastIndexOf(".")))); //$NON-NLS-1$
			final int height = Integer.parseInt(dh.toString().substring(0,
						Math.min(dh.toString().length(), dh.toString().lastIndexOf(".")))); //$NON-NLS-1$
			d = new Dimension(width, height);
		}

		setCustomizerLeftUpperCorner(new Point(getPCGenOption("customizer.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("customizer.windowLeftUpperCorner.Y", -1.0).intValue())); //$NON-NLS-1$
		dw = getPCGenOption("customizer.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("customizer.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw.doubleValue(), 0.0) && !CoreUtility.doublesEqual(dh.doubleValue(), 0.0))
		{
			setCustomizerDimension(new Dimension(dw.intValue(), dh.intValue()));
		}


		setKitSelectorLeftUpperCorner(new Point(getPCGenOption("kitSelector.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("kitSelector.windowLeftUpperCorner.Y", -1.0).intValue())); //$NON-NLS-1$
		dw = getPCGenOption("kitSelector.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("kitSelector.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw.doubleValue(), 0.0) && !CoreUtility.doublesEqual(dh.doubleValue(), 0.0))
		{
			setKitSelectorDimension(new Dimension(dw.intValue(), dh.intValue()));
		}
		
		//
		// Read in the buy/sell percentages for the gear tab
		// If not in the .ini file and ignoreCost is set, then use 0%
		// Otherwise set buy to 100% and sell to %50
		//
		int buyRate = getPCGenOption("GearTab.buyRate", -1); //$NON-NLS-1$
		int sellRate = getPCGenOption("GearTab.sellRate", -1); //$NON-NLS-1$

		if ((buyRate < 0) || (sellRate < 0))
		{
			if (getPCGenOption("GearTab.ignoreCost", false)) //$NON-NLS-1$
			{
				buyRate = 0;
				sellRate = 0;
			}
			else
			{
				buyRate = 100;
				sellRate = 50;
			}
		}

		Globals.initCustColumnWidth(CoreUtility.split(getOptions().getProperty("pcgen.options.custColumnWidth", ""), ',')); //$NON-NLS-1$ //$NON-NLS-2$

		showD20InfoAtStart = getPCGenOption("showD20InfoAtStart", true); //$NON-NLS-1$
		loadURLs = getPCGenOption("loadURLs", false); //$NON-NLS-1$
		allowOverride = getPCGenOption("allowOverride", false); //$NON-NLS-1$
		showOGLOnLoad = getPCGenOption("showOGLOnLoad", true); //$NON-NLS-1$
		showMatureOnLoad = getPCGenOption("showMatureOnLoad", true); //$NON-NLS-1$
		showSponsorsOnLoad = getPCGenOption("showSponsorsOnLoad", true); //$NON-NLS-1$

		Globals.setSourceDisplay(SourceFormat.values()[getPCGenOption("sourceDisplay", SourceFormat.LONG.ordinal())]); //$NON-NLS-1$
		Globals.setCountry(getPCGenOption("country", null)); //$NON-NLS-1$

		setAbilitiesShownAsATab(getPCGenOption("abilitiesShownAsTab", false)); //$NON-NLS-1$
		setAlwaysOverwrite(getPCGenOption("alwaysOverwrite", false)); //$NON-NLS-1$
		setAutoFeatsRefundable(getPCGenOption("autoFeatsRefundable", false)); //$NON-NLS-1$
		setUseFeatBenefits(getPCGenOption("useFeatBenefits", true)); //$NON-NLS-1$
		setAutogenExoticMaterial(getPCGenOption("autoGenerateExoticMaterial", false)); //$NON-NLS-1$
		setAutogenMagic(getPCGenOption("autoGenerateMagic", false)); //$NON-NLS-1$
		setAutogenMasterwork(getPCGenOption("autoGenerateMasterwork", false)); //$NON-NLS-1$
		setAutogenRacial(getPCGenOption("autoGenerateRacial", false)); //$NON-NLS-1$
		setChaTabPlacement(getOptionTabPlacement("chaTabPlacement", SwingConstants.TOP)); //$NON-NLS-1$
		setClassTab_AvailableListMode(getPCGenOption("ClassTab.availableListMode", GuiConstants.INFOCLASS_VIEW_NAME)); //$NON-NLS-1$
		setClassTab_SelectedListMode(getPCGenOption("ClassTab.selectedListMode", GuiConstants.INFOCLASS_VIEW_NAME)); //$NON-NLS-1$
		setCreatePcgBackup(getPCGenOption("createPcgBackup", true));
		setCleanupTempFiles(getPCGenOption("cleanupTempFiles", false)); //$NON-NLS-1$
		setCustomizerSplit1(getPCGenOption("customizer.split1", -1)); //$NON-NLS-1$
		setCustomizerSplit2(getPCGenOption("customizer.split2", -1)); //$NON-NLS-1$
		setDefaultOSType(getPCGenOption("defaultOSType", null)); //$NON-NLS-1$
		setDmNotes(getPCGenOption("dmnotes", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setDomainTab_ListMode(getPCGenOption("DomainTab.ListMode", GuiConstants.INFODOMAIN_VIEW_NAME)); //$NON-NLS-1$
		setEnforceSpendingBeforeLevelUp(getPCGenOption("enforceSpendingBeforeLevelUp", false)); //$NON-NLS-1$
		setEquipTab_AvailableListMode(getPCGenOption("EquipTab.availableListMode", GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)); //$NON-NLS-1$
		setEquipTab_SelectedListMode(getPCGenOption("EquipTab.selectedListMode", GuiConstants.INFOEQUIPPING_VIEW_NAME)); //$NON-NLS-1$
//		setExcSkillCost(getPCGenOption("excSkillCost", 0)); //$NON-NLS-1$
		setExpertGUI(getPCGenOption("expertGUI", false)); //$NON-NLS-1$
		setFeatAutoColor(getPCGenOption("featAutoColor", Color.yellow.darker().getRGB())); //$NON-NLS-1$
		setFeatTab_AvailableListMode(getPCGenOption("FeatTab.availableListMode", GuiConstants.INFOFEATS_VIEW_PREREQTREE)); //$NON-NLS-1$
		setFeatTab_SelectedListMode(getPCGenOption("FeatTab.selectedListMode", GuiConstants.INFOFEATS_VIEW_NAMEONLY)); //$NON-NLS-1$
		setFeatVirtualColor(getPCGenOption("featVirtualColor", Color.magenta.getRGB())); //$NON-NLS-1$
		setGearTab_AllowDebt(getPCGenOption("GearTab.allowDebt", false)); //$NON-NLS-1$
		setGearTab_AutoResize(getPCGenOption("GearTab.autoResize", false)); //$NON-NLS-1$
		setGearTab_AvailableListMode(getPCGenOption("GearTab.availableListMode", //$NON-NLS-1$
				GuiConstants.INFOINVENTORY_VIEW_TYPE_NAME));
		setGearTab_BuyRate(buyRate);
		setGearTab_IgnoreCost(getPCGenOption("GearTab.ignoreCost", false)); //$NON-NLS-1$
		setGearTab_SelectedListMode(getPCGenOption("GearTab.selectedListMode", GuiConstants.INFOINVENTORY_VIEW_NAME)); //$NON-NLS-1$
		setGearTab_SellRate(sellRate);
		setGrimHPMode(getPCGenOption("grimHPMode", false)); //$NON-NLS-1$
		setGrittyACMode(getPCGenOption("grittyACMode", false)); //$NON-NLS-1$
		setGUIUsesOutputNameEquipment(getPCGenOption("GUIUsesOutputNameEquipment", false)); //$NON-NLS-1$
		setGUIUsesOutputNameSpells(getPCGenOption("GUIUsesOutputNameSpells", false)); //$NON-NLS-1$
		setHiddenSources(getPCGenOption("hiddenSources", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setHideMonsterClasses(getPCGenOption("hideMonsterClasses", false)); //$NON-NLS-1$
		setHPMaxAtFirstLevel(getPCGenOption("hpMaxAtFirstLevel", true)); //$NON-NLS-1$
		setHPMaxAtFirstClassLevel(getPCGenOption("hpMaxAtFirstClassLevel", false)); //$NON-NLS-1$
		setHPMaxAtFirstPCClassLevelOnly(getPCGenOption("hpMaxAtFirstPCClassLevelOnly", false)); //$NON-NLS-1$
		setHPPercent(getPCGenOption("hpPercent", 100)); //$NON-NLS-1$
		setHPRollMethod(getPCGenOption("hpRollMethod", Constants.HP_STANDARD)); //$NON-NLS-1$
		setIgnoreMonsterHDCap(getPCGenOption("ignoreMonsterHDCap", false)); //$NON-NLS-1$
		setIncludeSkills(getPCGenOption("includeSkills", 3)); //$NON-NLS-1$
		setSkillsTab_IncludeSkills(getPCGenOption("skillsTab_IncludeSkills", 1)); //$NON-NLS-1$
//		setIntCrossClassSkillCost(getPCGenOption("intCrossClassSkillCost", 2)); //$NON-NLS-1$
		setInvalidDmgText(getPCGenOption("invalidDmgText", LanguageBundle.getString("SettingsHandler.114")));  //$NON-NLS-1$//$NON-NLS-2$
		setInvalidToHitText(getPCGenOption("invalidToHitText", LanguageBundle.getString("SettingsHandler.114")));  //$NON-NLS-1$//$NON-NLS-2$
		setLastTipShown(getPCGenOption("lastTipOfTheDayTipShown", -1)); //$NON-NLS-1$
		setLoadCampaignsAtStart(getPCGenOption("loadCampaignsAtStart", false)); //$NON-NLS-1$
		setLoadCampaignsWithPC(getPCGenOption("loadCampaignsWithPC", false)); //$NON-NLS-1$
		setLookAndFeel(getPCGenOption("looknFeel", 1)); //$NON-NLS-1$
		setMaxPotionSpellLevel(getPCGenOption("maxPotionSpellLevel", 3)); //$NON-NLS-1$
		setMaxWandSpellLevel(getPCGenOption("maxWandSpellLevel", 4)); //$NON-NLS-1$
		setMetamagicAllowedInEqBuilder(getPCGenOption("allowMetamagicInCustomizer", false)); //$NON-NLS-1$
		setOptionAllowedInSources(getPCGenOption("optionAllowedInSources", true)); //$NON-NLS-1$
		setPccFilesLocation(new File(expandRelativePath(getPCGenOption("pccFilesLocation", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "data")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgenCustomDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenCustomDir", //$NON-NLS-1$
			Globals.getUserFilesPath() + File.separator + "customsources")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setPcgenVendorDataDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenVendorDataDir", //$NON-NLS-1$
			Globals.getUserFilesPath() + File.separator + "vendordata")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgenDocsDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenDocsDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "docs")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgenSystemDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenSystemDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "system")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgenThemePackDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenThemePackDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "lib" + File.separator + "themes")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setPcgenOutputSheetDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenOutputSheetDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "outputsheets")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgenPreviewDir(new File(expandRelativePath(getOptions().getProperty("pcgen.files.pcgenPreviewDir", //$NON-NLS-1$
				System.getProperty("user.dir") + File.separator + "preview")))); //$NON-NLS-1$ //$NON-NLS-2$
		setGmgenPluginDir(new File(expandRelativePath(getOptions().getProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "plugins")))); //$NON-NLS-1$ //$NON-NLS-2$
		setPcgPath(new File(expandRelativePath(getOptions().getProperty("pcgen.files.characters", //$NON-NLS-1$
						Globals.getDefaultPcgPath()))));
		setBackupPcgPath(new File(expandRelativePath(getOptions().getProperty("pcgen.files.characters.backup", "")))); //$NON-NLS-1$
		setPortraitsPath(new File(expandRelativePath(getOptions().getProperty("pcgen.files.portraits", //$NON-NLS-1$
						Globals.getDefaultPcgPath()))));
		setPostExportCommandStandard(getPCGenOption("postExportCommandStandard", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setPostExportCommandPDF(getPCGenOption("postExportCommandPDF", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setPrereqFailColor(getPCGenOption("prereqFailColor", Color.red.getRGB())); //$NON-NLS-1$
		setPrereqQualifyColor(getPCGenOption("prereqQualifyColor", Color.black.getRGB())); //$NON-NLS-1$
		setPreviewTabShown(getPCGenOption("previewTabShown", true)); //$NON-NLS-1$
		setQuickLaunchSources(getPCGenOption("quickLaunchSources", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setRaceTab_ListMode(getPCGenOption("RaceTab.ListMode", GuiConstants.INFORACE_VIEW_NAME)); //$NON-NLS-1$
		setRanStartingWizard(getPCGenOption("ranStartingWizard", false)); //$NON-NLS-1$
		setROG(getPCGenOption("isROG", false)); //$NON-NLS-1$
		setSaveCustomInLst(getPCGenOption("saveCustomInLst", false)); //$NON-NLS-1$
		setSaveOutputSheetWithPC(getPCGenOption("saveOutputSheetWithPC", false)); //$NON-NLS-1$
		setPrintSpellsWithPC(getPCGenOption("printSpellsWithPC", true)); //$NON-NLS-1$
		setSelectedSpellSheet(expandRelativePath(getOptions().getProperty("pcgen.files.selectedSpellOutputSheet", ""))); //$NON-NLS-1$ //$NON-NLS-2$
		setSelectedCharacterHTMLOutputSheet(expandRelativePath(getOptions().getProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
					"")), aPC); //$NON-NLS-1$
		setSelectedCharacterPDFOutputSheet(expandRelativePath(getOptions().getProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
					"")), aPC); //$NON-NLS-1$
		setSelectedEqSetTemplate(expandRelativePath(getOptions().getProperty("pcgen.files.selectedEqSetTemplate", ""))); //$NON-NLS-1$ //$NON-NLS-2$
		setSelectedPartyHTMLOutputSheet(expandRelativePath(getOptions().getProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
					""))); //$NON-NLS-1$
		setSelectedPartyPDFOutputSheet(expandRelativePath(getOptions().getProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
					""))); //$NON-NLS-1$
		setShowFeatDialogAtLevelUp(getPCGenOption("showFeatDialogAtLevelUp", true)); //$NON-NLS-1$
		setShowHPDialogAtLevelUp(getPCGenOption("showHPDialogAtLevelUp", true)); //$NON-NLS-1$
		setShowMemoryArea(getPCGenOption("showMemoryArea", false)); //$NON-NLS-1$
		setShowImagePreview(getPCGenOption("showImagePreview", true)); //$NON-NLS-1$
		setShowSingleBoxPerBundle(getPCGenOption("showSingleBoxPerBundle", false)); //$NON-NLS-1$
		setOutputDeprecationMessages(getPCGenOption("outputDeprecationMessages", true));
		setInputUnconstructedMessages(getPCGenOption("inputUnconstructedMessages", false));
		setShowStatDialogAtLevelUp(getPCGenOption("showStatDialogAtLevelUp", true)); //$NON-NLS-1$
		setShowTipOfTheDay(getPCGenOption("showTipOfTheDay", true)); //$NON-NLS-1$
		setShowToolBar(getPCGenOption("showToolBar", true)); //$NON-NLS-1$
		setShowSkillModifier(getPCGenOption("showSkillModifier", true)); //$NON-NLS-1$
		setShowSkillRanks(getPCGenOption("showSkillRanks", true)); //$NON-NLS-1$
		setShowWarningAtFirstLevelUp(getPCGenOption("showWarningAtFirstLevelUp", true)); //$NON-NLS-1$
		setSingleChoicePreference(getPCGenOption("ChooserSingleChoiceMethod",
			Constants.CHOOSER_SINGLE_CHOICE_METHOD_NONE)); //$NON-NLS-1$
		setSkillsTab_AvailableListMode(getPCGenOption("SkillsTab.availableListMode", //$NON-NLS-1$
				GuiConstants.INFOSKILLS_VIEW_TYPE_NAME));
		setSkillsTab_SelectedListMode(getPCGenOption("SkillsTab.selectedListMode", GuiConstants.INFOSKILLS_VIEW_NAME)); //$NON-NLS-1$
		setSkinLFThemePack(getPCGenOption("skinLFThemePack", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setSpellMarketPriceAdjusted(getPCGenOption("spellMarketPriceAdjusted", false)); //$NON-NLS-1$
		setSpellsTab_AvailableListMode(getPCGenOption("SpellsTab.availableListMode", GuiConstants.INFOSPELLS_VIEW_CLASS)); //$NON-NLS-1$
		setSpellsTab_SelectedListMode(getPCGenOption("SpellsTab.selectedListMode", GuiConstants.INFOSPELLS_VIEW_CLASS)); //$NON-NLS-1$
		setSourceStatusReleaseColor(getPCGenOption("sourceStatusReleaseColor", Color.black.getRGB())); //$NON-NLS-1$
		setSourceStatusAlphaColor(getPCGenOption("sourceStatusAlphaColor", Color.red.getRGB())); //$NON-NLS-1$
		setSourceStatusBetaColor(getPCGenOption("sourceStatusBetaColor", new Color(128, 0, 0).getRGB())); //$NON-NLS-1$
		setSourceStatusTestColor(getPCGenOption("sourceStatusTestColor", Color.magenta.getRGB())); //$NON-NLS-1$
		setSummaryTabShown(getPCGenOption("summaryTabShown", true)); //$NON-NLS-1$
		setTabPlacement(getOptionTabPlacement("tabPlacement", SwingConstants.BOTTOM)); //$NON-NLS-1$
		setTemplateSelTab_ListMode(getPCGenOption("TemplateTab.selectedListMode", GuiConstants.INFOTEMPLATE_VIEW_NAME)); //$NON-NLS-1$
		setTemplateTab_ListMode(getPCGenOption("TemplateTab.availableListMode", GuiConstants.INFOTEMPLATE_VIEW_NAME)); //$NON-NLS-1$
		setToolTipTextShown(getPCGenOption("toolTipTextShown", true)); //$NON-NLS-1$
		setUseHigherLevelSlotsDefault(getPCGenOption("useHigherLevelSlotsDefault", false)); //$NON-NLS-1$
		setUseAdvancedSourceSelect(getPCGenOption("useAdvancedSourceSelect", false)); //$NON-NLS-1$
		setUseWaitCursor(getPCGenOption("useWaitCursor", true)); //$NON-NLS-1$
		setWantToLoadMasterworkAndMagic(getPCGenOption("loadMasterworkAndMagicFromLst", false)); //$NON-NLS-1$
		setWeaponProfPrintout(getPCGenOption("weaponProfPrintout",
			Constants.DEFAULT_PRINTOUT_WEAPONPROF)); //$NON-NLS-1$

		// Load up all the RuleCheck stuff from the options.ini file
		// It's stored as:
		//   pcgen.options.rulechecks=aKey:Y|bKey:N|cKey:Y
		parseRuleChecksFromOptions(getPCGenOption("ruleChecks", "")); //$NON-NLS-1$ //$NON-NLS-2$

		return d;
	}

	/**
	 * Retreive the chosen campaign files from properties for
	 * use by the rest of PCGen.
	 * 
	 * @param gameMode The GameMode to reteieve the files for.
	 */
	private static void getChosenCampaignFiles(GameMode gameMode)
	{
		List<String> uriStringList =
				CoreUtility.split(getOptions().getProperty(
					"pcgen.files.chosenCampaignSourcefiles." + gameMode.getName(), //$NON-NLS-1$
					""), ',');
		List<URI> uriList = new ArrayList<URI>(uriStringList.size());
		for (String str : uriStringList)
		{
			try {
				uriList.add(new URI(str));
			} catch (URISyntaxException e) {
				Logging.errorPrint("Settings error: Unable to convert " + str
						+ " to a URI: " + e.getLocalizedMessage());
			}
		}
		PersistenceManager.getInstance().setChosenCampaignSourcefiles(uriList, gameMode); //$NON-NLS-1$
	}

	/**
	 * Initialise the data and game modes - does the parsing of the data, so 
	 * the plugins must be loaded before this method is called.
	 */
	public static void initGameModes()
	{
		try
		{
			PersistenceManager.getInstance().initialize();
		}
		catch (PersistenceLayerException e)
		{
			ShowMessageDelegate.showMessageDialog(
				e.getMessage(), Constants.APPLICATION_NAME, MessageType.INFORMATION);
		}

		setGame(getPCGenOption("game", Constants.GAMEMODE_EDITION_THREE_POINT_FIVE)); //$NON-NLS-1$
		game.clearLoadContext();

		Globals.createEmptyRace();

		showNatWeaponTab = getPCGenOption("showNatWeaponTab", false); //$NON-NLS-1$
		validateBonuses = getPCGenOption("validateBonuses", false); //$NON-NLS-1$

		debugFeats = getPCGenOption("debugFeats", false); //$NON-NLS-1$
	}

	public static void setOptionsProperties(final PlayerCharacter aPC)
	{
		if (getPcgPath() != null)
		{
			getOptions().setProperty("pcgen.files.characters", retractRelativePath(getPcgPath().getAbsolutePath())); //$NON-NLS-1$
		}
		else
		{
			// hasn't been set properly yet
			getOptions().setProperty("pcgen.files.characters", retractRelativePath(Globals.getDefaultPath())); //$NON-NLS-1$
		}
		if (getBackupPcgPath() != null && !getBackupPcgPath().getPath().equals(""))
		{
			getOptions().setProperty("pcgen.files.characters.backup", retractRelativePath(getBackupPcgPath().getAbsolutePath())); //$NON-NLS-1$
		}
		else
		{
			getOptions().setProperty("pcgen.files.characters.backup", ""); //$NON-NLS-1$
		}

		getOptions().setProperty("pcgen.files.portraits", retractRelativePath(getPortraitsPath().getAbsolutePath())); //$NON-NLS-1$
		getOptions().setProperty("pcgen.files.selectedSpellOutputSheet", retractRelativePath(getSelectedSpellSheet())); //$NON-NLS-1$
		getOptions().setProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterHTMLOutputSheet(aPC)));
		getOptions().setProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterPDFOutputSheet(aPC)));
		getOptions().setProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedPartyHTMLOutputSheet()));
		getOptions().setProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedPartyPDFOutputSheet()));
		getOptions().setProperty("pcgen.files.selectedEqSetTemplate", retractRelativePath(getSelectedEqSetTemplate())); //$NON-NLS-1$
		getOptions().setProperty("pcgen.files.chosenCampaignSourcefiles", //$NON-NLS-1$
			StringUtil.join(PersistenceManager.getInstance().getChosenCampaignSourcefiles(), ", "));

		getOptions().setProperty("pcgen.options.custColumnWidth", StringUtil.join(Globals.getCustColumnWidth(), ", ")); //$NON-NLS-1$

		if (getPcgenCustomDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenCustomDir", //$NON-NLS-1$
				retractRelativePath(getPcgenCustomDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenCustomDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenVendorDataDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenVendorDataDir", //$NON-NLS-1$
				retractRelativePath(getPcgenVendorDataDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenVendorDataDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenDocsDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenDocsDir", //$NON-NLS-1$
				retractRelativePath(getPcgenDocsDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenDocsDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenSystemDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenSystemDir", //$NON-NLS-1$
				retractRelativePath(getPcgenSystemDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenSystemDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenOutputSheetDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenOutputSheetDir", //$NON-NLS-1$
				retractRelativePath(getPcgenOutputSheetDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenOutputSheetDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenPreviewDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenPreviewDir", retractRelativePath(getPcgenPreviewDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenPreviewDir", Constants.EMPTY_STRING);
		}

		if (getGmgenPluginDir() != null)
		{
			getOptions().setProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
				retractRelativePath(getGmgenPluginDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("gmgen.files.gmgenPluginDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getPcgenThemePackDir() != null)
		{
			getOptions().setProperty("pcgen.files.pcgenThemePackDir", //$NON-NLS-1$
				retractRelativePath(getPcgenThemePackDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.pcgenThemePackDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getBrowserPath() != null)
		{
			setPCGenOption("browserPath", getBrowserPath()); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("browserPath", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (PCGen_Frame1.getInst() != null)
		{
			setOpenRecentOption("openRecentPCs", PCGen_Frame1.getInst().getOpenRecentPCs()); //$NON-NLS-1$
			setOpenRecentOption("openRecentParties", PCGen_Frame1.getInst().getOpenRecentParties()); //$NON-NLS-1$
		}

		if (getGame() != null)
		{
			setPCGenOption("game", getGame().getName()); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("game", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			setPCGenOption("skinLFThemePack", getSkinLFThemePack()); //$NON-NLS-1$
		}
		catch (NullPointerException e)
		{
			//TODO: Should this really be ignored???  XXX
		}

		if (getPccFilesLocation() != null)
		{
			setPCGenOption("pccFilesLocation", retractRelativePath(getPccFilesLocation().getAbsolutePath())); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("pccFilesLocation", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (getLeftUpperCorner() != null)
		{
			setPCGenOption("windowLeftUpperCorner.X", getLeftUpperCorner().getX()); //$NON-NLS-1$
			setPCGenOption("windowLeftUpperCorner.Y", getLeftUpperCorner().getY()); //$NON-NLS-1$
		}

		setPCGenOption("windowState",getWindowState()); //$NON-NLS-1$

		if (Globals.getRootFrame() != null)
		{
			setPCGenOption("windowWidth", Globals.getRootFrame().getSize().getWidth()); //$NON-NLS-1$
			setPCGenOption("windowHeight", Globals.getRootFrame().getSize().getHeight()); //$NON-NLS-1$
		}

		if (getCustomizerLeftUpperCorner() != null)
		{
			setPCGenOption("customizer.windowLeftUpperCorner.X", getCustomizerLeftUpperCorner().getX()); //$NON-NLS-1$
			setPCGenOption("customizer.windowLeftUpperCorner.Y", getCustomizerLeftUpperCorner().getY()); //$NON-NLS-1$
		}

		if (getCustomizerDimension() != null)
		{
			setPCGenOption("customizer.windowWidth", getCustomizerDimension().getWidth()); //$NON-NLS-1$
			setPCGenOption("customizer.windowHeight", getCustomizerDimension().getHeight()); //$NON-NLS-1$
		}

		if (getKitSelectorLeftUpperCorner() != null)
		{
			setPCGenOption("kitSelector.windowLeftUpperCorner.X", getKitSelectorLeftUpperCorner().getX()); //$NON-NLS-1$
			setPCGenOption("kitSelector.windowLeftUpperCorner.Y", getKitSelectorLeftUpperCorner().getY()); //$NON-NLS-1$
		}

		if (getKitSelectorDimension() != null)
		{
			setPCGenOption("kitSelector.windowWidth", getKitSelectorDimension().getWidth()); //$NON-NLS-1$
			setPCGenOption("kitSelector.windowHeight", getKitSelectorDimension().getHeight()); //$NON-NLS-1$
		}

		final String paperName = Globals.getPaperInfo(PaperInfo.NAME);

		if (paperName != null)
		{
			setPCGenOption("paperName", paperName); //$NON-NLS-1$
		}

		//
		// Remove old-style option values
		//
		setPCGenOption("allStatsValue", null);
		setPCGenOption("purchaseMethodName", null);
		setPCGenOption("rollMethod", null);
		setPCGenOption("rollMethodExpression", null);

		for (int idx = 0; idx < SystemCollections.getUnmodifiableGameModeList().size(); idx++)
		{
			final GameMode gameMode = SystemCollections.getUnmodifiableGameModeList().get(idx);
			String gameModeKey = gameMode.getName();
			if (gameMode.getUnitSet() != null && gameMode.getUnitSet().getDisplayName() != null)
			{
				setPCGenOption("gameMode." + gameModeKey + ".unitSetName", gameMode.getUnitSet().getDisplayName());
			}
			setPCGenOption("gameMode." + gameModeKey + ".purchaseMethodName", gameMode.getPurchaseModeMethodName()); //$NON-NLS-1$
			setPCGenOption("gameMode." + gameModeKey + ".rollMethod", gameMode.getRollMethod()); //$NON-NLS-1$
			setPCGenOption("gameMode." + gameModeKey + ".rollMethodExpression", gameMode.getRollMethodExpressionName()); //$NON-NLS-1$
			setPCGenOption("gameMode." + gameModeKey + ".allStatsValue", gameMode.getAllStatsValue());
			setPCGenOption("gameMode." + gameModeKey + ".xpTableName", gameMode.getDefaultXPTableName());
			setPCGenOption("gameMode." + gameModeKey + ".characterType", gameMode.getDefaultCharacterType());
		}

		setRuleChecksInOptions("ruleChecks"); //$NON-NLS-1$

		setPCGenOption("abilitiesShownAsTab", isAbilitiesShownAsATab()); //$NON-NLS-1$
		setPCGenOption("allowMetamagicInCustomizer", isMetamagicAllowedInEqBuilder()); //$NON-NLS-1$
		setPCGenOption("allowOverride", allowOverride); //$NON-NLS-1$
		setPCGenOption("alwaysOverwrite", getAlwaysOverwrite()); //$NON-NLS-1$
		setPCGenOption("autoFeatsRefundable", isAutoFeatsRefundable()); //$NON-NLS-1$
		setPCGenOption("useFeatBenefits", useFeatBenefits()); //$NON-NLS-1$
		setPCGenOption("autoGenerateExoticMaterial", isAutogenExoticMaterial()); //$NON-NLS-1$
		setPCGenOption("autoGenerateMagic", isAutogenMagic()); //$NON-NLS-1$
		setPCGenOption("autoGenerateMasterwork", isAutogenMasterwork()); //$NON-NLS-1$
		setPCGenOption("autoGenerateRacial", isAutogenRacial()); //$NON-NLS-1$
		setPCGenOption("chaTabPlacement", convertTabPlacementToString(chaTabPlacement)); //$NON-NLS-1$
		setPCGenOption("ChooserSingleChoiceMethod", getSingleChoicePreference()); //$NON-NLS-1$
		setPCGenOption("ClassTab.availableListMode", getClassTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("ClassTab.selectedListMode", getClassTab_SelectedListMode()); //$NON-NLS-1$
		setPCGenOption("cleanupTempFiles", getCleanupTempFiles()); //$NON-NLS-1$
		setPCGenOption("country", Globals.getCountry()); //$NON-NLS-1$
		setPCGenOption("createPcgBackup", getCreatePcgBackup()); //$NON-NLS-1$
		setPCGenOption("customizer.split1", getCustomizerSplit1()); //$NON-NLS-1$
		setPCGenOption("customizer.split2", getCustomizerSplit2()); //$NON-NLS-1$
		setPCGenOption("defaultOSType", getDefaultOSType()); //$NON-NLS-1$
		setPCGenOption("dmnotes", getDmNotes()); //$NON-NLS-1$
		setPCGenOption("DomainTab.ListMode", getDomainTab_ListMode()); //$NON-NLS-1$
		setPCGenOption("EquipTab.availableListMode", getEquipTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("EquipTab.selectedListMode", getEquipTab_SelectedListMode()); //$NON-NLS-1$
//		setPCGenOption("excSkillCost", getExcSkillCost()); //$NON-NLS-1$
		setPCGenOption("expertGUI", isExpertGUI()); //$NON-NLS-1$
		setPCGenOption("featAutoColor", "0x" + Integer.toHexString(getFeatAutoColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("FeatTab.availableListMode", getFeatTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("FeatTab.selectedListMode", getFeatTab_SelectedListMode()); //$NON-NLS-1$
		setPCGenOption("featVirtualColor", "0x" + Integer.toHexString(getFeatVirtualColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("GearTab.allowDebt", getGearTab_AllowDebt()); //$NON-NLS-1$
		setPCGenOption("GearTab.autoResize", getGearTab_AutoResize()); //$NON-NLS-1$
		setPCGenOption("GearTab.availableListMode", getGearTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("GearTab.buyRate", getGearTab_BuyRate()); //$NON-NLS-1$
		setPCGenOption("GearTab.ignoreCost", getGearTab_IgnoreCost()); //$NON-NLS-1$
		setPCGenOption("GearTab.selectedListMode", getGearTab_SelectedListMode()); //$NON-NLS-1$
		setPCGenOption("GearTab.sellRate", getGearTab_SellRate()); //$NON-NLS-1$
		setPCGenOption("grimHPMode", isGrimHPMode()); //$NON-NLS-1$
		setPCGenOption("grittyACMode", isGrittyACMode()); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameEquipment", guiUsesOutputNameEquipment()); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameSpells", guiUsesOutputNameSpells()); //$NON-NLS-1$
		setPCGenOption("hideMonsterClasses", hideMonsterClasses()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstLevel", isHPMaxAtFirstLevel()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstClassLevel", isHPMaxAtFirstClassLevel()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstPCClassLevelOnly", isHPMaxAtFirstPCClassLevelOnly()); //$NON-NLS-1$
		setPCGenOption("hpPercent", getHPPercent()); //$NON-NLS-1$
		setPCGenOption("hpRollMethod", getHPRollMethod()); //$NON-NLS-1$
		setPCGenOption("ignoreMonsterHDCap", isIgnoreMonsterHDCap()); //$NON-NLS-1$
		setPCGenOption("includeSkills", getIncludeSkills()); //$NON-NLS-1$
		setPCGenOption("skillsTab_IncludeSkills", getSkillsTab_IncludeSkills()); //$NON-NLS-1$
//		setPCGenOption("intCrossClassSkillCost", getIntCrossClassSkillCost()); //$NON-NLS-1$
		setPCGenOption("invalidDmgText", getInvalidDmgText()); //$NON-NLS-1$
		setPCGenOption("invalidToHitText", getInvalidToHitText()); //$NON-NLS-1$
		setPCGenOption("lastTipOfTheDayTipShown", getLastTipShown()); //$NON-NLS-1$
		setPCGenOption("loadCampaignsAtStart", isLoadCampaignsAtStart()); //$NON-NLS-1$
		setPCGenOption("loadCampaignsWithPC", isLoadCampaignsWithPC()); //$NON-NLS-1$
		setPCGenOption("loadMasterworkAndMagicFromLst", wantToLoadMasterworkAndMagic()); //$NON-NLS-1$
		setPCGenOption("loadURLs", loadURLs); //$NON-NLS-1$
		setPCGenOption("looknFeel", getLookAndFeel()); //$NON-NLS-1$
		setPCGenOption("maxPotionSpellLevel", getMaxPotionSpellLevel()); //$NON-NLS-1$
		setPCGenOption("maxWandSpellLevel", getMaxWandSpellLevel()); //$NON-NLS-1$
		setPCGenOption("nameDisplayStyle", getNameDisplayStyle()); //$NON-NLS-1$
		setPCGenOption("optionAllowedInSources", isOptionAllowedInSources()); //$NON-NLS-1$
		setPCGenOption("postExportCommandStandard", SettingsHandler.getPostExportCommandStandard()); //$NON-NLS-1$
		setPCGenOption("postExportCommandPDF", SettingsHandler.getPostExportCommandPDF()); //$NON-NLS-1$
		setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(getPrereqFailColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("prereqQualifyColor", "0x" + Integer.toHexString(getPrereqQualifyColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("previewTabShown", isPreviewTabShown()); //$NON-NLS-1$
		setPCGenOption("RaceTab.ListMode", getRaceTab_ListMode()); //$NON-NLS-1$
		setPCGenOption("ranStartingWizard", ranStartingWizard); //$NON-NLS-1$
		setPCGenOption("saveCustomInLst", isSaveCustomInLst()); //$NON-NLS-1$
		setPCGenOption("saveOutputSheetWithPC", getSaveOutputSheetWithPC()); //$NON-NLS-1$
		setPCGenOption("printSpellsWithPC", getPrintSpellsWithPC()); //$NON-NLS-1$
		setPCGenOption("showD20InfoAtStart", showD20InfoAtStart); //$NON-NLS-1$
		setPCGenOption("showFeatDialogAtLevelUp", getShowFeatDialogAtLevelUp()); //$NON-NLS-1$
		setPCGenOption("enforceSpendingBeforeLevelUp", getEnforceSpendingBeforeLevelUp()); //$NON-NLS-1$
		setPCGenOption("showHPDialogAtLevelUp", getShowHPDialogAtLevelUp()); //$NON-NLS-1$
		setPCGenOption("showMemoryArea", isShowMemoryArea()); //$NON-NLS-1$
		setPCGenOption("showImagePreview", isShowImagePreview()); //$NON-NLS-1$
		setPCGenOption("showNatWeaponTab", showNatWeaponTab); //$NON-NLS-1$
		setPCGenOption("showOGLOnLoad", showOGLOnLoad); //$NON-NLS-1$
		setPCGenOption("showMatureOnLoad", showMatureOnLoad); //$NON-NLS-1$
		setPCGenOption("showSponsorsOnLoad", showSponsorsOnLoad); //$NON-NLS-1$
		setPCGenOption("showStatDialogAtLevelUp", getShowStatDialogAtLevelUp()); //$NON-NLS-1$
		setPCGenOption("showTipOfTheDay", getShowTipOfTheDay()); //$NON-NLS-1$
		setPCGenOption("showToolBar", isShowToolBar()); //$NON-NLS-1$
		setPCGenOption("showSkillModifier", getShowSkillModifier()); //$NON-NLS-1$
		setPCGenOption("showSkillRanks", getShowSkillRanks()); //$NON-NLS-1$
		setPCGenOption("showSingleBoxPerBundle", getShowSingleBoxPerBundle()); //$NON-NLS-1$
		setPCGenOption("showWarningAtFirstLevelUp", isShowWarningAtFirstLevelUp()); //$NON-NLS-1$
		setPCGenOption("SkillsTab.availableListMode", getSkillsTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("SkillsTab.selectedListMode", getSkillsTab_SelectedListMode()); //$NON-NLS-1$
		setPCGenOption("sourceDisplay", Globals.getSourceDisplay().ordinal()); //$NON-NLS-1$
		setPCGenOption("sourceStatusReleaseColor", "0x" + Integer.toHexString(getSourceStatusReleaseColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("sourceStatusAlphaColor", "0x" + Integer.toHexString(getSourceStatusAlphaColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("sourceStatusBetaColor", "0x" + Integer.toHexString(getSourceStatusBetaColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("sourceStatusTestColor", "0x" + Integer.toHexString(getSourceStatusTestColor())); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("spellMarketPriceAdjusted", isSpellMarketPriceAdjusted()); //$NON-NLS-1$
		setPCGenOption("SpellsTab.availableListMode", getSpellsTab_AvailableListMode()); //$NON-NLS-1$
		setPCGenOption("SpellsTab.selectedListMode", getSpellsTab_SelectedListMode()); //$NON-NLS-1$
		setPCGenOption("summaryTabShown", isSummaryTabShown()); //$NON-NLS-1$
		setPCGenOption("tabPlacement", convertTabPlacementToString(tabPlacement)); //$NON-NLS-1$
		setPCGenOption("TemplateTab.availableListMode", getTemplateTab_ListMode()); //$NON-NLS-1$
		setPCGenOption("TemplateTab.selectedListMode", getTemplateSelTab_ListMode()); //$NON-NLS-1$
		setPCGenOption("toolTipTextShown", isToolTipTextShown()); //$NON-NLS-1$
		setPCGenOption("useHigherLevelSlotsDefault", isUseHigherLevelSlotsDefault()); //$NON-NLS-1$
		setPCGenOption("useWaitCursor", getUseWaitCursor()); //$NON-NLS-1$
		setPCGenOption("validateBonuses", validateBonuses); //$NON-NLS-1$
		setPCGenOption("weaponProfPrintout", SettingsHandler.getWeaponProfPrintout()); //$NON-NLS-1$
		setPCGenOption("debugFeats", debugFeats); //$NON-NLS-1$
		setPCGenOption("outputDeprecationMessages", outputDeprecationMessages()); //$NON-NLS-1$
		setPCGenOption("inputUnconstructedMessages", inputUnconstructedMessages()); //$NON-NLS-1$
		setPCGenOption("hiddenSources", getHiddenSources()); //$NON-NLS-1$
		setPCGenOption("quickLaunchSources", getQuickLaunchSources()); //$NON-NLS-1$
		setPCGenOption("useAdvancedSourceSelect", useAdvancedSourceSelect()); //$NON-NLS-1$
	}

	public static void setPCGenOption(final String optionName, final int optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setPCGenOption(final String optionName, final String optionValue)
	{
		if (optionValue==null) {
			getOptions().remove("pcgen.options." + optionName); //$NON-NLS-1$
		}
		else {
			getOptions().setProperty("pcgen.options." + optionName, optionValue); //$NON-NLS-1$
		}
	}

	public static int getPCGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getPCGenOption(optionName, String.valueOf(defaultValue))).intValue();
	}

	public static String getPCGenOption(final String optionName, final String defaultValue)
	{
		return getOptions().getProperty("pcgen.options." + optionName, defaultValue); //$NON-NLS-1$
	}
	
	public static boolean hasPCGenOption(final String optionName)
	{
		return getOptions().containsKey("pcgen.options." + optionName);
	}

	public static String getPDFOutputSheetPath()
	{
		if ("".equals(selectedCharacterPDFOutputSheet)) //$NON-NLS-1$
		{
			return getPcgenOutputSheetDir().toString();
		}

		return new File(selectedCharacterPDFOutputSheet).getParentFile().getAbsolutePath();
	}

	/**
	 * Where to load the data (lst) files from
	 * @param argPccFilesLocation
	 */
	public static void setPccFilesLocation(final File argPccFilesLocation)
	{
		pccFilesLocation = argPccFilesLocation;
	}

	/**
	 * Where to load the data (lst) files from
	 * @deprecated Use ConfigurationSettings.getPccFilesDir() instead.
	 * @return pcc files location
	 */
	public static File getPccFilesLocation()
	{
		return pccFilesLocation;
	}

	/**
	 * Ensures that the path specified exists.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void ensurePathExists(final File path)
	{
		if (path != null && !path.exists())
		{
			path.mkdirs();
		}
	}

	/**
	 * Sets the path to the character files.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setPcgPath(final File path)
	{
		if (path != null && !path.exists())
		{
			path.mkdirs();
		}
		pcgPath = path;
	}

	/**
	 * Returns the path to the character files.
	 *
	 * @return    the <code>pcgPath</code> property
	 */
	public static File getPcgPath()
	{
		return pcgPath;
	}

	/**
	 * Sets the path that was last used in a character or output file chooser.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setLastUsedPcgPath(final File path)
	{
		if (path != null && !path.exists())
		{
			path.mkdirs();
		}
		lastUsedPcgPath = path;
	}

	/**
	 * @return The path that was last used in a character or output file chooser.
	 */
	public static File getLastUsedPcgPath()
	{
		if (lastUsedPcgPath == null)
		{
			return pcgPath;
		}
		return lastUsedPcgPath;
	}
	
	public static void setPcgenCustomDir(final File aFile)
	{
		if (aFile != null && !aFile.exists())
		{
			aFile.mkdirs();
		}
		pcgenCustomDir = aFile;
	}

	/**
	 * @deprecated Use ConfigurationSettings.getCustomDir()
	 * @return
	 */
	public static File getPcgenCustomDir()
	{
		return pcgenCustomDir;
	}

	public static void setPcgenVendorDataDir(final File aFile)
	{
		if (aFile != null && !aFile.exists())
		{
			aFile.mkdirs();
		}
		pcgenVendorDataDir = aFile;
	}

	/**
	 * @deprecated Use ConfigurationSettings.getVendorDataDir()
	 * @return
	 */
	public static File getPcgenVendorDataDir()
	{
		return pcgenVendorDataDir;
	}

	public static void setPcgenSponsorDir(final File aFile)
	{
		pcgenSponsorDir = aFile;
	}

	/**
	 * @deprecated 
	 * @return
	 */
	public static File getPcgenSponsorDir()
	{
		return pcgenSponsorDir;
	}

	public static void setPcgenDocsDir(final File argPcgenDocsDir)
	{
		pcgenDocsDir = argPcgenDocsDir;
	}

	/**
	 * @deprecated Use ConfigurationSettings.getDocsDir()
	 * @return
	 */
	public static File getPcgenDocsDir()
	{
		return pcgenDocsDir;
	}

	public static void setPcgenFilesDir(final File aFile)
	{
		if (aFile != null)
		{
			pcgenFilesDir = aFile;
		}
	}

	/**
	 * @deprecated Use ConfigurationSettings.getSettingsDir()
	 * @return
	 */
	public static File getPcgenFilesDir()
	{
		return pcgenFilesDir;
	}
	
	/**
	 * Return a handle to the directory in which the PCGen config 
	 * is stored. Takes into account the FilePaths setting as well 
	 * as the PcgenFilesDir setting.
	 *  
	 * @return The directory in which the PCGen config is stored.
	 */
	public static File getDecodedPCGenFilesDir()
	{
		String fType = SettingsHandler.getFilePaths();
		if ((fType == null) || (fType.length() < 1))
		{
			// make sure we have a default
			if (Globals.isMacPlatform)
			{
				fType = "mac_user";
			} else {
				fType = "pcgen";
			}
		}

		if (fType.equals("pcgen"))
		{
			return new File(System.getProperty("user.dir"));
		}
		else if (fType.equals("user"))
		{
			return new File(System.getProperty("user.home")
				+ File.separator + ".pcgen");
		}
		else if (fType.equals("mac_user"))
		{
			return new File(Globals.defaultMacOptionsPath);
		}
		return getPcgenFilesDir();
	}

	public static void setPcgenOutputSheetDir(final File aFile)
	{
		pcgenOutputSheetDir = aFile;
	}

	/**
	 * @deprecated Use ConfigurationSettings.getOutputSheetsDir()
	 * @return
	 */
	public static File getPcgenOutputSheetDir()
	{
		return pcgenOutputSheetDir;
	}

	public static void setPcgenSystemDir(final File aFile)
	{
		pcgenSystemDir = aFile;
	}

	/**
	 * @deprecated Use ConfigurationSettings.getSystemsDir() 
	 * @return
	 */
	public static File getPcgenSystemDir()
	{
		return pcgenSystemDir;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public static File getPcgenThemePackDir()
	{
		return pcgenThemePackDir;
	}

	public static void setPcgenPreviewDir(final File aFile)
	{
		pcgenPreviewDir = aFile;
	}
	
	/**
	 * @deprecated Use ConfigurationSettings.getPreviewDir()
	 * @return
	 */
	public static File getPcgenPreviewDir()
	{
		return pcgenPreviewDir;
	}
	
	/**
	 * Sets the path to the portrait files.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setPortraitsPath(final File path)
	{
		portraitsPath = path;
	}

	/**
	 * @deprecated Use PCGenSettings.getPortraitsDir()
	 * @return
	 */
	public static File getPortraitsPath()
	{
		return portraitsPath;
	}

	public static void setPostExportCommandStandard(final String argPreference)
	{
		postExportCommandStandard = argPreference;
	}

	public static void setPostExportCommandPDF(final String argPreference)
	{
		postExportCommandPDF = argPreference;
	}

	public static String getPostExportCommandStandard()
	{
		return postExportCommandStandard;
	}

	public static String getPostExportCommandPDF()
	{
		return postExportCommandPDF;
	}

	public static void setPrereqFailColor(final int newColor)
	{
		prereqFailColor = newColor & 0x00FFFFFF;
	}

	public static int getPrereqFailColor()
	{
		return prereqFailColor;
	}

	public static String getPrereqFailColorAsHtmlStart()
	{
		final StringBuffer rString = new StringBuffer("<font color="); //$NON-NLS-1$

		if (getPrereqFailColor() != 0)
		{
			rString.append("\"#").append(Integer.toHexString(getPrereqFailColor())).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			rString.append("red"); //$NON-NLS-1$
		}

		rString.append('>');

		return rString.toString();
	}
	public static String getPrereqFailColorAsHtmlEnd()
	{
		return "</font>"; //$NON-NLS-1$
	}

	public static void setPrereqQualifyColor(final int newColor)
	{
		prereqQualifyColor = newColor & 0x00FFFFFF;
	}

	public static int getPrereqQualifyColor()
	{
		return prereqQualifyColor;
	}

	/**
	 * Output spells on standard PC output sheet?
	 * @param arg
	 **/
	public static void setPrintSpellsWithPC(final boolean arg)
	{
		printSpellsWithPC = arg;
	}

	public static boolean getPrintSpellsWithPC()
	{
		return printSpellsWithPC;
	}

	/**
	 * I guess only ROG can document this?
	 * @return TRUR if ROG, else FALSE
	 */
	public static boolean isROG()
	{
		return isROG;
	}

	public static void setRaceTab_ListMode(final int listMode)
	{
		raceTab_ListMode = listMode;
	}

	public static int getRaceTab_ListMode()
	{
		return raceTab_ListMode;
	}

	public static void setTemplateTab_ListMode(final int listMode)
	{
		templateTab_ListMode = listMode;
	}

	public static int getTemplateTab_ListMode()
	{
		return templateTab_ListMode;
	}

	public static void setTemplateSelTab_ListMode(final int listMode)
	{
		templateSelTab_ListMode = listMode;
	}

	public static int getTemplateSelTab_ListMode()
	{
		return templateSelTab_ListMode;
	}

	/**
	 * Set's the ruleCheckMap key to 'Y' or 'N'
	 * @param aKey
	 * @param aBool
	 **/
	public static void setRuleCheck(final String aKey, final boolean aBool)
	{
		String aVal = "N"; //$NON-NLS-1$

		if (aBool)
		{
			aVal = "Y"; //$NON-NLS-1$
		}

		ruleCheckMap.put(aKey, aVal);
	}

	/**
	 * Gets this PC's choice on a Rule
	 * @param aKey
	 * @return true or false
	 **/
	public static boolean getRuleCheck(final String aKey)
	{
		if (ruleCheckMap.containsKey(aKey))
		{
			final String aVal = ruleCheckMap.get(aKey);

			if (aVal.equals("Y")) //$NON-NLS-1$
			{
				return true;
			}
		}

		return false;
	}

	public static void setSaveCustomEquipment(final boolean aBool)
	{
		setSaveCustomInLst(aBool);
	}

	public static boolean getSaveCustomEquipment()
	{
		return isSaveCustomInLst();
	}

	/**
	 * save the outputsheet location with the PC?
	 * @param arg
	 **/
	public static void setSaveOutputSheetWithPC(final boolean arg)
	{
		saveOutputSheetWithPC = arg;
	}

	public static boolean getSaveOutputSheetWithPC()
	{
		return saveOutputSheetWithPC;
	}

	/**
	 * Sets the current HTML output sheet for a single character.
	 *
	 * @param  path  a string containing the path to the HTML output sheet
	 * @param aPC
	 */
	public static void setSelectedCharacterHTMLOutputSheet(final String path, final PlayerCharacter aPC)
	{
		if (getSaveOutputSheetWithPC() && (aPC != null))
		{
			aPC.setSelectedCharacterHTMLOutputSheet(path);
		}

		selectedCharacterHTMLOutputSheet = path;
	}

	/**
	 * Returns the current HTML output sheet for a single character.
	 *
	 * @return    the <code>selectedCharacterHTMLOutputSheet</code> property
	 * @param aPC
	 **/
	public static String getSelectedCharacterHTMLOutputSheet(final PlayerCharacter aPC)
	{
		if (getSaveOutputSheetWithPC() && (aPC != null))
		{
			if (aPC.getSelectedCharacterHTMLOutputSheet().length() > 0)
			{
				return aPC.getSelectedCharacterHTMLOutputSheet();
			}
		}

		return selectedCharacterHTMLOutputSheet;
	}

	/**
	 * Sets the current PDF output sheet for a single character.
	 *
	 * @param path  a string containing the path to the PDF output sheet
	 * @param aPC
	 */
	public static void setSelectedCharacterPDFOutputSheet(final String path, final PlayerCharacter aPC)
	{
		if (getSaveOutputSheetWithPC() && (aPC != null))
		{
			aPC.setSelectedCharacterPDFOutputSheet(path);
		}

		selectedCharacterPDFOutputSheet = path;
	}

	/**
	 * Returns the current PDF output sheet for a single character.
	 *
	 * @return    the <code>selectedCharacterPDFOutputSheet</code> property
	 * @param aPC
	 */
	public static String getSelectedCharacterPDFOutputSheet(final PlayerCharacter aPC)
	{
		if (getSaveOutputSheetWithPC() && (aPC != null))
		{
			if (aPC.getSelectedCharacterPDFOutputSheet().length() > 0)
			{
				return aPC.getSelectedCharacterPDFOutputSheet();
			}
		}

		return selectedCharacterPDFOutputSheet;
	}

	/**
	 * Sets the current EquipSet template.
	 *
	 * @param  path  a string containing the path to the template
	 **/
	public static void setSelectedEqSetTemplate(final String path)
	{
		selectedEqSetTemplate = path;
	}

	/**
	 * Returns the current EquipSet template.
	 *
	 * @return    the <code>selectedEqSetTemplate</code> property
	 **/
	public static String getSelectedEqSetTemplate()
	{
		return selectedEqSetTemplate;
	}

	public static String getSelectedEqSetTemplateName()
	{
		if (selectedEqSetTemplate.length() > 0)
		{
			final int i = selectedEqSetTemplate.lastIndexOf("\\"); //$NON-NLS-1$

			return selectedEqSetTemplate.substring(i + 1);
		}

		return selectedEqSetTemplate;
	}

	/**
	 * Sets the current party HTML template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedPartyHTMLOutputSheet(final String path)
	{
		selectedPartyHTMLOutputSheet = path;
	}

	/**
	 * Returns the current party HTML template.
	 *
	 * @return    the <code>selectedPartyHTMLOutputSheet</code> property
	 **/
	public static String getSelectedPartyHTMLOutputSheet()
	{
		return selectedPartyHTMLOutputSheet;
	}

	/**
	 * Sets the current party PDF template.
	 *
	 * @param  path  a string containing the path to the template
	 **/
	public static void setSelectedPartyPDFOutputSheet(final String path)
	{
		selectedPartyPDFOutputSheet = path;
	}

	/**
	 * Returns the current party PDF template.
	 *
	 * @return    the <code>selectedPartyPDFOutputSheet</code> property
	 **/
	public static String getSelectedPartyPDFOutputSheet()
	{
		return selectedPartyPDFOutputSheet;
	}

	/**
	 * Sets the current Spell output sheet
	 *
	 * @param  path  a string containing the path to the template
	 **/
	public static void setSelectedSpellSheet(final String path)
	{
		selectedSpellSheet = path;
	}

	/**
	 * Returns the current spell output sheet
	 *
	 * @return    the <code>selectedSpellSheet</code> property
	 **/
	public static String getSelectedSpellSheet()
	{
		return selectedSpellSheet;
	}

	public static String getSelectedSpellSheetName()
	{
		if (selectedSpellSheet.length() > 0)
		{
			final int i = selectedSpellSheet.lastIndexOf("\\"); //$NON-NLS-1$

			return selectedSpellSheet.substring(i + 1);
		}

		return selectedSpellSheet;
	}

	public static void setShowD20Info(final boolean aBool)
	{
		showD20InfoAtStart = aBool;
	}

	/**
	 * Sets whether the feats dialog should be shown at level up.
	 * NOTE: This function has been disabled as it interferes with class builds. 
	 * See bug #1502512 at 
	 * https://sourceforge.net/tracker/index.php?func=detail&aid=1502512&group_id=25576&atid=384719
	 *  
	 * @param argShowFeatDialogAtLevelUp Should the feats dialog be shown at level up?
	 */
	public static void setShowFeatDialogAtLevelUp(final boolean argShowFeatDialogAtLevelUp)
	{
		showFeatDialogAtLevelUp = true; //argShowFeatDialogAtLevelUp;
	}

	/**
	 * Returns whether the feats dialog should be shown at level up.
	 * @return true if the feats dialog should be shown at level up.
	 */
	public static boolean getShowFeatDialogAtLevelUp()
	{
		return showFeatDialogAtLevelUp;
	}

	/**
	 * Sets whether the hit point dialog should be shown at level up.
	 * @param argShowHPDialogAtLevelUp Should the hit point dialog be shown at level up?
	 */
	public static void setShowHPDialogAtLevelUp(final boolean argShowHPDialogAtLevelUp)
	{
		showHPDialogAtLevelUp = argShowHPDialogAtLevelUp;
	}

	/**
	 * Returns whether the hit point dialog should be shown at level up.
	 * @return true if the hit point dialog should be shown at level up.
	 */
	public static boolean getShowHPDialogAtLevelUp()
	{
		return showHPDialogAtLevelUp;
	}

	public static void setShowLicense(final boolean arg)
	{
		showOGLOnLoad = arg;
	}

	public static void setShowMature(final boolean arg)
	{
		showMatureOnLoad = arg;
	}

	public static void setShowSponsors(final boolean arg)
	{
		showSponsorsOnLoad = arg;
	}

	/**
	 * Sets whether the Stat dialog should be shown at level up.
	 * @param argShowStatDialogAtLevelUp Should the Stat dialog should be shown at level up?
	 */
	public static void setShowStatDialogAtLevelUp(final boolean argShowStatDialogAtLevelUp)
	{
		showStatDialogAtLevelUp = argShowStatDialogAtLevelUp;
	}

	/**
	 * Returns whether the Stat dialog should be shown at level up.
	 * @return true if the Stat dialog should be shown at level up.
	 */
	public static boolean getShowStatDialogAtLevelUp()
	{
		return showStatDialogAtLevelUp;
	}

	public static void setShowTipOfTheDay(final boolean argShowTipOfTheDay)
	{
		showTipOfTheDay = argShowTipOfTheDay;
	}

	public static boolean getShowTipOfTheDay()
	{
		return showTipOfTheDay;
	}

	/**
	 * Sets the argShowWarningAtFirstLevelUp.
	 * @param argShowWarningAtFirstLevelUp The argShowWarningAtFirstLevelUp to set
	 */
	public static void setShowWarningAtFirstLevelUp(final boolean argShowWarningAtFirstLevelUp)
	{
		SettingsHandler.showWarningAtFirstLevelUp = argShowWarningAtFirstLevelUp;
	}

	/**
	 * Returns the showWarningAtFirstLevelUp.
	 * @return boolean
	 */
	public static boolean isShowWarningAtFirstLevelUp()
	{
		return showWarningAtFirstLevelUp;
	}

	public static void setSingleChoicePreference(final int argPreference)
	{
		singleChoicePreference = argPreference;
	}

	public static int getSingleChoicePreference()
	{
		return singleChoicePreference;
	}

	public static void setSkillsTab_AvailableListMode(final int listMode)
	{
		skillsTab_AvailableListMode = listMode;
	}

	public static int getSkillsTab_AvailableListMode()
	{
		return skillsTab_AvailableListMode;
	}

	public static void setSkillsTab_IncludeSkills(final int anInt)
	{
		skillsTab_IncludeSkills = anInt;
	}

	public static int getSkillsTab_IncludeSkills()
	{
		return skillsTab_IncludeSkills;
	}

	public static void setSkillsTab_SelectedListMode(final int listMode)
	{
		skillsTab_SelectedListMode = listMode;
	}

	public static int getSkillsTab_SelectedListMode()
	{
		return skillsTab_SelectedListMode;
	}

	public static void setSkinLFThemePack(final String argSkinLFThemePack)
	{
		skinLFThemePack = argSkinLFThemePack;
	}

	public static String getSkinLFThemePack()
	{
		return skinLFThemePack;
	}

	public static void setSpellsTab_AvailableListMode(final int listMode)
	{
		spellsTab_AvailableListMode = listMode;
	}

	public static int getSpellsTab_AvailableListMode()
	{
		return spellsTab_AvailableListMode;
	}

	public static void setSpellsTab_SelectedListMode(final int listMode)
	{
		spellsTab_SelectedListMode = listMode;
	}

	public static int getSpellsTab_SelectedListMode()
	{
		return spellsTab_SelectedListMode;
	}

	public static void setTabPlacement(final int anInt)
	{
		tabPlacement = anInt;
	}

	public static int getTabPlacement()
	{
		return tabPlacement;
	}

	/**
	 * Returns the path to the temporary output location (for previews).
	 *
	 * @return    the <code>tempPath</code> property
	 */
	public static File getTempPath()
	{
		return tempPath;
	}

	public static void setToolBarShown(final boolean argShowToolBar)
	{
		setShowToolBar(argShowToolBar);
	}

	public static boolean isToolBarShown()
	{
		return isShowToolBar();
	}

	public static void setToolTipTextShown(final boolean showToolTipText)
	{
		toolTipTextShown = showToolTipText;

		final PCGen_Frame1 frame = PCGen_Frame1.getInst();

		// Guard against load order
		if (frame != null)
		{
			PCGen_Frame1.forceUpdate_PlayerTabs();
		}

		Utility.handleToolTipShownStateChange();
	}

	public static boolean isToolTipTextShown()
	{
		return toolTipTextShown;
	}

	public static void setUseFeatBenefits(final boolean arg)
	{
		useFeatBenefits = arg;
	}

	/**
	 * @return Returns the useHigherLevelSlotsDefault.
	 */
	public static boolean isUseHigherLevelSlotsDefault()
	{
		return useHigherLevelSlotsDefault;
	}

	/**
	 * @param useHigherLevelSlotsDefault The useHigherLevelSlotsDefault to set.
	 */
	public static void setUseHigherLevelSlotsDefault(
		boolean useHigherLevelSlotsDefault)
	{
		SettingsHandler.useHigherLevelSlotsDefault = useHigherLevelSlotsDefault;
	}

	public static void setUseWaitCursor(final boolean b)
	{
		useWaitCursor = b;
		PCGen_Frame1.useWaitCursor(b);
	}

	public static boolean getUseWaitCursor()
	{
		return useWaitCursor;
	}

	public static void setWantToLoadMasterworkAndMagic(final boolean bFlag)
	{
		wantToLoadMasterworkAndMagic = bFlag;
	}

	public static void setWeaponProfPrintout(final boolean argPreference)
	{
		weaponProfPrintout = argPreference;
	}

	public static boolean getWeaponProfPrintout()
	{
		return weaponProfPrintout;
	}

	public static boolean guiUsesOutputNameEquipment()
	{
		return guiUsesOutputNameEquipment;
	}

	public static boolean guiUsesOutputNameSpells()
	{
		return guiUsesOutputNameSpells;
	}

	/**
	 * Checks to see if the user has set a value for this key
	 * @param aKey
	 * @return true or false
	 **/
	public static boolean hasRuleCheck(final String aKey)
	{
		return ruleCheckMap.containsKey(aKey);
	}

	public static boolean hideMonsterClasses()
	{
		return hideMonsterClasses;
	}

	public static void readGUIOptionsProperties()
	{
		setNameDisplayStyle(getPCGenOption("nameDisplayStyle", Constants.DISPLAY_STYLE_NAME)); //$NON-NLS-1$

		// Calling setToolTipTextShown doesn't update menu checkbox state
		// toolTip state change, and menu checkbox state change are
		// handled in gui code pcGenGUI.java just after returning
		// from this method.
		if (((Globals.javaVersionMajor >= 1) && (Globals.javaVersionMinor >= 4))
			|| (!System.getProperty("os.name").substring(0, 3).equalsIgnoreCase("MAC"))) //$NON-NLS-1$ //$NON-NLS-2$
		//(! System.getProperty("os.name").substring(1,3).equalsIgnoreCase("LIN")))
		{
			setToolTipTextShown(getPCGenOption("toolTipTextShown", isToolTipTextShown())); //$NON-NLS-1$

			//System.out.println("Java Ver >= 1.4 || OS Name != MAC -- toolTip bug avoidance unnecessary");
		}
		else
		{
			setToolTipTextShown(getPCGenOption("toolTipTextShown", false)); //$NON-NLS-1$

			//System.out.println("Java Ver < 1.4 && OS Name = MAC -- Defaulting toolTips OFF -- MAC/Java 1.3 Bug");
		}

		// Menu stuff
		setOpenRecentPCs(getOpenRecentOption("openRecentPCs")); //$NON-NLS-1$
		setOpenRecentParties(getOpenRecentOption("openRecentParties")); //$NON-NLS-1$
	}

	/**
	 * Opens the options.ini
	 */
	public static void readOptionsProperties()
	{
		// read in the filepath.ini settings before anything else
		readFilePaths();

		// now get the Filter settings
		readFilterSettings();

		// Globals.getOptionsPath() will _always_ return a string
		final String optionsLocation = Globals.getOptionsPath();

		FileInputStream in = null;

		try
		{
			in = new FileInputStream(optionsLocation);
			getOptions().load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			Logging.debugPrint(LanguageBundle.getString("SettingsHandler.no.options.file")); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.options.file"), ex); //$NON-NLS-1$
			}
		}
	}

	/**
	 * retrieve filter settings
	 *
	 * <br>author: Thomas Behr 19-02-02
	 *
	 * @param optionName   the name of the property to retrieve
	 * @return filter settings
	 */
	public static String retrieveFilterSettings(final String optionName)
	{
		return getFilterSettings().getProperty("pcgen.filters." + optionName, //$NON-NLS-1$
			getOptions().getProperty("pcgen.filters." + optionName, "")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean showD20Info()
	{
		return showD20InfoAtStart;
	}

	public static boolean allowFeatDebugging()
	{
		return debugFeats;
	}

	public static boolean showLicense()
	{
		return showOGLOnLoad;
	}

	public static boolean showMature()
	{
		return showMatureOnLoad;
	}

	public static boolean showSponsors() {
		return showSponsorsOnLoad;
	}

	public static boolean showNaturalWeaponTab()
	{
		return showNatWeaponTab;
	}

	/**
	 * store the filter settings for a given Filterable
	 *
	 * <br>author: Thomas Behr 19-02-02
	 *
	 * @param filterable - the Filterable whose settings
	 *              will be stored
	 */
	public static void storeFilterSettings(final Filterable filterable)
	{
		final String name = filterable.getName();

		if (name == null)
		{
			return;
		}

		getFilterSettings().setProperty("pcgen.filters." + name + ".mode", Integer.toString(filterable.getFilterMode())); //$NON-NLS-1$ //$NON-NLS-2$
		getFilterSettings().setProperty("pcgen.filters." + name + ".available", //$NON-NLS-1$ //$NON-NLS-2$
			FilterFactory.filterListToString(filterable.getAvailableFilters()));
		getFilterSettings().setProperty("pcgen.filters." + name + ".selected", //$NON-NLS-1$ //$NON-NLS-2$
			FilterFactory.filterListToString(filterable.getSelectedFilters()));
		getFilterSettings().setProperty("pcgen.filters." + name + ".removed", //$NON-NLS-1$ //$NON-NLS-2$
			FilterFactory.filterListToString(filterable.getRemovedFilters()));
	}

	public static boolean useFeatBenefits()
	{
		return useFeatBenefits;
	}

	public static boolean wantToLoadMasterworkAndMagic()
	{
		return wantToLoadMasterworkAndMagic;
	}

	private static String getPropertiesFileHeader(final String description)
	{
		return "# Emacs, this is -*- java-properties-generic -*- mode."//$NON-NLS-1$
			+ Constants.LINE_SEPARATOR + "#" //$NON-NLS-2$
			+ Constants.LINE_SEPARATOR + description //$NON-NLS-1$
			+ Constants.LINE_SEPARATOR + "# Do not edit this file manually." //$NON-NLS-1$
			+ Constants.LINE_SEPARATOR;
	}

	/**
	 * Writes out filepaths.ini
	 **/
	public static void writeFilePaths()
	{
		final String fType = getFilePaths();
		final String header = getPropertiesFileHeader(
				"# filepaths.ini -- location of other .ini files set in pcgen");

		if (!fType.equals("pcgen") && !fType.equals("user") && !fType.equals("mac_user")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			if (fType != null)
			{
				setFilePaths(fType);
			}
			else if (getPcgenFilesDir() != null)
			{
				setFilePaths(getPcgenFilesDir().getAbsolutePath());
			}
		}

		// if it's the users home directory, we need to make sure
		// that the $HOME/.pcgen directory exists
		if (fType.equals("user")) //$NON-NLS-1$
		{
			final String aLoc = System.getProperty("user.home") + File.separator + ".pcgen"; //$NON-NLS-1$ //$NON-NLS-2$
			final File aFile = new File(aLoc);

			if (!aFile.exists())
			{
				// Directory doesn't exist, so create it
				aFile.mkdir();
				Logging.errorPrint(LanguageBundle.getFormattedString("SettingsHandler.dir.does.not.exist", aLoc)); //$NON-NLS-1$
			}
			else if (!aFile.isDirectory())
			{
				String notDir = LanguageBundle.getFormattedString(
					"SettingsHandler.is.not.a.directory", aLoc); //$NON-NLS-2$
				ShowMessageDelegate.showMessageDialog(
					notDir,
					Constants.APPLICATION_NAME,
					MessageType.ERROR);
			}
		}

		// if it's the standard Mac user directory, we need to make sure
		// that the $HOME/Library/Preferences/pcgen directory exists
		if (fType.equals("mac_user")) //$NON-NLS-1$
		{
			final String aLoc = Globals.defaultMacOptionsPath;
			final File aFile = new File(aLoc);

			if (!aFile.exists())
			{
				// Directory doesn't exist, so create it
				aFile.mkdir();
				Logging.errorPrint(LanguageBundle.getFormattedString("SettingsHandler.dir.does.not.exist", aLoc)); //$NON-NLS-1$
			}
			else if (!aFile.isDirectory())
			{
				String notDir = LanguageBundle.getFormattedString(
					"SettingsHandler.is.not.a.directory", aLoc); //$NON-NLS-2$
				ShowMessageDelegate.showMessageDialog(
					notDir,
					Constants.APPLICATION_NAME,
					MessageType.ERROR);
			}
		}

		FileOutputStream out = null;

		try
		{
			out = new FileOutputStream(fileLocation);
			getFilepathProp().store(out, header);
		}
		catch (FileNotFoundException fnfe)
		{
			final File f = new File(fileLocation);
			if (!f.canWrite()) {
				Logging.errorPrint(LanguageBundle.getFormattedString("SettingsHandler.filepaths.readonly", fileLocation)) ; //$NON-NLS-1$
			}
			else {
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.filepaths.write"), fnfe); //$NON-NLS-1$
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint(LanguageBundle.getString("SettingsHandler.filepaths.write"), e); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				// Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.filepaths.ini.write"), ex); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Opens (options.ini) for writing and calls {@link setOptionsProperties}.
	 * @param aPC
	 */
	public static void writeOptionsProperties(final PlayerCharacter aPC)
	{
		writeFilePaths();
		writeFilterSettings();

		// Globals.getOptionsPath() will _always_ return a string
		final String optionsLocation = Globals.getOptionsPath();
		final String header = getPropertiesFileHeader(
				"# options.ini -- options set in pcgen");

		// Make sure all the Properties are set
		setOptionsProperties(aPC);

		FileOutputStream out = null;

		try
		{
			out = new FileOutputStream(optionsLocation);
			getOptions().mystore(out, header);
		}
		catch (FileNotFoundException fnfe)
		{
			final File f = new File(fileLocation);
			if (!f.canWrite()) {
				Logging.errorPrint(LanguageBundle.getFormattedString("SettingsHandler.options.ini.read.only", optionsLocation)); //$NON-NLS-1$
			}
			else {
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.write.options.ini"), fnfe); //$NON-NLS-1$
			}
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				// Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.options.ini.write"), ex); //$NON-NLS-1$
			}
		}
	}

	static boolean isAutogenExoticMaterial()
	{
		return autogenExoticMaterial;
	}

	static boolean isAutogenMagic()
	{
		return autogenMagic;
	}

	static boolean isAutogenMasterwork()
	{
		return autogenMasterwork;
	}

	static boolean isAutogenRacial()
	{
		return autogenRacial;
	}

	static void setPreviewTabShown(final boolean showPreviewTab)
	{
		previewTabShown = showPreviewTab;
	}

	static boolean isPreviewTabShown()
	{
		return previewTabShown;
	}

	/** Sets whether 'automatic' class-granted feats can be turned in for other feats
	 * @param argAutoFeatsRefundable
	 */
	private static void setAutoFeatsRefundable(final boolean argAutoFeatsRefundable)
	{
		autoFeatsRefundable = argAutoFeatsRefundable;
	}

	/** Returns whether 'automatic' class-granted feats can be turned in for other feats
	 *  @return true if 'automatic' class-granted feats can be turned in for other feats
	 */
	private static boolean isAutoFeatsRefundable()
	{
		return autoFeatsRefundable;
	}

	private static void setAutogenExoticMaterial(final boolean aBool)
	{
		autogenExoticMaterial = aBool;
	}

	private static void setAutogenMagic(final boolean aBool)
	{
		autogenMagic = aBool;
	}

	private static void setAutogenMasterwork(final boolean aBool)
	{
		autogenMasterwork = aBool;
	}

	private static void setAutogenRacial(final boolean aBool)
	{
		autogenRacial = aBool;
	}

	private static Properties getFilterSettings()
	{
		return FILTERSETTINGS;
	}

	private static void setGrimHPMode(final boolean argGrimHPMode)
	{
		grimHPMode = argGrimHPMode;
	}

	private static boolean isGrimHPMode()
	{
		return grimHPMode;
	}

	private static void setGrittyACMode(final boolean aBool)
	{
		grittyACMode = aBool;
	}

	private static boolean isGrittyACMode()
	{
		return grittyACMode;
	}

	private static void setOpenRecentOption(final String optionName, final String[] strings)
	{
		String value = ""; //$NON-NLS-1$

		if (strings.length > 0)
		{
			value += strings[0];

			for (int i = 1; i < strings.length; ++i)
			{
				value += ("|" + strings[i]); //$NON-NLS-1$
			}
		}

		setPCGenOption(optionName, value);
	}

	private static String[] getOpenRecentOption(final String optionName)
	{
		final String value = getPCGenOption(optionName, ""); //$NON-NLS-1$

		if (value == null)
		{
			return Globals.EMPTY_STRING_ARRAY;
		}

		final StringTokenizer tok = new StringTokenizer(value, "|"); //$NON-NLS-1$
		final List<String> strings = new ArrayList<String>();

		while (tok.hasMoreTokens())
		{
			strings.add(tok.nextToken());
		}

		return strings.toArray(new String[strings.size()]);
	}

	private static void setOpenRecentPCs(final String[] strings)
	{
		PCGen_Frame1.getInst().setOpenRecentPCs(strings);
	}

	private static void setOpenRecentParties(final String[] strings)
	{
		PCGen_Frame1.getInst().setOpenRecentParties(strings);
	}

	private static int getOptionTabPlacement(final String optionName, final int defaultValue)
	{
		final String aString = getPCGenOption(optionName, convertTabPlacementToString(defaultValue));
		int iVal;

		try
		{
			iVal = Integer.parseInt(aString);

			switch (iVal)
			{
				case SwingConstants.TOP:
				case SwingConstants.LEFT:
				case SwingConstants.BOTTOM:
				case SwingConstants.RIGHT:
					break;

				default:
					iVal = defaultValue;

					break;
			}
		}
		catch (NumberFormatException exc)
		{
			if ("TOP".equals(aString)) //$NON-NLS-1$
			{
				iVal = SwingConstants.TOP;
			}
			else if ("LEFT".equals(aString)) //$NON-NLS-1$
			{
				iVal = SwingConstants.LEFT;
			}
			else if ("BOTTOM".equals(aString)) //$NON-NLS-1$
			{
				iVal = SwingConstants.BOTTOM;
			}
			else if ("RIGHT".equals(aString)) //$NON-NLS-1$
			{
				iVal = SwingConstants.RIGHT;
			}
			else
			{
				iVal = defaultValue;
			}
		}

		return iVal;
	}

	/**
	 * Puts all properties into the <code>Properties</code> object,
	 * (<code>options</code>). This is called by
	 * <code>writeOptionsProperties</code>, which then saves the
	 * <code>options</code> into a file.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 * @param optionName
	 * @param optionValue
	 */
	public static void setPCGenOption(final String optionName, final boolean optionValue)
	{
		setPCGenOption(optionName, optionValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setPCGenOption(final String optionName, final double optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	/**
	 * Set most of this objects static properties from the loaded <code>options</code>.
	 * Called by readOptionsProperties. Most of the static properties are
	 * set as a side effect, with the main screen size being returned.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 * @param optionName
	 * @param defaultValue
	 *
	 * @return the default <code>Dimension</code> to set the screen size to
	 */
	public static boolean getPCGenOption(final String optionName, final boolean defaultValue)
	{
		final String option = getPCGenOption(optionName, defaultValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	private static Double getPCGenOption(final String optionName, final double defaultValue)
	{
		return new Double(getPCGenOption(optionName, Double.toString(defaultValue)));
	}

	private static void setPcgenThemePackDir(final File aFile)
	{
		pcgenThemePackDir = aFile;
	}

	/**
	 * What does this do???
	 * @param ROG
	 */
	private static void setROG(final boolean ROG)
	{
		isROG = ROG;
	}

	private static void setRanStartingWizard(final boolean ran)
	{
		SettingsHandler.ranStartingWizard = ran;
	}

	/**
	 * Set's the RuleChecks in the options.ini file
	 * @param optionName
	 **/
	private static void setRuleChecksInOptions(final String optionName)
	{
		String value = ""; //$NON-NLS-1$

		for (Iterator<String> i = ruleCheckMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next();
			final String aVal = ruleCheckMap.get(aKey);

			if (value.length() == 0)
			{
				value = aKey + "|" + aVal; //$NON-NLS-1$
			}
			else
			{
				value += ("," + aKey + "|" + aVal); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		//setPCGenOption(optionName, value);
		getOptions().setProperty("pcgen.options." + optionName, value); //$NON-NLS-1$
	}

	private static void setSaveCustomInLst(final boolean aBool)
	{
		saveCustomInLst = aBool;
	}

	private static boolean isSaveCustomInLst()
	{
		return saveCustomInLst;
	}

	private static void setShowToolBar(final boolean argShowToolBar)
	{
		showToolBar = argShowToolBar;
	}

	private static boolean isShowToolBar()
	{
		return showToolBar;
	}

	public static void setShowSkillModifier(final boolean argShowSkillMod)
	{
		showSkillModifier = argShowSkillMod;
	}

	public static boolean getShowSkillModifier()
	{
		return showSkillModifier;
	}

	public static void setShowSkillRanks(final boolean argShowSkillRanks)
	{
		showSkillRanks = argShowSkillRanks;
	}

	public static boolean getShowSkillRanks()
	{
		return showSkillRanks;
	}

	private static void setSpellMarketPriceAdjusted(final boolean aBool)
	{
		spellMarketPriceAdjusted = aBool;
	}

	private static boolean isSpellMarketPriceAdjusted()
	{
		return spellMarketPriceAdjusted;
	}

	private static void setSummaryTabShown(final boolean showSummaryTab)
	{
		summaryTabShown = showSummaryTab;
	}

	private static boolean isSummaryTabShown()
	{
		return summaryTabShown;
	}

	private static String getTmpPath()
	{
		return tmpPath;
	}

	private static String convertTabPlacementToString(final int placement)
	{
		switch (placement)
		{
			case SwingConstants.BOTTOM:
				return "BOTTOM"; //$NON-NLS-1$

			case SwingConstants.LEFT:
				return "LEFT"; //$NON-NLS-1$

			case SwingConstants.RIGHT:
				return "RIGHT"; //$NON-NLS-1$

			case SwingConstants.TOP:default:
				return "TOP"; //$NON-NLS-1$
		}
	}

	/*
	 * If the path starts with an @ then it's a relative path
	 */
	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@")) //$NON-NLS-1$
		{
			path = System.getProperty("user.dir") + path.substring(1); //$NON-NLS-1$
		}

		return path;
	}

	/**
	 * Parse all the user selected RuleChecks out of the options.ini file
	 * of the form:
	 *  aKey|Y,bKey|N,cKey|Y
	 * @param aString
	 **/
	private static void parseRuleChecksFromOptions(final String aString)
	{
		if (aString.length() <= 0)
		{
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(aString, ","); //$NON-NLS-1$

		while (aTok.hasMoreTokens())
		{
			final String bs = aTok.nextToken();
			final StringTokenizer bTok = new StringTokenizer(bs, "|"); //$NON-NLS-1$
			final String aKey = bTok.nextToken();
			final String aVal = bTok.nextToken();
			ruleCheckMap.put(aKey, aVal);
		}
	}

	/**
	 * Opens the filepaths.ini file for reading
	 **/
	private static void readFilePaths()
	{
		FileInputStream in = null;

		try
		{
			in = new FileInputStream(fileLocation);
			getFilepathProp().load(in);
			String fType = SettingsHandler.getFilePaths();

			if ((fType == null) || (fType.length() < 1))
			{
				// make sure we have a default
				if (Globals.isMacPlatform)
				{
					fType = "mac_user"; //$NON-NLS-1$
				}
				else
				{
					fType = "user"; //$NON-NLS-1$
				}
			}

			if (fType.equals("pcgen")) //$NON-NLS-1$
			{
				setPcgenFilesDir(new File(System.getProperty("user.dir"))); //$NON-NLS-1$
			}
			else if (fType.equals("user")) //$NON-NLS-1$
			{
				setPcgenFilesDir(new File(System.getProperty("user.home") + File.separator + ".pcgen")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (fType.equals("mac_user")) //$NON-NLS-1$
			{
				setPcgenFilesDir(new File(Globals.defaultMacOptionsPath));
			}
			else
			{
				setPcgenFilesDir(new File(fType));
			}
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filepaths.ini")); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.filepaths.ini"), ex); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Opens the filter.ini file for reading
	 *
	 * <br>author: Thomas Behr 10-03-02
	 **/
	private static void readFilterSettings()
	{
		// Globals.getFilterPath() will _always_ return a string
		final String filterLocation = Globals.getFilterPath();

		FileInputStream in = null;

		try
		{
			in = new FileInputStream(filterLocation);
			getFilterSettings().load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filter.ini")); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.filter.ini"), ex); //$NON-NLS-1$
			}
		}
	}

	/*
	 * setup relative paths
	 */
	private static String retractRelativePath(String path)
	{
		final File userDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$

		if (path.startsWith(userDir.getAbsolutePath()))
		{
			path = "@" + path.substring(userDir.getAbsolutePath().length()); //$NON-NLS-1$
		}

		return path;
	}

	/**
	 * Opens the filter.ini file for writing
	 *
	 * <br>author: Thomas Behr 10-03-02
	 */
	private static void writeFilterSettings()
	{
		// Globals.getFilterPath() will _always_ return a string
		final String filterLocation = Globals.getFilterPath();
		final String header = getPropertiesFileHeader(
				"# filter.ini -- filters set in pcgen");

		FileOutputStream out = null;

		try
		{
			out = new FileOutputStream(filterLocation);
			getFilterSettings().store(out, header);
		}
		catch (FileNotFoundException fnfe)
		{
			final File f = new File(fileLocation);
			if (!f.canWrite()) {
				Logging.errorPrint(LanguageBundle.getFormattedString("SettingsHandler.filter.ini.readonly", filterLocation)); //$NON-NLS-1$
			}
			else {
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.write.filter.ini"), fnfe); //$NON-NLS-1$
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.write.filter.ini"), e); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				Logging.errorPrint(LanguageBundle.getString("SettingsHandler.can.not.close.filter.ini.write"), ex); //$NON-NLS-1$
			}
		}

		// remove old filter stuff!
		for (Iterator<Object> it = getOptions().keySet().iterator(); it.hasNext();)
		{
			if (((String) it.next()).startsWith("pcgen.filters.")) //$NON-NLS-1$
			{
				it.remove();
			}
		}
	}

	/**
	 * <p>Returns the window state.  This corresponds to the values returned/accepted
	 * by <code>Frame.getExtendedState</code> and <code>Frame.setExtendedState</code>.</p>
	 *
	 * @return Returns the windowState.
	 */
	public static int getWindowState()
	{
		return windowState;
	}

	/**
	 * Sets the window state.  This corresponds to the values returned/accepted
	 * by <code>Frame.getExtendedState</code> and <code>Frame.setExtendedState</code>.</p>
	 *
	 * @param argWindowState The argWindowState to set.
	 */
	public static void setWindowState(final int argWindowState)
	{
		SettingsHandler.windowState = argWindowState;
	}

	/**
	 * Shows the program memory use in the status bar if <code>true</code>.
	 *
	 * @return show memory setting for the status bar
	 */
	public static boolean isShowMemoryArea()
	{
		return showMemoryArea;
	}

	/**
	 * Toggles showing program memory use in the status bar if <code>true</code>.
	 * Updates the status bar when state changes.
	 *
	 * @param showMemoryArea <code>true</code> to show memory use
	 */
	public static void setShowMemoryArea(final boolean showMemoryArea)
	{
		PCGen_Frame1.showMemoryArea(
				SettingsHandler.showMemoryArea = showMemoryArea);
	}

	/**
	 * Shows character portrait preview in the file chooser if <code>true</code>.
	 *
	 * @return show portrait preview
	 */
	public static boolean isShowImagePreview()
	{
		return showImagePreview;
	}

	/**
	 * Toggles displaying the character portrait preview in the file chooser
	 *
	 * @param showImagePreview <code>true</code> to show portrait preview
	 */
	public static void setShowImagePreview(final boolean showImagePreview)
	{
		SettingsHandler.showImagePreview = showImagePreview;
	}

	/**
	 * @return The showSingleBoxPerBundle value.
	 */
	public static boolean getShowSingleBoxPerBundle()
	{
		return showSingleBoxPerBundle;
	}

	/**
	 * Set the showSingleBoxPerBundle value.
	 * 
	 * @param b The new showSingleBoxPerBundle value.
	 */
	public static void setShowSingleBoxPerBundle(boolean b)
	{
		showSingleBoxPerBundle = b;
	}

	private static boolean outputDeprecationMessages = true;
	
	public static boolean outputDeprecationMessages()
	{
		return outputDeprecationMessages;
	}

	public static void setOutputDeprecationMessages(boolean b)
	{
		outputDeprecationMessages = b;
	}

	private static boolean inputUnconstructedMessages = true;
	
	public static boolean inputUnconstructedMessages()
	{
		return inputUnconstructedMessages;
	}

	public static void setInputUnconstructedMessages(boolean b)
	{
		inputUnconstructedMessages = b;
	}

	/**
	 * @return the hiddenSources
	 */
	public static String getHiddenSources()
	{
		return hiddenSources;
	}

	/**
	 * @param hiddenSources the hiddenSources to set
	 */
	public static void setHiddenSources(String hiddenSources)
	{
		SettingsHandler.hiddenSources = hiddenSources;
	}

	/**
	 * @return the quickLaunchSources
	 */
	public static String getQuickLaunchSources()
	{
		return quickLaunchSources;
	}

	/**
	 * @param quickLaunchSources the quickLaunchSources to set
	 */
	public static void setQuickLaunchSources(String quickLaunchSources)
	{
		SettingsHandler.quickLaunchSources = quickLaunchSources;
	}

	/**
	 * @return the useAdvancedSourceSelect
	 */
	public static boolean useAdvancedSourceSelect()
	{
		return useAdvancedSourceSelect;
	}

	/**
	 * @param useAdvancedSourceSelect the useAdvancedSourceSelect to set
	 */
	public static void setUseAdvancedSourceSelect(boolean useAdvancedSourceSelect)
	{
		SettingsHandler.useAdvancedSourceSelect = useAdvancedSourceSelect;
	}
}
