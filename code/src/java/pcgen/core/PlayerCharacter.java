/*
 * PlayerCharacter.java
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.content.RollMethod;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.*;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.inst.ObjectCache;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.analysis.AddObjectActions;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.analysis.ClassSkillApplication;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SpecialAbilityResolution;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.analysis.SpellPoint;
import pcgen.core.analysis.StatAnalysis;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusPair;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.GuiConstants;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.AttackType;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

/**
 * <code>PlayerCharacter</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class PlayerCharacter extends Observable implements Cloneable,
		VariableContainer, AssociationStore
{
	// Constants for use in getBonus
	private static String lastVariable = null;

	static
	{
		FacetInitialization.initialize();
	}

	private CharID id = new CharID();

	private DomainFacet domainFacet = FacetLibrary.getFacet(DomainFacet.class);
	private TemplateFacet templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
	private ConditionalTemplateFacet conditionalTemplateFacet =
            FacetLibrary.getFacet(ConditionalTemplateFacet.class);
	private DeityFacet deityFacet = FacetLibrary.getFacet(DeityFacet.class);
	private AlignmentFacet alignmentFacet = FacetLibrary.getFacet(AlignmentFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);
	private NonAbilityFacet nonAbilityFacet = FacetLibrary.getFacet(NonAbilityFacet.class);
	private UnlockedStatFacet unlockedStatFacet = FacetLibrary.getFacet(UnlockedStatFacet.class);
	private StatLockFacet statLockFacet = FacetLibrary.getFacet(StatLockFacet.class);
	private CheckFacet checkFacet = FacetLibrary.getFacet(CheckFacet.class);
	private SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
	private CompanionModFacet companionModFacet = FacetLibrary.getFacet(CompanionModFacet.class);
	private CampaignFacet campaignFacet = FacetLibrary.getFacet(CampaignFacet.class);
	private ExpandedCampaignFacet expandedCampaignFacet = FacetLibrary.getFacet(ExpandedCampaignFacet.class);
	private BioSetFacet bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
	private UserEquipmentFacet userEquipmentFacet = FacetLibrary.getFacet(UserEquipmentFacet.class);
	private EquipmentFacet equipmentFacet = FacetLibrary.getFacet(EquipmentFacet.class);
	private EquippedEquipmentFacet equippedFacet = FacetLibrary.getFacet(EquippedEquipmentFacet.class);
	private SourcedEquipmentFacet activeEquipmentFacet = FacetLibrary.getFacet(SourcedEquipmentFacet.class);
	private ActiveAbilityFacet abFacet = FacetLibrary.getFacet(ActiveAbilityFacet.class);
	private ConditionallyGrantedAbilityFacet cabFacet =
            FacetLibrary.getFacet(ConditionallyGrantedAbilityFacet.class);
	private ConditionalAbilityFacet conditionalFacet = FacetLibrary.getFacet(ConditionalAbilityFacet.class);
	private GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);
	private DirectAbilityFacet directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
	private KitFacet kitFacet = FacetLibrary.getFacet(KitFacet.class);
	private BonusWeaponProfFacet wpBonusFacet = FacetLibrary.getFacet(BonusWeaponProfFacet.class);
	private AutoListWeaponProfFacet alWeaponProfFacet = FacetLibrary.getFacet(AutoListWeaponProfFacet.class);
	private DamageReductionFacet drFacet = FacetLibrary.getFacet(DamageReductionFacet.class);
	private AutoListArmorProfFacet armorProfListFacet = FacetLibrary.getFacet(AutoListArmorProfFacet.class);
	private ArmorProfProviderFacet armorProfFacet = FacetLibrary.getFacet(ArmorProfProviderFacet.class);
	private AutoListShieldProfFacet shieldProfListFacet = FacetLibrary.getFacet(AutoListShieldProfFacet.class);
	private ShieldProfProviderFacet shieldProfFacet = FacetLibrary.getFacet(ShieldProfProviderFacet.class);
	private CharacterSpellResistanceFacet srFacet = FacetLibrary.getFacet(CharacterSpellResistanceFacet.class);
	private AddedTemplateFacet addedTemplateFacet = FacetLibrary.getFacet(AddedTemplateFacet.class);
	private WeaponProfFacet weaponProfFacet = FacetLibrary.getFacet(WeaponProfFacet.class);
	private MasterFacet masterFacet = FacetLibrary.getFacet(MasterFacet.class);
	private AutoEquipmentListFacet autoListEquipmentFacet = FacetLibrary.getFacet(AutoEquipmentListFacet.class);
	private MonsterCSkillFacet monCSkillFacet = FacetLibrary.getFacet(MonsterCSkillFacet.class);
	private ProhibitedSchoolFacet prohibitedSchoolFacet = FacetLibrary.getFacet(ProhibitedSchoolFacet.class);
	private MasterSkillFacet masterSkillFacet = FacetLibrary.getFacet(MasterSkillFacet.class);
	private SubClassFacet subClassFacet = FacetLibrary.getFacet(SubClassFacet.class);
	private FollowerFacet followerFacet = FacetLibrary.getFacet(FollowerFacet.class);
	
	private LanguageFacet languageFacet = FacetLibrary.getFacet(LanguageFacet.class);
	private LanguageFacet freeLangFacet = FacetLibrary.getFacet(FreeLanguageFacet.class);
	private AutoLanguageFacet autoLangFacet = FacetLibrary.getFacet(AutoLanguageFacet.class);
	private LanguageFacet addLangFacet = FacetLibrary.getFacet(AddLanguageFacet.class);
	private LanguageFacet skillLangFacet = FacetLibrary.getFacet(SkillLanguageFacet.class);
	private StartingLanguageFacet startingLangFacet = FacetLibrary.getFacet(StartingLanguageFacet.class);

	private ObjectCache cache = new ObjectCache();
	private AssociationSupport assocSupt = new AssociationSupport();
	private BonusManager bonusManager = new BonusManager(this);
	private BonusChangeFacet bonusChangeFacet = FacetLibrary.getFacet(BonusChangeFacet.class);
	private EquipSetFacet equipSetFacet = FacetLibrary.getFacet(EquipSetFacet.class);

	private HitPointFacet hitPointFacet = FacetLibrary.getFacet(HitPointFacet.class);

	private UnarmedDamageFacet unarmedDamageFacet = FacetLibrary.getFacet(UnarmedDamageFacet.class);
	private SubRaceFacet subRaceFacet = FacetLibrary.getFacet(SubRaceFacet.class);
	private RacialSubTypesFacet subTypesFacet = FacetLibrary.getFacet(RacialSubTypesFacet.class);
	private RaceTypeFacet raceTypeFacet = FacetLibrary.getFacet(RaceTypeFacet.class);
	private HandsFacet handsFacet = FacetLibrary.getFacet(HandsFacet.class);
	private LegsFacet legsFacet = FacetLibrary.getFacet(LegsFacet.class);
	private FaceFacet faceFacet = FacetLibrary.getFacet(FaceFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
	private LevelTableFacet levelTableFacet = FacetLibrary.getFacet(LevelTableFacet.class);
	private SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
	private GenderFacet genderFacet = FacetLibrary.getFacet(GenderFacet.class);
	private HeightFacet heightFacet = FacetLibrary.getFacet(HeightFacet.class);
	private WeightFacet weightFacet = FacetLibrary.getFacet(WeightFacet.class);
	private RegionFacet regionFacet = FacetLibrary.getFacet(RegionFacet.class);
	private MoneyFacet moneyFacet = FacetLibrary.getFacet(MoneyFacet.class);
	private ChallengeRatingFacet crFacet = FacetLibrary.getFacet(ChallengeRatingFacet.class);
	private InitiativeFacet initiativeFacet = FacetLibrary.getFacet(InitiativeFacet.class);
	private NonProficiencyPenaltyFacet nonppFacet = FacetLibrary.getFacet(NonProficiencyPenaltyFacet.class);
	private ReachFacet reachFacet = FacetLibrary.getFacet(ReachFacet.class);
	private XPFacet xpFacet = FacetLibrary.getFacet(XPFacet.class);
	private FactFacet factFacet = FacetLibrary.getFacet(FactFacet.class);
	private QualifyFacet qualifyFacet = FacetLibrary.getFacet(QualifyFacet.class);
	private FavoredClassFacet favClassFacet = FacetLibrary.getFacet(FavoredClassFacet.class);
	private VariableFacet variableFacet = FacetLibrary.getFacet(VariableFacet.class);
	private VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
	private FollowerOptionFacet foFacet = FacetLibrary.getFacet(FollowerOptionFacet.class);
	private FollowerLimitFacet followerLimitFacet = FacetLibrary.getFacet(FollowerLimitFacet.class);
	private AvailableSpellFacet availSpellFacet = FacetLibrary.getFacet(AvailableSpellFacet.class);
	private MovementResultFacet moveResultFacet = FacetLibrary.getFacet(MovementResultFacet.class);
	private UnencumberedLoadFacet unencumberedLoadFacet = FacetLibrary.getFacet(UnencumberedLoadFacet.class);
	private AutoEquipmentFacet autoEquipFacet = FacetLibrary.getFacet(AutoEquipmentFacet.class);
	private SpellBookFacet spellBookFacet = FacetLibrary.getFacet(SpellBookFacet.class);
	private HasAnyFavoredClassFacet hasAnyFavoredFacet = FacetLibrary.getFacet(HasAnyFavoredClassFacet.class);
	private TotalWeightFacet totalWeightFacet = FacetLibrary.getFacet(TotalWeightFacet.class);
	private LoadFacet loadFacet = FacetLibrary.getFacet(LoadFacet.class);
	private AppliedBonusFacet appliedBonusFacet = FacetLibrary.getFacet(AppliedBonusFacet.class);
	private AddedBonusFacet addedBonusFacet = FacetLibrary.getFacet(AddedBonusFacet.class);
	private GlobalAddedSkillCostFacet globalAddedSkillCostFacet =
            FacetLibrary.getFacet(GlobalAddedSkillCostFacet.class);
	private GlobalSkillCostFacet globalSkillCostFacet = FacetLibrary.getFacet(GlobalSkillCostFacet.class);
	private LocalAddedSkillCostFacet localAddedSkillCostFacet =
            FacetLibrary.getFacet(LocalAddedSkillCostFacet.class);
	private LocalSkillCostFacet localSkillCostFacet = FacetLibrary.getFacet(LocalSkillCostFacet.class);
	private ListSkillCostFacet listSkillCostFacet = FacetLibrary.getFacet(ListSkillCostFacet.class);
	private SpellSupportFacet spellSupportFacet = FacetLibrary.getFacet(SpellSupportFacet.class);
	private LegalDeityFacet legalDeityFacet = FacetLibrary.getFacet(LegalDeityFacet.class);
	private AgeFacet ageFacet = FacetLibrary.getFacet(AgeFacet.class);
	private AgeSetFacet ageSetFacet = FacetLibrary.getFacet(AgeSetFacet.class);
	private MultiClassFacet multiClassFacet = FacetLibrary.getFacet(MultiClassFacet.class);
	private ArmorClassFacet armorClassFacet = FacetLibrary.getFacet(ArmorClassFacet.class);
	private SpellsFacet spellsFacet = FacetLibrary.getFacet(SpellsFacet.class);
	private ActiveSpellsFacet activeSpellsFacet = FacetLibrary.getFacet(ActiveSpellsFacet.class);
	private SpellListFacet spellListFacet = FacetLibrary.getFacet(SpellListFacet.class);
	private ClassSpellListFacet classSpellListFacet = FacetLibrary.getFacet(ClassSpellListFacet.class);

	private FormulaResolvingFacet resolveFacet = FacetLibrary.getFacet(FormulaResolvingFacet.class);
	private PrerequisiteFacet prereqFacet = FacetLibrary.getFacet(PrerequisiteFacet.class);
	private BonusCheckingFacet bonusFacet = FacetLibrary.getFacet(BonusCheckingFacet.class);
	private ObjectAdditionFacet additionFacet = FacetLibrary.getFacet(ObjectAdditionFacet.class);
	private AddLevelFacet addLevelFacet = FacetLibrary.getFacet(AddLevelFacet.class);
	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);
	private LevelInfoFacet levelInfoFacet = FacetLibrary.getFacet(LevelInfoFacet.class);

	// List of Note objects
	private final ArrayList<NoteItem> notesList = new ArrayList<NoteItem>();

	private final ArrayList<Equipment> primaryWeapons   = new ArrayList<Equipment>();
	private final ArrayList<Equipment> secondaryWeapons = new ArrayList<Equipment>();

	private ClassSource defaultDomainSource = null;

	private Map<String, Integer> autoEquipOutputOrderCache = new HashMap<String, Integer>();

	// Temporary Bonuses
	private List<Equipment> tempBonusItemList = new ArrayList<Equipment>();

	private String calcEquipSetId = "0.1"; //$NON-NLS-1$
	private String descriptionLst = "EMPTY"; //$NON-NLS-1$
	private String tabName = Constants.EMPTY_STRING;

	// whether to add auto known spells each level
	private boolean autoKnownSpells = true;

	// whether higher level spell slots should be used for lower levels
	private boolean useHigherKnownSlots =
			SettingsHandler.isUseHigherLevelSlotsDefault();
	private boolean useHigherPreppedSlots =
			SettingsHandler.isUseHigherLevelSlotsDefault();

	// should we also load companions on master load?
	private boolean autoLoadCompanion = false;

	// Should we sort the gear automatically?
	private boolean autoSortGear = true;

	// Should we resize the gear automatically?
	private boolean autoResize = SettingsHandler.getGearTab_AutoResize();

	// output sheet locations
	private String outputSheetHTML = Constants.EMPTY_STRING;
	private String outputSheetPDF = Constants.EMPTY_STRING;
	private boolean[] ageSetKitSelections = new boolean[Constants.NUMBER_OF_AGESET_KIT_SELECTIONS];
	private boolean dirtyFlag = false;
	private int serial = 0;
	private boolean displayUpdate = false;
	private boolean importing = false;

	// Should temp mods/bonuses be used/saved?
	private boolean useTempMods = true;

	// null is <none selected>
	private int costPool = 0;
	private int currentEquipSetNumber = 0;

	// order in which the equipment will be output.
	private int equipOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;

	// pool of stats allowed to distribute
	private int poolAmount = 0;

	// order in which the skills will be output.
	private int skillsOutputOrder = GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC;
	private int spellLevelTemp = 0;
	private VariableProcessor variableProcessor;

	// used by point buy. Total number of points for method, not points
	// remaining
	private int pointBuyPoints = -1;

	private boolean processLevelAbilities = true;

	// store most recent value of bonus to skill points
	private int modSkillPointsBuffer = (int) getStatBonusTo("MODSKILLPOINTS", "NUMBER");

	/**
	 * This map stores any user bonuses (entered through the GUI) to the
	 * corresponding ability pool.
	 */
	private Map<Category<Ability>, BigDecimal> theUserPoolBonuses = null;

	// A cache outside of the variable cache to hold the values that will not alter after 20th level.
	private Integer epicBAB = null;
	private HashMap<PCCheck, Integer> epicCheckMap = new HashMap<PCCheck, Integer>();

	// /////////////////////////////////////
	// operations
    
	/**
	 * Constructor.
	 */
	public PlayerCharacter()
	{
		this(true);
	}

	/**
	 * Constructor.
	 *
	 * @param load true if loading the character
	 */
	public PlayerCharacter(boolean load)
	{
		resolveFacet.associatePlayerCharacter(id, this);
		bonusFacet.associatePlayerCharacter(id, this);
		additionFacet.associatePlayerCharacter(id, this);
		prereqFacet.associatePlayerCharacter(id, this);
		addLevelFacet.associatePlayerCharacter(id, this);
		trackingFacet.associatePlayerCharacter(id, this);
		
		variableProcessor = new VariableProcessorPC(this);

		for (int i = 0; i < Constants.NUMBER_OF_AGESET_KIT_SELECTIONS; i++)
		{
			ageSetKitSelections[i] = false;
		}

		statFacet.addAll(id, Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class));
		checkFacet.addAll(id, Globals.getContext().ref.getOrderSortedCDOMObjects(PCCheck.class));
		bioSetFacet.set(id, Globals.getBioSet());
		campaignFacet.addAll(id, PersistenceManager.getInstance().getLoadedCampaigns());

		setRace(Globals.s_EMPTYRACE);
		setName(Constants.EMPTY_STRING);
		setFeats(0);
		rollStats(SettingsHandler.getGame().getRollMethod());
		addSpellBook(new SpellBook(Globals.getDefaultSpellBook(),
			SpellBook.TYPE_KNOWN_SPELLS));
		addSpellBook(new SpellBook(Globals.INNATE_SPELL_BOOK_NAME,
			SpellBook.TYPE_INNATE_SPELLS));
		populateSkills(SettingsHandler.getSkillsTab_IncludeSkills());
		setStringFor(StringKey.HANDED, PropertyFactory.getString("in_right")); //$NON-NLS-1$
		if (load)
		{
			insertBonusLanguageAbility();
		}
	}

	/**
	 * Adds a virtual ability that grants a bonus language to the character.
	 */
	public void insertBonusLanguageAbility()
	{
		Ability a = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Ability.class, AbilityCategory.LANGBONUS, "*LANGBONUS");
		setAssoc(a, AssociationKey.NEEDS_SAVING, true);
		grantedAbilityFacet.add(id, AbilityCategory.LANGBONUS, Nature.VIRTUAL, a, a);
	}

	/**
	 * Set the age.
	 * 
	 * @param i The character's age
	 */
	public void setAge(final int i)
	{
		ageFacet.set(id, i);
		setDirty(true);
		calcActiveBonuses();
	}

	/**
	 * Get the age.
	 * 
	 * @return age
	 */
	public int getAge()
	{
		return ageFacet.getAge(id);
	}

	/**
	 * Alignment of this PC.
	 * 
	 * @return alignment
	 */
	public PCAlignment getPCAlignment()
	{
		return alignmentFacet.get(id);
	}

	/**
	 * Retrieve those skills in the character's skill list that match the
	 * supplied visibility level.
	 * 
	 * @param vis
	 *            What level of visibility skills are desired.
	 * 
	 * @return A list of the character's skills matching the visibility
	 *         criteria.
	 */
	public List<Skill> getPartialSkillList(Visibility vis)
	{
		// Now select the required set of skills, based on their visibility.
		return Globals.getObjectsOfVisibility(skillFacet.getSet(id), vis);
	}

	/**
	 * Get the armor proficiency list.
	 * 
	 * @return armor proficiency list
	 */
	public Collection<ProfProvider<ArmorProf>> getArmorProfList()
	{
		return armorProfFacet.getQualifiedSet(id);
	}

	/**
	 * Returns the Spell Stat bonus for a class.
	 * 
	 * @param aClass the class to calculate the bonus for
	 * @return base spell stat bonus
	 */
	public int getBaseSpellStatBonus(final PCClass aClass)
	{
		if (aClass == null)
		{
			return 0;
		}

		int baseSpellStat = 0;
		PCStat ss = aClass.get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
				baseSpellStat = StatAnalysis.getTotalStatFor(this, ss);
				// final List<TypedBonus> bonuses = getBonusesTo("STAT",
				// "BASESPELLSTAT");
				// bonuses.addAll( getBonusesTo("STAT",
				// "BASESPELLSTAT;CLASS."+aClass.getKeyName()) );
				// bonuses.addAll( getBonusesTo("STAT", "CAST." + statString) );
				// baseSpellStat += TypedBonus.totalBonuses(bonuses);
				baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT");
				baseSpellStat +=
						(int) getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS."
							+ aClass.getKeyName());
				baseSpellStat +=
						(int) getTotalBonusTo("STAT", "CAST." + ss.getAbb());
				baseSpellStat =
						StatAnalysis.getModForNumber(this, baseSpellStat, ss);
		}
		return baseSpellStat;
	}

	/**
	 * Set the character's BIO.
	 * 
	 * @param bio the biography
	 */
	public void setBio(final String bio)
	{
		setStringFor(StringKey.BIO, bio);
	}

	/**
	 * Get the BIO.
	 * 
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(StringKey.BIO);
	}

	/**
	 * Set the birthday.
	 * 
	 * @param birthday the birthday to be set
	 */
	public void setBirthday(final String birthday)
	{
		setStringFor(StringKey.BIRTHDAY, birthday);
	}

	/**
	 * Get the birthday.
	 * 
	 * @return birthday
	 */
	public String getBirthday()
	{
		return getSafeStringFor(StringKey.BIRTHDAY);
	}

	/**
	 * Set the birthplace.
	 * 
	 * @param birthPlace the place of birth
	 */
	public void setBirthplace(final String birthPlace)
	{
		setStringFor(StringKey.BIRTHPLACE, birthPlace);
	}

	/**
	 * Get the birthplace.
	 * 
	 * @return birthplace
	 */
	public String getBirthplace()
	{
		return getSafeStringFor(StringKey.BIRTHPLACE);
	}

	/**
	 * Set the current EquipSet that is used to Bonus/Equip calculations.
	 * 
	 * @param eqSetId The equipSet to be used for Bonus Calculations and output
	 */
	public void setCalcEquipSetId(final String eqSetId)
	{
		calcEquipSetId = eqSetId;
		setDirty(true);
	}

	/**
	 * Get the id for the equipment set being used for calculation.
	 * 
	 * @return id
	 */
	public String getCalcEquipSetId()
	{
		if (equipSetFacet.isEmpty(id))
		{
			return calcEquipSetId;
		}

		if (getEquipSetByIdPath(calcEquipSetId) == null)
		{
			// PC does not have that equipset ID
			// so we need to find one they do have
			for (EquipSet eSet : equipSetFacet.getSet(id))
			{
				if (eSet.getParentIdPath().equals(Constants.EQUIP_SET_ROOT_ID))
				{
					calcEquipSetId = eSet.getIdPath();

					return calcEquipSetId;
				}
			}
		}

		return calcEquipSetId;
	}

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc).
	 */
	public void setCalcEquipmentList()
	{
		setCalcEquipmentList(false);
	}

	/**
	 * Set's current equipmentList to selected output EquipSet then loops
	 * through all the equipment and sets the correct status of each (equipped,
	 * carried, etc). Boolean parameter useTempBonuses controls whether or
	 * not the temporary bonuses associated with equipment are applied.
	 * 
	 * @param useTempBonuses whether to apply Temporary bonuses from equipment.
	 */
	public void setCalcEquipmentList(final boolean useTempBonuses)
	{
		// First we get the EquipSet that is going to be used
		// to calculate everything from
		final String calcId = getCalcEquipSetId();
		final EquipSet eSet = getEquipSetByIdPath(calcId);

		if (eSet == null)
		{
			Logging
				.debugPrint("No EquipSet has been selected for calculations yet."); //$NON-NLS-1$
			return;
		}

		// set PC's equipmentList to new one
		/*
		 * TODO This "global reset" directly followed by testing in the
		 * EquipSets and re-adding items as local equipment is something that
		 * needs to be cleaned up
		 */
		equipmentFacet.removeAll(id);

		// get all the PC's EquipSet's
		final List<EquipSet> pcEquipSetList = new ArrayList<EquipSet>(getEquipSet());

		if (pcEquipSetList.isEmpty())
		{
			equippedFacet.reset(id);
			return;
		}

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(pcEquipSetList);

		// loop through all the EquipSet's and create equipment
		// then set status to equipped and add to PC's equipment list
		for (EquipSet es : pcEquipSetList)
		{
			if (es.getItem() == null || !es.isPartOf(calcId))
			{
				continue;
			}

			es.equipItem(this);
			es.addNoteToItem();

			addLocalEquipment(es.getItem());
		}

		// loop through all equipment and make sure that
		// containers contents are updated
		for (Equipment eq : getEquipmentSet())
		{
			if (eq.isContainer())
			{
				eq.updateContainerContentsString(this);
			}

			// also make sure the masterList output order is
			// preserved as this equipmentList is a modified
			// clone of the original
			final Equipment anEquip = getEquipmentNamed(eq.getName());

			if (anEquip != null)
			{
				eq.setOutputIndex(anEquip.getOutputIndex());
			}
		}

		// if temporary bonuses, read the bonus equipList
		if (useTempBonuses)
		{
			for (Equipment eq : getTempBonusItemList())
			{
				// make sure that this EquipSet is the one
				// this temporary bonus item comes from
				// to make sure we keep them together
				final Equipment anEquip = getEquipmentNamed(eq.getName(), getEquipmentSet());

				if (anEquip == null)
				{
					continue;
				}

				eq.setQty(anEquip.getQty());
				eq.setNumberCarried(anEquip.getCarried());

				if (anEquip.isEquipped())
				{
					if (eq.isWeapon())
					{
						eq.put(IntegerKey.SLOTS, 0);
						eq.put(ObjectKey.CURRENT_COST, BigDecimal.ZERO);
						eq.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
						eq.setLocation(anEquip.getLocation());
					}
					else
					{
						// replace the orig item with the bonus item
						eq.setLocation(anEquip.getLocation());
						removeLocalEquipment(anEquip);
						anEquip.setIsEquipped(false, this);
						anEquip.setLocation(EquipmentLocation.NOT_CARRIED);
						anEquip.setNumberCarried(0f);
					}

					eq.setIsEquipped(true, this);
					eq.setNumberEquipped(1);
				}
				else
				{
					eq.put(ObjectKey.CURRENT_COST, BigDecimal.ZERO);
					eq.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
					eq.setLocation(EquipmentLocation.EQUIPPED_TEMPBONUS);
					eq.setIsEquipped(false, this);
				}

				// Adding this type to be correctly treated by Merge
				eq.addType(Type.TEMPORARY);
				addLocalEquipment(eq);
			}
		}

		// all done!
		equippedFacet.reset(id);
	}

	/**
	 * Apply the bonus from a follower to the master pc.
	 *
	 * TODO Although it's not obvious from this code, the aPC parameter passed here IS
	 * this pc, everywhere that this method is called. Refactor this to remove the aPC
	 * parameter. 
	 *
	 * @param aPC the pc to apply the bonus to
	 */
	public void setCalcFollowerBonus(final PlayerCharacter aPC)
	{
		setDirty(true);

		for (Follower aF : getFollowerList())
		{
			final CompanionList cList = aF.getType();
			final String rType = cList.getKeyName();
			final Race fRace = aF.getRace();

			for (CompanionMod cm : Globals.getCompanionMods(cList))
			{
				final String aType = cm.getType();
				if (aType.equalsIgnoreCase(rType) && cm.appliesToRace(fRace))
				{
					// Found race and type of follower
					// so add bonus to the master
					companionModFacet.add(id, cm);
					BonusActivation.activateBonuses(cm, aPC);
				}
			}
		}
	}

	/**
	 * Set the catchphrase.
	 * 
	 * @param phrase the catchphrase
	 */
	public void setCatchPhrase(final String phrase)
	{
		setStringFor(StringKey.CATCH_PHRASE, phrase);
	}

	/**
	 * Get the catchphrase.
	 * 
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(StringKey.CATCH_PHRASE);
	}

	/**
	 * Get a class, represented by a given key, from among those possessed by this pc.
	 * 
	 * @param key the class's key
	 * @return PCClass
	 */
	public PCClass getClassKeyed(final String key)
	{
		for (PCClass aClass : getClassSet())
		{
			if (aClass.getKeyName().equalsIgnoreCase(key))
			{
				return aClass;
			}
		}

		return null;
	}

	/**
	 * Get the class list.
	 * 
	 * @return classList
	 */
	public ArrayList<PCClass> getClassList()
	{
		/*
		 * TODO This is a discussion we have to have about where items are sorted
		 */
		return new ArrayList<PCClass>(getClassSet());
	}

	/**
	 * Gets the Set of PCClass objects for this Character.
	 * @return a set of PCClass objects
	 */
	public Set<PCClass> getClassSet()
	{
		return classFacet.getClassSet(id);
	}

	/**
	 * Set the cost pool, which is the number of points the character has spent. 
	 * 
	 * @param i the number of points spent
	 */
	public void setCostPool(final int i)
	{
		costPool = i;
	}

	/**
	 * Get the cost pool, which is the number of points the character has spent.
	 * 
	 * @return costPool
	 */
	public int getCostPool()
	{
		return costPool;
	}

	/**
	 * Get a list of types that apply to this character.
	 * 
	 * @return a List of Strings where each String is a type that the character
	 *         has. The list returned will never be null
	 * 
	 * @deprecated Use getRaceType() and getRacialSubTypes() instead
	 */
	@Deprecated
	public List<String> getTypes()
	{
		final List<String> list = new ArrayList<String>();

		Race race = getRace();
		if (race != null)
		{
			list.add(race.getType());
		}
		else
		{
			list.add("Humanoid");
		}

		for (PCTemplate t : templateFacet.getSet(id))
		{
			list.add(t.getType());
		}

		return list;
	}

	/**
	 * Get a pipe separated list of creature types for this PC (defaults to humanoid).
	 * @return the list of types
	 */
	@Deprecated
	public String getCritterType()
	{
		final StringBuffer critterType = new StringBuffer();

		// Not too sure about this if, but that's what the previous code
		// implied...
		Race race = getRace();
		if (race != null)
		{
			critterType.append(race.getType());
		}
		else
		{
			critterType.append("Humanoid");
		}

		for (PCTemplate t : templateFacet.getSet(id))
		{
			final String aType = t.getType();

			if (!"".equals(aType))
			{
				critterType.append('|').append(aType);
			}
		}

		return critterType.toString();
	}

	/**
	 * Returns a String with the characters Race Type (e.g. Humanoid).
	 * 
	 * @return The character's race type or &quot;None&quot;
	 */
	public String getRaceType()
	{
		RaceType rt = raceTypeFacet.getRaceType(id);
		return rt == null ? Constants.NONE : rt.toString();
	}

	/**
	 * Gets a <tt>Collection</tt> of racial subtypes for the character (e.g. Good).
	 * 
	 * @return A unmodifiable <tt>Collection</tt> of subtypes.
	 */
	public Collection<RaceSubType> getRacialSubTypes()
	{
		return subTypesFacet.getRacialSubTypes(id);
	}

	/**
	 * Set the current equipment set name.
	 * 
	 * @param aName the name of the new current equipment set
	 */
	public void setCurrentEquipSetName(final String aName)
	{
		setStringFor(StringKey.CURRENT_EQUIP_SET_NAME, aName);
	}

	/**
	 * Get the current equipment set name.
	 * 
	 * @return equipment set name
	 */
	public String getCurrentEquipSetName()
	{
		return getSafeStringFor(StringKey.CURRENT_EQUIP_SET_NAME);
	}

	/**
	 * Get the deity.
	 * 
	 * @return deity
	 */
	public Deity getDeity()
	{
		return deityFacet.get(id);
	}

	/**
	 * Set the description.
	 * 
	 * @param aString the description to set
	 */
	public void setDescription(final String aString)
	{
		setStringFor(StringKey.DESCRIPTION, aString);
	}

	/**
	 * Get the description.
	 * 
	 * @return description
	 */
	public String getDescription()
	{
		return getSafeStringFor(StringKey.DESCRIPTION);
	}

	/**
	 * Selector.
	 * 
	 * @return description lst
	 */
	public String getDescriptionLst()
	{
		return descriptionLst;
	}

	/**
	 * Sets the character changed since last save.
	 * 
	 * @param dirtyState the new "dirty" value (may be false to indicate no change)
	 */
	public void setDirty(final boolean dirtyState)
	{
		if (dirtyState)
		{
			serial++;
			cache = new ObjectCache();
			getVariableProcessor().setSerial(serial);
			cabFacet.update(id);
		}

		// TODO - This is kind of strange. We probably either only want to
		// notify our observers if we have gone from not dirty to dirty and not
		// the reverse case. At a minimum we should probably tell them the
		// state anyway.
		if (dirtyFlag != dirtyState)
		{
			dirtyFlag = dirtyState;

			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets whether the character has been changed since last saved.
	 * 
	 * @return true if dirty
	 */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/**
	 * Returns the serial for the instance - every time something changes the
	 * serial is incremented. Use to detect change in PlayerCharacter.
	 * 
	 * @return serial
	 */
	public int getSerial()
	{
		return serial;
	}

	/**
	 * @return display name
	 */
	public String getDisplayName()
	{
		final String custom = getTabName();

		if (!Constants.EMPTY_STRING.equals(custom))
		{
			return custom;
		}

		final StringBuffer displayName = new StringBuffer().append(getName());

		// TODO - i18n
		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				break;

			case Constants.DISPLAY_STYLE_NAME_CLASS:
				displayName.append(" the ").append(getDisplayClassName());

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE:
				displayName.append(" the ").append(getDisplayRaceName());

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				displayName.append(" the ").append(getDisplayRaceName())
					.append(' ').append(getDisplayClassName());

				break;

			case Constants.DISPLAY_STYLE_NAME_FULL:
				return getFullDisplayName();

			default:
				break; // custom broken
		}

		return displayName.toString();
	}

	/**
	 * set display update TODO - This probably doesn't belong here. It seems to
	 * only be used by InfoSkills.
	 * 
	 * @param update Whether the display should be updated
	 */
	public void setDisplayUpdate(final boolean update)
	{
		this.displayUpdate = update;
	}

	/**
	 * is display update.
	 * 
	 * @return True if display update
	 */
	public boolean isDisplayUpdate()
	{
		return displayUpdate;
	}

	/**
	 * Get the list of equipment sets.
	 * 
	 * @return List
	 */
	public Collection<EquipSet> getEquipSet()
	{
		return equipSetFacet.getSet(id);
	}

	/**
	 * Get the equipment set indexed by path.
	 * 
	 * @param path the "path" of the equipSet to return
	 * @return EquipSet
	 */
	public EquipSet getEquipSetByIdPath(final String path)
	{
		return equipSetFacet.getEquipSetByIdPath(id, path);
	}

	/**
	 * Get the equipment set by name.
	 * 
	 * @param aName the name of the equipSet to retrieve
	 * @return an Equip set
	 */
	public EquipSet getEquipSetByName(final String aName)
	{
		return equipSetFacet.getEquipSetByName(id, aName);
	}

	/**
	 * Set the number of the current equipSet.
	 * 
	 * @param anInt the new value for current equipSet index
	 */
	public void setEquipSetNumber(final int anInt)
	{
		currentEquipSetNumber = anInt;
		setDirty(true);
	}

	/**
	 * Get the current equipment set number.
	 * 
	 * @return equipSet number
	 */
	public int getEquipSetNumber()
	{
		return currentEquipSetNumber;
	}

	/**
	 * gets the total weight in an EquipSet.
	 * 
	 * @param idPath The root of the equipment set (or subset)
	 * @return equipment set weight
	 */
	public double getEquipSetWeightDouble(final String idPath)
	{
		if (equipSetFacet.isEmpty(id))
		{
			return 0.0;
		}

		double totalWeight = 0.0;

		for (EquipSet es : equipSetFacet.getSet(id))
		{
			final String abIdPath = idPath + Constants.EQUIP_SET_PATH_SEPARATOR;
			final String esIdPath = es.getIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;

			if (!esIdPath.startsWith(abIdPath))
			{
				continue;
			}

			final Equipment eqI = es.getItem();

			if (eqI != null)
			{
				if ((eqI.getCarried() > 0.0f) && (eqI.getParent() == null))
				{
					if (eqI.getChildCount() > 0)
					{
						totalWeight +=
							eqI.getWeightAsDouble(this) + eqI.getContainedWeight(this);
					}
					else
					{
						totalWeight += eqI.getWeightAsDouble(this) * eqI.getCarried();
					}
				}
			}
		}

		return totalWeight;
	}

	/**
	 * Count the total number of items of aName within EquipSet idPath.
	 * 
	 * @param idPath The root of the equipment set (or subset)
	 * @param aName The equipment to count
	 * @return equipment set count
	 */
	public Float getEquipSetCount(final String idPath, final String aName)
	{
		return equipSetFacet.getEquipSetCount(id, idPath, aName);
	}

	/**
	 * Get equipment set.
	 * 
	 * @return equipment set
	 */
	public Set<Equipment> getEquipmentSet()
	{
		return equipmentFacet.getSet(id);
	}

	/**
	 * Get the character's "equipped" equipment.
	 * @return a set of the "equipped" equipment
	 */
	public Set<Equipment> getEquippedEquipmentSet()
	{
		return equippedFacet.getSet(id);
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List<Equipment> getEquipmentListInOutputOrder()
	{
		return sortEquipmentList(getEquipmentSet(), Constants.MERGE_ALL);
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * Deals with merge as well.  See the Constants package for acceptable values
	 * of merge .
	 * 
	 * @param merge controls how much merging is done.
	 * 
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List<Equipment> getEquipmentListInOutputOrder(final int merge)
	{
		return sortEquipmentList(getEquipmentSet(), merge);
	}

	/**
	 * Get the master list of equipment.
	 * 
	 * @return equipment master list
	 */
	public List<Equipment> getEquipmentMasterList()
	{
		Set<Equipment> set = userEquipmentFacet.getSet(id);
		final List<Equipment> aList = new ArrayList<Equipment>(set);
		aList.addAll(autoListEquipmentFacet.getSet(id));
		aList.addAll(autoEquipFacet.getAutoEquipment(id));
		return aList;
	}

	/**
	 * Get equipment master list in output order.
	 * 
	 * @return equipment master list in output order
	 */
	public List<Equipment> getEquipmentMasterListInOutputOrder()
	{
		final List<Equipment> l = getEquipmentMasterList();
		Collections.sort(l, CoreUtility.equipmentComparator);
		return l;
	}

	/**
	 * Search for a piece of equipment in the specified list by name.
	 * 
	 * TODO - This does not belong in PlayerCharacter. Move to Equipment if
	 * needed.
	 * 
	 * TODO - This probably won't work with i18n. Should always search by key.
	 * 
	 * @param aString
	 *            The name of the equipment.
	 * @param aList
	 *            The Collection of equipment to search in.
	 * 
	 * @return The <tt>Equipment</tt> object or <tt>null</tt>
	 */
	private Equipment getEquipmentNamed(
		final String aString,
		final Collection<Equipment> aList)
	{
		Equipment match = null;

		for (Equipment eq : aList)
		{
			if (aString.equalsIgnoreCase(eq.getName()))
			{
				match = eq;
			}
		}

		return match;
	}

	/**
	 * Search among the PCs equipment for a named piece of equipment.
	 * @param name The name of the piece of equipment.
	 * @return null or the equipment named.
	 */
	public Equipment getEquipmentNamed(final String name)
	{
		return getEquipmentNamed(name, getEquipmentMasterList());
	}

	/**
	 * Set the characters eye colour.
	 * 
	 * @param aString
	 *            the colour of their eyes
	 */
	public void setEyeColor(final String aString)
	{
		setStringFor(StringKey.EYE_COLOR, aString);
	}

	/**
	 * Get the characters eye colour.
	 * 
	 * @return the colour of their eyes
	 */
	public String getEyeColor()
	{
		return getSafeStringFor(StringKey.EYE_COLOR);
	}

	/**
	 * Get a number that represents the number of feats added to this character
	 * by BONUS statements.
	 * 
	 * @return the number of feats added by bonus statements
	 */
	private double getBonusFeatPool()
	{
		String aString = Globals.getBonusFeatString();

		final StringTokenizer aTok =
				new StringTokenizer(aString, Constants.PIPE, false);
		final int startLevel = Integer.parseInt(aTok.nextToken());
		final int rangeLevel = Integer.parseInt(aTok.nextToken());

		double pool = getTotalBonusTo("FEAT", "POOL");
		double pcpool = getTotalBonusTo("FEAT", "PCPOOL");
		double mpool = getTotalBonusTo("FEAT", "MONSTERPOOL");
		double bonus = getTotalBonusTo("ABILITYPOOL", "FEAT");
		double classLvlBonus = getNumFeatsFromLevels();

		Logging.debugPrint(""); //$NON-NLS-1$
		Logging.debugPrint("=============="); //$NON-NLS-1$
		Logging.debugPrint("level " + this.totalNonMonsterLevels()); //$NON-NLS-1$

		Logging.debugPrint("POOL:   " + pool); //$NON-NLS-1$
		Logging.debugPrint("PCPOOL: " + pcpool); //$NON-NLS-1$
		Logging.debugPrint("MPOOL:  " + mpool); //$NON-NLS-1$
		Logging.debugPrint("APOOL:  " + bonus); //$NON-NLS-1$
		Logging.debugPrint("LVLBONUS:  " + classLvlBonus); //$NON-NLS-1$

		double startAdjust = rangeLevel == 0 ? 0 : startLevel / rangeLevel;

		double nonMonsterAdjustment =
			this.totalNonMonsterLevels() >= startLevel
				? 1.0d + pcpool - startAdjust
				: pcpool;

		pool += CoreUtility.epsilonFloor(nonMonsterAdjustment);
		pool += CoreUtility.epsilonFloor(mpool);
		pool += CoreUtility.epsilonFloor(bonus);
		pool += CoreUtility.epsilonFloor(classLvlBonus);

		Logging.debugPrint(""); //$NON-NLS-1$
		Logging.debugPrint("Total Bonus: " + pool); //$NON-NLS-1$
		Logging.debugPrint("=============="); //$NON-NLS-1$
		Logging.debugPrint(""); //$NON-NLS-1$

		return pool;
	}

	/**
	 * Calculates the number of feats that should be granted as a result of LEVELPERFEAT 
	 * entries in classes that the character has levels in. Stacking rules based on 
	 * LEVELTYPE are applied as part of this calculation. 
	 * 
	 * @return the number of feats granted
	 */
	double getNumFeatsFromLevels()
	{
		Map<String, Double> featByLevelType = new HashMap<String, Double>();
		for (PCClass pcClass : getClassSet())
		{
			int lvlPerFeat = pcClass.getSafe(IntegerKey.LEVELS_PER_FEAT);
			if (lvlPerFeat != 0)
			{
				double bonus = (double) getLevel(pcClass) / lvlPerFeat;
				Double existing = featByLevelType.get(pcClass.get(StringKey.LEVEL_TYPE));
				if (existing == null)
				{
					existing = 0d;
				}
				existing += bonus;
				featByLevelType.put(pcClass.get(StringKey.LEVEL_TYPE), existing);
			}
		}

		double bonus = 0d;
		for (String lvlType : featByLevelType.keySet())
		{
			Double existing = featByLevelType.get(lvlType);
			bonus += CoreUtility.epsilonFloor(existing);
		}
		return bonus;
	}

	/**
	 * Checks whether a PC is allowed to level up. A PC is not allowed to level
	 * up if the "Enforce Spending" option is set and he still has unallocated
	 * skill points and/or feat slots remaining. This can be used to enforce
	 * correct spending of these resources when creating high-level multiclass
	 * characters.
	 * 
	 * @return true if the PC can level up
	 */
	public boolean canLevelUp()
	{
		return !SettingsHandler.getEnforceSpendingBeforeLevelUp()
			|| (getSkillPoints() <= 0 && getRemainingFeatPoolPoints() <= 0);
	}

	/**
	 * Sets the filename of the character.
	 * 
	 * @param newFileName the name of the file this character will be saved in
	 */
	public void setFileName(final String newFileName)
	{
		setStringFor(StringKey.FILE_NAME, newFileName);
	}

	/**
	 * Gets the filename of the character.
	 * 
	 * @return file name of character
	 */
	public String getFileName()
	{
		return getSafeStringFor(StringKey.FILE_NAME);
	}

	/**
	 * Returns the followers associated with this character.
	 * 
	 * @return A <tt>Set</tt> of <tt>Follower</tt> objects.
	 */
	public Collection<Follower> getFollowerList()
	{
		return followerFacet.getSet(id);
	}

	/**
	 * Returns a very descriptive name for the character.
	 * 
	 * The format is [name] the [level]th level [race name] [classes]
	 * 
	 * @return A descriptive string name for the character.
	 */
	public String getFullDisplayName()
	{
		final int levels = getTotalLevels();
		final String displayClass;

		// If you aren't multi-classed, don't display redundant class level
		// information in addition to the total PC level
		displayClass = classFacet.getCount(id) > 1
			? getFullDisplayClassName()
			: getDisplayClassName();

		return new StringBuffer()
			.append(getName())
			.append(" the ")
			.append(levels)
			.append(getOrdinal(levels))
			.append(" level ")
			.append(getDisplayRaceName())
			.append(' ')
			.append(displayClass).toString();
	}

	/**
	 * Returns a region (including subregion) string for the character.
	 * 
	 * <p/> Build on-the-fly so removing templates won't mess up region
	 * 
	 * @return character region
	 */
	public String getFullRegion()
	{
		return regionFacet.getFullRegion(id);
	}

	/**
	 * Sets the character's gender.
	 * 
	 * <p>
	 * The gender will only be changed if the character does not have a template
	 * that locks the character's gender.
	 * 
	 * @param g
	 *            A gender to try and set.
	 */
	public void setGender(final Gender g)
	{
		genderFacet.setGender(id, g);
		setDirty(true);
	}

	/**
	 * Returns a string for the character's gender.
	 * 
	 * <p>
	 * This method will return the stored gender or the template locked gender
	 * if there is one. This means the <tt>setGender()</tt> side effect is not
	 * really required.
	 * 
	 * @return A <tt>String</tt> version of the character's gender. TODO -
	 *         Gender should be an object so it can be i18n.
	 */
	public Gender getGenderObject()
	{
		return genderFacet.getGender(id);
	}

	/**
	 * Checks if the user is allowed to change the character's gender.
	 * 
	 * <p>
	 * That is, if no template with a gender lock has been specified.
	 * 
	 * @return <tt>true</tt> if the user can freely set the character's
	 *         gender.
	 */
	public boolean canSetGender()
	{
		return genderFacet.canSetGender(id);
	}

	/**
	 * Sets the character's wealth.
	 * 
	 * <p>
	 * Gold here is used as a character's total purchase power not actual gold
	 * pieces.
	 * 
	 * @param aString
	 *            A String gold amount. TODO - Do this parsing elsewhere.
	 */
	public void setGold(final String aString)
	{
		moneyFacet.setGold(id, new BigDecimal(aString));
		setDirty(true);
	}

	/**
	 * Returns the character's total wealth.
	 * 
	 * @see pcgen.core.PlayerCharacter#setGold(String)
	 * 
	 * @return A <tt>BigDecimal</tt> value for the character's wealth.
	 */
	public BigDecimal getGold()
	{
		return moneyFacet.getGold(id);
	}

	/**
	 * Sets the character's hair color as a string.
	 * 
	 * @param aString
	 *            The hair color to set.
	 */
	public void setHairColor(final String aString)
	{
		setStringFor(StringKey.HAIR_COLOR, aString);
	}

	/**
	 * Gets the character's hair color.
	 * 
	 * @return A hair color string.
	 */
	public String getHairColor()
	{
		return getSafeStringFor(StringKey.HAIR_COLOR);
	}

	/**
	 * Sets the character's hair style.
	 * 
	 * @param aString
	 *            A hair style.
	 */
	public void setHairStyle(final String aString)
	{
		setStringFor(StringKey.HAIR_STYLE, aString);
	}

	/**
	 * Gets the character's hair style.
	 * 
	 * @return The character's hair style.
	 */
	public String getHairStyle()
	{
		return getSafeStringFor(StringKey.HAIR_STYLE);
	}

	/**
	 * Sets the character's handedness.
	 * 
	 * @param aString
	 *            A String to use as a handedness.
	 * 
	 * TODO - This should probably be an object as some systems may use the
	 * information.
	 */
	public void setHanded(final String aString)
	{
		setStringFor(StringKey.HANDED, aString);
	}

	/**
	 * Returns the character's handedness string.
	 * 
	 * @return A String for handedness.
	 */
	public String getHanded()
	{
		return getSafeStringFor(StringKey.HANDED);
	}

	/**
	 * Sets the character's height in inches.
	 * 
	 * @param i
	 *            A height in inches.
	 * 
	 * TODO - This should be a double value stored in CM
	 */
	public void setHeight(final int i)
	{
		heightFacet.setHeight(id, i);
		setDirty(true);
	}

	/**
	 * Gets the character's height in inches.
	 * 
	 * @return The character's height in inches.
	 */
	public int getHeight()
	{
		return heightFacet.getHeight(id);
	}

	/**
	 * Marks the character as being in the process of being loaded.
	 * 
	 * <p>
	 * This information is used to prevent the system from trying to calculate
	 * values on partial information or values that should be set from the saved
	 * character.
	 * 
	 * <p>
	 * TODO - This is pretty dangerous.
	 * 
	 * @param newIsImporting
	 *            <tt>true</tt> to mark the character as being imported.
	 */
	public void setImporting(final boolean newIsImporting)
	{
		this.importing = newIsImporting;
	}

	/**
	 * Sets the character's interests.
	 * 
	 * @param aString
	 *            A string of interests for the character.
	 */
	public void setInterests(final String aString)
	{
		setStringFor(StringKey.INTERESTS, aString);
	}

	/**
	 * Gets a string of interests for the character.
	 * 
	 * @return A String of interests or an empty string.
	 */
	public String getInterests()
	{
		return getSafeStringFor(StringKey.INTERESTS);
	}

	/**
	 * Gets the character's list of languages.
	 * 
	 * @return An unmodifiable language set.
	 */
	public Set<Language> getLanguageSet()
	{
		return languageFacet.getSet(id);
	}

	/*
	 * TODO This is a discussion we have to have about where items are sorted
	 */

	/**
	 * Get a sorted list of the languages that this character knows.
	 * @return a sorted list of language objects
	 */
	public Set<Language> getSortedLanguageSet()
	{
		return new TreeSet<Language>(languageFacet.getSet(id));
	}

	/**
	 * Sets the character's location.
	 * 
	 * @param aString
	 *            A location.
	 */
	public void setLocation(final String aString)
	{
		setStringFor(StringKey.LOCATION, aString);
	}

	/**
	 * Gets the character's location.
	 * 
	 * @return The character's location.
	 */
	public String getLocation()
	{
		return getSafeStringFor(StringKey.LOCATION);
	}

	/**
	 * This method returns the effective level of this character for purposes of
	 * applying companion mods to a companion of the specified type.
	 * <p>
	 * <b>Note</b>: This whole structure is kind of messed up since nothing
	 * enforces that a companion mod of a given type always looks at the same
	 * variable (either Class or Variable).  Note it seems that this used to
	 * be driven off types but now it's driven from a list of companion mods
	 * but the java doc has not been updated.
	 * 
	 * @param compList
	 *            A list of companionMods to get level for
	 * @return The effective level for this companion type
	 */
	public int getEffectiveCompanionLevel(final CompanionList compList)
	{
		final Collection<CompanionMod> mods = Globals.getCompanionMods(compList);

		for (CompanionMod cMod : mods)
		{
			Map<String, Integer> varmap = cMod.getMapFor(MapKey.APPLIED_VARIABLE);

			for (final String varName : varmap.keySet())
			{
				final int lvl = this.getVariableValue(varName, Constants.EMPTY_STRING).intValue();
				if (lvl > 0)
				{
					return lvl;
				}
			}

			Map<CDOMSingleRef<? extends PCClass>, Integer> ac = cMod.getMapFor(MapKey.APPLIED_CLASS);

			for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : ac.entrySet())
			{
				PCClass pcclass = me.getKey().resolvesTo();
				String key = pcclass.getKeyName();
				int lvl = getLevel(getClassKeyed(key));
				if (lvl > 0)
				{
					return lvl;
				}
			}
		}
		return 0;
	}

	/**
	 * Set the master for this object also set the level dependent stats based
	 * on the masters level and info contained in the companionModList Array
	 * such as HitDie, SR, BONUS, SA, etc.
	 * 
	 * @param aM
	 *            The master to be set.
	 */
	public void setMaster(final Follower aM)
	{
		masterFacet.set(id, aM);

		final PlayerCharacter mPC = getMasterPC();

		if (mPC == null)
		{
			return;
		}

		// make sure masters Name and fileName are correct
		if (!aM.getFileName().equals(mPC.getFileName()))
		{
			aM.setFileName(mPC.getFileName());
			setDirty(true);
		}

		if (!aM.getName().equals(mPC.getName()))
		{
			aM.setName(mPC.getName());
			setDirty(true);
		}

		// Get total wizard + sorcerer levels as they stack like a mother
		int mTotalLevel = 0;
		int addHD = 0;

		for (PCClass mClass : mPC.getClassSet())
		{
			boolean found = false;

			for (CompanionMod cMod : Globals.getCompanionMods(aM.getType()))
			{
				if ((cMod.getLevelApplied(mClass) > 0) && !found)
				{
					mTotalLevel += getLevel(mClass);
					found = true;
				}
			}
		}

		List<CompanionMod> newCompanionMods = new ArrayList<CompanionMod>();

		// Clear the companionModList so we can add everything to it
		Collection<CompanionMod> oldCompanionMods = companionModFacet.removeAll(id);

		for (CompanionMod cMod : Globals.getCompanionMods(aM.getType()))
		{
			// Check all the masters classes
			for (PCClass mClass : mPC.getClassSet())
			{
				final int mLev = getLevel(mClass) + aM.getAdjustment();
				final int compLev = cMod.getLevelApplied(mClass);

				if (compLev < 0)
				{
					continue;
				}

				// This CompanionMod must be for this Class
				// and for the correct level or lower
				if ((compLev <= mLev) || (compLev <= mTotalLevel))
				{
					if (cMod.qualifies(this, cMod))
					{
						if (!oldCompanionMods.contains(cMod))
						{
							newCompanionMods.add(cMod);
						}
						companionModFacet.add(id, cMod);
						addHD += cMod.getSafe(IntegerKey.HIT_DIE);
					}
				}
			}
			Map<String, Integer> varmap =
					cMod.getMapFor(MapKey.APPLIED_VARIABLE);
			for (String varName : varmap.keySet())
			{
				final int mLev =
						mPC.getVariableValue(varName, Constants.EMPTY_STRING)
							.intValue()
							+ aM.getAdjustment();

				if (mLev >= cMod.getVariableApplied(varName))
				{
					if (cMod.qualifies(this, cMod))
					{
						if (!oldCompanionMods.contains(cMod))
						{
							newCompanionMods.add(cMod);
						}
						companionModFacet.add(id, cMod);
						addHD += cMod.getSafe(IntegerKey.HIT_DIE);
					}
				}
			}
		}

		// Add additional HD if required
		LevelCommandFactory lcf = getRace().get(ObjectKey.MONSTER_CLASS);

		final int usedHD = aM.getUsedHD();
		addHD -= usedHD;

		// if ((newClass != null) && (addHD != 0))
		if ((lcf != null) && (addHD != 0))
		{
			// set the new HD (but only do it once!)
			incrementClassLevel(addHD, lcf.getPCClass(), true);
			aM.setUsedHD(addHD + usedHD);
			setDirty(true);
		}

		// If it's a familiar, we need to change it's Skills
		if (getUseMasterSkill())
		{
			final Collection<Skill> mList = mPC.getSkillSet();
			final List<Skill> sKeyList = new ArrayList<Skill>();

			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			refreshSkillList();
			for (Skill fSkill : getSkillSet())
			{
				for (Skill mSkill : mList)
				{
					// first check to see if familiar
					// already has ranks in the skill
					if (mSkill.equals(fSkill))
					{
						// need higher rank of the two
						if (SkillRankControl.getRank(mPC, mSkill).intValue() > SkillRankControl
							.getRank(this, fSkill).intValue())
						{
							// first zero current
							SkillRankControl.setZeroRanks(lcf == null ? null
								: lcf.getPCClass(), this, fSkill);
							// We don't pass in a class here so that the real
							// skills can be distinguished from the ones from
							// the master.
							SkillRankControl.modRanks(SkillRankControl.getRank(
								mPC, mSkill).doubleValue(), null, true, this,
								fSkill);
						}
					}

					// build a list of all skills a master
					// Possesses, but the familiar does not
					if (!hasSkill(mSkill)
						&& !sKeyList.contains(mSkill))
					{
						sKeyList.add(mSkill);
					}
				}
			}

			// now add all the skills only the master has
			for (Skill newSkill : sKeyList)
			{
				// familiar doesn't have skill,
				// but master does, so add it
				final double sr = SkillRankControl.getRank(mPC, newSkill)
						.doubleValue();

				if (ChooseActivation.hasChooseToken(newSkill))
				{
					continue;
				}

				// We don't pass in a class here so that the real skills can be
				// distinguished from the ones form the master.
				SkillRankControl.modRanks(sr, null, true, this, newSkill);
				skillFacet.add(id, newSkill);
			}
		}

		// Setup the default EquipSet if not already present
		if (!hasEquipSet())
		{
			String idPath = getNewIdPath(null);
			EquipSet eSet =
					new EquipSet(idPath, PropertyFactory.getString("in_ieDefault"));
			addEquipSet(eSet);
		}

		oldCompanionMods.removeAll(companionModFacet.getSet(id));
		for (CompanionMod cMod : oldCompanionMods)
		{
			CDOMObjectUtilities.removeAdds(cMod, this);
			CDOMObjectUtilities.restoreRemovals(cMod, this);
		}

		for (CompanionMod cMod : newCompanionMods)
		{
			CDOMObjectUtilities.addAdds(cMod, this);
			CDOMObjectUtilities.checkRemovals(cMod, this);

			for (CDOMReference<PCTemplate> ref : cMod
				.getSafeListFor(ListKey.TEMPLATE))
			{
				for (PCTemplate pct : ref.getContainedObjects())
				{
					addTemplate(pct);
				}
			}
			for (CDOMReference<PCTemplate> ref : cMod
				.getSafeListFor(ListKey.REMOVE_TEMPLATES))
			{
				for (PCTemplate pct : ref.getContainedObjects())
				{
					removeTemplate(pct);
				}
			}

			for (TransitionChoice<Kit> kit : cMod
				.getSafeListFor(ListKey.KIT_CHOICE))
			{
				kit.act(kit.driveChoice(this), cMod, this);
			}
		}
		setDirty(true);
	}

	/**
	 * Returns the maximum number of followers this character can have from
	 * the given companion list. This method does not adjust for any followers
	 * already selected by the character.
	 * 
	 * @param cList
	 *            A list of potential follower races
	 * @return The max number of followers -1 for any number
	 */
	public int getMaxFollowers(CompanionList cList)
	{
		int ret = followerLimitFacet.getMaxFollowers(id, cList);
		return (ret == -1) ? getOldFollowerLimit(cList) : ret;
	}

	private int getOldFollowerLimit(CompanionList cList)
	{
		// Old way of handling this
		// If the character qualifies for any companion mod of this type
		// they can take unlimited number of them.
		for (CompanionMod cMod : Globals.getCompanionMods(cList))
		{
			Map<String, Integer> varmap =
					cMod.getMapFor(MapKey.APPLIED_VARIABLE);
			for (String varName : varmap.keySet())
			{
				if (this.getVariableValue(varName, Constants.EMPTY_STRING)
					.intValue() > 0)
				{
					return -1;
				}
			}
			Map<CDOMSingleRef<? extends PCClass>, Integer> ac =
					cMod.getMapFor(MapKey.APPLIED_CLASS);
			for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : ac
				.entrySet())
			{
				PCClass pcclass = me.getKey().resolvesTo();
				String key = pcclass.getKeyName();
				for (PCClass pcClass : getClassSet())
				{
					if (pcClass.getKeyName().equals(key))
					{
						return me.getValue();
					}
				}
			}
		}

		return 0;
	}

	/**
	 * Gets the list of potential followers of a given type.
	 * 
	 * @param aType
	 *            Type of follower to retrieve list for e.g. Familiar
	 * @param comp
	 *            the comparator that will be used to order the returned map.
     * @return A MAP of FollowerOption objects representing the possible list
	 *         of follower choices.
	 */
	public Map<FollowerOption, CDOMObject> getAvailableFollowers(
		final String aType,
		Comparator<FollowerOption> comp)
	{
		return foFacet.getAvailableFollowers(id, aType, comp);
	}

	/**
	 * Get the Follower object that is the "master" for this object.
	 * 
	 * @return follower master
	 */
	public Follower getMaster()
	{
		return masterFacet.get(id);
	}

	/**
	 * Get the PlayerCharacter that is the "master" for this object.
	 * 
	 * @return master PC
	 */
	public PlayerCharacter getMasterPC()
	{
		Follower followerMaster = getMaster();
		if (followerMaster == null)
		{
			return null;
		}

		for (PlayerCharacter nPC : Globals.getPCList())
		{
			if (followerMaster.getFileName().equals(nPC.getFileName()))
			{
				return nPC;
			}
		}

		// could not find a filename match, let's try the Name
		for (PlayerCharacter nPC : Globals.getPCList())
		{
			if (followerMaster.getName().equals(nPC.getName()))
			{
				return nPC;
			}
		}

		// no Name and no FileName match, so must not be loaded
		return null;
	}

	/**
	 * Sets the character's name.
	 * 
	 * @param aString
	 *            A name to set.
	 */
	public void setName(final String aString)
	{
		setStringFor(StringKey.NAME, aString);
	}

	/**
	 * Gets the character's name.
	 * 
	 * @return The name
	 */
	public String getName()
	{
		return getSafeStringFor(StringKey.NAME);
	}

	/**
	 * Takes all the Temporary Bonuses and Merges them into just the unique
	 * named bonuses.
	 * 
	 * @return List of Strings
	 */
	public List<String> getNamedTempBonusList()
	{
		return bonusManager.getNamedTempBonusList();
	}

	/**
	 * Takes all the Temporary Bonuses and Merges them into just the unique 
	 * named bonuses.
	 *
	 * @return    List of Strings
	 */
	public List<String> getNamedTempBonusDescList()
	{
		return bonusManager.getNamedTempBonusDescList();
	}

	/**
	 * @return nonProficiencyPenalty. Searches templates first.
	 */
	public int getNonProficiencyPenalty()
	{
		return nonppFacet.getPenalty(id);
	}

	/**
	 * Gets a list of notes associated with the character.
	 * 
	 * @return A list of <tt>NoteItem</tt> objects.
	 */
	public ArrayList<NoteItem> getNotesList()
	{
		return notesList;
	}

	/**
	 * Sets a string of phobias for the character.
	 * 
	 * @param aString
	 *            A string to set.
	 */
	public void setPhobias(final String aString)
	{
		setStringFor(StringKey.PHOBIAS, aString);
	}

	/**
	 * Gets the phobia string for the character.
	 * 
	 * @return A phobia string.
	 */
	public String getPhobias()
	{
		return getSafeStringFor(StringKey.PHOBIAS);
	}

	/**
	 * Sets the name of the player for this character.
	 * 
	 * @param aString
	 *            A name to set.
	 */
	public void setPlayersName(final String aString)
	{
		setStringFor(StringKey.PLAYERS_NAME, aString);
	}

	/**
	 * Gets the name of the player for this character.
	 * 
	 * @return The player's name.
	 */
	public String getPlayersName()
	{
		return getSafeStringFor(StringKey.PLAYERS_NAME);
	}

    /**
     * Set the value of the feat pool.
     * @param pool value to set the feat pool to
     */
	public void setPoolAmount(final int pool)
	{
		poolAmount = pool;
	}

    /**
     * Get the value of the feat pool.
     * @return the feat pool amount
     */
	public int getPoolAmount()
	{
		return poolAmount;
	}

	/**
	 * Selector Sets the path to the portrait of the character.
	 * 
	 * @param newPortraitPath
	 *            the path to the portrait file
	 */
	public void setPortraitPath(final String newPortraitPath)
	{
		setStringFor(StringKey.PORTRAIT_PATH, newPortraitPath);
	}

	/**
	 * Selector Gets the path to the portrait of the character.
	 * 
	 * @return the path to the portrait file
	 */
	public String getPortraitPath()
	{
		return getSafeStringFor(StringKey.PORTRAIT_PATH);
	}

	/**
	 * Selector gets the character's primary weapons.
	 * 
	 * @return primary weapons
	 */
	public List<Equipment> getPrimaryWeapons()
	{
		return primaryWeapons;
	}

	/**
	 * Get the character's race.
	 * 
	 * @return the character's race
	 */
	public Race getRace()
	{
		return raceFacet.get(id);
	}

	/**
	 * Set the character's region.
	 * 
	 * @param region the character's region
	 */
	public void setRegion(final String region)
	{
		Region r = Region.getConstant(region);
		regionFacet.setRegion(id, r);
	}

	/**
	 * Set the character's sub region.
	 * 
	 * @param aString the character's sub region
	 */
	public void setSubRegion(final String aString)
	{
		SubRegion subregion = SubRegion.getConstant(aString);
		regionFacet.setSubRegion(id, subregion);
	}

	/**
	 * Selector build on-the-fly so removing templates won't mess up region.
	 * 
	 * @return character region
	 */
	public String getRegionString()
	{
		return regionFacet.getRegion(id);
	}

	/**
	 * Set the character's residence.
	 * 
	 * @param aString the character's residence
	 */
	public void setResidence(final String aString)
	{
		setStringFor(StringKey.RESIDENCE, aString);
	}

	/**
	 * Get the character's residence.
	 * 
	 * @return residence
	 */
	public String getResidence()
	{
		return getSafeStringFor(StringKey.RESIDENCE);
	}

	/**
	 * Get the character's secondary weapons.
	 * 
	 * @return secondary weapons
	 */
	public List<Equipment> getSecondaryWeapons()
	{
		return secondaryWeapons;
	}

	/**
	 * Set the location of the HTML sheet for this character.
	 * 
	 * @param aString the location of the HTML sheet
	 */
	public void setSelectedCharacterHTMLOutputSheet(final String aString)
	{
		outputSheetHTML = aString;
	}

	/**
	 * Get the Location of HTML Output Sheet to be used for this character.
	 * 
	 * @return HTML output sheet
	 */
	public String getSelectedCharacterHTMLOutputSheet()
	{
		return outputSheetHTML;
	}

	/**
	 * Set the location of the PDF Output Sheet for this character.
	 * 
	 * @param aString the location of the PDF Output Sheet
	 */
	public void setSelectedCharacterPDFOutputSheet(final String aString)
	{
		outputSheetPDF = aString;
	}

	/**
	 * Get the location of the PDF Output Sheet to be used for this character.
	 * 
	 * @return pdf output sheet
	 */
	public String getSelectedCharacterPDFOutputSheet()
	{
		return outputSheetPDF;
	}

	/**
	 * Get list of shield proficiencies.
	 * 
	 * @return shield prof list
	 */
	public Collection<ProfProvider<ShieldProf>> getShieldProfList()
	{
		return shieldProfFacet.getQualifiedSet(id);
	}

	/**
	 * Get size.
	 * 
	 * @return size
	 */
	public String getSize()
	{
		return sizeFacet.getSizeAbb(id);
	}

	/**
	 * Get skill list.
	 * 
	 * @return list of skills
	 */
	public Collection<Skill> getSkillSet()
	{
		return skillFacet.getSet(id);
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the same
	 * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
	 * -1) are not included in this list.
	 * 
	 * @return An ArrayList of the skill objects in output order.
	 */
	public List<Skill> getSkillListInOutputOrder()
	{
		return getSkillListInOutputOrder(new ArrayList<Skill>(getSkillSet()));
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the same
	 * outputIndex they will be ordered by name. Note hidden skills (outputIndex =
	 * -1) are not included in this list.
	 * 
	 * @param skills A list of skills which will be sorted, filtered and returned
	 * 
	 * @return An ArrayList of the skill objects in output order.
	 */
	public List<Skill> getSkillListInOutputOrder(final List<Skill> skills)
	{
		final PlayerCharacter pc = this;
		Collections.sort(skills, new Comparator<Skill>()
		{
			/**
			 * Comparator will be specific to Skill objects
			 */
			@Override
			public int compare(final Skill skill1, final Skill skill2)
			{
				Integer obj1Index = pc.getAssoc(skill1, AssociationKey.OUTPUT_INDEX);
				Integer obj2Index = pc.getAssoc(skill2, AssociationKey.OUTPUT_INDEX);

				// Force unset items (index of 0) to appear at the end
				if (obj1Index == null || obj1Index == 0)
				{
					obj1Index = Constants.ARBITRARY_END_SKILL_INDEX;
				}

				if (obj2Index == null || obj2Index == 0)
				{
					obj2Index = Constants.ARBITRARY_END_SKILL_INDEX;
				}

				if (obj1Index > obj2Index)
				{
					return 1;
				}
				else if (obj1Index < obj2Index)
				{
					return -1;
				}
				else
				{
					return skill1.getOutputName().compareToIgnoreCase(
						skill2.getOutputName());
				}
			}
		});

		// Remove the hidden skills from the list
		for (Iterator<Skill> i = skills.iterator(); i.hasNext();)
		{
			final Skill bSkill = i.next();

			Visibility skVis = bSkill.getSafe(ObjectKey.VISIBILITY);
			Integer outputIndex =
					this.getAssoc(bSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex != null && outputIndex == -1
				|| skVis.equals(Visibility.HIDDEN)
				|| skVis.equals(Visibility.DISPLAY_ONLY))
			{
				i.remove();
			}
		}

		return skills;
	}

	/**
	 * Get skill points.
	 * 
	 * @return skill points
	 */
	public int getSkillPoints()
	{
		int returnValue = 0;

		// First compute gained points, and then remove the already spent ones.
		// We can't use Remaining points because the level may be removed, and
		// then we have
		// to display this as -x on the "Total Skill Points" field
		for (PCLevelInfo li : getLevelInfo())
		{
			returnValue += li.getSkillPointsGained(this);
		}

		for (Skill aSkill : getSkillSet())
		{
			List<NamedValue> rankList =
					getAssocList(aSkill, AssociationListKey.SKILL_RANK);
			if (rankList != null)
			{
				for (NamedValue sd : rankList)
				{
					final PCClass pcClass = getClassKeyed(sd.name);
					if (pcClass != null)
					{
						final double curRank = sd.getWeight();
						// Only add the cost for skills associated with a class.
						// Skill ranks from feats etc are free.
						final int cost =
								this.getSkillCostForClass(aSkill, pcClass)
									.getCost();
						returnValue -= (int) (cost * curRank);
					}
				}
			}
		}
		if (Globals.getGameModeHasPointPool())
		{
			returnValue += (int) getRemainingFeatPoints(false); // DO NOT CALL
			// getFeats() here! It
			// will set up a
			// recursive loop and
			// result in a stack
			// overflow!
		}
		return returnValue;
	}

	/**
	 * Set skin colour.
	 * 
	 * @param colour the new skin colour
	 */
	public void setSkinColor(final String colour)
	{
		setStringFor(StringKey.SKIN_COLOR, colour);
	}

	/**
	 * Get skin colour.
	 * 
	 * @return skin colour
	 */
	public String getSkinColor()
	{
		return getSafeStringFor(StringKey.SKIN_COLOR);
	}

	/**
	 * Get list of special abilities.
	 * 
	 * @return List of special abilities
	 */
	public List<SpecialAbility> getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		List<SpecialAbility> aList =
				new ArrayList<SpecialAbility>();
		// Try all possible PObjects
		for (CDOMObject cdo : getCDOMObjectList())
		{
			SpecialAbilityResolution.addSpecialAbilitiesToList(aList, this, cdo);
			SpecialAbilityResolution.addSABToList(aList, this, cdo);
		}

		Collections.sort(aList);

		return aList;
	}

	/**
	 * Get list of special abilities as Strings.
	 * 
	 * @return List of special abilities as Strings
	 */
	public List<String> getSpecialAbilityListStrings()
	{
		List<String> bList = new ArrayList<String>();
		List<SpecialAbility> saList =
			new ArrayList<SpecialAbility>();
		for (CDOMObject cdo : getCDOMObjectList())
		{
			saList.clear();
			SpecialAbilityResolution.addSpecialAbilitiesToList(saList, this, cdo);
			SpecialAbilityResolution.addSABToList(saList, this, cdo);
			for (SpecialAbility sa : saList)
			{
				final String saText = sa.getParsedText(this, this, cdo);
				if (saText != null && !saText.equals(""))
				{
					bList.add(saText);
				}
			}
		}
		
		Collections.sort(bList);

		return bList;
	}

	/**
	 * same as getSpecialAbilityList except if if you have the same ability
	 * twice, it only lists it once with (2) at the end.
	 * 
	 * @return List
	 */
	public ArrayList<String> getSpecialAbilityTimesList()
	{
		final List<String> abilityList = getSpecialAbilityListStrings();
		final List<String> sortList = new ArrayList<String>();
		final int[] numTimes = new int[abilityList.size()];

		for (int i = 0; i < abilityList.size(); i++)
		{
			final String ability = abilityList.get(i);
			if (!sortList.contains(ability))
			{
				sortList.add(ability);
				numTimes[i] = 1;
			}
			else
			{
				for (int j = 0; j < sortList.size(); j++)
				{
					final String testAbility = sortList.get(j);
					if (testAbility.equals(ability))
					{
						numTimes[j]++;
					}
				}
			}
		}

		final ArrayList<String> retList = new ArrayList<String>();
		for (int i = 0; i < sortList.size(); i++)
		{
			String ability = sortList.get(i);
			if (numTimes[i] > 1)
			{
				ability = ability + " (" + numTimes[i] + ")";
			}
			retList.add(ability);
		}

		return retList;
	}

	/**
	 * Set speech tendency.
	 * 
	 * @param tendency the speech tendency
	 */
	public void setSpeechTendency(final String tendency)
	{
		setStringFor(StringKey.SPEECH_TENDENCY, tendency);
	}

	/**
	 * Get speech tendency.
	 * 
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(StringKey.SPEECH_TENDENCY);
	}

	/**
	 * Set the name of the spellbook to auto add new known spells to.
	 * 
	 * @param aString
	 *            The new spellbook name.
	 */
	public void setSpellBookNameToAutoAddKnown(final String aString)
	{
		setStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN, aString);
	}

	/**
	 * Get the name of the spellbook to auto add new known spells to.
	 * 
	 * @return spellbook name
	 */
	public String getSpellBookNameToAutoAddKnown()
	{
		return getSafeStringFor(StringKey.SPELLBOOK_AUTO_ADD_KNOWN);
	}

	/**
	 * Retrieve a spell book object given the name of the spell book.
	 * 
	 * @param name
	 *            The name of the spell book to be retrieved.
	 * @return The spellbook (or null if not present).
	 */
	public SpellBook getSpellBookByName(final String name)
	{
		return spellBookFacet.getBookNamed(id, name);
	}

	/**
	 * Get spell books.
	 * 
	 * @return spellBooks
	 */
	public List<String> getSpellBookNames()
	{
		return new ArrayList<String>(spellBookFacet.getBookNames(id));
	}

	/**
	 * Get spell books.
	 * 
	 * @return spellBooks
	 */
	public Collection<SpellBook> getSpellBooks()
	{
		return spellBookFacet.getBooks(id);
	}

	/**
	 * Get spell class given an index.
	 * 
	 * @param ix the index
	 * @return spell class
	 */
	public PObject getSpellClassAtIndex(final int ix)
	{
		final List<? extends PObject> aList = getSpellClassList();

		if ((ix >= 0) && (ix < aList.size()))
		{
			return aList.get(ix);
		}

		return null;
	}

	/**
	 * a temporary placeholder used for computing the DC of a spell Set from
	 * within Spell.java before the getVariableValue() call.
	 * 
	 * @param i the temporary spell level.
	 */
	public void setSpellLevelTemp(final int i)
	{
		// Explicitly should *not* set the dirty flag to true.
		spellLevelTemp = i;
	}

	/**
	 * Get spell level temp.
	 * 
	 * @return temp spell level
	 */
	public int getSpellLevelTemp()
	{
		return spellLevelTemp;
	}

	/**
	 * Accessor, Gets the sub-race of the character.
	 * @return character subrace.
	 */
	public String getSubRace()
	{
		return subRaceFacet.getSubRace(id);
	}

	/**
	 * Selector <p/> Build on-the-fly so removing templates won't mess up sub
	 * region.
	 * 
	 * @return character sub region
	 */
	public String getSubRegion()
	{
		return regionFacet.getSubRegion(id);
	}

	/**
	 * Set the name on the tab.
	 * 
	 * @param name the new name for the tab
	 */
	public void setTabName(final String name)
	{
		tabName = name;
		setDirty(true);
		setChanged();
		notifyObservers("TabName");
	}

	/**
	 * Get tab name.
	 * 
	 * @return name on tab
	 */
	public String getTabName()
	{
		return tabName;
	}

	/**
	 * Temporary Bonuses
	 */

	/**
	 * List if Items which have Temp Bonuses applied to them.
	 * 
	 * @return List
	 */
	private List<Equipment> getTempBonusItemList()
	{
		return tempBonusItemList;
	}

	/**
	 * Temp Bonus list.
	 * 
	 * @return List
	 */
	public Map<BonusObj, BonusManager.TempBonusInfo> getTempBonusMap()
	{
		return bonusManager.getTempBonusMap();
	}

	/**
	 * Get temp bonus filters.
	 * 
	 * @return temp bonus filters
	 */
	public Set<String> getTempBonusFilters()
	{
		return bonusManager.getTempBonusFilters();
	}

	/**
	 * Set temp bonus filter.
	 * 
	 * @param aBonusStr the temporary bonus to add.
	 */
	public void setTempBonusFilter(final String aBonusStr)
	{
		bonusManager.addTempBonusFilter(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * Unset temp bonus filter.
	 * 
	 * @param aBonusStr the temporary bonus to remove.
	 */
	public void unsetTempBonusFilter(final String aBonusStr)
	{
		bonusManager.removeTempBonusFilter(aBonusStr);
		calcActiveBonuses();
	}

	/**
	 * Get a set of the templates applies to this pc.
	 * @return the set of Templates.
	 */
	public Collection<PCTemplate> getTemplateSet()
	{
		return templateFacet.getSet(id);
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on output.
	 * 
	 * @return The list of templates visible on output sheets.
	 */
	public List<PCTemplate> getOutputVisibleTemplateList()
	{
		List<PCTemplate> tl = new ArrayList<PCTemplate>();

		for (PCTemplate template : getTemplateSet())
		{
			if ((template.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
				|| (template.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY))
			{
				tl.add(template);
			}
		}
		return tl;
	}

	/**
	 * Set trait 1.
	 * 
	 * @param aString the trait.
	 */
	public void setTrait1(final String aString)
	{
		setStringFor(StringKey.TRAIT1, aString);
	}

	/**
	 * Get trait 1.
	 * 
	 * @return trait 1
	 */
	public String getTrait1()
	{
		return getSafeStringFor(StringKey.TRAIT1);
	}

	/**
	 * Set trait 2.
	 * 
	 * @param aString the trait.
	 */
	public void setTrait2(final String aString)
	{
		setStringFor(StringKey.TRAIT2, aString);
	}

	/**
	 * Get trait 2.
	 * 
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(StringKey.TRAIT2);
	}

	/* TODO This should call getPObjectList() to get a list of PObjects to test against.
	 * I don't want to change the behaviour for now however.
	*/

	/**
	 * Evaluates the variable string passed in and returns its value.
	 *
	 * This should probably be refactored to return a String instead.
	 *
	 * @param variableString the variable to evaluate
	 * @param isMax if multiple values are stored, whether to return the largest value
	 * found or the first.
	 * @return the value of the variable.
	 */
	public Float getVariable(final String variableString, final boolean isMax)
	{
		double value = 0.0;
		boolean found = false;

		if (lastVariable != null)
		{
			if (lastVariable.equals(variableString))
			{
				StringBuffer sb = new StringBuffer(Constants.ARBITRARY_INITIAL_STRINGBUFFER_LENGTH);
				sb.append("This is a deliberate warning message, not an error - ");
				sb.append("Avoiding infinite loop in getVariable: repeated lookup ");
				sb.append("of \"").append(lastVariable).append("\" at ")
					.append(value);
				Logging.debugPrint(sb.toString());
				lastVariable = null;
				return new Float(value);
			}
		}
		
		try
		{
			VariableKey vk = VariableKey.valueOf(variableString);
			Double val = variableFacet.getVariableValue(id, vk, isMax);
			if (val != null)
			{
				value = val;
				found = true;
			}
		}
		catch (IllegalArgumentException e)
		{
			//This variable is not in the data - must be builtin?
		}

		boolean includeBonus = true;
		if (!found)
		{
			lastVariable = variableString;
			value = getVariableValue(variableString, Constants.EMPTY_STRING);
			includeBonus = false;
			found = true;
			lastVariable = null;
		}

		if (found && includeBonus)
		{
			value += getTotalBonusTo("VAR", variableString);
		}

		return new Float(value);
	}

	public Collection<WeaponProf> getWeaponProfSet()
	{
		return weaponProfFacet.getProfs(id);
	}

	public SortedSet<WeaponProf> getSortedWeaponProfs()
	{
		return Collections.unmodifiableSortedSet(new TreeSet<WeaponProf>(
			weaponProfFacet.getProfs(id)));
	}

	/**
	 * Sets the character's weight in pounds.
	 * 
	 * @param i
	 *            A weight to set.
	 */
	public void setWeight(final int i)
	{
		weightFacet.setWeight(id, i);
		setDirty(true);
	}

	/**
	 * Gets the character's weight in pounds.
	 * 
	 * @return The character's weight.
	 */
	public int getWeight()
	{
		return weightFacet.getWeight(id);
	}

	public void setPointBuyPoints(final int argPointBuyPoints)
	{
		pointBuyPoints = argPointBuyPoints;
	}

	public int getPointBuyPoints()
	{
		return pointBuyPoints;
	}

	public int getTotalPointBuyPoints()
	{
		return pointBuyPoints + (int) getTotalBonusTo("POINTBUY", "POINTS");
	}

	public void setXP(final int xp)
	{
		xpFacet.setXP(id, xp);
		setDirty(true);
	}

	public int getXP()
	{
		return xpFacet.getXP(id);
	}

	public void addEquipSet(final EquipSet set)
	{
		equipSetFacet.add(id, set);
		setDirty(true);
	}

	/**
	 * Add an item of equipment to the character.
	 * 
	 * @param eq
	 *            The equipment to be added.
	 */
	public void addEquipment(final Equipment eq)
	{
		equipmentFacet.add(id, eq, this);
		userEquipmentFacet.add(id, eq, this);
		setDirty(true);
	}

	/**
	 * Cache the output index of an automatic equipment item.
	 * @param item The equipment item.
	 */
	public void cacheOutputIndex(Equipment item)
	{
		if (item.isAutomatic())
		{
			Logging.debugPrint("Caching " + item.getKeyName() + " - "
				+ item.getOutputIndex() + " item");
			autoEquipOutputOrderCache.put(item.getKeyName(), item
				.getOutputIndex());
		}
	}

	/**
	 * Retrieve the cached output index of the automatic equipment item.
	 * @param key The key of the equipment item.
	 * @return The output index.
	 */
	public int getCachedOutputIndex(String key)
	{
		Integer order = autoEquipOutputOrderCache.get(key);
		return order != null ? order : -1;
	}

	/**
	 * Update the number of a particular equipment item the character possesses.
	 * Mostly concerned with ensuring that the spellbook objects remain in sync
	 * with the number of equipment spellbooks.
	 * 
	 * @param eq
	 *            The Equipment being updated.
	 * @param oldQty
	 *            The original number of items.
	 * @param newQty
	 *            The new number of items.
	 */
	public void updateEquipmentQty(final Equipment eq, double oldQty,
		double newQty)
	{
		if (eq.isType(Constants.TYPE_SPELLBOOK))
		{
			String baseBookname = eq.getName();
			String bookName = eq.getName();
			int old = (int) oldQty;
			int newQ = (int) newQty;

			// Add any new items
			for (int i = old; i < newQ; i++)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + (i + 1);
				}
				SpellBook book =
						new SpellBook(bookName, SpellBook.TYPE_SPELL_BOOK);
				book.setEquip(eq);
				addSpellBook(book);
			}

			// Remove any old items
			for (int i = old; i > newQ; i--)
			{
				if (i > 0)
				{
					bookName = baseBookname + " #" + i;
				}
				delSpellBook(bookName);
			}
		}
		setDirty(true);
	}

	public void addFollower(final Follower aFollower)
	{
		followerFacet.add(id, aFollower);
		setDirty(true);
	}

	private void addLocalEquipment(final Equipment eq)
	{
		equipmentFacet.add(id, eq, this);
	}

	public void addNotesItem(final NoteItem item)
	{
		notesList.add(item);
		setDirty(true);
	}

	/**
	 * Adds a "temporary" bonus.
	 * 
	 * @param aBonus The bonus object to add.
	 * @param source The source of the temporary bonus
	 * @param target The object getting the bonus (typically the PC, can also be equipment).
	 */
	public void addTempBonus(final BonusObj aBonus, Object source, Object target)
	{
		bonusManager.addTempBonus(aBonus, source, target);
		setDirty(true);
	}

	/**
	 * Add a piece of equipment to the temporary bonus list.
	 * @param aEq The piece of equipment to add.
	 */
	public void addTempBonusItemList(final Equipment aEq)
	{
		getTempBonusItemList().add(aEq);
		setDirty(true);
	}

	/**
	 * Compute the total bonus from a List of BonusObjs.
	 * 
	 * @param aList The list of objects
	 * @param source The source of the bonus objects.
	 * @return The aggregate bonus
	 */
	public double calcBonusFromList(final List<BonusObj> aList, CDOMObject source)
	{
		double iBonus = 0;

		for (BonusObj bonus : aList)
		{
			iBonus += bonus.resolve(this, source.getQualifiedKey()).doubleValue();
		}

		return iBonus;
	}

	/**
	 * Checks that the parameter passed in is in the list of objects for which this PC qualifies.
	 * @param obj the object to test for qualification.
	 * @return true if the PC is qualified to have this object.
	 */
	public boolean checkQualifyList(CDOMObject obj)
	{
		return qualifyFacet.grantsQualify(id, obj);
	}

	/**
	 * Check whether this PC has this WeaponProf.
	 * @param wp The WeaponProf to check.
	 * @return True if the PC has the WeaponProf
	 */
	public boolean hasWeaponProf(final WeaponProf wp)
	{
		return weaponProfFacet.containsProf(id, wp);
	}

	/**
	 * Remove an EqSet from the PC's Equipped Equipment.
	 * @param eSet - The EquipSet to remove.
	 * @return true if the object was removed.
	 */
	public boolean delEquipSet(final EquipSet eSet)
	{
		boolean found = equipSetFacet.delEquipSet(id, eSet);
		setDirty(true);
		return found;
	}

	/**
	 * Search all the PCs Equipment sets for instances of eq and delete them.
	 * @param eq The Equipment to delete.
	 */
	public void delEquipSetItem(final Equipment eq)
	{
		equipSetFacet.delEquipSetItem(id, eq);
		setDirty(true);
	}

	/**
	 * Remove a Follower from this PC.
	 * @param aFollower The follower to remove.
	 */
	public void delFollower(final Follower aFollower)
	{
		followerFacet.remove(id, aFollower);
		setDirty(true);
	}

	/**
	 * Check whether the PC has this variable.
	 * @param variableString The variable to check for.
	 * @return True if the PC has the variable.
	 */
	public boolean hasVariable(final String variableString)
	{
		try
		{
			return variableFacet.contains(id, VariableKey .valueOf(variableString));
		}
		catch (IllegalArgumentException e)
		{
			//Built in variable
			return false;
		}
	}

	/**
	 *
	 * @return
	 */
	public int racialSizeInt()
	{
		return sizeFacet.racialSizeInt(id);
	}

	/**
	 *
	 * @param eq
	 */
	public void removeEquipment(final Equipment eq)
	{
		if (eq.isType(Constants.TYPE_SPELLBOOK))
		{
			delSpellBook(eq.getName());
		}

		equipmentFacet.remove(id, eq, this);
		userEquipmentFacet.remove(id, eq, this);
		setDirty(true);
	}

	/**
	 *
	 * @param eq
	 */
	private void removeLocalEquipment(final Equipment eq)
	{
		equipmentFacet.remove(id, eq, this);
		setDirty(true);
	}

	/**
	 * Now we use the ACTYPE tag on misc info to determine the formula
	 * 
	 * @return ac total
	 */
	public int getACTotal()
	{
		return armorClassFacet.calcACOfType(id, "Total");
	}

	public void setAlignment(PCAlignment align)
	{
		alignmentFacet.set(id, align);
		setDirty(true);
	}

	/**
	 * @return the allowDebt
	 */
	public boolean isAllowDebt()
	{
		return moneyFacet.isAllowDebt(id);
	}

	/**
	 * @param allowDebt the allowDebt to set
	 */
	public void setAllowDebt(boolean allowDebt)
	{
		moneyFacet.setAllowDebt(id, allowDebt);
	}

	public String getAttackString(AttackType at)
	{
		return getAttackString(at, 0);
	}

	public String getAttackString(AttackType at, final int bonus)
	{
		return getAttackString(at, bonus, 0);
	}

	/**
	 * Calculates and returns an attack string for one of Melee, Ranged or
	 * Unarmed damage. This will be returned in attack string format i.e.
	 * +11/+6/+1. The attack string returned by this function normally only
	 * includes the attacks generated by the characters Base Attack Bonus. There
	 * are two bonuses to TOHIT that may be applied to the attack string
	 * returned by this function. The first bonus increases only the size of the
	 * attacks generated. The second increases both the size and number of
	 * attacks
	 * 
	 * @param at
	 *            The type of attack. Takes an AttackType (an enumeration)
	 * 
	 * @param TOHITBonus
	 *            A bonus that will be added to the TOHIT numbers. This bonus
	 *            affects only the numbers produced, not the number of attacks
	 * 
	 * @param BABBonus
	 *            This bonus will be added to BAB before the number of attacks
	 *            has been determined.
	 * @return The attack string for this character
	 */

	public String getAttackString(AttackType at, final int TOHITBonus,
		int BABBonus)
	{
		final String cacheLookup =
				"AttackString:" + at.getIdentifier() + "," + TOHITBonus + ","
					+ BABBonus;
		final String cached =
				getVariableProcessor().getCachedString(cacheLookup);

		if (cached != null)
		{
			return cached;
		}

		// index: 0 = melee; 1 = ranged; 2 = unarmed
		// now we see if this PC is a Familiar
		// Initialise to some large negative number
		int masterBAB = -9999;
		int masterTotal = -9999;
		final PlayerCharacter nPC = getMasterPC();

		// check for Epic
		/* 
		final int totalClassLevels = getTotalCharacterLevel();
		Map<String, String> totalLvlMap = null;
		final Map<String, String> classLvlMap;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			String epicAttack = epicAttackMap.get(cacheLookup);
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap =
					getCharacterLevelHashMap(SettingsHandler.getGame()
						.getBabMaxLvl());

			// insure class-levels total is below some value (20)
			getVariableProcessor().pauseCache();
			setClassLevelsBrazenlyTo(classLvlMap);
		}
		*/
		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			masterBAB = nPC.baseAttackBonus();

			final String copyMasterBAB =
					replaceMasterString(getCopyMasterBAB(), masterBAB);
			masterBAB =
					getVariableValue(copyMasterBAB, Constants.EMPTY_STRING)
						.intValue();
			masterTotal = masterBAB + TOHITBonus;
		}

		final int BAB = baseAttackBonus();

		int attackCycle = 1;
		int workingBAB = BAB + TOHITBonus;
		int subTotal = BAB;
		int raceBAB = 0;

		final List<Integer> ab = new ArrayList<Integer>(10);
		final StringBuffer attackString = new StringBuffer();

		// Assume a max of 10 attack cycles
		for (int total = 0; total < 10; ++total)
		{
			ab.add(Integer.valueOf(0));
		}

		// Some classes (like the Monk or Ranged Sniper) use
		// a different attack cycle than the standard classes
		// So compute the base attack for this type (BAB, RAB, UAB)
		for (PCClass pcClass : getClassSet())
		{
			// Get the attack bonus
			final int b = pcClass.baseAttackBonus(this);

			// Get the attack cycle
			final int c = pcClass.attackCycle(at);

			// add to all other classes
			if (c < ab.size())
			{
				final int d = ab.get(c).intValue() + b;

				// set new value for iteration
				ab.set(c, Integer.valueOf(d));
			}

			if (c != 3)
			{
				raceBAB += b;
			}
		}

		// Iterate through all the possible attack cycle values
		// and find the one with the highest attack value
		for (int i = 2; i < 10; ++i)
		{
			final int newAttack = ab.get(i).intValue();
			final int oldAttack = ab.get(attackCycle).intValue();

			if ((newAttack / i) > (oldAttack / attackCycle))
			{
				attackCycle = i;
			}
		}
		/*
				// restore class levels to original value if altered
				if (totalLvlMap != null)
				{
					setClassLevelsBrazenlyTo(totalLvlMap);
					getVariableProcessor().restartCache();
				}
		*/
		// total Number of Attacks for this PC
		int attackTotal = ab.get(attackCycle).intValue();

		// Default cut-off before multiple attacks (e.g. 5)
		final int defaultAttackCycle = SettingsHandler.getGame().getBabAttCyc();

		if (attackTotal == 0)
		{
			attackCycle = defaultAttackCycle;
		}

		// FAMILIAR: check to see if the masters BAB is better
		workingBAB = Math.max(workingBAB, masterTotal);
		subTotal = Math.max(subTotal, masterBAB);
		raceBAB = Math.max(raceBAB, masterBAB);

		if (attackCycle != defaultAttackCycle)
		{
			if ((attackTotal / attackCycle) < (subTotal / defaultAttackCycle))
			{
				attackCycle = defaultAttackCycle;
				attackTotal = subTotal;
			}
			else
			{
				workingBAB -= raceBAB;
				subTotal -= raceBAB;
			}
		}

		int maxAttacks = SettingsHandler.getGame().getBabMaxAtt();
		final int minMultiBab = SettingsHandler.getGame().getBabMinVal();

		// If there is a bonus to BAB, it needs to be added to ALL of
		// the variables used to determine the number of attacks
		attackTotal += BABBonus;
		workingBAB += BABBonus;
		subTotal += BABBonus;

		do
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}

			attackString.append(Delta.toString(workingBAB));
			workingBAB -= attackCycle;
			attackTotal -= attackCycle;
			subTotal -= attackCycle;
			maxAttacks--;
		}
		while (((attackTotal >= minMultiBab) || (subTotal >= minMultiBab))
			&& (maxAttacks > 0));

		getVariableProcessor().addCachedString(cacheLookup,
			attackString.toString());
		return attackString.toString();
	}

	public Set<Language> getAutoLanguages()
	{
		return autoLangFacet.getSet(id);
	}

	/**
	 * @return the autoResize
	 */
	public boolean isAutoResize()
	{
		return autoResize;
	}

	/**
	 * @param autoResize the autoResize to set
	 */
	public void setAutoResize(boolean autoResize)
	{
		this.autoResize = autoResize;
	}

	/**
	 * Sets the autoSortGear.
	 * 
	 * @param autoSortGear
	 *            The autoSortGear to set
	 */
	public void setAutoSortGear(final boolean autoSortGear)
	{
		this.autoSortGear = autoSortGear;
		setDirty(true);
	}

	/**
	 * Returns the autoSortGear.
	 * 
	 * @return boolean
	 */
	public boolean isAutoSortGear()
	{
		return autoSortGear;
	}

	/**
	 * whether we should add auto known spells at level up
	 * 
	 * @param aBool
	 */
	public void setAutoSpells(final boolean aBool)
	{
		autoKnownSpells = aBool;
		setDirty(true);
	}

	public boolean getAutoSpells()
	{
		return autoKnownSpells;
	}

	/**
	 * @return the ignoreCost
	 */
	public boolean isIgnoreCost()
	{
		return moneyFacet.isIgnoreCost(id);
	}

	/**
	 * @param ignoreCost the ignoreCost to set
	 */
	public void setIgnoreCost(boolean ignoreCost)
	{
		moneyFacet.setIgnoreCost(id, ignoreCost);
	}

	/**
	 * Determine whether higher level known spell slots can be used for lower
	 * level spells, or if known spells are restricted to their own level only.
	 * 
	 * @return Returns the useHigherKnownSlots.
	 */
	public boolean getUseHigherKnownSlots()
	{
		return useHigherKnownSlots;
	}

	/**
	 * Set whether higher level known spell slots can be used for lower level
	 * spells, or if known spells are restricted to their own level only.
	 * 
	 * @param useHigher
	 *            Can higher level known spell slots be used?
	 */
	public void setUseHigherKnownSlots(boolean useHigher)
	{
		this.useHigherKnownSlots = useHigher;
	}

	/**
	 * Determine whether higher level prepared spell slots can be used for lower
	 * level spells, or if prepared spells are restricted to their own level
	 * only.
	 * 
	 * @return Returns the useHigherPreppedSlots.
	 */
	public boolean getUseHigherPreppedSlots()
	{
		return useHigherPreppedSlots;
	}

	/**
	 * Set whether higher level prepared spell slots can be used for lower level
	 * spells, or if prepared spells are restricted to their own level only.
	 * 
	 * @param useHigher
	 *            Can higher level prepared spell slots be used?
	 */
	public void setUseHigherPreppedSlots(boolean useHigher)
	{
		this.useHigherPreppedSlots = useHigher;
	}

	/**
	 * Returns the &quot;Base&quot; check value for the check at the index
	 * specified.
	 * 
	 * <p>
	 * This method caps the base check based on the game mode setting for
	 * {@link pcgen.core.GameMode#getChecksMaxLvl() checks max level}.
	 * 
	 * @param check
	 *            The index of the check to get
	 * 
	 * @return The base check value.
	 */
	public int getBaseCheck(final PCCheck check)
	{
		final String cacheLookup = "getBaseCheck:" + check.getKeyName(); //$NON-NLS-1$

		Float total = null;
		if (epicCheckMap.containsKey(check))
		{
			total = epicCheckMap.get(check).floatValue();
		}
		else
		{
			total = getVariableProcessor().getCachedVariable(cacheLookup);
		}

		if (total != null)
		{
			return total.intValue();
		}

		double bonus = 0;
		boolean isEpic = false;
		final int totalClassLevels;
		Map<String, Integer> totalLvlMap = null;
		final Map<String, Integer> classLvlMap;

		totalClassLevels = totalNonMonsterLevels();
		if (totalClassLevels > SettingsHandler.getGame().getChecksMaxLvl())
		{
			isEpic = true;
			Integer epicCheck = epicCheckMap.get(check);
			if (epicCheck == null)
			{
				totalLvlMap = getTotalLevelHashMap();
				classLvlMap = getCharacterLevelHashMap(SettingsHandler
						.getGame().getChecksMaxLvl());
				getVariableProcessor().pauseCache();
				setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels
				// total is below some
				// value (e.g. 20)
			}
			else
			{
				// Logging.errorPrint("getBaseCheck(): '" + cacheLookup + "' =
				// epic='" + epicCheck + "'"); //$NON-NLS-1$
				return epicCheck;
			}
		}

		final String checkName = check.getKeyName();
		bonus = getTotalBonusTo("CHECKS", "BASE." + checkName);

		//
		// now we see if this PC is a Familiar/Mount
		final PlayerCharacter nPC = getMasterPC();

		if ((nPC != null) && (getCopyMasterCheck().length() > 0))
		{
			int masterBonus = nPC.getBaseCheck(check);

			final String copyMasterCheck = replaceMasterString(
					getCopyMasterCheck(), masterBonus);
			masterBonus = getVariableValue(copyMasterCheck,
					Constants.EMPTY_STRING).intValue();

			// use masters save if better
			bonus = Math.max(bonus, masterBonus);
		}

		if (isEpic)
		{
			epicCheckMap.put(check, (int) bonus);
		}

		if (totalLvlMap != null)
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
			getVariableProcessor().restartCache();
		}
		return (int) bonus;
	}

	/**
	 * Returns the total check value for the check specified for the character.
	 * 
	 * <p>
	 * This total includes all check bonuses the character has.
	 * 
	 * @param check
	 *            The check to get.
	 * 
	 * @return A check value.
	 */
	public int getTotalCheck(PCCheck check)
	{
		int bonus = getBaseCheck(check);
		return bonus
			+ (int) getTotalBonusTo("CHECKS", check.getKeyName());
	}

	/**
	 * return bonus total for a specific bonusType e.g:
	 * getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses
	 * 
	 * @param mainType
	 * @param subType
	 * @param bonusType
	 * @return bonus due to type
	 */
	public double getBonusDueToType(final String mainType,
		final String subType, final String bonusType)
	{
		return bonusManager.getBonusDueToType(mainType, subType, bonusType);
	}

	public String getCopyMasterBAB()
	{
		return masterFacet.getCopyMasterBAB(id);
	}

	public String getCopyMasterCheck()
	{
		return masterFacet.getCopyMasterCheck(id);
	}

	public String getCopyMasterHP()
	{
		return masterFacet.getCopyMasterHP(id);
	}

	public void setCurrentHP(final int currentHP)
	{
		setDirty(true);
	}

	public boolean setDeity(final Deity aDeity)
	{
		if (!canSelectDeity(aDeity))
		{
			return false;
		}

		deityFacet.set(id, aDeity);

		if (!isImporting())
		{
			getSpellList();
			AddObjectActions.globalChecks(aDeity, this);
		}
		setDirty(true);

		calcActiveBonuses();

		return true;
	}

	/**
	 * Returns the character's Effective Character Level.
	 * 
	 * <p>
	 * The level is calculated by adding total non-monster levels, total
	 * hitdice, and level adjustment.
	 * 
	 * @return The ECL of the character.
	 */
	public int getECL()
	{
		return levelFacet.getECL(id);
	}

	/**
	 * Set the order in which equipment should be sorted for output.
	 * 
	 * @param i
	 *            The new output order
	 */
	public void setEquipOutputOrder(final int i)
	{
		equipOutputOrder = i;
		setDirty(true);
	}

	/**
	 * @return The selected Output Order for equipment.
	 */
	public int getEquipOutputOrder()
	{
		return equipOutputOrder;
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching the
	 * supplied type and status criteria.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List<Equipment> getEquipmentOfType(final String typeName,
		final int status)
	{
		return getEquipmentOfType(typeName, Constants.EMPTY_STRING, status);
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching the
	 * supplied type, sub type and status criteria.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param subtypeName
	 *            The subtype of equipment to be selected (empty string for no
	 *            subtype)
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List<Equipment> getEquipmentOfType(
		final String typeName,
		final String subtypeName,
		final int status)
	{
		final List<Equipment> aArrayList = new ArrayList<Equipment>();

		for (Equipment eq : getEquipmentSet())
		{
			final boolean subTypeOk = Constants.EMPTY_STRING.equals(subtypeName)
							|| eq.typeStringContains(subtypeName);

			final boolean statusOk = status == 3
						|| (status == 2 && !eq.isEquipped())
						|| (status == 1 && eq.isEquipped());

			if (eq.typeStringContains(typeName) && subTypeOk && statusOk)
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Retrieves a list, sorted in output order, of the character's equipment
	 * matching the supplied type and status criteria. This list is in ascending
	 * order of the equipment's outputIndex field. If multiple items of
	 * equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in this list.
	 * 
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status: 1 (equipped) 2 (not equipped) 3 (don't
	 *            care)
	 * @return An ArrayList of the matching equipment objects in output order.
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final int status)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status),
			Constants.MERGE_ALL);
	}

	/**
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param status
	 *            The required status
	 * @param merge
	 *            What type of merge for like equipment
	 * @return An ArrayList of equipment objects
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final int status, final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), merge);
	}

	/**
	 * @param typeName
	 *            The type of equipment to be selected
	 * @param subtypeName
	 *            The subtype of equipment to be selected
	 * @param status
	 *            The required status
	 * @param merge
	 *            What sort of merging should occur
	 * @return An ArrayList of equipment objects
	 */
	public List<Equipment> getEquipmentOfTypeInOutputOrder(
		final String typeName, final String subtypeName, final int status,
		final int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, subtypeName,
			status), Constants.MERGE_ALL);
	}

	/**
	 * Retrieve the expanded list of weapons Expanded weapons include: double
	 * weapons and melee+ranged weapons Output order is assumed Merge of like
	 * equipment depends on the passed in int
	 * 
	 * @param merge
	 * 
	 * @return the sorted list of weapons.
	 */
	public List<Equipment> getExpandedWeapons(final int merge)
	{
		final List<Equipment> weapList =
				sortEquipmentList(getEquipmentOfType("Weapon", 3), merge);

		//
		// If any weapon is both Melee and Ranged, then make 2 weapons
		// for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in two hands show attacks
		// for both heads, head 1 and head 2 else
		// if wielded in 1 hand, just show damage by head
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = weapList.get(idx);

			if (equip.isDouble()
				&& (equip.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS))
			{
				Equipment eqm = equip.clone();
				eqm.removeType(Type.DOUBLE);
				eqm.addType(Type.HEAD1);

				// Add "Head 1 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Head 1 only"));

				if (eqm.getOutputName().indexOf("Head 1 only") < 0)
				{
					eqm.put(StringKey.OUTPUT_NAME, EquipmentUtilities
						.appendToName(eqm.getOutputName(), "Head 1 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = equip.clone();

				final String altType = eqm.getType(false);
				if (altType.length() != 0)
				{
					eqm.removeListFor(ListKey.TYPE);
					for (String s : altType.split("\\."))
					{
						eqm.addType(Type.getConstant(s));
					}
				}

				eqm.removeType(Type.DOUBLE);
				eqm.addType(Type.HEAD2);
				EquipmentHead head = eqm.getEquipmentHead(1);
				String altDamage = eqm.getAltDamage(this);
				if (altDamage.length() != 0)
				{
					head.put(StringKey.DAMAGE, altDamage);
				}
				head.put(IntegerKey.CRIT_MULT, eqm.getAltCritMultiplier());
				head.put(IntegerKey.CRIT_RANGE, eqm.getRawCritRange(false));
				head.removeListFor(ListKey.EQMOD);
				head.addAllToListFor(ListKey.EQMOD, eqm
					.getEqModifierList(false));

				// Add "Head 2 only" to the name of the weapon
				eqm.setWholeItemName(eqm.getName());
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Head 2 only"));

				if (eqm.getOutputName().indexOf("Head 2 only") < 0)
				{
					eqm.put(StringKey.OUTPUT_NAME, EquipmentUtilities
						.appendToName(eqm.getOutputName(), "Head 2 only"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(idx + 2, eqm);
			}

			//
			// Leave else here, as otherwise will show attacks
			// for both heads for thrown double weapons when
			// it should only show one
			//
			else if (equip.isMelee() && equip.isRanged()
				&& (equip.getRange(this).intValue() != 0))
			{
				//
				// Strip off the Ranged portion, set range to 0
				//
				Equipment eqm = equip.clone();
				eqm.addType(Type.BOTH);
				eqm.removeType(Type.RANGED);
				eqm.removeType(Type.THROWN);
				eqm.put(IntegerKey.RANGE, 0);
				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.set(idx, eqm);

				//
				// Replace any primary weapons
				int iPrimary;

				for (iPrimary = getPrimaryWeapons().size() - 1; iPrimary >= 0; --iPrimary)
				{
					final Equipment teq = getPrimaryWeapons().get(iPrimary);

					if (teq == equip)
					{
						break;
					}
				}

				if (iPrimary >= 0)
				{
					getPrimaryWeapons().set(iPrimary, eqm);
				}

				//
				// Replace any secondary weapons
				int iSecondary;

				for (iSecondary = getSecondaryWeapons().size() - 1; iSecondary >= 0; --iSecondary)
				{
					final Equipment teq = getSecondaryWeapons().get(iSecondary);

					if (teq == equip)
					{
						break;
					}
				}

				if (iSecondary >= 0)
				{
					getSecondaryWeapons().set(iSecondary, eqm);
				}

				//
				// Add thrown portion, strip Melee
				//
				eqm = equip.clone();
				eqm.addType(Type.RANGED);
				eqm.addType(Type.THROWN);
				eqm.addType(Type.BOTH);
				eqm.removeType(Type.MELEE);

				// Add "Thrown" to the name of the weapon
				eqm.setName(EquipmentUtilities.appendToName(eqm.getName(),
					"Thrown"));

				if (eqm.getOutputName().indexOf("Thrown") < 0)
				{
					eqm.put(StringKey.OUTPUT_NAME, EquipmentUtilities
						.appendToName(eqm.getOutputName(), "Thrown"));
				}

				PlayerCharacterUtilities.setProf(equip, eqm);
				weapList.add(++idx, eqm);

				if (iPrimary >= 0)
				{
					getPrimaryWeapons().add(++iPrimary, eqm);
				}
				else if (iSecondary >= 0)
				{
					getSecondaryWeapons().add(++iSecondary, eqm);
				}
			}
		}

		return weapList;
	}

	/**
	 * renaming to standard convention due to refactoring of export
	 * 
	 * Build on-the-fly so removing templates doesn't mess up favoured list
	 * 
	 * @author Thomas Behr 08-03-02
	 */
	public SortedSet<PCClass> getFavoredClasses()
	{
		SortedSet<PCClass> favored = new TreeSet<PCClass>(
				CDOMObjectUtilities.CDOM_SORTER);
		favored.addAll(favClassFacet.getSet(id));
		return favored;
	}

	/**
	 * Calculates the level of the character's favored class
	 * 
	 * @return level
	 */
	public int getFavoredClassLevel()
	{
		return favClassFacet.getFavoredClassLevel(id);
	}

	/**
	 * Calculates total bonus from Feats
	 * 
	 * @param aType
	 * @param aName
	 * @return feat bonus to
	 */
	public double getFeatBonusTo(String aType, String aName)
	{
		return getPObjectWithCostBonusTo(aggregateFeatList(), aType
			.toUpperCase(), aName.toUpperCase());
	}

	/**
	 * Returns the Feat definition of a feat possessed by the character.
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return the Feat (not the CharacterFeat) searched for, <code>null</code>
	 *         if not found.
	 */
	public Ability getFeatNamed(final String featName)
	{
		Ability ability = AbilityUtilities.retrieveAbilityKeyed(
				AbilityCategory.FEAT, featName);
		Collection<AbilityCategory> cats =
				SettingsHandler.getGame().getAllAbilityCatsForKey(Constants.FEAT_CATEGORY);
		for (AbilityCategory abilityCategory : cats)
		{
			Ability contained = getMatchingAbility(abilityCategory, ability);
			if (contained != null)
			{
				return contained;
			}
		}
		return null;
	}

	public Ability getMatchingAbility(Category<Ability> abilityCategory,
			Ability ability, Nature nature)
	{
		Ability contained = abFacet.getContained(id, abilityCategory,
				nature, ability);
		if (contained != null)
		{
			return contained;
		}
		contained = grantedAbilityFacet.getContained(id, abilityCategory,
				nature, ability);
		if (contained != null)
		{
			return contained;
		}
		return null;
	}

	private Ability getMatchingAbility(AbilityCategory abilityCategory,
			Ability ability)
	{
		Ability contained = getMatchingAbility(abilityCategory, ability,
				Nature.NORMAL);
		if (contained != null)
		{
			return contained;
		}
		contained = getMatchingAbility(abilityCategory, ability,
				Nature.VIRTUAL);
		if (contained != null)
		{
			return contained;
		}
		contained = getMatchingAbility(abilityCategory, ability,
				Nature.AUTOMATIC);
		if (contained != null)
		{
			return contained;
		}
		return null;
	}

	/**
	 * Searches the characters feats for an Ability object which is a clone of
	 * the same Base ability as the Ability passed in
	 * 
	 * @param anAbility
	 * @return the Ability if found, otherwise null
	 */
	public Ability getAbilityMatching(final Ability anAbility)
	{
		List<Ability> abilityList = new ArrayList<Ability>();
		for (AbilityCategory cat : SettingsHandler.getGame()
				.getAllAbilityCategories())
		{
			abilityList.addAll(getAggregateAbilityListNoDuplicates(cat));
		}
		Nature nature = getAbilityNature(anAbility);
		for (Ability ability : abilityList)
		{
			boolean nameCheck = (ability.getKeyName().compareToIgnoreCase(
					anAbility.getKeyName()) == 0);
			boolean catCheck = ability.getCategory().compareToIgnoreCase(
					anAbility.getCategory()) == 0;
			if (nameCheck
					&& catCheck
					&& ((nature == Nature.ANY) || (this
							.getAbilityNature(ability) == nature)))
			{
				return ability;
			}
		}
		
		return null;
	}

	public void setHasMadeKitSelectionForAgeSet(final int index,
		final boolean arg)
	{
		if ((index >= 0) && (index < 10))
		{
			ageSetKitSelections[index] = arg;
		}
		setDirty(true);
	}

	public Collection<Kit> getKitInfo()
	{
		return kitFacet.getSet(id);
	}

	public int getLevelAdjustment()
	{
		return levelFacet.getLevelAdjustment(id);
	}

	public Collection<PCLevelInfo> getLevelInfo()
	{
		return levelInfoFacet.getSet(id);
	}

	public Collection<PCLevelInfo> clearLevelInfo()
	{
		return levelInfoFacet.removeAll(id);
	}

	public PCLevelInfo getLevelInfo(int index)
	{
		return levelInfoFacet.get(id, index);
	}

	public String getLevelInfoClassKeyName(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return levelInfoFacet.get(id, idx).getClassKeyName();
		}

		return Constants.EMPTY_STRING;
	}

	public int getLevelInfoClassLevel(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return levelInfoFacet.get(id, idx).getClassLevel();
		}

		return 0;
	}

	public PCLevelInfo getLevelInfoFor(final String classKey, int level)
	{
		for (PCLevelInfo pcl : getLevelInfo())
		{
			if (pcl.getClassKeyName().equals(classKey))
			{
				level--;
			}

			if (level <= 0)
			{
				return pcl;
			}
		}

		return null;
	}

	public int getLevelInfoSize()
	{
		return levelInfoFacet.getCount(id);
	}

	/**
	 * whether we should load companions on master load
	 * 
	 * @param aBool
	 */
	public void setLoadCompanion(final boolean aBool)
	{
		autoLoadCompanion = aBool;
		setDirty(true);
	}

	public boolean getLoadCompanion()
	{
		return autoLoadCompanion;
	}

	/**
	 * @return the number of Character Domains possible
	 */
	public int getMaxCharacterDomains()
	{
		return (int) getTotalBonusTo("DOMAIN", "NUMBER");
	}

	/**
	 * @param source
	 * @param aPC
	 * @return the number of Character Domains possible and check the level of
	 *         the source class if the result is 0.
	 */
	public int getMaxCharacterDomains(final PCClass source,
		final PlayerCharacter aPC)
	{
		int i = getMaxCharacterDomains();
		if (i == 0 && !hasDefaultDomainSource())
			i =
					(int) source.getBonusTo("DOMAIN", "NUMBER", getLevel(source), aPC);
		return i;
	}

	/**
	 * Calculate the maximum number of ranks the character is allowed to have in
	 * the specified skill.
	 * 
	 * @param aSkill
	 *            The skill being checked.
	 * @param aClass
	 *            The name of the current class in which points are being spent -
	 *            only used to check cross-class skill cost.
	 * @return max rank
	 */
	public Float getMaxRank(Skill aSkill, final PCClass aClass)
	{
		int levelForSkillPurposes = getTotalLevels();
		final BigDecimal maxRanks;

		if (aSkill == null)
		{
			return 0.0f;
		}
		if (aSkill.getSafe(ObjectKey.EXCLUSIVE))
		{
			// Exclusive skills only count levels in classes which give access
			// to the skill
			levelForSkillPurposes = 0;

			for (PCClass bClass : getClassSet())
			{
				if (this.isClassSkill(aSkill, bClass))
				{
					levelForSkillPurposes += getLevel(bClass);
				}
			}

			if (levelForSkillPurposes == 0)
			{
				// No classes qualify for this exclusive skill, so treat it as a
				// cross-class skill
				// This does not seem right to me! JD
				levelForSkillPurposes = (getTotalLevels());

				maxRanks =
						SkillUtilities.maxCrossClassSkillForLevel(
							levelForSkillPurposes, this);
			}
			else
			{
				maxRanks =
						SkillUtilities.maxClassSkillForLevel(
							levelForSkillPurposes, this);
			}
		}
		else if (!this.isClassSkill(aSkill)
			&& (this.getSkillCostForClass(aSkill, aClass)
				.equals(SkillCost.CLASS)))
		{
			// Cross class skill - but as cost is 1 only return a whole number
			maxRanks =
					new BigDecimal(SkillUtilities.maxCrossClassSkillForLevel(
						levelForSkillPurposes, this).intValue()); // This was (int) (i/2.0) previously
		}
		else if (!this.isClassSkill(aSkill))
		{
			// Cross class skill
			maxRanks =
					SkillUtilities.maxCrossClassSkillForLevel(
						levelForSkillPurposes, this);
		}
		else
		{
			// Class skill
			maxRanks =
					SkillUtilities.maxClassSkillForLevel(levelForSkillPurposes,
						this);
		}
		return new Float(maxRanks.floatValue());
	}

	public double movementOfType(final String moveType)
	{
		return moveResultFacet.movementOfType(id, moveType);
	}

	/**
	 * Checks if the stat is a non ability.
	 * 
	 * @return true, if is non ability
	 */
	public boolean isNonAbility(PCStat stat)
	{
		return nonAbilityFacet.isNonAbility(id, stat);
	}

	public int getNumberOfMovements()
	{
		return moveResultFacet.countMovementTypes(id);
	}

	public int getOffHandLightBonus()
	{
		final int div =
				getVariableValue("OFFHANDLIGHTBONUS", Constants.EMPTY_STRING)
					.intValue();
		return div;
	}

	/*
	 * returns true if Equipment is in the primary weapon list
	 */
	public boolean isPrimaryWeapon(final Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}

		for (Equipment eqI : primaryWeapons)
		{
			if (eqI.getName().equalsIgnoreCase(eq.getName())
				&& (eqI.getLocation() == eq.getLocation()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isProficientWith(final Equipment eq)
	{
		if (eq.isShield())
		{
			return shieldProfFacet.isProficientWithShield(id, eq);
		}
		else if (eq.isArmor())
		{
			return armorProfFacet.isProficientWithArmor(id, eq);
		}
		else if (eq.isWeapon())
		{
			return weaponProfFacet.isProficientWithWeapon(id, eq);
		}

		return false;
	}

	/**
	 * Changes the race of the character. First it removes the current Race, and
	 * any bonus attributes (e.g. feats), then add the new Race.
	 * 
	 * @param newRace
	 */
	public void setRace(final Race newRace)
	{
		final Race oldRace = getRace();
		// remove current race attributes

		if (oldRace != null)
		{
			removeAllCharacterSpells(oldRace);
		}

		// add new race attributes
		raceFacet.remove(id);

		if (newRace != null)
		{
			raceFacet.set(id, newRace);
		}

		if (!isImporting())
		{
			getSpellList();
			AddObjectActions.globalChecks(newRace, this);
			adjustMoveRates();
			calcActiveBonuses();
		}

		setDirty(true);
	}

	/**
	 * return bonus from a Race
	 * 
	 * @param aType
	 * @param aName
	 * @return race bonus to
	 */
	public double getRaceBonusTo(String aType, String aName)
	{
		if (getRace() == null)
		{
			return 0;
		}

		final List<BonusObj> tempList = BonusUtilities.getBonusFromList(
				getRace().getBonusList(this), aType.toUpperCase(), aName
						.toUpperCase());

		return calcBonusFromList(tempList, getRace());
	}

	public int getSR()
	{
		return calcSR(true);
	}

	/*
	 * returns true if Equipment is in the secondary weapon list
	 */
	public boolean isSecondaryWeapon(final Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}

		for (Equipment eqI : secondaryWeapons)
		{
			if (eqI.getName().equalsIgnoreCase(eq.getName())
				&& (eqI.getLocation() == eq.getLocation()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculates total bonus from Size adjustments
	 * 
	 * @param aType
	 * @param aName
	 * @return size adjustment bonus to
	 */
	public double getSizeAdjustmentBonusTo(String aType, String aName)
	{
		return getBonusDueToType(aType.toUpperCase(), aName.toUpperCase(),
			"SIZE");
	}

	/**
	 * Set the order in which skills should be sorted for output.
	 * 
	 * @param i
	 *            The new output order
	 */
	public void setSkillsOutputOrder(final int i)
	{
		skillsOutputOrder = i;
		setDirty(true);
	}

	/**
	 * @return The selected Output Order for skills.
	 */
	public int getSkillsOutputOrder()
	{
		return skillsOutputOrder;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster and of the desired caster level.
	 * 
	 * @param minLevel
	 * @return boolean
	 */
	public boolean isSpellCaster(final int minLevel)
	{
		return isSpellCaster(minLevel, false) > 0;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster and of the total of all of their
	 * spellcasting levels is at least the desired caster level.
	 * 
	 * @param minLevel
	 *            The desired caster level
	 * @param sumOfLevels
	 *            True if all of the character caster levels should be added
	 *            together before the comparison.
	 * @return boolean
	 */
	public int isSpellCaster(final int minLevel, final boolean sumOfLevels)
	{
		return isSpellCaster(null, minLevel, sumOfLevels);
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the desired
	 * caster level.
	 * 
	 * @param spellType
	 *            The type of spellcaster (i.e. "Arcane" or "Divine")
	 * @param minLevel
	 *            The desired caster level
	 * @param sumLevels
	 *            True if all of the character caster levels should be added
	 *            together before the comparison.
	 * @return boolean
	 */
	public int isSpellCaster(final String spellType, final int minLevel,
		final boolean sumLevels)
	{
		int classTotal = 0;
		int runningTotal = 0;

		for (PCClass pcClass : getClassSet())
		{
			if (spellType == null
				|| spellType.equalsIgnoreCase(pcClass.getSpellType()))
			{
				int classLevels =
						(int) getTotalBonusTo("CASTERLEVEL", pcClass
							.getKeyName());
				if ((classLevels == 0)
					&& (canCastSpellTypeLevel(pcClass.getSpellType(), 0, 1) || canCastSpellTypeLevel(
						pcClass.getSpellType(), 1, 1)))
				{
					// missing CASTERLEVEL hack
					classLevels = getLevel(pcClass);
				}
				classLevels +=
						(int) getTotalBonusTo("PCLEVEL", pcClass.getKeyName());
				if (sumLevels)
				{
					runningTotal += classLevels;
				}
				else
				{
					if (classLevels >= minLevel)
					{
						classTotal++;
					}
				}
			}
		}

		if (sumLevels)
		{
			return runningTotal >= minLevel ? 1 : 0;
		}
		return classTotal;
	}

	public void getSpellList()
	{
		// all non-spellcaster spells are added to race
		// so return if it's null
		Race race = getRace();
		if (race == null)
		{
			return;
		}

		removeAllCharacterSpells(race);
		spellsFacet.process(id);
		setDirty(true);
	}

	/**
	 * Parses a spells range (short, medium or long) into an Integer based on
	 * the spell and spell casters level
	 * 
	 * @param aSpell
	 *            The spell being output.
	 * @param si
	 *            The info about conditions applied to the spell
	 * @return spell range
	 */
	public String getSpellRange(final CharacterSpell aSpell, final SpellInfo si)
	{
		String aRange = aSpell.getSpell().getListAsString(ListKey.RANGE);
		String aSpellClass = aSpell.getVariableSource(this);
		int rangeInFeet = 0;
		String aString =
				Globals.getGameModeSpellRangeFormula(aRange.toUpperCase());

		if (aRange.equalsIgnoreCase("CLOSE") && (aString == null))
		{
			aString = "((CASTERLEVEL/2).TRUNC*5)+25"; //$NON-NLS-1$
		}
		else if (aRange.equalsIgnoreCase("MEDIUM") && (aString == null))
		{
			aString = "(CASTERLEVEL*10)+100"; //$NON-NLS-1$
		}
		else if (aRange.equalsIgnoreCase("LONG") && (aString == null))
		{
			aString = "(CASTERLEVEL*40)+400"; //$NON-NLS-1$
		}

		if (aString != null)
		{
			List<Ability> metaFeats = null;
			if (si != null)
			{
				metaFeats = si.getFeatList();
			}
			rangeInFeet =
				getVariableValue(aSpell, aString, aSpellClass)
					.intValue();
			if ((metaFeats != null) && !metaFeats.isEmpty())
			{
				for (Ability feat : metaFeats)
				{
					rangeInFeet +=
							(int) BonusCalc.bonusTo(feat, "SPELL", "RANGE", this, this);

					final int iMult =
							(int) BonusCalc.bonusTo(feat, "SPELL", "RANGEMULT", this, this);

					if (iMult > 0)
					{
						rangeInFeet = (rangeInFeet * iMult);
					}
				}
			}

			aRange +=
					(" ("
						+ Globals.getGameModeUnitSet()
							.displayDistanceInUnitSet(rangeInFeet)
						+ Globals.getGameModeUnitSet().getDistanceUnit() + ")");
		}
		else
		{
			aRange = parseSpellString(aSpell, aRange);
		}

		return aRange;
	}

	/**
	 * Computes the Caster Level for a Class
	 * 
	 * @param aClass
	 * @return caster level for class
	 */
	public int getCasterLevelForClass(final PCClass aClass)
	{
		final Spell sp = new Spell();
		final CharacterSpell cs = new CharacterSpell(aClass, sp); 
		final String aSpellClass = "CLASS:" + aClass.getKeyName();
		return getVariableValue(cs, "CASTERLEVEL", aSpellClass).intValue();
	}

	/**
	 * Computes the Caster Level for a Class
	 * 
	 * @param aSpell
	 * @return caster level for spell
	 */
	public int getCasterLevelForSpell(final CharacterSpell aSpell)
	{
		return getVariableValue(aSpell, "CASTERLEVEL",
				aSpell.getVariableSource(this)).intValue();
	}

	/**
	 * Calculates total bonus from all stats
	 * 
	 * @param aType
	 * @param aName
	 * @return stat bonus to
	 */
	public double getStatBonusTo(String aType, String aName)
	{
		return statFacet.getStatBonusTo(id, aType, aName);
	}

	/**
	 * Parses through all templates to calculate total bonus
	 * 
	 * @param aType
	 * @param aName
	 * @return template bonus to
	 */
	public double getTemplateBonusTo(String aType, String aName)
	{
		return getPObjectWithCostBonusTo(templateFacet.getSet(id), aType.toUpperCase(),
			aName.toUpperCase());
	}

	/**
	 * Get the total bonus from Stats, Size, Age, Alignment, Classes,
	 * companions, Equipment, Feats, Templates, Domains, Races, etc This value
	 * is taken from an already populated HashMap for speed
	 * 
	 * @param bonusType
	 *            Type of bonus ("COMBAT" or "SKILL")
	 * @param bonusName
	 *            Name of bonus ("AC" or "Hide");
	 * @return total bonus to
	 */
	public double getTotalBonusTo(final String bonusType, final String bonusName)
	{
		return bonusManager.getTotalBonusTo(bonusType, bonusName);
	}

	public int getTotalLevels()
	{
		return levelFacet.getTotalLevels(id);
	}

	/**
	 * Get the value of the desired stat at the point just before the character
	 * was raised to the next level.
	 * 
	 * @param stat
	 *            The Stat to check.
	 * @param level
	 *            The level we want to see the stat at.
	 * @param includePost
	 *            Should stat mods that occurred after levelling be included?
	 * @return The stat as it was at the level
	 */
	public int getTotalStatAtLevel(
		final PCStat stat,
		final int level,
		final boolean includePost)
	{
		int curStat = StatAnalysis.getTotalStatFor(this, stat);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust =
					levelInfoFacet.get(id, idx).getTotalStatMod(stat, true);
			curStat -= statLvlAdjust;
		}
		// If the user doesn't want POST changes, we remove any made in the
		// target level only
		if (!includePost && level > 0)
		{
			int statLvlAdjust =
					levelInfoFacet.get(id, level - 1).getTotalStatMod(stat, true);
			statLvlAdjust -=
					levelInfoFacet.get(id, level - 1).getTotalStatMod(stat, false);
			curStat -= statLvlAdjust;

		}

		return curStat;
	}

	public int getTwoHandDamageDivisor()
	{
		int div =
				getVariableValue("TWOHANDDAMAGEDIVISOR", Constants.EMPTY_STRING)
					.intValue();

		if (div == 0)
		{
			div = 2;
		}

		return div;
	}

	/**
	 * Get the unarmed damage string for this PC as adjusted by the booleans
	 * passed in.
	 * @param includeStrBonus
	 * @param adjustForPCSize
	 * 
	 * @return the unarmed damage string
	 */
	public String getUnarmedDamageString(final boolean includeStrBonus,
		final boolean adjustForPCSize)
	{
		String retString = "2|1d2";

		for (PCClass pcClass : getClassSet())
		{
			retString =
					PlayerCharacterUtilities.getBestUDamString(retString,
						pcClass.getUdamForLevel(getLevel(pcClass), this,
							adjustForPCSize));
		}

		int sizeInt = sizeInt();
		for (List<String> unarmedDamage : unarmedDamageFacet.getSet(id))
		{
			String aDamage;
			if (unarmedDamage.size() == 1)
			{
				aDamage = unarmedDamage.get(0);
			}
			else
			{
				aDamage = unarmedDamage.get(sizeInt);
			}
			retString =
					PlayerCharacterUtilities.getBestUDamString(retString,
						aDamage);
		}
		//Test against the default for the race
		String pObjDamage = unarmedDamageFacet.getUDamForRace(id);
		retString =
				PlayerCharacterUtilities.getBestUDamString(retString,
					pObjDamage);

		// string is in form sides|damage, just return damage portion
		StringBuilder ret =
				new StringBuilder(retString
					.substring(retString.indexOf('|') + 1));
		if (includeStrBonus)
		{
			int sb = (int) getStatBonusTo("DAMAGE", "TYPE.MELEE");
			sb += (int) getStatBonusTo("DAMAGE", "TYPE=MELEE");
			if (sb != 0)
			{
				ret.append(Delta.toString(sb));
			}
		}
		return ret.toString();
	}

	public boolean getUseMasterSkill()
	{
		return masterFacet.getUseMasterSkill(id);
	}

	/**
	 * whether we should use/save Temporary bonuses
	 * 
	 * @param aBool
	 */
	public void setUseTempMods(final boolean aBool)
	{
		useTempMods = aBool;
		// commented out setDirty because this causes a re-load of all tabs
		// every time any tab is viewed! merton_monk
		// setDirty(true);
	}

	public boolean getUseTempMods()
	{
		return useTempMods;
	}

	/**
	 * Evaluates a variable for this character e.g:
	 * getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 * 
	 * @param aString
	 *            The variable to be evaluated
	 * @param src
	 *            The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	public Float getVariableValue(final String aString, final String src)
	{
		return getVariableValue(null, aString, src);
	}

	public Float getVariableValue(final String varName, final String src,
		final PlayerCharacter aPC)
	{
		return getVariableValue(null, varName, src);
	}

	/**
	 * Evaluates a variable for this character e.g:
	 * getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 * 
	 * @param aSpell
	 *            This is specifically to compute bonuses to CASTERLEVEL for a
	 *            specific spell.
	 * @param aString
	 *            The variable to be evaluated
	 * @param src
	 *            The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	private Float getVariableValue(final CharacterSpell aSpell, String aString,
		String src)
	{
		VariableProcessor vp = getVariableProcessor();
		return vp.getVariableValue(aSpell, aString, src, getSpellLevelTemp());
	}

	/**
	 * @return VariableProcessor
	 */
	public VariableProcessor getVariableProcessor()
	{
		return variableProcessor;
	}

	public int getTotalCasterLevelWithSpellBonus(CharacterSpell acs,
		final Spell aSpell, final String spellType, final String classOrRace,
		final int casterLev)
	{
		if (aSpell != null && acs.getFixedCasterLevel() != null)
		{
			return getVariableValue(acs.getFixedCasterLevel(),
				Constants.EMPTY_STRING).intValue();
		}

		int tBonus = casterLev;
		boolean replaceCasterLevel = false;

		String tType;
		String tStr;
		// final List<TypedBonus> bonuses = new ArrayList<TypedBonus>();
		final List<CasterLevelSpellBonus> bonuses =
				new ArrayList<CasterLevelSpellBonus>();

		if (classOrRace != null)
		{
			// bonuses.addAll(getBonusesTo("CASTERLEVEL", classOrRace));
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", classOrRace);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", classOrRace);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}

			// Support both types of syntax for CLASS:
			// BONUS:CASTERLEVEL|Sorcerer|1 and
			// BONUS:CASTERLEVEL|CLASS.Sorcerer|1
			if (!classOrRace.startsWith("RACE."))
			{
				tStr = "CLASS." + classOrRace;
				// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
				tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
				if (tBonus > 0)
				{
					tType = getSpellBonusType("CASTERLEVEL", tStr);
					bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
				}
			}
		}

		if (aSpell == null)
		{
			return (tBonus);
		}

		if (!spellType.equals(Constants.NONE))
		{
			tStr = "TYPE." + spellType;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset = getBonusesTo("CASTERLEVEL", tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		tStr = "SPELL." + aSpell.getKeyName();
		// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
		tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}
		tStr += ".RESET";
		// final List<TypedBonus> reset = getBonusesTo("CASTERLEVEL", tStr);
		// if ( reset.size() > 0 )
		// {
		// bonuses.addAll(reset);
		// replaceCasterLevel = true;
		// }
		tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
		if (tBonus > 0)
		{
			replaceCasterLevel = true;
			tType = getSpellBonusType("CASTERLEVEL", tStr);
			bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
		}

		/*
		 * This wraps in TreeSet because it looks to me like this is ordered
		 * (given .RESET)
		 */
		for (SpellSchool school : new TreeSet<SpellSchool>(aSpell
			.getSafeListFor(ListKey.SPELL_SCHOOL)))
		{
			tStr = "SCHOOL." + school.toString();
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus != 0) // Allow negative bonus to casterlevel
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		for (String subschool : new TreeSet<String>(aSpell
			.getSafeListFor(ListKey.SPELL_SUBSCHOOL)))
		{
			tStr = "SUBSCHOOL." + subschool;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		//Not wrapped because it wasn't in 5.14
		for (String desc : aSpell.getSafeListFor(ListKey.SPELL_DESCRIPTOR))
		{
			tStr = "DESCRIPTOR." + desc;
			// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
			tStr += ".RESET";
			// final List<TypedBonus> reset1 = getBonusesTo("CASTERLEVEL",
			// tStr);
			// if ( reset.size() > 0 )
			// {
			// bonuses.addAll(reset1);
			// replaceCasterLevel = true;
			// }
			tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
			if (tBonus > 0)
			{
				replaceCasterLevel = true;
				tType = getSpellBonusType("CASTERLEVEL", tStr);
				bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
			}
		}

		final HashMapToList<CDOMList<Spell>, Integer> domainMap =
				this.getLevelInfo(aSpell);
		if (domainMap != null)
		{
			for (CDOMList<?> spellList : domainMap.getKeySet())
			{
				if (spellList instanceof DomainSpellList)
				{
					tStr = "DOMAIN." + spellList.getKeyName();
					// bonuses.addAll( getBonusesTo("CASTERLEVEL", tStr) );
					tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
					tStr += ".RESET";
					// final List<TypedBonus> reset1 =
					// getBonusesTo("CASTERLEVEL", tStr);
					// if ( reset.size() > 0 )
					// {
					// bonuses.addAll(reset1);
					// replaceCasterLevel = true;
					// }
					tBonus = (int) getTotalBonusTo("CASTERLEVEL", tStr);
					if (tBonus > 0)
					{
						replaceCasterLevel = true;
						tType = getSpellBonusType("CASTERLEVEL", tStr);
						bonuses.add(new CasterLevelSpellBonus(tBonus, tType));
					}
				}
			}
		}

		// now go through all bonuses, checking types to see what should add
		// together
		for (int z = 0; z < bonuses.size() - 1; z++)
		{
			final CasterLevelSpellBonus zBonus = bonuses.get(z);

			String zType = zBonus.getType();
			if ((zBonus.getBonus() == 0) || zType.equals(""))
			{
				continue;
			}

			boolean zReplace = false;
			boolean zStack = false;
			if (zType.endsWith(".REPLACE"))
			{
				zType = zType.substring(0, zType.length() - 8);
				zReplace = true;
			}
			else
			{
				if (zType.endsWith(".STACK"))
				{
					zType = zType.substring(0, zType.length() - 6);
					zStack = true;
				}
			}

			for (int k = z + 1; k < bonuses.size(); k++)
			{
				final CasterLevelSpellBonus kBonus = bonuses.get(k);

				String kType = kBonus.getType();
				if ((kBonus.getBonus() == 0) || kType.equals(""))
				{
					continue;
				}

				boolean kReplace = false;
				boolean kStack = false;
				if (kType.endsWith(".REPLACE"))
				{
					kType = kType.substring(0, kType.length() - 8);
					kReplace = true;
				}
				else
				{
					if (kType.endsWith(".STACK"))
					{
						kType = kType.substring(0, kType.length() - 6);
						kStack = true;
					}
				}

				if (!zType.equals(kType))
				{
					continue;
				}

				// if both end in ".REPLACE", add together and save for later
				// comparison
				if (zReplace && kReplace)
				{
					kBonus.setBonus(zBonus.getBonus() + kBonus.getBonus());
					zBonus.setBonus(0);
					continue;
				}

				// if either ends in ".STACK", then they will add
				if (zStack || kStack)
				{
					continue;
				}

				// otherwise, only keep max
				if (zBonus.getBonus() > kBonus.getBonus())
				{
					kBonus.setBonus(0);
				}
				else
				{
					zBonus.setBonus(0);
				}
			}
		}

		int result = 0;
		if (!replaceCasterLevel)
		{
			result += casterLev;
		}

		// result += TypedBonus.totalBonuses(bonuses);
		// Now go through bonuses and add it up
		for (CasterLevelSpellBonus resultBonus : bonuses)
		{
			result += resultBonus.getBonus();
		}

		if (result <= 0)
		{
			result = 1; // Casterlevel must be at least 1
		}

		return (result);
	}

	private String getSpellBonusType(final String bonusType,
		final String bonusName)
	{
		return bonusManager.getSpellBonusType(bonusType, bonusName);
	}

	public Collection<Vision> getVisionList()
	{
		return visionFacet.getActiveVision(id);
	}

	public String getVision()
	{
		final StringBuffer visionString = new StringBuffer();

		for (Vision vision : getVisionList())
		{
			if (visionString.length() > 0)
			{
				visionString.append(", ");
			}

			visionString.append(vision);
		}

		return visionString.toString();
	}

	public int abilityAC()
	{
		return armorClassFacet.calcACOfType(id, "Ability");
	}

	/**
	 * returns all equipment (from the equipmentList) of type aString
	 * 
	 * @param aList
	 * @param aType
	 * @return List
	 */
	public List<Equipment> addEqType(final List<Equipment> aList,
		final String aType)
	{
		for (Equipment eq : getEquipmentSet())
		{
			if (eq.typeStringContains(aType))
			{
				aList.add(eq);
				setDirty(true);
			}
			else if (aType.equalsIgnoreCase("CONTAINED")
				&& (eq.getParent() != null))
			{
				aList.add(eq);
				setDirty(true);
			}
		}

		return aList;
	}

	/**
	 * Adds a <tt>Kit</tt> to the applied list of kits for the character.
	 * 
	 * @param aKit
	 *            The <tt>Kit</tt> to add.
	 */
	public void addKit(final Kit aKit)
	{
		kitFacet.add(id, aKit);
		setDirty(true);
	}

	public void addSkill(final Skill addSkill)
	{
		// First, check to see if skill is already in list
		if (hasSkill(addSkill))
		{
			return;
		}

		//
		// Skill not found, add to list
		//
		skillFacet.add(id, addSkill);
		setDirty(true);

		if (!isImporting())
		{
			AddObjectActions.doBaseChecks(addSkill, this);
			BonusActivation.activateBonuses(addSkill, this);
			calcActiveBonuses();
		}
	}

	/**
	 * @param acs
	 *            is the CharacterSpell object containing the spell which is to
	 *            be modified
	 * @param aFeatList
	 *            is the list of feats to be added to the SpellInfo object
	 * @param classKey
	 *            is the name of the class whose list of character spells will
	 *            be modified
	 * @param bookName
	 *            is the name of the book for the SpellInfo object
	 * @param spellLevel
	 *            is the original (unadjusted) level of the spell not including
	 *            feat adjustments
	 * @param adjSpellLevel
	 *            is the adjustedLevel (including feat adjustments) of this
	 *            spell, it may be higher if the user chooses a higher level.
	 * 
	 * @return an empty string on successful completion, otherwise the return
	 *         value indicates the reason the add function failed.
	 */
	public String addSpell(CharacterSpell acs, final List<Ability> aFeatList,
		final String classKey, final String bookName, final int adjSpellLevel,
		final int spellLevel)
	{
		if (acs == null)
		{
			return "Invalid parameter to add spell";
		}

		PCClass aClass = null;
		final Spell aSpell = acs.getSpell();

		if ((bookName == null) || (bookName.length() == 0))
		{
			return "Invalid spell list/book name.";
		}

		if (!hasSpellBook(bookName))
		{
			return "Could not find spell list/book " + bookName;
		}

		if (classKey != null)
		{
			aClass = getClassKeyed(classKey);

			if ((aClass == null) && (classKey.lastIndexOf('(') >= 0))
			{
				aClass =
						getClassKeyed(classKey.substring(0,
							classKey.lastIndexOf('(')).trim());
			}
		}

		// If this is a spellbook, the class doesn't have to be one the PC has
		// already.
		SpellBook spellBook = getSpellBookByName(bookName);
		if (aClass == null && spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			aClass =
					Globals.getContext().ref.silentlyGetConstructedCDOMObject(
						PCClass.class, classKey);
			if ((aClass == null) && (classKey.lastIndexOf('(') >= 0))
			{
				aClass =
						Globals.getContext().ref
							.silentlyGetConstructedCDOMObject(PCClass.class,
								classKey
									.substring(0, classKey.lastIndexOf('('))
									.trim());
			}
		}

		if (aClass == null)
		{
			return "No class keyed " + classKey;
		}

		if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
			&& !bookName.equals(Globals.getDefaultSpellBook()))
		{
			return aClass.getDisplayName() + " can only add to "
				+ Globals.getDefaultSpellBook();
		}

		// Divine spellcasters get no bonus spells at level 0
		// TODO: allow classes to define how many bonus spells they get each
		// level!
		// int numSpellsFromSpecialty = aClass.getNumSpellsFromSpecialty();
		// if (spellLevel == 0 &&
		// "Divine".equalsIgnoreCase(aClass.getSpellType()))
		// {
		// numSpellsFromSpecialty = 0;
		// }
		// all the exists checks are done.

		// don't allow adding spells which are not qualified for.
		if (!aSpell.qualifies(this, aSpell))
		{
			return "You do not qualify for " + acs.getSpell().getDisplayName()
				+ ".";
		}

		// don't allow adding spells which are prohibited to known
		// or prepared lists
		// But if a spell is both prohibited and in a speciality
		// which can be the case for some spells, then allow it.
		if (spellBook.getType() != SpellBook.TYPE_SPELL_BOOK
			&& !acs.isSpecialtySpell(this) && SpellCountCalc.isProhibited(aSpell, aClass, this))
		{
			return acs.getSpell().getDisplayName() + " is prohibited.";
		}

		// Now let's see if they should be able to add this spell
		// first check for known/cast/threshold
		final int known = this.getSpellSupport(aClass).getKnownForLevel(spellLevel, "null", this);
		int specialKnown = 0;
		final int cast =
				this.getSpellSupport(aClass).getCastForLevel(adjSpellLevel, bookName, true, true, this);
		SpellCountCalc.memorizedSpellForLevelBook(this, aClass, adjSpellLevel, bookName);

		final boolean isDefault =
				bookName.equals(Globals.getDefaultSpellBook());

		if (isDefault)
		{
			specialKnown = this.getSpellSupport(aClass).getSpecialtyKnownForLevel(spellLevel, this);
		}

		int numPages = 0;

		// known is the maximum spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		// Modified this to use new availableSpells() method so you can "blow"
		// higher-level slots on
		// lower-level spells
		// in re BUG [569517]
		// sk4p 13 Dec 2002
		if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			// If this is a spellbook rather than known spells
			// or prepared spells, then let them add spells up to
			// the page limit of the book.
			setSpellLevelTemp(spellLevel);
			/*
			 * TODO Need to understand more about this context of formula
			 * resolution (in context of a spell??) in order to understand how
			 * to put this method into the Formula interface
			 */
			numPages =
					getVariableValue(acs,
						spellBook.getPageFormula().toString(), "").intValue();
			// Check number of pages remaining in the book
			if (numPages + spellBook.getNumPagesUsed() > spellBook
				.getNumPages())
			{
				return "There are not enough pages left to add this spell to the spell book.";
			}
			spellBook.setNumPagesUsed(numPages + spellBook.getNumPagesUsed());
			spellBook.setNumSpells(spellBook.getNumSpells() + 1);
		}
		else if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
			&& !availableSpells(adjSpellLevel, aClass, bookName, true, acs
				.isSpecialtySpell(this)))
		{
			String ret;
			int maxAllowed;
			// If this were a specialty spell, would there be room?
			if (!acs.isSpecialtySpell(this)
				&& availableSpells(adjSpellLevel, aClass, bookName, true, true))
			{
				ret =
						"Your remaining slot(s) must be filled with your speciality.";
				maxAllowed = known;
			}
			else
			{
				ret =
						"You can only learn "
							+ (known + specialKnown)
							+ " spells for level "
							+ adjSpellLevel
							+ "\nand there are no higher-level slots available.";
				maxAllowed = known + specialKnown;
			}
			int memTot =
					SpellCountCalc.memorizedSpellForLevelBook(this, aClass, adjSpellLevel, bookName);
			int spellDifference = maxAllowed - memTot;
			if (spellDifference > 0)
			{
				ret +=
						"\n"
							+ spellDifference
							+ " spells from lower levels are using slots for this level.";
			}
			return ret;
		}
		else if (aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
			&& !isDefault
			&& !availableSpells(adjSpellLevel, aClass, bookName, false, acs
				.isSpecialtySpell(this)))
		{
			String ret;
			int maxAllowed;
			if (!acs.isSpecialtySpell(this)
				&& availableSpells(adjSpellLevel, aClass, bookName, false, true))
			{
				ret =
						"Your remaining slot(s) must be filled with your speciality or domain.";
				maxAllowed =
						this.getSpellSupport(aClass).getCastForLevel(adjSpellLevel, bookName, false, true, this);
			}
			else
			{
				ret =
						"You can only prepare "
							+ cast
							+ " spells for level "
							+ adjSpellLevel
							+ "\nand there are no higher-level slots available.";
				maxAllowed = cast;
			}
			int memTot =
					SpellCountCalc.memorizedSpellForLevelBook(this, aClass, adjSpellLevel, bookName);
			int spellDifference = maxAllowed - memTot;
			if (spellDifference > 0)
			{
				ret +=
						"\n"
							+ spellDifference
							+ " spells from lower levels are using slots for this level.";
			}
			return ret;
		}

		// determine if this spell already exists
		// for this character in this book at this level
		SpellInfo si = null;
		final List<CharacterSpell> acsList =
				getCharacterSpells(aClass, acs.getSpell(), bookName,
					adjSpellLevel);
		if (!acsList.isEmpty())
		{
			for (int x = acsList.size() - 1; x >= 0; x--)
			{
				final CharacterSpell c = acsList.get(x);
				if (!c.equals(acs))
				{
					acsList.remove(x);
				}
			}
		}
		final boolean isEmpty = acsList.isEmpty();
		if (!isEmpty)
		{
			// I am not sure why this code is set up like this but it is
			// bogus. I am trying to break as little as possible so if
			// I have one matching spell I will use it otherwise I will
			// use the passed in spell.
			if (acsList.size() == 1)
			{
				final CharacterSpell tcs = acsList.get(0);
				si =
						tcs.getSpellInfoFor(this, bookName, adjSpellLevel, -1,
							aFeatList);
			}
			else
			{
				si =
						acs.getSpellInfoFor(this, bookName, adjSpellLevel, -1,
							aFeatList);
			}
		}

		if (si != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorised
			if (isDefault)
			{
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			}
			si.setTimes(si.getTimes() + 1);
		}
		else
		{
			if (isEmpty && !containsCharacterSpell(aClass, acs))
			{
				addCharacterSpell(aClass, acs);
			}
			else if (isEmpty)
			{
				// Make sure that we are working on the same spell object, not just the same spell
				for (CharacterSpell characterSpell : getCharacterSpells(aClass))
				{
					if (characterSpell.equals(acs))
					{
						acs = characterSpell;
					}
				}
			}
			si = acs.addInfo(spellLevel, adjSpellLevel, 1, bookName, aFeatList);

			//
			//
			if (Globals.hasSpellPPCost())
			{
				final Spell theSpell = acs.getSpell();
				int ppCost = theSpell.getSafe(IntegerKey.PP_COST);
				for (Ability feat : aFeatList)
				{
					ppCost +=
							(int) BonusCalc.bonusTo(feat, "PPCOST", theSpell.getKeyName(), this, this);
				}
				si.setActualPPCost(ppCost);
			}
			if (Spell.hasSpellPointCost())
			{
				final Spell theSpell = acs.getSpell();
				int spellPointCost =
						SpellPoint.getSpellPointCostActual(theSpell);
				for (Ability feat : aFeatList)
				{
					spellPointCost +=
							(int) BonusCalc.bonusTo(feat, "SPELLPOINTCOST", theSpell
							.getKeyName(), this, this);
				}
				si.setActualSpellPointCost(spellPointCost);
			}
		}
		// Set number of pages on the spell
		si.setNumPages(si.getNumPages() + numPages);
		setDirty(true);
		return "";
	}

	/**
	 * return value indicates if book was actually added or not
	 * 
	 * @param aName
	 * @return TRUE or FALSE
	 */
	public boolean addSpellBook(final String aName)
	{
		if (aName != null && (aName.length() > 0)
			&& !spellBookFacet.containsBookNamed(id, aName))
		{
			return addSpellBook(new SpellBook(aName,
					SpellBook.TYPE_PREPARED_LIST));
		}
		return false;
	}

	public boolean addSpellBook(final SpellBook book)
	{
		if (!spellBookFacet.containsBookNamed(id, book.getName()))
		{
			spellBookFacet.add(id, book);
			setDirty(true);
			return true;
		}
		return false;
	}

	public void addTemplate(final PCTemplate inTemplate)
	{
		if (inTemplate == null)
		{
			return;
		}

		// Don't allow multiple copies of template.
		if (templateFacet.contains(id, inTemplate))
		{
			return;
		}

		int lockMonsterSkillPoints = 0; // this is what this value was before
		// adding this template
		for (PCClass pcClass : getClassSet())
		{
			if (pcClass.isMonster())
			{
				lockMonsterSkillPoints =
						(int) getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");
				break;
			}
		}

		templateFacet.add(id, inTemplate);

		this.setDirty(true);

		calcActiveBonuses();

		if (!isImporting())
		{
			getSpellList();
			List<AbilitySelection> templateFeats = feats(inTemplate,
					getTotalLevels(), totalHitDice(), true);
			for (int j = 0, y = templateFeats.size(); j < y; ++j)
			{
				AbilityUtilities.modFeatsFromList(this, templateFeats.get(j));
			}
			AddObjectActions.globalChecks(inTemplate, this);
		}

		getAutomaticAbilityList(AbilityCategory.FEAT);

		calcActiveBonuses();
		int postLockMonsterSkillPoints; // this is what this value was before
		// adding this template
		boolean first = true;
		for (PCClass pcClass : getClassSet())
		{
			if (pcClass.isMonster())
			{
				postLockMonsterSkillPoints =
						(int) getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");

				if (postLockMonsterSkillPoints != lockMonsterSkillPoints
					&& postLockMonsterSkillPoints > 0)
				{
					for (PCLevelInfo pi : getLevelInfo())
					{
						final int newSkillPointsGained =
								pcClass
									.recalcSkillPointMod(this, pi.getClassLevel());
						if (pi.getClassKeyName().equals(pcClass.getKeyName()))
						{
							final int formerGained = pi.getSkillPointsGained(this);
							pi.setSkillPointsGained(this, newSkillPointsGained);
							pi.setSkillPointsRemaining(pi
								.getSkillPointsRemaining()
								+ newSkillPointsGained - formerGained);
							setAssoc(pcClass, AssociationKey.SKILL_POOL,
								pcClass.getSkillPool(this)
									+ newSkillPointsGained - formerGained);
						}
					}
				}
			}
			//
			// Recalculate HPs in case HD have changed.
			//
			if (!isImporting())
			{
				Modifier<HitDie> dieLock = inTemplate.get(ObjectKey.HITDIE);
				if (dieLock != null)
				{
					for (int level = 1; level <= getLevel(pcClass); level++)
					{
						HitDie baseHD = pcClass.getSafe(ObjectKey.LEVEL_HITDIE);
						if (!baseHD.equals(getLevelHitDie(pcClass, level)))
						{
							// If the HD has changed from base reroll
							rollHP(pcClass, level, first);
						}
					}
				}
			}
			first = false;
		}

		// karianna bug 1184888
		adjustMoveRates();

		setDirty(true);
	}

	public void adjustGold(final double delta)
	{
		moneyFacet.adjustGold(id, delta);
		setDirty(true);
	}

	/**
	 * recalculate all the move rates and modifiers
	 */
	public void adjustMoveRates()
	{
		moveResultFacet.reset(id);
		setDirty(true);
	}

	public List<Spell> aggregateSpellList(final String school,
		final String subschool, final String descriptor, final int minLevel,
		final int maxLevel)
	{
		final List<Spell> retList = new ArrayList<Spell>();

		for (PObject pObj : getSpellClassList())
		{
			for (int a = minLevel; a <= maxLevel; a++)
			{
				for (CharacterSpell cs : getCharacterSpells(pObj, a))
				{
					final Spell aSpell = cs.getSpell();
					SpellSchool ss =
						Globals.getContext().ref
							.silentlyGetConstructedCDOMObject(
								SpellSchool.class, school);

					if ((school.length() == 0)
						|| (ss != null) && aSpell.containsInList(ListKey.SPELL_SCHOOL, ss)
						|| (subschool.length() == 0)
						|| aSpell.containsInList(ListKey.SPELL_SUBSCHOOL,
							subschool)
						|| (descriptor.length() == 0)
						|| aSpell.containsInList(ListKey.SPELL_DESCRIPTOR,
							descriptor))
					{
						retList.add(aSpell);
					}
				}
			}
		}

		return retList;
	}

	/**
	 * Get the Alternative HP for Gamemodes that don't use the traditions 
	 * HP approach (e.g.  Wound points)  
	 * 
	 * @return The alternative HP for this PC
	 */
	public int altHP()
	{
		final int i = (int) getTotalBonusTo("HP", "ALTHP");
		return i;
	}

	public int baseAC()
	{
		return armorClassFacet.calcACOfType(id, "Base");
	}

	/**
	 * @return Total base attack bonus as an int
	 */
	public int baseAttackBonus()
	{
		// check for cached version
		final String cacheLookup = "BaseAttackBonus";
		Float total;
		if (epicBAB != null)
		{
			total = epicBAB.floatValue();
		}
		else
		{
			total = getVariableProcessor().getCachedVariable(cacheLookup);
		}
		if (total != null)
		{
			return total.intValue();
		}

		// get Master's BAB
		final PlayerCharacter nPC = getMasterPC();

		if ((nPC != null) && (getCopyMasterBAB().length() > 0))
		{
			int masterBAB = nPC.baseAttackBonus();
			final String copyMasterBAB =
					replaceMasterString(getCopyMasterBAB(), masterBAB);
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();

			getVariableProcessor().addCachedVariable(cacheLookup,
				Float.valueOf(masterBAB));
			return masterBAB;
		}

		// Check for Epic
		final int totalClassLevels = totalNonMonsterLevels();
		Map<String, Integer> totalLvlMap = null;
		final Map<String, Integer> classLvlMap;
		boolean isEpic = false;
		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			isEpic = true;
			if (epicBAB == null)
			{
				totalLvlMap = getTotalLevelHashMap();
				classLvlMap =
						getCharacterLevelHashMap(SettingsHandler.getGame()
							.getBabMaxLvl());

				// ensure total class-levels below some value (e.g. 20)
				getVariableProcessor().pauseCache();
				setClassLevelsBrazenlyTo(classLvlMap);
			}
			else
			{
				//Logging.errorPrint("baseAttackBonus(): '" + cacheLookup + "' = epic:'" + epicBAB + "'"); //$NON-NLS-1$
				return epicBAB;
			}
		}

		final int bab = (int) getTotalBonusTo("COMBAT", "BAB");

		if (isEpic)
		{
			epicBAB = bab;
		}
		if (totalLvlMap != null)
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
			getVariableProcessor().restartCache();
		}

		getVariableProcessor().addCachedVariable(cacheLookup,
			Float.valueOf(bab));
		return bab;
	}

	/**
	 * Calculate the AC for a particular ACTYPE e.g.  Flatfooted
	 */
	public int calcACOfType(final String ACType)
	{
		return armorClassFacet.calcACOfType(id, ACType);
	}

	/**
	 * Creates the activeBonusList which is used to calculate all the bonuses to
	 * a PC
	 */
	public void calcActiveBonuses()
	{
		if (isImporting() || (getRace() == null))
		{
			return;
		}

		// Keep rebuilding the active bonus map until the
		// contents do not change. This is to cope with the
		// situation where we have a variable A that has a prereq
		// that depends on variable B that will not be the correct
		// value until after the map has been completely created.

		do
		{
			bonusManager.checkpointBonusMap();
			setDirty(true);
			calcActiveBonusLoop();
			if (Globals.checkRule(RuleConstants.RETROSKILL))
			{
				checkSkillModChange();
			}
		} while (!bonusManager.compareToCheckpoint());
		// If the newly calculated bonus map is different to the old one
		// loop again until they are the same.
	}

	/*
	 * These are designed to catch a re-entrant bonus loop, which can occur
	 * when a BONUS contains a level limited item in a Formula, such as BAB
	 */
	private int cablInt = 1;
	private int lastCablInt = 0;

	private void calcActiveBonusLoop()
	{
		if (cablInt == lastCablInt)
		{
			return;
		}
		lastCablInt = cablInt;
		bonusManager.setActiveBonusList();
		// buildBonusMap(bonuses);
		bonusManager.buildActiveBonusMap();
		cablInt++;
		bonusChangeFacet.reset(id);
	}

	/**
	 * Calculate the Challenge Rating
	 * 
	 * @return CR
	 */
	public float calcCR()
	{
		return crFacet.getCR(id);
	}

	/**
	 * Get all possible sources of Damage Resistance and calculate
	 * 
	 * @return DR
	 */
	public String calcDR()
	{
		return drFacet.getDRString(id);
	}

	public int calcSR(final boolean includeEquipment)
	{
		int SR = srFacet.getSR(id);

		if (includeEquipment)
		{
			for (Equipment eq : getEquippedEquipmentSet())
			{
				SR = Math.max(SR, eq.getSafe(ObjectKey.SR).getReduction()
						.resolve(this, eq.getQualifiedKey()).intValue());

				for (EquipmentModifier eqMod : eq.getEqModifierList(true))
				{
					SR = Math.max(SR, eqMod.getSR(eq, this));
				}

				for (EquipmentModifier eqMod : eq.getEqModifierList(false))
				{
					SR = Math.max(SR, eqMod.getSR(eq, this));
				}
			}
		}

		SR += (int) getTotalBonusTo("MISC", "SR");
		// SR += (int) getBonusValue("MISC", "SR");

		//
		// This would make more sense to just not add in the first place...
		//
		if (!includeEquipment)
		{
			SR -= (int) getEquipmentBonusTo("MISC", "SR");
		}

		return SR;
	}

	/**
	 * Method will go through the list of classes that the PC has and see if
	 * they can cast spells of desired type at desired <b>spell level</b>.
	 * 
	 * @param spellType
	 *            Spell type to check for
	 * @param spellLevel
	 *            Desired spell level
	 * @param minNumSpells
	 *            Minimum number of spells at the desired spell level
	 * @return boolean <p/> author David Wilson
	 *         <eldiosyeldiablo@users.sourceforge.net>
	 */
	private boolean canCastSpellTypeLevel(final String spellType,
		final int spellLevel, final int minNumSpells)
	{
		for (PCClass aClass : getClassSet())
		{
			String classSpellType = aClass.get(StringKey.SPELLTYPE);
			if (classSpellType != null
				&& ("Any".equalsIgnoreCase(spellType) || classSpellType
					.equalsIgnoreCase(spellType)))
			{
				// Get the number of known spells for the level
				int knownForLevel = this.getSpellSupport(aClass).getKnownForLevel(spellLevel, "null", this);
				knownForLevel +=
						this.getSpellSupport(aClass).getSpecialtyKnownForLevel(spellLevel, this);
				if (knownForLevel >= minNumSpells)
				{
					return true;
				}

				// See if the character can cast
				// at the required spell level
				if (this.getSpellSupport(aClass).getCastForLevel(spellLevel, this) >= minNumSpells)
				{
					return true;
				}

				// If they don't memorise spells and don't have
				// a CastList then they use something funky
				// like Power Points (psionic)
				if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
					&& !this.getSpellSupport(aClass).hasKnownList() && this.getSpellSupport(aClass).zeroCastSpells())
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Method will go through the list of classes that the PC has and see if
	 * they can cast spells of desired type at desired <b>spell level</b>.
	 * 
	 * @param spellType
	 *            Spell type to check for
	 * @param spellLevel
	 *            Desired spell level
	 * @return The number of spells castable
	 **/
	public int countSpellCastTypeLevel(final String spellType,
		final int spellLevel)
	{
		int known = 0;
		int cast = 0;
		for (PCClass aClass : getClassSet())
		{
			String classSpellType = aClass.get(StringKey.SPELLTYPE);
			if (classSpellType != null
				&& ("Any".equalsIgnoreCase(spellType) || classSpellType
					.equalsIgnoreCase(spellType)))
			{
				// Get the number of known spells for the level
				known += this.getSpellSupport(aClass).getKnownForLevel(spellLevel, "null", this);
				known += this.getSpellSupport(aClass).getSpecialtyKnownForLevel(spellLevel, this);

				// See if the character can cast
				// at the required spell level
				cast += this.getSpellSupport(aClass).getCastForLevel(spellLevel, this);

				// If they don't memorise spells and don't have
				// a CastList then they use something funky
				// like Power Points (psionic)
				if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
					&& !this.getSpellSupport(aClass).hasKnownList() && this.getSpellSupport(aClass).zeroCastSpells())
				{
					return Integer.MAX_VALUE;
				}
			}
		}

		return known == 0 ? cast : known;
	}

	/**
	 * Check whether a deity can be selected by this character
	 * 
	 * @return <code>true</code> means the deity can be a selected by a
	 *         character with the given properties; <code>false</code> means
	 *         the character cannot.
	 */
	public boolean canSelectDeity(final Deity aDeity)
	{
		return legalDeityFacet.allows(id, aDeity);
	}

	public int classAC()
	{
		return armorClassFacet.calcACOfType(id, "ClassDefense");
	}

	/**
	 * Return value indicates whether or not a spell was deleted.
	 * 
	 * @param si
	 * @param aClass
	 * @param bookName
	 * @return String
	 */
	public String delSpell(SpellInfo si, final PCClass aClass,
		final String bookName)
	{
		if ((bookName == null) || (bookName.length() == 0))
		{
			return "Invalid spell book name.";
		}

		if (aClass == null)
		{
			return "Error: Class is null";
		}

		final CharacterSpell acs = si.getOwner();

		final boolean isDefault =
				bookName.equals(Globals.getDefaultSpellBook());

		// yes, you can remove spells from the default spellbook,
		// but they will just get added back in when the character
		// is re-loaded. But, allow them to do it anyway, just in case
		// there is some weird spell that keeps getting loaded by
		// accident (or is saved in the .pcg file)
		if (isDefault
			&& this.getSpellSupport(aClass).isAutoKnownSpell(acs.getSpell(), si
			.getActualLevel(), false, this))
		{
			Logging.errorPrint("Notice: removing "
				+ acs.getSpell().getDisplayName()
				+ " even though it is an auto known spell");
		}

		SpellBook spellBook = getSpellBookByName(bookName);
		if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			int pagesPerSpell = si.getNumPages() / si.getTimes();
			spellBook.setNumPagesUsed(spellBook.getNumPagesUsed()
				- pagesPerSpell);
			spellBook.setNumSpells(spellBook.getNumSpells() - 1);
			si.setNumPages(si.getNumPages() - pagesPerSpell);
		}
		si.setTimes(si.getTimes() - 1);

		if (si.getTimes() <= 0)
		{
			acs.removeSpellInfo(si);
		}

		// Remove the spell form the character's class instance if it
		// is no longer present in any book
		if (acs.getInfoList().isEmpty())
		{
			removeCharacterSpell(aClass, acs);
		}

		return "";
	}

	/**
	 * Calculate different kinds of bonuses to saves. possible tokens are
	 * <ul>
	 * <li>save</li>
	 * <li>save.TOTAL</li>
	 * <li>save.BASE</li>
	 * <li>save.MISC</li>
	 * <li>save.list</li>
	 * <li>save.TOTAL.list</li>
	 * <li>save.BASE.list</li>
	 * <li>save.MISC.list</li>
	 * </ul>
	 * where<br />
	 * save := "CHECK1"|"CHECK2"|"CHECK3"<br />
	 * list := ((include|exclude)del)*(include|exclude)<br />
	 * include := "FEATS"|"MAGIC"|"RACE"<br />
	 * exclude := "NOFEATS"|"NOMAGIC"|"NORACE"|"NOSTAT" <br />
	 * del := "." <br />
	 * given as regular expression. <p/> "include"-s will add the appropriate
	 * modifier "exclude"-s will subtract the appropriate modifier <p/> (This
	 * means <tt>save.MAGIC.NOMAGIC</tt> equals 0, whereas
	 * <tt>save.RACE.RACE</tt> equals 2 times the racial bonus) <p/> If you
	 * use unrecognised terminals, their value will amount to 0 This means
	 * <tt>save.BLABLA</tt> equals 0 whereas <tt>save.MAGIC.BLABLA</tt>
	 * equals <tt>save.MAGIC</tt> <p/> <br>
	 * author: Thomas Behr 09-03-02
	 * 
	 * @param check
	 * @param tokenString
	 *            tokenString to parse
	 * @return the calculated save bonus
	 */
	public int calculateSaveBonus(final PCCheck check, final String tokenString)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		int save = 0;
		String saveType = check.toString();

		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();

			if ("TOTAL".equals(tokens[i]))
			{
				save += getTotalCheck(check);
			}
			else if ("BASE".equals(tokens[i]))
			{
				save += getBaseCheck(check);
			}
			else if ("MISC".equals(tokens[i]))
			{
				save += (int) getTotalBonusTo("CHECKS", saveType);
			}

			if ("EPIC".equals(tokens[i]))
			{
				save += (int) getBonusDueToType("CHECKS", saveType, "EPIC");
			}

			if ("MAGIC".equals(tokens[i]))
			{
				save += (int) getEquipmentBonusTo("CHECKS", saveType);
			}

			if ("RACE".equals(tokens[i]))
			{
				save += calculateSaveBonusRace(check);
			}

			if ("FEATS".equals(tokens[i]))
			{
				save += (int) getFeatBonusTo("CHECKS", saveType);
			}

			if ("STATMOD".equals(tokens[i]))
			{
				save += (int) checkFacet.getCheckBonusTo(id, "CHECKS", saveType);
			}

			/**
			 * exclude stuff
			 */
			if ("NOEPIC".equals(tokens[i]))
			{
				save -= (int) getBonusDueToType("CHECKS", saveType, "EPIC");
			}

			if ("NOMAGIC".equals(tokens[i]))
			{
				save -= (int) getEquipmentBonusTo("CHECKS", saveType);
			}

			if ("NORACE".equals(tokens[i]))
			{
				save -= calculateSaveBonusRace(check);
			}

			if ("NOFEATS".equals(tokens[i]))
			{
				save -= (int) getFeatBonusTo("CHECKS", saveType);
			}

			if ("NOSTAT".equals(tokens[i]) || "NOSTATMOD".equals(tokens[i]))
			{
				save -= (int) checkFacet.getCheckBonusTo(id, "CHECKS", saveType);
			}
		}

		return save;
	}

	/**
	 * return value indicates whether or not a book was actually removed
	 * 
	 * @param aName
	 * @return true or false
	 */
	public boolean delSpellBook(final String aName)
	{
		if ((aName.length() > 0)
			&& !aName.equals(Globals.getDefaultSpellBook())
			&& spellBookFacet.containsBookNamed(id, aName))
		{
			processSpellBookRemoval(aName);
			return true;
		}

		return false;
	}

	private void processSpellBookRemoval(String aName)
	{
		spellBookFacet.removeBookNamed(id, aName);
		setDirty(true);

		for (PCClass pcClass : getClassSet())
		{
			for (CharacterSpell cs : getCharacterSpells(pcClass, aName))
			{
				cs.removeSpellInfo(cs.getSpellInfoFor(this, aName, -1,
					-1));
			}
		}
	}

	private void determinePrimaryOffWeapon()
	{
		primaryWeapons.clear();
		secondaryWeapons.clear();

		if (!hasEquipment())
		{
			return;
		}

		final List<Equipment> unequippedPrimary = new ArrayList<Equipment>();
		final List<Equipment> unequippedSecondary = new ArrayList<Equipment>();

		for (Equipment eq : getEquipmentSet())
		{
			if (!eq.isWeapon() || (eq.getSlots(this) < 1))
			{
				continue;
			}

			final boolean isEquipped = eq.isEquipped();

			if ((eq.getLocation() == EquipmentLocation.EQUIPPED_PRIMARY)
				|| ((eq.getLocation() == EquipmentLocation.EQUIPPED_BOTH) && primaryWeapons
					.isEmpty())
				|| (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS))
			{
				if (isEquipped)
				{
					primaryWeapons.add(eq);
				}
				else
				{
					unequippedPrimary.add(eq);
				}
			}
			else if ((eq.getLocation() == EquipmentLocation.EQUIPPED_BOTH)
				&& !primaryWeapons.isEmpty())
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}

			if (eq.getLocation() == EquipmentLocation.EQUIPPED_SECONDARY)
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}

			if (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS)
			{
				for (int y = 0; y < (eq.getNumberEquipped() - 1); ++y)
				{
					if (isEquipped)
					{
						secondaryWeapons.add(eq);
					}
					else
					{
						unequippedSecondary.add(eq);
					}
				}
			}
		}

		if (Globals.checkRule(RuleConstants.EQUIPATTACK))
		{
			if (unequippedPrimary.size() != 0)
			{
				primaryWeapons.addAll(unequippedPrimary);
			}

			if (unequippedSecondary.size() != 0)
			{
				secondaryWeapons.addAll(unequippedSecondary);
			}
		}
	}

	public int dodgeAC()
	{
		return armorClassFacet.calcACOfType(id, "Dodge");
	}

	public int equipmentAC()
	{
		return armorClassFacet.calcACOfType(id, "Equipment")
				+ armorClassFacet.calcACOfType(id, "Armor");
	}

	public int flatfootedAC()
	{
		return armorClassFacet.calcACOfType(id, "Flatfooted");
	}

	/**
	 * Does the character have this ability as an auto ability.
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object to check
	 * 
	 * @return <tt>true</tt> if the character has the ability
	 */
	public boolean hasAutomaticAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		return abFacet.contains(id, aCategory, Nature.AUTOMATIC, anAbility)
				|| grantedAbilityFacet.contains(id, aCategory, Nature.AUTOMATIC, anAbility);
	}

	/**
	 * Does the character have this ability as a virtual ability.
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object to check
	 * 
	 * @return <tt>true</tt> if the character has the ability
	 */
	public boolean hasVirtualAbility(final AbilityCategory aCategory,
		final Ability anAbility)
	{
		return abFacet.contains(id, aCategory, Nature.VIRTUAL, anAbility)
				|| grantedAbilityFacet.contains(id, aCategory, Nature.VIRTUAL, anAbility);
	}

	public boolean hasMadeKitSelectionForAgeSet(final int index)
	{
		return ((index >= 0) && (index < 10) && ageSetKitSelections[index]);
	}

	public boolean hasSpecialAbility(final String abilityKey)
	{
		for (SpecialAbility sa : getSpecialAbilityList())
		{
			if (sa.getKeyName().equalsIgnoreCase(abilityKey))
			{
				return true;
			}
		}

		return false;
	}

	public int hitPoints()
	{
		int total = 0;

		String aString = Globals.getGameModeHPFormula();
		if (aString.length() != 0)
		{
			for (;;)
			{
				int startIdx = aString.indexOf("$$");
				if (startIdx < 0)
				{
					break;
				}
				int endIdx = aString.indexOf("$$", startIdx + 2);
				if (endIdx < 0)
				{
					break;
				}

				String lookupString = aString.substring(startIdx + 2, endIdx);
				lookupString =
						pcgen.io.ExportHandler.getTokenString(this,
							lookupString);
				aString =
						aString.substring(0, startIdx) + lookupString
							+ aString.substring(endIdx + 2);
			}
			total = getVariableValue(aString, "").intValue();
		}
		else
		{
			final double iConMod = getStatBonusTo("HP", "BONUS");

			for (PCClass pcClass : getClassSet())
			{
				total += getClassHitPoints(pcClass, (int) iConMod);
			}

		}
		total += (int) getTotalBonusTo("HP", "CURRENTMAX");

		//
		// now we see if this PC is a Familiar
		final PlayerCharacter nPC = getMasterPC();

		if (nPC == null)
		{
			return total;
		}

		if (getCopyMasterHP().length() == 0)
		{
			return total;
		}
		//
		// In order for the BONUS's to work, the PC we want
		// to get the hit points for must be the "current" one.
		//
		int masterHP = nPC.hitPoints();

		final String copyMasterHP =
				replaceMasterString(getCopyMasterHP(), masterHP);
		masterHP = getVariableValue(copyMasterHP, "").intValue();

		return masterHP;
	}

	private int getClassHitPoints(PCClass pcClass, int iConMod)
	{
		int total = 0;

		for (int i = 0; i <= getLevel(pcClass); ++i)
		{
			PCClassLevel pcl = getActiveClassLevel(pcClass, i);
			Integer hp = getHP(pcl);
			if (hp != null && hp > 0)
			{
				int iHp = hp + iConMod;

				if (iHp < 1)
				{
					iHp = 1;
				}

				total += iHp;
			}
		}

		return total;
	}

	/**
	 * Check to see if this PC should ignore Encumbrance for a specified load
	 * (Constants.HEAVY_LOAD, etc) If the check is more than the testing type,
	 * return true
	 * 
	 * @param load
	 * @return true or false
	 */
	public boolean ignoreEncumberedLoadMove(final Load load)
	{
		return unencumberedLoadFacet.ignoreLoad(id, load);
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character. there is no way to bypass the prerequisites
	 * with this method, also this method does not print warning messages see:
	 * incrementClassLevel(int, PCClass, boolean, boolean);
	 * 
	 * @param mod
	 *            the number of levels to add/remove
	 * @param aClass
	 *            the class to adjust
	 */
	public void incrementClassLevel(final int mod, final PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
		setDirty(true);
	}

	/**
	 * Initiative Modifier
	 * 
	 * @return initiative modifier
	 */
	public int initiativeMod()
	{
		return initiativeFacet.getInitiative(id);
	}

	/**
	 * Calculates the number of languages that the character is qualified 
	 * for.
	 *  
	 * @return The number of languages allowed
	 */
	public int getBonusLanguageCount()
	{
		int i = Math.max(0, (int) getStatBonusTo("LANG", "BONUS"));
		if (getRace() != null)
		{
			i += getTotalBonusTo("LANGUAGES", "NUMBER");
		}
		return i;
	}

	/**
	 * Lists all the tokens that match prefix with associated values
	 * 
	 * @param bonusType
	 * @param bonusName
	 * @return String TODO - Not sure what this is trying to do.
	 */
	public String listBonusesFor(String bonusType, String bonusName)
	{
		return bonusManager.listBonusesFor(bonusType, bonusName);
	}

	public boolean loadDescriptionFilesInDirectory(final String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			@Override
			public boolean accept(final File parentDir, final String fileName)
			{
				final File descriptionFile = new File(parentDir, fileName);

				if (PCGFile.isPCGenListFile(descriptionFile))
				{
					BufferedReader descriptionReader = null;

					try
					{
						if (descriptionFile.exists())
						{
							final char[] inputLine;

							// final BufferedReader descriptionReader = new
							// BufferedReader(new FileReader(descriptionFile));
							descriptionReader =
									new BufferedReader(new InputStreamReader(
										new FileInputStream(descriptionFile),
										"UTF-8"));

							final int length = (int) descriptionFile.length();
							inputLine = new char[length];
							descriptionReader.read(inputLine, 0, length);
							setDescriptionLst(getDescriptionLst()
								+ new String(inputLine));
						}
					}
					catch (IOException exception)
					{
						Logging
							.errorPrint(
								"IOException in PlayerCharacter.loadDescriptionFilesInDirectory",
								exception);
					}
					finally
					{
						if (descriptionReader != null)
						{
							try
							{
								descriptionReader.close();
							}
							catch (IOException e)
							{
								Logging
									.errorPrint(
										"Couldn't close descriptionReader in PlayerCharacter.loadDescriptionFilesInDirectory",
										e);

								// Not much to do...
							}
						}
					}
				}
				else if (parentDir.isDirectory())
				{
					loadDescriptionFilesInDirectory(parentDir.getPath()
						+ File.separator + fileName);
				}

				return false;
			}
		});

		return false;
	}

	public void makeIntoExClass(final PCClass aClass)
	{
		CDOMSingleRef<PCClass> exc = aClass.get(ObjectKey.EX_CLASS);

		try
		{
			PCClass cl = exc.resolvesTo();
			PCClass bClass = getClassKeyed(cl.getKeyName());

			if (bClass == null)
			{
				bClass = cl.clone(); //Still required :(

				rebuildLists(bClass, aClass, getLevel(aClass), this);

				bClass.setLevel(getLevel(aClass), this);
				for (int i = 0; i < getLevel(aClass); ++i)
				{
					PCClassLevel frompcl = getActiveClassLevel(aClass, i + 1);
					Integer hp = getHP(frompcl);
					PCClassLevel topcl = getActiveClassLevel(bClass, i + 1);
					setHP(topcl, hp);
				}

				classFacet.replaceClass(id, aClass, bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, getLevel(aClass), this);
				bClass.setLevel(getLevel(bClass) + getLevel(aClass),
					this);

				for (int i = 0; i < getLevel(aClass); ++i)
				{
					PCClassLevel frompcl = getActiveClassLevel(aClass, i + 1);
					Integer hp = getHP(frompcl);
					PCClassLevel topcl =
							getActiveClassLevel(bClass, getLevel(bClass) + i + 1);
					setHP(topcl, hp);
				}

				classFacet.removeClass(id, aClass);
			}

			//
			// change all the levelling info to the ex-class as well
			//
			for (int idx = getLevelInfoSize() - 1; idx >= 0; --idx)
			{
				final PCLevelInfo li = levelInfoFacet.get(id, idx);

				if (li.getClassKeyName().equals(aClass.getKeyName()))
				{
					li.setClassKeyName(bClass.getKeyName());
				}
			}

			//
			// Find all skills associated with old class and link them to new
			// class
			//
			for (Skill skill : getSkillSet())
			{
				SkillRankControl.replaceClassRank(this, skill, aClass
					.getKeyName(), cl.getKeyName());
			}

			setAssoc(bClass, AssociationKey.SKILL_POOL, aClass
				.getSkillPool(this));
		}
		catch (NumberFormatException nfe)
		{
			ShowMessageDelegate.showMessageDialog(nfe.getMessage(),
				Constants.APPLICATION_NAME, MessageType.INFORMATION);
		}
	}

	public int minXPForECL()
	{
		return levelTableFacet.minXPForLevel(getECL(), id);
	}

	public int minXPForNextECL()
	{
		return levelTableFacet.minXPForLevel(getECL() + 1, id);
	}

	public int miscAC()
	{
		return armorClassFacet.calcACOfType(id, "Misc");
	}

	/**
	 * Apply any modifications to attack rolls from wearing armour the 
	 * PC is not proficient in.
	 */
	public int modFromArmorOnWeaponRolls()
	{
		int bonus = 0;

		/*
		 * Equipped some armor that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : getEquipmentOfType("Armor", 1))
		{
			if ((eq != null) && (!isProficientWith(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		/*
		 * Equipped a shield that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : getEquipmentOfType("Shield", 1))
		{
			if ((eq != null) && (!isProficientWith(eq)))
			{
				bonus += eq.acCheck(this).intValue();
			}
		}

		return bonus;
	}

	/**
	 * Figure out if Load should affect AC and Skills, if so, set the load
	 * appropriately, otherwise set a light load to eliminate the effects of
	 * heavier loads
	 * 
	 * @return a loadType appropriate for this Pc
	 */
	private Load getHouseRuledLoadType()
	{
		if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			return getLoadType();
		}
		return Load.LIGHT;
	}

	/**
	 * Calculate the AC bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * @return PC's AC bonus from equipment
	 */
	private int modToACFromEquipment()
	{
		int bonus = 0;
		for (Equipment eq : getEquippedEquipmentSet())
		{
			bonus += eq.getACMod(this).intValue();
		}
		return bonus;
	}

	/**
	 * Calculate the ACCHECK bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * TODO Penalty for load could/should be GameMode specific?
	 * 
	 * @return PC's ACCHECK bonus from equipment
	 */
	private int modToACCHECKFromEquipment()
	{
		Load load = getHouseRuledLoadType();
		int bonus = 0;

		int penaltyForLoad =
				(Load.MEDIUM == load) ? -3 : (Load.HEAVY == load) ? -6 : 0;

		for (Equipment eq : getEquippedEquipmentSet())
		{
			bonus += eq.acCheck(this).intValue();
		}

		bonus = Math.min(bonus, penaltyForLoad);
		
		// TODO Would be nice to one day explicitly have this as a ACCHECK type of 'bonus' 
		// as opposed to MISC
		bonus += (int) getTotalBonusTo("MISC", "ACCHECK");
		return bonus;
	}

	/**
	 * Calculate the SpellFailure bonus from equipped items. Extracted from
	 * modToFromEquipment.
	 * 
	 * @return PC's SpellFailure bonus from equipment
	 */
	private int modToSpellFailureFromEquipment()
	{
		int bonus = 0;
		for (Equipment eq : getEquippedEquipmentSet())
		{
			bonus += eq.spellFailure(this).intValue();
		}
		bonus += (int) getTotalBonusTo("MISC", "SPELLFAILURE");
		return bonus;
	}

	/**
	 * Calculate the MAXDEX bonus taking account of equipped items. Extracted
	 * from modToFromEquipment.
	 * 
	 * @return MAXDEX bonus
	 */
	private int modToMaxDexFromEquipment()
	{
		final int statBonus = (int) getStatBonusTo("MISC", "MAXDEX");
		final Load load = getHouseRuledLoadType();
		int bonus =
				(load == Load.MEDIUM) ? 3 : (load == Load.HEAVY) ? 1
					: (load == Load.OVERLOAD) ? 0 : statBonus;

		// If this is still true after all the equipment has been
		// examined, then we should use the Maximum - Maximum Dex modifier.
		boolean useMax = (load == Load.LIGHT);

		for (Equipment eq : getEquippedEquipmentSet())
		{
			final int potentialMax = eq.getMaxDex(this).intValue();
			if (potentialMax != Constants.MAX_MAXDEX)
			{
				if (useMax || bonus > potentialMax)
				{
					bonus = potentialMax;
				}
				useMax = false;
			}
		}

		if (useMax)
		{
			bonus = Constants.MAX_MAXDEX;
		}

		bonus += ((int) getTotalBonusTo("MISC", "MAXDEX") - statBonus);

		if (bonus < 0)
		{
			bonus = 0;
		}
		else if (bonus > Constants.MAX_MAXDEX)
		{
			bonus = Constants.MAX_MAXDEX;
		}
		return bonus;
	}

	/**
	 * Calculate the MAXDEX or ACCHECK or SPELLFAILURE or AC bonus from all currently
	 * equipped items.
	 * 
	 * @param typeName The type of modification we're trying to calculate
	 * @return The calculation from the equipment or if the typeName doesn't match then 0
	 */
	public int modToFromEquipment(final String typeName)
	{
		if (typeName.equals("AC"))
		{
			return modToACFromEquipment();
		}
		if (typeName.equals("ACCHECK"))
		{
			return modToACCHECKFromEquipment();
		}
		if (typeName.equals("MAXDEX"))
		{
			return modToMaxDexFromEquipment();
		}
		if (typeName.equals("SPELLFAILURE"))
		{
			return modToSpellFailureFromEquipment();
		}
		return 0;
	}

	public double multiclassXPMultiplier()
	{
		return multiClassFacet.getMultiClassXPMultiplier(id);
	}

	public boolean hasAnyFavoredClass()
	{
		return hasAnyFavoredFacet.contains(id, Boolean.TRUE);
	}

	public int naturalAC()
	{
		return armorClassFacet.calcACOfType(id, "NaturalArmor");
	}

	/**
	 * Takes a String and a Class name and computes spell based variable such as
	 * Class level.
	 * 
	 * @param aSpell The spell object
	 * @param aString the variable to evaluate
	 * @return String
	 */
	public String parseSpellString(
		final CharacterSpell aSpell,
		String aString)
	{
		String aSpellClass = aSpell.getVariableSource(this);

		if (aSpellClass.length() == 0)
		{
			return aString;
		}

		// Only want to replace items between ()'s
		while (aString.lastIndexOf('(') >= 0)
		{
			boolean found = false;

			final int start = aString.indexOf('(');
			int end = 0;
			int level = 0;

			for (int i = start; i < aString.length(); i++)
			{
				if (aString.charAt(i) == '(')
				{
					level++;
				}
				else if (aString.charAt(i) == ')')
				{
					level--;
					if (level == 0)
					{
						end = i;
						break;
					}
				}
			}

			/*
			 * int x = CoreUtility.innerMostStringStart(aString); int y =
			 * CoreUtility.innerMostStringEnd(aString); // bounds checking if
			 * ((start > end) || (start >= aString.length())) { break; } if
			 * ((end <= 0) || (end >= aString.length())) { break; }
			 */
			final String inCalc = aString.substring(start + 1, end);

			String replacement = "0";

			final Float fVal = getVariableValue(aSpell, inCalc, aSpellClass);
			if (!CoreUtility.doublesEqual(fVal.floatValue(), 0.0f))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			else if ((inCalc.indexOf("MIN") >= 0)
				|| (inCalc.indexOf("MAX") >= 0))
			{
				found = true;
				replacement = fVal.intValue() + "";
			}
			else if (inCalc.toUpperCase().indexOf("MIN(") >= 0
				|| inCalc.toUpperCase().indexOf("MAX(") >= 0)
			{
				found = true;
				replacement = fVal.intValue() + "";
			}

			if (found)
			{
				aString =
						aString.substring(0, start) + replacement
							+ aString.substring(end + 1);
			}
			else
			{
				aString =
						aString.substring(0, start) + "[" + inCalc + "]"
							+ aString.substring(end + 1);
			}
		}

		return aString;
	}

	/**
	 * Populate the characters skills list with skill that the character does
	 * not have ranks in according to the required level. The levels are defined
	 * in constants in the Skill class, but are None, Untrained or All.
	 * 
	 * @param level
	 *            The level of extra skills to be added.
	 */
	public void populateSkills(final int level)
	{
		removeExcessSkills(level);
		addNewSkills(level);

		// Now regenerate the output order
		final int sort;
		final boolean sortOrder;

		switch (getSkillsOutputOrder())
		{
			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_DSC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_ASC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_DSC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			default:

				// Manual sort, or unrecognised, so do no sorting.
				return;
		}

		final List<Skill> localSkillList = new ArrayList<Skill>(getSkillSet());
		final SkillComparator comparator =
				new SkillComparator(this, sort, sortOrder);
		int nextOutputIndex = 1;
		Collections.sort(localSkillList, comparator);

		for (Skill skill : localSkillList)
		{
			Integer outputIndex =
					this.getAssoc(skill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex == null || outputIndex >= 0)
			{
				this.setAssoc(skill, AssociationKey.OUTPUT_INDEX,
					nextOutputIndex++);
			}
		}
	}

	/**
	 * Removes a "temporary" bonus
	 * 
	 * @param aBonus
	 */
	public void removeTempBonus(final BonusObj aBonus)
	{
		bonusManager.removeTempBonus(aBonus);
		setDirty(true);
	}

	public void removeTempBonusItemList(final Equipment aEq)
	{
		getTempBonusItemList().remove(aEq);
		setDirty(true);
	}

	public void removeTemplate(final PCTemplate inTmpl)
	{
		if (inTmpl == null)
		{
			return;
		}

		templateFacet.remove(id, inTmpl);

		// karianna 1184888
		adjustMoveRates();

		// re-evaluate non-spellcaster spell lists
		getSpellList();
		calcActiveBonuses();
		setDirty(true);
	}

	private String replaceMasterString(String aString, final int aNum)
	{
		while (true)
		{
			final int x = aString.indexOf("MASTER");

			if (x == -1)
			{
				break;
			}

			final String leftString = aString.substring(0, x);
			final String rightString = aString.substring(x + 6);
			aString = leftString + Integer.toString(aNum) + rightString;
		}

		return aString;
	}

	public PCLevelInfo addLevelInfo(final String classKeyName)
	{
		final PCLevelInfo li = new PCLevelInfo(classKeyName);
		addLevelInfo(li);
		return li;
	}

	public void addLevelInfo(final PCLevelInfo pli)
	{
		levelInfoFacet.add(id, pli);
	}

	public void saveStatIncrease(final PCStat stat, final int mod,
		final boolean isPreMod)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			levelInfoFacet.get(id, idx).addModifiedStat(stat, mod, isPreMod);
		}

		setDirty(true);
	}

	public int getStatIncrease(final PCStat stat, final boolean includePost)
	{
		final int idx = getLevelInfoSize() - 1;

		if (idx >= 0)
		{
			return levelInfoFacet.get(id, idx).getTotalStatMod(stat, includePost);
		}
		return 0;
	}

	public int sizeAC()
	{
		return armorClassFacet.calcACOfType(id, "Size");
	}

	public int sizeInt()
	{
		return sizeFacet.sizeInt(id);
	}

	public int totalHitDice()
	{
		return levelFacet.getMonsterLevelCount(id);
	}

	public int totalNonMonsterLevels()
	{
		return levelFacet.getNonMonsterLevelCount(id);
	}

	public BigDecimal totalValue()
	{
		BigDecimal totalValue = BigDecimal.ZERO;

		for (Equipment eq : getEquipmentMasterList())
		{
			totalValue =
					totalValue.add(eq.getCost(this).multiply(
						new BigDecimal(eq.qty())));
		}

		return totalValue;
	}

	public Float totalWeight()
	{
		return totalWeightFacet.getTotalWeight(id);
	}

	public int touchAC()
	{
		return armorClassFacet.calcACOfType(id, "Touch");
	}

	/**
	 * replaces oldItem with newItem in all EquipSets
	 * 
	 * @param oldItem
	 * @param newItem
	 */
	public void updateEquipSetItem(final Equipment oldItem,
		final Equipment newItem)
	{
		equipSetFacet.updateEquipSetItem(id, oldItem, newItem);
		setDirty(true);
	}

	/**
	 * Gets whether the character has been changed since last saved.
	 * 
	 * @return true or false
	 */
	public boolean wasEverSaved()
	{
		return !Constants.EMPTY_STRING.equals(getFileName());
	}

	/**
	 * @return true if character is currently being read from file.
	 */
	public boolean isImporting()
	{
		return importing;
	}

	public void giveClassesAway(final PCClass toClass, final PCClass fromClass,
		int iCount)
	{
		if ((toClass == null) || (fromClass == null))
		{
			return;
		}

		// Will take destination class over maximum?
		if (toClass.hasMaxLevel()
			&& (getLevel(toClass) + iCount) > toClass
				.getSafe(IntegerKey.LEVEL_LIMIT))
		{
			iCount =
					toClass.getSafe(IntegerKey.LEVEL_LIMIT)
						- getLevel(toClass);
		}

		// Enough levels to move?
		if ((getLevel(fromClass) <= iCount) || (iCount < 1))
		{
			return;
		}

		final int iFromLevel = getLevel(fromClass) - iCount;
		final int iToLevel = getLevel(toClass);

		toClass.setLevel(iToLevel + iCount, this);

		for (int i = 0; i < iCount; ++i)
		{
			PCClassLevel frompcl = getActiveClassLevel(fromClass, iFromLevel + i);
			Integer hp = getHP(frompcl);
			PCClassLevel topcl = getActiveClassLevel(toClass, iToLevel + i);
			setHP(topcl, hp);
			setHP(frompcl, Integer.valueOf(0));
		}

		rebuildLists(toClass, fromClass, iCount, this);

		fromClass.setLevel(iFromLevel, this);

		// first, change the toClass current PCLevelInfo level
		for (PCLevelInfo pcl : getLevelInfo())
		{
			if (pcl.getClassKeyName().equals(toClass.getKeyName()))
			{
				final int iTo =
						(pcl.getClassLevel() + getLevel(toClass)) - iToLevel;
				pcl.setClassLevel(iTo);
			}
		}

		// change old class PCLevelInfo to the new class
		for (PCLevelInfo pcl : getLevelInfo())
		{
			if (pcl.getClassKeyName().equals(fromClass.getKeyName())
				&& (pcl.getClassLevel() > iFromLevel))
			{
				final int iFrom = pcl.getClassLevel() - iFromLevel;
				pcl.setClassKeyName(toClass.getKeyName());
				pcl.setClassLevel(iFrom);
			}
		}

		/*
		 * // get skills associated with old class and link to new class for
		 * (Iterator e = getSkillList().iterator(); e.hasNext();) { Skill aSkill =
		 * (Skill) e.next(); aSkill.replaceClassRank(fromClass.getName(),
		 * toClass.getName()); } toClass.setSkillPool(fromClass.getSkillPool());
		 */
	}

	public void addFreeLanguage(final Language aLang, CDOMObject source)
	{
		freeLangFacet.add(id, aLang, source);
		setDirty(true);
	}

	public void removeFreeLanguage(final Language aLang, CDOMObject source)
	{
		freeLangFacet.remove(id, aLang, source);
		setDirty(true);
	}

	public void addAddLanguage(final Language aLang, CDOMObject source)
	{
		addLangFacet.add(id, aLang, source);
		setDirty(true);
	}

	public void removeAddLanguage(final Language aLang, CDOMObject source)
	{
		addLangFacet.remove(id, aLang, source);
		setDirty(true);
	}

	public Set<Language> getSkillLanguages()
	{
		return skillLangFacet.getSet(id);
	}

	public void addSkillLanguage(final Language aLang, CDOMObject source)
	{
		skillLangFacet.add(id, aLang, source);
		setDirty(true);
	}

	public void removeSkillLanguage(final Language aLang, CDOMObject source)
	{
		skillLangFacet.remove(id, aLang, source);
		setDirty(true);
	}

	/**
	 * Scan through the list of domains the character has to ensure that they
	 * are all still valid. Any invalid domains will be removed from the
	 * character.
	 */
	void validateCharacterDomains()
	{
		if (!isImporting())
		{
			getSpellList();
		}

		//Clone to avoid Concurrent Mod Exception, CODE-153
		for (Domain d : new ArrayList<Domain>(domainFacet.getSet(id)))
		{
			if (!isDomainValid(d, this.getDomainSource(d)))
			{
				removeDomain(d);
			}
		}
	}

	private boolean isDomainValid(Domain domain, ClassSource cs)
	{
		if (domain == null)
		{
			return false;
		}
		final PCClass aClass = getClassKeyed(cs.getPcclass().getKeyName());
		return  ((aClass != null) && (getLevel(aClass) >= cs.getLevel()));
	}

	/**
	 * Active BonusObjs
	 * 
	 * @return List
	 */
	public Collection<BonusObj> getActiveBonusList()
	{
		return bonusManager.getActiveBonusList();
	}

	private synchronized void setClassLevelsBrazenlyTo(
		final Map<String, Integer> lvlMap)
	{
		// set class levels to class name,level pair
		for (PCClass pcClass : getClassSet())
		{
			Integer lvl = lvlMap.get(pcClass.getKeyName());
			int setLevel = (lvl == null) ? 0 : lvl;
			setLevelWithoutConsequence(pcClass, setLevel);
		}
		// Recalculate bonuses, based on new level
		calcActiveBonuses();
		// setDirty(true);
	}

	private String getDisplayClassName()
	{
		ArrayList<PCClass> classList = getClassList();
		return (classFacet.isEmpty(id) ? "Nobody" : classList.get(
			classList.size() - 1).getDisplayClassName(this));
	}

	private String getDisplayRaceName()
	{
		final String raceName = getRace().toString();

		return (raceName.equals(Constants.NONESELECTED) ? "Nothing"
			: raceName);
	}

	/**
	 * Parses through all Equipment items and calculates total Bonus
	 * 
	 * @param aType
	 * @param aName
	 * @return equipment bonus to
	 */
	private double getEquipmentBonusTo(String aType, String aName)
	{
		double bonus = 0;

		if (!hasEquipment())
		{
			return bonus;
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		for (Equipment eq : getEquippedEquipmentSet())
		{
			final List<BonusObj> tempList =
					eq.getBonusListOfType(this, aType, aName, true);

			if (eq.isWeapon() && eq.isDouble())
			{
				tempList.addAll(eq.getBonusListOfType(this, aType, aName,
					false));
			}

			bonus += calcBonusFromList(tempList, eq);
		}

		return bonus;
	}

	private String getFullDisplayClassName()
	{
		if (classFacet.isEmpty(id))
		{
			return "Nobody";
		}

		final StringBuffer buf = new StringBuffer();

		boolean first = true;
		for (PCClass c : getClassSet())
		{
			if (!first)
			{
				buf.append('/');
				first = false;
			}
			buf.append(c.getFullDisplayClassName(this));
		}

		return buf.toString();
	}

	/**
	 * Return a hashmap of the first maxCharacterLevel character levels that a
	 * character has taken This will be a hash of "Class name"=>"number of
	 * levels as a string". For example, {"Fighter"=>"2", "Cleric":"16"}
	 * 
	 * @param maxCharacterLevel
	 *            the maximum character level that we can include in this map
	 * @return character level map
	 */
	private Map<String, Integer> getCharacterLevelHashMap(
		final int maxCharacterLevel)
	{
		final Map<String, Integer> lvlMap = new HashMap<String, Integer>();

		int characterLevels = 0;
		for (int i = 0; i < getLevelInfoSize(); ++i)
		{
			final String classKeyName = getLevelInfoClassKeyName(i);
			final PCClass aClass =
					Globals.getContext().ref.silentlyGetConstructedCDOMObject(
						PCClass.class, classKeyName);

			if (aClass.isMonster() || characterLevels < maxCharacterLevel)
			{
				// we can use this class level if it is a monster level, or if
				// we have not yet hit our maximum number of characterLevels
				Integer val = lvlMap.get(classKeyName);
				Integer newVal = (val == null) ? Integer.valueOf(1) : (val + 1);
				lvlMap.put(classKeyName, newVal);
			}

			if (!aClass.isMonster())
			{
				// If the class level was not a monster level then it counts
				// towards the total number of character levels
				characterLevels++;
			}
		}

		return lvlMap;
	}

	public int getNumAttacks()
	{
		return Math.min(Math.max(baseAttackBonus() / 5, 4), 1);
	}

	private String getOrdinal(final int cardinal)
	{
		switch (cardinal)
		{
			case 1:
				return "st";

			case 2:
				return "nd";

			case 3:
				return "rd";

			default:
				return "th";
		}
	}

	/**
	 * Returns a bonus.
	 * 
	 * @param aList
	 * @param aType
	 * @param aName
	 * @return double
	 */
	private double getPObjectWithCostBonusTo(
		final Collection<? extends CDOMObject> aList, final String aType,
		final String aName)
	{
		double iBonus = 0;

		if (aList.isEmpty())
		{
			return iBonus;
		}

		for (CDOMObject anObj : aList)
		{
			final List<BonusObj> tempList = BonusUtilities.getBonusFromList(
					anObj.getBonusList(this), aType, aName);
			iBonus += calcBonusWithCostFromList(tempList);
		}

		return iBonus;
	}

	private HashMap<String, Integer> getTotalLevelHashMap()
	{
		final HashMap<String, Integer> lvlMap = new HashMap<String, Integer>();

		for (PCClass aClass : getClassSet())
		{
			lvlMap.put(aClass.getKeyName(), getLevel(aClass));
		}

		return lvlMap;
	}

	/**
	 * Get the class level as a String
	 * 
	 * @param aClassKey
	 * @param doReplace
	 * @return class level as String
	 */
	public String getClassLevelString(String aClassKey, final boolean doReplace)
	{
		int lvl = 0;
		int idx = aClassKey.indexOf(";BEFORELEVEL=");

		if (idx < 0)
		{
			idx = aClassKey.indexOf(";BEFORELEVEL.");
		}

		if (idx > 0)
		{
			lvl = Integer.parseInt(aClassKey.substring(idx + 13));
			aClassKey = aClassKey.substring(0, idx);
		}

		if (doReplace)
		{
			aClassKey = aClassKey.replace('{', '(').replace('}', ')');
		}

		if (aClassKey.startsWith("TYPE=") || aClassKey.startsWith("TYPE."))
		{
			int totalLevels = 0;
			String[] classTypes = aClassKey.substring(5).split("\\.");
			CLASSFOR: for (PCClass cl : getClassSet())
			{
				for (String type : classTypes)
				{
					if (!cl.isType(type))
					{
						continue CLASSFOR;
					}
					if (lvl > 0)
					{
						totalLevels += getLevelBefore(cl.getKeyName(), lvl);
					}

					totalLevels += getLevel(cl);
				}
			}
			return Integer.toString(totalLevels);
		}
		else
		{
			final PCClass aClass = getClassKeyed(aClassKey);

			if (aClass != null)
			{
				if (lvl > 0)
				{
					return Integer.toString(getLevelBefore(aClass.getKeyName(),
						lvl));
				}

				return Integer.toString(getLevel(aClass));
			}

			return "0";
		}
	}

	public int getLevelBefore(final String classKey, final int charLevel)
	{
		String thisClassKey;
		int lvl = 0;

		for (int idx = 0; idx < charLevel; ++idx)
		{
			thisClassKey = getLevelInfoClassKeyName(idx);

			if (thisClassKey.length() == 0)
			{
				break;
			}

			if (thisClassKey.equals(classKey))
			{
				++lvl;
			}
		}

		return lvl;
	}

	List<? extends CDOMObject> getCDOMObjectList()
	{
		List<CDOMObject> list = new ArrayList<CDOMObject>();
		for (CDOMObject po : getPObjectList())
		{
			if (po != null)
			{
				list.add(po);
			}
		}
		for (PCClass cl : getClassSet())
		{
			for (int i = 1; i <= getLevel(cl); i++)
			{
				PCClassLevel classLevel = getActiveClassLevel(cl, i);
				list.add(classLevel);
			}
		}
		list.addAll(getConditionalTemplateObjects());
		return list;
	}

	private Collection<PCTemplate> getConditionalTemplateObjects()
	{
		return conditionalTemplateFacet.getSet(id);
	}

	private List<? extends PObject> getPObjectList()
	{
		// Possible object types include:
		// Campaigns
		// Alignment (PCAlignment)
		// BioSet (ageSet)
		// Check (PObject)
		// Class (PCClass)
		// CompanionMod
		// Deity
		// Domain (CharacterDomain)
		// Equipment (includes EqMods)
		// Feat (virtual feats, auto feats)
		// Race
		// SizeAdjustment
		// Skill
		// Stat (PCStat)
		// Template (PCTemplate)
		//

		final ArrayList<PObject> results = new ArrayList<PObject>();

		// Loaded campaigns
		results.addAll(expandedCampaignFacet.getSet(id));

		// Alignment
		PCAlignment align = alignmentFacet.get(id);
		if (align != null)
		{
			results.add(align);
		}

		// armorProfList is still just a list of Strings
		// results.addAll(getArmorProfList());
		// BioSet
		results.add(bioSetFacet.get(id));

		results.addAll(checkFacet.getSet(id));

		// Class
		results.addAll(classFacet.getClassSet(id));

		// CompanionMod
		results.addAll(companionModFacet.getSet(id));

		// Deity
		Deity deity = deityFacet.get(id);
		if (deity != null)
		{
			results.add(deity);
		}

		// Domain
		results.addAll(domainFacet.getSet(id));

		// Equipment
		for (Equipment eq : activeEquipmentFacet.getSet(id))
		{
			results.add(eq);

			for (EquipmentModifier eqMod : eq.getEqModifierList(true))
			{
				results.add(eqMod);
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(false))
			{
				results.add(eqMod);
			}
		}

		// Feats and abilities (virtual feats, auto feats)
		List<Ability> abilities = getFullAbilityList();
		results.addAll(abilities);

		// Race
		Race race = raceFacet.get(id);
		if (race != null)
		{
			results.add(race);
		}

		// SizeAdjustment
		SizeAdjustment sa = sizeFacet.getSizeAdjustment(id);
		if (sa != null)
		{
			results.add(sa);
		}

		// Skill
		results.addAll(skillFacet.getSet(id));

		// Stat (PCStat)
		results.addAll(statFacet.getSet(id));

		// Template (PCTemplate)
		results.addAll(templateFacet.getSet(id));

		// weaponProfList is still just a list of Strings
		// results.addAll(getWeaponProfList());
		return results;
	}

	/**
	 * @param level
	 */
	private void addNewSkills(final int level)
	{
		final List<Skill> addItems = new ArrayList<Skill>();
		final List<Skill> skillList = new ArrayList<Skill>(getSkillSet());

		for (Skill aSkill : Globals.getContext().ref
			.getConstructedCDOMObjects(Skill.class))
		{
			if (includeSkill(aSkill, level) && !skillList.contains(aSkill))
			{
				addItems.add(aSkill);
			}
		}

		skillFacet.addAll(id, addItems);
		// setDirty(true);
	}

	/**
	 * availableSpells sk4p 13 Dec 2002
	 * 
	 * For learning or preparing a spell: Are there slots available at this
	 * level or higher Fixes BUG [569517]
	 * 
	 * @param level
	 *            the level being checked for availability
	 * @param aClass
	 *            the class under consideration
	 * @param bookName
	 *            the name of the spellbook
	 * @param knownLearned
	 *            "true" if this is learning a spell, "false" if prepping
	 * @param isSpecialtySpell
	 *            "true" if this is a speciality for the given class
	 * @return true or false, a new spell can be added
	 */
	public boolean availableSpells(final int level, final PCClass aClass,
		final String bookName, final boolean knownLearned,
		final boolean isSpecialtySpell)
	{
		boolean available = false;
		final boolean isDivine =
				("Divine".equalsIgnoreCase(aClass.get(StringKey.SPELLTYPE)));
		final boolean canUseHigher =
				knownLearned ? getUseHigherKnownSlots()
					: getUseHigherPreppedSlots();
		int knownTot;
		int knownNon;
		int knownSpec;
		int memTot;
		int memNon;
		int memSpec;

		// int excTot
		int excNon;

		// int excTot
		int excSpec;
		int lowExcSpec = 0;
		int lowExcNon = 0;
		int goodExcSpec = 0;
		int goodExcNon = 0;

		for (int i = 0; i < level; ++i)
		{
			// Get the number of castable slots
			if (knownLearned)
			{
				knownNon = this.getSpellSupport(aClass).getKnownForLevel(i, bookName, this);
				knownSpec = this.getSpellSupport(aClass).getSpecialtyKnownForLevel(i, this);
				knownTot = knownNon + knownSpec; // TODO: : value never used
			}
			else
			{
				// Get the number of castable slots
				knownTot =
						this.getSpellSupport(aClass).getCastForLevel(i, bookName, true, true, this);
				knownNon =
						this.getSpellSupport(aClass).getCastForLevel(i, bookName, false, true, this);
				knownSpec = knownTot - knownNon;
			}

			// Now get the number of spells memorised, total and specialities
			memTot = SpellCountCalc.memorizedSpellForLevelBook(this, aClass, i, bookName);
			memSpec =
					SpellCountCalc.memorizedSpecialtiesForLevelBook(i, bookName, this, aClass);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while ((excNon > 0) && (lowExcNon < 0))
			{
				--excNon;
				++lowExcNon;
			}

			while ((excSpec > 0) && (lowExcSpec < 0))
			{
				--excSpec;
				++lowExcSpec;
			}

			if (!isDivine || knownLearned)
			{
				// If I'm not divine, I can use non-specialty slots of this
				// level
				// to take up the slack of my excess speciality spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And I can use non-specialty slots of this level to take
				// up the slack of my excess speciality spells of this level.
				//
				while ((excNon > 0) && (excSpec < 0))
				{
					--excNon;
					++excSpec;
				}
			}

			// Now, if there are slots left over, I don't add them to the
			// running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				lowExcSpec += excSpec;
			}

			if (excNon < 0)
			{
				lowExcNon += excNon;
			}
		}

		for (int i = level; i <= Constants.MAX_SPELL_LEVEL; ++i)
		{
			if (knownLearned)
			{
				knownNon = this.getSpellSupport(aClass).getKnownForLevel(i, bookName, this);
				knownSpec = this.getSpellSupport(aClass).getSpecialtyKnownForLevel(i, this);
				knownTot = knownNon + knownSpec; // for completeness
			}
			else
			{
				// Get the number of castable slots
				knownTot =
						this.getSpellSupport(aClass).getCastForLevel(i, bookName, true, true, this);
				knownNon =
						this.getSpellSupport(aClass).getCastForLevel(i, bookName, false, true, this);
				knownSpec = knownTot - knownNon;
			}

			// At the level currently being looped through, if the number of
			// casts
			// is zero, that means we have reached a level beyond which no
			// higher-level
			// casts are possible. Therefore, it's time to break.
			// Likewise if we aren't allowed to use higher level slots, no sense
			// in
			// going higher than the spell's level.
			//
			if (!canUseHigher && i > level)
			{
				break;
			}

			// Now get the number of spells memorised, total and specialities
			memTot = SpellCountCalc.memorizedSpellForLevelBook(this, aClass, i, bookName);
			memSpec =
					SpellCountCalc.memorizedSpecialtiesForLevelBook(i, bookName, this, aClass);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while ((excNon > 0) && (lowExcNon < 0))
			{
				--excNon;
				++lowExcNon;
			}

			while ((excNon > 0) && (goodExcNon < 0))
			{
				--excNon;
				++goodExcNon;
			}

			while ((excSpec > 0) && (lowExcSpec < 0))
			{
				--excSpec;
				++lowExcSpec;
			}

			while ((excSpec > 0) && (goodExcSpec < 0))
			{
				--excSpec;
				++goodExcSpec;
			}

			if (!isDivine)
			{
				// If I'm not divine, I can use non-specialty slots of this
				// level
				// to take up the slack of my excess speciality spells from
				// lower levels.
				while ((excNon > 0) && (lowExcSpec < 0))
				{
					--excNon;
					++lowExcSpec;
				}

				// And also for levels sufficiently high for the spell that got
				// me
				// into this mess, but of lower level than the level currently
				// being calculated.
				while ((excNon > 0) && (goodExcSpec < 0))
				{
					--excNon;
					++goodExcSpec;
				}

				// And finally use non-specialty slots of this level to take
				// up the slack of excess speciality spells of this level.
				//
				while ((excNon > 0) && (excSpec < 0))
				{
					--excNon;
					++excSpec;
				}
			}

			// Right now, if there are slots left over at this level,
			// it means that there are slots left to add the spell that started
			// all of this.
			if (isDivine)
			{
				if (isSpecialtySpell && (excSpec > 0))
				{
					available = true;
				}

				if (!isSpecialtySpell && (excNon > 0))
				{
					available = true;
				}
			}
			else
			{
				if (!isSpecialtySpell && (excNon > 0))
				{
					available = true;
				}

				if (isSpecialtySpell && ((excNon > 0) || (excSpec > 0)))
				{
					available = true;
				}
			}

			// If we found a slot, we need look no further.
			if (available)
			{
				break;
			}

			// Now, if there are slots left over, I don't add them to the
			// running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				goodExcSpec += excSpec;
			}

			if (excNon < 0)
			{
				goodExcNon += excNon;
			}
		}

		return available;
	}

	/**
	 * Compute total bonus from a List of BonusObjs Use cost of bonus to adjust
	 * total bonus up or down This method takes a list of bonus objects.
	 * 
	 * For each object in the list, it gets the creating object and queries it
	 * for its "COST". It then multiplies the value of the bonus by this cost
	 * and adds it to the cumulative total so far. If subSearch is true, the
	 * choices made in the object that the bonus originated in are searched, the
	 * effective bonus is multiplied by the number of times this bonus appears
	 * in the list.
	 * 
	 * Note: This COST seems to be used for several different things in the code
	 * base, in feats for instance, it is used to modify the feat pool by
	 * amounts other than 1 when selecting a given feat. Here it is used as a
	 * multiplier to say how effective a given bonus is i.e. a bonus with a COST
	 * of 0.5 counts for half its normal value. The COST is limited to a max of
	 * 1, so it can only make bonuses less effective.
	 * 
	 * @param aList
	 *            a list of bonus objects
	 * @return the calculated cumulative bonus
	 */
	private double calcBonusWithCostFromList(final List<BonusObj> aList)
	{
		return bonusManager.calcBonusesWithCost(aList);
	}

	/**
	 * calculate the total racial modifier to save: racial bonuses like the
	 * standard halfling's +1 on all saves template bonuses like the Lightfoot
	 * halfling's +1 on all saves racial base modifiers for certain monsters
	 * 
	 * @param check
	 * @return int
	 */
	private int calculateSaveBonusRace(PCCheck check)
	{
		int save;
		final String sString = check.toString();
		Race race = getRace();
		save = (int) BonusCalc.bonusTo(race, "CHECKS", "BASE." + sString, this, this);
		save += (int) BonusCalc.bonusTo(race, "CHECKS", sString, this, this);

		return save;
	}

	/**
	 * Counts the number of spells inside a spellbook Yes, divine casters can
	 * have a "spellbook"
	 * 
	 * @param aString
	 * @return spells in a book
	 */
	public int countSpellsInBook(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());
		final int levelNum;

		if (sbookNum >= getSpellBookCount())
		{
			return 0;
		}

		if (aTok.hasMoreTokens())
		{
			levelNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			levelNum = -1;
		}

		String bookName = Globals.getDefaultSpellBook();

		if (sbookNum > 0)
		{
			bookName = getSpellBookNames().get(sbookNum);
		}

		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			final List<CharacterSpell> aList =
					getCharacterSpells(aObject, null, bookName, levelNum);
			return aList.size();
		}

		return 0;
	}

	public SizeAdjustment getSizeAdjustment()
	{
		return sizeFacet.getSizeAdjustment(id);
	}

	public int getSpellClassCount()
	{
		return getSpellClassList().size();
	}

	/**
	 * Get the spell class list
	 * 
	 * @return List
	 */
	public List<? extends PObject> getSpellClassList()
	{
		final ArrayList<PObject> aList = new ArrayList<PObject>();

		Race race = getRace();
		if (!getCharacterSpells(race).isEmpty())
		{
			aList.add(race);
		}

		for (PCClass pcClass : getClassSet())
		{
			if (pcClass.get(StringKey.SPELLTYPE) != null)
			{
				aList.add(pcClass);
			}
		}

		return aList;
	}

	/**
	 * Check if the character has the given Deity.
	 * 
	 * @param deity
	 *            Deity to check for.
	 * @return <code>true</code> if the character has the Deity,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasDeity(final Deity deity)
	{
		return deityFacet.matches(id, deity);
	}

	private boolean includeSkill(final Skill skill, final int level)
	{
		if (level == 2
			|| SkillRankControl.getTotalRank(this, skill).floatValue() > 0)
		{
			return true;
		}
		if (level != 1 || !skill.getSafe(ObjectKey.USE_UNTRAINED))
		{
			return false;
		}
		if (skill.getSafe(ObjectKey.EXCLUSIVE))
		{
			return this.isClassSkill(skill) || this.isCrossClassSkill(skill);
		}
		else
		{
			return skill.qualifies(this, skill);
		}
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character. there is no way to bypass the prerequisites
	 * with this method, see: incrementClassLevel(int, PCClass, boolean,
	 * boolean);
	 * 
	 * 
	 * @param numberOfLevels
	 *            number of levels to add
	 * @param globalClass
	 *            the class to add the levels to
	 * @param bSilent
	 *            whether or not to display warning messages
	 */
	public void incrementClassLevel(final int numberOfLevels,
		final PCClass globalClass, final boolean bSilent)
	{
		incrementClassLevel(numberOfLevels, globalClass, bSilent, false);
	}

	/**
	 * Change the number of levels a character has in a particular class. Note:
	 * It is assumed that this method is not used as part of loading a
	 * previously saved character.
	 * 
	 * @param numberOfLevels
	 *            The number of levels to add or remove. If a positive number is
	 *            passed in then that many levels will be added. If the number
	 *            of levels passed in is negative then that many levels will be
	 *            removed from the specified class.
	 * @param globalClass
	 *            The global class from the data store. The class as stored in
	 *            the character will be compared to this one using the
	 *            getClassNamed() method
	 * @param bSilent
	 *            If true do not display any warning messages about adding or
	 *            removing too many levels
	 * @param bypassPrereqs
	 *            Whether we should bypass the checks as to whether or not the
	 *            PC qualifies to take this class. If true, the checks will be
	 *            bypassed
	 */
	public void incrementClassLevel(final int numberOfLevels,
		final PCClass globalClass, final boolean bSilent,
		final boolean bypassPrereqs)
	{
		// If not importing, load the spell list
		if (!isImporting())
		{
			getSpellList();
		}

		// Make sure the character qualifies for the class if adding it
		if (numberOfLevels > 0)
		{
			if (!bypassPrereqs && !globalClass.qualifies(this, globalClass))
			{
				return;
			}

			Race race = getRace();
			if (globalClass.isMonster()
				&& !SettingsHandler.isIgnoreMonsterHDCap()
				&& !race.isAdvancementUnlimited()
				&& ((totalHitDice() + numberOfLevels) > race
					.maxHitDiceAdvancement()) && !bSilent)
			{
				ShowMessageDelegate
					.showMessageDialog(
						"Cannot increase Monster Hit Dice for this character beyond "
							+ race.maxHitDiceAdvancement()
							+ ". This character's current number of Monster Hit Dice is "
							+ totalHitDice(), Constants.APPLICATION_NAME,
						MessageType.INFORMATION);

				return;
			}
		}

		// Check if the character already has the class.
		PCClass pcClassClone = getClassKeyed(globalClass.getKeyName());

		// If the character did not already have the class...
		if (pcClassClone == null)
		{
			// add the class even if setting to level 0
			if (numberOfLevels >= 0)
			{
				// Get a clone of the class so we don't modify the globals!
				pcClassClone = globalClass.clone(); //Still required :(

				// Make sure the clone was successful
				if (pcClassClone == null)
				{
					Logging
						.errorPrint("PlayerCharacter::incrementClassLevel => "
							+ "Clone of class " + globalClass.getKeyName()
							+ " failed!");

					return;
				}

				// If not importing, add extra feats
				if (!isImporting() && classFacet.isEmpty(id))
				{
					adjustFeats(pcClassClone.getSafe(IntegerKey.START_FEATS));
				}

				// Add the class to the character classes as level 0
				classFacet.addClass(id, pcClassClone);

				// do the following only if adding a level of a class for the
				// first time
//				if (numberOfLevels > 0)
//				{
//					for (CDOMReference<Language> ref : pcClassClone
//						.getSafeListFor(ListKey.AUTO_LANGUAGES))
//					{
//						langAutoFacet.addAll(id, ref.getContainedObjects(),
//								pcClassClone);
//					}
//				}
			}
			else
			{
				// mod is < 0 and character does not have class. Return.
				return;
			}
		}

		// Add or remove levels as needed
		if (numberOfLevels > 0)
		{
			for (int i = 0; i < numberOfLevels; ++i)
			{
				int currentLevel = getLevel(pcClassClone);
				final PCLevelInfo playerCharacterLevelInfo = addLevelInfo(pcClassClone.getKeyName());

				// if we fail to add the level, remove and return
				if (!pcClassClone.addLevel(false, bSilent, this, bypassPrereqs))
				{
					PCClassLevel failedpcl = getActiveClassLevel(pcClassClone, currentLevel + 1);
					removeLevelInfo(pcClassClone.getKeyName());
					return;
				}
			}
		}
		else if (numberOfLevels < 0)
		{
			for (int i = 0; i < -numberOfLevels; ++i)
			{
				int currentLevel = getLevel(pcClassClone);
				pcClassClone.subLevel(bSilent, this);
				removeLevelInfo(pcClassClone.getKeyName());
				getActiveClassLevel(pcClassClone, currentLevel);
			}
		}

		// Handle any feat changes as a result of level changes
		for (PCTemplate template : templateFacet.getSet(id))
		{
			final List<AbilitySelection> templateFeats =
					feats(template, getTotalLevels(), totalHitDice(), true);

			for (int j = 0, y = templateFeats.size(); j < y; ++j)
			{
				AbilityUtilities.modFeatsFromList(this, templateFeats.get(j));
			}
		}

		calcActiveBonuses();
	}

	private void rebuildLists(final PCClass toClass, final PCClass fromClass,
		final int iCount, final PlayerCharacter aPC)
	{
		final int fromLevel = getLevel(fromClass);
		final int toLevel = getLevel(toClass);

		for (int i = 0; i < iCount; ++i)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);

			toClass.doPlusLevelMods(toLevel + i + 1, aPC);
		}
	}

	private void removeExcessSkills(final int level)
	{
		boolean modified = false;
		// Wrap to avoid a ConcurrentModificationException
		for (Skill skill : new ArrayList<Skill>(skillFacet.getSet(id)))
		{
			if (!includeSkill(skill, level))
			{
				skillFacet.remove(id, skill);
				modified = true;
			}
		}

		if (modified)
		{
			setDirty(true);
		}
	}

	private boolean removeLevelInfo(final String classKeyName)
	{
		for (int idx = getLevelInfoSize() - 1; idx >= 0; --idx)
		{
			final PCLevelInfo li = levelInfoFacet.get(id, idx);

			if (li.getClassKeyName().equals(classKeyName))
			{
				levelInfoFacet.remove(id, li);
				setDirty(true);

				return true;
			}
		}

		return false;
	}

	/**
	 * <code>rollStats</code> roll Globals.s_ATTRIBLONG.length random stats
	 * Method: 1: 4d6 Drop Lowest 2: 3d6 3: 5d6 Drop 2 Lowest 4: 4d6 reroll 1's
	 * drop lowest 5: 4d6 reroll 1's and 2's drop lowest 6: 3d6 +5 7: 5d6 Drop
	 * lowest and middle as per FREQ #458917
	 * 
	 * @param method
	 *            the method to be used for rolling.
	 */
	public void rollStats(final int method)
	{
		int aMethod = method;
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			aMethod = Constants.CHARACTER_STAT_METHOD_PURCHASE;
		}
		rollStats(aMethod, statFacet.getSet(id), SettingsHandler.getGame()
			.getCurrentRollingMethod(), false);
	}

	public void rollStats(final int method, final Collection<PCStat> aStatList,
		final RollMethod rollMethod, boolean aSortedFlag)
	{
		int[] rolls = new int[aStatList.size()];

		for (int i = 0; i < rolls.length; i++)
		{
			switch (method)
			{
				case Constants.CHARACTER_STAT_METHOD_PURCHASE:
					rolls[i] = SettingsHandler.getGame().getPurchaseModeBaseStatScore(this);
					break;
				case Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME:
					rolls[i] = SettingsHandler.getGame().getAllStatsValue();
					break;

				case Constants.CHARACTER_STAT_METHOD_ROLLED:
					final String diceExpression = rollMethod.getMethodRoll();
					rolls[i] = RollingMethods.roll(diceExpression);
					break;

				default:
					rolls[i] = 0;
					break;
			}
		}
		if (aSortedFlag)
		{
			Arrays.sort(rolls);
		}

		int i = rolls.length - 1;
		for (PCStat currentStat : aStatList)
		{
			this.setAssoc(currentStat, AssociationKey.STAT_SCORE, 0);

			if (!currentStat.getSafe(ObjectKey.ROLLED))
			{
				continue;
			}

			int roll =
					rolls[i--]
						+ this.getAssoc(currentStat, AssociationKey.STAT_SCORE);

			if (roll < currentStat.getSafe(IntegerKey.MIN_VALUE))
			{
				roll = currentStat.getSafe(IntegerKey.MIN_VALUE);
			}

			if (roll > currentStat.getSafe(IntegerKey.MAX_VALUE))
			{
				roll = currentStat.getSafe(IntegerKey.MAX_VALUE);
			}

			this.setAssoc(currentStat, AssociationKey.STAT_SCORE, roll);
		}

		if (method != Constants.CHARACTER_STAT_METHOD_PURCHASE)
		{
			this.setPoolAmount(0);
			this.costPool = 0;
		}
		//TODO Why does rolling stats delete the language list?!?
		languageFacet.removeAll(id);
		if (method != Constants.CHARACTER_STAT_METHOD_PURCHASE)
		{
			setPoolAmount(0);
		}
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name. Note
	 * hidden items (outputIndex = -1) are not included in list.
	 * 
	 * @param unsortedEquip
	 *            An ArrayList of the equipment to be sorted.
	 * @param merge
	 *            How to merge.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List<Equipment> sortEquipmentList(final Collection<Equipment> unsortedEquip, final int merge)
	{
		if (unsortedEquip.isEmpty())
		{
			return Collections.emptyList();
		}

		// Merge list for duplicates
		// The sorting is done during the Merge
		final List<Equipment> sortedList =
				CoreUtility.mergeEquipmentList(unsortedEquip, merge);

		// Remove the hidden items from the list
		for (Iterator<Equipment> i = sortedList.iterator(); i.hasNext();)
		{
			final Equipment item = i.next();

			if (item.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	/**
	 * @param descriptionLst
	 *            The descriptionLst to set.
	 */
	private void setDescriptionLst(final String descriptionLst)
	{
		this.descriptionLst = descriptionLst;
	}

	/**
	 * Prepares this PC object for output by ensuring that all its
	 * info is up to date.
	 */
	public void preparePCForOutput()
	{
		// Get the EquipSet used for output and calculations
		// possibly include equipment from temporary bonuses
		setCalcEquipmentList(getUseTempMods());

		// Make sure spell lists are setup
		getSpellList();

		// Force refresh of skills
		refreshSkillList();
		
		int includeSkills = SettingsHandler.getIncludeSkills();

		// Include the skills from the skills tab if that preference is set
		if (includeSkills == SettingsHandler.INCLUDE_SKILLS_SKILLS_TAB)
		{
			includeSkills = SettingsHandler.getSkillsTab_IncludeSkills();
		}

		populateSkills(includeSkills);

		// Determine which hands weapons are currently being wielded in
		determinePrimaryOffWeapon();

		// Apply penalties to attack if not proficient in worn armour
		modFromArmorOnWeaponRolls();

		// Recalculate the movement rates
		adjustMoveRates();

		// Calculate any active bonuses
		calcActiveBonuses();
	}

	private static class CasterLevelSpellBonus
	{
		private int bonus;
		private String type;

		/**
		 * Constructor
		 * 
		 * @param b
		 * @param t
		 */
		public CasterLevelSpellBonus(final int b, final String t)
		{
			bonus = b;
			type = t;
		}

		/**
		 * Get bonus
		 * 
		 * @return bonus
		 */
		public int getBonus()
		{
			return (bonus);
		}

		/**
		 * Get type
		 * 
		 * @return type
		 */
		public String getType()
		{
			return (type);
		}

		/**
		 * Set bonus
		 * 
		 * @param newBonus
		 */
		public void setBonus(final int newBonus)
		{
			bonus = newBonus;
		}

		@Override
		public String toString()
		{
			return ("bonus: " + bonus + "    type: " + type);
		}

	}

	/**
	 * @param info
	 * @return character level
	 */
	public int getCharacterLevel(final PCLevelInfo info)
	{
		int i = 1;
		for (PCLevelInfo element : getLevelInfo())
		{
			if (info == element)
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Return a list of bonus languages which the character may select from.
	 * This function is not efficient, but is sufficient for it's current use of
	 * only being called when the user requests the bonus language selection
	 * list. Note: A check will be made for the ALL language and it will be
	 * replaced with the current list of languages in globals. These should be
	 * further restricted by the prerequisites of the languages to ensure that
	 * 'secret' languages are not offered.
	 * 
	 * @return List of bonus languages for the character.
	 */
	public Set<Language> getLanguageBonusSelectionList()
	{
		return startingLangFacet.getSet(id);
	}

	/**
	 * Retrieve the bonus for the stat excluding either temporary bonuses,
	 * equipment bonuses or both. This method ensure stacking rules are applied
	 * to all included bonuses. If not excluding either, it is quicker to use
	 * getTotalBonusTo.
	 * 
	 * @param stat
	 *            The stat to calculate the bonus for.
	 * @param useTemp
	 *            Should temp bonuses be included?
	 * @param useEquip
	 *            Should equipment bonuses be included?
	 * @return The bonus to the stat.
	 */
	public int getPartialStatBonusFor(
		PCStat stat,
		boolean useTemp,
		boolean useEquip)
	{
		return bonusManager.getPartialStatBonusFor(stat, useTemp, useEquip);
	}

	/**
	 * Retrieve the stat as it was at a particular level excluding either
	 * temporary bonuses, equipment bonuses or both. This method ensures
	 * stacking rules are applied to all included bonuses. If not excluding
	 * either, it is quicker to use getTotalStatAtLevel.
	 * 
	 * @param stat
	 *            The stat to calculate the value of.
	 * @param level
	 *            The level we want to see the stat at.
	 * @param usePost
	 *            Should stat mods that occurred after levelling be included?
	 * @param useTemp
	 *            Should temp bonuses be included?
	 * @param useEquip
	 *            Should equipment bonuses be included?
	 * @return The stat as it was at the level
	 */
	public int getPartialStatAtLevel(
		PCStat stat,
		int level,
		boolean usePost,
		boolean useTemp,
		boolean useEquip)
	{
		int curStat =
				StatAnalysis.getPartialStatFor(this, stat, useTemp, useEquip);
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust =
					levelInfoFacet.get(id, idx).getTotalStatMod(stat, usePost);
			curStat -= statLvlAdjust;
		}

		return curStat;
	}

	/**
	 * Returns a deep copy of the PlayerCharacter. Note: This method does a
	 * shallow copy of many lists in here that seem to point to "system"
	 * objects. These copies should be validated before using this method.
	 * 
	 * @return a new deep copy of the <code>PlayerCharacter</code>
	 */
	@Override
	public Object clone()
	{
		PlayerCharacter aClone = null;

		// calling super.clone won't work because it will not create
		// new data instances for all the final variables and I won't
		// be able to reset them. Need to call new PlayerCharacter()
		// aClone = (PlayerCharacter)super.clone();
		aClone = new PlayerCharacter();
		for (NoteItem n : getNotesList())
		{
			aClone.addNotesItem(n);
		}
		aClone.primaryWeapons.addAll(getPrimaryWeapons());
		aClone.secondaryWeapons.addAll(getSecondaryWeapons());
		aClone.domainFacet.copyContents(id, aClone.id);
		aClone.templateFacet.addAll(aClone.id, templateFacet.getSet(id));
		aClone.companionModFacet.addAll(aClone.id, companionModFacet.getSet(id));
		aClone.raceFacet.set(aClone.id, raceFacet.get(id));
		aClone.deityFacet.set(aClone.id, deityFacet.get(id));
		aClone.alignmentFacet.set(aClone.id, alignmentFacet.get(id));
		aClone.statFacet.addAll(aClone.id, statFacet.getSet(id));
		aClone.skillFacet.addAll(aClone.id, skillFacet.getSet(id));
		aClone.languageFacet.copyContents(id, aClone.id);
		aClone.kitFacet.addAll(aClone.id, kitFacet.getSet(id));
		aClone.equipmentFacet.copyContents(id, aClone.id);
		aClone.userEquipmentFacet.copyContents(id, aClone.id);
		aClone.autoLangFacet.copyContents(id, aClone.id);
		aClone.freeLangFacet.copyContents(id, aClone.id);
		aClone.addLangFacet.copyContents(id, aClone.id);
		aClone.skillLangFacet.copyContents(id, aClone.id);
		aClone.classFacet.copyContents(id, aClone.id);
		aClone.regionFacet.copyContents(id, aClone.id);
		aClone.moneyFacet.copyContents(id, aClone.id);
		aClone.factFacet.copyContents(id, aClone.id);
		aClone.ageFacet.set(id, ageFacet.get(id));
		aClone.abFacet.copyContents(id, aClone.id);
		aClone.grantedAbilityFacet.copyContents(id, aClone.id);
		aClone.xpFacet.setEarnedXP(aClone.id, xpFacet.getEarnedXP(id));
		aClone.heightFacet.setHeight(aClone.id, heightFacet.getHeight(id));
		aClone.weightFacet.setWeight(aClone.id, weightFacet.getWeight(id));
		aClone.genderFacet.setGender(aClone.id, genderFacet.getGender(id));
		for (PCClass cloneClass : aClone.classFacet.getClassSet(aClone.id))
		{
			cloneClass.addFeatPoolBonus(aClone);
		}
		Follower followerMaster = getMaster();
		if (followerMaster != null)
		{
			aClone.masterFacet.set(id, followerMaster.clone());
		}
		else
		{
			aClone.masterFacet.remove(id);
		}
		for (EquipSet eqSet : equipSetFacet.getSet(id))
		{
			aClone.addEquipSet((EquipSet) eqSet.clone());
		}
		for (PCLevelInfo info : getLevelInfo())
		{
			aClone.levelInfoFacet.add(aClone.id, info.clone());
		}
		for (String book : spellBookFacet.getBookNames(id))
		{
			aClone.addSpellBook(book);
		}
		aClone.tempBonusItemList.addAll(tempBonusItemList);
		aClone.bonusManager = bonusManager.buildDeepClone(aClone);
		aClone.setBio(getBio());
		aClone.setBirthday(getBirthday());
		aClone.setBirthplace(getBirthplace());
		aClone.setCatchPhrase(getCatchPhrase());
		aClone.setCurrentEquipSetName(getCurrentEquipSetName());
		aClone.setDescription(getDescription());
		aClone.setDescriptionLst(getDescriptionLst());
		aClone.setEyeColor(getEyeColor());
		aClone.setFileName(getFileName());
		aClone.setGender(getGenderObject());
		aClone.setHairColor(getHairColor());
		aClone.setHairStyle(getHairStyle());
		aClone.setHanded(getHanded());
		aClone.setInterests(getInterests());
		aClone.setLocation(getLocation());
		aClone.setName(getName());
		aClone.setPhobias(getPhobias());
		aClone.setPlayersName(getPlayersName());
		aClone.setPortraitPath(getPortraitPath());
		if (getRegionString() != null)
		{
			aClone.setRegion(getRegionString());
		}
		aClone.setResidence(getResidence());
		aClone.setSkinColor(getSkinColor());
		aClone.setSpeechTendency(getSpeechTendency());
		if (getSubRegion() != null)
		{
			aClone.setSubRegion(getSubRegion());
		}
		aClone.tabName = tabName;
		aClone.setTrait1(getTrait1());
		aClone.setTrait2(getTrait2());
		aClone.autoKnownSpells = autoKnownSpells;
		aClone.autoLoadCompanion = autoLoadCompanion;
		aClone.autoSortGear = autoSortGear;
		aClone.outputSheetHTML = outputSheetHTML;
		aClone.outputSheetPDF = outputSheetPDF;
		aClone.ageSetKitSelections = new boolean[10];

		System.arraycopy(ageSetKitSelections, 0, aClone.ageSetKitSelections, 0,
			ageSetKitSelections.length);

		aClone.serial = serial;
		// Not sure what this is for
		aClone.displayUpdate = displayUpdate;
		aClone.setImporting(false);
		aClone.useTempMods = useTempMods;
		aClone.setFeats(numberOfRemainingFeats);
		aClone.costPool = costPool;
		aClone.currentEquipSetNumber = currentEquipSetNumber;
		aClone.equipOutputOrder = equipOutputOrder;
		aClone.poolAmount = poolAmount;

		// order in which the skills will be output.
		aClone.skillsOutputOrder = skillsOutputOrder;
		aClone.spellLevelTemp = spellLevelTemp;
		// Is this OK?
		aClone.variableProcessor = new VariableProcessorPC(aClone);
		aClone.pointBuyPoints = pointBuyPoints;

		aClone.setDirty(true);
		aClone.adjustMoveRates();
		aClone.calcActiveBonuses();
		//Just to be safe
		aClone.equippedFacet.reset(id);

		return aClone;
	}

	/**
	 * Set the string for the characteristic
	 * 
	 * @param key
	 * @param s
	 */
	public void setStringFor(StringKey key, String s)
	{
		factFacet.set(id, key, s);
		setDirty(true);
	}

	private Float getEquippedQty(EquipSet eSet, Equipment eqI)
	{
		return equipSetFacet.getEquippedQuantity(id, eSet, eqI);
	}

	/**
	 * If an item can only go in one location, return the name of that location
	 * to add to an EquipSet
	 * 
	 * @param eqI
	 * @return single location
	 */
	private String getSingleLocation(Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots(this) == 0)
			{
				// TODO - Yuck. This should not look at the name!!
				if (eqI.modifiedName().endsWith("Primary"))
				{
					return Constants.EQUIP_LOCATION_NATURAL_PRIMARY;
				}
				return Constants.EQUIP_LOCATION_NATURAL_SECONDARY;
			}
		}

		// Always force weapons to go through the chooser dialog
		// unless they are also armor (ie: with Armor Spikes)
		if ((eqI.isWeapon()) && !(eqI.isArmor()))
		{
			return Constants.EMPTY_STRING;
		}

		List<EquipSlot> eqSlotList =
				SystemCollections.getUnmodifiableEquipSlotList();

		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return Constants.EMPTY_STRING;
		}

		for (EquipSlot es : eqSlotList)
		{
			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}

		return Constants.EMPTY_STRING;
	}

	/**
	 * returns true if you can put Equipment into a location in EquipSet
	 * 
	 * @param eSet
	 * @param locName
	 * @param eqI
	 * @param eqTarget
	 * @return true if equipment can be added
	 */
	private boolean canEquipItem(EquipSet eSet, String locName, Equipment eqI,
		Equipment eqTarget)
	{
		final String idPath = eSet.getIdPath();

		// If target is a container, allow it
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// TODO - Should make sure eqI can be contained by eqTarget
			return true;
		}

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.EQUIP_LOCATION_CARRIED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_EQUIPPED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_NOTCARRIED))
		{
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed())
		{
			return true;
		}

		// allow many Secondary Natural weapons
		if (locName.equals(Constants.EQUIP_LOCATION_NATURAL_SECONDARY))
		{
			return true;
		}

		// Don't allow weapons that are too large for PC
		if (eqI.isWeapon() && eqI.isWeaponOutsizedForPC(this)
			&& !eqI.isNatural())
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map<String, String> slotMap = new HashMap<String, String>();

		for (EquipSet es : getEquipSet())
		{
			String esID = es.getParentIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
			String abID = idPath + Constants.EQUIP_SET_PATH_SEPARATOR;

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName))
			{
				final Equipment eItem = es.getItem();
				final String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				if (eItem != null)
				{
					existNum += eItem.getSlots(this);
				}

				slotMap.put(locName, String.valueOf(existNum));
			}
		}

		for (EquipSet es : getEquipSet())
		{
			String esID = es.getParentIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
			String abID = idPath + Constants.EQUIP_SET_PATH_SEPARATOR;

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqI.isWeapon())
			{
				// weapons can never occupy the same slot
				if (es.getName().equals(locName))
				{
					return false;
				}

				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.EQUIP_LOCATION_BOTH) || locName
					.equals(Constants.EQUIP_LOCATION_DOUBLE))
					&& (es.getName().equals(Constants.EQUIP_LOCATION_PRIMARY)
						|| es.getName().equals(Constants.EQUIP_LOCATION_SECONDARY)
						|| es.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| es.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.EQUIP_LOCATION_PRIMARY) || locName
					.equals(Constants.EQUIP_LOCATION_SECONDARY))
					&& (es.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| es.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (es.getName().equals(locName))
			{
				final String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				existNum += eqI.getSlots(this);

				EquipSlot eSlot = Globals.getEquipSlotByName(locName);

				if (eSlot == null)
				{
					return true;
				}

				for (String slotType : eSlot.getContainType())
				{
					if (eqI.isType(slotType))
					{
						// if the item takes more slots, return false
						if (existNum > (eSlot.getSlotCount() + (int) getTotalBonusTo(
							"SLOTS", slotType)))
						{
							return false;
						}
					}
				}

				return true;
			}
		}

		return true;
	}

	/**
	 * Checks to see if Equipment exists in selected EquipSet and if so, then
	 * return the EquipSet containing eqI
	 * 
	 * @param eSet
	 * @param eqI
	 * @return EquipSet
	 */
	public EquipSet getEquipSetForItem(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

		for (EquipSet es : getEquipSet())
		{
			String esIdPath = es.getIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
			String rIdPath = rPath + Constants.EQUIP_SET_PATH_SEPARATOR;

			if (!esIdPath.startsWith(rIdPath))
			{
				continue;
			}

			if (eqI.getName().equals(es.getValue()))
			{
				return es;
			}
		}

		return null;
	}

	/**
	 * returns new id_Path with the last id one higher than the current highest
	 * id for EquipSets with the same ParentIdPath
	 * 
	 * @param eSet
	 * @return new id path
	 */
	private String getNewIdPath(EquipSet eSet)
	{
		String pid = Constants.EQUIP_SET_ROOT_ID;
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (EquipSet es : getEquipSet())
		{
			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		return pid + Constants.EQUIP_SET_PATH_SEPARATOR + newID;
	}

	public EquipSet addEquipToTarget(final EquipSet eSet,
		final Equipment eqTarget, String locName, final Equipment eqI,
		Float newQty)
	{
		float tempQty = 1.0f;
		if (newQty != null)
		{
			tempQty = newQty.floatValue();
		}
		else
		{
			newQty = Float.valueOf(tempQty);
		}
		boolean addAll = false;
		boolean mergeItem = false;

		Equipment masterEq = getEquipmentNamed(eqI.getName());
		if (masterEq == null)
		{
			return null;
		}
		float diffQty =
				masterEq.getQty().floatValue()
					- getEquippedQty(eSet, eqI).floatValue();

		// if newQty is less than zero, we want to
		// add all of this item to the EquipSet
		// or all remaining items that havn't already
		// been added to the EquipSet
		if (newQty.floatValue() < 0.0f)
		{
			tempQty = diffQty;
			newQty =
					new Float(tempQty + getEquippedQty(eSet, eqI).floatValue());
			addAll = true;
		}

		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (tempQty > diffQty)
		{
			return null;
		}

		// check to see if the target item is a container
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// set these to newQty just for testing
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);

			// Make sure the container accepts items
			// of this type and is not full
			if (eqTarget.canContain(this, eqI) == 1)
			{
				locName = eqTarget.getName();
				addAll = true;
				mergeItem = true;
			}
			else
			{
				return null;
			}
		}

		// If locName is empty equip this item to its default location.
		// If there is more than one option return with an error.
		if (locName == null || locName.length() == 0)
		{
			locName = getSingleLocation(eqI);

			if (locName.length() == 0)
			{
				return null;
			}
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canEquipItem(eSet, locName, eqI, eqTarget))
		{
			return null;
		}

		if (eqI.isContainer())
		{
			// don't merge containers
			mergeItem = false;
		}

		EquipSet existingSet = getEquipSetForItem(eSet, eqI);

		if (addAll && mergeItem && (existingSet != null))
		{
			newQty =
					new Float(tempQty + getEquippedQty(eSet, eqI).floatValue());
			existingSet.setQty(newQty);
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);
			setDirty(true);

			if ((eqTarget != null) && eqTarget.isContainer())
			{
				eqTarget.updateContainerContentsString(this);
			}

			return existingSet;
		}
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(this, eqI);
			eqI.setParent(eqTarget);
		}

		// construct the new IdPath
		// new id is one larger than any
		// other id at this path level
		String id = getNewIdPath(eSet);

		// now create a new EquipSet to add
		// this Equipment item to
		EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);

		// set the Quantity of equipment
		eqI.setQty(newQty);
		newSet.setQty(newQty);

		addEquipSet(newSet);
		setDirty(true);

		return newSet;
	}

	/**
	 * Gets a 'safe' String representation
	 * 
	 * @param key
	 * @return a 'safe' String
	 */
	public String getSafeStringFor(StringKey key)
	{
		String s = factFacet.get(id, key);
		if (s == null)
		{
			s = Constants.EMPTY_STRING;
		}
		return s;
	}

	/**
	 * Sets if ADD: level abilities should be processed when incrementing a
	 * level.
	 * 
	 * <p>
	 * <b>Note</b>: This is kind of a hack used by the Kit code to allow a kit
	 * to specify what the level abilities are.
	 * 
	 * @param yesNo
	 *            Yes if level increases should process ADD: level abilities.
	 */
	public void setDoLevelAbilities(boolean yesNo)
	{
		processLevelAbilities = yesNo;
	}

	/**
	 * Returns if level increases will process ADD: level abilities.
	 * 
	 * @return <tt>true</tt> if ADD: level abilities will be processed.
	 */
	public boolean doLevelAbilities()
	{
		return processLevelAbilities;
	}

	/**
	 * Whether to allow adjustment of the Global Feat pool
	 * 
	 * @param allow
	 */
	public void setAllowFeatPoolAdjustment(boolean allow)
	{
		this.allowFeatPoolAdjustment = allow;
	}


	/*
	 * For debugging purposes Dumps contents of spell books to System.err
	 * 
	 * static public void dumpSpells(final PlayerCharacter pc) { final List
	 * bookList = pc.getSpellBooks(); for(int bookIdx = 0; bookIdx <
	 * bookList.size(); ++bookIdx) { final String bookName = (String)
	 * pc.getSpellBooks().get(bookIdx);
	 * 
	 * System.err.println("=========="); System.err.println("Book:" + bookName);
	 * final List casterList = pc.getSpellClassList(); for(int casterIdx = 0;
	 * casterIdx < casterList.size(); ++casterIdx) { final PObject aCaster =
	 * (PObject) casterList.get(casterIdx); final List spellList =
	 * aCaster.getCharacterSpellList(); if (spellList == null) { continue; }
	 * System.err.println("Class/Race:" + aCaster.getName());
	 * 
	 * for (Iterator i = spellList.iterator(); i.hasNext();) { final
	 * CharacterSpell cs = (CharacterSpell) i.next();
	 * 
	 * for (Iterator csi = cs.getInfoListIterator(); csi.hasNext();) { final
	 * SpellInfo sInfo = (SpellInfo) csi.next(); if
	 * (bookName.equals(sInfo.getBook())) {
	 * System.err.println(cs.getSpell().getOutputName() + sInfo.toString() + "
	 * level:" + Integer.toString(sInfo.getActualLevel())); } } } } } }
	 */

	// --------------------------------------------------
	// Feat/Ability stuff
	// --------------------------------------------------

	// whether to adjust the feat pool when requested
	private boolean allowFeatPoolAdjustment = true;

	// pool of feats remaining to distribute
	private double numberOfRemainingFeats = 0;

	public HashMap<Nature, Set<Ability>> getAbilitiesSet()
	{
		HashMap<Nature, Set<Ability>> st =
				new HashMap<Nature, Set<Ability>>();

		st.put(Nature.AUTOMATIC, new HashSet<Ability>());
		st.put(Nature.NORMAL, new HashSet<Ability>());
		st.put(Nature.VIRTUAL, new HashSet<Ability>());
		st.put(Nature.ANY, new HashSet<Ability>());

		st.get(Nature.VIRTUAL).addAll(
			getAbilitySetByNature(Nature.VIRTUAL));
		st.get(Nature.AUTOMATIC).addAll(
			getAbilitySetByNature(Nature.AUTOMATIC));
		st.get(Nature.NORMAL).addAll(
			getAbilitySetByNature(Nature.NORMAL));

		st.get(Nature.ANY).addAll(st.get(Nature.NORMAL));
		st.get(Nature.ANY).addAll(st.get(Nature.AUTOMATIC));
		st.get(Nature.ANY).addAll(st.get(Nature.VIRTUAL));

		return st;
	}

	public List<Ability> getAllAbilities()
	{
		Set<Category<Ability>> abCats = new HashSet<Category<Ability>>();
		abCats.addAll(abFacet.getCategories(id));
		abCats.addAll(grantedAbilityFacet.getCategories(id));

		List<Ability> list = new ArrayList<Ability>();

		for (Category<Ability> ac : abCats)
		{
			list.addAll(getAutomaticAbilityList(ac));
			list.addAll(getRealAbilitiesList(ac));
			list.addAll(getVirtualAbilityList(ac));
		}
		return list;
	}

	public Set<Ability> getRealAbilitiesList(final Category<Ability> aCategory)
	{
		Set<Ability> newSet = new HashSet<Ability>();
		newSet.addAll(abFacet.get(id, aCategory, Nature.NORMAL));
		newSet.addAll(grantedAbilityFacet.get(id, aCategory, Nature.NORMAL));
		return Collections.unmodifiableSet(newSet);
	}

	/**
	 * Get a list of real abilities of a particular AbilityCategory
	 * no matter which AbilityCategory list they reside in.
	 * 
	 * @param aCategory The AbilityCategory of the desired abilities.
	 * @return List of abilities
	 */
	public List<Ability> getRealAbilitiesListAnyCat(
		final AbilityCategory aCategory)
	{
		List<Ability> abilities = new ArrayList<Ability>();
		for (AbilityCategory cat : SettingsHandler.getGame()
			.getAllAbilityCategories())
		{
			for (Ability ability : getRealAbilitiesList(cat))
			{
				if (aCategory.getKeyName().equals(ability.getCategory()))
				{
					abilities.add(ability);
				}
			}
		}
		return abilities;
	}

	/**
	 * Does the character have this ability (not virtual or auto).
	 * 
	 * @param aCategory
	 *            The ability category to check.
	 * @param anAbility
	 *            The Ability object (of category FEAT) to check
	 * 
	 * @return True if the character has the feat
	 */
	public boolean hasRealAbility(final Category<Ability> aCategory,
		final Ability anAbility)
	{
		return abFacet.contains(id, aCategory, Nature.NORMAL, anAbility)
				|| grantedAbilityFacet.contains(id, aCategory, Nature.NORMAL, anAbility);
	}

	/**
	 * Checks if the character has the specified ability.
	 * 
	 * <p>
	 * If <tt>aCategory</tt> is <tt>null</tt> then all categories that have
	 * the same innate ability category will be checked.
	 * <p>
	 * If <tt>anAbilityType</tt> is <tt>ANY</tt> then all Natures will be
	 * checked for the ability.
	 * 
	 * @param aCategory
	 *            An <tt>AbilityCategory</tt> or <tt>null</tt>
	 * @param anAbilityType
	 *            A <tt>Nature</tt>.
	 * @param anAbility
	 *            The <tt>Ability</tt> to check for.
	 * 
	 * @return <tt>true</tt> if the character has the ability with the
	 *         criteria specified.
	 */
	public boolean hasAbility(final AbilityCategory aCategory,
		final Nature anAbilityType, final Ability anAbility)
	{
		final List<AbilityCategory> categories;
		if (aCategory == null)
		{
			// A null category means we have to check all categories for
			// abilities of the same innate category as the passed in one.
			categories = new ArrayList<AbilityCategory>();
			final Collection<AbilityCategory> allCategories =
					SettingsHandler.getGame().getAllAbilityCategories();
			for (final AbilityCategory cat : allCategories)
			{
				if (cat.getParentCategory().equals(anAbility.getCDOMCategory()))
				{
					categories.add(cat);
				}
			}
		}
		else
		{
			categories = new ArrayList<AbilityCategory>();
			categories.add(aCategory);
		}

		final int start, end;
		if (anAbilityType == Nature.ANY)
		{
			start = 0;
			end = Nature.values().length - 2;
		}
		else
		{
			start = end = anAbilityType.ordinal();
		}
		for (int i = start; i <= end; i++)
		{
			final Nature nature = Nature.values()[i];
			boolean hasIt = false;
			for (final AbilityCategory cat : categories)
			{
				switch (nature)
				{
					case NORMAL:
						hasIt = hasRealAbility(cat, anAbility);
						break;
					case AUTOMATIC:
						hasIt = hasAutomaticAbility(cat, anAbility);
						break;
					case VIRTUAL:
						hasIt = hasVirtualAbility(cat, anAbility);
						break;
				}
				if (hasIt)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the characterFeat ArrayList contains the named Feat.
	 * 
	 * @param featName
	 *            String name of the feat to check for.
	 * @return <code>true</code> if the character has the feat,
	 *         <code>false</code> otherwise.
	 */

	public boolean hasRealFeatNamed(final String featName)
	{
		Ability ability = AbilityUtilities.retrieveAbilityKeyed(
				AbilityCategory.FEAT, featName);
		return hasRealAbility(AbilityCategory.FEAT, ability);
	}

	public boolean removeRealAbility(final Category<Ability> aCategory,
		final Ability anAbility)
	{
		return abFacet.remove(id, aCategory, Nature.NORMAL, anAbility);
	}

	public void adjustFeats(final double arg)
	{
		if (allowFeatPoolAdjustment)
		{
			numberOfRemainingFeats += arg;
		}
		setDirty(true);
	}

	public void adjustAbilities(final Category<Ability> aCategory,
		final BigDecimal arg)
	{
		if (arg.equals(BigDecimal.ZERO))
		{
			return;
		}
		if (aCategory == AbilityCategory.FEAT)
		{
			adjustFeats(arg.doubleValue());
			return;
		}
		if (theUserPoolBonuses == null)
		{
			theUserPoolBonuses = new HashMap<Category<Ability>, BigDecimal>();
		}
		BigDecimal userMods = theUserPoolBonuses.get(aCategory);
		if (userMods != null)
		{
			userMods = userMods.add(arg);
		}
		else
		{
			userMods = arg;
		}
		theUserPoolBonuses.put(aCategory, userMods);
		setDirty(true);
	}

	// TODO - This method is ridiculously dangerous.
	public void setFeats(final double arg)
	{
		if (allowFeatPoolAdjustment)
		{
			numberOfRemainingFeats = arg;
		}
		setDirty(true);
	}

	public void setUserPoolBonus(final AbilityCategory aCategory,
		final BigDecimal anAmount)
	{
		if (theUserPoolBonuses == null)
		{
			theUserPoolBonuses = new HashMap<Category<Ability>, BigDecimal>();
		}
		theUserPoolBonuses.put(aCategory, anAmount);
	}

	public double getUserPoolBonus(final AbilityCategory aCategory)
	{
		BigDecimal userBonus = null;
		if (theUserPoolBonuses != null)
		{
			userBonus = theUserPoolBonuses.get(aCategory);
		}
		if (userBonus == null)
		{
			return 0.0d;
		}
		return userBonus.doubleValue();
	}

	public BigDecimal getTotalAbilityPool(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			BigDecimal spent = getAbilityPoolSpent(aCategory);
			return spent.add(new BigDecimal(getRemainingFeatPoolPoints()));
		}
		Number basePool = aCategory.getPoolFormula().resolve(this,
				getClass().toString());

		if (!aCategory.allowFractionalPool())
		{
			basePool = new Float(basePool.intValue());
		}
		double bonus = getTotalBonusTo("ABILITYPOOL", aCategory.getKeyName());
		// double bonus = getBonusValue("ABILITYPOOL", aCategory.getKeyName());
		if (!aCategory.allowFractionalPool())
		{
			bonus = Math.floor(bonus);
		}
		// User bonuses already handle the fractional pool flag.
		final double userBonus = getUserPoolBonus(aCategory);
		return BigDecimal.valueOf(basePool.floatValue() + bonus + userBonus);
	}

	private Set<Ability> getSelectedAbilities(final AbilityCategory aCategory)
	{
		return getRealAbilitiesList(aCategory);
	}

	/**
	 * Get the remaining Feat Points (or Skill Points if the GameMode uses a Point Pool).  
	 * 
	 * @return Number of remaining Feat Points
	 */
	public double getRemainingFeatPoolPoints()
	{
		if (Globals.getGameModeHasPointPool())
		{
			return getSkillPoints();
		}
		return getRemainingFeatPoints(true);
	}

	public BigDecimal getAvailableAbilityPool(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return BigDecimal.valueOf(getRemainingFeatPoolPoints());
		}
		return getTotalAbilityPool(aCategory).subtract(
			getAbilityPoolSpent(aCategory));
	}

	/**
	 * Get the number of remaining feat points.
	 * 
	 * @param bIncludeBonus - Flag whether to include any bonus feat points
	 * @return number of remaining feat points
	 */
	public double getRemainingFeatPoints(final boolean bIncludeBonus)
	{
		double retVal = numberOfRemainingFeats;
		if (bIncludeBonus)
		{
			retVal += getBonusFeatPool();
		}
		return retVal;
	}

	/**
	 * Query whether this PC should be able to select the ability passed in.
	 * That is, does the PC meet the prerequisites and is the feat not one the
	 * PC already has, or if the PC has the feat already, is it one that can be
	 * taken multiple times. TODO: When the PlayerCharacter Object can have
	 * abilities of category other than "FEAT" it will likely have methods to
	 * test "hasRealAbility" and "hasVirtualAbility", change this (or add
	 * another) to deal with them
	 * 
	 * @param anAbility
	 *            the ability to test
	 * @param autoQualify
	 *            if true, the PC automatically meets the prerequisites
	 * @return true if the PC can take, false otherwise
	 */
	public boolean canSelectAbility(final Ability anAbility,
		final boolean autoQualify)
	{
		final boolean qualify     = anAbility.qualifies(this, anAbility);
		final boolean canTakeMult = anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED);
		final boolean hasOrdinary = hasRealAbility(AbilityCategory.FEAT, anAbility);
		final boolean hasAuto     = hasAutomaticAbility(AbilityCategory.FEAT, anAbility);

		final boolean notAlreadyHas = !(hasOrdinary || hasAuto);

		return (autoQualify || qualify) && (canTakeMult || notAlreadyHas);
	}

	/**
	 * get unused feat count.
	 * 
	 * @return unused feat count
	 */
	public double getUsedFeatCount()
	{
		double iCount = 0;

		Collection<Ability> abilities = abFacet.get(id, AbilityCategory.FEAT, Nature.NORMAL);
		if (abilities == null)
		{
			return 0;
		}
		for (Ability aFeat : abilities)
		{
			//
			// Don't increment the count for
			// hidden feats so the number
			// displayed matches this number
			//
			if (aFeat.getSafe(ObjectKey.VISIBILITY) == Visibility.HIDDEN
				|| aFeat.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY)
			{
				continue;
			}
			final int subfeatCount = getSelectCorrectedAssociationCount(aFeat);
			double cost = aFeat.getSafe(ObjectKey.SELECTION_COST).doubleValue();
			if (ChooseActivation.hasChooseToken(aFeat))
			{
				iCount += Math.ceil(subfeatCount * cost);
			}
			else
			{
				int select =
						aFeat.getSafe(FormulaKey.SELECT).resolve(this, "")
							.intValue();
				double relativeCost = cost / select;
				if (!AbilityCategory.FEAT.allowFractionalPool())
				{
					iCount += (int) Math.ceil(relativeCost);
				}
				else
				{
					iCount += relativeCost;
				}
			}
		}

		return iCount;
	}

	public BigDecimal getAbilityPoolSpent(final AbilityCategory aCategory)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			return BigDecimal.valueOf(getUsedFeatCount());
		}

		double spent = 0.0d;

		final Set<Ability> abilities = getSelectedAbilities(aCategory);
		if (abilities != null)
		{
			for (final Ability ability : abilities)
			{
				final int subfeatCount =
						getSelectCorrectedAssociationCount(ability);
				double cost =
						ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
				if (ChooseActivation.hasChooseToken(ability))
				{
					spent += Math.ceil(subfeatCount * cost);
				}
				else
				{
					int select =
							ability.getSafe(FormulaKey.SELECT)
								.resolve(this, "").intValue();
					double relativeCost = cost / select;
					if (!aCategory.allowFractionalPool())
					{
						spent += (int) Math.ceil(relativeCost);
					}
					else
					{
						spent += relativeCost;
					}
				}
			}
		}
		if (!aCategory.allowFractionalPool())
		{
			return BigDecimal.valueOf((int) Math.ceil(spent));
		}
		return BigDecimal.valueOf(spent);
	}

	public void addFeat(final Ability aFeat)
	{
		if (hasRealAbility(AbilityCategory.FEAT, aFeat))
		{
			Logging.errorPrint("Adding duplicate feat: "
				+ aFeat.getDisplayName());
		}

		if (aFeat == null)
		{
			Logging.errorPrint("Cannot add null feat");
		}
		else
		{
			abFacet.add(id, AbilityCategory.FEAT, Nature.NORMAL, aFeat);
			calcActiveBonuses();
		}
	}

	public void addAbility(final Category<Ability> aCategory,
			final Ability anAbility)
	{
		if (hasRealAbility(aCategory, anAbility))
		{
			Logging.errorPrint("Adding duplicate ability: "
				+ anAbility.getDisplayName());
		}

		if (anAbility == null)
		{
			Logging.errorPrint("Cannot add null Ability");
		}
		else
		{
			abFacet.add(id, aCategory, Nature.NORMAL, anAbility);
			calcActiveBonuses();
		}
	}

	public Ability addAbilityNeedCheck(final Category<Ability> aCategory,
			final Ability anAbility)
	{
		// See if our choice is not auto or virtual
		/*
		 * TODO Should this check parent/peer categories??
		 */
		Ability pcAbility = getMatchingAbility(aCategory,
				anAbility, Nature.NORMAL);
		
		// (pcAbility == null) means we don't have this feat, so we need to add it
		if (pcAbility == null)
		{
			// Adding feat for first time
			pcAbility = anAbility.clone();
			abFacet.add(id, aCategory, Nature.NORMAL, pcAbility);
			calcActiveBonuses();
		}

		return pcAbility;
	}

	public Ability getAutomaticAbilityKeyed(final AbilityCategory aCategory,
		final String anAbilityKey)
	{
		for (final Ability ability : getAutomaticAbilityList(aCategory))
		{
			if (ability.getKeyName().equals(anAbilityKey))
			{
				return ability;
			}
		}
		return null;
	}

	public Ability getAbilityKeyed(final AbilityCategory aCategory,
		final String aKey)
	{
		final List<Ability> abilities = getAggregateAbilityList(aCategory);
		for (final Ability ability : abilities)
		{
			if (ability.getKeyName().equals(aKey))
			{
				return ability;
			}
		}

		return null;
	}

	/**
	 * Get an ability of any category that matches the key.
	 * @param aKey The key to search for
	 * @return An ability with the key, or null if none.
	 */
	private Ability getAbilityKeyed(final String aKey)
	{
		final List<Ability> abilities = getFullAbilityList();
		for (final Ability ability : abilities)
		{
			if (ability.getKeyName().equals(aKey))
			{
				return ability;
			}
		}

		return null;
	}

	/**
	 * Identify if the character has an ability, of any category, that
	 * matches the key.
	 * @param aKey The key to search for
	 * @return True if an ability is found, false otherwise.
	 */
	public boolean hasAbilityKeyed(final String aKey)
	{
		return getAbilityKeyed(aKey) != null;
	}

	public List<Ability> aggregateFeatList()
	{
		return rebuildFeatAggreagateList();
	}

	/**
	 * Retrieve a list of all abilities held by the character in the specified 
	 * category. <br>
	 * NB: Abilities are only returned in the category they are taken 
	 * in, so if parent category is supplied only those taken directly in the
	 * parent category will be returned. e.g. If asking for feats, Power Attack 
	 * taken as a fighter feat will nto be returned. You would need to query 
	 * fighter feats to get that. <br>
	 * NB: Duplicate abilities may be returned also. This may occur where an 
	 * ability is taken multiple times, but in different natures. 
	 * e.g. Skill Focus in two different skills, but once as Normal and once 
	 * as Automatic.  
	 * 
	 * @param aCategory The ability category to be queried.  
	 * @return The list of abilities of the category regardless of nature.
	 */
	public List<Ability> getAggregateAbilityList(final AbilityCategory aCategory)
	{
		// Note we use the direct feat lists here to make feats behave like other abilities.
		//		if (aCategory == AbilityCategory.FEAT)
		//		{
		//			return aggregateFeatList();
		//		}

		final List<Ability> abilities =
				new ArrayList<Ability>(getRealAbilitiesList(aCategory));
		abilities.addAll(getVirtualAbilityList(aCategory));
		abilities.addAll(getAutomaticAbilityList(aCategory));

		return abilities;
	}

	/**
	 * Retrieve a list of all abilities held by the character in the specified 
	 * category. <br>
	 * NB: Abilities are only returned in the category they are taken 
	 * in, so if parent category is supplied only those taken directly in the
	 * parent category will be returned. e.g. If asking for feats, Power Attack 
	 * taken as a fighter feat will not be returned. You would need to query 
	 * fighter feats to get that. <br>
	 * NB: Duplicate abilities will not be returned by this method. The order
	 * of priority is normal, virtual then automatic.
	 * 
	 * @param aCategory The ability category to be queried.  
	 * @return The list of abilities of the category regardless of nature.
	 */
	public List<Ability> getAggregateAbilityListNoDuplicates(
		final AbilityCategory aCategory)
	{
		List<Ability> aggregate = new ArrayList<Ability>();
		final Map<String, Ability> aHashMap = new HashMap<String, Ability>();

		for (Ability aFeat : getRealAbilitiesList(aCategory))
		{
			if (aFeat != null)
			{
				aHashMap.put(aFeat.getKeyName(), aFeat);
			}
		}

		addUniqueAbilitiesToMap(aHashMap, getVirtualAbilityList(aCategory));
		addUniqueAbilitiesToMap(aHashMap, getAutomaticAbilityList(aCategory));

		aggregate.addAll(aHashMap.values());
		return aggregate;
	}

	private List<Ability> rebuildFeatAggreagateList()
	{
		final Map<String, Ability> aHashMap = new HashMap<String, Ability>();

		for (Ability aFeat : getRealAbilitiesList(AbilityCategory.FEAT))
		{
			if (aFeat != null)
			{
				aHashMap.put(aFeat.getKeyName(), aFeat);
			}
		}

		addUniqueAbilitiesToMap(aHashMap, getVirtualAbilityList(AbilityCategory.FEAT));
		List<Ability> aggregate = new ArrayList<Ability>();
		aggregate.addAll(aHashMap.values());
		addUniqueAbilitiesToMap(aHashMap, getAutomaticAbilityList(AbilityCategory.FEAT));
		//TODO Is this a bug?
		aggregate = new ArrayList<Ability>();
		aggregate.addAll(aHashMap.values());
		return aggregate;
	}

	/**
	 * @param aHashMap
	 * @param abilityList TODO
	 */
	private void addUniqueAbilitiesToMap(final Map<String, Ability> aHashMap,
		Collection<Ability> abilityList)
	{
		for (Ability vFeat : abilityList)
		{
			if (!aHashMap.containsKey(vFeat.getKeyName()))
			{
				aHashMap.put(vFeat.getKeyName(), vFeat);
			}
//			else if (vFeat.getSafe(ObjectKey.MULTIPLE_ALLOWED))
//			{
//				Ability aggregateFeatOrig = aHashMap.get(vFeat.getKeyName());
//				Ability aggregateFeat = aggregateFeatOrig.clone();
//				for (String aString : getAssociationList(aggregateFeatOrig))
//				{
//					addAssociation(aggregateFeat, aString);
//				}
//
//				for (String aString : getAssociationList(vFeat))
//				{
//					if (aggregateFeat.getSafe(ObjectKey.STACKS)
//						|| !containsAssociated(aggregateFeat, aString))
//					{
//						addAssociation(aggregateFeat, aString);
//					}
//				}
//
//				aHashMap.put(vFeat.getKeyName(), aggregateFeat);
//			}
		}
	}

	public List<Ability> aggregateVisibleFeatList()
	{
		return getAggregateVisibleAbilityList(AbilityCategory.FEAT);
	}

	public List<Ability> getAggregateVisibleAbilityList(
		final AbilityCategory aCategory)
	{
		final List<Ability> abilities = new ArrayList<Ability>();
		abilities.addAll(getRealAbilitiesListAnyCat(aCategory));
		abilities.addAll(getAutomaticAbilityList(aCategory));
		abilities.addAll(getVirtualAbilityList(aCategory));
		final List<Ability> ret = new ArrayList<Ability>(abilities.size());
		for (final Ability ability : abilities)
		{
			if (ability.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT
				|| ability.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY)
			{
				ret.add(ability);
			}
		}
		return ret;
	}

	private Set<Ability> getAbilitySetByNature(Nature n)
	{
		GameMode gm = SettingsHandler.getGame();

		Set<AbilityCategory> Sc = new HashSet<AbilityCategory>();
		Sc.addAll(gm.getAllAbilityCategories());

		Set<Ability> Sa = new HashSet<Ability>();

		switch (n)
		{
			case AUTOMATIC:
				for (AbilityCategory Ac : Sc)
				{
					Sa.addAll(this.getAutomaticAbilityList(Ac));
				}
				break;

			case NORMAL:
				for (AbilityCategory Ac : Sc)
				{
					Sa.addAll(this.getRealAbilitiesList(Ac));
				}
				break;

			case VIRTUAL:
				for (AbilityCategory Ac : Sc)
				{
					Sa.addAll(this.getVirtualAbilityList(Ac));
				}
				break;

			default:
				Logging.errorPrint("Attempt to get abilities of Nature: " + n);
		}

		return Sa;
	}

	/**
	 * Return a set of all abilities no matter what category or 
	 * nature that the PC has. 
	 * @return Set of all abilities.
	 */
	public Set<Ability> getFullAbilitySet()
	{
		GameMode gm = SettingsHandler.getGame();
		Set<AbilityCategory> catSet = new HashSet<AbilityCategory>();
		catSet.addAll(gm.getAllAbilityCategories());
		Set<Ability> abilitySet = new HashSet<Ability>();

		for (AbilityCategory cat : catSet)
		{
			abilitySet.addAll(this.getAggregateAbilityList(cat));
		}

		return abilitySet;
	}

	/**
	 * Return a list of all abilities no matter what category or 
	 * nature that the PC has. Note: This method allows duplicates,
	 * such as when the same ability has been added by different 
	 * categories.
	 * @return List of all abilities.
	 */
	public List<Ability> getFullAbilityList()
	{
		GameMode gm = SettingsHandler.getGame();
		Set<AbilityCategory> catSet = new HashSet<AbilityCategory>();
		catSet.addAll(gm.getAllAbilityCategories());
		List<Ability> abilityList = new ArrayList<Ability>();
		for (AbilityCategory cat : catSet)
		{
			abilityList.addAll(this.getAggregateAbilityListNoDuplicates(cat));
		}

		return abilityList;
	}

	public Set<Ability> getVirtualAbilityList(final Category<Ability> aCategory)
	{
		Set<Ability> newSet = new HashSet<Ability>();
		newSet.addAll(abFacet.get(id, aCategory, Nature.VIRTUAL));
		newSet.addAll(grantedAbilityFacet.get(id, aCategory, Nature.VIRTUAL));
		return Collections.unmodifiableSet(newSet);
	}

	/**
	 * Returns the list of automatic abilities of the specified category the
	 * character possesses.
	 * 
	 * @param aCategory
	 *            The <tt>AbilityCategory</tt> to check.
	 * 
	 * @return A <tt>List</tt> of <tt>Ability</tt> objects.
	 * 
	 * @author boomer70
	 * @since 5.11.1
	 */
	public Set<Ability> getAutomaticAbilityList(
			final Category<Ability> aCategory)
	{
		Set<Ability> newSet = new HashSet<Ability>();
		newSet.addAll(abFacet.get(id, aCategory, Nature.AUTOMATIC));
		newSet.addAll(grantedAbilityFacet.get(id, aCategory, Nature.AUTOMATIC));
		return Collections.unmodifiableSet(newSet);
	}

	private <A extends PrereqObject> void processAbilityListsOnAdd(
		CDOMObject cdo,
		CDOMReference<? extends CDOMList<A>> ref)
	{
		for (CDOMList<A> list : ref.getContainedObjects())
		{
			if (list instanceof AbilityList)
			{
				CDOMReference r = ref;
				processAbilityList(cdo, r);
				break; // Only do once
			}
		}
	}

	private void processAbilityList(CDOMObject cdo,
			CDOMReference<AbilityList> ref)
	{
		Collection<CDOMReference<Ability>> mods = cdo.getListMods(ref);
		for (CDOMReference<Ability> objref : mods)
		{
			Collection<Ability> objs = objref.getContainedObjects();
			Collection<AssociatedPrereqObject> assoc = cdo.getListAssociations(
					ref, objref);
			for (Ability ab : objs)
			{
				for (AssociatedPrereqObject apo : assoc)
				{
					Nature nature = apo.getAssociation(AssociationKey.NATURE);
					Category<Ability> cat = apo
							.getAssociation(AssociationKey.CATEGORY);
					if (ab.getSafe(ObjectKey.MULTIPLE_ALLOWED))
					{
						List<String> choices = apo
								.getAssociation(AssociationKey.ASSOC_CHOICES);
						if (choices == null)
						{
							CategorizedAbilitySelection cas = new CategorizedAbilitySelection(
									cdo, cat, ab, nature, "");
							cas.addAllPrerequisites(apo.getPrerequisiteList());
							applyAbility(cas);
						}
						else
						{
							for (final String choice : choices)
							{
								if (AbilityUtilities.canAddAssociation(this, ab, choice))
								{
									CategorizedAbilitySelection cas = new CategorizedAbilitySelection(
											cdo, cat, ab, nature, choice);
									cas.addAllPrerequisites(apo.getPrerequisiteList());
									applyAbility(cas);
								}
							}
						}
					}
					else
					{
						CategorizedAbilitySelection cas = new CategorizedAbilitySelection(
								cdo, cat, ab, nature);
						cas.addAllPrerequisites(apo.getPrerequisiteList());
						applyAbility(cas);
					}
				}
			}
		}
		cabFacet.update(id);
	}

	private void applyAbility(CategorizedAbilitySelection cas)
	{
		if (cas.hasPrerequisites())
		{
			conditionalFacet.add(id, cas);
		}
		else
		{
			directAbilityFacet.add(id, cas);
		}
	}

	private void addTemplatesIfMissing(Collection<PCTemplate> templateList)
	{
		for (PCTemplate pct : templateList)
		{
			addTemplate(pct);
		}
	}

	/**
	 * Determine the character's facing.
	 * 
	 * @return The facing.
	 */
	public Point2D.Double getFace()
	{
		return faceFacet.getFace(id);
	}

	/**
	 * Determine the number of hands the character has.
	 * 
	 * @return The number of hands.
	 */
	public int getHands()
	{
		return handsFacet.getHands(id);
	}

	/**
	 * Determine the number of legs the character has.
	 * 
	 * @return The number of legs.
	 */
	public int getLegs()
	{
		return legsFacet.getLegs(id);
	}

	/**
	 * Determine the character's reach. This is based on their race, any applied
	 * templates and any other bonuses to reach.
	 * 
	 * @return The reach radius.
	 */
	public int getReach()
	{
		return reachFacet.getReach(id);
	}

	/**
	 * Gets a list of ability objects whose name is the same as that of the ability object
	 * passed in, no matter what category they were added in.
	 * 
	 * @param ab the ability
	 * 
	 * @return the list of matching feats
	 */
	public List<Ability> getFeatNamedAnyCat(Ability ab)
	{
		List<Ability> feats = new ArrayList<Ability>();
		for (AbilityCategory cat : SettingsHandler.getGame()
			.getAllAbilityCategories())
		{
			Ability tempFeat = getMatchingAbility(cat, ab);
			if (tempFeat != null)
			{
				feats.add(tempFeat);
			}
		}

		return feats;
	}

	public boolean hasSpellInSpellbook(Spell spell, String spellbookname)
	{
		for (PObject po : getPObjectList())
		{
			List<CharacterSpell> csl =
					getCharacterSpells(po, spell, spellbookname, -1);
			if (csl != null && !csl.isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	public void resetEpicCache()
	{
		epicBAB = null;
		epicCheckMap.clear();
	}

	// public double getBonusValue(final String aBonusType, final String
	// aBonusName )
	// {
	// return TypedBonus.totalBonuses(getBonusesTo(aBonusType, aBonusName));
	// }

	public int getCritRange(Equipment e, boolean primary)
	{
		if (!primary && !e.isDouble())
		{
			return 0;
		}
		int raw = e.getRawCritRange(primary);
		int add = (int) e.bonusTo(this, "EQMWEAPON", "CRITRANGEADD", primary);
		int dbl =
				1 + (int) e.bonusTo(this, "EQMWEAPON", "CRITRANGEDOUBLE",
					primary);
		return raw * dbl + add;

	}

	/**
	 * Retrieve the list of the keynames of any feats
	 * that the PC qualifies for at the supplied level and
	 * hit dice. 
	 * 
	 * @param pct
	 *
	 * @param level
	 *
	 * @param hitdice
	 *
	 * @param addNew
	 *
	 * @return
	 */
	public List<AbilitySelection> feats(
		PCTemplate pct,
		final int level,
		final int hitdice,
		final boolean addNew)
	{
		final List<AbilitySelection> feats = new ArrayList<AbilitySelection>();

		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				Collection<? extends AbilitySelection> featList =
						getAssocList(lt, AssociationListKey.TEMPLATE_FEAT);
				if (featList == null && addNew
					&& lt.get(IntegerKey.LEVEL) <= level)
				{
					featList = getLevelFeat(lt);
				}
				if (featList != null)
				{
					feats.addAll(featList);
				}
			}
		}
		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			Collection<? extends AbilitySelection> featList =
					getAssocList(lt, AssociationListKey.TEMPLATE_FEAT);
			if (featList == null && addNew && lt.get(IntegerKey.LEVEL) <= level)
			{
				featList = getLevelFeat(lt);
			}
			if (featList != null)
			{
				feats.addAll(featList);
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			Collection<? extends AbilitySelection> featList =
					getAssocList(lt, AssociationListKey.TEMPLATE_FEAT);
			if (featList == null && addNew
				&& lt.get(IntegerKey.HD_MAX) <= hitdice
				&& lt.get(IntegerKey.HD_MIN) >= hitdice)
			{
				featList = getLevelFeat(lt);
			}
			if (featList != null)
			{
				feats.addAll(featList);
			}
		}

		Collection<? extends AbilitySelection> featList = getAssocList(pct,
				AssociationListKey.TEMPLATE_FEAT);
		if (featList == null && addNew)
		{
			featList = getLevelFeat(pct);
		}
		if (featList != null)
		{
			feats.addAll(featList);
		}

		return feats;
	}

	/**
	 * This is the function that implements a chooser for Feats granted by level
	 * and/or HD by Templates.
	 * 
	 * @param pct
	 *            The template to be checked for the choices to offer
	 */
	private Collection<? extends AbilitySelection> getLevelFeat(PCTemplate pct)
	{
		PersistentTransitionChoice<AbilitySelection> choice = pct.get(ObjectKey.TEMPLATE_FEAT);
		if (choice == null)
		{
			return Collections.emptyList();
		}
		Collection<? extends AbilitySelection> result = choice
				.driveChoice(this);
		choice.act(result, pct, this);
		return result;
	}

	public Collection<PCTemplate> getTemplatesAdded(CDOMObject po)
	{
		return addedTemplateFacet.getFromSource(id, po);
	}

	public void setTemplatesAdded(CDOMObject po, PCTemplate pct)
	{
		addedTemplateFacet.add(id, pct, po);
	}

	public boolean isClassSkill(Skill sk, PCClass pcc)
	{
		return SkillCost.CLASS.equals(cache.getSkillCost(this, sk, pcc));
	}

	public boolean isClassSkill(Skill sk)
	{
		for (PCClass cl : getClassSet())
		{
			if (isClassSkill(sk, cl))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isCrossClassSkill(Skill sk, PCClass pcc)
	{
		return SkillCost.CROSS_CLASS.equals(cache.getSkillCost(this, sk, pcc));
	}

	private boolean isCrossClassSkill(Skill sk)
	{
		for (PCClass cl : getClassSet())
		{
			if (isCrossClassSkill(sk, cl))
			{
				return true;
			}
		}
		return false;
	}

	public SkillCost getSkillCostForClass(Skill sk, PCClass cl)
	{
		return cache.getSkillCost(this, sk, cl);
	}

	public void addAssociation(CDOMObject obj, String o)
	{
		assocSupt.addAssoc(obj, AssociationListKey.CHOICES,
			new FixedStringList(o));
		List<ChooseResultActor> actors = obj.getListFor(ListKey.CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseResultActor cra : actors)
			{
				cra.apply(this, obj, o);
			}
		}
	}

	public void addAssociation(CDOMObject obj, FixedStringList o)
	{
		assocSupt.addAssoc(obj, AssociationListKey.CHOICES, o);
	}

	public boolean containsAssociated(CDOMObject obj, String o)
	{
		List<FixedStringList> list =
				assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		if (list != null)
		{
			for (FixedStringList fsl : list)
			{
				if (FixedStringList.CASE_INSENSITIVE_ORDER.compare(fsl, new FixedStringList(o)) == 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	public int getSelectCorrectedAssociationCount(CDOMObject obj)
	{
		return assocSupt.getAssocCount(obj, AssociationListKey.CHOICES)
			/ obj.getSafe(FormulaKey.SELECT).resolve(this, "").intValue();
	}

	public List<String> getAssociationList(CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		List<FixedStringList> assocList =
				assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		if (assocList != null)
		{
			for (FixedStringList ac : assocList)
			{
				final String choiceStr = ac.get(0);
				list.add(choiceStr);
			}
		}
		return list;
	}

	public boolean hasAssociations(CDOMObject obj)
	{
		return assocSupt.hasAssocs(obj, AssociationListKey.CHOICES);
	}

	public List<String> removeAllAssociations(CDOMObject obj)
	{
		List<String> list = getAssociationList(obj);
		assocSupt.removeAllAssocs(obj, AssociationListKey.CHOICES);
		List<ChooseResultActor> actors = obj.getListFor(ListKey.CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseResultActor cra : actors)
			{
				for (String o : list)
				{
					cra.remove(this, obj, o);
				}
			}
		}
		return list;
	}

	public void removeAssociation(CDOMObject obj, String o)
	{
		List<ChooseResultActor> actors = obj.getListFor(ListKey.CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseResultActor cra : actors)
			{
				cra.remove(this, obj, o);
			}
		}
		assocSupt.removeAssoc(obj, AssociationListKey.CHOICES,
			new FixedStringList(o));
	}

	public int getDetailedAssociationCount(CDOMObject obj)
	{
		List<FixedStringList> assocs =
				assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		int count = 0;
		if (assocs != null)
		{
			for (FixedStringList choice : assocs)
			{
				count += choice.size();
			}
		}
		return count;
	}

	public List<FixedStringList> getDetailedAssociations(CDOMObject obj)
	{
		List<FixedStringList> list = assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		if (list == null)
		{
			list = Collections.emptyList();
		}
		return list;
	}

	public List<String> getExpandedAssociations(CDOMObject obj)
	{
		List<FixedStringList> assocs =
				assocSupt.getAssocList(obj, AssociationListKey.CHOICES);
		List<String> list = new ArrayList<String>();
		if (assocs != null)
		{
			for (FixedStringList choice : assocs)
			{
				for (String s : choice)
				{
					list.add(s);
				}
			}
		}
		return list;
	}

	public String getFirstAssociation(CDOMObject obj)
	{
		return assocSupt.getAssocList(obj, AssociationListKey.CHOICES).get(0)
			.get(0);
	}

	public <T> void addAssoc(Object obj, AssociationListKey<T> ak, T o)
	{
		assocSupt.addAssoc(obj, ak, o);
	}

	public int getAssocCount(Object obj, AssociationListKey<?> ak)
	{
		return assocSupt.getAssocCount(obj, ak);
	}

	public <T> List<T> getAssocList(Object obj, AssociationListKey<T> ak)
	{
		return assocSupt.getAssocList(obj, ak);
	}

	public <T extends Comparable<T>> void sortAssocList(Object obj,
		AssociationListKey<T> ak)
	{
		assocSupt.sortAssocList(obj, ak);
	}

	public <T> Collection<T> getSafeAssocList(Object obj,
		AssociationListKey<T> alk)
	{
		List<T> list = getAssocList(obj, alk);
		if (list == null)
		{
			return new ArrayList<T>();
		}
		return list;
	}

	public <T> List<T> removeAllAssocs(Object obj, AssociationListKey<T> ak)
	{
		return assocSupt.removeAllAssocs(obj, ak);
	}

	public <T> void removeAssoc(Object obj, AssociationListKey<T> ak, T o)
	{
		assocSupt.removeAssoc(obj, ak, o);
	}

	public <T> T getAssoc(Object obj, AssociationKey<T> ak)
	{
		return assocSupt.getAssoc(obj, ak);
	}

	public boolean hasAssocs(Object obj, AssociationKey<?> ak)
	{
		return assocSupt.hasAssocs(obj, ak);
	}

	public <T> void removeAssoc(Object obj, AssociationKey<T> ak)
	{
		assocSupt.removeAssoc(obj, ak);
	}

	public <T> void setAssoc(Object obj, AssociationKey<T> ak, T o)
	{
		assocSupt.setAssoc(obj, ak, o);
	}

	public boolean hasUnlockedStat(PCStat stat)
	{
		return unlockedStatFacet.contains(id, stat);
	}

	public Number getLockedStat(PCStat stat)
	{
		return statLockFacet.getLockedStat(id, stat);
	}

	public String getDescription(PObject cdo)
	{
		List<Description> theDescriptions =
				cdo.getListFor(cdo.getDescriptionKey());

		if (theDescriptions == null)
		{
			return Constants.EMPTY_STRING;
		}
		final StringBuilder sb = new StringBuilder();
		boolean needcomma = false;
		for (final Description desc : theDescriptions)
		{
			final String str = desc.getDescription(this, cdo);
			if (str.length() > 0)
			{
				if (needcomma)
				{
					sb.append(Constants.COMMA).append(' ');
				}
				sb.append(str);
				needcomma = true;
			}
		}
		return sb.toString();
	}

	public HashMapToList<CDOMList<Spell>, Integer> getMasterLevelInfo(Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> hml =
				cache.get(MapKey.SPELL_MASTER_INFO, sp);
		if (hml == null)
		{
			hml = SpellLevel.getMasterLevelInfo(this, sp);
			cache.addToMapFor(MapKey.SPELL_MASTER_INFO, sp, hml);
		}
		HashMapToList<CDOMList<Spell>, Integer> newhml =
				new HashMapToList<CDOMList<Spell>, Integer>();
		newhml.addAllLists(hml);
		return newhml;
	}

	public HashMapToList<CDOMList<Spell>, Integer> getPCBasedLevelInfo(Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> hml =
				cache.get(MapKey.SPELL_PC_INFO, sp);
		if (hml == null)
		{
			hml = availSpellFacet.getPCBasedLevelInfo(id, sp);
			cache.addToMapFor(MapKey.SPELL_PC_INFO, sp, hml);
		}
		HashMapToList<CDOMList<Spell>, Integer> newhml =
				new HashMapToList<CDOMList<Spell>, Integer>();
		newhml.addAllLists(hml);
		return newhml;
	}

	public DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> getPCBasedLevelInfo()
	{
		DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> map = cache
				.get(ObjectKey.SPELL_PC_INFO);
		if (map == null)
		{
			map = availSpellFacet.getPCBasedLevelInfo(id);
			cache.put(ObjectKey.SPELL_PC_INFO, map);
		}
		DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> newmap = new DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer>();
		newmap.addAll(map);
		return newmap;
	}

	/**
	 * This method gets the information about the levels at which classes and
	 * domains may cast the spell.
	 *
	 * @param sp The spell to get the info for.
	 *
	 * @return Map containing the class levels and domains that may cast the
	 *         spell
	 */
	public HashMapToList<CDOMList<Spell>, Integer> getLevelInfo(Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> levelInfo = getMasterLevelInfo(sp);
		levelInfo.addAllLists(getPCBasedLevelInfo(sp));
		return levelInfo;
	}

	public CharacterSpell getCharacterSpellForSpell(PObject po, Spell spell)
	{
		List<CharacterSpell> cspells = new ArrayList<CharacterSpell>(
				getCharacterSpells(po));
		// Add in the spells granted by objects
		SpellLevel.addBonusKnowSpellsToList(this, po, cspells);

		for (CharacterSpell cs : cspells)
		{
			Spell sp = cs.getSpell();
			if (spell.equals(sp) && (cs.getOwner().equals(po)))
			{
				return cs;
			}
		}
		return null;
	}

	/**
	 * Get a list of CharacterSpells from the character spell list.
	 * @param spellSource
	 * @param aSpell
	 * @param book
	 * @param level
	 * @return list of CharacterSpells from the character spell list
	 */
	public final List<CharacterSpell> getCharacterSpells(
		PObject spellSource,
		final Spell aSpell,
		final String book,
		final int level)
	{
		List<CharacterSpell> csList = new ArrayList<CharacterSpell>(getCharacterSpells(spellSource));

		// Add in the spells granted by objects
		SpellLevel.addBonusKnowSpellsToList(this, spellSource, csList);

		final ArrayList<CharacterSpell> aList = new ArrayList<CharacterSpell>();
		if (csList.size() == 0)
		{
			return aList;
		}

		for (CharacterSpell cs : csList)
		{
			if ((aSpell == null) || cs.getSpell().equals(aSpell))
			{
				final SpellInfo si = cs.getSpellInfoFor(this, book, level, -1, null);

				if (si != null)
				{
					aList.add(cs);
				}
			}
		}

		return aList;
	}

	/**
	 * Returns DC for a spell and SpellInfo.
	 * @param sp the spell
	 * @param cs TODO
	 * @param si the spell info
	 * @return DC for a spell and SpellInfo
	 */
	public int getDC(final Spell sp, CharacterSpell cs, final SpellInfo si)
	{
		PObject ow = null;
		int spellLevel = 0;
		int metaDC = 0;
		
		spellLevel = si.getActualLevel();
		ow = cs.getOwner();

		String fixedDC = si.getFixedDC();
		// TODO Temp fix for 1223858, better fix would be to move fixedDC to
		// spellInfo
		/*
		 * TODO Need to evaluate how duplicative this logic is and what is
		 * really necessary
		 */
		if (fixedDC != null && "INNATE".equalsIgnoreCase(si.getBook()))
		{
			return getVariableValue(fixedDC, "").intValue();
		}

		// Check for a non class based fixed DC
		if (fixedDC != null && ow != null && !(ow instanceof PCClass))
		{
			return getVariableValue(fixedDC, "").intValue();
		}
		
		if (si.getFeatList() != null)
		{
			for (Ability metaFeat : si.getFeatList())
			{
				spellLevel -= metaFeat.getSafe(IntegerKey.ADD_SPELL_LEVEL);
				metaDC += BonusCalc.bonusTo(metaFeat, "DC", "FEATBONUS", this,
						this);
			}
		}
		
		return getDC(sp, null, spellLevel, metaDC, ow);
	}

	public int getDC(final Spell sp, PCClass aClass, int spellLevel,
			int metaDC, CDOMObject ow)
	{
		String bonDomain = "";
		if (ow instanceof Domain)
		{
			bonDomain = "DOMAIN." + ow.getKeyName();

			ClassSource source = getDomainSource((Domain) ow);
			if (source != null)
			{
				aClass = getClassKeyed(source.getPcclass().getKeyName());
			}
		}

		boolean useStatFromSpell = false;
		String bonClass = "";
		String spellType = "";
		String classKey = "";

		if ((aClass != null) || (ow instanceof PCClass))
		{
			if ((aClass == null) || (ow instanceof PCClass))
			{
				aClass = (PCClass) ow;
			}

			bonClass = "CLASS." + aClass.getKeyName();
			classKey = "CLASS:" + aClass.getKeyName();
			spellType = aClass.getSpellType();
			useStatFromSpell = aClass.getSafe(ObjectKey.USE_SPELL_SPELL_STAT);
		}

		if (!(ow instanceof PCClass) && !(ow instanceof Domain))
		{
			// get BASESPELLSTAT from spell itself
			useStatFromSpell = true;
		}

		// set the spell Level used in aPC.getVariableValue()
		setSpellLevelTemp(spellLevel);

		// must be done after spellLevel is set above
		int dc =
				getVariableValue(Globals.getGameModeBaseSpellDC(), classKey)
					.intValue()
					+ metaDC;
		dc += (int) getTotalBonusTo("DC", "ALLSPELLS");

		if (useStatFromSpell)
		{
			// get the BASESPELLSTAT from the spell itself
			PCStat stat = sp.get(ObjectKey.SPELL_STAT);
			if (stat != null)
			{
				dc += StatAnalysis.getStatModFor(this, stat);
			}
		}

		if (sp.getKeyName().length() > 0)
		{
			dc += (int) getTotalBonusTo("DC", "SPELL." + sp.getKeyName());
		}

		// DOMAIN.name
		if (bonDomain.length() > 0)
		{
			dc += (int) getTotalBonusTo("DC", bonDomain);
		}

		// CLASS.name
		if (bonClass.length() > 0)
		{
			dc += (int) getTotalBonusTo("DC", bonClass);
		}

		dc += (int) getTotalBonusTo("DC", "TYPE." + spellType);

		if (spellType.equals("ALL"))
		{
			for (Type aType : sp.getTrueTypeList(false))
			{
				dc += (int) getTotalBonusTo("DC", "TYPE." + aType);
			}
		}

		for (SpellSchool aType : sp.getSafeListFor(ListKey.SPELL_SCHOOL))
		{
			dc += (int) getTotalBonusTo("DC", "SCHOOL." + aType.toString());
		}

		for (String aType : sp.getSafeListFor(ListKey.SPELL_SUBSCHOOL))
		{
			dc += (int) getTotalBonusTo("DC", "SUBSCHOOL." + aType);
		}

		for (String aType : sp.getSafeListFor(ListKey.SPELL_DESCRIPTOR))
		{
			dc += (int) getTotalBonusTo("DC", "DESCRIPTOR." + aType);
		}

		setSpellLevelTemp(0); // reset

		return dc;
	}

	/**
	 * Returns concentration bonus for a spell and SpellInfo.
	 * @param sp the spell
	 * @param cs TODO
	 * @param si the spell info
	 * @return concentration bonus for a spell and SpellInfo
	 */
	public int getConcentration(final Spell sp, CharacterSpell cs, final SpellInfo si)
	{
		PObject ow = null;
		int spellLevel = 0;
		int metaConcentration = 0;
		
		spellLevel = si.getActualLevel();
		ow = cs.getOwner();

		String fixedConcentration = si.getFixedConcentration();

		if (fixedConcentration != null && "INNATE".equalsIgnoreCase(si.getBook()))
		{
			return getVariableValue(fixedConcentration, "").intValue();
		}

		// Check for a non class based fixed Concentration
		if (fixedConcentration != null && ow != null && !(ow instanceof PCClass))
		{
			return getVariableValue(fixedConcentration, "").intValue();
		}
		
		if (si.getFeatList() != null)
		{
			for (Ability metaFeat : si.getFeatList())
			{
				spellLevel -= metaFeat.getSafe(IntegerKey.ADD_SPELL_LEVEL);
				metaConcentration += BonusCalc.bonusTo(metaFeat, "CONCENTRATION", "FEATBONUS", this,
						this);
			}
		}
		
		return getConcentration(sp, cs, null, spellLevel, metaConcentration, ow);
	}

	public int getConcentration(final Spell sp, final CharacterSpell aSpell, PCClass aClass, 
			int spellLevel, int metaConcentration, CDOMObject ow)
	{
		String bonDomain = "";
		if (ow instanceof Domain)
		{
			bonDomain = "DOMAIN." + ow.getKeyName();

			ClassSource source = getDomainSource((Domain) ow);
			if (source != null)
			{
				aClass = getClassKeyed(source.getPcclass().getKeyName());
			}
		}

		boolean useStatFromSpell = false;
		String bonClass = "";
		String spellType = "";
		String classKey = "";

		if ((aClass != null) || (ow instanceof PCClass))
		{
			if ((aClass == null) || (ow instanceof PCClass))
			{
				aClass = (PCClass) ow;
			}

			bonClass = "CLASS." + aClass.getKeyName();
			classKey = "CLASS:" + aClass.getKeyName();
			spellType = aClass.getSpellType();
			useStatFromSpell = aClass.getSafe(ObjectKey.USE_SPELL_SPELL_STAT);
		}

		if (!(ow instanceof PCClass) && !(ow instanceof Domain))
		{
			// get BASESPELLSTAT from spell itself
			useStatFromSpell = true;
		}

		// set the spell Level used in aPC.getVariableValue()
		setSpellLevelTemp(spellLevel);

		// must be done after spellLevel is set above
		int concentration =
				getVariableValue(aSpell, Globals.getGameModeBaseSpellConcentration(), classKey)
					.intValue()
					+ metaConcentration;
		concentration += (int) getTotalBonusTo("CONCENTRATION", "ALLSPELLS");

		if (useStatFromSpell)
		{
			// get the BASESPELLSTAT from the spell itself
			PCStat stat = sp.get(ObjectKey.SPELL_STAT);
			if (stat != null)
			{
				concentration += StatAnalysis.getStatModFor(this, stat);
			}
		}

		if (sp.getKeyName().length() > 0)
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", "SPELL." + sp.getKeyName());
		}

		// DOMAIN.name
		if (bonDomain.length() > 0)
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", bonDomain);
		}

		// CLASS.name
		if (bonClass.length() > 0)
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", bonClass);
		}

		concentration += (int) getTotalBonusTo("CONCENTRATION", "TYPE." + spellType);

		if (spellType.equals("ALL"))
		{
			for (Type aType : sp.getTrueTypeList(false))
			{
				concentration += (int) getTotalBonusTo("CONCENTRATION", "TYPE." + aType);
			}
		}

		for (SpellSchool aType : sp.getSafeListFor(ListKey.SPELL_SCHOOL))
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", "SCHOOL." + aType.toString());
		}

		for (String aType : sp.getSafeListFor(ListKey.SPELL_SUBSCHOOL))
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", "SUBSCHOOL." + aType);
		}

		for (String aType : sp.getSafeListFor(ListKey.SPELL_DESCRIPTOR))
		{
			concentration += (int) getTotalBonusTo("CONCENTRATION", "DESCRIPTOR." + aType);
		}

		setSpellLevelTemp(0); // reset

		return concentration;
	}
	
	public boolean hasSkill(Skill skill)
	{
		return skillFacet.contains(id, skill);
	}

	public boolean hasTemplate(PCTemplate template)
	{
		return templateFacet.contains(id, template);
	}

	public Collection<PCStat> getStatSet()
	{
		return statFacet.getSet(id);
	}

	public boolean hasDefaultDomainSource()
	{
		return defaultDomainSource != null;
	}
	
	public ClassSource getDefaultDomainSource()
	{
		return defaultDomainSource;
	}
	
	public void setDefaultDomainSource(ClassSource cs)
	{
		defaultDomainSource = cs;
	}

	public void addDomain(Domain domain)
	{
		addDomain(domain, defaultDomainSource);
	}

	public void addDomain(Domain domain, ClassSource source)
	{
		domainFacet.add(id, domain, source);
		if (source != null)
		{
			String classKey = source.getPcclass().getKeyName();
			PCClass domainClass = getClassKeyed(classKey);
			if (domainClass != null)
			{
				final int _maxLevel = this.getSpellSupport(domainClass).getMaxCastLevel();
				DomainApplication.addSpellsToClassForLevels(this, domain,
						domainClass, 0, _maxLevel);
			}
		}
		setDirty(true);
	}

	public boolean hasDomain(Domain domain)
	{
		return domainFacet.contains(id, domain);
	}

	public void removeDomain(Domain domain)
	{
		domainFacet.remove(id, domain);
		setDirty(true);
	}

	public boolean hasDomains()
	{
		return !domainFacet.isEmpty(id);
	}

	public int getDomainCount()
	{
		return domainFacet.getCount(id);
	}

	public Set<Domain> getDomainSet()
	{
		return domainFacet.getSet(id);
	}

		public Set<Domain> getSortedDomainSet()
	{
		SortedSet<Domain> domains = new TreeSet<Domain>(
				CDOMObjectUtilities.CDOM_SORTER);
		domains.addAll(getDomainSet());
		return domains;
	}

	public ClassSource getDomainSource(Domain d)
	{
		return domainFacet.getSource(id, d);
	}

	public void setTempBonusMap(
			Map<BonusObj, BonusManager.TempBonusInfo> tempBonusMap)
	{
		bonusManager.setTempBonusMap(tempBonusMap);
	}

	public Map<String, String> getBonusStrings(String bonusString,
			String substring)
	{
		return bonusManager.getBonuses(bonusString, substring);
	}

	public Set<String> getTempBonusNames()
	{
		return bonusManager.getTempBonusNames();
	}

	public boolean isApplied(BonusObj bonus)
	{
		return appliedBonusFacet.contains(id, bonus);
	}

	public SpellSupportForPCClass getSpellSupport(PCClass cl)
	{
		SpellSupportForPCClass ss = spellSupportFacet.getSource(id, cl);
		if (ss == null)
		{
			ss = new SpellSupportForPCClass(cl);
			spellSupportFacet.add(id, cl, ss);
		}
		return ss;
	}

	public Map<BonusObj, BonusManager.TempBonusInfo> getTempBonusMap(String sourceStr, String targetStr)
	{
		return bonusManager.getTempBonusMap(sourceStr, targetStr);
	}
	
	public String getBonusContext(BonusObj bonus, boolean shortForm)
	{
		return bonusManager.getBonusContext(bonus, shortForm);
	}

	public List<BonusPair> getStringListFromBonus(BonusObj bonus)
	{
		return bonusManager.getStringListFromBonus(bonus);
	}

	public void setApplied(BonusObj bonusObj, boolean bool)
	{
		if (bool)
		{
			appliedBonusFacet.add(id, bonusObj);
		}
		else
		{
			appliedBonusFacet.remove(id, bonusObj);
		}
	}

	public boolean hasTemplates()
	{
		return !templateFacet.isEmpty(id);
	}

	public int getTemplateCount()
	{
		return templateFacet.getCount(id);
	}

	public int getStatCount()
	{
		return statFacet.getCount(id);
	}

	public void removeSkill(Skill sk)
	{
		skillFacet.remove(id, sk);
	}

	public void removeAllSkills()
	{
		skillFacet.removeAll(id);
	}
	
	public void refreshSkillList()
	{
		for (final Skill skill : Globals.getContext().ref
				.getConstructedCDOMObjects(Skill.class))
		{
			if (!hasSkill(skill))
			{
				if (!CoreUtility.doublesEqual(SkillRankControl
						.getSkillRankBonusTo(this, skill), 0.0))
				{
					addSkill(skill);
				}
			}
		}
	}

	public int getLanguageCount()
	{
		return languageFacet.getCount(id);
	}

	public boolean hasLanguage(Language lang)
	{
		return languageFacet.contains(id, lang);
	}

	public void setSubstitutionLevel(PCClass pcc,
			PCClassLevel originalClassLevel)
	{
		try
		{
			classFacet.setClassLevel(id, pcc, originalClassLevel.clone());
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PCClassLevel getActiveClassLevel(PCClass pcc, int lvl)
	{
		return classFacet.getClassLevel(id, pcc, lvl);
	}

	public int getClassCount()
	{
		return classFacet.getCount(id);
	}

	public boolean hasClass()
	{
		return !classFacet.isEmpty(id);
	}

	public void removeClass(PCClass pcc)
	{
		classFacet.removeClass(id, pcc);
	}

	public void addClass(PCClass pcc)
	{
		classFacet.addClass(id, pcc);
	}

	public final int getLevel(PCClass pcc)
	{
		return classFacet.getLevel(id, pcc);
	}

	/**
	 * set the level to arg without impacting spells, hp, or anything else - use
	 * this with great caution only.
	 */
	public final void setLevelWithoutConsequence(PCClass pcc, final int level)
	{
		classFacet.setLevel(id, pcc, level);
	}

	public boolean hasEquipment()
	{
		return !equipmentFacet.isEmpty(id);
	}

	/*
	 * These are present here because they (1) Should be contained within
	 * PlayerCharacter (2) Should disappear once LanguageFacet can be reused
	 * with different parameters in a DI system if we go that direction
	 */
	public static class FreeLanguageFacet extends LanguageFacet {}

	public static class AddLanguageFacet extends LanguageFacet {}

	public static class SkillLanguageFacet extends LanguageFacet {}

	public boolean hasUserVirtualAbility(AbilityCategory cat, Ability abilityInfo)
	{
		return abFacet.contains(id, cat, Nature.VIRTUAL, abilityInfo);
	}

	public void addUserAbility(CategorizedAbilitySelection cas)
	{
		Ability newAbility = cas.getAbility();
		abFacet.add(id, cas.getAbilityCategory(), cas.getNature(), newAbility);
		String choice = cas.getSelection();
		if (choice != null)
		{
			if (AbilityUtilities.canAddAssociation(this, newAbility, choice))
			{
				addAssociation(newAbility, choice);
			}
		}
	}

	public Set<Ability> getAbilityList(Category<Ability> cat, Nature nature)
	{
		Set<Ability> newSet = new HashSet<Ability>();
		newSet.addAll(abFacet.get(id, cat, nature));
		newSet.addAll(grantedAbilityFacet.get(id, cat, nature));
		return newSet;
	}
		
	public Nature getAbilityNature(Category<Ability> cat, Ability ability)
	{
		Nature n = abFacet.getNature(id, cat, ability);
		Nature n2 = grantedAbilityFacet.getNature(id, cat, ability);
		return Nature.getBestNature(n, n2);
	}

	public Nature getAbilityNature(Ability ability)
	{
		Nature n = abFacet.getNature(id, ability.getCDOMCategory(), ability);
		Nature n2 = grantedAbilityFacet.getNature(id, ability.getCDOMCategory(), ability);
		return Nature.getBestNature(n, n2);
	}

	public boolean containsKit(Kit kit)
	{
		return kitFacet.contains(id, kit);
	}
	
	/*
	 * Yes, this method really is what it says. The primary reason for this
	 * being in PlayerCharacter is that I don't want to export id at this time
	 * (it's private to avoid changing too much outside of PlayerCharacter at
	 * this time). In the future, the Unit Tests should behave better - but I
	 * think that generally goes along with Equipment
	 * Location/Equipped/NumberEquipped/NumberCarried all being made consistent
	 * (they are highly correlated, but no control is exerted over them by
	 * Equipment to ensure appropriate states are maintained)
	 */
	public void doAfavorForAunitTestThatIgnoresEquippingRules()
	{
		equippedFacet.reset(id);
	}

	public boolean containsRacialSubType(RaceSubType st)
	{
		return subTypesFacet.contains(id, st);
	}

	public int getRacialSubTypeCount()
	{
		return subTypesFacet.getCount(id);
	}

	public void processAddition(CDOMObject cdo)
	{
		for (CDOMReference<PCTemplate> tr : cdo
				.getSafeListFor(ListKey.TEMPLATE))
		{
			addTemplatesIfMissing(tr.getContainedObjects());
		}
		for (CDOMReference ref : cdo.getModifiedLists())
		{
			processAbilityListsOnAdd(cdo, ref);
		}
	}

	public void processRemoval(CDOMObject cdo)
	{
		conditionalFacet.removeAllFromSource(id, cdo);
		directAbilityFacet.removeAllFromSource(id, cdo);
		autoLangFacet.removeAll(id, cdo);
		setDirty(true);
	}

	public void addWeaponBonus(CDOMObject owner, WeaponProf choice)
	{
		wpBonusFacet.add(id, choice, owner);
	}

	public List<WeaponProf> getBonusWeaponProfs(CDOMObject owner)
	{
		return new ArrayList<WeaponProf>(wpBonusFacet.getSet(id, owner));
	}

	public void removeWeaponBonus(CDOMObject owner, WeaponProf choice)
	{
		wpBonusFacet.remove(id, choice, owner);
	}

	public void addFavoredClass(PCClass cls, Object source)
	{
		favClassFacet.add(id, cls, source);
	}

	public void removeFavoredClass(PCClass cls, Object source)
	{
		favClassFacet.remove(id, cls, source);
	}

	public PCClass getLegacyFavoredClass()
	{
		List<? extends PCClass> list = favClassFacet.getSet(id, this);
		if (list.isEmpty())
		{
			return null;
		}
		return list.get(0);
	}

	public void addWeaponProf(CDOMObject owner, WeaponProf choice)
	{
		alWeaponProfFacet.add(id, choice, owner);
	}

	public void removeWeaponProf(CDOMObject owner, WeaponProf choice)
	{
		alWeaponProfFacet.remove(id, choice, owner);
	}

	public Integer getDR(String key)
	{
		return drFacet.getDR(id, key);
	}

	/*
	 * WARNING: Use this method SPARINGLY... and only for transition to the
	 * facet model. It is NOT an excuse to throw around a PlayerCharacter object
	 * when unnecessary
	 */
	public CharID getCharID()
	{
		return id;
	}

	public double getSpellBookCount()
	{
		return spellBookFacet.getCount(id);
	}

	public boolean hasSpellBook(String bookName)
	{
		return spellBookFacet.containsBookNamed(id, bookName);
	}

	public Vision getVision(VisionType type)
	{
		return visionFacet.getActiveVision(id, type);
	}

	public int getVisionCount()
	{
		return visionFacet.getVisionCount(id);
	}

	/*
	 * Size is taken into account for the currentPC via getLoadMultForSize
	 */
	public Float getMaxLoad()
	{
		return loadFacet.getMaxLoad(id);
	}

	public Float getMaxLoad(double mult)
	{
		return loadFacet.getMaxLoad(id, mult);
	}

	public Load getLoadType()
	{
		return loadFacet.getLoadType(id);
	}

	public void addArmorProf(CDOMObject owner, ArmorProf ap)
	{
		armorProfListFacet.add(id, ap, owner);
	}

	public void removeArmorProf(CDOMObject owner, ArmorProf ap)
	{
		armorProfListFacet.remove(id, ap, owner);
	}

	public void addShieldProf(CDOMObject owner, ShieldProf sp)
	{
		shieldProfListFacet.add(id, sp, owner);
	}

	public void removeShieldProf(CDOMObject owner, ShieldProf sp)
	{
		shieldProfListFacet.remove(id, sp, owner);
	}

	public Double getMovementOfType(String moveType)
	{
		return moveResultFacet.getMovementOfType(id, moveType);
	}

	public int getBaseMovement(String moveType, Load load)
	{
		return moveResultFacet.getBaseMovement(id, moveType, load);
	}

	public boolean hasMovement(String moveType)
	{
		return moveResultFacet.hasMovement(id, moveType);
	}

	public List<NamedValue> getMovementValues()
	{
		return moveResultFacet.getMovementValues(id);
	}

	public boolean hasEquipSet()
	{
		return !equipSetFacet.isEmpty(id);
	}

	public boolean hasFollowers()
	{
		return !followerFacet.isEmpty(id);
	}

	public void addAppliedAbility(CategorizedAbilitySelection cas)
	{
		directAbilityFacet.add(id, cas);
	}

	public void removeAppliedAbility(CategorizedAbilitySelection cas)
	{
		directAbilityFacet.remove(id, cas);
	}

	public void addAutoEquipment(Equipment e, CDOMObject obj)
	{
		autoListEquipmentFacet.add(id, e, obj);
	}

	public void removeAutoEquipment(Equipment e, CDOMObject obj)
	{
		autoListEquipmentFacet.remove(id, e, obj);
	}

	public void addMonCSkill(Skill skill, CDOMObject obj)
	{
		monCSkillFacet.add(id, skill, obj);
	}

	public void removeMonCSkill(Skill skill, CDOMObject obj)
	{
		monCSkillFacet.remove(id, skill, obj);
	}

	public Set<Skill> getMonCSkills()
	{
		return monCSkillFacet.getSet(id);
	}

	public Collection<? extends SpellProhibitor> getProhibitedSchools(Object source)
	{
		return prohibitedSchoolFacet.getSet(id, source);
	}

	public boolean containsProhibitedSchools(Object source)
	{
		return prohibitedSchoolFacet.containsFrom(id, source);
	}

	public void addProhibitedSchool(SpellProhibitor prohibSchool, Object source)
	{
		prohibitedSchoolFacet.add(id, prohibSchool, source);
	}

	public void removeProhibitedSchools(Object source)
	{
		prohibitedSchoolFacet.removeAll(id, source);
	}

	public boolean hasCharacterSpells(CDOMObject cdo)
	{
		return activeSpellsFacet.containsFrom(id, cdo);
	}

	public Collection<? extends CharacterSpell> getCharacterSpells(CDOMObject cdo)
	{
		return activeSpellsFacet.getSet(id, cdo);
	}

	public Collection<CharacterSpell> getCharacterSpells(PObject spellSource,
			int level)
	{
		List<CharacterSpell> csList = new ArrayList<CharacterSpell>(
				getCharacterSpells(spellSource));
		// Add in the spells granted by objects
		SpellLevel.addBonusKnowSpellsToList(this, spellSource, csList);

		ArrayList<CharacterSpell> aList = new ArrayList<CharacterSpell>();
		for (CharacterSpell cs : csList)
		{
			if (cs.hasSpellInfoFor(level))
			{
				aList.add(cs);
			}
		}

		return aList;
	}

	public Collection<CharacterSpell> getCharacterSpells(PObject spellSource,
			String bookName)
	{
		List<CharacterSpell> csList = new ArrayList<CharacterSpell>(
				getCharacterSpells(spellSource));
		// Add in the spells granted by objects
		SpellLevel.addBonusKnowSpellsToList(this, spellSource, csList);

		ArrayList<CharacterSpell> aList = new ArrayList<CharacterSpell>();
		for (CharacterSpell cs : csList)
		{
			if (cs.hasSpellInfoFor(bookName))
			{
				aList.add(cs);
			}
		}

		return aList;
	}

	public int getCharacterSpellCount(CDOMObject cdo)
	{
		return activeSpellsFacet.getCountFrom(id, cdo);
	}

	public void addCharacterSpell(CDOMObject cdo, CharacterSpell cs)
	{
		activeSpellsFacet.add(id, cs, cdo);
	}

	public void removeCharacterSpell(CDOMObject cdo, CharacterSpell cs)
	{
		activeSpellsFacet.remove(id, cs, cdo);
	}

	public boolean containsCharacterSpell(CDOMObject cdo, CharacterSpell cs)
	{
		return activeSpellsFacet.containsFrom(id, cs, cdo);
	}

	private void removeAllCharacterSpells(CDOMObject cdo)
	{
		activeSpellsFacet.removeAll(id, cdo);
	}

	public void addBonus(BonusObj bonus, CDOMObject source)
	{
		addedBonusFacet.add(id, bonus, source);
	}

	public List<? extends BonusObj> getAddedBonusList(CDOMObject source)
	{
		return addedBonusFacet.getSet(id, source);
	}

	public void removeAddedBonus(BonusObj bonus, CDOMObject source)
	{
		addedBonusFacet.remove(id, bonus, source);
	}

	public void addGlobalCost(SkillCost sc, Skill skill, CDOMObject obj)
	{
		globalAddedSkillCostFacet.add(id, skill, sc, obj);
	}

	public void removeGlobalCost(SkillCost sc, Skill skill, CDOMObject obj)
	{
		globalAddedSkillCostFacet.remove(id, skill, sc, obj);
	}

	public boolean hasGlobalCost(Skill skill, SkillCost sc)
	{
		return globalSkillCostFacet.contains(id, sc, skill)
				|| globalAddedSkillCostFacet.contains(id, skill, sc);
	}

	public void addLocalCost(PCClass pcc, Skill skill, SkillCost sc,
			CDOMObject owner)
	{
		localAddedSkillCostFacet.add(id, pcc, skill, sc, owner);
	}

	public void removeLocalCost(PCClass pcc, Skill skill, SkillCost sc,
			CDOMObject owner)
	{
		localAddedSkillCostFacet.remove(id, pcc, skill, sc, owner);
	}

	public boolean hasLocalCost(PCClass cl, Skill skill, SkillCost sc)
	{
		return localSkillCostFacet.contains(id, cl, sc, skill)
				|| localAddedSkillCostFacet.contains(id, cl, skill, sc);
	}

	public boolean hasLocalCost(Collection<ClassSkillList> coll, Skill skill,
			SkillCost sc)
	{
		for (ClassSkillList csl : coll)
		{
			if (listSkillCostFacet.contains(id, csl, sc, skill))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasMasterSkill(List<ClassSkillList> skillLists, Skill sk)
	{
		for (ClassSkillList csl : skillLists)
		{
			if (masterSkillFacet.hasMasterSkill(csl, sk))
			{
				return true;
			}
		}
		return false;
	}

	public String getSubClassName(PCClass cl)
	{
		return subClassFacet.getSource(id, cl);
	}

	public void setSubClassName(PCClass cl, String key)
	{
		subClassFacet.add(id, cl, key);
	}

	public boolean hasTempApplied(CDOMObject mod)
	{
		return bonusManager.hasTempBonusesApplied(mod);
	}

	public Collection<BonusContainer> getBonusContainerList()
	{
		List<BonusContainer> list = new ArrayList<BonusContainer>(getCDOMObjectList());
		list.add(getAgeSet());
		GameMode gm = SettingsHandler.getGame();
		if (gm.isPurchaseStatMode())
		{
			PointBuyMethod pbm = gm.getContext().ref
					.silentlyGetConstructedCDOMObject(PointBuyMethod.class, gm
							.getPurchaseModeMethodName());
			list.add(pbm);
		}
		return list;
	}

	public int getAgeSetIndex()
	{
		return ageSetFacet.getAgeSetIndex(id);
	}

	public AgeSet getAgeSet()
	{
		return ageSetFacet.get(id);
	}

	public SkillCost skillCostForPCClass(Skill sk, PCClass aClass)
	{
		if (this.isClassSkill(aClass, sk))
		{
			return SkillCost.CLASS;
		}
		else if (sk.getSafe(ObjectKey.EXCLUSIVE)
				&& !this.isCrossClassSkill(aClass, sk))
		{
			return SkillCost.EXCLUSIVE;
		}
		else
		{
			return SkillCost.CROSS_CLASS;
		}
	}

	public boolean isCrossClassSkill(PCClass aClass, Skill sk)
	{
		if ((aClass == null) || this.isClassSkill(aClass, sk))
		{
			return false;
		}
		List<ClassSkillList> classSkillList = ClassSkillApplication
				.getClassSkillList(this, aClass);
	
		return hasGlobalCost(sk, SkillCost.CROSS_CLASS)
				|| hasLocalCost(aClass, sk, SkillCost.CROSS_CLASS)
				|| hasLocalCost(classSkillList, sk, SkillCost.CROSS_CLASS);
	}

	public boolean isClassSkill(PCClass aClass, Skill sk)
	{
		if (aClass == null)
		{
			return false;
		}
		
		// test for SKILLLIST skill
		// TODO Can this be eliminated?
		if (aClass.hasClassSkill(this, sk))
		{
			return true;
		}
	
		List<ClassSkillList> classSkillList = ClassSkillApplication
				.getClassSkillList(this, aClass);
		return hasGlobalCost(sk, SkillCost.CLASS)
				|| hasLocalCost(aClass, sk, SkillCost.CLASS)
				|| hasLocalCost(classSkillList, sk, SkillCost.CLASS)
				|| hasMasterSkill(classSkillList, sk);
	}

	public float getSkillRank(Skill sk)
	{
		return SkillRankControl.getRank(this, sk);
	}

	public boolean isQualified(CDOMObject po)
	{
		return po.qualifies(this, po);
	}

	public void reInheritClassLevels(PCClass pcc)
	{
		try
		{
			for (PCClassLevel pcl : pcc.getOriginalClassLevelCollection())
			{
				classFacet.setClassLevel(id, pcc, pcl);
			}
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addUserVirtualAbility(AbilityCategory cat, Ability newAbility)
	{
		abFacet.add(id, cat, Nature.VIRTUAL, newAbility);
	}
	
	public void checkSkillModChange()
	{
		int modSkillPointsCurrent = (int) getStatBonusTo("MODSKILLPOINTS", "NUMBER");
		if (modSkillPointsBuffer != modSkillPointsCurrent)
		{
			modSkillPointsBuffer = modSkillPointsCurrent;

			for (PCClass pcClass : getClassSet())
			{
				for (PCLevelInfo pi : getLevelInfo())
				{
					final int newSkillPointsGained =
						pcClass.recalcSkillPointMod(this, pi.getClassLevel());
					if (pi.getClassKeyName().equals(pcClass.getKeyName()))
					{
						final int formerGained = pi.getSkillPointsGained(this);
						pi.setSkillPointsGained(this, newSkillPointsGained);
						pi.setSkillPointsRemaining(pi.getSkillPointsRemaining()
								+ newSkillPointsGained - formerGained);
						setAssoc(pcClass,
							AssociationKey.SKILL_POOL,
							pcClass.getSkillPool(this)
								+ newSkillPointsGained
								- formerGained);
						setDirty(true);
					}
				}
			}
		}
	}

	public BioSet getBioSet()
	{
		return bioSetFacet.get(id);
	}

	public HitDie getLevelHitDie(PCClass pcClass, final int classLevel)
	{
		return hitPointFacet.getLevelHitDie(id, pcClass, classLevel);
	}

	/**
	 * Rolls hp for the current level according to the rules set in options.
	 *
	 * @param pcClass TODO
	 * @param aLevel
	 * @param first
	 */
	/*
	 * REFACTOR This really needs to be part of the PCClassLevel importing into
	 * a PlayerCharacter? Some thought needs to be put into where this stuff is
	 * stored - should PCLevelInfo be adapted to store all of the non-static
	 * information about a PCClassLevel?
	 */
	public void rollHP(PCClass pcClass, int aLevel, boolean first)
	{
		hitPointFacet.rollHP(id, pcClass, aLevel, first);
		setDirty(true);
	}

	public void setHP(PCClassLevel pcl, Integer hp)
	{
		hitPointFacet.set(id, pcl, hp);
	}

	public Integer getHP(PCClassLevel pcl)
	{
		return hitPointFacet.get(id, pcl);
	}

	public void removeHP(PCClassLevel pcl)
	{
		hitPointFacet.remove(id, pcl);
	}

	public void addClassSpellList(CDOMListObject<Spell> list, PCClass pcClass)
	{
		spellListFacet.add(id, list, pcClass);
	}

	public List<? extends CDOMList<Spell>> getSpellLists(CDOMObject cdo)
	{
		return spellListFacet.getSet(id, cdo);
	}

	public boolean hasSpellList(CDOMObject cdo, CDOMList<Spell> list)
	{
		return spellListFacet.containsFrom(id, list, cdo);
	}

	public void setSpellLists(PCClass pcClass)
	{
		classSpellListFacet.process(id, pcClass);
	}

	public void addDefaultSpellList(PCClass pcc)
	{
		classSpellListFacet.addDefaultSpellList(id, pcc);
	}
}
