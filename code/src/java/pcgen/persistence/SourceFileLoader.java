/*
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.util.AbstractMapToList;
import pcgen.base.util.FormatManager;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.scope.EquipmentPartScope;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.inst.GlobalModifiers;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
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
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.EqModAttachment;
import pcgen.core.analysis.RaceUtilities;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.io.PCGFile;
import pcgen.output.channel.ChannelUtilities;
import pcgen.persistence.lst.AbilityCategoryLoader;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.BioSetLoader;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.CompanionModLoader;
import pcgen.persistence.lst.FeatLoader;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.GenericLocalVariableLoader;
import pcgen.persistence.lst.GlobalModifierLoader;
import pcgen.persistence.lst.KitLoader;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SourceEntry;
import pcgen.persistence.lst.VariableLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.LoadValidator;
import pcgen.rules.context.ReferenceContextUtilities;
import pcgen.rules.context.VariableContext;
import pcgen.rules.persistence.CDOMControlLoader;
import pcgen.rules.persistence.DynamicLoader;
import pcgen.rules.persistence.TableLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PCGenTask;
import pcgen.util.Logging;

public class SourceFileLoader extends PCGenTask implements Observer
{

	/*
	 * File lists
	 */
	private final AbstractMapToList<ListKey<?>, CampaignSourceEntry> fileLists = new HashMapToList<>();

	/*
	 * Loaders
	 */
	private final PCClassLoader classLoader = new PCClassLoader();
	private final LstObjectFileLoader<Language> languageLoader = new GenericLoader<>(Language.class);
	private final LstLineFileLoader abilityCategoryLoader = new AbilityCategoryLoader();
	private final LstLineFileLoader companionModLoader = new CompanionModLoader();
	private final LstObjectFileLoader kitLoader = new KitLoader();
	private final LstLineFileLoader bioLoader = new BioSetLoader();
	private final LstObjectFileLoader abilityLoader = new AbilityLoader();
	private final LstObjectFileLoader featLoader = new FeatLoader();
	private final LstObjectFileLoader<PCTemplate> templateLoader = new GenericLoader<>(PCTemplate.class);
	private final LstObjectFileLoader<Equipment> equipmentLoader =
			new GenericLocalVariableLoader<>(Equipment.class, "PC.EQUIPMENT");
	private final LstObjectFileLoader<EquipmentModifier> eqModLoader =
			new GenericLocalVariableLoader<>(EquipmentModifier.class, EquipmentPartScope.PC_EQUIPMENT_PART);
	private final LstObjectFileLoader<Race> raceLoader = new GenericLoader<>(Race.class);
	private final LstObjectFileLoader<Skill> skillLoader = new GenericLocalVariableLoader<>(Skill.class, "PC.SKILL");
	private final LstObjectFileLoader<WeaponProf> wProfLoader = new GenericLoader<>(WeaponProf.class);
	private final LstObjectFileLoader<ArmorProf> aProfLoader = new GenericLoader<>(ArmorProf.class);
	private final LstObjectFileLoader<ShieldProf> sProfLoader = new GenericLoader<>(ShieldProf.class);
	private final LstObjectFileLoader<Deity> deityLoader = new GenericLoader<>(Deity.class);
	private final LstObjectFileLoader<Domain> domainLoader = new GenericLoader<>(Domain.class);
	private final LstObjectFileLoader<PCCheck> savesLoader = new GenericLocalVariableLoader<>(PCCheck.class, "PC.SAVE");
	private final LstObjectFileLoader<PCAlignment> alignmentLoader = new GenericLoader<>(PCAlignment.class);
	private final LstObjectFileLoader<PCStat> statLoader = new GenericLocalVariableLoader<>(PCStat.class, "PC.STAT");
	private final LstObjectFileLoader<SizeAdjustment> sizeLoader =
			new GenericLocalVariableLoader<>(SizeAdjustment.class, "PC.SIZE");
	private final LstObjectFileLoader<Spell> spellLoader = new GenericLoader<>(Spell.class);
	private final LstLineFileLoader dataControlLoader = new CDOMControlLoader();
	private final VariableLoader variableLoader = new VariableLoader();
	private final LstLineFileLoader tableLoader = new TableLoader();
	private final LstLineFileLoader globalModifierLoader = new GlobalModifierLoader();
	private final LstLineFileLoader dynamicLoader = new DynamicLoader();

	/*
	 * Other properties
	 */
	private final Collection<CampaignSourceEntry> licenseFiles = new ArrayList<>();
	private final Collection<String> sourcesSet = new TreeSet<>();
	private final Collection<Campaign> loadedCampaigns = new ArrayList<>();
	private final StringBuilder sec15 = new StringBuilder(500);
	private final StringBuilder licensesToDisplayString = new StringBuilder(500);
	private final StringBuilder matureCampaigns = new StringBuilder(100);
	private final CampaignSourceEntry globalCampaign;
	private boolean showD20 = false;
	private boolean showLicensed = true;
	private boolean showMature = false;
	private boolean showOGL = false;
	private final List<Campaign> selectedCampaigns;
	private final GameMode selectedGame;
	private DataSet dataset = null;
	private int progress = 0;
	private final UIDelegate uiDelegate;

	public SourceFileLoader(SourceSelectionFacade selection, UIDelegate delegate)
	{
		//Ensure object lists are not null (but rather empty)
		for (ListKey<CampaignSourceEntry> lk : CampaignLoader.OBJECT_FILE_LISTKEY)
		{
			fileLists.initializeListFor(lk);
		}
		this.uiDelegate = delegate;
		selectedCampaigns = new ArrayList<>();
		for (Campaign campaign : selection.getCampaigns())
		{
			Campaign camp = Globals.getCampaignKeyed(campaign.getKeyName());
			selectedCampaigns.add(camp);
		}
		selectedGame = SystemCollections.getGameModeNamed(selection.getGameMode().get().getName());
		globalCampaign = new CampaignSourceEntry(new Campaign(), URI.create("file:/System%20Configuration%20Document"));
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
		savesLoader.addObserver(this);
		alignmentLoader.addObserver(this);
		statLoader.addObserver(this);
		dataControlLoader.addObserver(this);
		dynamicLoader.addObserver(this);
	}

	@Override
	public void run()
	{
		Globals.emptyLists();
		SettingsHandler.setGame(selectedGame.getName());
		Globals.initPreferences();
		Globals.emptyLists();

		Handler handler = new LoadHandler();
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
	 * @return a list of licenses read from the campaign license files
	 */
	public Iterable<String> getOtherLicenses()
	{
		Collection<String> licenses = new ArrayList<>();
		for (CampaignSourceEntry licenseFile : licenseFiles)
		{
			try
			{
				String dataBuffer = LstFileLoader.readFromURI(licenseFile.getURI());
				licenses.add(dataBuffer);
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
		return fileLists.getKeySet().stream()
		                .mapToInt(fileLists::sizeOfListFor)
		                .sum();
	}

	private void addCustomFilesToStartOfList()
	{
		CampaignSourceEntry tempSource;

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
			fileLists.removeFromListFor(ListKey.FILE_BIO_SET, tempSource);
			fileLists.addToListFor(ListKey.FILE_BIO_SET, 0, tempSource);
		}

		//
		// Add the custom class file to the start of the list if it exists
		//
		File classFile = new File(CustomData.customClassFilePath(true));
		if (classFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, classFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_CLASS, tempSource);
			fileLists.addToListFor(ListKey.FILE_CLASS, 0, tempSource);
		}

		//
		// Add the custom deity file to the start of the list if it exists
		//
		File deityFile = new File(CustomData.customDeityFilePath(true));
		if (deityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, deityFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_DEITY, tempSource);
			fileLists.addToListFor(ListKey.FILE_DEITY, 0, tempSource);
		}

		//
		// Add the custom domain file to the start of the list if it exists
		//
		File domainFile = new File(CustomData.customDomainFilePath(true));
		if (domainFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, domainFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_DOMAIN, tempSource);
			fileLists.addToListFor(ListKey.FILE_DOMAIN, 0, tempSource);
		}

		//
		// Add the custom ability file to the start of the list if it exists
		//
		File abilityFile = new File(CustomData.customAbilityFilePath(true));
		if (abilityFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, abilityFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_ABILITY, tempSource);
			fileLists.addToListFor(ListKey.FILE_ABILITY, 0, tempSource);
		}

		//
		// Add the custom feat file to the start of the list if it exists
		//
		File featFile = new File(CustomData.customFeatFilePath(true));
		if (featFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, featFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_FEAT, tempSource);
			fileLists.addToListFor(ListKey.FILE_FEAT, 0, tempSource);
		}

		//
		// Add the custom language file to the start of the list if it exists
		//
		File languageFile = new File(CustomData.customLanguageFilePath(true));
		if (languageFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, languageFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_LANGUAGE, tempSource);
			fileLists.addToListFor(ListKey.FILE_LANGUAGE, 0, tempSource);
		}

		//
		// Add the custom race file to the start of the list if it exists
		//
		File raceFile = new File(CustomData.customRaceFilePath(true));
		if (raceFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, raceFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_RACE, tempSource);
			fileLists.addToListFor(ListKey.FILE_RACE, 0, tempSource);
		}

		//
		// Add the custom skill file to the start of the list if it exists
		//
		File skillFile = new File(CustomData.customSkillFilePath(true));
		if (skillFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, skillFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_SKILL, tempSource);
			fileLists.addToListFor(ListKey.FILE_SKILL, 0, tempSource);
		}

		//
		// Add the custom spell file to the start of the list if it exists
		//
		File spellFile = new File(CustomData.customSpellFilePath(true));
		if (spellFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, spellFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_SPELL, tempSource);
			fileLists.addToListFor(ListKey.FILE_SPELL, 0, tempSource);
		}

		//
		// Add the custom template file to the start of the list if it exists
		//
		File templateFile = new File(CustomData.customTemplateFilePath(true));
		if (templateFile.exists())
		{
			tempSource = new CampaignSourceEntry(customCampaign, templateFile.toURI());
			fileLists.removeFromListFor(ListKey.FILE_TEMPLATE, tempSource);
			fileLists.addToListFor(ListKey.FILE_TEMPLATE, 0, tempSource);
		}
	}

	private void addDefaultEquipmentMods(LoadContext context) throws PersistenceLayerException
	{
		URI uri = URI.create("file:/" + eqModLoader.getClass().getName() + ".java");
		context.setSourceURI(uri);
		SourceEntry source = new CampaignSourceEntry(new Campaign(), uri);
		LoadContext subContext = context.dropIntoContext("PC.EQUIPMENT");
		String aLine;
		aLine = "Add Type\tKEY:ADDTYPE\tTYPE:ALL\tCOST:0\tNAMEOPT:NONAME\tSOURCELONG:PCGen Internal\t"
				+ "CHOOSE:EQBUILDER.EQTYPE|COUNT=ALL|TITLE=desired TYPE(s)";
		eqModLoader.parseLine(subContext, null, aLine, source);

		//
		// Add internal equipment modifier for adding weapon/armor types to
		// equipment
		//
		aLine = Constants.INTERNAL_EQMOD_WEAPON + "\tTYPE:Weapon\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(subContext, null, aLine, source);

		aLine = Constants.INTERNAL_EQMOD_ARMOR + "\tTYPE:Armor\tVISIBLE:NO\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		eqModLoader.parseLine(subContext, null, aLine, source);
	}

	private void loadCampaigns() throws PersistenceLayerException
	{
		// Unload the existing campaigns and load our selected campaign
		Globals.emptyLists();
		PersistenceManager pManager = PersistenceManager.getInstance();
		List<URI> uris = new ArrayList<>();
		for (Campaign campaign : selectedCampaigns)
		{
			uris.add(campaign.getSourceURI());
		}
		pManager.setChosenCampaignSourcefiles(uris);

		sourcesSet.clear();
		licenseFiles.clear();

		if (selectedCampaigns.isEmpty())
		{
			throw new PersistenceLayerException("You must select at least one campaign to load.");
		}
		// 21 Nov 2002: Put load inside a try/finally block to make sure
		// that file lines were cleared even if an exception occurred.
		// -- sage_sam
		try
		{
			LoadContext context = Globals.getContext();
			loadCampaigns(selectedGame, selectedCampaigns, context);

			// Load custom items
			loadCustomItems(context);

			finishLoad(selectedGame, selectedCampaigns, context);
			// Check for valid race types
			//			checkRaceTypes();

			// Verify weapons are melee or ranged
			verifyWeaponsMeleeOrRanged(context);

			for (Campaign campaign : selectedCampaigns)
			{
				sourcesSet.add(SourceFormat.getFormattedString(campaign, SourceFormat.MEDIUM, true));
			}
			context.setLoaded(selectedCampaigns);

			/*
			 * This needs to happen after auto equipment generation and after
			 * context.setLoaded, not in finishLoad
			 */
			context.loadCampaignFacets();

			dataset = new DataSet(context, selectedGame, new DefaultListFacade<>(selectedCampaigns));
			//			//  Show the licenses
			//			showLicensesIfNeeded();
		}
		catch (Throwable thr)
		{
			Logging.errorPrint("Exception loading files.", thr);
			uiDelegate.showErrorMessage(Constants.APPLICATION_NAME, "Failed to load campaigns, see log for details.");
		}
	}

	private void loadCampaigns(GameMode gamemode, final List<Campaign> aSelectedCampaignsList, LoadContext context)
		throws PersistenceLayerException
	{
		Logging.log(Logging.INFO, "Loading game " + gamemode + " and sources " + aSelectedCampaignsList + ".");

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

		// Sort the campaigns
		sortCampaignsByRank(aSelectedCampaignsList);

		// Read the campaigns
		Collection<Campaign> loaded = readPccFiles(aSelectedCampaignsList);

		// Add custom campaign files at the start of the lists
		addCustomFilesToStartOfList();

		// Notify our observers of how many files we intend
		// to load in total so that they can set up any
		// progress meters that they want to.
		setMaximum(countTotalFilesToLoad());

		// Load using the new LstFileLoaders
		List<CampaignSourceEntry> dataDefFileList = fileLists.getListFor(ListKey.FILE_DATACTRL);
		dataDefFileList = addDefaultDataControlIfNeeded(dataDefFileList);
		dataControlLoader.loadLstFiles(context, dataDefFileList);
		processFactDefinitions(context);
		tableLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_DATATABLE));

		//Load Variables (foundation for other items)
		variableLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_VARIABLE));
		defineBuiltinVariables(context);
		dynamicLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_DYNAMIC));
		List<CampaignSourceEntry> globalModFileList = fileLists.getListFor(ListKey.FILE_GLOBALMOD);
		if (globalModFileList.isEmpty())
		{
			File defaultGameModeDir = new File(gameModeDir, "default");
			File df = new File(defaultGameModeDir, "compatibilityGlobalModifier.lst");
			Campaign c = new Campaign();
			c.setName("Default Global Modifier File");
			CampaignSourceEntry cse = new CampaignSourceEntry(c, df.toURI());
			globalModFileList.add(cse);
		}
		globalModifierLoader.loadLstFiles(context, globalModFileList);

		// load ability categories first as they used to only be at the game mode
		abilityCategoryLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_ABILITY_CATEGORY));

		//Force all AbilityCategory objects to be imported as manufacturers
		for (AbilityCategory ac : context.getReferenceContext().getConstructedCDOMObjects(AbilityCategory.class))
		{
			context.getReferenceContext().getManufacturerId(ac);
		}

		for (Campaign c : loaded)
		{
			c.applyTo(context.getReferenceContext());
		}

		sizeLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_SIZE));
		//Now load PCC stat, check, alignment
		statLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_STAT));
		savesLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_SAVE));
		alignmentLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_ALIGNMENT));

		// load weapon profs first
		wProfLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_WEAPON_PROF));
		WeaponProf wp =
				context.getReferenceContext().silentlyGetConstructedCDOMObject(WeaponProf.class, "Unarmed Strike");
		if (wp == null)
		{
			wp = new WeaponProf();
			wp.setName(LanguageBundle.getString("Equipment.UnarmedStrike"));
			wp.put(StringKey.KEY_NAME, "Unarmed Strike");
			wp.addToListFor(ListKey.TYPE, Type.SIMPLE);
			context.getReferenceContext().importObject(wp);
		}

		aProfLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_ARMOR_PROF));
		sProfLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_SHIELD_PROF));

		// load skills before classes to handle class skills
		skillLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_SKILL));

		// load before races to handle auto known languages
		languageLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_LANGUAGE));

		// load before race or class to handle feats
		featLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_FEAT));

		// load before race or class to handle abilities
		abilityLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_ABILITY));

		raceLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_RACE));

		//Domain must load before CLASS - thpr 10/29/06
		domainLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_DOMAIN));

		spellLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_SPELL));
		deityLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_DEITY));

		classLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_CLASS));

		templateLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_TEMPLATE));

		// loaded before equipment (required)
		eqModLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_EQUIP_MOD));

		equipmentLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_EQUIP));
		companionModLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_COMPANION_MOD));
		kitLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_KIT));

		// Load the bio settings files
		bioLoader.setGameMode(gamemode.getName());
		bioLoader.loadLstFiles(context, fileLists.getListFor(ListKey.FILE_BIO_SET));

		// Add default EQ mods
		addDefaultEquipmentMods(context);

		classLoader.loadSubLines(context);

		/*
		 * This is technically bad behavior, but we at least want to provide the
		 * hint here since we are using WeakReferences as a container for
		 * references to ensure those that are not used are not resolved.
		 */
		System.gc(); // NOPMD
	}

	/**
	 * Places the built in variables, if required, into the given LoadContext.
	 * 
	 * @param context
	 *            The LoadContext in which the built in variables will be loaded, if
	 *            necessary
	 */
	public static void defineBuiltinVariables(LoadContext context)
	{
		CControl.getChannelConstants().stream()
			.filter(control -> allowControl(context, control))
			.forEach(control -> enableBuiltInControl(context, control));
	}

	private static boolean allowControl(LoadContext context, CControl control)
	{
		return control.getControllingFeature().isEmpty() || ControlUtilities
			.hasControlToken(context, control.getControllingFeature().get());
	}

	/**
	 * Enables the built-in version of the given Code Control.
	 * 
	 * @param context
	 *            The LoadContext in which the CodeControl exists
	 * @param control
	 *            The CodeControl to be enabled using its default values
	 */
	private static void enableBuiltInControl(LoadContext context,
		CControl control)
	{
		AbstractReferenceContext referenceContext = context.getReferenceContext();
		FormatManager<?> formatManager = referenceContext.getFormatManager(control.getFormat());
		String varName = control.getDefaultValue();
		if (control.isChannel())
		{
			varName = ChannelUtilities.createVarName(varName);
		}
		defineVariable(context.getVariableContext(), formatManager, varName);
		if (control.isAutoGranted())
		{
			GlobalModifiers modifiers = referenceContext
				.constructNowIfNecessary(GlobalModifiers.class, GlobalModifierLoader.GLOBAL_MODIFIERS);
			modifiers.addGrantedVariable(varName);
		}
	}

	private static void defineVariable(VariableContext varContext, FormatManager<?> formatManager, String varName)
	{
		LegalScope varScope = varContext.getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
		varContext.assertLegalVariableID(varName, varScope, formatManager);
	}

	/**
	 * Add default data control files to the supplied list, but only if it is empty.
	 * 
	 * @param dataDefFileList The list of data control files.
	 */
	public static List<CampaignSourceEntry> addDefaultDataControlIfNeeded(List<CampaignSourceEntry> dataDefFileList)
	{
		if (dataDefFileList == null)
		{
			dataDefFileList = new ArrayList<>();
		}
		if (dataDefFileList.isEmpty())
		{
			File gameModeDir = new File(ConfigurationSettings.getSystemsDir(), "gameModes");
			File defaultGameModeDir = new File(gameModeDir, "default");
			File df = new File(defaultGameModeDir, "compatibilityDataControl.lst");
			Campaign c = new Campaign();
			c.setName("Default Data Control File");
			CampaignSourceEntry cse = new CampaignSourceEntry(c, df.toURI());
			dataDefFileList.add(cse);
		}
		return dataDefFileList;
	}

	public static void processFactDefinitions(LoadContext context)
	{
		Collection<? extends ContentDefinition> defs =
				context.getReferenceContext().getConstructedCDOMObjects(FactDefinition.class);
		for (ContentDefinition<?, ?> fd : defs)
		{
			fd.activate(context);
		}
		defs = context.getReferenceContext().getConstructedCDOMObjects(FactSetDefinition.class);
		for (ContentDefinition<?, ?> fd : defs)
		{
			fd.activate(context);
		}
	}

	private void finishLoad(GameMode gameMode, List<Campaign> aSelectedCampaignsList, LoadContext context)
	{
		createLangBonusObject(context);
		AbstractReferenceContext refContext = context.getReferenceContext();
		refContext.buildDeferredObjects();
		refContext.buildDerivedObjects();
		referenceAllCategories(context);
		context.resolveDeferredTokens();
		LoadValidator validator = new LoadValidator(aSelectedCampaignsList);
		refContext.validate(validator);
		refContext.resolveReferences(validator);
		context.resolvePostValidationTokens();
		context.resolvePostDeferredTokens();
		context.getVariableContext().validateDefaults();
		//Test for items we know we use (temporary)
		//Alignment
		if (ControlUtilities.isFeatureEnabled(context, CControl.ALIGNMENTFEATURE)
			&& !context.getVariableContext().hasDefaultModifier(refContext.getManufacturer(PCAlignment.class)))
		{
			Logging.errorPrint("GameMode " + gameMode.getName() + " has Alignment text - "
				+ "Thus it  requires a default value for ALIGNMENT format");
		}
		//Face
		if (!context.getVariableContext().hasDefaultModifier(FormatUtilities.ORDEREDPAIR_MANAGER))
		{
			Logging.errorPrint(gameMode.getName() + " did not have required default value for ORDEREDPAIR format");
		}

		ReferenceContextUtilities.validateAssociations(refContext, validator);
		for (Equipment eq : refContext.getConstructedCDOMObjects(Equipment.class))
		{
			eq.setToCustomSize(null);
			EqModAttachment.finishEquipment(eq);
		}
		if (RaceUtilities.getUnselectedRace() == null)
		{
			Logging.errorPrint(gameMode.getName() + " did not have required Race with 'Unselected' Group");
		}
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
			context.getReferenceContext().getManufacturerId(cat);
		}
	}

	public static void createLangBonusObject(LoadContext context)
	{
		Ability a = AbilityCategory.LANGBONUS.newInstance();
		a.setKeyName("*LANGBONUS");
		context.getReferenceContext().importObject(a);
		a.put(ObjectKey.INTERNAL, true);
		context.unconditionallyProcess(a, "CHOOSE", "LANG|!PC,LANGBONUS");
		context.unconditionallyProcess(a, "VISIBLE", "NO");
		context.unconditionallyProcess(a, "AUTO", "LANG|%LIST");
		context.unconditionallyProcess(a, "MULT", "YES");
	}

	private void loadCustomItems(LoadContext context)
	{
		if (!PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT))
		{
			return;
		}

		final BufferedReader br = CustomData.getCustomEquipmentReader();

		try
		{
			if (br != null)
			{
				String aLine = br.readLine();

				if (aLine == null)
				{
					return;
				}

				if (aLine.startsWith("BASEITEM:"))
				{
					final int idx = aLine.indexOf('\t', 9);

					if (idx < 10)
					{
						return;
					}

					final String baseItemKey = aLine.substring(9, idx);
					aLine = aLine.substring(idx + 1);

					Equipment aEq = context.getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class,
						baseItemKey);

					if (aEq != null)
					{
						aEq = aEq.clone();
						aEq.setBase();
						aEq.load(aLine, "\t", ":", null);
						if (!aEq.isType(Constants.TYPE_CUSTOM))
						{
							aEq.addType(Type.CUSTOM);
						}
						context.getReferenceContext().importObject(aEq);
					}
				}
			}

			CustomData.setCustomItemsLoaded(true);
		}
		catch (IOException e)
		{
			logError("Error when loading custom items", e);
		}
	}

	/**
	 * This method is called to verify that all weapons loaded from the
	 * equipment files are classified as either Melee or Ranged. This is
	 * required so that to-hit values can be calculated for that weapon.
	 *
	 * @throws PersistenceLayerException
	 *             if a weapon is neither melee or ranged, indicating the name
	 *             of the weapon that caused the error
	 */
	private static void verifyWeaponsMeleeOrRanged(LoadContext context) throws PersistenceLayerException
	{
		//
		// Check all the weapons to see if they are either Melee or Ranged, to avoid
		// problems when we go to export/preview the character
		//
		for (Equipment aEq : context.getReferenceContext().getConstructedCDOMObjects(Equipment.class))
		{
			if (aEq.isWeapon() && !aEq.isMelee() && !aEq.isRanged())
			{
				throw new PersistenceLayerException(
					"Weapon: " + aEq.getName() + " is neither Melee nor Ranged." + Constants.LINE_SEPARATOR
						+ Constants.APPLICATION_NAME + " cannot calculate \"to hit\" unless one of these is selected."
						+ Constants.LINE_SEPARATOR + "Source: " + aEq.getSourceURI());
			}
		}
	}

	/**
	 * This method sorts the provided listof Campaign objects by rank.
	 *
	 * @param aSelectedCampaignsList
	 *            List of Campaign objects to sort
	 */
	public static void sortCampaignsByRank(final List<Campaign> aSelectedCampaignsList)
	{
		aSelectedCampaignsList.sort((c1, c2) -> c2.getSafe(IntegerKey.CAMPAIGN_RANK) - c1.getSafe(IntegerKey.CAMPAIGN_RANK));

	}

	/**
	 * Logs an error that has occurred during data loading. This will not only
	 * log the message to the system error log, but it will also notify all
	 * observers of the error.
	 * 
	 * @param message
	 *            the error to notify listeners about
	 * @param e
	 */
	private void logError(String message, Throwable e)
	{
		Logging.errorPrint(message, e);
		//setChanged();
	}

	/**
	 * This method reads the PCC (Campaign) files and, if options are allowed to
	 * be set in the sources, sets the SettingsHandler settings to reflect the
	 * changes from the campaign files.
	 *
	 * @param aSelectedCampaignsList
	 *            List of Campaigns to load
	 */
	private Collection<Campaign> readPccFiles(Iterable<Campaign> aSelectedCampaignsList)
	{
		Collection<Campaign> loadedSet = new HashSet<>();

		// Create aggregate collections of source files to load
		// along with any options required by the campaigns...
		for (Campaign campaign : aSelectedCampaignsList)
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Loading campaign " + campaign);
			}

			loadedCampaigns.add(campaign);

			List<String> copyright = campaign.getListFor(ListKey.SECTION_15);
			if (copyright != null)
			{
				sec15.append("<br><b>Source Material:</b>");
				sec15.append(SourceFormat.getFormattedString(campaign, SourceFormat.LONG, true));
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
				if (licenseList != null && !licenseList.isEmpty())
				{
					licensesToDisplayString.append(licenseList);
				}

				List<CampaignSourceEntry> licenseURIs = campaign.getSafeListFor(ListKey.LICENSE_FILE);
				if (licenseURIs != null)
				{
					licenseFiles.addAll(licenseURIs);
				}
			}

			// check if maturity warning needs to be shown
			showMature |= campaign.getSafe(ObjectKey.IS_MATURE);

			if (campaign.getSafe(ObjectKey.IS_MATURE))
			{
				matureCampaigns.append(SourceFormat.LONG.getField(campaign)).append(" (").append(campaign.getSafe(StringKey.PUB_NAME_LONG)).append(")<br>");
			}

			// Load the LST files to be loaded for the campaign
			addQualifiedSources(campaign, ListKey.FILE_LST_EXCLUDE);
			for (ListKey<CampaignSourceEntry> lk : CampaignLoader.OBJECT_FILE_LISTKEY)
			{
				addQualifiedSources(campaign, lk);
			}
			loadedSet.add(campaign);

			if (PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_ALLOWED_IN_SOURCES, true))
			{
				setCampaignOptions(campaign);
			}

			// Add all sub-files to the main campaign, regardless of exclusions
			for (CampaignSourceEntry fName : campaign.getSafeListFor(ListKey.FILE_PCC))
			{
				URI uri = fName.getURI();
				if (PCGFile.isPCGenCampaignFile(uri))
				{
					loadedSet.add(Globals.getCampaignByURI(uri, false));
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

		return loadedSet;
	}

	/**
	 * Add only those source files that either have no requirements, or that the
	 * requirements are satisfied.
	 * 
	 * @param c
	 *            The list being populated.
	 * @param lk
	 *            The list of potential sources to be added.
	 */
	private void addQualifiedSources(Campaign c, ListKey<CampaignSourceEntry> lk)
	{
		for (CampaignSourceEntry cse : c.getSafeListFor(lk))
		{
			List<Prerequisite> prerequisites = cse.getPrerequisites();
			if (prerequisites.isEmpty() || PrereqHandler.passesAll(prerequisites, null, cse))
			{
				fileLists.addToListFor(lk, cse);
			}
		}
	}

	/**
	 * Sets the options specified in the campaign aCamp.
	 * 
	 * @param aCamp
	 */
	private static void setCampaignOptions(Campaign aCamp)
	{
		Set<String> keys = aCamp.getKeysFor(MapKey.PROPERTY);
		if (keys != null)
		{
			for (final String key : keys)
			{
				String value = aCamp.get(MapKey.PROPERTY, key);
				if (key.contains("."))
				{
					PCGenSettings.getInstance().setProperty(key, value);
				}
				else
				{
					PCGenSettings.OPTIONS_CONTEXT.setProperty(key, value);
				}
				// Note: This is just until we transition all settings from the legacy settings
				SettingsHandler.setPCGenOption(key, value);
			}
			// Make sure any game mode settings are applied.
			SystemCollections.getUnmodifiableGameModeList().forEach(GameMode::applyPreferences);
		}
	}

	/**
	 * This method makes sure that the files specified by an LSTEXCLUDE tag are
	 * stripped out of the source files to be loaded on a global basis.
	 */
	private void stripLstExcludes()
	{
		for (ListKey<?> lk : fileLists.getKeySet())
		{
			if (!ListKey.FILE_LST_EXCLUDE.equals(lk))
			{
				stripLstExcludes(lk);
			}
		}
	}

	private void stripLstExcludes(ListKey<?> lk)
	{
		List<CampaignSourceEntry> excludes = fileLists.getListFor(ListKey.FILE_LST_EXCLUDE);
		if (excludes != null)
		{
			for (CampaignSourceEntry exc : excludes)
			{
				URI uri = exc.getURI();
				for (CampaignSourceEntry cse : fileLists.getListFor(lk))
				{
					if (cse.getURI().equals(uri))
					{
						fileLists.removeFromListFor(lk, cse);
					}
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

	@Override
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

	private final class LoadHandler extends Handler
	{

		private LoadHandler()
		{
			setLevel(Logging.LST_WARNING);
		}

		@Override
		public void close()
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
