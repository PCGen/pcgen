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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
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
		if (negated)
		{
			sb.append('!');
		}
		sb.append(getTokenName());
		sb.append('=');
		sb.append(ranks);
		if (pcs != null)
		{
			sb.append('[').append(pcs.getLSTformat()).append(']');
		}
		return sb.toString();
	}

	public boolean initialize(LoadContext context, SelectionCreator<Skill> sc,
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
		negated = negate;
		if (value != null)
		{
			pcs = context.getPrimitiveChoiceFilter(sc, value);
			return pcs != null;
		}
		return true;
	}

	public Set<Skill> getSet(PlayerCharacter pc)
	{
		Set<Skill> skillSet = new HashSet<Skill>();
		for (Skill sk : pc.getSkillSet())
		{
			boolean allow = (pcs == null) || pcs.allow(pc, sk);
			if (allow && (negated ^ (ranks <= pc.getSkillRank(sk))))
			{
				skillSet.add(sk);
			}
		}
		return skillSet;
	}

	public GroupingState getGroupingState()
	{
		GroupingState gs = pcs == null ? GroupingState.ANY : pcs
				.getGroupingState().reduce();
		return negated ? gs.negate() : gs;
	}

	@Override
	public int hashCode()
	{
		return pcs == null ? 0 : pcs.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof RanksToken)
		{
			RanksToken other = (RanksToken) o;
			if (negated == other.negated && ranks == other.ranks)
			{
				if (pcs == null)
				{
					return other.pcs == null;
				}
				return pcs.equals(other.pcs);
			}
		}
		return false;
	}
}
