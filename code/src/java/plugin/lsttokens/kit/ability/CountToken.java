/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit.ability;

import pcgen.core.kit.KitAbilities;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * COUNT Token for KitAbilities
 */
public class CountToken extends AbstractToken implements CDOMPrimaryToken<KitAbilities>
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "COUNT";
	}

	@Override
	public Class<KitAbilities> getTokenClass()
	{
		return KitAbilities.class;
	}

	@Override
	public ParseResult parseToken(LoadContext context, KitAbilities kitAbil, String value)
	{
		try
		{
			Integer quan = Integer.valueOf(value);
			if (quan <= 0)
			{
				return new ParseResult.Fail(getTokenName() + " expected an integer > 0");
			}
			kitAbil.setCount(quan);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, KitAbilities kitAbil)
	{
		Integer bd = kitAbil.getCount();
		if (bd == null)
		{
			return null;
		}
		return new String[]{bd.toString()};
	}
}
