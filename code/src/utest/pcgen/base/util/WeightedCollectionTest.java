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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeightedCollectionTest
{

	private static final Integer I3 = 3;

	private static final Integer I2 = 2;

	private static final Integer I1 = 1;

	private WeightedCollection<Integer> wc;

	@BeforeEach
	void setUp()
	{
		wc = new WeightedCollection<>();
	}

	@Test
	public void testBadIntConstructor()
	{
		assertThrows(IllegalArgumentException.class,
				() -> new WeightedCollection<Integer>(-5));
	}

	@Test
	public void testBadCollectionConstructor()
	{
		assertThrows(NullPointerException.class,
				() -> new WeightedCollection<>((Collection<Integer>) null));
	}

	@Test
	public void testCollectionConstructorSemantics()
	{
		Collection<Integer> c = new ArrayList<>();
		assertTrue(c.add(I1));
		assertTrue(c.add(I2));
		assertTrue(c.add(null));
		AbstractCollection<Integer> col = new WeightedCollection<>(c);
		assertEquals(3, col.size());
		c.add(4);
		assertEquals(3, col.size());
		col.clear();
		assertEquals(4, c.size());
	}

	@Test
	public void testSize()
	{
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
		assertTrue(wc.add(7, 3));
		assertEquals(13, wc.size());
		assertTrue(wc.add(7, 3));
		assertEquals(16, wc.size());
		assertTrue(wc.addAll(Arrays.asList(3, 4, 5, 6), 2));
		assertEquals(24, wc.size());
		assertTrue(wc.remove(7));
		assertEquals(18, wc.size());
		assertFalse(wc.remove(7));
		assertEquals(18, wc.size());
	}

	@Test
	public void testBadAddNegative()
	{
		try
		{
			wc.add(4, -3);
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	@Test
	public void testBadAddZero()
	{
		assertFalse(wc.add(4, 0));
	}

	@Test
	public void testBadAddAllNegative()
	{
		try
		{
			wc.addAll(Arrays.asList(3, 4, 5), -3);
			fail();
		}
		catch (IllegalArgumentException iae)
		{
			// OK
		}
	}

	@Test
	public void testBadAddAllZero()
	{
		assertFalse(wc.addAll(Arrays.asList(3, 4, 5), 0));
	}

	@Test
	public void testSimple()
	{
		assertTrue(wc.isEmpty());
		assertFalse(wc.contains(I1));
		assertTrue(wc.add(I1));
		assertFalse(wc.isEmpty());
		assertTrue(wc.contains(I1));
		assertTrue(wc.contains(1)); // value semantic
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
		assertTrue(wc.add(7, 3));
		assertEquals(13, wc.size());
		assertFalse(wc.contains(4));
		assertTrue(wc.contains(5));
		assertTrue(wc.contains(6));
		assertTrue(wc.contains(7));
		assertFalse(wc.contains(8));
		assertTrue(wc.add(7, 3));
		assertEquals(16, wc.size());
		assertFalse(wc.contains(8));
		assertTrue(wc.addAll(Arrays.asList(3, 4, null, 8), 2));
		assertTrue(wc.contains(8));
		assertEquals(24, wc.size());
		assertTrue(wc.contains(7));
		assertTrue(wc.remove(7));
		assertFalse(wc.contains(7));
		assertEquals(18, wc.size());
		assertFalse(wc.remove(7));
		assertEquals(18, wc.size());
		assertTrue(wc.add(null, 5));
		assertEquals(23, wc.size());
		assertFalse(wc.isEmpty());
		wc.clear();
		assertEquals(0, wc.size());
		assertTrue(wc.isEmpty());
	}

	@Test
	public void testBadEquals()
	{
		assertNotNull(wc);
		assertNotEquals(1, wc);
	}

	@Test
	public void testEquals()
	{
		assertTrue(wc.add(2, 5));
		assertTrue(wc.add(1, 2));
		WeightedCollection<Integer> wc2 = new WeightedCollection<>(15);
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
		assertNotEquals(wc, wc2);
		assertNotEquals(wc2, wc);
		assertTrue(wc2.add(2));
		assertEquals(wc2, wc);
		assertEquals(wc2, wc);
		assertEquals(wc.hashCode(), wc2.hashCode());
		wc2.add(null);
		assertNotEquals(wc, wc2);
		assertNotEquals(wc2, wc);
		wc.add(null, 2);
		assertNotEquals(wc, wc2);
		assertNotEquals(wc2, wc);
		wc2.add(null);
		assertEquals(wc2, wc);
		assertEquals(wc2, wc);
		assertEquals(wc.hashCode(), wc2.hashCode());
	}

	@Test
	public void testToString()
	{
		assertEquals("WeightedCollection: []", wc.toString());
		assertTrue(wc.add(1));
		assertEquals("WeightedCollection: [1 (1)]", wc.toString());
		assertTrue(wc.add(2));
		assertEquals("WeightedCollection: [1 (1), 2 (1)]", wc.toString());
		assertTrue(wc.add(1, 2));
		assertEquals("WeightedCollection: [1 (3), 2 (1)]", wc.toString());
	}

	@Test
	public void testUnweightedHasNextIterator()
	{
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
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
			// OK
		}
		assertEquals(5, wc.size());
		assertFalse(wc.contains(it2));
	}

	@Test
	public void testUnweightedNextIterator()
	{
		Iterator<Integer> it = wc.unweightedIterator();
		assertNotNull(it);
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
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
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
			// OK
		}
		assertEquals(5, wc.size());
		assertFalse(wc.contains(it2));
	}

	@Test
	public void testWeightedHasNextIterator()
	{
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
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
			// OK
		}
	}

	@Test
	public void testWeightedNextIterator()
	{
		Iterator<Integer> it = wc.iterator();
		assertNotNull(it);
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
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
		try
		{
			it.next();
			fail();
		}
		catch (NoSuchElementException e)
		{
			// OK
		}
	}

	@Test
	public void testBadWeightedRemove()
	{
		Iterator<Integer> it = wc.iterator();
		try
		{
			it.remove();
			fail();
		}
		catch (IllegalStateException | UnsupportedOperationException e)
		{
			// OK
		}
	}

	@Test
	public void testBadUnweightedRemove()
	{
		Iterator<Integer> it = wc.unweightedIterator();
		try
		{
			it.remove();
			fail();
		}
		catch (IllegalStateException e)
		{
			// OK
		}
	}

	@Test
	public void testBadGetRandomValue()
	{
		try
		{
			wc.getRandomValue();
			fail();
		}
		catch (IndexOutOfBoundsException e)
		{
			// OK
		}
	}

	@Test
	public void testGetRandomValue()
	{
		wc.add(1);
		wc.add(1);
		Object o = wc.getRandomValue();
		assertEquals(1, o);
		wc.clear();
		wc.add(null);
		o = wc.getRandomValue();
		assertNull(o);
	}

	@Test
	public void testGetWeight()
	{
		assertTrue(wc.isEmpty());
		assertTrue(wc.add(I1));
		assertEquals(1, wc.getWeight(I1));
		assertTrue(wc.add(1)); // value semantic
		assertEquals(2, wc.getWeight(I1));
		assertTrue(wc.add(I2));
		assertEquals(1, wc.getWeight(I2));
		assertTrue(wc.addAll(Arrays.asList(3, 4, null, 8), 2));
		assertEquals(2, wc.getWeight(3));
		assertTrue(wc.remove(I1));
		assertEquals(0, wc.getWeight(I1));
		assertFalse(wc.remove(I1));
		assertEquals(0, wc.getWeight(I1));
		assertEquals(0, wc.getWeight(875));
		assertEquals(2, wc.getWeight(null));
		wc.clear();
		assertEquals(0, wc.getWeight(I2));
	}

	@Test
	public void testNullComparatorConstructor()
	{
		WeightedCollection<String> swc = new WeightedCollection<>((Comparator<String>) null);
		String s1 = "asting";
		String s2 = "aString1";
		String s3 = "Bobcat";
		assertTrue(swc.isEmpty());
		assertFalse(swc.contains(s1));
		assertTrue(swc.add(s1));
		assertFalse(swc.isEmpty());
		assertTrue(swc.contains(s1));
		assertTrue(swc.contains(new String("asting"))); // value semantic
		assertFalse(swc.contains(s2));
		assertEquals(1, swc.size());
		assertTrue(swc.add(s1));
		assertTrue(swc.contains(s1));
		assertEquals(2, swc.size());
		assertFalse(swc.contains(s2));
		assertTrue(swc.add(s2));
		assertTrue(swc.contains(s2));
		assertEquals(3, swc.size());
		assertTrue(swc.add(s3));
		assertEquals(4, swc.size());
		assertFalse(swc.contains(null));
		assertTrue(swc.add(null));
		assertTrue(swc.contains(null));
		assertEquals(5, swc.size());
		assertTrue(swc.add(null));
		assertEquals(6, swc.size());
		assertFalse(swc.contains("Cat"));
		assertFalse(swc.contains("dog"));
		assertFalse(swc.contains("Eagle"));
		assertFalse(swc.contains("Purple"));
		assertTrue(swc.addAll(Arrays.asList("Cat", null, "dog", "Eagle")));
		assertEquals(10, swc.size());
		assertTrue(swc.contains("Cat"));
		assertTrue(swc.contains("dog"));
		assertTrue(swc.contains("Eagle"));
		assertFalse(swc.contains("Purple"));
		assertTrue(swc.add("Purple", 3));
		assertEquals(13, swc.size());
		assertTrue(swc.contains("Cat"));
		assertTrue(swc.contains("dog"));
		assertTrue(swc.contains("Eagle"));
		assertTrue(swc.contains("Purple"));
		assertFalse(swc.contains("Snake"));
		assertTrue(swc.add("Purple", 3));
		assertEquals(16, swc.size());
		assertFalse(swc.contains("Snake"));
		assertTrue(swc.addAll(Arrays.asList("Cat", "dog", null, "Snake"), 2));
		assertTrue(swc.contains("Snake"));
		assertEquals(24, swc.size());
		assertTrue(swc.contains("Purple"));
		assertTrue(swc.remove("Purple"));
		assertFalse(swc.contains("Purple"));
		assertEquals(18, swc.size());
		assertFalse(swc.remove("Purple"));
		assertEquals(18, swc.size());
		assertTrue(swc.add(null, 5));
		assertEquals(23, swc.size());
		assertFalse(swc.isEmpty());
		assertEquals(swc, swc);
		assertNotEquals(swc, wc);
		swc.clear();
		assertEquals(0, swc.size());
		assertTrue(swc.isEmpty());
		assertEquals(swc, swc);
		assertEquals(swc, wc);
	}

	@Test
	public void testInsensitiveComparatorConstructor()
	{
		WeightedCollection<String> swc = new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
		String s1 = "asting";
		String s2 = "aString1";
		String s3 = "Bobcat";
		assertTrue(swc.isEmpty());
		assertFalse(swc.contains(s1));
		assertTrue(swc.add(s1));
		assertFalse(swc.isEmpty());
		assertTrue(swc.contains(s1));
		assertTrue(swc.contains(new String("asting"))); // value semantic
		assertFalse(swc.contains(s2));
		assertEquals(1, swc.size());
		assertTrue(swc.add(s1));
		assertTrue(swc.contains(s1));
		assertEquals(2, swc.size());
		assertFalse(swc.contains(s2));
		assertTrue(swc.add(s2));
		assertTrue(swc.contains(s2));
		assertEquals(3, swc.size());
		assertTrue(swc.add(s3));
		assertEquals(4, swc.size());
		assertFalse(swc.contains(null));
		try
		{
			assertTrue(swc.add(null));
			fail();
		}
		catch (NullPointerException e)
		{
			//ok
		}
		assertFalse(swc.contains("Cat"));
		assertFalse(swc.contains("dog"));
		assertFalse(swc.contains("Eagle"));
		assertFalse(swc.contains("Purple"));
		assertTrue(swc.addAll(Arrays.asList("Cat", "dog", "Eagle")));
		assertEquals(7, swc.size());
		assertTrue(swc.contains("Cat"));
		assertTrue(swc.contains("dog"));
		assertTrue(swc.contains("Eagle"));
		assertFalse(swc.contains("Purple"));
		assertTrue(swc.add("Purple", 3));
		assertEquals(10, swc.size());
		assertTrue(swc.contains("Cat"));
		assertTrue(swc.contains("dog"));
		assertTrue(swc.contains("Eagle"));
		assertTrue(swc.contains("Purple"));
		assertFalse(swc.contains("Snake"));
		assertTrue(swc.add("Purple", 3));
		assertEquals(13, swc.size());
		assertFalse(swc.contains("Snake"));
		assertTrue(swc.addAll(Arrays.asList("Cat", "dog", "Snake"), 2));
		assertTrue(swc.contains("Snake"));
		assertEquals(19, swc.size());
		assertTrue(swc.contains("Purple"));
		assertTrue(swc.remove("Purple"));
		assertFalse(swc.contains("Purple"));
		assertEquals(13, swc.size());
		assertFalse(swc.remove("Purple"));
		assertEquals(13, swc.size());
		assertFalse(swc.isEmpty());
		assertEquals(swc, swc);
		assertNotEquals(swc, wc);
		swc.clear();
		assertEquals(0, swc.size());
	}

	@Test
	public void testComparatorEquals()
	{
		WeightedCollection<String> iwc = new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
		WeightedCollection<String> swc = new WeightedCollection<>();
		assertTrue(iwc.isEmpty());
		assertEquals(iwc, iwc);
		assertEquals(swc, swc);
		//See testArchitectureProof() on why this should be True
		assertEquals(iwc, swc);
		assertEquals(swc, iwc);
		//See testArchitectureProof() on why this should be True
		assertEquals(iwc, wc);
		iwc.add("asting");
		swc.add("asting");
		iwc.add("aString");
		swc.add("aString");
		assertEquals(iwc, swc);
		assertEquals(swc, iwc);
	}

	@Test
	public void testArchitectureProof()
	{
		TreeSet<String> ciSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		TreeSet<String> csSet = new TreeSet<>(String::compareTo);
		//To prove existing behavior
		assertEquals(ciSet, csSet);
		ciSet.add("asting");
		csSet.add("asting");
		ciSet.add("aString");
		csSet.add("aString");
		assertEquals(ciSet, csSet);
		assertNotEquals(ciSet.toString(), csSet.toString());
	}


}
