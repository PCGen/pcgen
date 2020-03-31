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

import java.util.Collection;
import java.util.logging.Level;

import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.converter.AddFilterConverter;
import pcgen.cdom.converter.NegateFilterConverter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class RanksToken implements QualifierToken<Skill>, PrimitiveFilter<Skill>
{

	private static final String MAXRANK = "MAXRANK";

	private PrimitiveCollection<Skill> pcs = null;

	private boolean wasRestricted = false;

	private boolean negated = false;

	private int ranks;

	private boolean maxRank = false;

	@Override
	public String getTokenName()
	{
		return "RANKS";
	}

	@Override
	public Class<Skill> getReferenceClass()
	{
		return Skill.class;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		StringBuilder sb = new StringBuilder();
		if (negated)
		{
			sb.append('!');
		}
		sb.append(getTokenName());
		sb.append('=');
		if (maxRank)
		{
			sb.append(MAXRANK);
		}
		else
		{
			sb.append(ranks);
		}
		if (wasRestricted)
		{
			sb.append('[').append(pcs.getLSTformat(useAny)).append(']');
		}
		return sb.toString();
	}

	@Override
	public boolean initialize(LoadContext context, SelectionCreator<Skill> sc, String condition, String value,
		boolean negate)
	{
		if (condition == null)
		{
			Logging.addParseMessage(Level.SEVERE,
				getTokenName() + " Must be a conditional Qualifier, e.g. " + getTokenName() + "=10");
			return false;
		}
		try
		{
			ranks = Integer.parseInt(condition);
		}
		catch (NumberFormatException e)
		{
			if (MAXRANK.equalsIgnoreCase(condition))
			{
				maxRank = true;
			}
			else
			{
				Logging.addParseMessage(Level.SEVERE,
					getTokenName() + " Must be a numerical conditional Qualifier, e.g. " + getTokenName()
						+ "=10 ... Offending value: " + condition);
				return false;
			}
		}
		negated = negate;
		if (value == null)
		{
			pcs = sc.getAllReference();
		}
		else
		{
			pcs = context.getPrimitiveChoiceFilter(sc, value);
			wasRestricted = true;
		}
		return pcs != null;
	}

	@Override
	public GroupingState getGroupingState()
	{
		GroupingState gs = pcs == null ? GroupingState.ANY : pcs.getGroupingState().reduce();
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
			if (negated == other.negated && ranks == other.ranks && maxRank == other.maxRank)
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

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Skill, R> c)
	{
		Converter<Skill, R> conv = new AddFilterConverter<>(c, this);
		conv = negated ? new NegateFilterConverter<>(conv) : conv;
		return pcs.getCollection(pc, conv);
	}

	@Override
	public boolean allow(PlayerCharacter pc, Skill sk)
	{
		float pcRanks = pc.getDisplay().getRank(sk);
		if (maxRank)
		{
			/*
			 * According to SkillRankControl any class can be used here
			 * (confusing!)
			 */
			double maxRanks = pc.getMaxRank(sk, pc.getClassList().get(0)).doubleValue();
			return maxRanks <= pcRanks;
		}
		return ranks <= pcRanks;
	}
}
