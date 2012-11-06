/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deprecated;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;

public class RaceToken implements CDOMCompatibilityToken<CDOMObject>
{

    @Override
	public String getTokenName()
	{
		return "CHOOSE";
	}

    @Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
			String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:RACE must not be empty", context);
		}
		if (((value.indexOf("RACE|") == 0) && ((value.indexOf("[") == 4) || (value
				.indexOf("|[") != -1))))
		{
			return context.processSubToken(obj, getTokenName(), "RACE",
					StringUtil.replaceAll(value.substring(5), "[", "ANY["));
		}
		return new ParseResult.Fail("CHOOSE:RACE not compatible", context);
	}

    @Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

    @Override
	public int compatibilityLevel()
	{
		return 5;
	}

    @Override
	public int compatibilitySubLevel()
	{
		return 16;
	}

    @Override
	public int compatibilityPriority()
	{
		return 0;
	}

}
