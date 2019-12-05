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
import pcgen.rules.persistence.token.ModifierFactory;

import plugin.modifier.testsupport.EvalManagerUtilities;

import org.junit.jupiter.api.Test;


class AddNumberModifierTest
{
    private FormatManager<Number> numManager = new NumberManager();

    @Test
    public void testInvalidConstruction()
    {
        try
        {
            ModifierFactory m = new AddModifierFactory();
            m.getModifier(null, null);
            fail("Expected AddModifier with null adder to fail");
        } catch (IllegalArgumentException | NullPointerException e)
        {
            //Yep!
        }
    }

    @Test
    public void testProcessNegative1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-5, modifier.process(-2, -3));
    }

    @Test
    public void testProcessNegative2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-6, modifier.process(-4, -2));
    }

    @Test
    public void testProcessPositive1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(5, modifier.process(2, 3));
    }

    @Test
    public void testProcessPositive2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(7, modifier.process(4, 3));
    }

    @Test
    public void testProcessZero1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(3, modifier.process(0, 3));
    }

    @Test
    public void testProcessZero2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(4, modifier.process(4, 0));
    }

    @Test
    public void testProcessZero3()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-3, modifier.process(0, -3));
    }

    @Test
    public void testProcessZero4()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-4, modifier.process(-4, 0));
    }

    @Test
    public void testProcessMixed1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-2, modifier.process(5, -7));
    }

    @Test
    public void testProcessMixed2()
    {
        BasicCalculation modifier = new AddModifierFactory();
        assertEquals(-1, modifier.process(-4, 3));
    }

    @Test
    public void testProcessDoubleNegative1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-1.7, modifier.process(-0.3, -1.4));
    }

    @Test
    public void testProcessDoubleNegative2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-7.1, modifier.process(-4.7, -2.4));
    }

    @Test
    public void testProcessDoublePositive1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(6.1, modifier.process(2.6, 3.5));
    }

    @Test
    public void testProcessDoublePositive2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(7.5, modifier.process(4.4, 3.1));
    }

    @Test
    public void testProcessDoubleZero1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(3.1, modifier.process(0.0, 3.1));
    }

    @Test
    public void testProcessDoubleZero2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(4.2, modifier.process(4.2, 0.0));
    }

    @Test
    public void testProcessDoubleZero3()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-3.4, modifier.process(0.0, -3.4));
    }

    @Test
    public void testProcessDoubleZero4()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-4.3, modifier.process(-4.3, 0.0));
    }

    @Test
    public void testProcessDoubleMixed1()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(1.6, modifier.process(3.2, -1.6));
    }

    @Test
    public void testProcessDoubleMixed2()
    {
        BasicCalculation<Number> modifier = new AddModifierFactory();
        assertEquals(-1.1, modifier.process(-4.2, 3.1));
    }

    @Test
    public void testGetModifier()
    {
        AddModifierFactory factory = new AddModifierFactory();
        FormulaModifier<Number> modifier =
                factory.getModifier("6.5", numManager);
        modifier.addAssociation("PRIORITY=35");
        assertEquals((35L << 32) + factory.getInherentPriority(), modifier.getPriority());
        assertEquals(numManager, modifier.getVariableFormat());
        assertEquals(10.8, modifier.process(EvalManagerUtilities.getInputEM(4.3)));
    }
}
