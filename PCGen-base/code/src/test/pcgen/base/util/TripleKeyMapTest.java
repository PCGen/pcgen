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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;
import pcgen.testsupport.TestSupport;

public class TripleKeyMapTest
{

	public void populate(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		tkm.put(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A, TestSupport.S1);
		tkm.put(TestSupport.I1, TestSupport.D1, TestSupport.CONST_B, TestSupport.S2);
		tkm.put(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A, TestSupport.S2);
		tkm.put(TestSupport.I1, TestSupport.D2, TestSupport.CONST_B, TestSupport.S3);
		tkm.put(TestSupport.I2, TestSupport.D1, TestSupport.CONST_C, TestSupport.S4);
		tkm.put(TestSupport.I2, TestSupport.D3, TestSupport.CONST_D, TestSupport.S5);
		tkm.put(TestSupport.I3, TestSupport.D4, TestSupport.CONST_D, TestSupport.S6);
		if (allowNull)
		{
			tkm.put(null, TestSupport.D3, TestSupport.CONST_D, TestSupport.S7);
			tkm.put(TestSupport.I4, null, TestSupport.CONST_D, TestSupport.S8);
			tkm.put(TestSupport.I4, TestSupport.D1, null, TestSupport.S9);
			tkm.put(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A, null);
		}
	}

	@Test
	public void testPutGetPlain()
	{
		runPutGet(new TripleKeyMap<>(), true);
	}

	@Test
	public void testPutGetIdentity()
	{
		runPutGet(getIdentityMap(), false);
	}

	@Test
	public void testContainsKeyPlain()
	{
		runContainsKey(new TripleKeyMap<>(), true);
	}

	@Test
	public void testContainsKeyIdentity()
	{
		runContainsKey(getIdentityMap(), false);
	}

	@Test
	public void testRemovePlain()
	{
		runRemove(new TripleKeyMap<>(), true);
	}

	@Test
	public void testRemoveIdentity()
	{
		runRemove(getIdentityMap(), false);
	}

	@Test
	public void testGetKeySetPlain()
	{
		runGetKeySet(new TripleKeyMap<>(), true);
	}

	@Test
	public void testGetKeySetIdentity()
	{
		runGetKeySet(getIdentityMap(), false);
	}

	@Test
	public void testGetSecondaryKeySetPlain()
	{
		runGetSecondaryKeySet(new TripleKeyMap<>(), true);
	}

	@Test
	public void testGetSecondaryKeySetIdentity()
	{
		runGetSecondaryKeySet(getIdentityMap(), false);
	}

	@Test
	public void testGetTertiaryKeySetPlain()
	{
		runGetTertiaryKeySet(new TripleKeyMap<>(), true);
	}

	@Test
	public void testGetTertiaryKeySetIdentity()
	{
		runGetTertiaryKeySet(getIdentityMap(), false);
	}

	@Test
	public void testClearIsEmptyPlain()
	{
		runClearIsEmpty(new TripleKeyMap<>(), true);
	}

	@Test
	public void testClearIsEmptyIdentity()
	{
		runClearIsEmpty(getIdentityMap(), false);
	}

	@Test
	public void testValuesPlain()
	{
		runValues(new TripleKeyMap<>(), true);
	}

	@Test
	public void testValuesIdentity()
	{
		runValues(getIdentityMap(), false);
	}

	public void runPutGet(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		assertNull(tkm.get(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A));
		populate(tkm, allowNull);
		assertNull(tkm.get(TestSupport.I1, TestSupport.D1, TestSupport.CONST_C));
		assertNull(tkm.get(TestSupport.I1, TestSupport.D4, TestSupport.CONST_A));
		assertNull(tkm.get(TestSupport.I5, TestSupport.D1, TestSupport.CONST_A));
		assertEquals(TestSupport.S1, tkm.get(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		assertEquals(TestSupport.S2, tkm.get(TestSupport.I1, TestSupport.D1, TestSupport.CONST_B));
		assertEquals(TestSupport.S2, tkm.get(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A));
		assertEquals(TestSupport.S3, tkm.get(TestSupport.I1, TestSupport.D2, TestSupport.CONST_B));
		assertEquals(TestSupport.S4, tkm.get(TestSupport.I2, TestSupport.D1, TestSupport.CONST_C));
		assertEquals(TestSupport.S5, tkm.get(TestSupport.I2, TestSupport.D3, TestSupport.CONST_D));
		assertEquals(TestSupport.S6, tkm.get(TestSupport.I3, TestSupport.D4, TestSupport.CONST_D));
		if (allowNull)
		{
			assertEquals(TestSupport.S7, tkm.get(null, TestSupport.D3, TestSupport.CONST_D));
			assertEquals(TestSupport.S8, tkm.get(TestSupport.I4, null, TestSupport.CONST_D));
			assertEquals(TestSupport.S9, tkm.get(TestSupport.I4, TestSupport.D1, null));
			assertNull(tkm.get(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
		}
	}

	public void runContainsKey(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		assertFalse(tkm.containsKey(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A));
		populate(tkm, allowNull);
		assertFalse(tkm.containsKey(TestSupport.I1, TestSupport.D1, TestSupport.CONST_C));
		assertFalse(tkm.containsKey(TestSupport.I1, TestSupport.D4, TestSupport.CONST_A));
		assertFalse(tkm.containsKey(TestSupport.I5, TestSupport.D1, TestSupport.CONST_A));
		assertTrue(tkm.containsKey(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		assertTrue(tkm.containsKey(TestSupport.I1, TestSupport.D1, TestSupport.CONST_B));
		assertTrue(tkm.containsKey(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A));
		assertTrue(tkm.containsKey(TestSupport.I1, TestSupport.D2, TestSupport.CONST_B));
		assertTrue(tkm.containsKey(TestSupport.I2, TestSupport.D1, TestSupport.CONST_C));
		assertTrue(tkm.containsKey(TestSupport.I2, TestSupport.D3, TestSupport.CONST_D));
		assertTrue(tkm.containsKey(TestSupport.I3, TestSupport.D4, TestSupport.CONST_D));
		if (allowNull)
		{
			assertTrue(tkm.containsKey(null, TestSupport.D3, TestSupport.CONST_D));
			assertTrue(tkm.containsKey(TestSupport.I4, null, TestSupport.CONST_D));
			assertTrue(tkm.containsKey(TestSupport.I4, TestSupport.D1, null));
			assertTrue(tkm.containsKey(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
		}
	}

	public void runRemove(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		assertNull(tkm.remove(TestSupport.I1, TestSupport.D2, TestSupport.CONST_A));
		populate(tkm, allowNull);
		assertTrue(tkm.containsKey(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		assertEquals(TestSupport.S1, tkm.remove(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		assertFalse(tkm.containsKey(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		assertNull(tkm.remove(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A));
		if (allowNull)
		{
			assertTrue(tkm.containsKey(null, TestSupport.D3, TestSupport.CONST_D));
			assertEquals(TestSupport.S7, tkm.remove(null, TestSupport.D3, TestSupport.CONST_D));
			assertFalse(tkm.containsKey(null, TestSupport.D3, TestSupport.CONST_D));
			assertNull(tkm.remove(null, TestSupport.D3, TestSupport.CONST_D));
			assertTrue(tkm.containsKey(TestSupport.I4, null, TestSupport.CONST_D));
			assertEquals(TestSupport.S8, tkm.remove(TestSupport.I4, null, TestSupport.CONST_D));
			assertFalse(tkm.containsKey(TestSupport.I4, null, TestSupport.CONST_D));
			assertNull(tkm.remove(TestSupport.I4, null, TestSupport.CONST_D));
			assertTrue(tkm.containsKey(TestSupport.I4, TestSupport.D1, null));
			assertEquals(TestSupport.S9, tkm.remove(TestSupport.I4, TestSupport.D1, null));
			assertFalse(tkm.containsKey(TestSupport.I4, TestSupport.D1, null));
			assertNull(tkm.remove(TestSupport.I4, TestSupport.D1, null));
			assertTrue(tkm.containsKey(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
			assertNull(tkm.remove(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
			assertFalse(tkm.containsKey(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
			assertNull(tkm.remove(TestSupport.I4, TestSupport.D2, TestSupport.CONST_A));
		}
	}

	public void runGetKeySet(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		Set<Integer> s = tkm.getKeySet();
		assertEquals(0, s.size());
		s.add(Integer.valueOf(-5));
		// Ensure not saved in DoubleKeyMap
		Set<Integer> s2 = tkm.getKeySet();
		assertEquals(0, s2.size());
		assertEquals(1, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm, allowNull);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = tkm.getKeySet();
		assertEquals(allowNull ? 5 : 3, s3.size());
		assertTrue(s3.contains(TestSupport.I1));
		assertTrue(s3.contains(TestSupport.I2));
		assertTrue(s3.contains(TestSupport.I3));
		if (allowNull)
		{
			assertTrue(s3.contains(TestSupport.I4));
			assertTrue(s3.contains(null));
		}
	}

	public void runGetSecondaryKeySet(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		Set<Double> s = tkm.getSecondaryKeySet(TestSupport.I4);
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(Double.valueOf(-5));
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Double> s2 = tkm.getSecondaryKeySet(TestSupport.I4);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm, allowNull);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = tkm.getSecondaryKeySet(TestSupport.I1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(TestSupport.D1));
		assertTrue(s3.contains(TestSupport.D2));
		if (allowNull)
		{
			Set<Double> s4 = tkm.getSecondaryKeySet(TestSupport.I4);
			assertEquals(3, s4.size());
			assertTrue(s4.contains(TestSupport.D1));
			assertTrue(s4.contains(TestSupport.D2));
			assertTrue(s4.contains(null));
			Set<Double> s5 = tkm.getSecondaryKeySet(null);
			assertEquals(1, s5.size());
			assertTrue(s5.contains(TestSupport.D3));
		}
	}

	public void runGetTertiaryKeySet(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		Set<Character> s = tkm.getTertiaryKeySet(TestSupport.I1, TestSupport.D1);
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(TestSupport.CONST_C);
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Character> s2 = tkm.getTertiaryKeySet(TestSupport.I1, TestSupport.D1);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(tkm, allowNull);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Character> s3 = tkm.getTertiaryKeySet(TestSupport.I1, TestSupport.D1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(TestSupport.CONST_A));
		assertTrue(s3.contains(TestSupport.CONST_B));
	}

	public void runClearIsEmpty(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		populate(tkm, allowNull);
		assertFalse(tkm.isEmpty());
		assertEquals(allowNull ? 5 : 3, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		if (allowNull)
		{
			tkm.put(null, TestSupport.D3, TestSupport.CONST_D, "Sa");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
			tkm.clear();
			assertTrue(tkm.isEmpty());
			assertEquals(0, tkm.firstKeyCount());
			tkm.put(TestSupport.I3, null, TestSupport.CONST_A, "Sb");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
			tkm.clear();
			assertTrue(tkm.isEmpty());
			assertEquals(0, tkm.firstKeyCount());
			tkm.put(TestSupport.I2, TestSupport.D4, null, "Sc");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
		}
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
	}

	public void runValues(TripleKeyMap<Integer, Double, Character, String> tkm, boolean allowNull)
	{
		Set<String> values = tkm.values(TestSupport.I1, TestSupport.D1);
		assertEquals(0, values.size());
		populate(tkm, allowNull);
		values = tkm.values(TestSupport.I1, TestSupport.D1);
		assertEquals(2, values.size());
		assertTrue(values.contains(TestSupport.S1));
		assertTrue(values.contains(TestSupport.S2));
		// prove independence
		tkm.remove(TestSupport.I1, TestSupport.D1, TestSupport.CONST_A);
		assertEquals(2, values.size());
		assertTrue(values.contains(TestSupport.S1));
		assertTrue(values.contains(TestSupport.S2));
		values = tkm.values(TestSupport.I1, TestSupport.D2);
		assertEquals(2, values.size());
		assertTrue(values.contains(TestSupport.S2));
		assertTrue(values.contains(TestSupport.S3));
		// prove independence
		values.remove(TestSupport.S2);
		assertEquals(1, values.size());
		assertTrue(values.contains(TestSupport.S3));
		assertEquals(2, tkm.values(TestSupport.I1, TestSupport.D2).size());
		if (allowNull)
		{
			values = tkm.values(null, TestSupport.D3);
			assertEquals(1, values.size());
			assertTrue(values.contains(TestSupport.S7));
			values = tkm.values(TestSupport.I4, null);
			assertEquals(1, values.size());
			assertTrue(values.contains(TestSupport.S8));
			values = tkm.values(TestSupport.I4, TestSupport.D1);
			assertEquals(1, values.size());
			assertTrue(values.contains(TestSupport.S9));
			values = tkm.values(TestSupport.I4, TestSupport.D2);
			assertEquals(1, values.size());
			assertTrue(values.contains(null));
		}

	}

	// TODO Need a test that respects order/behavior of underlying lists for
	// class constructor
	@Test
	public void testNullInConstructor()
	{
		assertThrows(NullPointerException.class, () -> new TripleKeyMap<>(null, HashMap.class, HashMap.class));
		assertThrows(NullPointerException.class, () -> new TripleKeyMap<>(HashMap.class, null, HashMap.class));
		assertThrows(NullPointerException.class, () -> new TripleKeyMap<>(HashMap.class, HashMap.class, null));
	}

	@Test
	public void testBadClassInConstructor()
	{
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(StrangeMap.class, HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, StrangeMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, HashMap.class, StrangeMap.class));
	}

	@Test
	public void testBadClassInConstructor2()
	{
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(NoPublicZeroArgConstructorMap.class, HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, NoPublicZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, HashMap.class, NoPublicZeroArgConstructorMap.class));
	}

	@Test
	public void testBadClassInConstructor3()
	{
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(NoZeroArgConstructorMap.class, HashMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, NoZeroArgConstructorMap.class, HashMap.class));
		assertThrows(IllegalArgumentException.class, () -> new TripleKeyMap<>(HashMap.class, HashMap.class, NoZeroArgConstructorMap.class));
	}

	@Test
	public void testClone()
	{
		TripleKeyMap<Integer, Double, Character, String> tkm =
				new TripleKeyMap<>();
		populate(tkm, true);
		TripleKeyMap<Integer, Double, Character, String> copy;
		try
		{
			copy = tkm.clone();
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
			return;
		}
		Double d1 = TestSupport.D1;
		// test independence
		tkm.put(TestSupport.I1, d1, TestSupport.CONST_D, TestSupport.S1);
		assertNull(copy.get(TestSupport.I1, d1, TestSupport.CONST_D));
		copy.put(TestSupport.I1, d1, TestSupport.CONST_C, TestSupport.S4);
		assertNull(tkm.get(TestSupport.I1, d1, TestSupport.CONST_C));
	}

	private TripleKeyMap<Integer, Double, Character, String> getIdentityMap()
	{
		return new TripleKeyMap<>(
				TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
	}

}
