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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class CaseInsensitiveMapTest
{

	public void populate(CaseInsensitiveMap<Double> cim)
	{
		cim.put(TestSupport.I0, TestSupport.D0);
		cim.put("ONE", TestSupport.D1);
		cim.put("tWo", TestSupport.D0);
		cim.put("This is Three!", TestSupport.D1);
		cim.put("null result", null);
		cim.put(null, Double.valueOf(-1));
	}

	@Test
	public void testPutGetExact()
	{
		CaseInsensitiveMap<Double> cim = new CaseInsensitiveMap<>();
		populate(cim);
		assertEquals(TestSupport.D0, cim.get(TestSupport.I0));
		assertEquals(TestSupport.D1, cim.get("ONE"));
		assertEquals(TestSupport.D0, cim.get("tWo"));
		assertEquals(TestSupport.D1, cim.get("This is Three!"));
		assertNull(cim.get("null result"));
		assertEquals(Double.valueOf(-1), cim.get(null));
	}

	@Test
	public void testPutGetDiffCase()
	{
		CaseInsensitiveMap<Double> cim = new CaseInsensitiveMap<>();
		populate(cim);
		assertEquals(TestSupport.D0, cim.get(TestSupport.I0));
		assertEquals(TestSupport.D1, cim.get("one"));
		assertEquals(TestSupport.D0, cim.get("TWO"));
		assertEquals(TestSupport.D1, cim.get("This is three!"));
		assertNull(cim.get("NULL RESULT"));
		assertEquals(Double.valueOf(-1), cim.get(null));
	}

	@Test
	public void testContainsKey()
	{
		CaseInsensitiveMap<Double> cim = new CaseInsensitiveMap<>();
		populate(cim);
		assertTrue(cim.containsKey(TestSupport.I0));
		assertTrue(cim.containsKey("one"));
		assertTrue(cim.containsKey("TWO"));
		assertTrue(cim.containsKey("This is three!"));
		assertTrue(cim.containsKey("NULL RESULT"));
		assertTrue(cim.containsKey(null));
	}

	@Test
	public void testRemove()
	{
		CaseInsensitiveMap<Double> cim = new CaseInsensitiveMap<>();
		populate(cim);
		assertEquals(TestSupport.D0, cim.get(TestSupport.I0));
		assertEquals(TestSupport.D1, cim.get("OnE"));
		assertEquals(TestSupport.D0, cim.get("two"));
		assertEquals(TestSupport.D1, cim.get("This IS three!"));
		assertNull(cim.get("NULL result"));
		assertEquals(Double.valueOf(-1), cim.get(null));

		assertEquals(TestSupport.D0, cim.remove(TestSupport.I0));
		assertEquals(TestSupport.D1, cim.remove("one"));
		assertEquals(TestSupport.D0, cim.remove("TWO"));
		assertEquals(TestSupport.D1, cim.remove("This is three!"));
		assertNull(cim.remove("NULL RESULT"));
		assertEquals(Double.valueOf(-1), cim.remove(null));

		assertNull(cim.get(TestSupport.I0));
		assertNull(cim.get("one"));
		assertNull(cim.get("TWO"));
		assertNull(cim.get("This is three!"));
		assertNull(cim.get("NULL RESULT"));
		assertNull(cim.get(null));
	}
}
