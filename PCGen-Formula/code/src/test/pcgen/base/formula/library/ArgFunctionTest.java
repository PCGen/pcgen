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
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class ArgFunctionTest extends AbstractFormulaTestCase
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
		varCapture = new DependencyVisitor();
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		varCapture = null;
		depManager = null;
		argManager = null;
	}

	private Node[] getNodes()
	{
		ASTNum four = new ASTNum(0);
		four.setToken("4");
		ASTNum five = new ASTNum(1);
		five.setToken("5");
		String formula = "abs(-4.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		return new Node[]{four, five, node};
	}

	private void resetManager()
	{
		depManager = getManagerFactory().generateDependencyManager(getFormulaManager(),
			getGlobalScopeInst());
		depManager = getManagerFactory().withVariables(depManager);
		argManager = new ArgumentDependencyManager();
		depManager = depManager.getWith(ArgumentDependencyManager.KEY,
			Optional.of(argManager));
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "arg()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		formula = "arg(2, 3)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testNoArg()
	{
		String formula = "4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, null);
		assertEquals(-1, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testInvalidTooHigh()
	{
		String formula = "arg(4)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidTooLow()
	{
		String formula = "arg(-1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidDouble()
	{
		String formula = "arg(1.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidNaN()
	{
		String formula = "arg(\"string\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testArgZero()
	{
		String formula = "arg(0)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testArgOne()
	{
		String formula = "arg(1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		DependencyManager fdm =
				getManagerFactory().generateDependencyManager(getFormulaManager(),
					getGlobalScopeInst());
		fdm = getManagerFactory().withVariables(fdm);
		/*
		 * Safe and "ignored" - if this test fails, need to change what FDM is
		 * passed in - it should NOT contain an ArgumentDependencyManager
		 */
		assertTrue(!fdm.get(ArgumentDependencyManager.KEY).isPresent());
		DependencyVisitor dv = new DependencyVisitor();
		dv.visit(node, fdm);
	}

	@Test
	public void testComplex()
	{
		String formula = "arg(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(2, argManager.getMaximumArgument());
		evaluatesTo(FormatUtilities.NUMBER_MANAGER, formula, node, Double.valueOf(4.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Override
	protected FormulaManager getFormulaManager()
	{
		FormulaManager formulaManager = super.getFormulaManager();
		FunctionLibrary functionManager = formulaManager.get(FormulaManager.FUNCTION);
		return formulaManager.getWith(FormulaManager.FUNCTION,
			ArgFunction.getWithArgs(functionManager, getNodes()));
	}
}
