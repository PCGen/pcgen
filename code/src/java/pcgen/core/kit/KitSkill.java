/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core.kit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.Skill;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Logging;

/**
 * {@code KitSkill}.
 */
public final class KitSkill extends BaseKit
{
	private Boolean free = null;
	private BigDecimal rank = null;
	private final List<CDOMReference<Skill>> skillList = new ArrayList<>();
	private CDOMSingleRef<PCClass> className = null;
	private Integer choiceCount;

	private final List<CDOMSingleRef<Language>> selection = new ArrayList<>();
	private List<KitSkillAdd> skillsToAdd;

	/**
	 * Used to make purchasing ranks of this skill not come out of the skill
	 * pool.
	 * @param argFree {@code true} to make the skill ranks free.
	 */
	public void setFree(Boolean argFree)
	{
		free = argFree;
	}

	/**
	 * Returns if the skill will be purchased for free.
	 * @return {@code true} if the skill will be free
	 */
	public boolean isFree()
	{
		return free != null && free;
	}

	public void setRank(BigDecimal setRank)
	{
		rank = setRank;
	}

	/**
	 * Get the rank of the skill
	 * @return rank
	 */
	public BigDecimal getRank()
	{
		return rank;
	}

	@Override
	public String toString()
	{
		final StringBuilder info = new StringBuilder(100);
		if (skillList.size() > 1)
		{
			// This is a choice of skills.
			info.append(getSafeCount()).append(" of (");
			info.append(ReferenceUtilities.joinLstFormat(skillList, ", "));
			info.append(")");
		}
		else
		{
			info.append(skillList.get(0).getLSTformat(false));
		}
		info.append(" (").append(rank);

		if (info.toString().endsWith(".0"))
		{
			info.setLength(info.length() - 2);
		}

		if (isFree())
		{
			info.append("/free");
		}

		if (selection != null && !selection.isEmpty())
		{
			info.append("/");
			info.append(StringUtil.join(selection, ", "));
		}

		info.append(')');

		return info.toString();
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		skillsToAdd = new ArrayList<>();
		List<Skill> skillChoices = getSkillChoices(aPC);

		if (skillChoices == null || skillChoices.isEmpty())
		{
			// They didn't make a choice so don't add any ranks.
			return false;
		}
		for (Skill skill : skillChoices)
		{
			BigDecimal ranksLeftToAdd = getRank();
			if (ranksLeftToAdd == null)
			{
				ranksLeftToAdd = BigDecimal.ONE;
			}
			double ranksLeft = ranksLeftToAdd.doubleValue();
			List<PCClass> classList = new ArrayList<>();
			if (className != null)
			{
				String classKey = className.get().getKeyName();
				// Make sure if they specified a class to add from we try that
				// class first.
				PCClass pcClass = aPC.getClassKeyed(classKey);
				if (pcClass != null)
				{
					classList.add(pcClass);
				}
				else
				{
					warnings.add("SKILL: Could not find specified class " + classKey + " in PC to add ranks from.");
				}
			}
			for (PCClass pcClass : aPC.getClassSet())
			{
				if (!classList.contains(pcClass))
				{
					classList.add(pcClass);
				}
			}

			// Try and find a class we can add them from.
			boolean oldImporting = aPC.isImporting();
			aPC.setImporting(true);
			for (PCClass pcClass : classList)
			{
				final KitSkillAdd sta = addRanks(aPC, pcClass, skill, ranksLeft, isFree(), warnings);
				if (sta != null)
				{
					skillsToAdd.add(sta);
					ranksLeft -= sta.getRanks();
					if (ranksLeft <= 0.0)
					{
						break;
					}
				}
			}
			aPC.setImporting(oldImporting);
			if (ranksLeft > 0.0)
			{
				warnings.add(
					"SKILL: Could not add " + ranksLeft + " ranks to " + skill.getKeyName() + ". Not enough points.");
			}
		}
		return true;
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		/** @todo Fix this to return what panes need to be refreshed */
		for (KitSkillAdd ksa : skillsToAdd)
		{
			updatePCSkills(aPC, ksa.getSkill(), (int) ksa.getRanks(), ksa.getCost(), ksa.getLanguages(),
				ksa.getPCClass());
		}
	}

	/**
	 * Needs documentation.
	 *
	 * @param pc update skills for this PC
	 * @param aSkill Skill to update
	 * @param aRank Number of ranks to add
	 * @param aCost Cost of added ranks
	 * @param langList Languages to be selected for a language skill
	 * @param pcClass skills apply to this class
	 *
	 * @return {@code true} for success
	 * TODO What about throwing on failure?
	 */
	private boolean updatePCSkills(final PlayerCharacter pc, final Skill aSkill, final int aRank, final double aCost,
		List<Language> langList, final PCClass pcClass)
	{
		boolean oldImporting = pc.isImporting();
		pc.setImporting(true);
		final String aString = SkillRankControl.modRanks(aRank, pcClass, true, pc, aSkill);
		pc.setImporting(oldImporting);

		if (!aString.isEmpty())
		{
			Logging.errorPrint("SKILL: " + aString);
			return false;
		}

		// Add any supplied languages
		ChoiceManagerList<Language> controller =
				ChooserUtilities.getConfiguredController(aSkill, pc, null, new ArrayList<>());
		for (Language lang : langList)
		{
			if (!controller.conditionallyApply(pc, lang))
			{
				Logging.errorPrint("Failed to apply Language into Skill: " + lang.getKeyName());
			}
		}

		//
		// Fix up the skill pools to reflect what we just spent.
		//
		double ptsToSpend = aCost;
		if (ptsToSpend >= 0.0)
		{
			for (PCLevelInfo info : pc.getLevelInfo())
			{
				if (info.getClassKeyName().equals(pcClass.getKeyName()))
				{
					// We are spending this class' points.
					int remaining = info.getSkillPointsRemaining();
					if (remaining == 0)
					{
						continue;
					}
					int left = remaining - (int) Math.min(remaining, ptsToSpend);
					info.setSkillPointsRemaining(left);
					ptsToSpend -= (remaining - left);
					if (ptsToSpend <= 0)
					{
						break;
					}
				}
			}
		}
		return true;
	}

	@Override
	public String getObjectName()
	{
		return "Skills";
	}

	private List<Skill> getSkillChoices(PlayerCharacter aPC)
	{
		final List<Skill> skillsOfType = new ArrayList<>();

		for (CDOMReference<Skill> ref : skillList)
		{
			skillsOfType.addAll(ref.getContainedObjects());
		}

		if (skillsOfType.isEmpty())
		{
			return null;
		}
		else if (skillsOfType.size() == 1)
		{
			return skillsOfType;
		}

		List<Skill> skillChoices = new ArrayList<>();
		skillChoices = Globals.getChoiceFromList("Select skill", skillsOfType, skillChoices, getSafeCount(), aPC);

		return skillChoices;
	}

	private KitSkillAdd addRanks(PlayerCharacter pc, PCClass pcClass, Skill aSkill, double ranksLeftToAdd,
		boolean isFree, List<String> warnings)
	{
		if (!isFree && pcClass.getSkillPool(pc) == 0)
		{
			return null;
		}

		double curRank = 0.0;
		if (pc.hasSkill(aSkill))
		{
			curRank = pc.getRank(aSkill).doubleValue();
		}
		double ranksToAdd = ranksLeftToAdd;
		if (!Globals.checkRule(RuleConstants.SKILLMAX) && (ranksToAdd > 0.0))
		{
			ranksToAdd = Math.min(pc.getMaxRank(aSkill, pcClass).doubleValue(), curRank + ranksLeftToAdd);
			ranksToAdd -= curRank;
			if (!CoreUtility.doublesEqual(ranksToAdd, ranksLeftToAdd))
			{
				warnings.add("SKILL: Could not add " + (ranksLeftToAdd - ranksToAdd) + " to " + aSkill.getDisplayName()
					+ ". Exceeds MAXRANK of " + pc.getMaxRank(aSkill, pcClass) + ".");
			}
		}
		int ptsToSpend = 0;
		int[] points = new int[pc.getLevelInfoSize()];
		if (!isFree)
		{
			double ranksAdded = 0.0;
			int skillCost = pc.getSkillCostForClass(aSkill, pcClass).getCost();
			ptsToSpend = (int) (ranksToAdd * skillCost);
			for (int i = 0; i < pc.getLevelInfoSize(); i++)
			{
				PCLevelInfo info = pc.getLevelInfo(i);
				if (info.getClassKeyName().equals(pcClass.getKeyName()))
				{
					// We are spending this class' points.
					points[i] = info.getSkillPointsRemaining();
				}
				else
				{
					points[i] = -1;
				}
			}
			for (int i = 0; i < points.length; i++)
			{
				int remaining = points[i];
				if (remaining <= 0)
				{
					continue;
				}
				int left = remaining - Math.min(remaining, ptsToSpend);
				points[i] = left;
				int spent = (remaining - left);
				ptsToSpend -= spent;
				ranksAdded += ((double) spent / (double) skillCost);
				if (ranksAdded == ranksToAdd || ptsToSpend <= 0)
				{
					break;
				}
			}

			ranksToAdd = ranksAdded;
			ptsToSpend = (int) (ranksToAdd * skillCost);
		}

		String ret = SkillRankControl.modRanks(ranksToAdd, pcClass, false, pc, aSkill);
		if (!ret.isEmpty())
		{
			if (isFree && ret.contains("You do not have enough skill points."))
			{
				SkillRankControl.modRanks(ranksToAdd, pcClass, true, pc, aSkill);
			}
			else
			{
				warnings.add(ret);
				return null;
			}
		}
		if (!isFree)
		{
			for (int i = 0; i < pc.getLevelInfoSize(); i++)
			{
				PCLevelInfo info = pc.getLevelInfo(i);
				if (points[i] >= 0)
				{
					info.setSkillPointsRemaining(points[i]);
				}
			}

		}
		List<Language> langList = new ArrayList<>();
		if (ChooseActivation.hasNewChooseToken(aSkill) && !selection.isEmpty())
		{
			ChoiceManagerList<Language> controller =
					ChooserUtilities.getConfiguredController(aSkill, pc, null, new ArrayList<>());
			int limit = (int) ranksToAdd;
			for (CDOMSingleRef<Language> ref : selection)
			{
				Language lang = ref.get();
				if (controller.conditionallyApply(pc, lang))
				{
					langList.add(lang);
					limit--;
				}
				if (limit <= 0)
				{
					break;
				}
			}
		}
		return new KitSkillAdd(aSkill, ranksToAdd, ptsToSpend, langList, pcClass);
	}

	public Boolean getFree()
	{
		return free;
	}

	public void addSkill(CDOMReference<Skill> ref)
	{
		skillList.add(ref);
	}

	public Collection<CDOMReference<Skill>> getSkills()
	{
		return skillList;
	}

	public void setPcclass(CDOMSingleRef<PCClass> ref)
	{
		className = ref;
	}

	public CDOMReference<PCClass> getPcclass()
	{
		return className;
	}

	public void setCount(Integer quan)
	{
		choiceCount = quan;
	}

	public Integer getCount()
	{
		return choiceCount;
	}

	public int getSafeCount()
	{
		return choiceCount == null ? 1 : choiceCount;
	}

	public void addSelection(CDOMSingleRef<Language> ref)
	{
		selection.add(ref);
	}

	public List<CDOMSingleRef<Language>> getSelections()
	{
		return selection;
	}
}
