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

public class OneToOneMapTest extends TestCase
{

	private OneToOneMap<Integer, Double> otom;

	@Override
	@Before
	public void setUp()
	{
		otom = new OneToOneMap<>();
	}

	public void populate()
	{
		otom.put(Integer.valueOf(0), Double.valueOf(0));
		otom.put(Integer.valueOf(3), Double.valueOf(5));
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
		assertNull(otom.get(Integer.valueOf(0)));
		assertNull(otom.getKeyFor(Double.valueOf(5)));
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
			otom.put(Integer.valueOf(3), null);
			fail("null value should be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//yep
		}
		assertNull(otom.remove(null));
	}

	@Test
	public void testPutGetExact()
	{
		populate();
		assertEquals(Double.valueOf(0), otom.get(Integer.valueOf(0)));
		assertEquals(Double.valueOf(5), otom.get(Integer.valueOf(3)));
		assertEquals(Integer.valueOf(0), otom.getKeyFor(Double.valueOf(0)));
		assertEquals(Integer.valueOf(3), otom.getKeyFor(Double.valueOf(5)));
		//double check direct overwrite
		Double ov = otom.put(Integer.valueOf(3), Double.valueOf(5));
		assertEquals(Double.valueOf(5), ov);
		assertEquals(Double.valueOf(5), otom.get(Integer.valueOf(3)));
		assertEquals(Integer.valueOf(3), otom.getKeyFor(Double.valueOf(5)));
		otom.remove(Integer.valueOf(3));
		assertEquals(Double.valueOf(0), otom.get(Integer.valueOf(0)));
		assertEquals(Integer.valueOf(0), otom.getKeyFor(Double.valueOf(0)));
		assertNull(otom.get(Integer.valueOf(3)));
		assertNull(otom.getKeyFor(Double.valueOf(5)));
	}

	@Test
	public void testOverwriteKey()
	{
		populate();
		Double ov = otom.put(Integer.valueOf(0), Double.valueOf(3.2));
		assertEquals(Double.valueOf(3.2), otom.get(Integer.valueOf(0)));
		assertEquals(Integer.valueOf(0), otom.getKeyFor(Double.valueOf(3.2)));
		assertEquals(Double.valueOf(0), ov);
		//check reset
		assertNull(otom.getKeyFor(Double.valueOf(0)));
	}

	@Test
	public void testOverwriteValue()
	{
		populate();
		Double ov = otom.put(Integer.valueOf(4), Double.valueOf(5));
		assertEquals(Double.valueOf(5), otom.get(Integer.valueOf(4)));
		assertEquals(Integer.valueOf(4), otom.getKeyFor(Double.valueOf(5)));
		assertNull(ov);
		//check reset
		assertNull(otom.get(Integer.valueOf(3)));
	}

	@Test
	public void testOverwriteMix()
	{
		populate();
		Double ov = otom.put(Integer.valueOf(0), Double.valueOf(5));
		assertEquals(Double.valueOf(0), ov);
		assertEquals(Double.valueOf(5), otom.get(Integer.valueOf(0)));
		assertEquals(Integer.valueOf(0), otom.getKeyFor(Double.valueOf(5)));
		//check reset
		assertNull(otom.get(Integer.valueOf(3)));
		assertNull(otom.getKeyFor(Double.valueOf(0)));
	}

	@Test
	public void testContainsKey()
	{
		populate();
		assertTrue(otom.containsKey(Integer.valueOf(0)));
		assertTrue(otom.containsKey(Integer.valueOf(3)));
		assertFalse(otom.containsKey(Integer.valueOf(2)));
		//Check values just in case
		assertFalse(otom.containsKey(Double.valueOf(0)));
	}

	@Test
	public void testContainsValue()
	{
		populate();
		assertTrue(otom.containsValue(Double.valueOf(0)));
		assertTrue(otom.containsValue(Double.valueOf(5)));
		assertFalse(otom.containsValue(Double.valueOf(2)));
		//Check values just in case
		assertFalse(otom.containsValue(Integer.valueOf(0)));
	}

	@Test
	public void testKeySet()
	{
		Set<Integer> s = otom.keySet();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate();
		s = otom.keySet();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Integer> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(Integer.valueOf(0)));
		assertTrue(full.remove(Integer.valueOf(3)));
		//check independence
		try
		{
			s.add(Integer.valueOf(2));
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(Integer.valueOf(4), Double.valueOf(6));
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
	}

	@Test
	public void testValues()
	{
		Collection<Double> s = otom.values();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate();
		s = otom.values();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Double> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(Double.valueOf(0)));
		assertTrue(full.remove(Double.valueOf(5)));
		//check independence
		try
		{
			s.add(Double.valueOf(2));
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(Integer.valueOf(4), Double.valueOf(6));
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
	}

}
