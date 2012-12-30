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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.PersistentTransitionChoice;
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
	 * Returns the total ranks of a skill rank + bonus ranks (racial, class, etc
	 * bonuses). Note that the total ranks could be higher than the max ranks if
	 * the ranks come from a familiar's master.
	 * 
	 * @param pc
	 * @return rank + bonus ranks (racial, class, etc. bonuses)
	 */
	public static Float getTotalRank(PlayerCharacter pc, Skill sk)
	{
		double baseRanks = pc.getRank(sk).doubleValue();
		double ranks = baseRanks + SkillRankControl.getSkillRankBonusTo(pc, sk);
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

		Double rank = aPC.getSkillRankForClass(sk, aClass);
		if (rank != null)
		{
			aPC.removeSkillRankValue(sk, aClass);
			String aResp = modRanks(-rank, aClass, false, aPC, sk);

			if (aResp.length() != 0)
			{
				// error or debug? XXX
				Logging.debugPrint(aResp);
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

		if ((aPC.getRank(sk).doubleValue() + rankMod) < 0.0)
		{
			return "Cannot lower rank below 0";
		}

		String classKey = "None";

		if (aClass != null)
		{
			classKey = aClass.getKeyName();
		}

		Double rank = aPC.getSkillRankForClass(sk, aClass);

		double currentRank = (rank == null) ?  0.0 : rank;

		if (CoreUtility.doublesEqual(currentRank, 0.0) && (rankMod < 0.0))
		{
			return "No more ranks found for class: " + classKey
					+ ". Try a different one.";
		}

		rankMod = modRanks2(rankMod, currentRank, aClass, aPC, sk);

		if (!ignorePrereqs)
		{
			if (aClass != null)
			{
				aPC.setSkillPool(aClass, aClass.getSkillPool(aPC)
					- (int) (i * rankMod));
			}

			aPC.setDirty(true);
		}

		return "";
	}

	public static void replaceClassRank(PlayerCharacter pc, Skill sk,
			PCClass oldClass, PCClass newClass)
	{
		Double rank = pc.getSkillRankForClass(sk, oldClass);
		if (rank != null)
		{
			pc.removeSkillRankValue(sk, oldClass);
			pc.setSkillRankValue(sk, newClass, rank);
		}
	}

	private static double modRanks2(double rankChange, double curRank,
			PCClass pcc, PlayerCharacter aPC, Skill sk)
	{
		double newRank = curRank + rankChange;

		//
		// Modify for the chosen class
		//
		if (CoreUtility.doublesEqual(newRank, 0.0))
		{
			aPC.removeSkillRankValue(sk, pcc);
		}
		else
		{
			aPC.setSkillRankValue(sk, pcc, newRank);
		}

		if (!aPC.isImporting())
		{
			if (ChooseActivation.hasChooseToken(sk))
			{
				if (!CoreUtility.doublesEqual(rankChange, 0)
						&& !CoreUtility.doublesEqual(curRank, (int) newRank))
				{
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
				}
			}
		}

		aPC.calcActiveBonuses();

		return rankChange;
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
	
			if (CoreUtility.doublesEqual(aPC.getRank(sk).doubleValue() + bonus,
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
