/*
 * Race.java
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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @author Michael Osterlie
 * @version $Revision$
 */
public final class Race extends PObject
{
	private List<String> monCCSkillList = null;
	private List<String> monCSkillList = null;
	// TODO - Why do we need a hit point map in the race?
	private Map<String, Integer> hitPointMap = new HashMap<String, Integer>();
	private Integer initMod = Integer.valueOf(0);
	private Integer startingAC = Integer.valueOf(10);
	private String hitDieLock = Constants.EMPTY_STRING;
	private String ageString = Constants.EMPTY_STRING;
	private String bonusSkillList = Constants.EMPTY_STRING;

	//private String face = "5 ft. by 5 ft.";
	private Point2D.Double face = new Point2D.Double(5, 0);
	private String favoredClass = Constants.EMPTY_STRING;
	// TODO - ABILITYOBJECT - Remove this.
	private String featList = Constants.EMPTY_STRING;
	private String heightString = Constants.EMPTY_STRING;
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private String mFeatList = Constants.EMPTY_STRING;
	private String monsterClass = null;
	private String size = Constants.EMPTY_STRING;
	private String weightString = Constants.EMPTY_STRING;

	//private String type = "Humanoid";
	private int[] hitDiceAdvancement;
	private boolean unlimitedAdvancement = false;
//	private int BAB = 0;
	private int CR = 0;
	private int bonusSkillsPerLevel = 0;
	private int hands = 2;
	private int hitDice = 0;
	private int hitDiceSize = 0;
	private int initialSkillMultiplier = 4;
	private int langNum = 0;
	private int legs = 2;
	private int monsterClassLevels = 0;
	private int reach = 5;
	private String raceType = Constants.s_NONE;
	private List<String> racialSubTypes = new ArrayList<String>();

	/**
	 * Sets this races advancement to not be limited.
	 * 
	 * @param yesNo <tt>true</tt> if this race allows unlimited
	 * advancement.
	 * 
	 * TODO - Why do we need a special flag for this?
	 */
	public void setAdvancementUnlimited(final boolean yesNo)
	{
		this.unlimitedAdvancement = yesNo;
	}

	/**
	 * Checks if this race's advancement is limited.
	 * 
	 * @return <tt>true</tt> if this race advances unlimitedly.
	 */
	public boolean isAdvancementUnlimited()
	{
		return unlimitedAdvancement;
	}

//	public void setAgeString(final String aString)
//	{
//		ageString = aString;
//	}

//	public void setBAB(final int newBAB)
//	{
//		BAB = newBAB;
//	}

	public void setBonusInitialFeats(final BonusObj bon)
	{
		addBonusList(bon);
	}

	public int getBonusInitialFeats()
	{
		return 0;
	}

	public void setBonusSkillList(final String aString)
	{
		bonusSkillList = aString;
	}

	public void setBonusSkillsPerLevel(final int i)
	{
		bonusSkillsPerLevel = i;
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setCR(final int newCR)
	{
		CR = newCR;
	}

	public int getCR()
	{
		return CR;
	}

	public String getDisplayVision(final PlayerCharacter aPC)
	{
		if (vision == null)
		{
			return "";
		}

		if (aPC == null)
		{
			return "";
		}

		final StringBuffer visionString = new StringBuffer(25);

		for (Vision vis : vision)
		{
			if (visionString.length() > 0)
			{
				visionString.append(';');
			}

			visionString.append(vis.toString(aPC));
		}

		return visionString.toString();
	}

	public void setFace(final double width, final double height)
	{
		face = new Point2D.Double(width, height);
	}

	public Point2D.Double getFace()
	{
		return face;
	}

	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}
	
	public void setFeatList(final String featList)
	{
		this.featList = featList;
	}

	public String getFeatList(final PlayerCharacter aPC)
	{
		return getFeatList(aPC, true);
	}

	public String getFeatList(final PlayerCharacter aPC, final boolean checkPC)
	{
		// This was messing up feats by race for several PC races.
		// so a new tag MFEAT has been added.
		// --- arcady 1/18/2002

		if (checkPC && (aPC!=null) && aPC.isMonsterDefault() && !"".equals(mFeatList))
		{
			return featList + "|" + mFeatList;
		}
		else if (!checkPC || (aPC != null))
		{
			return featList;
		}
		else
		{
			return "";
		}
	}
	
	public void setHands(final int newHands)
	{
		hands = newHands;
	}

	/**
	 * Made public for use on equipping tab -- bug 586332
	 * sage_sam, 22 Nov 2002
	 * @return hands
	 */
	public int getHands()
	{
		return hands;
	}

	public void setHeightString(final String aString)
	{
		heightString = aString;
	}

	public void setHitDice(final int newHitDice)
	{
		if (newHitDice < 0)
		{
			ShowMessageDelegate.showMessageDialog("Invalid number of hit dice in race " + displayName, "PCGen", MessageType.ERROR);

			return;
		}

		hitDice = newHitDice;
	}

	public void setHitDiceAdvancement(final int[] advancement)
	{
		hitDiceAdvancement = advancement;
	}

//	public int[] getHitDiceAdvancement()
//	{
//		return hitDiceAdvancement;
//	}
	public int getHitDiceAdvancement(final int index)
	{
		return hitDiceAdvancement[index];
	}

	public void setHitDiceSize(final int newHitDiceSize)
	{
		hitDiceSize = newHitDiceSize;
	}

	public int getHitDiceSize(final PlayerCharacter aPC)
	{
		return getHitDiceSize(aPC, true);
	}

	public int getHitDiceSize(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC!=null) && aPC.isMonsterDefault()))
		{
			return hitDiceSize;
		}
		return 0;
	}

	public void setHitDieLock(final String hitDieLock)
	{
		this.hitDieLock = hitDieLock;
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
			return Integer.valueOf(0);
		}

		return aHP;
	}

	public void setHitPointMap(final HashMap<String, Integer> newMap)
	{
		hitPointMap.clear();
		hitPointMap.putAll(newMap);
	}

	public int getHitPointMapSize()
	{
		return hitPointMap.size();
	}

	public void setInitMod(final Integer initMod)
	{
		this.initMod = initMod;
	}

	public void setInitialSkillMultiplier(final int initialSkillMultiplier)
	{
		this.initialSkillMultiplier = initialSkillMultiplier;
	}

	public int getInitialSkillMultiplier()
	{
		return initialSkillMultiplier;
	}

	public void setLangNum(final int langNum)
	{
		this.langNum = langNum;
	}

	public void setLegs(final int argLegs)
	{
		legs = argLegs;
	}

	public int getLegs()
	{
		return legs;
	}

	public void setLevelAdjustment(final String newLevelAdjustment)
	{
		levelAdjustment = newLevelAdjustment;
	}

	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int lvlAdjust;

		//if there's a current PC, go ahead and evaluate the formula
		if (aPC != null)
		{
			return aPC.getVariableValue(levelAdjustment, "").intValue();
		}

		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}

		return lvlAdjust;
	}

	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
	}

	public void setMFeatList(final String mFeatList)
	{
		this.mFeatList = mFeatList;
	}

	public String getMFeatList()
	{
		return mFeatList;
	}

	public void setMonCCSkillList(final String aString)
	{
		if (monCCSkillList == null)
		{
			monCCSkillList = new ArrayList<String>();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (".CLEAR".equals(bString))
			{
				monCCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{
				for ( Skill skill : Globals.getSkillList() )
				{
					if (skill.isType(bString.substring(5)))
					{
						monCCSkillList.add(skill.getKeyName());
					}
				}
			}
			else
			{
				monCCSkillList.add(bString);
			}
		}
	}

	public void setMonCSkillList(final String aString)
	{
		if (monCSkillList == null)
		{
			monCSkillList = new ArrayList<String>();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (".CLEAR".equals(bString))
			{
				monCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{
				for ( Skill skill : Globals.getSkillList() )
				{
					if (skill.isType(bString.substring(5)))
					{
						monCSkillList.add(skill.getKeyName());
					}
				}
			}
			else
			{
				monCSkillList.add(bString);
			}
		}
	}

	public void setMonsterClass(final String string)
	{
		monsterClass = string;
	}

	public String getMonsterClass(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC != null) && !aPC.isMonsterDefault()))
		{
			return monsterClass;
		}
		return null;
	}

	public void setMonsterClassLevels(final int num)
	{
		monsterClassLevels = num;
	}

	public int getMonsterClassLevels(final PlayerCharacter aPC)
	{
		return getMonsterClassLevels(aPC, true);
	}

	public int getMonsterClassLevels(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC!= null) && !aPC.isMonsterDefault()))
		{
			return monsterClassLevels;
		}
		return 0;
	}

	public boolean isNonAbility(final int statIdx)
	{
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + statList.get(statIdx).getAbb() + "|10";

		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariableDefinition(i);

			if (varString.endsWith(aStat))
			{
				return true;
			}
		}

		return false;
	}

	public int getNumberOfHitDiceAdvancements()
	{
		return (hitDiceAdvancement != null) ? hitDiceAdvancement.length : 0;
	}

	/**
	 * Retrieve Unarmed Damage according to the Race
	 * @return UDAM damage die (ie 1d3)
	 */
	public String getUdam()
	{
		final int iSize = Globals.sizeInt(getSize());
		final SizeAdjustment defAdj = SettingsHandler.getGame().getDefaultSizeAdjustment();
		final SizeAdjustment sizAdj = SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize);
		if ((defAdj != null) && (sizAdj != null))
		{
			return Globals.adjustDamage("1d3", defAdj.getAbbreviation(), sizAdj.getAbbreviation());
		}
		return "1d3";
	}

	public String getRaceType()
	{
		return raceType;
	}

	public void setRaceType(final String aType)
	{
		raceType = aType;
	}

	public void addRacialSubType(final String aSubType)
	{
		racialSubTypes.add(aSubType);
	}

	public boolean removeRacialSubType(final String aSubType)
	{
		return racialSubTypes.remove(aSubType);
	}

	public List<String> getRacialSubTypes()
	{
		return Collections.unmodifiableList(racialSubTypes);
	}

	/**
	 * Produce a tailored PCC output, used for saving custom races.
	 * @return PCC Text
	 */
	public String getPCCText()
	{
		// 29 July 2003 : sage_sam corrected order
		final StringBuffer txt = new StringBuffer(super.getPCCText());

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVCLASS:").append(favoredClass);
		}

		if ((size != null) && (size.length() > 0))
		{
			txt.append("\tSIZE:").append(size);
		}

		if (reach != 5)
		{
			txt.append("\tREACH:").append(reach);
		}

		if ((getChooseLanguageAutos() != null) && (getChooseLanguageAutos().length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(getChooseLanguageAutos());
		}

		if ((getLanguageBonus() != null) && !getLanguageBonus().isEmpty())
		{
			final StringBuffer buffer = new StringBuffer();

			for ( Language lang : getLanguageBonus() )
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}

				buffer.append(lang.toString());
			}

			txt.append("\tLANGBONUS:").append(buffer.toString());
		}

		if ((getWeaponProfBonus().size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for ( final String profKey : getWeaponProfBonus() )
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append(profKey);
			}

			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}

		if ((mFeatList != null) && (mFeatList.length() > 0))
		{
			txt.append("\tMFEAT:").append(mFeatList);
		}

		if (legs != 2)
		{
			txt.append("\tLEGS:").append(legs);
		}

		if (hands != 2)
		{
			txt.append("\tHANDS:").append(hands);
		}

		if ((getNaturalWeapons() != null) && (getNaturalWeapons().size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for ( Equipment natEquip : getNaturalWeapons() )
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				String eqName = natEquip.getName();
				int index = eqName.indexOf(" (Natural/Primary)");

				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Primary)".length());
				}

				index = eqName.indexOf(" (Natural/Secondary)");

				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Secondary)".length());
				}

				buffer.append(eqName).append(',');
				buffer.append(natEquip.getType(false)).append(',');

				if (!natEquip.isAttacksProgress())
				{
					buffer.append('*');
				}

				buffer.append((int) natEquip.bonusTo(null, "WEAPON", "ATTACKS", true) + 1).append(',');
				buffer.append(natEquip.getDamage(null));
			}

			txt.append("\tNATURALATTACKS:").append(buffer.toString());
		}

		if (initialSkillMultiplier != 4)
		{
			txt.append("\tSKILLMULT:").append(initialSkillMultiplier);
		}

		if (monsterClass != null)
		{
			txt.append("\tMONSTERCLASS:").append(monsterClass);
			txt.append(':').append(monsterClassLevels);
		}

		List<String> templates = getTemplateList();
		if ((templates != null) && (templates.size() > 0))
		{
			for ( String template : templates )
			{
				txt.append("\tTEMPLATE:").append(template);
			}
		}

		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length > 0))
		{
			txt.append("\tHITDICEADVANCEMENT:");

			for (int index = 0; index < hitDiceAdvancement.length; index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}

				if ((hitDiceAdvancement[index] == -1) && isAdvancementUnlimited())
				{
					txt.append('*');
				}
				else
				{
					txt.append(hitDiceAdvancement[index]);
				}
			}
		}

		if (CR != 0)
		{
			txt.append("\tCR:");

			if (CR < 0)
			{
				txt.append("1/").append(-CR);
			}
			else
			{
				txt.append(CR);
			}
		}

		if (startingAC.intValue() != 10)
		{
			txt.append("\tAC:").append(startingAC.toString());
		}

/*
   if (ageString != null && !Constants.s_NONE.equals(ageString) && ageString.length() > 0)
   {
	   txt.append("\tAGE:").append(ageString);
   }
   if (BAB != 0)
   {
	   txt.append("\tBAB:").append(BAB);
   }
 */
		if(CoreUtility.doublesEqual(face.getY(), 0.0))
		{
			txt.append("\tFACE:").append( face.getX() + " ft.");
		}
		else
		{
			txt.append("\tFACE:").append( face.getX() + " ft. by " + face.getY() + " ft.");
		}

		if ((featList != null) && (featList.length() > 0))
		{
			txt.append("\tFEAT:").append(featList);
		}

		if ((hitDice != 0) || (hitDiceSize != 0))
		{
			txt.append("\tHITDICE:").append(hitDice).append(',').append(hitDiceSize);
		}

		if (initMod.intValue() != 0)
		{
			txt.append("\tINIT:").append(initMod.toString());
		}

		if (langNum != 0)
		{
			txt.append("\tLANGNUM:").append(langNum);
		}

		if (!"0".equals(levelAdjustment))
		{
			txt.append("\tLEVELADJUSTMENT:").append(levelAdjustment);
		}

		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}

		if (!Constants.s_NONE.equals(displayName))
		{
			txt.append("\tRACENAME:").append(displayName);
		}

		if ((bonusSkillList != null) && (bonusSkillList.length() > 0))
		{
			txt.append("\tSKILL:").append(bonusSkillList);
		}

		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tXTRASKILLPTSPERLVL:").append(bonusSkillsPerLevel);
		}

//		txt.append(super.getPCCText(false));
		return txt.toString();
	}

	public void setReach(final int newReach)
	{
		reach = newReach;
	}

	public int getReach()
	{
		return reach;
	}

	public void setSize(final String argSize)
	{
		this.size = argSize;
	}

	public String getSize()
	{
		return size;
	}

	public void setStartingAC(final Integer anInt)
	{
		startingAC = anInt;
	}

	public Object clone()
	{
		Race aRace = null;

		try
		{
			aRace = (Race) super.clone();
			aRace.favoredClass = favoredClass;
			aRace.bonusSkillsPerLevel = bonusSkillsPerLevel;
			aRace.size = size;

			aRace.bonusSkillList = bonusSkillList;
			aRace.ageString = ageString;
			aRace.heightString = heightString;
			aRace.weightString = weightString;
			aRace.featList = featList;
			aRace.langNum = langNum;
			aRace.initialSkillMultiplier = initialSkillMultiplier;
			aRace.levelAdjustment = levelAdjustment;
			aRace.CR = CR;
//			aRace.BAB = BAB;
			aRace.hitDice = hitDice;
			aRace.hitDiceSize = hitDiceSize;
			aRace.hitPointMap = new HashMap<String, Integer>(hitPointMap);
			aRace.hitDiceAdvancement = hitDiceAdvancement;
			aRace.hands = hands;
			aRace.reach = reach;
			aRace.face = face;
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return aRace;
	}

	/**
	 * returns true if the race has HD advancement
	 * @return true if the race has HD advancement
	 */
	public boolean hasAdvancement()
	{
		return hitDiceAdvancement != null;
	}

	/**
	 * Overridden to only consider the race's name.
	 * @return hash code
	 */
	public int hashCode()
	{
		return getKeyName().hashCode();
	}

	public int hitDice(final PlayerCharacter aPC)
	{
		return hitDice(aPC, true);
	}

	public int hitDice(final PlayerCharacter aPC, final boolean checkPC)
	{
		if (!checkPC || ((aPC != null) && aPC.isMonsterDefault()))
		{
			return hitDice;
		}
		return 0;
	}

	/**
	 * TODO: Note that this code does *not* work like that in PCClass
	 * Does it need to be?
	 * @param aPC
	 **/
	public void rollHP(final PlayerCharacter aPC)
	{
		if (!aPC.isImporting())
		{
			final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN");
			final int max = hitDiceSize + (int) aPC.getTotalBonusTo("HD", "MAX");

			for (int x = 0; x < hitDice; ++x)
			{
				setHitPoint(x, Integer.valueOf(Globals.rollHP(min, max, getKeyName(), x + 1)));
			}
		}

		aPC.setCurrentHP(aPC.hitPoints());
	}

	protected int getSR(final PlayerCharacter aPC)
	{
		int intSR;

		//if there's a current PC, go ahead and evaluate the formula
		if ((getSRFormula() != null) && (aPC != null))
		{
			return aPC.getVariableValue(getSRFormula(), "").intValue();
		}

		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			intSR = Integer.parseInt(getSRFormula());
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			intSR = 0;
		}

		return intSR;
	}

	protected void doGlobalTypeUpdate(final String aString)
	{
		Globals.getRaceTypes().add(aString);
	}

	int getBAB(final PlayerCharacter aPC)
	{
//		if ((aPC != null) && aPC.isMonsterDefault())
//		{
//			// "BAB" not being used on races any more; instead using a BONUS tag.
//			// This will fix a bug this causes for default monsters.  Bug #647163
//			// sage_sam 03 Dec 2002
//			if (BAB == 0)
//			{
//				BAB = (int) bonusTo("COMBAT", "BAB", aPC, aPC);
//			}
//
//			return BAB;
//		}
		return 0;
	}

	String getHitDieLock()
	{
		return hitDieLock;
	}

	int getLangNum()
	{
		return langNum;
	}

	String getMonsterClass(final PlayerCharacter aPC)
	{
		return getMonsterClass(aPC, true);
	}

	int bonusForSkill(final String skillName)
	{
		if (getBonusSkillList().length() == 0)
		{
			return 0;
		}

		final StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=");

		while (aTok.hasMoreTokens())
		{
			final String skillList = aTok.nextToken();
			final int anInt = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(skillList, ",", false);

			while (bTok.hasMoreTokens())
			{
				final String aSkill = bTok.nextToken();

				if (aSkill.equals(skillName))
				{
					return anInt;
				}
			}
		}

		return 0;
	}

	int calcHitPoints(final int iConMod)
	{
		int total = 0;

		for (int i = 0; i <= hitDice; i++)
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

	boolean canBeAlignment(final String aString)
	{
		if (getPreReqCount() != 0)
		{
			for (int e = 0; e < getPreReqCount(); e++)
			{
				final Prerequisite prereq = getPreReq(e);

				if ("ALIGN".equalsIgnoreCase( prereq.getKind() ))
				{
					String alignStr = aString;
					final String[] aligns = SettingsHandler.getGame().getAlignmentListStrings(false);
					try
					{
						final int align = Integer.parseInt(alignStr);
						alignStr = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}
					String desiredAlignment = prereq.getKey();
					try
					{
						final int align = Integer.parseInt(desiredAlignment);
						desiredAlignment = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}

					return desiredAlignment.equalsIgnoreCase(alignStr);
				}
			}
		}

		return true;
	}

	boolean hasMonsterCCSkill(final String aName)
	{
		if ((monCCSkillList == null) || monCCSkillList.isEmpty())
		{
			return false;
		}

		if (monCCSkillList.contains(aName))
		{
			return true;
		}

		for ( String mSkill : monCCSkillList )
		{
			if (mSkill.lastIndexOf('%') >= 0)
			{
				mSkill = mSkill.substring(0, mSkill.length() - 1);

				if (aName.startsWith(mSkill))
				{
					return true;
				}
			}
		}

		return false;
	}

	boolean hasMonsterCSkill(final String aName)
	{
		if ((monCSkillList == null) || monCSkillList.isEmpty())
		{
			return false;
		}

		if (monCSkillList.contains(aName))
		{
			return true;
		}

		if (monCSkillList.contains("LIST"))
		{
			for (int e = 0; e < getAssociatedCount(); ++e)
			{
				final String aString = getAssociated(e);

				if (aName.startsWith(aString) || aString.startsWith(aName))
				{
					return true;
				}
			}
		}

		for ( String mSkill : monCSkillList )
		{
			if (mSkill.lastIndexOf('%') >= 0)
			{
				mSkill = mSkill.substring(0, mSkill.length() - 1);

				if (aName.startsWith(mSkill))
				{
					return true;
				}
			}
		}

		return false;
	}

	int maxHitDiceAdvancement()
	{
		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length >= 1))
		{
			return hitDiceAdvancement[hitDiceAdvancement.length - 1];
		}
		return 0;
	}

	int sizesAdvanced(final int HD)
	{
		if (hitDiceAdvancement != null)
		{
			for (int x = 0; x < hitDiceAdvancement.length; x++)
			{
				if ((HD <= hitDiceAdvancement[x]) || (hitDiceAdvancement[x] == -1))
				{
					return x;
				}
			}
		}

		return 0;
	}

	private String getBonusSkillList()
	{
		return bonusSkillList;
	}
}
