/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class CaseInsensitiveMapTest extends TestCase
{

	CaseInsensitiveMap<Double> cim;

	@Override
	@Before
	public void setUp()
	{
		cim = new CaseInsensitiveMap<Double>();
	}

	public void populate()
	{
		cim.put(Integer.valueOf(0), Double.valueOf(0));
		cim.put("ONE", Double.valueOf(1));
		cim.put("tWo", Double.valueOf(0));
		cim.put("This is Three!", Double.valueOf(1));
		cim.put("null result", null);
		cim.put(null, Double.valueOf(-1));
	}

	@Test
	public void testPutGetExact()
	{
		populate();
		assertEquals(Double.valueOf(0), cim.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), cim.get("ONE"));
		assertEquals(Double.valueOf(0), cim.get("tWo"));
		assertEquals(Double.valueOf(1), cim.get("This is Three!"));
		assertNull(cim.get("null result"));
		assertEquals(Double.valueOf(-1), cim.get(null));
	}

	@Test
	public void testPutGetDiffCase()
	{
		populate();
		assertEquals(Double.valueOf(0), cim.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), cim.get("one"));
		assertEquals(Double.valueOf(0), cim.get("TWO"));
		assertEquals(Double.valueOf(1), cim.get("This is three!"));
		assertNull(cim.get("NULL RESULT"));
		assertEquals(Double.valueOf(-1), cim.get(null));
	}

	@Test
	public void testContainsKey()
	{
		populate();
		assertTrue(cim.containsKey(Integer.valueOf(0)));
		assertTrue(cim.containsKey("one"));
		assertTrue(cim.containsKey("TWO"));
		assertTrue(cim.containsKey("This is three!"));
		assertTrue(cim.containsKey("NULL RESULT"));
		assertTrue(cim.containsKey(null));
	}

	@Test
	public void testRemove()
	{
		populate();
		assertEquals(Double.valueOf(0), cim.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), cim.get("OnE"));
		assertEquals(Double.valueOf(0), cim.get("two"));
		assertEquals(Double.valueOf(1), cim.get("This IS three!"));
		assertNull(cim.get("NULL result"));
		assertEquals(Double.valueOf(-1), cim.get(null));

		assertEquals(Double.valueOf(0), cim.remove(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), cim.remove("one"));
		assertEquals(Double.valueOf(0), cim.remove("TWO"));
		assertEquals(Double.valueOf(1), cim.remove("This is three!"));
		assertNull(cim.remove("NULL RESULT"));
		assertEquals(Double.valueOf(-1), cim.remove(null));

		assertNull(cim.get(Integer.valueOf(0)));
		assertNull(cim.get("one"));
		assertNull(cim.get("TWO"));
		assertNull(cim.get("This is three!"));
		assertNull(cim.get("NULL RESULT"));
		assertNull(cim.get(null));
	}
}
