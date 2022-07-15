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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class MappedDequeTest
{

	private TypedKey<Character> key1 = new TypedKey<>();
	private TypedKey<Character> key2 = new TypedKey<>();
	private TypedKey<String> key3 = new TypedKey<>();
	private TypedKey<Character> key4 = new TypedKey<>(TestSupport.CONST_F);

	@Test
	public void testNonNullKey()
	{
		MappedDeque deque = new MappedDeque();
		assertThrows(NullPointerException.class, () -> deque.peek(null));
		assertThrows(NullPointerException.class, () -> deque.pop(null));
		assertThrows(NullPointerException.class, () -> deque.set(null, TestSupport.CONST_A));
		assertThrows(NullPointerException.class, () -> deque.set(null, null));
		assertThrows(NullPointerException.class, () -> deque.push(null, TestSupport.CONST_A));
		assertThrows(NullPointerException.class, () -> deque.push(null, null));
	}

	@Test
	public void testPushPopPeek()
	{
		MappedDeque deque = new MappedDeque();
		assertNull(deque.peek(key1));
		deque.push(key1, TestSupport.CONST_A);
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		assertNull(deque.peek(key2));
		deque.push(key1, TestSupport.CONST_B);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertNull(deque.peek(key2));
		deque.push(key2, TestSupport.CONST_C);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertEquals(TestSupport.CONST_C, deque.peek(key2));
		MappedDeque alt = new MappedDeque();
		//independence
		alt.push(key2, TestSupport.CONST_D);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertEquals(TestSupport.CONST_C, deque.peek(key2));
		assertEquals(TestSupport.CONST_D, alt.peek(key2));
		//it's a Deque
		assertEquals(TestSupport.CONST_B, deque.pop(key1));
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		//null value is legal
		deque.push(key1, null);
		assertNull(deque.peek(key1));
		assertNull(deque.pop(key1));
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		//Pop to empty
		assertEquals(TestSupport.CONST_A, deque.pop(key1));
		assertNull(deque.peek(key1));
	}

	@Test
	public void testSet()
	{
		MappedDeque deque = new MappedDeque();
		assertNull(deque.peek(key1));
		deque.push(key1, TestSupport.CONST_A);
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		assertNull(deque.peek(key2));
		deque.push(key1, TestSupport.CONST_E);
		assertEquals(TestSupport.CONST_E, deque.peek(key1));
		assertNull(deque.peek(key2));
		//Set so we should never see E again
		deque.set(key1, TestSupport.CONST_B);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertNull(deque.peek(key2));
		deque.push(key2, TestSupport.CONST_C);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertEquals(TestSupport.CONST_C, deque.peek(key2));
		MappedDeque alt = new MappedDeque();
		//independence
		alt.push(key2, TestSupport.CONST_D);
		assertEquals(TestSupport.CONST_B, deque.peek(key1));
		assertEquals(TestSupport.CONST_C, deque.peek(key2));
		assertEquals(TestSupport.CONST_D, alt.peek(key2));
		//it's a Deque - but E was overwritten
		assertEquals(TestSupport.CONST_B, deque.pop(key1));
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		//null value is legal
		deque.push(key1, null);
		assertNull(deque.peek(key1));
		assertNull(deque.pop(key1));
		assertEquals(TestSupport.CONST_A, deque.peek(key1));
		//Pop to empty
		assertEquals(TestSupport.CONST_A, deque.pop(key1));
		assertNull(deque.peek(key1));
		//Set also works if never set
		deque.set(key3, TestSupport.CONST_A.toString());
		assertEquals(TestSupport.CONST_A.toString(), deque.peek(key3));
		assertEquals(TestSupport.CONST_A.toString(), deque.pop(key3));
		assertNull(deque.peek(key3));
	}

	@Test
	public void testPop()
	{
		MappedDeque deque = new MappedDeque();
		assertNull(key1.getDefaultValue());
		//Thus this also null
		assertNull(deque.pop(key1));
		//But KEY2 has a default value, so it works here
		assertEquals(key4.getDefaultValue(), deque.pop(key4));
		//as well as after push/pop and set/pop
		deque.push(key4, TestSupport.CONST_E);
		assertEquals(TestSupport.CONST_E, deque.pop(key4));
		assertEquals(key4.getDefaultValue(), deque.pop(key4));
		deque.set(key4, TestSupport.CONST_D);
		assertEquals(TestSupport.CONST_D, deque.pop(key4));
		assertEquals(key4.getDefaultValue(), deque.pop(key4));
	}
}
