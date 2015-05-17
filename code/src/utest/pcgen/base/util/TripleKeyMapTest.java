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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

public class TripleKeyMapTest extends TestCase
{

	private static final Character CA = 'A';

	private static final Character CB = 'B';

	private static final Character CC = 'C';

	private static final Character CD = 'D';

	private static final Integer I1 = 1;

	private static final Integer I2 = 2;

	private static final Integer I3 = 3;

	private static final Integer I4 = 4;

	private static final Double D1 = 1.0;

	private static final Double D2 = 2.0;

	private static final Double D3 = 3.0;

	private static final Double D4 = 4.0;

	private static final String S1 = "S1";

	private static final String S2 = "S2";

	private static final String S3 = "S3";

	private static final String S4 = "S4";

	private static final String S5 = "S5";

	private static final String S6 = "S6";

	private static final String S7 = "S7";

	private static final String S8 = "S8";

	private static final String S9 = "S9";

	TripleKeyMap<Integer, Double, Character, String> tkm;

	public void populate(boolean allowNull)
	{
		tkm.put(I1, D1, CA, S1);
		tkm.put(I1, D1, CB, S2);
		tkm.put(I1, D2, CA, S2);
		tkm.put(I1, D2, CB, S3);
		tkm.put(I2, D1, CC, S4);
		tkm.put(I2, D3, CD, S5);
		tkm.put(I3, D4, CD, S6);
		if (allowNull)
		{
			tkm.put(null, D3, CD, S7);
			tkm.put(I4, null, CD, S8);
			tkm.put(I4, D1, null, S9);
			tkm.put(I4, D2, CA, null);
		}
	}

	@Test
	public void testPutGet()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runPutGet(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runPutGet(false);
	}

	@Test
	public void testContainsKey()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runContainsKey(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runContainsKey(false);
	}

	@Test
	public void testRemove()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runRemove(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runRemove(false);
	}

	@Test
	public void testGetKeySet()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runGetKeySet(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runGetKeySet(false);
	}

	@Test
	public void testGetSecondaryKeySet()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runGetSecondaryKeySet(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runGetSecondaryKeySet(false);
	}

	@Test
	public void testGetTertiaryKeySet()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runGetTertiaryKeySet(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runGetTertiaryKeySet(false);
	}

	@Test
	public void testClearIsEmpty()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runClearIsEmpty(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runClearIsEmpty(false);
	}

	@Test
	public void testValues()
	{
		tkm = new TripleKeyMap<Integer, Double, Character, String>();
		runValues(true);
		tkm =
				new TripleKeyMap<Integer, Double, Character, String>(
					TreeMap.class, IdentityHashMap.class, IdentityHashMap.class);
		runValues(false);
	}

	public void runPutGet(boolean allowNull)
	{
		assertNull(tkm.get(I1, D2, CA));
		populate(allowNull);
		assertNull(tkm.get(I1, D1, CC));
		assertNull(tkm.get(I1, D4, CA));
		assertNull(tkm.get(Integer.valueOf(5), D1, CA));
		assertEquals(S1, tkm.get(I1, D1, CA));
		assertEquals(S2, tkm.get(I1, D1, CB));
		assertEquals(S2, tkm.get(I1, D2, CA));
		assertEquals(S3, tkm.get(I1, D2, CB));
		assertEquals(S4, tkm.get(I2, D1, CC));
		assertEquals(S5, tkm.get(I2, D3, CD));
		assertEquals(S6, tkm.get(I3, D4, CD));
		if (allowNull)
		{
			assertEquals(S7, tkm.get(null, D3, CD));
			assertEquals(S8, tkm.get(I4, null, CD));
			assertEquals(S9, tkm.get(I4, D1, null));
			assertNull(tkm.get(I4, D2, CA));
		}
	}

	public void runContainsKey(boolean allowNull)
	{
		assertFalse(tkm.containsKey(I1, D2, CA));
		populate(allowNull);
		assertFalse(tkm.containsKey(I1, D1, CC));
		assertFalse(tkm.containsKey(I1, D4, CA));
		assertFalse(tkm.containsKey(Integer.valueOf(5), D1, CA));
		assertTrue(tkm.containsKey(I1, D1, CA));
		assertTrue(tkm.containsKey(I1, D1, CB));
		assertTrue(tkm.containsKey(I1, D2, CA));
		assertTrue(tkm.containsKey(I1, D2, CB));
		assertTrue(tkm.containsKey(I2, D1, CC));
		assertTrue(tkm.containsKey(I2, D3, CD));
		assertTrue(tkm.containsKey(I3, D4, CD));
		if (allowNull)
		{
			assertTrue(tkm.containsKey(null, D3, CD));
			assertTrue(tkm.containsKey(I4, null, CD));
			assertTrue(tkm.containsKey(I4, D1, null));
			assertTrue(tkm.containsKey(I4, D2, CA));
		}
	}

	public void runRemove(boolean allowNull)
	{
		assertNull(tkm.remove(I1, D2, CA));
		populate(allowNull);
		assertTrue(tkm.containsKey(I1, D1, CA));
		assertEquals(S1, tkm.remove(I1, D1, CA));
		assertFalse(tkm.containsKey(I1, D1, CA));
		assertNull(tkm.remove(I1, D1, CA));
		if (allowNull)
		{
			assertTrue(tkm.containsKey(null, D3, CD));
			assertEquals(S7, tkm.remove(null, D3, CD));
			assertFalse(tkm.containsKey(null, D3, CD));
			assertNull(tkm.remove(null, D3, CD));
			assertTrue(tkm.containsKey(I4, null, CD));
			assertEquals(S8, tkm.remove(I4, null, CD));
			assertFalse(tkm.containsKey(I4, null, CD));
			assertNull(tkm.remove(I4, null, CD));
			assertTrue(tkm.containsKey(I4, D1, null));
			assertEquals(S9, tkm.remove(I4, D1, null));
			assertFalse(tkm.containsKey(I4, D1, null));
			assertNull(tkm.remove(I4, D1, null));
			assertTrue(tkm.containsKey(I4, D2, CA));
			assertNull(tkm.remove(I4, D2, CA));
			assertFalse(tkm.containsKey(I4, D2, CA));
			assertNull(tkm.remove(I4, D2, CA));
		}
	}

	public void runGetKeySet(boolean allowNull)
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
		populate(allowNull);
		assertEquals(1, s.size());
		assertEquals(0, s2.size());
		Set<Integer> s3 = tkm.getKeySet();
		assertEquals(allowNull ? 5 : 3, s3.size());
		assertTrue(s3.contains(I1));
		assertTrue(s3.contains(I2));
		assertTrue(s3.contains(I3));
		if (allowNull)
		{
			assertTrue(s3.contains(I4));
			assertTrue(s3.contains(null));
		}
	}

	public void runGetSecondaryKeySet(boolean allowNull)
	{
		Set<Double> s = tkm.getSecondaryKeySet(Integer.valueOf(4));
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
		Set<Double> s2 = tkm.getSecondaryKeySet(I4);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(allowNull);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Double> s3 = tkm.getSecondaryKeySet(I1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(D1));
		assertTrue(s3.contains(D2));
		if (allowNull)
		{
			Set<Double> s4 = tkm.getSecondaryKeySet(I4);
			assertEquals(3, s4.size());
			assertTrue(s4.contains(D1));
			assertTrue(s4.contains(D2));
			assertTrue(s4.contains(null));
			Set<Double> s5 = tkm.getSecondaryKeySet(null);
			assertEquals(1, s5.size());
			assertTrue(s5.contains(Double.valueOf(3)));
		}
	}

	public void runGetTertiaryKeySet(boolean allowNull)
	{
		Set<Character> s = tkm.getTertiaryKeySet(I1, D1);
		assertEquals(0, s.size());
		int sSize = 1;
		try
		{
			s.add(CC);
		}
		catch (UnsupportedOperationException uoe)
		{
			// This is OK, just account for it
			sSize = 0;
		}
		// Ensure not saved in DoubleKeyMap
		Set<Character> s2 = tkm.getTertiaryKeySet(I1, D1);
		assertEquals(0, s2.size());
		assertEquals(sSize, s.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		populate(allowNull);
		assertEquals(sSize, s.size());
		assertEquals(0, s2.size());
		Set<Character> s3 = tkm.getTertiaryKeySet(I1, D1);
		assertEquals(2, s3.size());
		assertTrue(s3.contains(CA));
		assertTrue(s3.contains(CB));
	}

	public void runClearIsEmpty(boolean allowNull)
	{
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		populate(allowNull);
		assertFalse(tkm.isEmpty());
		assertEquals(allowNull ? 5 : 3, tkm.firstKeyCount());
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
		if (allowNull)
		{
			tkm.put(null, D3, CD, "Sa");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
			tkm.clear();
			assertTrue(tkm.isEmpty());
			assertEquals(0, tkm.firstKeyCount());
			tkm.put(I3, null, CA, "Sb");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
			tkm.clear();
			assertTrue(tkm.isEmpty());
			assertEquals(0, tkm.firstKeyCount());
			tkm.put(I2, D4, null, "Sc");
			assertFalse(tkm.isEmpty());
			assertEquals(1, tkm.firstKeyCount());
		}
		tkm.clear();
		assertTrue(tkm.isEmpty());
		assertEquals(0, tkm.firstKeyCount());
	}

	public void runValues(boolean allowNull)
	{
		Set<String> values = tkm.values(I1, D1);
		assertEquals(0, values.size());
		populate(allowNull);
		values = tkm.values(I1, D1);
		assertEquals(2, values.size());
		assertTrue(values.contains(S1));
		assertTrue(values.contains(S2));
		// prove independence
		tkm.remove(I1, D1, CA);
		assertEquals(2, values.size());
		assertTrue(values.contains(S1));
		assertTrue(values.contains(S2));
		values = tkm.values(I1, D2);
		assertEquals(2, values.size());
		assertTrue(values.contains(S2));
		assertTrue(values.contains(S3));
		// prove independence
		values.remove(S2);
		assertEquals(1, values.size());
		assertTrue(values.contains(S3));
		assertEquals(2, tkm.values(I1, D2).size());
		if (allowNull)
		{
			values = tkm.values(null, D3);
			assertEquals(1, values.size());
			assertTrue(values.contains(S7));
			values = tkm.values(I4, null);
			assertEquals(1, values.size());
			assertTrue(values.contains(S8));
			values = tkm.values(I4, D1);
			assertEquals(1, values.size());
			assertTrue(values.contains(S9));
			values = tkm.values(I4, D2);
			assertEquals(1, values.size());
			assertTrue(values.contains(null));
		}

	}

	// TODO Need a test that respects order/behavior of underlying lists for
	// class constructor
	public void testNullInConstructor()
	{
		try
		{
			new TripleKeyMap(null, HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, null, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, HashMap.class, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK, expected
		}
	}

	public void testBadClassInConstructor()
	{
		try
		{
			new TripleKeyMap(StrangeMap.class, HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, StrangeMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, HashMap.class, StrangeMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	public void testBadClassInConstructor2()
	{
		try
		{
			new TripleKeyMap(NoPublicZeroArgConstructorMap.class, HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, NoPublicZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, HashMap.class, NoPublicZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


	public void testBadClassInConstructor3()
	{
		try
		{
			new TripleKeyMap(NoZeroArgConstructorMap.class, HashMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, NoZeroArgConstructorMap.class, HashMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
		try
		{
			new TripleKeyMap(HashMap.class, HashMap.class, NoZeroArgConstructorMap.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//OK, expected
		}
	}


}

