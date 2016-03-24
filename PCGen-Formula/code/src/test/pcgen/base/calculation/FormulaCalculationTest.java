/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

import org.junit.Test;

import pcgen.base.calculation.testsupport.BasicCalc;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.SimpleVariableStore;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.testsupport.AbstractFormulaTestCase;

public class FormulaCalculationTest extends AbstractFormulaTestCase
{
	private static final String _4_CEIL_4_3 = "4+ceil(4.3)";
	private BasicCalculation basic = new BasicCalc(new NumberAdd());
	private ComplexNEPFormula formula = new ComplexNEPFormula(_4_CEIL_4_3);

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		FormulaUtilities.loadBuiltInFunctions(getFunctionLibrary());
		FormulaUtilities.loadBuiltInOperators(getOperatorLibrary());
	}

	@Test
	public void testConstructor()
	{
		try
		{
			new FormulaCalculation(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new FormulaCalculation(null, basic);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new FormulaCalculation(formula, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testProcess()
	{
		EvaluationManager manager = new EvaluationManager();
		manager.set(EvaluationManager.FMANAGER, getFormulaManager());
		manager.set(EvaluationManager.INSTANCE, getGlobalScopeInst());
		manager.set(EvaluationManager.ASSERTED, Number.class);
		FormulaCalculation fc = new FormulaCalculation(formula, basic);
		manager.set(EvaluationManager.INPUT, 8);
		assertEquals(17, fc.process(manager));
		FormulaCalculation fc2 =
				new FormulaCalculation(new ComplexNEPFormula("value()"), basic);
		manager.set(EvaluationManager.INPUT, 8);
		assertEquals(16, fc2.process(manager));
	}

	@Test
	public void testRoundRobin()
	{
		FormulaCalculation fc = new FormulaCalculation(formula, basic);
		assertEquals(_4_CEIL_4_3, fc.getInstructions());
		FormulaCalculation fc2 =
				new FormulaCalculation(new ComplexNEPFormula("value()"), basic);
		assertEquals("value()", fc2.getInstructions());
	}

	@Test
	public void testArray()
	{
		FormulaManager formulaManager = getFormulaManager();
		SimpleVariableStore vs =
				(SimpleVariableStore) formulaManager.getResolver();
		ArrayFormatManager aManager =
				new ArrayFormatManager(numberManager, ',');
		getVariableLibrary().assertLegalVariableID("arr", getGlobalScope(),
			aManager);
		VariableID varID =
				new VariableID(getGlobalScopeInst(), aManager, "arr");
		vs.put(varID, new Number[]{4, 5, 6, 7, 8, 9});
		FormulaCalculation fc2 =
				new FormulaCalculation(new ComplexNEPFormula("arr[5]"), basic);
		EvaluationManager manager =
				EvaluationManager.generate(getFormulaManager(),
					getGlobalScopeInst(), Number.class);
		manager.set(EvaluationManager.INPUT, 8);
		assertEquals(17, fc2.process(manager));
	}

}
