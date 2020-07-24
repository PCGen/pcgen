/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class DefaultMapTest
{

	public void populate(DefaultMap<Object, Double> dm)
	{
		dm.put(TestSupport.I0, TestSupport.D0);
		dm.put("ONE", TestSupport.D1);
		dm.put("tWo", TestSupport.D0);
		dm.put("This is Three!", TestSupport.D1);
		dm.put("null result", null);
		dm.put(null, Double.valueOf(-1));
	}

	@Test
	public void testGetDefault()
	{
		DefaultMap<Object, Double> dm = new DefaultMap<>();
		assertNull(dm.getDefaultValue());
		dm.setDefaultValue(Double.valueOf(5.4));
		assertEquals(Double.valueOf(5.4), dm.getDefaultValue());
	}

	@Test
	public void testPutGetExact()
	{
		DefaultMap<Object, Double> dm = new DefaultMap<>();
		populate(dm);
		assertEquals(TestSupport.D0, dm.get(TestSupport.I0));
		assertEquals(TestSupport.D1, dm.get("ONE"));
		assertEquals(TestSupport.D0, dm.get("tWo"));
		assertEquals(TestSupport.D1, dm.get("This is Three!"));
		assertNull(dm.get("null result"));
		assertEquals(Double.valueOf(-1), dm.get(null));
		assertNull(dm.get("wasnotakey"));
		dm.setDefaultValue(Double.valueOf(5.4));
		assertEquals(Double.valueOf(5.4), dm.get("wasnotakey"));
		//Make sure nulls were not polluted by default
		assertNull(dm.get("null result"));
		assertEquals(Double.valueOf(-1), dm.get(null));
	}

	@Test
	public void testContainsKey()
	{
		DefaultMap<Object, Double> dm = new DefaultMap<>();
		populate(dm);
		dm.setDefaultValue(Double.valueOf(5.4));
		assertTrue(dm.containsKey(TestSupport.I0));
		assertTrue(dm.containsKey("ONE"));
		assertTrue(dm.containsKey("tWo"));
		assertTrue(dm.containsKey("This is Three!"));
		assertTrue(dm.containsKey("null result"));
		assertTrue(dm.containsKey(null));
		assertFalse(dm.containsKey("wasnotakey"));
	}

	@Test
	public void testRemoveRestoreDefault()
	{
		DefaultMap<Object, Double> dm = new DefaultMap<>();
		populate(dm);
		dm.setDefaultValue(Double.valueOf(5.4));
		assertEquals(TestSupport.D0, dm.get(TestSupport.I0));
		assertEquals(TestSupport.D1, dm.get("ONE"));
		assertEquals(TestSupport.D0, dm.get("tWo"));
		assertEquals(TestSupport.D1, dm.get("This is Three!"));
		assertNull(dm.get("null result"));
		assertEquals(Double.valueOf(-1), dm.get(null));

		assertEquals(TestSupport.D0, dm.remove(TestSupport.I0));
		assertEquals(TestSupport.D1, dm.remove("ONE"));
		assertEquals(TestSupport.D0, dm.remove("tWo"));
		assertEquals(TestSupport.D1, dm.remove("This is Three!"));
		assertNull(dm.remove("null result"));
		assertEquals(Double.valueOf(-1), dm.remove(null));

		assertEquals(Double.valueOf(5.4), dm.get(TestSupport.I0));
		assertEquals(Double.valueOf(5.4), dm.get("ONE"));
		assertEquals(Double.valueOf(5.4), dm.get("tWo"));
		assertEquals(Double.valueOf(5.4), dm.get("This is Three!"));
		assertEquals(Double.valueOf(5.4), dm.get("null result"));
		assertEquals(Double.valueOf(5.4), dm.get(null));
	}
}
