/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.load;

import pcgen.core.system.LoadInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class LoadmultstepToken extends AbstractNonEmptyToken<LoadInfo> implements CDOMPrimaryToken<LoadInfo>
{

	@Override
	public String getTokenName()
	{
		return "LOADMULTSTEP";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, LoadInfo info, String value)
	{
		try
		{
			int step = Integer.parseInt(value);
			if (step <= 0)
			{
				return new ParseResult.Fail(getTokenName() + " expected a positive integer, found : " + value);
			}
			info.setLoadMultStep(Integer.parseInt(value));
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	@Override
	public String[] unparse(LoadContext context, LoadInfo info)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<LoadInfo> getTokenClass()
	{
		return LoadInfo.class;
	}
}
