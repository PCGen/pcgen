/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.FormulaFactory;

import org.junit.jupiter.api.Test;


class NamedFormulaTest
{

    @SuppressWarnings("unused")
    @Test
    public void testNullConstructor()
    {
        try
        {
            new NamedFormula(null, FormulaFactory.getFormulaFor("1"));
            fail("Expected NamedFormula to reject null argument in constructor");
        } catch (NullPointerException e)
        {
            // OK
        }
        try
        {
            new NamedFormula("Name", null);
            fail("Expected NamedFormula to reject null argument in constructor");
        } catch (NullPointerException e)
        {
            // OK
        }
    }

    @Test
    public void testBasics()
    {
        NamedFormula nf1 =
                new NamedFormula("Foo", FormulaFactory.getFormulaFor("2"));
        assertEquals("Foo", nf1.getName());
        assertEquals(FormulaFactory.getFormulaFor(2), nf1.getFormula());
    }

    @Test
    public void testToString()
    {
        NamedFormula nf1 =
                new NamedFormula("Foo", FormulaFactory.getFormulaFor("2"));
        assertEquals("Foo:2", nf1.toString());
    }
}
