/*
 * GameModeFileLoader.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.persistence;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.Collection;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.Sponsor;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.core.CustomData;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.PointBuyCost;
import pcgen.core.QualifiedObject;
import pcgen.core.RuleCheck;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.BioSetLoader;
import pcgen.persistence.lst.EquipIconLoader;
import pcgen.persistence.lst.EquipSlotLoader;
import pcgen.persistence.lst.GameModeLoader;
import pcgen.persistence.lst.LevelLoader;
import pcgen.persistence.lst.LoadInfoLoader;
import pcgen.persistence.lst.LocationLoader;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.persistence.lst.MigrationLoader;
import pcgen.persistence.lst.PointBuyLoader;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.persistence.lst.SimplePrefixLoader;
import pcgen.persistence.lst.SizeAdjustmentLoader;
import pcgen.persistence.lst.StatsAndChecksLoader;
import pcgen.persistence.lst.TraitLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CodeControlLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 *
 */
public class GameModeFileLoader extends PCGenTask
{

	private static final FilenameFilter gameModeFileFilter =
			(aFile, aString) ->
			{
				try
				{
					final File d = new File(aFile, aString);

					if (d.isDirectory())
					{
						// the directory must contain
						// a "miscinfo.lst" file and a
						// "statsandchecks.lst" file to be
						// a complete gameMode
						return new File(d, "statsandchecks.lst").exists() &&
								new File(d, "miscinfo.lst").exists();
					}
				}
				catch (SecurityException e)
				{
					Logging.errorPrint("GameModes.listGameFiles", e);
				}

				return false;
			};

	@Override
	public String getMessage()
	{
		return LanguageBundle.getString("in_taskLoadGameModes"); //$NON-NLS-1$
	}

	@Override
	public void execute()
	{
		String[] gameFiles = GameModeFileLoader.getGameFilesList();
		if ((gameFiles != null) && (gameFiles.length > 0))
		{
			setMaximum(gameFiles.length + 1);
			loadGameModes(gameFiles);
		}
		loadSponsorsLstFile();
	}

	/**
	 * Get a list of all the directories in system/gameModes/
	 * that contain a file named statsandchecks.lst and miscinfo.lst
	 * @return game files list
	 */
	private static String[] getGameFilesList()
	{
		final String aDirectory =
				ConfigurationSettings.getSystemsDir() + File.separator + "gameModes"
						+ File.separator;

		return new File(aDirectory).list(gameModeFileFilter);
	}

	private static UnitSet DEFAULT_UNIT_SET;
	private LstLineFileLoader ruleCheckLoader = new SimpleLoader<>(RuleCheck.class);
	private LstLineFileLoader loadInfoLoader = new LoadInfoLoader();
	private LstLineFileLoader eqSlotLoader = new EquipSlotLoader();
	private LstLineFileLoader paperLoader = new SimplePrefixLoader<>(PaperInfo.class, "NAME");
	private LstLineFileLoader pointBuyLoader = new PointBuyLoader();
	private LstLineFileLoader traitLoader = new TraitLoader();
	private LstLineFileLoader locationLoader = new LocationLoader();
	private LstLineFileLoader sizeLoader = new SizeAdjustmentLoader();
	private LstLineFileLoader statCheckLoader = new StatsAndChecksLoader();
	private LstLineFileLoader migrationLoader = new MigrationLoader();
	private LstLineFileLoader bioLoader = new BioSetLoader();
	private LstLineFileLoader sponsorLoader = new SimplePrefixLoader<>(Sponsor.class, "SPONSOR");
	private LstLineFileLoader equipIconLoader = new EquipIconLoader();
	private LstLineFileLoader codeControlLoader = new CodeControlLoader();

	/**
	 * Load a sponsors lst file.
	 * First try the game mode directory. If that fails, try
	 * reading the file from the default game mode directory.
	 */
	private void loadSponsorsLstFile()
	{
		File sponsorDir = new File(ConfigurationSettings.getSystemsDir(), "sponsors");

		try
		{
			File sponsorFile = new File(sponsorDir, "sponsors.lst");
			sponsorLoader.loadLstFile(Globals.getGlobalContext(), sponsorFile.toURI(), null);
		}
		catch (final PersistenceLayerException ple)
		{
			Logging.errorPrint("Warning: sponsors file is missing");
		}
	}

	private void loadGameModes(String[] gameFiles)
	{

		SystemCollections.clearGameModeList();
		File gameModeDir = new File(ConfigurationSettings.getSystemsDir(), "gameModes");
		int progress = 0;
		for (final String gameFile : gameFiles)
		{
			File specGameModeDir = new File(gameModeDir, gameFile);
			File miscInfoFile = new File(specGameModeDir, "miscinfo.lst");
			final GameMode gm = GameModeFileLoader.loadGameModeMiscInfo(gameFile, miscInfoFile.toURI());
			if (gm != null)
			{
				String gmName = gm.getName();
				//SettingsHandler.setGame(gmName);
				LoadContext context = gm.getModeContext();
				loadGameModeInfoFile(gm, new File(specGameModeDir, "level.lst").toURI(), "level");
				loadGameModeInfoFile(gm, new File(specGameModeDir, "rules.lst").toURI(), "rules");

				// Load equipmentslot.lst
				GameModeFileLoader.loadGameModeLstFile(context, eqSlotLoader, gmName, gameFile,
									"equipmentslots.lst");

				// Load paperInfo.lst
				GameModeFileLoader.loadGameModeLstFile(context, paperLoader, gmName, gameFile, "paperInfo.lst");

				// Load bio files
				GameModeFileLoader.loadGameModeLstFile(context, traitLoader, gmName, gameFile, "bio" + File.separator +
						"traits.lst");
				GameModeFileLoader.loadGameModeLstFile(context, locationLoader, gmName, gameFile, "bio" +
						File.separator + "locations.lst");

				// Load load.lst and check for completeness
				GameModeFileLoader.loadGameModeLstFile(context, loadInfoLoader, gmName, gameFile, "load.lst");

				// Load sizeAdjustment.lst
				GameModeFileLoader.loadGameModeLstFile(context, sizeLoader, gmName, gameFile,
									"sizeAdjustment.lst");

				// Load statsandchecks.lst
				GameModeFileLoader.loadGameModeLstFile(context, statCheckLoader, gmName, gameFile,
									"statsandchecks.lst");

				// Load equipIcons.lst
				GameModeFileLoader.loadGameModeLstFile(context, equipIconLoader, gmName, gameFile,
					"equipIcons.lst");

				GameModeFileLoader.loadGameModeLstFile(context, codeControlLoader, gmName, gameFile,
						"codeControl.lst");
				
				// Load pointbuymethods.lst
				loadPointBuyFile(context, gameFile, gmName);
				for (final PointBuyCost pbc : context.getReferenceContext().getConstructedCDOMObjects(PointBuyCost.class))
				{
					gm.addPointBuyStatCost(pbc);
				}

				// Load migration.lst
				GameModeFileLoader.loadGameModeLstFile(context, migrationLoader, gmName, gameFile,
					"migration.lst");

				GameModeFileLoader.loadGameModeLstFile(context, bioLoader, gmName, gameFile, "bio" + File.separator
						+ "biosettings.lst");
			}
			try
			{
				GameModeFileLoader.addDefaultWieldCategories(gm.getModeContext());
			}
			catch (final PersistenceLayerException ple)
			{
				Logging.errorPrint("Error Initializing PreParserFactory");
				Logging.errorPrint("  " + ple.getMessage(), ple);
				throw new UnreachableError();
			}

			progress++;
			setProgress(progress);
		}

		SystemCollections.sortGameModeList();
	}

	/**
	 * Load a game mode file.
	 * First try the game mode directory. If that fails, try
	 * reading the file from the default game mode directory.
	 * @param lstFileLoader the Loader object for the type of file.
	 * @param gameModeName the game mode
	 * @param gameModeFolderName the name of the folder that the game mode is located in
	 * @param lstFileName the lst file to load
	 */
	private static void loadGameModeLstFile(LoadContext context, LstLineFileLoader lstFileLoader,
	                                        String gameModeName, String gameModeFolderName, String lstFileName)
	{
		loadGameModeLstFile(context, lstFileLoader, gameModeName, gameModeFolderName, lstFileName, true);
	}

	/**
	 * Load a game mode file.
	 * First try the game mode directory. If that fails, try
	 * reading the file from the default game mode directory.
	 * @param lstFileLoader the Loader object for the type of file.
	 * @param gameModeName the game mode
	 * @param gameModeFolderName the name of the folder that the game mode is located in
	 * @param lstFileName the lst file to load
	 * @param showMissing show the missing file as a warning. Some files are optional and shouldn't generate a warning
	 * @return true if the file was loaded, false if it was missing.
	 */
	private static boolean loadGameModeLstFile(LoadContext context, LstLineFileLoader lstFileLoader,
	                                           String gameModeName, String gameModeFolderName, String lstFileName, final boolean showMissing)
	{
		File gameModeDir = new File(ConfigurationSettings.getSystemsDir(), "gameModes");

		try
		{
			File specGameModeDir = new File(gameModeDir, gameModeFolderName);
			File gameModeFile = new File(specGameModeDir, lstFileName);
			if (gameModeFile.exists())
			{
				lstFileLoader.loadLstFile(context, gameModeFile.toURI(), gameModeName);
				return true;
			}
		}
		catch (final PersistenceLayerException ple)
		{
			//This is OK, grab the default
		}

		try
		{
			File specGameModeDir = new File(gameModeDir, "default");
			File gameModeFile = new File(specGameModeDir, lstFileName);
			if (gameModeFile.exists())
			{
				lstFileLoader.loadLstFile(context, gameModeFile.toURI(), gameModeName);
				return true;
			}
		}
		catch (final PersistenceLayerException ple2)
		{
			if (showMissing)
			{
				Logging.errorPrint("Warning: game mode " + gameModeName + " is missing file "
						+ lstFileName);
			}
		}
		return false;
	}

	private void loadGameModeInfoFile(GameMode gameMode, URI uri, String aType)
	{
		String data;
		try
		{
			data = LstFileLoader.readFromURI(uri).toString();
		}
		catch (final PersistenceLayerException ple)
		{
			Logging.errorPrint(LanguageBundle.getFormattedString(
					"Errors.LstSystemLoader.loadGameModeInfoFile", //$NON-NLS-1$
					uri, ple.getMessage()));
			return;
		}

		String[] fileLines = data.split(LstFileLoader.LINE_SEPARATOR_REGEXP);
		String xpTable = "";
		for (int i = 0; i < fileLines.length; i++)
		{
			String aLine = fileLines[i];

			// Ignore commented-out and empty lines
			if (((!aLine.isEmpty()) && (aLine.charAt(0) == '#')) || (aLine.isEmpty()))
			{
				continue;
			}

			if (aType.equals("load"))
			{
				gameMode.addLoadString(aLine.intern());
			}
			else if (aType.equals("level"))
			{
				xpTable = LevelLoader.parseLine(gameMode, aLine, i + 1, uri, xpTable);
			}
			else if (aType.equals("rules"))
			{
				try
				{
					ruleCheckLoader.parseLine(gameMode.getModeContext(), aLine, uri);
				}
				catch (final PersistenceLayerException e)
				{
					Logging.errorPrint(LanguageBundle.getFormattedString(
							"Errors.LstSystemLoader.loadGameModeInfoFile", //$NON-NLS-1$
							uri, e.getMessage()));
				}
			}
		}
	}

	private static GameMode loadGameModeMiscInfo(String aName, URI uri)
	{
		GameMode gameMode = null;
		String data;
		try
		{
			data = LstFileLoader.readFromURI(uri).toString();
		}
		catch (final PersistenceLayerException ple)
		{
			Logging.errorPrint(LanguageBundle.getFormattedString(
					"Errors.LstSystemLoader.loadGameModeInfoFile", //$NON-NLS-1$
					uri, ple.getMessage()));
			return gameMode;
		}

		String[] fileLines = data.split(LstFileLoader.LINE_SEPARATOR_REGEXP);

		for (int i = 0; i < fileLines.length; i++)
		{
			String aLine = fileLines[i];

			// Ignore commented-out and empty lines
			if (((!aLine.isEmpty()) && (aLine.charAt(0) == '#')) || aLine.isEmpty())
			{
				continue;
			}

			if (gameMode == null)
			{
				gameMode = new GameMode(aName);
				SystemCollections.addToGameModeList(gameMode);
				gameMode.getModeContext().getReferenceContext().importObject(AbilityCategory.FEAT);
			}

			GameModeLoader.parseMiscGameInfoLine(gameMode, aLine, uri, i + 1);
		}
		
		// Record how the FEAT category was configured
		AbilityCategory feat = new AbilityCategory();
		feat.copyFields(AbilityCategory.FEAT);
		gameMode.setFeatTemplate(feat);

		int[] dieSizes = gameMode.getDieSizes();
		if (dieSizes == null || dieSizes.length == 0)
		{
			final int[] defaultDieSizes = {
				1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000
			};
			gameMode.setDieSizes(defaultDieSizes);
			Logging.log(Logging.LST_ERROR, "GameMode (" + gameMode.getName() +
					") : MiscInfo.lst did not contain any valid DIESIZES. " +
					"Using the system default DIESIZES.");
		}
		addDefaultUnitSet(gameMode);
		addDefaultTabInfo(gameMode);
		gameMode.applyPreferences();
		return gameMode;
	}

	/**
	 * Load the purchase mode/point buy definitions from either the new location
	 * under the custom sources folder, or in the old location with the game
	 * mode.
	 *
	 * @param gameFile
	 *            The location of the game mode directory.
	 * @param gmName
	 *            The name of the game mode being loaded.
	 */
	private void loadPointBuyFile(LoadContext context, String gameFile, String gmName)
	{
		File pointBuyFile =
				new File(CustomData.customPurchaseModeFilePath(true, gmName));
		boolean useGameModeFile = true;
		if (pointBuyFile.exists())
		{
			try
			{
				pointBuyLoader.loadLstFile(context, pointBuyFile.toURI(), gmName);
				useGameModeFile = false;
			}
			catch (final PersistenceLayerException e)
			{
				// Ignore - its OK if the file cannot be loaded
			}
		}
		if (useGameModeFile)
		{
			if (!GameModeFileLoader.loadGameModeLstFile(context, pointBuyLoader, gmName, gameFile,
									 "pointbuymethods.lst", false))
			{
				GameModeFileLoader.loadGameModeLstFile(context, pointBuyLoader, gmName, gameFile,
									"pointbuymethods_system.lst", false);
			}
		}
	}

	public static void addDefaultUnitSet(GameMode gameMode)
	{
		LoadContext context = gameMode.getModeContext();
		UnitSet us = context.getReferenceContext().silentlyGetConstructedCDOMObject(
				UnitSet.class, Constants.STANDARD_UNITSET_NAME);
		if (us == null)
		{
			gameMode.getModeContext().getReferenceContext().importObject(getDefaultUnitSet());
		}
	}

	private static synchronized UnitSet getDefaultUnitSet()
	{
		if (DEFAULT_UNIT_SET == null)
		{
			// create default Unit Set in case none is specified in the game mode
			DEFAULT_UNIT_SET = new UnitSet();
			DEFAULT_UNIT_SET.setName(Constants.STANDARD_UNITSET_NAME);
			DEFAULT_UNIT_SET.setInternal(true);
			DEFAULT_UNIT_SET.setHeightUnit(Constants.STANDARD_UNITSET_HEIGHT_UNIT);
			DEFAULT_UNIT_SET.setHeightFactor(Constants.STANDARD_UNITSET_HEIGHT_FACTOR);
			DEFAULT_UNIT_SET.setHeightDisplayPattern(Constants.STANDARD_UNITSET_HEIGHT_DISPLAY_PATTERN);
			DEFAULT_UNIT_SET.setDistanceUnit(Constants.STANDARD_UNITSET_DISTANCE_UNIT);
			DEFAULT_UNIT_SET.setDistanceFactor(Constants.STANDARD_UNITSET_DISTANCE_FACTOR);
			DEFAULT_UNIT_SET.setDistanceDisplayPattern(Constants.STANDARD_UNITSET_DISTANCE_DISPLAY_PATTERN);
			DEFAULT_UNIT_SET.setWeightUnit(Constants.STANDARD_UNITSET_WEIGHT_UNIT);
			DEFAULT_UNIT_SET.setWeightFactor(Constants.STANDARD_UNITSET_WEIGHT_FACTOR);
			DEFAULT_UNIT_SET.setWeightDisplayPattern(Constants.STANDARD_UNITSET_WEIGHT_DISPLAY_PATTERN);
		}
		return DEFAULT_UNIT_SET;
	}

	public static void addDefaultTabInfo(GameMode gameMode)
	{
		LoadContext context = gameMode.getModeContext();
		for (final Tab aTab : Tab.values())
		{
			TabInfo ti = context.getReferenceContext().silentlyGetConstructedCDOMObject(
					TabInfo.class, aTab.toString());
			if (ti == null)
			{
				ti = context.getReferenceContext().constructCDOMObject(TabInfo.class, aTab.toString());
				ti.setTabName(aTab.label());
			}
		}
	}

	public static void addDefaultWieldCategories(LoadContext context)
			throws PersistenceLayerException
	{
		PreParserFactory prereqParser;

		try
		{
			prereqParser = PreParserFactory.getInstance();
		}
		catch (final PersistenceLayerException ple)
		{
			Logging.errorPrint("Error Initializing PreParserFactory");
			Logging.errorPrint("  " + ple.getMessage(), ple);
			throw new UnreachableError(ple);
		}

		AbstractReferenceContext refContext = context.getReferenceContext();
		Collection<WieldCategory> categories = refContext.getConstructedCDOMObjects(WieldCategory.class);

		WieldCategory light = null;
		WieldCategory twoHanded = null;
		WieldCategory oneHanded = null;
		WieldCategory tooLarge = null;
		WieldCategory tooSmall = null;

		for (final WieldCategory wc : categories)
		{
			String name = wc.getKeyName();
			if ("Light".equalsIgnoreCase(name))
			{
				light = wc;
			}
			if ("TwoHanded".equalsIgnoreCase(name))
			{
				twoHanded = wc;
			}
			if ("OneHanded".equalsIgnoreCase(name))
			{
				oneHanded = wc;
			}
			if ("TooLarge".equalsIgnoreCase(name))
			{
				tooLarge = wc;
			}
			if ("TooSmall".equalsIgnoreCase(name))
			{
				tooSmall = wc;
			}
		}
		boolean buildLight = false;
		if (light == null)
		{
			light = new WieldCategory();
			light.setName("Light");
			refContext.importObject(light);
			buildLight = true;
		}
		boolean buildTwoHanded = false;
		if (twoHanded == null)
		{
			twoHanded = new WieldCategory();
			twoHanded.setName("TwoHanded");
			refContext.importObject(twoHanded);
			buildTwoHanded = true;
		}
		boolean buildOneHanded = false;
		if (oneHanded == null)
		{
			oneHanded = new WieldCategory();
			oneHanded.setName("OneHanded");
			refContext.importObject(oneHanded);
			buildOneHanded = true;
		}
		boolean buildTooLarge = false;
		if (tooLarge == null)
		{
			tooLarge = new WieldCategory();
			tooLarge.setName("TooLarge");
			refContext.importObject(tooLarge);
			buildTooLarge = true;
		}
		boolean buildTooSmall = false;
		if (tooSmall == null)
		{
			tooSmall = new WieldCategory();
			tooSmall.setName("TooSmall");
			refContext.importObject(tooSmall);
			buildTooSmall = true;
		}

		CDOMDirectSingleRef<WieldCategory> tooSmallRef = CDOMDirectSingleRef.getRef(tooSmall);
		CDOMDirectSingleRef<WieldCategory> lightRef = CDOMDirectSingleRef.getRef(light);
		CDOMDirectSingleRef<WieldCategory> oneHandedRef = CDOMDirectSingleRef.getRef(oneHanded);
		CDOMDirectSingleRef<WieldCategory> twoHandedRef = CDOMDirectSingleRef.getRef(twoHanded);
		CDOMDirectSingleRef<WieldCategory> tooLargeRef = CDOMDirectSingleRef.getRef(tooLarge);
		if (buildLight)
		{
			light.setHandsRequired(1);
			light.setFinessable(true);
			light.addDamageMult(1, 1.0f);
			light.addDamageMult(2, 1.0f);
			Prerequisite p = prereqParser.parse("PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(
					tooSmallRef);
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<>(
					oneHandedRef);
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
			qo = new QualifiedObject<>(
					twoHandedRef);
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+3");
			qo = new QualifiedObject<>(
					tooLargeRef);
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			light.setWieldCategoryStep(1, oneHandedRef);
			light.setWieldCategoryStep(2, twoHandedRef);
		}
		if (buildTwoHanded)
		{
			twoHanded.setFinessable(false);
			twoHanded.setHandsRequired(2);
			twoHanded.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(
					tooSmallRef);
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			qo = new QualifiedObject<>(
					lightRef);
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<>(
					oneHandedRef);
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<>(
					tooLargeRef);
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			twoHanded.setWieldCategoryStep(-2, lightRef);
			twoHanded.setWieldCategoryStep(-1, oneHandedRef);
		}
		if (buildOneHanded)
		{
			oneHanded.setHandsRequired(1);
			oneHanded.setFinessable(false);
			oneHanded.addDamageMult(1, 1.0f);
			oneHanded.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(
					tooSmallRef);
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<>(
					lightRef);
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<>(
					twoHandedRef);
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
			qo = new QualifiedObject<>(
					tooLargeRef);
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			oneHanded.setWieldCategoryStep(-1, lightRef);
			oneHanded.setWieldCategoryStep(1, twoHandedRef);
		}
		if (buildTooLarge)
		{
			tooLarge.setFinessable(false);
			tooLarge.setHandsRequired(999);
			tooLarge.setWieldCategoryStep(-3, lightRef);
			tooLarge.setWieldCategoryStep(-2, oneHandedRef);
			tooLarge.setWieldCategoryStep(-1, twoHandedRef);
			tooLarge.setWieldCategoryStep(0, twoHandedRef);
		}
		if (buildTooSmall)
		{
			tooSmall.setFinessable(false);
			tooSmall.setHandsRequired(2);
			tooSmall.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<>(
					tooSmallRef);
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			qo = new QualifiedObject<>(
					lightRef);
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<>(
					oneHandedRef);
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<>(
					tooLargeRef);
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			tooSmall.setWieldCategoryStep(-2, lightRef);
			tooSmall.setWieldCategoryStep(-1, oneHandedRef);
		}

	}

}
