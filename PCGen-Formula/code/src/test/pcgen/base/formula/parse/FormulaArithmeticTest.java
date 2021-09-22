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

public class FormulaArithmeticTest extends AbstractFormulaTestCase
{

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		FormulaUtilities.loadBuiltInOperators(getOperatorLibrary());
	}

	@Test
	public void testIntegerPositive()
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
	public void testIntegerZero()
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
	public void testIntegerNegative()
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
	public void testDoubleOne()
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
	public void testDoublePositive()
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
	public void testDoubleNegative()
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
	public void testDoubleNegativeNoLeading()
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
	public void testDoublePositiveNoLeading()
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
	public void testAddIntegerInteger()
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
	public void testAddIntegerDouble()
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
	public void testAddDoubleInteger()
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
	public void testAddDoubleDouble()
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
	public void testAddIntegerNegativeInteger()
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
	public void testAddIntegerNegativeDouble()
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
	public void testAddDoubleNegativeInteger()
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
	public void testAddDoubleNegativeDouble()
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
	public void testAddNegativeIntegerInteger()
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
	public void testAddNegativeIntegerDouble()
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
	public void testAddNegativeDoubleInteger()
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
	public void testAddNegativeDoubleDouble()
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
	public void testAddMultiple()
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
	public void testAddMultipleWithDouble1()
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
	public void testAddMultipleWithDouble2()
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
	public void testAddLevelSetExpectationsOnIntegerMath()
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
	public void testSubtractIntegerInteger()
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
	public void testSubtractIntegerDouble()
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
	public void testSubtractDoubleInteger()
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
	public void testSubtractDoubleDouble()
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
	public void testSubtractIntegerNegativeInteger()
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
	public void testSubtractIntegerNegativeDouble()
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
	public void testSubtractDoubleNegativeInteger()
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
	public void testSubtractDoubleNegativeDouble()
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
	public void testSubtractNegativeIntegerInteger()
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
	public void testSubtractNegativeIntegerDouble()
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
	public void testSubtractNegativeDoubleInteger()
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
	public void testSubtractNegativeDoubleDouble()
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
	public void testSubtractMultiple()
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
	public void testSubtractMultipleWithDouble1()
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
	public void testSubtractMultipleWithDouble2()
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
	public void testEqualIntegerInteger()
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
	public void testEqualIntegerDouble()
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
	public void testEqualDoubleInteger()
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
	public void testEqualDoubleDouble()
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
	public void testEqualIntegerNegativeInteger()
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
	public void testEqualIntegerNegativeDouble()
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
	public void testEqualDoubleNegativeInteger()
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
	public void testEqualDoubleNegativeDouble()
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
	public void testEqualNegativeIntegerInteger()
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
	public void testEqualNegativeIntegerDouble()
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
	public void testEqualNegativeDoubleInteger()
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
	public void testEqualNegativeDoubleDouble()
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
	public void testEqualPositiveInteger()
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
	public void testEqualNegativeInteger()
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
	public void testEqualPositiveDouble()
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
	public void testEqualNegativeDouble()
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
	public void testEqualDoubleFirst()
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
	public void testEqualDoubleSecond()
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
	public void testNotEqualIntegerInteger()
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
	public void testNotEqualIntegerDouble()
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
	public void testNotEqualDoubleInteger()
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
	public void testNotEqualDoubleDouble()
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
	public void testNotEqualIntegerNegativeInteger()
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
	public void testNotEqualIntegerNegativeDouble()
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
	public void testNotEqualDoubleNegativeInteger()
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
	public void testNotEqualDoubleNegativeDouble()
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
	public void testNotEqualNegativeIntegerInteger()
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
	public void testNotEqualNegativeIntegerDouble()
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
	public void testNotEqualNegativeDoubleInteger()
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
	public void testNotEqualNegativeDoubleDouble()
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
	public void testNotEqualPositiveInteger()
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
	public void testNotEqualNegativeInteger()
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
	public void testNotEqualPositiveDouble()
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
	public void testNotEqualNegativeDouble()
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
	public void testNotEqualDoubleFirst()
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
	public void testNotEqualDoubleSecond()
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
	public void testLessThanIntegerInteger()
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
	public void testLessThanIntegerDouble()
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
	public void testLessThanDoubleInteger()
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
	public void testLessThanDoubleDouble()
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
	public void testLessThanIntegerNegativeInteger()
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
	public void testLessThanIntegerNegativeDouble()
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
	public void testLessThanDoubleNegativeInteger()
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
	public void testLessThanDoubleNegativeDouble()
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
	public void testLessThanNegativeIntegerInteger()
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
	public void testLessThanNegativeIntegerDouble()
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
	public void testLessThanNegativeDoubleInteger()
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
	public void testLessThanNegativeDoubleDouble()
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
	public void testLessThanPositiveInteger()
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
	public void testLessThanNegativeInteger()
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
	public void testLessThanPositiveDouble()
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
	public void testLessThanNegativeDouble()
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
	public void testLessThanDoubleFirst()
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
	public void testLessThanDoubleSecond()
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
	public void testGreaterThanIntegerInteger()
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
	public void testGreaterThanIntegerDouble()
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
	public void testGreaterThanDoubleInteger()
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
	public void testGreaterThanDoubleDouble()
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
	public void testGreaterThanIntegerNegativeInteger()
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
	public void testGreaterThanIntegerNegativeDouble()
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
	public void testGreaterThanDoubleNegativeInteger()
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
	public void testGreaterThanDoubleNegativeDouble()
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
	public void testGreaterThanNegativeIntegerInteger()
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
	public void testGreaterThanNegativeIntegerDouble()
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
	public void testGreaterThanNegativeDoubleInteger()
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
	public void testGreaterThanNegativeDoubleDouble()
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
	public void testGreaterThanPositiveInteger()
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
	public void testGreaterThanNegativeInteger()
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
	public void testGreaterThanPositiveDouble()
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
	public void testGreaterThanNegativeDouble()
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
	public void testGreaterThanDoubleFirst()
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
	public void testGreaterThanDoubleSecond()
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
	public void testLessThanOrEqualToIntegerInteger()
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
	public void testLessThanOrEqualToIntegerDouble()
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
	public void testLessThanOrEqualToDoubleInteger()
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
	public void testLessThanOrEqualToDoubleDouble()
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
	public void testLessThanOrEqualToIntegerNegativeInteger()
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
	public void testLessThanOrEqualToIntegerNegativeDouble()
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
	public void testLessThanOrEqualToDoubleNegativeInteger()
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
	public void testLessThanOrEqualToDoubleNegativeDouble()
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
	public void testLessThanOrEqualToNegativeIntegerInteger()
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
	public void testLessThanOrEqualToNegativeIntegerDouble()
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
	public void testLessThanOrEqualToNegativeDoubleInteger()
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
	public void testLessThanOrEqualToNegativeDoubleDouble()
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
	public void testLessThanOrEqualToPositiveInteger()
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
	public void testLessThanOrEqualToNegativeInteger()
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
	public void testLessThanOrEqualToPositiveDouble()
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
	public void testLessThanOrEqualToNegativeDouble()
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
	public void testLessThanOrEqualToDoubleFirst()
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
	public void testLessThanOrEqualToDoubleSecond()
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
	public void testGreaterThanOrEqualToIntegerInteger()
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
	public void testGreaterThanOrEqualToIntegerDouble()
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
	public void testGreaterThanOrEqualToDoubleInteger()
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
	public void testGreaterThanOrEqualToDoubleDouble()
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
	public void testGreaterThanOrEqualToIntegerNegativeInteger()
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
	public void testGreaterThanOrEqualToIntegerNegativeDouble()
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
	public void testGreaterThanOrEqualToDoubleNegativeInteger()
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
	public void testGreaterThanOrEqualToDoubleNegativeDouble()
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
	public void testGreaterThanOrEqualToNegativeIntegerInteger()
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
	public void testGreaterThanOrEqualToNegativeIntegerDouble()
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
	public void testGreaterThanOrEqualToNegativeDoubleInteger()
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
	public void testGreaterThanOrEqualToNegativeDoubleDouble()
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
	public void testGreaterThanOrEqualToPositiveInteger()
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
	public void testGreaterThanOrEqualToNegativeInteger()
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
	public void testGreaterThanOrEqualToPositiveDouble()
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
	public void testGreaterThanOrEqualToNegativeDouble()
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
	public void testGreaterThanOrEqualToDoubleFirst()
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
	public void testGreaterThanOrEqualToDoubleSecond()
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
	public void testMultiplyIntegerInteger()
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
	public void testMultiplyIntegerDouble()
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
	public void testMultiplyDoubleInteger()
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
	public void testMultiplyDoubleDouble()
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
	public void testMultiplyIntegerNegativeInteger()
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
	public void testMultiplyIntegerNegativeDouble()
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
	public void testMultiplyDoubleNegativeInteger()
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
	public void testMultiplyDoubleNegativeDouble()
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
	public void testMultiplyNegativeIntegerInteger()
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
	public void testMultiplyNegativeIntegerDouble()
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
	public void testMultiplyNegativeDoubleInteger()
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
	public void testMultiplyNegativeDoubleDouble()
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
	public void testMultiplyMultiple()
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
	public void testMultiplyMultipleWithDouble1()
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
	public void testMultiplyMultipleWithDouble2()
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
	public void testMultiplySetExpectations()
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
	public void testDivideIntegerInteger()
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
	public void testDivideIntegerDouble()
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
	public void testDivideDoubleInteger()
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
	public void testDivideDoubleDouble()
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
	public void testDivideIntegerNegativeInteger()
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
	public void testDivideIntegerNegativeDouble()
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
	public void testDivideDoubleNegativeInteger()
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
	public void testDivideDoubleNegativeDouble()
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
	public void testDivideNegativeIntegerInteger()
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
	public void testDivideNegativeIntegerDouble()
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
	public void testDivideNegativeDoubleInteger()
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
	public void testDivideNegativeDoubleDouble()
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
	public void testDivideMultiple()
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
	public void testDivideMultipleWithDouble1()
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
	public void testDivideMultipleWithDouble2()
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
	public void testDivideSetExpectationsNumerator()
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
	public void testDivideSetExpectations()
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
	public void testRemainderIntegerInteger()
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
	public void testRemainderIntegerDouble()
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
	public void testRemainderDoubleInteger()
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
	public void testRemainderDoubleDouble()
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
	public void testRemainderIntegerNegativeInteger()
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
	public void testRemainderIntegerNegativeDouble()
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
	public void testRemainderDoubleNegativeInteger()
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
	public void testRemainderDoubleNegativeDouble()
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
	public void testRemainderNegativeIntegerInteger()
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
	public void testRemainderNegativeIntegerDouble()
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
	public void testRemainderNegativeDoubleInteger()
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
	public void testRemainderNegativeDoubleDouble()
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
	public void testRemainderMultiple()
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
	public void testRemainderMultipleWithDouble1()
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
	public void testRemainderMultipleWithDouble2()
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
	public void testRemainderSetExpectationsNumerator()
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
	public void testRemainderSetExpectations()
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
	public void testExponentIntegerInteger()
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
	public void testExponentIntegerDouble()
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
	public void testExponentDoubleInteger()
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
	public void testExponentDoubleDouble()
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
	public void testExponentNegativeIntegerInteger()
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
	public void testExponentNegativeIntegerDouble()
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
	public void testExponentNegativeDoubleInteger()
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
	public void testExponentNegativeDoubleDouble()
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
	public void testExponentMultiple()
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
	public void testExponentMultipleWithDouble1()
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
	public void testExponentMultipleWithDouble2()
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
	public void testExponentSetExpectationsBase()
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
	public void testExponentSetExpectationsPower()
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
	public void testParens()
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
	public void testExtraParens()
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
	public void testNotValidBooleanAdd()
	{
		String formula = "(4<5)+(5>6)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidSubA()
	{
		String formula = "((4<5)+(5>6))-1";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidSubB()
	{
		String formula = "5+((4<5)+(5>6))";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidNoFunc()
	{
		String formula = "5+foo(5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}


	@Test
	public void testNotValidExponRoot()
	{
		String formula = "(4<5)^5";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidExpon()
	{
		String formula = "5^(9>8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}


	@Test
	public void testExponNotValidSubA()
	{
		String formula = "((4<5)+(5>6))^1";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}

	@Test
	public void testExponNotValidSubB()
	{
		String formula = "5^((4<5)+(5>6))";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, FormatUtilities.BOOLEAN_MANAGER, Optional.empty());
	}
}
