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
import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.SortedProperties;
import pcgen.persistence.PersistenceManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.SystemUtils;

/**
 * Should be cleaned up more.
 *
 * <b>NB: This class is being gradually replaced with use of
 * {@link pcgen.system.PropertyContext} and its children.</b>
 **/
public final class SettingsHandler
{

    //
    // For EqBuilder
    //
    private static final IntegerProperty maxPotionSpellLevel =
            new SimpleIntegerProperty(Constants.DEFAULT_MAX_POTION_SPELL_LEVEL);
    private static final IntegerProperty maxWandSpellLevel =
            new SimpleIntegerProperty(Constants.DEFAULT_MAX_WAND_SPELL_LEVEL);

    // do settings need a restart
    private static final BooleanProperty settingsNeedRestart = new SimpleBooleanProperty(false);

    // Map of RuleCheck keys and their settings
    private static final Map<String, String> ruleCheckMap = new HashMap<>();

    private static final Properties FILTERSETTINGS = new Properties();
    private static final ObjectProperty<GameMode> game = new SimpleObjectProperty<>(new GameMode("default"));
    private static boolean loadURLs = false;
    private static boolean hpMaxAtFirstLevel = true;
    private static boolean hpMaxAtFirstClassLevel = true;
    private static boolean hpMaxAtFirstPCClassLevelOnly = true;
    private static int hpRollMethod = Constants.HP_STANDARD;
    private static int hpPercent = Constants.DEFAULT_HP_PERCENT;
    private static boolean ignoreMonsterHDCap = false;

    private static boolean gearTab_IgnoreCost = false;
    private static boolean gearTab_AllowDebt = false;
    private static int gearTab_SellRate = Constants.DEFAULT_GEAR_TAB_SELL_RATE;
    private static int gearTab_BuyRate = Constants.DEFAULT_GEAR_TAB_BUY_RATE;

    private static final SortedProperties OPTIONS = new SortedProperties();
    private static final Properties FILEPATHS = new Properties();
    private static final String FILE_LOCATION = Globals.getFilepathsPath();
    private static File backupPcgPath = null;
    private static boolean createPcgBackup = true;

    private static File gmgenPluginDir = new File(Globals.getDefaultPath() + File.separator + "plugins"); //$NON-NLS-1$
    private static int prereqQualifyColor = Constants.DEFAULT_PREREQ_QUALIFY_COLOUR;
    private static int prereqFailColor = Constants.DEFAULT_PREREQ_FAIL_COLOUR;

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
    private static boolean showWarningAtFirstLevelUp = true;
    private static boolean alwaysOverwrite = false;
    private static String defaultOSType = ""; //$NON-NLS-1$

    private static final String TMP_PATH = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
    private static final File TEMP_PATH = new File(getTmpPath());
    private static boolean useHigherLevelSlotsDefault = false;
    private static boolean weaponProfPrintout = Constants.DEFAULT_PRINTOUT_WEAPONPROF;
    private static String postExportCommandStandard = ""; //$NON-NLS-1$
    private static String postExportCommandPDF = ""; //$NON-NLS-1$
    private static boolean hideMonsterClasses = false;
    private static boolean guiUsesOutputNameEquipment = false;
    private static boolean guiUsesOutputNameSpells = false;
    private static int lastTipShown = -1;
    private static boolean showTipOfTheDay = true;

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

    public static void setDefaultOSType(final String argDefaultOSType)
    {
        defaultOSType = argDefaultOSType;
    }

    public static String getDefaultOSType()
    {
        return defaultOSType;
    }

    /**
     * Sets the path to the backup directory for character files.
     *
     * @param path the {@code File} representing the path
     */
    public static void setBackupPcgPath(final File path)
    {
        backupPcgPath = path;
    }

    /**
     * Returns the path to the backup directory for character files.
     *
     * @return the {@code backupPcgPath} property
     */
    public static File getBackupPcgPath()
    {
        return backupPcgPath;
    }

    /**
     * Sets the flag to determine whether PCGen should backup pcg files before saving
     *
     * @param argCreatePcgBackup the {@code flag}
     */
    public static void setCreatePcgBackup(final boolean argCreatePcgBackup)
    {
        createPcgBackup = argCreatePcgBackup;
    }

    /**
     * Returns the flag to determine whether PCGen should backup pcg files before saving
     *
     * @return the {@code createPcgBackup} property
     */
    public static boolean getCreatePcgBackup()
    {
        return createPcgBackup;
    }

    public static String getFilePaths()
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
     *
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

    public static void setGMGenOption(final String optionName, final String optionValue)
    {
        getOptions().setProperty("gmgen.options." + optionName, optionValue); //$NON-NLS-1$
    }

    /**
     * Set most of this objects static properties from the loaded {@code options}.
     * Called by readOptionsProperties. Most of the static properties are
     * set as a side effect, with the main screen size being returned.
     *
     * @param optionName
     * @param defaultValue
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

    public static void setGame(final String key)
    {
        final GameMode newMode = SystemCollections.getGameModeNamed(key);

        if (newMode != null)
        {
            game.setValue(newMode);
        }
        // new key for game mode specific options are pcgen.options.gameMode.X.optionName
        // but offer downward compatible support to read in old version for unitSet from 5.8.0
        String unitSetName = getOptions().getProperty("pcgen.options.gameMode." + key + ".unitSetName",
                getOptions().getProperty("pcgen.options.unitSetName." + key, game.get().getDefaultUnitSet()));
        if (!game.get().selectUnitSet(unitSetName))
        {
            if (!game.get().selectDefaultUnitSet())
            {
                game.get().selectUnitSet(Constants.STANDARD_UNITSET_NAME);
            }
        }
        game.get().setDefaultXPTableName(getPCGenOption(
                "gameMode." + key + ".xpTableName", "")); //$NON-NLS-1$ //$NON-NLS-2$
        game.get().setDefaultCharacterType(getPCGenOption(
                "gameMode." + key + ".characterType", "")); //$NON-NLS-1$ //$NON-NLS-2$

        AbilityCategory featTemplate = game.get().getFeatTemplate();
        if (featTemplate != null)
        {
            AbilityCategory.FEAT.copyFields(featTemplate);
        }
        getChosenCampaignFiles(game.get());
    }

    public static ObjectProperty<GameMode> getGameAsProperty()
    {
        return game;
    }

    /**
     * @deprecated use getGameAsProperty
     */
    @Deprecated
    public static GameMode getGame()
    {
        return game.get();
    }

    public static void setGearTab_AllowDebt(final boolean allowDebt)
    {
        gearTab_AllowDebt = allowDebt;
    }

    public static boolean getGearTab_AllowDebt()
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

    public static void setGearTab_IgnoreCost(final boolean ignoreCost)
    {
        gearTab_IgnoreCost = ignoreCost;
    }

    public static boolean getGearTab_IgnoreCost()
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
            return ConfigurationSettings.getOutputSheetsDir();
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

    public static void setLastTipShown(final int argLastTipShown)
    {
        lastTipShown = argLastTipShown;
    }

    public static int getLastTipShown()
    {
        return lastTipShown;
    }

    public static void setLoadURLs(final boolean aBool)
    {
        loadURLs = aBool;
    }

    public static boolean isLoadURLs()
    {
        return loadURLs;
    }

    public static IntegerProperty maxWandSpellLevel()
    {
        return maxWandSpellLevel;
    }

    public static IntegerProperty maxPotionSpellLevel()
    {
        return maxPotionSpellLevel;
    }

    public static BooleanProperty settingsNeedRestartProperty()
    {
        return settingsNeedRestart;
    }

    public static SortedProperties getOptions()
    {
        return OPTIONS;
    }

    public static void getOptionsFromProperties(final PlayerCharacter aPC)
    {
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
            } else
            {
                buyRate = 100;
                sellRate = 50;
            }
        }

        loadURLs = getPCGenOption("loadURLs", false); //$NON-NLS-1$

        Globals.setSourceDisplay(
                SourceFormat.values()[getPCGenOption("sourceDisplay", SourceFormat.LONG.ordinal())]); //$NON-NLS-1$

        setAlwaysOverwrite(getPCGenOption("alwaysOverwrite", false)); //$NON-NLS-1$
        setCreatePcgBackup(getPCGenOption("createPcgBackup", true));
        setDefaultOSType(getPCGenOption("defaultOSType", null)); //$NON-NLS-1$
        setGearTab_AllowDebt(getPCGenOption("GearTab.allowDebt", false)); //$NON-NLS-1$
        setGearTab_BuyRate(buyRate);
        setGearTab_IgnoreCost(getPCGenOption("GearTab.ignoreCost", false)); //$NON-NLS-1$
        setGearTab_SellRate(sellRate);
        setGUIUsesOutputNameEquipment(getPCGenOption("GUIUsesOutputNameEquipment", false)); //$NON-NLS-1$
        setGUIUsesOutputNameSpells(getPCGenOption("GUIUsesOutputNameSpells", false)); //$NON-NLS-1$
        setHideMonsterClasses(getPCGenOption("hideMonsterClasses", false)); //$NON-NLS-1$
        setHPMaxAtFirstLevel(getPCGenOption("hpMaxAtFirstLevel", true)); //$NON-NLS-1$
        setHPMaxAtFirstClassLevel(getPCGenOption("hpMaxAtFirstClassLevel", false)); //$NON-NLS-1$
        setHPMaxAtFirstPCClassLevelOnly(getPCGenOption("hpMaxAtFirstPCClassLevelOnly", false)); //$NON-NLS-1$
        setHPPercent(getPCGenOption("hpPercent", 100)); //$NON-NLS-1$
        setHPRollMethod(getPCGenOption("hpRollMethod", Constants.HP_STANDARD)); //$NON-NLS-1$
        setIgnoreMonsterHDCap(getPCGenOption("ignoreMonsterHDCap", false)); //$NON-NLS-1$
        setLastTipShown(getPCGenOption("lastTipOfTheDayTipShown", -1)); //$NON-NLS-1$
        maxWandSpellLevel.set(getPCGenOption("maxWandSpellLevel", 4));
        maxPotionSpellLevel.set(getPCGenOption("maxPotionSpellLevel", 3));
        setGmgenPluginDir(
                new File(expandRelativePath(getOptions().getProperty("gmgen.files.gmgenPluginDir", //$NON-NLS-1$
                        System.getProperty("user.dir") + File.separator + "plugins")))); //$NON-NLS-1$ //$NON-NLS-2$
        setBackupPcgPath(
                new File(expandRelativePath(getOptions().getProperty("pcgen.files.characters.backup", "")))); //$NON-NLS-1$
        setPostExportCommandStandard(getPCGenOption("postExportCommandStandard", "")); //$NON-NLS-1$ //$NON-NLS-2$
        setPostExportCommandPDF(getPCGenOption("postExportCommandPDF", "")); //$NON-NLS-1$ //$NON-NLS-2$
        setPrereqFailColor(getPCGenOption("prereqFailColor", Color.red.getRGB())); //$NON-NLS-1$
        setPrereqQualifyColor(getPCGenOption("prereqQualifyColor", SystemColor.text.getRGB())); //$NON-NLS-1$
        setSaveCustomInLst(getPCGenOption("saveCustomInLst", false)); //$NON-NLS-1$
        setSaveOutputSheetWithPC(getPCGenOption("saveOutputSheetWithPC", false)); //$NON-NLS-1$
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
        setOutputDeprecationMessages(getPCGenOption("outputDeprecationMessages", true));
        setInputUnconstructedMessages(getPCGenOption("inputUnconstructedMessages", false));
        setShowStatDialogAtLevelUp(getPCGenOption("showStatDialogAtLevelUp", true)); //$NON-NLS-1$
        setShowTipOfTheDay(getPCGenOption("showTipOfTheDay", true)); //$NON-NLS-1$
        setShowWarningAtFirstLevelUp(getPCGenOption("showWarningAtFirstLevelUp", true)); //$NON-NLS-1$
        setUseHigherLevelSlotsDefault(getPCGenOption("useHigherLevelSlotsDefault", false)); //$NON-NLS-1$
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
            } catch (URISyntaxException e)
            {
                Logging
                        .errorPrint("Settings error: Unable to convert " + str + " to a URI: " + e.getLocalizedMessage());
            }
        }
        PersistenceManager.getInstance().setChosenCampaignSourcefiles(uriList, gameMode);
    }

    public static void setOptionsProperties(final PlayerCharacter aPC)
    {
        if (getBackupPcgPath() != null && !getBackupPcgPath().getPath().equals(""))
        {
            getOptions().setProperty("pcgen.files.characters.backup", //$NON-NLS-1$
                    retractRelativePath(getBackupPcgPath().getAbsolutePath()));
        } else
        {
            getOptions().setProperty("pcgen.files.characters.backup", ""); //$NON-NLS-1$
        }

        getOptions().setProperty(
                "pcgen.files.selectedSpellOutputSheet", retractRelativePath(getSelectedSpellSheet())); //$NON-NLS-1$
        getOptions().setProperty("pcgen.files.selectedCharacterHTMLOutputSheet", //$NON-NLS-1$
                retractRelativePath(getSelectedCharacterHTMLOutputSheet(aPC)));
        getOptions().setProperty("pcgen.files.selectedCharacterPDFOutputSheet", //$NON-NLS-1$
                retractRelativePath(getSelectedCharacterPDFOutputSheet(aPC)));
        getOptions().setProperty("pcgen.files.selectedPartyHTMLOutputSheet", //$NON-NLS-1$
                retractRelativePath(getSelectedPartyHTMLOutputSheet()));
        getOptions().setProperty("pcgen.files.selectedPartyPDFOutputSheet", //$NON-NLS-1$
                retractRelativePath(getSelectedPartyPDFOutputSheet()));
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
        } else
        {
            getOptions().setProperty("gmgen.files.gmgenPluginDir", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (getGame() != null)
        {
            setPCGenOption("game", getGame().getName()); //$NON-NLS-1$
        } else
        {
            setPCGenOption("game", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (int idx = 0;idx < SystemCollections.getUnmodifiableGameModeList().size();idx++)
        {
            final GameMode gameMode = SystemCollections.getUnmodifiableGameModeList().get(idx);
            String gameModeKey = gameMode.getName();
            if (gameMode.getUnitSet() != null && gameMode.getUnitSet().getDisplayName() != null)
            {
                setPCGenOption("gameMode." + gameModeKey + ".unitSetName", gameMode.getUnitSet().getDisplayName());
            }
            setPCGenOption("gameMode." + gameModeKey + ".purchaseMethodName", //$NON-NLS-1$
                    gameMode.getPurchaseModeMethodName());
            setPCGenOption("gameMode." + gameModeKey + ".rollMethod", //$NON-NLS-1$
                    gameMode.getRollMethod());
            setPCGenOption("gameMode." + gameModeKey + ".rollMethodExpression", //$NON-NLS-1$
                    gameMode.getRollMethodExpressionName());
            setPCGenOption("gameMode." + gameModeKey + ".allStatsValue", gameMode.getAllStatsValue());
            setPCGenOption("gameMode." + gameModeKey + ".xpTableName", gameMode.getDefaultXPTableName());
            setPCGenOption("gameMode." + gameModeKey + ".characterType", gameMode.getDefaultCharacterType());
        }

        setRuleChecksInOptions("ruleChecks"); //$NON-NLS-1$

        setPCGenOption("alwaysOverwrite", getAlwaysOverwrite()); //$NON-NLS-1$
        setPCGenOption("createPcgBackup", getCreatePcgBackup()); //$NON-NLS-1$
        setPCGenOption("defaultOSType", getDefaultOSType()); //$NON-NLS-1$
        setPCGenOption("GearTab.allowDebt", getGearTab_AllowDebt()); //$NON-NLS-1$
        setPCGenOption("GearTab.buyRate", getGearTab_BuyRate()); //$NON-NLS-1$
        setPCGenOption("GearTab.ignoreCost", getGearTab_IgnoreCost()); //$NON-NLS-1$
        setPCGenOption("GearTab.sellRate", getGearTab_SellRate()); //$NON-NLS-1$
        setPCGenOption("GUIUsesOutputNameEquipment", guiUsesOutputNameEquipment()); //$NON-NLS-1$
        setPCGenOption("GUIUsesOutputNameSpells", guiUsesOutputNameSpells()); //$NON-NLS-1$
        setPCGenOption("hideMonsterClasses", hideMonsterClasses()); //$NON-NLS-1$
        setPCGenOption("hpMaxAtFirstLevel", isHPMaxAtFirstLevel()); //$NON-NLS-1$
        setPCGenOption("hpMaxAtFirstClassLevel", isHPMaxAtFirstClassLevel()); //$NON-NLS-1$
        setPCGenOption("hpMaxAtFirstPCClassLevelOnly", isHPMaxAtFirstPCClassLevelOnly()); //$NON-NLS-1$
        setPCGenOption("hpPercent", getHPPercent()); //$NON-NLS-1$
        setPCGenOption("hpRollMethod", getHPRollMethod()); //$NON-NLS-1$
        setPCGenOption("ignoreMonsterHDCap", isIgnoreMonsterHDCap()); //$NON-NLS-1$
        setPCGenOption("lastTipOfTheDayTipShown", getLastTipShown()); //$NON-NLS-1$
        setPCGenOption("loadURLs", loadURLs); //$NON-NLS-1$
        setPCGenOption("maxPotionSpellLevel", maxPotionSpellLevel().get()); //$NON-NLS-1$
        setPCGenOption("maxWandSpellLevel", maxWandSpellLevel().get()); //$NON-NLS-1$
        setPCGenOption("postExportCommandStandard", SettingsHandler.getPostExportCommandStandard()); //$NON-NLS-1$
        setPCGenOption("postExportCommandPDF", SettingsHandler.getPostExportCommandPDF()); //$NON-NLS-1$
        setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(getPrereqFailColor())); //$NON-NLS-1$ //$NON-NLS-2$
        setPCGenOption("prereqQualifyColor", "0x" //$NON-NLS-1$ //$NON-NLS-2$
                + Integer.toHexString(getPrereqQualifyColor()));
        setPCGenOption("saveCustomInLst", isSaveCustomInLst()); //$NON-NLS-1$
        setPCGenOption("saveOutputSheetWithPC", getSaveOutputSheetWithPC()); //$NON-NLS-1$
        setPCGenOption("printSpellsWithPC", getPrintSpellsWithPC()); //$NON-NLS-1$
        setPCGenOption("showHPDialogAtLevelUp", getShowHPDialogAtLevelUp()); //$NON-NLS-1$
        setPCGenOption("showStatDialogAtLevelUp", getShowStatDialogAtLevelUp()); //$NON-NLS-1$
        setPCGenOption("showTipOfTheDay", getShowTipOfTheDay()); //$NON-NLS-1$
        setPCGenOption("showWarningAtFirstLevelUp", isShowWarningAtFirstLevelUp()); //$NON-NLS-1$
        setPCGenOption("sourceDisplay", Globals.getSourceDisplay().ordinal()); //$NON-NLS-1$
        setPCGenOption("useHigherLevelSlotsDefault", isUseHigherLevelSlotsDefault()); //$NON-NLS-1$
        setPCGenOption("weaponProfPrintout", SettingsHandler.getWeaponProfPrintout()); //$NON-NLS-1$
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
        } else
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
        if ("".equals(selectedCharacterPDFOutputSheet)) //$NON-NLS-1$
        {
            return ConfigurationSettings.getOutputSheetsDir();
        }

        return new File(selectedCharacterPDFOutputSheet).getParentFile().getAbsolutePath();
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
        final StringBuilder rString = new StringBuilder("<font color="); //$NON-NLS-1$

        if (getPrereqFailColor() != 0)
        {
            rString.append("\"#") //$NON-NLS-1$
                    .append(Integer.toHexString(getPrereqFailColor()))
                    .append("\""); //$NON-NLS-1$
        } else
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
     *
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
     * Set's the ruleCheckMap key to 'Y' or 'N'
     *
     * @param aKey
     * @param aBool
     **/
    public static void setRuleCheck(final String aKey, final boolean aBool)
    {
        String aVal = (aBool) ? "Y" : "N";

        ruleCheckMap.put(aKey, aVal);
    }

    /**
     * Gets this PC's choice on a Rule
     *
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

    /**
     * save the outputsheet location with the PC?
     *
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
     * @param path a string containing the path to the HTML output sheet
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
     * @param aPC
     * @return the {@code selectedCharacterHTMLOutputSheet} property
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
     * @param path a string containing the path to the PDF output sheet
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
     * @param aPC
     * @return the {@code selectedCharacterPDFOutputSheet} property
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
     * @param path a string containing the path to the template
     **/
    public static void setSelectedEqSetTemplate(final String path)
    {
        selectedEqSetTemplate = path;
    }

    /**
     * Returns the current EquipSet template.
     *
     * @return the {@code selectedEqSetTemplate} property
     **/
    public static String getSelectedEqSetTemplate()
    {
        return selectedEqSetTemplate;
    }

    /**
     * Sets the current party HTML template.
     *
     * @param path a string containing the path to the template
     */
    public static void setSelectedPartyHTMLOutputSheet(final String path)
    {
        selectedPartyHTMLOutputSheet = path;
    }

    /**
     * Returns the current party HTML template.
     *
     * @return the {@code selectedPartyHTMLOutputSheet} property
     **/
    public static String getSelectedPartyHTMLOutputSheet()
    {
        return selectedPartyHTMLOutputSheet;
    }

    /**
     * Sets the current party PDF template.
     *
     * @param path a string containing the path to the template
     **/
    public static void setSelectedPartyPDFOutputSheet(final String path)
    {
        selectedPartyPDFOutputSheet = path;
    }

    /**
     * Returns the current party PDF template.
     *
     * @return the {@code selectedPartyPDFOutputSheet} property
     **/
    public static String getSelectedPartyPDFOutputSheet()
    {
        return selectedPartyPDFOutputSheet;
    }

    /**
     * Sets the current Spell output sheet
     *
     * @param path a string containing the path to the template
     **/
    public static void setSelectedSpellSheet(final String path)
    {
        selectedSpellSheet = path;
    }

    /**
     * Returns the current spell output sheet
     *
     * @return the {@code selectedSpellSheet} property
     **/
    public static String getSelectedSpellSheet()
    {
        return selectedSpellSheet;
    }

    /**
     * Sets whether the hit point dialog should be shown at level up.
     *
     * @param argShowHPDialogAtLevelUp Should the hit point dialog be shown at level up?
     */
    public static void setShowHPDialogAtLevelUp(final boolean argShowHPDialogAtLevelUp)
    {
        showHPDialogAtLevelUp = argShowHPDialogAtLevelUp;
    }

    /**
     * Returns whether the hit point dialog should be shown at level up.
     *
     * @return true if the hit point dialog should be shown at level up.
     */
    public static boolean getShowHPDialogAtLevelUp()
    {
        return showHPDialogAtLevelUp;
    }

    /**
     * Sets whether the Stat dialog should be shown at level up.
     *
     * @param argShowStatDialogAtLevelUp Should the Stat dialog should be shown at level up?
     */
    public static void setShowStatDialogAtLevelUp(final boolean argShowStatDialogAtLevelUp)
    {
        showStatDialogAtLevelUp = argShowStatDialogAtLevelUp;
    }

    /**
     * Returns whether the Stat dialog should be shown at level up.
     *
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
     *
     * @param argShowWarningAtFirstLevelUp The argShowWarningAtFirstLevelUp to set
     */
    public static void setShowWarningAtFirstLevelUp(final boolean argShowWarningAtFirstLevelUp)
    {
        SettingsHandler.showWarningAtFirstLevelUp = argShowWarningAtFirstLevelUp;
    }

    /**
     * Returns the showWarningAtFirstLevelUp.
     *
     * @return boolean
     */
    public static boolean isShowWarningAtFirstLevelUp()
    {
        return showWarningAtFirstLevelUp;
    }

    /**
     * Returns the path to the temporary output location (for previews).
     *
     * @return the {@code tempPath} property
     */
    public static File getTempPath()
    {
        return TEMP_PATH;
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
    public static void setUseHigherLevelSlotsDefault(boolean useHigherLevelSlotsDefault)
    {
        SettingsHandler.useHigherLevelSlotsDefault = useHigherLevelSlotsDefault;
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
     *
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

        try (InputStream in = new FileInputStream(optionsLocation))
        {
            getOptions().load(in);
        } catch (IOException e)
        {
            // Not an error, this file may not exist yet
            if (Logging.isDebugMode())
            {
                Logging.debugPrint(LanguageBundle.getString("SettingsHandler.no.options.file")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Puts all properties into the {@code Properties} object,
     * ({@code options}). This is called by
     * {@code writeOptionsProperties}, which then saves the
     * {@code options} into a file.
     *
     * @param optionName
     * @param optionValue
     */
    private static void setPCGenOption(final String optionName, final boolean optionValue)
    {
        setPCGenOption(optionName, optionValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Set most of this objects static properties from the loaded {@code options}.
     * Called by readOptionsProperties. Most of the static properties are
     * set as a side effect, with the main screen size being returned.
     *
     * @param optionName
     * @param defaultValue
     * @return the default {@code Dimension} to set the screen size to
     */
    private static boolean getPCGenOption(final String optionName, final boolean defaultValue)
    {
        final String option = getPCGenOption(optionName, defaultValue ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

        return "true".equalsIgnoreCase(option); //$NON-NLS-1$
    }

    /**
     * Set's the RuleChecks in the options.ini file
     *
     * @param optionName
     **/
    private static void setRuleChecksInOptions(final String optionName)
    {
        StringBuilder value = new StringBuilder(); //$NON-NLS-1$

        for (final Map.Entry<String, String> entry : ruleCheckMap.entrySet())
        {
            final String aKey = entry.getKey();
            final String aVal = entry.getValue();

            if (value.length() == 0)
            {
                value = new StringBuilder(aKey + "|" + aVal); //$NON-NLS-1$
            } else
            {
                value.append(",").append(aKey).append("|").append(aVal); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        //setPCGenOption(optionName, value);
        getOptions().setProperty("pcgen.options." + optionName, value.toString()); //$NON-NLS-1$
    }

    private static void setSaveCustomInLst(final boolean aBool)
    {
        saveCustomInLst = aBool;
    }

    private static boolean isSaveCustomInLst()
    {
        return saveCustomInLst;
    }

    private static String getTmpPath()
    {
        return TMP_PATH;
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
     * aKey|Y,bKey|N,cKey|Y
     *
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
        try (InputStream in = new FileInputStream(FILE_LOCATION))
        {
            FILEPATHS.load(in);
        } catch (IOException e)
        {
            // Not an error, this file may not exist yet
            if (Logging.isDebugMode())
            {
                Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filepaths.ini")); //$NON-NLS-1$
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

        try (InputStream in = new FileInputStream(filterLocation))
        {
            FILTERSETTINGS.load(in);
        } catch (IOException e)
        {
            // Not an error, this file may not exist yet
            if (Logging.isDebugMode())
            {
                Logging.debugPrint(LanguageBundle.getString("SettingsHandler.will.create.filter.ini")); //$NON-NLS-1$
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
