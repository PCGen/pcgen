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

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.format.NumberManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;

import plugin.modifier.testsupport.EvalManagerUtilities;

import org.junit.jupiter.api.Test;


public class SetNumberModifierTest
{

    private final PCGenScope varScope = new GlobalPCScope();
    FormatManager<Number> numManager = new NumberManager();

    @Test
    public void testInvalidConstruction()
    {
        try
        {
            SetModifierFactory m = new SetModifierFactory();
            m.getModifier(null, null);
            fail("Expected SetModifier with null set value to fail");
        } catch (IllegalArgumentException | NullPointerException e)
        {
            //Yep!
        }
    }

    @Test
    public void testProcessNegative1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-3, modifier.process(-2, -3));
    }

    @Test
    public void testProcessNegative2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-2, modifier.process(-4, -2));
    }

    @Test
    public void testProcessPositive1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3, modifier.process(2, 3));
    }

    @Test
    public void testProcessPositive2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3, modifier.process(4, 3));
    }

    @Test
    public void testProcessZero1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3, modifier.process(0, 3));
    }

    @Test
    public void testProcessZero2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(0, modifier.process(4, 0));
    }

    @Test
    public void testProcessZero3()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-3, modifier.process(0, -3));
    }

    @Test
    public void testProcessZero4()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(0, modifier.process(-4, 0));
    }

    @Test
    public void testProcessMixed1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-7, modifier.process(5, -7));
    }

    @Test
    public void testProcessMixed2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3, modifier.process(-4, 3));
    }

    @Test
    public void testProcessDoubleNegative1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-3.4, modifier.process(-2.3, -3.4));
    }

    @Test
    public void testProcessDoubleNegative2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-2.4, modifier.process(-4.3, -2.4));
    }

    @Test
    public void testProcessDoublePositive1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3.5, modifier.process(2.6, 3.5));
    }

    @Test
    public void testProcessDoublePositive2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3.1, modifier.process(4.4, 3.1));
    }

    @Test
    public void testProcessDoubleZero1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3.1, modifier.process(0.0, 3.1));
    }

    @Test
    public void testProcessDoubleZero2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(0.0, modifier.process(4.2, 0.0));
    }

    @Test
    public void testProcessDoubleZero3()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-3.4, modifier.process(0.0, -3.4));
    }

    @Test
    public void testProcessDoubleZero4()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(0.0, modifier.process(-4.3, 0.0));
    }

    @Test
    public void testProcessDoubleMixed1()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(-7.2, modifier.process(5.3, -7.2));
    }

    @Test
    public void testProcessDoubleMixed2()
    {
        SetModifierFactory modifier = new SetModifierFactory();
        assertEquals(3.1, modifier.process(-4.2, 3.1));
    }

    @Test
    public void testGetModifier()
    {
        SetModifierFactory factory = new SetModifierFactory();
        FormulaModifier<Number> modifier =
                factory.getModifier("6.5", numManager);
        modifier.addAssociation("PRIORITY=35");
        assertEquals((35L << 32) + factory.getInherentPriority(), modifier.getPriority());
        assertEquals(numManager, modifier.getVariableFormat());
        assertEquals(6.5, modifier.process(EvalManagerUtilities.getInputEM(4.3)));
    }

    @Test
    public void testGetFormulaModifier()
    {
        FormulaSetupFactory formulaSetupFactory = new FormulaSetupFactory();
        ScopeManagerInst legalScopeManager = new ScopeManagerInst();
        formulaSetupFactory.setLegalScopeManagerSupplier(() -> legalScopeManager);
        FormulaManager formulaManager = formulaSetupFactory.generate();
        legalScopeManager.registerScope(varScope);
        SetModifierFactory factory = new SetModifierFactory();
        FormulaModifier<Number> modifier = factory.getModifier("6+5", numManager);
        modifier.addAssociation("PRIORITY=35");
        assertEquals((35L << 32) + factory.getInherentPriority(), modifier.getPriority());
        assertEquals(numManager, modifier.getVariableFormat());
        EvaluationManager evalManager = EvalManagerUtilities.getInputEM(4.3);
        assertEquals(11, modifier.process(
                evalManager.getWith(EvaluationManager.FMANAGER, formulaManager)));
    }
}
