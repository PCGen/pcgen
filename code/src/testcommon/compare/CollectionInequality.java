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

import pcgen.base.test.InequalityTester;

public class CollectionInequality implements InequalityTest<Collection<?>>
{

    @Override
    public String testInequality(Collection<?> s1, Collection<?> s2, InequalityTester t, String location)
    {
        if (s1.size() != s2.size())
        {
            return "@CI=" + location + ": Inequality in Set Size: " + s1 + " " + s2;
        }
        if (s1.equals(s2))
        {
            return null;
        }
        Collection<Object> l1 = new ArrayList<>(s1);
        Collection<Object> l2 = new ArrayList<>(s2);
        l1.removeAll(l2);
        if (l1.isEmpty())
        {
            return null;
        }
        l2.removeAll(new ArrayList<Object>(s1));
        return "@CI=" + location + ": " + s1.getClass() + " Inequality between: " + l1 + " " + l2;
    }

}
