/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.VariableManager;

/**
 * SplitFormulaSetup provides a single location to quickly build the necessary
 * items for processing a formula.
 * 
 * When constructed a SplitFormulaSetup has a set of "common information"
 * (operators, functions) established.
 * 
 * For each individual scope/solution area (to be served by a different Solver),
 * a user can call getIndividualSetup() to build the necessary items (which will
 * share the common items established at construction of the SplitFormulaSetup.
 */
public class SplitFormulaSetup
{

	/**
	 * The SolverFactory for this SplitFormulaSetup.
	 */
	private final SolverFactory solverFactory = new SolverFactory();

	/**
	 * The LegalScopeManager for this SplitFormulaSetup.
	 */
	private final ScopeManagerInst legalScopeLib = new ScopeManagerInst();

	/**
	 * The SimpleFunctionLibrary for this SplitFormulaSetup.
	 */
	private final SimpleFunctionLibrary functionLib =
			new SimpleFunctionLibrary();

	/**
	 * The SimpleOperatorLibrary for this SplitFormulaSetup.
	 */
	private final SimpleOperatorLibrary operatorLib =
			new SimpleOperatorLibrary();

	/**
	 * The VariableLibrary for this SplitFormulaSetup.
	 */
	private final VariableLibrary variableLibrary = new VariableManager(
		legalScopeLib);

	/**
	 * Loads built-in Functions and Operators into this SplitFormulaSetup.
	 */
	public final void loadBuiltIns()
	{
		FormulaUtilities.loadBuiltInFunctions(functionLib);
		FormulaUtilities.loadBuiltInOperators(operatorLib);
	}

	/**
	 * Returns the VariableLibrary for this SplitFormulaSetup.
	 * 
	 * @return The VariableLibrary for this SplitFormulaSetup
	 */
	public VariableLibrary getVariableLibrary()
	{
		return variableLibrary;
	}

	/**
	 * Returns the FunctionLibrary for this SplitFormulaSetup.
	 * 
	 * @return The FunctionLibrary for this SplitFormulaSetup
	 */
	public FunctionLibrary getFunctionLibrary()
	{
		return functionLib;
	}

	/**
	 * Returns the OperatorLibrary for this SplitFormulaSetup.
	 * 
	 * @return The OperatorLibrary for this SplitFormulaSetup
	 */
	public OperatorLibrary getOperatorLibrary()
	{
		return operatorLib;
	}

	/**
	 * Returns the LegalScopeManager for this SplitFormulaSetup.
	 * 
	 * @return The LegalScopeManager for this SplitFormulaSetup
	 */
	public ScopeManagerInst getLegalScopeManager()
	{
		return legalScopeLib;
	}

	/**
	 * Returns the SolverFactory for this SplitFormulaSetup.
	 * 
	 * @return The SolverFactory for this SplitFormulaSetup
	 */
	public SolverFactory getSolverFactory()
	{
		return solverFactory;
	}
}
