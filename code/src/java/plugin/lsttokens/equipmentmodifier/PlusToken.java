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
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with PLUS token
 */
public class PlusToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "PLUS";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		try
		{
			Integer plus = Integer.valueOf(value);
			if (plus.intValue() == 0)
			{
				Logging.errorPrint(getTokenName()
						+ " must be an integer not equal to 0");
				return false;
			}
			context.getObjectContext().put(mod, IntegerKey.PLUS, plus);
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

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Integer plus = context.getObjectContext().getInteger(mod,
				IntegerKey.PLUS);
		if (plus == null)
		{
			return null;
		}
		if (plus.intValue() == 0)
		{
			context.addWriteMessage(getTokenName()
					+ " must be an integer not equal to 0");
			return null;
		}
		return new String[] { plus.toString() };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
