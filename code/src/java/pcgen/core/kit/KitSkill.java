/*
 * KitSkill.java
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
 *
 * Created on September 23, 2002, 10:28 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import pcgen.core.analysis.SkillLanguage;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.Logging;

/**
 * <code>KitSkill</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitSkill extends BaseKit
{
	private Boolean free = null;
	private BigDecimal rank = null;
	private List<CDOMReference<Skill>> skillList =
			new ArrayList<CDOMReference<Skill>>();
	private CDOMSingleRef<PCClass> className = null;
	private Integer choiceCount;

	private List<CDOMSingleRef<Language>> selection =
			new ArrayList<CDOMSingleRef<Language>>();
	private transient List<KitSkillAdd> skillsToAdd;

	/**
	 * Used to make purchasing ranks of this skill not come out of the skill
	 * pool.
	 * @param argFree <code>true</code> to make the skill ranks free.
	 */
	public void setFree(Boolean argFree)
	{
		free = argFree;
	}

	/**
	 * Returns if the skill will be purchased for free.
	 * @return <code>true</code> if the skill will be free
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
		final StringBuffer info = new StringBuffer(100);
		if (skillList.size() > 1)
		{
			// This is a choice of skills.
			info.append(getSafeCount() + " of (");
			info.append(ReferenceUtilities.joinLstFormat(skillList, ", "));
			info.append(")");
		}
		else
		{
			info.append(skillList.get(0).getLSTformat());
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
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		skillsToAdd = new ArrayList<KitSkillAdd>();
		List<Skill> skillChoices = getSkillChoices();

		if (skillChoices == null || skillChoices.size() == 0)
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
			List<PCClass> classList = new ArrayList<PCClass>();
			if (className != null)
			{
				String classKey = className.resolvesTo().getKeyName();
				// Make sure if they specified a class to add from we try that
				// class first.
				PCClass pcClass = aPC.getClassKeyed(classKey);
				if (pcClass != null)
				{
					classList.add(pcClass);
				}
				else
				{
					warnings.add("SKILL: Could not find specified class "
						+ classKey + " in PC to add ranks from.");
				}
			}
			for (PCClass pcClass : aPC.getClassList())
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
				final KitSkillAdd sta =
						addRanks(aPC, pcClass, skill, ranksLeft, isFree(),
							warnings);
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
				warnings.add("SKILL: Could not add " + ranksLeft
					+ " ranks to " + skill.getKeyName()
					+ ". Not enough points.");
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
			updatePCSkills(aPC, ksa.getSkill(), (int) ksa.getRanks(), ksa
				.getCost(), ksa.getLanguages(), ksa.getPCClass());
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
	 * @return <code>true</code> for success
	 * TODO What about throwing on failure?
	 */
	private boolean updatePCSkills(final PlayerCharacter pc,
		final Skill aSkill, final int aRank, final double aCost,
		List<Language> langList, final PCClass pcClass)
	{
		final Skill skill = pc.addSkill(aSkill);

		boolean oldImporting = pc.isImporting();
		pc.setImporting(true);
		final String aString =
				SkillRankControl.modRanks(aRank, pcClass, true, pc, skill);
		pc.setImporting(oldImporting);

		if (aString.length() > 0)
		{
			Logging.errorPrint("SKILL: " + aString);
			return false;
		}

		// Add any supplied languages
		if (!langList.isEmpty())
		{
			pc.addLanguages(langList);

			for (Iterator<Language> i = langList.iterator(); i.hasNext();)
			{
				pc.addAssociation(skill, i.next().getKeyName());
			}

		}
		//
		// Fix up the skill pools to reflect what we just spent.
		//
		List<PCLevelInfo> pcLvlInfo = pc.getLevelInfo();
		double ptsToSpend = aCost;
		if (ptsToSpend >= 0.0)
		{
			for (PCLevelInfo info : pcLvlInfo)
			{
				if (info.getClassKeyName().equals(pcClass.getKeyName()))
				{
					// We are spending this class' points.
					int remaining = info.getSkillPointsRemaining();
					if (remaining == 0)
					{
						continue;
					}
					int left =
							remaining - (int) Math.min(remaining, ptsToSpend);
					info.setSkillPointsRemaining(left);
					ptsToSpend -= (remaining - left);
					if (ptsToSpend <= 0)
					{
						break;
					}
				}
			}
		}
		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.refresh();

		return true;
	}

	@Override
	public String getObjectName()
	{
		return "Skills";
	}

	private List<Skill> getSkillChoices()
	{
		final List<Skill> skillsOfType = new ArrayList<Skill>();

		for (CDOMReference<Skill> ref : skillList)
		{
			for (Skill s : ref.getContainedObjects())
			{
				skillsOfType.add(s);
			}
		}

		if (skillsOfType.size() == 0)
		{
			return null;
		}
		else if (skillsOfType.size() == 1)
		{
			return skillsOfType;
		}

		List<Skill> skillChoices = new ArrayList<Skill>();
		Globals.getChoiceFromList("Select skill", skillsOfType, skillChoices,
			getSafeCount());

		return skillChoices;
	}

	private KitSkillAdd addRanks(PlayerCharacter pc, PCClass pcClass,
		Skill aSkill, double ranksLeftToAdd, boolean isFree,
		List<String> warnings)
	{
		if (!isFree && pcClass.getSkillPool(pc) == 0)
		{
			return null;
		}

		final Skill pcSkill = pc.getSkillKeyed(aSkill.getKeyName());
		double curRank = 0.0;
		if (pcSkill != null)
		{
			curRank = SkillRankControl.getRank(pc, pcSkill).doubleValue();
		}
		double ranksToAdd = ranksLeftToAdd;
		if (!Globals.checkRule(RuleConstants.SKILLMAX) && (ranksToAdd > 0.0))
		{
			ranksToAdd =
					Math.min(pc.getMaxRank(aSkill.getKeyName(), pcClass)
						.doubleValue(), curRank + ranksLeftToAdd);
			ranksToAdd -= curRank;
			if (ranksToAdd != ranksLeftToAdd)
			{
				warnings.add("SKILL: Could not add " + (ranksLeftToAdd - ranksToAdd)
					+ " to " + aSkill.getDisplayName()
					+ ". Excedes MAXRANK of "
					+ pc.getMaxRank(aSkill.getDisplayName(), pcClass) + ".");
			}
		}
		int ptsToSpend = 0;
		List<PCLevelInfo> pcLvlInfo = pc.getLevelInfo();
		int[] points = new int[pcLvlInfo.size()];
		if (!isFree)
		{
			double ranksAdded = 0.0;
			int skillCost = pc.getSkillCostForClass(aSkill, pcClass).getCost();
			ptsToSpend = (int) (ranksToAdd * skillCost);
			for (int i = 0; i < pcLvlInfo.size(); i++)
			{
				PCLevelInfo info = pcLvlInfo.get(i);
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
		final Skill skill = pc.addSkill(aSkill);

		String ret =
				SkillRankControl
					.modRanks(ranksToAdd, pcClass, false, pc, skill);
		if (ret.length() > 0)
		{
			if (isFree
				&& ret.indexOf("You do not have enough skill points.") != -1)
			{
				SkillRankControl.modRanks(ranksToAdd, pcClass, true, pc, skill);
			}
			else
			{
				warnings.add(ret);
				return null;
			}
		}
		if (!isFree)
		{
			for (int i = 0; i < pcLvlInfo.size(); i++)
			{
				PCLevelInfo info = pcLvlInfo.get(i);
				if (points[i] >= 0)
				{
					info.setSkillPointsRemaining(points[i]);
				}
			}

		}
		List<Language> langList = new ArrayList<Language>();
		if (SkillLanguage.isLanguage(aSkill) && !selection.isEmpty())
		{
			langList =
					KitSkill.getLanguageList(selection, aSkill, pc,
						(int) ranksToAdd);
		}
		return new KitSkillAdd(aSkill, ranksToAdd, ptsToSpend, langList,
			pcClass);
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

	/**
	 * Gets the list of languages from the langKeyList that are
	 * valid to add to the character for the given skill. No more
	 * than the specified number of languages will be returned. 
	 * 
	 * @param selection The list of language keys
	 * @param skill The language skill 
	 * @param aPC The character being processed
	 * @param maxNumLangs The maximum number of languages to add
	 * 
	 * @return the language list
	 */
	public static List<Language> getLanguageList(
		List<CDOMSingleRef<Language>> selection, Skill skill,
		PlayerCharacter aPC, int maxNumLangs)
	{
		List<Language> selected = new ArrayList<Language>();
		List<Language> available = new ArrayList<Language>();
		List<Language> excludedLangs = new ArrayList<Language>();

		SkillLanguage.buildLanguageListsForSkill(aPC, skill, selected,
			available, excludedLangs);

		List<Language> theLanguages = new ArrayList<Language>(maxNumLangs);
		for (CDOMSingleRef<Language> langKey : selection)
		{
			Language lang = langKey.resolvesTo();
			if (available.contains(lang))
			{
				theLanguages.add(lang);
				if (theLanguages.size() >= maxNumLangs)
				{
					break;
				}
			}
		}

		return theLanguages;
	}

	public List<CDOMSingleRef<Language>> getSelections()
	{
		return selection;
	}
}
