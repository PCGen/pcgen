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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class DefaultMapTest extends TestCase
{

	private DefaultMap<Object, Double> dm;

	@Override
	@Before
	public void setUp()
	{
		dm = new DefaultMap<>();
	}

	public void populate()
	{
		dm.put(Integer.valueOf(0), Double.valueOf(0));
		dm.put("ONE", Double.valueOf(1));
		dm.put("tWo", Double.valueOf(0));
		dm.put("This is Three!", Double.valueOf(1));
		dm.put("null result", null);
		dm.put(null, Double.valueOf(-1));
	}

	@Test
	public void testGetDefault()
	{
		assertNull(dm.getDefaultValue());
		dm.setDefaultValue(Double.valueOf(5.4));
		assertEquals(Double.valueOf(5.4), dm.getDefaultValue());
	}

	@Test
	public void testPutGetExact()
	{
		populate();
		assertEquals(Double.valueOf(0), dm.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), dm.get("ONE"));
		assertEquals(Double.valueOf(0), dm.get("tWo"));
		assertEquals(Double.valueOf(1), dm.get("This is Three!"));
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
		populate();
		dm.setDefaultValue(Double.valueOf(5.4));
		assertTrue(dm.containsKey(Integer.valueOf(0)));
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
		populate();
		dm.setDefaultValue(Double.valueOf(5.4));
		assertEquals(Double.valueOf(0), dm.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), dm.get("ONE"));
		assertEquals(Double.valueOf(0), dm.get("tWo"));
		assertEquals(Double.valueOf(1), dm.get("This is Three!"));
		assertNull(dm.get("null result"));
		assertEquals(Double.valueOf(-1), dm.get(null));

		assertEquals(Double.valueOf(0), dm.remove(Integer.valueOf(0)));
		assertEquals(Double.valueOf(1), dm.remove("ONE"));
		assertEquals(Double.valueOf(0), dm.remove("tWo"));
		assertEquals(Double.valueOf(1), dm.remove("This is Three!"));
		assertNull(dm.remove("null result"));
		assertEquals(Double.valueOf(-1), dm.remove(null));

		assertEquals(Double.valueOf(5.4), dm.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(5.4), dm.get("ONE"));
		assertEquals(Double.valueOf(5.4), dm.get("tWo"));
		assertEquals(Double.valueOf(5.4), dm.get("This is Three!"));
		assertEquals(Double.valueOf(5.4), dm.get("null result"));
		assertEquals(Double.valueOf(5.4), dm.get(null));
	}
}
