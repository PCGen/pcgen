/*
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
 */
package pcgen.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * This class contains all settings-related code moved from Globals.java
 *
 * Should be cleaned up more.
 * 
 * <b>NB: This class is being gradually replaced with use of 
 * {@link pcgen.system.PropertyContext} and its children.</b>   
 *
 * @author jujutsunerd
 **/
public final class SettingsHandler
{
	private static boolean autoFeatsRefundable;
	private static boolean autogenExoticMaterial;
	private static boolean autogenMagic;
	private static boolean autogenMasterwork;
	private static boolean autogenRacial;
	public static boolean validateBonuses;

	//
	// For EqBuilder
	//
	private static int maxPotionSpellLevel = Constants.DEFAULT_MAX_POTION_SPELL_LEVEL;
	private static int maxWandSpellLevel   = Constants.DEFAULT_MAX_WAND_SPELL_LEVEL;
	private static boolean allowMetamagicInCustomizer;
	private static boolean spellMarketPriceAdjusted;

	// Map of RuleCheck keys and their settings
	private static final Map<String, String> ruleCheckMap = new HashMap<>();

	/** That browserPath is set to null is intentional. */
	private static String browserPath; //Intentional null

	/**
	 *  See @javax.swing.SwingConstants.
	 */
	private static int chaTabPlacement = SwingConstants.TOP;
	private static Dimension customizerDimension;
	private static Point customizerLeftUpperCorner;
	private static int customizerSplit1 = -1;
	private static int customizerSplit2 = -1;
	private static boolean enforceSpendingBeforeLevelUp;
	private static final Properties FILTERSETTINGS = new Properties();
	public static GameMode game = new GameMode("default");
	private static boolean grimHPMode;
	private static boolean grittyACMode;
	private static Dimension kitSelectorDimension;
	private static Point kitSelectorLeftUpperCorner;
	private static boolean useWaitCursor = true;
	private static boolean loadURLs;
	private static boolean hpMaxAtFirstLevel = true;
	private static boolean hpMaxAtFirstClassLevel = true;
	private static boolean hpMaxAtFirstPCClassLevelOnly = true;
	private static int hpRollMethod = Constants.HP_STANDARD;
	private static int hpPercent    = Constants.DEFAULT_HP_PERCENT;
	private static boolean ignoreMonsterHDCap;

	private static String invalidDmgText;
	private static String invalidToHitText;
	private static boolean gearTab_IgnoreCost;
	private static boolean gearTab_AutoResize;
	private static boolean gearTab_AllowDebt;
	private static int gearTab_SellRate = Constants.DEFAULT_GEAR_TAB_SELL_RATE;
	private static int gearTab_BuyRate  = Constants.DEFAULT_GEAR_TAB_BUY_RATE;
	private static boolean isROG;
	private static Point leftUpperCorner;
	private static int windowState = Frame.NORMAL;
	private static int looknFeel = 1; // default to Java L&F
	private static final Properties options = new Properties();
	private static final Properties filepaths = new Properties();
	private static final String fileLocation = Globals.getFilepathsPath();
	private static File pccFilesLocation;
	private static File backupPcgPath;
	private static boolean createPcgBackup = true;
	private static File portraitsPath = new File(Globals.getDefaultPath());
	private static final File pcgenSponsorDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "system" //$NON-NLS-1$
			+ File.separator + "sponsors"); //$NON-NLS-1$

	private static File gmgenPluginDir =
		new File(Globals.getDefaultPath()
			+ File.separator + "plugins"); //$NON-NLS-1$
	private static int prereqQualifyColor = Constants.DEFAULT_PREREQ_QUALIFY_COLOUR;
	private static int prereqFailColor    = Constants.DEFAULT_PREREQ_FAIL_COLOUR;
	private static boolean previewTabShown;

	/////////////////////////////////////////////////
	private static boolean saveCustomInLst;
	private static String selectedCharacterHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedCharacterPDFOutputSheet = ""; //$NON-NLS-1$
	private static boolean saveOutputSheetWithPC;
	private static boolean printSpellsWithPC = true;
	private static String selectedPartyHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedPartyPDFOutputSheet = ""; //$NON-NLS-1$
	private static String selectedEqSetTemplate = ""; //$NON-NLS-1$
	private static String selectedSpellSheet = ""; //$NON-NLS-1$
	private static boolean showFeatDialogAtLevelUp = true;
	private static boolean showHPDialogAtLevelUp = true;
	private static boolean showStatDialogAtLevelUp = true;
	private static boolean showToolBar = true;
	private static boolean showSkillModifier;
	private static boolean showSkillRanks;
	private static boolean showWarningAtFirstLevelUp = true;
	private static String skinLFThemePack;
	private static boolean alwaysOverwrite;
	private static String defaultOSType = ""; //$NON-NLS-1$

	private static int tabPlacement = SwingConstants.BOTTOM;
	private static final File tempPath;

	static
	{
		tempPath = new File(System.getProperty("java.io.tmpdir"));
	}

	private static boolean useHigherLevelSlotsDefault;
	private static boolean wantToLoadMasterworkAndMagic;
	private static int nameDisplayStyle = Constants.DISPLAY_STYLE_NAME;
	private static boolean weaponProfPrintout = Constants.DEFAULT_PRINTOUT_WEAPONPROF;
	private static String postExportCommandStandard = ""; //$NON-NLS-1$
	private static String postExportCommandPDF = ""; //$NON-NLS-1$
	private static boolean hideMonsterClasses;
	private static boolean guiUsesOutputNameEquipment;
	private static boolean guiUsesOutputNameSpells;
	private static int lastTipShown = -1;
	private static final boolean showMemoryArea = false;
	private static boolean showImagePreview = true;
	private static boolean showTipOfTheDay = true;
	private static boolean showSingleBoxPerBundle;

	private SettingsHandler()
	{
	}


	/**
	 * Method setAlwaysOverwrite sets the alwaysOverwrite of this SettingsHandler object.
	 * @param argAlwaysOverwrite the alwaysOverwrite of this SettingsHandler object.
	 *
	 */
	public static void setAlwaysOverwrite(final boolean argAlwaysOverwrite)
	{
		alwaysOverwrite = argAlwaysOverwrite;
	}

	/**
	 * Method getAlwaysOverwrite returns the alwaysOverwrite of this SettingsHandler
	 * object.
	 * @return the alwaysOverwrite (type boolean) of this SettingsHandler object.
	 */
	public static boolean getAlwaysOverwrite()
	{
		return alwaysOverwrite;
	}

	/**
	 * Method setAutogen ...
	 *
	 * @param idx of type int
	 * @param bFlag of type boolean
	 */
	public static void setAutogen(final int idx, final boolean bFlag)
	{
		switch (idx)
		{
			case Constants.AUTOGEN_RACIAL:
				autogenRacial = bFlag;

				break;

			case Constants.AUTOGEN_MASTERWORK:
				autogenMasterwork = bFlag;

				break;

			case Constants.AUTOGEN_MAGIC:
				autogenMagic = bFlag;

				break;

			case Constants.AUTOGEN_EXOTIC_MATERIAL:
				autogenExoticMaterial = bFlag;

				break;

			default:
				break;
		}
	}

	/**
	 * Method getAutogen ...
	 *
	 * @param idx of type int
	 * @return boolean
	 */
	public static boolean getAutogen(final int idx)
	{
		if (!wantToLoadMasterworkAndMagic())
		{
			switch (idx)
			{
				case Constants.AUTOGEN_RACIAL:
					return autogenRacial;

				case Constants.AUTOGEN_MASTERWORK:
					return autogenMasterwork;

				case Constants.AUTOGEN_MAGIC:
					return autogenMagic;

				case Constants.AUTOGEN_EXOTIC_MATERIAL:
					return autogenExoticMaterial;

				default:
					break;
			}
		}

		return false;
	}

	/**
	 * Method setChaTabPlacement sets the chaTabPlacement of this SettingsHandler object.
	 *
	 *   See @javax.swing.SwingConstants.
	 *
	 * @param argChaTabPlacement the chaTabPlacement of this SettingsHandler object.
	 *
	 */
	public static void setChaTabPlacement(final int argChaTabPlacement)
	{
		chaTabPlacement = argChaTabPlacement;
	}

	/**
	 * Method getChaTabPlacement returns the chaTabPlacement of this SettingsHandler
	 * object.
	 *
	 *   See @javax.swing.SwingConstants.
	 *
	 * @return the chaTabPlacement (type int) of this SettingsHandler object.
	 */
	public static int getChaTabPlacement()
	{
		return chaTabPlacement;
	}

	/**
	 * Method getCustomizerDimension returns the customizerDimension of this
	 * SettingsHandler object.
	 * @return the customizerDimension (type Dimension2D) of this SettingsHandler object.
	 */
	private static Dimension2D getCustomizerDimension()
	{
		return customizerDimension;
	}

	/**
	 * Method getCustomizerLeftUpperCorner returns the customizerLeftUpperCorner of this
	  * SettingsHandler object.
	 * @return the customizerLeftUpperCorner (type Point2D) of this SettingsHandler object.
	 */
	private static Point2D getCustomizerLeftUpperCorner()
	{
		return customizerLeftUpperCorner;
	}

	/**
	 * Method getEnforceSpendingBeforeLevelUp returns the enforceSpendingBeforeLevelUp
	 * of this SettingsHandler object.
	 * @return the enforceSpendingBeforeLevelUp (type boolean) of this SettingsHandler
	 * object.
	 */
	static boolean getEnforceSpendingBeforeLevelUp()
	{
		return enforceSpendingBeforeLevelUp;
	}

	/**
	 * Method setFilePaths sets the filePaths of this SettingsHandler object.
	 * @param aString the filePaths of this SettingsHandler object.
	 *
	 */
	private static void setFilePaths(final String aString)
	{
		filepaths.setProperty("pcgen.filepaths", aString); //$NON-NLS-1$
	}

	/**
	 * Method getFilePaths returns the filePaths of this SettingsHandler object.
	 * @return the filePaths (type String) of this SettingsHandler object.
	 */
	static String getFilePaths()
	{
		String def_type = "user";
		if (SystemUtils.IS_OS_MAC)
		{
			def_type = "mac_user";
		}
		return filepaths.getProperty("pcgen.filepaths", def_type); //$NON-NLS-1$
		// $NON-NLS-2$
	}

	/**
	 * Puts all properties into the {@code Properties} object,
	 * ({@code options}). This is called by
	 * {@code writeOptionsProperties}, which then saves the
	 * {@code options} into a file.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the {@code getProperty} method, and that this is
	 * why settings are stored as static properties of {@code Global},
	 * but converted into a {@code Properties} object for
	 * storage and retrieval.
	 * @param optionName
	 * @param optionValue
	 */
	public static void setGMGenOption(final String optionName, final boolean optionValue)
	{
		setGMGenOption(optionName, optionValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Method setGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type int
	 */
	public static void setGMGenOption(final String optionName, final int optionValue)
	{
		setGMGenOption(optionName, String.valueOf(optionValue));
	}

	/**
	 * Method setGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type double
	 */
	public static void setGMGenOption(final String optionName, final double optionValue)
	{
		setGMGenOption(optionName, String.valueOf(optionValue));
	}

	/**
	 * Method setGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type String
	 */
	public static void setGMGenOption(final String optionName, final String optionValue)
	{
		options.setProperty("gmgen.options." + optionName, optionValue); //$NON-NLS-1$
	}

	/**
	 * Set most of this objects static properties from the loaded {@code options}.
	 * Called by readOptionsProperties. Most of the static properties are
	 * set as a side effect, with the main screen size being returned.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the {@code getProperty} method, and that this is
	 * why settings are stored as static properties of {@code Global},
	 * but converted into a {@code Properties} object for
	 * storage and retrieval.
	 * @param optionName
	 * @param defaultValue
	 *
	 * @return the default {@code Dimension} to set the screen size to
	 */
	public static boolean getGMGenOption(final String optionName, final boolean defaultValue)
	{
		final String option = getGMGenOption(optionName, defaultValue ? "true" :
		"false"); //$NON-NLS-1$ //$NON-NLS-2$

		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Method getGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type int
	 * @return int
	 */
	public static int getGMGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getGMGenOption(optionName, String.valueOf(defaultValue)));
	}

	/**
	 * Method getGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type double
	 * @return Double
	 */
	public static Double getGMGenOption(final String optionName, final double defaultValue)
	{
		return new Double(getGMGenOption(optionName, Double.toString(defaultValue)));
	}

	/**
	 * Method getGMGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type String
	 * @return String
	 */
	public static String getGMGenOption(final String optionName, final String defaultValue)
	{
		return options.getProperty("gmgen.options." + optionName, defaultValue);
	}

	/**
	 * Method setGUIUsesOutputNameEquipment sets the GUIUsesOutputNameEquipment of this SettingsHandler object.
	 * @param argUseOutputNameEquipment the GUIUsesOutputNameEquipment of this SettingsHandler object.
	 *
	 */
	public static void setGUIUsesOutputNameEquipment(final boolean argUseOutputNameEquipment)
	{
		guiUsesOutputNameEquipment = argUseOutputNameEquipment;
	}

	/**
	 * Method setGUIUsesOutputNameSpells sets the GUIUsesOutputNameSpells of this SettingsHandler object.
	 * @param argUseOutputNameSpells the GUIUsesOutputNameSpells of this SettingsHandler
	  *                                 object.
	 *
	 */
	public static void setGUIUsesOutputNameSpells(final boolean argUseOutputNameSpells)
	{
		guiUsesOutputNameSpells = argUseOutputNameSpells;
	}

	/**
	 * Method setGame sets the game of this SettingsHandler object.
	 * @param g the game of this SettingsHandler object.
	 *
	 */
	public static void setGame(final String g)
	{
		final GameMode newMode = SystemCollections.getGameModeNamed(g);

		if (newMode != null)
		{
			game = newMode;
		}
		// new key for game mode specific options are pcgen.options.gameMode.X.optionName
		// but offer downward compatible support to read in old version for unitSet from 5.8.0


		String unitSetName = options.getProperty("pcgen.options.gameMode." +
						g + ".unitSetName",
				options.getProperty("pcgen.options.unitSetName." + g, game.getDefaultUnitSet()));
		if  (!game.selectUnitSet(unitSetName))
		{
			if  (!game.selectDefaultUnitSet())
			{
				game.selectUnitSet(Constants.STANDARD_UNITSET_NAME);
			}
		}
		game.setDefaultXPTableName(getPCGenOption("gameMode." + g + ".xpTableName", "")); //$NON-NLS-1$ //$NON-NLS-2$
		game.setDefaultCharacterType(getPCGenOption("gameMode." + g + ".characterType", "")); //$NON-NLS-1$ //$NON-NLS-2$
		
		AbilityCategory featTemplate = game.getFeatTemplate();
		if (featTemplate != null)
		{
			AbilityCategory.FEAT.copyFields(featTemplate);
		}
		getChosenCampaignFiles(game);
	}

	/**
	 * Method getGame returns the game of this SettingsHandler object.
	 * @return the game (type GameMode) of this SettingsHandler object.
	 */
	public static GameMode getGame()
	{
		return game;
	}

	/**
	 * Method getGearTab_AllowDebt returns the gearTab_AllowDebt of this SettingsHandler
	  * object.
	 * @return the gearTab_AllowDebt (type boolean) of this SettingsHandler object.
	 */
	static boolean getGearTab_AllowDebt()
	{
		return gearTab_AllowDebt;
	}

	/**
	 * Method setGearTab_BuyRate sets the gearTab_BuyRate of this SettingsHandler object.
	 * @param argBuyRate the gearTab_BuyRate of this SettingsHandler object.
	 *
	 */
	public static void setGearTab_BuyRate(final int argBuyRate)
	{
		gearTab_BuyRate = argBuyRate;
	}

	/**
	 * Method getGearTab_BuyRate returns the gearTab_BuyRate of this SettingsHandler
	 * object.
	 * @return the gearTab_BuyRate (type int) of this SettingsHandler object.
	 */
	public static int getGearTab_BuyRate()
	{
		return gearTab_BuyRate;
	}

	/**
	 * Method getGearTab_IgnoreCost returns the gearTab_IgnoreCost of this
	 * SettingsHandler object.
	 * @return the gearTab_IgnoreCost (type boolean) of this SettingsHandler object.
	 */
	static boolean getGearTab_IgnoreCost()
	{
		return gearTab_IgnoreCost;
	}

	/**
	 * Method setGearTab_SellRate sets the gearTab_SellRate of this SettingsHandler
	 * object.
	 * @param argSellRate the gearTab_SellRate of this SettingsHandler object.
	 *
	 */
	public static void setGearTab_SellRate(final int argSellRate)
	{
		gearTab_SellRate = argSellRate;
	}

	/**
	 * Method getGearTab_SellRate returns the gearTab_SellRate of this SettingsHandler
	 * object.
	 * @return the gearTab_SellRate (type int) of this SettingsHandler object.
	 */
	public static int getGearTab_SellRate()
	{
		return gearTab_SellRate;
	}

	/**
	 * Method getGmgenPluginDir returns the gmgenPluginDir of this SettingsHandler object.
	 * @return the gmgenPluginDir (type File) of this SettingsHandler object.
	 */
	public static File getGmgenPluginDir()
	{
		return gmgenPluginDir;
	}

	/**
	 * Method setHPMaxAtFirstLevel sets the HPMaxAtFirstLevel of this SettingsHandler
	 * object.
	 * @param aBool the HPMaxAtFirstLevel of this SettingsHandler object.
	 *
	 */
	public static void setHPMaxAtFirstLevel(final boolean aBool)
	{
		hpMaxAtFirstLevel = aBool;
	}

	/**
	 * Method isHPMaxAtFirstLevel returns the HPMaxAtFirstLevel of this SettingsHandler
	 * object.
	 * @return the HPMaxAtFirstLevel (type boolean) of this SettingsHandler object.
	 */
	public static boolean isHPMaxAtFirstLevel()
	{
		return hpMaxAtFirstLevel;
	}

	/**
	 * Method setHPMaxAtFirstClassLevel sets the HPMaxAtFirstClassLevel of this
	 * SettingsHandler object.
	 * @param aBool the HPMaxAtFirstClassLevel of this SettingsHandler object.
	 *
	 */
	public static void setHPMaxAtFirstClassLevel(final boolean aBool)
	{
		hpMaxAtFirstClassLevel = aBool;
	}

	/**
	 * Method isHPMaxAtFirstClassLevel returns the HPMaxAtFirstClassLevel of this
	 * SettingsHandler object.
	 * @return the HPMaxAtFirstClassLevel (type boolean) of this SettingsHandler object.
	 */
	public static boolean isHPMaxAtFirstClassLevel()
	{
		return hpMaxAtFirstClassLevel;
	}

	/**
	 * Method isHPMaxAtFirstPCClassLevelOnly returns the HPMaxAtFirstPCClassLevelOnly of this SettingsHandler object.
	 * @return the HPMaxAtFirstPCClassLevelOnly (type boolean) of this SettingsHandler
	 * object.
	 */
	public static boolean isHPMaxAtFirstPCClassLevelOnly()
	{
		return hpMaxAtFirstPCClassLevelOnly;
	}

	/**
	 * Method setHPPercent sets the HPPercent of this SettingsHandler object.
	 * @param argHPPct the HPPercent of this SettingsHandler object.
	 *
	 */
	public static void setHPPercent(final int argHPPct)
	{
		hpPercent = argHPPct;
	}

	/**
	 * Method getHPPercent returns the HPPercent of this SettingsHandler object.
	 * @return the HPPercent (type int) of this SettingsHandler object.
	 */
	public static int getHPPercent()
	{
		return hpPercent;
	}

	/**
	 * Method setHPRollMethod sets the HPRollMethod of this SettingsHandler object.
	 * @param aBool the HPRollMethod of this SettingsHandler object.
	 *
	 */
	public static void setHPRollMethod(final int aBool)
	{
		hpRollMethod = aBool;
	}

	/**
	 * Method getHPRollMethod returns the HPRollMethod of this SettingsHandler object.
	 * @return the HPRollMethod (type int) of this SettingsHandler object.
	 */
	public static int getHPRollMethod()
	{
		return hpRollMethod;
	}

	/**
	 * Method getHTMLOutputSheetPath returns the HTMLOutputSheetPath of this SettingsHandler object.
	 * @return the HTMLOutputSheetPath (type String) of this SettingsHandler object.
	 */
	public static String getHTMLOutputSheetPath()
	{
		if (selectedCharacterHTMLOutputSheet != null &&
				selectedCharacterHTMLOutputSheet.isEmpty()) //$NON-NLS-1$
		{
			return ConfigurationSettings.getOutputSheetsDir();
		}

		return new File(selectedCharacterHTMLOutputSheet).getParentFile()
		.getAbsolutePath();
	}

	/**
	 * Method setIgnoreMonsterHDCap sets the ignoreMonsterHDCap of this SettingsHandler object.
	 * @param argIgoreCap the ignoreMonsterHDCap of this SettingsHandler object.
	 *
	 */
	public static void setIgnoreMonsterHDCap(final boolean argIgoreCap)
	{
		ignoreMonsterHDCap = argIgoreCap;
	}

	/**
	 * Method isIgnoreMonsterHDCap returns the ignoreMonsterHDCap of this
	 * SettingsHandler object.
	 * @return the ignoreMonsterHDCap (type boolean) of this SettingsHandler object.
	 */
	public static boolean isIgnoreMonsterHDCap()
	{
		return ignoreMonsterHDCap;
	}

	/**
	 * @param string The invalidDmgText to set.
	 */
	public static void setInvalidDmgText(final String string)
	{
		SettingsHandler.invalidDmgText = string;
	}

	/**
	 * Method getInvalidDmgText returns the invalidDmgText of this SettingsHandler
	 * object.
	 * @return the invalidDmgText (type String) of this SettingsHandler object.
	 */
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

	/**
	 * Method getInvalidToHitText returns the invalidToHitText of this SettingsHandler
	 * object.
	 * @return the invalidToHitText (type String) of this SettingsHandler object.
	 */
	public static String getInvalidToHitText()
	{
		return invalidToHitText;
	}

	/**
	 * Method getKitSelectorDimension returns the kitSelectorDimension of this SettingsHandler object.
	 * @return the kitSelectorDimension (type Dimension2D) of this SettingsHandler
	 * object.
	 */
	private static Dimension2D getKitSelectorDimension()
	{
		return kitSelectorDimension;
	}

	/**
	 * Method getKitSelectorLeftUpperCorner returns the kitSelectorLeftUpperCorner of
	 * this SettingsHandler object.
	 * @return the kitSelectorLeftUpperCorner (type Point2D) of this SettingsHandler object.
	 */
	private static Point2D getKitSelectorLeftUpperCorner()
	{
		return kitSelectorLeftUpperCorner;
	}

	/**
	 * Method getLeftUpperCorner returns the leftUpperCorner of this SettingsHandler object.
	 * @return the leftUpperCorner (type Point2D) of this SettingsHandler object.
	 */
	private static Point2D getLeftUpperCorner()
	{
		return leftUpperCorner;
	}

	/**
	 * Method setLoadURLs sets the loadURLs of this SettingsHandler object.
	 * @param aBool the loadURLs of this SettingsHandler object.
	 *
	 */
	public static void setLoadURLs(final boolean aBool)
	{
		loadURLs = aBool;
	}

	/**
	 * Method isLoadURLs returns the loadURLs of this SettingsHandler object.
	 * @return the loadURLs (type boolean) of this SettingsHandler object.
	 */
	public static boolean isLoadURLs()
	{
		return loadURLs;
	}

	/**
	 * Method setMaxPotionSpellLevel sets the maxPotionSpellLevel of this
	 * SettingsHandler object.
	 * @param anInt the maxPotionSpellLevel of this SettingsHandler object.
	 *
	 */
	public static void setMaxPotionSpellLevel(final int anInt)
	{
		maxPotionSpellLevel = anInt;
	}

	/**
	 * Method getMaxPotionSpellLevel returns the maxPotionSpellLevel of this
	 * SettingsHandler object.
	 * @return the maxPotionSpellLevel (type int) of this SettingsHandler object.
	 */
	public static int getMaxPotionSpellLevel()
	{
		return maxPotionSpellLevel;
	}

	/**
	 * Method setMaxWandSpellLevel sets the maxWandSpellLevel of this SettingsHandler
	 * object.
	 * @param anInt the maxWandSpellLevel of this SettingsHandler object.
	 *
	 */
	public static void setMaxWandSpellLevel(final int anInt)
	{
		maxWandSpellLevel = anInt;
	}

	/**
	 * Method getMaxWandSpellLevel returns the maxWandSpellLevel of this SettingsHandler
	 * object.
	 * @return the maxWandSpellLevel (type int) of this SettingsHandler object.
	 */
	public static int getMaxWandSpellLevel()
	{
		return maxWandSpellLevel;
	}

	/**
	 * Method setMetamagicAllowedInEqBuilder sets the metamagicAllowedInEqBuilder of
	 * this SettingsHandler object.
	 * @param aBool the metamagicAllowedInEqBuilder of this SettingsHandler object.
	 *
	 */
	public static void setMetamagicAllowedInEqBuilder(final boolean aBool)
	{
		allowMetamagicInCustomizer = aBool;
	}

	/**
	 * Method isMetamagicAllowedInEqBuilder returns the metamagicAllowedInEqBuilder of
	 * this SettingsHandler object.
	 * @return the metamagicAllowedInEqBuilder (type boolean) of this SettingsHandler
	 * object.
	 */
	public static boolean isMetamagicAllowedInEqBuilder()
	{
		return allowMetamagicInCustomizer;
	}

	/**
	 * Method setNameDisplayStyle sets the nameDisplayStyle of this SettingsHandler
	 * object.
	 * @param style the nameDisplayStyle of this SettingsHandler object.
	 *
	 */
	public static void setNameDisplayStyle(final int style)
	{
		nameDisplayStyle = style;
	}

	/**
	 * Method getNameDisplayStyle returns the nameDisplayStyle of this SettingsHandler object.
	 * @return the nameDisplayStyle (type int) of this SettingsHandler object.
	 */
	public static int getNameDisplayStyle()
	{
		return nameDisplayStyle;
	}

	/**
	 * Method getOptions returns the options of this SettingsHandler object.
	 * @return the options (type Properties) of this SettingsHandler object.
	 */
	public static Properties getOptions()
	{
		return options;
	}

	/**
	 * Method getOptionsFromProperties ...
	 *
	 * @param aPC of type PlayerCharacter
	 * @return Dimension
	 */
	public static Dimension getOptionsFromProperties(final PlayerCharacter aPC)
	{
		Dimension d = new Dimension(0, 0);

		final String tempBrowserPath = getPCGenOption("browserPath", ""); //$NON-NLS-1$ //$NON-NLS-2$

		browserPath = null;
		if (!StringUtils.isEmpty(tempBrowserPath)) //$NON-NLS-1$
		{
			browserPath = tempBrowserPath;
		}

		leftUpperCorner =
				new Point(getPCGenOption("windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("windowLeftUpperCorner.Y", -1.0).intValue());


		SettingsHandler.windowState = getPCGenOption("windowState",Frame.NORMAL);

		Double dw = getPCGenOption("windowWidth", 0.0); //$NON-NLS-1$
		Double dh = getPCGenOption("windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(

				dh, 0.0))
		{
			final int width = Integer.parseInt(dw.toString().substring(0,
						Math.min(dw.toString().length(), dw.toString().lastIndexOf('.')))); //$NON-NLS-1$
			final int height = Integer.parseInt(dh.toString().substring(0,
						Math.min(dh.toString().length(), dh.toString().lastIndexOf('.')))); //$NON-NLS-1$
			d = new Dimension(width, height);
		}

		customizerLeftUpperCorner =
				new Point(getPCGenOption("customizer.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("customizer.windowLeftUpperCorner.Y", -1.0).intValue());
		dw = getPCGenOption("customizer.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("customizer.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(dh, 0.0))
		{
			customizerDimension = new Dimension(dw.intValue(), dh.intValue());
		}


		kitSelectorLeftUpperCorner =
				new Point(getPCGenOption("kitSelector.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("kitSelector.windowLeftUpperCorner.Y", -1.0).intValue());
		dw = getPCGenOption("kitSelector.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("kitSelector.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(

				dh, 0.0))
		{
			kitSelectorDimension = new Dimension(dw.intValue(), dh.intValue());
		}

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

		Globals.initCustColumnWidth(CoreUtility.split(options.getProperty("pcgen.options.custColumnWidth", ""), ',')); //$NON-NLS-1$ //$NON-NLS-2$

		loadURLs = getPCGenOption("loadURLs", false); //$NON-NLS-1$

		Globals.setSourceDisplay(SourceFormat.values()[getPCGenOption("sourceDisplay", SourceFormat.LONG.ordinal())]); //$NON-NLS-1$


		alwaysOverwrite = getPCGenOption("alwaysOverwrite", false);
		autoFeatsRefundable = getPCGenOption("autoFeatsRefundable", false);

		autogenExoticMaterial = getPCGenOption("autoGenerateExoticMaterial", false);

		autogenMagic = getPCGenOption("autoGenerateMagic", false);
		autogenMasterwork = getPCGenOption("autoGenerateMasterwork", false);

		autogenRacial = getPCGenOption("autoGenerateRacial", false);
		chaTabPlacement = getOptionTabPlacement("chaTabPlacement", SwingConstants.TOP);

		createPcgBackup = getPCGenOption("createPcgBackup", true);
		customizerSplit1 = getPCGenOption("customizer.split1", -1);
		customizerSplit2 = getPCGenOption("customizer.split2", -1);
		defaultOSType = getPCGenOption("defaultOSType", null);
		enforceSpendingBeforeLevelUp =
				getPCGenOption("enforceSpendingBeforeLevelUp", false);


		gearTab_AllowDebt = getPCGenOption("GearTab.allowDebt", false);
		gearTab_AutoResize = getPCGenOption("GearTab.autoResize", false);
		gearTab_BuyRate = buyRate;
		gearTab_IgnoreCost = getPCGenOption("GearTab.ignoreCost", false);
		gearTab_SellRate = sellRate;
		grimHPMode = getPCGenOption("grimHPMode", false);
		grittyACMode = getPCGenOption("grittyACMode", false);
		guiUsesOutputNameEquipment = getPCGenOption("GUIUsesOutputNameEquipment", false);

		guiUsesOutputNameSpells = getPCGenOption("GUIUsesOutputNameSpells", false);

		hideMonsterClasses = getPCGenOption("hideMonsterClasses", false);
		hpMaxAtFirstLevel = getPCGenOption("hpMaxAtFirstLevel", true);
		hpMaxAtFirstClassLevel = getPCGenOption("hpMaxAtFirstClassLevel", false);

		hpMaxAtFirstPCClassLevelOnly =
				getPCGenOption("hpMaxAtFirstPCClassLevelOnly", false);

		hpPercent = getPCGenOption("hpPercent", 100);
		hpRollMethod = getPCGenOption("hpRollMethod", Constants.HP_STANDARD);

		ignoreMonsterHDCap = getPCGenOption("ignoreMonsterHDCap", false);
		SettingsHandler.invalidDmgText =
				getPCGenOption("invalidDmgText", LanguageBundle.getString("SettingsHandler.114"));


		SettingsHandler.invalidToHitText =
				getPCGenOption("invalidToHitText", LanguageBundle.getString("SettingsHandler.114"));

		lastTipShown = getPCGenOption("lastTipOfTheDayTipShown", -1);
		looknFeel = getPCGenOption("looknFeel", 1);
		maxPotionSpellLevel = getPCGenOption("maxPotionSpellLevel", 3);
		maxWandSpellLevel = getPCGenOption("maxWandSpellLevel", 4);
		allowMetamagicInCustomizer = getPCGenOption("allowMetamagicInCustomizer", false);

		pccFilesLocation =
				new File(expandRelativePath(getPCGenOption("pccFilesLocation", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "data")));

		setPcgenOutputSheetDir(new File(expandRelativePath(options.getProperty("pcgen.files.pcgenOutputSheetDir", //$NON-NLS-1$
				System.getProperty("user.dir") + File.separator + "outputsheets")))); //$NON-NLS-1$ //$NON-NLS-2$


		gmgenPluginDir =
				new File(expandRelativePath(options.getProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
						System.getProperty("user.dir") + File.separator + "plugins")));


		backupPcgPath =
				new File(expandRelativePath(options.getProperty("pcgen.files.characters.backup", "")));


		portraitsPath =
				new File(expandRelativePath(options.getProperty("pcgen.files.portraits", //$NON-NLS-1$
						Globals.getDefaultPcgPath())));
		postExportCommandStandard = getPCGenOption("postExportCommandStandard", "");

		postExportCommandPDF = getPCGenOption("postExportCommandPDF", "");
		setPrereqFailColor(getPCGenOption("prereqFailColor", Color.red.getRGB())); //$NON-NLS-1$
		setPrereqQualifyColor(getPCGenOption("prereqQualifyColor", Color.black.getRGB())); //$NON-NLS-1$

		previewTabShown = getPCGenOption("previewTabShown", true);
		isROG = getPCGenOption("isROG", false);
		saveCustomInLst = getPCGenOption("saveCustomInLst", false);
		saveOutputSheetWithPC = getPCGenOption("saveOutputSheetWithPC", false);

		printSpellsWithPC = getPCGenOption("printSpellsWithPC", true);
		selectedSpellSheet =
				expandRelativePath(options.getProperty("pcgen.files.selectedSpellOutputSheet", ""));


		setSelectedCharacterHTMLOutputSheet(expandRelativePath(options.getProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
					"")), aPC); //$NON-NLS-1$
		setSelectedCharacterPDFOutputSheet(expandRelativePath(options.getProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
					"")), aPC); //$NON-NLS-1$
		selectedEqSetTemplate =
				expandRelativePath(options.getProperty("pcgen.files.selectedEqSetTemplate", ""));



		selectedPartyHTMLOutputSheet =
				expandRelativePath(options.getProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
					""));
		selectedPartyPDFOutputSheet =
				expandRelativePath(options.getProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
					""));
		setShowFeatDialogAtLevelUp(getPCGenOption("showFeatDialogAtLevelUp", true)); //$NON-NLS-1$

		showHPDialogAtLevelUp = getPCGenOption("showHPDialogAtLevelUp", true);

		showImagePreview = getPCGenOption("showImagePreview", true);
		showSingleBoxPerBundle = getPCGenOption("showSingleBoxPerBundle", false);

		outputDeprecationMessages = getPCGenOption("outputDeprecationMessages", true);
		inputUnconstructedMessages = getPCGenOption("inputUnconstructedMessages", false);

		showStatDialogAtLevelUp = getPCGenOption("showStatDialogAtLevelUp", true);

		showTipOfTheDay = getPCGenOption("showTipOfTheDay", true);
		showToolBar = getPCGenOption("showToolBar", true);
		showSkillModifier = getPCGenOption("showSkillModifier", true);
		showSkillRanks = getPCGenOption("showSkillRanks", true);
		SettingsHandler.showWarningAtFirstLevelUp =
				getPCGenOption("showWarningAtFirstLevelUp", true);


		skinLFThemePack = getPCGenOption("skinLFThemePack", "");

		spellMarketPriceAdjusted = getPCGenOption("spellMarketPriceAdjusted", false);

		tabPlacement = getOptionTabPlacement("tabPlacement", SwingConstants.BOTTOM);

		useHigherLevelSlotsDefault = getPCGenOption("useHigherLevelSlotsDefault", false);

		useWaitCursor = getPCGenOption("useWaitCursor", true);
		wantToLoadMasterworkAndMagic =
				getPCGenOption("loadMasterworkAndMagicFromLst", false);


		weaponProfPrintout = getPCGenOption("weaponProfPrintout",
			Constants.DEFAULT_PRINTOUT_WEAPONPROF);

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
				CoreUtility.split(options.getProperty(
					"pcgen.files.chosenCampaignSourcefiles." + gameMode.getName(), //$NON-NLS-1$
					""), ',');
		List<URI> uriList = new ArrayList<>(uriStringList.size());
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
	 * Method setOptionsProperties sets the optionsProperties of this SettingsHandler object.
	 * @param aPC the optionsProperties of this SettingsHandler object.
	 *
	 */
	public static void setOptionsProperties(final PlayerCharacter aPC)
	{
		if (backupPcgPath != null && !backupPcgPath.getPath().isEmpty())
		{
			options.setProperty("pcgen.files.characters.backup", retractRelativePath(


					backupPcgPath.getAbsolutePath())); //$NON-NLS-1$
		}
		else
		{
			options.setProperty("pcgen.files.characters.backup", ""); //$NON-NLS-1$
		}

		options.setProperty("pcgen.files.portraits", retractRelativePath(
				portraitsPath.getAbsolutePath())); //$NON-NLS-1$


		options.setProperty("pcgen.files.selectedSpellOutputSheet", retractRelativePath(
				selectedSpellSheet)); //$NON-NLS-1$

		options.setProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterHTMLOutputSheet(aPC)));
		options.setProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterPDFOutputSheet(aPC)));
		options.setProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(selectedPartyHTMLOutputSheet));
		options.setProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(selectedPartyPDFOutputSheet));
		options.setProperty("pcgen.files.selectedEqSetTemplate", retractRelativePath(


				selectedEqSetTemplate)); //$NON-NLS-1$

		options.setProperty("pcgen.files.chosenCampaignSourcefiles", //$NON-NLS-1$
			StringUtil.join(PersistenceManager.getInstance().getChosenCampaignSourcefiles(), ", "));


		options.setProperty("pcgen.options.custColumnWidth", StringUtil.join(Globals.getCustColumnWidth(), ", ")); //$NON-NLS-1$


		if (gmgenPluginDir != null)
		{
			options.setProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
				retractRelativePath(gmgenPluginDir.getAbsolutePath()));
		}
		else
		{
			options.setProperty("gmgen.files.gmgenPluginDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (browserPath != null)
		{
			setPCGenOption("browserPath", browserPath); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("browserPath", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (game != null)
		{
			setPCGenOption("game", game.getName()); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("game", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			setPCGenOption("skinLFThemePack", skinLFThemePack); //$NON-NLS-1$
		}
		catch (NullPointerException e)
		{
			//TODO: Should this really be ignored???  XXX
		}

		if (pccFilesLocation != null)
		{
			setPCGenOption("pccFilesLocation", retractRelativePath(pccFilesLocation.getAbsolutePath())); //$NON-NLS-1$
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

		setPCGenOption("windowState", windowState); //$NON-NLS-1$

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

		setRuleChecksInOptions(); //$NON-NLS-1$

		setPCGenOption("allowMetamagicInCustomizer", allowMetamagicInCustomizer); //$NON-NLS-1$

		setPCGenOption("alwaysOverwrite", alwaysOverwrite); //$NON-NLS-1$
		setPCGenOption("autoFeatsRefundable", autoFeatsRefundable); //$NON-NLS-1$
		setPCGenOption("autoGenerateExoticMaterial", autogenExoticMaterial); //$NON-NLS-1$

		setPCGenOption("autoGenerateMagic", autogenMagic); //$NON-NLS-1$
		setPCGenOption("autoGenerateMasterwork", autogenMasterwork); //$NON-NLS-1$
		setPCGenOption("autoGenerateRacial", autogenRacial); //$NON-NLS-1$
		setPCGenOption("chaTabPlacement", convertTabPlacementToString(chaTabPlacement)); //$NON-NLS-1$

		setPCGenOption("createPcgBackup", createPcgBackup); //$NON-NLS-1$
		setPCGenOption("customizer.split1", customizerSplit1); //$NON-NLS-1$
		setPCGenOption("customizer.split2", customizerSplit2); //$NON-NLS-1$
		setPCGenOption("defaultOSType", defaultOSType); //$NON-NLS-1$
		setPCGenOption("GearTab.allowDebt", gearTab_AllowDebt); //$NON-NLS-1$
		setPCGenOption("GearTab.autoResize", gearTab_AutoResize); //$NON-NLS-1$
		setPCGenOption("GearTab.buyRate", gearTab_BuyRate); //$NON-NLS-1$
		setPCGenOption("GearTab.ignoreCost", gearTab_IgnoreCost); //$NON-NLS-1$
		setPCGenOption("GearTab.sellRate", gearTab_SellRate); //$NON-NLS-1$
		setPCGenOption("grimHPMode", grimHPMode); //$NON-NLS-1$
		setPCGenOption("grittyACMode", grittyACMode); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameEquipment", guiUsesOutputNameEquipment()); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameSpells", guiUsesOutputNameSpells()); //$NON-NLS-1$
		setPCGenOption("hideMonsterClasses", hideMonsterClasses()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstLevel", hpMaxAtFirstLevel); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstClassLevel", hpMaxAtFirstClassLevel); //$NON-NLS-1$

		setPCGenOption("hpMaxAtFirstPCClassLevelOnly", hpMaxAtFirstPCClassLevelOnly); //$NON-NLS-1$

		setPCGenOption("hpPercent", hpPercent); //$NON-NLS-1$
		setPCGenOption("hpRollMethod", hpRollMethod); //$NON-NLS-1$
		setPCGenOption("ignoreMonsterHDCap", ignoreMonsterHDCap); //$NON-NLS-1$
		setPCGenOption("invalidDmgText", invalidDmgText); //$NON-NLS-1$
		setPCGenOption("invalidToHitText", invalidToHitText); //$NON-NLS-1$
		setPCGenOption("lastTipOfTheDayTipShown", lastTipShown); //$NON-NLS-1$
		setPCGenOption("loadMasterworkAndMagicFromLst", wantToLoadMasterworkAndMagic()); //$NON-NLS-1$
		setPCGenOption("loadURLs", loadURLs); //$NON-NLS-1$
		setPCGenOption("looknFeel", looknFeel); //$NON-NLS-1$
		setPCGenOption("maxPotionSpellLevel", maxPotionSpellLevel); //$NON-NLS-1$
		setPCGenOption("maxWandSpellLevel", maxWandSpellLevel); //$NON-NLS-1$
		setPCGenOption("nameDisplayStyle", nameDisplayStyle); //$NON-NLS-1$
		setPCGenOption("postExportCommandStandard", getPostExportCommandStandard()); //$NON-NLS-1$
		setPCGenOption("postExportCommandPDF", getPostExportCommandPDF()); //$NON-NLS-1$

		setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(prereqFailColor)); //$NON-NLS-1$ //$NON-NLS-2$

		setPCGenOption("prereqQualifyColor", "0x" + Integer.toHexString(prereqQualifyColor)); //$NON-NLS-1$ //$NON-NLS-2$

		setPCGenOption("previewTabShown", previewTabShown); //$NON-NLS-1$
		setPCGenOption("saveCustomInLst", saveCustomInLst); //$NON-NLS-1$
		setPCGenOption("saveOutputSheetWithPC", saveOutputSheetWithPC); //$NON-NLS-1$

		setPCGenOption("printSpellsWithPC", printSpellsWithPC); //$NON-NLS-1$
		setPCGenOption("showFeatDialogAtLevelUp", showFeatDialogAtLevelUp); //$NON-NLS-1$

		setPCGenOption("enforceSpendingBeforeLevelUp", enforceSpendingBeforeLevelUp); //$NON-NLS-1$

		setPCGenOption("showHPDialogAtLevelUp", showHPDialogAtLevelUp); //$NON-NLS-1$

		setPCGenOption("showMemoryArea", showMemoryArea); //$NON-NLS-1$
		setPCGenOption("showImagePreview", showImagePreview); //$NON-NLS-1$
		setPCGenOption("showStatDialogAtLevelUp", showStatDialogAtLevelUp); //$NON-NLS-1$

		setPCGenOption("showTipOfTheDay", showTipOfTheDay); //$NON-NLS-1$
		setPCGenOption("showToolBar", showToolBar); //$NON-NLS-1$
		setPCGenOption("showSkillModifier", showSkillModifier); //$NON-NLS-1$
		setPCGenOption("showSkillRanks", showSkillRanks); //$NON-NLS-1$
		setPCGenOption("showSingleBoxPerBundle", showSingleBoxPerBundle); //$NON-NLS-1$

		setPCGenOption("showWarningAtFirstLevelUp", showWarningAtFirstLevelUp); //$NON-NLS-1$
		setPCGenOption("sourceDisplay", Globals.getSourceDisplay().ordinal());
		//$NON-NLS-1$
		setPCGenOption("spellMarketPriceAdjusted", spellMarketPriceAdjusted);
		//$NON-NLS-1$
		setPCGenOption("tabPlacement", convertTabPlacementToString(tabPlacement)); //$NON-NLS-1$

		setPCGenOption("useHigherLevelSlotsDefault", useHigherLevelSlotsDefault);
		//$NON-NLS-1$
		setPCGenOption("useWaitCursor", useWaitCursor); //$NON-NLS-1$
		setPCGenOption("validateBonuses", validateBonuses); //$NON-NLS-1$
		setPCGenOption("weaponProfPrintout", getWeaponProfPrintout()); //$NON-NLS-1$
		setPCGenOption("outputDeprecationMessages", outputDeprecationMessages()); //$NON-NLS-1$
		setPCGenOption("inputUnconstructedMessages", inputUnconstructedMessages());
		//$NON-NLS-1$
	}

	/**
	 * Method setPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type int
	 */
	private static void setPCGenOption(final String optionName, final int optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	/**
	 * Method setPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type String
	 */
	public static void setPCGenOption(final String optionName, final String optionValue)
	{
		if (optionValue==null) {
			options.remove("pcgen.options." + optionName); //$NON-NLS-1$
		}
		else {
			options.setProperty("pcgen.options." + optionName, optionValue);
			//$NON-NLS-1$
		}
	}

	/**
	 * Method getPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type int
	 * @return int
	 */
	private static int getPCGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getPCGenOption(optionName, String.valueOf(defaultValue)));
	}

	/**
	 * Method getPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type String
	 * @return String
	 */
	public static String getPCGenOption(final String optionName, final String defaultValue)
	{
		return options.getProperty("pcgen.options." + optionName, defaultValue); //$NON-NLS-1$
	}

	/**
	 * Method getPDFOutputSheetPath returns the PDFOutputSheetPath of this SettingsHandler object.
	 * @return the PDFOutputSheetPath (type String) of this SettingsHandler object.
	 */
	public static String getPDFOutputSheetPath()
	{
		if (!StringUtils.isEmpty(selectedCharacterPDFOutputSheet)) //$NON-NLS-1$
		{
			return ConfigurationSettings.getOutputSheetsDir();
		}

		return new File(selectedCharacterPDFOutputSheet).getParentFile().getAbsolutePath();
	}

	/**
	 * Where to load the data (lst) files from
	 * @deprecated Use ConfigurationSettings.getPccFilesDir() instead.
	 * @return pcc files location
	 */
	@Deprecated
	public static File getPccFilesLocation()
	{
		return pccFilesLocation;
	}

	/**
	 * Ensures that the path specified exists.
	 *
	 * @param  path  the {@code File} representing the path
	 */
	public static void ensurePathExists(final File path)
	{
		if (path != null && !path.exists())
		{
			path.mkdirs();
		}
	}

	/**
	 * @deprecated 
	 * @return the sponsor directory
	 */
	@Deprecated
	public static File getPcgenSponsorDir()
	{
		return pcgenSponsorDir;
	}

	/**
	 * Method setPcgenOutputSheetDir sets the pcgenOutputSheetDir of this
	 * SettingsHandler object.
	 * @param aFile the pcgenOutputSheetDir of this SettingsHandler object.
	 *
	 */
	private static void setPcgenOutputSheetDir(final File aFile)
	{
	}

	/**
	 * @deprecated Use PCGenSettings.getPortraitsDir()
	 * @return the portraits directory
	 */
	@Deprecated
	public static File getPortraitsPath()
	{
		return portraitsPath;
	}

	/**
	 * Method setPostExportCommandStandard sets the postExportCommandStandard of this
	 * SettingsHandler object.
	 * @param argPreference the postExportCommandStandard of this SettingsHandler object.
	 *
	 */
	public static void setPostExportCommandStandard(final String argPreference)
	{
		postExportCommandStandard = argPreference;
	}

	/**
	 * Method setPostExportCommandPDF sets the postExportCommandPDF of this SettingsHandler object.
	 * @param argPreference the postExportCommandPDF of this SettingsHandler object.
	 *
	 */
	public static void setPostExportCommandPDF(final String argPreference)
	{
		postExportCommandPDF = argPreference;
	}

	/**
	 * Method getPostExportCommandStandard returns the postExportCommandStandard of this SettingsHandler object.
	 * @return the postExportCommandStandard (type String) of this SettingsHandler
	 * object.
	 */
	public static String getPostExportCommandStandard()
	{
		return postExportCommandStandard;
	}

	/**
	 * Method getPostExportCommandPDF returns the postExportCommandPDF of this
	 * SettingsHandler object.
	 * @return the postExportCommandPDF (type String) of this SettingsHandler object.
	 */
	public static String getPostExportCommandPDF()
	{
		return postExportCommandPDF;
	}

	/**
	 * Method setPrereqFailColor sets the prereqFailColor of this SettingsHandler object.
	 * @param newColor the prereqFailColor of this SettingsHandler object.
	 *
	 */
	private static void setPrereqFailColor(final int newColor)
	{
		prereqFailColor = newColor & 0x00FFFFFF;
	}

	/**
	 * Method getPrereqFailColorAsHtmlStart returns the prereqFailColorAsHtmlStart of this SettingsHandler object.
	 * @return the prereqFailColorAsHtmlStart (type String) of this SettingsHandler
	 * object.
	 */
	public static String getPrereqFailColorAsHtmlStart()
	{
		final StringBuilder rString = new StringBuilder("<font color="); //$NON-NLS-1$

		if (prereqFailColor == 0)
		{
			rString.append("red"); //$NON-NLS-1$
		}
		else
		{
			rString.append("\"#")
				   .append(Integer.toHexString(prereqFailColor))
				   .append('"'); //$NON-NLS-1$ //$NON-NLS-2$
		}

		rString.append('>');

		return rString.toString();
	}
	/**
	 * Method getPrereqFailColorAsHtmlEnd returns the prereqFailColorAsHtmlEnd of this SettingsHandler object.
	 * @return the prereqFailColorAsHtmlEnd (type String) of this SettingsHandler object.
	 */
	public static String getPrereqFailColorAsHtmlEnd()
	{
		return "</font>"; //$NON-NLS-1$
	}

	/**
	 * Method setPrereqQualifyColor sets the prereqQualifyColor of this SettingsHandler
	 * object.
	 * @param newColor the prereqQualifyColor of this SettingsHandler object.
	 *
	 */
	private static void setPrereqQualifyColor(final int newColor)
	{
		prereqQualifyColor = newColor & 0x00FFFFFF;
	}

	/**
	 * Output spells on standard PC output sheet?
	 * @param arg
	 **/
	public static void setPrintSpellsWithPC(final boolean arg)
	{
		printSpellsWithPC = arg;
	}

	/**
	 * Method getPrintSpellsWithPC returns the printSpellsWithPC of this SettingsHandler object.
	 * @return the printSpellsWithPC (type boolean) of this SettingsHandler object.
	 */
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

	/**
	 * Method getSaveOutputSheetWithPC returns the saveOutputSheetWithPC of this SettingsHandler object.
	 * @return the saveOutputSheetWithPC (type boolean) of this SettingsHandler object.
	 */
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
		if (saveOutputSheetWithPC && (aPC != null))
		{
			aPC.setSelectedCharacterHTMLOutputSheet(path);
		}

		selectedCharacterHTMLOutputSheet = path;
	}

	/**
	 * Returns the current HTML output sheet for a single character.
	 *
	 * @return    the {@code selectedCharacterHTMLOutputSheet} property
	 * @param aPC
	 **/
	public static String getSelectedCharacterHTMLOutputSheet(final PlayerCharacter aPC)
	{
		if (saveOutputSheetWithPC && (aPC != null))
		{
			if (!aPC.getSelectedCharacterHTMLOutputSheet().isEmpty())
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
		if (saveOutputSheetWithPC && (aPC != null))
		{
			aPC.setSelectedCharacterPDFOutputSheet(path);
		}

		selectedCharacterPDFOutputSheet = path;
	}

	/**
	 * Returns the current PDF output sheet for a single character.
	 *
	 * @return    the {@code selectedCharacterPDFOutputSheet} property
	 * @param aPC
	 */
	public static String getSelectedCharacterPDFOutputSheet(final PlayerCharacter aPC)
	{
		if (saveOutputSheetWithPC && (aPC != null))
		{
			if (!aPC.getSelectedCharacterPDFOutputSheet().isEmpty())
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
	 * @return    the {@code selectedEqSetTemplate} property
	 **/
	public static String getSelectedEqSetTemplate()
	{
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
	 * Sets the current party PDF template.
	 *
	 * @param  path  a string containing the path to the template
	 **/
	public static void setSelectedPartyPDFOutputSheet(final String path)
	{
		selectedPartyPDFOutputSheet = path;
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
	 * @return    the {@code selectedSpellSheet} property
	 **/
	public static String getSelectedSpellSheet()
	{
		return selectedSpellSheet;
	}

	/**
	 * Sets whether the feats dialog should be shown at level up.
	 * NOTE: This function has been disabled as it interferes with class builds.
	 * @see <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1502512&group_id=25576&atid=384719">#1502512</a>
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

	/**
	 * Sets whether the Stat dialog should be shown at level up.
	 * @param argShowStatDialogAtLevelUp Should the Stat dialog should be shown at level
	  *                                     up?
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

	/**
	 * Method setTabPlacement sets the tabPlacement of this SettingsHandler object.
	 *
	 *   See @javax.swing.SwingConstants
	 *
	 * @param anInt the tabPlacement of this SettingsHandler object.
	 *
	 */
	public static void setTabPlacement(final int anInt)
	{
		tabPlacement = anInt;
	}

	/**
	 * Method getTabPlacement returns the tabPlacement of this SettingsHandler object.
	 *
	 *   See @javax.swing.SwingConstants
	 *
	 * @return the tabPlacement (type int) of this SettingsHandler object.
	 */
	public static int getTabPlacement()
	{
		return tabPlacement;
	}

	/**
	 * Returns the path to the temporary output location (for previews).
	 *
	 * @return    the {@code tempPath} property
	 */
	public static File getTempPath()
	{
		return tempPath;
	}

	/**
	 * @return Returns the useHigherLevelSlotsDefault.
	 */
	static boolean isUseHigherLevelSlotsDefault()
	{
		return useHigherLevelSlotsDefault;
	}

	/**
	 * Method setWantToLoadMasterworkAndMagic sets the wantToLoadMasterworkAndMagic of this SettingsHandler object.
	 * @param bFlag the wantToLoadMasterworkAndMagic of this SettingsHandler object.
	 *
	 */
	public static void setWantToLoadMasterworkAndMagic(final boolean bFlag)
	{
		wantToLoadMasterworkAndMagic = bFlag;
	}

	/**
	 * Method setWeaponProfPrintout sets the weaponProfPrintout of this SettingsHandler object.
	 * @param argPreference the weaponProfPrintout of this SettingsHandler object.
	 *
	 */
	public static void setWeaponProfPrintout(final boolean argPreference)
	{
		weaponProfPrintout = argPreference;
	}

	/**
	 * Method getWeaponProfPrintout returns the weaponProfPrintout of this SettingsHandler object.
	 * @return the weaponProfPrintout (type boolean) of this SettingsHandler object.
	 */
	public static boolean getWeaponProfPrintout()
	{
		return weaponProfPrintout;
	}

	/**
	 * Method guiUsesOutputNameEquipment ...
	 * @return boolean
	 */
	public static boolean guiUsesOutputNameEquipment()
	{
		return guiUsesOutputNameEquipment;
	}

	/**
	 * Method guiUsesOutputNameSpells ...
	 * @return boolean
	 */
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

	/**
	 * Method hideMonsterClasses ...
	 * @return boolean
	 */
	public static boolean hideMonsterClasses()
	{
		return hideMonsterClasses;
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
			options.load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle
					.getString("SettingsHandler.no.options.file")); //$NON-NLS-1$
			}
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
	 * Method wantToLoadMasterworkAndMagic ...
	 * @return boolean
	 */
	private static boolean wantToLoadMasterworkAndMagic()
	{
		return wantToLoadMasterworkAndMagic;
	}

	/**
	 * Method getPropertiesFileHeader ...
	 *
	 * @param description of type String
	 * @return String
	 */
	private static String getPropertiesFileHeader(final String description)
	{
		return "# Emacs, this is -*- java-properties-generic -*- mode."//$NON-NLS-1$
			+ Constants.LINE_SEPARATOR + '#' //$NON-NLS-2$
			+ Constants.LINE_SEPARATOR + description //$NON-NLS-1$
			+ Constants.LINE_SEPARATOR + "# Do not edit this file manually." //$NON-NLS-1$
			+ Constants.LINE_SEPARATOR;
	}

	/**
	 * Writes out filepaths.ini
	 **/
	private static void writeFilePaths()
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
		}


		if (fType.equals("user") || fType.equals("mac_user")) //$NON-NLS-1$
		{
			final String aLoc;
			if (fType.equals("user"))
			{
				// if it's the users home directory, we need to make sure
				// that the $HOME/.pcgen directory exists
				aLoc = System.getProperty("user.home") + File.separator + ".pcgen";
			}
			else
			{
				// if it's the standard Mac user directory, we need to make sure
				// that the $HOME/Library/Preferences/pcgen directory exists
				aLoc = Globals.defaultMacOptionsPath;
			}

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
			filepaths.store(out, header);
		}
		catch (FileNotFoundException fnfe)
		{
			final File f = new File(fileLocation);
			if (f.canWrite())
			{
				Logging.errorPrint(LanguageBundle.getString(
						"SettingsHandler.filepaths.write"), fnfe); //$NON-NLS-1$
			}
			else
			{
				Logging.errorPrint(LanguageBundle.getFormattedString(
						"SettingsHandler.filepaths.readonly",
						fileLocation
				)); //$NON-NLS-1$
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
	 * Method isAutogenExoticMaterial returns the autogenExoticMaterial of this
	 * SettingsHandler object.
	 * @return the autogenExoticMaterial (type boolean) of this SettingsHandler object.
	 */
	static boolean isAutogenExoticMaterial()
	{
		return autogenExoticMaterial;
	}

	/**
	 * Method isAutogenMagic returns the autogenMagic of this SettingsHandler object.
	 * @return the autogenMagic (type boolean) of this SettingsHandler object.
	 */
	static boolean isAutogenMagic()
	{
		return autogenMagic;
	}

	/**
	 * Method isAutogenMasterwork returns the autogenMasterwork of this SettingsHandler object.
	 * @return the autogenMasterwork (type boolean) of this SettingsHandler object.
	 */
	static boolean isAutogenMasterwork()
	{
		return autogenMasterwork;
	}

	/**
	 * Method isAutogenRacial returns the autogenRacial of this SettingsHandler object.
	 * @return the autogenRacial (type boolean) of this SettingsHandler object.
	 */
	static boolean isAutogenRacial()
	{
		return autogenRacial;
	}

	/**
	 * Method setPreviewTabShown sets the previewTabShown of this SettingsHandler object.
	 * @param showPreviewTab the previewTabShown of this SettingsHandler object.
	 *
	 */
	static void setPreviewTabShown(final boolean showPreviewTab)
	{
		previewTabShown = showPreviewTab;
	}

	/**
	 * Method isPreviewTabShown returns the previewTabShown of this SettingsHandler object.
	 * @return the previewTabShown (type boolean) of this SettingsHandler object.
	 */
	static boolean isPreviewTabShown()
	{
		return previewTabShown;
	}

	/**
	 * Method getOptionTabPlacement ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type int
	 * @return int
	 */
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
	 * Puts all properties into the {@code Properties} object,
	 * ({@code options}). This is called by
	 * {@code writeOptionsProperties}, which then saves the
	 * {@code options} into a file.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the {@code getProperty} method, and that this is
	 * why settings are stored as static properties of {@code Global},
	 * but converted into a {@code Properties} object for
	 * storage and retrieval.
	 * @param optionName
	 * @param optionValue
	 */
	private static void setPCGenOption(final String optionName, final boolean optionValue)
	{
		setPCGenOption(optionName, optionValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Method setPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param optionValue of type double
	 */
	private static void setPCGenOption(final String optionName, final double optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	/**
	 * Set most of this objects static properties from the loaded {@code options}.
	 * Called by readOptionsProperties. Most of the static properties are
	 * set as a side effect, with the main screen size being returned.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the {@code getProperty} method, and that this is
	 * why settings are stored as static properties of {@code Global},
	 * but converted into a {@code Properties} object for
	 * storage and retrieval.
	 * @param optionName
	 * @param defaultValue
	 *
	 * @return the default {@code Dimension} to set the screen size to
	 */
	public static boolean getPCGenOption(final String optionName, final boolean defaultValue)
	{
		final String option = getPCGenOption(optionName, defaultValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Method getPCGenOption ...
	 *
	 * @param optionName of type String
	 * @param defaultValue of type double
	 * @return Double
	 */
	private static Double getPCGenOption(final String optionName, final double defaultValue)
	{
		return new Double(getPCGenOption(optionName, Double.toString(defaultValue)));
	}

	/**
	 * Set's the RuleChecks in the options.ini file
	 **/
	private static void setRuleChecksInOptions()
	{
		String value = ""; //$NON-NLS-1$

		for (final Entry<String, String> stringStringEntry : ruleCheckMap.entrySet())
		{
			final String aVal = stringStringEntry.getValue();

			if (value.isEmpty())
			{
				value = stringStringEntry.getKey() + '|' + aVal; //$NON-NLS-1$
			}
			else
			{
				value += (',' + stringStringEntry.getKey() + '|' + aVal); //$NON-NLS-1$
				// $NON-NLS-2$
			}
		}

		//setPCGenOption(optionName, value);
		options.setProperty("pcgen.options." + "ruleChecks", value); //$NON-NLS-1$
	}

	/**
	 * Method convertTabPlacementToString ...
	 *
	 * @param placement of type int
	 * @return String
	 */
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

	/**
	 * Method expandRelativePath ...
	 *
	 * @param path of type String
	 * @return String
	 */
 /*
	 * If the path starts with an @ then it's a relative path
	 */
	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@")) //$NON-NLS-1$
		{
			return System.getProperty("user.dir") + path.substring(1); //$NON-NLS-1$
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
			filepaths.load(in);
			String fType = getFilePaths();
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle
					.getString("SettingsHandler.will.create.filepaths.ini")); //$NON-NLS-1$
			}
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
	 **/
	private static void readFilterSettings()
	{
		// Globals.getFilterPath() will _always_ return a string
		final String filterLocation = Globals.getFilterPath();

		FileInputStream in = null;

		try
		{
			in = new FileInputStream(filterLocation);
			FILTERSETTINGS.load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle
					.getString("SettingsHandler.will.create.filter.ini")); //$NON-NLS-1$
			}
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

	/**
	 * Method retractRelativePath ...
	 *
	 * @param path of type String
	 * @return String
	 */
 /*
	 * setup relative paths
	 */
	private static String retractRelativePath(String path)
	{
		final File userDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$

		if (path.startsWith(userDir.getAbsolutePath()))
		{
			return '@' + path.substring(userDir.getAbsolutePath().length()); //$NON-NLS-1$
		}

		return path;
	}

	/**
	 * Opens the filter.ini file for writing
	 */
	private static void writeFilterSettings()
	{
		final String filterLocation = Globals.getFilterPath();
		final String header = getPropertiesFileHeader(
				"# filter.ini -- filters set in pcgen");

		FileOutputStream out = null;

		try
		{
			out = new FileOutputStream(filterLocation);
			FILTERSETTINGS.store(out, header);
		}
		catch (FileNotFoundException fnfe)
		{
			final File f = new File(fileLocation);
			if (f.canWrite())
			{
				Logging.errorPrint(LanguageBundle.getString(
						"SettingsHandler.can.not.write.filter.ini"), fnfe); //$NON-NLS-1$
			}
			else
			{
				Logging.errorPrint(LanguageBundle.getFormattedString(
						"SettingsHandler.filter.ini.readonly",
						filterLocation
				)); //$NON-NLS-1$
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
		options.keySet().removeIf(o -> ((String) o).startsWith("pcgen.filters."));
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
	
	/**
	 * Method outputDeprecationMessages ...
	 * @return boolean
	 */
	public static boolean outputDeprecationMessages()
	{
		return outputDeprecationMessages;
	}

	/**
	 * Method setOutputDeprecationMessages sets the outputDeprecationMessages of this SettingsHandler object.
	 * @param b the outputDeprecationMessages of this SettingsHandler object.
	 *
	 */
	public static void setOutputDeprecationMessages(boolean b)
	{
		outputDeprecationMessages = b;
	}

	private static boolean inputUnconstructedMessages = true;
	
	/**
	 * Method inputUnconstructedMessages ...
	 * @return boolean
	 */
	public static boolean inputUnconstructedMessages()
	{
		return inputUnconstructedMessages;
	}

	/**
	 * Method setInputUnconstructedMessages sets the inputUnconstructedMessages of this SettingsHandler object.
	 * @param b the inputUnconstructedMessages of this SettingsHandler object.
	 *
	 */
	public static void setInputUnconstructedMessages(boolean b)
	{
		inputUnconstructedMessages = b;
	}

}
