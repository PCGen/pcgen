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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.cdom.helper.AttackCycle;
import pcgen.cdom.helper.ShieldProfProvider;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.SkillCostCalc;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SubstitutionLevelSupport;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.DeferredLine;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.AttackType;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * <code>PCClass</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 */
public class PCClass extends PObject
{

	public static final CDOMReference<ClassSkillList> MONSTER_SKILL_LIST;
	
	public static final CDOMReference<DomainList> ALLOWED_DOMAINS;

	static
	{
		ClassSkillList wpl = new ClassSkillList();
		wpl.setName("*MonsterSkill");
		MONSTER_SKILL_LIST = CDOMDirectSingleRef.getRef(wpl);
		DomainList dl = new DomainList();
		dl.setName("*Allowed");
		ALLOWED_DOMAINS = CDOMDirectSingleRef.getRef(dl);
	}

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
	 * ALLCLASSLEVELS skillPool is part each PCClassLevel and what that level
	 * grants to each PlayerCharacter (added by the PCClassLevel Factory, not
	 * by a tag)
	 */
	private int skillPool = 0;

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
	private String classKey = null;

	/** The level of this class for the PC this PCClass is assigned to. */
	/*
	 * ALLCLASSLEVELS This is a fundamental part of the PCClassLevel creation
	 * and stored information
	 */
	protected int level = 0; // TODO - This should be moved.

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
		if (classKey == null)
		{
			classKey = "CLASS:" + getKeyName(); //$NON-NLS-1$

		}
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

		List<BonusObj> rawBonusList = getRawBonusList(aPC);
		if ((asLevel == 0) || rawBonusList.isEmpty())
		{
			return 0;
		}

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();

		for (final BonusObj bonus : rawBonusList)
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
					if (bonus.hasPrerequisites())
					{
						localPreReqList.addAll(bonus.getPrerequisiteList());
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
								aPC.getVariableValue(aString, getQualifiedKey())
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

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", getKeyName());
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
						+ getKeyName());

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
						getSpellSupport().getCharacterSpells(null,
							Constants.EMPTY_STRING, ix);
				List<Spell> bList = new ArrayList<Spell>();

				if (!aList.isEmpty())
				{
					// Assume no null check on castInfo requried, because
					// getNumFromCastList above would have returned -1
					if ((ix > 0)
						&& "DIVINE".equalsIgnoreCase(getSpellType()))
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
	public final List<CDOMListObject<Spell>> getClassSpellList(PlayerCharacter pc)
	{
		return pc.getAssocList(this, AssociationListKey.CLASSSPELLLIST);
	}

	/*
	 * PCCLASSLEVELONLY This is only relevant for the PCClassLevel (obviously?)
	 */
	public final int getLevel()
	{
		return level;
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
	public String getSpellKey(PlayerCharacter pc)
	{
		String stableSpellKey = pc.getAssoc(this, AssociationKey.SPELL_KEY_CACHE);
		if (stableSpellKey != null)
		{
			return stableSpellKey;
		}

		List<CDOMListObject<Spell>> classSpellList = pc.getAssocList(this,
				AssociationListKey.CLASSSPELLLIST);
		if (classSpellList == null)
		{
			chooseClassSpellList(pc);

			classSpellList = pc.getAssocList(this,
					AssociationListKey.CLASSSPELLLIST);

			if (classSpellList == null)
			{
				stableSpellKey = "CLASS" + Constants.PIPE + getKeyName();

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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in 
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType()
	{
		String castInfo = get(StringKey.SPELLTYPE);
		return castInfo == null ? Constants.s_NONE : castInfo;
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
			if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(get(StringKey.SPELLTYPE)))
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
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", getKeyName());
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
	public void setSubClassKey(PlayerCharacter pc, final String aKey)
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

		pc.removeAssoc(this, AssociationKey.SPELL_KEY_CACHE);
		getSpellKey(pc);
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
		List<SubClass> subClassList = getListFor(ListKey.SUB_CLASS);
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
		List<SubstitutionClass> substitutionClassList = getListFor(ListKey.SUBSTITUTION_CLASS);
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
				addFeatPoolBonus(aPC);
			}

			chooseClassSkillList(aPC);
		}

		if (!aPC.isImporting())
		{
			aPC.calcActiveBonuses();
			aPC.getSpellTracker().buildSpellLevelMap(newLevel);
		}

		if ((level == 1) && !aPC.isImporting() && (curLevel == 0))
		{
			checkForSubClass(aPC);
			getSpellKey(aPC);
		}

		if (!aPC.isImporting() && (curLevel < level))
		{
			checkForSubstitutionClass(level, aPC);
		}

		for (PCClass pcClass : aPC.getClassList())
		{
			pcClass.calculateKnownSpellsForClassLevel(aPC);
		}

		// check to see if we have dropped a level.
		if (curLevel > newLevel)
		{
			aPC.resetEpicCache();
		}
	}

	/**
	 * Add the bonus to the character's feat pool that is granted by the class.
	 * NB: LEVELSPERFEAT is now handled via PLayerCHaracter.getNumFeatsFromLevels() 
	 * rather than bonuses. Only the standard feat progression for the gamemode is 
	 * handled here.
	 * @param aPC The character to bonus.
	 */
	void addFeatPoolBonus(final PlayerCharacter aPC)
	{
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
			if (divisor > 0)
			{
				StringBuffer aBuf =
					new StringBuffer("0|FEAT|PCPOOL|")
						.append("max(CL");
				// Make sure we only take off the startlevel value once
				if (this == aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(0)))
				{
					aBuf.append("-").append(startLevel);
					aBuf.append("+").append(rangeLevel);
				}
				aBuf.append(",0)/").append(divisor);
//						Logging.debugPrint("Feat bonus for " + this + " is "
//							+ aBuf.toString());
				BonusObj bon = Bonus.newBonus(aBuf.toString());
				bon.setCreatorObject(this);
				bon.setSaveToPCG(false);
				aPC.addAssoc(this, AssociationListKey.BONUS, bon);
			}
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
		final String spellKey = getSpellKey(aPC);

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
					Globals.getSpellsIn(-1, getSpellKey(aPC),
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
			final String spellKey = getSpellKey(aPC);

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
					DomainApplication.addSpellsToClassForLevels(cd.getDomain(),
							this, 0, _maxLevel);
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

	public boolean isQualified(final PlayerCharacter aPC)
	{
		return aPC != null
			&& PrereqHandler.passesAll(getPrerequisiteList(), aPC, this);
	}

	@Override
	public String getPCCText()
	{
		final StringBuffer pccTxt = new StringBuffer(200);
		pccTxt.append("CLASS:").append(getDisplayName());
		pccTxt.append(super.getPCCText(false));

		// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
		{
			pccTxt.append(lineSep).append(me.getKey()).append('\t');
			pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext()
					.unparse(me.getValue()), "\t"));
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

		// TODO - Add ABILITY tokens.

		return pccTxt.toString();
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
		for (BonusObj bonus : getRawBonusList(aPC))
		{
			if ((bonus.getPCLevel() <= level))
			{
				if (bonus.hasPrerequisites())
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void addClassSpellList(CDOMListObject<Spell> list, PlayerCharacter pc)
	{
		pc.addAssoc(this, AssociationListKey.CLASSSPELLLIST, list);
		pc.removeAssoc(this, AssociationKey.SPELL_KEY_CACHE);
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the spell list is
	 * calculated based on other factors, it is not a Tag
	 */
	public void clearClassSpellList(PlayerCharacter pc)
	{
		pc.removeAllAssocs(this, AssociationListKey.CLASSSPELLLIST);
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final void addSubClass(final SubClass sClass)
	{
		sClass.setHitPointMap(hitPointMap);
		sClass.put(ObjectKey.LEVEL_HITDIE, get(ObjectKey.LEVEL_HITDIE));
		addToListFor(ListKey.SUB_CLASS, sClass);
	}

	/*
	 * PCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected substitutionClass, if any, is structured into the PCClassLevel
	 * during the construction of the PCClassLevel
	 */
	public final void addSubstitutionClass(final SubstitutionClass sClass)
	{
		sClass.setHitPointMap(hitPointMap);
		sClass.put(ObjectKey.LEVEL_HITDIE, get(ObjectKey.LEVEL_HITDIE));
		addToListFor(ListKey.SUBSTITUTION_CLASS, sClass);
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

		return cr == null ? 0 : cr.resolve(aPC, getQualifiedKey()).floatValue();
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

			spellCache = null;
			spellCacheValid = false;

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

			levelMap = new TreeMap<Integer, PCClassLevel>();
			for (Map.Entry<Integer, PCClassLevel> me : aClass.levelMap.entrySet())
			{
				levelMap.put(me.getKey(), me.getValue().clone());
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
	 * PCCLASSANDLEVEL Since this is required in both places...
	 */
	@Override
	public final String toString()
	{
		return displayName;
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public boolean hasClassSkill(PlayerCharacter pc, final String aString)
	{
		List<ClassSkillList> classSkillList = pc.getAssocList(this, AssociationListKey.CLASSSKILLLIST);
		if ((classSkillList == null) || classSkillList.isEmpty())
		{
			return false;
		}

		for (ClassSkillList key : classSkillList)
		{
			final PCClass pcClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, key.getLSTformat());

			if ((pcClass != null) && SkillCostCalc.hasCSkill(pc, pcClass, aString))
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

	public boolean hasSkill(PlayerCharacter pc, final String aName)
	{
		if (hasSkill(pc, aName, this))
		{
			return true;
		}
		return false;
	}

	private boolean hasSkill(PlayerCharacter pc, String aName, CDOMObject cdo)
	{
		List<Skill> assocCSkill = pc.getAssocList(cdo, AssociationListKey.CSKILL);
		if (assocCSkill != null)
		{
			for (Skill sk : assocCSkill)
			{
				//Have to do slow due to cloning :P
				if (sk.getKeyName().equals(aName))
				{
					return true;
				}
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
		// int spMod = getSkillPoints();
		int lockedMonsterSkillPoints;
		int spMod = getSafe(FormulaKey.START_SKILL_POINTS).resolve(aPC,
			getQualifiedKey()).intValue();
		
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
			if (aPC.getAge() <= 0)
			{
				// Only generate a random age if the user hasn't set one!
				Globals.getBioSet().randomize("AGE", aPC);
			}
		}
		else
		{
			spMod *= Globals.getSkillMultiplierForLevel(total);
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
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public final List<ClassSkillList> getClassSkillList(PlayerCharacter pc)
	{
		List<ClassSkillList> classSkillList = pc.getAssocList(this, AssociationListKey.CLASSSKILLLIST);
		if (classSkillList == null)
		{
			List<ClassSkillList> returnList = new ArrayList<ClassSkillList>(2);
			AbstractReferenceContext ref = Globals.getContext().ref;
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
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", getKeyName());
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
						"BASESPELLKNOWNSTAT;CLASS." + getKeyName());

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
		if (!PrereqHandler.passesAll(aSpell.getPrerequisiteList(), aPC, aSpell))
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

		for (SpellProhibitor prohibit : getSafeListFor(ListKey.PROHIBITED_SPELLS))
		{
			if (prohibit.isProhibited(aSpell, aPC))
			{
				return true;
			}
		}

		List<SpellProhibitor> assocList =
				aPC.getAssocList(this, AssociationListKey.PROHIBITED_SCHOOLS);
		if (assocList != null)
		{
			for (SpellProhibitor prohibit : assocList)
			{
				if (prohibit.isProhibited(aSpell, aPC))
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
	 * @param aPC
	 * @param adjustForPCSize
	 * @param includeCrit
	 * @return the unarmed damage string
	 */
	String getUdamForLevel(int aLevel, final PlayerCharacter aPC,
		boolean adjustForPCSize)
	{
		aLevel += (int) aPC.getTotalBonusTo("UDAM", "CLASS." + getKeyName());
		return getUDamForEffLevel(aLevel, aPC, adjustForPCSize);
	}

	/**
	 * Get the unarmed Damage for this class at the given level.
	 * 
	 * @param aLevel
	 * @param aPC
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	String getUDamForEffLevel(int aLevel, final PlayerCharacter aPC,
			boolean adjustForPCSize)
	{
		int iSize = Globals.sizeInt(aPC.getSize());

		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		AbstractReferenceContext ref = Globals.getContext().ref;
		final Equipment eq =
			ref.silentlyGetConstructedCDOMObject(
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
		List<CDOMObject> classObjects = new ArrayList<CDOMObject>();
		//Negative increment to start at highest level until an UDAM is found
		for (int i = aLevel; i >= 1; i--)
		{
			classObjects.add(getClassLevel(i));
		}
		classObjects.add(this);
		for (CDOMObject cdo : classObjects)
		{
			List<String> udam = cdo.getListFor(ListKey.UNARMED_DAMAGE);
			if (udam != null)
			{
				aDamage = udam.get(iSize);
				break;
			}
		}
		return aDamage;
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
			if (!PrereqHandler.passesAll(getPrerequisiteList(), aPC, this))
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
		aPC.selectTemplates(this, aPC.isImporting());
		PCClassLevel classLevel = getClassLevel(newLevel);
		aPC.selectTemplates(classLevel, aPC.isImporting());

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
			total = aPC.getTotalLevels();

			if (isMonster())
			{
				// If we have less levels that the races monster levels
				// then we can not give a stat bonus (i.e. an Ogre has
				// 4 levels of Giant, so it does not get a stat increase at
				// 4th level because that is already taken into account in
				// its racial stat modifiers, but it will get one at 8th
				LevelCommandFactory lcf = aPC.getRace().get(ObjectKey.MONSTER_CLASS);
				int monLevels = 0;
				if (lcf != null)
				{
					monLevels = lcf.getLevelCount().resolve(aPC, "").intValue();
				}

				if (total <= monLevels)
				{
					processBonusStats = false;
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
					final int bonusStats = Globals.getBonusStatsForLevel(total, aPC);
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
				for (TransitionChoice<Kit> kit : getSafeListFor(ListKey.KIT_CHOICE))
				{
					kit.act(kit.driveChoice(aPC), this, aPC);
				}
				TransitionChoice<Region> region = get(ObjectKey.REGION_CHOICE);
				if (region != null)
				{
					region.act(region.driveChoice(aPC), this, aPC);
				}
				addAdds(aPC);
				aPC.addNaturalWeapons(getListFor(ListKey.NATURAL_WEAPON));
			}

			for (TransitionChoice<Kit> kit : classLevel
					.getSafeListFor(ListKey.KIT_CHOICE))
			{
				kit.act(kit.driveChoice(aPC), classLevel, aPC);
			}
			TransitionChoice<Region> region = classLevel
					.get(ObjectKey.REGION_CHOICE);
			if (region != null)
			{
				region.act(region.driveChoice(aPC), classLevel, aPC);
			}

			// Make sure any natural weapons are added
			aPC.addNaturalWeapons(classLevel.getListFor(ListKey.NATURAL_WEAPON));
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
		if (containsKey(ObjectKey.EXCHANGE_LEVEL) && (getLevel() == 1)
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
		LevelExchange le = get(ObjectKey.EXCHANGE_LEVEL);
		
			try
 			{
				PCClass cl = le.getExchangeClass().resolvesTo();
				int iMinLevel = le.getMinDonatingLevel();
				int iMaxDonation = le.getMaxDonatedLevels();
				int iLowest = le.getDonatingLowerLevelBound();
				final PCClass aClass = aPC.getClassKeyed(cl.getKeyName());

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
		if (newLevel == 1)
		{
			addVariablesForLevel(0, aPC);
		}
		addVariablesForLevel(newLevel, aPC);

		// moved after changeSpecials and addVariablesForLevel
		// for bug #688564 -- sage_sam, 18 March 2003
		aPC.calcActiveBonuses();
		addAddsForLevel(newLevel, aPC, pcLevelInfo);
		if (!aPC.isImporting() && aPC.doLevelAbilities())
		{
			getClassLevel(newLevel).addAdds(aPC);
			getClassLevel(newLevel).checkRemovals(aPC);
		}
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
		for (SpecialAbility sa : getSafeListFor(ListKey.SAB))
		{
			removeFromListFor(ListKey.SAB, sa);
			sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(), sa
					.getSADesc());
			sa.setQualificationClass(oldClass, newClass);
			addToListFor(ListKey.SAB, sa);
		}
		for (PCClassLevel pcl : getClassLevelCollection())
		{
			for (SpecialAbility sa : pcl.getSafeListFor(ListKey.SAB))
			{
				if (sa.getSASource().length() != 0)
				{
					pcl.removeFromListFor(ListKey.SAB, sa);
					sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(),
							sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					pcl.addToListFor(ListKey.SAB, sa);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new
		// name
		//
		for (VariableKey vk : getVariableKeys())
		{
			put(vk, FormulaFactory.getFormulaFor(get(vk).toString().replaceAll(
					"=" + oldClass, "=" + newClass)));
		}
		//
		// Go through the bonus list (BONUS) and adjust the class to the new
		// name
		//
		renameBonusTarget(this, oldClass, newClass);

		//Now repeat for Class Levels
		for (PCClassLevel pcl : getClassLevelCollection())
		{
			for (VariableKey vk : pcl.getVariableKeys())
			{
				pcl.put(vk, FormulaFactory.getFormulaFor(pcl.get(vk).toString()
						.replaceAll("=" + oldClass, "=" + newClass)));
			}
			renameBonusTarget(pcl, oldClass, newClass);
		}
	}

	private static void renameBonusTarget(CDOMObject cdo, String oldClass,
			String newClass)
	{
		List<BonusObj> bonusList = cdo.getListFor(ListKey.BONUS);
		if (bonusList != null)
		{
			for (BonusObj bonusObj : bonusList)
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
					final BonusObj aBonus = Bonus.newBonus(bonus.substring(0,
							offs + 1)
							+ newClass
							+ bonus.substring(offs + oldClass.length() + 1));

					if (aBonus != null)
					{
						aBonus.setCreatorObject(cdo);
						cdo.addToListFor(ListKey.BONUS, aBonus);
					}
					cdo.removeFromListFor(ListKey.BONUS, bonusObj);
				}
			}
		}
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
				getSpellSupport().getCharacterSpells(null, bookName, aLevel);

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
				getSpellSupport().getCharacterSpells(null, bookName, aLevel);

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
			final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), level);

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
				setSubClassKey(aPC, Constants.s_NONE);

				//
				// Remove all skills associated with this class
				//
				for (Skill skill : aPC.getSkillList())
				{
					SkillRankControl.setZeroRanks(this, aPC, skill);
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

			PCClassLevel classLevel = getClassLevel(newLevel + 1);
			// be sure to remove any natural weapons
			for (Equipment eq : classLevel.getSafeListFor(ListKey.NATURAL_WEAPON))
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
		StringBuilder prefix = new StringBuilder();
		prefix.append(getQualifiedKey()).append('|').append(aLevel);
		CDOMObject cdo;
		if (aLevel == 0)
		{
			cdo = this;
		}
		else
		{
			cdo = getClassLevel(aLevel);
		}
		for (VariableKey vk : cdo.getVariableKeys())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(prefix).append('|').append(vk.toString()).append('|')
					.append(cdo.get(vk));
			aPC.addVariable(sb.toString());
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
		Set<String> statsAlreadyBonused = new HashSet<String>();
		boolean allowStacks = SettingsHandler.getGame().isBonusStatAllowsStack();
		for (int ix = 0; ix < statsToChoose; ++ix)
		{
			final StringBuffer sStats = new StringBuffer();
			final List<String> selectableStats = new ArrayList<String>();

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
					")");

				if (allowStacks || !statsAlreadyBonused.contains(aStat.getAbb()))
				{
					sStats.append("\n");
					selectableStats.add(aStat.getDisplayName());
				}
				else
				{
					sStats.append(" * Already incremented.\n");
				}
			}

			final String[] selectionValues = selectableStats.toArray(new String[]{});
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
							selectionValues,
							selectionValues[0]);

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
						statsAlreadyBonused.add(aStat.getAbb());
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

		for (SubstitutionClass sc : getListFor(ListKey.SUBSTITUTION_CLASS))
		{
			if (!PrereqHandler.passesAll(sc.getPrerequisiteList(), aPC, this))
			{
				continue;
			}
			if (!sc.hasClassLevel(level))
			{
				continue;
			}
			if (!SubstitutionLevelSupport.qualifiesForSubstitutionLevel(sc, aPC, level))
			{
				continue;
			}

			choiceList.add(sc);
		}
		Collections.sort(choiceList); // sort the SubstitutionClass's 
		choiceList.add(0, this); // THEN add the base class as the first choice
	}

	/*
	 * PCCLASSONLY This is really part of the PCClassLevel Factory, and
	 * therefore only needs to be placed in PCClass
	 */
	private void checkForSubClass(final PlayerCharacter aPC)
	{
		List<SubClass> subClassList = getListFor(ListKey.SUB_CLASS);
		if (subClassList == null || subClassList.isEmpty())
		{
			return;
		}

		List<String> columnNames = new ArrayList<String>(3);
		columnNames.add("Name");
		columnNames.add("Cost");
		columnNames.add("Other");

		List<List> choiceList = new ArrayList<List>();
		boolean subClassSelected = (!getSubClassKey().equals(Constants.s_NONE) && !getSubClassKey()
				.equals(""));

		for (SubClass sc : subClassList)
		{
			/*
			 * BUG MULTIPREREQS would fail here on a SubClass :( - thpr 11/4/06
			 * 
			 * STOP THE MAGIC, I want to delete MULTIPREREQs
			 */
			if (!PrereqHandler.passesAll(sc.getPrerequisiteList(), aPC, this))
			{
				continue;
			}

			final List<Object> columnList = new ArrayList<Object>(3);

			columnList.add(sc);
			columnList.add(Integer.toString(sc.getSafe(IntegerKey.COST)));
			columnList.add(sc.getSupplementalDisplayInfo());

			// If a subclass has already been selected, only add that one 
			if (!subClassSelected || getSubClassKey().equals(sc.getKeyName()))
			{
				choiceList.add(columnList);
			}
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
		if (getSafe(ObjectKey.ALLOWBASECLASS)
				&& (!subClassSelected || getKeyName().equals(getSubClassKey())))
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
			aPC.removeAllAssocs(this, AssociationListKey.PROHIBITED_SCHOOLS);
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
				if (!PrereqHandler.passesAll(sub.getPrerequisiteList(), aPC, this))
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

			setSubClassKey(aPC, sc.getKeyName());

			if (sc.get(ObjectKey.CHOICE) != null)
			{
				addSpecialty(sc.getChoice());
			}

			columnNames.add("Specialty");

			if (sc.getSafe(IntegerKey.COST) != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1
					.setMessageText("Make a selection.  You must make as many selections "
						+ "necessary to cover the cost of your previous selections.");
				c1.setTotalChoicesAvail(sc.getSafe(IntegerKey.COST));
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
					SpellProhibitor prohibSchool = new SpellProhibitor();
					prohibSchool.setType(ProhibitedSpellType.SCHOOL);
					prohibSchool.addValue(sc.getChoice());
					SpellProhibitor prohibSubSchool = new SpellProhibitor();
					prohibSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);
					prohibSubSchool.addValue(sc.getChoice());
					aPC.addAssoc(this, AssociationListKey.PROHIBITED_SCHOOLS,
						prohibSchool);
					aPC.addAssoc(this, AssociationListKey.PROHIBITED_SCHOOLS,
						prohibSubSchool);
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
		List<SubstitutionClass> substitutionClassList = getListFor(ListKey.SUBSTITUTION_CLASS);
		if (substitutionClassList == null || substitutionClassList.isEmpty())
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
			SubstitutionLevelSupport.applyLevelArrayModsToLevel(sc, this, aLevel, aPC);
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
	private void chooseClassSkillList(PlayerCharacter pc)
	{
		TransitionChoice<ClassSkillList> csc = get(ObjectKey.SKILLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (level < 1))
		{
			return;
		}

		pc.removeAllAssocs(this, AssociationListKey.CLASSSKILLLIST);
		for (ClassSkillList st : csc.driveChoice(null))
		{
			pc.addAssoc(pc, AssociationListKey.CLASSSKILLLIST, st);
		}
	}

	/*
	 * PCCLASSONLY Since this is part of the construction of a PCClassLevel,
	 * this is only part of PCClass...
	 */
	private void chooseClassSpellList(PlayerCharacter pc)
	{
		TransitionChoice<CDOMListObject<Spell>> csc = get(ObjectKey.SPELLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (level < 1))
		{
			return;
		}

		clearClassSpellList(pc);
		for (CDOMListObject<Spell> st : csc.driveChoice(null))
		{
			addClassSpellList(st, pc);
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

		List<QualifiedObject<CDOMReference<Equipment>>> e = otherClass
				.getListFor(ListKey.EQUIPMENT);
		if (e != null)
		{
			addAllToListFor(ListKey.EQUIPMENT, e);
		}

		List<QualifiedObject<CDOMReference<WeaponProf>>> wp = otherClass
				.getListFor(ListKey.WEAPONPROF);
		if (wp != null)
		{
			addAllToListFor(ListKey.WEAPONPROF, wp);
		}
		QualifiedObject<Boolean> otherWP = otherClass
				.get(ObjectKey.HAS_DEITY_WEAPONPROF);
		if (otherWP != null)
		{
			put(ObjectKey.HAS_DEITY_WEAPONPROF, otherWP);
		}

		List<ArmorProfProvider> ap = otherClass
				.getListFor(ListKey.AUTO_ARMORPROF);
		if (ap != null)
		{
			addAllToListFor(ListKey.AUTO_ARMORPROF, ap);
		}

		List<ShieldProfProvider> sp = otherClass
				.getListFor(ListKey.AUTO_SHIELDPROF);
		if (sp != null)
		{
			addAllToListFor(ListKey.AUTO_SHIELDPROF, sp);
		}

		List<BonusObj> bonusList = otherClass.getListFor(ListKey.BONUS);
		if (bonusList != null)
		{
			addAllToListFor(ListKey.BONUS, bonusList);
		}
		try
		{
			ownBonuses();
		}
		catch (CloneNotSupportedException ce)
		{
			// TODO Auto-generated catch block
			ce.printStackTrace();
		}

		for (VariableKey vk : otherClass.getVariableKeys())
		{
			put(vk, otherClass.get(vk));
		}

		if (otherClass.containsListFor(ListKey.CSKILL))
		{
			removeListFor(ListKey.CSKILL);
			addAllToListFor(ListKey.CSKILL, otherClass
					.getListFor(ListKey.CSKILL));
		}

		if (otherClass.containsListFor(ListKey.CCSKILL))
		{
			removeListFor(ListKey.CCSKILL);
			addAllToListFor(ListKey.CCSKILL, otherClass
					.getListFor(ListKey.CCSKILL));
		}

		removeListFor(ListKey.KIT_CHOICE);
		addAllToListFor(ListKey.KIT_CHOICE, otherClass
				.getSafeListFor(ListKey.KIT_CHOICE));

		remove(ObjectKey.REGION_CHOICE);
		if (otherClass.containsKey(ObjectKey.REGION_CHOICE))
		{
			put(ObjectKey.REGION_CHOICE, otherClass
					.get(ObjectKey.REGION_CHOICE));
		}

		removeListFor(ListKey.SAB);
		addAllToListFor(ListKey.SAB, otherClass.getSafeListFor(ListKey.SAB));
		
		/*
		 * TODO Does this need to have things from the Class Level objects?
		 * I don't think so based on deferred processing of levels...
		 */

		addAllToListFor(ListKey.DAMAGE_REDUCTION, otherClass
				.getListFor(ListKey.DAMAGE_REDUCTION));

		for (CDOMReference<Vision> ref : otherClass
				.getSafeListMods(Vision.VISIONLIST))
		{
			for (AssociatedPrereqObject apo : otherClass.getListAssociations(
					Vision.VISIONLIST, ref))
			{
				putToList(Vision.VISIONLIST, ref, apo);
			}
		}

		/*
		 * TODO This is a clone problem, but works for now - thpr 10/3/08
		 */
		if (otherClass instanceof SubClass)
		{
			levelMap.clear();
			copyLevelsFrom(otherClass);
			((SubClass) otherClass).applyLevelArrayModsTo(this);
		}

		addAllToListFor(ListKey.NATURAL_WEAPON, otherClass
				.getListFor(ListKey.NATURAL_WEAPON));

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
					DomainApplication.applyDomain(aPC, aDomain);
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
					+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + getKeyName());
		final int max =
				getLevelHitDie(aPC, aLevel).getDie()
					+ (int) aPC.getTotalBonusTo("HD", "MAX")
					+ (int) aPC.getTotalBonusTo("HD", "MAX;CLASS." + getKeyName());

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

	SortedMap<Integer, PCClassLevel> levelMap = new TreeMap<Integer, PCClassLevel>();

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
	
	public boolean hasClassLevel(int lvl)
	{
		return levelMap.containsKey(lvl);
	}

	public int getClassLevelCount()
	{
		return levelMap.size();
	}

	public Collection<PCClassLevel> getClassLevelCollection()
	{
		return Collections.unmodifiableCollection(levelMap.values());
	}

	public void copyLevelsFrom(PCClass cl)
	{
		for (Map.Entry<Integer, PCClassLevel> me : cl.levelMap.entrySet())
		{
			try
			{
				levelMap.put(me.getKey(), me.getValue().clone());
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public String getFullKey()
	{
		return getKeyName();
	}

	//Temporary hack
	@Override
	public String bonusStringPrefix()
	{
		return "0|";
	}

	/*
	 * This exists solely due to the token transition.  The new load method
	 * is during LST load (not deferred), so it can resolve references.  The 
	 * problem is that this means old tokens load into the SubClass, not 
	 * the PCClass and are lost.  So this is a hack to restore them 
	 * into the PCClass when the SubClass is applied, without disrupting 
	 * the class levels that are in the PCClass. (which would cause duplication 
	 * or other errors)
	 */
	public void performReallyBadHackForOldTokens(DeferredLine line)
	{
		SortedMap<Integer, PCClassLevel> saveLevelMap = levelMap;
		levelMap = new TreeMap<Integer, PCClassLevel>();
		final PCClassLoader classLoader = new PCClassLoader();
		try
		{
			classLoader.parseLine(Globals.getContext(), this, line.lstLine, line.source);
		}
		catch (PersistenceLayerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		levelMap = saveLevelMap;
	}

	public void stealClassLevel(PCClass pcc, int cl)
	{
		try
		{
			levelMap.put(cl, pcc.getClassLevel(cl).clone());
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public PObject getActiveEquivalent(PlayerCharacter pc)
	{
		return pc.getClassKeyed(getKeyName());
	}

	@Override
	public List<BonusObj> getRawBonusList(PlayerCharacter pc)
	{
		List<BonusObj> list = super.getRawBonusList(pc);
		for (int i = 1; i <= level; i++)
		{
			PCClassLevel pcl = getClassLevel(i);
			if (pcl != null)
			{
				List<BonusObj> bonusList = pcl.getListFor(ListKey.BONUS);
				if (bonusList != null)
				{
					list.addAll(bonusList);
				}
				if (pc != null)
				{
					List<BonusObj> listToo = pc.getAssocList(pcl,
							AssociationListKey.BONUS);
					if (listToo != null)
					{
						list.addAll(listToo);
					}
				}
			}
		}
		return list;
	}

	@Override
	public void ownBonuses() throws CloneNotSupportedException
	{
		super.ownBonuses();
		for (PCClassLevel pcl : this.getClassLevelCollection())
		{
			pcl.ownBonuses();
		}
	}
}
