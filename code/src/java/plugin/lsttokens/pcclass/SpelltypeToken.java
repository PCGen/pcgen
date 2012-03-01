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
package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SPELLTYPE Token
 */
public class SpelltypeToken implements CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCClass pcc, String value)
	{
		if (value == null || value.length() == 0)
		{
			// CONSIDER Deprecate this behavior
			return ParseResult.SUCCESS;
		}
		if (value.equalsIgnoreCase(Constants.LST_NONE))
		{
			// CONSIDER Deprecate this behavior
			return ParseResult.SUCCESS;
		}
		context.getObjectContext().put(pcc, StringKey.SPELLTYPE, value);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		String target = context.getObjectContext().getString(pcc,
				StringKey.SPELLTYPE);
		if (target == null)
		{
			return null;
		}
		return new String[] { target };
	}

	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

}
