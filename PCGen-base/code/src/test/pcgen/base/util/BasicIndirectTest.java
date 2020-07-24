/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.testsupport.TestSupport;

public class BasicIndirectTest
{

	private BasicIndirect<Number> indirectDouble = new BasicIndirect<>(
			new NumberManager(), TestSupport.D4);
	private BasicIndirect<String> indirectString = new BasicIndirect<>(
			new StringManager(), "Hello!");

	@Test
	public void testConstructor()
	{
		//Using .equals to prove a point if this is changed to legal :)
		assertThrows(NullPointerException.class, () -> new BasicIndirect<>(new StringManager(), null).equals(indirectDouble));
		assertThrows(NullPointerException.class, () -> new BasicIndirect<>(null, "Hello!"));
		assertThrows(NullPointerException.class, () -> new BasicIndirect<String>(null, null));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testConstructorGenericsViolation()
	{
		Object o = 4;
		//Mess with generics
		assertThrows(IllegalArgumentException.class, () -> new BasicIndirect(new StringManager(), o));
	}

	@Test
	public void testResolvesTo()
	{
		assertEquals(4.0, indirectDouble.get());
		assertEquals("Hello!", indirectString.get());
	}

	@Test
	public void testGetUnconverted()
	{
		assertEquals("4.0", indirectDouble.getUnconverted());
		assertEquals("Hello!", indirectString.getUnconverted());
	}

	@Test
	public void testToString()
	{
		assertEquals("4.0", indirectDouble.toString());
		assertEquals("Hello!", indirectString.toString());
	}
	
	@Test
	public void testEquals()
	{
		BasicIndirect<Number> indirectInt =
				new BasicIndirect<>(
						new NumberManager(),
						TestSupport.I4
				);
		BasicIndirect<Number> indirectDoubleToo =
				new BasicIndirect<>(
						new NumberManager(),
						new Double(4.0)
				);
		BasicIndirect<String> indirectStringHi =
				new BasicIndirect<>(new StringManager(), "Hi!");
		assertTrue(indirectDouble.equals(indirectDoubleToo));
		assertTrue(indirectDoubleToo.equals(indirectDouble));
		assertEquals(indirectDouble.hashCode(), indirectDoubleToo.hashCode());
		assertFalse(indirectDouble.equals(new Object()));
		assertFalse(indirectDouble.equals(indirectInt));
		assertFalse(indirectDouble.equals(indirectString));
		assertFalse(indirectString.equals(indirectStringHi));
		assertFalse(indirectStringHi.equals(indirectString));
	}
}
