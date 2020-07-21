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

class AddingFormulaTest
{

    @Test
    void testToString()
    {
        assertEquals("+1", new AddingFormula(1).toString());
        assertEquals("+0", new AddingFormula(0).toString());
        assertEquals("+3", new AddingFormula(3).toString());
        assertEquals("-3", new AddingFormula(-3).toString());
    }


    @Test
    void testIdentity()
    {
        AddingFormula f = new AddingFormula(1);
        assertEquals(1, f.resolve(0));
        assertEquals(3, f.resolve(2.5));
    }

    @Test
    void testEquality()
    {
        AddingFormula f1 = new AddingFormula(1);
        AddingFormula f2 = new AddingFormula(1);
        AddingFormula f3 = new AddingFormula(2);
        AddingFormula f4 = new AddingFormula(-1);
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
        AddingFormula f = new AddingFormula(3);
        assertEquals(8, f.resolve(5));
        assertEquals(10, f.resolve(7.5));
    }

    @Test
    void testZero()
    {
        AddingFormula f = new AddingFormula(0);
        assertEquals(5, f.resolve(5));
        assertEquals(2, f.resolve(2.3));
    }

    @Test
    void testNegative()
    {
        AddingFormula f = new AddingFormula(-2);
        assertEquals(3, f.resolve(5));
        assertEquals(-8, f.resolve(-6.7));
    }

    @Test
    void testStackedFormula()
    {
        AddingFormula plusOne = new AddingFormula(1);
        assertEquals(3, plusOne.resolve(plusOne.resolve(1)));
        assertEquals(3, plusOne.resolve(plusOne.resolve(1.2435643516)));
    }

    @Test
    void testIntegerOverflow()
    {
        AddingFormula plusOne = new AddingFormula(1);
        assertEquals(Integer.MIN_VALUE, plusOne.resolve(Integer.MAX_VALUE));
    }

    @Test
    void testInputNotNull()
    {
        AddingFormula plusOne = new AddingFormula(1);
        assertThrows(IllegalArgumentException.class, () -> plusOne.resolve((Number[]) null));
    }

    @Test
    void testInputNotEmpty()
    {
        AddingFormula plusOne = new AddingFormula(1);
        assertThrows(IllegalArgumentException.class, () -> plusOne.resolve());
    }

    @Test
    void testInputNotLongerThan1()
    {
        AddingFormula plusOne = new AddingFormula(1);
        assertThrows(IllegalArgumentException.class, () -> plusOne.resolve(4, 2.5));
    }
}
