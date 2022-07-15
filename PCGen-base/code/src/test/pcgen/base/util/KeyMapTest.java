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
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class KeyMapTest
{

	public void populate(KeyMap<Double> km)
	{
		km.put(TestSupport.S1, TestSupport.D0);
		km.put(TestSupport.S4, TestSupport.D5);
	}

	@Test
	public void testEmptyClear()
	{
		KeyMap<Double> km = new KeyMap<>();
		assertEquals(0, km.size());
		assertTrue(km.isEmpty());
		populate(km);
		assertEquals(2, km.size());
		assertFalse(km.isEmpty());
		km.clear();
		assertTrue(km.isEmpty());
		assertNull(km.get(TestSupport.S1));
		assertNull(km.getKeyFor(TestSupport.D5));
	}

	@Test
	public void testPutGetBad()
	{
		KeyMap<Double> km = new KeyMap<>();
		assertThrows(NullPointerException.class, () -> km.put(null, Double.valueOf(5.4)));
		assertThrows(NullPointerException.class, () -> km.put(TestSupport.S4, null));
	}

	@Test
	public void testPutGetExact()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		assertEquals(TestSupport.D0, km.get(TestSupport.S1));
		assertEquals(TestSupport.D5, km.get(TestSupport.S4));
		assertEquals(TestSupport.S1, km.getKeyFor(TestSupport.D0));
		assertEquals(TestSupport.S4, km.getKeyFor(TestSupport.D5));
		//double check direct overwrite
		Double ov = km.put(TestSupport.S4, TestSupport.D5);
		assertEquals(TestSupport.D5, ov);
		assertEquals(TestSupport.D5, km.get(TestSupport.S4));
		assertEquals(TestSupport.S4, km.getKeyFor(TestSupport.D5));
		km.remove(TestSupport.S4);
		assertEquals(TestSupport.D0, km.get(TestSupport.S1));
		assertEquals(TestSupport.S1, km.getKeyFor(TestSupport.D0));
		assertNull(km.get(TestSupport.S4));
		assertNull(km.getKeyFor(TestSupport.D5));
		//safe
		km.remove(null);
		assertEquals(TestSupport.D0, km.get(TestSupport.S1));
		assertEquals(TestSupport.S1, km.getKeyFor(TestSupport.D0));
		assertNull(km.get(TestSupport.S4));
		assertNull(km.getKeyFor(TestSupport.D5));
	}

	@Test
	public void testOverwriteKey()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		Double ov = km.put(TestSupport.S1, TestSupport.D3_2);
		assertEquals(TestSupport.D3_2, km.get(TestSupport.S1));
		assertEquals(TestSupport.S1, km.getKeyFor(TestSupport.D3_2));
		assertEquals(TestSupport.D0, ov);
		//check reset
		assertNull(km.getKeyFor(TestSupport.D0));
	}

	@Test
	public void testOverwriteValue()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		Double ov = km.put(TestSupport.S2, TestSupport.D5);
		assertEquals(TestSupport.D5, km.get(TestSupport.S2));
		assertEquals(TestSupport.S2, km.getKeyFor(TestSupport.D5));
		assertNull(ov);
		//check reset
		assertNull(km.get(TestSupport.S4));
	}

	@Test
	public void testOverwriteMix()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		Double ov = km.put(TestSupport.S1, TestSupport.D5);
		assertEquals(TestSupport.D0, ov);
		assertEquals(TestSupport.D5, km.get(TestSupport.S1));
		assertEquals(TestSupport.S1, km.getKeyFor(TestSupport.D5));
		//check reset
		assertNull(km.get(TestSupport.S4));
		assertNull(km.getKeyFor(TestSupport.D0));
	}

	@Test
	public void testContainsKey()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		assertTrue(km.containsKey(TestSupport.S1));
		assertTrue(km.containsKey(TestSupport.S4));
		assertFalse(km.containsKey(TestSupport.S3));
	}

	@Test
	public void testContainsValue()
	{
		KeyMap<Double> km = new KeyMap<>();
		populate(km);
		assertTrue(km.containsValue(TestSupport.D0));
		assertTrue(km.containsValue(TestSupport.D5));
		assertFalse(km.containsValue(TestSupport.D2));
		//Check values just in case
		assertFalse(km.containsValue(TestSupport.S1));
	}

	@Test
	public void testKeySet()
	{
		KeyMap<Double> km = new KeyMap<>();
		Set<String> s = km.keySet();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate(km);
		s = km.keySet();
		assertNotNull(s);
		assertEquals(2, s.size());
		//copy since we don't know what is returned is modifiable
		Set<String> full = new HashSet<>(s);
		//make sure we didn't lose anything
		assertEquals(2, full.size());
		assertTrue(full.remove(TestSupport.S1));
		assertTrue(full.remove(TestSupport.S4));
		//check independence
		try
		{
			s.add(TestSupport.S3);
			assertEquals(2, km.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		km.put(TestSupport.S2, TestSupport.D6);
		assertEquals(3, km.keySet().size());
		assertEquals(3, s.size());
	}

	@Test
	public void testKeyValues()
	{
		KeyMap<Double> km = new KeyMap<>();
		Collection<Double> s = km.keySortedValues();
		assertNotNull(s);
		assertEquals(0, s.size());
		populate(km);
		s = km.keySortedValues();
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
			assertEquals(2, km.keySet().size());
		}
		catch (UnsupportedOperationException e)
		{
			//expected
		}
		assertEquals(3, s.size());
		assertEquals(2, km.keySortedValues().size());
		km.put(TestSupport.S5, TestSupport.D6);
		s = km.keySortedValues();
		assertEquals(3, km.keySet().size());
		assertEquals(3, s.size());
		Iterator<Double> iterator = s.iterator();
		assertEquals(0.0, iterator.next());
		assertEquals(5.0, iterator.next());
		assertEquals(6.0, iterator.next());
	}
}
