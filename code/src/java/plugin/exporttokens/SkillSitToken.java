/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
 * derived from export token SkillToken.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 */
package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.helper.SkillSituation;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.analysis.QualifiedName;
import pcgen.core.analysis.SkillInfoUtilities;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.SkillCostDisplay;
import pcgen.core.display.SkillDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SkillToken;
import pcgen.io.exporttoken.SkillToken.SkillDetails;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;

import org.apache.commons.lang3.StringUtils;

public class SkillSitToken extends Token
{
	public static final String TOKENNAME = "SKILLSIT";

	// Cache the skill list as it is expensive to build
	private List<Skill> cachedSkillList = null;
	private PlayerCharacter lastPC = null;
	private int lastPCSerial;

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		SkillDetails details = SkillToken.buildSkillDetails(tokenSource);

		Object aSkill = getSkill(pc, details, eh);

		return getSkillProperty(aSkill, details.getProperty(0), pc);
	}

	/**
	 * Select the target skill based on the supplied criteria. Uses the
	 * id in the details object to either retrieve a skill by name or by
	 * position in the skill list.
	 *
	 * @param pc The character being processed.
	 * @param details The parsed details of the token.
	 * @param eh The ExportHandler
	 * @return The matching skill, or null if none match.
	 */
	private Object getSkill(PlayerCharacter pc, SkillDetails details, ExportHandler eh)
	{
		Object skill = null;
		try
		{
			int i = Integer.parseInt(details.getSkillId());
			final List<Skill> pcSkills = new ArrayList<>(getSkillList(pc));

			SkillFilter filter = details.getSkillFilter();
			if (filter == null || filter == SkillFilter.Selected)
			{
				filter = pc.getSkillFilter();
			}

			Iterator<Skill> iter = pcSkills.iterator();
			while (iter.hasNext())
			{
				Skill sk = iter.next();
				if (!pc.includeSkill(sk, filter) || !sk.qualifies(pc, null))
				{
					iter.remove();
				}
			}

			if ((i >= (pcSkills.size() - 1)) && eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			for (iter = pcSkills.iterator(); i >= 0;)
			{
				Skill sk = iter.next();

				if (i == 0)
				{
					return sk;
				}
				i--; //wasn't the base skill
				List<String> situations = new ArrayList<>(sk.getUniqueListFor(ListKey.SITUATION));
				int numSits = situations.size();
				if (i < numSits)
				{
					Collections.sort(situations);
				}
				for (String situation : situations)
				{
					double bonus = pc.getTotalBonusTo("SITUATION", sk.getKeyName() + '=' + situation);
					if (bonus > 0.01 || bonus < -0.01)
					{
						if (i == 0)
						{
							return new SkillSituation(sk, situation, bonus);
						}
						i--; //Wasn't this situation
					}
				}
			}
		}
		catch (NumberFormatException exc)
		{
			String skillName = details.getSkillId();
			int equalLoc = skillName.indexOf('=');
			if (equalLoc == -1)
			{
				//Allowing SKILL.Spot.<subtoken>
				skill = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class,
					skillName);
			}
			else
			{
				//Allowing SKILL.Spot=Situation.<subtoken>
				String situation = skillName.substring(equalLoc + 1);
				Skill sk = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class,
					skillName.substring(0, equalLoc));
				double bonus = pc.getTotalBonusTo("SITUATION", sk.getKeyName() + '=' + situation);
				return new SkillSituation(sk, situation, bonus);
			}
		}
		return skill;
	}

	private synchronized List<Skill> getSkillList(PlayerCharacter pc)
	{
		if (pc == lastPC && pc.getSerial() == lastPCSerial)
		{
			return cachedSkillList;
		}

		final List<Skill> pcSkills =
				SkillDisplay.getSkillListInOutputOrder(pc, pc.getDisplay().getPartialSkillList(View.VISIBLE_EXPORT));
		cachedSkillList = pcSkills;
		lastPC = pc;
		lastPCSerial = pc.getSerial();
		return pcSkills;
	}

	/**
	 * Calculate the value of the specified skill property for the
	 * supplied skill and character.
	 *
	 * @param aSkill The skill to be processed.
	 * @param property The property being processed.
	 * @param pc The character to be reported.
	 * @return The skill tag output value.
	 */
	protected String getSkillProperty(Object aSkill, String property, PlayerCharacter pc)
	{
		if (aSkill == null)
		{
			return "";
		}

		int action = SkillToken.getPropertyId(property);
		return getSkillPropValue(aSkill, action, property, pc);
	}

	/**
	 * Evaluate the property for the supplied skill and character. For
	 * properties such as ACP and the extended UNTRAINED property, the
	 * property text is required to be further parsed to pull out user
	 * defined text to be output in each case.
	 *
	 * @param skillSit The skill to be reported upon.
	 * @param property The property to be reported.
	 * @param propertyText The original text of the property.
	 * @param pc The character to be reported upon.
	 * @return The value of the property.
	 */
	private String getSkillPropValue(Object skillSit, int property, String propertyText, PlayerCharacter pc)
	{
		StringBuilder retValue = new StringBuilder();

		if (((property == SkillToken.SKILL_ABMOD) || (property == SkillToken.SKILL_MISC)) && false)
			//&& aSkill.get(ObjectKey.KEY_STAT) == null)
		{
			retValue.append("n/a");
		}
		else
		{
			Skill skill;
			boolean isSituation;
			String situation;
			SkillSituation sit;
			if (skillSit instanceof Skill)
			{
				sit = null;
				skill = (Skill) skillSit;
				isSituation = false;
				situation = "";
			}
			else if (skillSit instanceof SkillSituation)
			{
				sit = (SkillSituation) skillSit;
				skill = sit.getSkill();
				isSituation = true;
				situation = sit.getSituation();
			}
			else
			{
				Logging.errorPrint("Internal Error: unexpected type: " + skillSit.getClass());
				return "";
			}
			switch (property)
			{
				case SkillToken.SKILL_NAME:
					String name = QualifiedName.qualifiedName(pc, skill);
					if (isSituation)
					{
						name += " (" + situation + ')';
					}
					retValue.append(name);
					break;

				case SkillToken.SKILL_TOTAL:
					int rank = SkillRankControl.getTotalRank(pc, skill).intValue()
						+ SkillModifier.modifier(skill, pc).intValue();
					if (isSituation)
					{
						rank += sit.getSituationBonus();
					}
					if (SettingsHandler.getGame().hasSkillRankDisplayText())
					{
						retValue.append(SettingsHandler.getGame().getSkillRankDisplayText(rank));
					}
					else
					{
						retValue.append(Integer.toString(rank));
					}
					break;

				case SkillToken.SKILL_RANK:
					Float sRank = SkillRankControl.getTotalRank(pc, skill);
					if (SettingsHandler.getGame().hasSkillRankDisplayText())
					{
						retValue.append(SettingsHandler.getGame().getSkillRankDisplayText(sRank.intValue()));
					}
					else
					{
						retValue.append(SkillRankControl.getTotalRank(pc, skill).toString());
					}
					break;

				case SkillToken.SKILL_MOD:
					int mod = SkillModifier.modifier(skill, pc).intValue();
					if (isSituation)
					{
						mod += sit.getSituationBonus();
					}
					retValue.append(Integer.toString(mod));
					break;

				case SkillToken.SKILL_ABILITY:
					retValue.append(SkillInfoUtilities.getKeyStatFromStats(pc, skill));
					break;

				case SkillToken.SKILL_ABMOD:
					retValue.append(Integer.toString(SkillModifier.getStatMod(skill, pc)));
					break;

				case SkillToken.SKILL_MISC:
					int misc = SkillModifier.modifier(skill, pc).intValue();
					if (isSituation)
					{
						misc += sit.getSituationBonus();
					}
					misc -= SkillModifier.getStatMod(skill, pc);
					retValue.append(Integer.toString(misc));
					break;

				case SkillToken.SKILL_UNTRAINED:
					retValue.append(skill.getSafe(ObjectKey.USE_UNTRAINED) ? "Y" : "NO");
					break;

				case SkillToken.SKILL_EXCLUSIVE:
					retValue.append(skill.getSafe(ObjectKey.EXCLUSIVE) ? "Y" : "N");
					break;

				case SkillToken.SKILL_UNTRAINED_EXTENDED:
					retValue.append(SkillToken.getUntrainedOutput(skill, propertyText));
					break;

				case SkillToken.SKILL_ACP:
					retValue.append(SkillToken.getAcpOutput(skill, propertyText));
					break;

				case SkillToken.SKILL_COST:
					SkillCost cost = null;
					for (PCClass pcc : pc.getDisplay().getClassSet())
					{
						if (cost == null)
						{
							cost = pc.getSkillCostForClass(skill, pcc);
						}
						else
						{
							SkillCost newCost = pc.getSkillCostForClass(skill, pcc);
							if (SkillCost.CLASS.equals(newCost) || SkillCost.EXCLUSIVE.equals(cost))
							{
								cost = newCost;
							}
						}
						if (SkillCost.CLASS.equals(cost))
						{
							break;
						}
					}
					retValue.append(cost.toString());
					break;

				case SkillToken.SKILL_EXCLUSIVE_TOTAL:
					int etRank = SkillRankControl.getTotalRank(pc, skill).intValue();
					boolean b = (skill.getSafe(ObjectKey.EXCLUSIVE) || !skill.getSafe(ObjectKey.USE_UNTRAINED))
						&& (etRank == 0);
					if (b)
					{
						retValue.append('0');
					}
					else
					{
						int mRank = etRank + SkillModifier.modifier(skill, pc).intValue();
						if (isSituation)
						{
							mRank += sit.getSituationBonus();
						}
						retValue.append(Integer.toString(mRank));
					}
					break;

				case SkillToken.SKILL_TRAINED_TOTAL:
					int tRank = SkillRankControl.getTotalRank(pc, skill).intValue();
					boolean isNotTrained = !skill.getSafe(ObjectKey.USE_UNTRAINED) && (tRank == 0);
					if (isNotTrained)
					{
						retValue.append('0');
					}
					else
					{
						int mRank = tRank + SkillModifier.modifier(skill, pc).intValue();
						if (isSituation)
						{
							mRank += sit.getSituationBonus();
						}
						retValue.append(Integer.toString(mRank));
					}
					break;

				case SkillToken.SKILL_EXPLANATION:
					boolean shortFrom = !("_LONG".equals(propertyText.substring(7)));

					String bonusDetails = SkillCostDisplay.getModifierExplanation(skill, pc, shortFrom);
					if (isSituation)
					{
						String sitDetails =
								SkillCostDisplay.getSituationModifierExplanation(skill, situation, pc, shortFrom);
						retValue.append(bonusDetails).append(" situational: ").append(sitDetails);
					}
					else
					{
						retValue.append(bonusDetails);
					}
					break;

				case SkillToken.SKILL_TYPE:
					String type = skill.getType();
					retValue.append(type);
					break;

				case SkillToken.SKILL_SIZE:
					int i = (int) (pc.getSizeAdjustmentBonusTo("SKILL", skill.getKeyName()));
					if (isSituation)
					{
						i += pc.getSizeAdjustmentBonusTo("SITUATION", skill.getKeyName() + '=' + situation);
					}
					retValue.append(Integer.toString(i));
					break;

				case SkillToken.SKILL_CLASSES:
					List<String> classes = new ArrayList<>();
					for (PCClass aClass : pc.getClassList())
					{
						if (pc.getSkillCostForClass(skill, aClass) == SkillCost.CLASS)
						{
							classes.add(aClass.getDisplayName());
						}
					}
					retValue.append(StringUtils.join(classes, "."));
					break;

				default:
					Logging.errorPrint(
						"In ExportHandler._writeSkillProperty the propIdvalue " + property + " is not handled.");

					break;
			}
		}
		return retValue.toString();
	}
}
