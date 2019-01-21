/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DividingFormulaTest
{

	@Test
	public void testToString()
	{
		assertEquals("/1", new DividingFormula(1).toString());
		assertEquals("/3", new DividingFormula(3).toString());
		assertEquals("/-3", new DividingFormula(-3).toString());
	}
	
	@Test
	public void testIdentity()
	{
		DividingFormula f = new DividingFormula(1);
		assertEquals(2, f.resolve(2).intValue());
		assertEquals(2, f.resolve(2.5).intValue());
		testBrokenCalls(f);
	}

	@Test
	public void testEquality()
	{
		DividingFormula f1 = new DividingFormula(1);
		DividingFormula f2 = new DividingFormula(1);
		DividingFormula f3 = new DividingFormula(2);
		DividingFormula f4 = new DividingFormula(-1);
		assertNotSame(f1, f2);
		assertEquals(f1.hashCode(), f2.hashCode());
		assertEquals(f1, f2);
		assertNotNull(f1);
		assertNotEquals(f1.hashCode(), f3.hashCode());
		assertNotEquals(f1, f3);
		assertNotEquals(f1.hashCode(), f4.hashCode());
		assertNotEquals(f1, f4);
	}

	@Test
	public void testPositive()
	{
		DividingFormula f = new DividingFormula(3);
		assertEquals(1, f.resolve(5).intValue());
		assertEquals(2, f.resolve(6).intValue());
		assertEquals(2, f.resolve(7).intValue());
		assertEquals(2, f.resolve(6.5).intValue());
		testBrokenCalls(f);
	}

	@SuppressWarnings("unused")
	@Test
	public void testZero()
	{
		try
		{
			new DividingFormula(0);
			fail("DividingFormula should not allow build with zero (will always fail)");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testNegative()
	{
		DividingFormula f = new DividingFormula(-2);
		assertEquals(-2, f.resolve(5).intValue());
		assertEquals(3, f.resolve(-6.7).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(DividingFormula f)
	{
		try
		{
			f.resolve((Number[]) null);
			fail("null should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve();
			fail("empty array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(4, 2.5);
			fail("two arguments in array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(4, 2.5);
			fail("two arguments should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

}
