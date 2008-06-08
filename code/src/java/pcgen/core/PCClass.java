/*
 * PCClass.java
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
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.content.TransitionChoice;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.AttackCycle;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability.Nature;
import pcgen.core.QualifiedObject.LevelAwareQualifiedObject;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MapKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.ReferenceContext;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.AttackType;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;
import pcgen.util.enumeration.VisionType;

/**
 * <code>PCClass</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 */
public class PCClass extends PObject
{

	public static final CDOMReference<ClassSkillList> MONSTER_SKILL_LIST = new CDOMDirectSingleRef<ClassSkillList>(
			new ClassSkillList());

	public static final CDOMReference<DomainList> ALLOWED_DOMAINS = CDOMDirectSingleRef
			.getRef(new DomainList());

	/*
	 * FINALALLCLASSLEVELS Since this applies to a ClassLevel line
	 */
	private List<LevelProperty<Movement>> movementList = null;

	/*
	 * FUTURETYPESAFETY This is an interesting case of Type Safety, that may not be
	 * possible, as this is a big magical in what it could be - School,
	 * Subschool, and other things. Need lots of investigation as to what the
	 * appropriate Type for this is or whether it's stuck as a String.
	 */
	/*
	 * FINALALLCLASSLEVELS This is the list of specialties that were taken as part of
	 * leveling up at a certain point. Therefore this gets moved to PCClassLevel =
	 * byproduct of addLevel
	 */
	private String specialty = null;

	/*
	 * STRINGREFACTOR This is currently taking in a delimited String and should
	 * be taking in a List or somesuch.
	 * 
	 * The challenge here (which requires a new CHOICE class - is that this may
	 * not be simply a list, but could include a CHOICE as well... need to figure
	 * out how to do that in a type safe way :/
	 */
	/*
	 * TYPESAFETY This is throwing around template names as Strings. :(
	 */
	/*
	 * ALLCLASSLEVELS The templates [based on their LevelProperty] (not the raw
	 * Strings) need to be stored in EACH individual PCClassLevel.
	 */
	private List<LevelProperty<String>> templates = null;

	/*
	 * FINALALLCLASSLEVELS The SR List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<String>> SR = null;

	/*
	 * FINALALLCLASSLEVELS The encumberedLoadMove List is level dependent -
	 * heck, it's in a LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Load>> encumberedLoadMove = null;

	/*
	 * FINALALLCLASSLEVELS The encumberedArmorMove List is level dependent -
	 * heck, it's in a LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Load>> encumberedArmorMove = null;

	/*
	 * FINALALLCLASSLEVELS This is pretty obvious, as these are already in a
	 * LevelProperty... these go into the PCClassLevel
	 */
	private List<LevelProperty<Equipment>> naturalWeapons = null;

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel (inheritAttributesFrom - although that 
	 * could really be cleaned up and a better method found)
	 */
	private List<SubClass> subClassList = null;

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel (inheritAttributesFrom -
	 * although that could really be cleaned up and a better method found)
	 */
	private List<SubstitutionClass> substitutionClassList = null;

	/*
	 * ALLCLASSLEVELS castForLevelMap is part of PCClassLevel - or nothing at
	 * all since this seems to be a form of cache? - DELETEVARIABLE
	 */
	private HashMap<Integer, Integer> castForLevelMap = null;

	/*
	 * ALLCLASSLEVELS hitPointMap is part of PCClassLevel
	 */
	private HashMap<Integer, Integer> hitPointMap = null; // TODO - This
	// should be in
	// PCLevelInfo

	/*
	 * ALLCLASSLEVELS The virtual feats [based on their level or LevelProperty]
	 * (not the raw Strings) need to be stored in EACH individual PCClassLevel.
	 * (This object is level dependent based on the input Tag and the use of a
	 * Map)
	 */
	/*
	 * REFACTOR Should decide whether this should be LevelProperty<Ability> and
	 * allow multiple instances of the same level within the List... that is how
	 * other variables are working - not this one because of the conversion from
	 * vFeatMap (which didn't use LevelProperty)
	 */
	private List<LevelProperty<List<Ability>>> vFeatList = null;

	/*
	 * ALLCLASSLEVELS Hard to tell here yet, since this is part of the Ability
	 * project, but this will need similar support to vFeatList??
	 */
	private DoubleKeyMap<AbilityCategory, Integer, List<Ability>> vAbilityMap =
			null;

	/*
	 * ALLCLASSLEVELS skillPool is part each PCClassLevel and what that level
	 * grants to each PlayerCharacter (added by the PCClassLevel Factory, not
	 * by a tag)
	 */
	private int skillPool = 0;

	/*
	 * FUTURETYPESAFETY Dependent upon classSkillChoices being type safe, which
	 * in turn is dependent on the Chooser system being type safe :/
	 */
	/*
	 * FINALPCCLASSLEVELONLY
	 * FINALALLCLASSLEVELS classSkillList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<ClassSkillList> classSkillList = null;

	/*
	 * FUTURETYPESAFETY Dependent upon classSpellChoices being type safe, which
	 * in turn is dependent on the Chooser system being type safe :/
	 */
	/*
	 * FINALPCCLASSLEVELONLY
	 * FINALALLCLASSLEVELS classSpellList is part of PCClassLevel (they are the
	 * selections the character takes at a given level) - triggered by
	 * addLevel
	 */
	private List<CDOMListObject<Spell>> classSpellList = null;

	/*
	 * TYPESAFETY This should be working with Skill objects, not Strings
	 */
	/*
	 * ALLCLASSLEVELS This is a list of the additional Class Skills that have
	 * been added by a LevelAbilityClassSkill (such as the additional class
	 * skills granted to the Expert class). Thease must be selected for the
	 * given class, and are thus NOT part of PCClass but PCClassLevel.
	 * 
	 * While looking at this, this needs to be tested, there was a TO-DO in the
	 * code indicating support for this may not be working properly.
	 */
	private List<String> skillList = null; // TODO - Not sure this support is
	// really working properly

	/*
	 * FINALALLCLASSLEVELS The Vision List is level dependent - heck, it's in a
	 * LevelProperty, so that should be pretty obvious :)
	 */
	private List<LevelProperty<Vision>> visionList = null;

	/*
	 * FUTURETYPESAFETY This should not be a String, but a member of a Typesafe
	 * Enumeration of Classes... unfortunately, that really requires a two-pass
	 * design for the LST files... because the EX Class may not exist at the 
	 * time this tag is hit.
	 */
	/*
	 * FINALALLCLASSLEVELS Because this indicates what a Class becomes when the
	 * prerequisites are no longer met, this must be passed into each and every
	 * PCClassLevel (though it can be given in its pure form).
	 */
	/*
	 * REFACTOR Consideration of HOW to actually perform this given the
	 * characteristics of PCClass vs. PCClassLevel are rather interesting...
	 * exactly how do you impose an ExClass on something in a way that is
	 * reasonably safe - given object structure - as well as being reasonably
	 * efficient?
	 */
	private String exClass = Constants.EMPTY_STRING;

	/*
	 * STRINGREFACTOR This is currently taking in a complex formula-ish string
	 * and needs to be put back into the Tag!
	 */
	/*
	 * UNKNOWNDESTINATION Not sure where to put this today. In fact, this is
	 * NEVER called from an instance of PCClass that is part of a
	 * PlayerCharacter, it is actually a REFERENCE item based on calculating the
	 * cost (GP cost?) of a piece of equipment. Therefore, there seems to be no
	 * reason at ALL to put this into PCClassLevel, but rather keep it as
	 * something entirely in PCClass. (Perhaps this really becomes a "static"
	 * method in PCClass: The Factory? This can't literally be static, since it
	 * is directly part of the PCClass instance, but it's also one of the few
	 * things that should be exposed for use in PCClass other than the
	 * PCClassLevel factory method.
	 */
	private String itemCreationMultiplier = Constants.EMPTY_STRING;

	/*
	 * STRINGREFACTOR This is currently processed outside of this set (format is
	 * of the form EXCHANGELEVEL:Ex Paladin|11|10|1). Should be processed in the
	 * tag and passed in here as an object.
	 */
	/*
	 * TYPESAFETY This is actually passing around a Class, and thus can be made
	 * type safe to the list of classes.
	 */
	/*
	 * UNKNOWNDESTINATION Don't know where to put this yet... this is a
	 * COMPLICATED function that allows the exchange of levels (presumably on a
	 * one-time basis). Thus, this needs to be tagged as performed, and thus
	 * unrepeatable.
	 */
	private String levelExchange = Constants.EMPTY_STRING;

	/*
	 * UNKNOWNDESTINATION This is (yet again) a bit complicated due to the fact
	 * that this is a prerequisite test. First, this is LEVELONEONLY in the
	 * sense that this prerequisite might only be justifiably tested for the
	 * first time a class is taken (if the Race changes, then all bets are off,
	 * right?). To maintain the existing code function (always check on level
	 * up) this becomes ALLCLASSLEVELS and gets passed into each PCClassLevel.
	 */
	private RaceType preRaceType = null;

	/*
	 * FINALALLCLASSLEVELS Because this indicates prohibited Spell Schools and Spells
	 * Known and Cast are granted by each PCClassLevel, this must be passed into
	 * each and every PCClassLevel (though it can be given in its pure form).
	 */
	/*
	 * TYPESAFETY Since this is storing Spell Schools/SubSchools, this should
	 * be type safe
	 */
	private List<String> prohibitedSchools = null;

	/*
	 * PCCLASSLEVELONLY Since this is not part of a tag and is related to how
	 * spells are related to a PCClassLevel
	 * 
	 * Actually, I may want to DELETEVARIABLE - this is really a cache, and 
	 * need to consider how valuable it is to have a cache vs. not have it.
	 */
	private String stableSpellKey = null;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the sublevel actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private String subClassKey = Constants.s_NONE;

	/*
	 * FINALALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * store what the substitution level actually is. This is NOT set by a tag, so it is
	 * PCCLASSLEVELONLY
	 */
	private Map<Integer, String> substitutionClassKey = null;

	// private TreeSet<Language> languageBonus = new TreeSet<Language>();

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubClass
	 * 
	 * CONSIDER Technically, that's not true ... this is really an indication of 
	 * whether the class HAS subclasses or not (an abstract test, not a practical
	 * "a subclass has been selected" test.  Is this therefore duplicate information??
	 * 
	 * Can I DELETEVARIABLE?
	 */
	private boolean hasSubClass = false;

	/*
	 * ALLCLASSLEVELS This goes into each PCClassLevel from PCClass in order to
	 * indicate if the given PCClassLevel is actualy a SubstitutionClass
	 * 
	 * CONSIDER Technically, that's not true ... this is really an indication of
	 * whether the class HAS substitution classes or not (an abstract test, not
	 * a practical "a substitution class has been selected" test. Is this
	 * therefore duplicate information??
	 * 
	 * Can I DELETEVARIABLE?
	 */
	private boolean hasSubstitutionClass = false;

	/*
	 * TYPESAFETY This is definitely something that needs to NOT be a String,
	 * but it gets VERY complicated to do that, since the keys are widely used
	 * in the variable processor.
	 */
	/*
	 * ALLCLASSLEVELS Must be in all PCClassLevels, since this is the master
	 * index of what this PCClassLevel was created from. Best for debugging, and
	 * not necessarily creation on the fly?
	 */
	/*
	 * REFACTOR This brings up a FASCINATING point, in that the Class Key today
	 * is used in variable processing. How is that to be handled going forward -
	 * so that PCClassLevels are actually what is checked and NOT the PCClass?
	 * What needs to be done to teach the system to iterate over all
	 * PCClassLevels that implement this key?
	 */
	private String classKey = Constants.EMPTY_STRING;

	/** The level of this class for the PC this PCClass is assigned to. */
	/*
	 * ALLCLASSLEVELS This is a fundamental part of the PCClassLevel creation
	 * and stored information
	 */
	protected int level = 0; // TODO - This should be moved.

	private SpellProgressionInfo castInfo = null;
	private SpellProgressionCache spellCache = null;
	private boolean spellCacheValid = false;

	//	private DoubleKeyMap<AbilityCategory, Integer, List<String>> theAutoAbilities = null;

	/**
	 * Default Constructor. Constructs an empty PCClass.
	 */
	public PCClass()
	{
		super();
	}

	/**
	 * Returns the abbreviation for this class.
	 * 
	 * @return The abbreviation string.
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getAbbrev()
	{
		String abb = get(StringKey.ABB);
		if (abb == null)
		{
			String name = getDisplayName();
			abb = name.substring(0, Math.min(3, name.length()));
		}
		return abb;
	}

	/**
	 * Sets the Key name for the class.
	 * 
	 * <p>
	 * This overrides the <tt>setKeyName</tt> method in <tt>PObject</tt> to
	 * also set the spell key for the class.
	 * 
	 * @param aKey
	 *            A (non-internationalized) string used to refer to this class
	 *            object.
	 * 
	 * @see pcgen.core.PObject#setKeyName(String)
	 */
	/*
	 * PCCLASSANDLEVEL Since the classKey is generally the universal index of whether
	 * two PCClassLevels are off of the same base, classKey will be populated into 
	 * each PCClassLevel.  This method must therefore also be in both PCClass and 
	 * PCClassLevel
	 */
	@Override
	public void setKeyName(final String aKey)
	{
		super.setKeyName(aKey);
		classKey = "CLASS:" + keyName; //$NON-NLS-1$
	}

	/**
	 * Return the qualified key, usually used as the source in a
	 * getVariableValue call. Overriden here to return CLASS:keyname
	 * 
	 * @return The qualified key of the object
	 */
	/*
	 * PCCLASSANDLEVEL Since the classKey is generally the universal index of whether
	 * two PCClassLevels are off of the same base, classKey will be populated into 
	 * each PCClassLevel.  This method must therefore also be in both PCClass and 
	 * PCClassLevel
	 */
	@Override
	public String getQualifiedKey()
	{
		return classKey;
	}

	/**
	 * Returns the total bonus to the specified bonus type and name.
	 * 
	 * <p>
	 * This method checks only bonuses associated with the class. It makes sure
	 * to return bonuses that are active only to the max level specified. What
	 * that means is that bonuses specified on class level lines will have a
	 * level parameter associated with them. Only bonuses specified on the level
	 * specified or lower will be totalled.
	 * 
	 * @param argType
	 *            Bonus type e.g. <code>BONUS:<b>DOMAIN</b></code>
	 * @param argMname
	 *            Bonus name e.g. <code>BONUS:DOMAIN|<b>NUMBER</b></code>
	 * @param asLevel
	 *            The maximum level to apply bonuses for.
	 * @param aPC
	 *            The <tt>PlayerCharacter</tt> bonuses are being calculated
	 *            for.
	 * 
	 * @return Total bonus value.
	 */
	/*
	 * REFACTOR There is potentially redundant information here - level and PC... 
	 * is this ever out of sync or can this method be removed/made private??
	 */
	public double getBonusTo(final String argType, final String argMname,
		final int asLevel, final PlayerCharacter aPC)
	{
		double i = 0;

		if ((asLevel == 0) || getBonusList().isEmpty())
		{
			return 0;
		}

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();

		for (final BonusObj bonus : getBonusList())
		{
			final StringTokenizer breakOnPipes =
					new StringTokenizer(bonus.toString().toUpperCase(),
						Constants.PIPE, false);
			final int aLevel = Integer.parseInt(breakOnPipes.nextToken());
			final String theType = breakOnPipes.nextToken();

			if (!theType.equals(type))
			{
				continue;
			}

			final String str = breakOnPipes.nextToken();
			final StringTokenizer breakOnCommas =
					new StringTokenizer(str, Constants.COMMA, false);

			while (breakOnCommas.hasMoreTokens())
			{
				final String theName = breakOnCommas.nextToken();

				if ((aLevel <= asLevel) && theName.equals(mname))
				{
					final String aString = breakOnPipes.nextToken();
					final List<Prerequisite> localPreReqList =
							new ArrayList<Prerequisite>();
					if (bonus.hasPreReqs())
					{
						localPreReqList.addAll(bonus.getPreReqList());
					}

					// TODO: This code should be removed after the 5.8 release
					// as the prereqs are processed by the bonus loading code.
					while (breakOnPipes.hasMoreTokens())
					{
						final String bString = breakOnPipes.nextToken();

						if (PreParserFactory.isPreReqString(bString))
						{
							Logging
								.debugPrint("Why is this prerequisite '" + bString + "' parsed in '" + getClass().getName() + ".getBonusTo(String,String,int)' rather than in the persistence layer?"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
							try
							{
								final PreParserFactory factory =
										PreParserFactory.getInstance();
								localPreReqList.add(factory.parse(bString));
							}
							catch (PersistenceLayerException ple)
							{
								Logging.errorPrint(ple.getMessage(), ple);
							}
						}
					}

					// must meet criteria for bonuses before adding them in
					// TODO: This is a hack to avoid VARs etc in class defs
					// being qualified for when Bypass class prereqs is
					// selected.
					// Should we be passing in the BonusObj here to allow it to
					// be referenced in Qualifies statements?
					if (PrereqHandler.passesAll(localPreReqList, aPC, null))
					{
						final double j =
								aPC.getVariableValue(aString, classKey)
									.doubleValue();
						i += j;
					}
				}
			}
		}

		return i;
	}

	/**
	 * Return the number of spells a character can cast in this class for the
	 * current level.
	 * 
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	public int getCastForLevel(final int spellLevel, final String bookName,
		final PlayerCharacter aPC)
	{
		return getCastForLevel(spellLevel, bookName, true, true, aPC);
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param pcLevel
	 *            The number of levels in this class that the character has
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param bookName
	 *            the name of the spell book we are interested in
	 * @param includeAdj
	 *            Seems to have something to do with speciality spells
	 * @param limitByStat
	 *            Do we return 0 for any spell level that the character does not
	 *            have a high enough stat to cast
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this
	 *         level.
	 */
	public int getCastForLevel(final int spellLevel, final String bookName,
		final boolean includeAdj, final boolean limitByStat,
		final PlayerCharacter aPC)
	{
		int pcLevel = getLevel();
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel +=
				(int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		if (getNumFromCastList(pcLevel, spellLevel, aPC) < 0)
		{
			// can't cast spells of this level
			// however, character might have a bonus spell slot e.g. from
			// certain feats
			return (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
				+ levelSpellLevel);
		}

		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
					+ levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType()
					+ levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any"
					+ levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", classKeyName
					+ allSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType()
					+ allSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any"
					+ allSpellLevel);

		final int index = bonusSpellIndex();

		final PCStat aStat;

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size()))
		{
			aStat = aPC.getStatList().getStatAt(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0)
		{
			statString = SettingsHandler.getGame().s_ATTRIBSHORT[index];
		}

		final int bonusStat =
				(int) aPC.getTotalBonusTo("STAT", "CAST." + statString)
					+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT")
					+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS."
						+ keyName);

		if ((index > -2) && limitByStat)
		{
			PCStat ss = get(ObjectKey.SPELL_STAT);
			if (ss != null)
			{
				final int maxSpellLevel =
					aPC.getVariableValue("MAXLEVELSTAT=" + ss.getAbb(), "")
						.intValue();

				if ((maxSpellLevel + bonusStat) < spellLevel)
				{
					return total;
				}
			}
		}

		stat += bonusStat;

		// Now we decide whether to adjust the number of slots down
		// the road by adding specialty slots.
		// Reworked to consider the fact that a lower-level
		// specialty spell can go into this level of specialty slot
		//
		int adj = 0;

		if (includeAdj && !bookName.equals(Globals.getDefaultSpellBook())
			&& (hasSpecialty() || aPC.hasCharacterDomainList()))
		{
			// We need to do this for EVERY spell level up to the
			// one really under consideration, because if there
			// are any specialty spells available BELOW this level,
			// we might wind up using THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ++ix)
			{
				final List<CharacterSpell> aList =
						getSpellSupport().getCharacterSpell(null,
							Constants.EMPTY_STRING, ix);
				List<Spell> bList = new ArrayList<Spell>();

				if (!aList.isEmpty())
				{
					// Assume no null check on castInfo requried, because
					// getNumFromCastList above would have returned -1
					if ((ix > 0)
						&& "DIVINE".equalsIgnoreCase(castInfo.getSpellType()))
					{
						for (CharacterDomain cd : aPC.getCharacterDomainList())
						{
							if (cd.isFromPCClass(getKeyName())
								&& (cd.getDomain() != null))
							{
								bList =
										Globals.getSpellsIn(ix,
											Constants.EMPTY_STRING, cd
												.getDomain().getKeyName());
							}
						}
					}

					for (CharacterSpell cs : aList)
					{
						int x = -1;

						if (!bList.isEmpty())
						{
							if (bList.contains(cs.getSpell()))
							{
								x = 0;
							}
						}
						else
						{
							x =
									cs.getInfoIndexFor(Constants.EMPTY_STRING,
										ix, 1);
						}

						if (x > -1)
						{
							PCClass target = this;
							if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE))
							{
								target = getSubClassKeyed(subClassKey);
							}
							adj = target.getSpecialtyKnownForLevel(spellLevel, aPC);

							break;
						}
					}
				}
				// end of what to do if aList is not empty

				if (adj > 0)
				{
					break;
				}
			}
			// end of looping up to this level looking for specialty spells that
			// can be cast
		}
		// end of deciding whether there are specialty slots to distribute

		int mult =
				(int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName
					+ levelSpellLevel);
		mult +=
				(int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE."
					+ getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		final int t = getNumFromCastList(pcLevel, spellLevel, aPC);

		total += ((t * mult) + adj);

		// TODO - God I hate all these strings. Return an array or list.
		final String bonusSpell =
				Globals.getBonusSpellMap().get(String.valueOf(spellLevel));

		// TODO - Yuck. Figure out how to get rid of hardcoded "0|0"

		if ((bonusSpell != null) && !bonusSpell.equals("0|0")) //$NON-NLS-1$
		{
			final StringTokenizer s =
					new StringTokenizer(bonusSpell, Constants.PIPE);
			final int base = Integer.parseInt(s.nextToken());
			final int range = Integer.parseInt(s.nextToken());

			if (stat >= base)
			{
				total += Math.max(0, (stat - base + range) / range);
			}
		}

		return total;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public final List<CDOMListObject<Spell>> getClassSpellList()
	{
		return classSpellList;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass since it
	 * is a Tag
	 */
	public final void setExClass(final String aString)
	{
		exClass = aString;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getExClass()
	{
		return exClass;
	}

	/*
	 * PCCLASSONLY Since this is a reference variable, it will likely
	 * only appear in PCCLASS
	 */
	public final void setItemCreationMultiplier(
		final String argItemCreationMultiplier)
	{
		itemCreationMultiplier = argItemCreationMultiplier;
	}

	/*
	 * PCCLASSONLY Since this is a reference variable, it will likely
	 * only appear in PCCLASS
	 */
	public final String getItemCreationMultiplier()
	{
		return itemCreationMultiplier;
	}

	/*
	 * PCCLASSLEVELONLY This is only relevant for the PCClassLevel (obviously?)
	 */
	public final int getLevel()
	{
		return level;
	}

	/*
	 * UNKNOWNDESTINATION Because this is a VERY strange variable and function,
	 * I have yet to architect exactly how this will work in the PCGen system.
	 * There will end up having to do some form of verification across multiple
	 * PCClassLevels, so this is really a similar solution to how the Challenge
	 * Rating (CRFormula) works.
	 */
	public final void setLevelExchange(final String aString)
	{
		levelExchange = aString;
	}

	/*
	 * UNKNOWNDESTINATION Because this is a VERY strange variable and function,
	 * I have yet to architect exactly how this will work in the PCGen system.
	 * There will end up having to do some form of verification across multiple
	 * PCClassLevels, so this is really a similar solution to how the Challenge
	 * Rating (CRFormula) works.
	 */
	public final String getLevelExchange()
	{
		return levelExchange;
	}

	/**
	 * set the level to arg without impacting spells, hp, or anything else - use
	 * this with great caution only TODO Then why is it even here, What is it
	 * used for (JSC 07/21/03)
	 * 
	 * @param arg
	 */
	/*
	 * DELETEMETHOD This method is NOT appropriate for either PCClass or
	 * PCClassLevel.  The equivalent functionality in order to sustain the 
	 * maxbablevel and maxcheckslevel globals will have to be done by
	 * PlayerCharacter as it filters out the PCClassLevels that are allowed
	 * to be used for those calculations.
	 */
	public final void setLevelWithoutConsequence(final int arg)
	{
		level = arg;
	}

	/**
	 * Identify if this class has a cap on the number of levels it is 
	 * possible to take.
	 * @return true if a cap on levels exists, false otherwise.
	 */
	public final boolean hasMaxLevel()
	{
		Integer ll = get(IntegerKey.LEVEL_LIMIT);
		return ll != null && ll != Constants.NO_LEVEL_LIMIT;
	}

	/*
	 * PCCLASSANDLEVEL This is a characteristic of both the PCClass and
	 * the individual PCClassLevels (since the prereq needs to be 
	 * enforced at every level-up)
	 * 
	 * Trying to DELETEMETHOD by cleaning out PRERACETYPE - but need
	 * some more guidance from Tir on how this should work - thpr 11/6/06
	 */
	public final void setPreRaceType(RaceType rt)
	{
		preRaceType = rt;
	}

	// public final void setSkillPool(final Integer argSkillPool)
	// {
	// skillPool = argSkillPool;
	// }

	/*
	 * REFACTOR This is BAD that this is referring to PCLevelInfo - that gets
	 * VERY confusing as far as object interaction. Can we get rid of
	 * PCLevelInfo altogether?
	 */
	public final int getSkillPool(final PlayerCharacter aPC)
	{
		int returnValue = 0;
		// //////////////////////////////////
		// Using this method will return skills for level 0 even when there is
		// no information
		// Byngl - December 28, 2004
		// for (int i = 0; i <= level; i++)
		// {
		// final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), i);
		//
		// if ((pcl != null) && pcl.getClassKeyName().equals(getKeyName()))
		// {
		// returnValue += pcl.getSkillPointsRemaining();
		// }
		// }
		for (PCLevelInfo pcl : aPC.getLevelInfo())
		{
			if (pcl.getClassKeyName().equals(getKeyName()))
			{
				returnValue += pcl.getSkillPointsRemaining();
			}
		}
		// //////////////////////////////////

		return returnValue;
	}

	/*
	 * FINALPCCLASSLEVELONLY created during PCClassLevel creation (in the factory)
	 */
	public final String getSpecialty()
	{
		return specialty;
	}

	/*
	 * FINALPCCLASSLEVELONLY For boolean testing of possession
	 */
	public final boolean hasSpecialty()
	{
		return specialty != null;
	}

	/*
	 * FINALPCCLASSLEVELONLY Input during construction of a PCClassLevel
	 */
	public final void addSpecialty(final String aSpecialty)
	{
		specialty = aSpecialty;
	}

	/*
	 * PCCLASSANDLEVEL This can be simplified, however, since there won't be the
	 * same subClass type delegation within the new PCClassLevel.
	 */
	/*
	 * Note this is NOT a bug in that this handles subLevel but does not do any
	 * work for substitution levels... remember that substitution levels are for
	 * a certain advancement level, not across the entire Class level
	 * progression.
	 */
	/*
	 * REFACTOR At least CONSIDER Whether this should be an @Override of 
	 * getDisplayName()?  What additional value does this provide by being
	 * a separate method?? - thpr 11/6/06
	 */
	public String getDisplayClassName()
	{
		if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE))
		{
			SubClass sc = getSubClassKeyed(subClassKey);
			if (sc != null)
			{
				return sc.getDisplayName();
			}
		}

		return getDisplayName();
	}

	/*
	 * PCCLASSLEVELONLY This can be simplified, however, since there won't be the
	 * same subClass type delegation within the new PCClassLevel. - note this
	 * method is really the PCClassLevel implementation of getDisplayClassName()
	 * above [so technically this method doesn't go into PCClass, the method
	 * above does)
	 */
	/*
	 * REFACTOR Once this is in PCClassLevel, at least CONSIDER Whether this
	 * should be an @Override of getDisplayName()? What additional value does
	 * this provide by being a separate method?? - thpr 11/6/06
	 */
	public String getDisplayClassName(final int aLevel)
	{
		String aKey = getSubstitutionClassKey(aLevel);
		if (aKey == null)
		{
			return getDisplayClassName();
		}
		String name = getSubstitutionClassKeyed(aKey).getDisplayName();
		if (name == null)
		{
			return getDisplayClassName();
		}

		return name;
	}

	/*
	 * PCCLASSLEVELONLY Must only be the PCClassLevel since this refers to the 
	 * level in the String that is returned.
	 */
	public String getFullDisplayClassName()
	{
		final StringBuffer buf = new StringBuffer();

		buf.append(getDisplayClassName());

		return buf.append(" ").append(level).toString();
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the subclass is activated, this is required in both locations.
	 * 
	 * Trying to DELETEMETHOD by deleting HASSUBCLASS - thpr 11/6/06
	 */
	public final void setHasSubClass(final boolean arg)
	{
		hasSubClass = arg;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the substitution class is activated, this is required in both
	 * locations.
	 * 
	 * Trying to DELETEMETHOD by deleting HASSUBSTITUTIONCLASS - thpr 11/6/06
	 */
	public final void setHasSubstitutionClass(final boolean arg)
	{
		hasSubstitutionClass = arg;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass because it
	 * is a Tag
	 */
	public final boolean addProhibitedSchool(String school)
	{
		if (prohibitedSchools == null)
		{
			prohibitedSchools = new ArrayList<String>();
		}
		boolean addedSchool = false;
		if (!prohibitedSchools.contains(school))
		{
			addedSchool = prohibitedSchools.add(school);
		}
		return addedSchool;
	}

	/*
	 * FINALPCCLASSONLY This is required in PCClass because it is used in
	 * construction of PCClassLevel... (or is it required there too??)
	 */
	public final void clearProhibitedSchools()
	{
		prohibitedSchools = null;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final List<String> getProhibitedSchools()
	{
		return prohibitedSchools;
	}

	/*
	 * PCCLASSLEVELONLY This is an active level calculation, and is therefore
	 * only appropriate in the PCClassLevel that has the particular Hit Die for
	 * which the calculation is required.
	 */
	public HitDie getLevelHitDie(final PlayerCharacter aPC, final int classLevel)
	{
		// Class Base Hit Die
		HitDie currDie = getSafe(ObjectKey.LEVEL_HITDIE);
		Modifier<HitDie> dieLock = aPC.getRace().get(ObjectKey.HITDIE);
		if (dieLock != null)
		{
			currDie = dieLock.applyModifier(currDie, this);
		}

		// Templates
		for (PCTemplate template : aPC.getTemplateList())
		{
			if (template != null)
			{
				Modifier<HitDie> lock = template.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, this);
				}
			}
		}

		// Levels
		PCClassLevel cl = levelMap.get(classLevel);
		if (cl != null)
		{
			Modifier<HitDie> lock = cl.get(ObjectKey.HITDIE);
			if (lock != null)
			{
				currDie = lock.applyModifier(currDie, this);
			}
		}

		return currDie;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellBaseStat()
	{
		Boolean useStat = get(ObjectKey.USE_SPELL_SPELL_STAT);
		if (useStat == null)
		{
			return "None";
		}
		else if (useStat)
		{
			return "SPELL";
		}
		Boolean otherCaster = get(ObjectKey.CASTER_WITHOUT_SPELL_STAT);
		if (otherCaster)
		{
			return "OTHER";
		}
		return get(ObjectKey.SPELL_STAT).getAbb();
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the class spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	@Override
	public String getSpellKey()
	{
		if (stableSpellKey != null)
		{
			return stableSpellKey;
		}

		if (classSpellList == null)
		{
			chooseClassSpellList();

			if (classSpellList == null)
			{
				stableSpellKey = "CLASS" + Constants.PIPE + keyName;

				return stableSpellKey;
			}
		}

		final StringBuffer aBuf = new StringBuffer();
		boolean needPipe = false;

		for (CDOMListObject<Spell> keyStr : classSpellList)
		{
			if (needPipe)
			{
				aBuf.append(Constants.PIPE);
			}
			needPipe = true;

			if (DomainSpellList.class.equals(keyStr.getClass()))
			{
				aBuf.append("DOMAIN").append(Constants.PIPE).append(
						keyStr.getLSTformat());
			}
			else
			{
				aBuf.append("CLASS").append(Constants.PIPE).append(
						keyStr.getLSTformat());
			}
		}

		stableSpellKey = aBuf.toString();

		return stableSpellKey;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass since 
	 * it is a Tag
	 */
	public final void setSpellType(final String newType)
	{
		if (castInfo == null && Constants.s_NONE.equals(newType))
		{
			//Don't create a SpellProgressionInfo to set to default!!
			return;
		}
		getConstructingSpellProgressionInfo().setSpellType(newType);
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType()
	{
		return castInfo == null ? Constants.s_NONE : castInfo.getSpellType();
	}

	/*
	 * PCCLASSLEVELONLY Since this is the PCClassLevel specific version
	 * of getCastList, it is only appropriate for the class levels.
	 * 
	 * May also be required in the Factory for PCClassLevels, so might
	 * also appear in PCClass.
	 */
	public List<Formula> getCastListForLevel(int aLevel)
	{
		if (!updateSpellCache(false))
		{
			return null;
		}
		return spellCache.getCastForLevel(aLevel);
	}

	public boolean hasCastList()
	{
		return updateSpellCache(false) && spellCache.hasCastProgression();
	}

	/*
	 * PCCLASSONLY For editing a PCClass and Global checks...
	 */
	public Map<Integer, List<Formula>> getCastProgression()
	{
		if (!updateSpellCache(false))
		{
			return null;
		}
		return spellCache.getCastProgression();
	}

	/**
	 * Return the level of the highest level spell offered by the class.
	 * 
	 * @return The level of the highest level spell available.
	 */
	public int getHighestLevelSpell()
	{
		if (!updateSpellCache(false))
		{
			return -1;
		}
		return Math.max(spellCache.getHighestCastSpellLevel(), spellCache
			.getHighestKnownSpellLevel());
	}

	/**
	 * Return the level of the highest level spell the character could possibly
	 * cast in this class. This can return a higher level than the class allows
	 * if the character has feats which give a bonus to casting.
	 * 
	 * @param pc
	 *            The character to be checked.
	 * @return The level of the highest level spell available.
	 */
	public int getHighestLevelSpell(PlayerCharacter pc)
	{
		final String classKeyName = "CLASS." + getKeyName();
		int mapHigh = getHighestLevelSpell();
		int high = mapHigh;
		for (int i = mapHigh; i < mapHigh + 30; i++)
		{
			final String levelSpellLevel = ";LEVEL." + i;
			if (pc.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel) > 0)
			{
				high = i;
			}
			else if (pc.getTotalBonusTo("SPELLKNOWN", classKeyName
				+ levelSpellLevel) > 0)
			{
				high = i;
			}
		}
		return high;
	}

	/**
	 * Return number of spells known for a level.
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 */
	public int getKnownForLevel(final int spellLevel, final PlayerCharacter aPC)
	{
		return getKnownForLevel(spellLevel, "null", aPC);
	}

	/**
	 * if castAs has been set, return knownList from that class
	 * 
	 * @return List
	 */
	/*
	 * REFACTOR This contains castAs which is Cross-Class behavior and is VERY
	 * bad. The caller of this method or the method calling getKnownList() or
	 * whatever is setting the castAs should really be able to do some other
	 * check first, rather than using castAs. Since CASTAS is deprecated, this
	 * CASTAS functionality (hopefully) can be removed from PCClass and
	 * PCClassLevel
	 */
	/*
	 * PCCLASSONLY This is for PCClass construction and Global queries
	 */
	public Map<Integer, List<Formula>> getKnownMap()
	{
		if (!updateSpellCache(false))
		{
			return null;
		}
		return spellCache.getKnownProgression();
	}

	/*
	 * PCCLASSANDLEVEL This is used for detecting spell casting ability
	 */
	public boolean hasKnownList()
	{
		return updateSpellCache(false) && spellCache.hasKnownProgression();
	}

	/*
	 * PCCLASSONLY This is required in PCClass for PCClass editing
	 * 
	 * DELETEMETHOD - this isn't used??? Or perhaps that indicates 
	 * that the GUI LST CLASS editor is incomplete :)
	 */
	public final Map<Integer, List<Formula>> getSpecialtyKnownList()
	{
		if (!updateSpellCache(false))
		{
			return null;
		}
		return spellCache.getSpecialtyKnownMap();
	}

	/**
	 * Get the number of spells this PC can cast based on Caster Level and
	 * desired Spell Level ex: how many 5th level spells can a 17th level wizard
	 * cast?
	 * 
	 * @param iCasterLevel
	 * @param iSpellLevel
	 * @param aPC
	 *            The character we are interested in
	 * @return int
	 */
	/*
	 * REFACTOR There seems to be redundant information here (if there
	 * is a PC, why do we need to know the PC Level?
	 */
	public int getNumFromCastList(final int iCasterLevel,
		final int iSpellLevel, final PlayerCharacter aPC)
	{
		if (iCasterLevel == 0)
		{
			// can't cast spells!
			return -1;
		}

		List<Formula> castListForLevel = getCastListForLevel(iCasterLevel);
		if (castListForLevel == null || iSpellLevel >= castListForLevel.size())
		{
			return -1;
		}
		return castListForLevel.get(iSpellLevel).resolve(aPC, "").intValue();
	}

	/*
	 * REFACTOR Again, there is rudundant information here in the fetching of
	 * what is currently possible for the current character level. This is
	 * generally something that should only appear in the PCClassLevel, but
	 * should be considered with the wider range of "what can I really cast"
	 * methods that are tagged to be refactored.
	 */
	public String getBonusCastForLevelString(final int spellLevel,
		final String bookName, final PlayerCharacter aPC)
	{
		if (getCastForLevel(spellLevel, bookName, true, true, aPC) > 0)
		{
			// if this class has a specialty, return +1
			if (hasSpecialty())
			{
				PCClass target = this;
				if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE))
				{
					target = getSubClassKeyed(subClassKey);
				}
				
				return "+"+target.getSpecialtyKnownForLevel(spellLevel, aPC);
			}

			if (!aPC.hasCharacterDomainList())
			{
				return "";
			}

			// if the spelllevel is >0 and this class has a characterdomain
			// associated with it, return +1
			if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(getSpellType()))
			{
				for (CharacterDomain cd : aPC.getCharacterDomainList())
				{
					if (cd.isFromPCClass(getKeyName()))
					{
						return "+1";
					}
				}
			}
		}

		return "";
	}

	/**
	 * Return the number of spells a character can cast in this class for a
	 * specified level.
	 * 
	 * @param spellLevel
	 *            The spell level we are interested in
	 * @param aPC
	 *            The character we are interested in
	 * @return The number of spells per day that this character can cast of the
	 *         given spell level.
	 */
	public int getCastForLevel(final int spellLevel, final PlayerCharacter aPC)
	{
		return getCastForLevel(spellLevel, Globals.getDefaultSpellBook(), true,
			true, aPC);
	}

	/**
	 * Return number of speciality spells known for a level for a given
	 * spellbook
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 */
	/*
	 * REFACTOR More redundant information and opportunity to refactor to 
	 * simplify the interface of PCClassLevel
	 */
	public int getSpecialtyKnownForLevel(final int spellLevel,
		final PlayerCharacter aPC)
	{
		int total;
		total =
				(int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS."
					+ getKeyName() + ";LEVEL." + spellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE."
					+ getSpellType() + ";LEVEL." + spellLevel);

		int pcLevel = getLevel();
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel +=
				(int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		final int index = baseSpellIndex();

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size()))
		{
			final PCStat aStat = aPC.getStatList().getStatAt(index);
			final int maxSpellLevel =
					aPC.getVariableValue("MAXLEVELSTAT=" + aStat.getAbb(), "")
						.intValue();

			if (spellLevel > maxSpellLevel)
			{
				return total;
			}
		}

		if (updateSpellCache(false))
		{
			List<Formula> specKnown =
				spellCache.getSpecialtyKnownForLevel(pcLevel);
			if (specKnown != null && specKnown.size() > spellLevel)
			{
				total += specKnown.get(spellLevel).resolve(aPC, "").intValue();
			}
		}

		// make sure any slots due from specialties (including domains) are
		// added
		total += getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY);

		return total;
	}

	/*
	 * FINALPCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (called during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public void setSubstitutionClassKey(final String aKey, final Integer aLevel)
	{
		if (substitutionClassKey == null)
		{
			substitutionClassKey = new HashMap<Integer, String>();
		}
		substitutionClassKey.put(aLevel, aKey);
	}

	/*
	 * FINALPCCLASSLEVELONLY Since this is getting the key that will appear in
	 * the PCClassLevel (was set during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public String getSubstitutionClassKey(final Integer aLevel)
	{
		if (substitutionClassKey == null)
		{
			return null;
		}
		return substitutionClassKey.get(aLevel);
	}

	/*
	 * PCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (called during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public void setSubClassKey(final String aKey)
	{
		subClassKey = aKey;

		if (!aKey.equals(getKeyName()))
		{
			final SubClass a = getSubClassKeyed(aKey);

			if (a != null)
			{
				inheritAttributesFrom(a);
			}
		}

		stableSpellKey = null;
		getSpellKey();
	}

	/*
	 * PCCLASSLEVELONLY Since this is setting the key that will appear in
	 * the PCClassLevel (was set during construction) this is only required
	 * in the level objects, not PCClass
	 */
	public String getSubClassKey()
	{
		if (subClassKey == null)
		{
			subClassKey = "";
		}

		return subClassKey;
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final SubClass getSubClassKeyed(final String aKey)
	{
		if (subClassList == null)
		{
			return null;
		}

		for (SubClass subClass : subClassList)
		{
			if (subClass.getKeyName().equals(aKey))
			{
				return subClass;
			}
		}

		return null;
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final SubstitutionClass getSubstitutionClassKeyed(final String aKey)
	{
		if (substitutionClassList == null)
		{
			return null;
		}

		for (SubstitutionClass sc : substitutionClassList)
		{
			if (sc.getKeyName().equals(aKey))
			{
				return sc;
			}
		}

		return null;
	}

	/*
	 * Note: Since this is local access, it does not need to override
	 * getTemplateList from PObject (although it is probably confusing that it
	 * doesn't). The key point is ensuring that getTemplates(final boolean flag,
	 * final PlayerCharacter aPC) is still properly overriding the PObject
	 * method so that the proper templates for this PCClass are applied to the
	 * PlayerCharacter.
	 */
	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<LevelProperty<String>> getTemplates()
	{
		if (templates == null)
		{
			final List<LevelProperty<String>> ret = Collections.emptyList();
			return ret;
		}
		return Collections.unmodifiableList(templates);
	}

	/*
	 * DELETEMETHOD I don't believe (?) that this is used in a place that
	 * actually has any effect???  Will (obviously) need to test that!!
	 */
	public void clearTemplates()
	{
		templates = null;
	}

	//	public void setAutoAbilities(final AbilityCategory aCategory, final int aLevel, final List<String> aList)
	//	{
	//		if ( aCategory == AbilityCategory.FEAT )
	//		{
	//			setFeatAutos(aLevel, CoreUtility.join(aList, Constants.PIPE));
	//			return;
	//		}
	//		if ( theAutoAbilities == null )
	//		{
	//			theAutoAbilities = new DoubleKeyMap<AbilityCategory, Integer, List<String>>();
	//		}
	//		List<String> abilities = theAutoAbilities.get(aCategory, aLevel);
	//		if ( abilities == null )
	//		{
	//			abilities = new ArrayList<String>();
	//		}
	//		abilities.addAll(aList);
	//	}

	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public void setHitPoint(final int aLevel, final Integer iRoll)
	{
		if (hitPointMap == null)
		{
			hitPointMap = new HashMap<Integer, Integer>();
		}
		hitPointMap.put(aLevel, iRoll);
	}

	/*
	 * PCCLASSLEVELONLY This is required for PlayerCharacter.makeEXclass()
	 */
	public Map<Integer, Integer> getHitPointMap()
	{
		return new HashMap<Integer, Integer>(hitPointMap);
	}

	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public int getHitPoint(final int aLevel)
	{
		if (hitPointMap == null)
		{
			return 0;
		}
		final Integer aHP = hitPointMap.get(aLevel);

		if (aHP == null)
		{
			return 0;
		}

		return aHP;
	}

	/*
	 * PCCLASSLEVELONLY This is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public final void setHitPointMap(Map<Integer, Integer> hpMap)
	{
		hitPointMap = null;
		if (hpMap != null)
		{
			hitPointMap = new HashMap<Integer, Integer>(hpMap);
		}
	}

	/**
	 * we over ride the PObject setVision() function to keep track of what
	 * levels this VISION: tag should take effect
	 * 
	 * @param aString
	 * @param aPC
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel PCClass since it is
	 * a Tag [with level dependence differences, of course)
	 */
	public final void addVision(int aLevel, Vision vis)
	{
		if (visionList == null)
		{
			visionList = new ArrayList<LevelProperty<Vision>>();
		}
		visionList.add(LevelProperty.getLevelProperty(aLevel, vis));
	}

	/*
	 * FINALPCCLASSONLY This is an editor and loader requirement, therefore
	 * PCClass only
	 */
	@Override
	public void clearVisionList()
	{
		if (visionList != null)
		{
			visionList.clear();
		}
	}

	/*
	 * FINALPCCLASSONLY This is an editor and loader requirement, therefore
	 * PCClass only
	 */
	@Override
	public boolean removeVisionType(VisionType type)
	{
		if (visionList == null)
		{
			return false;
		}
		for (LevelProperty<Vision> lp : visionList)
		{
			if (lp.getObject().getType().equals(type))
			{
				return visionList.remove(lp);
			}
		}
		return false;
	}

	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public boolean isAutoKnownSpell(final String spellName,
		final int spellLevel, final PlayerCharacter aPC)
	{
		return isAutoKnownSpell(spellName, spellLevel, false, aPC);
	}

	public void setLevel(final int newLevel, final PlayerCharacter aPC)
	{
		final int curLevel = level;

		if (newLevel >= 0)
		{
			level = newLevel;
		}

		if (level == 1)
		{
			if (level > curLevel || aPC.isImporting())
			{
					final boolean isMonsterClass =
							aPC.getRace().getMonsterClass(aPC, false) != null
								&& aPC.getRace().getMonsterClass(aPC, false)
									.equalsIgnoreCase(this.getKeyName());
					Integer mLevPerFeat = get(IntegerKey.LEVELS_PER_FEAT);
					int startLevel;
					int rangeLevel;
					int divisor;
					if (mLevPerFeat == null)
					{
						String aString = Globals.getBonusFeatString();
						StringTokenizer aTok = 
							new StringTokenizer(aString, "|", false);
						startLevel = Integer.parseInt(aTok.nextToken());
						rangeLevel = Integer.parseInt(aTok.nextToken());
						divisor = rangeLevel;
					}
					else
					{
						startLevel = 0;
						rangeLevel = 0;
						divisor = mLevPerFeat;
					}
					if (divisor > 0)
					{
						if (SettingsHandler.isMonsterDefault() && isMonsterClass)
						{
							int monLev =
									aPC.getRace().getMonsterClassLevels(aPC,
										false);

							StringBuffer aBuf = new StringBuffer(
									"0|FEAT|MONSTERPOOL|");
							aBuf.append("max(0,floor((CL-");
							aBuf.append(monLev);
							aBuf.append(")/");
							aBuf.append(divisor);
							aBuf.append("))");
							BonusObj bon = Bonus.newBonus(aBuf.toString());
							bon.setCreatorObject(this);
							addBonusList(bon);
						}
						else
						{
							StringBuffer aBuf = new StringBuffer(
									"0|FEAT|PCPOOL|(CL-" + startLevel + "+"
									+ rangeLevel + ")/");
							aBuf.append(divisor);
							BonusObj bon = Bonus.newBonus(aBuf.toString());
							bon.setCreatorObject(this);
							addBonusList(bon);
						}
					}
			}

			chooseClassSkillList();
		}

		if (!aPC.isImporting())
		{
			aPC.calcActiveBonuses();
			aPC.getSpellTracker().buildSpellLevelMap(newLevel);
		}

		if ((level == 1) && !aPC.isImporting() && (curLevel == 0))
		{
			checkForSubClass(aPC);
			getSpellKey();
		}

		if (!aPC.isImporting() && (curLevel < level))
		{
			checkForSubstitutionClass(level, aPC);
		}

		for (PCClass pcClass : aPC.getClassList())
		{
			pcClass.calculateKnownSpellsForClassLevel(aPC);
		}
	}

	/*
	 * PCCLASSLEVELONLY This really is modification of a PlayerCharacter from
	 * losing a level, so this is definitely part of PCClassLevel, if it is even
	 * used at all. Because this is losing a level, this will no longer be
	 * required in a CDOM world, perhaps this can be refactored out of existance
	 * before then?
	 * 
	 * May be DELETEMETHOD (related to subLevel)
	 */
	protected void removeKnownSpellsForClassLevel(final PlayerCharacter aPC)
	{
		final String spellKey = getSpellKey();

		if (!containsListFor(ListKey.KNOWN_SPELLS) || aPC.isImporting()
				|| !aPC.getAutoSpells())
		{
			return;
		}

		if (getSpellSupport().getCharacterSpellCount() == 0)
		{
			return;
		}

		for (Iterator<CharacterSpell> iter =
				getSpellSupport().getCharacterSpellList().iterator(); iter
			.hasNext();)
		{
			final CharacterSpell charSpell = iter.next();

			final Spell aSpell = charSpell.getSpell();

			// Check that the character can still cast spells of this level.
			final Integer[] spellLevels = aSpell.levelForKey(spellKey, aPC);
			for (Integer i = 0; i < spellLevels.length; i++)
			{
				final int spellLevel = spellLevels[i];
				if (spellLevel == -1)
				{
					continue;
				}

				final boolean isKnownAtThisLevel =
						isAutoKnownSpell(aSpell.getKeyName(), spellLevel, true,
							aPC);

				if (!isKnownAtThisLevel)
				{
					iter.remove();
				}
			}
		}
	}

	/*
	 * PCCLASSLEVELONLY This modifies the PlayerCharacter to have the
	 * appropriate known spells for this classlevel. As this is called from
	 * setLevel (really from addLevel), this is clearly part of PCClassLevel. In
	 * fact, I wonder how much this can be refactored out (this is really a CDOM
	 * issue) and no longer be loading lots of gunk into the PlayerCharacter.
	 */
	protected void calculateKnownSpellsForClassLevel(final PlayerCharacter aPC)
	{
		// If this class has at least one entry in the "Known spells" tag
		// And we aer set up to automatically assign known spells...
		if (containsListFor(ListKey.KNOWN_SPELLS) && !aPC.isImporting()
				&& aPC.getAutoSpells())
		{
			// Get every spell that can be cast by this class.
			final List<Spell> cspelllist =
					Globals.getSpellsIn(-1, getSpellKey(),
						Constants.EMPTY_STRING);
			if (cspelllist.isEmpty())
			{
				return;
			}

			// Recalculate the number of spells per day of each level
			// that this chracter can cast in this class.
			calcCastPerDayMapForLevel(aPC);

			// Get the maximum spell level that this character can cast.
			final int _maxLevel = getMaxCastLevel();

			// Get the key for this class (i.e. "CLASS|Cleric")
			final String spellKey = getSpellKey();

			// For every spell that this class can ever cast.
			for (Spell spell : cspelllist)
			{
				// For each spell level that this class can cast this spell at
				final Integer[] spellLevels = spell.levelForKey(spellKey, aPC);
				for (Integer si = 0; si < spellLevels.length; ++si)
				{
					final int spellLevel = spellLevels[si];
					if (spellLevel == -1)
					{
						continue;
					}
					if (spellLevel <= _maxLevel)
					{
						// If the spell is autoknown at this level
						if (isAutoKnownSpell(spell.getKeyName(), spellLevel,
							true, aPC))
						{
							CharacterSpell cs =
									getSpellSupport()
										.getCharacterSpellForSpell(spell, this);
							if (cs == null)
							{
								// Create a new character spell for this level.
								cs = new CharacterSpell(this, spell);
								cs.addInfo(spellLevel, 1, Globals
									.getDefaultSpellBook());
								getSpellSupport().addCharacterSpell(cs);
							}
							else
							{
								if (cs.getSpellInfoFor(Globals
									.getDefaultSpellBook(), spellLevel, -1) == null)
								{
									cs.addInfo(spellLevel, 1, Globals
										.getDefaultSpellBook());
								}
								else
								{
									// already know this one
								}
							}
						}
					}
				}
			}

			for (CharacterDomain cd : aPC.getCharacterDomainList())
			{
				if ((cd.getDomain() != null) && cd.isFromPCClass(getKeyName()))
				{
					cd.getDomain()
						.addSpellsToClassForLevels(this, 0, _maxLevel);
				}
			}
		}
	}

	/**
	 * Get the highest level of spell that this class can cast.
	 * 
	 * @return the highest level of spells that this class can cast, or -1 if
	 *         this class can not cast spells
	 */
	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	public int getMaxCastLevel()
	{
		int currHighest = -1;
		if (castForLevelMap != null)
		{
			for (int key : castForLevelMap.keySet())
			{
				final Integer value = castForLevelMap.get(key);
				if (value != null)
				{
					if (value > 0 && key > currHighest)
					{
						currHighest = key;
					}
				}
			}
		}
		return currHighest;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/*
	 * FUTUREREFACTOR This would really be nice to have initilized when the LST files
	 * are read in, which is possible because the ClassTypes are all defined as part
	 * of the GameMode... however the problem is that the order of the ISMONSTER tag
	 * and the TYPE tags cannot be defined - .MODs and .COPYs make it impossible to 
	 * guarantee an order.  Therefore, this must wait for a two-pass design in the
	 * import system - thpr 10/4/06
	 */
	public boolean isMonster()
	{
		Boolean mon = get(ObjectKey.IS_MONSTER);
		if (mon != null)
		{
			return mon.booleanValue();
		}

		if (getMyTypeCount() == 0)
		{
			return false;
		}

		for (String type : getTypeList(false))
		{
			final ClassType aClassType =
					SettingsHandler.getGame().getClassTypeByName(type);

			if ((aClassType != null) && aClassType.isMonster())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * get the Natural Attacks for this level
	 * 
	 * @return natural weapons list
	 */
	/*
	 * FINALPCCLASSLEVELONLY This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	/*
	 * CONSIDER The fact that this does NOT override PObject would break
	 * EditorMainForm, if EditorMainForm was smart enough to realize that a
	 * PCClass can have natural weapons. Currently it does not. It would also
	 * need to be smart enough to deal with level dependencies of Natural
	 * Weapons in PCClasses, so that is non-trivial. This issue is trackered as
	 * 1590553
	 */
	public List<Equipment> getNaturalWeapons(int aLevel)
	{
		if (naturalWeapons == null)
		{
			return new ArrayList<Equipment>();
		}

		final List<Equipment> tempArray = new ArrayList<Equipment>();

		for (LevelProperty<Equipment> lp : naturalWeapons)
		{
			if (lp.getLevel() == level)
			{
				tempArray.add(lp.getObject());
			}
		}

		return tempArray;
	}

	/*
	 * FINALPCCLASSONLY For editing PCClasses
	 */
	public List<LevelProperty<Equipment>> getAllNaturalWeapons()
	{
		if (naturalWeapons == null)
		{
			return new ArrayList<LevelProperty<Equipment>>();
		}
		return naturalWeapons;
	}

	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see pcgen.core.PObject#getWeaponProfAutos()
	//	 */
	//	public List<String> getWeaponProfAutos() {
	//		// first build up the list of the standard auto weapon profs
	//		final List<String> list = super.getWeaponProfAutos();
	//
	//		// then add in the proficiencies for each natural weapon
	//		// we have active.
	//		if (naturalWeapons != null) {
	//			for (Iterator<LevelProperty> li = naturalWeapons.iterator(); li
	//					.hasNext();) {
	//				final LevelProperty lp = li.next();
	//				if (lp.getLevel() <= level) {
	//					final Equipment weapon = (Equipment) lp.getObject();
	//					list.add(weapon.getSimpleName());
	//				}
	//			}
	//		}
	//		return list;
	//	}

	/*
	 * TYPESAFETY This needs to be checking vs. a RaceType TypesafeEnumeration,
	 * not a general String
	 */
	/*
	 * REFACTOR How exactly does this work in the new PCClass is a PCClassLevel
	 * Factory model? Does this exist as a method in PCClass that should be
	 * called before getLevel() is used (and before the user tries to add the
	 * level to PlayerCharacter?) or is this a check that the level performs 
	 * when the code attempts to add it to the PC?
	 */
	public boolean isQualified(final PlayerCharacter aPC)
	{

		if (aPC == null)
		{
			return false;
		}

		// if (isMonster() && (preRaceType != null) &&
		// !contains(aPC.getCritterType(), preRaceType))
		if (isMonster()
			&& (preRaceType != null)
			&& (!preRaceType.equals(aPC.getRace().get(ObjectKey.RACETYPE)) && !(aPC
				.getCritterType().indexOf(preRaceType.toString()) >= 0)))
		// Move the check for type out of race and into PlayerCharacter to make
		// it easier for a template to adjust it.
		{
			return false;
		}

		if (!PrereqHandler.passesAll(getPreReqList(), aPC, this))
		{
			return false;
		}

		return true;
	}

	/**
	 * should be "5|4/-" where 5 = level, 4/- is the SR value.
	 * 
	 * @param srString
	 */
	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	@Override
	public void setSR(int aLevel, String srString)
	{
		if (SR == null)
		{
			SR = new ArrayList<LevelProperty<String>>();
		}
		SR.add(LevelProperty.getLevelProperty(aLevel, srString));
	}

	/*
	 * FINALPCCLASSONLY Since this is part of LST file import
	 */
	public void clearSR()
	{
		SR = null;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is required to fetch the SR
	 * 
	 * UNWIND the level checking will have to be unwound into the users of this
	 * class, as these SRs will not pass from one PCClassLevel to another unless
	 * they are specified...
	 */
	@Override
	protected int getSR(PlayerCharacter aPC)
	{
		if (aPC == null)
		{
			return 0;
		}

		LevelProperty<String> activeLP = null;

		if (SR != null)
		{
			final int lvl = level;
			for (LevelProperty<String> lp : SR)
			{
				if (lp.getLevel() > lvl)
				{
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel())
				{
					activeLP = lp;
					continue;
				}
			}
		}

		//if there's a current PC, go ahead and evaluate the formula
		if (activeLP != null)
		{
			return aPC
				.getVariableValue(activeLP.getObject(), getQualifiedKey())
				.intValue();
		}

		return 0;

	}

	/*
	 * FINALPCCLASSONLY This is for building a PCClass
	 */
	public List<LevelProperty<String>> getSRlist()
	{
		if (SR == null)
		{
			/*
			 * CONSIDER This is a heavy-weight get... and inconsistent with
			 * getFeatList, et al. What should be the proper method (should the
			 * caller be required to gate on null, should these be safe gets,
			 * should the variables be initialized empty lists??) - thpr
			 * 11/10/06
			 */
			SR = new ArrayList<LevelProperty<String>>();
		}
		return SR;
	}

	/*
	 * FINALPCCLASSONLY This is for editing classes
	 */
	public LevelProperty<String> getSRforLevel(int aLevel)
	{
		if (SR != null)
		{
			for (LevelProperty<String> lp : SR)
			{
				if (lp.getLevel() == aLevel)
				{
					return lp;
				}
			}
		}
		return null;
	}

	@Override
	public String getPCCText()
	{
		final StringBuffer pccTxt = new StringBuffer(200);
		pccTxt.append("CLASS:").append(getDisplayName());
		pccTxt.append(super.getPCCText(false));
		checkAdd(pccTxt, "", "EXCLASS:", exClass);

		checkAdd(pccTxt, "", "EXCHANGELEVEL:", levelExchange);

		if (hasSubClass)
		{
			pccTxt.append("\tHASSUBCLASS:Y");
		}
		if (hasSubstitutionClass)
		{
			pccTxt.append("\tHASSUBSTITUTIONLEVEL:Y");
		}

		if (prohibitedSchools != null)
		{
			pccTxt.append('\t').append("PROHIBITED:");
			pccTxt.append(StringUtil.join(prohibitedSchools, ","));
		}

		if (castInfo != null)
		{
			checkAdd(pccTxt, Constants.s_NONE, "SPELLTYPE:", castInfo
				.getSpellType());
		}

		if (itemCreationMultiplier.length() != 0)
		{
			pccTxt.append("\tITEMCREATE:").append(itemCreationMultiplier);
		}

		// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		String regionString = getRegionString();
		if ((regionString != null) && !regionString.startsWith("0|"))
		{
			final int x = regionString.indexOf('|');
			pccTxt.append(lineSep).append(regionString.substring(0, x)).append(
				"\tREGION:").append(regionString.substring(x + 1));
		}

		// if (kitString != null && !kitString.startsWith("0|"))
		// {
		// int x = kitString.indexOf('|');
		// pccTxt.append(lineSep + kitString.substring(0,
		// x)).append("\tKIT:").append(kitString.substring(x + 1));
		// }
		List<String> kits = getSafeListFor(ListKey.KITS);
		for (int iKit = 0; iKit < kits.size(); ++iKit)
		{
			final String kitString = kits.get(iKit);
			final int x = kitString.indexOf('|');

			if (x >= 0)
			{
				pccTxt.append(lineSep + kitString.substring(0, x)).append(
					"\tKIT:").append(kitString.substring(x + 1));
			}
		}

		pccTxt.append('\t');
		pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
		{
			pccTxt.append(lineSep).append(me.getKey()).append('\t');
			pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext()
					.unparse(me.getValue()), "\t"));
		}

		// Output the level based DR only
		for (DamageReduction reduction : getDRList())
		{
			for (Prerequisite prereq : reduction.getPreReqList())
			{
				if (DamageReduction.isPrereqForClassLevel(prereq, getKeyName()))
				{
					pccTxt.append(lineSep).append(prereq.getOperand()).append(
						"\t").append(reduction.getPCCText(false));
				}
			}
		}

		if (SR != null)
		{
			for (LevelProperty<String> lp : SR)
			{
				pccTxt.append(lineSep).append(lp.getLevel()).append("\tSR:")
					.append(lp.getObject());
			}
		}

		// Output the list of spells associated with the class.
		int cap = getSpellSupport().getMaxSpellListLevel();
		if (hasMaxLevel() && cap > getSafe(IntegerKey.LEVEL_LIMIT))
		{
			cap = getSafe(IntegerKey.LEVEL_LIMIT);
		}
		for (int i = 0; i <= cap; i++)
		{
			final List<PCSpell> spellList =
					getSpellSupport().getSpellListForLevel(i);

			if (spellList != null)
			{
				for (PCSpell spell : spellList)
				{
					pccTxt.append(lineSep).append(i).append("\tSPELLS:")
						.append(spell.getPCCText());
				}
			}

		}

		if (templates != null)
		{
			for (final LevelProperty<String> lp : templates)
			{
				pccTxt.append(lineSep).append(lp.getLevel());
				pccTxt.append("\tTEMPLATE:").append(lp.getObject());
			}
		}

		for (int x = 0; x < getBonusList().size(); ++x)
		{
			final BonusObj aBonus = getBonusList().get(x);
			String bonusString = aBonus.toString();
			final int levelEnd = bonusString.indexOf('|');
			final String maybeLevel = bonusString.substring(0, levelEnd);

			pccTxt.append(lineSep);

			if (CoreUtility.isIntegerString(maybeLevel))
			{
				pccTxt.append(maybeLevel);
				bonusString = bonusString.substring(levelEnd + 1);
			}
			else
			{
				pccTxt.append("0");
			}

			pccTxt.append("\tBONUS:").append(bonusString);
		}

		for (int x = 0; x < getVariableCount(); ++x)
		{
			final String c = getVariableDefinition(x);
			final int y = c.indexOf('|');
			pccTxt.append(lineSep).append(c.substring(0, y))
				.append("\tDEFINE:").append(c.substring(y + 1));
		}

		List<LevelAbility> levelAbilityList = getLevelAbilityList();
		if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
		{
			for (LevelAbility ability : levelAbilityList)
			{
				pccTxt.append(lineSep).append(String.valueOf(ability.level()))
					.append("\tADD:").append(ability.getTagData());
			}
		}

		final List<SpecialAbility> specialAbilityList =
				getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (SpecialAbility sa : specialAbilityList)
			{
				final String src = sa.getSASource();
				final String lev = src.substring(src.lastIndexOf('|') + 1);
				pccTxt.append(lineSep).append(lev).append("\tSAB:").append(
					sa.toString());
			}
		}

		List<SpecialAbility> saList = new ArrayList<SpecialAbility>();
		addSABToList(saList, null);
		for (SpecialAbility sa : saList)
		{
			final String src = sa.getSASource();
			final String lev = src.substring(src.lastIndexOf('|') + 1);
			pccTxt.append(lineSep).append(lev).append("\tSAB:").append(
				sa.toString());
		}

		// TODO - Add ABILITY tokens.

		List<String> udamList = getListFor(ListKey.UDAM);
		if ((udamList != null) && (udamList.size() != 0))
		{
			for (int x = 0; x < udamList.size(); ++x)
			{
				String udamItem = udamList.get(x);
				if (udamItem != null)
				{
					pccTxt.append(lineSep).append(String.valueOf(x)).append(
					"\tUDAM:").append(udamList.get(x));
				}
			}
		}

		List<String> umultList = getListFor(ListKey.UMULT);
		if (umultList != null)
		{
			for (String st : umultList)
			{
				final int sepPos = st.indexOf("|");
				pccTxt.append(lineSep).append(st.substring(0, sepPos)).append(
					"\tUMULT:").append(st.substring(sepPos + 1));
			}
		}

		return pccTxt.toString();
	}

	/*
	 * PCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public List<Ability> getVirtualFeatList(final int aLevel)
	{
		final List<Ability> aList = new ArrayList<Ability>();

		if (vFeatList != null)
		{
			for (LevelProperty<List<Ability>> lp : vFeatList)
			{
				if (lp.getLevel() <= aLevel)
				{
					aList.addAll(lp.getObject());
				}
			}
		}

		return aList;
	}

	/**
	 * Here is where we do the real work of setting the vision information on
	 * the PObject
	 * 
	 * Must Override to fix 1489300
	 * 
	 * @param aPC
	 * @return Map
	 */
	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory) [with level dependent
	 * differences, of course]
	 */
	/*
	 * UNWIND I'm not going to unwind this one early, because PC.getVisionList 
	 * actually depends on this for a bug fix - this will 'naturally' be fixed 
	 * when PCClassLevel is broken out because it is used in few places.
	 */
	@Override
	public List<Vision> getVision()
	{
		List<Vision> returnList = super.getVision();
		if (visionList != null)
		{
			if (returnList == null)
			{
				returnList = new ArrayList<Vision>();
			}
			for (LevelProperty<Vision> vis : visionList)
			{
				if (vis.getLevel() <= level)
				{
					returnList.add(vis.getObject());
				}
			}
		}

		return returnList;
	}

	/**
	 * Sets qualified BonusObj's to "active"
	 * 
	 * @param aPC
	 */
	/*
	 * DELETEMETHOD Because this appears to be simply a correction for PCLevel
	 * (above and beyond what PObject's activateBonuses method does), this 
	 * becomes useless in the new architecture, as the bonuses will only be 
	 * present and visible to the PlayerCharacter in the PCClassLevels that the
	 * PlayerCharacter has.
	 * 
	 * The hasPreReqs test here which is not in PObject is simply a shortcut
	 * of what is already done in bonus.qualifies, so the operation of this
	 * (while looking more complicated) really only differs from PObject in 
	 * the level dependence
	 */
	@Override
	public void activateBonuses(final PlayerCharacter aPC)
	{
		for (BonusObj bonus : getBonusList())
		{
			if ((bonus.getPCLevel() <= level))
			{
				if (bonus.hasPreReqs())
				{
					// TODO: This is a hack to avoid VARs etc in class defs
					// being qualified for when Bypass class prereqs is
					// selected.
					// Should we be passing in the BonusObj here to allow it to
					// be referenced in Qualifies statements?
					if (bonus.qualifies(aPC))
					{
						bonus.setApplied(true);
					}
					else
					{
						bonus.setApplied(false);
					}
				}
				else
				{
					bonus.setApplied(true);
				}
			}
		}
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSkill(ClassSkillList csl)
	{
		if (classSkillList == null)
		{
			classSkillList = new ArrayList<ClassSkillList>();
		}
		classSkillList.add(csl);
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void clearClassSkillList()
	{
		classSkillList = null;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSpellList(CDOMListObject<Spell> list)
	{
		if (classSpellList == null)
		{
			classSpellList = new ArrayList<CDOMListObject<Spell>>();
		}
		classSpellList.add(list);
		/*
		 * CONSIDER I have taken out classSpellString = null; which is now the
		 * equivalent of classSpellChoices = null; ... I don't understand why in
		 * this unique situation of Player Character Import that resetting this
		 * produces better behavior than adding a class (which doesn't delete
		 * the list). Seems to me a case of unnecessary (and confusing)
		 * deletion... - thpr 10/29/06
		 */
		/*
		 * CONSIDER This is a confusing side-effect - this really needs to be
		 * identified better as a cached variable (transient) so that it is
		 * recognized that this is simply making a 'cache' dirty - thpr 11/4/06
		 */
		stableSpellKey = null;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void clearClassSpellList()
	{
		classSpellList = null;
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final void addSubClass(final SubClass sClass)
	{
		if (subClassList == null)
		{
			subClassList = new ArrayList<SubClass>();
		}

		sClass.setHitPointMap(hitPointMap);
		sClass.put(ObjectKey.LEVEL_HITDIE, get(ObjectKey.LEVEL_HITDIE));
		subClassList.add(sClass);
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final void addSubstitutionClass(final SubstitutionClass sClass)
	{
		if (substitutionClassList == null)
		{
			substitutionClassList = new ArrayList<SubstitutionClass>();
		}

		sClass.setHitPointMap(hitPointMap);
		sClass.put(ObjectKey.LEVEL_HITDIE, get(ObjectKey.LEVEL_HITDIE));
		substitutionClassList.add(sClass);
	}

	private SpellProgressionInfo getConstructingSpellProgressionInfo()
	{
		if (castInfo == null)
		{
			castInfo = new SpellProgressionInfo();
		}
		return castInfo;
	}

	/**
	 * Add a level of this class to the character. Note this call is assumed to
	 * only be used when loading characters, and some behaviour is tailored for
	 * this.
	 * 
	 * @param pcLevelInfo
	 * 
	 * @param levelMax
	 *            True if the level caps, if any, for this class should be
	 *            respected.
	 * @param aPC
	 *            The character having the level added.
	 */
	/*
	 * DELETEMETHOD This really becomes a factory call to PCClass to get an
	 * appropriate PCClassLevel to be added to the PlayerCharacter
	 */
	public void addLevel(final PCLevelInfo pcLevelInfo, final boolean levelMax,
		final PlayerCharacter aPC)
	{
		addLevel(pcLevelInfo, levelMax, false, aPC, true);
	}

	/*
	 * PCCLASSLEVELONLY Since this is a selection made during levelup (from
	 * a LevelAbilityClassSkill) this is only required in PCClassLevel
	 */
	public void addSkillToList(final String aString)
	{
		if (skillList == null)
		{
			skillList = new ArrayList<String>();
		}
		if (!skillList.contains(aString))
		{
			skillList.add(aString);
		}
	}

	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addTemplate(int lvl, final String template)
	{
		if (templates == null)
		{
			templates = new ArrayList<LevelProperty<String>>();
		}
		templates.add(LevelProperty.getLevelProperty(lvl, template));
	}

	/**
	 * Adds virtual feats to the vFeatList
	 * 
	 * @param aLevel
	 *            level
	 * @param vList
	 *            list of feats
	 */
	/*
	 * PCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method
	 */
	public void addVirtualFeats(final int aLevel, final List<Ability> vList)
	{
		if (vFeatList == null)
		{
			vFeatList = new ArrayList<LevelProperty<List<Ability>>>();
		}
		boolean found = false;
		for (LevelProperty<List<Ability>> lp : vFeatList)
		{
			if (lp.getLevel() == aLevel)
			{
				found = true;
				lp.getObject().addAll(vList);
			}
		}
		if (!found)
		{
			List<Ability> arrayList = new ArrayList<Ability>(vList);
			vFeatList.add(LevelProperty.getLevelProperty(aLevel, arrayList));
		}

		super.addVirtualFeats(vList);
	}

	/**
	 * returns the value at which another attack is gained attackCycle of 4
	 * means a second attack is gained at a BAB of +5/+1
	 * 
	 * @param index
	 * @return int
	 */
	/*
	 * PCCLASSANDLEVEL Some derivative of this will likely need to exist in both
	 * PCClass (since it's a tag) and PCClassLevel (since there will have to be
	 * some method of detecting what the BAB of a given PCClassLevel is and then
	 * grouping those in the proper groups (see
	 * PlayerCharacter.getAttackString()) to determine what the final attack
	 * bonuses are.
	 */
	public int attackCycle(final AttackType at)
	{
		for (AttackCycle ac : getSafeListFor(ListKey.ATTACK_CYCLE))
		{
			if (at.equals(ac.getAttackType()))
			{
				return ac.getValue();
			}
		}
		return SettingsHandler.getGame().getBabAttCyc();
	}

	public int baseAttackBonus(final PlayerCharacter aPC)
	{
		if (level == 0)
		{
			return 0;
		}

		// final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int)
		// getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB", level, aPC);

		return i;
	}

	/**
	 * -2 means that the spell itself indicates what stat should be used,
	 * otherwise this method returns an index into the global list of stats for
	 * which stat the bonus spells are based upon.
	 * 
	 * @return int Index of the class' spell stat, or -2 if spell based
	 */
	/*
	 * REFACTOR Why is this returning an INT and not a PCStat or something like
	 * that? or why is the user not just using getSpellBaseStat and processing
	 * the response by itself??
	 */
	public int baseSpellIndex()
	{
		if (getSafe(ObjectKey.USE_SPELL_SPELL_STAT))
		{
			return -2;
		}
		if (getSafe(ObjectKey.CASTER_WITHOUT_SPELL_STAT))
		{
			return -1;
		}
		PCStat ss = get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
			return SettingsHandler.getGame().getStatFromAbbrev(ss.getAbb());
		}
		Logging.debugPrint("Found Class: " + getDisplayName()
				+ " that did not have any SPELLSTAT defined");
		return -1;
	}

	/**
	 * Returns the index of the stat to use for bonus spells.
	 * 
	 * <p>
	 * The method checks to see if a BONUSSPELLSTAT: has been set for the class.
	 * If it is set to a stat that stat is returned. If it is set to None -1 is
	 * returned. If it is set to Default then the BASESPELLSTAT is returned.
	 * 
	 * @return An index into the stat array or -1 if no bonus spells should be
	 *         granted for this class.
	 * 
	 * TODO - Why doesn't this return a PCStat?
	 */
	/*
	 * REFACTOR Why is this returning an INT and not a PCStat or something like
	 * that? or why is the user not just using getBonusSpellBaseStat and
	 * processing the response by itself??
	 */
	public int bonusSpellIndex()
	{
		Boolean hbss = get(ObjectKey.HAS_BONUS_SPELL_STAT);
		if (hbss == null)
		{
			return baseSpellIndex();
		}
		else if (hbss)
		{
			PCStat bss = get(ObjectKey.BONUS_SPELL_STAT);
			return SettingsHandler.getGame().getStatFromAbbrev(bss.getAbb());
		}
		else
		{
			return -1;
		}
	}

	/*
	 * FORMULAREFACTOR This situation may no longer need a formula to calculate
	 * the Challenge Rating of the PCClass, due to the fact that the challenge
	 * rating may be level based. That is not to say it won't be required, but
	 * may be avoided?? Not necessarily, because a ClassType of NPC still uses a
	 * Formula that can't be resolved.
	 */
	/*
	 * REFACTOR I don't like the fact that this method is accessing the
	 * ClassTypes and using one of those to set one of its variables. Should
	 * this be done when a PCClassLevel is built? Is that possible?
	 */
	public float calcCR(final PlayerCharacter aPC)
	{
		Formula cr = get(FormulaKey.CR);
		if (cr == null)
		{
			for (String type : getTypeList(false))
			{
				final ClassType aClassType =
						SettingsHandler.getGame().getClassTypeByName(type);
				if (aClassType != null)
				{
					String crf = aClassType.getCRFormula();
					if (!"0".equals(crf))
					{
						cr = FormulaFactory.getFormulaFor(crf);
					}
				}
			}
		}

		return cr == null ? 0 : cr.resolve(aPC, classKey).floatValue();
	}

	/*
	 * PCCLASSLEVELONLY Since this is level dependent it only makes sense there.
	 */
	public String classLevelString()
	{
		StringBuffer aString = new StringBuffer();

		if (!getSubClassKey().equals(Constants.s_NONE)
			&& !"".equals(getSubClassKey()))
		{
			aString.append(getSubClassKey());
		}
		else
		{
			aString.append(getKeyName());
		}

		aString = aString.append(' ').append(level);

		return aString.toString();
	}

	@Override
	public PCClass clone()
	{
		PCClass aClass = null;

		try
		{
			aClass = (PCClass) super.clone();
			aClass.setSubClassKey(getSubClassKey());

			if (prohibitedSchools != null)
			{
				aClass.prohibitedSchools =
						new ArrayList<String>(prohibitedSchools);
			}
			if (castInfo != null)
			{
				aClass.castInfo = castInfo.clone();
			}
			spellCache = null;
			spellCacheValid = false;
			if (vFeatList != null)
			{
				//I guess a shallow clone is OK???? already was that way ... - thpr 11/2/06
				aClass.vFeatList =
						new ArrayList<LevelProperty<List<Ability>>>(vFeatList);
			}
			if (vAbilityMap != null)
			{
				aClass.vAbilityMap =
						new DoubleKeyMap<AbilityCategory, Integer, List<Ability>>(
							vAbilityMap);
			}
			//			if ( theAutoAbilities != null )
			//			{
			//				aClass.theAutoAbilities = new DoubleKeyMap<AbilityCategory, Integer, List<String>>(theAutoAbilities);
			//			}
			// TODO - Why is this not copying the skillList from the master?
			aClass.skillList = null;

			aClass.classSkillList = null;
			aClass.classSpellList = null;
			aClass.stableSpellKey = null;

			aClass.setLevelExchange(levelExchange);

			List<KnownSpellIdentifier> ksl = getListFor(ListKey.KNOWN_SPELLS);
			if (ksl != null)
			{
				aClass.removeListFor(ListKey.KNOWN_SPELLS);
				for (KnownSpellIdentifier ksi : ksl)
				{
					aClass.addToListFor(ListKey.KNOWN_SPELLS, ksi);
				}
			}
			List<AttackCycle> acList = getListFor(ListKey.ATTACK_CYCLE);
			if (acList != null)
			{
				aClass.removeListFor(ListKey.ATTACK_CYCLE);
				for (AttackCycle ac : acList)
				{
					aClass.addToListFor(ListKey.ATTACK_CYCLE, ac);
				}
			}

			if (hitPointMap != null)
			{
				aClass.hitPointMap = new HashMap<Integer, Integer>(hitPointMap);
			}
			aClass.substitutionClassList = substitutionClassList;

			if (naturalWeapons != null)
			{
				aClass.naturalWeapons =
						new ArrayList<LevelProperty<Equipment>>(naturalWeapons);
			}
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
				Constants.s_APPNAME, MessageType.ERROR);
		}

		return aClass;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the subclass is activated, this is required in both locations.
	 */
	public final boolean hasSubClass()
	{
		return hasSubClass;
	}

	/*
	 * PCCLASSANDLEVEL Since this (or a new boolean identifier, perhaps, to
	 * avoid confusion) is both a tag and an identifier for each class level as
	 * to whether the substitution class is activated, this is required in both
	 * locations.
	 */
	public final boolean hasSubstitutionClass()
	{
		return hasSubstitutionClass;
	}

	/*
	 * PCCLASSANDLEVEL Since this is required in both places...
	 */
	@Override
	public final String toString()
	{
		return displayName;
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and PCClass, since it is a Tag
	 * 
	 * Need to look into the details of stableSpellKey to figure out the appropriate
	 * place for that
	 */
	@Override
	public void setName(final String newName)
	{
		super.setName(newName);
		/*
		 * CONSIDER This (below) is a bit of a confusing side effect - needs to be
		 * far more explicit that stableSpellKey is a cache, and that there are 
		 * also side effects to getSpellKey (side effects are bad :( ) - 
		 * thpr 11/4/06
		 */
		stableSpellKey = null;
		getSpellKey();
	}

	/*
	 * PCCLASSLEVELONLY since the specialty list is created during PCClassLevel
	 * creation (in the factory)
	 */
	public boolean isSpecialtySpell(final Spell aSpell)
	{
		if (hasSpecialty())
		{
			return aSpell.containsInList(ListKey.SPELL_SCHOOL, specialty)
					|| aSpell.containsInList(ListKey.SPELL_SUBSCHOOL, specialty)
					|| aSpell.containsInList(ListKey.SPELL_DESCRIPTOR, specialty);
		}
		return false;
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public List<SubClass> getSubClassList()
	{
		return subClassList;
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public List<SubstitutionClass> getSubstitutionClassList()
	{
		return substitutionClassList;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * 
	 * @param aBonus
	 * @param anObj
	 * @param aPC
	 * @return double
	 */
	@Override
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj,
		PlayerCharacter aPC)
	{
		double retVal = 0;
		int iTimes = 1;

		final String aType = aBonus.getTypeOfBonus();

		// String aName = aBonus.getBonusInfo();
		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			String choiceString = getChoiceString();
			if (choiceString.startsWith("SALIST|")
				&& (choiceString.indexOf("|VAR|") >= 0))
			{
				iTimes = 1;
			}
		}

		String bString = aBonus.toString();

		if (getAssociatedCount() != 0)
		{
			int span = 4;
			int idx = bString.indexOf("%VAR");

			if (idx == -1)
			{
				idx = bString.indexOf("%LIST");
				span = 5;
			}

			if (idx >= 0)
			{
				final String firstPart = bString.substring(0, idx);
				final String secondPart = bString.substring(idx + span);

				for (int i = 1; i < getAssociatedCount(); ++i)
				{
					final String xString =
							new StringBuffer().append(firstPart).append(
								getAssociated(i)).append(secondPart).toString();
					retVal += calcPartialBonus(xString, iTimes, aBonus, anObj);
				}

				bString =
						new StringBuffer().append(firstPart).append(
							getAssociated(0)).append(secondPart).toString();
			}
		}

		retVal += calcPartialBonus(bString, iTimes, aBonus, anObj);

		return retVal;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * 
	 * @param aBonus
	 * @param anObj
	 * @param listString
	 * @param aPC
	 * @return double
	 */
	@Override
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj,
		final String listString, PlayerCharacter aPC)
	{
		return calcBonusFrom(aBonus, anObj, aPC);
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public boolean hasClassSkill(final String aString)
	{
		if ((classSkillList == null) || classSkillList.isEmpty())
		{
			return false;
		}

		for (ClassSkillList key : classSkillList)
		{
			final PCClass pcClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, key.getLSTformat());

			if ((pcClass != null) && pcClass.hasCSkill(aString))
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * PCCLASSLEVELONLY? This really seems to be a shortcut test that is part of
	 * the GUI presentation of a PlayerCharacter. It would be really nice if
	 * this could be deleted, but it may actually be providing some speed (need
	 * to evaluate whether a better test exists, however, since it seems REALLY 
	 * slow for something that should be a quick test!)
	 */
	public boolean hasKnownSpells(final PlayerCharacter aPC)
	{
		for (int i = 0; i <= getHighestLevelSpell(); i++)
		{
			if (getKnownForLevel(i, aPC) > 0)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * <p>
	 * TODO - I am not sure this code is really working properly.
	 * 
	 * @param aString
	 * @return
	 */
	/*
	 * PCCLASSLEVELONLY Since this is a selection made during levelup (from
	 * a LevelAbilityClassSkill) this is only required in PCClassLevel
	 */
	public boolean hasSkill(final String aString)
	{
		if (skillList == null)
		{
			return false;
		}
		for (String key : skillList)
		{
			if (key.equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * PCCLASSLEVELONLY Since this is really only something that will be done
	 * within a PlayerCharacter (real processing) it is only required in
	 * PCClassLevel.
	 * 
	 * As a side note, I'm not sure what I think of accessing the ClassTypes and
	 * using one of those to set the response to this request. Should this be
	 * done when a PCClassLevel is built? Is that possible? How does that
	 * interact with a PlayerCharacter being reimported if those rules change?
	 */
	public boolean hasXPPenalty()
	{
		for (String type : getTypeList(false))
		{
			final ClassType aClassType =
					SettingsHandler.getGame().getClassTypeByName(type);
				if ((aClassType != null) && !aClassType.getXPPenalty())
			{
				return false;
			}
		}
		return true;
	}

	/*
	 * REFACTOR to DELETEMETHOD this really can't be anywhere in PCClass or
	 * PCClassLevel since this is acting across a group of PCClassLevels.
	 * Perhaps a Utility method is required to calculate this across a group of
	 * PCClassLevels?
	 */
	public int hitPoints(final int iConMod)
	{
		int total = 0;

		for (int i = 0; i <= getLevel(); ++i)
		{
			if (getHitPoint(i) > 0)
			{
				int iHp = getHitPoint(i) + iConMod;

				if (iHp < 1)
				{
					iHp = 1;
				}

				total += iHp;
			}
		}

		return total;
	}

	public int recalcSkillPointMod(final PlayerCharacter aPC, final int total)
	{
		final int spMod;

		if (isMonster() && aPC.isMonsterDefault())
		{
			spMod = getMonsterSkillPointMod(aPC, total);
		}
		else
		{
			spMod = getNonMonsterSkillPointMod(aPC, total);
		}
		return spMod;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill pool is
	 * calculated based on other factors, it is not a Tag
	 */
	public final int skillPool()
	{
		return skillPool;
	}

	/*
	 * PCCLASSLEVELONLY This is only part of the level, as the skill pool is
	 * calculated based on other factors, it is not a Tag
	 */
	public void setSkillPool(final int i)
	{
		skillPool = i;
	}

	/*
	 * REFACTOR TO DELETEMETHOD I would really like to get rid of this, since it
	 * it used as a "funky spells" test - which should be more explicit than
	 * implicit in zero cast spells.
	 * 
	 * Not to mention, this isn't entirely true, is it? I mean, if the only
	 * spells a Class can get are from the ability score related to their
	 * spells, then the book system would use a 0 rather than a -. This 0
	 * actually contains information which indicates the system may not be zero
	 * after all...
	 */
	public boolean zeroCastSpells()
	{
		if (!updateSpellCache(false) || !spellCache.hasCastProgression())
		{
			return true;
		}
		/*
		 * CONSIDER This is just blatently wrong because it is not considering
		 * formulas, and not considering bonuses... - thpr 11/8/06
		 *
		 * May not be a big issue other than a poorly named method, but
		 * need to check what is really required here
		 */
		for (List<Formula> l : spellCache.getCastProgression().values())
		{
			for (Formula st : l)
			{
				try
				{
					if (Integer.parseInt(st.toString()) > 0)
					{
						return false;
					}
				}
				catch (NumberFormatException nfe)
				{
					// ignore
				}
			}
		}

		return true;
	}

	/*
	 * REFACTOR TO DELETEMETHOD I don't understand why the .CLEAR related
	 * functionality only exists in PCClass and not other PObjects??? Perhaps
	 * this should be up in PObject and therefore not be necessary here?... No,
	 * I suspect this is level related based on how SpecialAbilitys store their
	 * source and then use that to check if the PC qualifies for the Special
	 * Ability. Hopefully that String processing can be factored out and this
	 * can only load the appropriate SpecialAbilitys into the PCClassLevels that
	 * a PlayerCharacter has.
	 */
	@Override
	public List<SpecialAbility> addSpecialAbilitiesToList(
		final List<SpecialAbility> aList, final PlayerCharacter aPC)
	{
		final List<SpecialAbility> specialAbilityList =
				getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList == null) || specialAbilityList.isEmpty())
		{
			return aList;
		}

		final List<SpecialAbility> bList = new ArrayList<SpecialAbility>();

		for (SpecialAbility sa : specialAbilityList)
		{
			if (sa.pcQualifiesFor(aPC))
			{
				final String saKey = sa.getKeyName();
				if (saKey.startsWith(".CLEAR"))
				{
					if (".CLEARALL".equals(saKey))
					{
						bList.clear();
					}
					else if (saKey.startsWith(".CLEAR."))
					{
						final String saToRemove = saKey.substring(7);

						for (int itIdx = bList.size() - 1; itIdx >= 0; --itIdx)
						{
							final String subKey = bList.get(itIdx).getKeyName();

							if (subKey.equals(saToRemove))
							{
								bList.remove(itIdx);
							}
							else if (subKey.indexOf('(') >= 0)
							{
								if (subKey.substring(0, subKey.indexOf('('))
									.trim().equals(saToRemove))
								{
									bList.remove(itIdx);
								}
							}
						}
					}
					//CONSIDER else what?  No error checking here?

					continue;
				}

				bList.add(sa);
			}
		}

		aList.addAll(bList);

		return aList;
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	final List<ClassSkillList> getClassSkillList()
	{
		if (classSkillList == null)
		{
			List<ClassSkillList> returnList = new ArrayList<ClassSkillList>(2);
			ReferenceContext ref = Globals.getContext().ref;
			Class<ClassSkillList> cl = ClassSkillList.class;
			ClassSkillList l = ref.silentlyGetConstructedCDOMObject(cl, getKeyName());
			if (l != null)
			{
				returnList.add(l);
			}
			if (subClassKey != null)
			{
				l = ref.silentlyGetConstructedCDOMObject(cl, subClassKey);
				if (l != null)
				{
					returnList.add(l);
				}
			}
			return returnList;
		}
		else
		{
			return classSkillList;
		}
	}

	/**
	 * Return number of spells known for a level for a given spellbook.
	 * 
	 * @param pcLevel
	 * @param spellLevel
	 * @param bookName
	 * @param aPC
	 * @return known for spell level
	 */
	int getKnownForLevel(final int spellLevel, final String bookName,
		final PlayerCharacter aPC)
	{
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		int pcLevel = getLevel();
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel +=
				(int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		/*
		 * CONSIDER Why is known testing getNumFromCastList??? - thpr 11/8/06
		 */
		if (updateSpellCache(false) && spellCache.hasCastProgression()
			&& (getNumFromCastList(pcLevel, spellLevel, aPC) < 0))
		{
			// Don't know any spells of this level
			// however, character might have a bonus spells e.g. from certain
			// feats
			return (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
				+ levelSpellLevel);
		}

		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
					+ levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE."
					+ getSpellType() + levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any"
					+ levelSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName
					+ allSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE."
					+ getSpellType() + allSpellLevel);
		total +=
				(int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any"
					+ allSpellLevel);

		final int index = baseSpellIndex();

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().size()))
		{
			final PCStat aStat = aPC.getStatList().getStatAt(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0)
		{
			statString = SettingsHandler.getGame().s_ATTRIBSHORT[index];
		}

		final int bonusStat =
				(int) aPC.getTotalBonusTo("STAT", "KNOWN." + statString)
					+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT")
					+ (int) aPC.getTotalBonusTo("STAT",
						"BASESPELLKNOWNSTAT;CLASS." + keyName);

		if (index > -2)
		{
			final int maxSpellLevel =
					aPC.getVariableValue("MAXLEVELSTAT=" + statString, "")
						.intValue();

			if ((maxSpellLevel + bonusStat) < spellLevel)
			{
				return total;
			}
		}

		stat += bonusStat;

		int mult =
				(int) aPC.getTotalBonusTo("SPELLKNOWNMULT", classKeyName
					+ levelSpellLevel);
		mult +=
				(int) aPC.getTotalBonusTo("SPELLKNOWNMULT", "TYPE."
					+ getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		if (!updateSpellCache(false))
		{
			return total;
		}

		if (spellCache.hasKnownProgression())
		{
			List<Formula> knownList = spellCache.getKnownForLevel(pcLevel);
			if (spellLevel >= 0 && spellLevel < knownList.size())
			{
				total += mult * knownList.get(spellLevel).resolve(aPC, "").intValue();

				// add Stat based bonus
				final String bonusSpell =
						Globals.getBonusSpellMap().get(
							String.valueOf(spellLevel));

				if (Globals.checkRule(RuleConstants.BONUSSPELLKNOWN)
					&& (bonusSpell != null) && !bonusSpell.equals("0|0"))
				{
					final StringTokenizer s =
							new StringTokenizer(bonusSpell, "|");
					final int base = Integer.parseInt(s.nextToken());
					final int range = Integer.parseInt(s.nextToken());

					if (stat >= base)
					{
						total += Math.max(0, (stat - base + range) / range);
					}
				}
			}
		}

		// if we have known spells (0==no known spells recorded)
		// or a psi specialty.
		if (total > 0 && spellLevel > 0)
		{
			// make sure any slots due from specialties
			// (including domains) are added
			total += getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY);
		}

		return total;
	}

	/*
	 * REFACTOR Exactly where does this end up? I think that passing a
	 * PlayerCharacter into an object like PCClass is generally (but certainly
	 * not always) bad form. In this case, the PC is present in order to test
	 * prerequisites, so perhaps this is an OK use of passing in
	 * PlayerCharacter... (double dispatch)
	 */
	public boolean isProhibited(final Spell aSpell, final PlayerCharacter aPC)
	{
		if (!PrereqHandler.passesAll(aSpell.getPreReqList(), aPC, aSpell))
		{
			return true;
		}

		for (SpellProhibitor prohibit : getSafeListFor(ListKey.SPELL_PROHIBITOR))
		{
			if (prohibit.isProhibited(aSpell, aPC))
			{
				return true;
			}
		}

		if (prohibitedSchools != null)
		{
			for (String school : prohibitedSchools)
			{
				if (aSpell.containsInList(ListKey.SPELL_SCHOOL, school)
					|| aSpell.containsInList(ListKey.SPELL_SUBSCHOOL, school))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get the unarmed Damage for this class at the given level.
	 * 
	 * @param aLevel
	 * @param includeCrit
	 * @param includeStrBonus
	 * @param aPC
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	/*
	 * REFACTOR There is redundant information being sent in here (level 
	 * and PlayerCharacter).
	 */
	/*
	 * PCCLASSLEVELONLY Since this is a level dependent calculation, this should
	 * be performed by the PCClassLevel.
	 */
	String getUdamForLevel(int aLevel, final boolean includeCrit,
		final boolean includeStrBonus, final PlayerCharacter aPC,
		boolean adjustForPCSize)
	{
		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		aLevel += (int) aPC.getTotalBonusTo("UDAM", "CLASS." + keyName);

		final Equipment eq =
			Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Equipment.class, "KEY_Unarmed Strike");

		if (eq != null)
		{
			aDamage = eq.getDamage(aPC);
		}
		else
		{
			aDamage = "1d3";
		}

		// resize the damage as if it were a weapon
		int iSize = Globals.sizeInt(aPC.getSize());

		if (adjustForPCSize)
		{
			aDamage =
					Globals.adjustDamage(aDamage, SettingsHandler.getGame()
						.getDefaultSizeAdjustment().getAbbreviation(),
						SettingsHandler.getGame().getSizeAdjustmentAtIndex(
							iSize).getAbbreviation());
		}

		//
		// Check the UDAM list for monk-like damage
		//
		List<String> udamList =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, keyName).getListFor(ListKey.UDAM);

		if ((udamList != null) && !udamList.isEmpty())
		{
			if (udamList.size() == 1)
			{
				final String aString = udamList.get(0);

				if (aString.startsWith("CLASS=")
					|| aString.startsWith("CLASS."))
				{
					final PCClass aClass =
							Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, aString.substring(6));

					if (aClass != null)
					{
						return aClass.getUdamForLevel(aLevel, includeCrit,
							includeStrBonus, aPC, adjustForPCSize);
					}

					Logging.errorPrint(keyName + " refers to "
						+ aString.substring(6) + " which isn't loaded.");

					return aDamage;
				}
			}

			final StringTokenizer aTok = new StringTokenizer(udamList.get(Math
					.min(Math.max(aLevel, 0), udamList.size() - 1)), ",", false);

			while ((iSize > -1) && aTok.hasMoreTokens())
			{
				aDamage = aTok.nextToken();

				if (iSize == 0)
				{
					break;
				}

				iSize -= 1;
			}
		}

		final StringBuffer aString = new StringBuffer(aDamage);
		int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE.MELEE");
		b += (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");

		if (includeStrBonus && (b > 0))
		{
			aString.append('+');
		}

		if (includeStrBonus && (b != 0))
		{
			aString.append(String.valueOf(b));
		}

		if (includeCrit)
		{
			final String dString = getUMultForLevel(aLevel);

			if (!"0".equals(dString))
			{
				aString.append("(x").append(dString).append(')');
			}
		}

		return aString.toString();
	}

	/**
	 * Adds a level of this class to the character.
	 * 
	 * TODO: Split the PlayerCharacter code out of PCClass (i.e. the level
	 * property). Then have a joining class assigned to PlayerCharacter that
	 * maps PCClass and number of levels in the class.
	 * 
	 * @param pcLevelInfo
	 * 
	 * @param argLevelMax
	 *            True if we should only allow extra levels if there are still
	 *            levels in this class to take. (i.e. a lot of prestige classes
	 *            stop at level 10, so if this is true it would not allow an
	 *            11th level of the class to be added
	 * @param bSilent
	 *            True if we are not to show any dialog boxes about errors or
	 *            questions.
	 * @param aPC
	 *            The character we are adding the level to.
	 * @param ignorePrereqs
	 *            True if prereqs for the level should be ignored. Used in 
	 *            situations such as when the character is being loaded.
	 * @return true or false
	 */
	/*
	 * REFACTOR Clearly this is part of the PCClass factory method that produces
	 * PCClassLevels combined with some other work that will need to be done to
	 * extract some of the complicated gunk out of here that goes out and puts
	 * information into PCLevelInfo and PlayerCharacter.
	 */
	boolean addLevel(final PCLevelInfo pcLevelInfo, final boolean argLevelMax,
		final boolean bSilent, final PlayerCharacter aPC,
		final boolean ignorePrereqs)
	{

		// Check to see if we can add a level of this class to the
		// current character
		final int newLevel = level + 1;
		boolean levelMax = argLevelMax;

		level += 1;
		if (!ignorePrereqs)
		{
			// When loading a character, classes are added before feats, so
			// this test would always fail on loading if feats are required
			boolean doReturn = false;
			if (!PrereqHandler.passesAll(getPreReqList(), aPC, this))
			{
				doReturn = true;
				if (!bSilent)
				{
					ShowMessageDelegate.showMessageDialog(
						"This character does not qualify for level " + level,
						Constants.s_APPNAME, MessageType.ERROR);
				}
			}
			level -= 1;
			if (doReturn)
			{
				return false;
			}
		}

		if (isMonster())
		{
			levelMax = false;
		}

		if (hasMaxLevel() && (newLevel > getSafe(IntegerKey.LEVEL_LIMIT)) && levelMax)
		{
			if (!bSilent)
			{
				ShowMessageDelegate.showMessageDialog(
					"This class cannot be raised above level "
						+ Integer.toString(getSafe(IntegerKey.LEVEL_LIMIT)), Constants.s_APPNAME,
					MessageType.ERROR);
			}

			return false;
		}

		// Add the level to the current character
		int total = aPC.getTotalLevels();

		// No longer need this since the race now sets a bonus itself and Templates
		// are not able to reassign their feats.  There was nothing else returned in
		// this number
		//		if (total == 0) {
		//			aPC.setFeats(aPC.getInitialFeats());
		//		}
		setLevel(newLevel, aPC);

		// the level has now been added to the character,
		// so now assign the attributes of this class level to the
		// character...
		for (final String template : getTemplates(aPC.isImporting(), aPC))
		{
			aPC.addTemplateKeyed(template);
		}

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		final int dnum =
				aPC.getMaxCharacterDomains(this, aPC)
					- aPC.getCharacterDomainUsed();

		if (!aPC.hasDomainSource("PCClass", getKeyName(), newLevel))
		{
			if (dnum > 0)
			{
				aPC.addDomainSource("PCClass", getKeyName(), newLevel, dnum);
			}
		}

		aPC.setAutomaticAbilitiesStable(null, false);
		//		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel, aPC, pcLevelInfo);
		aPC.addAddsFromAllObjForLevel();

		// Don't roll the hit points if the gui is not being used.
		// This is so GMGen can add classes to a person without pcgen flipping
		// out
		if (Globals.getUseGUI())
		{
			rollHP(aPC, level, (SettingsHandler.isHPMaxAtFirstClassLevel()
				? aPC.totalNonMonsterLevels() : aPC.getTotalLevels()) == 1);
		}

		if (!aPC.isImporting())
		{
			modDomainsForLevel(newLevel, true, aPC);
		}

		int levelUpStats = 0;

		// Add any bonus feats or stats that will be gained from this level
		// i.e. a bonus feat every 3 levels
		if (aPC.getTotalLevels() > total)
		{
			boolean processBonusStats = true;
			boolean processBonusFeats = true;
			total = aPC.getTotalLevels();

			if (isMonster())
			{
				// If we have less levels that the races monster levels
				// then we can not give a stat bonus (i.e. an Ogre has
				// 4 levels of Giant, so it does not get a stat increase at
				// 4th level because that is already taken into account in
				// its racial stat modifiers, but it will get one at 8th
				if (total <= aPC.getRace().getMonsterClassLevels(aPC))
				{
					processBonusStats = false;
				}

				/*
				 * If we are using default monsters and we have not yet added
				 * all of the racial monster levels then we can not add any
				 * feats. i.e. a default monster Ogre will not get a feat at 1st
				 * or 3rd level because they have already been allocated in the
				 * race, but a non default monster will get the 2 bonus feats
				 * instead. Both versions of the monster will get one at 6th
				 * level. i.e. default Ogre with 2 class levels, or no default
				 * Ogre with 4 giant levels and 2 class levels.
				 */
				if (aPC.isMonsterDefault()
					&& total <= aPC.getRace().getMonsterClassLevels(aPC))
				{
					processBonusFeats = false;
				}
			}

			if (!aPC.isImporting())
			{
				// We do not want to do these
				// calculations a second time when are
				// importing a character. The feat
				// number and the stat point pool are
				// already saved in the import file.

				//				if (processBonusFeats) {
				//					final double bonusFeats = aPC.getBonusFeatsForNewLevel(this);
				//					if (bonusFeats > 0) {
				//						// aPC.setFeats(aPC.getFeats() + bonusFeats);
				//						aPC.adjustFeats(bonusFeats);
				//					}
				//				}

				if (processBonusStats)
				{
					final int bonusStats = Globals.getBonusStatsForLevel(total);
					if (bonusStats > 0)
					{
						aPC.setPoolAmount(aPC.getPoolAmount() + bonusStats);

						if (!bSilent
							&& SettingsHandler.getShowStatDialogAtLevelUp())
						{
							levelUpStats =
									askForStatIncrease(aPC, bonusStats, true);
						}
					}
				}
			}
		}

		// Update Skill Points. Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		final int spMod;
		spMod = recalcSkillPointMod(aPC, total);

		PCLevelInfo pcl;

		if (aPC.getLevelInfoSize() > 0)
		{
			pcl = aPC.getLevelInfo().get(aPC.getLevelInfoSize() - 1);

			if (pcl != null)
			{
				pcl.setLevel(level);
				pcl.setSkillPointsGained(spMod);
				pcl.setSkillPointsRemaining(pcl.getSkillPointsGained());
			}
		}

		skillPool = skillPool() + spMod;

		aPC.setSkillPoints(spMod + aPC.getSkillPoints());

		if (!aPC.isImporting())
		{
			//
			// Ask for stat increase after skill points have been calculated
			//
			if (levelUpStats > 0)
			{
				askForStatIncrease(aPC, levelUpStats, false);
			}

			if (newLevel == 1)
			{
				List<String> l = getSafeListFor(ListKey.KITS);
				for (int i = 0; i > l.size(); i++)
				{
					KitUtilities.makeKitSelections(0, l.get(i), i, aPC);
				}
				makeRegionSelection(0, aPC);
			}

			List<String> l = getSafeListFor(ListKey.KITS);
			for (int i = 0; i > l.size(); i++)
			{
				KitUtilities.makeKitSelections(newLevel, l.get(i), i, aPC);
			}
			makeRegionSelection(newLevel, aPC);

			// Make sure any natural weapons are added
			if (naturalWeapons != null)
			{
				List<Equipment> natWeap = new ArrayList<Equipment>();
				for (LevelProperty<Equipment> lp : naturalWeapons)
				{
					if (lp.getLevel() <= newLevel)
					{
						natWeap.add(lp.getObject());
					}
				}
				aPC.addNaturalWeapons(natWeap);
			}
		}

		// this is a monster class, so don't worry about experience
		if (isMonster())
		{
			return true;
		}

		if (!aPC.isImporting())
		{
			checkRemovals(aPC);
			final int minxp = aPC.minXPForECL();
			if (aPC.getXP() < minxp)
			{
				aPC.setXP(minxp);
			}
			else if (aPC.getXP() >= aPC.minXPForNextECL())
			{
				if (!bSilent)
				{
					ShowMessageDelegate.showMessageDialog(SettingsHandler
						.getGame().getLevelUpMessage(), Constants.s_APPNAME,
						MessageType.INFORMATION);
				}
			}
		}

		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel() == 1)
			&& !aPC.isImporting())
		{
			exchangeLevels(aPC);
		}
		return true;
	}

	/*
	 * REFACTOR This is going to require some inginuity to be able to do this in
	 * the new PCClass/PCClassLevel world, since this is an interaction across
	 * multiple PCClassLevels.
	 */
	private void exchangeLevels(final PlayerCharacter aPC)
	{
		final StringTokenizer aTok =
				new StringTokenizer(levelExchange, "|", false);

		if (aTok.countTokens() != 4)
		{
			Logging.errorPrint("levelExhange: invalid token count: "
				+ aTok.countTokens());
		}
		else
		{
			try
			{
				final String classKey = aTok.nextToken(); // Class to get
				// levels from
				final int iMinLevel = Integer.parseInt(aTok.nextToken()); // Minimum
				// level
				// required
				// in
				// donating
				// class
				int iMaxDonation = Integer.parseInt(aTok.nextToken()); // Maximum
				// levels
				// donated
				// from
				// class
				final int iLowest = Integer.parseInt(aTok.nextToken()); // Lowest
				// that
				// donation
				// can
				// lower
				// donating
				// class
				// level
				// to

				final PCClass aClass = aPC.getClassKeyed(classKey);

				if (aClass != null)
				{
					final int iLevel = aClass.getLevel();

					if (iLevel >= iMinLevel)
					{
						iMaxDonation = Math.min(iMaxDonation, iLevel - iLowest);
						if (hasMaxLevel())
						{
							iMaxDonation =
									Math.min(iMaxDonation, getSafe(IntegerKey.LEVEL_LIMIT) - 1);
						}

						if (iMaxDonation > 0)
						{
							//
							// Build the choice list
							//
							final List<String> choiceNames =
									new ArrayList<String>();

							for (int i = 0; i <= iMaxDonation; ++i)
							{
								choiceNames.add(Integer.toString(i));
							}

							//
							// Get number of levels to exchange for this class
							//
							final ChooserInterface c =
									ChooserFactory.getChooserInstance();
							c
								.setTitle("Select number of levels to convert from "
									+ aClass.getDisplayName()
									+ " to "
									+ getDisplayName());
							c.setTotalChoicesAvail(1);
							c.setPoolFlag(false);
							c.setAvailableList(choiceNames);
							c.setVisible(true);

							final List<String> selectedList =
									c.getSelectedList();
							int iLevels = 0;

							if (!selectedList.isEmpty())
							{
								iLevels = Integer.parseInt(selectedList.get(0));
							}

							if (iLevels > 0)
							{
								aPC.giveClassesAway(this, aClass, iLevels);
							}
						}
					}
				}
			}
			catch (NumberFormatException exc)
			{
				ShowMessageDelegate.showMessageDialog("levelExchange:"
					+ Constants.s_LINE_SEP + exc.getMessage(),
					Constants.s_APPNAME, MessageType.ERROR);
			}
		}
	}

	/*
	 * DELETEMETHOD I hope this can be deleted, since minus level support will not
	 * work the same way in the new PCClass/PCClassLevel world. If nothing else, it
	 * is massively a REFACTOR item to put this into the PlayerCharacter that is
	 * doing the removal.
	 */
	void doMinusLevelMods(final PlayerCharacter aPC, final int oldLevel)
	{
		subAddsForLevel(oldLevel, aPC);
		aPC.removeVariable("CLASS:" + getKeyName() + "|"
			+ Integer.toString(oldLevel));
	}

	/*
	 * REFACTOR Since this is side effects of adding a level, the
	 * PlayerCharacter needs to perform this work, not PCClass.
	 * Then again, this could be in PCClassLevel.
	 */
	void doPlusLevelMods(final int newLevel, final PlayerCharacter aPC,
		final PCLevelInfo pcLevelInfo)
	{
		addVariablesForLevel(newLevel, aPC);

		// moved after changeSpecials and addVariablesForLevel
		// for bug #688564 -- sage_sam, 18 March 2003
		aPC.calcActiveBonuses();
		addAddsForLevel(newLevel, aPC, pcLevelInfo);
	}

	/**
	 * Update the name of the required class for all special abilites, DEFINE's,
	 * and BONUS's
	 * 
	 * @param oldClass
	 *            The name of the class that should have the special abliities
	 *            changed
	 * @param newClass
	 *            The name of the new class for the altered special abilities
	 */
	@Override
	/*
	 * REFACTOR Great theory, wrong universe.  Well, mayne not, but the name implies 
	 * events which aren't occurring here.  Need to at least rename this...
	 */
	void fireNameChanged(final String oldClass, final String newClass)
	{
		//
		// This gets called on clone(), so don't traverse the list if the names
		// are the same
		//
		if (oldClass.equals(newClass))
		{
			return;
		}

		//
		// Go through the specialty list (SA) and adjust the class to the new
		// name
		//
		final List<SpecialAbility> specialAbilityList =
				getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList != null)
		{
			for (int idx = specialAbilityList.size() - 1; idx >= 0; --idx)
			{
				SpecialAbility sa = specialAbilityList.get(idx);

				// TODO - This looks like it should have always been a reference
				// to getSASource not getSource.
				if (sa.getSASource().length() != 0)
				// if (sa.getSource().length() != 0)
				{
					removeSpecialAbility(sa);
					sa =
							new SpecialAbility(sa.getKeyName(), sa
								.getSASource(), sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					addSpecialAbilityToList(sa);
				}
			}
		}

		for (int lev : mapChar.getSecondaryKeySet(MapKey.SAB))
		{
			for (SpecialAbility sa : mapChar.getListFor(MapKey.SAB, lev))
			{
				if (sa.getSASource().length() != 0)
				{
					mapChar.removeFromListFor(MapKey.SAB, lev, sa);
					sa =
							new SpecialAbility(sa.getKeyName(), sa
								.getSASource(), sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					addSAB(sa, lev);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new
		// name
		//
		if (getVariableCount() > 0)
		{
			for (int idx = getVariableCount() - 1; idx >= 0; --idx)
			{
				final Variable variable = getVariable(idx);
				String formula = variable.getValue();

				formula = formula.replaceAll("=" + oldClass, "=" + newClass);

				variable.setValue(formula);
			}
		}

		//
		// Go through the bonus list (BONUS) and adjust the class to the new
		// name
		//
		if (getBonusList() != null)
		{
			for (BonusObj bonusObj : getBonusList())
			{
				final String bonus = bonusObj.toString();
				int offs = -1;

				for (;;)
				{
					offs = bonus.indexOf('=' + oldClass, offs + 1);

					if (offs < 0)
					{
						break;
					}

					addBonusList(bonus.substring(0, offs + 1) + newClass
						+ bonus.substring(offs + oldClass.length() + 1));
					removeBonusList(bonusObj);
				}
			}
		}

		//
		// Go through the damage reduction list (DR) and adjust the class to the
		// new name
		//
		for (DamageReduction reduction : getDRList())
		{
			for (Prerequisite prereq : reduction.getPreReqList())
			{
				if (DamageReduction.isPrereqForClassLevel(prereq, oldClass))
				{
					prereq.setKey(newClass);
				}
			}
		}
	}

	@Override
	String makeBonusString(final String bonusString, final String chooseString,
		final PlayerCharacter aPC)
	{
		return "0|" + super.makeBonusString(bonusString, chooseString, aPC);
	}

	/**
	 * Added to help deal with lower-level spells prepared in higher-level
	 * slots. BUG [569517] Works in conjunction with PlayerCharacter method
	 * availableSpells() sk4p 13 Dec 2002
	 * 
	 * @param aLevel
	 * @param bookName
	 * @return int
	 */
	int memorizedSpecialtiesForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final List<CharacterSpell> aList =
				getSpellSupport().getCharacterSpell(null, bookName, aLevel);

		if (aList.isEmpty())
		{
			return m;
		}

		for (CharacterSpell cs : aList)
		{
			if (cs.isSpecialtySpell())
			{
				m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
			}
		}

		return m;
	}

	int memorizedSpellForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final List<CharacterSpell> aList =
				getSpellSupport().getCharacterSpell(null, bookName, aLevel);

		if (aList.isEmpty())
		{
			return m;
		}

		for (CharacterSpell cs : aList)
		{
			m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
		}

		return m;
	}

	void subLevel(final boolean bSilent, final PlayerCharacter aPC)
	{

		if (aPC != null)
		{
			int total = aPC.getTotalLevels();

			int spMod = 0;
			final PCLevelInfo pcl = aPC.getLevelInfoFor(keyName, level);

			if (pcl != null)
			{
				spMod = pcl.getSkillPointsGained();
			}
			else
			{
				Logging
					.errorPrint("ERROR: could not find class/level info for "
						+ displayName + "/" + level);
			}

			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			List<LevelAbility> levelAbilityList = getLevelAbilityList();
			if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
			{
				for (LevelAbility levAbility : levelAbilityList)
				{
					if ((levAbility.level() == level) && levAbility.isFeat())
					{
						aPC.adjustFeats(-1);
					}
				}
			}

			final Integer zeroInt = Integer.valueOf(0);
			final int newLevel = level - 1;

			if (level > 0)
			{
				setHitPoint(level - 1, zeroInt);
			}

			//			aPC.adjustFeats(-aPC.getBonusFeatsForNewLevel(this));
			setLevel(newLevel, aPC);
			removeKnownSpellsForClassLevel(aPC);

			doMinusLevelMods(aPC, newLevel + 1);

			modDomainsForLevel(newLevel, false, aPC);

			if (newLevel == 0)
			{
				setSubClassKey(Constants.s_NONE);

				//
				// Remove all skills associated with this class
				//
				for (Skill skill : aPC.getSkillList())
				{
					skill.setZeroRanks(this, aPC);
				}

				spMod = skillPool();
			}

			if (!isMonster() && (total > aPC.getTotalLevels()))
			{
				total = aPC.getTotalLevels();

				// Roll back any stat changes that were made as part of the
				// level

				final List<PCLevelInfoStat> moddedStats =
						new ArrayList<PCLevelInfoStat>();
				if (pcl.getModifiedStats(true) != null)
				{
					moddedStats.addAll(pcl.getModifiedStats(true));
				}
				if (pcl.getModifiedStats(false) != null)
				{
					moddedStats.addAll(pcl.getModifiedStats(false));
				}
				if (!moddedStats.isEmpty())
				{
					for (PCLevelInfoStat statToRollback : moddedStats)
					{
						for (Iterator<PCStat> i = aPC.getStatList().iterator(); i
							.hasNext();)
						{
							final PCStat aStat = i.next();

							if (aStat.getAbb().equalsIgnoreCase(
								statToRollback.getStatAbb()))
							{
								aStat.setBaseScore(aStat.getBaseScore()
									- statToRollback.getStatMod());
								break;
							}
						}
					}
				}
			}

			if (!isMonster() && (total == 0))
			{
				aPC.setSkillPoints(0);
				// aPC.setFeats(0);
				aPC.getSkillList().clear();
				aPC.clearRealAbilities(null);
				//				aPC.clearRealFeats();
				//				aPC.getWeaponProfList().clear();
				aPC.setDirty(true);
			}
			else
			{
				aPC.setSkillPoints(aPC.getSkillPoints() - spMod);
				skillPool = skillPool() - spMod;
			}

			if (getLevel() == 0)
			{
				aPC.getClassList().remove(this);
			}

			aPC.validateCharacterDomains();

			// be sure to remove any natural weapons
			for (Equipment eq : this.getNaturalWeapons(newLevel + 1))
			{
				/*
				 * This is a COMPLETE hack to emulate PC.removeNaturalWeapons 
				 * without going through the convoluted "I am level dependent,
				 * you're not" game that is required in today's PCClass is
				 * level-aware world.  This becomes a lot easier in a 
				 * PCClass/PCClassLevel world, and the system can go back to
				 * using PC.removeNaturalWeapons
				 */
				aPC.removeEquipment(eq);
				aPC.delEquipSetItem(eq);
				aPC.setDirty(true);
			}
		}
		else
		{
			Logging
				.errorPrint("No current pc in subLevel()? How did this happen?");

			return;
		}
	}

	/**
	 * This method can be called to determine if the number of extra HD for
	 * purposes of skill points, feats, etc. See MM p. 11 extracted 03 Dec 2002
	 * by sage_sam for bug #646816
	 * 
	 * @param aPC
	 *            currently selected PlayerCharacter
	 * @param hdTotal
	 *            int number of monster HD the character has
	 * @return int number of HD considered "Extra"
	 */
	/*
	 * DELETEMETHOD This seems like something that violates the hardcoding rules -
	 * need to check what this should really be and how it should be structured
	 * in the code, new tags, etc. Message sent to PCGen-Experimental on Yahoo
	 */
	private static int getExtraHD(final PlayerCharacter aPC, final int hdTotal)
	{
		// Determine the EHD modifier based on the size category
		final int sizeInt = aPC.getRace().getSafe(
				FormulaKey.SIZE).resolve(aPC, "").intValue();
		final int ehdMod;

		switch (sizeInt)
		{
			case 8: // Collossal
				ehdMod = 32;

				break;

			case 7: // Gargantuan
				ehdMod = 16;

				break;

			case 6: // Huge
				ehdMod = 4;

				break;

			case 5: // Large
				ehdMod = 2;

				break;

			default: // Medium and smaller
				ehdMod = 1;

				break;
		}

		// EHD = total HD - base HD for size (min of zero)
		return Math.max(0, hdTotal - ehdMod);
	}

	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	private boolean isAutoKnownSpell(final String aSpellKey,
		final int spellLevel, final boolean useMap, final PlayerCharacter aPC)
	{
		List<KnownSpellIdentifier> knownSpellsList = getListFor(ListKey.KNOWN_SPELLS);
		if (knownSpellsList == null)
		{
			return false;
		}

		final Spell aSpell = Globals.getSpellKeyed(aSpellKey);

		if (useMap)
		{
			final Integer val = castForLevelMap.get(spellLevel);

			if ((val == null) || val == 0 || (aSpell == null))
			{
				return false;
			}
		}
		else if ((getCastForLevel(spellLevel, aPC) == 0) || (aSpell == null))
		{
			return false;
		}

		if (isProhibited(aSpell, aPC) && !isSpecialtySpell(aSpell))
		{
			return false;
		}

		// iterate through the KNOWNSPELLS: tag
		for (KnownSpellIdentifier filter : knownSpellsList)
		{
			if (filter.matchesFilter(aSpell, spellLevel))
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * This method calculates skill modifier for a monster character.
	 * 
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam for bug #629643
	 * and updated to fix the bug.
	 */
	private int getMonsterSkillPointMod(final PlayerCharacter aPC,
		final int total)
	{
		int spMod = 0;
		final int lockedMonsterSkillPoints =
				(int) aPC.getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");

		// Set the monster's base skills at the first level
		if (total == 1)
		{
			if (lockedMonsterSkillPoints == 0)
			{
				spMod = (int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER");
			}
			else
			{
				spMod = lockedMonsterSkillPoints;
			}
		}

		// This is not the first level added...
		else
		{
			if (getExtraHD(aPC, total) > 0)
			{
				spMod = getSafe(FormulaKey.START_SKILL_POINTS).resolve(aPC,
						classKey).intValue();
				if (lockedMonsterSkillPoints == 0)
				{
					spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");
				}
				else
				{
					spMod += lockedMonsterSkillPoints;
				}
				spMod = updateBaseSkillMod(aPC, spMod);
			}
		}

		if (spMod < 0)
		{
			spMod = 0;
		}

		return spMod;
	}

	/*
	 * This method calculates skill modifier for a non-monster character.
	 * 
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam for bug #629643
	 */
	private int getNonMonsterSkillPointMod(final PlayerCharacter aPC,
		final int total)
	{
		// int spMod = getSkillPoints();
		int lockedMonsterSkillPoints;
		int spMod = getSafe(FormulaKey.START_SKILL_POINTS).resolve(aPC,
				classKey).intValue();

		spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");

		if (isMonster())
		{
			lockedMonsterSkillPoints =
					(int) aPC.getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");
			if (lockedMonsterSkillPoints > 0)
			{
				spMod = lockedMonsterSkillPoints;
			}
			else if (total == 1)
			{
				int monSkillPts =
						(int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER");
				if (monSkillPts != 0)
				{
					spMod = monSkillPts;
				}
			}

			if (total != 1)
			{
				// If this level is one that is not entitled to skill points
				// based
				// on the monster's size, zero out the skills for this level
				final int nonSkillHD =
						(int) aPC.getTotalBonusTo("MONNONSKILLHD", "NUMBER");
				if (total <= nonSkillHD)
				{
					spMod = 0;
				}
			}
		}

		spMod = updateBaseSkillMod(aPC, spMod);

		if (total == 1)
		{
			if (SettingsHandler.getGame().isPurchaseStatMode())
			{
				aPC.setPoolAmount(0);
			}

			spMod *= aPC.getRace().getSafe(IntegerKey.INITIAL_SKILL_MULT);
			Globals.getBioSet().randomize("AGE", aPC);
		}
		else
		{
			spMod *= Globals.getSkillMultiplierForLevel(total);
		}

		return spMod;
	}

	/**
	 * Build a caster level map for this class. The map will be of the form
	 * <String,String> where the key is the spell level and the value is the
	 * number of times per day that spell level can be cast by the character
	 * 
	 * @param aPC
	 * 
	 * TODO: Why is this not a Map<Integer,Integer>
	 */
	/*
	 * PCCLASSLEVELONLY This calculation is dependent upon the class level
	 * and is therefore appropriate only for PCClassLevel
	 */
	private void calcCastPerDayMapForLevel(final PlayerCharacter aPC)
	{
		//
		// TODO: Shouldn't we be using Globals.getLevelInfo().size() instead of
		// 100?
		// Byngl -- November 25, 2002
		//
		if (castForLevelMap == null)
		{
			castForLevelMap = new HashMap<Integer, Integer>(100);
		}
		for (int i = 0; i < 100; i++)
		{
			final int s = getCastForLevel(i, aPC);
			castForLevelMap.put(i, s);
		}
	}

	/**
	 * <p>
	 * This function adds all templates up to the current level to
	 * <code>templatesAdded</code> and returns a list of the names of those
	 * templates.
	 * </p>
	 * <p>
	 * The function requires that templates be stored in the
	 * <code>templates</code> list as a string in the form
	 * LVL|[CHOOSE:]Template|Template|Template...
	 * </p>
	 * <p>
	 * Passing <code>false</code> to this function results in nothing
	 * happening, it doesn't add anything to the class.
	 * </p>
	 * 
	 * @param flag
	 *            If false, function returns empty <code>ArrayList</code> (?)
	 * @param aPC
	 * @return A list of templates added by this function
	 */
	/*
	 * DELETEMETHOD if not PCCLASSONLY.  This is required in PCClass since
	 * it is really done during addLevel.
	 */
	@Override
	public List<String> getTemplates(final boolean flag,
		final PlayerCharacter aPC)
	{
		final ArrayList<String> newTemplates = new ArrayList<String>();

		if (flag)
		{
			return newTemplates;
		}

		for (final LevelProperty<String> template : getTemplates())
		{
			if (level < template.getLevel())
			{
				continue;
			}

			/*
			 * The template string will either be a CHOOSE: tag or a bar
			 * separated list of templates
			 */
			final String tString = template.getObject();

			if (tString.startsWith("CHOOSE:"))
			{
				newTemplates.add(PCTemplate.chooseTemplate(this, tString
					.substring(7), true, aPC));
			}
			else
			{
				for (String templ : tString.split("\\|"))
				{
					newTemplates.add(templ);
				}
			}
		}

		return newTemplates;
	}

	/*
	 * PCCLASSLEVELONLY Since this is a level dependent calculation, this should
	 * be performed by the PCClassLevel.
	 */
	private String getUMultForLevel(final int aLevel)
	{
		String aString = "0";

		List<String> umultList = getListFor(ListKey.UMULT);
		if ((umultList == null) || umultList.isEmpty())
		{
			return aString;
		}

		for (String umult : umultList)
		{
			final int pos = umult.lastIndexOf('|');

			if ((pos >= 0)
				&& (aLevel <= Integer.parseInt(umult.substring(0, pos))))
			{
				aString = umult.substring(pos + 1);
			}
		}

		return aString;
	}

	/*
	 * REFACTOR This should really be better at recognizing level dependent
	 * items and storing them appropriately and not relying on PObject to ever
	 * be level aware. This is a general problem across PCClass and I would like
	 * this to be solved before the great PCClass/PCClassLevel splitting.
	 */
	/*
	 * PCCLASSONLY Since this is really just an extracted method of something
	 * that happens in the factory production of a PCClassLevel, it is only
	 * required in PCClass.
	 */
	private void addVariablesForLevel(final int aLevel,
		final PlayerCharacter aPC)
	{
		if (getVariableCount() == 0)
		{
			return;
		}

		if (aLevel == 1)
		{
			addVariablesForLevel(0, aPC);
		}

		final String prefix = classKey + '|';

		for (Iterator<Variable> i = getVariableIterator(); i.hasNext();)
		{
			final Variable v = i.next();

			if (v.getLevel() == aLevel)
			{
				aPC.addVariable(prefix + v.getDefinition());
			}
		}
	}

	//
	// Ask user to select a stat to increment. This can happen before skill
	// points
	// are calculated, so an increase to the appropriate stat can give more
	// skill points
	//
	/*
	 * REFACTOR This is part of the leveling up process, but really can't be
	 * part of the production of a PCClassLevel object, since that is
	 * PlayerCharacter independent. This really needs to happen during the
	 * addition of a PCClassLevel to a PlayerCharacter.
	 */
	private final int askForStatIncrease(final PlayerCharacter aPC,
		final int statsToChoose, final boolean isPre)
	{
		//
		// If 1st time here (checks for preincrement), then will only ask if
		// want to ask before level up
		// If 2nd time here, will ask if there are any remaining points
		// unassigned.
		// So, hitting cancel on the 1st popup will cause the 2nd popup to ask
		// again.
		// This is to handle cases where the user is adding multiple levels, so
		// the SKILL point total
		// won't be too messed up
		//
		if (isPre)
		{
			if (!Globals.checkRule(RuleConstants.INTBEFORE))
			{
				return statsToChoose;
			}
		}

		String extraMsg = "";

		if (isPre)
		{
			extraMsg = "\nRaising a stat here may award more skill points.";
		}

		int iCount = 0;

		for (int ix = 0; ix < statsToChoose; ++ix)
		{
			final StringBuffer sStats = new StringBuffer();

			for (Iterator<PCStat> i = aPC.getStatList().iterator(); i.hasNext();)
			{
				final PCStat aStat = i.next();
				final int iAdjStat =
						aPC.getStatList().getTotalStatFor(aStat.getAbb());
				final int iCurStat =
						aPC.getStatList().getBaseStatFor(aStat.getAbb());
				sStats.append(aStat.getAbb()).append(": ").append(iCurStat);

				if (iCurStat != iAdjStat)
				{
					sStats.append(" adjusted: ").append(iAdjStat);
				}

				sStats.append(" (").append(
					aPC.getStatList().getStatModFor(aStat.getAbb())).append(
					")\n");
			}

			final InputInterface ii = InputFactory.getInputInstance();
			final Object selectedValue =
					ii
						.showInputDialog(
							null,
							"Choose stat to increment or select Cancel to increment stat on the Summary tab."
								+ extraMsg
								+ "\n\n"
								+ "Current Stats:\n"
								+ sStats + "\n", Constants.s_APPNAME,
							MessageType.INFORMATION,
							SettingsHandler.getGame().s_ATTRIBLONG,
							SettingsHandler.getGame().s_ATTRIBLONG[0]);

			if (selectedValue != null)
			{
				for (Iterator<PCStat> i = aPC.getStatList().iterator(); i
					.hasNext();)
				{
					final PCStat aStat = i.next();

					if (aStat.getDisplayName().equalsIgnoreCase(
						selectedValue.toString()))
					{
						aPC.saveStatIncrease(aStat.getAbb(), 1, isPre);
						aStat.setBaseScore(aStat.getBaseScore() + 1);
						aPC.setPoolAmount(aPC.getPoolAmount() - 1);
						++iCount;

						break;
					}
				}
			}
		}

		return statsToChoose - iCount;
	}

	/**
	 * Build a list of Substitution Classes for the user to choose from. The
	 * list passed in will be populated.
	 * 
	 * @param choiceNames
	 *            The list of substitution classes to choose from.
	 * @param level
	 *            The class level to determine the choices for  
	 * @param aPC
	 */
	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private void buildSubstitutionClassChoiceList(
		final List<PCClass> choiceList, final int level,
		final PlayerCharacter aPC)
	{

		for (SubstitutionClass sc : substitutionClassList)
		{
			if (!PrereqHandler.passesAll(sc.getPreReqList(), aPC, this))
			{
				continue;
			}
			if (!sc.hasLevelArrayModsForLevel(level))
			{
				continue;
			}
			if (!sc.qualifiesForSubstitutionLevel(aPC, level))
			{
				continue;
			}

			choiceList.add(sc);
		}
		Collections.sort(choiceList); // sort the SubstitutionClass's 
		choiceList.add(0, this); // THEN add the base class as the first choice
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 * 
	 * @param bString
	 *            Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST
	 *            or %VAR bonus section
	 * @param iTimes
	 *            multiply bonus * iTimes
	 * @param aBonus
	 *            The bonuse Object used for calcs
	 * @param anObj
	 * @return partial bonus
	 */
	private double calcPartialBonus(final String bString, final int iTimes,
		final BonusObj aBonus, final Object anObj)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);

		if (aBonus.getPCLevel() >= 0)
		{
			// discard first token (Level)
			aTok.nextToken();
		}

		aTok.nextToken(); // Is this intended to be thrown away? Why?

		final String aList = aTok.nextToken();
		final String aVal = aTok.nextToken();

		double iBonus = 0;

		if (aList.equals("ALL"))
		{
			return 0;
		}

		if (anObj instanceof PlayerCharacter)
		{
			iBonus =
					((PlayerCharacter) anObj).getVariableValue(aVal, classKey)
						.doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aVal);
			}
			catch (NumberFormatException e)
			{
				// Should this be ignored?
				Logging
					.errorPrint("PCClass calcPartialBonus NumberFormatException in BONUS: "
						+ bString);
			}
		}

		return iBonus * iTimes;
	}

	/*
	 * DELETEMETHOD through refactoring this to another location. While this is
	 * yet another potentially useful utility function, PCClass really isn't the
	 * appropriate place for this method.
	 */
	private static void checkAdd(final StringBuffer txt, final String comp,
		final String label, final String value)
	{
		if ((value != null) && !comp.equals(value))
		{
			txt.append('\t').append(label).append(value);
		}
	}

	/*
	 * PCCLASSONLY This is really part of the PCClassLevel Factory, and
	 * therefore only needs to be placed in PCClass
	 */
	private void checkForSubClass(final PlayerCharacter aPC)
	{
		if (!hasSubClass || (subClassList == null) || (subClassList.isEmpty()))
		{
			return;
		}

		List<String> columnNames = new ArrayList<String>(3);
		columnNames.add("Name");
		columnNames.add("Cost");
		columnNames.add("Other");

		List<List> choiceList = new ArrayList<List>();

		for (SubClass sc : subClassList)
		{
			/*
			 * BUG MULTIPREREQS would fail here on a SubClass :( - thpr 11/4/06
			 * 
			 * STOP THE MAGIC, I want to delete MULTIPREREQs
			 */
			if (!PrereqHandler.passesAll(sc.getPreReqList(), aPC, this))
			{
				continue;
			}

			final List<Object> columnList = new ArrayList<Object>(3);

			columnList.add(sc);
			columnList.add(Integer.toString(sc.getCost()));
			columnList.add(sc.getSupplementalDisplayInfo());

			if (!getSubClassKey().equals(Constants.s_NONE))
			{
				// We already have a subclass requested.
				// If it is legal we will return that.
				choiceList.clear();
				choiceList.add(columnList);
				break;
			}

			choiceList.add(columnList);
		}

		Collections.sort(choiceList, new Comparator<List>()
		{
			public int compare(List o1, List o2)
			{
				try
				{
					PCClass class1 = ((List<PCClass>) o1).get(0);
					PCClass class2 = ((List<PCClass>) o2).get(0);
					return class1.compareTo(class2);
				}
				catch (RuntimeException e)
				{
					return 0;
				}
			}
		});

		// add base class to the chooser at the TOP
		if (getSafe(ObjectKey.ALLOWBASECLASS))
		{
			final List<Object> columnList2 = new ArrayList<Object>(3);
			columnList2.add(this);
			columnList2.add("0");
			columnList2.add("");
			choiceList.add(0, columnList2);
		}

		/*
		 * REFACTOR This makes an assumption that SubClasses are ONLY Schools, which may
		 * not be a fabulous assumption
		 */
		final ChooserInterface c = ChooserFactory.getChooserInstance();

		c.setTitle("School Choice (Specialisation)");
		c
			.setMessageText("Make a selection.  The cost column indicates the cost of that selection. "
				+ "If this cost is non-zero, you will be asked to also "
				+ "select items from this list to give up to cover that cost.");
		c.setTotalChoicesAvail(1);
		c.setPoolFlag(false);

		// c.setCostColumnNumber(1); // Allow 1 choice, regardless of
		// cost...cost will be applied in second phase
		c.setAvailableColumnNames(columnNames);
		c.setAvailableList(choiceList);

		if (choiceList.size() == 1)
		{
			c.setSelectedList(choiceList);
		}
		else if (choiceList.size() != 0)
		{
			c.setVisible(true);
		}

		List<List<PCClass>> selectedList;
		if (!getSafe(ObjectKey.ALLOWBASECLASS))
		{
			while (c.getSelectedList().size() == 0)
			{
				c.setVisible(true);
			}
			selectedList = c.getSelectedList();

		}
		else
		{
			selectedList = c.getSelectedList();
		}

		if (selectedList.size() == 0)
		{
			return;
		}

		List<PCClass> selectedRow = selectedList.get(0);
		if (selectedRow.size() == 0)
		{
			return;
		}
		PCClass subselected = selectedRow.get(0);

		if (!selectedList.isEmpty() && subselected instanceof SubClass)
		{
			clearProhibitedSchools();
			/*
			 * CONSIDER What happens to this reset during PCClass/PCClassLevel split
			 */
			specialty = null;

			SubClass sc = (SubClass) subselected;

			choiceList = new ArrayList<List>();

			for (SubClass sub : subClassList)
			{
				if (sub.equals(sc))
				{
					//Skip the selected specialist school
					continue;
				}
				/*
				 * BUG MULTIPREREQS would fail here on a SubClass :( - thpr 11/4/06
				 * 
				 * STOP THE MAGIC, I want to delete MULTIPREREQs
				 */
				if (!PrereqHandler.passesAll(sub.getPreReqList(), aPC, this))
				{
					continue;
				}

				final List<Object> columnList = new ArrayList<Object>(3);

				int displayedCost = sub.getProhibitCost();
				if (displayedCost == 0)
				{
					continue;
				}

				columnList.add(sub);
				columnList.add(Integer.toString(displayedCost));
				columnList.add(sub.getSupplementalDisplayInfo());
				columnList.add(sub.getChoice());

				choiceList.add(columnList);
			}

			setSubClassKey(sc.getKeyName());

			if (sc.getChoice().length() > 0)
			{
				addSpecialty(sc.getChoice());
			}

			columnNames.add("Specialty");

			if (sc.getCost() != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1
					.setMessageText("Make a selection.  You must make as many selections "
						+ "necessary to cover the cost of your previous selections.");
				c1.setTotalChoicesAvail(sc.getCost());
				c1.setPoolFlag(true);
				c1.setCostColumnNumber(1);
				c1.setNegativeAllowed(true);
				c1.setVisible(true);
				selectedList = c1.getSelectedList();

				for (Iterator<List<PCClass>> i = selectedList.iterator(); i
					.hasNext();)
				{
					final List columns = i.next();
					sc = (SubClass) columns.get(0);
					addProhibitedSchool(sc.getChoice());
				}
			}
		}
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	private void checkForSubstitutionClass(final int aLevel,
		final PlayerCharacter aPC)
	{
		if (!hasSubstitutionClass || (substitutionClassList == null)
			|| (substitutionClassList.isEmpty()))
		{
			return;
		}
		List<String> columnNames = new ArrayList<String>(1);
		columnNames.add("Name");

		List<PCClass> choiceList = new ArrayList<PCClass>();
		buildSubstitutionClassChoiceList(choiceList, level, aPC);
		if (choiceList.size() <= 1)
		{
			return; // This means the there are no classes for which
			// the pc meets the prerequisitions and thus the 
			// base class is chosen.
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Substitution Levels");
		c.setMessageText("Choose one of the listed substitution levels "
			+ "or the base class(top entry).  "
			+ "Pressing Close will take the standard class level.");
		c.setTotalChoicesAvail(1);
		c.setPoolFlag(false);

		c.setAvailableColumnNames(columnNames);
		c.setAvailableList(choiceList);

		c.setVisible(true);

		List<PCClass> selectedList = c.getSelectedList();
		PCClass selected = null;
		if (!selectedList.isEmpty())
		{
			selected = selectedList.get(0);
		}

		if ((!selectedList.isEmpty()) && selected instanceof SubstitutionClass)
		{
			SubstitutionClass sc = (SubstitutionClass) selected;
			setSubstitutionClassKey(sc.getKeyName(), aLevel);
			sc.applyLevelArrayModsToLevel(this, aLevel, aPC);
			return;
		}
		else
		{
			/*	
			 *  the original code has the below line..
			 *	however, it appears to not be needed.
			 *	I say this because if the original buildSubstitutionClassChoiceList
			 *	method returned an empty list, it returned right away without
			 *	calling this method.
			*/
			setSubstitutionClassKey(null, aLevel);
			return;
		}

	}

	/*
	 * PCCLASSONLY Since this is a choice of ClassSkillList, this is part of the
	 * PCClass factory of PCClassLevels??
	 */
	private void chooseClassSkillList()
	{
		TransitionChoice<ClassSkillList> csc = get(ObjectKey.SKILLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (level < 1))
		{
			return;
		}

		clearClassSkillList();

		ChoiceSet<? extends ClassSkillList> choiceSet = csc.getChoices();
		Set<? extends ClassSkillList> lists = choiceSet.getSet(null);
		if (lists.size() == 1)
		{
			addClassSkill(lists.iterator().next());
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose class-skills this class will inherit");
		c.setTotalChoicesAvail(csc.getCount());
		c.setPoolFlag(false);
		c.setAvailableList(new ArrayList<ClassSkillList>(lists));
		c.setVisible(true);

		List<ClassSkillList> selectedList = c.getSelectedList();
		for (ClassSkillList st : selectedList)
		{
			addClassSkill(st);
		}
	}

	/*
	 * PCCLASSONLY Since this is part of the construction of a PCClassLevel,
	 * this is only part of PCClass...
	 */
	private void chooseClassSpellList()
	{
		TransitionChoice<CDOMListObject<Spell>> csc = get(ObjectKey.SPELLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (level < 1))
		{
			return;
		}

		clearClassSpellList();

		ChoiceSet<? extends CDOMListObject<Spell>> choiceSet = csc.getChoices();
		Set<? extends CDOMListObject<Spell>> lists = choiceSet.getSet(null);
		if (lists.size() == 1)
		{
			addClassSpellList(lists.iterator().next());
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose list of spells this class will use");
		c.setTotalChoicesAvail(csc.getCount());
		c.setPoolFlag(false);
		c.setAvailableList(new ArrayList<CDOMListObject<Spell>>(lists));
		c.setVisible(true);

		List<CDOMListObject<Spell>> selectedList = c.getSelectedList();
		for (CDOMListObject<Spell> st : selectedList)
		{
			addClassSpellList(st);
		}
	}

	/*
	 * REFACTOR Some derivative of this method will be in PCClass only as part
	 * of the factory creation of a PCClassLevel... or perhaps in PCClassLevel
	 * so it can steal some information from other PCClassLevels of that
	 * PCClass. Either way, this will be far from its current form in the final
	 * solution.
	 */
	/*
	 * CONSIDER Why does this not inherit classSkillChoices?
	 */
	private void inheritAttributesFrom(final PCClass otherClass)
	{
		Boolean hbss = otherClass.get(ObjectKey.HAS_BONUS_SPELL_STAT);
		if (hbss != null)
		{
			put(ObjectKey.HAS_BONUS_SPELL_STAT, hbss);
			PCStat bss = otherClass.get(ObjectKey.BONUS_SPELL_STAT);
			if (bss != null)
			{
				put(ObjectKey.BONUS_SPELL_STAT, bss);
			}
		}

		Boolean usbs = otherClass.get(ObjectKey.USE_SPELL_SPELL_STAT);
		if (usbs != null)
		{
			put(ObjectKey.USE_SPELL_SPELL_STAT, usbs);
		}
		Boolean cwss = otherClass.get(ObjectKey.CASTER_WITHOUT_SPELL_STAT);
		if (cwss != null)
		{
			put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, cwss);
		}
		PCStat ss = otherClass.get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
			put(ObjectKey.SPELL_STAT, ss);
		}

		TransitionChoice<CDOMListObject<Spell>> slc = otherClass
				.get(ObjectKey.SPELLLIST_CHOICE);
		if (slc != null)
		{
			put(ObjectKey.SPELLLIST_CHOICE, slc);
		}

		Set<String> s = otherClass.getAutoMapKeys();
		if (s != null)
		{
			for (String key : s)
			{
				for (String value : otherClass.getAuto(key))
				{
					addAutoArray(key, value);
				}
			}
		}

		if (!otherClass.getBonusList().isEmpty())
		{
			getBonusList().addAll(otherClass.getBonusList());
		}

		if (otherClass.getVariableCount() > 0)
		{
			addAllVariablesFrom(otherClass);
		}

		if (otherClass.getCSkillList() != null)
		{
			clearCSkills();
			addAllCSkills(otherClass.getCSkillList());
		}

		if (otherClass.getCcSkillList() != null)
		{
			clearCcSkills();
			addAllCcSkills(otherClass.getCcSkillList());
		}

		otherClass.setKitList(getSafeListFor(ListKey.KITS));

		if (otherClass.getRegionString() != null)
		{
			setRegionString(otherClass.getRegionString());
		}

		for (SpecialAbility sa : otherClass
			.getSafeListFor(ListKey.SPECIAL_ABILITY))
		{
			addSpecialAbilityToList(sa);
		}

		for (int lev : otherClass.mapChar.getSecondaryKeySet(MapKey.SAB))
		{
			for (SpecialAbility sa : otherClass.mapChar.getListFor(MapKey.SAB,
				lev))
			{
				addSAB(sa, lev);
			}
		}

		if (!otherClass.getDRList().isEmpty())
		{
			for (DamageReduction dr : otherClass.getDRList())
			{
				try
				{
					addDR(dr.clone());
				}
				catch (CloneNotSupportedException e)
				{
					Logging.errorPrint("Failed to clone DR for PCClass "
						+ keyName + ".", e);
				}
			}
		}

		if (otherClass.SR != null)
		{
			SR = new ArrayList<LevelProperty<String>>(otherClass.SR);
		}

		if (otherClass.vision != null)
		{
			vision = otherClass.vision;
		}

		if (otherClass instanceof SubClass)
		{
			((SubClass) otherClass).applyLevelArrayModsTo(this);
		}

		if (otherClass.naturalWeapons != null)
		{
			naturalWeapons =
					new ArrayList<LevelProperty<Equipment>>(
						otherClass.naturalWeapons);
		}

		put(ObjectKey.LEVEL_HITDIE, otherClass.get(ObjectKey.LEVEL_HITDIE));
	}

	private void modDomainsForLevel(final int aLevel, final boolean adding,
		final PlayerCharacter aPC)
	{

		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting())
		{
			return;
		}

		/*
		 * Note this uses ALL of the domains up to and including this level,
		 * because there is the possibility (albeit strange) that the PC was not
		 * qualified at a previous level change, but the PlayerCharacter is now
		 * qualified for the given Domain. Even this has quirks, since it is
		 * only applied at the time of level increase, but I think that quirk
		 * should be resolved by a CDOM system around 6.0 - thpr 10/23/06
		 */
		for (QualifiedObject<CDOMSingleRef<Domain>> qo : getSafeListFor(ListKey.DOMAIN))
		{
			CDOMSingleRef<Domain> ref = qo.getObject(aPC);
			if (ref != null)
			{
				addDomain(aPC, ref.resolvesTo(), adding);
			}
		}
		for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
		{
			if (me.getKey() > aLevel)
			{
				break;
			}
			PCClassLevel pcl = me.getValue();
			for (QualifiedObject<CDOMSingleRef<Domain>> qo : pcl
					.getSafeListFor(ListKey.DOMAIN))
			{
				CDOMSingleRef<Domain> ref = qo.getObject(aPC);
				if (ref != null)
				{
					addDomain(aPC, ref.resolvesTo(), adding);
				}
			}
		}
	}

	private void addDomain(final PlayerCharacter aPC, Domain d,
			final boolean adding)
	{
		if (d.qualifies(aPC))
		{
			String domKey = d.getKeyName();
			if (adding)
			{
				if (!aPC.containsCharacterDomain(this.getKeyName(), domKey))
				{
					Domain aDomain = d.clone();

					final CharacterDomain aCD =
							aPC.getNewCharacterDomain(getKeyName());
					aCD.setDomain(aDomain, aPC);
					aPC.addCharacterDomain(aCD);
					aDomain = aCD.getDomain();
					aDomain.setIsLocked(true, aPC);
				}
			}
			else
			{
				if (aPC.containsCharacterDomain(domKey))
				{
					aPC.removeCharacterDomain(domKey);
				}
			}
		}
	}

	/**
	 * Rolls hp for the current level according to the rules set in options.
	 * 
	 * @param aLevel
	 * @param aPC
	 * @param first
	 */
	/*
	 * REFACTOR This really needs to be part of the PCClassLevel importing into
	 * a PlayerCharacter. Some thought needs to be put into where this stuff is
	 * stored - should PCLevelInfo be adapted to store all of the non-static
	 * information about a PCClassLevel?
	 */
	public void rollHP(final PlayerCharacter aPC, int aLevel, boolean first)
	{
		int roll = 0;

		final int min =
				1 + (int) aPC.getTotalBonusTo("HD", "MIN")
					+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + keyName);
		final int max =
				getLevelHitDie(aPC, aLevel).getDie()
					+ (int) aPC.getTotalBonusTo("HD", "MAX")
					+ (int) aPC.getTotalBonusTo("HD", "MAX;CLASS." + keyName);

		if (Globals.getGameModeHPFormula().length() == 0)
		{
			if ((first && aLevel == 1) && SettingsHandler.isHPMaxAtFirstLevel())
			{
				roll = max;
			}
			else
			{
				if (!aPC.isImporting())
				{
					roll = Globals.rollHP(min, max, getDisplayName(), aLevel);
				}
			}
		}

		roll += ((int) aPC.getTotalBonusTo("HP", "CURRENTMAXPERLEVEL"));
		setHitPoint(aLevel - 1, Integer.valueOf(roll));
		aPC.setCurrentHP(aPC.hitPoints());
	}

	/*
	 * This method updates the base skill modifier based on stat bonus, race
	 * bonus, and template bonus. Created(Extracted from addLevel) 20 Nov 2002
	 * by sage_sam for bug #629643
	 */
	private int updateBaseSkillMod(final PlayerCharacter aPC, int spMod)
	{
		// skill min is 1, unless class gets 0 skillpoints per level (for second
		// apprentice class)
		final int skillMin = (spMod > 0) ? 1 : 0;

		if (getSafe(ObjectKey.MOD_TO_SKILLS))
		{
			spMod += (int) aPC.getStatBonusTo("MODSKILLPOINTS", "NUMBER");

			if (spMod < 1)
			{
				spMod = 1;
			}
		}

		// Race modifiers apply after Intellegence. BUG 577462
		spMod += aPC.getRace().getSafe(IntegerKey.SKILL_POINTS_PER_LEVEL);
		spMod = Math.max(skillMin, spMod); // Minimum 1, not sure if bonus
		// skills per

		// level can be < 1, better safe than sorry
		if (!aPC.getTemplateList().isEmpty())
		{
			for (PCTemplate template : aPC.getTemplateList())
			{
				spMod += template.getSafe(IntegerKey.BONUS_CLASS_SKILL_POINTS);
			}
		}

		return spMod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.core.PObject#addNaturalWeapon(pcgen.core.Equipment, int)
	 */
	/*
	 * FINALPCCLASSANDLEVEL Input from a Tag, and factory creation of a PCClassLevel
	 * require this method (of course, a level independent version for PCClassLevel)
	 */
	@Override
	public void addNaturalWeapon(final Equipment weapon, final int aLevel)
	{
		if (naturalWeapons == null)
		{
			naturalWeapons = new ArrayList<LevelProperty<Equipment>>();
		}
		naturalWeapons.add(LevelProperty.getLevelProperty(aLevel, weapon));
	}

	/**
	 * Retrieve the list of spells for the class. Warning this overrides the
	 * PObject method getSpellList and obnly returns the spells up to the level
	 * held in the class. This may not be what you expect.
	 * 
	 * @see pcgen.core.PObject#getSpellList()
	 */
	/*
	 * DELETEMETHOD This implies a whole host of work to be done in order to use
	 * PObject's version of this method.
	 * 
	 * First of all, SpellSupport is level aware, and for many reasons, that is
	 * bad (and will be unnecessary, yea!). Once SpellSupport's knowledge of
	 * levels is eliminated, this can be removed, as it will not differ from
	 * PObject's version of this method.
	 * 
	 * However, in order NOT to break PCClass and PCClassLevel, the level
	 * awareness that is in SpellSupport needs to be transferred out to the
	 * PCClass, which will likely have to create one SpellSupport object per
	 * level and have those stored and ready for passing to PCClassLevels.
	 */
	@Override
	public List<PCSpell> getSpellList()
	{
		return getSpellSupport().getSpellList(getLevel());
	}

	public int getMinLevelForSpellLevel(int spellLevel, boolean allowBonus)
	{
		if (!updateSpellCache(false))
		{
			return -1;
		}
		return spellCache.getMinLevelForSpellLevel(spellLevel, allowBonus);
	}

	public int getMaxSpellLevelForClassLevel(int classLevel)
	{
		if (!updateSpellCache(false))
		{
			return -1;
		}
		return spellCache.getMaxSpellLevelForClassLevel(classLevel);
	}

	@Override
	public Load getEncumberedArmorMove()
	{
		LevelProperty<Load> activeLP = null;

		if (encumberedArmorMove != null)
		{
			for (LevelProperty<Load> lp : encumberedArmorMove)
			{
				if (lp.getLevel() > level)
				{
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel())
				{
					activeLP = lp;
					continue;
				}
			}
		}

		return activeLP == null ? super.getEncumberedArmorMove() : activeLP
			.getObject();
	}

	@Override
	public Load getEncumberedLoadMove()
	{
		LevelProperty<Load> activeLP = null;

		if (encumberedLoadMove != null)
		{
			for (LevelProperty<Load> lp : encumberedLoadMove)
			{
				if (lp.getLevel() > level)
				{
					continue;
				}
				if (activeLP == null || activeLP.getLevel() < lp.getLevel())
				{
					activeLP = lp;
					continue;
				}
			}
		}

		return activeLP == null ? super.getEncumberedLoadMove() : activeLP
			.getObject();
	}

	@Override
	public void setEncumberedArmorMove(Load load, int lvl)
	{
		if (encumberedArmorMove == null)
		{
			encumberedArmorMove = new ArrayList<LevelProperty<Load>>();
		}
		encumberedArmorMove.add(LevelProperty.getLevelProperty(lvl, load));
	}

	@Override
	public void setEncumberedLoadMove(Load load, int lvl)
	{
		if (encumberedLoadMove == null)
		{
			encumberedLoadMove = new ArrayList<LevelProperty<Load>>();
		}
		encumberedLoadMove.add(LevelProperty.getLevelProperty(lvl, load));
	}

	@Override
	public void setMovement(Movement m, int level)
	{
		if (movementList == null)
		{
			movementList = new ArrayList<LevelProperty<Movement>>();
		}
		movementList.add(LevelProperty.getLevelProperty(level, m));

	}

	@Override
	public List<Movement> getMovements()
	{
		if (movementList == null)
		{
			return Collections.emptyList();
		}
		List<Movement> returnList = new ArrayList<Movement>();
		for (LevelProperty<Movement> prop : movementList)
		{
			if (prop.getLevel() <= level)
			{
				returnList.add(prop.getObject());
			}
		}
		return returnList;
	}

	public void removeAllAutoAbilites(final int alevel)
	{
		for (AbilityCategory category : getAbilityCategories())
		{
			for (QualifiedObject<String> qo : new ArrayList<QualifiedObject<String>>(
				getRawAbilityObjects(category, Nature.AUTOMATIC)))
			{
				if (qo instanceof QualifiedObject.LevelAwareQualifiedObject)
				{
					QualifiedObject.LevelAwareQualifiedObject<String> aqo =
							(LevelAwareQualifiedObject<String>) qo;
					if (aqo.level == level)
					{
						removeAbility(category, Nature.AUTOMATIC, qo);
					}
				}
			}
		}
	}

	public void removeAllVirtualAbilites(final int alevel)
	{
		for (AbilityCategory category : getAbilityCategories())
		{
			for (QualifiedObject<String> qo : new ArrayList<QualifiedObject<String>>(
				getRawAbilityObjects(category, Nature.VIRTUAL)))
			{
				if (qo instanceof QualifiedObject.LevelAwareQualifiedObject)
				{
					QualifiedObject.LevelAwareQualifiedObject<String> aqo =
							(LevelAwareQualifiedObject<String>) qo;
					if (aqo.level == level)
					{
						removeAbility(category, Nature.VIRTUAL, qo);
					}
				}
			}
		}
	}
	/**
	 * Remove the level based DR.  Used by Substitution class levels
	 * @param level
	 */
	public void removeLevelDR(int level)
	{
		List<DamageReduction> newDR = new ArrayList<DamageReduction>();
		for (DamageReduction reduction : getDRList())
		{
			for (Prerequisite prereq : reduction.getPreReqList())
			{
				if (!DamageReduction.isPrereqForClassLevel(prereq, getKeyName())
					&& !prereq.getOperand().equals(Integer.toString(level))
					)
				{
					newDR.add(reduction);
				}
			}
		}
		clearDR();
		for (DamageReduction reduction : newDR)
		{
			addDR(reduction);
		}
		
	}

	//	public void removeAutoAbilities(final AbilityCategory aCategory, final int aLevel)
	//	{
	//		if ( aCategory == AbilityCategory.FEAT )
	//		{
	//			removeAllAutoFeats(aLevel);
	//			return;
	//		}
	//		
	//		if ( theAutoAbilities == null )
	//		{
	//			return;
	//		}
	//		theAutoAbilities.put(aCategory, aLevel, null);
	//	}

	SortedMap<Integer, PCClassLevel> levelMap = new TreeMap<Integer, PCClassLevel>();
//	List<PCClassLevel> repeatLevelObjects = new ArrayList<PCClassLevel>();

	public PCClassLevel getClassLevel(int lvl)
	{
		if (!levelMap.containsKey(lvl))
		{
			PCClassLevel classLevel = new PCClassLevel();
			classLevel.put(IntegerKey.LEVEL, Integer.valueOf(lvl));
			classLevel.setName(getDisplayName() + "(" + lvl + ")");
			classLevel.put(ObjectKey.PARENT, this);
			levelMap.put(lvl, classLevel);
		}
		return levelMap.get(lvl);
	}

	public int getClassLevelCount()
	{
		return levelMap.size();
	}

	public Collection<PCClassLevel> getClassLevelCollection()
	{
		return Collections.unmodifiableCollection(levelMap.values());
	}

	public int getPCClassLevel(PCClassLevel pcl)
	{
		if (this.equals(pcl.get(ObjectKey.PARENT)))
		{
			for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
			{
				if (me.getValue().equals(pcl))
				{
					return me.getKey().intValue();
				}
			}
		}
		return -1;
	}

//	public PCClassLevel getRepeatLevel(int level, String objectName)
//	{
//		PCClassLevel pcl = new PCClassLevel();
//		repeatLevelObjects.add(pcl);
//		pcl.put(ObjectKey.PARENT, this);
//		pcl.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
//		String originalLevels = level + ":" + objectName;
//		pcl.put(StringKey.REPEAT, originalLevels);
//		pcl.setName(getDisplayName() + "(" + originalLevels + ")");
//		return pcl;
//	}
//	
//	public Collection<PCClassLevel> getRepeatLevels()
//	{
//		return Collections.unmodifiableList(repeatLevelObjects);
//	}

	public boolean updateSpellCache(boolean force)
	{
		if (force || !spellCacheValid)
		{
			SpellProgressionCache cache = new SpellProgressionCache();
			for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
			{
				Integer lvl = me.getKey();
				PCClassLevel cl = me.getValue();
				List<Formula> cast = cl.getListFor(ListKey.CAST);
				if (cast != null)
				{
					cache.setCast(lvl, cast);
				}
				List<Formula> known = cl.getListFor(ListKey.KNOWN);
				if (known != null)
				{
					cache.setKnown(lvl, known);
				}
				List<Formula> spec = cl.getListFor(ListKey.SPECIALTYKNOWN);
				if (spec != null)
				{
					cache.setSpecialtyKnown(lvl, spec);
				}
			}
			if (!cache.isEmpty())
			{
				spellCache = cache;
			}
			spellCacheValid = true;
		}
		return spellCache != null;
	}

	/**
	 * Retrieve this object's visibility in the GUI and on the output sheet
	 * @return Visibility in the GUI and on the output sheet 
	 */
	@Override
	public Visibility getVisibility()
	{
		return getSafe(ObjectKey.VISIBILITY);
	}

}
