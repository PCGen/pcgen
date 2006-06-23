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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 */
public class PCClass extends PObject
{
	protected String spellBaseStat = Constants.s_NONE;
	protected String bonusSpellBaseStat = Constants.s_DEFAULT;
	protected int level = 0;
	protected int numSpellsFromSpecialty = 0;
	private ArrayList<String> acList = new ArrayList<String>();
	private ArrayList<String> domainList = new ArrayList<String>();
	private ArrayList<String> featAutos = new ArrayList<String>();
	private ArrayList<String> featList = new ArrayList<String>();
	private ArrayList<String> knownList = new ArrayList<String>();
	private ArrayList<String> knownSpellsList = new ArrayList<String>();
	private ArrayList<String> specialtyList = new ArrayList<String>();
	private ArrayList<String> specialtyknownList = new ArrayList<String>();
	private final ArrayList<String> templates = new ArrayList<String>();
	private ArrayList<LevelProperty> SR = null;
	private ArrayList<String> addDomains = new ArrayList<String>();
	private ArrayList<LevelProperty> naturalWeapons = null;
	private ArrayList<SubClass> subClassList = null; // list of SubClass objects
	private ArrayList<String> templatesAdded = null;
	private ArrayList<String> uattList = new ArrayList<String>();
//	private ArrayList vFeatList = new ArrayList();
	private ArrayList<String> weaponProfBonus = new ArrayList<String>();
	private final HashMap<String, String> castForLevelMap = new HashMap<String, String>();
	private HashMap<String, Integer> hitPointMap = new HashMap<String, Integer>();
	private HashMap<String, List<Ability>> vFeatMap = new HashMap<String, List<Ability>>();
	protected HashMap<Integer, String> hitDieLockMap = new HashMap<Integer, String>();
	private int skillPool = 0;
	private List<String> classSkillList = null;
	private List<String> classSpellList = null;
	private List<String> skillList = new ArrayList<String>();
	private List<LevelProperty> visionList = null;
	private Map<String, String> attackCycleMap = new HashMap<String, String>();
	private Map<String, String> castMap = new HashMap<String, String>();
	private String CRFormula = null; // null or formula
	private String XPPenalty = null; // Valid values are null, "YES", "NO"
	private String abbrev = "";
	private String attackBonusType = "O";
	private String attackCycle = "";
	private String castAs = "";
	private String classSkillString = null;
	private String classSpellString = null;
	private List<String> deityList = new ArrayList<String>();
	private String exClass = "";

	//private int ageSet = 2;
	private String itemCreationMultiplier = "";
	private String levelExchange = "";

	// monsterFlag and XPPenalty can't be boolean, because null has a meaning.
	// Null means use the default (look for the types on class types)
	private String monsterFlag = null; // Valid values are null, "YES", "NO"
	private String preRaceType = null;
	private String prohibitedString = Constants.s_NONE;
	private String specialsString = "";
	private String spellType = Constants.s_NONE;
	private String stableSpellKey = null;
	private String subClassKey = Constants.s_NONE;
	private String subClassString = Constants.s_NONE;
	private TreeSet<Language> languageBonus = new TreeSet<Language>();
	private boolean hasSubClass = false;
	private boolean memorizeSpells = true;
	private boolean modToSkills = true; // stat bonus applied to skills per level
	private boolean multiPreReqs = false;
	private boolean usesSpellbook = false;
	private List<SpellProhibitor> prohibitSpellDescriptorList = null;
	private int hitDie = 0;
	private int initMod = 0;
	private int initialFeats = 0;
	private Integer levelsPerFeat = null;
	private int maxLevel = 20;
//	private int skillPoints = 0;
	private int maxCastLevel = -1; // max level CAST: tag is found
	private int maxKnownLevel = -1; // max level KNOWN: tag is found
	private HashMap<Integer, Integer> highestSpellLevelMap;
	private String skillPointFormula = "0";

	private boolean hasSpellFormulas = false;

	private String classKey = "";

	/**
	 *
	 */
	public PCClass()
	{
		super();
		deityList.add("ANY");
	}


	public final Collection<String> getACList()
	{
		return acList;
	}

	public final void setAbbrev(final String argAbbrev)
	{
		abbrev = argAbbrev;
	}

	public final String getAbbrev()
	{
		return abbrev;
	}

	public void setKeyName(final String aKey)
	{
		super.setKeyName(aKey);
		classKey = "CLASS:" + keyName;
	}

	/* addDomains is the prestige domains this class has access to */
	public final ArrayList<String> getAddDomains()
	{
		return addDomains;
	}

	public final void setAttackBonusType(final String aString)
	{
		attackBonusType = aString;
	}

	public final String getAttackBonusType()
	{
		return attackBonusType;
	}

	/**
	 * Parse the ATTACKCYCLE: string and build HashMap
	 * Only allowed values in attackCycle are: BAB, RAB or UAB
	 * @param aString
	 **/
	public final void setAttackCycle(final String aString)
	{
		attackCycle = aString;
		if (aString.indexOf('|')==-1)
			return;

		final StringTokenizer aTok = new StringTokenizer(attackCycle, "|");

		while (aTok.hasMoreTokens())
		{
			final String attackType = aTok.nextToken();
			final String aVal = aTok.nextToken();
			attackCycleMap.put(attackType, aVal);
		}
	}

	/* returns the unadjusted unprocessed attackCycle */
	public final String getAttackCycle()
	{
		return attackCycle;
	}

	public final void setCastAs(final String aString)
	{
		castAs = aString;
	}

	/**
	 * returns the CASTAS: tag for this class, or just the name of the
	 * class if one hasn't been set
	 * @return cast as
	 *
	 **/
	public final String getCastAs()
	{
		if (castAs == null || castAs.equals(""))
			return keyName;
		return castAs;
	}

	/**
	 * Method sets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 *
	 * author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 * @param baseStat
	 */
	public final void setBonusSpellBaseStat(final String baseStat)
	{
		bonusSpellBaseStat = baseStat;
	}

	/**
	 * Method gets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 *
	 * author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 *
	 * @return String
	 */
	public final String getBonusSpellBaseStat()
	{
		return bonusSpellBaseStat;
	}

	public double getBonusTo(final String argType, final String argMname, final int asLevel, final PlayerCharacter aPC)
	{
		double i = 0;

		if ((asLevel == 0) || getBonusList().isEmpty())
		{
			return 0;
		}

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();

		//final String typePlusMName = new StringBuffer(type).append('.').append(mname).append('.').toString();
		for ( BonusObj bonus : getBonusList() )
		{
			final StringTokenizer breakOnPipes = new StringTokenizer(bonus.toString().toUpperCase(), "|", false);
			final int aLevel = Integer.parseInt(breakOnPipes.nextToken());
			final String theType = breakOnPipes.nextToken();

			if (!theType.equals(type))
			{
				continue;
			}

			final String str = breakOnPipes.nextToken();
			final StringTokenizer breakOnCommas = new StringTokenizer(str, ",", false);

			while (breakOnCommas.hasMoreTokens())
			{
				final String theName = breakOnCommas.nextToken();

				if ((aLevel <= asLevel) && theName.equals(mname))
				{
					final String aString = breakOnPipes.nextToken();
					final List<Prerequisite> localPreReqList = new ArrayList<Prerequisite>();
					if (bonus.getPrereqList() != null)
					{
						localPreReqList.addAll(bonus.getPrereqList());
					}

					//TODO: This code should be removed after the 5.8 release as the prereqs are processed by the bonus loading code.
					while (breakOnPipes.hasMoreTokens())
					{
						final String bString = breakOnPipes.nextToken();

						if (bString.startsWith("PRE") || bString.startsWith("!PRE"))
						{
							Logging.debugPrint("Why is this prerequisite '" + bString + "' parsed in '"+getClass().getName()+".getBonusTo(String,String,int)' rather than in the persistence layer?");
							try
							{
								final PreParserFactory factory = PreParserFactory.getInstance();
								localPreReqList.add(factory.parse( bString ) );
							}
							catch (PersistenceLayerException ple) {
								Logging.errorPrint(ple.getMessage(), ple);
							}
						}
					}

					// must meet criteria for bonuses before adding them in
					//TODO: This is a hack to avoid VARs etc in class defs being qualified for when Bypass class prereqs is selected.
					// Should we be passing in the BonusObj here to allow it to be referenced in Qualifies statements?
					if (PrereqHandler.passesAll(localPreReqList, aPC, null))
					{
						final double j = aPC.getVariableValue(aString, classKey).doubleValue();
						i += j;
					}
				}
			}
		}

		return i;
	}

	/**
	 * Return the number of spells a character can cast in this class for a specified level.
	 *
	 * @param pcLevel The number of levels in this class that the character has
	 * @param spellLevel The spell level we are interested in
	 * @param bookName the name of the spell book we are interested in
	 * @param aPC The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this level.
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel, final String bookName, final PlayerCharacter aPC)
	{
		return getCastForLevel(pcLevel, spellLevel, bookName, true, aPC);
	}

	/**
	 * Return the number of spells a character can cast in this class for a specified level.
	 *
	 * @param pcLevel The number of levels in this class that the character has
	 * @param spellLevel The spell level we are interested in
	 * @param bookName the name of the spell book we are interested in
	 * @param includeAdj Seems to have something to do with speciality spells
	 * @param aPC The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this level.
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel, final String bookName, final boolean includeAdj, final PlayerCharacter aPC)
	{
		return getCastForLevel(pcLevel, spellLevel, bookName, includeAdj, true, aPC);
	}

	/**
	 * Return the number of spells a character can cast in this class for a specified level.
	 *
	 * @param pcLevel The number of levels in this class that the character has
	 * @param spellLevel The spell level we are interested in
	 * @param bookName the name of the spell book we are interested in
	 * @param includeAdj Seems to have something to do with speciality spells
	 * @param limitByStat Do we return 0 for any spell level that the character does not have a high enough stat to cast
	 * @param aPC The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this level.
	 */
	public int getCastForLevel(int pcLevel, final int spellLevel, final String bookName, final boolean includeAdj, final boolean limitByStat, final PlayerCharacter aPC)
	{
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		if (getNumFromCastList(pcLevel, spellLevel, aPC) < 0)
		{
			// can't cast spells of this level
			// however, character might have a bonus spell slot e.g. from certain feats
			return (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel);
		}

		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + allSpellLevel);

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

		final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "CAST." + statString)
			+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT")
			+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS." + keyName);

		if ((index > -2) && limitByStat)
		{
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();

			if ((maxSpellLevel + bonusStat) < spellLevel)
			{
				return total;
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
			&& ((specialtyList.size() > 0) || (aPC.getCharacterDomainList().size() > 0)))
		{
			// We need to do this for EVERY spell level up to the
			// one really under consideration, because if there
			// are any specialty spells available BELOW this level,
			// we might wind up using THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ++ix)
			{
				final List<CharacterSpell> aList = getSpellSupport().getCharacterSpell(null, "", ix);
				List<Spell> bList = new ArrayList<Spell>();

				if (!aList.isEmpty())
				{
					if ((ix > 0) && "DIVINE".equalsIgnoreCase(spellType))
					{
						for ( CharacterDomain cd : aPC.getCharacterDomainList() )
						{
							if (cd.isFromPCClass(getKeyName()) && (cd.getDomain() != null))
							{
								bList = Globals.getSpellsIn(ix, "", cd.getDomain().getKeyName());
							}
						}
					}

					for ( CharacterSpell cs : aList )
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
							x = cs.getInfoIndexFor("", ix, 1);
						}

						if (x > -1)
						{
							adj = 1;

							break;
						}
					}
				}
				 // end of what to do if aList is not empty

				if (adj == 1)
				{
					break;
				}
			}
			 // end of looping up to this level looking for specialty spells that can be cast
		}
		 // end of deciding whether there are specialty slots to distribute

		int mult = (int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName + levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE." + getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		final int t = getNumFromCastList(pcLevel, spellLevel, aPC);

		total += ((t * mult) + adj);

		final String bonusSpell = Globals.getBonusSpellMap().get(String.valueOf(spellLevel));

		if ((bonusSpell != null) && !bonusSpell.equals("0|0"))
		{
			final StringTokenizer s = new StringTokenizer(bonusSpell, "|");
			final int base = Integer.parseInt(s.nextToken());
			final int range = Integer.parseInt(s.nextToken());

			if (stat >= base)
			{
				total += Math.max(0, (stat - base + range) / range);
			}
		}

		return total;
	}

	/**
	 * Set the Class Skill string
	 * @param aString
	 */
	public final void setClassSkillString(final String aString)
	{
		classSkillString = aString;
	}

	/**
	 * Return the value set by the SKILLLIST token
	 *
	 * @return The pipe-delimited list of class skills
	 */
	public final String getClassSkillString()
	{
		return classSkillString;
	}

	public List<String> getClassSpecialAbilityList(final PlayerCharacter aPC)
	{
		final List<String> aList = new ArrayList<String>();
		final List<String> formattedList = new ArrayList<String>();
		final List<SpecialAbility> abilityList = getListFor(ListKey.SPECIAL_ABILITY);

		//
		// Determine the list of abilities from this class
		// that the character is eligable for
		//
		if (abilityList == null)
		{
			return aList;
		}

		if (!abilityList.isEmpty())
		{
			for ( SpecialAbility saAbility : abilityList )
			{
				final String aString = saAbility.toString();

				boolean found = false;

				// TODO shouldn't this be
				// if ( aList.contains( aString ) )
				for ( String str : aList )
				{
					if (aString.equals(str))
					{
						found = true;

						break;
					}
				}

				if (!found && saAbility.pcQualifiesFor(aPC))
				{
					aList.add(aString);
				}
			}
		}

		//
		// From the list of allowed SAs, format the output strings
		// to include all of the variables
		for ( String str : aList )
		{
			StringTokenizer varTok = new StringTokenizer(str, "|", false);
			final String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();

			if (varCount != 0)
			{
				varValue = new int[varCount];

				for (int j = 0; j < varCount; ++j)
				{
					// Get the value for each variable
					final String vString = varTok.nextToken();
					varValue[j] = aPC.getVariable(vString, true, true, "", "", 0).intValue();
				}
			}

			final StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;

			boolean isZero = false;

			// Fill in each % with the value of the appropriate token
			while (varTok.hasMoreTokens())
			{
				final String nextTok = varTok.nextToken();

				if ("%".equals(nextTok))
				{
					if (varCount == 0)
					{
						// If this is the first token, then set the count of successfull token replacements to 0
						isZero = true;
					}

					if ((varValue != null) && (varCount < varValue.length))
					{
						final int thisVar = varValue[varCount++];

						// Update isZero if this token has a value of anything other than 0
						isZero &= (thisVar == 0);
						newAbility.append(thisVar);
					}
					else
					{
						newAbility.append('%');
					}
				}
				else
				{
					newAbility.append(nextTok);
				}
			}

			if (!isZero)
			{
				// If all of the tokens for this ability were 0 then we do not show it,
				// otherwise we add it to the return list.
				formattedList.add(newAbility.toString());
			}
		}

		return formattedList;
	}

	public final List<String> getClassSpellList()
	{
		return classSpellList;
	}

	public final void setDeityList(final List<String> deityList)
	{
		// deityList must be a concrete list so we can clone it,
		// but we can not guarantee that the list passed in is
		// a ArrayList, so we have to copy the entries.
		// This should not be onerous as it is done infrequently
		// and the lists are short (1-2 entries).
		this.deityList = new ArrayList<String>(deityList);
	}

	public final List<String> getDeityList()
	{
		return deityList;
	}

	public final ArrayList<String> getDomainList()
	{
		return domainList;
	}

	public final void setExClass(final String aString)
	{
		exClass = aString;
	}

	public final String getExClass()
	{
		return exClass;
	}

	public final Collection<String> getFeatAutos()
	{
		return featAutos;
	}

	public final ArrayList<String> getFeatList()
	{
		return featList;
	}

	public final void setHitDie(final int dice)
	{
		hitDie = dice;
	}

	public int getBaseHitDie()
	{
		return hitDie;
	}

	public void putHitDieLock(final String hitDieLock, final int aLevel)
	{
		hitDieLockMap.put(aLevel, hitDieLock);
	}

	protected String getHitDieLock(final int aLevel)
	{
		return hitDieLockMap.get(aLevel);
	}

	public final void setInitialFeats(final int feats)
	{
		initialFeats = feats;
	}

	public final int getInitialFeats()
	{
		return initialFeats;
	}

	public final void setItemCreationMultiplier(final String argItemCreationMultiplier)
	{
		itemCreationMultiplier = argItemCreationMultiplier;
	}

	public final String getItemCreationMultiplier()
	{
		return itemCreationMultiplier;
	}

	public final int getLevel()
	{
		return level;
	}

	public final void setLevelExchange(final String aString)
	{
		levelExchange = aString;
	}

	public final String getLevelExchange()
	{
		return levelExchange;
	}

	/**
	 * set the level to arg without impacting spells, hp, or anything else
	 * - use this with great caution only
	 * TODO Then why is it even here, What is it used for (JSC 07/21/03)
	 * @param arg
	 **/
	public final void setLevelWithoutConsequence(final int arg)
	{
		level = arg;
	}

	public final void setMaxLevel(final int maxLevel)
	{
		this.maxLevel = maxLevel;
	}

	public final int getMaxLevel()
	{
		return maxLevel;
	}

	public final void setMemorizeSpells(final boolean memorizeSpells)
	{
		this.memorizeSpells = memorizeSpells;
	}

	public final boolean getMemorizeSpells()
	{
		return memorizeSpells;
	}

	public final void setMultiPreReqs(final boolean multiPreReqs)
	{
		this.multiPreReqs = multiPreReqs;
	}

	public final void setPreRaceType(final String preRaceType)
	{
		this.preRaceType = preRaceType.toUpperCase();
	}

//	public final void setSkillPool(final Integer argSkillPool)
//	{
//		skillPool = argSkillPool;
//	}

	public final int getSkillPool(final PlayerCharacter aPC)
	{
		int returnValue = 0;
////////////////////////////////////
// Using this method will return skills for level 0 even when there is no information
// Byngl - December 28, 2004
//		for (int i = 0; i <= level; i++)
//		{
//			final PCLevelInfo pcl = aPC.getLevelInfoFor(getKeyName(), i);
//
//			if ((pcl != null) && pcl.getClassKeyName().equals(getKeyName()))
//			{
//				returnValue += pcl.getSkillPointsRemaining();
//			}
//		}
		for ( PCLevelInfo pcl : aPC.getLevelInfo() )
		{
			if (pcl.getClassKeyName().equals(getKeyName()))
			{
				returnValue += pcl.getSkillPointsRemaining();
			}
		}
////////////////////////////////////

		return returnValue;
	}

	public final void setSpecialsString(final String aString)
	{
		specialsString = aString;
	}

	public final Collection<String> getSpecialtyList()
	{
		return specialtyList;
	}

	public void setAddDomains(final int level, final String aString, final String delimiter)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, delimiter, false);
		final String prefix = Integer.toString(level) + '|';

		while (aTok.hasMoreTokens())
		{
			addDomains.add(prefix + aTok.nextToken());
		}
	}

	public String getDisplayClassName()
	{
		if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE))
		{
			return getSubClassKeyed(subClassKey).getDisplayName();
		}

		return displayName;
	}

	public String getFullDisplayClassName()
	{
		final StringBuffer buf = new StringBuffer();

		buf.append(getDisplayClassName());

		return buf.append(" ").append(level).toString();
	}

	public final void setHasSubClass(final boolean arg)
	{
		hasSubClass = arg;
	}

	public final void setHasSpellFormula(final boolean arg)
	{
		hasSpellFormulas = arg;
	}

	public final void setProhibitedString(final String aString)
	{
		prohibitedString = aString;
	}

	public final String getProhibitedString()
	{
		return prohibitedString;
	}

	// HITDIE:num --- sets the hit die to num regardless of class.
	// HITDIE:%/num --- divides the classes hit die by num.
	// HITDIE:%*num --- multiplies the classes hit die by num.
	// HITDIE:%+num --- adds num to the classes hit die.
	// HITDIE:%-num --- subtracts num from the classes hit die.
	// HITDIE:%upnum --- moves the hit die num steps up the die size list d4,d6,d8,d10,d12. Stops at d12.
	// HITDIE:%downnum --- moves the hit die num steps down the die size list d4,d6,d8,d10,d12. Stops at d4.
	// Regardless of num it will never allow a hit die below 1.
	public int getLevelHitDie(final PlayerCharacter aPC, final int classLevel)
	{
		//Class Base Hit Die
		int currHitDie = getLevelHitDieUnadjusted(aPC, classLevel);

		//Race
		String dieLock = aPC.getRace().getHitDieLock();
		if (dieLock.length() != 0)
		{
			currHitDie = calcHitDieLock(dieLock, currHitDie);
		}

		//Templates
		for ( PCTemplate template : aPC.getTemplateList() )
		{
			if (template != null)
			{
				dieLock = template.getHitDieLock();
				if(dieLock.length() != 0) {
					currHitDie = calcHitDieLock(dieLock, currHitDie);
				}
			}
		}

		//Levels
		dieLock = hitDieLockMap.get(classLevel);
		if(dieLock != null && dieLock.length() != 0) {
			currHitDie = calcHitDieLock(dieLock, currHitDie);
		}

		return currHitDie;
	}

	private int calcHitDieLock(String dieLock, final int currDie)
	{
		final int[] dieSizes = Globals.getDieSizes();
		int diedivide;

		StringTokenizer tok = new StringTokenizer(dieLock, "|");
		dieLock = tok.nextToken();
		String prereq = null;
		if (tok.hasMoreTokens())
		{
			prereq = tok.nextToken();
		}

		if (prereq != null)
		{
			if (prereq.startsWith("CLASS.TYPE"))
			{
				if (!isType(prereq.substring(prereq.indexOf("=")+1,prereq.length())))
				{
					return currDie;
				}
			}
			else if (prereq.startsWith("CLASS="))
			{
				if (!getKeyName().equals(prereq.substring(prereq.indexOf("="),prereq.length())))
				{
					return currDie;
				}
			}
		}

		if (dieLock.startsWith("%/"))
		{
			diedivide = Integer.parseInt(dieLock.substring(2));

			if (diedivide <= 0)
			{
				diedivide = 1; // Idiot proof it. Stop Divide by zero errors.
			}

			diedivide = currDie / diedivide;
		}
		else if (dieLock.startsWith("%*"))
		{
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide *= currDie;
		}
		else if (dieLock.startsWith("%+"))
		{ // possibly redundant with BONUS:HD MAX|num
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide += currDie;
		}
		else if (dieLock.startsWith("%-"))
		{ // possibly redundant with BONUS:HD MAX|num if that will take negative numbers.
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = currDie - diedivide;
		}
		else if (dieLock.startsWith("%up"))
		{
			diedivide = Integer.parseInt(dieLock.substring(3));

			// lock in valid values.
			if (diedivide > 4)
			{
				diedivide = 4;
			}

			if (diedivide < 0)
			{
				diedivide = 0;
			}

			for (int i = 3; i <= (7 - diedivide); ++i)
			{
				if (currDie == dieSizes[i])
				{
					return dieSizes[i + diedivide];
				}
			}

			diedivide = dieSizes[7]; // If they went too high, they get maxed out.
		}
		else if (dieLock.startsWith("%Hup"))
		{
			diedivide = Integer.parseInt(dieLock.substring(4));

			for (int i = 0; i < ((dieSizes.length) - diedivide); ++i)
			{
				if (currDie == dieSizes[i])
				{
					return dieSizes[i + diedivide];
				}
			}

			diedivide = dieSizes[dieSizes.length]; // If they went too high, they get maxed out.
		}
		else if (dieLock.startsWith("%down"))
		{
			diedivide = Integer.parseInt(dieLock.substring(5));

			// lock in valid values.
			if (diedivide > 4)
			{
				diedivide = 4;
			}

			if (diedivide < 0)
			{
				diedivide = 0;
			}

			for (int i = (3 + diedivide); i <= 7; ++i)
			{
				if (currDie == dieSizes[i])
				{
					return dieSizes[i - diedivide];
				}
			}

			diedivide = dieSizes[3]; // Minimum valid if too low.
		}
		else if (dieLock.startsWith("%Hdown"))
		{
			diedivide = Integer.parseInt(dieLock.substring(5));

			for (int i = diedivide; i < dieSizes.length; ++i)
			{
				if (currDie == dieSizes[i])
				{
					return dieSizes[i - diedivide];
				}
			}

			diedivide = dieSizes[0]; // floor them if they're too low.
		}
		else
		{
			diedivide = Integer.parseInt(dieLock);
		}

		if (diedivide <= 0)
		{
			diedivide = 1; // Idiot proof it.
		}
		return diedivide;
	}

	public final int getLevelHitDieUnadjusted(final PlayerCharacter aPC, final int classLevel)
	{
		if ("None".equals(subClassKey))
		{
			return hitDie;
		}
		final SubClass aSubClass = getSubClassKeyed(subClassKey);
		if (aSubClass != null)
		{
			return aSubClass.getLevelHitDie(aPC, classLevel);
		}
		return hitDie;
	}

	/*
	 * sets whether stat modifier is applied to skill points
	 * at level-up time
	 */
	public final void setModToSkills(final boolean bool)
	{
		modToSkills = bool;
	}

	public final boolean getModToSkills()
	{
		return modToSkills;
	}

	public final void setSkillPointFormula(final String argFormula)
	{
		skillPointFormula = argFormula;
	}

	public String getSkillPointFormula()
	{
		return skillPointFormula;
	}

//	public final void setSkillPoints(final int points)
//	{
//		skillPoints = points;
//	}

//	public int getSkillPoints()
//	{
//		return skillPoints;
//	}

	public String getSpecialtyListString(final PlayerCharacter aPC)
	{
		final StringBuffer retString = new StringBuffer();

		for ( String spec : specialtyList )
		{
			if (retString.length() > 0)
			{
				retString.append(',');
			}

			retString.append(spec);
		}

		for ( CharacterDomain cd : aPC.getCharacterDomainList() )
		{
			if (cd.getDomain() != null)
			{
				if (retString.length() > 0)
				{
					retString.append(',');
				}

				retString.append(cd.getDomain().getKeyName());
			}
		}

		return retString.toString();
	}

	public final void setSpellBaseStat(final String baseStat)
	{
		spellBaseStat = baseStat;
	}

	public final String getSpellBaseStat()
	{
		return spellBaseStat;
	}

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
				stableSpellKey = "CLASS|" + keyName;

				return stableSpellKey;
			}
		}

		final StringBuffer aBuf = new StringBuffer();

		for ( String keyStr : classSpellList )
		{
			if (aBuf.length() > 0)
			{
				aBuf.append('|');
			}

			if (keyStr.endsWith("(Domain)"))
			{
				aBuf.append("DOMAIN|").append(keyStr.substring(0, keyStr.length() - 8));
			}
			else
			{
				aBuf.append("CLASS|").append(keyStr);
			}
		}

		stableSpellKey = aBuf.toString();

		return stableSpellKey;
	}

	public final void setSpellLevelString(final String aString)
	{
		classSpellString = aString;
	}

	public final String getSpellLevelString()
	{
		return classSpellString;
	}

	public final void setSpellType(final String newType)
	{
		spellType = newType;
	}

	public final String getSpellType()
	{
		return spellType;
	}

	public void setCastMap(final int index, final String cast)
	{
		if (index > maxCastLevel)
		{
			maxCastLevel = index;
		}
		castMap.put(String.valueOf(index), cast);
	}

	/**
	 * if castAs has been set, return castMap from that class
	 * @return List of strings
	 */
	public Map<String, String> getCastMap()
	{
		if ("".equals(castAs) || getKeyName().equals(castAs))
		{
			return castMap;
		}

		final PCClass aClass = Globals.getClassKeyed(castAs);

		if (aClass != null)
		{
			return aClass.getCastMap();
		}

		return castMap;
	}

	/**
	 * Return CAST: string for a level
	 * @param aInt
	 * @return String
	 */
	public String getCastStringForLevel(int aInt)
	{
		if (aInt > maxCastLevel)
		{
			aInt = maxCastLevel;
		}
		final String aLevel = String.valueOf(aInt);

		if (getCastMap().containsKey(aLevel))
		{
			return getCastMap().get(aLevel);
		}

		return "";
	}

	/**
	 * Return the level of the highest level spell offered by the class.
	 *
	 * @return The level of the highest level spell available.
	 */
	public int getHighestLevelSpell()
	{
		// check to see if we have a cached value first
		if (highestSpellLevelMap != null)
		{
			final Integer highest = highestSpellLevelMap.get(-1);
			if (highest != null)
			{
				return highest.intValue();
			}
		}


		Map<String, String> aCastMap = getCastMap();
		int highestCastable = -1;
		for ( String entry : aCastMap.values() )
		{
			highestCastable = Math.max(highestCastable, entry.split(",").length - 1);
		}

		// Highest Known spell for level
		List<String> known = getKnownList();
		int highestKnown = -1;
		for ( String element : known )
		{
			highestKnown = Math.max(highestKnown, element.split(",").length - 1);
		}

		int highest = Math.max(highestCastable, highestKnown);

		// cache the value
		if (highestSpellLevelMap == null)
		{
			highestSpellLevelMap = new HashMap<Integer, Integer>();
			highestSpellLevelMap.put(-1, highest);
		}
		return highest;
	}

	/**
	 * Return the level of the highest level spell the character
	 * could possibly cast in this class. This can return a higher
	 * level than the class allows if the character has feats
	 * which give a bonus to casting.
	 *
	 * @param pc The character to be checked.
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
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 */
	public int getKnownForLevel(final int pcLevel, final int spellLevel, final PlayerCharacter aPC)
	{
		return getKnownForLevel(pcLevel, spellLevel, "null", aPC);
	}

	public void setLevelsPerFeat(final Integer newLevels)
	{
		if (newLevels.intValue() < 0)
		{
			return;
		}

		levelsPerFeat = newLevels;
	}

	public final Integer getLevelsPerFeat()
	{
		if (levelsPerFeat == null)
		{
			levelsPerFeat = new Integer(-1); // -1 to indicate it's not a 'set' value, this is to avoid null pointer errors
		}
		return levelsPerFeat;
	}

	/**
	 * if castAs has been set, return knownList from that class
	 * @return List
	 */
	public List<String> getKnownList()
	{
		if ("".equals(castAs) || getKeyName().equals(castAs))
		{
			return knownList;
		}

		final PCClass aClass = Globals.getClassKeyed(castAs);

		if (aClass != null)
		{
			return aClass.getKnownList();
		}

		return knownList;
	}

	/**
	 * Return KNOWN: string for a level
	 * @param aInt The level  of the class to be retrieved
	 * @return String The KNOWN string, if any
	 */
	public String getKnownStringForLevel(int aInt)
	{
		List<String> known = getKnownList();

		if (aInt > maxKnownLevel)
		{
			aInt = maxKnownLevel;
		}
		if (aInt >= 0 && aInt < known.size())
		{
			return known.get(aInt);
		}

		return "";
	}

	/**
	 * @return The list of automatically known spells.
	 */
	public List<String> getKnownSpellsList()
	{
		return knownSpellsList;
	}

	public final Collection<String> getSpecialtyKnownList()
	{
		return specialtyknownList;
	}

	/**
	 * Get the number of spells this PC can cast based on
	 * Caster Level and desired Spell Level
	 * ex: how many 5th level spells can a 17th level wizard cast?
	 * @param iCasterLevel
	 * @param iSpellLevel
	 * @param aPC The character we are interested in
	 * @return int
	 **/
	public int getNumFromCastList(final int iCasterLevel, final int iSpellLevel, final PlayerCharacter aPC)
	{
		int aNum = -1;
		if (iCasterLevel == 0)
		{
			// can't cast spells!
			return aNum;
		}

		if (!getCastMap().containsKey(String.valueOf(iCasterLevel)))
		{
			//Recurse in case we are actually past the end of a class's definition - use the last enterd value
			return getNumFromCastList(iCasterLevel - 1, iSpellLevel, aPC);
		}

		int iCount = 0;
		String aString = getCastStringForLevel(iCasterLevel);

		final StringTokenizer aTok = new StringTokenizer(aString, ",");

		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();

			if (iCount == iSpellLevel)
			{
				if ((aPC != null) && hasSpellFormula())
				{
					aNum = aPC.getVariableValue(aString, "").intValue();
				}
				else
				{
					try
					{
						aNum = Integer.parseInt(aString);
					}
					catch (NumberFormatException ex)
					{
						// ignore
						aNum = 0;
					}
				}
				return aNum;
			}

			++iCount;
		}

		return aNum;
	}

	public final void setNumSpellsFromSpecialty(final int anInt)
	{
		numSpellsFromSpecialty = anInt;
	}

	public String getBonusCastForLevelString(final int pcLevel, final int spellLevel, final String bookName, final PlayerCharacter aPC)
	{
		if (getCastForLevel(pcLevel, spellLevel, bookName, aPC) > 0)
		{
			// if this class has a specialty, return +1
			if (specialtyList.size() > 0)
			{
				return "+1";
			}

			if (aPC.getCharacterDomainList().isEmpty())
			{
				return "";
			}

			// if the spelllevel is >0 and this class has a characterdomain associated with it, return +1
			if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(spellType))
			{
				for ( CharacterDomain cd : aPC.getCharacterDomainList() )
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
	 * Return the number of spells a character can cast in this class for a specified level.
	 *
	 * @param pcLevel The number of levels in this class that the character has
	 * @param spellLevel The spell level we are interested in
	 * @param aPC The character we are interested in
	 * @return The number of spells per day that this cahracter can cast of this level.
	 */
	public int getCastForLevel(final int pcLevel, final int spellLevel, final PlayerCharacter aPC)
	{
		return getCastForLevel(pcLevel, spellLevel, Globals.getDefaultSpellBook(), true, aPC);
	}

	/**
	 * Return number of speciality spells known
	 * for a level for a given spellbook
	 * @param pcLevel
	 * @param spellLevel
	 * @param aPC
	 * @return int
	 **/
	public int getSpecialtyKnownForLevel(int pcLevel, final int spellLevel, final PlayerCharacter aPC)
	{
		int total;
		total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS." + getKeyName() + ";LEVEL." + spellLevel);
		total += (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE." + getSpellType() + ";LEVEL." + spellLevel);

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		final int index = baseSpellIndex();

		if (index != -2)
		{
			final PCStat aStat = aPC.getStatList().getStatAt(index);
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + aStat.getAbb(), "").intValue();

			if (spellLevel > maxSpellLevel)
			{
				return total;
			}
		}

		StringTokenizer aTok;
		int x = spellLevel;

		for ( String aString : specialtyknownList )
		{
			if (pcLevel == 1)
			{
				aTok = new StringTokenizer(aString, ",");

				while (aTok.hasMoreTokens())
				{
					final String spells = aTok.nextToken();
					final int t = Integer.parseInt(spells);

					if (x == 0)
					{
						total += t;

						break;
					}

					--x;
				}
			}

			--pcLevel;

			if (pcLevel < 1)
			{
				break;
			}
		}

		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if ((total > 0) && (spellLevel > 0))
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

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

	public String getSubClassKey()
	{
		if (subClassKey == null)
		{
			subClassKey = "";
		}

		return subClassKey;
	}

	public final SubClass getSubClassKeyed(final String aKey)
	{
		if (subClassList == null)
		{
			return null;
		}

		for ( SubClass subClass : subClassList )
		{
			if (subClass.getKeyName().equals(aKey))
			{
				return subClass;
			}
		}

		return null;
	}

	public final void setSubClassString(final String aString)
	{
		subClassString = aString;
	}

	public final String getSubClassString()
	{
		return subClassString;
	}

	public ArrayList<String> getTemplates()
	{
		return templates;
	}

	public final Collection<String> getUattList()
	{
		return uattList;
	}

	/**
	 * Set whether or not this class should be displayed to the user in the UI.
	 * @param visible 	true if the class should be displayed to the user.
	 */
	public final void setVisible(final boolean visible)
	{
		this.visible = visible;
	}

	/**
	 * Identify if this class should be displayed to the user in the UI.
	 * @return true if the class should be displayed to the user.
	 */
	public final boolean isVisible()
	{
		return visible;
	}

	public void setFeatAutos(final int aLevel, final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		final String prefix = aLevel + "|";

		while (aTok.hasMoreTokens())
		{
			final String fName = aTok.nextToken();

			if (fName.startsWith(".CLEAR"))
			{
				if (fName.startsWith(".CLEAR."))
				{
					final String postFix = "|" + fName.substring(7);

					//remove feat by name, must run through all 20 levels
					for (int i = 0; i < 45; ++i)
					{
						featAutos.remove(i + postFix);
					}
				}
				else // clear em all
				{
					featAutos.clear();
				}
			}
			else
			{
				getFeatAutos().add(prefix + fName);
			}
		}
	}

	public void setHitPoint(final int aLevel, final Integer iRoll)
	{
		hitPointMap.put(Integer.toString(aLevel), iRoll);
	}

	public Integer getHitPoint(final int j)
	{
		final Integer aHP = hitPointMap.get(Integer.toString(j));

		if (aHP == null)
		{
			return new Integer(0);
		}

		return aHP;
	}

	public final void setHitPointMap(final HashMap<String, Integer> newMap)
	{
		hitPointMap.clear();
		hitPointMap.putAll(newMap);
	}

	public final HashMap<String, Integer> getHitPointMap()
	{
		return hitPointMap;
	}

	/**
	 * Identical function exists in PCTemplate.java. Refactor. XXX
	 * @param aString
	 */
	public void setLanguageBonus(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();

			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				final Language aLang = Globals.getLanguageKeyed(token);

				if (aLang != null)
				{
					getLanguageBonus().add(aLang);
				}
			}
		}
	}

	public String getUattForLevel(int aLevel)
	{
		final String aString = "0";

		if (uattList.isEmpty())
		{
			return aString;
		}

		for ( String uatt : uattList )
		{
			if (aLevel == 1)
			{
				return uatt;
			}

			--aLevel;

			if (aLevel < 1)
			{
				break;
			}
		}

		return null;
	}

	public void setProhibitSpell(String aString)
	{
		SpellProhibitor aProhibitor = new SpellProhibitor(aString);

		if (prohibitSpellDescriptorList == null)
		{
			prohibitSpellDescriptorList = new ArrayList<SpellProhibitor>();
		}
		prohibitSpellDescriptorList.add(aProhibitor);
	}

	/**
	 * we over ride the PObject setVision() function to keep
	 * track of what levels this VISION: tag should take effect
	 * @param aString
	 * @param aPC
	 **/
	public final void setVision(final String aString, final PlayerCharacter aPC)
	{
		// Class based vision lines are of the form:
		// 1|Darkvision(60'),Lowlight
		if (".CLEAR".equals(aString))
		{
			visionList = null;

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final int lvl = Integer.parseInt(aTok.nextToken());
		final String newString = aString.substring(aString.indexOf('|') + 1);

		if (visionList == null)
		{
			visionList = new ArrayList<LevelProperty>();
		}

		final LevelProperty lp = new LevelProperty(lvl, newString);
		visionList.add(lp);
	}

	public final ArrayList<String> getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	public boolean isAutoKnownSpell(final String spellName, final int spellLevel, final PlayerCharacter aPC)
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

		for ( PCClass pcClass : aPC.getClassList() )
		{
			pcClass.calculateKnownSpellsForClassLevel(aPC);
		}
	}


	protected void removeKnownSpellsForClassLevel(final PlayerCharacter aPC)
	{
		final String spellKey = getSpellKey();

		if ((knownSpellsList.size() == 0) || aPC.isImporting() || !aPC.getAutoSpells())
		{
			return;
		}

		if (getSpellSupport().getCharacterSpellCount() == 0)
		{
			return;
		}

		for (Iterator<CharacterSpell> iter = getSpellSupport().getCharacterSpellList().iterator(); iter.hasNext();)
		{
			final CharacterSpell charSpell = iter.next();

			final Spell aSpell = charSpell.getSpell();

			// Check that the character can still cast spells of this level.
			final int[] spellLevels = aSpell.levelForKey(spellKey, aPC);
			for (int i = 0; i < spellLevels.length; i++)
			{
				final int spellLevel = spellLevels[i];

				final boolean isKnownAtThisLevel = isAutoKnownSpell(aSpell.getKeyName(), spellLevel, true, aPC);

				if (!isKnownAtThisLevel)
				{
					iter.remove();
				}
			}
		}
	}



	/**
	 *
   * @param aPC
   */
	protected void calculateKnownSpellsForClassLevel(final PlayerCharacter aPC)
	{
		// If this class has at least one entry in the "Known spells" tag
		// And we aer set up to automatically assign known spells...
		if ((knownSpellsList.size() > 0) && !aPC.isImporting() && aPC.getAutoSpells())
		{
			// Get every spell that can be cast by this class.
			final List<Spell> cspelllist = Globals.getSpellsIn(-1, getSpellKey(), "");
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
			for ( Spell spell : cspelllist )
			{
				// For each spell level that this class can cast this spell at
				final int[] spellLevels = spell.levelForKey(spellKey, aPC);
				for (int si = 0; si < spellLevels.length; ++si)
				{
					final int spellLevel = spellLevels[si];

					if (spellLevel<=_maxLevel) {
						// If the spell is autoknown at this level
						if (isAutoKnownSpell(spell.getKeyName(), spellLevel, true, aPC))
						{
							CharacterSpell cs = getSpellSupport().getCharacterSpellForSpell(spell, this);
							if (cs == null)
							{
								// Create a new character spell for this level.
								cs = new CharacterSpell(this, spell);
								cs.addInfo(spellLevel, 1, Globals.getDefaultSpellBook());
								getSpellSupport().addCharacterSpell(cs);
							}
							else
							{
								if (cs.getSpellInfoFor(Globals.getDefaultSpellBook(),spellLevel,-1) == null)
								{
									cs.addInfo(spellLevel, 1, Globals.getDefaultSpellBook());
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

			for ( CharacterDomain cd : aPC.getCharacterDomainList() )
			{
				if ((cd.getDomain() != null) && cd.isFromPCClass(getKeyName()))
				{
					cd.getDomain().addSpellsToClassForLevels(this, 0, _maxLevel);
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
	public int getMaxCastLevel()
	{
		int currHighest=-1;
		for ( String key : castForLevelMap.keySet() )
		{
			final int keyInt = Integer.parseInt(key);

			final String value = castForLevelMap.get(key);
			if (value != null && !value.equals(""))
			{
				final int val = Integer.parseInt(value);
				if (val>0 && keyInt>currHighest)
				{
					currHighest = keyInt;
				}
			}
		}
		return currHighest;
	}

	public boolean isMonster()
	{
		if (monsterFlag != null)
		{
			return "YES".equals(monsterFlag);
		}

		if (getMyTypeCount() == 0)
		{
			return false;
		}

		for ( String type : getSafeListFor( ListKey.TYPE ) )
		{
			final ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(type);

			if ((aClassType != null) && aClassType.isMonster())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Class section of Natural Attacks
	 * Is just a wrapper to remove the level dependent stuff
	 * @param obj
	 * @param aString
	 **/
	public void setNaturalAttacks(final PObject obj, final String aString)
	{
		final StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		final int lvl = Integer.parseInt(attackTok.nextToken());
		final String sNat = attackTok.nextToken();
		final LevelProperty lp = new LevelProperty(lvl, sNat);

		if (naturalWeapons == null)
		{
			naturalWeapons = new ArrayList<LevelProperty>();
		}

		naturalWeapons.add(lp);
	}

	/**
	 * get the Natural Attacks for this level
	 * @return natural weapons list
	 **/
	public List<Equipment> getNaturalWeapons()
	{
		final List<Equipment> tempArray = new ArrayList<Equipment>();

		if ((naturalWeapons == null) || (naturalWeapons.isEmpty()))
		{
			return tempArray;
		}

		for ( LevelProperty lp : naturalWeapons )
		{
			if (lp.getLevel() <= level)
			{
				final Equipment weapon = (Equipment) lp.getObject();
				tempArray.add(weapon);
				addWeaponProfAutos( weapon.getName() );
			}
		}

		return tempArray;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.PObject#getWeaponProfAutos()
	 */
	public List<String> getWeaponProfAutos() {
		// first build up the list of the standard auto weapon profs
		final List<String> list = super.getWeaponProfAutos();

		// then add in the proficiencies for each natural weapon
		// we have active.
		if (naturalWeapons != null)
		{
			for (Iterator<LevelProperty> li = naturalWeapons.iterator(); li.hasNext();)
			{
				final LevelProperty lp = li.next();
				if (lp.getLevel() <= level)
				{
					final Equipment weapon = (Equipment) lp.getObject();
					list.add( weapon.getSimpleName() );
				}
			}
		}
		return list;
	}

	public boolean isQualified(final PlayerCharacter aPC)
	{

		if (aPC == null)
		{
			return false;
		}

//		if (isMonster() && (preRaceType != null) && !contains(aPC.getCritterType(), preRaceType))
		if (isMonster() && (preRaceType != null) && (!aPC.getRace().getRaceType().equalsIgnoreCase(preRaceType) && !contains(aPC.getCritterType(), preRaceType)))
		// Move the check for type out of race and into PlayerCharacter to make it easier for a template to adjust it.
		{
			return false;
		}

		if (!canBePrestige(aPC))
		{
			return false;
		}

		return true;
	}

	/**
	 * should be "5|4/-" where 5 = level, 4/- is the SR value.
	 * @param srString
	 */
	public void setSR(final String srString)
	{
		if (".CLEAR".equals(srString))
		{
			SR = null;

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(srString, "|", false);
		final int lvl = Integer.parseInt(aTok.nextToken());
		final String tokenSrString = aTok.nextToken();

		if (".CLEAR".equals(tokenSrString))
		{
			SR = null;
		}
		else
		{
			if (SR == null)
			{
				SR = new ArrayList<LevelProperty>();
			}

			final LevelProperty lp = new LevelProperty(lvl, tokenSrString);
			SR.add(lp);
		}
	}

	/**
	 * Assumption: SR list is sorted by level.
	 * @return SR formula
	 */
	public String getSRFormula()
	{
		LevelProperty lp = null;

		if (SR != null)
		{
			final int lvl = level;

			for (int i = 0, x = SR.size(); i < x; ++i)
			{
				if (SR.get(i).getLevel() > lvl)
				{
					break;
				}

				lp = SR.get(i);
			}
		}

		if (lp != null)
		{
			return lp.getProperty();
		}

		return null;
	}

	/**
	 * needed for Class Editor - returns contents of SR(index).
	 * @param index
	 * @param delimiter
	 * @return String
	 */
	public String getSRListString(final int index, final String delimiter)
	{
		if ((SR != null) && (SR.size() > index))
		{
			final LevelProperty lp = SR.get(index);

			return lp.getLevel() + delimiter + lp.getProperty();
		}

		return null;
	}

	public final void setSpellBookUsed(final boolean argUseBook)
	{
		usesSpellbook = argUseBook;
	}

	public final boolean getSpellBookUsed()
	{
		return usesSpellbook;
	}

	public void setCRFormula(final String argCRFormula)
	{
		CRFormula = argCRFormula;
	}

	public void setMonsterFlag(final String monster)
	{
		monsterFlag = monster;
	}

	public String getPCCText()
	{
		final StringBuffer pccTxt = new StringBuffer(200);
		pccTxt.append("CLASS:").append(getDisplayName());
		pccTxt.append(super.getPCCText(false));
		pccTxt.append("\tABB:").append(getAbbrev());
		checkAdd(pccTxt, "", "EXCLASS:", exClass);

		checkAdd(pccTxt, "", "EXCHANGELEVEL:", levelExchange);

		if (hasSubClass)
		{
			pccTxt.append("\tHASSUBCLASS:Y");
		}

		pccTxt.append("\tHD:").append(hitDie);
		checkAdd(pccTxt, "ANY", "DEITY:", CoreUtility.join(deityList, '|'));
		checkAdd(pccTxt, "", "ATTACKCYCLE", attackCycle);
		checkAdd(pccTxt, "", "CASTAS:", castAs);
		checkAdd(pccTxt, Constants.s_NONE, "PROHIBITED:", prohibitedString);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLSTAT:", spellBaseStat);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLTYPE:", spellType);

		if (usesSpellbook)
		{
			pccTxt.append("\tSPELLBOOK:Y");
		}

//		if (skillPoints != 0)
//		{
//			pccTxt.append("\tSTARTSKILLPTS:").append(skillPoints);
//		}
		if (skillPointFormula.length() != 0)
		{
			pccTxt.append("\tSTARTSKILLPTS:").append(skillPointFormula);
		}

		if (!visible)
		{
			pccTxt.append("\tVISIBLE:N");
		}

		if (initialFeats != 0)
		{
			pccTxt.append("\tXTRAFEATS:").append(initialFeats);
		}

		if (levelsPerFeat != null)
		{
			pccTxt.append("\tLEVELSPERFEAT:").append(levelsPerFeat.intValue());
		}

		if (maxLevel != 20)
		{
			pccTxt.append("\tMAXLEVEL:").append(maxLevel);
		}

		if (!memorizeSpells)
		{
			pccTxt.append("\tMEMORIZE:N");
		}

		if (multiPreReqs)
		{
			pccTxt.append("\tMULTIPREREQS:Y");
		}

		if (!knownSpellsList.isEmpty())
		{
			pccTxt.append("\tKNOWNSPELLS:");

			boolean flag = false;

			for ( String spell : knownSpellsList )
			{
				if (flag)
				{
					pccTxt.append('|');
				}

				flag = true;
				pccTxt.append(spell);
			}
		}

		if (itemCreationMultiplier.length() != 0)
		{
			pccTxt.append("\tITEMCREATE:").append(itemCreationMultiplier);
		}

		if (classSpellString != null)
		{
			pccTxt.append("\tSPELLLIST:").append(classSpellString).append('\t');
		}

		checkAdd(pccTxt, "", "SPECIALS:", specialsString);
		checkAdd(pccTxt, "", "SKILLLIST:", classSkillString);

		if (weaponProfBonus.size() != 0)
		{
			pccTxt.append("\tWEAPONBONUS:");

			for (int x = 0; x < weaponProfBonus.size(); ++x)
			{
				if (x != 0)
				{
					pccTxt.append('|');
				}

				pccTxt.append(weaponProfBonus.get(x));
			}
		}

// now all the level-based stuff
		final String lineSep = System.getProperty("line.separator");

		String regionString = getRegionString();
		if ((regionString != null) && !regionString.startsWith("0|"))
		{
			final int x = regionString.indexOf('|');
			pccTxt.append(lineSep).append(regionString.substring(0, x)).append("\tREGION:").append(regionString
				.substring(x + 1));
		}

//		if (kitString != null && !kitString.startsWith("0|"))
//		{
//			int x = kitString.indexOf('|');
//			pccTxt.append(lineSep + kitString.substring(0, x)).append("\tKIT:").append(kitString.substring(x + 1));
//		}
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

		for (int x = 0; x < specialtyknownList.size(); ++x)
		{
			pccTxt.append("\tSPECIALTYKNOWN:").append(specialtyknownList.get(x));
		}

		pccTxt.append(lineSep);

		for (int x = 0; x < castMap.size(); ++x)
		{
			if (castMap.containsKey(String.valueOf(x)))
			{
				final String c = castMap.get(String.valueOf(x));
				final String l = lineSep + String.valueOf(x) + "\tCAST:";
				checkAdd(pccTxt, "0", l, c);
			}
		}

		for (int x = 0; x < knownList.size(); ++x)
		{
			final String c = knownList.get(x);
			final String l = lineSep + String.valueOf(x + 1) + "\tKNOWN:";
			checkAdd(pccTxt, "0", l, c);
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
			for ( LevelProperty lp : SR )
			{
				pccTxt.append(lineSep).append(lp.getLevel()).append("\tSR:").append(lp.getProperty());
			}
		}

		// Output the list of spells associated with the class.
		for (int i=0;i<=maxLevel;i++)
		{
			final List<PCSpell> spellList = getSpellSupport().getSpellListForLevel(i);

			if (spellList != null)
			{
				for ( PCSpell spell : spellList )
				{
					pccTxt.append(lineSep).append(i).append("\tSPELLS:").append(spell.getPCCText());
				}
			}

		}

		for (int x = 0; x < templates.size(); ++x)
		{
			final String c = templates.get(x);
			final int y = c.indexOf('|');
			pccTxt.append(lineSep).append(c.substring(0, y)).append("\tTEMPLATE:").append(c.substring(y + 1));
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
			pccTxt.append(lineSep).append(c.substring(0, y)).append("\tDEFINE:").append(c.substring(y + 1));
		}

		List<LevelAbility> levelAbilityList = getLevelAbilityList();
		if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
		{
			for ( LevelAbility ability : levelAbilityList )
			{
				pccTxt.append(lineSep).append(String.valueOf(ability.level())).append("\tADD:").append(ability.getTagData());
			}
		}

		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for ( SpecialAbility sa : specialAbilityList )
			{
				final String src = sa.getSASource();
				final String lev = src.substring(src.lastIndexOf('|') + 1);
				pccTxt.append(lineSep).append(lev).append("\tSA:").append(sa.toString());
			}
		}

		if ((addDomains != null) && (addDomains.size() != 0))
		{
			buildPccText(pccTxt, addDomains.iterator(), "|", "\tADDDOMAINS:", lineSep);
		}

		buildPccText(pccTxt, domainList.iterator(), "|", "\tDOMAIN:", lineSep);

		buildPccText(pccTxt, featList.iterator(), ":", "\tFEAT:", lineSep);

		buildPccText(pccTxt, featAutos.iterator(), "|", "\tFEATAUTO:", lineSep);

		if ((uattList != null) && (uattList.size() != 0))
		{
			for (int x = 0; x < uattList.size(); ++x)
			{
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append("\tUATT:").append(uattList.get(x));
			}
		}

		List<String> udamList = getListFor(ListKey.UDAM);
		if ((udamList != null) && (udamList.size() != 0))
		{
			for (int x = 0; x < udamList.size(); ++x)
			{
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append("\tUDAM:").append(udamList.get(x));
			}
		}

		List<String> umultList = getListFor(ListKey.UMULT);
		if ((umultList != null) && (umultList.size() != 0))
		{
			buildPccText(pccTxt, umultList.iterator(), "|", "\tUMULT:", lineSep);
		}

		return pccTxt.toString();
	}

	public List<Ability> getVirtualFeatList(final int aLevel)
	{
		final List<Ability> aList = new ArrayList<Ability>();

		for (int i = -9; i <= aLevel; i++)
		{
			if (vFeatMap.containsKey(String.valueOf(i)))
			{
				aList.addAll(vFeatMap.get(String.valueOf(i)));
			}
		}

		return aList;
	}

	/**
	 * Here is where we do the real work of setting the vision
	 * information on the PObject
	 * @param aPC
	 * @return Map
	 **/
	public Map<String, String> getVision(final PlayerCharacter aPC)
	{
		LevelProperty lp;

		if (visionList != null)
		{
			for ( LevelProperty vision : visionList )
			{
				if ( vision.getLevel() <= level )
				{
					super.setVision(vision.getProperty(), aPC);
				}
			}
		}

		return super.getVision();
	}

	public void setWeaponProfBonus(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			getWeaponProfBonus().add(aTok.nextToken());
		}
	}

	public void setXPPenalty(final String argXPPenalty)
	{
		XPPenalty = argXPPenalty;
	}

	/**
	 * Sets qualified BonusObj's to "active"
	 * @param aPC
	 **/
	public void activateBonuses(final PlayerCharacter aPC)
	{
		for ( BonusObj bonus : getBonusList() )
		{
			if ((bonus.getPCLevel() <= level))
			{
				if (bonus.hasPreReqs())
				{
					//TODO: This is a hack to avoid VARs etc in class defs being qualified for when Bypass class prereqs is selected.
					// Should we be passing in the BonusObj here to allow it to be referenced in Qualifies statements?
					if (PrereqHandler.passesAll(bonus.getPrereqList(), aPC, null))
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

	public void addClassSpellList(final String tok)
	{
		if (classSpellList == null)
		{
			newClassSpellList();
		}

		classSpellList.add(tok);
		classSpellString = null;
		stableSpellKey = null;
	}

	public void addDomainList(final String domainItem)
	{
		domainList.add(domainItem);
	}

	public final void addSubClass(final SubClass sClass)
	{
		if (subClassList == null)
		{
			subClassList = new ArrayList<SubClass>();
		}

		sClass.setHitPointMap((HashMap<String, Integer>) getHitPointMap().clone());
		sClass.setHitDie(hitDie);
		subClassList.add(sClass);
	}

	public void addFeatList(final int aLevel, final String aFeatList)
	{
		final String aString = aLevel + ":" + aFeatList;
		featList.add(aString);
	}

	public void addKnown(final int iLevel, final String aString)
	{
		if (iLevel > maxKnownLevel)
		{
			maxKnownLevel = iLevel;
		}
		// pad to with empty entries
		while (knownList.size() < (iLevel - 1))
		{
			knownList.add("0");
		}

		// Replace existing with new entry
		if (knownList.size() >= iLevel)
		{
			knownList.set(iLevel - 1, aString);
		}
		else
		{
			knownList.add(aString);
		}
	}

	public void addKnownSpellsList(final String aString)
	{
		final StringTokenizer aTok;

		if (aString.startsWith(".CLEAR"))
		{
			knownSpellsList.clear();

			if (".CLEAR".equals(aString))
			{
				return;
			}

			aTok = new StringTokenizer(aString.substring(6), "|", false);
		}
		else
		{
			aTok = new StringTokenizer(aString, "|", false);
		}

		while (aTok.hasMoreTokens())
		{
			knownSpellsList.add(aTok.nextToken());
		}
	}

	/**
	 * Add a level of this class to the character. Note this call is assumed
	 * to only be used when loading characters, and some behaviour is
	 * tailored for this.
	 * @param pcLevelInfo
	 *
	 * @param levelMax True if the level caps, if any, for this class
	 *                  should be respected.
	 * @param aPC      The character having the level added.
	 */
	public void addLevel(final PCLevelInfo pcLevelInfo, final boolean levelMax, final PlayerCharacter aPC)
	{
		addLevel(pcLevelInfo, levelMax, false, aPC, true);
	}

	public void addSkillToList(final String aString)
	{
		if (!skillList.contains(aString))
		{
			skillList.add(aString);
		}
	}

	public void addTemplate(final String template)
	{
		templates.add(template);
	}

	/**
	 * Adds virtual feats to the vFeatMao
	 * @param aLevel level
	 * @param vList list of feats
	 **/
	public void addVirtualFeats(final int aLevel, final List<Ability> vList)
	{
		final String levelString = String.valueOf(aLevel);
		List<Ability> vFeatsAtLevel;

		if (vFeatMap.containsKey( levelString ))
		{
			vFeatsAtLevel = vFeatMap.get( levelString );
		}
		else
		{
			vFeatsAtLevel = new ArrayList<Ability>();
			vFeatMap.put(levelString, vFeatsAtLevel);
		}
		vFeatsAtLevel.addAll(vList);

		super.addVirtualFeats(vList);
	}

	/**
	 * returns the value at which another attack is gained
	 * attackCycle of 4 means a second attack is gained at a BAB of +5/+1
	 * @param index
	 * @return int
	 **/
	public int attackCycle(final int index)
	{
		String aKey = null;

		if (index == Constants.ATTACKSTRING_MELEE)
		{
			// Base attack
			aKey = "BAB";
		}
		else if (index == Constants.ATTACKSTRING_RANGED)
		{
			// Ranged attack
			aKey = "RAB";
		}
		else if (index == Constants.ATTACKSTRING_UNARMED)
		{
			// Unarmed attack
			aKey = "UAB";
		}

		final String aString = attackCycleMap.get(aKey);

		if (aString != null)
		{
			return Integer.parseInt(aString);
		}
		return SettingsHandler.getGame().getBabAttCyc();
	}

	public int baseAttackBonus(final PlayerCharacter aPC)
	{
		if (level == 0)
		{
			return 0;
		}

		//final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int) getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB", aPC);

		return i;
	}

	/*
	 * -2 means that the spell itself indicates what stat should be used,
	 * otherwise this method returns an index into the global list of stats for
	 * which stat the bonus spells are based upon.
	 *
	 * @return int
	 */
	public int baseSpellIndex()
	{
		String tmpSpellBaseStat = getSpellBaseStat();

		return "SPELL".equals(tmpSpellBaseStat) ? (-2 // means base spell stat is based upon spell itself
		) : SettingsHandler.getGame().getStatFromAbbrev(tmpSpellBaseStat);
	}

	public int bonusSpellIndex()
	{
		String tmpSpellBaseStat = getBonusSpellBaseStat();

		if (tmpSpellBaseStat.equals(Constants.s_NONE))
		{
			return -1;
		}
		else if (tmpSpellBaseStat.equals(Constants.s_DEFAULT))
		{
			tmpSpellBaseStat = getSpellBaseStat();
		}

		return SettingsHandler.getGame().getStatFromAbbrev(tmpSpellBaseStat);
	}

	public int calcCR(final PlayerCharacter aPC)
	{
		String wCRFormula = "0";

		if (CRFormula != null)
		{
			wCRFormula = CRFormula;
		}
		else
		{
			for ( String type : getSafeListFor(ListKey.TYPE) )
			{
				final ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(type);

				if ((aClassType != null) && !"0".equals(aClassType.getCRFormula()))
				{
					wCRFormula = aClassType.getCRFormula();
				}
			}
		}

		return aPC.getVariableValue(wCRFormula, classKey).intValue();
	}

	public String classLevelString()
	{
		StringBuffer aString = new StringBuffer();

		if (!getSubClassKey().equals(Constants.s_NONE) && !"".equals(getSubClassKey()))
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

	public Object clone()
	{
		PCClass aClass = null;

		try
		{
			aClass = (PCClass) super.clone();
			aClass.setSubClassKey(getSubClassKey());

//			aClass.setSubClassString(getSubClassString());
			aClass.setProhibitedString(getProhibitedString());
			aClass.setHitDie(hitDie);
//			aClass.setSkillPoints(skillPoints);
			aClass.setSkillPointFormula(skillPointFormula);
			aClass.setInitialFeats(initialFeats);
			aClass.setSpellBaseStat(spellBaseStat);
			aClass.setBonusSpellBaseStat(bonusSpellBaseStat);
			aClass.setSpellType(spellType);
			aClass.setAttackBonusType(attackBonusType);
			aClass.specialtyknownList = (ArrayList<String>) specialtyknownList.clone();
			aClass.knownList = (ArrayList<String>) knownList.clone();
			aClass.castMap = new HashMap<String, String>(castMap);
			aClass.uattList = (ArrayList<String>) uattList.clone();
			aClass.acList = (ArrayList<String>) acList.clone();
			aClass.languageBonus = (TreeSet<Language>) languageBonus.clone();
			aClass.weaponProfBonus = (ArrayList<String>) weaponProfBonus.clone();
			aClass.featList = (ArrayList<String>) featList.clone();
//			aClass.vFeatList = (ArrayList) vFeatList.clone();
			aClass.vFeatMap = new HashMap<String, List<Ability>>(vFeatMap);
			aClass.hitDieLockMap = new HashMap<Integer, String>(hitDieLockMap);
			aClass.featAutos = (ArrayList<String>) featAutos.clone();
			aClass.skillList = new ArrayList<String>();

			aClass.classSkillString = classSkillString;
			aClass.classSkillList = null;
			aClass.classSpellString = classSpellString;
			aClass.classSpellList = null;
			aClass.stableSpellKey = null;

			aClass.setSpecialsString(specialsString);
			aClass.setExClass(exClass);
			aClass.setLevelExchange(levelExchange);
			aClass.maxCastLevel = maxCastLevel;
			aClass.maxKnownLevel = maxKnownLevel;

			aClass.abbrev = abbrev;
			aClass.memorizeSpells = memorizeSpells;
			aClass.multiPreReqs = multiPreReqs;
			aClass.deityList = new ArrayList<String>(deityList);
			aClass.maxLevel = maxLevel;
			aClass.knownSpellsList = new ArrayList<String>(knownSpellsList);
			aClass.attackCycle = attackCycle;
			aClass.attackCycleMap = new HashMap<String, String>(attackCycleMap);
			aClass.castAs = castAs;
			aClass.preRaceType = preRaceType;
			aClass.modToSkills = modToSkills;
			aClass.levelsPerFeat = levelsPerFeat;
			aClass.initMod = initMod;
			aClass.specialtyList = new ArrayList<String>(specialtyList);

			//aClass.ageSet = ageSet;
			aClass.domainList = (ArrayList<String>) domainList.clone();
			aClass.addDomains = (ArrayList<String>) addDomains.clone();
			aClass.hitPointMap = new HashMap<String, Integer>(hitPointMap);
			aClass.hasSubClass = hasSubClass;
			aClass.subClassList = subClassList;

			aClass.hasSpellFormulas = hasSpellFormulas;

			if (naturalWeapons != null)
			{
				aClass.naturalWeapons = (ArrayList<LevelProperty>) naturalWeapons.clone();
			}
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return aClass;
	}

	public final boolean hasSpellFormula()
	{
		return hasSpellFormulas;
	}

	public final boolean hasSubClass()
	{
		return hasSubClass;
	}

	public final boolean multiPreReqs()
	{
		return multiPreReqs;
	}

	public final String toString()
	{
		return displayName;
	}

	public void setName(final String newName)
	{
		super.setName(newName);

		int i = 3;

		if ("".equals(abbrev))
		{
			if (newName.length() < 3)
			{
				i = newName.length();
			}

			abbrev = newName.substring(0, i);
		}

		stableSpellKey = null;
		getSpellKey();
	}

	public boolean isSpecialtySpell(final Spell aSpell)
	{
		final Collection<String> aList = getSpecialtyList();

		if ((aList == null) || (aList.size() == 0))
		{
			return false;
		}

		return (aSpell.descriptorListContains(aList) || aSpell.schoolContains(aList) || aSpell.subschoolContains(aList));
	}

	public ArrayList<SubClass> getSubClassList()
	{
		return subClassList;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * @param aBonus
	 * @param anObj
	 * @param aPC
	 * @return double
	 **/
	public double calcBonusFrom(final BonusObj aBonus, final Object anObj, PlayerCharacter aPC)
	{
		double retVal = 0;
		int iTimes = 1;

		final String aType = aBonus.getTypeOfBonus();

		//String aName = aBonus.getBonusInfo();
		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			String choiceString = getChoiceString();
			if (choiceString.startsWith("SALIST|") && (choiceString.indexOf("|VAR|") >= 0))
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
					final String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart)
						.toString();
					retVal += calcPartialBonus(xString, iTimes, aBonus, anObj);
				}

				bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString();
			}
		}

		retVal += calcPartialBonus(bString, iTimes, aBonus, anObj);

		return retVal;
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 * @param aBonus
	 * @param anObj
	 * @param listString
	 * @param aPC
	 * @return double
	 **/
	public double calcBonusFrom(
			final BonusObj  aBonus,
			final Object    anObj,
			final String    listString,
			PlayerCharacter aPC)
	{
		return calcBonusFrom(aBonus, anObj, aPC);
	}

	public boolean hasClassSkillList(final String aString)
	{
		if ((classSkillList == null) || classSkillList.isEmpty())
		{
			return false;
		}

		for ( String key : classSkillList )
		{
			final PCClass pcClass = Globals.getClassKeyed(key);

			if ((pcClass != null) && pcClass.hasCSkill(aString))
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasKnownSpells(final PlayerCharacter aPC)
	{
		for (int i = 0; i <= getHighestLevelSpell(); i++)
		{
			if (getKnownForLevel(getLevel(), i, aPC) > 0)
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasSkill(final String aString)
	{
		for ( String key : skillList )
		{
			if (key.equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	public boolean hasXPPenalty()
	{
		String wXPPenalty = "YES";

		if (XPPenalty != null)
		{
			return "YES".equals(XPPenalty);
		}

		for ( String type : getSafeListFor(ListKey.TYPE) )
		{
			final ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(type);

			if ((aClassType != null) && !aClassType.getXPPenalty())
			{
				wXPPenalty = "NO";
			}
		}

		return "YES".equals(wXPPenalty);
	}

	public int hitPoints(final int iConMod)
	{
		int total = 0;

		for (int i = 0; i <= getLevel(); ++i)
		{
			if (getHitPoint(i).intValue() > 0)
			{
				int iHp = getHitPoint(i).intValue() + iConMod;

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

	public final int skillPool()
	{
		return skillPool;
	}

	public void setSkillPool(final int i)
	{
		skillPool = i;
	}

	public String specialsString()
	{
		return specialsString;
	}

	public boolean zeroCastSpells()
	{
		for ( String key : getCastMap().keySet() )
		{
			final String aVal = getCastMap().get(key);
			final StringTokenizer aTok = new StringTokenizer(aVal, ",");
			int numSpells = 0;

			while (aTok.hasMoreTokens())
			{
				final String spellNum = aTok.nextToken();

				try
				{
					numSpells = Integer.parseInt(spellNum);
				}
				catch (NumberFormatException nfe)
				{
					// ignore
				}

				if (numSpells > 0)
				{
					return false;
				}
			}
		}

		return true;
	}

	protected List<SpecialAbility> addSpecialAbilitiesToList(final List<SpecialAbility> aList, final PlayerCharacter aPC)
	{
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList == null) || specialAbilityList.isEmpty())
		{
			return aList;
		}

		final List<SpecialAbility> bList = new ArrayList<SpecialAbility>();

		for ( SpecialAbility sa : specialAbilityList )
		{
			final String saKey = sa.getKeyName();

			if (sa.pcQualifiesFor(aPC))
			{
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
								if (subKey.substring(0, subKey.indexOf('(')).trim().equals(saToRemove))
								{
									bList.remove(itIdx);
								}
							}
						}
					}

					continue;
				}

				bList.add(sa);
			}
		}

		aList.addAll(bList);

		return aList;
	}

	protected void doGlobalTypeUpdate(final String aString)
	{
		//add to global PCClassType list for future filtering
		if (!Globals.getPCClassTypeList().contains(aString))
		{
			Globals.getPCClassTypeList().add(aString);
		}
	}

	final List<String> getClassSkillList()
	{
		return classSkillList;
	}

	/**
	 * Return number of spells known for a level for a given spellbook.
	 * @param pcLevel
	 * @param spellLevel
	 * @param bookName
	 * @param aPC
	 * @return known for spell level
	 */
	int getKnownForLevel(int pcLevel, final int spellLevel, final String bookName, final PlayerCharacter aPC)
	{
		int total = 0;
		int stat = 0;
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", keyName);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		if ((getCastMap().size() > 0) && (getNumFromCastList(pcLevel, spellLevel, aPC) < 0))
		{
			// Don't know any spells of this level
			// however, character might have a bonus spells e.g. from certain feats
			return (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel);
		}
		if (pcLevel > maxKnownLevel)
		{
			pcLevel = maxKnownLevel;
		}

		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + allSpellLevel);

		final int index = baseSpellIndex();
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

		final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "KNOWN." + statString)
			+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT")
			+ (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT;CLASS." + keyName);

		if (index > -2)
		{
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();

			if ((maxSpellLevel + bonusStat) < spellLevel)
			{
				return total;
			}
		}

		stat += bonusStat;

		int mult = (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", classKeyName + levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", "TYPE." + getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		boolean psiSpecialty = false;

		if (!getKnownList().isEmpty())
		{
			if (pcLevel > getKnownList().size())
			{
				// doesn't know any spells of this level
				return 0;
			}

			final String aString = getKnownList().get(pcLevel - 1);
			final StringTokenizer aTok = new StringTokenizer(aString, ",");
			int iCount = 0;

			while (aTok.hasMoreTokens())
			{
				String spells = aTok.nextToken();

				if (iCount == spellLevel)
				{
					if (spells.endsWith("+d"))
					{
						psiSpecialty = true;

						if (spells.length() > 1)
						{
							spells = spells.substring(0, spells.length() - 2);
						}
					}

					int t;
					if (hasSpellFormula())
					{
						t = aPC.getVariableValue(spells, "").intValue();
					}
					else
					{
						t = Integer.parseInt(spells);
					}
					total += (t * mult);

					// add Stat based bonus
					final String bonusSpell = Globals.getBonusSpellMap().get(String.valueOf(spellLevel));

					if (Globals.checkRule(RuleConstants.BONUSSPELLKNOWN) && (bonusSpell != null) && !bonusSpell.equals("0|0"))
					{
						final StringTokenizer s = new StringTokenizer(bonusSpell, "|");
						final int base = Integer.parseInt(s.nextToken());
						final int range = Integer.parseInt(s.nextToken());

						if (stat >= base)
						{
							total += Math.max(0, (stat - base + range) / range);
						}
					}

					if (psiSpecialty)
					{
						total += numSpellsFromSpecialty;
					}
				}

				iCount++;
			}
		}

		// if we have known spells (0==no known spells recorded)
		// or a psi specialty.
		if (((total > 0) && (spellLevel > 0)) && !psiSpecialty)
		{
			// make sure any slots due from specialties
			// (including domains) are added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

	final Set<Language> getLanguageBonus()
	{
		return languageBonus;
	}

	final int getNumSpellsFromSpecialty()
	{
		return numSpellsFromSpecialty;
	}

	public boolean isProhibited(final Spell aSpell, final PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);

		if (! PrereqHandler.passesAll( aSpell.getPreReqList(), aPC, this) )
		{
			return true;
		}

		if ( prohibitSpellDescriptorList != null )
		{
			for (SpellProhibitor prohibit : prohibitSpellDescriptorList)
			{
				if (PrereqHandler.passesAll(prohibit.getPrereqList(), aPC, null))
				{
					if (prohibit.getType() == SpellProhibitor.TYPE_ALIGNMENT
						&& Globals.checkRule(RuleConstants.PROHIBITSPELLS))
					{
						int hits = 0;
						for (Iterator desc = aSpell.getDescriptorList().iterator();
							 desc.hasNext(); )
						{
							String aDescriptor = desc.next().toString().toUpperCase();
							for (Iterator it = prohibit.getValueList().iterator();
								 it.hasNext(); )
							{
								if (aDescriptor.equals(it.next().toString()))
								{
									hits++;
								}
							}
						}
						if (hits == prohibit.getValueList().size())
						{
							return true;
						}
					}
					else if (prohibit.getType() == SpellProhibitor.TYPE_DESCRIPTOR)
					{
						int hits = 0;
						for (String desc : aSpell.getDescriptorList())
						{
							for (String prohib : prohibit.getValueList())
							{
								if (desc.equals(prohib))
								{
									hits++;
								}
							}
						}
						if (hits == prohibit.getValueList().size())
						{
							return true;
						}
					}
				}
			}
		}

		while (aTok.hasMoreTokens())
		{
			final String a = aTok.nextToken();

			if (aSpell.getSchools().contains(a) || aSpell.getSubschools().contains(a))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the unarmed Damage for this class at the given level.
	 * @param aLevel
	 * @param includeCrit
	 * @param includeStrBonus
	 * @param aPC
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	String getUdamForLevel(
			int                   aLevel,
			final boolean         includeCrit,
			final boolean         includeStrBonus,
			final PlayerCharacter aPC,
			boolean               adjustForPCSize)
	{
		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;

		aLevel += (int) aPC.getTotalBonusTo("UDAM", "CLASS." + keyName);

		int iLevel = aLevel;
		final Equipment eq = EquipmentList.getEquipmentKeyed("KEY_Unarmed Strike");

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
			aDamage = Globals.adjustDamage(
					aDamage,
					SettingsHandler.getGame().getDefaultSizeAdjustment().getAbbreviation(),
					SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize).getAbbreviation());
		}

		//
		// Check the UDAM list for monk-like damage
		//
		List<String> udamList = Globals.getClassKeyed(keyName).getListFor(ListKey.UDAM);

		if ((udamList != null) && !udamList.isEmpty())
		{
			if (udamList.size() == 1)
			{
				final String aString = udamList.get(0);

				if (aString.startsWith("CLASS=") || aString.startsWith("CLASS."))
				{
					final PCClass aClass = Globals.getClassKeyed(aString.substring(6));

					if (aClass != null)
					{
						return aClass.getUdamForLevel(aLevel, includeCrit, includeStrBonus, aPC, adjustForPCSize);
					}

					Logging.errorPrint(keyName + " refers to " + aString.substring(6) + " which isn't loaded.");

					return aDamage;
				}
			}

			if (aLevel > udamList.size())
			{
				iLevel = udamList.size();
			}

			final StringTokenizer aTok = new StringTokenizer(udamList.get(Math.max(iLevel - 1, 0)).toString(), ",",
					false);

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
	 * Increases or decreases the initiative modifier by the given value.
	 * @param initModDelta
	 */
	public void addInitMod(final int initModDelta)
	{
		initMod += initModDelta;
	}



	/**
	 * Adds a level of this class to the current Global PC.
	 *
	 * This method is deeply evil. This instance of the PCClass
	 * has been assigned to a PlayerCharacter, but the only way we can get
	 * from this class back to the PlayerCharacter is to get
	 * the current global character and hope that the caller
	 * is only calling this method on a PCClass embedded within
	 * the current global PC.
	 *
	 * TODO: Split the PlayerCharacter code out of PCClass (i.e. the level
	 * property). Then have a joining class assigned to PlayerCharacter
	 * that maps PCClass and number of levels in the class.
	 * @param pcLevelInfo
	 *
	 * @param argLevelMax True if we should only allow extra levels if there are still
	 *                    levels in this class to take. (i.e. a lot of prestige classes
	 *                    stop at level 10, so if this is true it would not allow an 11th
	 *                    level of the class to be added
	 * @param bSilent True if we are not to show any dialog boxes about errors or questions.
	 * @param aPC The character we are adding the level to.
	 * @param isLoading True if the character is being loaded and prereqs for the
	 *                   level should be ignored.
	 * @return true or false
	 */
	boolean addLevel(final PCLevelInfo pcLevelInfo, final boolean argLevelMax, final boolean bSilent, final PlayerCharacter aPC, final boolean isLoading)
	{

		// Check to see if we can add a level of this class to the
		// current character
		final int newLevel = level + 1;
		boolean levelMax = argLevelMax;

		level += 1;
		if (!isLoading)
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

		if ((newLevel > maxLevel) && levelMax)
		{
			if (!bSilent)
			{
				ShowMessageDelegate.showMessageDialog("This class cannot be raised above level " + Integer.toString(maxLevel),
					Constants.s_APPNAME, MessageType.ERROR);
			}

			return false;
		}


		// Add the level to the current character
		int total = aPC.getTotalLevels();

		if (total == 0)
		{
			aPC.setFeats(aPC.getInitialFeats());
		}
		setLevel(newLevel, aPC);

		// the level has now been added to the character,
		// so now assign the attributes of this class level to the
		// character...
		for ( String template : getTemplates(aPC.isImporting(), aPC) )
		{
			aPC.addTemplateKeyed(template);
		}

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		final int dnum = aPC.getMaxCharacterDomains(this, aPC) - aPC.getCharacterDomainUsed();

		if (!aPC.hasDomainSource("PCClass", getKeyName(), newLevel))
		{
			if (dnum > 0)
			{
				aPC.addDomainSource("PCClass", getKeyName(), newLevel, dnum);
			}
		}


		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel, aPC, pcLevelInfo);

		//Don't roll the hit points if the gui is not being used.
		//This is so GMGen can add classes to a person without pcgen flipping out
		if (Globals.getUseGUI())
		{
			rollHP(aPC, level, aPC.getTotalLevels() == 1);
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
				if (total <= aPC.getRace().getMonsterClassLevels(aPC) )
				{
					processBonusStats = false;
				}

				/*
				 * If we are usign default monsters and we have not yet added
				 * all of the racial monster levels then we can not add any feats.
				 * i.e. a default monster Ogre will not get a feat at 1st or 3rd level
				 * because they have already been allocated in the race, but a
				 * non default monster will get the 2 bonus feats instead.
				 * Both versions of the monster will get one at 6th level. i.e. default
				 * Ogre with 2 class levels, or no default Ogre with 4 giant levels and 2
				 * class levels.
				 */
				if (aPC.isMonsterDefault() && total <= aPC.getRace().getMonsterClassLevels(aPC) )
				{
					processBonusFeats = false;
				}
			}

			if (!aPC.isImporting())
			{
				// We do not want to do these
				// calculations a second time when are
				// importing a character.  The feat
				// number and the stat point pool are
				// already saved in the import file.

				if (processBonusFeats)
				{
					final double bonusFeats = aPC.getBonusFeatsForNewLevel(this);
					if (bonusFeats > 0)
					{
//						aPC.setFeats(aPC.getFeats() + bonusFeats);
						aPC.adjustFeats(bonusFeats);
					}
				}


				if (processBonusStats)
				{
					final int bonusStats = Globals.getBonusStatsForLevel(total);
					if (bonusStats > 0)
					{
						aPC.setPoolAmount(aPC.getPoolAmount() + bonusStats);

						if (!bSilent && SettingsHandler.getShowStatDialogAtLevelUp())
						{
							levelUpStats = askForStatIncrease(aPC, bonusStats, true);
						}
					}
				}
			}
		}

		// Update Skill Points.  Modified 20 Nov 2002 by sage_sam
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
			aPC.addNaturalWeapons( getNaturalWeapons());
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
					ShowMessageDelegate.showMessageDialog(SettingsHandler.getGame().getLevelUpMessage(), Constants.s_APPNAME, MessageType.INFORMATION);
				}
			}
		}

		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel() == 1) && !aPC.isImporting())
		{
			exchangeLevels(aPC);
		}
		return true;
	}

	/**
	 * @param aPC
	 */
	private void exchangeLevels(final PlayerCharacter aPC) {
		final StringTokenizer aTok = new StringTokenizer(levelExchange, "|", false);

		if (aTok.countTokens() != 4)
		{
			Logging.errorPrint("levelExhange: invalid token count: " + aTok.countTokens());
		}
		else
		{
			try
			{
				final String classKey = aTok.nextToken(); // Class to get levels from
				final int iMinLevel = Integer.parseInt(aTok.nextToken()); // Minimum level required in donating class
				int iMaxDonation = Integer.parseInt(aTok.nextToken()); // Maximum levels donated from class
				final int iLowest = Integer.parseInt(aTok.nextToken()); // Lowest that donation can lower donating class level to

				final PCClass aClass = aPC.getClassKeyed(classKey);

				if (aClass != null)
				{
					final int iLevel = aClass.getLevel();

					if (iLevel >= iMinLevel)
					{
						iMaxDonation = Math.min(Math.min(iMaxDonation, iLevel - iLowest), getMaxLevel() - 1);

						if (iMaxDonation > 0)
						{
							//
							// Build the choice list
							//
							final List<String> choiceNames = new ArrayList<String>();

							for (int i = 0; i <= iMaxDonation; ++i)
							{
								choiceNames.add(Integer.toString(i));
							}

							//
							// Get number of levels to exchange for this class
							//
							final ChooserInterface c = ChooserFactory.getChooserInstance();
							c.setTitle("Select number of levels to convert from " + aClass.getDisplayName() + " to " + getDisplayName());
							c.setPool(1);
							c.setPoolFlag(false);
							c.setAvailableList(choiceNames);
							c.setVisible(true);

							final List<String> selectedList = c.getSelectedList();
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
				ShowMessageDelegate.showMessageDialog("levelExchange:" + Constants.s_LINE_SEP + exc.getMessage(), Constants.s_APPNAME,
					MessageType.ERROR);
			}
		}
	}


	void doMinusLevelMods(final PlayerCharacter aPC, final int oldLevel)
	{
		if (!isMonster())
		{
			changeFeatsForLevel(oldLevel, false, aPC);
		}

		subAddsForLevel(oldLevel, aPC);
		aPC.removeVariable("CLASS:" + getKeyName() + "|" + Integer.toString(oldLevel));
	}

	void doPlusLevelMods(final int newLevel, final PlayerCharacter aPC, final PCLevelInfo pcLevelInfo)
	{
		if (!isMonster())
		{
			changeFeatsForLevel(newLevel, true, aPC);
		}

		addVariablesForLevel(newLevel, aPC);

		// moved after changeSpecials and addVariablesForLevel
		// for bug #688564 -- sage_sam, 18 March 2003
		aPC.calcActiveBonuses();
		addAddsForLevel(newLevel, aPC, pcLevelInfo);
	}

	/**
	 * Update the name of the required class for all special abilites, DEFINE's, and BONUS's
	 *
	 * @param oldClass The name of the class that should have the special abliities changed
	 * @param newClass The name of the new class for the altered special abilities
	 */
	void fireNameChanged(final String oldClass, final String newClass)
	{
		//
		// This gets called on clone(), so don't traverse the list if the names are the same
		//
		if (oldClass.equals(newClass))
		{
			return;
		}

		//
		// Go through the specialty list (SA) and adjust the class to the new name
		//
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList != null)
		{
			for (int idx = specialAbilityList.size() - 1; idx >= 0; --idx)
			{
				SpecialAbility sa = specialAbilityList.get(idx);

				if (sa.getSource().length() != 0)
				{
					sa = new SpecialAbility(sa.getKeyName(), sa.getSASource(), sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					specialAbilityList.set(idx, sa);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new name
		//
		if (getVariableCount() > 0)
		{
			for (int idx = getVariableCount() - 1; idx >= 0; --idx)
			{
				final Variable variable = getVariable(idx);
				String formula = variable.getValue();

				formula = formula.replaceAll("="+oldClass, "="+newClass);

				variable.setValue(formula);
			}
		}

		//
		// Go through the bonus list (BONUS) and adjust the class to the new name
		//
		if (getBonusList() != null)
		{
			for ( BonusObj bonusObj : getBonusList() )
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
		// Go through the damage reduction list (DR) and adjust the class to the new name
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

	String makeBonusString(final String bonusString, final String chooseString, final PlayerCharacter aPC)
	{
		return "0|" + super.makeBonusString(bonusString, chooseString, aPC);
	}

	/**
	 * Added to help deal with lower-level spells prepared in higher-level slots.
	 * BUG [569517]
	 * Works in conjunction with PlayerCharacter method availableSpells()
	 * sk4p 13 Dec 2002
	 * @param aLevel
	 * @param bookName
	 * @return int
	 */
	int memorizedSpecialtiesForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final List<CharacterSpell> aList = getSpellSupport().getCharacterSpell(null, bookName, aLevel);

		if (aList.isEmpty())
		{
			return m;
		}

		for ( CharacterSpell cs : aList )
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
		final List<CharacterSpell> aList = getSpellSupport().getCharacterSpell(null, bookName, aLevel);

		if (aList.isEmpty())
		{
			return m;
		}

		for ( CharacterSpell cs : aList )
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
			final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

			if ((specialAbilityList != null) && !specialAbilityList.isEmpty())
			{
				// remove any choice or SPECIALS: related special abilities the PC no longer qualifies for
				for (int i = specialAbilityList.size() - 1; i >= 0; --i)
				{
					final SpecialAbility sa = specialAbilityList.get(i);

					if (sa.getSource().startsWith("PCCLASS|") && !sa.pcQualifiesFor(aPC))
					{
						specialAbilityList.remove(sa);
					}
				}
			}

			int spMod = 0;
			final PCLevelInfo pcl = aPC.getLevelInfoFor(keyName, level);

			if (pcl != null)
			{
				spMod = pcl.getSkillPointsGained();
			}
			else
			{
				Logging.errorPrint("ERROR: could not find class/level info for " + displayName + "/" + level);
			}

			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			List<LevelAbility> levelAbilityList = getLevelAbilityList();
			if ((levelAbilityList != null) && !levelAbilityList.isEmpty())
			{
				for ( LevelAbility levAbility : levelAbilityList )
				{
					if ((levAbility.level() == level) && levAbility.isFeat())
					{
						aPC.adjustFeats( - 1);
					}
				}
			}

			final Integer zeroInt = new Integer(0);
			final int newLevel = level - 1;

			if (level > 0)
			{
				setHitPoint(level - 1, zeroInt);
			}

			aPC.adjustFeats( - aPC.getBonusFeatsForNewLevel(this) );
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
				for ( Skill skill : aPC.getSkillList() )
				{
					skill.setZeroRanks(this, aPC);
				}

				spMod = skillPool();
			}

			if (!isMonster() && (total > aPC.getTotalLevels()))
			{
				total = aPC.getTotalLevels();

				// Roll back any stat changes that were made as part of the level

				final List<PCLevelInfoStat> moddedStats = new ArrayList<PCLevelInfoStat>();
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
					for ( PCLevelInfoStat statToRollback : moddedStats )
					{
						for (Iterator<PCStat> i = aPC.getStatList().iterator(); i.hasNext();)
						{
							final PCStat aStat = i.next();

							if (aStat.getAbb().equalsIgnoreCase(statToRollback.getStatAbb()))
							{
								aStat.setBaseScore(aStat.getBaseScore() - statToRollback.getStatMod());
								break;
							}
						}
					}
				}
			}

			if (!isMonster() && (total == 0))
			{
				aPC.setSkillPoints(0);
				aPC.setFeats(0);
				aPC.getSkillList().clear();
				aPC.clearRealFeats();
				aPC.getWeaponProfList().clear();
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
			aPC.removeNaturalWeapons(this);
		}
		else
		{
			Logging.errorPrint("No current pc in subLevel()? How did this happen?");

			return;
		}
	}

	private double getBonusTo(final String type, final String mname, final PlayerCharacter aPC)
	{
		return getBonusTo(type, mname, level, aPC);
	}

	/**
	 * This method can be called to determine if the number of extra HD
	 * for purposes of skill points, feats, etc. See MM p. 11
	 * extracted 03 Dec 2002 by sage_sam for bug #646816
	 * @param aPC currently selected PlayerCharacter
	 * @param hdTotal int number of monster HD the character has
	 * @return int number of HD considered "Extra"
	 */
	private static int getExtraHD(final PlayerCharacter aPC, final int hdTotal)
	{
		// Determine the EHD modifier based on the size category
		final int sizeInt = Globals.sizeInt(aPC.getRace().getSize());
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

	private boolean isAutoKnownSpell(final String aSpellKey, final int spellLevel, final boolean useMap, final PlayerCharacter aPC)
	{
		if (knownSpellsList.isEmpty())
		{
			return false;
		}

		final Spell aSpell = Globals.getSpellKeyed(aSpellKey);

		if (useMap)
		{
			final String val = castForLevelMap.get(String.valueOf(spellLevel));

			if ((val == null) || (Integer.parseInt(val) == 0) || (aSpell == null))
			{
				return false;
			}
		}
		else if ((getCastForLevel(level, spellLevel, aPC) == 0) || (aSpell == null))
		{
			return false;
		}

		if (isProhibited(aSpell, aPC) && !isSpecialtySpell(aSpell))
		{
			return false;
		}

		boolean flag = true;

		// iterate through the KNOWNSPELLS: tag
		for ( String spellStr : knownSpellsList )
		{
			flag = true;

			final StringTokenizer spellTok = new StringTokenizer(spellStr, ",", false);

			// must satisfy all elements in a comma delimited list
			while (spellTok.hasMoreTokens() && flag)
			{
				final String bString = spellTok.nextToken();

				// if the argument starts with LEVEL=, compare the level to the desired spellLevel
				if (bString.startsWith("LEVEL=") || bString.startsWith("LEVEL."))
				{
					flag = Integer.parseInt(bString.substring(6)) == spellLevel;
				}

				// if it starts with TYPE=, compare it to the spells type list
				else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
				{
					flag = aSpell.isType(bString.substring(5));
				}

				// otherwise it must be the spell's name
				else
				{
					flag = bString.equals(aSpellKey);
				}
			}

			// if we found an entry in KNOWNSPELLS: that is satisfied, we can stop
			if (flag)
			{
				break;
			}
		}

		return flag;
	}

	/*
	 * This method calculates skill modifier for a monster character.
	 *
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643 and updated to fix the bug.
	 */
	private int getMonsterSkillPointMod(final PlayerCharacter aPC, final int total)
	{
		int spMod = 0;
		final int lockedMonsterSkillPoints = (int) aPC.getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");

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
//				spMod = getSkillPoints();
				spMod = aPC.getVariableValue(getSkillPointFormula(), classKey).intValue();
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
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643
	 */
	private int getNonMonsterSkillPointMod(final PlayerCharacter aPC, final int total)
	{
//		int spMod = getSkillPoints();
		int lockedMonsterSkillPoints;
		int spMod = aPC.getVariableValue(getSkillPointFormula(), classKey).intValue();

		spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");

		if (isMonster())
		{
			lockedMonsterSkillPoints = (int) aPC.getTotalBonusTo("MONSKILLPTS", "LOCKNUMBER");
			if (lockedMonsterSkillPoints > 0)
			{
				spMod = lockedMonsterSkillPoints;
			}
			else if (total == 1)
			{
				int monSkillPts = (int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER");
				if (monSkillPts != 0)
				{
					spMod = monSkillPts;
				}
			}

			if (total != 1)
			{
				// If this level is one that is not entitled to skill points based
				// on the monster's size, zero out the skills for this level
				final int nonSkillHD = (int) aPC.getTotalBonusTo("MONNONSKILLHD", "NUMBER");
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

			spMod *= Math.min(Globals.getSkillMultiplierForLevel(total), aPC.getRace().getInitialSkillMultiplier());
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
	 * <String,String> where the key is the spell level and the value
	 * is the number of times per day that spell level can be cast by the character
	 * @param aPC
	 *
	 * TODO: Why is this not a Map<Integer,Integer>
	 */
	private void calcCastPerDayMapForLevel(final PlayerCharacter aPC)
	{
		//
		// TODO: Shouldn't we be using Globals.getLevelInfo().size() instead of 100?
		// Byngl -- November 25, 2002
		//
		for (int i = 0; i < 100; i++)
		{
			final int s = getCastForLevel(level, i, aPC);
			castForLevelMap.put(String.valueOf(i), String.valueOf(s));
		}
	}

	/**
	 * <p>This function adds all templates up to the current level to
	 * <code>templatesAdded</code> and returns a list of the names of those
	 * templates.</p>
	 * <p>The function requires that templates be stored in the <code>templates</code> list
	 * as a string in the form LVL|[CHOOSE:]Template|Template|Template...</p>
	 * <p>Passing <code>false</code> to this function results in nothing happening, although
	 * the function still parses all of the template lines, it doesn't add anything
	 * to the class.</p>
	 *
	 * @param flag If false, function returns empty <code>ArrayList</code> (?)
	 * @param aPC
	 * @return A list of templates added by this function
	 */
	public List<String> getTemplates(final boolean flag, final PlayerCharacter aPC)
	{
		final ArrayList<String> newTemplates = new ArrayList<String>();
		templatesAdded = new ArrayList<String>();

		for (int x = 0; x < templates.size(); ++x)
		{
			final String template = templates.get(x);
			final StringTokenizer aTok = new StringTokenizer(template, "|", false);

			if (level < Integer.parseInt(aTok.nextToken()))
			{
				continue;
			}

			//The next token will either be a CHOOSE: tag or a template;
			//we handle CHOOSE: tags by retrieving the rest of the string
			final String tString = aTok.nextToken();

			if (tString.startsWith("CHOOSE:") && !flag)
			{
				newTemplates.add(PCTemplate.chooseTemplate(this, template.substring(template.indexOf("CHOOSE:") + 7), true, aPC));
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
			}
			else if (!flag)
			{
				newTemplates.add(tString);
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));

				while (aTok.hasMoreTokens())
				{
					newTemplates.add(aTok.nextToken());
					templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
				}
			}
		}

		return newTemplates;
	}

	private static String getToken(int tokenNum, final String aList, final String delim)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, delim, false);

		while (aTok.hasMoreElements() && (tokenNum >= 0))
		{
			final String aString = aTok.nextToken();

			if (tokenNum == 0)
			{
				return aString;
			}

			--tokenNum;
		}

		return null;
	}

	private String getUMultForLevel(final int aLevel)
	{
		String aString = "0";

		List<String> umultList = getListFor(ListKey.UMULT);
		if ((umultList == null) || umultList.isEmpty())
		{
			return aString;
		}

		for ( String umult : umultList )
		{
			final int pos = umult.lastIndexOf('|');

			if ((pos >= 0) && (aLevel <= Integer.parseInt(umult.substring(0, pos))))
			{
				aString = umult.substring(pos + 1);
			}
		}

		return aString;
	}

	private void addVariablesForLevel(final int aLevel, final PlayerCharacter aPC)
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
	// Ask user to select a stat to increment. This can happen before skill points
	// are calculated, so an increase to the appropriate stat can give more skill points
	//
	private final int askForStatIncrease(final PlayerCharacter aPC, final int statsToChoose, final boolean isPre)
	{
		//
		// If 1st time here (checks for preincrement), then will only ask if want to ask before level up
		// If 2nd time here, will ask if there are any remaining points unassigned.
		// So, hitting cancel on the 1st popup will cause the 2nd popup to ask again.
		// This is to handle cases where the user is adding multiple levels, so the SKILL point total
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
				final int iAdjStat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
				final int iCurStat = aPC.getStatList().getBaseStatFor(aStat.getAbb());
				sStats.append(aStat.getAbb()).append(": ").append(iCurStat);

				if (iCurStat != iAdjStat)
				{
					sStats.append(" adjusted: ").append(iAdjStat);
				}

				sStats.append(" (").append(aPC.getStatList().getStatModFor(aStat.getAbb())).append(")\n");
			}

			final InputInterface ii = InputFactory.getInputInstance();
			final Object selectedValue = ii.showInputDialog(null,
					"Choose stat to increment or select Cancel to increment stat on the Summary tab." + extraMsg
					+ "\n\n" + "Current Stats:\n" + sStats + "\n", Constants.s_APPNAME, MessageType.INFORMATION,
					SettingsHandler.getGame().s_ATTRIBLONG, SettingsHandler.getGame().s_ATTRIBLONG[0]);

			if (selectedValue != null)
			{
				for (Iterator<PCStat> i = aPC.getStatList().iterator(); i.hasNext();)
				{
					final PCStat aStat = i.next();

					if (aStat.getDisplayName().equalsIgnoreCase(selectedValue.toString()))
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

	private static void buildPccText(final StringBuffer pccTxt, final Iterator<String> listIterator, final String separator, final String label,
		final String lineSep)
	{
		while (listIterator.hasNext())
		{
			final String listItem = listIterator.next();
			final int sepPos = listItem.indexOf(separator);
			pccTxt.append(lineSep).append(listItem.substring(0, sepPos)).append(label).append(listItem.substring(sepPos
					+ 1));
		}
	}

	/**
	 * Build a list of Sub-Classes for the user to choose from. The two lists
	 * passed in will be populated.
	 *
	 * @param choiceList The list of sub-classes to choose from.
	 * @param removeList The list of sub-classes that cannot be chosen
	 * @param useProhibitCost SHould the prohibited cost be used rather
	 *         than the cost of the sub-class.
	 * @param aPC
	 */
	private void buildSubClassChoiceList(final List<List> choiceList, final List<List> removeList, final boolean useProhibitCost, final PlayerCharacter aPC)
	{
		int displayedCost;

		boolean subClassSelected = false;
		for ( SubClass sc : subClassList )
		{
			if (! PrereqHandler.passesAll(sc.getPreReqList(), aPC, this ) )
			{
				continue;
			}

			final List<Object> columnList = new ArrayList<Object>(3);

			if (useProhibitCost)
			{
				displayedCost = sc.getProhibitCost();
			}
			else
			{
				if (!this.getSubClassKey().equals("None"))
				{
					// We already have a subclass requested.
					// If it is legal we will return that.
					subClassSelected = true;
				}
				displayedCost = sc.getCost();
			}

			boolean added = false;
			columnList.add(sc);
			columnList.add(String.valueOf(displayedCost));

			StringBuffer otherColumn = new StringBuffer();
			if (sc.getNumSpellsFromSpecialty() != 0)
			{
				otherColumn.append("SPECIALTY SPELLS:").append(sc.getNumSpellsFromSpecialty());
				added = true;
			}

			if (sc.getSpellBaseStat() != null)
			{
				if (otherColumn.length() > 0)
				{
					otherColumn.append(" ");
				}
				otherColumn.append("SPELL BASE STAT:").append(sc.getSpellBaseStat());
				added = true;
			}

			if (!added)
			{
				otherColumn.append(' ');
			}
			columnList.add(otherColumn.toString());

			if (displayedCost == 0)
			{
				removeList.add(columnList);
			}

			choiceList.add(columnList);
		}
		if (useProhibitCost == false && subClassSelected == true)
		{
			// We want to return just the selected class.
			for (Iterator<List> i = choiceList.iterator(); i.hasNext(); )
			{
				List columns = i.next();
				SubClass sc = (SubClass)columns.get(0);
				if (!sc.getKeyName().equals(this.getSubClassKey()))
				{
					i.remove();
				}
			}
		}
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 * @param bString    Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param iTimes    multiply bonus * iTimes
	 * @param aBonus    The bonuse Object used for calcs
	 * @param anObj
	 * @return partial bonus
	 **/
	private double calcPartialBonus(final String bString, final int iTimes, final BonusObj aBonus, final Object anObj)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);

		if (aBonus.getPCLevel() >= 0)
		{
			// discard first token (Level)
			aTok.nextToken();
		}

		aTok.nextToken(); //Is this intended to be thrown away? Why?

		final String aList = aTok.nextToken();
		final String aVal = aTok.nextToken();

		double iBonus = 0;

		if (aList.equals("ALL"))
		{
			return 0;
		}

		if (anObj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) anObj).getVariableValue(aVal, classKey).doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aVal);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("PCClass calcPartialBonus NumberFormatException in BONUS: " + bString);
			}
		}

		return iBonus * iTimes;
	}

	private boolean canBePrestige(final PlayerCharacter aPC)
	{
		return PrereqHandler.passesAll(getPreReqList(), aPC, this);
	}

	/**
	 * This method adds or deletes feats for a level.
	 * @param aLevel the level to affect
	 * @param addThem whether to add or remove feats
	 * @param aPC
	 */
	private void changeFeatsForLevel(final int aLevel, final boolean addThem, final PlayerCharacter aPC)
	{

		if ((aPC == null) || featList.isEmpty())
		{
			return;
		}


		PCLevelInfo pcLevelInfo = null;
		for ( PCLevelInfo pcl : aPC.getLevelInfo() )
		{
			if (pcl.getClassKeyName().equalsIgnoreCase(getKeyName()) && pcl.getLevel()==aLevel)
			{
				pcLevelInfo = pcl;
				break;
			}
		}

		for ( String feats : featList )
		{
			if (aLevel == Integer.parseInt(getToken(0, feats, ":")))
			{
				final double preFeatCount = aPC.getUsedFeatCount();
				AbilityUtilities.modFeatsFromList(aPC, pcLevelInfo, getToken(1, feats, ":"), addThem, aLevel == 1);

				final double postFeatCount = aPC.getUsedFeatCount();

				// Adjust the feat count by the total number that were given
				aPC.adjustFeats(postFeatCount - preFeatCount);
			}
		}
	}

	private static void checkAdd(final StringBuffer txt, final String comp, final String label, final String value)
	{
		if ((value != null) && !comp.equals(value))
		{
			txt.append('\t').append(label).append(value);
		}
	}

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
		List<List> removeList = new ArrayList<List>();
		buildSubClassChoiceList(choiceList, removeList, false, aPC);

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("School Choice (Specialisation)");
		c.setMessageText("Make a selection.  The cost column indicates the cost of that selection. "
			+ "If this cost is non-zero, you will be asked to also "
			+ "select items from this list to give up to cover that cost.");
		c.setPool(1);
		c.setPoolFlag(false);

		//c.setCostColumnNumber(1);		// Allow 1 choice, regardless of cost...cost will be applied in second phase
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

		List<List<SubClass>> selectedList = c.getSelectedList();

		if (!selectedList.isEmpty())
		{
			setProhibitedString("");
			specialtyList.clear();

			SubClass sc = selectedList.get(0).get(0);
			choiceList = new ArrayList<List>();
			removeList = new ArrayList<List>();
			buildSubClassChoiceList(choiceList, removeList, true, aPC);

			// Remove the specialist school
			for (Iterator<List> iter = choiceList.iterator(); iter.hasNext();)
			{
				final List columns = iter.next();

				if (columns.get(0).equals(sc))
				{
					iter.remove();

					break;
				}
			}

			choiceList.removeAll(removeList);
			setSubClassKey(sc.getKeyName());

			if (sc.getChoice().length() > 0)
			{
				specialtyList.add(sc.getChoice());
			}

			if (sc.getCost() != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1.setMessageText("Make a selection.  You must make as many selections "
					+ "necessary to cover the cost of your previous selections.");
				c1.setPool(sc.getCost());
				c1.setPoolFlag(true);
				c1.setCostColumnNumber(1);
				c1.setNegativeAllowed(true);
				c1.setVisible(true);
				selectedList = c1.getSelectedList();

				for (Iterator<List<SubClass>> i = selectedList.iterator(); i.hasNext();)
				{
					final List columns = i.next();
					sc = (SubClass)columns.get(0);

					if (prohibitedString.length() > 0)
					{
						prohibitedString = prohibitedString.concat(",");
					}

					prohibitedString = prohibitedString.concat(sc.getChoice());
				}
			}
		}
	}

	private void chooseClassSkillList()
	{
		// if no entry or no choices, just return
		if (classSkillString == null)
		{
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(classSkillString, "|", false);
		int amt = 0;

		if (classSkillString.indexOf('|') >= 0)
		{
			amt = Integer.parseInt(aTok.nextToken());
		}

		final List<String> aList = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}

		if (aList.size() == 1)
		{
			classSkillList = aList;

			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose class-skills this class will inherit");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.setVisible(true);

		final List<String> selectedList = c.getSelectedList();
		classSkillList = new ArrayList<String>();

		for ( String sel : selectedList )
		{
			classSkillList.add(sel);
		}
	}

	private void chooseClassSpellList()
	{
		// if no entry or no choices, just return
		if ((classSpellString == null) || (level < 1))
		{
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(classSpellString, "|", false);
		int amt = 0;

		if (classSpellString.indexOf('|') >= 0)
		{
			amt = Integer.parseInt(aTok.nextToken());
		}

		final List<String> aList = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}

		if (aList.size() == amt)
		{
			classSpellList = aList;

			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose list of spells this class will use");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.setVisible(true);

		final List<String> selectedList = c.getSelectedList();
		classSpellList = new ArrayList<String>();

		classSpellList.addAll( selectedList );
	}

	private static boolean contains(final String big, final String little)
	{
		return big.indexOf(little) >= 0;
	}

	private void inheritAttributesFrom(final PCClass otherClass)
	{
		if (otherClass.getBonusSpellBaseStat() != null)
		{
			setBonusSpellBaseStat(otherClass.getBonusSpellBaseStat());
		}

		if (otherClass.getSpellBaseStat() != null)
		{
			setSpellBaseStat(otherClass.getSpellBaseStat());
		}

		if (otherClass.classSpellString != null)
		{
			classSpellString = otherClass.classSpellString;
		}

		addAutoArray(otherClass.getSafeListFor(ListKey.AUTO_ARRAY));

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

		final List<SpecialAbility> specialAbilityList = getSafeListFor(ListKey.SPECIAL_ABILITY);
		specialAbilityList.addAll(otherClass.getSafeListFor(ListKey.SPECIAL_ABILITY));

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
			SR = (ArrayList<LevelProperty>) otherClass.SR.clone();
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
			naturalWeapons = (ArrayList<LevelProperty>) otherClass.naturalWeapons.clone();
		}
	}

	private void modDomainsForLevel(final int aLevel, final boolean adding, final PlayerCharacter aPC)
	{

		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting())
		{
			return;
		}

		int c = 2;

		if (aLevel > 9)
		{
			c = 3;
		}

		if (domainList.isEmpty())
		{
			return;
		}

		for ( String domainKey : domainList )
		{
			final StringTokenizer aTok = new StringTokenizer(domainKey, "|");
			final int bLevel = Integer.parseInt(aTok.nextToken());
			int d = c;

			if (aLevel == bLevel)
			{
				final StringTokenizer bTok = new StringTokenizer(domainKey.substring(c), "[]|", true);
				boolean addNow = true;
				String subKey = "";
				boolean inPreReqs = false;

				while (bTok.hasMoreTokens())
				{
					final String bString = bTok.nextToken();

					if (!inPreReqs && !"[".equals(bString) && !"|".equals(bString))
					{
						subKey = bString;
					}

					d += bString.length();

					if (bTok.hasMoreTokens())
					{
						if ("[".equals(domainKey.substring(d, d + 1)))
						{
							addNow = false;
						}
					}
					else
					{
						addNow = true;
					}

					if ("[".equals(bString))
					{
						inPreReqs = true;
					}
					else if ("]".equals(bString))
					{ // this ends a PRExxx tag so next time through we can add name
						addNow = true;
						inPreReqs = false;
					}

					if (addNow && !adding)
					{
						final int l = aPC.getCharacterDomainIndex(subKey);

						if (l > -1)
						{
							aPC.getCharacterDomainList().remove(l);
						}
					}
					else if (adding && addNow && (subKey.length() > 0))
					{
						if (aPC.getCharacterDomainIndex(subKey) == -1)
						{
							Domain aDomain = Globals.getDomainKeyed(subKey);

							if (aDomain != null)
							{
								aDomain = (Domain) aDomain.clone();

								final CharacterDomain aCD = aPC.getNewCharacterDomain(getKeyName());
								aCD.setDomain(aDomain, aPC);
								aPC.addCharacterDomain(aCD);
								aDomain = aCD.getDomain();
								aDomain.setIsLocked(true,aPC);
							}
						}

						subKey = "";
					}
				}
			}
		}
	}

	private void newClassSpellList()
	{
		if (classSpellList == null)
		{
			classSpellList = new ArrayList<String>();
		}
		else
		{
			classSpellList.clear();
		}
	}


	/**
	 * Rolls hp for the current level according to the rules set in options.
	 * @param aLevel
	 * @param aPC
	 * @param first
	 */
	public void rollHP(final PlayerCharacter aPC, int aLevel, boolean first)
	{
		int roll = 0;

		final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN")
			+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + keyName);
		final int max = getLevelHitDie(aPC, aLevel) + (int) aPC.getTotalBonusTo("HD", "MAX")
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
		setHitPoint(aLevel - 1, new Integer(roll));
		aPC.setCurrentHP(aPC.hitPoints());
	}

	/*
	 * This method updates the base skill modifier based on stat
	 * bonus, race bonus, and template bonus.
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643
	 */
	private int updateBaseSkillMod(final PlayerCharacter aPC, int spMod)
	{
		// skill min is 1, unless class gets 0 skillpoints per level (for second apprentice class)
		final int skillMin = (spMod > 0) ? 1 : 0;

		if (modToSkills)
		{
			spMod += (int) aPC.getStatBonusTo("MODSKILLPOINTS", "NUMBER");

			if (spMod < 1)
			{
				spMod = 1;
			}
		}

		//Race modifiers apply after Intellegence. BUG 577462
		spMod += aPC.getRace().getBonusSkillsPerLevel();
		spMod = Math.max(skillMin, spMod); //Minimum 1, not sure if bonus skills per

		// level can be < 1, better safe than sorry
		if (!aPC.getTemplateList().isEmpty())
		{
			for ( PCTemplate template : aPC.getTemplateList() )
			{
				spMod += template.getBonusSkillsPerLevel();
			}
		}

		return spMod;
	}

	private static class LevelProperty
	{
		private String property = "";
		private int propLevel = 0;
		private PObject object;

		LevelProperty(final int argLevel, final String argProperty)
		{
			propLevel = argLevel;
			property = argProperty;
		}

		LevelProperty(final int argLevel, final PObject argObject)
		{
			propLevel = argLevel;
			object = argObject;
		}

		public final int getLevel()
		{
			return propLevel;
		}

		public final String getProperty()
		{
			return property;
		}

		public final PObject getObject()
		{
			return object;
		}
	}
	/* (non-Javadoc)
	 * @see pcgen.core.PObject#addNaturalWeapon(pcgen.core.Equipment, int)
	 */
	public void addNaturalWeapon(final Equipment weapon, final int aLevel) {
		final LevelProperty lp = new LevelProperty(aLevel, weapon);
		naturalWeapons.add(lp);
	}

	/**
	 * Retrieve the list of spells for the class. Warning this overrides the
	 * PObject method getSpellList and obnly returns the spells up to the
	 * level held in the class. This may not be what you expect.
	 *
	 * @see pcgen.core.PObject#getSpellList()
	 */
	public List<PCSpell> getSpellList()
	{
		return getSpellSupport().getSpellList(getLevel());
	}

	/**
	 * Retrieve the full list of spells for the class. This will return all
	 * spells defined for the class irrespective of the level in the class
	 * currently held.
	 *
	 * @return The full list of spells for the class.
	 */
	public List<PCSpell> getFullSpellList()
	{
		return getSpellSupport().getSpellList(-1);
	}
}
