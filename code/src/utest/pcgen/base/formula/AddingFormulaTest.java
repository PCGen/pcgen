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

class AddingFormulaTest
{

    private AddingFormula f;

    @BeforeEach
    void setUp()
    {
        f = new AddingFormula(5);
    }

    @Test
    public void testToString()
    {
        assertEquals("+1", new AddingFormula(1).toString());
        assertEquals("+0", new AddingFormula(0).toString());
        assertEquals("+3", new AddingFormula(3).toString());
        assertEquals("-3", new AddingFormula(-3).toString());
    }


    @Test
    public void testIdentity()
    {
        AddingFormula f = new AddingFormula(1);
        assertEquals(1, f.resolve(0).intValue());
        assertEquals(3, f.resolve(2.5).intValue());
    }

    @Test
    public void testEquality()
    {
        AddingFormula f1 = new AddingFormula(1);
        AddingFormula f2 = new AddingFormula(1);
        AddingFormula f3 = new AddingFormula(2);
        AddingFormula f4 = new AddingFormula(-1);
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
        AddingFormula f = new AddingFormula(3);
        assertEquals(8, f.resolve(5).intValue());
        assertEquals(10, f.resolve(7.5).intValue());
    }

    @Test
    public void testZero()
    {
        AddingFormula f = new AddingFormula(0);
        assertEquals(5, f.resolve(5).intValue());
        assertEquals(2, f.resolve(2.3).intValue());
    }

    @Test
    public void testNegative()
    {
        AddingFormula f = new AddingFormula(-2);
        assertEquals(3, f.resolve(5).intValue());
        assertEquals(-8, f.resolve(-6.7).intValue());
    }

    @Test
    public void testInputNotNull()
    {
        assertThrows(IllegalArgumentException.class, () -> f.resolve((Number[]) null));
    }

    @Test
    public void testInputNotEmpty()
    {
        assertThrows(IllegalArgumentException.class, () -> f.resolve());
    }

    @Test
    public void testInputNotLongerThan1()
    {
        assertThrows(IllegalArgumentException.class, () -> f.resolve(4, 2.5));
    }
}
