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

import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;

public class BasicObjectContainerTest extends TestCase
{

	private BasicObjectContainer<Number> indirectDouble =
			new BasicObjectContainer<Number>(new NumberManager(),
				Double.valueOf(4));
	private BasicObjectContainer<String> indirectString =
			new BasicObjectContainer<String>(new StringManager(), "Hello!");

	@Test
	public void testConstructor()
	{
		try
		{
			//Using .equals to prove a point if this is changed to legal :)
			new BasicObjectContainer<String>(new StringManager(), null)
				.equals(indirectDouble);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new BasicObjectContainer<String>(null, "Hello!");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new BasicObjectContainer<String>(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			Object o = 4;
			//Mess with generics
			new BasicObjectContainer(new StringManager(), o);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testResolvesTo()
	{
		assertFalse(indirectDouble.contains(null));
		assertFalse(indirectDouble.contains(3.0));
		assertTrue(indirectDouble.contains(4.0));
		assertTrue(indirectDouble.contains(new Double(4.0)));
		assertTrue(indirectString.contains("Hello!"));
		assertTrue(indirectString.contains(new String("Hello!")));
		assertFalse(indirectString.contains("Hi!"));
		assertFalse(indirectString.contains("hello!"));
	}

	@Test
	public void testGetUnconverted()
	{
		assertEquals("4.0", indirectDouble.getLSTformat(false));
		assertEquals("Hello!", indirectString.getLSTformat(false));
	}

	@Test
	public void testGetManagedClass()
	{
		assertEquals(Number.class, indirectDouble.getReferenceClass());
		assertEquals(String.class, indirectString.getReferenceClass());
	}
	
	@Test
	public void testContainedObjects()
	{
		Collection c = indirectDouble.getContainedObjects();
		assertEquals(1, c.size());
		Object o = c.iterator().next();
		assertEquals(4.0, o);
		c = indirectString.getContainedObjects();
		assertEquals(1, c.size());
		o = c.iterator().next();
		assertEquals("Hello!", o);
	}

//CONSIDER Dodging this item until it's defined if this is equal to BasicObjectContainer or any ObjectContainer...
//	@Test
//	public void testEquals()
//	{
//		BasicObjectContainer<Number> indirectInt =
//				new BasicObjectContainer<Number>(new NumberManager(),
//					Integer.valueOf(4));
//		BasicObjectContainer<Number> indirectDoubleToo =
//				new BasicObjectContainer<Number>(new NumberManager(),
//					new Double(4.0));
//		BasicObjectContainer<String> indirectStringHi =
//				new BasicObjectContainer<String>(new StringManager(), "Hi!");
//		assertTrue(indirectDouble.equals(indirectDoubleToo));
//		assertTrue(indirectDoubleToo.equals(indirectDouble));
//		assertFalse(indirectDouble.equals(indirectInt));
//		assertFalse(indirectDouble.equals(indirectString));
//		assertFalse(indirectString.equals(indirectStringHi));
//		assertFalse(indirectStringHi.equals(indirectString));
//	}

}
