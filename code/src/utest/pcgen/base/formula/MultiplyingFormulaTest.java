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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class MultiplyingFormulaTest
{
    private MultiplyingFormula timesOne;

	@BeforeEach
	void setUp()
	{
        timesOne = new MultiplyingFormula(1);
	}

	@Test
	void testToString()
	{
		assertEquals("*1", new MultiplyingFormula(1).toString());
		assertEquals("*3", new MultiplyingFormula(3).toString());
		assertEquals("*0", new MultiplyingFormula(0).toString());
		assertEquals("*-3", new MultiplyingFormula(-3).toString());
	}

	@Test
	void testIdentity()
	{
		MultiplyingFormula f = new MultiplyingFormula(1);
        assertEquals(2, f.resolve(2));
        assertEquals(2, f.resolve(2.5));
	}

	@Test
	void testZero()
	{
		MultiplyingFormula f = new MultiplyingFormula(0);
        assertEquals(0, f.resolve(5));
        assertEquals(0, f.resolve(2.3));
	}

	@Test
	void testEquality()
	{
		MultiplyingFormula f1 = new MultiplyingFormula(1);
		MultiplyingFormula f2 = new MultiplyingFormula(1);
		MultiplyingFormula f3 = new MultiplyingFormula(2);
		MultiplyingFormula f4 = new MultiplyingFormula(-1);
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
		MultiplyingFormula f = new MultiplyingFormula(3);
        assertEquals(15, f.resolve(5));
	}

	@Test
	void testNegative()
	{
		MultiplyingFormula f = new MultiplyingFormula(-2);
        assertEquals(-10, f.resolve(5));
	}

    @Test
    void testRoundsLikeIntegerCastOnResult()
    {
        MultiplyingFormula negative = new MultiplyingFormula(-2);
        MultiplyingFormula positive = new MultiplyingFormula(2);
        assertEquals((int) (-6.7 * -2), negative.resolve(-6.7));
        assertEquals((int) (2 * 5.5), positive.resolve(5.5));
    }

	@Test
	void testInputNotNull()
	{
        assertThrows(IllegalArgumentException.class, () -> timesOne.resolve((Number[]) null));
	}

	@Test
	void testInputNotEmpty()
	{
        assertThrows(IllegalArgumentException.class, () -> timesOne.resolve());
	}

	@Test
	void testInputNotLongerThan1()
	{
        assertThrows(IllegalArgumentException.class, () -> timesOne.resolve(4, 2.5));
	}

}
