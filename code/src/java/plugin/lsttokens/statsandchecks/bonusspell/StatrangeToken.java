/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.statsandchecks.bonusspell;

import pcgen.cdom.content.BonusSpellInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements CDOMPrimaryToken<BonusSpellInfo>
{

	@Override
	public String getTokenName()
	{
		return "STATRANGE";
	}

	@Override
	public Class<BonusSpellInfo> getTokenClass()
	{
		return BonusSpellInfo.class;
	}

	@Override
	public ParseResult parseToken(LoadContext context, BonusSpellInfo bsi, String value)
	{
		try
		{
			int intValue = Integer.valueOf(value);
			if (intValue < 1)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer >= " + 1);
			}
			bsi.setStatRange(intValue);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, BonusSpellInfo bsi)
	{
		int range = bsi.getStatRange();
		if (range == 0)
		{
			return null;
		}
		return new String[]{String.valueOf(range)};
	}

}
