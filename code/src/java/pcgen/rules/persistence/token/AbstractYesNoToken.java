/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;

/**
 * Parses a token of the form: token:boolean
 * 
 * @param <T>
 *            The type of object on which this AbstractYesNoToken can be used
 */
public abstract class AbstractYesNoToken<T extends CDOMObject> extends AbstractNonEmptyToken<T>
{

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, T obj, String value)
	{
		return parseYesNoToObjectKey(context, obj, value, getTokenName(), getObjectKey());
	}

	public static ParseResult parseYesNoToObjectKey(LoadContext context,
		CDOMObject obj, String value, String tokenName,
		ObjectKey<Boolean> objectKey)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				return new ParseResult.Fail(
					"You should use 'YES' as the " + tokenName + ": " + value);
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				return new ParseResult.Fail(
					"You should use 'YES' or 'NO' as the " + tokenName + ": "
						+ value);
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				return new ParseResult.Fail(
					"You should use 'YES' or 'NO' as the " + tokenName + ": "
						+ value);
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(obj, objectKey, set);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, T obj)
	{
		Boolean b = context.getObjectContext().getObject(obj, getObjectKey());
		if (b == null)
		{
			return null;
		}
		return new String[]{b ? "YES" : "NO"};
	}

	protected abstract ObjectKey<Boolean> getObjectKey();
}
