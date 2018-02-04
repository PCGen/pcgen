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
package plugin.lsttokens.sponsor;

import java.net.MalformedURLException;

import pcgen.cdom.content.Sponsor;
import pcgen.core.utils.CoreUtility;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with IMAGELARGE Token
 */
public class ImagelargeToken implements CDOMPrimaryToken<Sponsor>
{

	@Override
	public String getTokenName()
	{
		return "IMAGELARGE";
	}

	@Override
	public Class<Sponsor> getTokenClass()
	{
		return Sponsor.class;
	}

	@Override
	public ParseResult parseToken(LoadContext context, Sponsor s, String value)
	{
		try
		{
			s.setLargeImage(CoreUtility.processFileToURL(value));
			return ParseResult.SUCCESS;
		}
		catch (MalformedURLException e)
		{
			return new ParseResult.Fail("Error in " + getTokenName() + ": "
					+ e.getMessage());
		}
	}

	@Override
	public String[] unparse(LoadContext context, Sponsor s)
	{
		// TODO Need to unparse
		return null;
	}
}
