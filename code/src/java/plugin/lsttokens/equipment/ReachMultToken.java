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
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;

/**
 * Deals with REACHMULT token
 */
public class ReachMultToken extends AbstractIntToken<Equipment> implements CDOMPrimaryParserToken<Equipment>
{
	public String getTokenName()
	{
		return "REACHMULT";
	}

	@Override
	protected IntegerKey integerKey()
	{
		return IntegerKey.REACH_MULT;
	}

	@Override
	protected int minValue()
	{
		return 1;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer mult = context.getObjectContext().getInteger(eq,
				IntegerKey.REACH_MULT);
		if (mult == null)
		{
			return null;
		}
		if (mult.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { mult.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
