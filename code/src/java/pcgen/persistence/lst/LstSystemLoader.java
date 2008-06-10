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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.Deity;
import pcgen.core.Description;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SourceEntry;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.gui.pcGenGUI;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * ???
 *
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class LstSystemLoader extends Observable implements SystemLoader,
		Observer
{
	private static final FilenameFilter gameModeFileFilter =
			new FilenameFilter()
			{
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

	private AbilityCategoryLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private BioSetLoader bioLoader = new BioSetLoader();
	private CampaignLoader campaignLoader = new CampaignLoader();
	private final CampaignSourceEntry globalCampaign;
	private GenericLoader<Deity> deityLoader = new GenericLoader<Deity>(Deity.class);
	private DomainLoader domainLoader = new DomainLoader();
	private AbilityLoader abilityLoader = new AbilityLoader();
	private FeatLoader featLoader = new FeatLoader();
	private final FilenameFilter pccFileFilter = new FilenameFilter()
	{
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
						campaignLoader.loadLstFile(uri);
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

	private LanguageLoader languageLoader = new LanguageLoader();
	private LoadInfoLoader loadInfoLoader = new LoadInfoLoader();
	private UnitSetLoader unitSetLoader = new UnitSetLoader();
	private EquipSlotLoader eqSlotLoader = new EquipSlotLoader();
	private final List<CampaignSourceEntry> bioSetFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<URI> chosenCampaignSourcefiles =
			new ArrayList<URI>();
	private final List<CampaignSourceEntry> classFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> classSkillFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> classSpellFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> companionmodFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> deityFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> domainFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> equipmentFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> equipmodFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> abilityCategoryFileList =
		new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> abilityFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> featFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> kitFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> languageFileList =
			new ArrayList<CampaignSourceEntry>();
	private List<URI> licenseFiles = new ArrayList<URI>();
	private final List<CampaignSourceEntry> lstExcludeFiles =
			new ArrayList<CampaignSourceEntry>();

	private final List<CampaignSourceEntry> raceFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<String> reqSkillFileList = new ArrayList<String>();
	private final List<CampaignSourceEntry> skillFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> spellFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> templateFileList =
			new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> weaponProfFileList =
		new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> armorProfFileList =
		new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> shieldProfFileList =
		new ArrayList<CampaignSourceEntry>();
	private LocationLoader locationLoader = new LocationLoader();
	private final Set<URI> loadedFiles = new HashSet<URI>();
	private PCClassLoader classLoader = new PCClassLoader();
	private PCTemplateLoader templateLoader = new PCTemplateLoader();
	private EquipmentLoader equipmentLoader = new EquipmentLoader();
	private EquipmentModifierLoader eqModLoader = new EquipmentModifierLoader();
	private CompanionModLoader companionModLoader = new CompanionModLoader();
	private KitLoader kitLoader = new KitLoader();
	private PaperInfoLoader paperLoader = new PaperInfoLoader();
	private PointBuyLoader pointBuyLoader = new PointBuyLoader();
	private SponsorLoader sponsorLoader = new SponsorLoader();
	private RaceLoader raceLoader = new RaceLoader();
	private final Set<String> sourcesSet = new TreeSet<String>();
	private SizeAdjustmentLoader sizeLoader = new SizeAdjustmentLoader();
	private GenericLoader<Skill> skillLoader = new GenericLoader<Skill>(Skill.class);
	private SpellLoader spellLoader = new SpellLoader();
	private StatsAndChecksLoader statCheckLoader = new StatsAndChecksLoader();

	/////////////////////////////////////////////////////////////////
	// Property(s)
	/////////////////////////////////////////////////////////////////
	private StringBuffer licensesToDisplayString = new StringBuffer();
	private StringBuffer matureCampaigns = new StringBuffer();
	private TraitLoader traitLoader = new TraitLoader();
	private GenericLoader<WeaponProf> wProfLoader = new GenericLoader<WeaponProf>(WeaponProf.class);
	private GenericLoader<ArmorProf> aProfLoader = new GenericLoader<ArmorProf>(ArmorProf.class);
	private GenericLoader<ShieldProf> sProfLoader = new GenericLoader<ShieldProf>(ShieldProf.class);
	private boolean customItemsLoaded = false;
	private boolean showD20 = false;
	private boolean showLicensed = true;
	private boolean showMature = false;
	private boolean showOGL = false;

	////////////////////////////////////////////////////////////
	// Constructor(s)
	////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of LstSystemLoader
	 */
	public LstSystemLoader()
	{
		//featLoader.addObserver(this);
		abilityCategoryLoader.addObserver(this);
		bioLoader.addObserver(this);
		campaignLoader.addObserver(this);
		companionModLoader.addObserver(this);
		deityLoader.addObserver(this);
		domainLoader.addObserver(this);
		equipmentLoader.addObserver(this);
		eqModLoader.addObserver(this);
		eqSlotLoader.addObserver(this);
		abilityLoader.addObserver(this);
		featLoader.addObserver(this);
		kitLoader.addObserver(this);
		languageLoader.addObserver(this);
		locationLoader.addObserver(this);
		classLoader.addObserver(this);
		paperLoader.addObserver(this);
		pointBuyLoader.addObserver(this);
		sponsorLoader.addObserver(this);
		raceLoader.addObserver(this);
		sizeLoader.addObserver(this);
		skillLoader.addObserver(this);
		spellLoader.addObserver(this);
		statCheckLoader.addObserver(this);
		templateLoader.addObserver(this);
		traitLoader.addObserver(this);
		wProfLoader.addObserver(this);
		aProfLoader.addObserver(this);
		sProfLoader.addObserver(this);
		try {
			globalCampaign =
				new CampaignSourceEntry(new Campaign(),
						new URI("file:/System%20Configuration%20Document"));
		} catch (URISyntaxException e) {
			throw new UnreachableError(e);
		}

	}

	public void setChosenCampaignSourcefiles(List<URI> l)
	{
		chosenCampaignSourcefiles.clear();
		chosenCampaignSourcefiles.addAll(l);
		SettingsHandler.getOptions().setProperty(
			"pcgen.files.chosenCampaignSourcefiles",
			StringUtil.join(chosenCampaignSourcefiles, ", "));
//		CoreUtility.join(chosenCampaignSourcefiles, ','));
	}

	public List<URI> getChosenCampaignSourcefiles()
	{
		return chosenCampaignSourcefiles;
	}

	////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	////////////////////////////////////////////////////////////
	/**
	 * @param argLoaded
	 *
	 */
	public void setCustomItemsLoaded(boolean argLoaded)
	{
		customItemsLoaded = argLoaded;
	}

	public boolean isCustomItemsLoaded()
	{
		return customItemsLoaded;
	}

	public Set<String> getSources()
	{
		return sourcesSet;
	}

	////////////////////////////////////////////////////////////
	// Private Method(s)
	////////////////////////////////////////////////////////////
	public void emptyLists()
	{
		loadedFiles.clear();
		chosenCampaignSourcefiles.clear();
		licensesToDisplayString = new StringBuffer();
		matureCampaigns = new StringBuffer();
		//Globals.getBioSet().clearUserMap();

		releaseFileData();
		//bioSet.clearUserMap();

		customItemsLoaded = false;
	}

	public void initialize() throws PersistenceLayerException
	{
		loadGameModes();
		loadSponsorsLstFile();

		// Load the initial campaigns
		loadPCCFilesInDirectory(SettingsHandler.getPccFilesLocation()
			.getAbsolutePath());
		loadPCCFilesInDirectory(SettingsHandler.getPcgenVendorDataDir()
			.getAbsolutePath());

		// Now that those are loaded, make sure to initialize the recursive campaigns
		campaignLoader.initRecursivePccFiles();

		Globals.sortPObjectListByName(Globals.getCampaignList());
	}

	/**
	 * Load a sponsors lst file.
	 * First try the game mode directory. If that fails, try
	 * reading the file from the default game mode directory.
	 */
	private void loadSponsorsLstFile()
	{
		File sponsorDir = new File(SettingsHandler.getPcgenSystemDir(), "sponsors");
		File sponsorFile = new File(sponsorDir, "sponsors.lst");

		try
		{
			sponsorLoader.loadLstFile(sponsorFile.toURI(), null);
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint("Warning: sponsors file is missing");
		}
	}

	/**
	 * @see pcgen.persistence.SystemLoader#loadCampaigns(List)
	 */
	public void loadCampaigns(final List<Campaign> aSelectedCampaignsList)
		throws PersistenceLayerException
	{
		sourcesSet.clear();
		licenseFiles.clear();

		if (aSelectedCampaignsList.size() == 0)
		{
			throw new PersistenceLayerException(
				"You must select at least one campaign to load.");
		}

		// 21 Nov 2002: Put load inside a try/finally block to make sure
		// that file lines were cleared even if an exception occurred.
		// -- sage_sam
		try
		{
			// The first thing we need to do is load the
			// correct statsandchecks.lst file for this gameMode
			if (SettingsHandler.getGame() != null)
			{
				File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");
				File specificGameModeDir = new File(gameModeDir, SettingsHandler.getGame().getFolderName());
				File statsAndChecks = new File(specificGameModeDir, "statsandchecks.lst");
				statCheckLoader.loadLstFile(statsAndChecks.toURI());
			}
			else
			{
				// Autoload campaigns is set but there
				// is no current gameMode, so just return
				return;
			}

			// Sort the campaigns
			sortCampaignsByRank(aSelectedCampaignsList);

			// Read the campaigns
			readPccFiles(aSelectedCampaignsList, null);

			// Add custom campaign files at the start of the lists
			addCustomFilesToStartOfList();

			// Notify our observers of how many files we intend
			// to load in total so that they can set up any
			// progress meters that they want to.
			setChanged();
			notifyObservers(Integer.valueOf(countTotalFilesToLoad()));

			// Load using the new LstFileLoaders

			// load ability categories first as they used to only be at the game mode
			abilityCategoryLoader.loadLstFiles(abilityCategoryFileList);

			LoadContext context = Globals.getContext();
			for (PCAlignment al : SettingsHandler.getGame()
					.getUnmodifiableAlignmentList())
			{
				context.ref.registerAbbreviation(al, al.getKeyName());
			}
			for (PCStat st : SettingsHandler.getGame()
					.getUnmodifiableStatList())
			{
				context.ref.registerAbbreviation(st, st.getAbb());
			}
			for (SizeAdjustment sz : SettingsHandler.getGame()
					.getUnmodifiableSizeAdjustmentList())
			{
				context.ref.registerAbbreviation(sz, sz.getAbbreviation());
			}
			
			// load weapon profs first
			wProfLoader.loadLstFiles(context, weaponProfFileList);
			aProfLoader.loadLstFiles(context, armorProfFileList);
			sProfLoader.loadLstFiles(context, shieldProfFileList);

			// load skills before classes to handle class skills
			skillLoader.loadLstFiles(context, skillFileList);

			// load before races to handle auto known languages
			languageLoader.loadLstFiles(context, languageFileList);

			// load before race or class to handle feats
			featLoader.loadLstFiles(context, featFileList);

			// load before race or class to handle abilities
			abilityLoader.loadLstFiles(context, abilityFileList);

			raceLoader.loadLstFiles(context, raceFileList);

			//Domain must load before CLASS - thpr 10/29/06
			domainLoader.loadLstFiles(context, domainFileList);

			spellLoader.loadLstFiles(context, spellFileList);
			deityLoader.loadLstFiles(context, deityFileList);

			classLoader.loadLstFiles(context, classFileList);
			
			templateLoader.loadLstFiles(context, templateFileList);
			
			// loaded before equipment (required)
			eqModLoader.loadLstFiles(context, equipmodFileList);

			equipmentLoader.loadLstFiles(context, equipmentFileList);
			companionModLoader.loadLstFiles(context, companionmodFileList);
			kitLoader.loadLstFiles(context, kitFileList);
			
			// Load the bio settings files
			bioLoader.loadLstFiles(bioSetFileList);

			// Check for the default deities
			checkRequiredDeities(context);

			// Add default EQ mods
			eqModLoader.addDefaultEquipmentMods(context);
			
			// Load custom items
			loadCustomItems();

			// Check for valid race types
			//			checkRaceTypes();

			// Verify weapons are melee or ranged
			verifyWeaponsMeleeOrRanged();
			verifyFavClassSyntax();

			//  Auto-gen additional equipment
			if (!SettingsHandler.wantToLoadMasterworkAndMagic())
			{
				EquipmentList.autoGenerateEquipment();
			}
			
			context.ref.buildDeferredObjects();
			context.ref.buildDerivedObjects();
			context.ref.validate();
			context.resolveReferences();
			context.buildTypeLists();

			//  Show the licenses
			showLicensesIfNeeded();
			showSponsorsIfNeeded();
		}
		catch (Throwable thr)
		{
			Logging.errorPrint("Exception loading files.", thr);
		}
		finally
		{
			releaseFileData();
			setChanged();
			notifyObservers("DONE");
		}
	}

	/**
	 * @return total files to load
	 */
	private int countTotalFilesToLoad()
	{
		int count = bioSetFileList.size();
		count += abilityCategoryFileList.size();
		count += classFileList.size();
		count += classSkillFileList.size();
		count += classSpellFileList.size();
		count += companionmodFileList.size();
		count += deityFileList.size();
		count += domainFileList.size();
		count += equipmentFileList.size();
		count += equipmodFileList.size();
		count += abilityFileList.size();
		count += featFileList.size();
		count += kitFileList.size();
		count += languageFileList.size();
		count += raceFileList.size();
		count += skillFileList.size();
		count += spellFileList.size();
		count += templateFileList.size();
		count += weaponProfFileList.size();
		count += armorProfFileList.size();
		count += shieldProfFileList.size();

		return count;
	}

	/**
	 * @see pcgen.persistence.SystemLoader#loadModItems(boolean)
	 */
	public void loadModItems(boolean flagDisplayError)
	{
		//No work to do
	}

	/**
	 * This just calls loadPCCFilesInDirectory.
	 * Note:  This only handles added campaigns right now, not removed ones
	 * <p/>
	 * author Ryan Koppenhaver <rlkoppenhaver@yahoo.com>
	 *
	 * @see pcgen.persistence.PersistenceManager#refreshCampaigns
	 */
	public void refreshCampaigns()
	{
		Globals.clearCampaignsForRefresh();
		loadPCCFilesInDirectory(SettingsHandler.getPccFilesLocation()
			.getAbsolutePath());
		loadPCCFilesInDirectory(SettingsHandler.getPcgenVendorDataDir()
			.getAbsolutePath());
	}

	/**
	 * Sets the options specified in the campaign aCamp.
	 * @param aCamp
	 */
	private static void setCampaignOptions(Campaign aCamp)
	{
		final Properties options = aCamp.getOptions();

		if (options != null)
		{
			for (Enumeration<?> e = options.propertyNames(); e
				.hasMoreElements();)
			{
				final String key = (String) e.nextElement();
				final String value = options.getProperty(key);
				SettingsHandler.setPCGenOption(key, value);
			}
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
				SettingsHandler.getPcgenSystemDir() + File.separator
					+ "gameModes" + File.separator;

		return new File(aDirectory).list(gameModeFileFilter);
	}

	private void addCustomFilesToStartOfList()
	{
		CampaignSourceEntry tempSource = null;

		// The dummy campaign for custom data.
		Campaign customCampaign = new Campaign();
		customCampaign.setName("Custom");
		customCampaign.addDescription(new Description("Custom data"));

		//
		// Add the custom bioset file to the start of the list if it exists
		//
		File bioSetFile = new File(CustomData.customBioSetFilePath(true));
		if (bioSetFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, bioSetFile
					.toURI());
			bioSetFileList.remove(tempSource);
			bioSetFileList.add(0, tempSource);
		}

		//
		// Add the custom class file to the start of the list if it exists
		//
		File classFile = new File(CustomData.customClassFilePath(true));
		if (classFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, classFile
					.toURI());
			classFileList.remove(tempSource);
			classFileList.add(0, tempSource);
		}

		//
		// Add the custom deity file to the start of the list if it exists
		//
		File deityFile = new File(CustomData.customDeityFilePath(true));
		if (deityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, deityFile
					.toURI());
			deityFileList.remove(tempSource);
			deityFileList.add(0, tempSource);
		}

		//
		// Add the custom domain file to the start of the list if it exists
		//
		File domainFile = new File(CustomData.customDomainFilePath(true));
		if (domainFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, domainFile
					.toURI());
			domainFileList.remove(tempSource);
			domainFileList.add(0, tempSource);
		}

		//
		// Add the custom ability file to the start of the list if it exists
		//
		File abilityFile = new File(CustomData.customAbilityFilePath(true));
		if (abilityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, abilityFile
					.toURI());
			abilityFileList.remove(tempSource);
			abilityFileList.add(0, tempSource);
		}

		//
		// Add the custom feat file to the start of the list if it exists
		//
		File featFile = new File(CustomData.customFeatFilePath(true));
		if (featFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, featFile
					.toURI());
			featFileList.remove(tempSource);
			featFileList.add(0, tempSource);
		}

		//
		// Add the custom language file to the start of the list if it exists
		//
		File languageFile = new File(CustomData.customLanguageFilePath(true));
		if (languageFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, languageFile
					.toURI());
			languageFileList.remove(tempSource);
			languageFileList.add(0, tempSource);
		}

		//
		// Add the custom race file to the start of the list if it exists
		//
		File raceFile = new File(CustomData.customRaceFilePath(true));
		if (raceFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, raceFile
					.toURI());
			raceFileList.remove(tempSource);
			raceFileList.add(0, tempSource);
		}

		//
		// Add the custom skill file to the start of the list if it exists
		//
		File skillFile = new File(CustomData.customSkillFilePath(true));
		if (skillFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, skillFile
					.toURI());
			skillFileList.remove(tempSource);
			skillFileList.add(0, tempSource);
		}

		//
		// Add the custom spell file to the start of the list if it exists
		//
		File spellFile = new File(CustomData.customSpellFilePath(true));
		if (spellFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, spellFile
					.toURI());
			spellFileList.remove(tempSource);
			spellFileList.add(0, tempSource);
		}

		//
		// Add the custom template file to the start of the list if it exists
		//
		File templateFile = new File(CustomData.customTemplateFilePath(true));
		if (templateFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, templateFile
					.toURI());
			templateFileList.remove(tempSource);
			templateFileList.add(0, tempSource);
		}
	}

	/**
	 * This method checks to make sure that the deities required for
	 * the current mode have been loaded into the Globals as Deities.
	 * Prior to calling this method, deities are stored as simple String objects.
	 *
	 * @throws PersistenceLayerException if something bizarre occurs, such as this
	 *                                   method being invoked more than once, a change to DeityLoader, or
	 *                                   an invalid LST file containing the default deities.
	 */
	private void checkRequiredDeities(LoadContext context) throws PersistenceLayerException
	{
		//
		// Add in the default deities (unless they're already there)
		//
		final List<String> gDeities = Globals.getGlobalDeityList();

		if ((gDeities != null) && (gDeities.size() != 0))
		{
			for (String aLine : gDeities)
			{
				deityLoader.parseLine(context, null, aLine, globalCampaign);
			}
		}
	}

	/**
	 * Reads the source file for the campaign aCamp and adds the names
	 * of files to be loaded to raceFileList, classFileList etc.
	 * @param aCamp
	 */
	private void loadCampaignFile(Campaign aCamp)
	{
		aCamp.setIsLoaded(true);

		final URI sourceFile = aCamp.getSourceURI();

		// Update the list of chosen campaign source files
		if (!chosenCampaignSourcefiles.contains(sourceFile))
		{
			chosenCampaignSourcefiles.add(sourceFile);
			SettingsHandler.getOptions().setProperty(
				"pcgen.files.chosenCampaignSourcefiles",
				StringUtil.join(chosenCampaignSourcefiles, ", "));
//			CoreUtility.join(chosenCampaignSourcefiles, ','));
		}

		// Update whether licenses need shown
		showOGL |= aCamp.isOGL();
		showD20 |= aCamp.isD20();
		showLicensed |= aCamp.isLicensed();

		if (aCamp.isLicensed())
		{
			List<String> licenseList = aCamp.getLicenses();
			if (licenseList != null && licenseList.size() > 0)
			{
				licensesToDisplayString.append(aCamp.getLicenses());
			}

			List<URI> licenseURIs = aCamp.getLicenseFiles();
			if (licenseURIs != null)
			{
				licenseFiles.addAll(licenseURIs);
			}
		}
		
		// check if maturity warning needs to be shown
		showMature |= aCamp.isMature();
		
		if (aCamp.isMature())
		{
			matureCampaigns.append(aCamp.getSourceEntry().getFieldByType(SourceEntry.SourceFormat.LONG) + 
				                   " (" + aCamp.getPubNameLong() + ")<br>");
		}

		// Load the LST files to be loaded for the campaign
		lstExcludeFiles.addAll(aCamp.getLstExcludeFiles());
		raceFileList.addAll(aCamp.getRaceFiles());
		classFileList.addAll(aCamp.getClassFiles());
		companionmodFileList.addAll(aCamp.getCompanionModFiles());
		skillFileList.addAll(aCamp.getSkillFiles());
		abilityCategoryFileList.addAll(aCamp.getAbilityCategoryFiles());
		abilityFileList.addAll(aCamp.getAbilityFiles());
		featFileList.addAll(aCamp.getFeatFiles());
		deityFileList.addAll(aCamp.getDeityFiles());
		domainFileList.addAll(aCamp.getDomainFiles());
		weaponProfFileList.addAll(aCamp.getWeaponProfFiles());
		armorProfFileList.addAll(aCamp.getArmorProfFiles());
		shieldProfFileList.addAll(aCamp.getShieldProfFiles());
		equipmentFileList.addAll(aCamp.getEquipFiles());
		classSkillFileList.addAll(aCamp.getClassSkillFiles());
		classSpellFileList.addAll(aCamp.getClassSpellFiles());
		spellFileList.addAll(aCamp.getSpellFiles());
		languageFileList.addAll(aCamp.getLanguageFiles());
		reqSkillFileList.addAll(aCamp.getReqSkillFiles());
		templateFileList.addAll(aCamp.getTemplateFiles());
		equipmodFileList.addAll(aCamp.getEquipModFiles());
		kitFileList.addAll(aCamp.getKitFiles());
		bioSetFileList.addAll(aCamp.getBioSetFiles());
	}

	private void loadCustomItems()
	{
		customItemsLoaded = true;

		if (!SettingsHandler.getSaveCustomEquipment())
		{
			return;
		}

		final BufferedReader br = CustomData.getCustomEquipmentReader();

		// Why is this here?  This implies it is somehow
		// order-independent and should precede the opening of
		// the file.  This is almost assuredly a bug of some
		// kind waiting to happen.  Aha!  Just look at what is
		// in the "finally" clause below.  --bko XXX
		EquipmentList.setAutoGeneration(true);

		/*        if (br == null)
		 {
		 return;
		 }
		 */
		try
		{
			while (br != null)
			{
				String aLine = br.readLine();

				if (aLine == null)
				{
					break;
				}

				if (aLine.startsWith("BASEITEM:"))
				{
					final int idx = aLine.indexOf('\t', 9);

					if (idx < 10)
					{
						continue;
					}

					final String baseItemKey = aLine.substring(9, idx);
					aLine = aLine.substring(idx + 1);

					Equipment aEq =
						Globals.getContext().ref.silentlyGetConstructedCDOMObject(
								Equipment.class, baseItemKey);

					if (aEq != null)
					{
						aEq = aEq.clone();
						aEq.load(aLine);
						if (!aEq.isType(Constants.s_CUSTOM))
						{
							aEq.addMyType(Constants.s_CUSTOM);
						}
						Globals.getContext().ref.importObject(aEq);
					}
				}
			}
		}
		catch (IOException e)
		{
			logError("Error when loading custom items", e);
		}
		finally
		{
			EquipmentList.setAutoGeneration(false);

			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException ex)
			{
				logError(
					"Error when closing infile after loading custom items", ex);
			}
		}
	}

	private static void loadGameModeInfoFile(GameMode gameMode, URI uri,
		String aType)
	{
		String data;
		try
		{
			data = LstFileLoader.readFromURI(uri).toString();
		}
		catch (PersistenceLayerException ple)
		{
			Logging.errorPrint(PropertyFactory.getFormattedString(
				"Errors.LstSystemLoader.loadGameModeInfoFile", //$NON-NLS-1$
				uri, ple.getMessage()));
			return;
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

			if (aType.equals("load"))
			{
				gameMode.addLoadString(aLine);
			}
			else if (aType.equals("level"))
			{
				final LevelInfo level = new LevelInfo();
				LevelLoader.parseLine(level, aLine, i + 1, uri);
				gameMode.addLevelInfo(level);
			}
			else if (aType.equals("rules"))
			{
				RuleCheckLoader.parseLine(gameMode, aLine, uri);
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
			Logging.errorPrint(PropertyFactory.getFormattedString(
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
		return gameMode;
	}

	private void loadPCCFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(pccFileFilter);
	}

	private void loadPCCFilesInDirectory(File aDirectory)
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
	private void loadGameModeLstFile(LstLineFileLoader lstFileLoader,
		String gameModeName, String gameModeFolderName, String lstFileName)
	{
		loadGameModeLstFile(lstFileLoader, gameModeName, gameModeFolderName, lstFileName, true);
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
	 */
	private void loadGameModeLstFile(LstLineFileLoader lstFileLoader,
		String gameModeName, String gameModeFolderName, String lstFileName, final boolean showMissing)
	{
		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");

		try
		{
			File specGameModeDir = new File(gameModeDir, gameModeFolderName);
			File gameModeFile = new File(specGameModeDir, lstFileName);
			if (gameModeFile.exists())
			{
				lstFileLoader.loadLstFile(gameModeFile.toURI(), gameModeName);
				return;
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
				lstFileLoader.loadLstFile(gameModeFile.toURI(), gameModeName);
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
	}

	private void loadGameModes()
	{
		final String[] gameFiles = getGameFilesList();

		if ((gameFiles == null) || (gameFiles.length == 0))
		{
			return;
		}

		SystemCollections.clearGameModeList();

		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");

		for (String gameFile : gameFiles)
		{
			SystemCollections.setEmptyUnitSetList(gameFile);
			File specGameModeDir = new File(gameModeDir, gameFile);
			File miscInfoFile = new File(specGameModeDir, "miscinfo.lst");
			final GameMode gm =
					loadGameModeMiscInfo(gameFile, miscInfoFile.toURI());
			String gmName = gm.getName();
			SettingsHandler.setGame(gmName);
			if (gm != null)
			{
				loadGameModeInfoFile(gm, new File(specGameModeDir, "level.lst")
						.toURI(), "level");
				loadGameModeInfoFile(gm, new File(specGameModeDir, "rules.lst")
						.toURI(), "rules");

				// Load equipmentslot.lst
				loadGameModeLstFile(eqSlotLoader, gmName, gameFile,
					"equipmentslots.lst");

				// Load paperInfo.lst
				loadGameModeLstFile(paperLoader, gmName, gameFile, "paperInfo.lst");

				// Load bio files
				loadGameModeLstFile(traitLoader, gmName, gameFile, "bio"
					+ File.separator + "traits.lst");
				loadGameModeLstFile(locationLoader, gmName, gameFile, "bio"
					+ File.separator + "locations.lst");
				loadGameModeLstFile(bioLoader, gmName, gameFile, "bio"
					+ File.separator + "biosettings.lst");

				// Load load.lst and check for completeness
				loadGameModeLstFile(loadInfoLoader, gmName, gameFile, "load.lst");

				// Load unitset.lst
				loadGameModeLstFile(unitSetLoader, gmName, gameFile, "unitset.lst",
					false);

				// Load pointbuymethods.lst
				loadPointBuyFile(gameFile, gmName);

				// Load sizeAdjustment.lst
				loadGameModeLstFile(sizeLoader, gmName, gameFile,
					"sizeAdjustment.lst");

				// Load statsandchecks.lst
				loadGameModeLstFile(statCheckLoader, gmName, gameFile,
					"statsandchecks.lst");
			}
		}

		SystemCollections.sortGameModeList();
	}

	/**
	 * Load the purchase mode/point buy definitions from either the new 
	 * location under the custom sources folder, or in the old location
	 * with the game mode.
	 *   
	 * @param gameFile The location of the game mode directory.
	 * @param gmName The name of the game mode being loaded.
	 */
	private void loadPointBuyFile(String gameFile, String gmName)
	{
		File pointBuyFile =
				new File(CustomData.customPurchaseModeFilePath(true, gmName));
		boolean useGameModeFile = true;
		if (pointBuyFile.exists())
		{
			try
			{
				pointBuyLoader.loadLstFile(pointBuyFile.toURI(), gmName);
				useGameModeFile = false;
			}
			catch (PersistenceLayerException e)
			{
				// Ignore - its OK if the file cannot be loaded
			}
		}
		if (useGameModeFile)
		{
			loadGameModeLstFile(pointBuyLoader, gmName, gameFile,
				"pointbuymethods.lst", false);
		}
	}

	/**
	 * This method reads the PCC (Campaign) files and, if options are allowed to be set
	 * in the sources, sets the SettingsHandler settings to reflect the changes from the
	 * campaign files.
	 *
	 * @param aSelectedCampaignsList List of Campaigns to load
	 * @param currentPC
	 */
	private void readPccFiles(final List<Campaign> aSelectedCampaignsList,
		final PlayerCharacter currentPC)
	{
		// Prime options based on currently selected preferences
		if (SettingsHandler.isOptionAllowedInSources())
		{
			SettingsHandler.setOptionsProperties(currentPC);
		}

		// Create aggregate collections of source files to load
		// along with any options required by the campaigns...
		for (Campaign campaign : aSelectedCampaignsList)
		{
			loadCampaignFile(campaign);

			if (SettingsHandler.isOptionAllowedInSources())
			{
				setCampaignOptions(campaign);
			}
		}

		// ...but make sure to remove those files specified by LSTEXCLUDE;
		// LSTEXCLUDE should be treated as a global exclusion.
		// sage_sam 29 Dec 2003, Bug #834834
		stripLstExcludes();

		//
		// This was added in v1.64. Why? This will read from options.ini, replacing anything that's been changed, not
		// just campaign-specific items. Commenting out as it breaks loading after selecting game mode on the Campaign menu.
		// Game mode reverts back to game mode saved in options.ini
		// - Byngl Sept 15, 2002
		// This allows options to be set by campaign files. It doesn't read directly from options.ini,
		// only from the properties. The setOptionsProperties call added above should prime these with
		// the current values before we load the campaigns.
		// - James Dempsey 09 Oct 2002
		if (SettingsHandler.isOptionAllowedInSources())
		{
			SettingsHandler.getOptionsFromProperties(currentPC);
		}
	}

	/**
	 * This method makes sure that the files specified by an LSTEXCLUDE tag
	 * are stripped out of the source files to be loaded on a global basis.
	 */
	private void stripLstExcludes()
	{
		raceFileList.removeAll(lstExcludeFiles);
		classFileList.removeAll(lstExcludeFiles);
		companionmodFileList.removeAll(lstExcludeFiles);
		skillFileList.removeAll(lstExcludeFiles);
		abilityCategoryFileList.removeAll(lstExcludeFiles);
		abilityFileList.removeAll(lstExcludeFiles);
		featFileList.removeAll(lstExcludeFiles);
		deityFileList.removeAll(lstExcludeFiles);
		domainFileList.removeAll(lstExcludeFiles);
		weaponProfFileList.removeAll(lstExcludeFiles);
		armorProfFileList.removeAll(lstExcludeFiles);
		shieldProfFileList.removeAll(lstExcludeFiles);
		equipmentFileList.removeAll(lstExcludeFiles);
		classSkillFileList.removeAll(lstExcludeFiles);
		classSpellFileList.removeAll(lstExcludeFiles);
		spellFileList.removeAll(lstExcludeFiles);
		languageFileList.removeAll(lstExcludeFiles);
		reqSkillFileList.removeAll(lstExcludeFiles);
		templateFileList.removeAll(lstExcludeFiles);
		equipmodFileList.removeAll(lstExcludeFiles);
		kitFileList.removeAll(lstExcludeFiles);
		bioSetFileList.removeAll(lstExcludeFiles);
	}

	/**
	 * This method releases the memory used by reading and storing
	 * the file data.
	 */
	private void releaseFileData()
	{
		lstExcludeFiles.clear();
		raceFileList.clear();
		classFileList.clear();
		companionmodFileList.clear();
		skillFileList.clear();
		abilityCategoryFileList.clear();
		abilityFileList.clear();
		featFileList.clear();
		deityFileList.clear();
		domainFileList.clear();
		templateFileList.clear();
		weaponProfFileList.clear();
		armorProfFileList.clear();
		shieldProfFileList.clear();
		equipmentFileList.clear();
		classSkillFileList.clear();
		classSpellFileList.clear();
		spellFileList.clear();
		reqSkillFileList.clear();
		languageFileList.clear();
		equipmodFileList.clear();
		kitFileList.clear();
		bioSetFileList.clear();
	}

	/**
	 * This method shows the OGL and D20 Licenses as needed/set in the SettingsHandler.
	 */
	private void showLicensesIfNeeded()
	{
		// Only worry about it if we're using the GUI
		if (Globals.getUseGUI())
		{
			if (showOGL && SettingsHandler.showLicense())
			{
				pcGenGUI.showLicense();
			}

			if (showLicensed && SettingsHandler.showLicense())
			{
				String licenseInfo = licensesToDisplayString.toString();
				if (licenseInfo.trim().length() > 0)
				{
					pcGenGUI.showLicense("Special Licenses", licenseInfo);
				}
				pcGenGUI.showLicense("Special Licenses", licenseFiles);
			}

			if (showD20 && SettingsHandler.showD20Info())
			{
				// D20 compliant removed as of 10/14/2003
				// some products may still be D20 compliant, so we need to display this
				// even though PCGen itself is not D20 compliant
				pcGenGUI.showMandatoryD20Info();
			}

			if (showMature && SettingsHandler.showMature())
			{
				String matureInfo = matureCampaigns.toString();
				if (matureInfo.trim().length() > 0)
				{
					pcGenGUI.showMature(matureInfo);
				}
			}
		}

		// Prevent redisplay (i.e. sources unloaded, then re-loaded
		Globals.getSection15().setLength(0);
		showOGL = false;
		showD20 = false;
		showLicensed = false;
	}

	private void showSponsorsIfNeeded()
	{
		// Only worry about it if we're using the GUI
		if (Globals.getUseGUI())
		{
			if (SettingsHandler.showSponsors())
			{
				pcGenGUI.showSponsors();
			}
		}
	}

	/**
	 * This method sorts the provided listof Campaign objects by rank.
	 *
	 * @param aSelectedCampaignsList List of Campaign objects to sort
	 */
	private void sortCampaignsByRank(final List<Campaign> aSelectedCampaignsList)
	{
		Collections.sort(aSelectedCampaignsList, new Comparator<Campaign>()
		{
			public int compare(Campaign c1, Campaign c2)
			{
				return c1.getSafe(IntegerKey.CAMPAIGN_RANK) - c2.getSafe(IntegerKey.CAMPAIGN_RANK);
			}
		});

		// Loop through, performing a swap sort
		for (Campaign campaign : aSelectedCampaignsList)
		{
			sourcesSet.add(campaign.getSourceEntry().getFormattedString(
				SourceEntry.SourceFormat.LONG, true));
		}

		// end of campaign sort
	}

	/**
	 * This method is called to verify that all weapons loaded from the equipment files
	 * are classified as either Melee or Ranged.  This is required so that to-hit values
	 * can be calculated for that weapon.
	 *
	 * @throws PersistenceLayerException if a weapon is neither melee or ranged, indicating
	 *                                   the name of the weapon that caused the error
	 */
	private void verifyWeaponsMeleeOrRanged() throws PersistenceLayerException
	{
		//
		// Check all the weapons to see if they are either Melee or Ranged, to avoid
		// problems when we go to export/preview the character
		//
		for (Equipment aEq : Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class))
		{
			if (aEq.isWeapon() && !aEq.isMelee() && !aEq.isRanged())
			{
				throw new PersistenceLayerException(
					"Weapon: "
						+ aEq.getName()
						+ " is neither Melee nor Ranged."
						+ Constants.s_LINE_SEP
						+ Constants.s_APPNAME
						+ " cannot calculate \"to hit\" unless one of these is selected."
						+ Constants.s_LINE_SEP + "Source: "
						+ aEq.getSourceURI());
			}
		}
	}

	private void verifyFavClassSyntax() throws PersistenceLayerException
	{
		for (Race r : Globals.getContext().ref.getConstructedCDOMObjects(Race.class)) {
			validateFavClassString(r.getKeyName(), "RACE", "FAVCLASS", r.getFavoredClass());
		}
		for (PCTemplate t : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
		{
			validateFavClassString(t.getKeyName(), "TEMPLATE", "FAVOREDCLASS", t.getFavoredClass());
		}
	}

	private void validateFavClassString(String key, String type, String tag, String fav)
			throws PersistenceLayerException
	{
		String favored = fav;
		if (fav.startsWith("CHOOSE:"))
		{
			favored = fav.substring(7);
		}
		
		if (favored.equalsIgnoreCase("ANY"))
		{
			return;
		}

		final StringTokenizer tok = new StringTokenizer(favored, "|");
		while (tok.hasMoreTokens())
		{
			String cl = tok.nextToken();
			int dotLoc = cl.indexOf(".");
			if (dotLoc == -1)
			{
				// Base Class
				PCClass pcclass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, cl);
				if (pcclass == null)
				{
					Logging.deprecationPrint("Class entry in " + tag
							+ " token in " + type + " " + key + ": " + cl
							+ " likely references a SubClass.  Should use "
							+ tag + ":PARENT.SUBCLASS syntax");
				}
			}
			else
			{
				String parent = cl.substring(0, dotLoc);
				PCClass pcclass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, parent);
				if (pcclass == null)
				{
					Logging.errorPrint("Invalid Class entry in " + tag
							+ " token in " + type + " " + key + ": " + cl
							+ " ... " + parent + " does not exist as a Class");
				}
				String subclass = cl.substring(dotLoc + 1);
				if (parent.equals(subclass))
				{
					if(pcclass.getSafe(ObjectKey.ALLOWBASECLASS) == false)
					{
						Logging.errorPrint("Invalid Class entry in " + tag
							+ " token in " + type + " " + key + ": " + cl
							+ " ... Base class is prohibited in " + parent);
					}
					 
				}
				else if (pcclass.getSubClassKeyed(subclass) == null)
				{
					Logging.errorPrint("Invalid Class entry in " + tag
							+ " token in " + type + " " + key + ": " + cl
							+ " ... " + subclass
							+ " does not exist as a SubClass of " + parent);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		// We are not going to do anything with the notifications
		// we have observers, so just pass them on
		setChanged();
		notifyObservers(arg);
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
		setChanged();
	}

}
