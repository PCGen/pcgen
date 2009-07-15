/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.skill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillCostCalc;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class CrossClassToken implements QualifierToken<Skill>
{

	private PrimitiveChoiceFilter<Skill> pcs = null;

	private boolean negated = false;

	public String getTokenName()
	{
		return "CROSSCLASS";
	}

	public Class<Skill> getChoiceClass()
	{
		return Skill.class;
	}

	public String getLSTformat(boolean useAny)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getTokenName());
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
		}
		return sb.toString();
	}

	public boolean initialize(LoadContext context, Class<Skill> cl,
			String condition, String value, boolean negate)
	{
		if (condition != null)
		{
			Logging.addParseMessage(Level.SEVERE, "Cannot make "
					+ getTokenName()
					+ " into a conditional Qualifier, remove =");
			return false;
		}
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(cl, value);
			return pcs != null;
		}
		negated = negate;
		return true;
	}

	public Set<Skill> getSet(PlayerCharacter pc)
	{
		Set<Skill> skillSet = new HashSet<Skill>();
		ArrayList<PCClass> classlist = pc.getClassList();
		ArrayList<Skill> skilllist = pc.getSkillList();
		for (Skill sk : skilllist)
		{
			CLASS: for (PCClass cl : classlist)
			{
				if (negated
						^ SkillCost.CROSS_CLASS.equals(SkillCostCalc
								.skillCostForPCClass(sk, cl, pc)))
				{
					skillSet.add(sk);
					break CLASS;
				}
			}
		}
		return skillSet;
	}

	public GroupingState getGroupingState()
	{
		GroupingState gs = pcs == null ? GroupingState.ANY : pcs
				.getGroupingState();
		return negated ? gs.negate() : gs;
	}
}
