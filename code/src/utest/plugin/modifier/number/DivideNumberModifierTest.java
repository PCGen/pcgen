/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.modifier.number;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.format.NumberManager;
import pcgen.base.util.FormatManager;

import plugin.modifier.testsupport.EvalManagerUtilities;

import org.junit.jupiter.api.Test;


public class DivideNumberModifierTest
{

    private final FormatManager<Number> numManager = new NumberManager();

    @Test
    public void testInvalidConstruction()
    {
        try
        {
            DivideModifierFactory m = new DivideModifierFactory();
            m.getModifier(null, null);
            fail("Expected DivideModifierFactory with null divide value to fail");
        } catch (IllegalArgumentException | NullPointerException e)
        {
            //Yep!
        }
    }

    @Test
    public void testProcessNegative1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-2, modifier.process(6, -3));
    }

    @Test
    public void testProcessNegative2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-4, modifier.process(8, -2));
    }

    @Test
    public void testProcessPositive1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(2, modifier.process(6, 3));
    }

    @Test
    public void testProcessPositive2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(4, modifier.process(12, 3));
    }

    @Test
    public void testProcessZero1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(0, modifier.process(0, 3));
    }

    @Test
    public void testProcessZero2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(Double.POSITIVE_INFINITY, modifier.process(4, 0));
    }

    @Test
    public void testProcessZero3()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(0, modifier.process(0, -3));
    }

    @Test
    public void testProcessZero4()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(Double.NEGATIVE_INFINITY, modifier.process(-4, 0));
    }

    @Test
    public void testProcessMixed1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(5, modifier.process(-35, -7));
    }

    @Test
    public void testProcessMixed2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-4, modifier.process(-12, 3));
    }

    @Test
    public void testProcessDoubleNegative1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-2.1, modifier.process(3.57, -1.7));
    }

    @Test
    public void testProcessDoubleNegative2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-2.6, modifier.process(4.16, -1.6));
    }

    @Test
    public void testProcessDoublePositive1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(2.6, modifier.process(9.1, 3.5));
    }

    @Test
    public void testProcessDoublePositive2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(1.9, modifier.process(5.89, 3.1));
    }

    @Test
    public void testProcessDoubleZero1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(0.0, modifier.process(0.0, 3.1));
    }

    @Test
    public void testProcessDoubleZero2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(Double.POSITIVE_INFINITY, modifier.process(4.2, 0.0));
    }

    @Test
    public void testProcessDoubleZero3()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-0.0, modifier.process(0.0, -3.4));
    }

    @Test
    public void testProcessDoubleZero4()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(Double.NEGATIVE_INFINITY, modifier.process(-4.3, 0.0));
    }

    @Test
    public void testProcessDoubleMixed1()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(5.3, modifier.process(-38.16, -7.2));
    }

    @Test
    public void testProcessDoubleMixed2()
    {
        BasicCalculation<Number> modifier = new DivideModifierFactory();
        assertEquals(-2.2, modifier.process(-3.08, 1.4));
    }

    @Test
    public void testGetModifier()
    {
        DivideModifierFactory factory = new DivideModifierFactory();
        FormulaModifier<Number> modifier =
                factory.getModifier("4.3", numManager);
        modifier.addAssociation("PRIORITY=35");
        assertEquals((35L << 32) + factory.getInherentPriority(), modifier.getPriority());
        assertEquals(numManager, modifier.getVariableFormat());
        assertEquals(3.2, modifier.process(EvalManagerUtilities.getInputEM(13.76)));
    }
}
