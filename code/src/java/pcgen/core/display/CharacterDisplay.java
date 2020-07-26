/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.display;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import pcgen.base.formula.Formula;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MovementType;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.facet.ActiveSpellsFacet;
import pcgen.cdom.facet.AutoLanguageGrantedFacet;
import pcgen.cdom.facet.AutoLanguageUnconditionalFacet;
import pcgen.cdom.facet.DamageReductionFacet;
import pcgen.cdom.facet.EquipSetFacet;
import pcgen.cdom.facet.EquipmentFacet;
import pcgen.cdom.facet.EquippedEquipmentFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.HitPointFacet;
import pcgen.cdom.facet.KitFacet;
import pcgen.cdom.facet.LevelInfoFacet;
import pcgen.cdom.facet.MasterFacet;
import pcgen.cdom.facet.NoteItemFacet;
import pcgen.cdom.facet.PrimaryWeaponFacet;
import pcgen.cdom.facet.SecondaryWeaponFacet;
import pcgen.cdom.facet.SkillRankFacet;
import pcgen.cdom.facet.SpellBookFacet;
import pcgen.cdom.facet.SpellListFacet;
import pcgen.cdom.facet.StartingLanguageFacet;
import pcgen.cdom.facet.StatBonusFacet;
import pcgen.cdom.facet.StatCalcFacet;
import pcgen.cdom.facet.StatValueFacet;
import pcgen.cdom.facet.SubClassFacet;
import pcgen.cdom.facet.SubstitutionClassFacet;
import pcgen.cdom.facet.XPTableFacet;
import pcgen.cdom.facet.analysis.AgeSetFacet;
import pcgen.cdom.facet.analysis.ArmorClassFacet;
import pcgen.cdom.facet.analysis.BaseMovementFacet;
import pcgen.cdom.facet.analysis.ChallengeRatingFacet;
import pcgen.cdom.facet.analysis.FavoredClassFacet;
import pcgen.cdom.facet.analysis.FollowerOptionFacet;
import pcgen.cdom.facet.analysis.HasAnyFavoredClassFacet;
import pcgen.cdom.facet.analysis.InitiativeFacet;
import pcgen.cdom.facet.analysis.LegsFacet;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.LevelTableFacet;
import pcgen.cdom.facet.analysis.LoadFacet;
import pcgen.cdom.facet.analysis.MovementResultFacet;
import pcgen.cdom.facet.analysis.MultiClassFacet;
import pcgen.cdom.facet.analysis.NonAbilityFacet;
import pcgen.cdom.facet.analysis.NonProficiencyPenaltyFacet;
import pcgen.cdom.facet.analysis.RaceTypeFacet;
import pcgen.cdom.facet.analysis.RacialSubTypesFacet;
import pcgen.cdom.facet.analysis.ResultFacet;
import pcgen.cdom.facet.analysis.SpecialAbilityFacet;
import pcgen.cdom.facet.analysis.SubRaceFacet;
import pcgen.cdom.facet.analysis.TotalWeightFacet;
import pcgen.cdom.facet.analysis.UnarmedDamageFacet;
import pcgen.cdom.facet.analysis.VisionFacet;
import pcgen.cdom.facet.fact.ChronicleEntryFacet;
import pcgen.cdom.facet.fact.FactFacet;
import pcgen.cdom.facet.fact.FollowerFacet;
import pcgen.cdom.facet.fact.PortraitThumbnailRectFacet;
import pcgen.cdom.facet.fact.PreviewSheetFacet;
import pcgen.cdom.facet.fact.RegionFacet;
import pcgen.cdom.facet.fact.SkillFilterFacet;
import pcgen.cdom.facet.fact.SuppressBioFieldFacet;
import pcgen.cdom.facet.fact.WeightFacet;
import pcgen.cdom.facet.input.ProhibitedSchoolFacet;
import pcgen.cdom.facet.input.UserSpecialAbilityFacet;
import pcgen.cdom.facet.model.ArmorProfProviderFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.ShieldProfProviderFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.WeaponProfModelFacet;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.AgeSet;
import pcgen.core.ArmorProf;
import pcgen.core.BioSet;
import pcgen.core.ChronicleEntry;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.FollowerOption;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.NoteItem;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.ShieldProf;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.core.Vision;
import pcgen.core.WeaponProf;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.VisionType;

public class CharacterDisplay
{

	private final CharID id;

	private FactFacet factFacet = FacetLibrary.getFacet(FactFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
	private RaceTypeFacet raceTypeFacet = FacetLibrary.getFacet(RaceTypeFacet.class);
	private RegionFacet regionFacet = FacetLibrary.getFacet(RegionFacet.class);
	private SpellBookFacet spellBookFacet = FacetLibrary.getFacet(SpellBookFacet.class);
	private ChronicleEntryFacet chronicleEntryFacet = FacetLibrary.getFacet(ChronicleEntryFacet.class);
	private AgeSetFacet ageSetFacet = FacetLibrary.getFacet(AgeSetFacet.class);
	private ActiveSpellsFacet activeSpellsFacet = FacetLibrary.getFacet(ActiveSpellsFacet.class);
	private SuppressBioFieldFacet suppressBioFieldFacet = FacetLibrary.getFacet(SuppressBioFieldFacet.class);
	private TemplateFacet templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
	private VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
	private FormulaResolvingFacet formulaResolvingFacet = FacetLibrary.getFacet(FormulaResolvingFacet.class);
	private ArmorClassFacet armorClassFacet = FacetLibrary.getFacet(ArmorClassFacet.class);
	private MovementResultFacet moveResultFacet = FacetLibrary.getFacet(MovementResultFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
	private SubClassFacet subClassFacet = FacetLibrary.getFacet(SubClassFacet.class);
	private FavoredClassFacet favClassFacet = FacetLibrary.getFacet(FavoredClassFacet.class);
	private HasAnyFavoredClassFacet hasAnyFavoredFacet = FacetLibrary.getFacet(HasAnyFavoredClassFacet.class);
	private StartingLanguageFacet startingLangFacet = FacetLibrary.getFacet(StartingLanguageFacet.class);
	private BioSetFacet bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
	private BaseMovementFacet baseMovementFacet = FacetLibrary.getFacet(BaseMovementFacet.class);
	private LegsFacet legsFacet = FacetLibrary.getFacet(LegsFacet.class);
	private StatValueFacet statValueFacet = FacetLibrary.getFacet(StatValueFacet.class);
	private SubstitutionClassFacet substitutionClassFacet = FacetLibrary.getFacet(SubstitutionClassFacet.class);
	private EquippedEquipmentFacet equippedFacet = FacetLibrary.getFacet(EquippedEquipmentFacet.class);
	private ArmorProfProviderFacet armorProfFacet = FacetLibrary.getFacet(ArmorProfProviderFacet.class);
	private SpellListFacet spellListFacet = FacetLibrary.getFacet(SpellListFacet.class);
	private HitPointFacet hitPointFacet = FacetLibrary.getFacet(HitPointFacet.class);
	private FollowerFacet followerFacet = FacetLibrary.getFacet(FollowerFacet.class);
	private LoadFacet loadFacet = FacetLibrary.getFacet(LoadFacet.class);
	private StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);
	private TotalWeightFacet totalWeightFacet = FacetLibrary.getFacet(TotalWeightFacet.class);
	private MultiClassFacet multiClassFacet = FacetLibrary.getFacet(MultiClassFacet.class);
	private LevelTableFacet levelTableFacet = FacetLibrary.getFacet(LevelTableFacet.class);
	private DamageReductionFacet drFacet = FacetLibrary.getFacet(DamageReductionFacet.class);
	private UnarmedDamageFacet unarmedDamageFacet = FacetLibrary.getFacet(UnarmedDamageFacet.class);
	private StatBonusFacet statBonusFacet = FacetLibrary.getFacet(StatBonusFacet.class);
	private NonAbilityFacet nonAbilityFacet = FacetLibrary.getFacet(NonAbilityFacet.class);
	private LevelInfoFacet levelInfoFacet = FacetLibrary.getFacet(LevelInfoFacet.class);
	private KitFacet kitFacet = FacetLibrary.getFacet(KitFacet.class);
	private AutoLanguageGrantedFacet autoLangGrantedFacet = FacetLibrary.getFacet(AutoLanguageGrantedFacet.class);
	private AutoLanguageUnconditionalFacet autoLangUnconditionalFacet =
			FacetLibrary.getFacet(AutoLanguageUnconditionalFacet.class);
	private XPTableFacet xpTableFacet = FacetLibrary.getFacet(XPTableFacet.class);
	private WeightFacet weightFacet = FacetLibrary.getFacet(WeightFacet.class);
	private NoteItemFacet noteItemFacet = FacetLibrary.getFacet(NoteItemFacet.class);
	private SubRaceFacet subRaceFacet = FacetLibrary.getFacet(SubRaceFacet.class);
	private UserSpecialAbilityFacet userSpecialAbilityFacet = FacetLibrary.getFacet(UserSpecialAbilityFacet.class);
	private SkillRankFacet skillRankFacet = FacetLibrary.getFacet(SkillRankFacet.class);
	private ShieldProfProviderFacet shieldProfFacet = FacetLibrary.getFacet(ShieldProfProviderFacet.class);
	private SpecialAbilityFacet specialAbilityFacet = FacetLibrary.getFacet(SpecialAbilityFacet.class);
	private SecondaryWeaponFacet secondaryWeaponFacet = FacetLibrary.getFacet(SecondaryWeaponFacet.class);
	private PrimaryWeaponFacet primaryWeaponFacet = FacetLibrary.getFacet(PrimaryWeaponFacet.class);
	private NonProficiencyPenaltyFacet nonppFacet = FacetLibrary.getFacet(NonProficiencyPenaltyFacet.class);
	private MasterFacet masterFacet = FacetLibrary.getFacet(MasterFacet.class);
	private FollowerOptionFacet foFacet = FacetLibrary.getFacet(FollowerOptionFacet.class);
	private StatCalcFacet statCalcFacet = FacetLibrary.getFacet(StatCalcFacet.class);
	private EquipmentFacet equipmentFacet = FacetLibrary.getFacet(EquipmentFacet.class);
	private EquipSetFacet equipSetFacet = FacetLibrary.getFacet(EquipSetFacet.class);
	private SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
	private DomainFacet domainFacet = FacetLibrary.getFacet(DomainFacet.class);
	private ChallengeRatingFacet crFacet = FacetLibrary.getFacet(ChallengeRatingFacet.class);
	private ProhibitedSchoolFacet prohibitedSchoolFacet = FacetLibrary.getFacet(ProhibitedSchoolFacet.class);
	private RacialSubTypesFacet subTypesFacet = FacetLibrary.getFacet(RacialSubTypesFacet.class);
	//private SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
	private WeaponProfModelFacet weaponProfFacet = FacetLibrary.getFacet(WeaponProfModelFacet.class);
	private LanguageFacet languageFacet = FacetLibrary.getFacet(LanguageFacet.class);
	private InitiativeFacet initiativeFacet = FacetLibrary.getFacet(InitiativeFacet.class);
	private PortraitThumbnailRectFacet portraitThumbnailRectFacet =
			FacetLibrary.getFacet(PortraitThumbnailRectFacet.class);
	private PreviewSheetFacet previewSheetFacet = FacetLibrary.getFacet(PreviewSheetFacet.class);
	private SkillFilterFacet skillFilterFacet = FacetLibrary.getFacet(SkillFilterFacet.class);
	private final ResultFacet resultFacet = FacetLibrary.getFacet(ResultFacet.class);

	public CharacterDisplay(CharID id)
	{
		this.id = id;
	}

	/**
	 * Gets a 'safe' String representation
	 * 
	 * @param key
	 * @return a 'safe' String
	 */
	public String getSafeStringFor(PCStringKey key)
	{
		String s = factFacet.get(id, key);
		if (s == null)
		{
			s = Constants.EMPTY_STRING;
		}
		return s;
	}

	/**
	 * Get the BIO.
	 * 
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(PCStringKey.BIO);
	}

	/**
	 * Get the catchphrase.
	 * 
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(PCStringKey.CATCHPHRASE);
	}

	/**
	 * Returns the character's handedness string.
	 * 
	 * @return A String for handedness.
	 */
	public String getHanded()
	{
		return getSafeStringFor(PCStringKey.HANDED);
	}

	/**
	 * Gets a string of interests for the character.
	 * 
	 * @return A String of interests or an empty string.
	 */
	public String getInterests()
	{
		return getSafeStringFor(PCStringKey.INTERESTS);
	}

	/**
	 * Gets the character's location.
	 * 
	 * @return The character's location.
	 */
	public String getLocation()
	{
		return getSafeStringFor(PCStringKey.LOCATION);
	}

	/**
	 * Get speech tendency.
	 * 
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(PCStringKey.SPEECHTENDENCY);
	}

	/**
	 * Get tab name.
	 * 
	 * @return name on tab
	 */
	public String getTabName()
	{
		return getSafeStringFor(PCStringKey.TABNAME);
	}

	/**
	 * Get trait 1.
	 * 
	 * @return trait 1
	 */
	public String getTrait1()
	{
		return getSafeStringFor(PCStringKey.PERSONALITY1);
	}

	/**
	 * Get trait 2.
	 * 
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(PCStringKey.PERSONALITY2);
	}

	/**
	 * Check  whether the field should be hidden from output. 
	 * @param field The BiographyField to check export suppression rules for.
	 * @return true if the field should not be output, false if it may be.
	 */
	public boolean getSuppressBioField(BiographyField field)
	{
		return suppressBioFieldFacet.getSuppressField(id, field);
	}

	public Collection<Vision> getVisionList()
	{
		return visionFacet.getActiveVision(id);
	}

	/**
	 * Returns a String with the characters Race Type (e.g. Humanoid).
	 * 
	 * @return The character's race type or &quot;None&quot;
	 */
	public String getRaceType()
	{
		RaceType rt = raceTypeFacet.getRaceType(id);
		return (rt == null) ? Constants.NONE : rt.toString();
	}

	public int getTotalLevels()
	{
		return levelFacet.getTotalLevels(id);
	}

	/**
	 * Get the Spell Resistance granted by the given template to a character at a
	 * given level (Class and Hit Dice). This will include the absolute
	 * adjustment made with {@literal SR:, LEVEL:<num>:SR and HD:<num>:SR tags}
	 * 
	 * Note: unlike DR and CR, the value returned here includes the PCs own
	 * Spell Resistance.
	 * 
	 * @param pct
	 * 			The PCTemplate for which the Spell Resistance will be returned.
	 * @param level
	 *            The level to calculate the SR for
	 * @param hitdice
	 *            The Hit dice to calculate the SR for
	 * 
	 * @return the Spell Resistance granted by the given Template at the given level
	 *         and HD
	 */
	public int getTemplateSR(PCTemplate pct, int level, int hitdice)
	{
		String qualifiedKey = pct.getQualifiedKey();
		Formula reduction = pct.getSafe(ObjectKey.SR).getReduction();
		int aSR = formulaResolvingFacet.resolve(id, reduction, qualifiedKey).intValue();

		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				if (lt.get(IntegerKey.LEVEL) <= level)
				{
					Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
					int ltSR = formulaResolvingFacet.resolve(id, ltReduction, qualifiedKey).intValue();
					aSR = Math.max(aSR, ltSR);
				}
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			if (lt.get(IntegerKey.LEVEL) <= level)
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR = formulaResolvingFacet.resolve(id, ltReduction, qualifiedKey).intValue();
				aSR = Math.max(aSR, ltSR);
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			if ((lt.get(IntegerKey.HD_MAX) <= hitdice) && (lt.get(IntegerKey.HD_MIN) >= hitdice))
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR = formulaResolvingFacet.resolve(id, ltReduction, qualifiedKey).intValue();
				aSR = Math.max(aSR, ltSR);
			}
		}

		return aSR;
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on output.
	 * 
	 * @return The list of templates visible on output sheets.
	 */
	@Deprecated
	public List<PCTemplate> getOutputVisibleTemplateList()
	{
		return getVisibleToTemplateList(View.VISIBLE_EXPORT);
	}

	/**
	 * Retrieve a list of the templates applied to this PC that should be
	 * visible on display.
	 * 
	 * @return The list of templates visible in the UI.
	 */
	public List<PCTemplate> getDisplayVisibleTemplateList()
	{
		return getVisibleToTemplateList(View.VISIBLE_DISPLAY);
	}

	private List<PCTemplate> getVisibleToTemplateList(View v)
	{

		Collection<PCTemplate> treeSet = new TreeSet<>(CDOMObjectUtilities::compareKeys);
		templateFacet.getSet(id).stream()
		             .filter(template -> template.getSafe(ObjectKey.VISIBILITY).isVisibleTo(v))
					 .forEach(treeSet::add);
		return new ArrayList<>(treeSet);
	}

	/**
	 * Get the Character's Region
	 * 
	 * @return character region
	 */
	public String getRegionString()
	{
		return regionFacet.getRegionString(id);
	}

	/**
	 * Get the Character's Region
	 * 
	 * @return character region
	 */
	public Optional<Region> getRegion()
	{
		return regionFacet.getRegion(id);
	}

	/**
	 * Get the Character's SubRegion
	 * 
	 * @return character sub region
	 */
	public Optional<String> getSubRegion()
	{
		return regionFacet.getSubRegion(id);
	}

	public int getSpellBookCount()
	{
		return spellBookFacet.getCount(id);
	}

	/**
	 * Get spell books.
	 * 
	 * @return spellBooks
	 */
	public List<String> getSpellBookNames()
	{
		return new ArrayList<>(spellBookFacet.getBookNames(id));
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

	@Deprecated
	public int calcACOfType(String type)
	{
		return armorClassFacet.calcACOfType(id, type);
	}

	public int getBaseMovement(MovementType moveType, Load load)
	{
		return moveResultFacet.getBaseMovement(id, moveType, load);
	}

	public boolean hasMovement(MovementType moveType)
	{
		return moveResultFacet.hasMovement(id, moveType);
	}

	public List<NamedValue> getMovementValues()
	{
		return moveResultFacet.getMovementValues(id);
	}

	public int getNumberOfMovements()
	{
		return moveResultFacet.countMovementTypes(id);
	}

	public Race getRace()
	{
		return raceFacet.get(id);
	}

	public String getPreviewSheet()
	{
		return previewSheetFacet.get(id);
	}

	public SkillFilter getSkillFilter()
	{
		return skillFilterFacet.get(id);
	}

	/**
	 * Gets the Set of PCClass objects for this Character.
	 * @return a set of PCClass objects
	 */
	public Set<PCClass> getClassSet()
	{
		return classFacet.getSet(id);
	}

	public String getSubClassName(PCClass cl)
	{
		return subClassFacet.get(id, cl);
	}

	public final int getLevel(PCClass pcc)
	{
		return classFacet.getLevel(id, pcc);
	}

	@Deprecated
	public SortedSet<PCClass> getFavoredClasses()
	{
		SortedSet<PCClass> favored = new TreeSet<>(CDOMObjectUtilities::compareKeys);
		favored.addAll(favClassFacet.getSet(id));
		return favored;
	}

	@Deprecated
	public boolean hasAnyFavoredClass()
	{
		return hasAnyFavoredFacet.contains(id, Boolean.TRUE);
	}

	public Collection<Follower> getFollowerList()
	{
		return followerFacet.getSet(id);
	}

	/**
	 * Get a sorted list of the languages that this character knows.
	 * @return a sorted list of language objects
	 */
	@Deprecated
	public Set<Language> getSortedLanguageSet()
	{
		return new TreeSet<>(languageFacet.getSet(id));
	}

	@Deprecated
	public int processOldInitiativeMod()
	{
		return initiativeFacet.getInitiative(id);
	}

	public SortedSet<WeaponProf> getSortedWeaponProfs()
	{
		return Collections.unmodifiableSortedSet(new TreeSet<>(weaponProfFacet.getSet(id)));
	}

	public Collection<RaceSubType> getRacialSubTypes()
	{
		return subTypesFacet.getRacialSubTypes(id);
	}

	public Collection<? extends SpellProhibitor> getProhibitedSchools(Object source)
	{
		return prohibitedSchoolFacet.getSet(id, source);
	}

	public String getPortraitPath()
	{
		return getSafeStringFor(PCStringKey.PORTRAIT_PATH);
	}

	public Rectangle getPortraitThumbnailRect()
	{
		Rectangle rect = portraitThumbnailRectFacet.get(id);
		return (rect == null) ? null : (Rectangle) rect.clone();
	}

	public String getName()
	{
		return getSafeStringFor(PCStringKey.NAME);
	}

	public String getFileName()
	{
		return getSafeStringFor(PCStringKey.FILE_NAME);
	}

	public String getPlayersName()
	{
		return getSafeStringFor(PCStringKey.PLAYERSNAME);
	}

	public Integer calcCR()
	{
		return crFacet.getCR(id);
	}

	public Integer calcBaseCR()
	{
		return crFacet.calcRaceCR(id);
	}

	public float getBaseHD()
	{
		return crFacet.getBaseHD(id);
	}

	public int getXPAward()
	{
		return crFacet.getXPAward(id);
	}

	public int getRacialHDSize()
	{
		int hdSize = 0;
		LevelCommandFactory lcf = getRace().get(ObjectKey.MONSTER_CLASS);
		if (lcf != null)
		{
			hdSize = getLevelHitDie(lcf.getPCClass(), 1).getDie();
		}
		return hdSize;
	}

	public Set<Domain> getSortedDomainSet()
	{
		SortedSet<Domain> domains = new TreeSet<>(CDOMObjectUtilities::compareKeys);
		domains.addAll(domainFacet.getSet(id));
		return domains;
	}

	/**
	 * Retrieve those skills in the character's skill list that match the
	 * supplied visibility level.
	 * 
	 * @param v What level of visibility skills are desired.
	 * 
	 * @return A list of the character's skills matching the visibility
	 *         criteria.
	 */
	public List<Skill> getPartialSkillList(View v)
	{
		// Now select the required set of skills, based on their visibility.
		return skillFacet.getSet(id)
		                 .stream()
		                 .filter(po -> po.getSafe(ObjectKey.VISIBILITY).isVisibleTo(v))
		                 .collect(Collectors.toList());
	}

	/**
	 * Alignment of this PC.
	 * 
	 * @return alignment
	 */
	public PCAlignment getPCAlignment()
	{
		return AlignmentCompat.getCurrentAlignment(getCharID());
	}

	public Object getGlobal(String varName)
	{
		return resultFacet.getGlobalVariable(id, varName);
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
		return new ArrayList<>(getClassSet());
	}

	/**
	 * Get the current equipment set name.
	 * 
	 * @return equipment set name
	 */
	public String getCurrentEquipSetName()
	{
		return getSafeStringFor(PCStringKey.CURRENT_EQUIP_SET_NAME);
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
	 * Get equipment set.
	 * 
	 * @return equipment set
	 */
	public Set<Equipment> getEquipmentSet()
	{
		return equipmentFacet.getSet(id);
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
	public Map<FollowerOption, CDOMObject> getAvailableFollowers(final String aType, Comparator<FollowerOption> comp)
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
	 * @return nonProficiencyPenalty. Searches templates first.
	 */
	public int getNonProficiencyPenalty()
	{
		return nonppFacet.getPenalty(id);
	}

	/**
	 * Selector gets the character's primary weapons.
	 * 
	 * @return primary weapons
	 */
	public Collection<Equipment> getPrimaryWeapons()
	{
		return primaryWeaponFacet.getSet(id);
	}

	public boolean hasPrimaryWeapons()
	{
		return !primaryWeaponFacet.isEmpty(id);
	}

	public boolean hasSecondaryWeapons()
	{
		return !secondaryWeaponFacet.isEmpty(id);
	}

	/**
	 * Get the character's secondary weapons.
	 * 
	 * @return secondary weapons
	 */
	public Collection<Equipment> getSecondaryWeapons()
	{
		return secondaryWeaponFacet.getSet(id);
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

	@Deprecated
	public List<SpecialAbility> getResolvedSpecialAbilities(CDOMObject cdo)
	{
		return specialAbilityFacet.getResolved(id, cdo);
	}

	@Deprecated
	public List<SpecialAbility> getResolvedUserSpecialAbilities(CDOMObject cdo)
	{
		return userSpecialAbilityFacet.getResolved(id, cdo);
	}

	/**
	 * Get the name of the spellbook to auto add new known spells to.
	 * 
	 * @return spellbook name
	 */
	public String getSpellBookNameToAutoAddKnown()
	{
		return getSafeStringFor(PCStringKey.SPELLBOOK_AUTO_ADD_KNOWN);
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
	 * Accessor, Gets the sub-race of the character.
	 * @return character subrace.
	 */
	@Deprecated
	public String getSubRace()
	{
		return subRaceFacet.getSubRace(id);
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
	 * Gets the character's weight in pounds.
	 * 
	 * @return The character's weight.
	 */
	public int getWeight()
	{
		return weightFacet.getWeight(id);
	}

	public String getXPTableName()
	{
		return xpTableFacet.get(id).getName();
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

	public Set<Language> getAutoLanguages()
	{
		Set<Language> languages = new HashSet<>();
		languages.addAll(autoLangGrantedFacet.getSet(id));
		languages.addAll(autoLangUnconditionalFacet.getSet(id));
		return languages;
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

	public Collection<Kit> getKitInfo()
	{
		return kitFacet.getSet(id);
	}

	public Collection<PCLevelInfo> getLevelInfo()
	{
		return levelInfoFacet.getSet(id);
	}

	public PCLevelInfo getLevelInfo(int index)
	{
		return levelInfoFacet.get(id, index);
	}

	public int getLevelInfoSize()
	{
		return levelInfoFacet.getCount(id);
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

	/**
	 * Checks if the stat is a non ability.
	 * 
	 * @return true, if is non ability
	 */
	public boolean isNonAbility(PCStat stat)
	{
		return nonAbilityFacet.isNonAbility(id, stat);
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

		return secondaryWeaponFacet.getSet(id)
		                           .stream()
		                           .anyMatch(eqB -> itemEquals(eq, eqB));
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
		return statBonusFacet.getStatBonusTo(id, aType, aName);
	}

	public String getUDamForRace()
	{
		return unarmedDamageFacet.getUDamForRace(id);
	}

	public Set<List<String>> getUnarmedDamage()
	{
		return unarmedDamageFacet.getSet(id);
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

	/*
	 * returns true if Equipment is in the primary weapon list
	 */
	public boolean isPrimaryWeapon(final Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}

		return primaryWeaponFacet.getSet(id)
		                         .stream()
		                         .anyMatch(eqB -> itemEquals(eq, eqB));
	}

	public int minXPForNextECL()
	{
		return levelTableFacet.minXPForLevel(levelFacet.getECL(id) + 1, id);
	}

	public double multiclassXPMultiplier()
	{
		return multiClassFacet.getMultiClassXPMultiplier(id);
	}

	public int totalHitDice()
	{
		return levelFacet.getMonsterLevelCount(id);
	}

	public int totalNonMonsterLevels()
	{
		return levelFacet.getNonMonsterLevelCount(id);
	}

	public Float totalWeight()
	{
		return totalWeightFacet.getTotalWeight(id);
	}

	public boolean hasKit(Kit kit)
	{
		return kitFacet.contains(id, kit);
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

	public boolean hasDomain(Domain domain)
	{
		return domainFacet.contains(id, domain);
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

	public int getStatCount()
	{
		return statFacet.getCount(id);
	}

	public PCClassLevel getActiveClassLevel(PCClass pcc, int lvl)
	{
		return classFacet.getClassLevel(id, pcc, lvl);
	}

	public int getClassCount()
	{
		return classFacet.getCount(id);
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

	public double getMovementOfType(MovementType moveType)
	{
		return moveResultFacet.getMovementOfType(id, moveType);
	}

	public boolean hasEquipSet()
	{
		return !equipSetFacet.isEmpty(id);
	}

	public Collection<? extends CharacterSpell> getCharacterSpells(CDOMObject cdo)
	{
		return activeSpellsFacet.getSet(id, cdo);
	}

	public AgeSet getAgeSet()
	{
		return ageSetFacet.get(id);
	}

	/**
	 * Retrieve the set of the character's chronicle entries.
	 * @return The character's chronicle entries.
	 */
	public Collection<ChronicleEntry> getChronicleEntries()
	{
		return chronicleEntryFacet.getSet(id);
	}

	public HitDie getLevelHitDie(PCClass pcClass, final int classLevel)
	{
		return hitPointFacet.getLevelHitDie(id, pcClass, classLevel);
	}

	public List<? extends CDOMList<Spell>> getSpellLists(CDOMObject cdo)
	{
		return spellListFacet.getSet(id, cdo);
	}

	/**
	 * @return display name
	 */
	public String getDisplayName()
	{
		final String custom = getSafeStringFor(PCStringKey.TABNAME);

		if (custom != null && !custom.isEmpty())
		{
			return custom;
		}

		return getName();
	}

	public String getDisplayClassName(PCClass pcClass)
	{
		if (pcClass != null)
		{
			String subClassKey = getSubClassName(pcClass);
			if ((subClassKey != null) && (!subClassKey.isEmpty()) && !subClassKey.equals(Constants.NONE))
			{
				SubClass sc = pcClass.getSubClassKeyed(subClassKey);
				if (sc != null)
				{
					return sc.getDisplayName();
				}
			}
		}

		return pcClass.getDisplayName();
	}

	/**
	 * Get the value of the weight token in format WEIGHT.X
	 * @param type Encumbrance type 
	 * @return The value of the weight token.
	 */
	public double getLoadToken(String type)
	{
		Float mult = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadMultiplier(type.toUpperCase());
		if (mult != null)
		{
			return getMaxLoad(mult).intValue();
		}
		return 0.0;
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
	 * Get the character's "equipped" equipment.
	 * @return a set of the "equipped" equipment
	 */
	public Set<Equipment> getEquippedEquipmentSet()
	{
		return equippedFacet.getSet(id);
	}

	/**
	 * Returns a region (including subregion) string for the character.
	 * 
	 * <p> Build on-the-fly so removing templates won't mess up region
	 * 
	 * @return character region
	 */
	public String getFullRegion()
	{
		return regionFacet.getFullRegion(id);
	}

	public Vision getVision(VisionType type)
	{
		return visionFacet.getActiveVision(id, type);
	}

	public String getSubstitutionClassName(PCClassLevel lvl)
	{
		return substitutionClassFacet.get(id, lvl);
	}

	public int getStat(PCStat stat)
	{
		return statValueFacet.get(id, stat).intValue();
	}

	public boolean containsRacialSubType(RaceSubType st)
	{
		return subTypesFacet.contains(id, st);
	}

	/**
	 * Determine the number of legs the character has.
	 * 
	 * @return The number of legs.
	 */
	public int getPreFormulaLegs()
	{
		return legsFacet.getLegs(id);
	}

	public Integer getDR(String key)
	{
		return drFacet.getDR(id, key);
	}

	public boolean hasMovement()
	{
		return !baseMovementFacet.isEmpty(id);
	}

	public Collection<WeaponProf> getWeaponProfSet()
	{
		return weaponProfFacet.getSet(id);
	}

	public List<? extends SpecialAbility> getUserSpecialAbilityList(CDOMObject source)
	{
		return userSpecialAbilityFacet.getSet(id, source);
	}

	public Integer getHP(PCClassLevel pcl)
	{
		return hitPointFacet.get(id, pcl);
	}

	public BioSet getBioSet()
	{
		return bioSetFacet.get(id);
	}

	public int getAgeSetIndex()
	{
		return ageSetFacet.getAgeSetIndex(id);
	}

	public boolean hasFollowers()
	{
		return !followerFacet.isEmpty(id);
	}

	public boolean hasEquipment()
	{
		return !equipmentFacet.isEmpty(id);
	}

	public boolean hasLanguage(Language lang)
	{
		return languageFacet.contains(id, lang);
	}

	public int getLanguageCount()
	{
		return languageFacet.getCount(id);
	}

	public boolean hasTemplates()
	{
		return !templateFacet.isEmpty(id);
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

	public int getTotalStatFor(PCStat stat)
	{
		return statCalcFacet.getTotalStatFor(id, stat);
	}

	public int getStatModFor(PCStat stat)
	{
		return statCalcFacet.getStatModFor(id, stat);
	}

	public int getBaseStatFor(PCStat stat)
	{
		return statCalcFacet.getBaseStatFor(id, stat);
	}

	public int getFollowerCount()
	{
		return followerFacet.getCount(id);
	}

	public int getRacialSubTypeCount()
	{
		return subTypesFacet.getCount(id);
	}

	public int getVisionCount()
	{
		return visionFacet.getVisionCount(id);
	}

	public int getBaseMovement()
	{
		return baseMovementFacet.getSet(id).iterator().next().getMovement();
	}

	public double movementOfType(MovementType moveType)
	{
		return moveResultFacet.movementOfType(id, moveType);
	}

	public int getNotesCount()
	{
		return noteItemFacet.getCount(id);
	}

	/**
	 * Gets a list of notes associated with the character.
	 * 
	 * @return A list of <tt>NoteItem</tt> objects.
	 */
	public Collection<NoteItem> getNotesList()
	{
		return noteItemFacet.getSet(id);
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
	 * returns ranks taken specifically in skill
	 * 
	 * @return ranks taken in skill
	 */
	public Float getRank(Skill sk)
	{
		return skillRankFacet.getRank(id, sk);
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

	public boolean isProficientWithArmor(final Equipment eq)
	{
		return armorProfFacet.isProficientWithArmor(id, eq);
	}

	private static boolean itemEquals(Equipment eqA, Equipment eqB)
	{
		return eqA.getName().equalsIgnoreCase(eqB.getName())
				&& (eqA.getLocation() == eqB.getLocation());
	}

	/**
	 * WARNING: Use this method SPARINGLY... and only for transition to the
	 * facet model. It is NOT an excuse to throw around a CharacterDisplay
	 * object when unnecessary
	 * 
	 * @return The id of the character as used by the facets.
	 */
	public CharID getCharID()
	{
		return id;
	}
}
