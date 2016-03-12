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

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.SimpleVariableStore;

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
	 * The LegalScopeLibrary for this SplitFormulaSetup.
	 */
	private final LegalScopeLibrary legalScopeLib = new LegalScopeLibrary();

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
	private final VariableLibrary variableLib = new VariableLibrary(
		legalScopeLib);

	/**
	 * Returns a new IndividualSetup for the given "Global" name. The returned
	 * IndividualSetup will have a unique Global Scope Instance, VariableStore
	 * (and thus FormulaManager and ScopeInformation).
	 * 
	 * Note: A LegalScope object with the given name MUST have been loaded into
	 * the LegalScopeLibrary of this SplitFormulaSetup or this method will throw
	 * an Exception.
	 * 
	 * Note: The LegalScope returned by the LegalScopeLibrary of this
	 * SplitFormulaSetup must also be a "Global" scope in that it must return
	 * null as the parent LegalScope or this method will throw an Exception.
	 * 
	 * @param globalName
	 *            The name of the global scope for the IndividualSetup to be
	 *            returned
	 * @return A new IndividualSetup for the given "Global" name
	 */
	public IndividualSetup getIndividualSetup(String globalName)
	{
		return new IndividualSetup(globalName);
	}

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
		return variableLib;
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
	 * Returns the LegalScopeLibrary for this SplitFormulaSetup.
	 * 
	 * @return The LegalScopeLibrary for this SplitFormulaSetup
	 */
	public LegalScopeLibrary getLegalScopeLibrary()
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

	/**
	 * An IndividualSetup is returned by a SplitFormulaSetup. This contains the
	 * information unique to a specific solution area.
	 */
	public class IndividualSetup
	{

		/**
		 * The ScopeInstanceFactory for this IndividualSetup.
		 */
		private final ScopeInstanceFactory instanceFactory =
				new ScopeInstanceFactory(legalScopeLib);

		/**
		 * The WriteableVariableStore for this IndividualSetup.
		 */
		private final WriteableVariableStore variableStore =
				new SimpleVariableStore();

		/**
		 * The FormulaManager for this IndividualSetup.
		 */
		private final FormulaManager formulaManager = new SimpleFormulaManager(
			functionLib, operatorLib, variableLib, getVariableStore());

		/**
		 * The "Global" LegalScope for this IndividualSetup.
		 */
		private final LegalScope globalScope;

		/**
		 * The "Global" ScopeInstance for this IndividualSetup.
		 */
		private final ScopeInstance globalScopeInst;

		/**
		 * The ScopeInformation for this IndividualSetup.
		 */
		private final ScopeInformation scopeInfo;

		/**
		 * Constructs a new IndividualSetup with the "global" LegalScope of the
		 * given name.
		 * 
		 * @param globalName
		 *            The name of the "global" LegalScope for this
		 *            IndividualSetup
		 */
		public IndividualSetup(String globalName)
		{
			globalScope = legalScopeLib.getScope(globalName);
			globalScopeInst = getInstanceFactory().getInstance(null,
				getGlobalScope(), null);
			scopeInfo =
					new ScopeInformation(getFormulaManager(),
						getGlobalScopeInst());
		}

		/**
		 * Return the ScopeInstanceFactory for this IndividualSetup.
		 * 
		 * @return the ScopeInstanceFactory for this IndividualSetup
		 */
		public ScopeInstanceFactory getInstanceFactory()
		{
			return instanceFactory;
		}

		/**
		 * Return the VariableStore for this IndividualSetup.
		 * 
		 * @return the VariableStore for this IndividualSetup
		 */
		public WriteableVariableStore getVariableStore()
		{
			return variableStore;
		}

		/**
		 * Return the FormulaManager for this IndividualSetup.
		 * 
		 * @return the FormulaManager for this IndividualSetup
		 */
		public FormulaManager getFormulaManager()
		{
			return formulaManager;
		}

		/**
		 * Return the "Global" LegalScope for this IndividualSetup.
		 * 
		 * @return the "Global" LegalScope for this IndividualSetup
		 */
		public LegalScope getGlobalScope()
		{
			return globalScope;
		}

		/**
		 * Return the "Global" ScopeInstance for this IndividualSetup.
		 * 
		 * @return the "Global" ScopeInstance for this IndividualSetup
		 */
		public ScopeInstance getGlobalScopeInst()
		{
			return globalScopeInst;
		}

		/**
		 * Return the ScopeInformation for this IndividualSetup.
		 * 
		 * @return the ScopeInformation for this IndividualSetup
		 */
		public ScopeInformation getScopeInfo()
		{
			return scopeInfo;
		}
	}

}
