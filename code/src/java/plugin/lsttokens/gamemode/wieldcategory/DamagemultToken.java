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

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class DamagemultToken extends AbstractTokenWithSeparator<WieldCategory>
		implements CDOMPrimaryToken<WieldCategory>
{

	@Override
	public String getTokenName()
	{
		return "DAMAGEMULT";
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
			WieldCategory wc, String value)
	{
		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);

		while (st.hasMoreTokens())
		{
			String set = st.nextToken();
			ParseResult pr = checkForIllegalSeparator('=', set);
			if (!pr.passed())
			{
				return pr;
			}

			int equalLoc = set.indexOf('=');
			if (equalLoc == -1)
			{
				return new ParseResult.Fail("No = in part of token, found: "
						+ set + " in " + value);
			}
			if (equalLoc != set.lastIndexOf('='))
			{
				return new ParseResult.Fail(
						"Too many = in part of token, found: " + set + " in "
								+ value);
			}
			String hands = set.substring(0, equalLoc);
			int numHands;
			try
			{
				numHands = Integer.parseInt(hands);
			}
			catch (NumberFormatException ex)
			{
				return new ParseResult.Fail(getTokenName()
						+ " expected an integer before '='.  Found: " + hands
						+ " in " + value);
			}
			String multiplier = set.substring(equalLoc + 1);
			float mult;
			try
			{
				mult = Float.parseFloat(multiplier);
			}
			catch (NumberFormatException ex)
			{
				return new ParseResult.Fail(getTokenName()
						+ " expected an float after '='.  Found: " + hands
						+ " in " + value);
			}
			wc.addDamageMult(numHands, mult);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	protected char separator()
	{
		return ',';
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
