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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class WeightedCollectionTest extends TestCase {

	private static final Integer I3 = Integer.valueOf(3);

	private static final Integer I2 = Integer.valueOf(2);

	private static final Integer I1 = Integer.valueOf(1);

	WeightedCollection<Integer> wc;

	@Override
	@Before
	public void setUp() {
		wc = new WeightedCollection<Integer>();
	}

	@Test
	public void testBadIntConstructor() {
		try {
			new WeightedCollection<Integer>(-5);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
	}

	@Test
	public void testBadCollectionConstructor() {
		try {
			new WeightedCollection<Integer>((Collection<Integer>) null);
			fail();
		} catch (NullPointerException npe) {
			// OK
		} catch (IllegalArgumentException iae) {
			// OK
		}
	}

	@Test
	public void testCollectionConstructorSemantics() {
		Collection<Integer> c = new ArrayList<Integer>();
		assertTrue(c.add(I1));
		assertTrue(c.add(I2));
		assertTrue(c.add(null));
		WeightedCollection<Integer> col = new WeightedCollection<Integer>(c);
		assertEquals(3, col.size());
		c.add(Integer.valueOf(4));
		assertEquals(3, col.size());
		col.clear();
		assertEquals(4, c.size());
	}

	@Test
	public void testSize() {
		assertTrue(wc.add(I1));
		assertEquals(1, wc.size());
		assertTrue(wc.add(I1));
		assertEquals(2, wc.size());
		assertTrue(wc.add(I2));
		assertEquals(3, wc.size());
		assertTrue(wc.add(I3));
		assertEquals(4, wc.size());
		assertTrue(wc.add(null));
		assertEquals(5, wc.size());
		assertTrue(wc.add(null));
		assertEquals(6, wc.size());
		assertTrue(wc.addAll(Arrays.asList(3, 4, 5, 6)));
		assertEquals(10, wc.size());
		assertTrue(wc.add(Integer.valueOf(7), 3));
		assertEquals(13, wc.size());
		assertTrue(wc.add(Integer.valueOf(7), 3));
		assertEquals(16, wc.size());
		assertTrue(wc.addAll(Arrays.asList(3, 4, 5, 6), 2));
		assertEquals(24, wc.size());
		assertTrue(wc.remove(Integer.valueOf(7)));
		assertEquals(18, wc.size());
		assertFalse(wc.remove(Integer.valueOf(7)));
		assertEquals(18, wc.size());
	}

	@Test
	public void testBadAddNegative() {
		try {
			wc.add(Integer.valueOf(4), -3);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
	}

	@Test
	public void testBadAddZero() {
		assertFalse(wc.add(Integer.valueOf(4), 0));
	}

	@Test
	public void testBadAddAllNegative() {
		try {
			wc.addAll(Arrays.asList(3, 4, 5), -3);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
	}

	@Test
	public void testBadAddAllZero() {
		assertFalse(wc.addAll(Arrays.asList(3, 4, 5), 0));
	}

	@Test
	public void testSimple() {
		assertTrue(wc.isEmpty());
		assertFalse(wc.contains(I1));
		assertTrue(wc.add(I1));
		assertFalse(wc.isEmpty());
		assertTrue(wc.contains(I1));
		assertTrue(wc.contains(new Integer(1))); // value semantic
		assertFalse(wc.contains(I2));
		assertEquals(1, wc.size());
		assertTrue(wc.add(I1));
		assertTrue(wc.contains(I1));
		assertEquals(2, wc.size());
		assertFalse(wc.contains(I2));
		assertTrue(wc.add(I2));
		assertTrue(wc.contains(I2));
		assertEquals(3, wc.size());
		assertTrue(wc.add(I3));
		assertEquals(4, wc.size());
		assertFalse(wc.contains(null));
		assertTrue(wc.add(null));
		assertTrue(wc.contains(null));
		assertEquals(5, wc.size());
		assertTrue(wc.add(null));
		assertEquals(6, wc.size());
		assertFalse(wc.contains(4));
		assertFalse(wc.contains(5));
		assertFalse(wc.contains(6));
		assertFalse(wc.contains(7));
		assertTrue(wc.addAll(Arrays.asList(3, null, 5, 6)));
		assertEquals(10, wc.size());
		assertFalse(wc.contains(4));
		assertTrue(wc.contains(5));
		assertTrue(wc.contains(6));
		assertFalse(wc.contains(7));
		assertTrue(wc.add(Integer.valueOf(7), 3));
		assertEquals(13, wc.size());
		assertFalse(wc.contains(4));
		assertTrue(wc.contains(5));
		assertTrue(wc.contains(6));
		assertTrue(wc.contains(7));
		assertFalse(wc.contains(8));
		assertTrue(wc.add(Integer.valueOf(7), 3));
		assertEquals(16, wc.size());
		assertFalse(wc.contains(8));
		assertTrue(wc.addAll(Arrays.asList(3, 4, null, 8), 2));
		assertTrue(wc.contains(8));
		assertEquals(24, wc.size());
		assertTrue(wc.contains(7));
		assertTrue(wc.remove(Integer.valueOf(7)));
		assertFalse(wc.contains(7));
		assertEquals(18, wc.size());
		assertFalse(wc.remove(Integer.valueOf(7)));
		assertEquals(18, wc.size());
		assertTrue(wc.add(null, 5));
		assertEquals(23, wc.size());
		assertFalse(wc.isEmpty());
		wc.clear();
		assertEquals(0, wc.size());
		assertTrue(wc.isEmpty());
	}

	@Test
	public void testBadEquals() {
		assertFalse(wc.equals(null));
		assertFalse(wc.equals(1));
	}

	@Test
	public void testEquals() {
		assertTrue(wc.add(2, 5));
		assertTrue(wc.add(1, 2));
		WeightedCollection<Integer> wc2 = new WeightedCollection<Integer>(15);
		assertTrue(wc2.isEmpty());
		assertEquals(0, wc2.size());
		assertTrue(wc2.add(2));
		assertFalse(wc2.isEmpty());
		assertEquals(1, wc2.size());
		assertTrue(wc2.add(2));
		assertEquals(2, wc2.size());
		assertTrue(wc2.add(2));
		assertTrue(wc2.add(1));
		assertTrue(wc2.add(2));
		assertTrue(wc2.add(1));
		assertFalse(wc.equals(wc2));
		assertFalse(wc2.equals(wc));
		assertTrue(wc2.add(2));
		assertTrue(wc.equals(wc2));
		assertTrue(wc2.equals(wc));
		assertTrue(wc.hashCode() == wc2.hashCode());
		wc2.add(null);
		assertFalse(wc.equals(wc2));
		assertFalse(wc2.equals(wc));
		wc.add(null, 2);
		assertFalse(wc.equals(wc2));
		assertFalse(wc2.equals(wc));
		wc2.add(null);
		assertTrue(wc.equals(wc2));
		assertTrue(wc2.equals(wc));
		assertTrue(wc.hashCode() == wc2.hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("WeightedCollection: []", wc.toString());
		assertTrue(wc.add(1));
		assertEquals("WeightedCollection: [1 (1)]", wc.toString());
		assertTrue(wc.add(2));
		assertEquals("WeightedCollection: [1 (1), 2 (1)]", wc.toString());
		assertTrue(wc.add(1, 2));
		assertEquals("WeightedCollection: [1 (3), 2 (1)]", wc.toString());
	}

	@Test
	public void testUnweightedHasNextIterator() {
		Iterator<Integer> it = wc.unweightedIterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I3));
		assertTrue(wc.add(null));
		assertTrue(wc.add(null));
		assertEquals(7, wc.size());
		it = wc.unweightedIterator();
		assertNotNull(it);
		assertTrue(it.hasNext());
		Object it1 = it.next();
		assertEquals(I1, it1);
		assertTrue(it.hasNext());
		Object it2 = it.next();
		// remove 2
		it.remove();
		assertEquals(I2, it2);
		assertTrue(it.hasNext());
		Object it3 = it.next();
		assertEquals(I3, it3);
		assertTrue(it.hasNext());
		Object it4 = it.next();
		assertNull(it4);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
		assertEquals(5, wc.size());
		assertFalse(wc.contains(it2));
	}

	@Test
	public void testUnweightedNextIterator() {
		Iterator<Integer> it = wc.unweightedIterator();
		assertNotNull(it);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I3));
		assertTrue(wc.add(null));
		assertTrue(wc.add(null));
		assertEquals(7, wc.size());
		it = wc.unweightedIterator();
		assertNotNull(it);
		Object it1 = it.next();
		assertEquals(I1, it1);
		Object it2 = it.next();
		// remove 2
		it.remove();
		assertEquals(I2, it2);
		Object it3 = it.next();
		assertEquals(I3, it3);
		Object it4 = it.next();
		assertNull(it4);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
		assertEquals(5, wc.size());
		assertFalse(wc.contains(it2));
	}

	@Test
	public void testWeightedHasNextIterator() {
		Iterator<Integer> it = wc.iterator();
		assertNotNull(it);
		assertFalse(it.hasNext());
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I3));
		assertTrue(wc.add(null));
		assertTrue(wc.add(null));
		assertEquals(7, wc.size());
		it = wc.iterator();
		assertNotNull(it);
		assertTrue(it.hasNext());
		Object it1 = it.next();
		assertEquals(I1, it1);
		assertTrue(it.hasNext());
		Object it2 = it.next();
		assertEquals(I1, it2);
		assertTrue(it.hasNext());
		Object it3 = it.next();
		assertEquals(I2, it3);
		assertTrue(it.hasNext());
		Object it4 = it.next();
		assertEquals(I2, it4);
		assertTrue(it.hasNext());
		Object it5 = it.next();
		assertEquals(I3, it5);
		assertTrue(it.hasNext());
		Object it6 = it.next();
		assertNull(it6);
		assertTrue(it.hasNext());
		Object it7 = it.next();
		assertNull(it7);
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
	}

	@Test
	public void testWeightedNextIterator() {
		Iterator<Integer> it = wc.iterator();
		assertNotNull(it);
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I1));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I2));
		assertTrue(wc.add(I3));
		assertTrue(wc.add(null));
		assertTrue(wc.add(null));
		assertEquals(7, wc.size());
		it = wc.iterator();
		assertNotNull(it);
		Object it1 = it.next();
		assertEquals(I1, it1);
		Object it2 = it.next();
		assertEquals(I1, it2);
		Object it3 = it.next();
		assertEquals(I2, it3);
		Object it4 = it.next();
		assertEquals(I2, it4);
		Object it5 = it.next();
		assertEquals(I3, it5);
		Object it6 = it.next();
		assertNull(it6);
		assertTrue(it.hasNext());
		it.remove();
		assertFalse(it.hasNext());
		try {
			it.next();
			fail();
		} catch (NoSuchElementException e) {
			// OK
		}
	}

	@Test
	public void testBadWeightedRemove() {
		Iterator<Integer> it = wc.iterator();
		try {
			it.remove();
			fail();
		} catch (IllegalStateException e) {
			// OK
		} catch (UnsupportedOperationException e) {
			// OK
		}
	}

	@Test
	public void testBadUnweightedRemove() {
		Iterator<Integer> it = wc.unweightedIterator();
		try {
			it.remove();
			fail();
		} catch (IllegalStateException e) {
			// OK
		}
	}

	@Test
	public void testBadGetRandomValue() {
		try {
			wc.getRandomValue();
			fail();
		} catch (IndexOutOfBoundsException e) {
			// OK
		}
	}

	@Test
	public void testGetRandomValue() {
		wc.add(1);
		wc.add(1);
		Object o = wc.getRandomValue();
		assertEquals(1, o);
		wc.clear();
		wc.add(null);
		o = wc.getRandomValue();
		assertNull(o);
	}

}
