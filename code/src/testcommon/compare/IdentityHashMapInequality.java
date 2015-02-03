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
import pcgen.base.util.HashMapToList;

public class IdentityHashMapInequality implements
		InequalityTest<IdentityHashMap>
{

	@Override
	public String testInequality(IdentityHashMap m1, IdentityHashMap m2,
		InequalityTester t, String location)
	{
		List<String> reasons = new ArrayList<String>();
		Set<?> k1 = m1.keySet();
		Set<?> k2 = m2.keySet();
		if (k1.size() != k2.size())
		{
			return "IMI=@" + location + ": Inequality in Map Key Size: "
				+ m1.keySet() + " " + m2.keySet();
		}
		HashMapToList<Integer, Integer> matches =
				new HashMapToList<Integer, Integer>();
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
		return reasons.isEmpty() ? null : StringUtil.join(reasons, "\n");
	}

	protected String processKeys(String location, Set<?> k1, Set<?> k2,
		HashMapToList<Integer, Integer> matches)
	{
		/*
		 * Walk through this establishing an "order"... Order needs to be kept
		 * and then passed to values...
		 */
		int i = 0;
		for (Object key1 : k1)
		{
			int j = 0;
			for (Object key2 : k2)
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

	protected String processValues(String location, Collection<?> v1,
		Collection<?> v2, HashMapToList<Integer, Integer> potential)
	{
		HashMapToList<Integer, Integer> matches =
				new HashMapToList<Integer, Integer>();
		ArrayList<Object> values1 = new ArrayList<Object>(v1);
		ArrayList<Object> values2 = new ArrayList<Object>(v2);
		for (Integer loc1 : potential.getKeySet())
		{
			Object o1 = values1.get(loc1);
			for (Integer loc2 : potential.getListFor(loc1))
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
		List<Integer> used = new ArrayList<Integer>();
		for (Integer m1 : matches.getKeySet())
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
