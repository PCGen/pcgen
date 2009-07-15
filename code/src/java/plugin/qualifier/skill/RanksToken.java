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
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class RanksToken implements QualifierToken<Skill>
{

	private PrimitiveChoiceFilter<Skill> pcs = null;

	private boolean negated = false;

	private int ranks;

	public String getTokenName()
	{
		return "RANKS";
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
		if (condition == null)
		{
			Logging.addParseMessage(Level.SEVERE, getTokenName()
					+ " Must be a conditional Qualifier, e.g. "
					+ getTokenName() + "=10");
			return false;
		}
		try
		{
			ranks = Integer.parseInt(condition);
		}
		catch (NumberFormatException e)
		{
			Logging.addParseMessage(Level.SEVERE, getTokenName()
					+ " Must be a numerical conditional Qualifier, e.g. "
					+ getTokenName() + "=10 ... Offending value: " + condition);
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
		ArrayList<Skill> skilllist = pc.getSkillList();
		for (Skill sk : skilllist)
		{
			boolean allow = ranks >= SkillRankControl.getRank(pc, sk);
			if (negated ^ allow)
			{
				skillSet.add(sk);
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
