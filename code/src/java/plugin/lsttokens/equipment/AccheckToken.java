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
 * Deals with ACCHECK token
 */
public class AccheckToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ACCHECK";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer acc = Integer.valueOf(value);
			if (acc.intValue() > 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be an integer <= 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.AC_CHECK, acc);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.deprecationPrint(getTokenName()
					+ " expected an integer.  Found: " + value
					+ "  Assuming zero.  Tag should be of the form: "
					+ getTokenName() + ":<int>");
			context.getObjectContext().put(eq, IntegerKey.AC_CHECK, 0);
			return true;
			// Logging.log(Logging.LST_ERROR, getTokenName()
			// + " expected an integer. Tag must be of the form: "
			// + getTokenName() + ":<int>");
			// return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer check = context.getObjectContext().getInteger(eq,
				IntegerKey.AC_CHECK);
		if (check == null)
		{
			return null;
		}
		if (check.intValue() > 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer <= 0");
			return null;
		}
		return new String[] { check.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
