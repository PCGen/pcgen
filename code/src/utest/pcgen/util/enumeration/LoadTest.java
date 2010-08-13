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

import junit.framework.TestCase;

import org.junit.Test;

public class LoadTest extends TestCase
{

	private final Load light = Load.valueOf("LIGHT");
	private final Load medium = Load.valueOf("MEDIUM");
	private final Load heavy = Load.valueOf("HEAVY");
	private final Load overload = Load.valueOf("OVERLOAD");

	@Test
	public void testLoadOrder()
	{
		assertTrue(light.compareTo(light) == 0);
		assertTrue(light.compareTo(medium) < 0);
		assertTrue(light.compareTo(heavy) < 0);
		assertTrue(light.compareTo(overload) < 0);
		assertTrue(medium.compareTo(light) > 0);
		assertTrue(medium.compareTo(medium) == 0);
		assertTrue(medium.compareTo(heavy) < 0);
		assertTrue(medium.compareTo(overload) < 0);
		assertTrue(heavy.compareTo(light) > 0);
		assertTrue(heavy.compareTo(medium) > 0);
		assertTrue(heavy.compareTo(heavy) == 0);
		assertTrue(heavy.compareTo(overload) < 0);
		assertTrue(overload.compareTo(light) > 0);
		assertTrue(overload.compareTo(medium) > 0);
		assertTrue(overload.compareTo(heavy) > 0);
		assertTrue(overload.compareTo(overload) == 0);
	}
}
