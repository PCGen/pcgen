/*
 * SkillToken.java
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
 *
 * Created on Aug 5, 2004
 *
 * $Id$
 *
 */
package pcgen.io.exporttoken;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
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
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>SkillToken</code> is the base class for the SKILL
 * family of tokens. It also handles the processing of the SKILL
 * token itself, which outputs select information about a
 * choosen skill. The format for this tag is SKILL.id.property
 * where id cna be either an index or a skill name and the
 * property is optional. eg SKILL.2.RANK or SKILL.BALANCE
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class SkillToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "SKILL";

	// Constants for the property to be output.
	private static final int SKILL_NAME = 0;
	private static final int SKILL_TOTAL = 1;
	private static final int SKILL_RANK = 2;
	private static final int SKILL_MOD = 3;
	private static final int SKILL_ABILITY = 4;
	private static final int SKILL_ABMOD = 5;
	private static final int SKILL_MISC = 6;
	private static final int SKILL_UNTRAINED = 7;
	private static final int SKILL_EXCLUSIVE = 8;
	private static final int SKILL_UNTRAINED_EXTENDED = 9;
	private static final int SKILL_ACP = 10;
	private static final int SKILL_EXCLUSIVE_TOTAL = 11;
	private static final int SKILL_TRAINED_TOTAL = 12;
	private static final int SKILL_EXPLANATION = 13;
	private static final int SKILL_TYPE = 14;
	private static final int SKILL_COST = 15;

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		SkillDetails details = buildSkillDetails(tokenSource);

		Skill aSkill = getSkill(pc, details, eh);

		return getSkillProperty(aSkill, details.properties[0], pc);
	}

	/**
	 * Select the target skill based on the supplied critieria. Uses the
	 * id in the details object to either retrieve a skill by name or by
	 * position in the skill list.
	 *
	 * @param pc The character being processed.
	 * @param details The parsed details of the token.
	 * @param eh The ExportHandler
	 * @return The matching skill, or null if none match.
	 */
	private Skill getSkill(PlayerCharacter pc, SkillDetails details,
		ExportHandler eh)
	{
		Skill skill = null;
		try
		{
			final int i = Integer.parseInt(details.skillId);
			final List<Skill> pcSkills =
					pc.getSkillListInOutputOrder(pc.getDisplay()
						.getPartialSkillList(Visibility.OUTPUT_ONLY));

			if ((i >= (pcSkills.size() - 1)) && eh != null
				&& eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			if (i < pcSkills.size())
			{
				skill = pcSkills.get(i);
			}
		}
		catch (NumberFormatException exc)
		{
			//Allowing SKILL.Spot.<subtoken>
			skill = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Skill.class, details.skillId);
		}
		return skill;
	}

	/**
	 * Given the source of the token, split it up into its skill id and
	 * properties. The token itself is ignopred as this has already been
	 * processed elsewhere. The expected format is token.skillid.property...
	 *
	 * @param tokenSource The source of the token.
	 * @return A SkillDetails containing the details of the token.
	 */
	protected SkillDetails buildSkillDetails(String tokenSource)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");

		// Work out how many properties there are
		int numProps = 1;
		if (aTok.countTokens() > 2)
		{
			numProps = aTok.countTokens() - 2;
		}
		final String[] props = new String[numProps];
		props[0] = "";

		// Split out the parts of the source
		String skillId = "";
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			if (i == 0)
			{
				// Ignore
				aTok.nextToken();
			}
			else if (i == 1)
			{
				skillId = aTok.nextToken();
			}
			else
			{
				props[i - 2] = aTok.nextToken();
			}
		}

		// Create and return the SkillDetails object.
		return new SkillDetails(skillId, props);
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
	protected String getSkillProperty(Skill aSkill, String property,
		PlayerCharacter pc)
	{
		if (aSkill == null)
		{
			return "";
		}

		int action = getPropertyId(property);
		return getSkillPropValue(aSkill, action, property, pc);
	}

	/**
	 * Convert a property name into the id of the property.
	 *
	 * @param property The property name.
	 * @return The id of the property.
	 */
	private int getPropertyId(String property)
	{
		int propId = 0;

		if ("NAME".equalsIgnoreCase(property))
		{
			propId= SKILL_NAME;
		}
		else if ("TOTAL".equalsIgnoreCase(property))
		{
			propId = SKILL_TOTAL;
		}
		else if ("RANK".equalsIgnoreCase(property))
		{
			propId= SKILL_RANK;
		}
		else if ("MOD".equalsIgnoreCase(property))
		{
			propId= SKILL_MOD;
		}
		else if ("ABILITY".equalsIgnoreCase(property))
		{
			propId= SKILL_ABILITY;
		}
		else if ("ABMOD".equalsIgnoreCase(property))
		{
			propId= SKILL_ABMOD;
		}
		else if ("MISC".equalsIgnoreCase(property))
		{
			propId= SKILL_MISC;
		}
		else if ("COST".equalsIgnoreCase(property))
		{
			propId= SKILL_COST;
		}
		else if ("UNTRAINED".equalsIgnoreCase(property))
		{
			propId= SKILL_UNTRAINED;
		}
		else if ("EXCLUSIVE".equalsIgnoreCase(property))
		{
			propId= SKILL_EXCLUSIVE;
		}
		else if (property.regionMatches(true, 0, "UNTRAINED", 0, 9))
		{
			propId= SKILL_UNTRAINED_EXTENDED;
		}
		else if (property.regionMatches(true, 0, "ACP", 0, 3))
		{
			propId= SKILL_ACP;
		}
		else if ("EXCLUSIVE_TOTAL".equalsIgnoreCase(property))
		{
			propId= SKILL_EXCLUSIVE_TOTAL;
		}
		else if ("TRAINED_TOTAL".equalsIgnoreCase(property))
		{
			propId= SKILL_TRAINED_TOTAL;
		}
		else if (property.regionMatches(true, 0, "EXPLAIN", 0, 7))
		{
			propId= SKILL_EXPLANATION;
		}
		else if ("TYPE".equalsIgnoreCase(property))
		{
			propId= SKILL_TYPE;
		}
		return propId;
	}

	/**
	 * Evaluate the property for the supplied skill and character. For
	 * properties such as ACP and the extended UNTRAINED property, the
	 * property text is required to be further parsed to pull out user
	 * defined text to be output in each case.
	 *
	 * @param aSkill The skill to be reported upon.
	 * @param property The property to be reported.
	 * @param propertyText The orginal text of the property.
	 * @param pc The character to be reported upon.
	 * @return The value of the property.
	 */
	private String getSkillPropValue(Skill aSkill, int property,
		String propertyText, PlayerCharacter pc)
	{
		StringBuilder retValue = new StringBuilder();

		if (((property == SKILL_ABMOD) || (property == SKILL_MISC))
			&& false)//&& aSkill.get(ObjectKey.KEY_STAT) == null)
		{
			retValue.append("n/a");
		}
		else
		{
			switch (property)
			{
				case SKILL_NAME:
					retValue.append(QualifiedName.qualifiedName(pc, aSkill));
					break;

				case SKILL_TOTAL:
					if (SettingsHandler.getGame().hasSkillRankDisplayText())
					{
						retValue.append(SettingsHandler.getGame()
							.getSkillRankDisplayText(
								SkillRankControl.getTotalRank(pc, aSkill).intValue()
									+ SkillModifier.modifier(aSkill, pc).intValue()));
					}
					else
					{
						retValue.append(Integer.toString(SkillRankControl.getTotalRank(pc, aSkill).intValue()
							+ SkillModifier.modifier(aSkill, pc).intValue()));
					}
					break;

				case SKILL_RANK:
					if (SettingsHandler.getGame().hasSkillRankDisplayText())
					{
						retValue.append(SettingsHandler.getGame()
							.getSkillRankDisplayText(
								SkillRankControl.getTotalRank(pc, aSkill).intValue()));
					}
					else
					{
						retValue.append(SkillRankControl.getTotalRank(pc, aSkill).toString());
					}
					break;

				case SKILL_MOD:
					retValue.append(SkillModifier.modifier(aSkill, pc).toString());
					break;

				case SKILL_ABILITY:
					retValue.append(SkillInfoUtilities.getKeyStatFromStats(pc, aSkill));
					break;

				case SKILL_ABMOD:
					retValue.append(Integer.toString(SkillModifier.getStatMod(aSkill, pc)));
					break;

				case SKILL_MISC:
					retValue.append(Integer.toString(SkillModifier.modifier(aSkill, pc)
						.intValue()
						- SkillModifier.getStatMod(aSkill, pc)));
					break;

				case SKILL_UNTRAINED:
					retValue.append(aSkill.getSafe(ObjectKey.USE_UNTRAINED) ? "Y" : "NO");
					break;

				case SKILL_EXCLUSIVE:
					retValue.append(aSkill.getSafe(ObjectKey.EXCLUSIVE) ? "Y" : "N");
					break;

				case SKILL_UNTRAINED_EXTENDED:
					retValue.append(getUntrainedOutput(aSkill, propertyText));
					break;

				case SKILL_ACP:
					retValue.append(getAcpOutput(aSkill, propertyText));
					break;

				case SKILL_COST:
					SkillCost cost = null;
					for (PCClass pcc : pc.getDisplay().getClassSet())
					{
						if (cost == null)
						{
							cost = pc.getSkillCostForClass(aSkill, pcc);
						}
						else
						{
							SkillCost newCost = pc.getSkillCostForClass(aSkill, pcc);
							if (SkillCost.CLASS.equals(newCost)
								|| SkillCost.EXCLUSIVE.equals(cost))
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

				case SKILL_EXCLUSIVE_TOTAL:
					retValue
						.append(Integer
							.toString(((aSkill.getSafe(ObjectKey.EXCLUSIVE) || !aSkill.getSafe(ObjectKey.USE_UNTRAINED)) && (SkillRankControl.getTotalRank(pc, aSkill)
								.intValue() == 0)) ? 0
								: (SkillRankControl.getTotalRank(pc, aSkill).intValue() + SkillModifier.modifier(aSkill, pc).intValue())));
					break;

				case SKILL_TRAINED_TOTAL:
					retValue.append(Integer
						.toString((!aSkill.getSafe(ObjectKey.USE_UNTRAINED) && (SkillRankControl.getTotalRank(pc, aSkill).intValue() == 0)) ? 0 : (SkillRankControl.getTotalRank(pc, aSkill).intValue() + SkillModifier.modifier(aSkill, pc)
							.intValue())));
					break;

				case SKILL_EXPLANATION:
					boolean shortFrom =
							!("_LONG".equals(propertyText.substring(7)));

					String bonusDetails =
							SkillCostDisplay.getModifierExplanation(aSkill, pc, shortFrom);
					retValue.append(bonusDetails);
					break;

				case SKILL_TYPE:
					String type = aSkill.getType();
					retValue.append(type);
					break;

				default:
					Logging
						.errorPrint("In ExportHandler._writeSkillProperty the propIdvalue "
							+ property + " is not handled.");

					break;
			}
		}
		return retValue.toString();
	}

	/**
	 * Process the untrained tag.
	 * Syntax: SKILL.%.UNTRAINEDfoo,bar
	 * where foo and bar are optional strings of unfixed length.
	 * Behavior: prints out foo if the skill is usable untrained,
	 * bar if not usable untrained.
	 * if bar is not supplied, nothing is printed if untrained. If neither foo
	 * nor bar are supplied, why are you using this tag?
	 *
	 * @param aSkill The skill to be processed.
	 * @param property The property
	 * @return The string to be output.
	 */
	private String getUntrainedOutput(Skill aSkill, String property)
	{
		StringTokenizer aTok = new StringTokenizer(property.substring(9), ",");
		String untrained_tok;
		String trained_tok;

		if (aTok.hasMoreTokens())
		{
			untrained_tok = aTok.nextToken();
		}
		else
		{
			untrained_tok = "";
		}

		if (aTok.hasMoreTokens())
		{
			trained_tok = aTok.nextToken();
		}
		else
		{
			trained_tok = "";
		}

		if (aSkill.getSafe(ObjectKey.USE_UNTRAINED))
		{
			return untrained_tok;
		}
		return trained_tok;
	}

	/**
	 * Process the Armour Check Penalty tag.
	 * Syntax: SKILL.%.ACPfoo,bar,baz,bot
	 * where foo, bar, baz, and bot are strings of unfixed length.
	 * Behavior: tests for armor check penalty interaction with this skill.
	 * foo is printed if the skill is not affected by ACP.
	 * bar is printed if the skill is affected by ACP.
	 * baz is printed if the skill is only affected by ACP if the user
	 *        is untrained
	 * bot is printed if the skill has the special weight penalty
	 *        (like Swim)
	 *
	 * @param aSkill The skill instance to be processed
	 * @param property The output property supplied.
	 * @return The ACP tag output.
	 */
	private String getAcpOutput(Skill aSkill, String property)
	{
		final StringTokenizer aTok =
				new StringTokenizer(property.substring(3), ",");
		int numArgs = aTok.countTokens();
		int acp = aSkill.getSafe(ObjectKey.ARMOR_CHECK).ordinal();
		String acpText[] = new String[numArgs]; 
				
		for (int i = 0; aTok.hasMoreTokens(); i++) {
			acpText[i] = aTok.nextToken();
		}
		return ((acp < numArgs) && (acp >= 0)) ? acpText[acp] : "";
	}

	// ================== Inner class =======================
	/**
	 * <code>SkillDetails</code> holds the parsed details of a skill
	 * token. Note that apart from updating the properties array contents,
	 * instances of this class are immutable.
	 *
	 */
	final protected static class SkillDetails
	{
		/** The id of the skill - normally an index or a skill name. */
		final protected String skillId;
		/** The list of properties for the token. */
		final protected String[] properties;

		/**
		 * Constructor for skill details. Creates an immutable instance
		 * with the specified id and properties list.
		 *
		 * @param inSkillId The id of the skill - normally an index or skill name.
		 * @param inProperties The loist of properties, can be types, prefixes
		 *         and properties to be displayed.
		 */
		SkillDetails(String inSkillId, String[] inProperties)
		{
			this.skillId = inSkillId;
			this.properties = inProperties;
		}

		public int getPropertyCount()
		{
			return properties.length;
		}

		public String getProperty(int i)
		{
			return properties[i];
		}

		/**
		 * Get the ID of the Skill
		 * @return the ID of the Skill
		 */
		public String getSkillId()
		{
			return skillId;
		}
	}
}
