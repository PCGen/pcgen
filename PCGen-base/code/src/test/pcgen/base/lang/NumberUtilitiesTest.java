/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

/**
 * Test the NumberUtilities class
 */
public class NumberUtilitiesTest
{

	@Test
	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(NumberUtilities.class);
	}

	@Test
	public void testGetNumberNull()
	{
		assertThrows(NullPointerException.class, () -> NumberUtilities.getNumber(null));
	}

	@Test
	public void testGetNumberBad()
	{
		assertThrows(NumberFormatException.class, () -> NumberUtilities.getNumber("1..5"));
	}

	@Test
	public void testGetPreciseNumberNull()
	{
		assertThrows(NullPointerException.class, () -> NumberUtilities.getPreciseNumber(null));
	}

	@Test
	public void testInteger()
	{
		assertEquals(1, NumberUtilities.getNumber("1"));
	}

	@Test
	public void testLargeInteger()
	{
		assertEquals(Double.valueOf("3141592653"), NumberUtilities.getNumber("3141592653"));
	}

	@Test
	public void testDouble()
	{
		assertEquals(1.5, NumberUtilities.getNumber("1.5"));
	}

	@Test
	public void testPreciseInteger()
	{
		assertEquals(1, NumberUtilities.getPreciseNumber("1"));
	}

	@Test
	public void testPreciseDouble()
	{
		assertEquals(new BigDecimal("1.5"), NumberUtilities.getPreciseNumber("1.5"));
	}

	@Test
	public void testPreciseLargeInteger()
	{
		assertEquals(new BigDecimal("3141592653"), NumberUtilities.getPreciseNumber("3141592653"));
	}

	@Test
	public void testAdd()
	{
		assertEquals(Integer.valueOf(6), NumberUtilities.add(3, 3));
		assertEquals(Integer.valueOf(5), NumberUtilities.add(3, 2));
		assertEquals(Double.valueOf(5.5), NumberUtilities.add(3, 2.5));
		assertEquals(Double.valueOf(4.5), NumberUtilities.add(1.5, 3));
		assertEquals(Integer.valueOf(0), NumberUtilities.add(3, -3));
	}

	@Test
	public void testSubtract()
	{
		assertEquals(Integer.valueOf(0), NumberUtilities.subtract(3, 3));
		assertEquals(Integer.valueOf(1), NumberUtilities.subtract(3, 2));
		assertEquals(Double.valueOf(0.5), NumberUtilities.subtract(3, 2.5));
		assertEquals(Double.valueOf(-1.5), NumberUtilities.subtract(1.5, 3));
		assertEquals(Integer.valueOf(6), NumberUtilities.subtract(3, -3));
	}

	@Test
	public void testMax()
	{
		assertEquals(Integer.valueOf(3), NumberUtilities.max(3, 3));
		assertEquals(Integer.valueOf(3), NumberUtilities.max(3, 2));
		assertEquals(Integer.valueOf(3), NumberUtilities.max(3, 2.5));
		assertEquals(Double.valueOf(4.5), NumberUtilities.max(4.5, 3));
		assertEquals(Integer.valueOf(3), NumberUtilities.max(3, -3));
	}

	@Test
	public void testMin()
	{
		assertEquals(Integer.valueOf(3), NumberUtilities.min(3, 3));
		assertEquals(Integer.valueOf(2), NumberUtilities.min(2, 3));
		assertEquals(Double.valueOf(2.5), NumberUtilities.min(3, 2.5));
		assertEquals(Integer.valueOf(1), NumberUtilities.min(1.5, 1));
		assertEquals(Integer.valueOf(-3), NumberUtilities.min(3, -3));
	}

	@Test
	public void testDivide()
	{
		assertEquals(Integer.valueOf(1), NumberUtilities.divide(3, 3));
		//Gets out of integer
		assertEquals(Double.valueOf(1.5), NumberUtilities.divide(3, 2));
		//Isn't expected to go back
		assertEquals(Double.valueOf(3), NumberUtilities.divide(7.5, 2.5));
		assertEquals(Integer.valueOf(3), NumberUtilities.divide(-9, -3));
		assertEquals(Integer.valueOf(-3), NumberUtilities.divide(-12, 4));
		assertEquals(Double.valueOf(5), NumberUtilities.divide(8, 1.6));
		assertEquals(Double.valueOf(0.2), NumberUtilities.divide(1.6, 8));
		assertEquals(Double.NEGATIVE_INFINITY, NumberUtilities.divide(-12, 0));
	}

	@Test
	public void testMultiply()
	{
		assertEquals(Integer.valueOf(9), NumberUtilities.multiply(3, 3));
		assertEquals(Integer.valueOf(6), NumberUtilities.multiply(3, 2));
		assertEquals(Double.valueOf(7.5), NumberUtilities.multiply(3, 2.5));
		assertEquals(Double.valueOf(4.5), NumberUtilities.multiply(1.5, 3));
		assertEquals(Integer.valueOf(-9), NumberUtilities.multiply(3, -3));
	}

}
