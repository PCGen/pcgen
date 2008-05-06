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

import pcgen.base.lang.UnreachableError;
import pcgen.core.*;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.pcGenGUI;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * ???
 *
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class LstSystemLoader extends Observable implements SystemLoader,
		Observer
{
	// list of PObjects for character spells with subclasses
	private static final List<PObject> pList = new ArrayList<PObject>();

	/**
	 * Define the order in which the file types are ordered
	 * so we don't have to keep renumbering them
	 * 
	 * This can be removed after 5.14
	 */
	private static final int[] loadOrder =
			{	LstConstants.CLASSSKILL_TYPE, LstConstants.CLASSSPELL_TYPE };
	private static final int MODE_EXCLUDE = -1;
	private static final int MODE_DEFAULT = 0;
	private static final int MODE_INCLUDE = +1;
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

	private static int lineNum = 0;
	private AbilityCategoryLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private BioSetLoader bioLoader = new BioSetLoader();
	private CampaignLoader campaignLoader = new CampaignLoader();
	private final CampaignSourceEntry globalCampaign;
	private DeityLoader deityLoader = new DeityLoader();
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
	private SkillLoader skillLoader = new SkillLoader();
	private SpellLoader spellLoader = new SpellLoader();
	private StatsAndChecksLoader statCheckLoader = new StatsAndChecksLoader();

	/////////////////////////////////////////////////////////////////
	// Property(s)
	/////////////////////////////////////////////////////////////////
	private String skillReq = "";
	private StringBuffer licensesToDisplayString = new StringBuffer();
	private StringBuffer matureCampaigns = new StringBuffer();
	private TraitLoader traitLoader = new TraitLoader();
	private WeaponProfLoader wProfLoader = new WeaponProfLoader();
	private ArmorProfLoader aProfLoader = new ArmorProfLoader();
	private ShieldProfLoader sProfLoader = new ShieldProfLoader();
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
			CoreUtility.join(chosenCampaignSourcefiles, ", "));
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

		skillReq = "";
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
		Globals.setSorted(false);
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

			// load weapon profs first
			wProfLoader.loadLstFiles(weaponProfFileList);
			aProfLoader.loadLstFiles(armorProfFileList);
			sProfLoader.loadLstFiles(shieldProfFileList);

			// load skills before classes to handle class skills
			skillLoader.loadLstFiles(skillFileList);

			// load before races to handle auto known languages
			languageLoader.loadLstFiles(languageFileList);

			// load before race or class to handle abilities
			abilityLoader.loadLstFiles(abilityFileList);

			// load before race or class to handle feats
			featLoader.loadLstFiles(featFileList);

			raceLoader.loadLstFiles(raceFileList);

			//Domain must load before CLASS - thpr 10/29/06
			domainLoader.loadLstFiles(domainFileList);

			spellLoader.loadLstFiles(spellFileList);
			deityLoader.loadLstFiles(deityFileList);

			classLoader.loadLstFiles(classFileList);
			
			templateLoader.loadLstFiles(templateFileList);
			
			// loaded before equipment (required)
			eqModLoader.loadLstFiles(equipmodFileList);

			equipmentLoader.loadLstFiles(equipmentFileList);
			companionModLoader.loadLstFiles(companionmodFileList);
			kitLoader.loadLstFiles(kitFileList);
			
			// TODO: Convert remaining items to new persistence framework!
			// Process file content by load order [file type]
			for (int loadIdx = 0; loadIdx < loadOrder.length; loadIdx++)
			{
				final int lineType = loadOrder[loadIdx];
				List<CampaignSourceEntry> fileList = getFilesForType(lineType);

				if ((fileList != null) && (!fileList.isEmpty()))
				{
					Logging.errorPrint("*WARNING*: You are using a file type that has been deprecated");
					Logging.errorPrint(" The following file types will not be supported after 5.14:");
					Logging.errorPrint("  Class Skill LST file");
					Logging.errorPrint("  Class Spell LST file");
					Logging.errorPrint("  Required Skill LST file");
					Logging.errorPrint(" Function for these files has been provided in other LST files");
					List<PObject> bArrayList = new ArrayList<PObject>();

					// This relies on new items being added to the *end* of an ArrayList.
					processFileList(lineType, fileList, bArrayList);
				}
			}
			// end of load order loop

			// Load the bio settings files
			bioLoader.loadLstFiles(bioSetFileList);

			// Check for the required skills
			if (reqSkillFileList != null)
			{
				addToGlobals(LstConstants.REQSKILL_TYPE, reqSkillFileList);
			}
			checkRequiredSkills();

			// Check for the default deities
			checkRequiredDeities();

			// Add default EQ mods
			eqModLoader.addDefaultEquipmentMods();
			
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
	 * @see pcgen.persistence.SystemLoader#loadFileIntoList(String, int, List)
	 * 
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	public void loadFileIntoList(URI fileName, int fileType,
		List<PObject> aList) throws PersistenceLayerException
	{
		URL url = null;
		try
		{
			url = fileName.toURL();
		}
		catch (MalformedURLException e)
		{
			try
			{
				setChanged();
				notifyObservers("Unable to convert '" + fileName + "' to a URL");
				url = new URL("http://g");
			}
			catch (MalformedURLException e1)
			{
				e1.printStackTrace();
			}
		}
		setChanged();
		notifyObservers(url);

		initFile(fileName, fileType, aList);
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
	 * This method gets the set of files to parse for a given object type.
	 *
	 * @param lineType int indicating the type of objects to retrieve the
	 *                 LST source lines for
	 * @return List containing the LST source lines for the requested
	 *         object type
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private List<CampaignSourceEntry> getFilesForType(final int lineType)
	{
		List<CampaignSourceEntry> lineList = null;

		switch (lineType)
		{
			case LstConstants.CLASSSKILL_TYPE:
				lineList = classSkillFileList;

				break;

			case LstConstants.CLASSSPELL_TYPE:
				lineList = classSpellFileList;

				break;

			default:
				logError("Campaign list corrupt; no such lineType ("
					+ lineType
					+ ") exists. Stopped parsing campaigns, but not aborting program.");
		}

		return lineList;
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
	 * This method adds a given list of objects to the appropriate Globals
	 * storage.
	 *
	 * @param lineType   int indicating the type of objects in the list
	 * @param aArrayList List containing the objects to add to Globals
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private void addToGlobals(final int lineType, final List<?> aArrayList)
	{
		String aClassName = "";

		for (int i = 0; i < aArrayList.size(); ++i)
		{
			switch (lineType)
			{
				case LstConstants.CLASSSKILL_TYPE:
					parseClassSkillFrom(aArrayList.get(i).toString());

					break;

				case LstConstants.CLASSSPELL_TYPE:
					aClassName =
							parseClassSpellFrom(aArrayList.get(i).toString(),
								aClassName);

					break;

				case LstConstants.REQSKILL_TYPE:

					final String aString = aArrayList.get(i).toString();
					if ("ALL".equals(aString) || "UNTRAINED".equals(aString))
					{
						skillReq = aString;
					}
					else
					{
						final Skill skillKeyed = Globals.getSkillKeyed(aString);

						if (skillKeyed != null)
						{
							skillKeyed.setRequired(true);
						}
						else
						{
							logError("The skill " + aString
								+ " defined as a REQSKILL could not be found."
								+ " The skill could not be made required.");
						}
					}

					break;

				case -1:
					break;

				default:
					logError("In LstSystemLoader.initValue the lineType "
						+ lineType + " is not handled.");

					break;
			}
		}

		aArrayList.clear();
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
	private void checkRequiredDeities() throws PersistenceLayerException
	{
		//
		// Add in the default deities (unless they're already there)
		//
		final List<String> gDeities = Globals.getGlobalDeityList();

		if ((gDeities != null) && (gDeities.size() != 0))
		{
			for (String aLine : gDeities)
			{
				deityLoader.parseLine(null, aLine, globalCampaign);
			}
		}
	}

	/**
	 * This method ensures that the global required skills are loaded and marked as required.
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private void checkRequiredSkills()
	{
		if (skillReq.length() > 0)
		{
			for (Iterator<Skill> e1 = Globals.getSkillList().iterator(); e1
				.hasNext();)
			{
				final Skill aSkill = e1.next();

				if (("UNTRAINED".equals(skillReq) && aSkill.isUntrained())
					|| skillReq.equals("ALL"))
				{
					aSkill.setRequired(true);
				}
			}
		}
	}

	/**
	 * This method counts the closing parens ')' in a given token.
	 *
	 * @param token String to count parens in.
	 * @return int number of closing parens in the token
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private int countCloseParens(final String token)
	{
		String dString = token;
		int parenCount = 0;

		while (dString.lastIndexOf(')') >= 0)
		{
			++parenCount;
			dString = dString.substring(0, dString.lastIndexOf(')'));
		}

		return parenCount;
	}

	/**
	 * This method counts the opening parens '(' in a given token.
	 *
	 * @param token String to count parens in.
	 * @return int number of opening parens in the token
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private int countOpenParens(final String token)
	{
		String dString = token;
		int parenCount = 0;

		while (dString.lastIndexOf('(') >= 0)
		{
			++parenCount;
			dString = dString.substring(0, dString.lastIndexOf('('));
		}

		return parenCount;
	}

	/**
	 * This method loads the contents of a file into the appropriate
	 * line lists.
	 *
	 * @param argFileName    String file to load
	 * @param fileType       int indicating the type of file
	 * @param aList          List to load the lines into
	 * @throws PersistenceLayerException
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private void initFile(URI argFileName, int fileType, List<PObject> aList)
		throws PersistenceLayerException
	{
		if (lstExcludeFiles.contains(argFileName))
		{
			return;
		}

		final StringBuilder dataBuffer = LstFileLoader.readFromURI(argFileName);

		/*
		 * Need to keep the Windows line separator as newline
		 * delimiter to ensure cross-platform portability.
		 *
		 * author: Thomas Behr 2002-11-13
		 */
		final Map<String, String> sourceMap = new HashMap<String, String>();
		final String newlinedelim = "\r\n";
		final String aString = dataBuffer.toString();
		final StringTokenizer newlineStr =
				new StringTokenizer(aString, newlinedelim);

		String nameString = "";
		String aLine = "";

		while (newlineStr.hasMoreTokens())
		{
			boolean isModItem;
			final boolean isForgetItem;
			aLine = newlineStr.nextToken();
			++lineNum;

			if (aLine.startsWith("CAMPAIGN:") && (fileType != LstConstants.CAMPAIGN_TYPE)) // && fileType != -1 sage_sam 10 Sept 2003
			{
				continue;
			}

			//
			// Ignore commented-out lines
			// and empty lines
			if (aLine.length() == 0)
			{
				continue;
			}

			if ((fileType != LstConstants.CAMPAIGN_TYPE)
				&& (aLine.length() > 0) && (aLine.charAt(0) == '#'))
			{
				continue;
			}

			if (aLine.startsWith("SOURCE")
				&& (fileType != LstConstants.CAMPAIGN_TYPE))
			{
				sourceMap.putAll(SourceLoader.parseLine(aLine, argFileName));

				continue;
			}

			// check for special case of CLASS:name.MOD
			isModItem = aLine.endsWith(".MOD");

			if (isModItem && aLine.startsWith("CLASS:"))
			{
				nameString = aLine.substring(0, aLine.length() - 4);
			}

			isForgetItem = aLine.endsWith(".FORGET");

			if (isForgetItem)
			{
				nameString = aLine.substring(0, aLine.length() - 7);
			}

			// first field is usually name
			// (only exception is class-level lines)
			// see if name ends with .MOD, if so, use
			// existing item instead of creating a new one
			if (aLine.indexOf('\t') > 2)
			{
				final StringTokenizer t = new StringTokenizer(aLine, "\t");
				nameString = t.nextToken();
				isModItem = nameString.endsWith(".MOD");

				if (isModItem)
				{
					nameString =
							nameString.substring(0, nameString.length() - 4);
				}
				else if (nameString.indexOf(".COPY=") > 0)
				{
					nameString =
							nameString.substring(0, nameString
								.indexOf(".COPY="));
				}
			}

			switch (fileType)
			{
				case -1: // if we're in the process of loading campaigns/sources when

					// another source is loaded via PCC:, then it's fileType=-1
				case LstConstants.CLASSSKILL_TYPE:

					//Deliberate fall-through
				case LstConstants.CLASSSPELL_TYPE:
					// boomer70 - hack for how
					PObject pObj = new PObject();
					pObj.setName(aLine);
					aList.add(pObj);

					break;

				default:
					logError("In LstSystemLoader.initValue the fileType "
						+ fileType + " is not handled.");

					break;
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
				CoreUtility.join(chosenCampaignSourcefiles, ", "));
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
							EquipmentList.getEquipmentKeyed(baseItemKey);

					if (aEq != null)
					{
						aEq = aEq.clone();
						aEq.load(aLine);
						if (!aEq.isType(Constants.s_CUSTOM))
						{
							aEq.addMyType(Constants.s_CUSTOM);
						}
						EquipmentList.addEquipment(aEq);
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
	 *@deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private static void parseClassSkillFrom(String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t");
		String className = aTok.nextToken();
		final PCClass aClass = Globals.getClassKeyed(className);
		String aName = className;

		if (aClass != null)
		{
			aName = aClass.getKeyName();
		}

		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
			aTok = new StringTokenizer(className, "|");

			String aString;
			Skill aSkill;

			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();

				final String aStringParen = aString + "(";
				aSkill = Globals.getSkillKeyed(aString);

				if (aSkill != null)
				{
					aSkill.getClassList().add(aName);
				}
				else
				{
					for (Skill skill : Globals.getSkillList())
					{
						if (skill.getKeyName().startsWith(aStringParen))
						{
							skill.getClassList().add(aName);
						}
					}
				}
			}
		}
	}

	/**
	 *@deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private static String parseClassSpellFrom(String aLine, String aKey)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t");
		final String aString = aTok.nextToken();

		if (aString.startsWith("DOMAIN:"))
		{
			aKey = aString.substring(7);

			final Domain aDom = Globals.getDomainKeyed(aKey);

			if (aDom != null)
			{
				aKey = "DOMAIN|" + aKey;
			}
			else
			{
				aKey = "";
			}
		}

		if (aString.startsWith("CLASS:"))
		{
			boolean isClass = true;
			aKey = "";

			if (aString.length() > 6)
			{
				aKey = aString.substring(6);
			}

			// first look for an actual class
			PObject aClass = Globals.getClassKeyed(aKey);

			//
			// If the class does not have any spell-casting, then it must either
			// be a domain or a subclass
			//
			if (aClass != null)
			{
				if (((PCClass) aClass).getSpellType().equalsIgnoreCase(
					Constants.s_NONE))
				{
					aClass = null;
				}
			}

			// then look for a domain
			if (aClass == null)
			{
				aClass = Globals.getDomainKeyed(aKey);

				if (aClass != null)
				{
					isClass = false;
				}
			}

			// if it's not one of those, leave it since it might be a subclass
			if (aClass != null)
			{
				aKey = aClass.getKeyName();
			}

			if (isClass)
			{
				aKey = "CLASS|" + aKey;
			}
			else
			{
				aKey = "DOMAIN|" + aKey;
			}
		}
		else if (aTok.hasMoreTokens())
		{
			PObject owner;
			final String key = aKey.substring(aKey.indexOf('|') + 1);

			if (aKey.startsWith("DOMAIN|"))
			{
				owner = Globals.getDomainKeyed(key);
			}
			else if (aKey.startsWith("CLASS|"))
			{
				owner = Globals.getClassKeyed(key);
			}
			else
			{
				return aKey;
			}

			if (owner == null) // then it must be a subclass
			{
				for (Iterator<PObject> i = pList.iterator(); i.hasNext();)
				{
					owner = i.next();

					if (owner.getKeyName().equals(key))
					{
						break;
					}
					owner = null;
				}

				if (owner == null)
				{
					owner = new PObject();
					owner.setName(key);
					owner.setKeyName(key);
					pList.add(owner);
				}
			}

			final int level = Integer.parseInt(aString);
			final String bString = aTok.nextToken();
			aTok = new StringTokenizer(bString, "|");

			while (aTok.hasMoreTokens())
			{
				final Spell aSpell =
						Globals.getSpellKeyed(aTok.nextToken().trim());

				if (aSpell != null)
				{
					aSpell.setLevelInfo(aKey, level);
				}
			}
		}

		return aKey;
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
	 * This method processes extra info from a line in a PCC/LST file,
	 * typically of the form INCLUDE or EXCLUDE.
	 *
	 * @param lineType    int indicating the type of line data
	 * @param pObjectList List of PObjects created from the data file
	 * @param extraInfo   String containing the extra info
	 *@deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private void processExtraInfo(int lineType,
		List<? extends PObject> pObjectList, String extraInfo)
	{
		final StringTokenizer infoTokenizer =
				new StringTokenizer(extraInfo, "|");
		int inMode = MODE_DEFAULT;
		ArrayList<String> includeExcludeNames = new ArrayList<String>();

		while (infoTokenizer.hasMoreTokens())
		{
			// Get the next token (duh)
			String currentToken = infoTokenizer.nextToken();

			// Count parens in the token for use in identifying the start/end of
			// an include/exclude group
			final int openParens = countOpenParens(currentToken);
			final int closeParens = countCloseParens(currentToken);

			boolean handled = false;

			// Handle the start of an INCLUDE or EXCLUDE group
			if (currentToken.startsWith("(EXCLUDE"))
			{
				String name = currentToken.substring(9);

				if (name.endsWith(")"))
				{
					name = name.substring(0, name.length() - 1);
				}

				includeExcludeNames.add(name);
				inMode = MODE_EXCLUDE;
				handled = true;
			}
			else if (currentToken.startsWith("(INCLUDE"))
			{
				String name = currentToken.substring(9);

				if (name.endsWith(")"))
				{
					name = name.substring(0, name.length() - 1);
				}

				includeExcludeNames.add(name);
				inMode = MODE_INCLUDE;
				handled = true;
			}

			// Handle the end of an INCLUDE or EXCLUDE group
			if (currentToken.endsWith(")") && (closeParens > openParens))
			{
				if (!handled)
				{
					includeExcludeNames.add(currentToken.substring(0,
						currentToken.length() - 1));
				}

				if (inMode == MODE_EXCLUDE)
				{
					// exclude
					for (int k = pObjectList.size() - 1; k >= 0; --k)
					{
						PObject anObject = pObjectList.get(k);

						if (includeExcludeNames.contains(anObject.getKeyName()))
						{
							pObjectList.remove(k);
						}
					}
				}
				else if (inMode == MODE_INCLUDE)
				{
					// include
					for (int k = pObjectList.size() - 1; k >= 0; --k)
					{
						PObject anObject = pObjectList.get(k);

						if (!includeExcludeNames
							.contains(anObject.getKeyName()))
						{
							pObjectList.remove(k);
						}
					}
				}

				handled = true;
				inMode = MODE_DEFAULT;
			}

			// If we get here without handling the token, we need to do something with it.
			if (!handled)
			{
				// Assume it is part of a larger INCLUDE or EXCLUDE unless
				// it is a REQSKILL or SPECIAL line.
				if (lineType != LstConstants.SPECIAL_TYPE)
				{
					includeExcludeNames.add(currentToken);
				}
				else
				{
					// It is a REQSKILL or SPECIAL line; add it to the original list of info
					// This will probably blow something up later via a ClassCastException.
					// boomer70 - This will definately blow something up and can't
					// work.  removing.
					Logging.errorPrint("Unhandled INCLUDE/EXCLUDE: "
						+ currentToken);
					//					pObjectList.add(currentToken);
				}
			}
		}
		// end while (infoTokenizer.hasMoreTokens())
	}

	/**
	 * This method processes a List of files of a given type, parsing
	 * the content of each file into PCGen core objects.
	 *
	 * @param lineType   int representing the type of files
	 * @param lineList   List containing the LST source lines
	 * @param bArrayList List that will contain pcgen.core objects
	 * @throws PersistenceLayerException if an error is found in the LST source
	 *@deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	private void processFileList(final int lineType, List<CampaignSourceEntry> lineList,
		List<PObject> bArrayList) throws PersistenceLayerException
	{
		//  Campaigns aren't processed here any more.
		if (lineType == LstConstants.CAMPAIGN_TYPE)
		{
			logError("No longer processing campaigns in processFileList.");

			return;
		}

		for (CampaignSourceEntry campaign : lineList)
		{
			URI uri = campaign.getURI();
			// Check whether the file was already [completely] loaded
			if (loadedFiles.contains(uri))
			{
				// if so, process next file (don't need to do this one again)
				continue;
			}

			// 3. Parse the file into a list of PObjects/Strings
			loadFileIntoList(uri, lineType, bArrayList);

			// 4. Check for restrictions on loading the file.
			List<String> excludeKeys = campaign.getExcludeItems();
			for (int k = bArrayList.size() - 1; k >= 0; --k)
			{
				PObject anObject = bArrayList.get(k);

				if (excludeKeys.contains(anObject.getKeyName()))
				{
					bArrayList.remove(k);
				}
			}
			List<String> includeKeys = campaign.getIncludeItems();
			for (int k = bArrayList.size() - 1; k >= 0; --k)
			{
				PObject anObject = bArrayList.get(k);

				if (!includeKeys.contains(anObject.getKeyName()))
				{
					bArrayList.remove(k);
				}
			}

			if (excludeKeys.size() == 0 && includeKeys.size() == 0)
			{
				// Using all data from the file.  Add it to the loaded list.
				loadedFiles.add(uri);
			}

			// 5. Add the resulting information to Globals.
			if (!bArrayList.isEmpty())
			{
				addToGlobals(lineType, bArrayList);
			}
		}
		// end lineList loop
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
				return c1.getRank() - c2.getRank();
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
		for (Iterator<Map.Entry<String, Equipment>> e2 =
				EquipmentList.getEquipmentListIterator(); e2.hasNext();)
		{
			final Equipment aEq = e2.next().getValue();

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
		for (Race r : Globals.getAllRaces()) {
			validateFavClassString(r.getKeyName(), "RACE", "FAVCLASS", r.getFavoredClass());
		}
		for (PCTemplate t : Globals.getTemplateList())
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
				PCClass pcclass = Globals.getClassKeyed(cl);
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
				PCClass pcclass = Globals.getClassKeyed(parent);
				if (pcclass == null)
				{
					Logging.errorPrint("Invalid Class entry in " + tag
							+ " token in " + type + " " + key + ": " + cl
							+ " ... " + parent + " does not exist as a Class");
				}
				String subclass = cl.substring(dotLoc + 1);
				if (parent.equals(subclass))
				{
					if(pcclass.getAllowBaseClass() == false)
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
	 * @deprecated as part of Trackers 1632898 and 1632897 - thpr 1/11/07
	 */
	@Deprecated
	public void logError(String message)
	{
		Logging.errorPrint(message);
		setChanged();
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
