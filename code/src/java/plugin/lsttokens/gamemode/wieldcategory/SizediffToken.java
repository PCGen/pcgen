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
package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SizediffToken extends AbstractNonEmptyToken<WieldCategory>
		implements CDOMPrimaryToken<WieldCategory>
{

	@Override
	public String getTokenName()
	{
		return "SIZEDIFF";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context,
			WieldCategory wc, String value)
	{
		try
		{
			wc.setSizeDifference(Integer.parseInt(value));
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, WieldCategory wc)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<WieldCategory> getTokenClass()
	{
		return WieldCategory.class;
	}
}
