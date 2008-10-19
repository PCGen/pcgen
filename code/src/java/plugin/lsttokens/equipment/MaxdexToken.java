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
 * Deals with MAXDEX token
 */
public class MaxdexToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "MAXDEX";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			context.getObjectContext().put(eq, IntegerKey.MAX_DEX,
					Integer.valueOf(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer maxDexBonus = context.getObjectContext().getInteger(eq,
				IntegerKey.MAX_DEX);
		if (maxDexBonus == null)
		{
			return null;
		}
		return new String[] { maxDexBonus.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
