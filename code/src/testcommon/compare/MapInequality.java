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
import java.util.Map;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.base.test.InequalityTester;

public class MapInequality implements InequalityTest<Map<?, ?>>
{

    @Override
    public String testInequality(Map<?, ?> m1, Map<?, ?> m2, InequalityTester t, String location)
    {
        Set<?> k1 = m1.keySet();
        Set<?> k2 = m2.keySet();
        if (k1.size() != k2.size())
        {
            return "MI=@" + location + ": Inequality in Map Key Size: " + m1.keySet() + " " + m2.keySet();
        }
        Collection<String> reasons = new ArrayList<>();
        if (!k1.equals(k2))
        {
            Iterator<?> i2 = k2.iterator();
            boolean found = false;
            //This makes a "bold" assertion that keys will always be in the same order.
            //Don't expect this class to work for IdentityHashMap
            for (Object o : k1)
            {
                Object o2 = i2.next();
                String reason = t.testEquality(o, o2, location + "/K/");
                if (reason != null)
                {
                    found = true;
                    reasons.add(reason);
                }
            }
            if (found)
            {
                reasons.add("@MI=" + location + ": Inequality in Map Keys: "
                        + m1.keySet() + " " + m2.keySet() + " {" + m1.values().iterator().next().getClass() + "}");
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
            boolean found = false;
            //This makes a "bold" assertion that values (due to keys) will always be in the same order.
            //Don't expect this class to work for IdentityHashMap
            for (Object o : c1)
            {
                Object o2 = i2.next();
                String reason = t.testEquality(o, o2, location + "/v/");
                if (reason != null)
                {
                    found = true;
                    reasons.add(reason);
                }
            }
            if (found)
            {
                reasons.add("@MI=" + location + ": Inequality in Map Values: " + m1.values() + " " + m2.values() + " {"
                        + m1.values().iterator().next().getClass() + "}");
            }
        }
        return reasons.isEmpty() ? null : StringUtil.join(reasons, "\n");
    }

}
