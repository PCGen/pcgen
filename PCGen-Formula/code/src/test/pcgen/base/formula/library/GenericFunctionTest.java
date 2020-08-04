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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
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

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		resetManager();
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		super.tearDown();
		argManager = null;
		depManager = null;
		varCapture = null;
	}

	private void resetManager()
	{
		depManager = getManagerFactory().generateDependencyManager(
			getGlobalScopeInst());
		depManager = getManagerFactory().withVariables(depManager);
		argManager = new ArgumentDependencyManager();
		depManager = depManager.getWith(ArgumentDependencyManager.KEY,
			Optional.of(argManager));
		varCapture = new DependencyVisitor();
	}

	@Test
	public void testInvalidWrongArgTooFew()
	{
		String formula = "d20Mod()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidWrongArgTooMany()
	{
		String formula = "d20Mod(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula2 = "floor((14-10)/2)";
		SimpleNode node2 = TestUtilities.doParse(formula2);
		isValid(formula2, node2, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		String formula = "noargs(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNoArgs()
	{
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

	@Override
	protected FunctionLibrary functionSetup(WriteableFunctionLibrary wfl)
	{
		String d20ModFormula = "floor((arg(0)-10)/2)";
		SimpleNode d20ModNode = TestUtilities.doParse(d20ModFormula);
		String noArgsFormula = "floor((14-10)/2)";
		SimpleNode noArgsNode = TestUtilities.doParse(noArgsFormula);
		String embedFormula = "floor((arg(0)-arg(1))/2)";
		SimpleNode embedNode = TestUtilities.doParse(embedFormula);
		wfl.addFunction(new GenericFunction("d20Mod", d20ModNode));
		wfl.addFunction(new GenericFunction("noargs", noArgsNode));
		wfl.addFunction(new GenericFunction("embed", embedNode));
		return wfl;
	}
}
