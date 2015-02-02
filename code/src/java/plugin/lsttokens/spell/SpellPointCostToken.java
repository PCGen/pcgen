/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.PointCost;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DURATION Token
 */
public class SpellPointCostToken extends AbstractTokenWithSeparator<Spell>
		implements CDOMPrimaryToken<Spell>
{

	@Override
	public String getTokenName()
	{
		return "SPELLPOINTCOST";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		Spell spell, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		boolean hasTotal = false;
		boolean hasNonTotal = false;

		while (aTok.hasMoreTokens())
		{
			String tok = aTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tok))
			{
				if (!first)
				{
					return new ParseResult.Fail("Non-sensical use of .CLEAR in "
							+ getTokenName() + ": " + value, context);
				}
				context.getObjectContext().removeList(spell,
						ListKey.SPELL_POINT_COST);
			}
			else
			{
				int equalLoc = tok.indexOf(Constants.EQUALS);
				String type;
				String cost;

				if (equalLoc == -1)
				{
					// Total
					hasTotal = true;
					type = "TOTAL";
					cost = tok;
				}
				else
				{
					hasNonTotal = true;
					if (tok.lastIndexOf(Constants.EQUALS) != equalLoc)
					{
						return new ParseResult.Fail("Invalid number of Arguments in "
								+ getTokenName() + "(" + spell.getDisplayName()
								+ "): " + value, context);
					}
					type = tok.substring(0, equalLoc);
					cost = tok.substring(equalLoc + 1);
				}
				int costInt;
				try
				{
					costInt = Integer.parseInt(cost);
				}
				catch (NumberFormatException e)
				{
					return new ParseResult.Fail("Invalid Value in " + getTokenName()
							+ "(" + spell.getDisplayName() + "): " + value
							+ ".  Value must be an integer.", context);
				}
				context.getObjectContext().addToList(spell,
						ListKey.SPELL_POINT_COST, new PointCost(type, costInt));
			}
			first = false;
		}
		if (hasTotal && hasNonTotal)
		{
			// TODO Error here?
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<PointCost> changes = context.getObjectContext().getListChanges(
				spell, ListKey.SPELL_POINT_COST);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		Collection<PointCost> added = changes.getAdded();
		if (added != null)
		{
			for (PointCost q : added)
			{
				StringBuilder sb = new StringBuilder();
				String type = q.getType();
				if (!"TOTAL".equals(type))
				{
					sb.append(type).append(Constants.EQUALS);
				}
				sb.append(q.getCost());
				set.add(sb.toString());
			}
		}
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		list.addAll(set);
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}

	@Override
	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
