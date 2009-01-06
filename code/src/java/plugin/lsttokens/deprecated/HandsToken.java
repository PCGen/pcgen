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
package plugin.lsttokens.deprecated;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with HANDS token
 */
public class HandsToken implements CDOMPrimaryToken<Equipment>, DeprecatedToken
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer hands = Integer.valueOf(value);
			if (hands.intValue() < 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(eq, IntegerKey.SLOTS, hands);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		//Unparses as SLOTS
		return null;
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

	public String getMessage(PObject obj, String value)
	{
		return getTokenName() + " is deprecated, please use SLOTS:";
	}
}
