/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ABB Token for pc stat
 */
public class AbbToken extends AbstractNonEmptyToken<PCStat> implements CDOMPrimaryToken<PCStat>
{

	/**
	 * Return token name
	 *
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "ABB";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, PCStat stat, String value)
	{
		if (!context.processToken(stat, "KEY", value))
		{
			return new ParseResult.Fail("Internal Error");
		}
		context.getObjectContext().put(stat, StringKey.ABB_KR, value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCStat stat)
	{
		String abb = context.getObjectContext().getString(stat, StringKey.ABB_KR);
		if (abb == null)
		{
			return null;
		}
		return new String[]{abb};
	}

	@Override
	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
