/*
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import pcgen.cdom.base.FormulaFactory;

import org.junit.jupiter.api.Test;

/**
 * This class tests the handling of DRs in PCGen
 */
class DamageReductionTest
{

    /**
     * Test the basic DR Handling
     */
    @Test
    public void testBasicDRHandling()
    {
        DamageReduction dr1 = new DamageReduction(FormulaFactory
                .getFormulaFor(5), "magic");
        DamageReduction dr2 = new DamageReduction(FormulaFactory
                .getFormulaFor(5), "-");
        assertNotEquals(dr1, dr2);

        dr2 = new DamageReduction(FormulaFactory.getFormulaFor(5), "Magic");
        assertEquals(dr1, dr2);

        dr2 = new DamageReduction(FormulaFactory.getFormulaFor(10), "magic");
        assertNotEquals(dr1, dr2);

        dr1 = new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic and good");
        dr2 = new DamageReduction(FormulaFactory.getFormulaFor(10),
                "good and magic");
        assertEquals(dr1, dr2);

        dr2 = new DamageReduction(FormulaFactory.getFormulaFor(10),
                "Good and magic");
        assertEquals(dr1, dr2);

        /*
         * TODO DR can be fooled
         */
        // dr1 = new DamageReduction(FormulaFactory.getFormulaFor(10), "magic or
        // good");
        // dr2 = new DamageReduction(FormulaFactory.getFormulaFor(10), "good and
        // magic");
        // assertFalse(dr1.equals(dr2)));
    }
}
