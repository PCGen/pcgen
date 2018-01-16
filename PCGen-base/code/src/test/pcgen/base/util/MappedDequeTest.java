/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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

import org.junit.Test;

public class MappedDequeTest extends TestCase
{

	private static final Character CONST_A = 'A';
	private static final Character CONST_B = 'B';
	private static final Character CONST_C = 'C';
	private static final Character CONST_D = 'D';
	private static final Character CONST_E = 'E';
	private static final Character CONST_F = 'F';

	private TypedKey<Character> KEY1 = new TypedKey<>();
	private TypedKey<Character> KEY2 = new TypedKey<>();
	private TypedKey<String> KEY3 = new TypedKey<>();
	private TypedKey<Character> KEY4 = new TypedKey<>(CONST_F);

	private MappedDeque deque;

	@Override
	protected void setUp() throws Exception
	{
		deque = new MappedDeque();
	}

	@Test
	public void testNonNullKey()
	{
		try
		{
			assertNull(deque.peek(null));
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(deque.pop(null));
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			deque.set(null, CONST_A);
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			deque.set(null, null);
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			deque.push(null, CONST_A);
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			deque.push(null, null);
			fail("null key is invalid");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
	}

	@Test
	public void testPushPopPeek()
	{
		assertNull(deque.peek(KEY1));
		deque.push(KEY1, CONST_A);
		assertEquals(CONST_A, deque.peek(KEY1));
		assertNull(deque.peek(KEY2));
		deque.push(KEY1, CONST_B);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertNull(deque.peek(KEY2));
		deque.push(KEY2, CONST_C);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertEquals(CONST_C, deque.peek(KEY2));
		MappedDeque alt = new MappedDeque();
		//independence
		alt.push(KEY2, CONST_D);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertEquals(CONST_C, deque.peek(KEY2));
		assertEquals(CONST_D, alt.peek(KEY2));
		//it's a Deque
		assertEquals(CONST_B, deque.pop(KEY1));
		assertEquals(CONST_A, deque.peek(KEY1));
		//null value is legal
		deque.push(KEY1, null);
		assertNull(deque.peek(KEY1));
		assertNull(deque.pop(KEY1));
		assertEquals(CONST_A, deque.peek(KEY1));
		//Pop to empty
		assertEquals(CONST_A, deque.pop(KEY1));
		assertNull(deque.peek(KEY1));
	}

	@Test
	public void testSet()
	{
		assertNull(deque.peek(KEY1));
		deque.push(KEY1, CONST_A);
		assertEquals(CONST_A, deque.peek(KEY1));
		assertNull(deque.peek(KEY2));
		deque.push(KEY1, CONST_E);
		assertEquals(CONST_E, deque.peek(KEY1));
		assertNull(deque.peek(KEY2));
		//Set so we should never see E again
		deque.set(KEY1, CONST_B);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertNull(deque.peek(KEY2));
		deque.push(KEY2, CONST_C);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertEquals(CONST_C, deque.peek(KEY2));
		MappedDeque alt = new MappedDeque();
		//independence
		alt.push(KEY2, CONST_D);
		assertEquals(CONST_B, deque.peek(KEY1));
		assertEquals(CONST_C, deque.peek(KEY2));
		assertEquals(CONST_D, alt.peek(KEY2));
		//it's a Deque - but E was overwritten
		assertEquals(CONST_B, deque.pop(KEY1));
		assertEquals(CONST_A, deque.peek(KEY1));
		//null value is legal
		deque.push(KEY1, null);
		assertNull(deque.peek(KEY1));
		assertNull(deque.pop(KEY1));
		assertEquals(CONST_A, deque.peek(KEY1));
		//Pop to empty
		assertEquals(CONST_A, deque.pop(KEY1));
		assertNull(deque.peek(KEY1));
		//Set also works if never set
		deque.set(KEY3, CONST_A.toString());
		assertEquals(CONST_A.toString(), deque.peek(KEY3));
		assertEquals(CONST_A.toString(), deque.pop(KEY3));
		assertNull(deque.peek(KEY3));
	}

	@Test
	public void testPop()
	{
		assertNull(KEY1.getDefaultValue());
		//Thus this also null
		assertNull(deque.pop(KEY1));
		//But KEY2 has a default value, so it works here
		assertEquals(KEY4.getDefaultValue(), deque.pop(KEY4));
		//as well as after push/pop and set/pop
		deque.push(KEY4, CONST_E);
		assertEquals(CONST_E, deque.pop(KEY4));
		assertEquals(KEY4.getDefaultValue(), deque.pop(KEY4));
		deque.set(KEY4, CONST_D);
		assertEquals(CONST_D, deque.pop(KEY4));
		assertEquals(KEY4.getDefaultValue(), deque.pop(KEY4));
	}
}
