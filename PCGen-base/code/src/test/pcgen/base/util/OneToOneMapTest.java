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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class OneToOneMapTest
{

	public void populate(OneToOneMap<Integer, Double> otom)
	{
		otom.put(TestSupport.I0, TestSupport.D0);
		otom.put(TestSupport.I3, TestSupport.D5);
	}

	@Test
	public void testEmptyClear()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		assertEquals(0, otom.size());
		assertTrue(otom.isEmpty());
		populate(otom);
		assertEquals(2, otom.size());
		assertFalse(otom.isEmpty());
		otom.clear();
		assertTrue(otom.isEmpty());
		assertNull(otom.get(TestSupport.I0));
		assertNull(otom.getKeyFor(TestSupport.D5));
	}

	@Test
	public void testPutGetBad()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		assertThrows(NullPointerException.class, () -> otom.put(null, Double.valueOf(5.4)));
		assertThrows(NullPointerException.class, () -> otom.put(TestSupport.I3, null));
		assertNull(otom.remove(null));
	}

	@Test
	public void testPutGetExact()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		assertEquals(TestSupport.D0, otom.get(TestSupport.I0));
		assertEquals(TestSupport.D5, otom.get(TestSupport.I3));
		assertEquals(TestSupport.I0, otom.getKeyFor(TestSupport.D0));
		assertEquals(TestSupport.I3, otom.getKeyFor(TestSupport.D5));
		//double check direct overwrite
		Double ov = otom.put(TestSupport.I3, TestSupport.D5);
		assertEquals(TestSupport.D5, ov);
		assertEquals(TestSupport.D5, otom.get(TestSupport.I3));
		assertEquals(TestSupport.I3, otom.getKeyFor(TestSupport.D5));
		otom.remove(TestSupport.I3);
		assertEquals(TestSupport.D0, otom.get(TestSupport.I0));
		assertEquals(TestSupport.I0, otom.getKeyFor(TestSupport.D0));
		assertNull(otom.get(TestSupport.I3));
		assertNull(otom.getKeyFor(TestSupport.D5));
	}

	@Test
	public void testOverwriteKey()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		Double ov = otom.put(TestSupport.I0, Double.valueOf(3.2));
		assertEquals(Double.valueOf(3.2), otom.get(TestSupport.I0));
		assertEquals(TestSupport.I0, otom.getKeyFor(Double.valueOf(3.2)));
		assertEquals(TestSupport.D0, ov);
		//check reset
		assertNull(otom.getKeyFor(TestSupport.D0));
	}

	@Test
	public void testOverwriteValue()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		Double ov = otom.put(TestSupport.I4, TestSupport.D5);
		assertEquals(TestSupport.D5, otom.get(TestSupport.I4));
		assertEquals(TestSupport.I4, otom.getKeyFor(TestSupport.D5));
		assertNull(ov);
		//check reset
		assertNull(otom.get(TestSupport.I3));
	}

	@Test
	public void testOverwriteMix()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		Double ov = otom.put(TestSupport.I0, TestSupport.D5);
		assertEquals(TestSupport.D0, ov);
		assertEquals(TestSupport.D5, otom.get(TestSupport.I0));
		assertEquals(TestSupport.I0, otom.getKeyFor(TestSupport.D5));
		//check reset
		assertNull(otom.get(TestSupport.I3));
		assertNull(otom.getKeyFor(TestSupport.D0));
	}

	@Test
	public void testContainsKey()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		assertTrue(otom.containsKey(TestSupport.I0));
		assertTrue(otom.containsKey(TestSupport.I3));
		assertFalse(otom.containsKey(TestSupport.I2));
		//Check values just in case
		assertFalse(otom.containsKey(TestSupport.D0));
	}

	@Test
	public void testContainsValue()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		populate(otom);
		assertTrue(otom.containsValue(TestSupport.D0));
		assertTrue(otom.containsValue(TestSupport.D5));
		assertFalse(otom.containsValue(TestSupport.D2));
		//Check values just in case
		assertFalse(otom.containsValue(TestSupport.I0));
	}

	@Test
	public void testKeySet()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		Set<Integer> s = otom.keySet();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate(otom);
		s = otom.keySet();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Integer> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(TestSupport.I0));
		assertTrue(full.remove(TestSupport.I3));
		//check independence
		try
		{
			s.add(TestSupport.I2);
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(TestSupport.I4, TestSupport.D6);
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
	}

	@Test
	public void testValues()
	{
		OneToOneMap<Integer, Double> otom = new OneToOneMap<>();
		Collection<Double> s = otom.values();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate(otom);
		s = otom.values();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<Double> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(TestSupport.D0));
		assertTrue(full.remove(TestSupport.D5));
		//check independence
		try
		{
			s.add(TestSupport.D2);
			assertEquals(2, otom.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		otom.put(TestSupport.I4, TestSupport.D6);
		assertEquals(3, otom.keySet().size());
		assertEquals(3, s.size());
	}

}
