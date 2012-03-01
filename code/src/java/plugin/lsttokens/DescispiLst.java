/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 *
 */
public class DescispiLst implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "DESCISPI";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				return new ParseResult.Fail("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.FALSE;
		}
		context.obj.put(obj, ObjectKey.DESC_PI, set);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Boolean descPI = context.getObjectContext().getObject(obj,
				ObjectKey.DESC_PI);
		if (descPI == null)
		{
			return null;
		}
		return new String[] { descPI.booleanValue() ? "YES" : "NO" };
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
