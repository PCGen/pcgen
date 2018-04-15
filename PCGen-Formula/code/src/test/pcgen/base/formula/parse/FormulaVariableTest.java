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

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.operator.bool.BooleanNot;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.formula.operator.number.NumberDivide;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberMultiply;
import pcgen.base.formula.operator.number.NumberSubtract;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;
import pcgen.base.util.FormatManager;

public class FormulaVariableTest extends AbstractFormulaTestCase
{

	private static final FormatManager<?> booleanManager =
			FormatUtilities.BOOLEAN_MANAGER;

	private WriteableVariableStore store;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		store = getVariableStore();
		OperatorLibrary operatorLibrary = getOperatorLibrary();
		operatorLibrary.addAction(new NumberEquals());
		operatorLibrary.addAction(new NumberAdd());
		operatorLibrary.addAction(new BooleanNot());
		operatorLibrary.addAction(new NumberSubtract());
		operatorLibrary.addAction(new NumberDivide());
		operatorLibrary.addAction(new NumberMultiply());
	}

	@Test
	public void testSimpleVariablePositive()
	{
		String formula = "a";
		store.put(getVariable(formula), 5);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var0 = vars.get(0);
		assertEquals("a", var0.getName());
		assertEquals(getGlobalScopeInst(), var0.getScope());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testSimpleVariableNegative()
	{
		String formula = "a";
		store.put(getVariable(formula), -7);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var0 = vars.get(0);
		assertEquals("a", var0.getName());
		assertEquals(getGlobalScopeInst(), var0.getScope());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testSimpleVariableDouble()
	{
		String formula = "a";
		store.put(getVariable(formula), -7.3);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var0 = vars.get(0);
		assertEquals("a", var0.getName());
		assertEquals(getGlobalScopeInst(), var0.getScope());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-7.3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testAddVariableVariable()
	{
		String formula = "a+b";
		store.put(getVariable("a"), 3);
		store.put(getVariable("b"), 7);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(10));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testAddVariableVariableDouble()
	{
		String formula = "a+b";
		store.put(getVariable("a"), 3.2);
		store.put(getVariable("b"), -7.9);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-4.7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testAddMultiple()
	{
		String formula = "a+b+c";
		store.put(getVariable("a"), 3.2);
		store.put(getVariable("b"), -7.9);
		store.put(getVariable("c"), 2.2);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABCVars(node);
		//Note integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(-2.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testSubtractInteger()
	{
		String formula = "a-3";
		store.put(getVariable("a"), 3.2);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var0 = vars.get(0);
		assertEquals("a", var0.getName());
		assertEquals(getGlobalScopeInst(), var0.getScope());
		//Note integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(0.2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testSubtractVariableVariable()
	{
		String formula = "a-b";
		store.put(getVariable("a"), 3.2);
		store.put(getVariable("b"), 2.1);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testEqualVariableDifferentVariable()
	{
		String formula = "a==b";
		store.put(getVariable("a"), 3.2);
		store.put(getVariable("b"), 2.1);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, booleanManager, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(booleanManager, formula, node, Boolean.FALSE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testEqualVariableDifferentType()
	{
		String formula = "a==b";
		store.put(getVariable("a"), 3.2);
		getVariableStore().put(getBooleanVariable("b"), false);
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isNotValid(formula, node, booleanManager, Optional.empty());
	}

	@Test
	public void testEqualZeroVariableZeroVariable()
	{
		String formula = "a==b";
		store.put(getVariable("a"), 0.0);
		store.put(getVariable("b"), 0);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, booleanManager, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(booleanManager, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testEqualVariableEqualVariable()
	{
		String formula = "a==b";
		store.put(getVariable("a"), -2.1);
		store.put(getVariable("b"), -2.1);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, booleanManager, Optional.empty());
		isStatic(formula, node, false);
		hasABVars(node);
		//Note integer math
		evaluatesTo(booleanManager, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	//TODO Not Equal
	//TODO Less Than
	//TODO Greater Than
	//TODO Less Than or Equal To
	//TODO Greater Than or Equal To
	//TODO Multiply
	//TODO Divide
	//TODO Remainder
	//TODO Exp

	@Test
	public void testParens()
	{
		String formula = "a*(b+c)";
		store.put(getVariable("a"), 2);
		store.put(getVariable("b"), 1.2);
		store.put(getVariable("c"), -0.3);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABCVars(node);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(1.8));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testExtraParens()
	{
		String formula = "((a/(((b-c)))))";
		store.put(getVariable("a"), 3);
		store.put(getVariable("b"), 1.2);
		store.put(getVariable("c"), -0.3);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, false);
		hasABCVars(node);
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	private void hasABCVars(SimpleNode node)
	{
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(3, vars.size());
		VariableID<?> var0 = vars.get(0);
		VariableID<?> var1 = vars.get(1);
		VariableID<?> var2 = vars.get(2);
		//Order independent
		if (var0.getName().equals("a"))
		{
			if (var1.getName().equals("b"))
			{
				assertEquals("c", var2.getName());
			}
			else
			{
				//a is var1
				assertEquals("c", var1.getName());
				assertEquals("b", var2.getName());
			}
		}
		else if (var0.getName().equals("b"))
		{
			if (var1.getName().equals("a"))
			{
				assertEquals("c", var2.getName());
			}
			else
			{
				//a is var2
				assertEquals("c", var1.getName());
				assertEquals("a", var2.getName());
			}
		}
		else
		{
			//"c" is var0
			assertEquals("c", var0.getName());
			if (var1.getName().equals("a"))
			{
				assertEquals("b", var2.getName());
			}
			else
			{
				//a is var2
				assertEquals("b", var1.getName());
				assertEquals("a", var2.getName());
			}
		}

		assertEquals(getGlobalScopeInst(), var0.getScope());
		assertEquals(getGlobalScopeInst(), var1.getScope());
		assertEquals(getGlobalScopeInst(), var2.getScope());
	}

	private void hasABVars(SimpleNode node)
	{
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(2, vars.size());
		VariableID<?> var0 = vars.get(0);
		VariableID<?> var1 = vars.get(1);
		//Order independent
		if (var0.getName().equals("a"))
		{
			assertEquals("b", var1.getName());
		}
		else
		{
			//a is var1
			assertEquals("b", var0.getName());
			assertEquals("a", var1.getName());
		}
		assertEquals(getGlobalScopeInst(), var0.getScope());
		assertEquals(getGlobalScopeInst(), var1.getScope());
	}

	@Test
	public void testBooleanNegation()
	{
		String formula = "!a";
		store.put(getBooleanVariable("a"), Boolean.FALSE);
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, booleanManager, Optional.empty());
		isStatic(formula, node, false);
		List<VariableID<?>> vars = getVariables(node);
		assertEquals(1, vars.size());
		VariableID<?> var0 = vars.get(0);
		assertEquals("a", var0.getName());
		evaluatesTo(booleanManager, formula, node, Boolean.TRUE);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

}
