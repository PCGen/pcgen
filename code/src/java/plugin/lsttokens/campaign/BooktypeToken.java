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
package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with BOOKTYPE Token
 */
public class BooktypeToken implements CDOMPrimaryToken<Campaign>
{

    @Override
	public String getTokenName()
	{
		return "BOOKTYPE";
	}

    @Override
	public ParseResult parseToken(LoadContext context, Campaign camp,
		String value)
	{
		if (value == null || value.length() == 0)
		{
			return new ParseResult.Fail(getTokenName() + " arguments may not be empty", context);
		}
		context.getObjectContext().put(camp, StringKey.BOOK_TYPE, value);
		return ParseResult.SUCCESS;
	}

    @Override
	public String[] unparse(LoadContext context, Campaign camp)
	{
		String booktype =
				context.getObjectContext().getString(camp, StringKey.BOOK_TYPE);
		if (booktype == null)
		{
			return null;
		}
		return new String[]{booktype};
	}

    @Override
	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
