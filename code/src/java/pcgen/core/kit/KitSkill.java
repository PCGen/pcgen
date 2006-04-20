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

import java.io.Serializable;
import java.util.*;

import pcgen.core.*;
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
public final class KitSkill extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String skillName = "";
	private String className = null;
	private boolean free = false;
	private double rank = 1.0;

	private transient List skillsToAdd = new ArrayList();

	/**
	 * Constructor.  Takes the name of the skill it will try and add.
	 * @param argSkill The name of the skill to add.
	 */
	public KitSkill(final String argSkill)
	{
		skillName = argSkill;
	}

	/**
	 * Used to make purchasing ranks of this skill not come out of the skill
	 * pool.
	 * @param argFree <code>true</code> to make the skill ranks free.
	 */
	public void setFree(final boolean argFree)
	{
		free = argFree;
	}

	/**
	 * Returns if the skill will be purchased for free.
	 * @return <code>true</code> if the skill will be free
	 */
	public boolean isFree()
	{
		return free;
	}

	/**
	 * Sets the number of ranks to add
	 * @param argRank String capable of being converted to a double
	 */
	public void setRank(final String argRank)
	{
		try
		{
			rank = Double.parseDouble(argRank);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Invalid rank \"" + argRank + "\" in KitSkill.setRank");
		}
	}

	/**
	 * Get the rank of the skill
	 * @return rank
	 */
	public double getRank()
	{
		return rank;
	}

	/**
	 * Get the name of the skill
	 * @return name
	 */
	public String getSkillName()
	{
		return skillName;
	}

	/**
	 * Get the name of the class of the skill
	 * @return name of class
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Set the class name of the skill
	 * @param aClassName
	 */
	public void setClassName(String aClassName)
	{
		className = aClassName;
	}

	public String toString()
	{
		final StringBuffer info = new StringBuffer(100);
		if (skillName.indexOf("|") != -1)
		{
			// This is a choice of skills.
			info.append(getChoiceCount() + " of (");
			info.append(skillName.replaceAll("\\|", ", "));
			info.append(")");
		}
		else
		{
			info.append(skillName);
		}
		info.append(" (").append(rank);

		if (info.toString().endsWith(".0"))
		{
			info.setLength(info.length() - 2);
		}

		if (free)
		{
			info.append("/free");
		}

		info.append(')');

		return info.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		skillsToAdd = new ArrayList();

		String skillNameInstance = getSkillName();

		if (skillNameInstance == null)
		{
			return false;
		}

		ArrayList skillChoices = new ArrayList();
		if (skillNameInstance.startsWith("TYPE=") || skillNameInstance.startsWith("TYPE.") || skillNameInstance.indexOf("|") > -1)
		{
			getSkillChoices(skillNameInstance, skillChoices);
		}
		else
		{
			skillChoices.add(skillNameInstance);
		}

		if (skillChoices.size() == 0)
		{
			// They didn't make a choice so don't add any ranks.
			return false;
		}
		for (Iterator i = skillChoices.iterator(); i.hasNext(); )
		{
			String curSkill = (String) i.next();
			Skill skill = Globals.getSkillNamed(curSkill);

			if (skill == null)
			{
				warnings.add("SKILL: Non-existant skill \"" + skillNameInstance + "\"");

				return false;
			}

			double ranksLeftToAdd = getRank();
			Set classList = new HashSet();
			if (getClassName() != null)
			{
				// Make sure if they specified a class to add from we try that
				// class first.
				PCClass pcClass = aPC.getClassNamed(getClassName());
				if (pcClass != null)
				{
					classList.add(pcClass);
				}
				else
				{
					warnings.add("SKILL: Could not find specified class " +
								 pcClass.getName() + " to add ranks from.");
				}
			}
			classList.addAll(aPC.getClassList());

			// Try and find a class we can add them from.
			for (Iterator j = aPC.getClassList().iterator(); j.hasNext(); )
			{
				PCClass pcClass = (PCClass) j.next();
				final KitSkillAdd sta = addRanks(aPC, pcClass, skill,
												 ranksLeftToAdd, isFree(),
												 warnings);
				if (sta != null)
				{
					final KitWrapper tta = new KitWrapper(sta);
					tta.setPObject(pcClass);
					skillsToAdd.add(tta);
					ranksLeftToAdd -= sta.getRanks();
					if (ranksLeftToAdd == 0.0)
					{
						break;
					}
				}
			}
			if (ranksLeftToAdd > 0.0)
			{
				warnings.add("SKILL: Could not add " + ranksLeftToAdd
							 + " ranks to " + skill.getName()
							 + ". Not enough points.");
			}
		}
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		/** TODO Fix this to return what panes need to be refreshed */
		for (Iterator i = skillsToAdd.iterator(); i.hasNext(); )
		{
			KitWrapper wrapper = (KitWrapper)i.next();
			KitSkillAdd ksa = (KitSkillAdd)wrapper.getObject();
			updatePCSkills(aPC, ksa.getSkill(), (int)ksa.getRanks(), ksa.getCost(), (PCClass)wrapper.getPObject());
		}
	}

	/**
	 * Needs documentation.
	 *
	 * @param pc update skills for this PC
	 * @param aSkill Skill to update
	 * @param aRank Number of ranks to add
	 * @param aCost Cost of added ranks
	 * @param pcClass skills apply to this class
	 *
	 * @return <code>true</code> for success
	 * TODO What about throwing on failure?
	 */
	private boolean updatePCSkills(final PlayerCharacter pc, final Skill aSkill,
			final int aRank, final double aCost, final PCClass pcClass)
	{
		final Skill skill = pc.addSkill(aSkill);

		final String aString = skill.modRanks(aRank, pcClass, true, pc);

		if (aString.length() > 0)
		{
			Logging.errorPrint("SKILL: " + aString);
			return false;
		}

		//
		// Fix up the skill pools to reflect what we just spent.
		//
		List pcLvlInfo = pc.getLevelInfo();
		double ptsToSpend = aCost;
		if (ptsToSpend >= 0.0)
		{
			for (Iterator i = pcLvlInfo.iterator(); i.hasNext(); )
			{
				PCLevelInfo info = (PCLevelInfo) i.next();
				if (info.getClassKeyName().equals(pcClass.getName()))
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
		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.refresh();

		return true;
	}

	public String getObjectName()
	{
		return "Skills";
	}

	private String getSkillChoices(String aSkillStr, List skillChoices)
	{
		final List skillsOfType = new ArrayList();

		final StringTokenizer aTok = new StringTokenizer(aSkillStr,	"|");
		while (aTok.hasMoreTokens())
		{
			String skill = aTok.nextToken();
			if (skill.startsWith("TYPE=") ||
				skill.startsWith("TYPE."))
			{
				final String skillType = skill.substring(5);

				for (Iterator e = Globals.getSkillList().iterator();
					 e.hasNext(); )
				{
					Skill checkSkill = (Skill) e.next();

					if (checkSkill.isType(skillType))
					{
						skillsOfType.add(checkSkill.getName());
					}
				}
			}
			else
			{
				skillsOfType.add(skill);
			}
		}

		if (skillsOfType.size() == 0)
		{
			return null;
		}
		else if (skillsOfType.size() == 1)
		{
			return (String) skillsOfType.get(0);
		}

		return Globals.chooseFromList("Select skill", skillsOfType,
									  skillChoices, this.getChoiceCount());
	}

	private KitSkillAdd addRanks(PlayerCharacter pc, PCClass pcClass,
								 Skill aSkill, double ranksLeftToAdd,
								 boolean isFree, List warnings)
	{
		if (isFree == false && pcClass.getSkillPool(pc) == 0)
		{
			return null;
		}

		final Skill pcSkill = pc.getSkillNamed(aSkill.getName());
		double curRank = 0.0;
		if (pcSkill != null)
		{
			curRank = pcSkill.getRank().doubleValue();
		}
		double ranksToAdd = ranksLeftToAdd;
		if (!Globals.checkRule(RuleConstants.SKILLMAX) && (ranksToAdd > 0.0))
		{
			ranksToAdd = Math.min(pc.getMaxRank(aSkill.getName(),
												pcClass).doubleValue(),
								  curRank + ranksLeftToAdd);
			ranksToAdd -= curRank;
			if (ranksToAdd != ranksLeftToAdd)
			{
				warnings.add("SKILL: Could not add "
							 + (ranksLeftToAdd - ranksToAdd) + " to "
							 + aSkill.getName() + ". Excedes MAXRANK of "
							 + pc.getMaxRank(aSkill.getName(), pcClass) + ".");
			}
		}
		int ptsToSpend = 0;
		List pcLvlInfo = pc.getLevelInfo();
		int[] points = new int[pcLvlInfo.size()];
		if (!isFree)
		{
			double ranksAdded = 0.0;
			ptsToSpend = (int)(ranksToAdd * aSkill.costForPCClass(pcClass, pc));
			for (int i = 0; i < pcLvlInfo.size(); i++ )
			{
				PCLevelInfo info = (PCLevelInfo)pcLvlInfo.get(i);
				if (info.getClassKeyName().equals(pcClass.getName()))
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
				ranksAdded += ((double)spent / (double)aSkill.costForPCClass(pcClass, pc));
				if (ranksAdded == ranksToAdd || ptsToSpend <= 0)
				{
					break;
				}
			}

			ranksToAdd = ranksAdded;
			ptsToSpend = (int)(ranksToAdd * aSkill.costForPCClass(pcClass, pc));
		}
		final Skill skill = pc.addSkill(aSkill);

		String ret = skill.modRanks(ranksToAdd, pcClass, pc);
		if (ret.length() > 0)
		{
			if (isFree && ret.indexOf("You do not have enough skill points.") != -1)
			{
				skill.modRanks(ranksToAdd, pcClass, true, pc);
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
				PCLevelInfo info = (PCLevelInfo)pcLvlInfo.get(i);
				if (points[i] >= 0)
				{
					info.setSkillPointsRemaining(points[i]);
				}
			}

		}
		return new KitSkillAdd(aSkill, ranksToAdd, ptsToSpend);
	}
}
