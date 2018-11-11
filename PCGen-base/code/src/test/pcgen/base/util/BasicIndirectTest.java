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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;

public class BasicIndirectTest extends TestCase
{

	private BasicIndirect<Number> indirectDouble = new BasicIndirect<>(
			new NumberManager(), Double.valueOf(4));
	private BasicIndirect<String> indirectString = new BasicIndirect<>(
			new StringManager(), "Hello!");

	@SuppressWarnings("unused")
	@Test
	public void testConstructor()
	{
		try
		{
			//Using .equals to prove a point if this is changed to legal :)
			new BasicIndirect<>(new StringManager(), null).equals(indirectDouble);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			new BasicIndirect<>(null, "Hello!");
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
		try
		{
			new BasicIndirect<String>(null, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//expected
		}
	}

	@SuppressWarnings({"unused", "unchecked", "rawtypes"})
	@Test
	public void testConstructorGenericsViolation()
	{
		try
		{
			Object o = 4;
			//Mess with generics
			new BasicIndirect(new StringManager(), o);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
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
						Integer.valueOf(4)
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
