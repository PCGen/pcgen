/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.math;

import junit.framework.TestCase;

public class OrderedPairTest extends TestCase
{
	@SuppressWarnings("unused")
	public void testConstructorXNull()
	{
		try
		{
			new OrderedPair(null, 4);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	@SuppressWarnings("unused")
	public void testConstructorYNull()
	{
		try
		{
			new OrderedPair(4, null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfNull()
	{
		try
		{
			OrderedPair.valueOf(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfNotNumeric()
	{
		try
		{
			OrderedPair.valueOf("SomeString");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfTooManyCommas()
	{
		try
		{
			OrderedPair.valueOf("1,3,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfNoTrailingNumber()
	{
		try
		{
			OrderedPair.valueOf("1,");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfNoLeadingNumber()
	{
		try
		{
			OrderedPair.valueOf(",4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfBadFirstNumber()
	{
		try
		{
			OrderedPair.valueOf("x,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
		try
		{
			OrderedPair.valueOf("3-0,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOfBadSecondNumber()
	{
		try
		{
			OrderedPair.valueOf("5,x");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
		try
		{
			OrderedPair.valueOf("5,5..6");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testValueOf()
	{
		OrderedPair op = OrderedPair.valueOf("4,6");
		assertEquals(Integer.valueOf(4), op.getPreciseX());
		assertEquals(Integer.valueOf(6), op.getPreciseY());
		assertEquals("4,6", op.toString());
	}

	public void testEquals()
	{
		OrderedPair op1 = OrderedPair.valueOf("4,6");
		OrderedPair op2 = OrderedPair.valueOf("4,6.0");
		OrderedPair op3 = OrderedPair.valueOf("4,6.1");
		OrderedPair op4 = OrderedPair.valueOf("4.0,6");
		OrderedPair op5 = OrderedPair.valueOf("4.0,6.0");
		OrderedPair op6 = OrderedPair.valueOf("4.0,6.1");
		OrderedPair op7 = OrderedPair.valueOf("4.1,6");
		OrderedPair op8 = OrderedPair.valueOf("4.1,6.0");
		OrderedPair op9 = OrderedPair.valueOf("4.1,6.1");
		OrderedPair op1b = OrderedPair.valueOf("4,6");
		assertFalse(op1.equals(new Object()));
		assertFalse(op1.equals(op2));
		assertFalse(op1.equals(op3));
		assertFalse(op1.equals(op4));
		assertFalse(op1.equals(op5));
		assertFalse(op1.equals(op6));
		assertFalse(op1.equals(op7));
		assertFalse(op1.equals(op8));
		assertFalse(op1.equals(op9));
		assertTrue(op1.equals(op1));
		assertTrue(op1.equals(op1b));
		assertTrue(op1b.equals(op1));
		assertTrue(op1b.hashCode() == op1.hashCode());
	}
	
}
