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

public class MultiplyingFormulaTest
{

	@Test
	public void testToString()
	{
		assertEquals("*1", new MultiplyingFormula(1).toString());
		assertEquals("*3", new MultiplyingFormula(3).toString());
		assertEquals("*0", new MultiplyingFormula(0).toString());
		assertEquals("*-3", new MultiplyingFormula(-3).toString());
	}
	
	@Test
	public void testIdentity()
	{
		MultiplyingFormula f = new MultiplyingFormula(1);
		assertEquals(2, f.resolve(2).intValue());
		assertEquals(2, f.resolve(2.5).intValue());
		testBrokenCalls(f);
	}

	@Test
	public void testEquality()
	{
		MultiplyingFormula f1 = new MultiplyingFormula(1);
		MultiplyingFormula f2 = new MultiplyingFormula(1);
		MultiplyingFormula f3 = new MultiplyingFormula(2);
		MultiplyingFormula f4 = new MultiplyingFormula(-1);
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
		MultiplyingFormula f = new MultiplyingFormula(3);
		assertEquals(15, f.resolve(5).intValue());
		//TODO Need to specify the order of operations - is this rounded first or second?
		//assertEquals(17, f.resolve(Double.valueOf(5.5)).intValue());
		testBrokenCalls(f);
	}

	@Test
	public void testZero()
	{
		MultiplyingFormula f = new MultiplyingFormula(0);
		assertEquals(0, f.resolve(5).intValue());
		assertEquals(0, f.resolve(2.3).intValue());
		testBrokenCalls(f);
	}

	@Test
	public void testNegative()
	{
		MultiplyingFormula f = new MultiplyingFormula(-2);
		assertEquals(-10, f.resolve(5).intValue());
		//TODO Need to specify the order of operations - is this rounded first or second?
		//assertEquals(13, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(MultiplyingFormula f)
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
