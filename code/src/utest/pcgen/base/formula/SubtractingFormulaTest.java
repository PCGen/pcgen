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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SubtractingFormulaTest
{
	private SubtractingFormula f;

	@BeforeEach
	void setUp()
	{
		f = new SubtractingFormula(1);
	}
	@Test
	public void testToString()
	{
		assertEquals("-1", new SubtractingFormula(1).toString());
		assertEquals("-3", new SubtractingFormula(3).toString());
		assertEquals("+3", new SubtractingFormula(-3).toString());
		assertEquals("-0", new SubtractingFormula(0).toString());
	}
	
	@Test
	public void testIdentity()
	{
		SubtractingFormula f = new SubtractingFormula(1);
		assertEquals(-1, f.resolve(0).intValue());
		assertEquals(1, f.resolve(2).intValue());
		assertEquals(1, f.resolve(2.5).intValue());
	}

	@Test
	public void testEquality()
	{
		SubtractingFormula f1 = new SubtractingFormula(1);
		SubtractingFormula f2 = new SubtractingFormula(1);
		SubtractingFormula f3 = new SubtractingFormula(2);
		SubtractingFormula f4 = new SubtractingFormula(-1);
		assertEquals(f1.hashCode(), f2.hashCode());
		assertEquals(f1, f2);
		assertNotEquals(f1.hashCode(), f3.hashCode());
		assertNotEquals(f1, f3);
		assertNotEquals(f1.hashCode(), f4.hashCode());
		assertNotEquals(f1, f4);
	}

	@Test
	public void testPositive()
	{
		SubtractingFormula f = new SubtractingFormula(3);
		assertEquals(2, f.resolve(5).intValue());
		assertEquals(2, f.resolve(5.5).intValue());
	}

	@Test
	public void testZero()
	{
		SubtractingFormula f = new SubtractingFormula(0);
		assertEquals(5, f.resolve(5).intValue());
		assertEquals(2, f.resolve(2.3).intValue());
	}

	@Test
	public void testNegative()
	{
		SubtractingFormula f = new SubtractingFormula(-2);
		assertEquals(7, f.resolve(5).intValue());
		assertEquals(-4, f.resolve(-6.7).intValue());
	}

	@Test
	void testStackedFormula()
	{
		assertEquals(-1, f.resolve(f.resolve(1)));
		assertEquals(-1, f.resolve(f.resolve(1.2435643516)));
	}

	@Test
	void testIntegerUnderflow()
	{
		assertEquals(Integer.MAX_VALUE, f.resolve(Integer.MIN_VALUE));
	}

	@Test
	void testInputNotNull()
	{
		assertThrows(IllegalArgumentException.class, () -> f.resolve((Number[]) null));
	}

	@Test
	void testInputNotEmpty()
	{
		assertThrows(IllegalArgumentException.class, () -> f.resolve());
	}

	@Test
	void testInputNotLongerThan1()
	{
		assertThrows(IllegalArgumentException.class, () -> f.resolve(4, 2.5));
	}

}
