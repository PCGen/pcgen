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
import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.SortedProperties;
import pcgen.persistence.PersistenceManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * Should be cleaned up more.
 * 
 * <b>NB: This class is being gradually replaced with use of 
 * {@link pcgen.system.PropertyContext} and its children.</b>   
 *
 **/
public final class SettingsHandler
{
	private static boolean autoFeatsRefundable = false;
	private static boolean autogenExoticMaterial = false;
	private static boolean autogenMagic = false;
	private static boolean autogenMasterwork = false;
	private static boolean autogenRacial = false;
	public static boolean validateBonuses = false;

	//
	// For EqBuilder
	//
	private static int maxPotionSpellLevel = Constants.DEFAULT_MAX_POTION_SPELL_LEVEL;
	private static int maxWandSpellLevel = Constants.DEFAULT_MAX_WAND_SPELL_LEVEL;
	private static boolean allowMetamagicInCustomizer = false;
	private static boolean spellMarketPriceAdjusted = false;

	// Map of RuleCheck keys and their settings
	private static final Map<String, String> ruleCheckMap = new HashMap<>();

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
	private static boolean enforceSpendingBeforeLevelUp = false;
	private static final Properties FILTERSETTINGS = new Properties();
	public static GameMode game = new GameMode("default");
	private static boolean grimHPMode = false;
	private static boolean grittyACMode = false;
	private static Dimension kitSelectorDimension = null;
	private static Point kitSelectorLeftUpperCorner = null;
	private static boolean useWaitCursor = true;
	private static boolean loadURLs = false;
	private static boolean hpMaxAtFirstLevel = true;
	private static boolean hpMaxAtFirstClassLevel = true;
	private static boolean hpMaxAtFirstPCClassLevelOnly = true;
	private static int hpRollMethod = Constants.HP_STANDARD;
	private static int hpPercent = Constants.DEFAULT_HP_PERCENT;
	private static boolean ignoreMonsterHDCap = false;

	private static String invalidDmgText;
	private static String invalidToHitText;
	private static boolean gearTab_IgnoreCost = false;
	private static boolean gearTab_AutoResize = false;
	private static boolean gearTab_AllowDebt = false;
	private static int gearTab_SellRate = Constants.DEFAULT_GEAR_TAB_SELL_RATE;
	private static int gearTab_BuyRate = Constants.DEFAULT_GEAR_TAB_BUY_RATE;
	private static boolean isROG = false;
	private static Point leftUpperCorner = null;
	private static int windowState = Frame.NORMAL;
	private static int looknFeel = 1; // default to Java L&F
	private static final SortedProperties OPTIONS = new SortedProperties();
	private static final Properties FILEPATHS = new Properties();
	private static final String FILE_LOCATION = Globals.getFilepathsPath();
	private static File pccFilesLocation = null;
	private static File backupPcgPath = null;
	private static boolean createPcgBackup = true;
	private static File portraitsPath = new File(Globals.getDefaultPath());

	private static File gmgenPluginDir = new File(Globals.getDefaultPath() + File.separator + "plugins"); //$NON-NLS-1$
	private static int prereqQualifyColor = Constants.DEFAULT_PREREQ_QUALIFY_COLOUR;
	private static int prereqFailColor = Constants.DEFAULT_PREREQ_FAIL_COLOUR;
	private static boolean previewTabShown = false;

	/////////////////////////////////////////////////
	private static boolean saveCustomInLst = false;
	private static String selectedCharacterHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedCharacterPDFOutputSheet = ""; //$NON-NLS-1$
	private static boolean saveOutputSheetWithPC = false;
	private static boolean printSpellsWithPC = true;
	private static String selectedPartyHTMLOutputSheet = ""; //$NON-NLS-1$
	private static String selectedPartyPDFOutputSheet = ""; //$NON-NLS-1$
	private static String selectedEqSetTemplate = ""; //$NON-NLS-1$
	private static String selectedSpellSheet = ""; //$NON-NLS-1$
	private static boolean showHPDialogAtLevelUp = true;
	private static boolean showStatDialogAtLevelUp = true;
	private static boolean showToolBar = true;
	private static boolean showSkillModifier = false;
	private static boolean showSkillRanks = false;
	private static boolean showWarningAtFirstLevelUp = true;
	private static String skinLFThemePack = null;
	private static boolean alwaysOverwrite = false;
	private static String defaultOSType = ""; //$NON-NLS-1$

	/**
	 *  See @javax.swing.SwingConstants
	 */
	private static int tabPlacement = SwingConstants.BOTTOM;
	private static final String TMP_PATH = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
	private static final File TEMP_PATH = new File(TMP_PATH);
	private static boolean useHigherLevelSlotsDefault = false;
	private static boolean wantToLoadMasterworkAndMagic = false;
	private static int nameDisplayStyle = Constants.DISPLAY_STYLE_NAME;
	private static boolean weaponProfPrintout = Constants.DEFAULT_PRINTOUT_WEAPONPROF;
	private static String postExportCommandStandard = ""; //$NON-NLS-1$
	private static String postExportCommandPDF = ""; //$NON-NLS-1$
	private static boolean hideMonsterClasses = false;
	private static boolean guiUsesOutputNameEquipment = false;
	private static boolean guiUsesOutputNameSpells = false;
	private static int lastTipShown = -1;

	private static boolean showTipOfTheDay = true;
	private static boolean showSingleBoxPerBundle = false;

	private SettingsHandler()
	{
	}

	public static void setAlwaysOverwrite(final boolean argAlwaysOverwrite)
	{
		alwaysOverwrite = argAlwaysOverwrite;
	}

	public static boolean getAlwaysOverwrite()
	{
		return alwaysOverwrite;
	}

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

	public static void setChaTabPlacement(final int argChaTabPlacement)
	{
		chaTabPlacement = argChaTabPlacement;
	}

	public static int getChaTabPlacement()
	{
		return chaTabPlacement;
	}

	static boolean getEnforceSpendingBeforeLevelUp()
	{
		return enforceSpendingBeforeLevelUp;
	}

	static String getFilePaths()
	{
		String def_type = "user";
		if (SystemUtils.IS_OS_MAC)
		{
			def_type = "mac_user";
		}
		return FILEPATHS.getProperty("pcgen.filepaths", def_type); //$NON-NLS-1$
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
		final String option = getGMGenOption(optionName, defaultValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	public static int getGMGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getGMGenOption(optionName, String.valueOf(defaultValue)));
	}

	public static Double getGMGenOption(final String optionName, final double defaultValue)
	{
		return Double.valueOf(getGMGenOption(optionName, Double.toString(defaultValue)));
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
		// new key for game mode specific options are pcgen.options.gameMode.X.optionName
		// but offer downward compatible support to read in old version for unitSet from 5.8.0
		String unitSetName = getOptions().getProperty("pcgen.options.gameMode." + g + ".unitSetName",
			getOptions().getProperty("pcgen.options.unitSetName." + g, game.getDefaultUnitSet()));
		if (!game.selectUnitSet(unitSetName))
		{
			if (!game.selectDefaultUnitSet())
			{
				game.selectUnitSet(Constants.STANDARD_UNITSET_NAME);
			}
		}
		game.setDefaultXPTableName(getPCGenOption(
			"gameMode." + g + ".xpTableName", "")); //$NON-NLS-1$ //$NON-NLS-2$
		game.setDefaultCharacterType(getPCGenOption(
			"gameMode." + g + ".characterType", "")); //$NON-NLS-1$ //$NON-NLS-2$

		AbilityCategory featTemplate = game.getFeatTemplate();
		if (featTemplate != null)
		{
			AbilityCategory.FEAT.copyFields(featTemplate);
		}
		getChosenCampaignFiles(game);
	}

	public static GameMode getGame()
	{
		return game;
	}

	static boolean getGearTab_AllowDebt()
	{
		return gearTab_AllowDebt;
	}

	public static void setGearTab_BuyRate(final int argBuyRate)
	{
		gearTab_BuyRate = argBuyRate;
	}

	public static int getGearTab_BuyRate()
	{
		return gearTab_BuyRate;
	}

	static boolean getGearTab_IgnoreCost()
	{
		return gearTab_IgnoreCost;
	}

	public static void setGearTab_SellRate(final int argSellRate)
	{
		gearTab_SellRate = argSellRate;
	}

	public static int getGearTab_SellRate()
	{
		return gearTab_SellRate;
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

		if (StringUtils.isEmpty(selectedCharacterHTMLOutputSheet))
		{
			return ConfigurationSettings.getOutputSheetsDir();
		}

		return new File(selectedCharacterHTMLOutputSheet).getParentFile().getAbsolutePath();
	}

	public static void setIgnoreMonsterHDCap(final boolean argIgoreCap)
	{
		ignoreMonsterHDCap = argIgoreCap;
	}

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

	public static void setLoadURLs(final boolean aBool)
	{
		loadURLs = aBool;
	}

	public static boolean isLoadURLs()
	{
		return loadURLs;
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
	}

	public static int getNameDisplayStyle()
	{
		return nameDisplayStyle;
	}

	public static SortedProperties getOptions()
	{
		return OPTIONS;
	}

	public static void getOptionsFromProperties(final PlayerCharacter aPC)
	{

		final String tempBrowserPath = getPCGenOption("browserPath", ""); //$NON-NLS-1$ //$NON-NLS-2$

		if (StringUtils.isEmpty(tempBrowserPath))
		{
			browserPath = null;
		}
		else
		{
			browserPath = tempBrowserPath;
		}

		//$NON-NLS-1$
		leftUpperCorner = new Point(
				getPCGenOption("windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("windowLeftUpperCorner.Y", -1.0).intValue()
		); //$NON-NLS-1$

		windowState = getPCGenOption("windowState", Frame.NORMAL); //$NON-NLS-1$

		Double dw = getPCGenOption("windowWidth", 0.0); //$NON-NLS-1$
		Double dh = getPCGenOption("windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(dh, 0.0))
		{
			final int width = Integer
				.parseInt(dw.toString().substring(0, Math.min(dw.toString().length(), dw.toString().lastIndexOf('.'))));
			final int height = Integer
				.parseInt(dh.toString().substring(0, Math.min(dh.toString().length(), dh.toString().lastIndexOf('.'))));
		}

		//$NON-NLS-1$
		customizerLeftUpperCorner = new Point(
				getPCGenOption(
						"customizer.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("customizer.windowLeftUpperCorner.Y", -1.0).intValue()
		); //$NON-NLS-1$
		dw = getPCGenOption("customizer.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("customizer.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(dh, 0.0))
		{
			customizerDimension = new Dimension(dw.intValue(), dh.intValue());
		}

		//$NON-NLS-1$
		kitSelectorLeftUpperCorner = new Point(
				getPCGenOption("kitSelector.windowLeftUpperCorner.X", -1.0).intValue(), //$NON-NLS-1$
				getPCGenOption("kitSelector.windowLeftUpperCorner.Y", -1.0).intValue()
		); //$NON-NLS-1$
		dw = getPCGenOption("kitSelector.windowWidth", 0.0); //$NON-NLS-1$
		dh = getPCGenOption("kitSelector.windowHeight", 0.0); //$NON-NLS-1$

		if (!CoreUtility.doublesEqual(dw, 0.0) && !CoreUtility.doublesEqual(dh, 0.0))
		{
			kitSelectorDimension = new Dimension(dw.intValue(), dh.intValue());
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

		Globals.initCustColumnWidth(
			CoreUtility.split(getOptions().getProperty("pcgen.options.custColumnWidth", ""), //$NON-NLS-1$//$NON-NLS-2$
			','));

		loadURLs = getPCGenOption("loadURLs", false); //$NON-NLS-1$

		Globals.setSourceDisplay(
			SourceFormat.values()[getPCGenOption("sourceDisplay", SourceFormat.LONG.ordinal())]); //$NON-NLS-1$

		setAlwaysOverwrite(getPCGenOption("alwaysOverwrite", false)); //$NON-NLS-1$
		autoFeatsRefundable = getPCGenOption("autoFeatsRefundable", false); //$NON-NLS-1$
		autogenExoticMaterial = getPCGenOption("autoGenerateExoticMaterial", false); //$NON-NLS-1$
		autogenMagic = getPCGenOption("autoGenerateMagic", false); //$NON-NLS-1$
		autogenMasterwork = getPCGenOption("autoGenerateMasterwork", false); //$NON-NLS-1$
		autogenRacial = getPCGenOption("autoGenerateRacial", false); //$NON-NLS-1$
		setChaTabPlacement(getOptionTabPlacement("chaTabPlacement", SwingConstants.TOP)); //$NON-NLS-1$
		createPcgBackup = getPCGenOption("createPcgBackup", true);
		customizerSplit1 = getPCGenOption("customizer.split1", -1); //$NON-NLS-1$
		customizerSplit2 = getPCGenOption("customizer.split2", -1); //$NON-NLS-1$
		defaultOSType = getPCGenOption("defaultOSType", null); //$NON-NLS-1$
		enforceSpendingBeforeLevelUp = getPCGenOption("enforceSpendingBeforeLevelUp", false); //$NON-NLS-1$
		gearTab_AllowDebt = getPCGenOption("GearTab.allowDebt", false); //$NON-NLS-1$
		gearTab_AutoResize = getPCGenOption("GearTab.autoResize", false); //$NON-NLS-1$
		setGearTab_BuyRate(buyRate);
		gearTab_IgnoreCost = getPCGenOption("GearTab.ignoreCost", false); //$NON-NLS-1$
		setGearTab_SellRate(sellRate);
		grimHPMode = getPCGenOption("grimHPMode", false); //$NON-NLS-1$
		grittyACMode = getPCGenOption("grittyACMode", false); //$NON-NLS-1$
		setGUIUsesOutputNameEquipment(getPCGenOption("GUIUsesOutputNameEquipment", false)); //$NON-NLS-1$
		setGUIUsesOutputNameSpells(getPCGenOption("GUIUsesOutputNameSpells", false)); //$NON-NLS-1$
		hideMonsterClasses = getPCGenOption("hideMonsterClasses", false); //$NON-NLS-1$
		setHPMaxAtFirstLevel(getPCGenOption("hpMaxAtFirstLevel", true)); //$NON-NLS-1$
		setHPMaxAtFirstClassLevel(getPCGenOption("hpMaxAtFirstClassLevel", false)); //$NON-NLS-1$
		hpMaxAtFirstPCClassLevelOnly = getPCGenOption("hpMaxAtFirstPCClassLevelOnly", false); //$NON-NLS-1$
		setHPPercent(getPCGenOption("hpPercent", 100)); //$NON-NLS-1$
		setHPRollMethod(getPCGenOption("hpRollMethod", Constants.HP_STANDARD)); //$NON-NLS-1$
		setIgnoreMonsterHDCap(getPCGenOption("ignoreMonsterHDCap", false)); //$NON-NLS-1$
		setInvalidDmgText(getPCGenOption("invalidDmgText", //$NON-NLS-1$
			LanguageBundle.getString("SettingsHandler.114"))); //$NON-NLS-1$
		setInvalidToHitText(getPCGenOption("invalidToHitText", //$NON-NLS-1$
			LanguageBundle.getString("SettingsHandler.114"))); //$NON-NLS-1$
		lastTipShown = getPCGenOption("lastTipOfTheDayTipShown", -1); //$NON-NLS-1$
		looknFeel = getPCGenOption("looknFeel", 1); //$NON-NLS-1$
		setMaxPotionSpellLevel(getPCGenOption("maxPotionSpellLevel", 3)); //$NON-NLS-1$
		setMaxWandSpellLevel(getPCGenOption("maxWandSpellLevel", 4)); //$NON-NLS-1$
		setMetamagicAllowedInEqBuilder(getPCGenOption("allowMetamagicInCustomizer", false)); //$NON-NLS-1$
		//$NON-NLS-1$
		pccFilesLocation = new File(expandRelativePath(getPCGenOption(
				"pccFilesLocation", //$NON-NLS-1$
				System.getProperty("user.dir") + File.separator + "data"
		))); //$NON-NLS-1$ //$NON-NLS-2$
		//$NON-NLS-1$
		gmgenPluginDir = new File(expandRelativePath(getOptions().getProperty(
				"gmgen.files.gmgenPluginDir", //$NON-NLS-1$
				System.getProperty("user.dir") + File.separator + "plugins"
		))); //$NON-NLS-1$ //$NON-NLS-2$
		backupPcgPath = new File(expandRelativePath(getOptions().getProperty("pcgen.files.characters.backup", "")));
		//$NON-NLS-1$
		//$NON-NLS-1$
		portraitsPath = new File(expandRelativePath(getOptions().getProperty(
				"pcgen.files.portraits", //$NON-NLS-1$
				Globals.getDefaultPcgPath()
		)));
		setPostExportCommandStandard(getPCGenOption("postExportCommandStandard", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setPostExportCommandPDF(getPCGenOption("postExportCommandPDF", "")); //$NON-NLS-1$ //$NON-NLS-2$
		setPrereqFailColor(getPCGenOption("prereqFailColor", Color.red.getRGB())); //$NON-NLS-1$
		setPrereqQualifyColor(getPCGenOption("prereqQualifyColor", SystemColor.text.getRGB())); //$NON-NLS-1$
		setPreviewTabShown(getPCGenOption("previewTabShown", true)); //$NON-NLS-1$
		isROG = getPCGenOption("isROG", false); //$NON-NLS-1$
		saveCustomInLst = getPCGenOption("saveCustomInLst", false); //$NON-NLS-1$
		saveOutputSheetWithPC = getPCGenOption("saveOutputSheetWithPC", false); //$NON-NLS-1$
		setPrintSpellsWithPC(getPCGenOption("printSpellsWithPC", true)); //$NON-NLS-1$
		setSelectedSpellSheet(
			expandRelativePath(
				getOptions().getProperty("pcgen.files.selectedSpellOutputSheet", ""))); //$NON-NLS-1$ //$NON-NLS-2$
		setSelectedCharacterHTMLOutputSheet(
			expandRelativePath(getOptions().getProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
				"")), //$NON-NLS-1$
			aPC);
		setSelectedCharacterPDFOutputSheet(
			expandRelativePath(getOptions().getProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
				"")), //$NON-NLS-1$
			aPC);
		setSelectedEqSetTemplate(
			expandRelativePath(
				getOptions().getProperty("pcgen.files.selectedEqSetTemplate", ""))); //$NON-NLS-1$ //$NON-NLS-2$
		setSelectedPartyHTMLOutputSheet(
			expandRelativePath(getOptions().getProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
				""))); //$NON-NLS-1$
		setSelectedPartyPDFOutputSheet(
			expandRelativePath(getOptions().getProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
				""))); //$NON-NLS-1$
		setShowHPDialogAtLevelUp(getPCGenOption("showHPDialogAtLevelUp", true)); //$NON-NLS-1$
		setShowSingleBoxPerBundle(getPCGenOption("showSingleBoxPerBundle", false)); //$NON-NLS-1$
		setOutputDeprecationMessages(getPCGenOption("outputDeprecationMessages", true));
		setInputUnconstructedMessages(getPCGenOption("inputUnconstructedMessages", false));
		setShowStatDialogAtLevelUp(getPCGenOption("showStatDialogAtLevelUp", true)); //$NON-NLS-1$
		showTipOfTheDay = getPCGenOption("showTipOfTheDay", true); //$NON-NLS-1$
		showToolBar = getPCGenOption("showToolBar", true); //$NON-NLS-1$
		showSkillModifier = getPCGenOption("showSkillModifier", true); //$NON-NLS-1$
		showSkillRanks = getPCGenOption("showSkillRanks", true); //$NON-NLS-1$
		showWarningAtFirstLevelUp = getPCGenOption("showWarningAtFirstLevelUp", true); //$NON-NLS-1$
		skinLFThemePack = getPCGenOption("skinLFThemePack", ""); //$NON-NLS-1$ //$NON-NLS-2$
		spellMarketPriceAdjusted = getPCGenOption("spellMarketPriceAdjusted", false); //$NON-NLS-1$
		setTabPlacement(getOptionTabPlacement("tabPlacement", SwingConstants.BOTTOM)); //$NON-NLS-1$
		useHigherLevelSlotsDefault = getPCGenOption("useHigherLevelSlotsDefault", false); //$NON-NLS-1$
		useWaitCursor = getPCGenOption("useWaitCursor", true); //$NON-NLS-1$
		setWantToLoadMasterworkAndMagic(getPCGenOption("loadMasterworkAndMagicFromLst", false)); //$NON-NLS-1$
		setWeaponProfPrintout(getPCGenOption("weaponProfPrintout", Constants.DEFAULT_PRINTOUT_WEAPONPROF));

		// Load up all the RuleCheck stuff from the options.ini file
		// It's stored as:
		//   pcgen.options.rulechecks=aKey:Y|bKey:N|cKey:Y
		parseRuleChecksFromOptions(getPCGenOption("ruleChecks", "")); //$NON-NLS-1$ //$NON-NLS-2$

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
				CoreUtility.split(
					getOptions().getProperty(
						"pcgen.files.chosenCampaignSourcefiles." + gameMode.getName(), //$NON-NLS-1$
				""), ',');
		List<URI> uriList = new ArrayList<>(uriStringList.size());
		for (String str : uriStringList)
		{
			try
			{
				uriList.add(new URI(str));
			}
			catch (URISyntaxException e)
			{
				Logging
					.errorPrint("Settings error: Unable to convert " + str + " to a URI: " + e.getLocalizedMessage());
			}
		}
		PersistenceManager.getInstance().setChosenCampaignSourcefiles(uriList, gameMode);
	}

	public static void setOptionsProperties(final PlayerCharacter aPC)
	{
		if (backupPcgPath != null && !backupPcgPath.getPath().isEmpty())
		{
			getOptions().setProperty("pcgen.files.characters.backup", //$NON-NLS-1$
				retractRelativePath(backupPcgPath.getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("pcgen.files.characters.backup", ""); //$NON-NLS-1$
		}

		getOptions().setProperty(
			"pcgen.files.portraits", retractRelativePath(getPortraitsPath().getAbsolutePath())); //$NON-NLS-1$
		getOptions().setProperty(
			"pcgen.files.selectedSpellOutputSheet", retractRelativePath(getSelectedSpellSheet())); //$NON-NLS-1$
		getOptions().setProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterHTMLOutputSheet(aPC)));
		getOptions().setProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(getSelectedCharacterPDFOutputSheet(aPC)));
		getOptions().setProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
			retractRelativePath(selectedPartyHTMLOutputSheet));
		getOptions().setProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
			retractRelativePath(selectedPartyPDFOutputSheet));
		getOptions().setProperty(
			"pcgen.files.selectedEqSetTemplate", retractRelativePath(getSelectedEqSetTemplate())); //$NON-NLS-1$
		getOptions().setProperty("pcgen.files.chosenCampaignSourcefiles", //$NON-NLS-1$
			StringUtil.join(PersistenceManager.getInstance().getChosenCampaignSourcefiles(), ", "));

		getOptions().setProperty(
			"pcgen.options.custColumnWidth", StringUtil.join(Globals.getCustColumnWidth(), ", ")); //$NON-NLS-1$

		if (getGmgenPluginDir() != null)
		{
			getOptions().setProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
				retractRelativePath(getGmgenPluginDir().getAbsolutePath()));
		}
		else
		{
			getOptions().setProperty("gmgen.files.gmgenPluginDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		setPCGenOption("browserPath", Objects.requireNonNullElse(browserPath, "")); //$NON-NLS-1$

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
			setPCGenOption("skinLFThemePack", skinLFThemePack); //$NON-NLS-1$
		}
		catch (NullPointerException e)
		{
			//TODO: Should this really be ignored???  XXX
		}

		if (getPccFilesLocation() != null)
		{
			setPCGenOption(
				"pccFilesLocation", retractRelativePath(getPccFilesLocation().getAbsolutePath())); //$NON-NLS-1$
		}
		else
		{
			setPCGenOption("pccFilesLocation", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (leftUpperCorner != null)
		{
			setPCGenOption("windowLeftUpperCorner.X", leftUpperCorner.getX()); //$NON-NLS-1$
			setPCGenOption("windowLeftUpperCorner.Y", leftUpperCorner.getY()); //$NON-NLS-1$
		}

		setPCGenOption("windowState", windowState); //$NON-NLS-1$

		if (Globals.getRootFrame() != null)
		{
			setPCGenOption("windowWidth", Globals.getRootFrame().getSize().getWidth()); //$NON-NLS-1$
			setPCGenOption("windowHeight", Globals.getRootFrame().getSize().getHeight()); //$NON-NLS-1$
		}

		if (customizerLeftUpperCorner != null)
		{
			setPCGenOption("customizer.windowLeftUpperCorner.X", customizerLeftUpperCorner.getX()); //$NON-NLS-1$
			setPCGenOption("customizer.windowLeftUpperCorner.Y", customizerLeftUpperCorner.getY()); //$NON-NLS-1$
		}

		if (customizerDimension != null)
		{
			setPCGenOption("customizer.windowWidth", customizerDimension.getWidth()); //$NON-NLS-1$
			setPCGenOption("customizer.windowHeight", customizerDimension.getHeight()); //$NON-NLS-1$
		}

		if (kitSelectorLeftUpperCorner != null)
		{
			setPCGenOption(
				"kitSelector.windowLeftUpperCorner.X", kitSelectorLeftUpperCorner.getX()); //$NON-NLS-1$
			setPCGenOption(
				"kitSelector.windowLeftUpperCorner.Y", kitSelectorLeftUpperCorner.getY()); //$NON-NLS-1$
		}

		if (kitSelectorDimension != null)
		{
			setPCGenOption("kitSelector.windowWidth", kitSelectorDimension.getWidth()); //$NON-NLS-1$
			setPCGenOption("kitSelector.windowHeight", kitSelectorDimension.getHeight()); //$NON-NLS-1$
		}

		//
		// Remove old-style option values
		//
		setPCGenOption("allStatsValue", null);
		setPCGenOption("purchaseMethodName", null);
		setPCGenOption("rollMethod", null);
		setPCGenOption("rollMethodExpression", null);

		SystemCollections.getUnmodifiableGameModeList().forEach(gameMode -> {
			String gameModeKey = gameMode.getName();
			if (gameMode.getUnitSet() != null && gameMode.getUnitSet().getDisplayName() != null)
			{
				setPCGenOption("gameMode." + gameModeKey + ".unitSetName", gameMode.getUnitSet().getDisplayName());
			}
			setPCGenOption(
					"gameMode." + gameModeKey + ".purchaseMethodName", //$NON-NLS-1$
					gameMode.getPurchaseModeMethodName()
			);
			setPCGenOption(
					"gameMode." + gameModeKey + ".rollMethod", //$NON-NLS-1$
					gameMode.getRollMethod()
			);
			setPCGenOption(
					"gameMode." + gameModeKey + ".rollMethodExpression", //$NON-NLS-1$
					gameMode.getRollMethodExpressionName()
			);
			setPCGenOption("gameMode." + gameModeKey + ".allStatsValue", gameMode.getAllStatsValue());
			setPCGenOption("gameMode." + gameModeKey + ".xpTableName", gameMode.getDefaultXPTableName());
			setPCGenOption("gameMode." + gameModeKey + ".characterType", gameMode.getDefaultCharacterType());
		});

		setRuleChecksInOptions(); //$NON-NLS-1$

		setPCGenOption("allowMetamagicInCustomizer", isMetamagicAllowedInEqBuilder()); //$NON-NLS-1$
		setPCGenOption("alwaysOverwrite", getAlwaysOverwrite()); //$NON-NLS-1$
		setPCGenOption("autoFeatsRefundable", autoFeatsRefundable); //$NON-NLS-1$
		setPCGenOption("autoGenerateExoticMaterial", isAutogenExoticMaterial()); //$NON-NLS-1$
		setPCGenOption("autoGenerateMagic", isAutogenMagic()); //$NON-NLS-1$
		setPCGenOption("autoGenerateMasterwork", isAutogenMasterwork()); //$NON-NLS-1$
		setPCGenOption("autoGenerateRacial", isAutogenRacial()); //$NON-NLS-1$
		setPCGenOption("chaTabPlacement", convertTabPlacementToString(chaTabPlacement)); //$NON-NLS-1$
		setPCGenOption("createPcgBackup", createPcgBackup); //$NON-NLS-1$
		setPCGenOption("customizer.split1", customizerSplit1); //$NON-NLS-1$
		setPCGenOption("customizer.split2", customizerSplit2); //$NON-NLS-1$
		setPCGenOption("defaultOSType", defaultOSType); //$NON-NLS-1$
		setPCGenOption("GearTab.allowDebt", getGearTab_AllowDebt()); //$NON-NLS-1$
		setPCGenOption("GearTab.autoResize", gearTab_AutoResize); //$NON-NLS-1$
		setPCGenOption("GearTab.buyRate", getGearTab_BuyRate()); //$NON-NLS-1$
		setPCGenOption("GearTab.ignoreCost", getGearTab_IgnoreCost()); //$NON-NLS-1$
		setPCGenOption("GearTab.sellRate", getGearTab_SellRate()); //$NON-NLS-1$
		setPCGenOption("grimHPMode", grimHPMode); //$NON-NLS-1$
		setPCGenOption("grittyACMode", grittyACMode); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameEquipment", guiUsesOutputNameEquipment()); //$NON-NLS-1$
		setPCGenOption("GUIUsesOutputNameSpells", guiUsesOutputNameSpells()); //$NON-NLS-1$
		setPCGenOption("hideMonsterClasses", hideMonsterClasses()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstLevel", isHPMaxAtFirstLevel()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstClassLevel", isHPMaxAtFirstClassLevel()); //$NON-NLS-1$
		setPCGenOption("hpMaxAtFirstPCClassLevelOnly", isHPMaxAtFirstPCClassLevelOnly()); //$NON-NLS-1$
		setPCGenOption("hpPercent", getHPPercent()); //$NON-NLS-1$
		setPCGenOption("hpRollMethod", getHPRollMethod()); //$NON-NLS-1$
		setPCGenOption("ignoreMonsterHDCap", isIgnoreMonsterHDCap()); //$NON-NLS-1$
		setPCGenOption("invalidDmgText", getInvalidDmgText()); //$NON-NLS-1$
		setPCGenOption("invalidToHitText", getInvalidToHitText()); //$NON-NLS-1$
		setPCGenOption("lastTipOfTheDayTipShown", lastTipShown); //$NON-NLS-1$
		setPCGenOption("loadMasterworkAndMagicFromLst", wantToLoadMasterworkAndMagic()); //$NON-NLS-1$
		setPCGenOption("loadURLs", loadURLs); //$NON-NLS-1$
		setPCGenOption("looknFeel", looknFeel); //$NON-NLS-1$
		setPCGenOption("maxPotionSpellLevel", getMaxPotionSpellLevel()); //$NON-NLS-1$
		setPCGenOption("maxWandSpellLevel", getMaxWandSpellLevel()); //$NON-NLS-1$
		setPCGenOption("nameDisplayStyle", getNameDisplayStyle()); //$NON-NLS-1$
		setPCGenOption("postExportCommandStandard", getPostExportCommandStandard()); //$NON-NLS-1$
		setPCGenOption("postExportCommandPDF", getPostExportCommandPDF()); //$NON-NLS-1$
		setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(prereqFailColor)); //$NON-NLS-1$ //$NON-NLS-2$
		setPCGenOption("prereqQualifyColor", "0x" //$NON-NLS-1$ //$NON-NLS-2$
						+ Integer.toHexString(prereqQualifyColor));
		setPCGenOption("previewTabShown", isPreviewTabShown()); //$NON-NLS-1$
		setPCGenOption("saveCustomInLst", saveCustomInLst); //$NON-NLS-1$
		setPCGenOption("saveOutputSheetWithPC", getSaveOutputSheetWithPC()); //$NON-NLS-1$
		setPCGenOption("printSpellsWithPC", getPrintSpellsWithPC()); //$NON-NLS-1$
		setPCGenOption("enforceSpendingBeforeLevelUp", getEnforceSpendingBeforeLevelUp()); //$NON-NLS-1$
		setPCGenOption("showHPDialogAtLevelUp", getShowHPDialogAtLevelUp()); //$NON-NLS-1$
		setPCGenOption("showStatDialogAtLevelUp", getShowStatDialogAtLevelUp()); //$NON-NLS-1$
		setPCGenOption("showTipOfTheDay", showTipOfTheDay); //$NON-NLS-1$
		setPCGenOption("showToolBar", showToolBar); //$NON-NLS-1$
		setPCGenOption("showSkillModifier", showSkillModifier); //$NON-NLS-1$
		setPCGenOption("showSkillRanks", showSkillRanks); //$NON-NLS-1$
		setPCGenOption("showSingleBoxPerBundle", getShowSingleBoxPerBundle()); //$NON-NLS-1$
		setPCGenOption("showWarningAtFirstLevelUp", showWarningAtFirstLevelUp); //$NON-NLS-1$
		setPCGenOption("sourceDisplay", Globals.getSourceDisplay().ordinal()); //$NON-NLS-1$
		setPCGenOption("spellMarketPriceAdjusted", spellMarketPriceAdjusted); //$NON-NLS-1$
		setPCGenOption("tabPlacement", convertTabPlacementToString(tabPlacement)); //$NON-NLS-1$
		setPCGenOption("useHigherLevelSlotsDefault", isUseHigherLevelSlotsDefault()); //$NON-NLS-1$
		setPCGenOption("useWaitCursor", useWaitCursor); //$NON-NLS-1$
		setPCGenOption("validateBonuses", validateBonuses); //$NON-NLS-1$
		setPCGenOption("weaponProfPrintout", getWeaponProfPrintout()); //$NON-NLS-1$
		setPCGenOption("outputDeprecationMessages", outputDeprecationMessages()); //$NON-NLS-1$
		setPCGenOption("inputUnconstructedMessages", inputUnconstructedMessages()); //$NON-NLS-1$
	}

	private static void setPCGenOption(final String optionName, final int optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setPCGenOption(final String optionName, final String optionValue)
	{
		if (optionValue == null)
		{
			getOptions().remove("pcgen.options." + optionName); //$NON-NLS-1$
		}
		else
		{
			getOptions().setProperty("pcgen.options." + optionName, optionValue); //$NON-NLS-1$
		}
	}

	private static int getPCGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getPCGenOption(optionName, String.valueOf(defaultValue)));
	}

	public static String getPCGenOption(final String optionName, final String defaultValue)
	{
		return getOptions().getProperty("pcgen.options." + optionName, defaultValue); //$NON-NLS-1$
	}

	public static String getPDFOutputSheetPath()
	{
		if (selectedCharacterPDFOutputSheet != null && selectedCharacterPDFOutputSheet.isEmpty()) //$NON-NLS-1$
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
	 * @deprecated Use PCGenSettings.getPortraitsDir()
	 * @return the portraits directory
	 */
	@Deprecated
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

	private static void setPrereqFailColor(final int newColor)
	{
		prereqFailColor = newColor & 0x00FFFFFF;
	}

	public static String getPrereqFailColorAsHtmlStart()
	{
		final StringBuilder rString = new StringBuilder("<font color="); //$NON-NLS-1$

		if (prereqFailColor == 0)
		{
			rString.append("red"); //$NON-NLS-1$
		}
		else
		{
			rString.append("\"#") //$NON-NLS-1$
			       .append(Integer.toHexString(prereqFailColor))
			       .append("\""); //$NON-NLS-1$
		}

		rString.append('>');

		return rString.toString();
	}

	public static String getPrereqFailColorAsHtmlEnd()
	{
		return "</font>"; //$NON-NLS-1$
	}

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
			return aVal.equals("Y");
		}

		return false;
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
	 * @return    the {@code selectedCharacterHTMLOutputSheet} property
	 * @param aPC
	 **/
	public static String getSelectedCharacterHTMLOutputSheet(final PlayerCharacter aPC)
	{
		if (getSaveOutputSheetWithPC() && (aPC != null))
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
		if (getSaveOutputSheetWithPC() && (aPC != null))
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
		if (getSaveOutputSheetWithPC() && (aPC != null))
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
	 * @return    the {@code tempPath} property
	 */
	public static File getTempPath()
	{
		return TEMP_PATH;
	}

	/**
	 * @return Returns the useHigherLevelSlotsDefault.
	 */
	static boolean isUseHigherLevelSlotsDefault()
	{
		return useHigherLevelSlotsDefault;
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
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle.getString("SettingsHandler.no.options.file")); //$NON-NLS-1$
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
				Logging.errorPrint(
					LanguageBundle.getString("SettingsHandler.can.not.close.options.file"), ex); //$NON-NLS-1$
			}
		}
	}

	private static boolean wantToLoadMasterworkAndMagic()
	{
		return wantToLoadMasterworkAndMagic;
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

	private static Double getPCGenOption(final String optionName, final double defaultValue)
	{
		return Double.valueOf(getPCGenOption(optionName, Double.toString(defaultValue)));
	}

	/**
	 * Set's the RuleChecks in the options.ini file
	 **/
	private static void setRuleChecksInOptions()
	{
		String value = ""; //$NON-NLS-1$

		for (final Map.Entry<String, String> stringStringEntry : ruleCheckMap.entrySet())
		{
			final String aVal = stringStringEntry.getValue();

			if (value.isEmpty())
			{
				value = stringStringEntry.getKey() + '|' + aVal; //$NON-NLS-1$
			}
			else
			{
				value += ("," + stringStringEntry.getKey() + "|" + aVal); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		//setPCGenOption(optionName, value);
		getOptions().setProperty("pcgen.options." + "ruleChecks", value); //$NON-NLS-1$
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

			case SwingConstants.TOP:
			default:
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
			in = new FileInputStream(FILE_LOCATION);
			FILEPATHS.load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filepaths.ini")); //$NON-NLS-1$
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
				Logging.errorPrint(
					LanguageBundle.getString("SettingsHandler.can.not.close.filepaths.ini"), ex); //$NON-NLS-1$
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
			FILTERSETTINGS.load(in);
		}
		catch (IOException e)
		{
			// Not an error, this file may not exist yet
			if (Logging.isDebugMode())
			{
				Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filter.ini")); //$NON-NLS-1$
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
				Logging.errorPrint(
					LanguageBundle.getString("SettingsHandler.can.not.close.filter.ini"), ex); //$NON-NLS-1$
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

}
