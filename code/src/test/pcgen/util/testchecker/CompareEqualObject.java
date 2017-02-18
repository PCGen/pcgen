/**
 * CompareEqualObject.java
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
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
 *
 */

package pcgen.util.testchecker;

import java.lang.reflect.Array;
import pcgen.util.TestChecker;

/**
 * Compare Objects
 */
public class CompareEqualObject extends TestChecker
{
	private Object obj;

	/**
	 * Constructor
	 * @param obj
	 */
	public CompareEqualObject(Object obj)
	{
		this.obj = obj;
	}

    @Override
	public boolean check(Object object)
	{
		return areEqual(this.obj, object);
	}

    @Override
	public StringBuilder scribe(StringBuilder buf)
	{
		buf.append("eq(");
		if (this.obj == null)
		{
			buf.append("null");
		}
		else
		{
			buf.append("<");
			buf.append(this.obj);
			buf.append(">");
		}
		buf.append(")");
		return buf;
	}

	private static boolean areEqual(Object obj1, Object obj2)
	{
		if (obj1 == null || obj2 == null)
		{
			return obj1 == null && obj2 == null;
		}
		else if (isArray(obj1))
		{
			return isArray(obj2) && arrayEq(obj1, obj2);
		}
		else
		{
			return obj1.equals(obj2);
		}
	}

	private static boolean arrayEq(Object obj1, Object obj2)
	{
		return arrayLenEq(obj1, obj2) && arrayElemEq(obj1, obj2);
	}

	private static boolean arrayLenEq(Object obj1, Object obj2)
	{
		return Array.getLength(obj1) == Array.getLength(obj2);
	}

	private static boolean arrayElemEq(Object obj1, Object obj2)
	{
		for (int i = 0; i < Array.getLength(obj1); i++)
		{
			if (!areEqual(Array.get(obj1, i), Array.get(obj2, i)))
				return false;
		}
		return true;
	}

	private static boolean isArray(Object obj)
	{
		return obj.getClass().isArray();
	}
}
