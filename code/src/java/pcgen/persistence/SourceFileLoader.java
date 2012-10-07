/*
 * SourceFileLoader.java
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
 * Created on Apr 30, 2010, 10:02:45 PM
 */
package pcgen.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.DataSet;
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
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.EqModAttachment;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.io.PCGFile;
import pcgen.persistence.lst.AbilityCategoryLoader;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.BioSetLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.CompanionModLoader;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.KitLoader;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SpellLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.LoadValidator;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SourceFileLoader extends PCGenTask implements Observer
{

	/*
	 * File lists
	 */
	private final List<CampaignSourceEntry> bioSetFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> abilityCategoryFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> classFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> companionmodFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> deityFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> domainFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> equipmentFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> equipmodFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> abilityFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> featFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> kitFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> languageFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> raceFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> skillFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> spellFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> templateFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> weaponProfFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> armorProfFileList = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> shieldProfFileList = new ArrayList<CampaignSourceEntry>();
	/*
	 * Loaders
	 */
	private PCClassLoader classLoader = new PCClassLoader();
	private GenericLoader<Language> languageLoader = new GenericLoader<Language>(Language.class);
	private AbilityCategoryLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private CompanionModLoader companionModLoader = new CompanionModLoader();
	private KitLoader kitLoader = new KitLoader();
	private SpellLoader spellLoader = new SpellLoader();
	private BioSetLoader bioLoader = new BioSetLoader();
	private AbilityLoader abilityLoader = new AbilityLoader();
	private FeatLoader featLoader = new FeatLoader();
	private GenericLoader<PCTemplate> templateLoader = new GenericLoader<PCTemplate>(PCTemplate.class);
	private GenericLoader<Equipment> equipmentLoader = new GenericLoader<Equipment>(Equipment.class);
	private GenericLoader<EquipmentModifier> eqModLoader = new GenericLoader<EquipmentModifier>(EquipmentModifier.class);
	private GenericLoader<Race> raceLoader = new GenericLoader<Race>(Race.class);
	private GenericLoader<Skill> skillLoader = new GenericLoader<Skill>(Skill.class);
	private GenericLoader<WeaponProf> wProfLoader = new GenericLoader<WeaponProf>(WeaponProf.class);
	private GenericLoader<ArmorProf> aProfLoader = new GenericLoader<ArmorProf>(ArmorProf.class);
	private GenericLoader<ShieldProf> sProfLoader = new GenericLoader<ShieldProf>(ShieldProf.class);
	private GenericLoader<Deity> deityLoader = new GenericLoader<Deity>(Deity.class);
	private GenericLoader<Domain> domainLoader = new GenericLoader<Domain>(Domain.class);
	/*
	 * Other properties
	 */
	private final List<CampaignSourceEntry> lstExcludeFiles = new ArrayList<CampaignSourceEntry>();
	private final List<CampaignSourceEntry> licenseFiles = new ArrayList<CampaignSourceEntry>();
	private final Set<String> sourcesSet = new TreeSet<String>();
	private List<Campaign> loadedCampaigns = new ArrayList<Campaign>();
	private StringBuffer sec15 = new StringBuffer();
	private StringBuffer licensesToDisplayString = new StringBuffer();
	private StringBuffer matureCampaigns = new StringBuffer();
	private final CampaignSourceEntry globalCampaign;
	private boolean showD20 = false;
	private boolean showLicensed = true;
	private boolean showMature = false;
	private boolean showOGL = false;
	private List<Campaign> selectedCampaigns;
	private GameMode selectedGame;
	private DataSet dataset = null;
	private int progress = 0;
	private final UIDelegate uiDelegate;

	public SourceFileLoader(SourceSelectionFacade selection, UIDelegate delegate)
	{
		this.uiDelegate = delegate;
		selectedCampaigns = new ArrayList<Campaign>();
		for (CampaignFacade campaign : selection.getCampaigns())
		{
			Campaign camp = Globals.getCampaignKeyed(campaign.getName());
			selectedCampaigns.add(camp);
		}
		selectedGame = SystemCollections.getGameModeNamed(selection.getGameMode().getReference().getName());
		globalCampaign = new CampaignSourceEntry(new Campaign(),
												 URI.create("file:/System%20Configuration%20Document"));
		abilityCategoryLoader.addObserver(this);
		bioLoader.addObserver(this);
		companionModLoader.addObserver(this);
		deityLoader.addObserver(this);
		domainLoader.addObserver(this);
		equipmentLoader.addObserver(this);
		eqModLoader.addObserver(this);
		abilityLoader.addObserver(this);
		featLoader.addObserver(this);
		kitLoader.addObserver(this);
		languageLoader.addObserver(this);
		classLoader.addObserver(this);
		raceLoader.addObserver(this);
		skillLoader.addObserver(this);
		spellLoader.addObserver(this);
		templateLoader.addObserver(this);
		wProfLoader.addObserver(this);
		aProfLoader.addObserver(this);
		sProfLoader.addObserver(this);
	}

	@Override
	public void execute()
	{
		Globals.emptyLists();
		SettingsHandler.setGame(selectedGame.getName());
		Globals.emptyLists();

		LoadHandler handler = new LoadHandler();
		Logging.registerHandler(handler);
		try
		{
			loadCampaigns();
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Failed to load sources", e);
			uiDelegate.showErrorMessage(Constants.APPLICATION_NAME, "Failed to load sources, see log for details.");
		}
		Logging.removeHandler(handler);
	}

	public String getOGL()
	{
		return sec15.toString();
	}

	public String getLicenses()
	{
		return licensesToDisplayString.toString();
	}

	/**
	 * @return  a list of licenses read from the campaign license files
	 */
	public List<String> getOtherLicenses()
	{
		List<String> licenses = new ArrayList<String>();
		for (CampaignSourceEntry licenseFile : licenseFiles)
		{
			try
			{
				StringBuilder dataBuffer = LstFileLoader.readFromURI(licenseFile.getURI());
				licenses.add(dataBuffer.toString());
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Could not read license at " + licenseFile, e);
			}
		}
		return licenses;
	}

	public String getMatureInfo()
	{
		return matureCampaigns.toString();
	}

	/**
	 *
	 * @return the dataSet that contains all of the data that was loaded
	 */
	public DataSetFacade getDataSetFacade()
	{
		return dataset;
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
			tempSource = new CampaignSourceEntry(customCampaign, bioSetFile.toURI());
			bioSetFileList.remove(tempSource);
			bioSetFileList.add(0, tempSource);
		}

		//
		// Add the custom class file to the start of the list if it exists
		//
		File classFile = new File(CustomData.customClassFilePath(true));
		if (classFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, classFile.toURI());
			classFileList.remove(tempSource);
			classFileList.add(0, tempSource);
		}

		//
		// Add the custom deity file to the start of the list if it exists
		//
		File deityFile = new File(CustomData.customDeityFilePath(true));
		if (deityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, deityFile.toURI());
			deityFileList.remove(tempSource);
			deityFileList.add(0, tempSource);
		}

		//
		// Add the custom domain file to the start of the list if it exists
		//
		File domainFile = new File(CustomData.customDomainFilePath(true));
		if (domainFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, domainFile.toURI());
			domainFileList.remove(tempSource);
			domainFileList.add(0, tempSource);
		}

		//
		// Add the custom ability file to the start of the list if it exists
		//
		File abilityFile = new File(CustomData.customAbilityFilePath(true));
		if (abilityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, abilityFile.toURI());
			abilityFileList.remove(tempSource);
			abilityFileList.add(0, tempSource);
		}

		//
		// Add the custom feat file to the start of the list if it exists
		//
		File featFile = new File(CustomData.customFeatFilePath(true));
		if (featFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, featFile.toURI());
			featFileList.remove(tempSource);
			featFileList.add(0, tempSource);
		}

		//
		// Add the custom language file to the start of the list if it exists
		//
		File languageFile = new File(CustomData.customLanguageFilePath(true));
		if (languageFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, languageFile.toURI());
			languageFileList.remove(tempSource);
			languageFileList.add(0, tempSource);
		}

		//
		// Add the custom race file to the start of the list if it exists
		//
		File raceFile = new File(CustomData.customRaceFilePath(true));
		if (raceFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, raceFile.toURI());
			raceFileList.remove(tempSource);
			raceFileList.add(0, tempSource);
		}

		//
		// Add the custom skill file to the start of the list if it exists
		//
		File skillFile = new File(CustomData.customSkillFilePath(true));
		if (skillFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, skillFile.toURI());
			skillFileList.remove(tempSource);
			skillFileList.add(0, tempSource);
		}

		//
		// Add the custom spell file to the start of the list if it exists
		//
		File spellFile = new File(CustomData.customSpellFilePath(true));
		if (spellFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, spellFile.toURI());
			spellFileList.remove(tempSource);
			spellFileList.add(0, tempSource);
		}

		//
		// Add the custom template file to the start of the list if it exists
		//
		File templateFile = new File(CustomData.customTemplateFilePath(true));
		if (templateFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, templateFile.toURI());
			templateFileList.remove(tempSource);
			templateFileList.add(0, tempSource);
		}
	}

	private void addDefaultEquipmentMods(LoadContext context)
			throws PersistenceLayerException
	{
		URI uri = URI.create("file:/" + eqModLoader.getClass().getName() + ".java");
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
		aLine = Constants.INTERNAL_EQMOD_WEAPON +
				"\tTYPE:Weapon\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(context, null, aLine, source);

		aLine = Constants.INTERNAL_EQMOD_ARMOR +
				"\tTYPE:Armor\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(context, null, aLine, source);
	}

	private void loadCampaigns() throws PersistenceLayerException
	{
		// Unload the existing campaigns and load our selected campaign
		Globals.emptyLists();
		PersistenceManager pManager = PersistenceManager.getInstance();
		pManager.clear();
		List<URI> uris = new ArrayList<URI>();
		for (CampaignFacade campaignFacade : selectedCampaigns)
		{
			uris.add(((Campaign)campaignFacade).getSourceURI());
		}
		pManager.setChosenCampaignSourcefiles(uris);

		sourcesSet.clear();
		licenseFiles.clear();

		if (selectedCampaigns.size() == 0)
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
			loadCampaigns(selectedGame, selectedCampaigns, context);

			// Load custom items
			loadCustomItems();

			finishLoad(selectedCampaigns, context);
			// Check for valid race types
			//			checkRaceTypes();

			// Verify weapons are melee or ranged
			verifyWeaponsMeleeOrRanged();

			//  Auto-gen additional equipment
			if (!PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES))
			{
				EquipmentList.autoGenerateEquipment();
			}

			for (Campaign campaign : selectedCampaigns)
			{
				sourcesSet.add(SourceFormat.getFormattedString(campaign,
															   SourceFormat.MEDIUM, true));
			}
			context.setLoaded(selectedCampaigns);

			dataset = new DataSet(context, selectedGame, new DefaultListFacade<CampaignFacade>(selectedCampaigns));
//			//  Show the licenses
//			showLicensesIfNeeded();
//			showSponsorsIfNeeded();
		}
		catch (Throwable thr)
		{
			Logging.errorPrint("Exception loading files.", thr);
			uiDelegate.showErrorMessage(Constants.APPLICATION_NAME, "Failed to load campaigns, see log for details.");
		}
	}

	private void loadCampaigns(GameMode gamemode, final List<Campaign> aSelectedCampaignsList,
							   LoadContext context) throws PersistenceLayerException
	{
		Logging.log(Logging.INFO, "Loading game " + gamemode + " and sources "
			+ aSelectedCampaignsList + ".");
		
//		// The first thing we need to do is load the
//		// correct statsandchecks.lst file for this gameMode
//		GameMode gamemode = SettingsHandler.getGame();
//		if (gamemode == null)
//		{
//			// Autoload campaigns is set but there
//			// is no current gameMode, so just return
//			return;
//		}
		File gameModeDir = new File(ConfigurationSettings.getSystemsDir(), "gameModes");
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
		setMaximum(countTotalFilesToLoad());

		// Load using the new LstFileLoaders

		// load ability categories first as they used to only be at the game mode
		abilityCategoryLoader.loadLstFiles(context, abilityCategoryFileList);
		//validateAbilityCategories(gamemode);

		for (Campaign c : loaded)
		{
			c.applyTo(context.ref);
		}

		// load weapon profs first
		wProfLoader.loadLstFiles(context, weaponProfFileList);
		WeaponProf wp = Globals.getContext().ref.silentlyGetConstructedCDOMObject(WeaponProf.class,
																				  "Unarmed Strike");
		if (wp == null)
		{
			wp = new WeaponProf();
			wp.setName(LanguageBundle.getString("Equipment.UnarmedStrike"));
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

		// Load the bio settings files
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
		context.validateAssociations(validator);
		for (Equipment eq : context.ref.getConstructedCDOMObjects(Equipment.class))
		{
			EqModAttachment.finishEquipment(eq);
		}
		int defaults = SizeUtilities.getDefaultSizeAdjustments();
		if (defaults == 0)
		{
			Logging.log(Logging.LST_WARNING,
						"Did not find a default size in Game Mode: " +
					SettingsHandler.getGame().getName());
		}
		else if (defaults > 1)
		{
			Logging.log(Logging.LST_WARNING,
						"Found more than one size claiming to be default in Game Mode: " +
					SettingsHandler.getGame().getName());
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
		Ability a = context.ref.constructCDOMObject(Ability.class, "*LANGBONUS");
		context.ref.reassociateCategory(AbilityCategory.LANGBONUS, a);
		a.put(ObjectKey.INTERNAL, true);
		context.unconditionallyProcess(a, "CHOOSE", "LANG|!PC,LANGBONUS");
		context.unconditionallyProcess(a, "VISIBLE", "NO");
		context.unconditionallyProcess(a, "AUTO", "LANG|%LIST");
		context.unconditionallyProcess(a, "MULT", "YES");
	}

//
//	public String getSection15()
//	{
//	}
	private void loadCustomItems()
	{
		if (!PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT))
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
						aEq.setBase(null);
						aEq.load(aLine);
						if (!aEq.isType(Constants.TYPE_CUSTOM))
						{
							aEq.addType(Type.CUSTOM);
						}
						Globals.getContext().ref.importObject(aEq);
					}
				}
			}
			
			CustomData.setCustomItemsLoaded(true);
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
						"Weapon: " + aEq.getName() + " is neither Melee nor Ranged." +
						Constants.LINE_SEPARATOR + Constants.APPLICATION_NAME +
						" cannot calculate \"to hit\" unless one of these is selected." +
						Constants.LINE_SEPARATOR + "Source: " + aEq.getSourceURI());
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
	 * Logs an error taht has occured during data loading.
	 * This will not only log the message to the system error log,
	 * but it will also notify all observers of the error.
	 * @param message the error to notify listeners about
	 * @param e
	 */
	private void logError(String message, Throwable e)
	{
		Logging.errorPrint(message, e);
		//setChanged();
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
//		// Prime options based on currently selected preferences
//		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES))
//		{
//			SettingsHandler.setOptionsProperties(currentPC);
//		}

		Set<Campaign> loadedSet = new HashSet<Campaign>();

		// Create aggregate collections of source files to load
		// along with any options required by the campaigns...
		for (Campaign campaign : aSelectedCampaignsList)
		{
			Logging.debugPrint("Loading campaign " + campaign);
			
			loadedCampaigns.add(campaign);
//
//			final URI sourceFile = campaign.getSourceURI();
//
//			List<URI> files = getChosenCampaignSourcefiles(game);
//			// Update the list of chosen campaign source files
//			if (!files.contains(sourceFile))
//			{
//				files.add(sourceFile);
//				PCGenSettings.getInstance().setProperty(
//						"pcgen.files.chosenCampaignSourcefiles." + game.getName(),
//						StringUtil.join(files, ", "));
//				//			CoreUtility.join(chosenCampaignSourcefiles, ','));
//			}
			List<String> copyright = campaign.getListFor(ListKey.SECTION_15);
			if (copyright != null)
			{
				sec15.append("<br><b>Source Material:</b>");
				sec15.append(SourceFormat.getFormattedString(campaign,
															 SourceFormat.LONG, true));
				sec15.append("<br>");
				sec15.append("<b>Section 15 Entry in Source Material:</b><br>");
				for (String license : copyright)
				{
					sec15.append(license).append("<br>");
				}
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

			if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES))
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
				else
				{
					Logging.errorPrint("The referenced source " + uri + " is not valid.");
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
//		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES))
//		{
//			SettingsHandler.getOptionsFromProperties(currentPC);
//		}
		return loadedSet;
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
				PCGenSettings.OPTIONS_CONTEXT.setProperty(key, value);
			}
		}
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
			for (Iterator<CampaignSourceEntry> it = list.iterator(); it.hasNext();)
			{
				CampaignSourceEntry cse = it.next();
				if (cse.getURI().equals(uri))
				{
					it.remove();
				}
			}
		}
	}

	public boolean hasD20Campaign()
	{
		return showD20;
	}

	public boolean hasMatureCampaign()
	{
		return showMature;
	}

	public boolean hasOGLCampaign()
	{
		return showOGL;
	}

	public boolean hasLicensedCampaign()
	{
		return showLicensed;
	}

	public void update(Observable o, Object arg)
	{
		if (arg instanceof URI)
		{
			progress++;
			URI uri = (URI) arg;
			if ("file".equalsIgnoreCase(uri.getScheme()))
			{
				setProgress(new File(uri).getName(), progress);
			}
			else
			{
				setProgress(String.valueOf(uri), progress);
			}
			//setProgress(progress);
		}
		else if (arg instanceof Exception)
		{
			sendErrorMessage((Exception) arg);
		}
	}

	private class LoadHandler extends Handler
	{

		public LoadHandler()
		{
			setLevel(Logging.LST_WARNING);
		}

		@Override
		public void close() throws SecurityException
		{
			// Nothing to do
		}

		@Override
		public void flush()
		{
			// Nothing to do
		}

		@Override
		public void publish(final LogRecord arg0)
		{
			sendErrorMessage(arg0);
		}

	}

}
