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
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with SPELLFAILURE token
 */
public class SpellfailureToken extends AbstractIntToken<Equipment> implements
		CDOMPrimaryToken<Equipment>
{
	public String getTokenName()
	{
		return "SPELLFAILURE";
	}

	@Override
	protected IntegerKey integerKey()
	{
		return IntegerKey.SPELL_FAILURE;
	}

	@Override
	protected int minValue()
	{
		return 0;
	}

	@Override
	protected ParseResult checkValue(Integer value)
	{
		ParseResult pr = super.checkValue(value);
		if (!pr.passed())
		{
			return pr;
		}
		else if (value.intValue() == 0)
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addWarningMessage(getTokenName()
				+ " should not be used if zero (default is zero)");
			return cpr;
		}
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Integer sf = context.getObjectContext().getInteger(eq,
				IntegerKey.SPELL_FAILURE);
		if (sf == null)
		{
			return null;
		}
		if (sf.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { sf.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
