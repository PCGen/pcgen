/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.analysis;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.Skill;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Logging;

public class SkillRankControl
{

	/**
	 * returns ranks taken specifically in skill
	 * 
	 * @return ranks taken in skill
	 */
	public static Float getRank(PlayerCharacter pc, Skill sk)
	{
		double rank = 0.0;

		List<NamedValue> rankList = pc.getAssocList(sk, AssociationListKey.SKILL_RANK);
		if (rankList != null)
		{
			for (NamedValue sd : rankList)
			{
				rank += sd.getWeight();
			}
		}

		return new Float(rank);
	}

	/**
	 * Returns the total ranks of a skill rank + bonus ranks (racial, class, etc
	 * bonuses). Note that the total ranks could be higher than the max ranks if
	 * the ranks come from a familiar's master.
	 * 
	 * @param aPC
	 * @return rank + bonus ranks (racial, class, etc. bonuses)
	 */
	public static Float getTotalRank(PlayerCharacter pc, Skill sk)
	{
		double baseRanks = getRank(pc, sk).doubleValue();
		double ranks = baseRanks
				+ (pc == null ? 0.0 : SkillRankControl.getSkillRankBonusTo(pc, sk));
		if (!Globals.checkRule(RuleConstants.SKILLMAX) && pc.hasClass())
		{
			/*
			 * Note: The class grabbed doesn't matter - it is only used for calculating cross-class skill rank cost.
			 * All classes of a multi-class character are scanned to determine if the skill is a class skill.
			 */
			double maxRanks = pc.getMaxRank(sk,
					pc.getClassList().get(0)).doubleValue();
			maxRanks = Math.max(maxRanks, baseRanks);
			ranks = Math.min(maxRanks, ranks);
		}
		return new Float(ranks);
	}

	/**
	 * Set the ranks for the specified class to zero
	 * 
	 * @param aClass
	 * @param aPC
	 */
	public static void setZeroRanks(PCClass aClass, PlayerCharacter aPC,
			Skill sk)
	{
		if (aClass == null)
		{
			return;
		}

		String aCName = aClass.getKeyName();
		List<NamedValue> rankList = aPC.getAssocList(sk, AssociationListKey.SKILL_RANK);
		if (rankList == null)
		{
			return;
		}
		for (NamedValue nv : rankList)
		{
			if (nv.name.equals(aCName))
			{
				aPC.removeAssoc(sk, AssociationListKey.SKILL_RANK, nv);
				double curRankCost = nv.getWeight();
				String aResp = modRanks(-curRankCost, aClass, false, aPC, sk);

				if (aResp.length() != 0)
				{
					// error or debug? XXX
					Logging.debugPrint(aResp);
				}
				break;
			}
		}
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
	public static String modRanks(double rankMod, PCClass aClass,
			boolean ignorePrereqs, PlayerCharacter aPC, Skill sk)
	{
		int i = 0;

		if (!ignorePrereqs)
		{
			if (aClass == null)
			{
				return "You must be at least level one before you can purchase skills.";
			}

			if ((rankMod > 0.0) && !sk.qualifies(aPC, sk))
			{
				return "You do not meet the prerequisites required to take this skill.";
			}

			SkillCost sc = aPC.getSkillCostForClass(sk, aClass);

			i = sc.getCost();
			if (i == 0)
			{
				return "You cannot purchase this exclusive skill.";
			}

			if ((rankMod > 0.0) && (aClass.getSkillPool(aPC) < (rankMod * i)))
			{
				return "You do not have enough skill points.";
			}

			double maxRank = aPC.getMaxRank(sk, aClass)
					.doubleValue();

			if (!Globals.checkRule(RuleConstants.SKILLMAX) && (rankMod > 0.0))
			{
				double ttlRank = getTotalRank(aPC, sk).doubleValue();

				if (ttlRank >= maxRank)
				{
					return "Skill rank at maximum (" + maxRank
							+ ") for your level.";
				}

				if ((ttlRank + rankMod) > maxRank)
				{
					return "Raising skill would make it above maximum ("
							+ maxRank + ") for your level.";
				}
			}
		}

		if ((getRank(aPC, sk).doubleValue() + rankMod) < 0.0)
		{
			return "Cannot lower rank below 0";
		}

		String classKey = "None";

		if (aClass != null)
		{
			classKey = aClass.getKeyName();
		}

		double currentRank = 0.0;
		double noneRank = 0.0;
		NamedValue active = null;
		List<NamedValue> rankList = aPC.getAssocList(sk, AssociationListKey.SKILL_RANK);
		if (rankList != null)
		{
			for (NamedValue nv : rankList)
			{
				if (nv.name.equals(classKey))
				{
					currentRank = nv.getWeight();
					active = nv;
					break;
				}
				else if (nv.name.equals("None"))
				{
					noneRank = nv.getWeight();
				}
			}
		}

		if (currentRank <= 0)
		{
			currentRank = noneRank;
		}

		if (CoreUtility.doublesEqual(currentRank, 0.0) && (rankMod < 0.0))
		{
			return "No more ranks found for class: " + classKey
					+ ". Try a different one.";
		}

		rankMod = modRanks2(rankMod, currentRank, active, classKey, aPC, sk);

		if (!ignorePrereqs)
		{
			if (aClass != null)
			{
				aPC.setAssoc(aClass, AssociationKey.SKILL_POOL, aClass
						.getSkillPool(aPC)
						- (int) (i * rankMod));
			}

			aPC.setDirty(true);
		}

		return "";
	}

	public static void replaceClassRank(PlayerCharacter pc, Skill sk,
			String oldClass, String newClass)
	{
		List<NamedValue> rankList = pc.getAssocList(sk, AssociationListKey.SKILL_RANK);
		if (rankList != null)
		{
			for (NamedValue nv : rankList)
			{
				if (nv.name.equals(oldClass))
				{
					pc.removeAssoc(sk, AssociationListKey.SKILL_RANK, nv);
					pc.addAssoc(sk, AssociationListKey.SKILL_RANK, new NamedValue(
							newClass, nv.getWeight()));
					break;
				}
			}
		}
	}

	private static double modRanks2(double g, double curRank,
			NamedValue active, String classKey, PlayerCharacter aPC, Skill sk)
	{
		double newRank = curRank + g;

		if (!aPC.isImporting())
		{
			if (ChooseActivation.hasChooseToken(sk))
			{
				if (!CoreUtility.doublesEqual(g, 0)
						&& !CoreUtility.doublesEqual(curRank, (int) newRank))
				{
					if (active != null)
					{
						active.addWeight(g);
					}
					ChooserUtilities.modChoices(sk, new ArrayList<Language>(),
							new ArrayList<Language>(), true, aPC, true, null);
					aPC.setDirty(true);
					int selectedLanguages = aPC
							.getSelectCorrectedAssociationCount(sk);
					int maxLanguages = getTotalRank(aPC, sk).intValue();
					if (selectedLanguages > maxLanguages)
					{
						newRank = curRank;
					}
					if (active != null)
					{
						active.removeWeight(g);
					}
					g = newRank - curRank;
				}
			}
		}

		//
		// Modify for the chosen class
		//
		if (CoreUtility.doublesEqual(newRank, 0.0))
		{
			aPC.removeAssoc(sk, AssociationListKey.SKILL_RANK, active);
		}
		else if (active != null)
		{
			active.addWeight(g);
		}
		else
		{
			aPC.addAssoc(sk, AssociationListKey.SKILL_RANK, new NamedValue(
					classKey, g));
		}

		aPC.calcActiveBonuses();

		return g;
	}

	public static String getRanksExplanation(PlayerCharacter pc, Skill sk)
	{
		List<NamedValue> assocList = pc.getAssocList(sk, AssociationListKey.SKILL_RANK);
		String result = StringUtil.join(assocList,
				", ");
		double bonus = getSkillRankBonusTo(pc, sk);
		if (bonus != 0d)
		{
			if (result.length() > 0)
			{
				result += "; ";
			}
			
			result += "Skillrank bonus " + NumberFormat.getNumberInstance().format(bonus);
		}
		return result;
	}

	/**
	 * Get the bonus to a skill rank
	 * 
	 * @param aPC
	 * @return bonus to skill rank
	 */
	public static double getSkillRankBonusTo(PlayerCharacter aPC, Skill sk)
	{
		double bonus = aPC.getTotalBonusTo("SKILLRANK", sk.getKeyName());
		for (Type singleType : sk.getTrueTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILLRANK", "TYPE." + singleType);
		}
	
		updateAdds(aPC, sk, bonus);
	
		return bonus;
	}

	private static void updateAdds(PlayerCharacter aPC, Skill sk, double bonus)
	{
		// Check for ADDs
		List<PersistentTransitionChoice<?>> adds = sk.getListFor(ListKey.ADD);
		if (adds != null)
		{
			int iCount = 0;
			for (PersistentTransitionChoice<?> ptc : adds)
			{
				iCount += aPC.getAssocCount(ptc, AssociationListKey.ADD);
			}
	
			if (CoreUtility.doublesEqual(getRank(aPC, sk).doubleValue() + bonus,
					0.0))
			{
				//
				// There was a total (because we've applied the ADD's, but now
				// there isn't.
				// Need to remove the ADDed items
				//
				if (iCount != 0)
				{
					CDOMObjectUtilities.removeAdds(sk, aPC);
					CDOMObjectUtilities.restoreRemovals(sk, aPC);
				}
			}
			else
			{
				//
				// There wasn't a total (because we haven't applied the ADDs),
				// but now there is
				// Need to apply the ADDed items
				//
				if (iCount == 0)
				{
					CDOMObjectUtilities.addAdds(sk, aPC);
					CDOMObjectUtilities.checkRemovals(sk, aPC);
				}
			}
		}
	}

}
