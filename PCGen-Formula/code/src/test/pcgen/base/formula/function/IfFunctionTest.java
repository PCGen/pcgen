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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class IfFunctionTest extends AbstractFormulaTestCase
{

	@Test
	public void testInvalidTooFewArg()
	{
		String formula = "if(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		formula = "if(2, 3)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidNotBool()
	{
		String formula = "if(2,4,5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidMMTarget()
	{
		String formula = "if(2>0,4,5<6)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidTrue()
	{
		String formula = "if(2>0,if(-0),5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidFalse()
	{
		String formula = "if(2>0,4,if(4))";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "if(ab,4,5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testIntegerPositive()
	{
		String formula = "if(1>0,2,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testIntegerNegative()
	{
		String formula = "if(-2<0,3,-4)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoublePositive()
	{
		String formula = "if(0.3>0,7.8,5.6)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(7.8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegative()
	{
		String formula = "if(-0.4<0,-3.4,-5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testZero()
	{
		String formula = "if(0==5,8.3,-3.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleZero()
	{
		String formula = "if(0.0==6.7,8.3,-3.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testNaryLeadingSpace()
	{
		String formula = "if( 4.6>0,-3.3,8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
	}

	@Test
	public void testNaryTrailingSpace()
	{
		String formula = "if(4.6>0,-3.3,8 )";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
	}

	@Test
	public void testNarySeparatingSpace()
	{
		String formula = "if(4.6>0 , -3.3 , 8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
	}

	@Test
	public void testNaryFunctionSeparatingSpace()
	{
		String formula = "if (4.6>0,-3.3,8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-3.3));
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(0, vars.size());
	}

	@Test
	public void testVar()
	{
		getVariableStore().put(getVariable("a"), 5);
		String formula = "if(4.6>0,a,8.1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
	}

	@Test
	public void testManyVar()
	{
		WriteableVariableStore variableStore = getVariableStore();
		variableStore.put(getVariable("a"), -5);
		variableStore.put(getVariable("b"), 5.1);
		variableStore.put(getVariable("c"), 3);
		String formula = "if(a>0,b,c)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(3));
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(3, vars.size());
		Set<String> set = new HashSet<String>();
		set.add("a");
		set.add("b");
		set.add("c");
		for (VariableID<?> vid: vars)
		{
			assertTrue(set.remove(vid.getName()));
		}
	}

	@Test
	public void testVariable1()
	{
		getVariableLibrary().assertLegalVariableID("a", getInstanceFactory().getScope("Global"), FormatUtilities.BOOLEAN_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Boolean> variable =
				(VariableID<Boolean>) getVariableLibrary().getVariableID(getGlobalScopeInst(), "a");
		getVariableStore().put(variable, true);
		String formula = "if(a, 4, 5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
	}

	@Test
	public void testVariable2()
	{
		getVariableStore().put(getVariable("a"), 5);
		String formula = "if(4<5, a, 3.4)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
	}

	@Test
	public void testVariable3()
	{
		getVariableStore().put(getVariable("a"), 5);
		String formula = "if(4<3, 3.4, a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
	}
}
