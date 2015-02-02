/*
 * Copyright 2008 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.core.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;

public class ParsingSeparator implements Iterator<String>
{

	private final String startingString;
	private final StringTokenizer base;
	private final String sep;
	private boolean hasABlank = false;

	public ParsingSeparator(String baseString, char separator)
	{
		if (baseString == null)
		{
			throw new IllegalArgumentException(
					"Choose Separator cannot take null initialization String");
		}
		sep = Character.toString(separator);
		startingString = baseString;
		base = new StringTokenizer(baseString, "[]()" + sep, true);
	}

    @Override
	public boolean hasNext()
	{
		return hasABlank || base.hasMoreTokens();
	}

    @Override
	public String next()
	{
		if (!hasNext())
		{
			throw new NoSuchElementException();
		}
		if (hasABlank)
		{
			hasABlank = false;
			return Constants.EMPTY_STRING;
		}
		StringBuilder temp = new StringBuilder(startingString.length());
		Stack<String> expected = new Stack<String>();
		while (base.hasMoreTokens())
		{
			String working = base.nextToken();
			if (sep.equals(working) && expected.isEmpty())
			{
				hasABlank = !base.hasMoreTokens();
				return temp.toString();
			}
			else
			{
				temp.append(working);
				if ("(".equals(working))
				{
					expected.push(")");
				}
				else if ("[".equals(working))
				{
					expected.push("]");
				}
				else if (")".equals(working))
				{
					if (expected.isEmpty())
					{
						throw new GroupingMismatchException(startingString
								+ " did not have an open parenthesis "
								+ "before close: " + temp.toString());
					}
					else if (!")".equals(expected.pop()))
					{
						throw new GroupingMismatchException(startingString
								+ " did not have matching parenthesis "
								+ "inside of brackets: " + temp.toString());
					}
				}
				else if ("]".equals(working))
				{
					if (expected.isEmpty())
					{
						throw new GroupingMismatchException(startingString
								+ " did not have an open bracket "
								+ "before close: " + temp.toString());
					}
					else if (!"]".equals(expected.pop()))
					{
						throw new GroupingMismatchException(startingString
								+ " did not have matching brackets "
								+ "inside of parenthesis: " + temp.toString());
					}
				}
			}
		}
		if (expected.isEmpty())
		{
			return temp.toString();
		}
		throw new GroupingMismatchException(startingString
				+ " reached end of String while attempting to match: "
				+ expected.pop());
	}

    @Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public static class GroupingMismatchException extends IllegalStateException
	{

		public GroupingMismatchException(String base)
		{
			super(base);
		}

	}
}
