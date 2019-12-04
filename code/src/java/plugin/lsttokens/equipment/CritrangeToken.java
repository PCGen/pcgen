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
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with CRITRANGE token
 */
public class CritrangeToken implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "CRITRANGE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, Equipment eq, String value)
	{
		if (ControlUtilities.hasControlToken(context, CControl.CRITRANGE))
		{
			return new ParseResult.Fail(getTokenName() + " is disabled when CRITRANGE control is used: " + value);
		}
		try
		{
			int cr = Integer.parseInt(value);
			if (cr < 0)
			{
				return new ParseResult.Fail(getTokenName() + " cannot be < 0");
			}
			context.getObjectContext().put(eq.getEquipmentHead(1), IntegerKey.CRIT_RANGE, cr);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer. " + "Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(1);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.getObjectContext().getInteger(head, IntegerKey.CRIT_RANGE);
		if (mult == null)
		{
			return null;
		}
		if (mult < 0)
		{
			context.addWriteMessage(getTokenName() + " cannot be negative: " + mult);
			return null;
		}
		return new String[]{mult.toString()};
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
