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
import pcgen.core.Equipment;
import pcgen.core.Movement;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MoveLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "MOVE";
	}

	private boolean validateMove(String value, String mod)
	{
		try
		{
			if (Integer.parseInt(mod) < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Invalid movement (cannot be negative): " + mod
								+ " in MOVE: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Invalid movement (must be an integer >= 0): " + mod
							+ " in MOVE: " + value);
			return false;
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Equipment)
		{
			return false;
		}
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 1)
		{
			cm = new Movement(1);
			String mod = moves.nextToken();
			if (!validateMove(value, mod))
			{
				return false;
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
				if (!validateMove(value, mod))
				{
					return false;
				}
				cm.assignMovement(x++, type, mod);
			}
			if (moves.countTokens() != 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Badly formed MOVE token "
								+ "(extra value at end of list): " + value);
				return false;
			}
		}
		cm.setMoveRatesFlag(0);
		context.obj.addToList(obj, ListKey.MOVEMENT, cm);
		return true;
	}

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
			StringBuilder sb = new StringBuilder();
			m.addTokenContents(sb);
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
