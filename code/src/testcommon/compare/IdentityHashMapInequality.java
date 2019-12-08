/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
package compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.test.InequalityTester;
import pcgen.base.util.AbstractMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;

public class IdentityHashMapInequality implements
		InequalityTest<IdentityHashMap>
{

	@Override
	public String testInequality(IdentityHashMap m1, IdentityHashMap m2,
		InequalityTester t, String location)
	{
		Collection<String> reasons = new ArrayList<>();
		Set<?> k1 = m1.keySet();
		Set<?> k2 = m2.keySet();
		if (k1.size() != k2.size())
		{
			return "IMI=@" + location + ": Inequality in Map Key Size: "
				+ m1.keySet() + " " + m2.keySet();
		}
		AbstractMapToList<Integer, Integer> matches =
				new HashMapToList<>();
		if (!k1.equals(k2))
		{
			String result = processKeys(location, k1, k2, matches);
			if (result != null)
			{
				return result;
			}
		}
		if (!m1.values().equals(m2.values()))
		{
			if (matches.isEmpty())
			{
				String result = processKeys(location, k1, k2, matches);
				if (result != null)
				{
					return result;
				}
			}
			return processValues(location, m1.values(), m2.values(), matches);
		}
		return null;
	}

	private static String processKeys(String location, Iterable<?> k1, Iterable<?> k2,
	                                  MapToList<Integer, Integer> matches)
	{
		/*
		 * Walk through this establishing an "order"... Order needs to be kept
		 * and then passed to values...
		 */
		int i = 0;
		for (final Object key1 : k1)
		{
			int j = 0;
			for (final Object key2 : k2)
			{
				if (key1.equals(key2))
				{
					matches.addToListFor(i, j);
				}
				j++;
			}
			if (!matches.containsListFor(i))
			{
				return "@IMI" + location + ":k: " + key1 + " not found in map2";
			}
			i++;
		}
		return null;
	}

	private static String processValues(String location, Collection<?> v1,
	                                    Collection<?> v2, AbstractMapToList<Integer, Integer> potential)
	{
		MapToList<Integer, Integer> matches =
				new HashMapToList<>();
		List<Object> values1 = new ArrayList<>(v1);
		List<Object> values2 = new ArrayList<>(v2);
		for (final Integer loc1 : potential.getKeySet())
		{
			Object o1 = values1.get(loc1);
			for (final Integer loc2 : potential.getListFor(loc1))
			{
				Object o2 = values2.get(loc2);
				if (o1.equals(o2))
				{
					matches.addToListFor(loc1, loc2);
				}
			}
		}
		if (potential.equals(matches))
		{
			return null;
		}
		//If not then we have keys that are .equals but not ==  and different targets :/
		Collection<Integer> used = new ArrayList<>();
		for (final Integer m1 : matches.getKeySet())
		{
			if (matches.sizeOfListFor(m1) == 1)
			{
				Integer single = matches.getElementInList(m1, 0);
				if (used.contains(single))
				{
					return "@IMI" + location + ":v:Value found single match twice";
				}
				used.add(single);
			}
		}
		if (used.size() == matches.size())
		{
			//They all only matched once :)
			return null;
		}
		return "This is a 'development of IdentityHashMapInequality is not complete message";
	}

}
