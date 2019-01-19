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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;

public class LoadTest
{

	private final Load light = Load.valueOf("LIGHT");
	private final Load medium = Load.valueOf("MEDIUM");
	private final Load heavy = Load.valueOf("HEAVY");
	private final Load overload = Load.valueOf("OVERLOAD");

	@Test
	public void testLoadOrder()
	{
		assertEquals(0, light.compareTo(light));
		assertTrue(light.compareTo(medium) < 0);
		assertTrue(light.compareTo(heavy) < 0);
		assertTrue(light.compareTo(overload) < 0);
		assertTrue(medium.compareTo(light) > 0);
		assertEquals(0, medium.compareTo(medium));
		assertTrue(medium.compareTo(heavy) < 0);
		assertTrue(medium.compareTo(overload) < 0);
		assertTrue(heavy.compareTo(light) > 0);
		assertTrue(heavy.compareTo(medium) > 0);
		assertEquals(0, heavy.compareTo(heavy));
		assertTrue(heavy.compareTo(overload) < 0);
		assertTrue(overload.compareTo(light) > 0);
		assertTrue(overload.compareTo(medium) > 0);
		assertTrue(overload.compareTo(heavy) > 0);
		assertEquals(0, overload.compareTo(overload));
	}
}
