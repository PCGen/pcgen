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
package pcgen.base.formula.parse;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

class FormulaArithmeticTest extends AbstractFormulaTestCase
{

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		FormulaUtilities.loadBuiltInOperators(getOperatorLibrary());
	}

	@Test
	void testIntegerPositive()
	{
		String formula = "1";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testIntegerZero()
	{
		String formula = "0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testIntegerNegative()
	{
		String formula = "-5";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDoubleOne()
	{
		String formula = "1.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDoublePositive()
	{
		String formula = "1.1";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDoubleNegative()
	{
		String formula = "-4.5";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-4.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDoubleNegativeNoLeading()
	{
		String formula = "-.5";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDoublePositiveNoLeading()
	{
		String formula = ".2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddIntegerInteger()
	{
		String formula = "2+3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddIntegerDouble()
	{
		String formula = "2+3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddDoubleInteger()
	{
		String formula = "2.1+3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddDoubleDouble()
	{
		String formula = "2.1+3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddIntegerNegativeInteger()
	{
		String formula = "2+-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddIntegerNegativeDouble()
	{
		String formula = "2+-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-1.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddDoubleNegativeInteger()
	{
		String formula = "2.1+-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.9));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddDoubleNegativeDouble()
	{
		String formula = "2.1+-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-1.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddNegativeIntegerInteger()
	{
		String formula = "-2+3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddNegativeIntegerDouble()
	{
		String formula = "-2+3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddNegativeDoubleInteger()
	{
		String formula = "-2.1+3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.9));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddNegativeDoubleDouble()
	{
		String formula = "-2.1+3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddMultiple()
	{
		String formula = "1+4+7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(12));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddMultipleWithDouble1()
	{
		String formula = "1+4.1+7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(12.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddMultipleWithDouble2()
	{
		String formula = "1+4+7.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(12.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testAddLevelSetExpectationsOnIntegerMath()
	{
		String formula = "1.1+-1.1";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note this is a Double
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractIntegerInteger()
	{
		String formula = "2-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractIntegerDouble()
	{
		String formula = "2-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-1.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractDoubleInteger()
	{
		String formula = "2.1-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.9));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractDoubleDouble()
	{
		String formula = "2.1-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-1.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractIntegerNegativeInteger()
	{
		String formula = "2--3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractIntegerNegativeDouble()
	{
		String formula = "2--3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractDoubleNegativeInteger()
	{
		String formula = "2.1--3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractDoubleNegativeDouble()
	{
		String formula = "2.1--3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(5.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractNegativeIntegerInteger()
	{
		String formula = "-2-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractNegativeIntegerDouble()
	{
		String formula = "-2-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-5.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractNegativeDoubleInteger()
	{
		String formula = "-2.1-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-5.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractNegativeDoubleDouble()
	{
		String formula = "-2.1-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-5.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractMultiple()
	{
		String formula = "1-4-7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-10));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractMultipleWithDouble1()
	{
		String formula = "1-4.1-7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-10.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testSubtractMultipleWithDouble2()
	{
		String formula = "1-4-7.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-10.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualIntegerInteger()
	{
		String formula = "2==3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualIntegerDouble()
	{
		String formula = "2==3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleInteger()
	{
		String formula = "2.1==3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleDouble()
	{
		String formula = "2.1==3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualIntegerNegativeInteger()
	{
		String formula = "2==-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualIntegerNegativeDouble()
	{
		String formula = "2==-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleNegativeInteger()
	{
		String formula = "2.1==-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleNegativeDouble()
	{
		String formula = "2.1==-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeIntegerInteger()
	{
		String formula = "-2==3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeIntegerDouble()
	{
		String formula = "-2==3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeDoubleInteger()
	{
		String formula = "-2.1==3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeDoubleDouble()
	{
		String formula = "-2.1==3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualPositiveInteger()
	{
		String formula = "6==6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeInteger()
	{
		String formula = "-3==-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualPositiveDouble()
	{
		String formula = "3.3==3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualNegativeDouble()
	{
		String formula = "-0.3==-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleFirst()
	{
		String formula = "2.0==2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testEqualDoubleSecond()
	{
		String formula = "3==3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualIntegerInteger()
	{
		String formula = "2!=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualIntegerDouble()
	{
		String formula = "2!=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleInteger()
	{
		String formula = "2.1!=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleDouble()
	{
		String formula = "2.1!=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualIntegerNegativeInteger()
	{
		String formula = "2!=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualIntegerNegativeDouble()
	{
		String formula = "2!=-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleNegativeInteger()
	{
		String formula = "2.1!=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleNegativeDouble()
	{
		String formula = "2.1!=-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeIntegerInteger()
	{
		String formula = "-2!=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeIntegerDouble()
	{
		String formula = "-2!=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeDoubleInteger()
	{
		String formula = "-2.1!=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeDoubleDouble()
	{
		String formula = "-2.1!=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualPositiveInteger()
	{
		String formula = "6!=6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeInteger()
	{
		String formula = "-3!=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualPositiveDouble()
	{
		String formula = "3.3!=3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualNegativeDouble()
	{
		String formula = "-0.3!=-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleFirst()
	{
		String formula = "2.0!=2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotEqualDoubleSecond()
	{
		String formula = "3!=3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanIntegerInteger()
	{
		String formula = "2<3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanIntegerDouble()
	{
		String formula = "2<3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleInteger()
	{
		String formula = "2.1<3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleDouble()
	{
		String formula = "2.1<3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanIntegerNegativeInteger()
	{
		String formula = "2<-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanIntegerNegativeDouble()
	{
		String formula = "2<-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleNegativeInteger()
	{
		String formula = "2.1<-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleNegativeDouble()
	{
		String formula = "2.1<-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeIntegerInteger()
	{
		String formula = "-2<3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeIntegerDouble()
	{
		String formula = "-2<3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeDoubleInteger()
	{
		String formula = "-2.1<3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeDoubleDouble()
	{
		String formula = "-2.1<3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanPositiveInteger()
	{
		String formula = "6<6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeInteger()
	{
		String formula = "-3<-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanPositiveDouble()
	{
		String formula = "3.3<3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanNegativeDouble()
	{
		String formula = "-0.3<-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleFirst()
	{
		String formula = "2.0<2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanDoubleSecond()
	{
		String formula = "3<3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanIntegerInteger()
	{
		String formula = "2>3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanIntegerDouble()
	{
		String formula = "2>3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleInteger()
	{
		String formula = "2.1>3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleDouble()
	{
		String formula = "2.1>3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanIntegerNegativeInteger()
	{
		String formula = "2>-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanIntegerNegativeDouble()
	{
		String formula = "2>-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleNegativeInteger()
	{
		String formula = "2.1>-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleNegativeDouble()
	{
		String formula = "2.1>-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeIntegerInteger()
	{
		String formula = "-2>3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeIntegerDouble()
	{
		String formula = "-2>3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeDoubleInteger()
	{
		String formula = "-2.1>3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeDoubleDouble()
	{
		String formula = "-2.1>3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanPositiveInteger()
	{
		String formula = "6>6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeInteger()
	{
		String formula = "-3>-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanPositiveDouble()
	{
		String formula = "3.3>3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanNegativeDouble()
	{
		String formula = "-0.3>-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleFirst()
	{
		String formula = "2.0>2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanDoubleSecond()
	{
		String formula = "3>3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToIntegerInteger()
	{
		String formula = "2<=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToIntegerDouble()
	{
		String formula = "2<=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleInteger()
	{
		String formula = "2.1<=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleDouble()
	{
		String formula = "2.1<=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToIntegerNegativeInteger()
	{
		String formula = "2<=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToIntegerNegativeDouble()
	{
		String formula = "2<=-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleNegativeInteger()
	{
		String formula = "2.1<=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleNegativeDouble()
	{
		String formula = "2.1<=-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeIntegerInteger()
	{
		String formula = "-2<=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeIntegerDouble()
	{
		String formula = "-4<=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeDoubleInteger()
	{
		String formula = "-5.1<=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeDoubleDouble()
	{
		String formula = "-5.1<=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToPositiveInteger()
	{
		String formula = "6<=6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeInteger()
	{
		String formula = "-3<=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToPositiveDouble()
	{
		String formula = "3.3<=3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToNegativeDouble()
	{
		String formula = "-0.3<=-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleFirst()
	{
		String formula = "2.0<=2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testLessThanOrEqualToDoubleSecond()
	{
		String formula = "3<=3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToIntegerInteger()
	{
		String formula = "2>=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToIntegerDouble()
	{
		String formula = "2>=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleInteger()
	{
		String formula = "2.1>=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleDouble()
	{
		String formula = "2.1>=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToIntegerNegativeInteger()
	{
		String formula = "2>=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToIntegerNegativeDouble()
	{
		String formula = "2>=-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleNegativeInteger()
	{
		String formula = "2.1>=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleNegativeDouble()
	{
		String formula = "2.1>=-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeIntegerInteger()
	{
		String formula = "-2>=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeIntegerDouble()
	{
		String formula = "-2>=3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeDoubleInteger()
	{
		String formula = "-2.1>=3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeDoubleDouble()
	{
		String formula = "-2.1>=3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToPositiveInteger()
	{
		String formula = "6>=6";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeInteger()
	{
		String formula = "-3>=-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToPositiveDouble()
	{
		String formula = "3.3>=3.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToNegativeDouble()
	{
		String formula = "-0.3>=-0.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleFirst()
	{
		String formula = "2.0>=2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testGreaterThanOrEqualToDoubleSecond()
	{
		String formula = "3>=3.0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.BOOLEAN_MANAGER, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyIntegerInteger()
	{
		String formula = "2*3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyIntegerDouble()
	{
		String formula = "2*3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(6.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyDoubleInteger()
	{
		String formula = "2.1*3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(6.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyDoubleDouble()
	{
		String formula = "2.1*3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(7.14));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyIntegerNegativeInteger()
	{
		String formula = "2*-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyIntegerNegativeDouble()
	{
		String formula = "2*-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-6.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyDoubleNegativeInteger()
	{
		String formula = "2.1*-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-6.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyDoubleNegativeDouble()
	{
		String formula = "2.1*-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-7.14));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyNegativeIntegerInteger()
	{
		String formula = "-2*3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyNegativeIntegerDouble()
	{
		String formula = "-2*3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-6.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyNegativeDoubleInteger()
	{
		String formula = "-2.1*3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-6.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyNegativeDoubleDouble()
	{
		String formula = "-2.1*3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-7.14));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyMultiple()
	{
		String formula = "1*4*7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(28));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyMultipleWithDouble1()
	{
		String formula = "1*4.1*7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(28.7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplyMultipleWithDouble2()
	{
		String formula = "1*4*7.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(28.8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testMultiplySetExpectations()
	{
		String formula = "1.3*0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Don't expect a test for Integer == Zero, Double return is OK
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideIntegerInteger()
	{
		String formula = "2/3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.0 / 3.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideIntegerDouble()
	{
		String formula = "2/3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.625));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideDoubleInteger()
	{
		String formula = "2.1/3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideDoubleDouble()
	{
		String formula = "2.1/3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.1 / 3.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideIntegerNegativeInteger()
	{
		String formula = "2/-8";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.25));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideIntegerNegativeDouble()
	{
		String formula = "2/-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.625));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideDoubleNegativeInteger()
	{
		String formula = "2.1/-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideDoubleNegativeDouble()
	{
		String formula = "2.1/-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.1 / -3.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideNegativeIntegerInteger()
	{
		String formula = "-2/3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.0 / 3.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideNegativeIntegerDouble()
	{
		String formula = "-2/3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.625));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideNegativeDoubleInteger()
	{
		String formula = "-2.1/3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-0.7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideNegativeDoubleDouble()
	{
		String formula = "-2.1/3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.1 / 3.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideMultiple()
	{
		String formula = "1/4/7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.0 / 4.0 / 7.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideMultipleWithDouble1()
	{
		String formula = "1/4.1/7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1 / 4.1 / 7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideMultipleWithDouble2()
	{
		String formula = "1/4/7.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.0 / 4.0 / 7.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideSetExpectationsNumerator()
	{
		String formula = "0/1.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Don't expect a test for Numerator == Integer Zero, Double return is OK
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testDivideSetExpectations()
	{
		String formula = "1.3/0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Double.POSITIVE_INFINITY));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderIntegerInteger()
	{
		String formula = "2%3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderIntegerDouble()
	{
		String formula = "2%3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderDoubleInteger()
	{
		String formula = "3.1%2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderDoubleDouble()
	{
		String formula = "2.1%3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderIntegerNegativeInteger()
	{
		String formula = "2%-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderIntegerNegativeDouble()
	{
		String formula = "2%-3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderDoubleNegativeInteger()
	{
		String formula = "2.1%-3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderDoubleNegativeDouble()
	{
		String formula = "2.1%-3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderNegativeIntegerInteger()
	{
		String formula = "-2%3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderNegativeIntegerDouble()
	{
		String formula = "-2%3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderNegativeDoubleInteger()
	{
		String formula = "-2.1%3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderNegativeDoubleDouble()
	{
		String formula = "-2.1%3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderMultiple()
	{
		String formula = "19%8%2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderMultipleWithDouble1()
	{
		String formula = "9%4.1%2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderMultipleWithDouble2()
	{
		String formula = "9%6%1.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderSetExpectationsNumerator()
	{
		String formula = "0%1.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Don't expect a test for Numerator == Integer Zero, Double return is OK
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testRemainderSetExpectations()
	{
		String formula = "1.3%0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Double.NaN));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentIntegerInteger()
	{
		String formula = "2^3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		//Note expectation setting here - NOT integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentIntegerDouble()
	{
		String formula = "2^3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Math.pow(2.0, 3.2)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentDoubleInteger()
	{
		String formula = "2.1^3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(9.261));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentDoubleDouble()
	{
		String formula = "2.1^3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Math.pow(2.1, 3.4)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentNegativeIntegerInteger()
	{
		String formula = "-2^3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Note integer math
		assertTrue(getVariables(node).isEmpty());
		//Note expectation setting here - NOT integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentNegativeIntegerDouble()
	{
		String formula = "-2^3.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-Math.pow(2.0, 3.2)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentNegativeDoubleInteger()
	{
		String formula = "-2.1^3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-9.261));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentNegativeDoubleDouble()
	{
		String formula = "-2.1^3.4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-Math.pow(2.1, 3.4)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentMultiple()
	{
		String formula = "1.03^4^7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Math.pow(1.03, 28)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentMultipleWithDouble1()
	{
		String formula = "1.03^4.1^7";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Math.pow(1.03, 28.7)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentMultipleWithDouble2()
	{
		String formula = "1.03^4^7.2";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(Math.pow(1.03, 28.8)));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentSetExpectationsBase()
	{
		String formula = "0^1.3";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Don't expect a test for Numerator == Integer Zero, Double return is OK
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExponentSetExpectationsPower()
	{
		String formula = "1.3^0";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		//Don't expect a test for Power == Integer Zero, Double return is OK
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.0));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testParens()
	{
		String formula = "3*(1+2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(9));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testExtraParens()
	{
		String formula = "((4/(((3-1)))))";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		assertTrue(getVariables(node).isEmpty());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	void testNotValidBooleanAdd()
	{
		String formula = "(4<5)+(5>6)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	void testNotValidSubA()
	{
		String formula = "((4<5)+(5>6))-1";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	void testNotValidSubB()
	{
		String formula = "5+((4<5)+(5>6))";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	void testNotValidNoFunc()
	{
		String formula = "5+foo(5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}


	@Test
	void testNotValidExponRoot()
	{
		String formula = "(4<5)^5";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	void testNotValidExpon()
	{
		String formula = "5^(9>8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}


	@Test
	void testExponNotValidSubA()
	{
		String formula = "((4<5)+(5>6))^1";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	void testExponNotValidSubB()
	{
		String formula = "5^((4<5)+(5>6))";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}
}
