/*
 * Copyright (c) 2010-13 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.StringUtil;
import pcgen.base.test.InequalityTester;

public class MapInequality implements InequalityTest<Map<?, ?>>
{

    @Override
	public String testInequality(Map<?, ?> m1, Map<?, ?> m2, InequalityTester t, String location)
	{
		List<String> reasons = new ArrayList<String>();
		if (m1.keySet().size() != m2.keySet().size())
		{
			return "MI=@" + location + ": Inequality in Map Key Size: " + m1.keySet() + " " + m2.keySet();
		}
		if (!m1.keySet().equals(m2.keySet()))
		{
			Iterator<?> i2 = m2.keySet().iterator();
			boolean found = false;
			for (Object o : m1.keySet())
			{
				Object o2 = i2.next();
				String reason = t.testEquality(o, o2, location + "/k/" + o.getClass());
				if (reason != null)
				{
					found = true;
					reasons.add(reason);
				}
			}
			if (found)
			{
				reasons.add("@MI=" + location + ": Inequality in Map Keys: " + m1.keySet() + " " + m2.keySet());
			}
		}
		if (!m1.values().equals(m2.values()))
		{
			Collection<?> c1 = m1.values();
			Collection<?> c2 = m2.values();
			if (c1.size() != c2.size())
			{
				reasons.add("@MI=" + location + ": Inequality in Value Size: " + c1.size() + " " + c2.size());
			}
			Iterator<?> i2 = c2.iterator();
			String potentialProb = null;
			for (Object o : c1)
			{
				Object o2 = i2.next();
				String reason = t.testEquality(o, o2, location + "/v/" + o.getClass());
				if (reason != null)
				{
					potentialProb = reason;
				}
			}
			if (potentialProb != null)
			{
				//Collection may be smarter at this than we are...
				String reason = t.testEquality(c1, c2, location + "/vc/" + c1.getClass());
				if (reason == null)
				{
					//ok!
					potentialProb = null;
				}
				else
				{
					reasons.add(potentialProb);
					reasons.add(reason);
				}
			}
			if (potentialProb != null)
			{
				reasons.add("@MI=" + location + ": Inequality in Map Values: " + m1.values() + " " + m2.values() + " {" + m1.values().iterator().next().getClass() + "}");
			}
		}
		return reasons.isEmpty() ? null : StringUtil.join(reasons, "\n");
	}

}
