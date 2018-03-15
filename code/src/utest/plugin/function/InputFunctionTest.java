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
package plugin.function;

import org.junit.Test;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.solver.AggressiveSolverManager;
import pcgen.base.solver.SolverFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FormulaSetupFacet;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.SolverFactoryFacet;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableLibraryFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.formula.MonitorableVariableStore;
import pcgen.cdom.formula.VariableChannel;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.output.channel.ChannelUtilities;
import plugin.function.testsupport.AbstractFormulaTestCase;
import plugin.function.testsupport.TestUtilities;

public class InputFunctionTest extends AbstractFormulaTestCase
{

	private FormulaSetupFacet formulaSetupFacet =
			FacetLibrary.getFacet(FormulaSetupFacet.class);
	private ScopeFacet scopeFacet = FacetLibrary.getFacet(ScopeFacet.class);
	private VariableLibraryFacet variableLibraryFacet =
			FacetLibrary.getFacet(VariableLibraryFacet.class);
	private VariableStoreFacet variableStoreFacet =
			FacetLibrary.getFacet(VariableStoreFacet.class);
	private SolverManagerFacet solverManagerFacet =
			FacetLibrary.getFacet(SolverManagerFacet.class);
	private SolverFactoryFacet solverFactoryFacet =
			FacetLibrary.getFacet(SolverFactoryFacet.class);
	private CharID id;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		getFunctionLibrary().addFunction(new InputFunction());
		formulaSetupFacet.initialize(context);
		solverFactoryFacet.initialize(context);
		FacetLibrary.getFacet(VariableLibraryFacet.class).initialize(context);
		id = CharID.getID(context.getDataSetID());
		scopeFacet.set(id, getInstanceFactory());
		variableStoreFacet.set(id,
			(MonitorableVariableStore) getVariableStore());
		SolverFactory solverFactory = solverFactoryFacet.get(id.getDatasetID());
		solverManagerFacet.set(id, new AggressiveSolverManager(
			getFormulaManager(), getManagerFactory(), solverFactory, getVariableStore()));
	}

	@Test
	public void testInvalidTooFewArg()
	{
		String formula = "input()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
		formula = "if(\"a\", \"b\")";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidNotString()
	{
		String formula = "input(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "input(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testNotValidNoChannel()
	{
		String formula = "input(\"notvalid\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testGlobalChannelStrength()
	{
		VariableLibrary varLib = variableLibraryFacet.get(id.getDatasetID());
		ScopeInstanceFactory instFactory = scopeFacet.get(id);
		ScopeInstance globalInstance =
				instFactory.getGlobalInstance(GlobalScope.GLOBAL_SCOPE_NAME);
		varLib.assertLegalVariableID(ChannelUtilities.createVarName("STR"),
			globalInstance.getLegalScope(), numberManager);
		VariableChannel<Number> strChannel =
				(VariableChannel<Number>) ChannelUtilities.getGlobalChannel(id, "STR");
		String formula = "input(\"STR\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, numberManager, null);
		isStatic(formula, node, false);
		evaluatesTo(formula, node, 0);
		strChannel.set(2);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

}
