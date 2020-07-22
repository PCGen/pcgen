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
package pcgen.base.formula.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class AbsFunctionTest extends AbstractFormulaTestCase
{

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "abs(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "abs(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidString()
	{
		String formula = "abs(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testIntegerPositive()
	{
		String formula = "abs(1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(1));
	}

	@Test
	public void testIntegerNegative()
	{
		String formula = "abs(-2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
	}

	@Test
	public void testDoublePositive()
	{
		String formula = "abs(6.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(6.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegative()
	{
		String formula = "abs(-5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegativeLeadingSpace()
	{
		String formula = "abs( -5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.3));
	}

	@Test
	public void testDoubleNegativeTrailingSpace()
	{
		String formula = "abs(-5.3 )";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.3));
	}

	@Test
	public void testDoubleNegativeSeparatingSpace()
	{
		String formula = "abs (-5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.3));
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(0, vars.size());
	}

	@Test
	public void testVariable()
	{
		getVariableStore().put(getVariable("a"), -5);
		String formula = "abs(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
	}
}
