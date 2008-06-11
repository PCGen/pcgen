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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class MapCollectionTest extends TestCase {

	public MapCollection mc;

	public Map<Integer, Double> m;

	@Override
	@Before
	public void setUp() {
		m = new HashMap<Integer, Double>();
		m.put(Integer.valueOf(1), Double.valueOf(1));
		m.put(Integer.valueOf(2), Double.valueOf(2));
		m.put(Integer.valueOf(3), null);
		m.put(null, Double.valueOf(4));
		m.put(Integer.valueOf(5), Double.valueOf(-1));
		mc = new MapCollection(m);
	}

	@Test
	public void testBadConstructor() {
		try {
			new MapCollection(null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
	}

	@Test
	public void testClear() {
		try {
			mc.clear();
			fail();
		} catch (UnsupportedOperationException uoe) {
			// OK
		}
	}

	@Test
	public void testIsEmpty() {
		assertTrue(new MapCollection(new HashMap<Integer, Double>()).isEmpty());
		assertFalse(mc.isEmpty());
		assertFalse(m.isEmpty());
		assertTrue(mc.contains(Integer.valueOf(1)));
		assertTrue(mc.contains(Integer.valueOf(2)));
		assertTrue(mc.contains(Integer.valueOf(3)));
		assertTrue(mc.contains(Double.valueOf(1)));
		assertTrue(mc.contains(Double.valueOf(2)));
		assertTrue(mc.contains(Double.valueOf(4)));
		assertTrue(mc.contains(null));
		assertFalse(mc.contains(Double.valueOf(5)));
		// Should impact the MapCollection (since it is a facade)
		m.clear();
		assertTrue(mc.isEmpty());
	}

	@Test
	public void testSize() {
		assertEquals(0, new MapCollection(new HashMap<Integer, Double>()).size());
		assertEquals(10, mc.size());
	}

	@Test
	public void testRemove() {
		try {
			mc.remove(Integer.valueOf(1));
			fail();
		} catch (UnsupportedOperationException uoe) {
			// OK
		}
	}

	@Test
	public void testAdd() {
		try {
			mc.add(Integer.valueOf(1));
			fail();
		} catch (UnsupportedOperationException uoe) {
			// OK
		}
	}

	@Test
	public void testAddAll() {
		try {
			mc.addAll(Arrays.asList(Integer.valueOf(1)));
			fail();
		} catch (UnsupportedOperationException uoe) {
			// OK
		}
	}

	@Test
	public void testEmptyIterator() {
		Iterator it = new MapCollection(new HashMap<Integer, Double>()).iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}

	@Test
	public void testIteratorRemove() {
		Iterator it = mc.iterator();
		try {
			it.remove();
			fail();
		} catch (UnsupportedOperationException uoe) {
			// OK because it might just not be legal
		} catch (IllegalStateException ise) {
			// OK because if it is made legal, it still requires .next() first!
		}
	}

	@Test
	public void testHasNextIterator() {
		Iterator it = mc.iterator();
		for (int i = 0; i < mc.size() / 2; i++) {
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			Object o = it.next();
			//Multiple times should be innocent
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			Object sub = it.next();
			if (o == null) {
				assertTrue(sub.equals(Double.valueOf(4)));
			} else if (o.equals(1)) {
				assertTrue(sub.equals(Double.valueOf(1)));
			} else if (o.equals(2)) {
				assertTrue(sub.equals(Double.valueOf(2)));
			} else if (o.equals(3)) {
				assertTrue(sub == null);
			} else if (o.equals(5)) {
				assertTrue(sub.equals(Double.valueOf(-1)));
			}
		}
		assertFalse(it.hasNext());
	}

	@Test
	public void testIterator() {
		Iterator it = mc.iterator();
		for (int i = 0; i < mc.size() / 2; i++) {
			Object o = it.next();
			Object sub = it.next();
			if (o == null) {
				assertTrue(sub.equals(Double.valueOf(4)));
			} else if (o.equals(1)) {
				assertTrue(sub.equals(Double.valueOf(1)));
			} else if (o.equals(2)) {
				assertTrue(sub.equals(Double.valueOf(2)));
			} else if (o.equals(3)) {
				assertTrue(sub == null);
			} else if (o.equals(5)) {
				assertTrue(sub.equals(Double.valueOf(-1)));
			}
		}
		try {
			it.next();
			fail();
		} catch (NoSuchElementException nsee) {
			// OK
		}
	}
}
