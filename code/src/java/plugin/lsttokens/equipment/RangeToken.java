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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with RANGE token
 */
public class RangeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "RANGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer range = Integer.valueOf(value);
			if (range.intValue() < 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
						+ " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.RANGE, range);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer range = context.getObjectContext().getInteger(eq,
				IntegerKey.RANGE);
		if (range == null)
		{
			return null;
		}
		if (range.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { range.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
