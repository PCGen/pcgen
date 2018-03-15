/*
 * Copyright 2009 (C) James Dempsey
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
package pcgen.gui2.facade;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.undo.UndoManager;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.NumericPCAttribute;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCAttribute;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.AutoEquipmentFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.fact.XPFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AgeSet;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.GameMode;
import pcgen.core.GearBuySellScheme;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.RollingMethods;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SimpleFacadeImpl;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.VariableProcessor;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.RaceUtilities;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.display.BonusDisplay;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.kit.BaseKit;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.AbilityCategoryFacade;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.HitPointListener;
import pcgen.facade.core.CharacterStubFacade;
import pcgen.facade.core.ClassFacade;
import pcgen.facade.core.CompanionSupportFacade;
import pcgen.facade.core.CoreViewNodeFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.DeityFacade;
import pcgen.facade.core.DescriptionFacade;
import pcgen.facade.core.DomainFacade;
import pcgen.facade.core.EquipModFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentListFacade.EquipmentListEvent;
import pcgen.facade.core.EquipmentListFacade.EquipmentListListener;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.core.GenderFacade;
import pcgen.facade.core.HandedFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.KitFacade;
import pcgen.facade.core.LanguageChooserFacade;
import pcgen.facade.core.LanguageFacade;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.core.SimpleFacade;
import pcgen.facade.core.SkillFacade;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.SpellSupportFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.core.TempBonusFacade;
import pcgen.facade.core.TemplateFacade;
import pcgen.facade.core.TodoFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.core.UIDelegate.CustomEquipResult;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.facade.util.event.ChangeListener;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;
import pcgen.io.PCGIOHandler;
import pcgen.output.channel.ChannelCompatibility;
import pcgen.output.channel.ChannelUtilities;
import pcgen.pluginmgr.PluginManager;
import pcgen.pluginmgr.messages.PlayerCharacterWasClosedMessage;
import pcgen.system.CharacterManager;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.View;

/**
 * The Class {@code CharacterFacadeImpl} is an implementation of
 * the {@link CharacterFacade} interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 * TODO: Who is responsible for undo management and how will it work?
 */
public class CharacterFacadeImpl implements CharacterFacade, EquipmentListListener, ListListener<EquipmentFacade>,
		HitPointListener 
{

	private static final PlayerCharacter DUMMY_PC = new PlayerCharacter();
	private List<ClassFacade> pcClasses;
	private DefaultListFacade<TempBonusFacade> appliedTempBonuses;
	private DefaultListFacade<TempBonusFacade> availTempBonuses;
	private WriteableReferenceFacade<PCAlignment> alignment;
	private DefaultListFacade<EquipmentSetFacade> equipmentSets;
	private DefaultReferenceFacade<GenderFacade> gender;
	private DefaultListFacade<CharacterLevelFacade> pcClassLevels;
    private DefaultListFacade<HandedFacade> availHands;
    private DefaultListFacade<GenderFacade> availGenders;
	private Map<StatFacade, WriteableReferenceFacade<Number>> statScoreMap;
	private final UndoManager undoManager;
	private final DelegatingDataSet dataSet;
	private DefaultReferenceFacade<RaceFacade> race;
	private DefaultReferenceFacade<DeityFacade> deity;
	private DefaultReferenceFacade<String> tabName;
	private DefaultReferenceFacade<String> name;
	private DefaultReferenceFacade<String> playersName;
	private PlayerCharacter theCharacter;
	private CharacterDisplay charDisplay;
	private DefaultReferenceFacade<EquipmentSetFacade> equipSet;
	private DefaultListFacade<LanguageFacade> languages;
	private EquipmentListFacadeImpl purchasedEquip;
	private DefaultReferenceFacade<File> file;
	private DefaultReferenceFacade<HandedFacade> handedness;
	private final UIDelegate delegate;
	private Set<Language> autoLanguagesCache;
	private CharacterLevelsFacadeImpl charLevelsFacade;
	private DefaultReferenceFacade<Integer> currentXP;
	private DefaultReferenceFacade<Integer> xpForNextlevel;
	private DefaultReferenceFacade<String> xpTableName;
	private DefaultReferenceFacade<String> characterType;
	private DefaultReferenceFacade<String> previewSheet;
	private DefaultReferenceFacade<SkillFilter> skillFilter;
	private DefaultReferenceFacade<Integer> age;
	private DefaultReferenceFacade<SimpleFacade> ageCategory;
	private DefaultListFacade<SimpleFacade> ageCategoryList;
	private DefaultReferenceFacade<String> poolPointText;
	private DefaultReferenceFacade<String> statTotalLabelText;
	private DefaultReferenceFacade<String> statTotalText;
	private DefaultReferenceFacade<String> modTotalLabelText;
	private DefaultReferenceFacade<String> modTotalText;
	private DefaultReferenceFacade<Integer> numBonusLang;
	private DefaultReferenceFacade<Integer> numSkillLang;
	private DefaultReferenceFacade<Integer> hpRef;
	private DefaultReferenceFacade<Integer> rollMethodRef;
	private DefaultReferenceFacade<String> carriedWeightRef;
	private DefaultReferenceFacade<String> loadRef;
	private DefaultReferenceFacade<String> weightLimitRef;
	private DefaultListFacade<DomainFacade> domains;
	private DefaultListFacade<DomainFacade> availDomains;
	private DefaultReferenceFacade<Integer> maxDomains;
	private DefaultReferenceFacade<Integer> remainingDomains;
	private DefaultListFacade<TemplateFacade> templates;
	private DefaultListFacade<RaceFacade> raceList;
	private DefaultListFacade<KitFacade> kitList;
	private DefaultReferenceFacade<File> portrait;
	private RectangleReference cropRect;
	private String selectedGender;
	private List<Language> currBonusLangs;
	private DefaultReferenceFacade<String> skinColor;
	private DefaultReferenceFacade<String> hairColor;
	private DefaultReferenceFacade<String> eyeColor;
	private DefaultReferenceFacade<Integer> heightRef;
	private DefaultReferenceFacade<Integer> weightRef;
	private DefaultReferenceFacade<BigDecimal> fundsRef;
	private DefaultReferenceFacade<BigDecimal> wealthRef;
	private DefaultReferenceFacade<GearBuySellFacade> gearBuySellSchemeRef;

	private Gui2InfoFactory infoFactory;
	private CharacterAbilities characterAbilities;
	private DescriptionFacade descriptionFacade;
	private SpellSupportFacadeImpl spellSupportFacade;
	private CompanionSupportFacadeImpl companionSupportFacade;
	private TodoManager todoManager;
	private boolean allowDebt;

	private int lastExportCharSerial = 0;
	private PlayerCharacter lastExportChar = null;
	private LanguageListener langListener;
	private TemplateListener templateListener;
	private XPListener xpListener;
	private AutoEquipListener autoEquipListener;

	/**
	 * Create a new character facade for an existing character.
	 * 
	 * @param pc The character to be represented
	 * @param delegate the UIDelegate for this CharacterFacade
	 * @param dataSetFacade The data set in use for the character
	 */
	public CharacterFacadeImpl(PlayerCharacter pc, UIDelegate delegate,
		DataSetFacade dataSetFacade)
	{
		this.delegate = delegate;
		theCharacter = pc;
		charDisplay = pc.getDisplay();
		dataSet = new DelegatingDataSet(dataSetFacade);
		buildAgeCategories();
		initForCharacter();
		undoManager = new UndoManager();
	}

	@Override
	public void closeCharacter()
	{
		FacetLibrary.getFacet(LanguageFacet.class)
			.removeDataFacetChangeListener(langListener);
		FacetLibrary.getFacet(TemplateFacet.class)
			.removeDataFacetChangeListener(templateListener);
		FacetLibrary.getFacet(XPFacet.class).removeDataFacetChangeListener(
			xpListener);
		FacetLibrary.getFacet(AutoEquipmentFacet.class).removeDataFacetChangeListener(autoEquipListener);

		characterAbilities.closeCharacter();
		charLevelsFacade.closeCharacter();
        companionSupportFacade.closeCharacter();
		PluginManager
			.getInstance()
			.getPostbox()
			.handleMessage(
				new PlayerCharacterWasClosedMessage(this, theCharacter));
		Globals.getPCList().remove(theCharacter);
		lastExportChar = null;
		/*
		 * Unfortunately, a dummy rather than null is necessary because the UI
		 * does model swaps and such that do not pause events in the UI so that
		 * it is trying to update things that do not exist
		 */
		theCharacter = DUMMY_PC;
		charDisplay = null;
        dataSet.detachDelegates();
	}
	

	private void initForCharacter()
	{
		// Calculate any active bonuses
		theCharacter.preparePCForOutput();

		todoManager = new TodoManager();

		infoFactory = new Gui2InfoFactory(theCharacter);
		characterAbilities = new CharacterAbilities(theCharacter, delegate, dataSet, todoManager);
		descriptionFacade = new DescriptionFacadeImpl(theCharacter);
		spellSupportFacade = new SpellSupportFacadeImpl(theCharacter, delegate, dataSet, todoManager, this);

		name = new DefaultReferenceFacade<>(charDisplay.getName());
		file = new DefaultReferenceFacade<>(new File(charDisplay.getFileName()));
		
		companionSupportFacade = new CompanionSupportFacadeImpl(theCharacter, todoManager, name, file, this);
		
		availTempBonuses = new DefaultListFacade<>();
		refreshAvailableTempBonuses();
		appliedTempBonuses = new DefaultListFacade<>();
		buildAppliedTempBonusList();
		kitList = new DefaultListFacade<>();
		refreshKitList();

		statScoreMap = new HashMap<>();
		for (StatFacade stat : dataSet.getStats())
		{
			if (stat instanceof PCStat)
			{
				statScoreMap.put(stat, getStatReferenceFacade(stat));
			}
			else
			{
				statScoreMap.put(stat, new DefaultReferenceFacade<>());
			}
		}

		File portraitFile = null;
		if (!StringUtils.isEmpty(charDisplay.getPortraitPath()))
		{
			portraitFile = new File(charDisplay.getPortraitPath());
		}
		portrait = new DefaultReferenceFacade<>(portraitFile);
		cropRect = new RectangleReference(charDisplay.getPortraitThumbnailRect());
		characterType = new DefaultReferenceFacade<>(charDisplay.getCharacterType());
		previewSheet = new DefaultReferenceFacade<>(charDisplay.getPreviewSheet());
		skillFilter = new DefaultReferenceFacade<>(charDisplay.getSkillFilter());

		tabName = new DefaultReferenceFacade<>(charDisplay.getTabName());
		playersName = new DefaultReferenceFacade<>(charDisplay.getPlayersName());
		race = new DefaultReferenceFacade<>(charDisplay.getRace());
		raceList = new DefaultListFacade<>();
		if (charDisplay.getRace() != null && !charDisplay.getRace().isUnselected())
		{
			raceList.addElement(charDisplay.getRace());
		}
		handedness = new DefaultReferenceFacade<>();
		gender = new DefaultReferenceFacade<>();
        
        availHands = new DefaultListFacade<>();
        availGenders = new DefaultListFacade<>();
		for (Handed handed : Handed.values())
		{
			availHands.addElement(handed);
		}
		for (Gender gender : Gender.values())
		{
			availGenders.addElement(gender);
		}
        
		if (charDisplay.getRace() != null)
		{
			for (HandedFacade handsFacade : availHands)
			{
				if (handsFacade.equals(charDisplay.getHandedObject()))
				{
					handedness.set(handsFacade);
					break;
				}
			}
			for (GenderFacade pcGender : availGenders)
			{
				if (pcGender.equals(charDisplay.getGenderObject()))
				{
					gender.set(pcGender);
					break;
				}
			}
		}

		GameMode game = (GameMode) dataSet.getGameMode();
		if (!game.getAlignmentText().isEmpty())
		{
			String channelName = ControlUtilities.getControlToken(Globals.getContext(),
				CControl.ALIGNMENTINPUT);
			alignment = (WriteableReferenceFacade<PCAlignment>) ChannelUtilities
					.getGlobalChannel(theCharacter.getCharID(), channelName);
		}
		age = new DefaultReferenceFacade<>(charDisplay.getAge());
		ageCategory = new DefaultReferenceFacade<>();
		updateAgeCategoryForAge();
		currentXP = new DefaultReferenceFacade<>(charDisplay.getXP());
		xpListener = new XPListener();
		FacetLibrary.getFacet(XPFacet.class).addDataFacetChangeListener(xpListener);
		xpForNextlevel = new DefaultReferenceFacade<>(charDisplay.minXPForNextECL());
		xpTableName = new DefaultReferenceFacade<>(charDisplay.getXPTableName());
		hpRef = new DefaultReferenceFacade<>(theCharacter.hitPoints());

		skinColor = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.SKINCOLOR));
		hairColor = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.HAIRCOLOR));
		eyeColor = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.EYECOLOR));
		weightRef = new DefaultReferenceFacade<>();
		heightRef = new DefaultReferenceFacade<>();
		refreshHeightWeight();

		purchasedEquip = new EquipmentListFacadeImpl(theCharacter.getEquipmentMasterList());
		autoEquipListener = new AutoEquipListener();
		FacetLibrary.getFacet(AutoEquipmentFacet.class).addDataFacetChangeListener(autoEquipListener);
		carriedWeightRef = new DefaultReferenceFacade<>();
		loadRef = new DefaultReferenceFacade<>();
		weightLimitRef = new DefaultReferenceFacade<>();
		equipSet = new DefaultReferenceFacade<>();
		equipmentSets = new DefaultListFacade<>();
		initEquipSet();

		rollMethodRef = new DefaultReferenceFacade<>(game.getRollMethod());

		charLevelsFacade =
				new CharacterLevelsFacadeImpl(theCharacter, delegate,
					todoManager, dataSet, this);
		pcClasses = new ArrayList<>();
		pcClassLevels = new DefaultListFacade<>();
		refreshClassLevelModel();
		charLevelsFacade.addHitPointListener(this);

		deity =
                new DefaultReferenceFacade<>(charDisplay.getDeity());
		domains = new DefaultListFacade<>();
		maxDomains =
                new DefaultReferenceFacade<>(
                        theCharacter.getMaxCharacterDomains());
		remainingDomains =
                new DefaultReferenceFacade<>(
                        theCharacter.getMaxCharacterDomains() - domains.getSize());
		availDomains = new DefaultListFacade<>();
		buildAvailableDomainsList();

		templates =
                new DefaultListFacade<>(
                        charDisplay.getDisplayVisibleTemplateList());
		templateListener = new TemplateListener(); 
		FacetLibrary.getFacet(TemplateFacet.class).addDataFacetChangeListener(templateListener);

		initTodoList();

		statTotalLabelText = new DefaultReferenceFacade<>();
		statTotalText = new DefaultReferenceFacade<>();
		modTotalLabelText = new DefaultReferenceFacade<>();
		modTotalText = new DefaultReferenceFacade<>();
		updateScorePurchasePool(false);

		languages = new DefaultListFacade<>();
		numBonusLang = new DefaultReferenceFacade<>(0);
		numSkillLang = new DefaultReferenceFacade<>(0);
		refreshLanguageList();
		langListener = new LanguageListener();
		FacetLibrary.getFacet(LanguageFacet.class).addDataFacetChangeListener(langListener);

		purchasedEquip.addListListener(spellSupportFacade);
		purchasedEquip.addEquipmentListListener(spellSupportFacade);
		fundsRef = new DefaultReferenceFacade<>(theCharacter.getGold());
		wealthRef = new DefaultReferenceFacade<>(theCharacter.totalValue());
		gearBuySellSchemeRef = new DefaultReferenceFacade<>(findGearBuySellRate());
		allowDebt = false;
	}

	private WriteableReferenceFacade<Number> getStatReferenceFacade(StatFacade stat)
	{
		return ChannelCompatibility.getStatScore(theCharacter.getCharID(), (PCStat) stat);
	}

	/**
	 * Build up the list of kits that the character has.
	 */
	private void refreshKitList()
	{
		List<Kit> kits = new ArrayList<>();
		for (Kit kit : charDisplay.getKitInfo())
		{
			kits.add(kit);
		}
		kitList.updateContents(kits);
	}

	private GearBuySellFacade findGearBuySellRate()
	{
		int buyRate = SettingsHandler.getGearTab_BuyRate();
		int sellRate = SettingsHandler.getGearTab_SellRate();
		for (GearBuySellFacade buySell : dataSet.getGearBuySellSchemes())
		{
			GearBuySellScheme scheme = (GearBuySellScheme) buySell;
			if (scheme.getBuyRate().intValue() == buyRate && scheme.getSellRate().intValue() == sellRate)
			{
				return scheme;
			}
		}

		GearBuySellScheme scheme =
				new GearBuySellScheme(LanguageBundle.getString("in_custom"), //$NON-NLS-1$
					new BigDecimal(buyRate), new BigDecimal(sellRate),
					new BigDecimal(100));
		return scheme;
	}

	/**
	 * Initialise the equipment set facades, ensuring that the character has a 
	 * default equipment set. 
	 */
	private void initEquipSet()
	{
		// Setup the default EquipSet if not already present
		if (!charDisplay.hasEquipSet())
		{
			String id = EquipmentSetFacadeImpl.getNewIdPath(charDisplay, null);
			EquipSet eSet = new EquipSet(id, LanguageBundle.getString("in_ieDefault"));
			theCharacter.addEquipSet(eSet);
			theCharacter.setCalcEquipSetId(id);
		}

		// Detach listeners from old set
		if (equipSet.get() != null)
		{
			EquipmentListFacade equippedItems = equipSet.get().getEquippedItems();
			equippedItems.removeListListener(this);
			equippedItems.removeEquipmentListListener(this);
		}
		
		// Make facades for each root equipset.
		List<EquipmentSetFacade> eqSetList = new ArrayList<>();
		EquipmentSetFacade currSet = null;
		String currIdPath = theCharacter.getCalcEquipSetId();
		for (EquipSet es : charDisplay.getEquipSet())
		{
			if (es.getParentIdPath().equals("0"))
			{
				final EquipmentSetFacadeImpl facade =
						new EquipmentSetFacadeImpl(delegate, theCharacter, es,
							dataSet, purchasedEquip, todoManager, this);
				eqSetList.add(facade);
				if (es.getIdPath().equals(currIdPath))
				{
					currSet = facade;
				}
			}
		}
		equipmentSets.updateContents(eqSetList);
		if (currSet != null)
		{
			equipSet.set(currSet);
		}

		EquipmentSetFacade set = equipSet.get();
		set.getEquippedItems().addListListener(this);
		set.getEquippedItems().addEquipmentListListener(this);
		refreshTotalWeight();

	}

	/**
	 * Create the list of known age categories in the current BioSet. 
	 */
	private void buildAgeCategories()
	{
		List<String> cats = new ArrayList<>();
		for (String aString : SettingsHandler.getGame().getBioSet().getAgeCategories())
		{
			final int idx = aString.indexOf('\t');

			if (idx >= 0)
			{
				aString = aString.substring(0, idx);
			}

			if (!cats.contains(aString))
			{
				cats.add(aString);
			}
		}
		Collections.sort(cats);
		ageCategoryList = new DefaultListFacade<>();
		for (String ageCat : cats)
		{
			ageCategoryList.addElement(new SimpleFacadeImpl(ageCat));
		}
	}

	/**
	 * Create an initial list of todo items 
	 */
	private void initTodoList()
	{
		if (isNewCharName(charDisplay.getName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Name", "in_sumTodoName", 1));
		}
		if (charDisplay.getRace() == null || charDisplay.getRace().isUnselected())
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Race", "in_irTodoRace", 100));
		}

		// Stats todo already done in updateScorePurchasePool
		updateLevelTodo();
	}

	/**
	 * Identify if the supplied name is a default one generated by the system
	 * e.g. Unnamed 1 or Unnamed 2
	 * @param charName The name to be checked.
	 * @return True if the name is a default.
	 */
	private boolean isNewCharName(String charName)
	{
		if (charName == null)
		{
			return true;
		}

		return charName.startsWith("Unnamed"); //$NON-NLS-1$
	}

    @Override
    public ListFacade<HandedFacade> getAvailableHands()
    {
        return availHands;
    }

    @Override
    public ListFacade<GenderFacade> getAvailableGenders()
    {
        return availGenders;
    }

	/**
	 * @see pcgen.core.facade.CharacterFacade#addAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	@Override
	public void addAbility(AbilityCategoryFacade category, AbilityFacade ability)
	{
		characterAbilities.addAbility(category, ability);
		refreshKitList();
		refreshAvailableTempBonuses();
		buildAvailableDomainsList();
		companionSupportFacade.refreshCompanionData();
		refreshEquipment();
		hpRef.set(theCharacter.hitPoints());
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#removeAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	@Override
	public void removeAbility(AbilityCategoryFacade category, AbilityFacade ability)
	{
		characterAbilities.removeAbility(category, ability);
		refreshKitList();
		companionSupportFacade.refreshCompanionData();
		hpRef.set(theCharacter.hitPoints());
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAbilities(pcgen.core.facade.AbilityCategoryFacade)
	 */
	@Override
	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category)
	{
		return characterAbilities.getAbilities(category);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getActiveAbilityCategories()
	 */
	@Override
	public ListFacade<AbilityCategoryFacade> getActiveAbilityCategories()
	{
		return characterAbilities.getActiveAbilityCategories();
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getTotalSelections(pcgen.core.facade.AbilityCategoryFacade)
	 */
	@Override
	public int getTotalSelections(AbilityCategoryFacade category)
	{
		return characterAbilities.getTotalSelections(category);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getRemainingSelections(pcgen.core.facade.AbilityCategoryFacade)
	 */
	@Override
	public int getRemainingSelections(AbilityCategoryFacade category)
	{
		return characterAbilities.getRemainingSelections(category);
	}

	@Override
	public void addAbilityCatSelectionListener(ChangeListener listener)
	{
		characterAbilities.addAbilityCatSelectionListener(listener);
	}

	@Override
	public void removeAbilityCatSelectionListener(ChangeListener listener)
	{
		characterAbilities.removeAbilityCatSelectionListener(listener);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setRemainingSelection(pcgen.core.facade.AbilityCategoryFacade, int)
	 */
	@Override
	public void setRemainingSelection(AbilityCategoryFacade category, int remaining)
	{
		characterAbilities.setRemainingSelection(category, remaining);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#hasAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	@Override
	public boolean hasAbility(AbilityCategoryFacade category, AbilityFacade ability)
	{
		return characterAbilities.hasAbility(category, ability);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAbilityNature(pcgen.core.facade.AbilityFacade)
	 */
	@Override
	public Nature getAbilityNature(AbilityFacade ability)
	{
		if (ability == null || !(ability instanceof Ability))
		{
			return null;
		}
		/*
		 * TODO This is making a somewhat DRASTIC assumption that ANY Ability
		 * Category is appropriate. Unfortunately, the point at which this
		 * method is called from the UI it is unclear to the untrained eye how
		 * to get the category.
		 */
		List<CNAbility> cnas = theCharacter.getMatchingCNAbilities((Ability) ability);
		Nature nature = null;
		for (CNAbility cna : cnas)
		{
			nature = Nature.getBestNature(nature, cna.getNature());
		}
		return nature;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#addCharacterLevels(pcgen.core.facade.ClassFacade[])
	 */
	@Override
	public void addCharacterLevels(ClassFacade[] classes)
	{
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		//SettingsHandler.setShowStatDialogAtLevelUp(false);

		int oldLevel = charLevelsFacade.getSize();
		boolean needFullRefresh = false;

		for (ClassFacade classFacade : classes)
		{
			if (classFacade instanceof PCClass)
			{
				int totalLevels = charDisplay.getTotalLevels();
				if (!validateAddLevel((PCClass) classFacade))
				{
					return;
				}
				Logging.log(Logging.INFO, charDisplay.getName()
					+ ": Adding level " + (totalLevels + 1) //$NON-NLS-1$
					+ " in class " + classFacade); //$NON-NLS-1$
				theCharacter.incrementClassLevel(1, (PCClass) classFacade);
				if (totalLevels == charDisplay.getTotalLevels())
				{
					// The level change was rejected - no further processing needed.
					return;
				}
				if (((PCClass) classFacade).containsKey(ObjectKey.EXCHANGE_LEVEL))
				{
					needFullRefresh = true;
				}
			}
			if (!pcClasses.contains(classFacade))
			{
				pcClasses.add(classFacade);
			}
			CharacterLevelFacadeImpl cl = new CharacterLevelFacadeImpl(classFacade,
					charLevelsFacade.getSize() + 1);
			pcClassLevels.addElement(cl);
			charLevelsFacade.addLevelOfClass(cl);
		}
		CharacterUtils.selectClothes(getTheCharacter());
		
		// Calculate any active bonuses
		theCharacter.calcActiveBonuses();

		if (needFullRefresh)
		{
			refreshClassLevelModel();
		}
		postLevellingUpdates();
		delegate.showLevelUpInfo(this, oldLevel);
	}

	/**
	 * Ensure any items that could be affected by the level up or down are refreshed.
	 */
	void postLevellingUpdates()
	{
		characterAbilities.rebuildAbilityLists();
		companionSupportFacade.refreshCompanionData();
		refreshKitList();
		refreshAvailableTempBonuses();
		refreshEquipment();
		currentXP.set(charDisplay.getXP());
		xpForNextlevel.set(charDisplay.minXPForNextECL());
		xpTableName.set(charDisplay.getXPTableName());
		hpRef.set(theCharacter.hitPoints());
		age.set(charDisplay.getAge());
		refreshHeightWeight();
		refreshStatScores();

		updateLevelTodo();
		buildAvailableDomainsList();
		spellSupportFacade.refreshAvailableKnownSpells();
		updateScorePurchasePool(false);
		refreshLanguageList();
	}

	/**
	 * Ensure any items that could be affected by the new equipment are refreshed.
	 */
	void postEquippingUpdates()
	{
		characterAbilities.rebuildAbilityLists();
		refreshAvailableTempBonuses();
		hpRef.set(theCharacter.hitPoints());
	}

	private void refreshHeightWeight()
	{
		weightRef.set(Globals.getGameModeUnitSet()
			.convertWeightToUnitSet(charDisplay.getWeight()));
		heightRef.set((int) Math.round(Globals.getGameModeUnitSet()
			.convertHeightToUnitSet(charDisplay.getHeight())));
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#removeCharacterLevels(int)
	 */
	@Override
	public void removeCharacterLevels(int levels)
	{
		for (int i = levels; i > 0 && !pcClassLevels.isEmpty(); i--)
		{
			ClassFacade classFacade = charLevelsFacade
					.getClassTaken(pcClassLevels.getElementAt(pcClassLevels.getSize() - 1));
			pcClassLevels.removeElement(pcClassLevels.getSize() - 1);
			if (classFacade instanceof PCClass)
			{
				Logging.log(Logging.INFO, charDisplay.getName()
					+ ": Removing level " + (pcClassLevels.getSize()+1) //$NON-NLS-1$
					+ " in class " + classFacade); //$NON-NLS-1$
				theCharacter.incrementClassLevel(-1, (PCClass) classFacade);
			}
			charLevelsFacade.removeLastLevel();
		}

		// Clean up the class list 
		for (Iterator<ClassFacade> iterator = pcClasses.iterator(); iterator.hasNext();)
		{
			ClassFacade classFacade = iterator.next();
			boolean stillPresent = false;
			for (CharacterLevelFacade charLevel : pcClassLevels)
			{
				if (charLevelsFacade.getClassTaken(charLevel) == classFacade)
				{
					stillPresent = true;
					break;
				}
			}

			if (!stillPresent)
			{
				iterator.remove();
			}
		}
		postLevellingUpdates();
	}

	/**
	 * Update the todo list to reflect the change in level or experience.
	 */
	private void updateLevelTodo()
	{
		if (charDisplay.getXP() >= charDisplay.minXPForNextECL())
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Class", "in_clTodoLevelUp", 120));
		}
		else
		{
			todoManager.removeTodo("in_clTodoLevelUp");
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getClassLevel(pcgen.core.facade.ClassFacade)
	 */
	@Override
	public int getClassLevel(ClassFacade c)
	{
		int clsLevel = 0;
		// We have to compare by class key as classes get cloned and we may have multiple instances of the same class in our level list 
		String classKey = c.getKeyName();
		for (CharacterLevelFacade charLevel : pcClassLevels)
		{
			if (charLevelsFacade.getClassTaken(charLevel).getKeyName().equals(classKey))
			{
				clsLevel++;
			}
		}
		return clsLevel;
	}

	private boolean validateAddLevel(PCClass theClass)
	{
		int levels = 1;

		if (theClass == null)
		{
			return false;
		}

		if (!theCharacter.isQualified(theClass))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_clYouAreNotQualifiedToTakeTheClass"));
			return false;
		}

		if (!theCharacter.canLevelUp())
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_Enforce_rejectLevelUp"));
			return false;
		}

		final PCClass aClass = theCharacter.getClassKeyed(theClass.getKeyName());

		// Check if the subclass (if any) is qualified for
		if (aClass != null)
		{
			String subClassKey = charDisplay.getSubClassName(aClass);
			if (subClassKey != null)
			{
				final PCClass subClass = aClass.getSubClassKeyed(subClassKey);
				if (subClass != null && !theCharacter.isQualified(subClass))
				{
					delegate.showErrorMessage(Constants.APPLICATION_NAME,
						LanguageBundle.getFormattedString(
							"in_sumYouAreNotQualifiedToTakeTheClass", //$NON-NLS-1$
							aClass.getDisplayName()
								+ "/" + subClass.getDisplayName())); //$NON-NLS-1$
					return false;
				}
			}
		}

		if (!Globals.checkRule(RuleConstants.LEVELCAP)
				&& theClass.hasMaxLevel()
				&& ((levels > theClass.getSafe(IntegerKey.LEVEL_LIMIT)) || ((aClass != null) && ((charDisplay
						.getLevel(aClass) + levels) > aClass.getSafe(IntegerKey.LEVEL_LIMIT)))))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_sumMaximumLevelIs", //$NON-NLS-1$
							String.valueOf(theClass.getSafe(IntegerKey.LEVEL_LIMIT))));
			return false;
		}

		// Check with the user on their first level up
		if (charDisplay.getTotalLevels() == 0)
		{
			if (SettingsHandler.getGame().isPurchaseStatMode()
					&& (theCharacter.getPointBuyPoints() > getUsedStatPool()))
			{
				if (!delegate.showWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"), //$NON-NLS-1$
						LanguageBundle.getString("in_sumPoolWarning")))//$NON-NLS-1$
				{
					return false;
				}
			}
			else if (allAbilitiesAreZero())
			{
				if (!delegate.showWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"),
						LanguageBundle.getString("in_sumAbilitiesZeroWarning")))
				{
					return false;
				}
			}
			else
			{
				Boolean proceed = delegate.maybeShowWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"),
						LanguageBundle.getString("in_sumAbilitiesWarning"),
						LanguageBundle.getString("in_sumAbilitiesWarningCheckBox"), PCGenSettings.OPTIONS_CONTEXT,
						PCGenSettings.OPTION_SHOW_WARNING_AT_FIRST_LEVEL_UP);
				if (Boolean.FALSE.equals(proceed))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determine if all of the character's stats are still set to 0.
	 *
	 * @return True if they are all zero, false if any are non-zero.
	 */
	private boolean allAbilitiesAreZero()
	{
		for (StatFacade stat : dataSet.getStats())
		{
			ReferenceFacade<Number> facade = getScoreBaseRef(stat);

			if (facade.get().intValue() != 0)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * This method gets the number of stat points used in the pool
	 * @param pc The PlayerCharacter to get used stat pool for
	 * @return used stat pool
	 */
	private int getUsedStatPool()
	{
		int i = 0;

		for (PCStat aStat : charDisplay.getStatSet())
		{
			if (!aStat.getSafe(ObjectKey.ROLLED))
			{
				continue;
			}

			final int statValue = theCharacter.getBaseStatFor(aStat);
			i += getPurchaseCostForStat(theCharacter, statValue);
		}
		i += (int) theCharacter.getTotalBonusTo("POINTBUY", "SPENT"); //$NON-NLS-1$ //$NON-NLS-2$
		return i;
	}

	private static int getPurchaseCostForStat(final PlayerCharacter aPC, int statValue)
	{
		final int iMax = SettingsHandler.getGame().getPurchaseScoreMax(aPC);
		final int iMin = SettingsHandler.getGame().getPurchaseScoreMin(aPC);

		if (statValue > iMax)
		{
			statValue = iMax;
		}

		if (statValue >= iMin)
		{
			return SettingsHandler.getGame().getAbilityScoreCost(statValue - iMin);
		}
		return 0;
	}

	void refreshAvailableTempBonuses()
	{
		List<TempBonusFacadeImpl> tempBonuses = new ArrayList<>();

		// first objects on the PC
		for (CDOMObject cdo : theCharacter.getCDOMObjectList())
		{
			scanForTempBonuses(tempBonuses, cdo);
		}

		//
		// next do all abilities to get TEMPBONUS:ANYPC only
		GameMode game = (GameMode) dataSet.getGameMode();
		for (AbilityCategory cat : game.getAllAbilityCategories())
		{
			if (cat.getParentCategory() == cat)
			{
				for (Ability aFeat : Globals.getContext().getReferenceContext()
					.getManufacturerId(cat).getAllObjects())
				{
					scanForAnyPcTempBonuses(tempBonuses, aFeat);
				}
			}
		}

		//
		// Do all the PC's spells
		for (Spell aSpell : theCharacter.aggregateSpellList("", "", "", 0, 9))
		{
			scanForTempBonuses(tempBonuses, aSpell);
		}

		// Do all the pc's innate spells.
		Collection<CharacterSpell> innateSpells = theCharacter.getCharacterSpells(charDisplay.getRace(),
				Constants.INNATE_SPELL_BOOK_NAME);
		for (CharacterSpell aCharacterSpell : innateSpells)
		{
			if (aCharacterSpell == null)
			{
				continue;
			}
			scanForTempBonuses(tempBonuses, aCharacterSpell.getSpell());
		}

		//
		// Next do all spells to get TEMPBONUS:ANYPC or TEMPBONUS:EQUIP
		for (Spell spell : Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(Spell.class))
		{
			scanForNonPcTempBonuses(tempBonuses, spell);
		}

		// do all Templates to get TEMPBONUS:ANYPC or TEMPBONUS:EQUIP
		for (PCTemplate aTemp : Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(PCTemplate.class))
		{
			scanForNonPcTempBonuses(tempBonuses, aTemp);
		}

		Collections.sort(tempBonuses);
		availTempBonuses.updateContents(tempBonuses);
	}

	private void scanForNonPcTempBonuses(List<TempBonusFacadeImpl> tempBonuses, PObject obj)
	{
		if (obj == null)
		{
			return;
		}
		if (TempBonusHelper.hasNonPCTempBonus(obj))
		{
			tempBonuses.add(new TempBonusFacadeImpl(obj));
		}
	}

	private void scanForAnyPcTempBonuses(List<TempBonusFacadeImpl> tempBonuses, PObject obj)
	{
		if (obj == null)
		{
			return;
		}
		if (TempBonusHelper.hasAnyPCTempBonus(obj))
		{
			tempBonuses.add(new TempBonusFacadeImpl(obj));
		}
	}

	private void scanForTempBonuses(List<TempBonusFacadeImpl> tempBonuses, CDOMObject obj)
	{
		if (obj == null)
		{
			return;
		}
		if (TempBonusHelper.hasTempBonus(obj))
		{
			tempBonuses.add(new TempBonusFacadeImpl(obj));
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAvailableTempBonuses()
	 */
	@Override
	public ListFacade<TempBonusFacade> getAvailableTempBonuses()
	{
		return availTempBonuses;
	}

	/**
	 * Build up the list of temporary bonuses which have been applied to this character.
	 */
	private void buildAppliedTempBonusList()
	{
		Set<String> found = new HashSet<>();
		for (TempBonusInfo tbi : theCharacter.getTempBonusMap().values())
		{
			Object aC = tbi.source;
			Object aT = tbi.target;
			String name = BonusDisplay.getBonusDisplayName(tbi);

			if (!found.contains(name))
			{
				found.add(name);
				TempBonusFacadeImpl facade = new TempBonusFacadeImpl((CDOMObject) aC, aT, name);
				facade.setActive(!theCharacter.getTempBonusFilters().contains(
					name));
				appliedTempBonuses.addElement(facade);
			}
		}
		
		
	}

	@Override
	public void addTempBonus(TempBonusFacade bonusFacade)
	{
		if (bonusFacade == null || !(bonusFacade instanceof TempBonusFacadeImpl))
		{
			return;
		}
		TempBonusFacadeImpl tempBonus = (TempBonusFacadeImpl) bonusFacade;
		
		// Allow selection of target for bonus affecting equipment
		CDOMObject originObj = tempBonus.getOriginObj();
		Equipment aEq = null;
		Object target = 
				TempBonusHelper.getTempBonusTarget(originObj,
					theCharacter, delegate, infoFactory);
		if (target == null)
		{
			return;
		}
		TempBonusFacadeImpl appliedTempBonus;
		if (target instanceof Equipment)
		{
			aEq = (Equipment) target;
			appliedTempBonus =
					TempBonusHelper.applyBonusToCharacterEquipment(aEq,
						originObj, theCharacter);
		}
		else
		{
			appliedTempBonus =
					TempBonusHelper.applyBonusToCharacter(originObj,
						theCharacter);
		}

		// Resolve choices and apply the bonus to the character.
		if (appliedTempBonus == null)
		{
			return;
		}
		
		appliedTempBonuses.addElement(appliedTempBonus);
		refreshStatScores();
		postLevellingUpdates();
	}

	@Override
	public void removeTempBonus(TempBonusFacade bonusFacade)
	{
		if (bonusFacade == null || !(bonusFacade instanceof TempBonusFacadeImpl))
		{
			return;
		}
		TempBonusFacadeImpl tempBonus = (TempBonusFacadeImpl) bonusFacade;
		
		Equipment aEq = null;
		if (tempBonus.getTarget() instanceof Equipment)
		{
			aEq = (Equipment) tempBonus.getTarget();
		}
		CDOMObject originObj = tempBonus.getOriginObj();
		TempBonusHelper.removeBonusFromCharacter(theCharacter, aEq, originObj);

		appliedTempBonuses.removeElement(tempBonus);
		refreshStatScores();
		postLevellingUpdates();
	}

	@Override
	public void setTempBonusActive(TempBonusFacade bonusFacade, boolean active)
	{
		if (bonusFacade == null || !(bonusFacade instanceof TempBonusFacadeImpl))
		{
			return;
		}
		TempBonusFacadeImpl tempBonus = (TempBonusFacadeImpl) bonusFacade;

		if (active)
		{
			theCharacter.unsetTempBonusFilter(tempBonus.toString());
		}
		else
		{
			theCharacter.setTempBonusFilter(tempBonus.toString());
		}
		tempBonus.setActive(active);
		appliedTempBonuses.modifyElement(tempBonus);
		refreshStatScores();
	}
	
	@Override
	public ListFacade<TempBonusFacade> getTempBonuses()
	{
		return appliedTempBonuses;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAlignmentRef()
	 */
	@Override
	public ReferenceFacade<PCAlignment> getAlignmentRef()
	{
		return alignment;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setAlignment(pcgen.core.facade.AlignmentFacade)
	 */
	@Override
	public void setAlignment(PCAlignment alignment)
	{
		if (!validateAlignmentChange(alignment))
		{
			return;
		}

		this.alignment.set(alignment);
		refreshLanguageList();

	}

	/**
	 * Validate the new alignment matches those allowed for the character's 
	 * classes. If not offer the user a choice of backing out or making the 
	 * classes into ex-classes.
	 * 
	 * @param newAlign The alignment to be set
	 */
	private boolean validateAlignmentChange(PCAlignment newAlign)
	{
		PCAlignment oldAlign = this.alignment.get();

		if (oldAlign == null || newAlign.equals(oldAlign))
		{
			return true;
		}

		//
		// Get a list of classes that will become unqualified (and have an ex-class)
		//
		StringBuilder unqualified = new StringBuilder(100);
		List<PCClass> classList = charDisplay.getClassList();
		List<PCClass> exclassList = new ArrayList<>();
		PCAlignment savedAlignmnet = charDisplay.getPCAlignment();
		for (PCClass aClass : classList)
		{
			ChannelCompatibility.setCurrentAlignment(theCharacter.getCharID(), newAlign);
			{
				if (!theCharacter.isQualified(aClass))
				{
					if (aClass.containsKey(ObjectKey.EX_CLASS))
					{
						if (unqualified.length() > 0)
						{
							unqualified.append(", "); //$NON-NLS-1$
						}

						unqualified.append(aClass.getKeyName());
						exclassList.add(aClass);
					}
				}
			}
		}

		//
		// Give the user a chance to bail
		//
		if (unqualified.length() > 0)
		{
			if (!delegate.showWarningConfirm(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_sumExClassesWarning") + Constants.LINE_SEPARATOR + unqualified))
			{
				ChannelCompatibility.setCurrentAlignment(theCharacter.getCharID(), savedAlignmnet);
				return false;
			}

		}

		//
		// Convert the class(es)
		//
		for (PCClass aClass : exclassList)
		{
			theCharacter.makeIntoExClass(aClass);
		}

		// Update the facade and UI
		refreshClassLevelModel();

		return true;
	}

	void refreshClassLevelModel()
	{
		List<CharacterLevelFacade> newlevels = new ArrayList<>();
		List<PCClass> newClasses = charDisplay.getClassList();
		Collection<PCLevelInfo> levelInfo = charDisplay.getLevelInfo();

		Map<String, PCClass> classMap = new HashMap<>();
		for (PCClass pcClass : newClasses)
		{
			classMap.put(pcClass.getKeyName(), pcClass);
		}

		for (PCLevelInfo lvlInfo : levelInfo)
		{
			final String classKeyName = lvlInfo.getClassKeyName();
			PCClass currClass = classMap.get(classKeyName);
			if (currClass == null)
			{
				Logging.errorPrint("No PCClass found for '" + classKeyName + "' in character's class list: "
						+ newClasses);
				return;
			}

			CharacterLevelFacadeImpl cl = new CharacterLevelFacadeImpl(currClass, newlevels.size() + 1);
			newlevels.add(cl);
		}

		pcClasses.clear();
		pcClasses.addAll(newClasses);

		pcClassLevels.updateContents(newlevels);
		// Now get the CharacterLevelsFacadeImpl to do a refresh too.
		charLevelsFacade.classListRefreshRequired();
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getDataSet()
	 */
	@Override
	public DataSetFacade getDataSet()
	{
		return dataSet;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSets()
	 */
	@Override
	public ListFacade<EquipmentSetFacade> getEquipmentSets()
	{
		return equipmentSets;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getGenderRef()
	 */
	@Override
	public ReferenceFacade<GenderFacade> getGenderRef()
	{
		return gender;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setGender(pcgen.cdom.enumeration.Gender)
	 */
	@Override
	public void setGender(GenderFacade gender)
	{
		theCharacter.setGender((Gender) gender);
		Gender newGender = charDisplay.getGenderObject();
		this.selectedGender = newGender.toString();
		this.gender.set(newGender);
		refreshLanguageList();
	}

	@Override
	public void setGender(String gender)
	{
		this.selectedGender = gender;
		if (charDisplay.getRace() != null)
		{
			for (GenderFacade raceGender : availGenders)
			{
				if (raceGender.toString().equals(gender))
				{
					setGender(raceGender);
				}
			}
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getModTotal(pcgen.core.facade.StatFacade)
	 */
	@Override
	public int getModTotal(StatFacade stat)
	{
		if (stat instanceof PCStat && !charDisplay.isNonAbility((PCStat) stat))
		{
			return theCharacter.getStatModFor((PCStat) stat);
		}
		return 0;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getScoreTotalRef(pcgen.core.facade.StatFacade)
	 */
	@Override
	public ReferenceFacade<Number> getScoreBaseRef(StatFacade stat)
	{
		WriteableReferenceFacade<Number> score = statScoreMap.get(stat);
		if (score == null)
		{
			score = getStatReferenceFacade(stat);
			statScoreMap.put(stat, score);
		}
		return score;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getScoreBase(pcgen.core.facade.StatFacade)
	 */
	@Override
	public int getScoreBase(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		return theCharacter.getBaseStatFor((PCStat) stat);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getScoreTotalString(pcgen.core.facade.StatFacade)
	 */
	@Override
	public String getScoreTotalString(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return "";
		}
		if (charDisplay.isNonAbility((PCStat) stat))
		{
			return "*"; //$NON-NLS-1$
		}

		return SettingsHandler.getGame().getStatDisplayText(theCharacter.getTotalStatFor((PCStat) stat));
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getScoreRaceBonus(pcgen.core.facade.StatFacade)
	 */
	@Override
	public int getScoreRaceBonus(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		PCStat activeStat = (PCStat) stat;
		if (charDisplay.isNonAbility(activeStat))
		{
			return 0;
		}

		//return Integer.valueOf(currentStatAnalysis.getTotalStatFor(aStat) - currentStatAnalysis.getBaseStatFor(aStat));
		int rBonus = (int) theCharacter.getRaceBonusTo("STAT", activeStat.getKeyName()); //$NON-NLS-1$
		rBonus += (int) theCharacter.getBonusDueToType("STAT", activeStat.getKeyName(), "RACIAL");

		return rBonus;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getScoreOtherBonus(pcgen.core.facade.StatFacade)
	 */
	@Override
	public int getScoreOtherBonus(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		PCStat activeStat = (PCStat) stat;
		if (charDisplay.isNonAbility(activeStat))
		{
			return 0;
		}

		//return Integer.valueOf(currentStatAnalysis.getTotalStatFor(aStat) - currentStatAnalysis.getBaseStatFor(aStat));
		int iRace = (int) theCharacter.getRaceBonusTo("STAT", activeStat.getKeyName()); //$NON-NLS-1$
		iRace += (int) theCharacter.getBonusDueToType("STAT", activeStat.getKeyName(), "RACIAL");

		return theCharacter.getTotalStatFor(activeStat)
				- theCharacter.getBaseStatFor(activeStat) - iRace;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setScoreBase(pcgen.core.facade.StatFacade, int)
	 */
	@Override
	public void setScoreBase(StatFacade stat, int score)
	{
		WriteableReferenceFacade<Number> facade = statScoreMap.get(stat);
		if (facade == null)
		{
			facade = getStatReferenceFacade(stat);
			facade.set(score);
			statScoreMap.put(stat, facade);
		}

		PCStat pcStat = null;
		final int pcPlayerLevels = charDisplay.totalNonMonsterLevels();
		Collection<PCStat> pcStatList = charDisplay.getStatSet();
		for (PCStat aStat : pcStatList)
		{
			if (stat.getKeyName().equals(aStat.getKeyName()))
			{
				pcStat = aStat;
				break;
			}
		}
		if (pcStat == null)
		{
			Logging.errorPrint("Unexpected stat '" + stat + "' found - ignoring.");
			return;
		}

		// Checking for bounds, locked stats and pool points
		String errorMsg = validateNewStatBaseScore(score, pcStat, pcPlayerLevels);
		if (StringUtils.isNotBlank(errorMsg))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, errorMsg);
			return;
		}

		final int baseScore = charDisplay.getStat(pcStat);
		// Deal with a point pool based game mode where you buy skills and feats as well as stats
		if (Globals.getGameModeHasPointPool())
		{
			if (pcPlayerLevels > 0)
			{
				int poolMod = getPurchaseCostForStat(theCharacter, score)
						- getPurchaseCostForStat(theCharacter, baseScore);
				//
				// Adding to stat
				//
				if (poolMod > 0)
				{
					if (poolMod > theCharacter.getSkillPoints())
					{
						delegate.showErrorMessage(Constants.APPLICATION_NAME,
								LanguageBundle.getFormattedString("in_sumStatPoolEmpty", Globals //$NON-NLS-1$
										.getGameModePointPoolName()));
						return;
					}
				}
				else if (poolMod < 0)
				{
					if (theCharacter.getStatIncrease(pcStat, true) < Math.abs(score - baseScore))
					{
						delegate.showErrorMessage(Constants.APPLICATION_NAME,
								LanguageBundle.getString("in_sumStatStartedHigher")); //$NON-NLS-1$
						return;
					}
				}

				theCharacter.adjustAbilities(AbilityCategory.FEAT,
					new BigDecimal(-poolMod));
				showPointPool();
			}
		}

		theCharacter.setStat(pcStat, score);
		facade.set(score);
		theCharacter.saveStatIncrease(pcStat, score - baseScore, false);
		theCharacter.calcActiveBonuses();
		hpRef.set(theCharacter.hitPoints());
		refreshLanguageList();

		updateScorePurchasePool(true);
		if (charLevelsFacade != null)
		{
			charLevelsFacade.fireSkillBonusEvent(this, 0, true);
		}
	}

	/**
	 * Assess if the new score is valid for the stat.
	 * 
	 * @param score The new score being checked.
	 * @param pcStat The stats being checked
	 * @param pcPlayerLevels The number of non moster levels the character currently has.
	 * @return An error message if the score is not valid.
	 */
	private String validateNewStatBaseScore(int score, PCStat pcStat, final int pcPlayerLevels)
	{
		if (charDisplay.isNonAbility(pcStat))
		{
			return LanguageBundle.getString("in_sumCannotModifyANonAbility"); //$NON-NLS-1$
		}
		else if (score < pcStat.getSafe(IntegerKey.MIN_VALUE))
		{
			return LanguageBundle.getFormattedString("in_sumCannotLowerStatBelow", SettingsHandler.getGame() //$NON-NLS-1$
					.getStatDisplayText(pcStat.getSafe(IntegerKey.MIN_VALUE)));
		}
		else if (score > pcStat.getSafe(IntegerKey.MAX_VALUE))
		{
			return LanguageBundle.getFormattedString("in_sumCannotRaiseStatAbove", SettingsHandler.getGame() //$NON-NLS-1$
					.getStatDisplayText(pcStat.getSafe(IntegerKey.MAX_VALUE)));
		}
		else if ((pcPlayerLevels < 2)
			&& SettingsHandler.getGame().isPurchaseStatMode())
		{
			final int maxPurchaseScore = SettingsHandler.getGame().getPurchaseScoreMax(theCharacter);

			if (score > maxPurchaseScore)
			{
				return LanguageBundle.getFormattedString("in_sumCannotRaiseStatAbovePurchase", SettingsHandler //$NON-NLS-1$
						.getGame().getStatDisplayText(maxPurchaseScore));
			}

			final int minPurchaseScore = SettingsHandler.getGame().getPurchaseScoreMin(theCharacter);

			if (score < minPurchaseScore)
			{
				return LanguageBundle.getFormattedString("in_sumCannotLowerStatBelowPurchase", SettingsHandler //$NON-NLS-1$
						.getGame().getStatDisplayText(minPurchaseScore));
			}
		}

		return null;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#rollStats()
	 */
	@Override
	public void rollStats()
	{
		GameMode game = (GameMode) dataSet.getGameMode();
		int rollMethod = game.getRollMethod();
		if (rollMethod == Constants.CHARACTER_STAT_METHOD_ROLLED && game.getCurrentRollingMethod() == null)
		{
			return;
		}
		if (rollMethod == Constants.CHARACTER_STAT_METHOD_USER)
		{
			// If a user asks to roll in user mode, set it to the current all same value.
			rollMethod = Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME;
		}
		theCharacter.rollStats(rollMethod);
		//XXX This is here to stop the stat mod from being stale. Can be removed once we merge with CDOM
		theCharacter.calcActiveBonuses();
		refreshStatScores();
		updateScorePurchasePool(true);
	}

	private void refreshStatScores()
	{
//		for (StatFacade stat : statScoreMap.keySet())
//		{
//			WriteableReferenceFacade<Integer> score = statScoreMap.get(stat);
//			if (stat instanceof PCStat)
//			{
//				score.set(theCharacter.getTotalStatFor((PCStat) stat));
//			}
//		}
		if (charLevelsFacade != null)
		{
			charLevelsFacade.fireSkillBonusEvent(this, 0, true);
			charLevelsFacade.updateSkillsTodo();
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#isStatRollEnabled()
	 */
	@Override
	public boolean isStatRollEnabled()
	{
		return (charLevelsFacade.getSize() == 0);
	}

	/**
	 * Update the  
	 */
	private void showPointPool()
	{
		if (poolPointText == null)
		{
			return;
		}

		int poolPointsTotal = 0;

		for (PCLevelInfo pcl : charDisplay.getLevelInfo())
		{
			poolPointsTotal += pcl.getSkillPointsGained(theCharacter);
		}

		int poolPointsUsed = poolPointsTotal - theCharacter.getSkillPoints();

		poolPointText.set(Integer.toString(poolPointsUsed) + " / " + Integer.toString(poolPointsTotal)); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getUndoManager()
	 */
	@Override
	public UndoManager getUndoManager()
	{
		return undoManager;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getRaceRef()
	 */
	@Override
	public ReferenceFacade<RaceFacade> getRaceRef()
	{
		return race;
	}

	/**
	 * @return A reference to a list containing the character's race.
	 */
	@Override
	public ListFacade<RaceFacade> getRaceAsList()
	{
		return raceList;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setRace(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public void setRace(RaceFacade race)
	{
		// TODO: We don't have a HP dialog implemented yet, so don't try to show it
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		//SettingsHandler.setShowStatDialogAtLevelUp(false);
		int oldLevel = charLevelsFacade.getSize();

		if (race == null)
		{
			race = RaceUtilities.getUnselectedRace();
		}
		this.race.set(race);
		if (race instanceof Race && race != charDisplay.getRace())
		{
			Logging.log(Logging.INFO, charDisplay.getName()
				+ ": Setting race to " + race); //$NON-NLS-1$
			theCharacter.setRace((Race) race);
			raceList.clearContents();
			if (!race.isUnselected())
			{
				raceList.addElement(race);
			}
		}
		refreshLanguageList();
		if (selectedGender != null)
		{
			setGender(selectedGender);
		}
		refreshRaceRelatedFields();

		if (oldLevel != charLevelsFacade.getSize())
		{
			delegate.showLevelUpInfo(this, oldLevel);
		}
	}

	private void refreshRaceRelatedFields()
	{
		race.set(charDisplay.getRace());

		if (charDisplay.getRace() != null)
		{
			for (HandedFacade handsFacade : availHands)
			{
				if (handsFacade.toString().equals(charDisplay.getHanded()))
				{
					handedness.set(handsFacade);
					break;
				}
			}
			for (GenderFacade pcGender : availGenders)
			{
				if (pcGender.equals(charDisplay.getGenderObject()))
				{
					gender.set(pcGender);
					break;
				}
			}
		}
		refreshClassLevelModel();
		refreshStatScores();
		age.set(charDisplay.getAge());
		updateAgeCategoryForAge();
		refreshHeightWeight();
		characterAbilities.rebuildAbilityLists();
		currentXP.set(charDisplay.getXP());
		xpForNextlevel.set(charDisplay.minXPForNextECL());
		xpTableName.set(charDisplay.getXPTableName());
		hpRef.set(theCharacter.hitPoints());
		alignment.set(charDisplay.getPCAlignment());
		refreshAvailableTempBonuses();
		companionSupportFacade.refreshCompanionData();

		updateLevelTodo();
		buildAvailableDomainsList();
		spellSupportFacade.refreshAvailableKnownSpells();
		updateScorePurchasePool(false);
		refreshEquipment();

		if (charDisplay.getRace() == null || charDisplay.getRace().isUnselected())
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Race", "in_irTodoRace", 100));
		}
		else
		{
			todoManager.removeTodo("in_irTodoRace");
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getTabNameRef()
	 */
	@Override
	public ReferenceFacade<String> getTabNameRef()
	{
		return tabName;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setTabName(java.lang.String)
	 */
	@Override
	public void setTabName(String name)
	{
		tabName.set(name);
		theCharacter.setPCAttribute(PCAttribute.TABNAME, name);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getNameRef()
	 */
	@Override
	public ReferenceFacade<String> getNameRef()
	{
		return name;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.name.set(name);
		theCharacter.setName(name);
		if (isNewCharName(charDisplay.getName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Name", "in_sumTodoName", 1));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoName");
		}
	}

	/**
	 * Check  whether the field should be output. 
	 * @param field The BiographyField to check export rules for.
	 * @return true if the field should be output, false if it may not be.
	 */
	@Override
	public boolean getExportBioField(BiographyField field)
	{
		return !charDisplay.getSuppressBioField(field);
	}

	/**
	 * Set whether the field should be output. 
	 * @param field The BiographyField to set export rules for.
	 * @param export Should the field be shown in output.
	 */
	@Override
	public void setExportBioField(BiographyField field, boolean export)
	{
		theCharacter.setSuppressBioField(field, !export);
	}

	@Override
	public ReferenceFacade<String> getSkinColorRef()
	{
		return skinColor;
	}

	@Override
	public void setSkinColor(String color)
	{
		skinColor.set(color);
		theCharacter.setPCAttribute(PCAttribute.SKINCOLOR, color);
	}

	@Override
	public ReferenceFacade<String> getHairColorRef()
	{
		return hairColor;
	}

	@Override
	public void setHairColor(String color)
	{
		hairColor.set(color);
		theCharacter.setPCAttribute(PCAttribute.HAIRCOLOR, color);
	}

	@Override
	public ReferenceFacade<String> getEyeColorRef()
	{
		return eyeColor;
	}

	@Override
	public void setEyeColor(String color)
	{
		eyeColor.set(color);
		theCharacter.setEyeColor(color);
	}

	@Override
	public ReferenceFacade<Integer> getHeightRef()
	{
		return heightRef;
	}

	@Override
	public void setHeight(int height)
	{
		int heightInInches =
				Globals.getGameModeUnitSet().convertHeightFromUnitSet(height);
		heightRef.set(height);
		theCharacter.setHeight(heightInInches);
	}

	@Override
	public ReferenceFacade<Integer> getWeightRef()
	{
		return weightRef;
	}

	@Override
	public void setWeight(int weight)
	{
		int weightInPounds =
				(int) Globals.getGameModeUnitSet().convertWeightFromUnitSet(
					weight);
		weightRef.set(weight);
		theCharacter.setPCAttribute(NumericPCAttribute.WEIGHT,weightInPounds);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getDeityRef()
	 */
	@Override
	public ReferenceFacade<DeityFacade> getDeityRef()
	{
		return deity;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setDeity(pcgen.core.facade.DeityFacade)
	 */
	@Override
	public void setDeity(DeityFacade deity)
	{
		this.deity.set(deity);
		if (deity instanceof Deity)
		{
			theCharacter.setDeity((Deity) deity);
		}
		refreshLanguageList();
		buildAvailableDomainsList();
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#addDomain(pcgen.core.facade.DomainFacade)
	 */
	@Override
	public void addDomain(DomainFacade domainFacade)
	{
		if (!(domainFacade instanceof DomainFacadeImpl))
		{
			return;
		}
		DomainFacadeImpl domainFI = (DomainFacadeImpl) domainFacade;
		Domain domain = domainFI.getRawObject();
		if (charDisplay.hasDomain(domain))
		{
			return;
		}

		if (!isQualifiedFor(domainFacade))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_qualifyMess", domain.getDisplayName()));

			return;
		}

		// Check selected domains vs Max number allowed
		if (charDisplay.getDomainCount() >= theCharacter.getMaxCharacterDomains())
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_errorNoMoreDomains"));

			return;
		}

		if (!theCharacter.hasDefaultDomainSource())
		{
			// No source for the domain yet? Default to the last added class level
			int level = charDisplay.getLevelInfoSize();
			PCLevelInfo highestLevelInfo = charDisplay.getLevelInfo(level - 1);
			PCClass cls = theCharacter.getClassKeyed(highestLevelInfo.getClassKeyName()); 
			theCharacter.setDefaultDomainSource(new ClassSource(cls, highestLevelInfo.getClassLevel()));
		}

		if (theCharacter.addDomain(domain))
		{
			domains.addElement(domainFI);
			DomainApplication.applyDomain(theCharacter, domain);

			theCharacter.calcActiveBonuses();

			remainingDomains.set(theCharacter.getMaxCharacterDomains() - charDisplay.getDomainCount());
			updateDomainTodo();
			spellSupportFacade.refreshAvailableKnownSpells();
			companionSupportFacade.refreshCompanionData();
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getDomains()
	 */
	@Override
	public ListFacade<DomainFacade> getDomains()
	{
		return domains;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#removeDomain(pcgen.core.facade.DomainFacade)
	 */
	@Override
	public void removeDomain(DomainFacade domain)
	{
		if (domains.removeElement(domain))
		{
			Domain dom = ((DomainFacadeImpl) domain).getRawObject();
			DomainApplication.removeDomain(theCharacter, dom);
			theCharacter.removeDomain(((DomainFacadeImpl) domain).getRawObject());
			remainingDomains.set(theCharacter.getMaxCharacterDomains() - charDisplay.getDomainCount());
			updateDomainTodo();
			spellSupportFacade.refreshAvailableKnownSpells();
		}
	}

	/**
	 * Update the todo list to reflect the change in number of domains.
	 */
	private void updateDomainTodo()
	{
		if (remainingDomains.get() > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.DOMAINS, "Domains", "in_domTodoDomainsLeft", 120));
			todoManager.removeTodo("in_domTodoTooManyDomains");
		}
		else if (remainingDomains.get() < 0)
		{
			todoManager
					.addTodo(new TodoFacadeImpl(Tab.DOMAINS, "Domains", "in_domTodoTooManyDomains", 120));
			todoManager.removeTodo("in_domTodoDomainsLeft");
		}
		else
		{
			todoManager.removeTodo("in_domTodoDomainsLeft");
			todoManager.removeTodo("in_domTodoTooManyDomains");
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getMaxDomains()
	 */
	@Override
	public ReferenceFacade<Integer> getMaxDomains()
	{
		return maxDomains;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getRemainingDomainSelectionsRef()
	 */
	@Override
	public ReferenceFacade<Integer> getRemainingDomainSelectionsRef()
	{
		return remainingDomains;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAvailableDomains()
	 */
	@Override
	public ListFacade<DomainFacade> getAvailableDomains()
	{
		return availDomains;
	}

	/**
	 * This method returns all available domains, without filtering.
	 */
	private void buildAvailableDomainsList()
	{
		List<DomainFacadeImpl> availDomainList = new ArrayList<>();
		List<DomainFacadeImpl> selDomainList = new ArrayList<>();
		Deity pcDeity = charDisplay.getDeity();

		if (pcDeity != null)
		{
			for (CDOMReference<Domain> domainRef : pcDeity.getSafeListMods(Deity.DOMAINLIST))
			{
				Collection<AssociatedPrereqObject> assoc = pcDeity.getListAssociations(Deity.DOMAINLIST, domainRef);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : domainRef.getContainedObjects())
					{
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new DomainFacadeImpl(d, apo.getPrerequisiteList()));
						}
					}
				}
			}
		}

		// Loop through the available prestige domains
		for (PCClass aClass : charDisplay.getClassList())
		{
			/*
			 * Need to do for the class, for compatibility, since level 0 is
			 * loaded into the class itself
			 */
			processDomainList(aClass, availDomainList);
			processAddDomains(aClass, availDomainList);
			for (int lvl = 0; lvl <= charDisplay.getLevel(aClass); lvl++)
			{
				PCClassLevel cl = charDisplay.getActiveClassLevel(aClass, lvl);
				processAddDomains(cl, availDomainList);
				processDomainList(cl, availDomainList);
			}
		}

		// Loop through the character's selected domains
		for (Domain d : charDisplay.getDomainSet())
		{
			DomainFacadeImpl domainFI = new DomainFacadeImpl(d);
			boolean found = false;
			for (DomainFacadeImpl row : availDomainList)
			{
				if (d.equals(row.getRawObject()))
				{
					domainFI = row;
					found = true;
					break;
				}
			}

			if (!found)
			{
				availDomainList.add(domainFI);
			}

			if (!isDomainInList(selDomainList, d))
			{
				selDomainList.add(domainFI);
			}
		}

		availDomains.updateContents(availDomainList);
		domains.updateContents(selDomainList);
		maxDomains.set(theCharacter.getMaxCharacterDomains());
		remainingDomains.set(theCharacter.getMaxCharacterDomains() - charDisplay.getDomainCount());
		updateDomainTodo();
	}

	/**
	 * Check if a domain is a list of domains, irrespective of prerequisites.
	 *  
	 * @param qualDomainList The list of domains with their prerequisites.
	 * @param qualDomain The domain to search for.
	 * @return tue if the domain is in the list 
	 */
	private boolean isDomainInList(List<DomainFacadeImpl> qualDomainList, Domain domain)
	{
		for (DomainFacadeImpl row : qualDomainList)
		{
			if (domain.equals(row.getRawObject()))
			{
				return true;
			}
		}
		return false;
	}

	private void processAddDomains(CDOMObject cdo, final List<DomainFacadeImpl> availDomainList)
	{
		Collection<CDOMReference<Domain>> domainRefs = cdo.getListMods(PCClass.ALLOWED_DOMAINS);
		if (domainRefs != null)
		{
			for (CDOMReference<Domain> ref : domainRefs)
			{
				Collection<AssociatedPrereqObject> assoc = cdo.getListAssociations(PCClass.ALLOWED_DOMAINS, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : ref.getContainedObjects())
					{
						/*
						 * TODO This gate produces a rather interesting, and
						 * potentially wrong situation. What if two ADDDOMAINS
						 * exist with different PRE? Doesn't this fail?
						 */
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new DomainFacadeImpl(d, apo.getPrerequisiteList()));
						}
					}
				}
			}
		}
	}

	private void processDomainList(CDOMObject obj, final List<DomainFacadeImpl> availDomainList)
	{
		for (QualifiedObject<CDOMSingleRef<Domain>> qo : obj.getSafeListFor(ListKey.DOMAIN))
		{
			CDOMSingleRef<Domain> ref = qo.getRawObject();
			Domain domain = ref.get();
			if (!isDomainInList(availDomainList, domain))
			{
				availDomainList.add(new DomainFacadeImpl(domain, qo.getPrerequisiteList()));
			}
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSetRef()
	 */
	@Override
	public ReferenceFacade<EquipmentSetFacade> getEquipmentSetRef()
	{
		return equipSet;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setEquipmentSet(pcgen.core.facade.EquipmentSetFacade)
	 */
	@Override
	public void setEquipmentSet(EquipmentSetFacade set)
	{
		EquipmentSetFacade oldSet = equipSet.get();
		if (oldSet != null)
		{
			oldSet.getEquippedItems().removeListListener(this);
			oldSet.getEquippedItems().removeEquipmentListListener(this);
		}
		if (set instanceof EquipmentSetFacadeImpl)
		{
			((EquipmentSetFacadeImpl) set).activateEquipSet();
		}
		equipSet.set(set);
		set.getEquippedItems().addListListener(this);
		set.getEquippedItems().addEquipmentListListener(this);
		refreshTotalWeight();
	}

	/**
	 * Regenerate the character's list of languages.
	 */
	void refreshLanguageList()
	{
		long startTime = new Date().getTime();
		List<Language> sortedLanguages = new ArrayList<>(charDisplay.getLanguageSet());
		Collections.sort(sortedLanguages);
		languages.updateContents(sortedLanguages);
		autoLanguagesCache = null;

		boolean allowBonusLangAfterFirst = Globals.checkRule(RuleConstants.INTBONUSLANG);
		boolean atFirstLvl = theCharacter.getTotalLevels() <= 1;

		int bonusLangMax = theCharacter.getBonusLanguageCount();
		
		currBonusLangs = new ArrayList<>();
		CNAbility a = theCharacter.getBonusLanguageAbility();
		List<String> currBonusLangNameList = theCharacter.getAssociationList(a);
		for (LanguageFacade langFacade : languages)
		{
			Language lang = (Language) langFacade;
			if (currBonusLangNameList.contains(lang.getKeyName()))
			{
				currBonusLangs.add(lang);
			}
		}
		int bonusLangRemain = bonusLangMax - currBonusLangs.size();
		if (!allowBonusLangAfterFirst && !atFirstLvl)
		{
			bonusLangRemain = 0;
		}
		numBonusLang.set(bonusLangRemain);
		if (bonusLangRemain > 0)
		{
			if (allowBonusLangAfterFirst)
			{
				todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY,
					"Languages", "in_sumTodoBonusLanguage", 110));
				todoManager.removeTodo("in_sumTodoBonusLanguageFirstOnly");
			}
			else
			{
				todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY,
					"Languages", "in_sumTodoBonusLanguageFirstOnly", 110));
				todoManager.removeTodo("in_sumTodoBonusLanguage");
			}
		}
		else
		{
			todoManager.removeTodo("in_sumTodoBonusLanguage");
			todoManager.removeTodo("in_sumTodoBonusLanguageFirstOnly");
		}

		int numSkillLangSelected = 0;
		int skillLangMax = 0;
		//TODO: Need to cope with multiple skill languages
		SkillFacade speakLangSkill = dataSet.getSpeakLanguageSkill();
		if (speakLangSkill != null)
		{
			Skill skill = (Skill) speakLangSkill;
			List<String> langList = theCharacter.getAssociationList(skill);
			numSkillLangSelected = langList.size();
			skillLangMax = SkillRankControl.getTotalRank(theCharacter, skill).intValue();
		}

		int skillLangRemain = skillLangMax - numSkillLangSelected;
		numSkillLang.set(skillLangRemain);
		if (skillLangRemain > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY,
				"Languages", "in_sumTodoSkillLanguage", 112));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoSkillLanguage");
		}
		if (skillLangRemain < 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY,
				"Languages", "in_sumTodoSkillLanguageTooMany", 112));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoSkillLanguageTooMany");
		}
		
		long endTime = new Date().getTime();
		Logging.log(Logging.DEBUG, "refreshLanguageList took " + (endTime - startTime) + " ms.");
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getLanguages()
	 */
	@Override
	public ListFacade<LanguageFacade> getLanguages()
	{
		return languages;
	}

	@Override
	public ListFacade<LanguageChooserFacade> getLanguageChoosers()
	{
		CNAbility cna = theCharacter.getBonusLanguageAbility();
		DefaultListFacade<LanguageChooserFacade> chooserList = new DefaultListFacade<>();
		chooserList.addElement(new LanguageChooserFacadeImpl(this,
			LanguageBundle.getString("in_sumLangBonus"), cna)); //$NON-NLS-1$

		SkillFacade speakLangSkill = dataSet.getSpeakLanguageSkill();
		if (speakLangSkill != null)
		{
			chooserList.addElement(new LanguageChooserFacadeImpl(this,
				LanguageBundle.getString("in_sumLangSkill"), //$NON-NLS-1$
				(Skill) speakLangSkill));
		}
		return chooserList;
	}

	@Override
	public void removeLanguage(LanguageFacade lang)
	{
		ChooseDriver owner = getLaguageOwner(lang);
		if (owner == null)
		{
			return;
		}

		List<Language> availLangs = new ArrayList<>();
		List<Language> selLangs = new ArrayList<>();
		ChoiceManagerList<Language> choiceManager = ChooserUtilities.getChoiceManager(owner, theCharacter);
		choiceManager.getChoices(theCharacter, availLangs, selLangs);
		selLangs.remove(lang);
		choiceManager.applyChoices(theCharacter, selLangs);
	}

	/**
	 * Identify the object that the language is associated with. i.e. The rules 
	 * object that granted the ability to use the language. 
	 * @param lang The language to be found.
	 * @return The granting rules object, or null if none or automatic.
	 */
	private ChooseDriver getLaguageOwner(LanguageFacade lang)
	{
		if (currBonusLangs.contains(lang))
		{
			return theCharacter.getBonusLanguageAbility();
		}
		else if (languages.containsElement(lang) && !isAutomatic(lang))
		{
			return (Skill) dataSet.getSpeakLanguageSkill();
		}
		return null;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getFileRef()
	 */
	@Override
	public ReferenceFacade<File> getFileRef()
	{
		return file;
	}

	@Override
	public void setFile(File file)
	{
		this.file.set(file);
		try
		{
			theCharacter.setFileName(file.getCanonicalPath());
		}
		catch (IOException e)
		{
			Logging.errorPrint("CharacterFacadeImpl.setFile failed for " + file, e);
			theCharacter.setFileName(file.getPath());
		}
	}

	
	/**
	 * Retrieve a copy of the current character suitable for export. This 
	 * attempts to minimise the expensive cloning function, by returning the 
	 * previously cloned character if the base character has not changed in 
	 * the meantime. 
	 * @return A copy of the current character.
	 */
	private synchronized PlayerCharacter getExportCharacter()
	{
		PlayerCharacter exportPc = lastExportChar;
		if (exportPc == null || theCharacter.getSerial() != lastExportCharSerial)
		{
			// Calling preparePCForOutput will mark export character as modified, so compare original character serial when checking for real changes
			// Get serial at beginning so we can detect if a change occurs during clone and preparePCForOutput
			lastExportCharSerial = theCharacter.getSerial();
			exportPc = theCharacter.clone();

			// Get the PC all up to date, (equipment and active bonuses etc)
			exportPc.preparePCForOutput();
			
			lastExportChar = exportPc;
			
			// It is possible another thread changed PC during export; log for now, the next export will rebuild
			int countSerialChanges = theCharacter.getSerial() - lastExportCharSerial;
			if (countSerialChanges > 0)
			{
				Logging.log(Logging.DEBUG,
					"Player character " + exportPc.getName() + " changed "
						+ countSerialChanges + " times during export.");
			}
		}
		return exportPc;
	}
	
	/**
	 * @see pcgen.core.facade.CharacterFacade#export(pcgen.io.ExportHandler, java.io.BufferedWriter)
	 */
	@Override
	public void export(ExportHandler theHandler, BufferedWriter buf) throws ExportException
	{
		final int maxRetries = 3;
		for (int i = 0; i < maxRetries; i++)
		{
			try
			{
				Logging.log(Logging.DEBUG, "Starting export at serial " + theCharacter.getSerial() + " to " + theHandler.getTemplateFile());
				PlayerCharacter exportPc =  getExportCharacter();
				//PlayerCharacter exportPc =  theCharacter;
				theHandler.write(exportPc, buf);
				Logging.log(Logging.DEBUG, "Finished export at serial " + theCharacter.getSerial() + " to " + theHandler.getTemplateFile());
				return;
			}
			catch (ConcurrentModificationException e)
			{
				Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
				for (Entry<Thread, StackTraceElement[]> threadEntry : allStackTraces.entrySet())
				{
					if (threadEntry.getValue().length > 1)
					{
						StringBuilder sb = new StringBuilder("Thread: " + threadEntry.getKey() + "\n");
						for (StackTraceElement elem : threadEntry.getValue())
						{
							sb.append("  ");
							sb.append(elem.toString());
							sb.append("\n");
						}
						Logging.log(Logging.INFO, sb.toString());
					}
				}
				Logging.log(Logging.WARNING, "Retrying export after ConcurrentModificationException", e);
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e1)
				{
					Logging.errorPrint("Interrupted sleep - probably closing.");
					return;

				}
			}
		}
		Logging.errorPrint("Unable to export using "
			+ theHandler.getTemplateFile()
			+ " due to concurrent modifications.");
	}

	@Override
	public void setDefaultOutputSheet(boolean pdf, File outputSheet)
	{
		UIPropertyContext context = UIPropertyContext.getInstance();
		String outputSheetPath = outputSheet.getAbsolutePath();
		if (pdf)
		{
			context.setProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET, outputSheetPath);
		}
		else
		{
			context.setProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET, outputSheetPath);
		}
		if (context.getBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC))
		{
			if (pdf)
			{
				theCharacter.setSelectedCharacterPDFOutputSheet(outputSheetPath);
			}
			else
			{
				theCharacter.setSelectedCharacterHTMLOutputSheet(outputSheetPath);
			}
		}
	}

	@Override
	public String getDefaultOutputSheet(boolean pdf)
	{
		UIPropertyContext context = UIPropertyContext.getInstance();
		if (context.getBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC))
		{
			String sheet;			
			if (pdf)
			{
				sheet = theCharacter.getSelectedCharacterPDFOutputSheet();
			}
			else
			{
				sheet = theCharacter.getSelectedCharacterHTMLOutputSheet();
			}
			if (StringUtils.isNotEmpty(sheet))
			{
				return sheet;
			}
		}

		if (pdf)
		{
			return context.getProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET);
		}
		return context.getProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getHandedRef()
	 */
	@Override
	public ReferenceFacade<HandedFacade> getHandedRef()
	{
		return handedness;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setHanded(java.lang.String)
	 */
	@Override
	public void setHanded(HandedFacade handedness)
	{
		this.handedness.set(handedness);
		theCharacter.setHanded((Handed) handedness);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getPlayersNameRef()
	 */
	@Override
	public ReferenceFacade<String> getPlayersNameRef()
	{
		return playersName;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setPlayersName(java.lang.String)
	 */
	@Override
	public void setPlayersName(String name)
	{
		playersName.set(name);
		theCharacter.setPCAttribute(PCAttribute.PLAYERSNAME, name);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.ClassFacade)
	 */
	@Override
	public boolean isQualifiedFor(ClassFacade c)
	{
		if (c instanceof PCClass)
		{
			return theCharacter.isQualified((PCClass) c);
		}
		return false;
	}

	@Override
	public UIDelegate getUIDelegate()
	{
		return delegate;
	}

	/**
	 * Save the character to disc using its filename. Note this method is not 
	 * part of the CharacterFacade and should only be used by the 
	 * ChracterManager class.
	 * 
	 * @throws NullPointerException 
	 * @throws IOException If the write fails
	 */
	public void save() throws NullPointerException, IOException
	{
		GameMode mode = (GameMode) dataSet.getGameMode();
		List<CampaignFacade> campaigns = ListFacades.wrap(dataSet.getCampaigns());
		(new PCGIOHandler()).write(theCharacter, mode, campaigns, file.get());
		theCharacter.setDirty(false);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#isAutomatic(pcgen.core.facade.LanguageFacade)
	 */
	@Override
	public boolean isAutomatic(LanguageFacade language)
	{
		if (autoLanguagesCache == null)
		{
			autoLanguagesCache = charDisplay.getAutoLanguages();
		}
		return autoLanguagesCache.contains(language);
	}

	public boolean isRemovable(LanguageFacade language)
	{
		if (isAutomatic(language))
		{
			return false;
		}
		if (currBonusLangs.contains(language))
		{
			boolean allowBonusLangAfterFirst = Globals.checkRule(RuleConstants.INTBONUSLANG);
			boolean atFirstLvl = theCharacter.getTotalLevels() <= 1;
			return allowBonusLangAfterFirst || atFirstLvl;
		}
		
		return true;
	}
	
	/**
	 * @see pcgen.core.facade.CharacterFacade#getCharacterLevelsFacade()
	 */
	@Override
	public CharacterLevelsFacade getCharacterLevelsFacade()
	{
		return charLevelsFacade;
	}

	@Override
	public DescriptionFacade getDescriptionFacade()
	{
		return descriptionFacade;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setXP(int)
	 */
	@Override
	public void setXP(final int xp)
	{
		if (xp == currentXP.get())
		{
			// We've already processed this change, most likely via the adjustXP method
			return;
		}
		theCharacter.setXP(xp);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getXPRef()
	 */
	@Override
	public ReferenceFacade<Integer> getXPRef()
	{
		return currentXP;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#adjustXP(int)
	 */
	@Override
	public void adjustXP(final int xp)
	{
		int currVal = currentXP.get();
		int newVal = currVal + xp;
		theCharacter.setXP(newVal);
		checkForNewLevel();
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getXPForNextLevelRef()
	 */
	@Override
	public ReferenceFacade<Integer> getXPForNextLevelRef()
	{
		return xpForNextlevel;
	}

	@Override
	public ReferenceFacade<String> getXPTableNameRef()
	{
		return xpTableName;
	}

	@Override
	public void setXPTable(String newTable)
	{

		xpTableName.set(newTable);
		theCharacter.setXPTable(newTable);
		checkForNewLevel();
	}

	private void checkForNewLevel()
	{
		currentXP.set(charDisplay.getXP());
		xpForNextlevel.set(charDisplay.minXPForNextECL());

		if (charDisplay.getXP() >= charDisplay.minXPForNextECL())
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, SettingsHandler.getGame().getLevelUpMessage());
		}
		updateLevelTodo();
	}

	@Override
	public ReferenceFacade<String> getCharacterTypeRef()
	{
		return characterType;
	}

	@Override
	public void setCharacterType(String newType)
	{

		characterType.set(newType);
		theCharacter.setCharacterType(newType);
		theCharacter.calcActiveBonuses();
		
		// This can affect traits mainly.
		characterAbilities.rebuildAbilityLists();
	}

	@Override
	public ReferenceFacade<String> getPreviewSheetRef()
	{
		return previewSheet;
	}

	@Override
	public void setPreviewSheet(String newSheet)
	{
		previewSheet.set(newSheet);
		theCharacter.setPreviewSheet(newSheet);
	}

	@Override
	public ReferenceFacade<SkillFilter> getSkillFilterRef()
	{
		return skillFilter;
	}

	@Override
	public void setSkillFilter(SkillFilter newFilter)
	{
		skillFilter.set(newFilter);
		theCharacter.setSkillFilter(newFilter);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#setAge(int)
	 */
	@Override
	public void setAge(final int age)
	{
		if (age == this.age.get())
		{
			// We've already processed this change, most likely via the setAgeCategory method
			return;
		}

		theCharacter.setPCAttribute(NumericPCAttribute.AGE, age);
		this.age.set(age);
		updateAgeCategoryForAge();
		refreshStatScores();
		refreshLanguageList();
	}

	/**
	 * Update the character's age category based on their age.
	 */
	private void updateAgeCategoryForAge()
	{
		AgeSet ageSet = charDisplay.getAgeSet();
		if (ageSet != null)
		{
			String ageCatName = ageSet.getName();
			for (SimpleFacade ageCatFacade : ageCategoryList)
			{
				if (ageCatFacade.toString().equals(ageCatName))
				{
					ageCategory.set(ageCatFacade);
				}
			}
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAgeRef()
	 */
	@Override
	public ReferenceFacade<Integer> getAgeRef()
	{
		return age;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getAgeCategories()
	 */
	@Override
	public ListFacade<SimpleFacade> getAgeCategories()
	{
		return ageCategoryList;
	}

	@Override
	public void setAgeCategory(final SimpleFacade ageCat)
	{
		if (ageCat == this.ageCategory.get())
		{
			// We've already processed this change, most likely via the setAge method
			return;
		}

		final Race pcRace = charDisplay.getRace();
		final String selAgeCat = ageCat.toString();

		if ((pcRace != null) && !pcRace.isUnselected())
		{
			if (selAgeCat != null)
			{
				final int idx = SettingsHandler.getGame().getBioSet().getAgeSetNamed(selAgeCat);

				if (idx >= 0)
				{
					ageCategory.set(ageCat);
					SettingsHandler.getGame().getBioSet().randomize("AGECAT" + Integer.toString(idx), theCharacter);
					age.set(charDisplay.getAge());
					ageCategory.set(ageCat);
					refreshStatScores();
					refreshLanguageList();
				}
			}
		}
	}

	@Override
	public ReferenceFacade<SimpleFacade> getAgeCategoryRef()
	{
		return ageCategory;
	}

	/**
	 * This method updates the purchase point pool and the stat total text. The 
	 * stat total text will be updated whether we are in purchase mode or not. 
	 * displayed 
	 * @param checkPurchasePoints boolean true if the pool should be checked
	 * for available points before doing the update.
	 */
	private void updateScorePurchasePool(boolean checkPurchasePoints)
	{
		int usedStatPool = getUsedStatPool();

		// Handle purchase mode for stats
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			// Let them dink on stats at 0th or 1st PC levels
			if (canChangePurchasePool())
			{
				theCharacter.setCostPool(usedStatPool);
				theCharacter.setPoolAmount(usedStatPool);
			}

			final String bString = Integer.toString(theCharacter.getCostPool());
			//	int availablePool = SettingsHandler.getPurchaseModeMethodPool();
			int availablePool = theCharacter.getPointBuyPoints();
			if (availablePool < 0)
			{
				availablePool = RollingMethods.roll(SettingsHandler.getGame().getPurchaseModeMethodPoolFormula());
				theCharacter.setPointBuyPoints(availablePool);
			}

			if (availablePool != 0)
			{
				statTotalLabelText.set(LanguageBundle.getFormattedString("in_sumStatCost", SettingsHandler //$NON-NLS-1$
						.getGame().getPurchaseModeMethodName()));
				statTotalText.set(LanguageBundle.getFormattedString(
						"in_sumStatPurchaseDisplay", bString, availablePool)); //$NON-NLS-1$
				modTotalLabelText.set("");
				modTotalText.set("");
			}

			if (checkPurchasePoints && (availablePool != 0))
			{
				//
				// Let the user know that they've exceeded their goal, but allow them to keep going if they want...
				// Only do this at 1st level or lower
				//
				if (canChangePurchasePool() && (availablePool > 0) && (usedStatPool > availablePool))
				{
					delegate.showInfoMessage(Constants.APPLICATION_NAME, LanguageBundle.getFormattedString(
							"in_sumYouHaveExcededTheMaximumPointsOf", //$NON-NLS-1$
							String.valueOf(availablePool), SettingsHandler.getGame().getPurchaseModeMethodName()));
				}
			}
		}

		// Non-purchase mode for stats
		if (!SettingsHandler.getGame().isPurchaseStatMode() || (theCharacter.getPointBuyPoints() == 0))
		{
			int statTotal = 0;
			int modTotal = 0;

			for (PCStat aStat : charDisplay.getStatSet())
			{
				if (charDisplay.isNonAbility(aStat) || !aStat.getSafe(ObjectKey.ROLLED))
				{
					continue;
				}

				final int currentStat = theCharacter.getBaseStatFor(aStat);
				final int currentMod = theCharacter.getStatModFor(aStat);

				statTotal += currentStat;
				modTotal += currentMod;
			}

			statTotalLabelText.set(LanguageBundle.getString("in_sumStatTotalLabel")); //$NON-NLS-1$
			statTotalText
					.set(LanguageBundle.getFormattedString("in_sumStatTotal", Integer.toString(statTotal)));
			modTotalLabelText.set(LanguageBundle.getString("in_sumModTotalLabel"));
			modTotalText.set(LanguageBundle.getFormattedString("in_sumModTotal", Integer.toString(modTotal)));
		}

		if (charLevelsFacade.getSize() == 0
				&& (allAbilitiesAreZero() || (SettingsHandler.getGame().isPurchaseStatMode() && (theCharacter
						.getPointBuyPoints() != getUsedStatPool()))))
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SUMMARY, "Ability Scores", "in_sumTodoStats", 50));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoStats");
		}
	}

	/**
	 * Idenitfy if the character can stil change purchase pool values - spent 
	 * or available. This action is restricted by level. 
	 * @return true if the character is allowed to change the purchase pool
	 */
	public boolean canChangePurchasePool()
	{
		// This is a problem for races with non-0 level
		// adjustment so only count PC & NPC levels, not
		// monster levels XXX
		int pcPlayerLevels = charDisplay.totalNonMonsterLevels();

		int maxDiddleLevel;
		if (poolPointText != null)
		{
			maxDiddleLevel = 0;
		}
		else
		{
			maxDiddleLevel = 1;
		}
		return pcPlayerLevels <= maxDiddleLevel;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getStatTotalLabelTextRef()
	 */
	@Override
	public ReferenceFacade<String> getStatTotalLabelTextRef()
	{
		return statTotalLabelText;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getStatTotalTextRef()
	 */
	@Override
	public ReferenceFacade<String> getStatTotalTextRef()
	{
		return statTotalText;
	}

	/**
	 * @return A reference to the label text for the character's modifier total
	 */
	@Override
	public ReferenceFacade<String> getModTotalLabelTextRef()
	{
		return modTotalLabelText;
	}

	/**
	 * @return A reference to the text for the character's modifier total
	 */
	@Override
	public ReferenceFacade<String> getModTotalTextRef()
	{
		return modTotalText;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getTodoList()
	 */
	@Override
	public ListFacade<TodoFacade> getTodoList()
	{
		return todoManager.getTodoList();
	}

	/**
	 * @return the PlayerCharacter the facade is fronting for.
	 */
	PlayerCharacter getTheCharacter()
	{
		return theCharacter;
	}

	@Override
	public ReferenceFacade<Integer> getTotalHPRef()
	{
		return hpRef;
	}

	@Override
	public ReferenceFacade<Integer> getRollMethodRef()
	{
		return rollMethodRef;
	}

	@Override
	public void refreshRollMethod()
	{
		if (!canChangePurchasePool())
		{
			return;
		}
		GameMode game = (GameMode) dataSet.getGameMode();
		rollMethodRef.set(game.getRollMethod());
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			int availablePool = RollingMethods.roll(SettingsHandler.getGame().getPurchaseModeMethodPoolFormula());
			theCharacter.setPointBuyPoints(availablePool);

			// Make sure all scores are within the valid range
			for (StatFacade stat : statScoreMap.keySet())
			{
				WriteableReferenceFacade<Number> score = statScoreMap.get(stat);
				if (score.get().intValue() < SettingsHandler.getGame().getPurchaseScoreMin(theCharacter)
						&& stat instanceof PCStat)
				{
					setStatToPurchaseNeutral((PCStat) stat, score);
				}
			}

		}

		hpRef.set(theCharacter.hitPoints());
		updateScorePurchasePool(false);
	}

	/**
	 * Reset the stat score to the neutral value (usually 10) for 
	 * the point buy method.
	 * 
	 * @param pcStat The stata ebing adjusted.
	 * @param scoreRef The reference tothe current score.
	 */
	private void setStatToPurchaseNeutral(PCStat pcStat, WriteableReferenceFacade<Number> scoreRef)
	{
		int newScore = SettingsHandler.getGame().getPurchaseModeBaseStatScore(theCharacter);
		if (StringUtils.isNotEmpty(validateNewStatBaseScore(newScore, pcStat, charDisplay.totalNonMonsterLevels())))
		{
			newScore = SettingsHandler.getGame().getPurchaseScoreMin(theCharacter);
			if (StringUtils
					.isNotEmpty(validateNewStatBaseScore(newScore, pcStat, charDisplay.totalNonMonsterLevels())))
			{
				return;
			}
		}

		theCharacter.setStat(pcStat, newScore);
		scoreRef.set(newScore);
	}

	@Override
	public void adjustFunds(BigDecimal modVal)
	{
		BigDecimal currFunds = theCharacter.getGold();
		theCharacter.setGold(currFunds.add(modVal));
		updateWealthFields();
	}

	@Override
	public void setFunds(BigDecimal newVal)
	{
		theCharacter.setGold(newVal);
		updateWealthFields();
	}

	@Override
	public ReferenceFacade<BigDecimal> getFundsRef()
	{
		return fundsRef;
	}

	@Override
	public ReferenceFacade<BigDecimal> getWealthRef()
	{
		return wealthRef;
	}

	@Override
	public ReferenceFacade<GearBuySellFacade> getGearBuySellRef()
	{
		return gearBuySellSchemeRef;
	}

	@Override
	public void setGearBuySellRef(GearBuySellFacade gearBuySell)
	{
		gearBuySellSchemeRef.set(gearBuySell);
		GearBuySellScheme scheme = (GearBuySellScheme) gearBuySell;
		int rate = scheme.getBuyRate().intValue();
		SettingsHandler.setGearTab_BuyRate(rate);
		rate = scheme.getSellRate().intValue();
		SettingsHandler.setGearTab_SellRate(rate);
	}

	/**
	 * Update the wealth related fields.
	 */
	private void updateWealthFields()
	{
		fundsRef.set(theCharacter.getGold());
		wealthRef.set(theCharacter.totalValue());
	}

	@Override
	public void setAllowDebt(boolean allowDebt)
	{
		this.allowDebt = allowDebt;
	}

	@Override
	public boolean isAllowDebt()
	{
		return allowDebt;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getPurchasedEquipment()
	 */
	@Override
	public EquipmentListFacade getPurchasedEquipment()
	{
		return purchasedEquip;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#addPurchasedEquipment(pcgen.core.facade.EquipmentFacade, int)
	 */
	@Override
	public void addPurchasedEquipment(EquipmentFacade equipment, int quantity, boolean customize, boolean free)
	{
		if (equipment == null || quantity <= 0)
		{
			return;
		}

		//		int nextOutputIndex = 1;
		Equipment equipItemToAdjust = (Equipment) equipment;

		if (customize)
		{
			equipItemToAdjust = openCustomizer(equipItemToAdjust);
			if (equipItemToAdjust == null)
			{
				return;
			}
		}
		else
		{
			if (equipItemToAdjust.getSafe(ObjectKey.MOD_CONTROL).getModifiersRequired())
			{
				if (!hasBeenAdjusted(equipItemToAdjust))
				{
					delegate.showErrorMessage(Constants.APPLICATION_NAME,
						LanguageBundle
							.getString("in_igBuyMustCustomizeItemFirst")); //$NON-NLS-1$

					return;
				}
			}
		}
		Equipment updatedItem = theCharacter.getEquipmentNamed(equipItemToAdjust.getName());

		if (!free
			&& !canAfford(equipItemToAdjust, quantity,
				(GearBuySellScheme) gearBuySellSchemeRef.get()))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_igBuyInsufficientFunds", quantity, //$NON-NLS-1$
							equipItemToAdjust.getName()));
			return;
		}

		if (updatedItem != null)
		{
			// item is already in inventory; update it
			final double prevQty = (updatedItem.qty() < 0) ? 0 : updatedItem.qty();
			final double newQty = prevQty + quantity;

			theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
			Float qty = new Float(newQty);
			updatedItem.setQty(qty);
			purchasedEquip.setQuantity(equipment, qty.intValue());
		}
		else
		{
			// item is not in inventory; add it
			updatedItem = equipItemToAdjust.clone();

			if (updatedItem != null)
			{
				// Set the number carried and add it to the character
				Float qty = new Float(quantity);
				updatedItem.setQty(qty);
				theCharacter.addEquipment(updatedItem);
			}
			purchasedEquip.addElement(updatedItem, quantity);
		}

		// Update the PC and equipment
		if (!free)
		{
			double itemCost = calcItemCost(updatedItem, quantity, (GearBuySellScheme) gearBuySellSchemeRef.get());
			theCharacter.adjustGold(itemCost * -1);
		}
		theCharacter.setCalcEquipmentList();
		theCharacter.setDirty(true);
		updateWealthFields();
	}

	private boolean hasBeenAdjusted(Equipment equipItemToAdjust)
	{
		Set<EquipmentModifier> allEqMods = new HashSet<>(equipItemToAdjust.getEqModifierList(true));
		allEqMods.addAll(equipItemToAdjust.getEqModifierList(false));
		for (EquipmentModifier eqMod : allEqMods)
		{
			if (!eqMod.isType(Constants.EQMOD_TYPE_BASEMATERIAL))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is called to determine whether the character can afford to buy
	 * the requested quantity of an item at the rate selected.
	 * @param selected Equipment item being bought, used to determine the base price
	 * @param purchaseQty double number of the item bought
	 * @param gearBuySellScheme The scheme for buying and selling rates
	 *
	 * This method was overhauled March, 2003 by sage_sam as part of FREQ 606205
	 * @return true if it can be afforded
	 */
	private boolean canAfford(Equipment selected, double purchaseQty, GearBuySellScheme gearBuySellScheme)
	{
		final float currentFunds = theCharacter.getGold().floatValue();

		final double itemCost = calcItemCost(selected, purchaseQty, gearBuySellScheme);

		return allowDebt || (itemCost <= currentFunds);
	}

	private double calcItemCost(Equipment selected, double purchaseQty, GearBuySellScheme gearBuySellScheme)
	{
		if (selected == null)
		{
			return 0;
		}
		
		BigDecimal rate = purchaseQty >= 0 ? gearBuySellScheme.getBuyRate() : gearBuySellScheme.getSellRate();
		if (purchaseQty < 0 && selected.isSellAsCash())
		{
			rate = gearBuySellScheme.getCashSellRate();
		}

		return (purchaseQty * rate.intValue()) * (float) 0.01 * selected.getCost(theCharacter).floatValue();
	}

	private Equipment openCustomizer(Equipment aEq)
	{
		if (aEq == null)
		{
			return null;
		}

		Equipment newEquip = aEq.clone();
		if (!newEquip.containsKey(ObjectKey.BASE_ITEM))
		{
			newEquip.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(aEq));
		}

		List<VarModifier<?>> modifiers = newEquip.getListFor(ListKey.MODIFY);
		if (modifiers != null)
		{
			for (VarModifier<?> vm : modifiers)
			{
				theCharacter.addModifier(vm, newEquip, newEquip);
			}
		}

		for (EquipmentHead head : newEquip.getEquipmentHeads())
		{
			modifiers = head.getListFor(ListKey.MODIFY);
			if (modifiers != null)
			{
				for (VarModifier<?> vm : modifiers)
				{
					theCharacter.addModifier(vm, head, head);
				}
			}
		}

		EquipmentBuilderFacadeImpl builder =
				new EquipmentBuilderFacadeImpl(newEquip, theCharacter, delegate);
		CustomEquipResult result =
				delegate.showCustomEquipDialog(this, builder);
		if (newEquip != null && result != CustomEquipResult.CANCELLED)
		{
			dataSet.addEquipment(newEquip);
		}
		//TODO if this is returning null, then the SolverManager needs to destroy the unused channels :/
		return result == CustomEquipResult.PURCHASE ? newEquip : null;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#removePurchasedEquipment(pcgen.core.facade.EquipmentFacade, int)
	 */
	@Override
	public void removePurchasedEquipment(EquipmentFacade equipment, int quantity, boolean free)
	{
		if (equipment == null || quantity <= 0)
		{
			return;
		}

		Equipment equipItemToAdjust = (Equipment) equipment;

		Equipment updatedItem = theCharacter.getEquipmentNamed(equipItemToAdjust.getName());
		double numRemoved = 0;

		// see if item is already in inventory; update it
		if (updatedItem != null)
		{
			final double prevQty = (updatedItem.qty() < 0) ? 0 : updatedItem.qty();
			numRemoved = Math.min(quantity, prevQty);
			final double newQty = Math.max(prevQty - numRemoved, 0);

			if (newQty <= 0)
			{
				// completely remove item
				updatedItem.setNumberCarried(new Float(0));
				updatedItem.setLocation(EquipmentLocation.NOT_CARRIED);

				final Equipment eqParent = updatedItem.getParent();

				if (eqParent != null)
				{
					eqParent.removeChild(theCharacter, updatedItem);
				}

				theCharacter.removeEquipment(updatedItem);
				purchasedEquip.removeElement(updatedItem);
			}
			else
			{
				// update item count
				theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
				Float qty = new Float(newQty);
				updatedItem.setQty(qty);
				updatedItem.setNumberCarried(qty);
				purchasedEquip.setQuantity(equipment, qty.intValue());
			}

			theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
			Float qty = new Float(newQty);
			updatedItem.setQty(qty);
			updatedItem.setNumberCarried(qty);
		}

		// Update the PC and equipment
		if (!free)
		{
			double itemCost =
					calcItemCost(updatedItem, numRemoved * -1,
						(GearBuySellScheme) gearBuySellSchemeRef.get());
			theCharacter.adjustGold(itemCost * -1);
		}
		theCharacter.setCalcEquipmentList();
		theCharacter.setDirty(true);
		updateWealthFields();
	}


	@Override
	public void deleteCustomEquipment(EquipmentFacade eqFacade)
	{
		if (eqFacade == null || !(eqFacade instanceof Equipment))
		{
			return;
		}

		Equipment itemToBeDeleted = (Equipment) eqFacade;
		
		if (!itemToBeDeleted.isType(Constants.TYPE_CUSTOM))
		{
			return;
		}
		
		if (!delegate.showWarningConfirm(LanguageBundle
			.getString("in_igDeleteCustomWarnTitle"), //$NON-NLS-1$
			LanguageBundle.getFormattedString("in_igDeleteCustomWarning", //$NON-NLS-1$
				itemToBeDeleted)))
		{
			return;
		}
		
		removePurchasedEquipment(itemToBeDeleted, Integer.MAX_VALUE, false);
		Globals.getContext().getReferenceContext().forget(itemToBeDeleted);
		
		if (dataSet.getEquipment() instanceof DefaultListFacade<?>)
		{
			((DefaultListFacade<EquipmentFacade>) dataSet.getEquipment())
				.removeElement(itemToBeDeleted);
		}
		
	}
	
	/**
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.EquipmentFacade)
	 */
	@Override
	public boolean isQualifiedFor(EquipmentFacade equipment)
	{
		final Equipment equip = (Equipment) equipment;
		final boolean accept = PrereqHandler.passesAll(equip, theCharacter, equip);

		if (accept && (equip.isShield() || equip.isWeapon() || equip.isArmor()))
		{
			return theCharacter.isProficientWith(equip);
		}

		return accept;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSizedForCharacter(pcgen.core.facade.EquipmentFacade)
	 */
	@Override
	public EquipmentFacade getEquipmentSizedForCharacter(EquipmentFacade equipment)
	{
		final Equipment equip = (Equipment) equipment;
		final SizeAdjustment newSize = charDisplay.getSizeAdjustment();
		if (equip.getSizeAdjustment() == newSize || !Globals.canResizeHaveEffect(equip, null))
		{
			return equipment;
		}

		final String existingKey = equip.getKeyName();
		final String newKey = equip.createKeyForAutoResize(newSize);

		Equipment potential = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class, newKey);

		if (newKey.equals(existingKey))
		{
			return equipment;
		}

		// If we've already resized this piece of equipment to this size
		// on a previous occasion, just substitute that piece of equipment
		// in place of the selected equipment.
		if (potential != null)
		{
			return potential;
		}

		final String newName = equip.createNameForAutoResize(newSize);
		potential = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class, newName);

		if (potential != null)
		{
			return potential;
		}

		final Equipment newEq = equip.clone();

		if (!newEq.containsKey(ObjectKey.BASE_ITEM))
		{
			newEq.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(equip));
		}

		newEq.setName(newName);
		newEq.put(StringKey.OUTPUT_NAME, newName);
		newEq.put(StringKey.KEY_NAME, newKey);
		newEq.resizeItem(theCharacter, newSize);
		newEq.removeType(Type.AUTO_GEN);
		newEq.removeType(Type.STANDARD);
		if (!newEq.isType(Constants.TYPE_CUSTOM))
		{
			newEq.addType(Type.CUSTOM);
		}

		Globals.getContext().getReferenceContext().importObject(newEq);

		return newEq;
	}

	/**
	 * Whether we should automatically resize all purchased gear to match the 
	 * character's size.
	 * @return true if equipment should be auto resize.
	 */
	@Override
	public boolean isAutoResize()
	{
		return theCharacter.isAutoResize();
	}

	/**
	 * Update whether we should automatically resize all purchased gear to match  
	 * the character's size.
	 * 
	 * @param autoResize The new value for auto resize equipment option.
	 */
	@Override
	public void setAutoResize(boolean autoResize)
	{
		theCharacter.setAutoResize(autoResize);
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#createEquipmentSet(java.lang.String)
	 */
	@Override
	public EquipmentSetFacade createEquipmentSet(String setName)
	{
		String id = EquipmentSetFacadeImpl.getNewIdPath(charDisplay, null);
		EquipSet eSet = new EquipSet(id, setName);
		theCharacter.addEquipSet(eSet);
		final EquipmentSetFacadeImpl facade =
				new EquipmentSetFacadeImpl(delegate, theCharacter, eSet,
					dataSet, purchasedEquip, todoManager, this);
		equipmentSets.addElement(facade);

		return facade;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#deleteEquipmentSet(pcgen.core.facade.EquipmentSetFacade)
	 */
	@Override
	public void deleteEquipmentSet(EquipmentSetFacade set)
	{
		if (set == null || !(set instanceof EquipmentSetFacadeImpl))
		{
			return;
		}
		EquipmentSetFacadeImpl setImpl = (EquipmentSetFacadeImpl) set;
		EquipSet eSet = setImpl.getEquipSet();

		theCharacter.delEquipSet(eSet);
		equipmentSets.removeElement(set);
	}

	@Override
	public ReferenceFacade<String> getCarriedWeightRef()
	{
		return carriedWeightRef;
	}

	@Override
	public ReferenceFacade<String> getLoadRef()
	{
		return loadRef;
	}

	@Override
	public ReferenceFacade<String> getWeightLimitRef()
	{
		return weightLimitRef;
	}

	/**
	 * @see pcgen.core.facade.EquipmentListFacade.EquipmentListListener#quantityChanged(pcgen.core.facade.EquipmentListFacade.EquipmentListEvent)
	 */
	@Override
	public void quantityChanged(EquipmentListEvent e)
	{
		refreshTotalWeight();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementAdded(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementAdded(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementRemoved(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementRemoved(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementsChanged(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementsChanged(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementModified(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementModified(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/**
	 * Refreshes the total weight by reading it from the current equipment set.  
	 */
	private void refreshTotalWeight()
	{
		String weight = Globals.getGameModeUnitSet().displayWeightInUnitSet(charDisplay.totalWeight().doubleValue());
		carriedWeightRef.set(weight);

		Load load = charDisplay.getLoadType();
		loadRef.set(CoreUtility.capitalizeFirstLetter(load.toString()));

		Float mult = SettingsHandler.getGame().getLoadInfo().getLoadMultiplier(load.toString());
		double limit = 0.0f;
		if (mult != null)
		{
			limit = charDisplay.getLoadToken(load.toString());
		}
		double lowerLimit = 0.0f;
		for (Load testLoad : Load.values())
		{
			double testLimit = charDisplay.getLoadToken(testLoad.toString());
			if (testLoad.compareTo(load) < 0 && testLimit > lowerLimit)
			{
				lowerLimit = testLimit;
			}
		}
		StringBuilder loadLimit = new StringBuilder(Globals.getGameModeUnitSet().displayWeightInUnitSet(lowerLimit));
		if (limit > 0)
		{
			loadLimit.append(" - ");
			loadLimit.append(Globals.getGameModeUnitSet().displayWeightInUnitSet(limit));
		}
		else
		{
			loadLimit.append("+ ");
		}
		loadLimit.append(Globals.getGameModeUnitSet().getWeightUnit());
		weightLimitRef.set(loadLimit.toString());
	}

	/**
	 * @see pcgen.core.facade.CharacterLevelsFacade.HitPointListener#hitPointsChanged(pcgen.core.facade.CharacterLevelsFacade.CharacterLevelEvent)
	 */
	@Override
	public void hitPointsChanged(CharacterLevelEvent e)
	{
		hpRef.set(theCharacter.hitPoints());
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getInfoFactory()
	 */
	@Override
	public InfoFactory getInfoFactory()
	{
		return infoFactory;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.InfoFacade)
	 */
	@Override
	public boolean isQualifiedFor(InfoFacade infoFacade)
	{
		if (!(infoFacade instanceof PObject))
		{
			return false;
		}

		PObject pObj = (PObject) infoFacade;
		if (!theCharacter.isQualified(pObj))
		{
			return false;
		}
		
		if (infoFacade instanceof Kit)
		{
			Kit kit = (Kit) infoFacade;
			BigDecimal totalCost = kit.getTotalCostToBeCharged(theCharacter);
			if (totalCost != null)
			{
				if (theCharacter.getGold().compareTo(totalCost) < 0)
				{
					// Character cannto afford the kit
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.DeityFacade)
	 */
	@Override
	public boolean isQualifiedFor(DeityFacade deityFacade)
	{
		if (!(deityFacade instanceof Deity))
		{
			return false;
		}
		Deity aDeity = (Deity) deityFacade;
		return PrereqHandler.passesAll(aDeity, theCharacter, aDeity)
				&& theCharacter.isQualified(aDeity);
	}
	
	/**
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.DomainFacade)
	 */
	@Override
	public boolean isQualifiedFor(DomainFacade domainFacade)
	{
		if (!(domainFacade instanceof DomainFacadeImpl))
		{
			return false;
		}

		DomainFacadeImpl domainFI = (DomainFacadeImpl) domainFacade;
		Domain domain = domainFI.getRawObject();
		if (!PrereqHandler.passesAll(domainFI, theCharacter, domain)
				|| !theCharacter.isQualified(domain))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isQualifiedFor(TempBonusFacade tempBonusFacade)
	{
		if (!(tempBonusFacade instanceof TempBonusFacadeImpl))
		{
			return false;
		}

		TempBonusFacadeImpl tempBonus = (TempBonusFacadeImpl) tempBonusFacade;
		CDOMObject originObj = tempBonus.getOriginObj();
		if (!theCharacter.isQualified(originObj))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isQualifiedFor(SpellFacade spellFacade,
		ClassFacade classFacade)
	{
		if (!(spellFacade instanceof SpellFacadeImplem)
			|| !(classFacade == null || classFacade instanceof PCClass))
		{
			return false;
		}

		SpellFacadeImplem spellFI = (SpellFacadeImplem) spellFacade;
		PCClass pcClass = (PCClass) classFacade;

		if (!theCharacter.isQualified(spellFI.getSpell()))
		{
			return false;
		}
		if (!spellFI.getCharSpell().isSpecialtySpell(theCharacter)
			&& SpellCountCalc.isProhibited(spellFI.getSpell(), pcClass,
				theCharacter))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isQualifiedFor(EquipmentFacade equipFacade, EquipModFacade eqModFacade)
	{
		if (!(equipFacade instanceof Equipment) || !(eqModFacade instanceof EquipmentModifier))
		{
			return false;
		}
		
		Equipment equip = (Equipment) equipFacade;
		EquipmentModifier eqMod = (EquipmentModifier) eqModFacade;
		
		//TODO: Handle second head
		return equip.canAddModifier(theCharacter, eqMod, true);
	}
	
	/**
	 * @see pcgen.core.facade.CharacterFacade#addTemplate(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public void addTemplate(TemplateFacade templateFacade)
	{
		if (templateFacade == null || !(templateFacade instanceof PCTemplate))
		{
			return;
		}

		PCTemplate template = (PCTemplate) templateFacade;

		if (!PrereqHandler.passesAll(template, theCharacter, template))
		{
			return;
		}

		if (!charDisplay.hasTemplate(template))
		{
			Logging.log(Logging.INFO, charDisplay.getName()
				+ ": Adding template " + template); //$NON-NLS-1$
			int oldLevel = charLevelsFacade.getSize();
			if (theCharacter.addTemplate(template))
			{
				Logging.log(Logging.INFO, charDisplay.getName()
					+ ": Successful add of template " + template); //$NON-NLS-1$
				templates.addElement(template);
				refreshRaceRelatedFields();

				if (oldLevel != charLevelsFacade.getSize())
				{
					delegate.showLevelUpInfo(this, oldLevel);
				}
			}
			else
			{
				Logging.log(
						Logging.INFO,
						charDisplay.getName()
							+ ": Nope: Add template " + template + " failed because no selection was made"); //$NON-NLS-1$
			}
		}
		else
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_irHaveTemplate"));
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#removeTemplate(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public void removeTemplate(TemplateFacade templateFacade)
	{
		if (templateFacade == null || !(templateFacade instanceof PCTemplate))
		{
			return;
		}

		PCTemplate template = (PCTemplate) templateFacade;

		if (charDisplay.hasTemplate(template) && template.isRemovable())
		{
			theCharacter.removeTemplate(template);
			theCharacter.calcActiveBonuses();
			templates.removeElement(template);
		}
		else
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_irNotRemovable"));
		}
	}

	private void refreshTemplates()
	{
		Collection<PCTemplate> pcTemplates = charDisplay.getDisplayVisibleTemplateList();
		for (PCTemplate template : pcTemplates)
		{
			if (!templates.containsElement(template))
			{
				templates.addElement(template);
			}
		}
		for (Iterator<TemplateFacade> iterator = templates.iterator(); iterator.hasNext();)
		{
			PCTemplate pcTemplate = (PCTemplate) iterator.next();
			if (!pcTemplates.contains(pcTemplate))
			{
				iterator.remove();
			}
		}
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getTemplates()
	 */
	@Override
	public ListFacade<TemplateFacade> getTemplates()
	{
		return templates;
	}

	@Override
	public void addCharacterChangeListener(CharacterChangeListener listener)
	{
		//TODO: implement this
	}

	@Override
	public void removeCharacterChangeListener(CharacterChangeListener listener)
	{
		//TODO: implement this
	}

	/**
	 * @see pcgen.core.facade.CharacterFacade#getSpellSupport()
	 */
	@Override
	public SpellSupportFacade getSpellSupport()
	{
		return spellSupportFacade;
	}

	@Override
	public ReferenceFacade<File> getPortraitRef()
	{
		return portrait;
	}

	@Override
	public void setPortrait(File file)
	{
		portrait.set(file);
		theCharacter.setPortraitPath(file == null ? null : file.getAbsolutePath());
	}

	@Override
	public ReferenceFacade<Rectangle> getThumbnailCropRef()
	{
		return cropRect;
	}

	@Override
	public void setThumbnailCrop(Rectangle rect)
	{
		cropRect.set(rect);
		theCharacter.setPortraitThumbnailRect(rect);
	}

	@Override
	public boolean isDirty()
	{
		return theCharacter.isDirty();
	}

	@Override
	public CompanionSupportFacade getCompanionSupport()
	{
		return companionSupportFacade;
	}

	@Override
	public String getCompanionType()
	{
		Follower master = charDisplay.getMaster();
		if (master != null)
		{
			return master.getType().getKeyName();
		}
		return null;
	}

	@Override
	public CharacterStubFacade getMaster()
	{
		Follower master = charDisplay.getMaster();
		if (master == null)
		{
			return null;
		}
		CompanionNotLoaded stub = new CompanionNotLoaded(master.getName(), new File(master.getFileName()),
				master.getRace(), master.getType().getKeyName());
		CharacterFacade masterFacade = CharacterManager.getCharacterMatching(stub);
		if (masterFacade != null)
		{
			return masterFacade;
		}
		return stub;
	}

	/**
	 * Since Rectangles are modifiable we make sure that no references of the reference
	 * object are leaked to the outside world. This guarantees that the underlying reference
	 * object will not changed after it is set.
	 */
	private static class RectangleReference extends DefaultReferenceFacade<Rectangle>
	{

		/**
		 * Create a new reference based on the supplied rectangle.
		 * @param rect
		 */
		public RectangleReference(Rectangle rect)
		{
			super(rect == null ? null : (Rectangle) rect.clone());
		}

		@Override
		public Rectangle get()
		{
			Rectangle rect = super.get();
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			return rect;
		}

		@Override
		public void set(Rectangle rect)
		{
			Rectangle old = get();
			if (ObjectUtils.equals(old, rect))
			{
				return;
			}
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			this.object = rect;
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			fireReferenceChangedEvent(this, old, rect);
		}

	}

	@Override
	public DefaultListFacade<KitFacade> getKits()
	{
		return kitList;
	}

	@Override
	public void addKit(KitFacade obj)
	{
		if (obj == null || !(obj instanceof Kit))
		{
			return;
		}

		Kit kit = (Kit) obj;
		if (!theCharacter.isQualified(kit))
		{
			return;
		}

		Logging.log(Logging.INFO, charDisplay.getName()
			+ ": Testing kit " + kit); //$NON-NLS-1$
		List<BaseKit> thingsToAdd = new ArrayList<>();
		List<String> warnings = new ArrayList<>();
		kit.testApplyKit(theCharacter, thingsToAdd, warnings);

		//
		// See if user wants to apply the kit even though there were errors
		//

		if (!showKitWarnings(kit, warnings))
		{
			return;
		}

		// The user is applying the kit so use the real PC now.
		Logging.log(Logging.INFO, charDisplay.getName()
			+ ": Adding kit " + kit); //$NON-NLS-1$
		kit.processKit(theCharacter, thingsToAdd);
		kitList.addElement(obj);

		// Kits can upate most things so do a thorough refresh
		race.set(charDisplay.getRace());
		refreshRaceRelatedFields();
		name.set(charDisplay.getName());
		characterType.set(charDisplay.getCharacterType());

		// Deity and domains
		deity.set(charDisplay.getDeity());
		buildAvailableDomainsList();
		
		refreshStatScores();
	}


	private void refreshEquipment()
	{
		fundsRef.set(theCharacter.getGold());
		wealthRef.set(theCharacter.totalValue());

		purchasedEquip.refresh(theCharacter.getEquipmentMasterList());
		initEquipSet();
	}

	/**
	 * Show the user any warnings from thekit application and get 
	 * their approval to continue.
	 * 
	 * @param kit The kit being applied.
	 * @param warnings The warnigns generated in the test application.
	 * @return true if the kit should be applied, false if not.
	 */
	private boolean showKitWarnings(Kit kit, List<String> warnings)
	{
		if (warnings.isEmpty())
		{
			return true;
		}

		HtmlInfoBuilder warningMsg = new HtmlInfoBuilder();

		warningMsg.append(LanguageBundle.getString("in_kitWarnStart")); //$NON-NLS-1$
		warningMsg.appendLineBreak();
		warningMsg.append("<UL>"); //$NON-NLS-1$
		for (String string : warnings)
		{
			warningMsg.appendLineBreak();
			warningMsg.append("<li>"); //$NON-NLS-1$
			warningMsg.append(string);
			warningMsg.append("</li>"); //$NON-NLS-1$
		}
		warningMsg.append("</UL>"); //$NON-NLS-1$
		warningMsg.appendLineBreak();
		warningMsg.append(LanguageBundle.getString("in_kitWarnEnd")); //$NON-NLS-1$

		return delegate.showWarningConfirm(kit.getDisplayName(), warningMsg.toString());
	}

	@Override
	public List<KitFacade> getAvailableKits()
	{
		List<KitFacade> kits = new ArrayList<>();
		for (KitFacade obj : dataSet.getKits())
		{
			if (obj == null || !(obj instanceof Kit))
			{
				continue;
			}

			if (((Kit) obj).isVisible(theCharacter, View.VISIBLE_DISPLAY))
			{
				kits.add(obj);
			}

		}

		return kits;
	}

	@Override
	public VariableProcessor getVariableProcessor()
	{
		return theCharacter.getVariableProcessor();
	}

	@Override
	public Float getVariable(String variableString, boolean isMax)
	{
		return theCharacter.getVariable(variableString, isMax);
	}

	@Override
	public boolean matchesCharacter(PlayerCharacter pc)
	{
		return theCharacter != null && theCharacter.equals(pc);
	}
	
	@Override
	public void modifyCharges(List<EquipmentFacade> targets)
	{
		List<Equipment> chargedEquip = new ArrayList<>();
		for (EquipmentFacade equipmentFacade : targets)
		{
			if (equipmentFacade instanceof Equipment && ((Equipment) equipmentFacade).getMaxCharges() > 0)
			{
				chargedEquip.add((Equipment) equipmentFacade);
			}
		}
		
		if (chargedEquip.isEmpty())
		{
			return;
		}
		
		for (Equipment equip : chargedEquip)
		{
			int selectedCharges = getSelectedCharges(equip);
			if (selectedCharges < 0)
			{
				return;
			}
			equip.setRemainingCharges(selectedCharges);
			purchasedEquip.modifyElement(equip);	
		}
	}
	
	private int getSelectedCharges(Equipment equip)
	{
		int minCharges = equip.getMinCharges();
		int maxCharges = equip.getMaxCharges();
		
		String selectedValue =
				delegate.showInputDialog(equip.toString(), LanguageBundle
					.getFormattedString("in_igNumCharges", //$NON-NLS-1$
						Integer.toString(minCharges),
						Integer.toString(maxCharges)), Integer.toString(equip
					.getRemainingCharges()));
		

		if (selectedValue == null)
		{
			return -1;
		}

		int charges;
		try
		{
			charges = Integer.parseInt(selectedValue.trim());
		}
		catch (NumberFormatException e) 
		{
			charges = minCharges-1;
		}
		if ((charges < minCharges) || (charges > maxCharges))
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_igValueOutOfRange"),
				Constants.APPLICATION_NAME, MessageType.ERROR);
			return getSelectedCharges(equip);
		}

		return charges;
	}

	@Override
	public void addNote(List<EquipmentFacade> targets)
	{
		List<Equipment> notedEquip = new ArrayList<>();
		for (EquipmentFacade equipmentFacade : targets)
		{
			if (equipmentFacade instanceof Equipment)
			{
				notedEquip.add((Equipment) equipmentFacade);
			}
		}
		
		if (notedEquip.isEmpty())
		{
			return;
		}
		
		for (Equipment equip : notedEquip)
		{
			String note = getNote(equip);
			if (note == null)
			{
				return;
			}
			equip.setNote(note);
			purchasedEquip.modifyElement(equip);	
		}
	}
	
	private String getNote(Equipment equip)
	{
		
		return delegate.showInputDialog(equip.toString(), LanguageBundle
					.getFormattedString("in_igEnterNote"), //$NON-NLS-1$
						equip.getNote());
	}
	
	/**
	 * The Class {@code LanguageListener} tracks adding and removal of
	 * languages to the character.
	 */
	public class LanguageListener implements DataFacetChangeListener<CharID, Language>
	{
		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, Language> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshLanguageList();
		}

		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, Language> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshLanguageList();
		}
		
	}
	
	/**
	 * The Class {@code TemplateListener} tracks adding and removal of
	 * templates to the character.
	 */
	public class TemplateListener implements DataFacetChangeListener<CharID, PCTemplate>
	{
		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshTemplates();
		}

		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshTemplates();
		}
		
	}
	
	/**
	 * The Class {@code XPListener} tracks changes to the character's experience value.
	 */
	public class XPListener implements DataFacetChangeListener<CharID, Integer>
	{
		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, Integer> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			checkForNewLevel();
		}

		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, Integer> dfce)
		{
			// Ignored - we will always get the added message.
		}
		
	}
	
	/**
	 * The Class {@code AutoEquipListener} tracks changes to the character's automatically granted equipment.
	 */
	public class AutoEquipListener implements DataFacetChangeListener<CharID, QualifiedObject<CDOMReference<Equipment>>>
	{
		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, QualifiedObject<CDOMReference<Equipment>>> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshEquipment();
		}

		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, QualifiedObject<CDOMReference<Equipment>>> dfce)
		{
			if (dfce.getCharID() != theCharacter.getCharID())
			{
				return;
			}
			refreshEquipment();
		}
		
	}

	@Override
	public List<CoreViewNodeFacade> getCoreViewTree(CorePerspective pers)
	{
		List<CoreViewNodeFacade> coreDebugList = CoreUtils.buildCoreDebugList(theCharacter, pers);
		return coreDebugList;
	}

	@Override
	public CharID getCharID()
	{
		return theCharacter.getCharID();
	}

}
