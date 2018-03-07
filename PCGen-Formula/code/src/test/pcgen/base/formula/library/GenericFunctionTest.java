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
package pcgen.base.formula.library;

import java.util.Optional;

import org.junit.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class GenericFunctionTest extends AbstractFormulaTestCase
{

	private ArgumentDependencyManager argManager;
	private DependencyManager depManager;
	private DependencyVisitor varCapture;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		String formula = "floor((arg(0)-10)/2)";
		SimpleNode node = TestUtilities.doParse(formula);
		FunctionLibrary ftnLibrary = getFunctionLibrary();
		ftnLibrary.addFunction(new GenericFunction("d20Mod", node));
		resetManager();
	}

	private void resetManager()
	{
		depManager = getManagerFactory().generateDependencyManager(getFormulaManager(),
			getGlobalScopeInst());
		depManager = getManagerFactory().withVariables(depManager);
		argManager = new ArgumentDependencyManager();
		depManager = depManager.getWith(ArgumentDependencyManager.KEY,
			Optional.of(argManager));
		varCapture = new DependencyVisitor();
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "d20Mod()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		formula = "d20Mod(2, 3)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		String formula2 = "floor((14-10)/2)";
		SimpleNode node2 = TestUtilities.doParse(formula2);
		getFunctionLibrary().addFunction(new GenericFunction("noargs", node2));
		formula = "noargs(2)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNoArgs()
	{
		String formula2 = "floor((14-10)/2)";
		SimpleNode node2 = TestUtilities.doParse(formula2);
		getFunctionLibrary().addFunction(new GenericFunction("noargs", node2));
		String formula = "noargs()";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(-1, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		resetManager();
		varCapture.visit(node, depManager);
		assertEquals(-1, argManager.getMaximumArgument());
	}

	@Test
	public void testInvalidNaN()
	{
		String formula = "d20Mod(\"string\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testArgOne()
	{
		String formula = "d20Mod(14)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		resetManager();
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
	}

	@Test
	public void testComplex()
	{
		String formula = "d20Mod(4+abs(-12))";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(3));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		resetManager();
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
	}

	@Test
	public void testEmbedded1()
	{
		String formula2 = "floor((arg(0)-arg(1))/2)";
		SimpleNode node2 = TestUtilities.doParse(formula2);
		getFunctionLibrary().addFunction(new GenericFunction("embed", node2));
		String formula = "d20Mod(embed(14,10))";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(-4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		resetManager();
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
	}

	@Test
	public void testEmbedded2()
	{
		String formula2 = "floor((arg(0)-arg(1))/2)";
		SimpleNode node2 = TestUtilities.doParse(formula2);
		getFunctionLibrary().addFunction(new GenericFunction("embed", node2));
		String formula = "embed(14,d20Mod(14))";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		resetManager();
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
	}

}
