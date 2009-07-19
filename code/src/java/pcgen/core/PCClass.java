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
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.helper.ShieldProfProvider;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.analysis.ClassSkillApplication;
import pcgen.core.analysis.ClassSpellApplication;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.ExchangeLevelApplication;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.analysis.SkillCostCalc;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.StatApplication;
import pcgen.core.analysis.SubClassApplication;
import pcgen.core.analysis.SubstitutionClassApplication;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.ReferenceContext;
import pcgen.util.Logging;
import pcgen.util.enumeration.AttackType;

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

	/*
	 * PCCLASSLEVELONLY This is only relevant for the PCClassLevel (obviously?)
	 */
	public final int getLevel(PlayerCharacter pc)
	{
		Integer level = pc.getAssoc(this, AssociationKey.CLASS_LEVEL);
		return level == null ? 0 : level;
	}

	/**
	 * set the level to arg without impacting spells, hp, or anything else - use
	 * this with great caution only TODO Then why is it even here, What is it
	 * used for (JSC 07/21/03)
	 * @param pc TODO
	 * @param arg
	 */
	/*
	 * DELETEMETHOD This method is NOT appropriate for either PCClass or
	 * PCClassLevel.  The equivalent functionality in order to sustain the
	 * maxbablevel and maxcheckslevel globals will have to be done by
	 * PlayerCharacter as it filters out the PCClassLevels that are allowed
	 * to be used for those calculations.
	 */
	public final void setLevelWithoutConsequence(PlayerCharacter pc, final int arg)
	{
		pc.setAssoc(this, AssociationKey.CLASS_LEVEL, arg);
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
	public String getDisplayClassName(PlayerCharacter pc)
	{
		if (pc != null)
		{
			String subClassKey = pc.getAssoc(this, AssociationKey.SUBCLASS_KEY);
			if (subClassKey != null && (subClassKey.length() > 0)
					&& !subClassKey.equals(Constants.s_NONE))
			{
				SubClass sc = getSubClassKeyed(subClassKey);
				if (sc != null)
				{
					return sc.getDisplayName();
				}
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
	public String getDisplayClassName(PlayerCharacter pc, final int aLevel)
	{
		PCClassLevel lvl = getActiveClassLevel(aLevel);
		String aKey = pc.getAssoc(lvl, AssociationKey.SUBSTITUTIONCLASS_KEY);
		if (aKey == null)
		{
			return getDisplayClassName(pc);
		}
		String name = getSubstitutionClassKeyed(aKey).getDisplayName();
		if (name == null)
		{
			return getDisplayClassName(pc);
		}

		return name;
	}

	/*
	 * PCCLASSLEVELONLY Must only be the PCClassLevel since this refers to the
	 * level in the String that is returned.
	 */
	public String getFullDisplayClassName(PlayerCharacter pc)
	{
		final StringBuffer buf = new StringBuffer();

		buf.append(getDisplayClassName(pc));

		return buf.append(" ").append(getLevel(pc)).toString();
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
		PCClassLevel cl = getActiveClassLevel(classLevel);
		if (cl != null)
		{
			if (cl.get(ObjectKey.DONTADD_HITDIE) != null)
			{
				currDie = HitDie.ZERO;	//null;
			}
			else
			{
				Modifier<HitDie> lock = cl.get(ObjectKey.HITDIE);
				if (lock != null)
				{
					currDie = lock.applyModifier(currDie, this);
				}
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
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType()
	{
		String castInfo = getSafe(StringKey.SPELLTYPE);
		return castInfo == null ? Constants.s_NONE : castInfo;
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

	public void setLevel(final int newLevel, final PlayerCharacter aPC)
	{
		final int curLevel = getLevel(aPC);

		if (newLevel >= 0)
		{
			setLevelWithoutConsequence(aPC, newLevel);
		}

		if (newLevel == 1)
		{
			if (newLevel > curLevel || aPC.isImporting())
			{
				addFeatPoolBonus(aPC);
			}

			ClassSkillApplication.chooseClassSkillList(aPC, this);
		}

		if (!aPC.isImporting())
		{
			aPC.calcActiveBonuses();
			//Need to do this again if caching is re-integrated
			//aPC.getSpellTracker().buildSpellLevelMap(newLevel);
		}

		if ((newLevel == 1) && !aPC.isImporting() && (curLevel == 0))
		{
			SubClassApplication.checkForSubClass(aPC, this);
			getSpellLists(aPC);
		}

		if (!aPC.isImporting() && (curLevel < newLevel))
		{
			SubstitutionClassApplication.checkForSubstitutionClass(this, newLevel, aPC);
		}

		for (PCClass pcClass : aPC.getClassList())
		{
			aPC.getSpellSupport(pcClass).calculateKnownSpellsForClassLevel(aPC);
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
				bon.setSaveToPCG(false);
				aPC.addAssoc(this, AssociationListKey.BONUS, bon);
			}
		}
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

		for (Type type : getTrueTypeList(false))
		{
			final ClassType aClassType =
					SettingsHandler.getGame().getClassTypeByName(type.toString());

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
		pccTxt.append("\t");
		pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));

		// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
		{
			pccTxt.append(lineSep).append(me.getKey()).append('\t');
			pccTxt.append(StringUtil.joinToStringBuffer(Globals.getContext()
					.unparse(me.getValue()), "\t"));
		}

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
			if ((bonus.getPCLevel() <= getLevel(aPC)))
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
						bonus.setApplied(aPC, true);
					}
					else
					{
						bonus.setApplied(aPC, false);
					}
				}
				else
				{
					bonus.setApplied(aPC, true);
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
		pc.removeAllAssocs(this, AssociationListKey.SPELL_LIST_CACHE);
	}

	/*
	 * FINALPCCLASSONLY This is really an item that the PCClass knows, and then the
	 * selected subClass, if any, is structured into the PCClassLevel during the
	 * construction of the PCClassLevel
	 */
	public final void addSubClass(final SubClass sClass)
	{
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
		for (Map.Entry<AttackType, Integer> me : getMapFor(MapKey.ATTACK_CYCLE)
				.entrySet())
		{
			if (at.equals(me.getKey()))
			{
				return me.getValue();
			}
		}
		return SettingsHandler.getGame().getBabAttCyc();
	}

	public int baseAttackBonus(final PlayerCharacter aPC)
	{
		if (getLevel(aPC) == 0)
		{
			return 0;
		}

		// final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int)
		// getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB", getLevel(aPC), aPC);

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
	public PCStat baseSpellStat()
	{
		if (getSafe(ObjectKey.USE_SPELL_SPELL_STAT))
		{
			return null;
		}
		if (getSafe(ObjectKey.CASTER_WITHOUT_SPELL_STAT))
		{
			return null;
		}
		PCStat ss = get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
			return ss;
		}
		Logging.debugPrint("Found Class: " + getDisplayName()
				+ " that did not have any SPELLSTAT defined");
		return null;
	}

	/**
	 * Returns the stat to use for bonus spells.
	 *
	 * <p>
	 * The method checks to see if a BONUSSPELLSTAT: has been set for the class.
	 * If it is set to a stat that stat is returned. If it is set to None null is
	 * returned. If it is set to Default then the BASESPELLSTAT is returned.
	 *
	 * @return the stat to use for bonus spells.
	 */
	public PCStat bonusSpellStat()
	{
		Boolean hbss = get(ObjectKey.HAS_BONUS_SPELL_STAT);
		if (hbss == null)
		{
			return baseSpellStat();
		}
		else if (hbss)
		{
			return get(ObjectKey.BONUS_SPELL_STAT);
		}
		else
		{
			return null;
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
			for (Type type : getTrueTypeList(false))
			{
				final ClassType aClassType =
						SettingsHandler.getGame().getClassTypeByName(type.toString());
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

	@Override
	public PCClass clone()
	{
		PCClass aClass = null;

		try
		{
			aClass = (PCClass) super.clone();

			List<KnownSpellIdentifier> ksl = getListFor(ListKey.KNOWN_SPELLS);
			if (ksl != null)
			{
				aClass.removeListFor(ListKey.KNOWN_SPELLS);
				for (KnownSpellIdentifier ksi : ksl)
				{
					aClass.addToListFor(ListKey.KNOWN_SPELLS, ksi);
				}
			}
			Map<AttackType, Integer> acmap = getMapFor(MapKey.ATTACK_CYCLE);
			if (acmap != null && !acmap.isEmpty())
			{
				aClass.removeMapFor(MapKey.ATTACK_CYCLE);
				for (Map.Entry<AttackType, Integer> me : acmap.entrySet())
				{
					aClass.addToMapFor(MapKey.ATTACK_CYCLE, me.getKey(), me
							.getValue());
				}
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
		return getDisplayName();
	}

	/*
	 * FINALPCCLASSLEVELONLY This is only part of the level, as the skill list is
	 * calculated based on other factors, it is not a Tag
	 */
	public boolean hasClassSkill(PlayerCharacter pc, Skill skill)
	{
		List<ClassSkillList> classSkillList = pc.getAssocList(this, AssociationListKey.CLASSSKILLLIST);
		if ((classSkillList == null) || classSkillList.isEmpty())
		{
			return false;
		}

		for (ClassSkillList key : classSkillList)
		{
			final PCClass pcClass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, key.getLSTformat());

			if ((pcClass != null) && SkillCostCalc.hasCSkill(pc, pcClass, skill))
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasSkill(PlayerCharacter pc, Skill skill)
	{
		return hasSkill(pc, skill, this);
	}

	private boolean hasSkill(PlayerCharacter pc, Skill skill, CDOMObject cdo)
	{
		List<Skill> assocCSkill = pc.getAssocList(cdo, AssociationListKey.CSKILL);
		return assocCSkill != null && assocCSkill.contains(skill);
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
		for (Type type : getTrueTypeList(false))
		{
			final ClassType aClassType =
					SettingsHandler.getGame().getClassTypeByName(type.toString());
				if ((aClassType != null) && !aClassType.getXPPenalty())
			{
				return false;
			}
		}
		return true;
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
		SizeAdjustment pcSize = aPC.getSizeAdjustment();

		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		ReferenceContext ref = Globals.getContext().ref;
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
			aDamage = Globals.adjustDamage(aDamage, SizeUtilities
					.getDefaultSizeAdjustment(), pcSize);
		}

		//
		// Check the UDAM list for monk-like damage
		//
		List<CDOMObject> classObjects = new ArrayList<CDOMObject>();
		//Negative increment to start at highest level until an UDAM is found
		for (int i = aLevel; i >= 1; i--)
		{
			classObjects.add(getActiveClassLevel(i));
		}
		classObjects.add(this);
		int iSize = aPC.sizeInt();
		for (CDOMObject cdo : classObjects)
		{
			List<String> udam = cdo.getListFor(ListKey.UNARMED_DAMAGE);
			if (udam != null)
			{
				if (udam.size() == 1)
				{
					aDamage = udam.get(0);
				}
				else
				{
					aDamage = udam.get(iSize);
				}
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
		final int newLevel = getLevel(aPC) + 1;
		boolean levelMax = argLevelMax;

		setLevelWithoutConsequence(aPC, newLevel);
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
						"This character does not qualify for level " + newLevel,
						Constants.s_APPNAME, MessageType.ERROR);
				}
			}
			setLevelWithoutConsequence(aPC, newLevel - 1);
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
		PCClassLevel classLevel = getActiveClassLevel(newLevel);
		aPC.selectTemplates(classLevel, aPC.isImporting());

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		final int dnum =
				aPC.getMaxCharacterDomains(this, aPC)
					- aPC.getDomainCount();

		if (dnum > 0 && !aPC.hasDefaultDomainSource())
		{
			aPC.setDefaultDomainSource(new ClassSource(this, newLevel));
		}

		aPC.setAutomaticAbilitiesStable(null, false);
		//		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel, aPC, pcLevelInfo);

		// Don't roll the hit points if the gui is not being used.
		// This is so GMGen can add classes to a person without pcgen flipping
		// out
		if (Globals.getUseGUI())
		{
			rollHP(aPC, getLevel(aPC), (SettingsHandler.isHPMaxAtFirstClassLevel()
				? aPC.totalNonMonsterLevels() : aPC.getTotalLevels()) == 1);
		}

		if (!aPC.isImporting())
		{
			DomainApplication.modDomainsForLevel(this, newLevel, true, aPC);
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
									StatApplication.askForStatIncrease(aPC, bonusStats, true);
						}
					}
				}
			}
		}

		// Update Skill Points. Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		//final int spMod;
		int spMod = recalcSkillPointMod(aPC, total);
		if (classLevel.get(ObjectKey.DONTADD_SKILLPOINTS) != null)
		{
			spMod = 0;
		}

		PCLevelInfo pcl;

		if (aPC.getLevelInfoSize() > 0)
		{
			pcl = aPC.getLevelInfo().get(aPC.getLevelInfoSize() - 1);

			if (pcl != null)
			{
				pcl.setLevel(getLevel(aPC));
				pcl.setSkillPointsGained(spMod);
				pcl.setSkillPointsRemaining(pcl.getSkillPointsGained());
			}
		}

		Integer currentPool = aPC.getAssoc(this, AssociationKey.SKILL_POOL);
		int newSkillPool = spMod + (currentPool == null ? 0 : currentPool);
		aPC.setAssoc(this, AssociationKey.SKILL_POOL, newSkillPool);

		aPC.setSkillPoints(spMod + aPC.getSkillPoints());

		if (!aPC.isImporting())
		{
			//
			// Ask for stat increase after skill points have been calculated
			//
			if (levelUpStats > 0)
			{
				StatApplication.askForStatIncrease(aPC, levelUpStats, false);
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
				CDOMObjectUtilities.addAdds(this, aPC);
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
			CDOMObjectUtilities.checkRemovals(this, aPC);
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
		if (containsKey(ObjectKey.EXCHANGE_LEVEL) && (getLevel(aPC) == 1)
				&& !aPC.isImporting())
		{
			ExchangeLevelApplication.exchangeLevels(aPC, this);
		}
		return true;
	}

	/*
	 * DELETEMETHOD I hope this can be deleted, since minus level support will not
	 * work the same way in the new PCClass/PCClassLevel world. If nothing else, it
	 * is massively a REFACTOR item to put this into the PlayerCharacter that is
	 * doing the removal.
	 */
	void doMinusLevelMods(final PlayerCharacter aPC, final int oldLevel)
	{
		CDOMObjectUtilities.removeAdds(getActiveClassLevel(oldLevel), aPC);
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
		if (!aPC.isImporting() && aPC.doLevelAbilities())
		{
			PCClassLevel activeClassLevel = getActiveClassLevel(newLevel);
			CDOMObjectUtilities.addAdds(activeClassLevel, aPC);
			CDOMObjectUtilities.checkRemovals(activeClassLevel, aPC);
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
		for (PCClassLevel pcl : getOriginalClassLevelCollection())
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
						cdo.addToListFor(ListKey.BONUS, aBonus);
					}
					cdo.removeFromListFor(ListKey.BONUS, bonusObj);
				}
			}
		}
	}

	void subLevel(final boolean bSilent, final PlayerCharacter aPC)
	{

		if (aPC != null)
		{
			int total = aPC.getTotalLevels();

			int oldLevel = getLevel(aPC);
			int spMod = 0;
			final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), oldLevel);

			if (pcl != null)
			{
				spMod = pcl.getSkillPointsGained();
			}
			else
			{
				Logging
					.errorPrint("ERROR: could not find class/level info for "
						+ getDisplayName() + "/" + oldLevel);
			}

			final int newLevel = oldLevel - 1;
			PCClassLevel classLevel = getActiveClassLevel(oldLevel);

			if (oldLevel > 0)
			{
				aPC.removeAssoc(classLevel, AssociationKey.HIT_POINTS);
			}

			//			aPC.adjustFeats(-aPC.getBonusFeatsForNewLevel(this));
			setLevel(newLevel, aPC);
			aPC.getSpellSupport(this).removeKnownSpellsForClassLevel(aPC);

			doMinusLevelMods(aPC, newLevel + 1);

			DomainApplication.modDomainsForLevel(this, newLevel, false, aPC);

			if (newLevel == 0)
			{
				SubClassApplication.setSubClassKey(aPC, this, Constants.s_NONE);

				//
				// Remove all skills associated with this class
				//
				for (Skill skill : aPC.getSkillList())
				{
					SkillRankControl.setZeroRanks(this, aPC, skill);
				}

				Integer currentPool = aPC.getAssoc(this, AssociationKey.SKILL_POOL);
				spMod = currentPool == null ? 0 : currentPool;
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
						for (PCStat aStat : aPC.getUnmodifiableStatList())
						{
							if (aStat.equals(statToRollback.getStat()))
							{
								aPC.setAssoc(aStat, AssociationKey.STAT_SCORE, aPC.getAssoc(aStat, AssociationKey.STAT_SCORE)
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
				Integer currentPool = aPC.getAssoc(this, AssociationKey.SKILL_POOL);
				int newSkillPool = (currentPool == null ? 0 : currentPool) - spMod;
				aPC.setAssoc(this, AssociationKey.SKILL_POOL, newSkillPool);
			}

			if (getLevel(aPC) == 0)
			{
				aPC.getClassList().remove(this);
			}

			aPC.validateCharacterDomains();

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
			cdo = getActiveClassLevel(aLevel);
		}
		for (VariableKey vk : cdo.getVariableKeys())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(prefix).append('|').append(vk.toString()).append('|')
					.append(cdo.get(vk));
			aPC.addVariable(sb.toString());
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
	public void inheritAttributesFrom(final PCClass otherClass)
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

		List<WeaponProfProvider> wp = otherClass.getListFor(ListKey.WEAPONPROF);
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
		QualifiedObject<Boolean> otherAllWP = otherClass
				.get(ObjectKey.HAS_ALL_WEAPONPROF);
		if (otherAllWP != null)
		{
			put(ObjectKey.HAS_ALL_WEAPONPROF, otherWP);
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
		}

		addAllToListFor(ListKey.NATURAL_WEAPON, otherClass
				.getListFor(ListKey.NATURAL_WEAPON));

		put(ObjectKey.LEVEL_HITDIE, otherClass.get(ObjectKey.LEVEL_HITDIE));
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

		HitDie lvlDie = getLevelHitDie(aPC, aLevel);
		if ((lvlDie == null) || (lvlDie.getDie() == 0))
		{
			roll = 0;
		}
		else
		{
			final int min =
					1 + (int) aPC.getTotalBonusTo("HD", "MIN")
						+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + getKeyName());
			final int max =
					getLevelHitDie(aPC, aLevel).getDie()
						+ (int) aPC.getTotalBonusTo("HD", "MAX")
						+ (int) aPC.getTotalBonusTo("HD", "MAX;CLASS." + getKeyName());

			if (Globals.getGameModeHPFormula().length() == 0)
			{
				if ((first && (aLevel == 1)) && SettingsHandler.isHPMaxAtFirstLevel())
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
		}
		PCClassLevel classLevel = getActiveClassLevel(aLevel - 1);
		aPC.setAssoc(classLevel, AssociationKey.HIT_POINTS, Integer.valueOf(roll));
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

	private SortedMap<Integer, PCClassLevel> levelMap = new TreeMap<Integer, PCClassLevel>();

	public PCClassLevel getActiveClassLevel(int lvl)
	{
		//For now, eventually this will be in PC
		return getOriginalClassLevel(lvl);
	}

	public PCClassLevel getOriginalClassLevel(int lvl)
	{
		if (!levelMap.containsKey(lvl))
		{
			PCClassLevel classLevel = new PCClassLevel();
			classLevel.put(IntegerKey.LEVEL, Integer.valueOf(lvl));
			classLevel.setName(getDisplayName() + "(" + lvl + ")");
			classLevel.put(ObjectKey.TOKEN_PARENT, this);
			levelMap.put(lvl, classLevel);
		}
		return levelMap.get(lvl);
	}

	public boolean hasOriginalClassLevel(int lvl)
	{
		return levelMap.containsKey(lvl);
	}

	public Collection<PCClassLevel> getOriginalClassLevelCollection()
	{
		return Collections.unmodifiableCollection(levelMap.values());
	}

	public void copyLevelsFrom(PCClass cl)
	{
		for (Map.Entry<Integer, PCClassLevel> me : cl.levelMap.entrySet())
		{
			try
			{
				PCClassLevel lvl = me.getValue().clone();
				lvl.put(ObjectKey.TOKEN_PARENT, this);
				levelMap.put(me.getKey(), lvl);
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public void stealClassLevel(PCClass pcc, int cl)
	{
		try
		{
			PCClassLevel lvl = pcc.getOriginalClassLevel(cl).clone();
			levelMap.put(cl, lvl);
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<BonusObj> getRawBonusList(PlayerCharacter pc)
	{
		List<BonusObj> list = super.getRawBonusList(pc);
		int lvl = getLevel(pc);
		for (int i = 1; i <= lvl; i++)
		{
			PCClassLevel pcl = getActiveClassLevel(i);
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
	public List<? extends CDOMList<Spell>> getSpellLists(PlayerCharacter pc)
	{
		List<CDOMList<Spell>> stableSpellList = pc.getAssocList(this,
				AssociationListKey.SPELL_LIST_CACHE);
		if (stableSpellList != null)
		{
			return stableSpellList;
		}

		List<CDOMListObject<Spell>> classSpellList = pc.getAssocList(this,
				AssociationListKey.CLASSSPELLLIST);
		if (classSpellList == null)
		{
			ClassSpellApplication.chooseClassSpellList(pc, this);

			classSpellList = pc.getAssocList(this,
					AssociationListKey.CLASSSPELLLIST);

			if (classSpellList == null)
			{
				ClassSpellList defaultList = get(ObjectKey.CLASS_SPELLLIST);
				pc.addAssoc(this, AssociationListKey.SPELL_LIST_CACHE, defaultList);
				return Collections.singletonList(defaultList);
			}
		}

		for (CDOMListObject<Spell> keyStr : classSpellList)
		{
			pc.addAssoc(this, AssociationListKey.SPELL_LIST_CACHE, keyStr);
		}
		return classSpellList;
	}

	@Override
	public String getVariableSource()
	{
		return "CLASS|" + this.getKeyName();
	}
}
