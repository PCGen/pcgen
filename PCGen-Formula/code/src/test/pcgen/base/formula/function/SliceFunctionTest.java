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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class SliceFunctionTest extends AbstractFormulaTestCase
{

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "slice(a, 2, 3, 4)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidTooFewArg()
	{
		String formula = "slice(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidString()
	{
		String formula = "slice(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "slice(ab, 0, 1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testSingleItem()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,1,2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, new Number[] {2});
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testBoundedSlice()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,1,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, new Number[] {2, 3});
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testUnboundedSlice()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, new Number[] {4, 5, 6, 7});
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testNegativeStart()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,-3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalid()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,3,1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testWastedEmpty()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,3,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNonIntegerStart()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,3.1,1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNonIntegerEnd()
	{
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		getVariableStore().put(getArray("ab"), array);
		String formula = "slice(ab,3,1.2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	protected VariableID<Number[]> getArray(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula,
			getInstanceFactory().getScope("Global"), TestUtilities.NUMBER_ARRAY_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number[]> variableID = (VariableID<Number[]>) variableLibrary
			.getVariableID(getGlobalScopeInst(), formula);
		return variableID;
	}
}
