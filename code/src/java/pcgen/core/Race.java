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
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
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
	private String favoredClass = Constants.EMPTY_STRING;
	// TODO - ABILITYOBJECT - Remove this.
	private String featList = Constants.EMPTY_STRING;
	private String monsterClass = null;
	private int monsterClassLevels = 0;
	
	/*
	 * TODO These four items are Deprecated, Default Monster Mode
	 */
	private String mFeatList = Constants.EMPTY_STRING;
	private int hitDice = 0;
	private int hitDiceSize = 0;
	private Map<String, Integer> hitPointMap = new HashMap<String, Integer>();


	/**
	 * Checks if this race's advancement is limited.
	 * 
	 * @return <tt>true</tt> if this race advances unlimitedly.
	 */
	public boolean isAdvancementUnlimited()
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		return hda == null
				|| Integer.MAX_VALUE == hda.get(hda.size() - 1).intValue();
	}

	public void setBonusInitialFeats(final BonusObj bon)
	{
		addBonusList(bon);
	}

	public int getBonusInitialFeats()
	{
		return 0;
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

	public Point2D.Double getFace()
	{
		BigDecimal width = get(ObjectKey.FACE_WIDTH);
		BigDecimal height = get(ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		return new Point2D.Double(width.doubleValue(), height.doubleValue());
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

		if (checkPC && (aPC != null) && aPC.isMonsterDefault()
			&& !"".equals(mFeatList))
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

	public void setHitDice(final int newHitDice)
	{
		if (newHitDice < 0)
		{
			ShowMessageDelegate.showMessageDialog(
				"Invalid number of hit dice in race " + displayName, "PCGen",
				MessageType.ERROR);

			return;
		}

		hitDice = newHitDice;
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
		if (!checkPC || ((aPC != null) && aPC.isMonsterDefault()))
		{
			return hitDiceSize;
		}
		return 0;
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

	public void setMFeatList(final String mFeatList)
	{
		this.mFeatList = mFeatList;
	}

	public String getMFeatList()
	{
		return mFeatList;
	}

	public void setMonsterClass(final String string)
	{
		monsterClass = string;
	}

	public String getMonsterClass(final PlayerCharacter aPC,
		final boolean checkPC)
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

	public int getMonsterClassLevels(final PlayerCharacter aPC,
		final boolean checkPC)
	{
		if (!checkPC || ((aPC != null) && !aPC.isMonsterDefault()))
		{
			return monsterClassLevels;
		}
		return 0;
	}

	public boolean isNonAbility(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		// An unlock will always override a lock, so check it first
		if (isUnlocked(statIdx))
		{
			return false;
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

	/**
	 * Takes an integer input which it uses to access Games mode's 
	 * "statlist" array. Test if that stat has been unlocked via a 
	 * DEFINE|UNLOCK 
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been unlocked
	 */
	public boolean isUnlocked(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		String aStat = "|UNLOCK." + statList.get(statIdx).getAbb() + "|";
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

	/**
	 * Retrieve Unarmed Damage according to the Race
	 * @return UDAM damage die (ie 1d3)
	 */
	public String getUdam()
	{
		/*
		 * TODO This has pc == null, which could be a problem
		 */
		final int iSize = getSafe(FormulaKey.SIZE).resolve(null, "").intValue();
		final SizeAdjustment defAdj =
				SettingsHandler.getGame().getDefaultSizeAdjustment();
		final SizeAdjustment sizAdj =
				SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize);
		if ((defAdj != null) && (sizAdj != null))
		{
			return Globals.adjustDamage("1d3", defAdj.getAbbreviation(), sizAdj
				.getAbbreviation());
		}
		return "1d3";
	}

	/**
	 * Produce a tailored PCC output, used for saving custom races.
	 * @return PCC Text
	 */
	@Override
	public String getPCCText()
	{
		// 29 July 2003 : sage_sam corrected order
		final StringBuffer txt = new StringBuffer(super.getPCCText());

		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVCLASS:").append(favoredClass);
		}

		if ((getChooseLanguageAutos() != null)
			&& (getChooseLanguageAutos().length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(getChooseLanguageAutos());
		}

		if ((getNaturalWeapons() != null) && (getNaturalWeapons().size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for (Equipment natEquip : getNaturalWeapons())
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				String eqName = natEquip.getName();
				int index = eqName.indexOf(" (Natural/Primary)");

				if (index >= 0)
				{
					eqName =
							eqName.substring(0, index)
								+ eqName.substring(index
									+ " (Natural/Primary)".length());
				}

				index = eqName.indexOf(" (Natural/Secondary)");

				if (index >= 0)
				{
					eqName =
							eqName.substring(0, index)
								+ eqName.substring(index
									+ " (Natural/Secondary)".length());
				}

				buffer.append(eqName).append(',');
				buffer.append(natEquip.getType(false)).append(',');

				if (!natEquip.isAttacksProgress())
				{
					buffer.append('*');
				}

				buffer
					.append(
						(int) natEquip.bonusTo(null, "WEAPON", "ATTACKS", true) + 1)
					.append(',');
				buffer.append(natEquip.getDamage(null));
			}

			txt.append("\tNATURALATTACKS:").append(buffer.toString());
		}

		if (monsterClass != null && !"(None)".equals(monsterClass))
		{
			txt.append("\tMONSTERCLASS:").append(monsterClass);
			txt.append(':').append(monsterClassLevels);
		}

		List<String> templates = getTemplateList();
		if ((templates != null) && (templates.size() > 0))
		{
			for (String template : templates)
			{
				txt.append("\tTEMPLATE:").append(template);
			}
		}

		if ((featList != null) && (featList.length() > 0))
		{
			txt.append("\tFEAT:").append(featList);
		}

		if (!Constants.s_NONE.equals(displayName))
		{
			txt.append("\tOUTPUTNAME:").append(displayName);
		}

		return txt.toString();
	}

	public int getReach()
	{
		Integer reach = get(IntegerKey.REACH);
		return reach == null ? 5 : reach;
	}

	@Override
	public Race clone()
	{
		Race aRace = null;

		try
		{
			aRace = (Race) super.clone();
			aRace.favoredClass = favoredClass;

			aRace.featList = featList;
			aRace.hitDice = hitDice;
			aRace.hitDiceSize = hitDiceSize;
			aRace.hitPointMap = new HashMap<String, Integer>(hitPointMap);
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
				Constants.s_APPNAME, MessageType.ERROR);
		}

		return aRace;
	}

	/**
	 * Overridden to only consider the race's name.
	 * @return hash code
	 */
	@Override
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
			final int max =
					hitDiceSize + (int) aPC.getTotalBonusTo("HD", "MAX");

			for (int x = 0; x < hitDice; ++x)
			{
				setHitPoint(x, Integer.valueOf(Globals.rollHP(min, max,
					getKeyName(), x + 1)));
			}
		}

		aPC.setCurrentHP(aPC.hitPoints());
	}

	@Override
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

	@Override
	protected void doGlobalTypeUpdate(final String aString)
	{
		Globals.getRaceTypes().add(aString);
	}

	String getMonsterClass(final PlayerCharacter aPC)
	{
		return getMonsterClass(aPC, true);
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
		if (hasPreReqs())
		{
			for (Prerequisite prereq : getPreReqList())
			{
				if ("ALIGN".equalsIgnoreCase(prereq.getKind()))
				{
					String alignStr = aString;
					final String[] aligns =
							SettingsHandler.getGame().getAlignmentListStrings(
								false);
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

	boolean hasMonsterCCSkill(Skill s)
	{
		CDOMReference<ClassSkillList> mList = PCClass.MONSTER_SKILL_LIST;
		Collection<CDOMReference<Skill>> mods = getListMods(mList);
		if (mods == null)
		{
			return false;
		}
		for (CDOMReference<Skill> ref : mods)
		{
			for (AssociatedPrereqObject apo : getListAssociations(mList, ref))
			{
				if (SkillCost.CROSS_CLASS.equals(apo
						.getAssociation(AssociationKey.SKILL_COST)))
				{
					if (ref.contains(s))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean hasMonsterCSkill(Skill s)
	{
		CDOMReference<ClassSkillList> mList = PCClass.MONSTER_SKILL_LIST;
		Collection<CDOMReference<Skill>> mods = getListMods(mList);
		if (mods == null)
		{
			return false;
		}
		for (CDOMReference<Skill> ref : mods)
		{
			for (AssociatedPrereqObject apo : getListAssociations(mList, ref))
			{
				if (SkillCost.CLASS.equals(apo
						.getAssociation(AssociationKey.SKILL_COST)))
				{
					if (ref.contains(s))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	int maxHitDiceAdvancement()
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		return hda == null ? 0 : hda.get(hda.size() - 1);
	}

	int sizesAdvanced(final int HD)
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		if (hda == null)
		{
			return 0;
		}
		else
		{
			int steps = 0;
			for (Integer hitDie : hda)
			{
				if (HD <= hitDie)
				{
					break;
				}
				steps++;
			}
			return steps;
		}
	}
}
