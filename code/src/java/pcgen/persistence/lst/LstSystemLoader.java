/*
 * LstSystemLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.Sponsor;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.PointBuyCost;
import pcgen.core.QualifiedObject;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * ???
 * @deprecated
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class LstSystemLoader implements SystemLoader
{
	private static final FilenameFilter gameModeFileFilter =
			new FilenameFilter()
			{
        @Override
				public boolean accept(File aFile, String aString)
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
							File f = new File(d, "statsandchecks.lst");

							if (f.exists())
							{
								f = new File(d, "miscinfo.lst");

								return f.exists();
							}

							return false;
						}
					}
					catch (SecurityException e)
					{
						Logging.errorPrint("GameModes.listGameFiles", e);
					}

					return false;
				}
			};

	private static UnitSet DEFAULT_UNIT_SET;

	private List<Campaign> loadedCampaigns = new ArrayList<Campaign>();
	private BioSetLoader bioLoader = new BioSetLoader();
	private CampaignLoader campaignLoader = new CampaignLoader();
	private final FilenameFilter pccFileFilter = new FilenameFilter()
	{
        @Override
		public boolean accept(File parentDir, String fileName)
		{
			try
			{
				if (".pcc".regionMatches(true, 0, fileName, fileName.length() - 4, 4))
				{
					URI uri = new File(parentDir,fileName).toURI();

					//Test to avoid reloading existing campaigns, so we can safely
					// call loadPCCFilesInDirectory repeatedly. -rlk 2002-03-30
					if (Globals.getCampaignByURI(uri, false) == null)
					{
						campaignLoader.loadLstFile(null, uri);
					}
				}
				/*
				 * This is a specific "hack" in order to speed loading when
				 * in a development (Subversion-based) environment - Tom
				 * Parker 1/17/07
				 */
				if (!".svn".equals(fileName))
				{
					if (!".lst".regionMatches(true, 0, fileName, fileName.length() - 4, 4))
					{
						File fileInDir = new File(parentDir, fileName);
						if (fileInDir.isDirectory()) {
							loadPCCFilesInDirectory(fileInDir);
						}
					}
				}
			}
			catch (PersistenceLayerException e)
			{
				// LATER: This is not an appropriate way to deal with this exception.
				// Deal with it this way because of the way the loading takes place.  XXX
				logError("PersistanceLayer", e);
			}

			return false;
		}
	};

	private LoadInfoLoader loadInfoLoader = new LoadInfoLoader();
	private EquipSlotLoader eqSlotLoader = new EquipSlotLoader();
	private final Map<String, List<URI>> chosenCampaignSourcefiles =
			new HashMap<String, List<URI>>();
	private LocationLoader locationLoader = new LocationLoader();
	private final Set<URI> loadedFiles = new HashSet<URI>();
	private SimpleLoader<PaperInfo> paperLoader = new SimplePrefixLoader<PaperInfo>(PaperInfo.class, "NAME");
	private PointBuyLoader pointBuyLoader = new PointBuyLoader();
	private SimpleLoader<Sponsor> sponsorLoader = new SimplePrefixLoader<Sponsor>(Sponsor.class, "SPONSOR");
	private SimpleLoader<RuleCheck> ruleCheckLoader = new SimpleLoader<RuleCheck>(RuleCheck.class);
	private SizeAdjustmentLoader sizeLoader = new SizeAdjustmentLoader();
	private StatsAndChecksLoader statCheckLoader = new StatsAndChecksLoader();

	/////////////////////////////////////////////////////////////////
	// Property(s)
	/////////////////////////////////////////////////////////////////
	private TraitLoader traitLoader = new TraitLoader();

	/* (non-Javadoc)
	 * @see pcgen.persistence.SystemLoader#setChosenCampaignSourcefiles(java.util.List, pcgen.core.GameMode)
	 */
    @Override
	public void setChosenCampaignSourcefiles(List<URI> l, GameMode game)
	{
		List<URI> files = chosenCampaignSourcefiles.get(game.getName());
		if (files == null)
		{
			files = new ArrayList<URI>();
			chosenCampaignSourcefiles.put(game.getName(), files);
		}
		files.clear();
		files.addAll(l);
		SettingsHandler.getOptions().setProperty(
			"pcgen.files.chosenCampaignSourcefiles." + game.getName(),
			StringUtil.join(files, ", "));
//		CoreUtility.join(chosenCampaignSourcefiles, ','));
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.SystemLoader#getChosenCampaignSourcefiles(pcgen.core.GameMode)
	 */
    @Override
	public List<URI> getChosenCampaignSourcefiles(GameMode game)
	{
		List<URI> files = chosenCampaignSourcefiles.get(game.getName());
		if (files == null)
		{
			files = new ArrayList<URI>();
			chosenCampaignSourcefiles.put(game.getName(), files);
		}
		return files;
	}

	////////////////////////////////////////////////////////////
	// Private Method(s)
	////////////////////////////////////////////////////////////
    @Override
	public void emptyLists()
	{
    	//TODO No one should be using this! (lists are now elsewhere)
	}

    @Override
	public void initialize() throws PersistenceLayerException
	{
		loadGameModes();
		loadSponsorsLstFile();

		// Load the initial campaigns
		loadPCCFilesInDirectory(ConfigurationSettings.getPccFilesDir());
		loadPCCFilesInDirectory(ConfigurationSettings.getVendorDataDir());

		// Now that those are loaded, make sure to initialize the recursive campaigns
		initRecursivePccFiles();

		Globals.sortPObjectListByName(Globals.getCampaignList());
	}

	/**
	 * Load a sponsors lst file.
	 * First try the game mode directory. If that fails, try
	 * reading the file from the default game mode directory.
	 */
	private void loadSponsorsLstFile()
	{
		File sponsorDir = new File(new File(ConfigurationSettings.getSystemsDir()), "sponsors");
		File sponsorFile = new File(sponsorDir, "sponsors.lst");

		try
		{
			sponsorLoader.loadLstFile(Globals.getGlobalContext(), sponsorFile.toURI(), null);
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint("Warning: sponsors file is missing");
		}
	}

	/**
	 * Get a list of all the directories in system/gameModes/
	 * that contain a file named statsandchecks.lst and miscinfo.lst
	 * @return game files list
	 */
	private static String[] getGameFilesList()
	{
		final String aDirectory =
				new File(ConfigurationSettings.getSystemsDir()) + File.separator
					+ "gameModes" + File.separator;

		return new File(aDirectory).list(gameModeFileFilter);
	}

	private void loadGameModeInfoFile(GameMode gameMode, URI uri, String aType)
	{
		String data;
		try
		{
			data = LstFileLoader.readFromURI(uri).toString();
		}
		catch (PersistenceLayerException ple)
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
			if (((aLine.length() > 0) && (aLine.charAt(0) == '#'))
				|| (aLine.length() == 0))
			{
				continue;
			}

			if (aType.equals("load"))
			{
				gameMode.addLoadString(aLine);
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
				catch (PersistenceLayerException e)
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
		catch (PersistenceLayerException ple)
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
			if (((aLine.length() > 0) && (aLine.charAt(0) == '#'))
					|| (aLine.length() == 0))
			{
				continue;
			}

			if (gameMode == null)
			{
				gameMode = new GameMode(aName);
				SystemCollections.addToGameModeList(gameMode);
				gameMode.getModeContext().ref.importObject(AbilityCategory.FEAT);
			}

			GameModeLoader.parseMiscGameInfoLine(gameMode, aLine, uri, i + 1);
		}
		int[] dieSizes = gameMode.getDieSizes();
		if (dieSizes == null || dieSizes.length == 0)
		{
			final int[]  defaultDieSizes       = new int[]{ 1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000 };
			gameMode.setDieSizes(defaultDieSizes);
			Logging.log(Logging.LST_ERROR, "GameMode (" + gameMode.getName() + ") : MiscInfo.lst did not contain any valid DIESIZES. " 
				+ "Using the system default DIESIZES.");
		}
		addDefaultUnitSet(gameMode);
		addDefaultTabInfo(gameMode);
		return gameMode;
	}

	public static void addDefaultTabInfo(GameMode gameMode)
	{
		LoadContext context = gameMode.getModeContext();
		for (Tab aTab : Tab.values())
		{
			TabInfo ti = context.ref.silentlyGetConstructedCDOMObject(
					TabInfo.class, aTab.toString());
			if (ti == null)
			{
				ti = context.ref.constructCDOMObject(TabInfo.class, aTab
						.toString());
				ti.setTabName(aTab.label());
			}
		}
	}

	public static void addDefaultUnitSet(GameMode gameMode)
	{
		LoadContext context = gameMode.getModeContext();
		UnitSet us = context.ref.silentlyGetConstructedCDOMObject(
				UnitSet.class, Constants.STANDARD_UNITSET_NAME);
		if (us == null)
		{
			gameMode.getModeContext().ref.importObject(getDefaultUnitSet());
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
	public void loadPCCFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(pccFileFilter);
	}

	public void loadPCCFilesInDirectory(File aDirectory)
	{
		aDirectory.list(pccFileFilter);
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
	private void loadGameModeLstFile(LoadContext context, LstLineFileLoader lstFileLoader,
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
	private boolean loadGameModeLstFile(LoadContext context, LstLineFileLoader lstFileLoader,
		String gameModeName, String gameModeFolderName, String lstFileName, final boolean showMissing)
	{
		File gameModeDir = new File(new File(ConfigurationSettings.getSystemsDir()), "gameModes");

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
		catch (PersistenceLayerException ple)
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
		catch (PersistenceLayerException ple2)
		{
			if (showMissing)
			{
				Logging.errorPrint("Warning: game mode " + gameModeName
						+ " is missing file " + lstFileName);
			}
		}
		return false;
	}

	public void loadGameModes()
	{
		final String[] gameFiles = getGameFilesList();

		if ((gameFiles == null) || (gameFiles.length == 0))
		{
			return;
		}

		SystemCollections.clearGameModeList();

		File gameModeDir = new File(new File(ConfigurationSettings.getSystemsDir()), "gameModes");

		for (String gameFile : gameFiles)
		{
			File specGameModeDir = new File(gameModeDir, gameFile);
			File miscInfoFile = new File(specGameModeDir, "miscinfo.lst");
			final GameMode gm =
					loadGameModeMiscInfo(gameFile, miscInfoFile.toURI());
			if (gm != null)
			{
				String gmName = gm.getName();
				SettingsHandler.setGame(gmName);
				LoadContext context = gm.getModeContext();
				loadGameModeInfoFile(gm, new File(specGameModeDir, "level.lst")
						.toURI(), "level");
				loadGameModeInfoFile(gm, new File(specGameModeDir, "rules.lst")
						.toURI(), "rules");

				// Load equipmentslot.lst
				loadGameModeLstFile(context, eqSlotLoader, gmName, gameFile,
					"equipmentslots.lst");

				// Load paperInfo.lst
				loadGameModeLstFile(context, paperLoader, gmName, gameFile, "paperInfo.lst");
				Globals.selectPaper(SettingsHandler.getPCGenOption("paperName", "A4"));

				// Load bio files
				loadGameModeLstFile(context, traitLoader, gmName, gameFile, "bio"
					+ File.separator + "traits.lst");
				loadGameModeLstFile(context, locationLoader, gmName, gameFile, "bio"
					+ File.separator + "locations.lst");

				// Load load.lst and check for completeness
				loadGameModeLstFile(context, loadInfoLoader, gmName, gameFile, "load.lst");

				// Load sizeAdjustment.lst
				loadGameModeLstFile(context, sizeLoader, gmName, gameFile,
					"sizeAdjustment.lst");

				// Load statsandchecks.lst
				loadGameModeLstFile(context, statCheckLoader, gmName, gameFile,
					"statsandchecks.lst");

				// Load pointbuymethods.lst
				loadPointBuyFile(context, gameFile, gmName);

				loadGameModeLstFile(context, bioLoader, gmName, gameFile, "bio"
						+ File.separator + "biosettings.lst");
			}
			try
			{
				addDefaultWieldCategories(gm.getModeContext());
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint("Error Initializing PreParserFactory");
				Logging.errorPrint("  " + ple.getMessage(), ple);
				throw new UnreachableError();
			}
		}

		SystemCollections.sortGameModeList();
	}

	public static void addDefaultWieldCategories(LoadContext context)
			throws PersistenceLayerException
	{
		PreParserFactory prereqParser;

		try
		{
			prereqParser = PreParserFactory.getInstance();
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint("Error Initializing PreParserFactory");
			Logging.errorPrint("  " + ple.getMessage(), ple);
			throw new UnreachableError();
		}

		ReferenceContext refContext = context.ref;
		Collection<WieldCategory> categories = refContext
				.getConstructedCDOMObjects(WieldCategory.class);

		WieldCategory light = null;
		WieldCategory twoHanded = null;
		WieldCategory oneHanded = null;
		WieldCategory tooLarge = null;
		WieldCategory tooSmall = null;

		for (WieldCategory wc : categories)
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
		boolean buildTwoHanded = false;
		boolean buildOneHanded = false;
		boolean buildTooLarge = false;
		boolean buildTooSmall = false;
		if (light == null)
		{
			light = new WieldCategory();
			light.setName("Light");
			refContext.importObject(light);
			buildLight = true;
		}
		if (twoHanded == null)
		{
			twoHanded = new WieldCategory();
			twoHanded.setName("TwoHanded");
			refContext.importObject(twoHanded);
			buildTwoHanded = true;
		}
		if (oneHanded == null)
		{
			oneHanded = new WieldCategory();
			oneHanded.setName("OneHanded");
			refContext.importObject(oneHanded);
			buildOneHanded = true;
		}
		if (tooLarge == null)
		{
			tooLarge = new WieldCategory();
			tooLarge.setName("TooLarge");
			refContext.importObject(tooLarge);
			buildTooLarge = true;
		}
		if (tooSmall == null)
		{
			tooSmall = new WieldCategory();
			tooSmall.setName("TooSmall");
			refContext.importObject(tooSmall);
			buildTooSmall = true;
		}

		if (buildLight)
		{
			light.setHandsRequired(1);
			light.setFinessable(true);
			light.addDamageMult(1, 1.0f);
			light.addDamageMult(2, 1.0f);
			Prerequisite p = prereqParser
					.parse("PREVARLTEQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooSmall));
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(oneHanded));
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(twoHanded));
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+3");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooLarge));
			qo.addPrerequisite(p);
			light.addCategorySwitch(qo);
			light.setWieldCategoryStep(1, CDOMDirectSingleRef.getRef(oneHanded));
			light.setWieldCategoryStep(2, CDOMDirectSingleRef.getRef(twoHanded));
		}
		if (buildTwoHanded)
		{
			twoHanded.setFinessable(false);
			twoHanded.setHandsRequired(2);
			twoHanded.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser
					.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooSmall));
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(light));
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(oneHanded));
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooLarge));
			qo.addPrerequisite(p);
			twoHanded.addCategorySwitch(qo);
			twoHanded.setWieldCategoryStep(-2, CDOMDirectSingleRef
					.getRef(light));
			twoHanded.setWieldCategoryStep( -1, CDOMDirectSingleRef.getRef(oneHanded));
		}
		if (buildOneHanded)
		{
			oneHanded.setHandsRequired(1);
			oneHanded.setFinessable(false);
			oneHanded.addDamageMult(1, 1.0f);
			oneHanded.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser
					.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooSmall));
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(light));
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(twoHanded));
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+2");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooLarge));
			qo.addPrerequisite(p);
			oneHanded.addCategorySwitch(qo);
			oneHanded.setWieldCategoryStep(-1, CDOMDirectSingleRef.getRef(light));
			oneHanded.setWieldCategoryStep(1, CDOMDirectSingleRef.getRef(twoHanded));
		}
		if (buildTooLarge)
		{
			tooLarge.setFinessable(false);
			tooLarge.setHandsRequired(999);
			tooLarge.setWieldCategoryStep(-3, CDOMDirectSingleRef.getRef(light));
			tooLarge.setWieldCategoryStep(-2, CDOMDirectSingleRef.getRef(oneHanded));
			tooLarge.setWieldCategoryStep(-1, CDOMDirectSingleRef.getRef(twoHanded));
			tooLarge.setWieldCategoryStep(0, CDOMDirectSingleRef.getRef(twoHanded));
		}
		if (buildTooSmall)
		{
			tooSmall.setFinessable(false);
			tooSmall.setHandsRequired(2);
			tooSmall.addDamageMult(2, 1.5f);
			Prerequisite p = prereqParser
					.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-3");
			QualifiedObject<CDOMSingleRef<WieldCategory>> qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooSmall));
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-2");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(light));
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVAREQ:EQUIP.SIZE.INT,PC.SIZE.INT-1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(oneHanded));
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			p = prereqParser.parse("PREVARGTEQ:EQUIP.SIZE.INT,PC.SIZE.INT+1");
			qo = new QualifiedObject<CDOMSingleRef<WieldCategory>>(
					CDOMDirectSingleRef.getRef(tooLarge));
			qo.addPrerequisite(p);
			tooSmall.addCategorySwitch(qo);
			tooSmall
					.setWieldCategoryStep( -2, CDOMDirectSingleRef.getRef(light));
			tooSmall.setWieldCategoryStep( -1, CDOMDirectSingleRef.getRef(oneHanded));
		}

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
			catch (PersistenceLayerException e)
			{
				// Ignore - its OK if the file cannot be loaded
			}
		}
		if (useGameModeFile)
		{
			if (!loadGameModeLstFile(context, pointBuyLoader, gmName, gameFile,
				"pointbuymethods.lst", false))
			{
				loadGameModeLstFile(context, pointBuyLoader, gmName, gameFile,
					"pointbuymethods_system.lst", false);
			}
		}
		GameMode mode = SettingsHandler.getGame();
		for (PointBuyCost pbc : context.ref
				.getConstructedCDOMObjects(PointBuyCost.class))
		{
			mode.addPointBuyStatCost(pbc);
		}
	}

	/**
	 * Logs an error taht has occured during data loading.
	 * This will not only log the message to the system error log,
	 * but it will also notify all observers of the error.
	 * @param message the error to notify listeners about
	 * @param e
	 */
	public void logError(String message, Throwable e)
	{
		Logging.errorPrint(message, e);
	}

	public void initRecursivePccFiles() throws PersistenceLayerException
	{
		campaignLoader.initRecursivePccFiles();
	}

    @Override
	public Collection<Campaign> getLoadedCampaigns()
	{
		return new ArrayList<Campaign>(loadedCampaigns);
	}

    @Override
	public boolean isLoaded(Campaign campaign)
	{
		return loadedCampaigns.contains(campaign);
	}

    @Override
	public void markAllUnloaded()
	{
		loadedCampaigns.clear();
	}

}
