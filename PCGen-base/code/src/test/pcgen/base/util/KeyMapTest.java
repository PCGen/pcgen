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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class KeyMapTest extends TestCase
{

	private static final String SA = "A";
	private static final String SC = "C";
	private static final String SF = "F";
	private static final String SB = "B";
	private static final Double D0 = Double.valueOf(0);
	private static final Double D2 = Double.valueOf(2);
	private static final Double D3_2 = Double.valueOf(3.2);
	private static final Double D5 = Double.valueOf(5);
	private static final Double D6 = Double.valueOf(6);

	private KeyMap<Double> otom;

	@Override
	@Before
	public void setUp()
	{
		otom = new KeyMap<>();
	}

	public void populate()
	{
		otom.put(SA, D0);
		otom.put(SB, D5);
	}

	@Test
	public void testEmptyClear()
	{
		assertEquals(0, otom.size());
		assertTrue(otom.isEmpty());
		populate();
		assertEquals(2, otom.size());
		assertFalse(otom.isEmpty());
		otom.clear();
		assertTrue(otom.isEmpty());
		assertNull(otom.get(SA));
		assertNull(otom.getKeyFor(D5));
	}

	@Test
	public void testPutGetBad()
	{
		try
		{
			otom.put(null, Double.valueOf(5.4));
			fail("null key should be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//yep
		}
		try
		{
			otom.put(SB, null);
			fail("null value should be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//yep
		}
	}

	@Test
	public void testPutGetExact()
	{
		populate();
		assertEquals(D0, otom.get(SA));
		assertEquals(D5, otom.get(SB));
		assertEquals(SA, otom.getKeyFor(D0));
		assertEquals(SB, otom.getKeyFor(D5));
		//double check direct overwrite
		Double ov = otom.put(SB, D5);
		assertEquals(D5, ov);
		assertEquals(D5, otom.get(SB));
		assertEquals(SB, otom.getKeyFor(D5));
		otom.remove(SB);
		assertEquals(D0, otom.get(SA));
		assertEquals(SA, otom.getKeyFor(D0));
		assertNull(otom.get(SB));
		assertNull(otom.getKeyFor(D5));
		//safe
		otom.remove(null);
		assertEquals(D0, otom.get(SA));
		assertEquals(SA, otom.getKeyFor(D0));
		assertNull(otom.get(SB));
		assertNull(otom.getKeyFor(D5));
	}

	@Test
	public void testOverwriteKey()
	{
		populate();
		Double ov = otom.put(SA, D3_2);
		assertEquals(D3_2, otom.get(SA));
		assertEquals(SA, otom.getKeyFor(D3_2));
		assertEquals(D0, ov);
		//check reset
		assertNull(otom.getKeyFor(D0));
	}

	@Test
	public void testOverwriteValue()
	{
		populate();
		Double ov = otom.put(SC, D5);
		assertEquals(D5, otom.get(SC));
		assertEquals(SC, otom.getKeyFor(D5));
		assertNull(ov);
		//check reset
		assertNull(otom.get(SB));
	}

	@Test
	public void testOverwriteMix()
	{
		populate();
		Double ov = otom.put(SA, D5);
		assertEquals(D0, ov);
		assertEquals(D5, otom.get(SA));
		assertEquals(SA, otom.getKeyFor(D5));
		//check reset
		assertNull(otom.get(SB));
		assertNull(otom.getKeyFor(D0));
	}

	@Test
	public void testContainsKey()
	{
		populate();
		assertTrue(otom.containsKey(SA));
		assertTrue(otom.containsKey(SB));
		assertFalse(otom.containsKey(SF));
	}

	@Test
	public void testContainsValue()
	{
		populate();
		assertTrue(otom.containsValue(D0));
		assertTrue(otom.containsValue(D5));
		assertFalse(otom.containsValue(D2));
		//Check values just in case
		assertFalse(otom.containsValue(SA));
	}

	@Test
	public void testKeySet()
	{
		Set<String> s = otom.keySet();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate();
		s = otom.keySet();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<String> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(SA));
		assertTrue(full.remove(SB));
		//check independence
		try
		{
			s.add(SF);
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(SC, D6);
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
	}

	@Test
	public void testKeyValues()
	{
		Collection<Double> s = otom.keySortedValues();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate();
		s = otom.keySortedValues();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Double> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(D0));
		assertTrue(full.remove(D5));
		//check independence
		try
		{
			s.add(D2);
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(SC, D6);
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
		//TODO Check actual ordering...
	}

	@Test
	public void testInsertValues()
	{
		Collection<Double> s = otom.insertOrderValues();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate();
		s = otom.insertOrderValues();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Double> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(D0));
		assertTrue(full.remove(D5));
		//check independence
		try
		{
			s.add(D2);
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(SC, D6);
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
		//TODO Check actual ordering...
	}

	@Test
	public void testGetItemInOrder()
	{
		populate();
		assertEquals(D0, otom.getItemInOrder(0));
		assertEquals(D5, otom.getItemInOrder(1));
	}
}
