/*
 * Copyright 2008-19 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import java.util.stream.Collectors;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MovementType;
import pcgen.core.SimpleMovement;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class MoveLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVE";
	}

	@Override
	protected char separator()
	{
		return ',';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);

		while (moves.countTokens() > 1)
		{
			MovementType type = MovementType.getConstant(moves.nextToken());
			String mod = moves.nextToken();
			try
			{
				SimpleMovement cm =
						new SimpleMovement(type, Integer.parseInt(mod));
				context.getObjectContext().addToList(obj,
					ListKey.SIMPLEMOVEMENT, cm);
			}
			catch (NumberFormatException e)
			{
				return new ParseResult.Fail(
					"Movement must be a number, found: " + mod);
			}
		}
		if (moves.countTokens() != 0)
		{
			return new ParseResult.Fail("Badly formed MOVE token " + "(extra value at end of list): " + value);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<SimpleMovement> changes = context.getObjectContext()
			.getListChanges(obj, ListKey.SIMPLEMOVEMENT);
		Collection<SimpleMovement> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Set<String> set = new TreeSet<>();
		for (SimpleMovement movement : added)
		{
			set.add(movement.getMovementType() + Constants.COMMA + movement.getMovement());
		}
		if (set.isEmpty())
		{
			return null;
		}
		return new String[]{
			set.stream().collect(Collectors.joining(Constants.COMMA))};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
