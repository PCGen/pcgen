/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.util.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoadTest
{

    private final Load light = Load.valueOf("LIGHT");
    private final Load medium = Load.valueOf("MEDIUM");
    private final Load heavy = Load.valueOf("HEAVY");
    private final Load overload = Load.valueOf("OVERLOAD");

    @Test
    public void testLoadOrder()
    {
        assertEquals(0, light.compareTo(light));
        Assertions.assertTrue(light.compareTo(medium) < 0);
        Assertions.assertTrue(light.compareTo(heavy) < 0);
        Assertions.assertTrue(light.compareTo(overload) < 0);
        Assertions.assertTrue(medium.compareTo(light) > 0);
        assertEquals(0, medium.compareTo(medium));
        Assertions.assertTrue(medium.compareTo(heavy) < 0);
        Assertions.assertTrue(medium.compareTo(overload) < 0);
        Assertions.assertTrue(heavy.compareTo(light) > 0);
        Assertions.assertTrue(heavy.compareTo(medium) > 0);
        assertEquals(0, heavy.compareTo(heavy));
        Assertions.assertTrue(heavy.compareTo(overload) < 0);
        Assertions.assertTrue(overload.compareTo(light) > 0);
        Assertions.assertTrue(overload.compareTo(medium) > 0);
        Assertions.assertTrue(overload.compareTo(heavy) > 0);
        assertEquals(0, overload.compareTo(overload));
    }
}
