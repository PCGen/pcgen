/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class LengthFunctionTest extends AbstractFormulaTestCase
{
	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "length(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, TestUtilities.NUMBER_ARRAY_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidString()
	{
		String formula = "length(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, TestUtilities.NUMBER_ARRAY_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "length(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, TestUtilities.NUMBER_ARRAY_MANAGER, Optional.empty());
	}

	@Test
	public void testVariable()
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		assertLegalVariable("a", "Global", TestUtilities.NUMBER_ARRAY_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number[]> variable = (VariableID<Number[]>) variableLibrary
			.getVariableID(getGlobalScopeInst(), "a");
		setVariable(variable, new Number[]{5});
		String formula = "length(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, TestUtilities.NUMBER_ARRAY_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1));
		setVariable(variable, new Number[]{5, 6, 7, 7, 8});
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5));
	}
}
