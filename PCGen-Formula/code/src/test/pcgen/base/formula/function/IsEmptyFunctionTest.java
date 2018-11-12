/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.function;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class IsEmptyFunctionTest extends AbstractFormulaTestCase
{

	private static final Number[] EMPTY_ARR = {};
	private static final Number[] ARR_1 = {Integer.valueOf(1)};

	private ArrayFormatManager<Number> manager =
			new ArrayFormatManager<>(new NumberManager(), '\n', ',');

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "isEmpty(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, manager, Optional.empty());
	}

	@Test
	public void testNotValidString()
	{
		String formula = "isEmpty(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, manager, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "isEmpty(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, manager, Optional.empty());
	}

	@Test
	public void testEmptyArrayVar()
	{
		getVariableStore().put(getArrayVariable("a"), EMPTY_ARR);
		String formula = "isEmpty(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, manager, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
	}

	@Test
	public void testNotEmptyArrayVar()
	{
		getVariableStore().put(getArrayVariable("a"), ARR_1);
		String formula = "isEmpty(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, manager, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
	}

	protected VariableID<Number[]> getArrayVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		ScopeInstance globalInst = getInstanceFactory().getGlobalInstance("Global");
		variableLibrary.assertLegalVariableID(formula, globalInst.getLegalScope(),
			manager);
		@SuppressWarnings("unchecked")
		VariableID<Number[]> variableID =
				(VariableID<Number[]>) variableLibrary.getVariableID(globalInst, formula);
		return variableID;
	}

}
