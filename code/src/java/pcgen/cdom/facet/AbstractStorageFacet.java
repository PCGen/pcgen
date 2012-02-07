/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet;

import java.util.Set;

import pcgen.base.test.InequalityTester;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.CharID;
import pcgen.util.Logging;

public abstract class AbstractStorageFacet
{
	public abstract void copyContents(CharID source, CharID copy);

	private static final DoubleKeyMap<CharID, Class<?>, Object> CACHE =
			new DoubleKeyMap<CharID, Class<?>, Object>();

	public Object removeCache(CharID id, Class<?> cl)
	{
		return CACHE.remove(id, cl);
	}

	public Object setCache(CharID id, Class<?> cl, Object o)
	{
		return CACHE.put(id, cl, o);
	}

	public Object getCache(CharID id, Class<?> cl)
	{
		return CACHE.get(id, cl);
	}

	public static boolean areEqualCache(CharID id1, CharID id2,
		InequalityTester t)
	{
		Set<Class<?>> set1 = CACHE.getSecondaryKeySet(id1);
		Set<Class<?>> set2 = CACHE.getSecondaryKeySet(id2);
		if (!set1.equals(set2))
		{
			return false;
		}
		for (Class<?> cl : set1)
		{
			Object obj1 = CACHE.get(id1, cl);
			Object obj2 = CACHE.get(id2, cl);
			String equal = t.testEquality(obj1, obj2);
			if (equal != null)
			{
				Logging.errorPrint(equal);
				return false;
			}
		}
		return true;
	}

}
