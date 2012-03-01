/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Movement;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 *
 */
public class MoveLst extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVE";
	}

	private ParseResult validateMove(String value, String mod)
	{
		try
		{
			if (Integer.parseInt(mod) < 0)
			{
				return new ParseResult.Fail(
						"Invalid movement (cannot be negative): " + mod
								+ " in MOVE: " + value);
			}
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
					"Invalid movement (must be an integer >= 0): " + mod
							+ " in MOVE: " + value);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	protected char separator()
	{
		return ',';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		//TODO Need to confirm with LST monkeys that MOVE works properly in equipment before making this permanent
//		if (obj instanceof Equipment)
//		{
//			return false;
//		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 1)
		{
			cm = new Movement(1);
			String mod = moves.nextToken();
			ParseResult pr = validateMove(value, mod);
			if (!pr.passed())
			{
				return pr;
			}
			cm.assignMovement(0, "Walk", mod);
		}
		else
		{
			cm = new Movement(moves.countTokens() / 2);

			int x = 0;
			while (moves.countTokens() > 1)
			{
				String type = moves.nextToken();
				String mod = moves.nextToken();
				ParseResult pr = validateMove(value, mod);
				if (!pr.passed())
				{
					return pr;
				}
				cm.assignMovement(x++, type, mod);
			}
			if (moves.countTokens() != 0)
			{
				return new ParseResult.Fail(
						"Badly formed MOVE token "
								+ "(extra value at end of list): " + value);
			}
		}
		cm.setMoveRatesFlag(0);
		context.obj.addToList(obj, ListKey.MOVEMENT, cm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Movement> changes = context.getObjectContext().getListChanges(
				obj, ListKey.MOVEMENT);
		Collection<Movement> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (Movement m : added)
		{
			if (m.getMoveRatesFlag() == 0)
			{
				StringBuilder sb = new StringBuilder();
				m.addTokenContents(sb);
				set.add(sb.toString());
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
