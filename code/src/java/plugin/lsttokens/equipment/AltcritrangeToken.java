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
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITRANGE token
 */
public class AltcritrangeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ALTCRITRANGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer cr = Integer.valueOf(value);
			if (cr.intValue() < 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " cannot be < 0");
				return false;
			}
			context.getObjectContext().put(eq.getEquipmentHead(2),
					IntegerKey.CRIT_RANGE, cr);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " expected an integer. " + "Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(2);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.getObjectContext().getInteger(head,
				IntegerKey.CRIT_RANGE);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
