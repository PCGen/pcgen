/*
 * Copyright (c) 2009 Mark Jeffries <motorviper@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.rules.context.LoadContext;

/**
 * Parses a token of the form: &lt;Token Name&gt;:&lt;int&gt;
 * 
 * @param <T>
 *            The type of object on which this AbstractIntToken can be used
 */
public abstract class AbstractIntToken<T extends CDOMObject>
{
	/**
	 * Override this to set a maximum value.
	 */
	protected int maxValue()
	{
		return Integer.MAX_VALUE;
	}

	/**
	 * Override this to set a minimum value.
	 */
	protected int minValue()
	{
		return Integer.MIN_VALUE;
	}

	/**
	 * Checks that the value is in the correct range.
	 * @param value The value to check.
	 * @return The result of the check.
	 */
	protected ParseResult checkValue(Integer value)
	{
		int max = maxValue();
		int min = minValue();
		if (max == Integer.MAX_VALUE)
		{
			if (value < min)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer >= " + min);
			}
		}
		else if (min == Integer.MIN_VALUE)
		{
			if (value > max)
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer <= " + max);
			}
		}
		else
		{
			if ((value > max) || (value < min))
			{
				return new ParseResult.Fail(getTokenName() + " must be an integer betwen " + min + " and " + max);
			}
		}
		return ParseResult.SUCCESS;
	}

	protected abstract String getTokenName();

	/**
	 * This must be overridden to specify the key.
	 * @return The key.
	 */
	protected abstract IntegerKey integerKey();

	public ParseResult parseToken(LoadContext context, T obj, String value)
	{
		try
		{
			Integer intValue = Integer.valueOf(value);
			ParseResult pr = checkValue(intValue);
			if (!pr.passed())
			{
				return pr;
			}
			context.getObjectContext().put(obj, integerKey(), intValue);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(
				getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
		}
	}

	public String[] unparse(LoadContext context, T obj)
	{
		Integer value = context.getObjectContext().getInteger(obj, integerKey());
		if (value == null)
		{
			return null;
		}
		ParseResult checkValue = checkValue(value);
		if (!checkValue.passed())
		{
			context.addWriteMessage(checkValue.toString());
			return null;
		}
		return new String[]{value.toString()};
	}
}
