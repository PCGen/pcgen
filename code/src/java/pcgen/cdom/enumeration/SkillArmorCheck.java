/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.util.List;

import pcgen.base.lang.UnreachableError;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SystemCollections;
import pcgen.util.enumeration.Load;

public enum SkillArmorCheck
{
	// # NO - Armor check penalty does not apply (Default).
	// # YES - Armor check penalty applies to this skill.
	// # PROFICIENT - Armor check penalty applies if the character is not
	// proficient in it.
	// # DOUBLE - Armor check penalty of double normal applies to this skill.
	// # WEIGHT - Weight carried penalty applies to the skill check.

	/*
	 * WARNING: These are ordered, and the order MUST NOT BE CHANGED, due to
	 * impacts to pcgen.io.exporttoken.SkillToken (among potentially other
	 * places)
	 */
	NONE
	{
		@Override
		public int calculateBonus(PlayerCharacter pc)
		{
			return 0;
		}
	},
	YES, NONPROF
	{
		@Override
		protected boolean useEquipment(PlayerCharacter pc, Equipment eq)
		{
			return !pc.isProficientWith(eq);
		}

		@Override
		protected int calculateMin(PlayerCharacter pc)
		{
			return 0;
		}
	},
	DOUBLE
	{
		@Override
		protected int getMultiplier()
		{
			return 2;
		}
	},
	WEIGHT
	{
		@Override
		protected int calculateMax(PlayerCharacter pc)
		{
			return -(int) (pc.totalWeight().doubleValue() / 5.0);
		}

	};

	public int calculateBonus(PlayerCharacter pc)
	{
		int min;
		int max;
		int multiplier;
		if (Globals.checkRule(RuleConstants.SYS_WTPSK))
		{
			multiplier = getMultiplier();
			min = calculateMin(pc) * multiplier;
			max = calculateMax(pc) * multiplier;
		}
		else
		{
			/*
			 * Do nothing. This is to simulate taking everything off before
			 * going swimming. Freq #505977
			 */
			min = 0;
			max = 0;
		}
		return Math.min(min, max + (int) pc.getTotalBonusTo("MISC", "ACCHECK"));
	}

	protected int getMultiplier()
	{
		return 1;
	}

	protected int calculateMin(PlayerCharacter pc)
	{
		int penalty = 0;
		if (Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			final Load load = Globals.loadTypeForLoadScore(pc.getVariableValue(
					"LOADSCORE", "").intValue(), pc.totalWeight(), pc);

			switch (load)
			{
			case LIGHT:
				penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty(
						"LIGHT");
				break;

			case MEDIUM:
				penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty(
						"MEDIUM");
				break;

			case HEAVY:
			case OVERLOAD:
				penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty(
						"HEAVY");
				break;

			default:
				throw new UnreachableError(
						"Internal Error: In Skill.modifier the load " + load
								+ " is not supported.");
			}
		}
		return penalty;
	}

	protected int calculateMax(PlayerCharacter pc)
	{
		int max = 0;
		final List<Equipment> itemList = pc.getEquipmentOfType("Armor", 1);
		for (Equipment eq : pc.getEquipmentOfType("Shield", 1))
		{
			if (!itemList.contains(eq))
			{
				itemList.add(eq);
			}
		}
		for (Equipment eq : itemList)
		{
			if (useEquipment(pc, eq))
			{
				max += eq.acCheck(pc).intValue();
			}
		}
		return max;
	}

	protected boolean useEquipment(PlayerCharacter pc, Equipment eq)
	{
		return true;
	}

}
