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
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with HASSUBSTITUTIONLEVEL Token
 */
public class HassubstitutionlevelToken implements DeprecatedToken,
		CDOMPrimaryToken<PCClass>
{

    @Override
	public String getTokenName()
	{
		return "HASSUBSTITUTIONLEVEL";
	}

    @Override
	public ParseResult parseToken(LoadContext context, PCClass obj, String value)
	{
		return ParseResult.SUCCESS;
	}

    @Override
	public String[] unparse(LoadContext context, PCClass obj)
	{
		// Intentional
		return null;
	}

    @Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

    @Override
	public String getMessage(CDOMObject obj, String value)
	{
		return getTokenName()
				+ " is no longer required in Class LST file: Ignoring";
	}
}
