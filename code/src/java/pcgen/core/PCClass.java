/*
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
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TreeMap;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.helper.ShieldProfProvider;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.analysis.AddObjectActions;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.ExchangeLevelApplication;
import pcgen.core.analysis.SizeUtilities;
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
import pcgen.facade.core.InfoFacade;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.Logging;
import pcgen.util.enumeration.AttackType;

import org.apache.commons.lang3.StringUtils;


public class PCClass extends PObject implements InfoFacade, Cloneable
{

	public static final CDOMReference<DomainList> ALLOWED_DOMAINS;

	static
	{
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
		FactKey<String> fk = FactKey.valueOf("Abb");
		String abb = getResolved(fk);
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
	public double getBonusTo(final String argType, final String argMname, final int asLevel, final PlayerCharacter aPC)
	{
		double i = 0;

		List<BonusObj> rawBonusList = getRawBonusList(aPC);

		for (int lvl = 1; lvl < asLevel; lvl++)
		{
			rawBonusList.addAll(aPC.getActiveClassLevel(this, lvl).getRawBonusList(aPC));
		}
		if ((asLevel == 0) || rawBonusList.isEmpty())
		{
			return 0;
		}

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();

		for (final BonusObj bonus : rawBonusList)
		{
			final StringTokenizer breakOnPipes =
					new StringTokenizer(bonus.toString().toUpperCase(), Constants.PIPE, false);
			final String theType = breakOnPipes.nextToken();

			if (!theType.equals(type))
			{
				continue;
			}

			final String str = breakOnPipes.nextToken();
			final StringTokenizer breakOnCommas = new StringTokenizer(str, Constants.COMMA, false);

			while (breakOnCommas.hasMoreTokens())
			{
				final String theName = breakOnCommas.nextToken();

				if (theName.equals(mname))
				{
					final String aString = breakOnPipes.nextToken();
					final List<Prerequisite> localPreReqList = new ArrayList<>();
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
							Logging.debugPrint(
							"Why is this prerequisite '" + bString + "' parsed in '" //$NON-NLS-1$//$NON-NLS-2$
							+ getClass().getName()
							+ ".getBonusTo(String,String,int)' rather than in the persistence layer?"); //$NON-NLS-1$
							try
							{
								final PreParserFactory factory = PreParserFactory.getInstance();
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
						final double j = aPC.getVariableValue(aString, getQualifiedKey()).doubleValue();
						i += j;
					}
				}
			}
		}

		return i;
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
		CDOMSingleRef<PCStat> ss = get(ObjectKey.SPELL_STAT);
		//TODO This could be null, do we need to worry about it?
		return ss.get().getKeyName();
	}

	/*
	 * FINALPCCLASSANDLEVEL This is required in PCClassLevel and should be present in
	 * PCClass for PCClassLevel creation (in the factory)
	 */
	public final String getSpellType()
	{
		FactKey<String> fk = FactKey.valueOf("SpellType");
		String castInfo = getResolved(fk);
		return StringUtils.isEmpty(castInfo) ? Constants.NONE : castInfo;
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
		final int curLevel = aPC.getLevel(this);

		if (newLevel >= 0)
		{
			aPC.setLevelWithoutConsequence(this, newLevel);
		}

		if (newLevel == 1)
		{
			if (newLevel > curLevel || aPC.isImporting())
			{
				addFeatPoolBonus(aPC);
			}
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
			aPC.setSpellLists(this);
		}

		if (!aPC.isImporting() && (curLevel < newLevel))
		{
			SubstitutionClassApplication.checkForSubstitutionClass(this, newLevel, aPC);
		}

		aPC.getClassSet()
		   .forEach(aPC::calculateKnownSpellsForClassLevel);
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
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			startLevel = Integer.parseInt(aTok.nextToken());
			rangeLevel = Integer.parseInt(aTok.nextToken());
			divisor = rangeLevel;
			if (divisor > 0)
			{
				StringBuilder aBuf = new StringBuilder("FEAT|PCPOOL|").append("max(CL");
				// Make sure we only take off the startlevel value once
				if (this == aPC.getClassKeyed(aPC.getLevelInfoClassKeyName(0)))
				{
					aBuf.append("-").append(startLevel);
					aBuf.append("+").append(rangeLevel);
				}
				aBuf.append(",0)/").append(divisor);
				//						Logging.debugPrint("Feat bonus for " + this + " is "
				//							+ aBuf.toString());
				BonusObj bon = Bonus.newBonus(Globals.getContext(), aBuf.toString());
				aPC.addBonus(bon, this);
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
			return mon;
		}

		ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(getClassType());

		if ((aClassType != null) && aClassType.isMonster())
		{
			return true;
		}
		else
		{
			for (Type type : getTrueTypeList(false))
			{
				aClassType = SettingsHandler.getGame().getClassTypeByName(type.toString());
				if ((aClassType != null) && aClassType.isMonster())
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getPCCText()
	{
		StringJoiner txt = new StringJoiner("\t");
		txt.add("CLASS:" + getDisplayName());
		txt.add(PrerequisiteWriter.prereqsToString(this));
		Globals.getContext().unparse(this).forEach(item -> txt.add(item));

		// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
		{
			txt.add(lineSep + me.getKey());
			txt.add(PrerequisiteWriter.prereqsToString(me.getValue()));
			Globals.getContext().unparse(me.getValue()).forEach(item -> txt.add(item));
		}

		return txt.toString();
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
	 * returns the value at which another attack is gained attackCycle of 4
	 * means a second attack is gained at a BAB of +5/+1
	 *
	 * @param at the AttackType
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
		for (Map.Entry<AttackType, Integer> me : getMapFor(MapKey.ATTACK_CYCLE).entrySet())
		{
			if (at == me.getKey())
			{
				return me.getValue();
			}
		}
		return SettingsHandler.getGame().getBabAttCyc();
	}

	public int baseAttackBonus(final PlayerCharacter aPC)
	{
		if (aPC.getLevel(this) == 0)
		{
			return 0;
		}

		return (int) getBonusTo("COMBAT", "BASEAB", aPC.getLevel(this), aPC);
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
		CDOMSingleRef<PCStat> ss = get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
			return ss.get();
		}
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Found Class: " + getDisplayName() + " that did not have any SPELLSTAT defined");
		}
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
			CDOMSingleRef<PCStat> bssref = get(ObjectKey.BONUS_SPELL_STAT);
			if (bssref != null)
			{
				return bssref.get();
			}
		}
		return null;
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
					aClass.addToMapFor(MapKey.ATTACK_CYCLE, me.getKey(), me.getValue());
				}
			}

			aClass.levelMap = new TreeMap<>();
			for (Map.Entry<Integer, PCClassLevel> me : levelMap.entrySet())
			{
				aClass.levelMap.put(me.getKey(), me.getValue().clone());
			}
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
		}

		return aClass;
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
			final ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(type.toString());
			if ((aClassType != null) && !aClassType.getXPPenalty())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the unarmed Damage for this class at the given level.
	 *
	 * @param aLevel the given level.
	 * @param aPC the PC with the level.
	 * @param adjustForPCSize whether to adjust the result for the PC's size.
	 * @return the unarmed damage string
	 */
	public String getUdamForLevel(int aLevel, final PlayerCharacter aPC, boolean adjustForPCSize)
	{
		aLevel += (int) aPC.getTotalBonusTo("UDAM", "CLASS." + getKeyName());
		return getUDamForEffLevel(aLevel, aPC, adjustForPCSize);
	}

	/**
	 * Get the unarmed Damage for this class at the given level.
	 *
	 * @param aLevel the given level.
	 * @param aPC the PC with the level.
	 * @param adjustForPCSize whether to adjust the result for the PC's size.
	 * @return the unarmed damage string
	 */
	String getUDamForEffLevel(int aLevel, final PlayerCharacter aPC, boolean adjustForPCSize)
	{
		int pcSize = adjustForPCSize ? aPC.sizeInt() : aPC.racialSizeInt();

		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		final Equipment eq = ref.silentlyGetConstructedCDOMObject(Equipment.class, "KEY_Unarmed Strike");

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
			int defSize = SizeUtilities.getDefaultSizeAdjustment().get(IntegerKey.SIZEORDER);
			aDamage = Globals.adjustDamage(aDamage, pcSize - defSize);
		}

		//
		// Check the UDAM list for monk-like damage
		//
		List<CDOMObject> classObjects = new ArrayList<>();
		//Negative increment to start at highest level until an UDAM is found
		for (int i = aLevel; i >= 1; i--)
		{
			classObjects.add(aPC.getActiveClassLevel(this, i));
		}
		classObjects.add(this);
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
					aDamage = udam.get(pcSize);
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
	public boolean addLevel(final boolean argLevelMax, final boolean bSilent, final PlayerCharacter aPC,
		final boolean ignorePrereqs)
	{

		// Check to see if we can add a level of this class to the
		// current character
		final int newLevel = aPC.getLevel(this) + 1;
		boolean levelMax = argLevelMax;

		aPC.setAllowInteraction(false);
		aPC.setLevelWithoutConsequence(this, newLevel);
		if (!ignorePrereqs)
		{
			// When loading a character, classes are added before feats, so
			// this test would always fail on loading if feats are required
			boolean doReturn = false;
			if (!qualifies(aPC, this))
			{
				doReturn = true;
				if (!bSilent)
				{
					ShowMessageDelegate.showMessageDialog("This character does not qualify for level " + newLevel,
						Constants.APPLICATION_NAME, MessageType.ERROR);
				}
			}
			aPC.setLevelWithoutConsequence(this, newLevel - 1);
			if (doReturn)
			{
				return false;
			}
		}
		aPC.setAllowInteraction(true);

		if (isMonster())
		{
			levelMax = false;
		}

		if (hasMaxLevel() && (newLevel > getSafe(IntegerKey.LEVEL_LIMIT)) && levelMax)
		{
			if (!bSilent)
			{
				ShowMessageDelegate.showMessageDialog(
					"This class cannot be raised above level " + Integer.toString(getSafe(IntegerKey.LEVEL_LIMIT)),
					Constants.APPLICATION_NAME, MessageType.ERROR);
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
		PCClassLevel classLevel = aPC.getActiveClassLevel(this, newLevel);

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		final int dnum = aPC.getMaxCharacterDomains(this, aPC) - aPC.getDomainCount();

		if (dnum > 0 && !aPC.hasDefaultDomainSource())
		{
			aPC.setDefaultDomainSource(new ClassSource(this, newLevel));
		}

		// Don't roll the hit points if the gui is not being used.
		// This is so GMGen can add classes to a person without pcgen flipping
		// out
		if (Globals.getUseGUI())
		{
			final int levels =
					SettingsHandler.isHPMaxAtFirstClassLevel() ? aPC.totalNonMonsterLevels() : aPC.getTotalLevels();
			final boolean isFirst = levels == 1;

			aPC.rollHP(this, aPC.getLevel(this), isFirst);
		}

		if (!aPC.isImporting())
		{
			DomainApplication.addDomainsUpToLevel(this, newLevel, aPC);
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

				//if (processBonusFeats) {
				//	final double bonusFeats = aPC.getBonusFeatsForNewLevel(this);
				//	if (bonusFeats > 0) {
				//		aPC.adjustFeats(bonusFeats);
				//	}
				//}

				if (processBonusStats)
				{
					final int bonusStats = Globals.getBonusStatsForLevel(total, aPC);
					if (bonusStats > 0)
					{
						aPC.setPoolAmount(aPC.getPoolAmount() + bonusStats);

						if (!bSilent && SettingsHandler.getShowStatDialogAtLevelUp())
						{
							levelUpStats = StatApplication.askForStatIncrease(aPC, bonusStats, true);
						}
					}
				}
			}
		}

		int spMod = getSkillPointsForLevel(aPC, classLevel, total);

		PCLevelInfo pcl;

		if (aPC.getLevelInfoSize() > 0)
		{
			pcl = aPC.getLevelInfo(aPC.getLevelInfoSize() - 1);

			if (pcl != null)
			{
				pcl.setClassLevel(aPC.getLevel(this));
				pcl.setSkillPointsGained(aPC, spMod);
				pcl.setSkillPointsRemaining(pcl.getSkillPointsGained(aPC));
			}
		}

		Integer currentPool = aPC.getSkillPool(this);
		int newSkillPool = spMod + (currentPool == null ? 0 : currentPool);
		aPC.setSkillPool(this, newSkillPool);

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
				AddObjectActions.doBaseChecks(this, aPC);
				CDOMObjectUtilities.addAdds(this, aPC);
				CDOMObjectUtilities.checkRemovals(this, aPC);
			}

			for (TransitionChoice<Kit> kit : classLevel.getSafeListFor(ListKey.KIT_CHOICE))
			{
				kit.act(kit.driveChoice(aPC), classLevel, aPC);
			}
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
					ShowMessageDelegate.showMessageDialog(SettingsHandler.getGame().getLevelUpMessage(),
						Constants.APPLICATION_NAME, MessageType.INFORMATION);
				}
			}
		}

		//
		// Allow exchange of classes only when assign 1st level
		//
		if (containsKey(ObjectKey.EXCHANGE_LEVEL) && (aPC.getLevel(this) == 1) && !aPC.isImporting())
		{
			ExchangeLevelApplication.exchangeLevels(aPC, this);
		}
		return true;
	}

	public int getSkillPointsForLevel(final PlayerCharacter aPC, PCClassLevel classLevel, int characterLevel)
	{
		// Update Skill Points. Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		//final int spMod;
		int spMod = aPC.recalcSkillPointMod(this, characterLevel);
		if (classLevel.get(ObjectKey.DONTADD_SKILLPOINTS) != null)
		{
			spMod = 0;
		}
		return spMod;
	}

	/*
	 * DELETEMETHOD I hope this can be deleted, since minus level support will not
	 * work the same way in the new PCClass/PCClassLevel world. If nothing else, it
	 * is massively a REFACTOR item to put this into the PlayerCharacter that is
	 * doing the removal.
	 */
	void doMinusLevelMods(final PlayerCharacter aPC, final int oldLevel)
	{
		PCClassLevel pcl = aPC.getActiveClassLevel(this, oldLevel);
		CDOMObjectUtilities.removeAdds(pcl, aPC);
		CDOMObjectUtilities.restoreRemovals(pcl, aPC);
	}

	void subLevel(final PlayerCharacter aPC)
	{

		if (aPC != null)
		{
			int total = aPC.getTotalLevels();

			int oldLevel = aPC.getLevel(this);
			int spMod = 0;
			final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), oldLevel);

			if (pcl != null)
			{
				spMod = pcl.getSkillPointsGained(aPC);
			}
			else
			{
				Logging.errorPrint("ERROR: could not find class/level info for " + getDisplayName() + "/" + oldLevel);
			}

			final int newLevel = oldLevel - 1;

			if (oldLevel > 0)
			{
				PCClassLevel classLevel = aPC.getActiveClassLevel(this, oldLevel - 1);
				aPC.removeHP(classLevel);
			}

			//			aPC.adjustFeats(-aPC.getBonusFeatsForNewLevel(this));
			setLevel(newLevel, aPC);
			aPC.removeKnownSpellsForClassLevel(this);

			doMinusLevelMods(aPC, newLevel + 1);

			DomainApplication.removeDomainsForLevel(this, newLevel + 1, aPC);

			if (newLevel == 0)
			{
				SubClassApplication.setSubClassKey(aPC, this, Constants.NONE);

				//
				// Remove all skills associated with this class
				//
				for (Skill skill : aPC.getSkillSet())
				{
					SkillRankControl.setZeroRanks(this, aPC, skill);
				}

				Integer currentPool = aPC.getSkillPool(this);
				spMod = currentPool == null ? 0 : currentPool;
			}

			if (!isMonster() && (total > aPC.getTotalLevels()))
			{
				total = aPC.getTotalLevels();

				// Roll back any stat changes that were made as part of the
				// level

				final List<PCLevelInfoStat> moddedStats = new ArrayList<>();
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
						for (PCStat aStat : aPC.getStatSet())
						{
							if (aStat.equals(statToRollback.getStat()))
							{
								aPC.setStat(aStat, aPC.getStat(aStat) - statToRollback.getStatMod());
								break;
							}
						}
					}
				}
			}

			aPC.setLevelWithoutConsequence(this, newLevel);

			if (isMonster() || (total != 0))
			{
				Integer currentPool = aPC.getSkillPool(this);
				int newSkillPool = (currentPool == null ? 0 : currentPool) - spMod;
				aPC.setSkillPool(this, newSkillPool);
				aPC.setDirty(true);
			}

			if (aPC.getLevel(this) == 0)
			{
				aPC.removeClass(this);
			}

			aPC.validateCharacterDomains();

			if (!aPC.isImporting())
			{
				final int maxxp = aPC.minXPForNextECL();
				if (aPC.getXP() >= maxxp)
				{
					aPC.setXP(Math.max(maxxp - 1, 0));
				}
			}
		}
		else
		{
			Logging.errorPrint("No current pc in subLevel()? How did this happen?");

			return;
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
			CDOMSingleRef<PCStat> bss = otherClass.get(ObjectKey.BONUS_SPELL_STAT);
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
		CDOMSingleRef<PCStat> ss = otherClass.get(ObjectKey.SPELL_STAT);
		if (ss != null)
		{
			put(ObjectKey.SPELL_STAT, ss);
		}

		TransitionChoice<CDOMListObject<Spell>> slc = otherClass.get(ObjectKey.SPELLLIST_CHOICE);
		if (slc != null)
		{
			put(ObjectKey.SPELLLIST_CHOICE, slc);
		}

		List<QualifiedObject<CDOMReference<Equipment>>> e = otherClass.getListFor(ListKey.EQUIPMENT);
		if (e != null)
		{
			addAllToListFor(ListKey.EQUIPMENT, e);
		}

		List<WeaponProfProvider> wp = otherClass.getListFor(ListKey.WEAPONPROF);
		if (wp != null)
		{
			addAllToListFor(ListKey.WEAPONPROF, wp);
		}
		QualifiedObject<Boolean> otherWP = otherClass.get(ObjectKey.HAS_DEITY_WEAPONPROF);
		if (otherWP != null)
		{
			put(ObjectKey.HAS_DEITY_WEAPONPROF, otherWP);
		}

		List<ArmorProfProvider> ap = otherClass.getListFor(ListKey.AUTO_ARMORPROF);
		if (ap != null)
		{
			addAllToListFor(ListKey.AUTO_ARMORPROF, ap);
		}

		List<ShieldProfProvider> sp = otherClass.getListFor(ListKey.AUTO_SHIELDPROF);
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
			ownBonuses(this);
		}
		catch (CloneNotSupportedException ce)
		{
			Logging.errorPrint("failed to clone", ce);
		}

		for (VariableKey vk : otherClass.getVariableKeys())
		{
			put(vk, otherClass.get(vk));
		}

		if (otherClass.containsListFor(ListKey.CSKILL))
		{
			removeListFor(ListKey.CSKILL);
			addAllToListFor(ListKey.CSKILL, otherClass.getListFor(ListKey.CSKILL));
		}

		if (otherClass.containsListFor(ListKey.LOCALCCSKILL))
		{
			removeListFor(ListKey.LOCALCCSKILL);
			addAllToListFor(ListKey.LOCALCCSKILL, otherClass.getListFor(ListKey.LOCALCCSKILL));
		}

		removeListFor(ListKey.KIT_CHOICE);
		addAllToListFor(ListKey.KIT_CHOICE, otherClass.getSafeListFor(ListKey.KIT_CHOICE));

		removeListFor(ListKey.SAB);
		addAllToListFor(ListKey.SAB, otherClass.getSafeListFor(ListKey.SAB));

		/*
		 * TODO Does this need to have things from the Class Level objects?
		 * I don't think so based on deferred processing of levels...
		 */

		addAllToListFor(ListKey.DAMAGE_REDUCTION, otherClass.getListFor(ListKey.DAMAGE_REDUCTION));

		for (CDOMReference<Vision> ref : otherClass.getSafeListMods(Vision.VISIONLIST))
		{
			for (AssociatedPrereqObject apo : otherClass.getListAssociations(Vision.VISIONLIST, ref))
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

		addAllToListFor(ListKey.NATURAL_WEAPON, otherClass.getListFor(ListKey.NATURAL_WEAPON));

		put(ObjectKey.LEVEL_HITDIE, otherClass.get(ObjectKey.LEVEL_HITDIE));
	}

	private SortedMap<Integer, PCClassLevel> levelMap = new TreeMap<>();

	public PCClassLevel getOriginalClassLevel(int lvl)
	{
		if (!levelMap.containsKey(lvl))
		{
			PCClassLevel classLevel = new PCClassLevel();
			classLevel.put(IntegerKey.LEVEL, lvl);
			classLevel.setName(getDisplayName() + "(" + lvl + ")");
			classLevel.put(StringKey.QUALIFIED_KEY, getQualifiedKey());
			classLevel.put(ObjectKey.SOURCE_CAMPAIGN, get(ObjectKey.SOURCE_CAMPAIGN));
			classLevel.put(StringKey.SOURCE_PAGE, get(StringKey.SOURCE_PAGE));
			classLevel.put(StringKey.SOURCE_LONG, get(StringKey.SOURCE_LONG));
			classLevel.put(StringKey.SOURCE_SHORT, get(StringKey.SOURCE_SHORT));
			classLevel.put(StringKey.SOURCE_WEB, get(StringKey.SOURCE_WEB));
			classLevel.put(ObjectKey.SOURCE_DATE, get(ObjectKey.SOURCE_DATE));
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
				lvl.put(StringKey.QUALIFIED_KEY, getQualifiedKey());
				lvl.put(ObjectKey.SOURCE_CAMPAIGN, get(ObjectKey.SOURCE_CAMPAIGN));
				lvl.put(StringKey.SOURCE_PAGE, get(StringKey.SOURCE_PAGE));
				lvl.put(StringKey.SOURCE_LONG, get(StringKey.SOURCE_LONG));
				lvl.put(StringKey.SOURCE_SHORT, get(StringKey.SOURCE_SHORT));
				lvl.put(StringKey.SOURCE_WEB, get(StringKey.SOURCE_WEB));
				lvl.put(ObjectKey.SOURCE_DATE, get(ObjectKey.SOURCE_DATE));
				lvl.put(ObjectKey.TOKEN_PARENT, this);
				lvl.setName(getDisplayName() + "(" + lvl.get(IntegerKey.LEVEL) + ")");
				lvl.ownBonuses(this);
				levelMap.put(me.getKey(), lvl);
			}
			catch (CloneNotSupportedException e)
			{
				Logging.errorPrint(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * Clear any data from the class levels. Primarily for use by the Classes 
	 * LST editor. 
	 */
	public void clearClassLevels()
	{
		levelMap.clear();
	}

	public String getFullKey()
	{
		return getKeyName();
	}

	@Override
	public void ownBonuses(Object owner) throws CloneNotSupportedException
	{
		super.ownBonuses(owner);
		for (PCClassLevel pcl : this.getOriginalClassLevelCollection())
		{
			pcl.ownBonuses(owner);
		}
	}

	@Override
	public boolean qualifies(PlayerCharacter aPC, Object owner)
	{
		if (Globals.checkRule(RuleConstants.CLASSPRE))
		{
			return true;
		}

		return super.qualifies(aPC, owner);
	}

	public String getBaseStat()
	{
		return getSpellBaseStat();
	}

	public String getHD()
	{
		HitDie hd = getSafe(ObjectKey.LEVEL_HITDIE);
		return String.valueOf(hd.getDie());
	}

	public String[] getTypes()
	{
		String type = getType();
		return type.split("\\.");
	}

	public String getClassType()
	{
		FactKey<String> fk = FactKey.valueOf("ClassType");
		return getResolved(fk);
	}

}
