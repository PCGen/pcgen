/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package selectionactor.testsupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.test.InequalityTester;
import pcgen.core.PlayerCharacter;

public final class InequalityTesterInst implements InequalityTester
{

	public static InequalityTester instance;

	public static Map<Class<?>, InequalityTest> INEQ_MAP = new HashMap<Class<?>, InequalityTest>();

	static
	{
		INEQ_MAP.put(Map.class, new MapInequality());
		INEQ_MAP.put(PlayerCharacter.class, new PlayerCharacterInequality());
	}

    @Override
	public String testEquality(Object o1, Object o2)
	{
		List<String> reasons = new ArrayList<String>();
		if (o1 == null)
		{
			if (o2 == null)
			{
				return null;
			}
			else
			{
				return "o1 is null, o2 is a "
						+ o2.getClass().getCanonicalName();
			}
		}
		if (o2 == null)
		{
			return "o1 is a " + o1.getClass().getCanonicalName()
					+ ", o2 is null";
		}
		Class<?> c1 = o1.getClass();
		Class<?> c2 = o2.getClass();
		if (c1.equals(c2))
		{
			if (INEQ_MAP.containsKey(c1))
			{
				return runTest(c1, o1, o2);
			}
			else
			{
				if (o1.equals(o2))
				{
					return null;
				}
				reasons
						.add(c1.getCanonicalName() + " objects not equal: "
								+ o1);
			}
		}
		else
		{
			reasons.add(c1.getClass() + " not same class as " + c2.getClass());
		}
		Class<?>[] if1array = c1.getInterfaces();
		Class<?>[] if2array = c2.getInterfaces();
		for (Class<?> if1 : if1array)
		{
			for (Class<?> if2 : if2array)
			{
				if (if1.equals(if2) && INEQ_MAP.containsKey(if1))
				{
					String rt = runTest(if1, o1, o2);
					if (rt == null)
					{
						return null;
					}
					else
					{
						reasons.add(rt);
					}
				}
			}
		}
		return reasons.isEmpty() ? null : reasons.toString();
	}

	private <T> String runTest(Class<T> c1, Object o1, Object o2)
	{
		return INEQ_MAP.get(c1).testInequality((T) o1, (T) o2, this);
	}

	public static synchronized InequalityTester getInstance()
	{
		if (instance == null)
		{
			instance = new InequalityTesterInst();
		}
		return instance;
	}
}
