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

package plugin.lsttokens.kit.prof;

import pcgen.core.kit.KitProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class RacialToken extends AbstractNonEmptyToken<KitProf> implements CDOMPrimaryToken<KitProf>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "RACIAL";
	}

	@Override
	public Class<KitProf> getTokenClass()
	{
		return KitProf.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitProf obj, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				return new ParseResult.Fail("You should use 'YES' as the " + getTokenName() + ": " + value);
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
			}
			set = Boolean.FALSE;
		}
		obj.setRacialProf(set);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitProf obj)
	{
		Boolean mult = obj.getRacialProf();
		if (mult == null)
		{
			return null;
		}
		return new String[]{mult ? "YES" : "NO"};
	}
}
