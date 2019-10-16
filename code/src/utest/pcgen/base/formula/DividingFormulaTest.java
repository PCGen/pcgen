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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


class DividingFormulaTest
{

    private DividingFormula divByTwo;

	@BeforeEach
	void setUp()
	{
        divByTwo = new DividingFormula(2);
	}

	@Test
    void testToString()
	{
		assertEquals("/1", new DividingFormula(1).toString());
		assertEquals("/3", new DividingFormula(3).toString());
		assertEquals("/-3", new DividingFormula(-3).toString());
	}
	
	@Test
    void testIdentity()
	{
		DividingFormula f = new DividingFormula(1);
        assertEquals(2, f.resolve(2));
        assertEquals(2, f.resolve(2.5));
	}

	@Test
    void testEquality()
	{
		DividingFormula f1 = new DividingFormula(1);
		DividingFormula f2 = new DividingFormula(1);
		DividingFormula f3 = new DividingFormula(2);
		DividingFormula f4 = new DividingFormula(-1);
		assertEquals(f1.hashCode(), f2.hashCode());
		assertEquals(f1, f2);
		assertNotEquals(f1.hashCode(), f3.hashCode());
		assertNotEquals(f1, f3);
		assertNotEquals(f1.hashCode(), f4.hashCode());
		assertNotEquals(f1, f4);
	}

	@Test
    void testPositive()
	{
		DividingFormula f = new DividingFormula(3);
        assertEquals(1, f.resolve(5));
        assertEquals(2, f.resolve(6));
        assertEquals(2, f.resolve(7));
        assertEquals(2, f.resolve(6.5));
	}

	@SuppressWarnings("unused")
	@Test
    void testZero()
	{
		assertThrows(IllegalArgumentException.class, () -> new DividingFormula(0));
	}

	@Test
    void testNegative()
	{
		DividingFormula f = new DividingFormula(-2);
        assertEquals(-2, f.resolve(5));
        assertEquals(3, f.resolve(-6.7));
		assertEquals(2, f.resolve(-4));
	}

	@Test
    void testStackedFormulas()
	{
        assertEquals(2, divByTwo.resolve(divByTwo.resolve(8)));
        assertEquals(2, divByTwo.resolve(divByTwo.resolve(8.74)));
	}

	@Test
	void testInputNotNull()
	{
        assertThrows(IllegalArgumentException.class, () -> divByTwo.resolve((Number[]) null));
	}

	@Test
	void testInputNotEmpty()
	{
        assertThrows(IllegalArgumentException.class, () -> divByTwo.resolve());
	}

	@Test
	void testInputNotLongerThan1()
	{
        assertThrows(IllegalArgumentException.class, () -> divByTwo.resolve(4, 2.5));
	}

}
