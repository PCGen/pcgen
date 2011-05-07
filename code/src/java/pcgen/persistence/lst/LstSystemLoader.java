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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.Sponsor;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.Deity;
import pcgen.core.Description;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.PaperInfo;
import pcgen.core.PlayerCharacter;
import pcgen.core.PointBuyCost;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.EqModAttachment;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.Prerequisite;
import pcgen.gui.pcGenGUI;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.LoadValidator;
import pcgen.rules.context.ReferenceContext;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

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

	private static UnitSet DEFAULT_UNIT_SET;

	private List<Campaign> loadedCampaigns = new ArrayList<Campaign>();
	private AbilityCategoryLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private BioSetLoader bioLoader = new BioSetLoader();
	private CampaignLoader campaignLoader = new CampaignLoader();
	private final CampaignSourceEntry globalCampaign;
	private GenericLoader<Deity> deityLoader = new GenericLoader<Deity>(Deity.class);
	private GenericLoader<Domain> domainLoader = new GenericLoader<Domain>(Domain.class);
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

	private GenericLoader<Language> languageLoader = new GenericLoader<Language>(Language.class);
	private LoadInfoLoader loadInfoLoader = new LoadInfoLoader();
	private EquipSlotLoader eqSlotLoader = new EquipSlotLoader();
	private final List<CampaignSourceEntry> bioSetFileList =
			new ArrayList<CampaignSourceEntry>();
	private final Map<String, List<URI>> chosenCampaignSourcefiles =
			new HashMap<String, List<URI>>();
	private final List<CampaignSourceEntry> classFileList =
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
	private List<CampaignSourceEntry> licenseFiles = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> lstExcludeFiles =
			new ArrayList<CampaignSourceEntry>();

	private final List<CampaignSourceEntry> raceFileList =
			new ArrayList<CampaignSourceEntry>();
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
	private GenericLoader<PCTemplate> templateLoader = new GenericLoader<PCTemplate>(PCTemplate.class);
	private GenericLoader<Equipment> equipmentLoader = new GenericLoader<Equipment>(Equipment.class);
	private GenericLoader<EquipmentModifier> eqModLoader = new GenericLoader<EquipmentModifier>(EquipmentModifier.class);
	private CompanionModLoader companionModLoader = new CompanionModLoader();
	private KitLoader kitLoader = new KitLoader();
	private SimpleLoader<PaperInfo> paperLoader = new SimplePrefixLoader<PaperInfo>(PaperInfo.class, "NAME");
	private PointBuyLoader pointBuyLoader = new PointBuyLoader();
	private SimpleLoader<Sponsor> sponsorLoader = new SimplePrefixLoader<Sponsor>(Sponsor.class, "SPONSOR");
	private SimpleLoader<RuleCheck> ruleCheckLoader = new SimpleLoader<RuleCheck>(RuleCheck.class);
	private GenericLoader<Race> raceLoader = new GenericLoader<Race>(Race.class);
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

	/* (non-Javadoc)
	 * @see pcgen.persistence.SystemLoader#setChosenCampaignSourcefiles(java.util.List, pcgen.core.GameMode)
	 */
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
		//chosenCampaignSourcefiles.clear();
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
		File sponsorDir = new File(SettingsHandler.getPcgenSystemDir(), "sponsors");
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
			LoadContext context = Globals.getContext();
			loadCampaigns(aSelectedCampaignsList, context);

			// Load custom items
			loadCustomItems();

			finishLoad(aSelectedCampaignsList, context);
			// Check for valid race types
			//			checkRaceTypes();

			// Verify weapons are melee or ranged
			verifyWeaponsMeleeOrRanged();

			//  Auto-gen additional equipment
			if (!SettingsHandler.wantToLoadMasterworkAndMagic())
			{
				EquipmentList.autoGenerateEquipment();
			}

			for (Campaign campaign : aSelectedCampaignsList)
			{
				sourcesSet.add(SourceFormat.getFormattedString(campaign,
					SourceFormat.MEDIUM, true));
			}
			context.setLoaded(aSelectedCampaignsList);

			//  Show the licenses
			showLicensesIfNeeded();
			showSponsorsIfNeeded();
		}
		catch (Throwable thr)
		{
			Logging.errorPrint("Exception loading files.", thr);
			//TODO: Add user message here.
		}
		finally
		{
			releaseFileData();
			setChanged();
			notifyObservers("DONE");
		}
	}

	public void loadCampaigns(final List<Campaign> aSelectedCampaignsList,
			LoadContext context) throws PersistenceLayerException
	{
		// The first thing we need to do is load the
		// correct statsandchecks.lst file for this gameMode
		GameMode gamemode = SettingsHandler.getGame();
		if (gamemode == null)
		{
			// Autoload campaigns is set but there
			// is no current gameMode, so just return
			return;
		}
		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");
		File specificGameModeDir = new File(gameModeDir, gamemode.getFolderName());

		// Sort the campaigns
		sortCampaignsByRank(aSelectedCampaignsList);

		// Read the campaigns
		Collection<Campaign> loaded = readPccFiles(context,
				aSelectedCampaignsList, null, gamemode);

		// Add custom campaign files at the start of the lists
		addCustomFilesToStartOfList();

		// Notify our observers of how many files we intend
		// to load in total so that they can set up any
		// progress meters that they want to.
		setChanged();
		notifyObservers(Integer.valueOf(countTotalFilesToLoad()));

		// Load using the new LstFileLoaders

		// load ability categories first as they used to only be at the game mode
		abilityCategoryLoader.loadLstFiles(context, abilityCategoryFileList);

		for (Campaign c : loaded)
		{
			c.applyTo(context.ref);
		}
		
		// load weapon profs first
		wProfLoader.loadLstFiles(context, weaponProfFileList);
		WeaponProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(WeaponProf.class,
						"Unarmed Strike");
		if (wp == null)
		{
			wp = new WeaponProf();
			wp.setName(PropertyFactory.getString("Equipment.UnarmedStrike"));
			wp.put(StringKey.KEY_NAME, "Unarmed Strike");
			wp.addToListFor(ListKey.TYPE, Type.SIMPLE);
			Globals.getContext().ref.importObject(wp);
		}
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
		
		// Load the bio settings files - make sure we add them to the right game mode too!
		bioLoader.setGameMode(gamemode.getName());
		bioLoader.loadLstFiles(context, bioSetFileList);

		// Check for the default deities
		checkRequiredDeities(specificGameModeDir, context);

		// Add default EQ mods
		addDefaultEquipmentMods(context);

		classLoader.loadSubLines(context);
		
		/*
		 * This is technically bad behavior, but we at least want to provide
		 * the hint here since we are using WeakReferences as a container
		 * for references to ensure those that are not used are not
		 * resolved.
		 */
		System.gc();
	}

	private void finishLoad(final List<Campaign> aSelectedCampaignsList,
			LoadContext context)
	{
		createLangBonusObject(context);
		context.ref.buildDeferredObjects();
		context.ref.buildDerivedObjects();
		referenceAllCategories(context);
		context.resolveDeferredTokens();
		LoadValidator validator = new LoadValidator(aSelectedCampaignsList);
		context.ref.validate(validator);
		context.resolveReferences(validator);
		context.resolvePostDeferredTokens();
		for (Equipment eq : context.ref
				.getConstructedCDOMObjects(Equipment.class))
		{
			EqModAttachment.finishEquipment(eq);
		}
		int defaults = SizeUtilities.getDefaultSizeAdjustments();
		if (defaults == 0)
		{
			Logging.log(Logging.LST_WARNING,
					"Did not find a default size in Game Mode: "
							+ SettingsHandler.getGame().getName());
		}
		else if (defaults > 1)
		{
			Logging.log(Logging.LST_WARNING,
					"Found more than one size claiming to be default in Game Mode: "
							+ SettingsHandler.getGame().getName());
		}
		context.buildTypeLists();
	}

	private void referenceAllCategories(LoadContext context)
	{
		GameMode gamemode = SettingsHandler.getGame();
		for (AbilityCategory cat : gamemode.getAllAbilityCategories())
		{
			/*
			 * Yes, these are thrown away... just need to make sure the
			 * manufacturer was built.
			 */
			context.ref.getManufacturer(Ability.class, cat);
		}
	}

	public static void createLangBonusObject(LoadContext context)
	{
		Ability a = context.ref
				.constructCDOMObject(Ability.class, "*LANGBONUS");
		context.ref.reassociateCategory(AbilityCategory.LANGBONUS, a);
		a.put(ObjectKey.INTERNAL, true);
		context.unconditionallyProcess(a, "CHOOSE", "LANG|!PC,LANGBONUS");
		context.unconditionallyProcess(a, "VISIBLE", "NO");
		context.unconditionallyProcess(a, "AUTO", "LANG|%LIST");
		context.unconditionallyProcess(a, "MULT", "YES");
	}

	private void addDefaultEquipmentMods(LoadContext context)
			throws PersistenceLayerException
	{
		URI uri;
		try
		{
			uri = new URI("file:/" + eqModLoader.getClass().getName() + ".java");
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		context.setSourceURI(uri);
		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				uri);
		String aLine;
		aLine = "Add Type\tKEY:ADDTYPE\tTYPE:ALL\tCOST:0\tNAMEOPT:NONAME\tSOURCELONG:PCGen Internal\tCHOOSE:EQBUILDER.EQTYPE|COUNT=ALL|TITLE=desired TYPE(s)";
		eqModLoader.parseLine(context, null, aLine, source);
		
		//
		// Add internal equipment modifier for adding weapon/armor types to
		// equipment
		//
		aLine = Constants.s_INTERNAL_EQMOD_WEAPON
				+ "\tTYPE:Weapon\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(context, null, aLine, source);
		
		aLine = Constants.s_INTERNAL_EQMOD_ARMOR
				+ "\tTYPE:Armor\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(context, null, aLine, source);
	}

	/**
	 * @return total files to load
	 */
	private int countTotalFilesToLoad()
	{
		int count = bioSetFileList.size();
		count += abilityCategoryFileList.size();
		count += classFileList.size();
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

		// Now that those are loaded, make sure to initialize the recursive campaigns
		try
		{
			campaignLoader.initRecursivePccFiles();
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Failed to refresh campaigns", e);
		}

		Globals.sortPObjectListByName(Globals.getCampaignList());
	}

	/**
	 * Sets the options specified in the campaign aCamp.
	 * @param aCamp
	 */
	private static void setCampaignOptions(Campaign aCamp)
	{
		Set<String> keys = aCamp.getKeysFor(MapKey.PROPERTY);
		if (keys != null)
		{
			for (String key : keys)
			{
				String value = aCamp.get(MapKey.PROPERTY, key);
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
		customCampaign.addToListFor(ListKey.DESCRIPTION, new Description("Custom data"));

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
	private void checkRequiredDeities(File dir, LoadContext context) throws PersistenceLayerException
	{
		context.setSourceURI(new File(dir, "miscinfo.lst").toURI());
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
						if (!aEq.isType(Constants.TYPE_CUSTOM))
						{
							aEq.addType(Type.CUSTOM);
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

	private void loadGameModeInfoFile(GameMode gameMode, URI uri, String aType)
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
					Logging.errorPrint(PropertyFactory.getFormattedString(
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
				UnitSet.class, Constants.s_STANDARD_UNITSET_NAME);
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
			DEFAULT_UNIT_SET.setName(Constants.s_STANDARD_UNITSET_NAME);
			DEFAULT_UNIT_SET.setInternal(true);
			DEFAULT_UNIT_SET.setHeightUnit(Constants.s_STANDARD_UNITSET_HEIGHTUNIT);
			DEFAULT_UNIT_SET.setHeightFactor(Constants.s_STANDARD_UNITSET_HEIGHTFACTOR);
			DEFAULT_UNIT_SET.setHeightDisplayPattern(Constants.s_STANDARD_UNITSET_HEIGHTDISPLAYPATTERN);
			DEFAULT_UNIT_SET.setDistanceUnit(Constants.s_STANDARD_UNITSET_DISTANCEUNIT);
			DEFAULT_UNIT_SET.setDistanceFactor(Constants.s_STANDARD_UNITSET_DISTANCEFACTOR);
			DEFAULT_UNIT_SET.setDistanceDisplayPattern(Constants.s_STANDARD_UNITSET_DISTANCEDISPLAYPATTERN);
			DEFAULT_UNIT_SET.setWeightUnit(Constants.s_STANDARD_UNITSET_WEIGHTUNIT);
			DEFAULT_UNIT_SET.setWeightFactor(Constants.s_STANDARD_UNITSET_WEIGHTFACTOR);
			DEFAULT_UNIT_SET.setWeightDisplayPattern(Constants.s_STANDARD_UNITSET_WEIGHTDISPLAYPATTERN);
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
		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");

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

		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(), "gameModes");

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
	 * This method reads the PCC (Campaign) files and, if options are allowed to be set
	 * in the sources, sets the SettingsHandler settings to reflect the changes from the
	 * campaign files.
	 *
	 * @param aSelectedCampaignsList List of Campaigns to load
	 * @param currentPC
	 * @param game The gamemode that the campaigns are part of.
	 */
	private Collection<Campaign> readPccFiles(LoadContext context,
			final List<Campaign> aSelectedCampaignsList,
			final PlayerCharacter currentPC,
			final GameMode game)
	{
		// Prime options based on currently selected preferences
		if (SettingsHandler.isOptionAllowedInSources())
		{
			SettingsHandler.setOptionsProperties(currentPC);
		}

		Set<Campaign> loadedSet = new HashSet<Campaign>();
		
		// Create aggregate collections of source files to load
		// along with any options required by the campaigns...
		for (Campaign campaign : aSelectedCampaignsList)
		{
			loadedCampaigns.add(campaign);
			
					final URI sourceFile = campaign.getSourceURI();
			
					List<URI> files = getChosenCampaignSourcefiles(game);
					// Update the list of chosen campaign source files
					if (!files.contains(sourceFile))
					{
						files.add(sourceFile);
						SettingsHandler.getOptions().setProperty(
							"pcgen.files.chosenCampaignSourcefiles." + game.getName(),
							StringUtil.join(files, ", "));
			//			CoreUtility.join(chosenCampaignSourcefiles, ','));
					}
			
					// Update whether licenses need shown
					showOGL |= campaign.getSafe(ObjectKey.IS_OGL);
					showD20 |= campaign.getSafe(ObjectKey.IS_D20);
					showLicensed |= campaign.getSafe(ObjectKey.IS_LICENSED);
			
					if (campaign.getSafe(ObjectKey.IS_LICENSED))
					{
						List<String> licenseList = campaign.getSafeListFor(ListKey.LICENSE);
						if (licenseList != null && licenseList.size() > 0)
						{
							licensesToDisplayString.append(licenseList);
						}
			
						List<CampaignSourceEntry> licenseURIs = 
							campaign.getSafeListFor(ListKey.LICENSE_FILE);
						if (licenseURIs != null)
						{
							licenseFiles.addAll(licenseURIs);
						}
					}
					
					// check if maturity warning needs to be shown
					showMature |= campaign.getSafe(ObjectKey.IS_MATURE);
					
					if (campaign.getSafe(ObjectKey.IS_MATURE))
					{
						matureCampaigns.append(SourceFormat.LONG.getField(campaign) + 
							                   " (" + campaign.getSafe(StringKey.PUB_NAME_LONG) + ")<br>");
					}
			
					// Load the LST files to be loaded for the campaign
					lstExcludeFiles.addAll(campaign.getSafeListFor(ListKey.FILE_LST_EXCLUDE));
					raceFileList.addAll(campaign.getSafeListFor(ListKey.FILE_RACE));
					classFileList.addAll(campaign.getSafeListFor(ListKey.FILE_CLASS));
					companionmodFileList.addAll(campaign.getSafeListFor(ListKey.FILE_COMPANION_MOD));
					skillFileList.addAll(campaign.getSafeListFor(ListKey.FILE_SKILL));
					abilityCategoryFileList.addAll(campaign.getSafeListFor(ListKey.FILE_ABILITY_CATEGORY));
					abilityFileList.addAll(campaign.getSafeListFor(ListKey.FILE_ABILITY));
					featFileList.addAll(campaign.getSafeListFor(ListKey.FILE_FEAT));
					deityFileList.addAll(campaign.getSafeListFor(ListKey.FILE_DEITY));
					domainFileList.addAll(campaign.getSafeListFor(ListKey.FILE_DOMAIN));
					weaponProfFileList.addAll(campaign.getSafeListFor(ListKey.FILE_WEAPON_PROF));
					armorProfFileList.addAll(campaign.getSafeListFor(ListKey.FILE_ARMOR_PROF));
					shieldProfFileList.addAll(campaign.getSafeListFor(ListKey.FILE_SHIELD_PROF));
					equipmentFileList.addAll(campaign.getSafeListFor(ListKey.FILE_EQUIP));
					spellFileList.addAll(campaign.getSafeListFor(ListKey.FILE_SPELL));
					languageFileList.addAll(campaign.getSafeListFor(ListKey.FILE_LANGUAGE));
					templateFileList.addAll(campaign.getSafeListFor(ListKey.FILE_TEMPLATE));
					equipmodFileList.addAll(campaign.getSafeListFor(ListKey.FILE_EQUIP_MOD));
					kitFileList.addAll(campaign.getSafeListFor(ListKey.FILE_KIT));
					bioSetFileList.addAll(campaign.getSafeListFor(ListKey.FILE_BIO_SET));
			loadedSet.add(campaign);

			if (SettingsHandler.isOptionAllowedInSources())
			{
				setCampaignOptions(campaign);
			}
			
			// Add all sub-files to the main campaign, regardless of exclusions
			for (CampaignSourceEntry fName : campaign.getSafeListFor(ListKey.FILE_PCC))
			{
				URI uri = fName.getURI();
				if (PCGFile.isPCGenCampaignFile(uri))
				{
					Campaign subCampaign = Globals.getCampaignByURI(uri, false);
					if (loadedSet.add(subCampaign))
					{
						subCampaign.applyTo(context.ref);
					}
				}
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
		return loadedSet;
	}

	/**
	 * This method makes sure that the files specified by an LSTEXCLUDE tag
	 * are stripped out of the source files to be loaded on a global basis.
	 */
	private void stripLstExcludes()
	{
		stripLstExcludes(raceFileList);
		stripLstExcludes(classFileList);
		stripLstExcludes(companionmodFileList);
		stripLstExcludes(skillFileList);
		stripLstExcludes(abilityCategoryFileList);
		stripLstExcludes(abilityFileList);
		stripLstExcludes(featFileList);
		stripLstExcludes(deityFileList);
		stripLstExcludes(domainFileList);
		stripLstExcludes(weaponProfFileList);
		stripLstExcludes(armorProfFileList);
		stripLstExcludes(shieldProfFileList);
		stripLstExcludes(equipmentFileList);
		stripLstExcludes(spellFileList);
		stripLstExcludes(languageFileList);
		stripLstExcludes(templateFileList);
		stripLstExcludes(equipmodFileList);
		stripLstExcludes(kitFileList);
		stripLstExcludes(bioSetFileList);
	}

	private void stripLstExcludes(List<CampaignSourceEntry> list)
	{
		for (CampaignSourceEntry exc : lstExcludeFiles)
		{
			URI uri = exc.getURI();
			for (Iterator<CampaignSourceEntry> it = list.iterator(); it
					.hasNext();)
			{
				CampaignSourceEntry cse = it.next();
				if (cse.getURI().equals(uri))
				{
					it.remove();
				}
			}
		}
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
		spellFileList.clear();
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
						+ Constants.APPLICATION_NAME
						+ " cannot calculate \"to hit\" unless one of these is selected."
						+ Constants.s_LINE_SEP + "Source: "
						+ aEq.getSourceURI());
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

	public void initRecursivePccFiles() throws PersistenceLayerException
	{
		campaignLoader.initRecursivePccFiles();
	}

	public Collection<Campaign> getLoadedCampaigns()
	{
		return new ArrayList<Campaign>(loadedCampaigns);
	}

	public boolean isLoaded(Campaign campaign)
	{
		return loadedCampaigns.contains(campaign);
	}

	public void markAllUnloaded()
	{
		loadedCampaigns.clear();
	}

}
