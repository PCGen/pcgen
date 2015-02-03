/*
 * Copyright (c) 2010-13 Tom Parker <thpr@users.sourceforge.net>
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
package compare;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.test.InequalityTester;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpellSupportForPCClass;

public final class InequalityTesterInst implements InequalityTester
{

	public static InequalityTester instance;

	public static Map<Class<?>, InequalityTest> INEQ_MAP =
			new HashMap<Class<?>, InequalityTest>();

	static
	{
		INEQ_MAP.put(Collection.class, new CollectionInequality());
		INEQ_MAP.put(Map.class, new MapInequality());
		INEQ_MAP.put(WeakReference.class, new WeakReferenceInequality());
		INEQ_MAP.put(IdentityHashMap.class, new IdentityHashMapInequality());
		INEQ_MAP.put(pcgen.cdom.facet.model.ClassFacet.ClassInfo.class, new ClassFacetInfoInequality());
		INEQ_MAP.put(PlayerCharacter.class, new IgnoreInequality());
		INEQ_MAP.put(SpellSupportForPCClass.class, new IgnoreInequality());
	}

	@Override
	public String testEquality(Object o1, Object o2, String location)
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
				return "@" + location + ": o1 is null, o2 is a "
					+ o2.getClass().getCanonicalName();
			}
		}
		if (o2 == null)
		{
			return "@IT=" + location + "o1 is a "
				+ o1.getClass().getCanonicalName() + ", o2 is null";
		}
		Class<?> c1 = o1.getClass();
		Class<?> c2 = o2.getClass();
		if (c1.equals(c2))
		{
			if (INEQ_MAP.containsKey(c1))
			{
				return runTest(c1, o1, o2, location + "/" + c1);
			}
			else
			{
				if (o1.equals(o2))
				{
					return null;
				}
				reasons.add("@IT=" + location + "/" + c1.getCanonicalName()
					+ " objects not equal: " + o1 + " " + o2);
			}
		}
		else
		{
			reasons.add("@IT=" + location + "/" + c1 + " not same class as "
				+ c2);
		}
		Set<Class<?>> ifs1 = getInterfaces(c1);
		Set<Class<?>> ifs2 = getInterfaces(c2);
		for (Class<?> if1 : ifs1)
		{
			for (Class<?> if2 : ifs2)
			{
				if (if1.equals(if2) && INEQ_MAP.containsKey(if1))
				{
					String rt =
							runTest(if1, o1, o2, location + "/" + c1 + " as "
								+ if1);
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
		return reasons.isEmpty() ? null : StringUtil.join(reasons, "\n");
	}

	private Set<Class<?>> getInterfaces(Class<?> c1)
	{
		HashSet<Class<?>> if1 = new HashSet<Class<?>>();
		if1.addAll(Arrays.asList(c1.getInterfaces()));
		Class<?> sc = c1.getSuperclass();
		if (sc != null)
		{
			if1.addAll(getInterfaces(sc));
		}
		return if1;
	}

	@SuppressWarnings("unchecked")
	private <T> String runTest(Class<T> c1, Object o1, Object o2, String context)
	{
		return INEQ_MAP.get(c1).testInequality(o1, o2, this, context);
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
