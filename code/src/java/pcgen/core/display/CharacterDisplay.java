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
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.SpellBookFacet;
import pcgen.cdom.facet.SubClassFacet;
import pcgen.cdom.facet.analysis.ArmorClassFacet;
import pcgen.cdom.facet.analysis.ChallengeRatingFacet;
import pcgen.cdom.facet.analysis.FaceFacet;
import pcgen.cdom.facet.analysis.FavoredClassFacet;
import pcgen.cdom.facet.analysis.HasAnyFavoredClassFacet;
import pcgen.cdom.facet.analysis.InitiativeFacet;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.MovementResultFacet;
import pcgen.cdom.facet.analysis.RaceTypeFacet;
import pcgen.cdom.facet.analysis.RacialSubTypesFacet;
import pcgen.cdom.facet.analysis.VisionFacet;
import pcgen.cdom.facet.fact.AgeFacet;
import pcgen.cdom.facet.fact.CharacterTypeFacet;
import pcgen.cdom.facet.fact.FactFacet;
import pcgen.cdom.facet.fact.FollowerFacet;
import pcgen.cdom.facet.fact.GenderFacet;
import pcgen.cdom.facet.fact.HandedFacet;
import pcgen.cdom.facet.fact.MoneyFacet;
import pcgen.cdom.facet.fact.PortraitThumbnailRectFacet;
import pcgen.cdom.facet.fact.RegionFacet;
import pcgen.cdom.facet.fact.SuppressBioFieldFacet;
import pcgen.cdom.facet.input.ProhibitedSchoolFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.WeaponProfFacet;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SpellProhibitor;
import pcgen.core.Vision;
import pcgen.core.WeaponProf;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;

public class CharacterDisplay
{

	private final CharID id;

	private FactFacet factFacet = FacetLibrary.getFacet(FactFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
	private RaceTypeFacet raceTypeFacet = FacetLibrary.getFacet(RaceTypeFacet.class);
	private RegionFacet regionFacet = FacetLibrary.getFacet(RegionFacet.class);
	private SpellBookFacet spellBookFacet = FacetLibrary.getFacet(SpellBookFacet.class);
	private SuppressBioFieldFacet suppressBioFieldFacet = FacetLibrary.getFacet(SuppressBioFieldFacet.class);
	private TemplateFacet templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
	private VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
	private FormulaResolvingFacet formulaResolvingFacet = FacetLibrary.getFacet(FormulaResolvingFacet.class);
	private ArmorClassFacet armorClassFacet = FacetLibrary.getFacet(ArmorClassFacet.class);
	private AgeFacet ageFacet = FacetLibrary.getFacet(AgeFacet.class);
	private MovementResultFacet moveResultFacet = FacetLibrary.getFacet(MovementResultFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private CharacterTypeFacet characterTypeFacet = FacetLibrary.getFacet(CharacterTypeFacet.class);
	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
	private SubClassFacet subClassFacet = FacetLibrary.getFacet(SubClassFacet.class);
	private FavoredClassFacet favClassFacet = FacetLibrary.getFacet(FavoredClassFacet.class);
	private HasAnyFavoredClassFacet hasAnyFavoredFacet = FacetLibrary.getFacet(HasAnyFavoredClassFacet.class);
	private FollowerFacet followerFacet = FacetLibrary.getFacet(FollowerFacet.class);
	private GenderFacet genderFacet = FacetLibrary.getFacet(GenderFacet.class);
	private MoneyFacet moneyFacet = FacetLibrary.getFacet(MoneyFacet.class);
	private ChallengeRatingFacet crFacet = FacetLibrary.getFacet(ChallengeRatingFacet.class);
	private ProhibitedSchoolFacet prohibitedSchoolFacet = FacetLibrary.getFacet(ProhibitedSchoolFacet.class);
	private RacialSubTypesFacet subTypesFacet = FacetLibrary.getFacet(RacialSubTypesFacet.class);
	private SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
	private WeaponProfFacet weaponProfFacet = FacetLibrary.getFacet(WeaponProfFacet.class);
	private FaceFacet faceFacet = FacetLibrary.getFacet(FaceFacet.class);
	private LanguageFacet languageFacet = FacetLibrary.getFacet(LanguageFacet.class);
	private InitiativeFacet initiativeFacet = FacetLibrary.getFacet(InitiativeFacet.class);
	private HandedFacet handedFacet = FacetLibrary.getFacet(HandedFacet.class);
	private PortraitThumbnailRectFacet portraitThumbnailRectFacet = FacetLibrary
			.getFacet(PortraitThumbnailRectFacet.class);

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
	 * Get the BIO.
	 * 
	 * @return the BIO
	 */
	public String getBio()
	{
		return getSafeStringFor(StringKey.BIO);
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
	 * Get the catchphrase.
	 * 
	 * @return catchphrase
	 */
	public String getCatchPhrase()
	{
		return getSafeStringFor(StringKey.CATCH_PHRASE);
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
	 * Get the characters eye colour.
	 * 
	 * @return the colour of their eyes
	 */
	public String getEyeColor()
	{
		return getSafeStringFor(StringKey.EYE_COLOR);
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
	 * Gets the character's hair style.
	 * 
	 * @return The character's hair style.
	 */
	public String getHairStyle()
	{
		return getSafeStringFor(StringKey.HAIR_STYLE);
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
	 * Gets a string of interests for the character.
	 * 
	 * @return A String of interests or an empty string.
	 */
	public String getInterests()
	{
		return getSafeStringFor(StringKey.INTERESTS);
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
	 * Gets the phobia string for the character.
	 * 
	 * @return A phobia string.
	 */
	public String getPhobias()
	{
		return getSafeStringFor(StringKey.PHOBIAS);
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
	 * Get speech tendency.
	 * 
	 * @return speech tendency
	 */
	public String getSpeechTendency()
	{
		return getSafeStringFor(StringKey.SPEECH_TENDENCY);
	}

	/**
	 * Get tab name.
	 * 
	 * @return name on tab
	 */
	public String getTabName()
	{
		return getSafeStringFor(StringKey.TAB_NAME);
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
	 * Get trait 2.
	 * 
	 * @return trait 2
	 */
	public String getTrait2()
	{
		return getSafeStringFor(StringKey.TRAIT2);
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
		return rt == null ? Constants.NONE : rt.toString();
	}

	public int getTotalLevels()
	{
		return levelFacet.getTotalLevels(id);
	}

	/**
	 * Get the Spell Resistance granted by the given template to a character at a
	 * given level (Class and Hit Dice). This will include the absolute
	 * adjustment made with SR:, LEVEL:<num>:SR and HD:<num>:SR tags
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
		int aSR =
				formulaResolvingFacet.resolve(id, reduction, qualifiedKey)
					.intValue();

		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				if (lt.get(IntegerKey.LEVEL) <= level)
				{
					Formula ltReduction =
							lt.getSafe(ObjectKey.SR).getReduction();
					int ltSR =
							formulaResolvingFacet.resolve(id, ltReduction,
								qualifiedKey).intValue();
					aSR = Math.max(aSR, ltSR);
				}
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			if (lt.get(IntegerKey.LEVEL) <= level)
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR =
						formulaResolvingFacet.resolve(id, ltReduction,
							qualifiedKey).intValue();
				aSR = Math.max(aSR, ltSR);
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			if (lt.get(IntegerKey.HD_MAX) <= hitdice
				&& lt.get(IntegerKey.HD_MIN) >= hitdice)
			{
				Formula ltReduction = lt.getSafe(ObjectKey.SR).getReduction();
				int ltSR =
						formulaResolvingFacet.resolve(id, ltReduction,
							qualifiedKey).intValue();
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
	public List<PCTemplate> getOutputVisibleTemplateList()
	{
		List<PCTemplate> tl = new ArrayList<PCTemplate>();

		TreeSet<PCTemplate> treeSet = new TreeSet<PCTemplate>(CDOMObjectUtilities.CDOM_SORTER);
		for (PCTemplate template : templateFacet.getSet(id))
		{
			if ((template.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
				|| (template.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY))
			{
				treeSet.add(template);
			}
		}
		tl.addAll(treeSet);
		return tl;
	}

	/**
	 * Get the Character's Region
	 * 
	 * @return character region
	 */
	public String getRegionString()
	{
		return regionFacet.getRegion(id);
	}

	/**
	 * Get the Character's SubRegion
	 * 
	 * @return character sub region
	 */
	public String getSubRegion()
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
		return new ArrayList<String>(spellBookFacet.getBookNames(id));
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

	public int calcACOfType(String type)
	{
		return armorClassFacet.calcACOfType(id, type);
	}

	public int getAge()
	{
		return ageFacet.getAge(id);
	}

	public String getBirthplace()
	{
		return getSafeStringFor(StringKey.BIRTHPLACE);
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

	public int getNumberOfMovements()
	{
		return moveResultFacet.countMovementTypes(id);
	}

	public Race getRace()
	{
		return raceFacet.get(id);
	}

	public String getCharacterType()
	{
		return characterTypeFacet.getCharacterType(id);
	}

	/**
	 * Gets the Set of PCClass objects for this Character.
	 * @return a set of PCClass objects
	 */
	public Set<PCClass> getClassSet()
	{
		return classFacet.getClassSet(id);
	}

	public String getSubClassName(PCClass cl)
	{
		return subClassFacet.getSource(id, cl);
	}

	public final int getLevel(PCClass pcc)
	{
		return classFacet.getLevel(id, pcc);
	}

	public SortedSet<PCClass> getFavoredClasses()
	{
		SortedSet<PCClass> favored = new TreeSet<PCClass>(CDOMObjectUtilities.CDOM_SORTER);
		favored.addAll(favClassFacet.getSet(id));
		return favored;
	}

	public boolean hasAnyFavoredClass()
	{
		return hasAnyFavoredFacet.contains(id, Boolean.TRUE);
	}

	public Collection<Follower> getFollowerList()
	{
		return followerFacet.getSet(id);
	}

	public Gender getGenderObject()
	{
		return genderFacet.getGender(id);
	}

	public BigDecimal getGold()
	{
		return moneyFacet.getGold(id);
	}

	/**
	 * Get a sorted list of the languages that this character knows.
	 * @return a sorted list of language objects
	 */
	public Set<Language> getSortedLanguageSet()
	{
		return new TreeSet<Language>(languageFacet.getSet(id));
	}

	public int initiativeMod()
	{
		return initiativeFacet.getInitiative(id);
	}

	public Handed getHandedObject()
	{
		return handedFacet.getHanded(id);
	}

	public Point2D.Double getFace()
	{
		return faceFacet.getFace(id);
	}

	public SortedSet<WeaponProf> getSortedWeaponProfs()
	{
		return Collections.unmodifiableSortedSet(new TreeSet<WeaponProf>(weaponProfFacet.getProfs(id)));
	}

	public String getSize()
	{
		return sizeFacet.getSizeAbb(id);
	}

	public String getResidence()
	{
		return getSafeStringFor(StringKey.RESIDENCE);
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
		return getSafeStringFor(StringKey.PORTRAIT_PATH);
	}

	public Rectangle getPortraitThumbnailRect()
	{
		Rectangle rect = portraitThumbnailRectFacet.get(id);
		return rect == null ? null : (Rectangle) rect.clone();
	}

	public String getName()
	{
		return getSafeStringFor(StringKey.NAME);
	}

	public String getFileName()
	{
		return getSafeStringFor(StringKey.FILE_NAME);
	}

	public String getPlayersName()
	{
		return getSafeStringFor(StringKey.PLAYERS_NAME);
	}

	public float calcCR()
	{
		return crFacet.getCR(id);
	}
}
