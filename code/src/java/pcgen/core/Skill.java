/*
 * Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.analysis.SkillCostCalc;
import pcgen.core.analysis.SkillLanguage;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Skill extends PObject
{
	private List<String> rankList = new ArrayList<String>();

	private int outputIndex = 0;

    /** Constructor */
	public Skill()
	{
		// Empty Constructor
	}

    /**
	 * Set this skill's output index, which controls the order
	 * in which the skills appear on a character sheet.
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @param newIndex the new output index for this skill (-1=hidden, 0=not set)
	 */
	public void setOutputIndex(final int newIndex)
	{
		outputIndex = newIndex;
	}

	/**
	 * Return the output index, which controls the order in
	 * which the skills appear on a character sheet
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @return the output index for this skill (-1=hidden, 0=not set)
	 */
	public int getOutputIndex()
	{
		return outputIndex;
	}

	/** returns ranks taken specifically in skill
	 * @return ranks taken in skill
	 */
	public Float getRank()
	{
		double rank = 0.0;

		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = rankList.get(i);
			final int iOffs = bSkill.indexOf(':');

			//
			// Ignore -1 return code (as -1 + 1 = 0 and that's the start of the string)
			//
			rank += Double.parseDouble(bSkill.substring(iOffs + 1));
		}

		return new Float(rank);
	}

    /**
     * Get list of ranks
     * @return rankList
     */
	public List<String> getRankList()
	{
		return rankList;
	}

    /**
     * Get a count of the sub types
     * @return count of sub types
     */
    public int getSubtypeCount()
	{
		final int i = getMyTypeCount();

		if (i == 0)
		{
			return 0;
		}

		return i - 1; // ignore first entry, the keystat
	}

	/**
     * Get an iterator for the sub types 
     * @return iterator for the sub types
	 */
    public Iterator<String> getSubtypeIterator()
	{
		final Iterator<String> it = getTypeList(false).iterator();

		if (it.hasNext())
		{
			if (get(ObjectKey.KEY_STAT) != null)
			{
				it.next(); // skip first entry, the keystat
				/*
				 * TODO This is magic, and makes tremendous assumptions about
				 * the DATA - BAD BAD BAD
				 */
			}
		}

		return it;
	}

	/**
	 * Returns the total ranks of a skill
	 *  rank + bonus ranks (racial, class, etc bonuses).
	 * Note that the total ranks could be higher than the max 
	 * ranks if the ranks come from a familiar's master.
	 *
	 * @param aPC
	 * @return rank + bonus ranks (racial, class, etc. bonuses)
	 */
	public Float getTotalRank(final PlayerCharacter aPC)
	{
		double baseRanks = getRank().doubleValue();
		double ranks = baseRanks
				+ (aPC == null ? 0.0 : getSkillRankBonusTo(aPC));
		if (!Globals.checkRule(RuleConstants.SKILLMAX)
			&& aPC.getClassList().size() > 0)
		{
			double maxRanks =
					aPC.getMaxRank(getKeyName(), aPC.getClassList().get(0))
						.doubleValue();
			maxRanks = Math.max(maxRanks, baseRanks);
			ranks = Math.min(maxRanks, ranks);
		}
		return new Float(ranks);
	}

	/** Set the ranks for the specified class to zero
	 *
	 * @param aClass
	 * @param aPC
	 */
	public void setZeroRanks(final PCClass aClass, final PlayerCharacter aPC)
	{
		if (aClass == null)
		{
			return;
		}

		final String aCName = aClass.getKeyName();
		String bSkill = "";
		int idx;

		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";

		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = rankList.get(idx);

			if (bSkill.startsWith(aCNameString))
			{
				break;
			}
		}

		if (idx >= rankList.size())
		{
			return;
		}

		final double curRankCost = Double.parseDouble(bSkill.substring(aCName.length() + 1));
		final String aResp = modRanks(-curRankCost, aClass, false, aPC);

		if (aResp.length() != 0)
		{
			// error or debug? XXX
			Logging.debugPrint(aResp);
		}
	}

	@Override
	public Skill clone()
	{
		Skill newSkill = null;

		try
		{
			newSkill = (Skill) super.clone();
			newSkill.rankList = new ArrayList<String>(rankList);
			newSkill.outputIndex = outputIndex;
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return newSkill;
	}

    /**
     * Modify the rank 
     * 
     * @param rankMod
     * @param aClass
     * @param aPC
     * @return message
	 */
    public String modRanks(final double rankMod, final PCClass aClass, final PlayerCharacter aPC)
	{
		return modRanks(rankMod, aClass, false, aPC);
	}

    /**
     * Modify the rank 
     * 
     * @param rankMod
     * @param aClass
     * @param ignorePrereqs
     * @param aPC
     * @return message
     */
	public String modRanks(double rankMod, final PCClass aClass, final boolean ignorePrereqs, final PlayerCharacter aPC)
	{
		int i = 0;

		if (!ignorePrereqs)
		{
			if (aClass == null)
			{
				return "You must be at least level one before you can purchase skills.";
			}

			if ((rankMod > 0.0) && !PrereqHandler.passesAll(getPreReqList(), aPC, this))
			{
				return "You do not meet the prerequisites required to take this skill.";
			}

			SkillCost sc = SkillCostCalc.skillCostForPCClass(this, aClass, aPC);

			if (sc.equals(SkillCost.EXCLUSIVE))
			{
				return "You cannot purchase this exclusive skill.";
			}

			if ((rankMod > 0.0) && (aClass.getSkillPool(aPC) < (rankMod * i)))
			{
				return "You do not have enough skill points.";
			}

			final double maxRank = aPC.getMaxRank(keyName, aClass).doubleValue();

			if (!Globals.checkRule(RuleConstants.SKILLMAX) && (rankMod > 0.0))
			{
				final double ttlRank = getTotalRank(aPC).doubleValue();

				if (ttlRank >= maxRank)
				{
					return "Skill rank at maximum (" + maxRank + ") for your level.";
				}

				if ((ttlRank + rankMod) > maxRank)
				{
					return "Raising skill would make it above maximum (" + maxRank + ") for your level.";
				}
			}
		}

		if ((getRank().doubleValue() + rankMod) < 0.0)
		{
			return "Cannot lower rank below 0";
		}

		String classKey = "None";

		if (aClass != null)
		{
			classKey = aClass.getKeyName();
		}

		String bSkill = "";
		int idx;

		//
		// Find the skill and class in question
		//
		final String classKeyString = classKey + ":";

		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = rankList.get(idx);

			if (bSkill.startsWith(classKeyString))
			{
				break;
			}
		}

		if (idx >= rankList.size())
		{
			//
			// If we are trying to lower a rank, and we happen to be using an older
			// character we've loaded, check to see if there is a value for class "None"
			// and allow the user to modify this.
			//
			if (rankMod < 0.0)
			{
				for (idx = 0; idx < rankList.size(); idx++)
				{
					bSkill = rankList.get(idx);

					if (bSkill.startsWith("None:"))
					{
						break;
					}
				}
			}

			if (idx >= rankList.size())
			{
				bSkill = classKey + ":0";
			}
		}

		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));

		if (CoreUtility.doublesEqual(curRank, 0.0) && (rankMod < 0.0))
		{
			return "No more ranks found for class: " + classKey + ". Try a different one.";
		}

		if (idx >= rankList.size())
		{
			rankList.add(idx, bSkill);
		}

		rankMod = modRanks2(rankMod, idx, bSkill, aPC);

		if (!ignorePrereqs)
		{
			if (aClass != null)
			{
				aClass.setSkillPool(aClass.getSkillPool(aPC) - (int) (i * rankMod));
			}

			aPC.setSkillPoints(aPC.getSkillPoints() - (int) (i * rankMod));
		}

		return "";
	}

	/**
     * Get the qualified name
     * @return qualified name
     */
	public String qualifiedName()
	{
		if (getAssociatedCount() == 0)
		{
			return this.getOutputName();
		}

		final StringBuffer buffer = new StringBuffer(getAssociatedCount() * 20);
		buffer.append(this.getOutputName()).append("(");

		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}

			buffer.append(getAssociated(i));
		}

		buffer.append(")");

		return buffer.toString();
	}

	void replaceClassRank(final String oldClass, final String newClass)
	{
		final String oldClassString = oldClass + ":";

		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = rankList.get(i);

			if (bSkill.startsWith(oldClassString))
			{
				rankList.set(i, newClass + bSkill.substring(oldClass.length()));
			}
		}
	}

	private double modRanks2(double g, final int idx, String bSkill, final PlayerCharacter aPC)
	{
		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1)); // current rank for currently selected class
		double newRank = curRank + g;

		if (!aPC.isImporting())
		{
			String choiceString = getChoiceString();
			if ((choiceString.length() > 0) && !CoreUtility.doublesEqual(g, 0)
				&& !CoreUtility.doublesEqual(curRank, (int) newRank))
			{
				final List<String> aArrayList = new ArrayList<String>();
				final double rankAdjustment = 0.0;
				String title = "";
				final StringTokenizer aTok = new StringTokenizer(choiceString, "|");

				if (aTok.hasMoreTokens())
				{
					title = aTok.nextToken();
				}

				if (choiceString.startsWith("Language"))
				{
					bSkill = bSkill.substring(0, iOffs + 1) + newRank;
					rankList.set(idx, bSkill);

					if (!SkillLanguage.chooseLanguageForSkill(aPC, this))
					{
						newRank = curRank;
					}
					else
					{
						final int selectedLanguages = getAssociatedCount();
						final int maxLanguages = getTotalRank(aPC).intValue();

						if (selectedLanguages > maxLanguages)
						{
							newRank = curRank;
						}
					}

					g = newRank - curRank;
				}
				else
				{
					// TODO This code doesn't seem to do anything real.
					// It passes empty lists as both available and selected.
					final ChooserInterface c = ChooserFactory.getChooserInstance();

					if (title.length() != 0)
					{
						c.setTitle(title);
					}

					c.setTotalChoicesAvail((int) (g + curRank + rankAdjustment));
					c.setPoolFlag(false);
					c.setAvailableList(aArrayList);

					final List<String> s = new ArrayList<String>();
					addAssociatedTo(s);
					c.setSelectedList(s);
					c.setVisible(true);

					final int selectedListSize = c.getSelectedList().size();
					newRank = selectedListSize - rankAdjustment;
					g = newRank - getRank().doubleValue(); // change in ranks
				}
			}
		}

		//
		// Modify for the chosen class
		//
		if (CoreUtility.doublesEqual(newRank, 0.0))
		{
			rankList.remove(idx);
		}
		else
		{
			bSkill = bSkill.substring(0, iOffs + 1) + newRank;
			rankList.set(idx, bSkill);
		}

		aPC.calcActiveBonuses();

		return g;
	}

	public String getRanksExplanation()
	{
		final StringBuffer ranksDetails = new StringBuffer();

		for ( int i = 0; i < getRankList().size(); i++ )
		{
			ranksDetails.append(getRankList().get(i));
			if (i + 1 < getRankList().size())
			{
				ranksDetails.append(", ");
			}
		}
	
		return ranksDetails.toString();
	}

	/**
     * Get the bonus to a skill rank
     * @param aPC
     * @return bonus to skill rank
     */
	public double getSkillRankBonusTo(PlayerCharacter aPC)
	{
		double bonus = aPC.getTotalBonusTo("SKILLRANK", getKeyName());
		for (String singleType : getTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILLRANK", "TYPE." + singleType);
		}

		updateAdds(aPC, bonus);

		return bonus;
	}

	private void updateAdds(PlayerCharacter aPC, double bonus)
	{
		// Check for ADDs
		List<LevelAbility> laList = getLevelAbilityList();
		if (laList != null)
		{
			int iCount = 0;
			for ( LevelAbility la : laList )
			{
				iCount += la.getAssociatedCount();
			}

			if (CoreUtility.doublesEqual(getRank().doubleValue() + bonus, 0.0))
			{
				//
				// There was a total (because we've applied the ADD's, but now there isn't.
				// Need to remove the ADDed items
				//
				if (iCount != 0)
				{
					subAddsForLevel(-9, aPC);
				}
			}
			else
			{
				//
				// There wasn't a total (because we haven't applied the ADDs), but now there is
				// Need to apply the ADDed items
				//
				if (iCount == 0)
				{
					addAddsForLevel(-9, aPC, null);
				}
			}
		}
	}


	//
	// Overrides PObject.globalChecks
	//
	protected void globalChecks(final boolean flag, final PlayerCharacter aPC)
	{
		aPC.setArmorProfListStable(false);
		List<String> l = getSafeListFor(ListKey.KITS);
		for (int i = 0; i > l.size(); i++)
		{
			KitUtilities.makeKitSelections(0, l.get(i), i, aPC);
		}
		makeRegionSelection(aPC);

		if (flag)
		{
			makeChoices(aPC);
		}
		activateBonuses(aPC);
	}



	/**
	 * Get the key attribute's description
     * @return description
	 */
	public String getKeyStatFromStats()
	{
		PCStat stat = get(ObjectKey.KEY_STAT);
		if (stat == null)
		{
			if (Globals.getGameModeHasPointPool())
			{
				List<PCStat> statList = getKeyStatList(null);
				StringBuilder sb = new StringBuilder();
				boolean needSlash = false;
				for (PCStat s : statList)
				{
					if (needSlash)
					{
						sb.append('/');
					}
					sb.append(s.getAbb());
				}
				return sb.toString();
			}
			else
			{
				return "";
			}
		}
		else
		{
			return stat.getAbb();
		}
	}

	/**
	 * Get a list of PCStat's that apply a SKILL bonus to this skill.
	 * Generates (optionally, if typeList is non-null) a list of String's types
     * 
	 * @param typeList 
	 * @return List of stats that apply
	 */
	public List<PCStat> getKeyStatList(List<String> typeList)
	{
		List<PCStat> aList = new ArrayList<PCStat>();
		if (Globals.getGameModeHasPointPool())
		{
			for (String aType : this.getTypeList(false))
			{
				List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();
				for (int idx = statList.size() - 1; idx >= 0; --idx)
				{
					final PCStat stat = statList.get(idx);
					//
					// Get a list of all BONUS:SKILL|TYPE.<type>|x for this skill that would come from current stat
					//
					List<BonusObj> bonusList = getBonusListOfType(stat, Bonus.getBonusTypeFromName("SKILL"), "TYPE." + aType);
					if (bonusList.size() > 0)
					{
						for(int iCount = bonusList.size() - 1; iCount >= 0; --iCount)
						{
							aList.add(stat);
						}
						if ((typeList != null) && !typeList.contains(aType))
						{
							typeList.add(aType);
						}
					}
				}
			}
		}
		return aList;
	}

	//
	// Get a list of all BonusObj's from passed stat that apply a bonus of the passed type and name
	//
	private static List<BonusObj> getBonusListOfType(final PCStat aStat, final int iType, final String aName)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : aStat.getBonusList() )
		{
			if (bonus.getTypeOfBonusAsInt() != iType)
			{
				continue;
			}

			if (bonus.getBonusInfoList().size() > 1)
			{
				final StringTokenizer aTok = new StringTokenizer(bonus.getBonusInfo(), ",");

				while (aTok.hasMoreTokens())
				{
					final String aBI = aTok.nextToken();

					if (aBI.equalsIgnoreCase(aName))
					{
						aList.add(bonus);
					}
				}
			}
			else if (bonus.getBonusInfo().equalsIgnoreCase(aName))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	public String getKeyStatAbb()
	{
		PCStat keyStat = get(ObjectKey.KEY_STAT);
		return keyStat == null ? "" : keyStat.getAbb();
	}
}
