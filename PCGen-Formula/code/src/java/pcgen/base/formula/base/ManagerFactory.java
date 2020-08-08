/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.Objects;
import java.util.Optional;

/**
 * A ManagerFactory is an object designed to produce the various manager objects used by
 * the visitors to a Formula. This provides a common construction location for typical
 * SolverSystem object structures.
 */
public class ManagerFactory
{
	/**
	 * The OperatorLibrary this ManagerFactory will use to construct Managers.
	 */
	private final OperatorLibrary opLib;

	/**
	 * The VariableLibrary this ManagerFactory will use to construct Managers.
	 */
	private final VariableLibrary varLib;

	/**
	 * The FunctionLibrary this ManagerFactory will use to construct Managers.
	 */
	private final FunctionLibrary functionLib;

	/**
	 * The VariableStore this ManagerFactory will use to construct Managers.
	 */
	private final VariableStore varStore;

	/**
	 * The ScopeInstanceFactory this ManagerFactory will use to construct Managers.
	 */
	private final ScopeInstanceFactory siFactory;

	/**
	 * Construct a new ManagerFactory with the given arguments.
	 * 
	 * @param opLib
	 *            The OperatorLibrary this ManagerFactory will use to construct Managers
	 * @param varLib
	 *            The VariableLibrary this ManagerFactory will use to construct Managers
	 * @param functionLib
	 *            The FunctionLibrary this ManagerFactory will use to construct Managers
	 * @param varStore
	 *            The VariableStore this ManagerFactory will use to construct Managers
	 * @param siFactory
	 *            The ScopeInstanceFactory this ManagerFactory will use to construct
	 *            Managers
	 */
	public ManagerFactory(OperatorLibrary opLib, VariableLibrary varLib,
		FunctionLibrary functionLib, VariableStore varStore,
		ScopeInstanceFactory siFactory)
	{
		this.opLib = Objects.requireNonNull(opLib);
		this.varLib = Objects.requireNonNull(varLib);
		this.functionLib = Objects.requireNonNull(functionLib);
		this.varStore = Objects.requireNonNull(varStore);
		this.siFactory = Objects.requireNonNull(siFactory);
	}

	/**
	 * Generates an initialized DependencyManager with the given argument and based on
	 * items known by the ManagerFactory.
	 * 
	 * @param scopeInst
	 *            The ScopeInstance to be contained in the DependencyManager
	 * @return An initialized DependencyManager with the given arguments
	 */
	public DependencyManager generateDependencyManager(ScopeInstance scopeInst)
	{
		return generateDependencyManager()
			.getWith(DependencyManager.INSTANCE, scopeInst);
	}

	/**
	 * Generates an initialized DependencyManager based on items known by the
	 * ManagerFactory.
	 * 
	 * @return An initialized DependencyManager
	 */
	public DependencyManager generateDependencyManager()
	{
		DependencyManager depManager = new DependencyManager();
		depManager = depManager.getWith(DependencyManager.VARLIB, varLib);
		depManager = depManager.getWith(DependencyManager.FUNCTION, functionLib);
		return depManager.getWith(DependencyManager.OPLIB, opLib);
	}

	/**
	 * Decorates a DependencyManager with additional contents to handle variable
	 * dependencies.
	 * 
	 * @param depManager
	 *            The DependencyManager from which the returned DependencyManager should
	 *            be derived
	 * @return The DependencyManager with contents to handle variables
	 */
	public DependencyManager withVariables(DependencyManager depManager)
	{
		return depManager
			.getWith(DependencyManager.VARSTRATEGY, Optional.of(new StaticStrategy()))
			.getWith(DependencyManager.VARIABLES, Optional.of(new VariableList()));
	}

	/**
	 * Constructs and initializes a new FormulaSemantics object for operating in the given
	 * scope.
	 * 
	 * @param scope
	 *            The ImplementedScope used to analyze a Formula processed with the
	 *            returned FormulaSemantics
	 * @return An initialized FormulaSemantics object for operating in the given scope
	 */
	public FormulaSemantics generateFormulaSemantics(ImplementedScope scope)
	{
		FormulaSemantics semantics = new FormulaSemantics();
		semantics = semantics.getWith(FormulaSemantics.FUNCTION, functionLib);
		semantics = semantics.getWith(FormulaSemantics.VARLIB, varLib);
		semantics = semantics.getWith(FormulaSemantics.OPLIB, opLib);
		return semantics.getWith(FormulaSemantics.SCOPE, scope);
	}

	/**
	 * Generates a new EvaluationManager based on items known by the ManagerFactory.
	 * 
	 * @return A new EvaluationManager based on items known by the ManagerFactory
	 */
	public EvaluationManager generateEvaluationManager()
	{
		EvaluationManager manager = new EvaluationManager();
		manager = manager.getWith(EvaluationManager.OPLIB, opLib);
		manager = manager.getWith(EvaluationManager.FUNCTION, functionLib);
		manager = manager.getWith(EvaluationManager.VARLIB, varLib);
		manager = manager.getWith(EvaluationManager.RESULTS, varStore);
		manager = manager.getWith(EvaluationManager.SIFACTORY, siFactory);
		return manager;
	}

	/**
	 * Creates a replacement ManagerFactory which contains the existing information in
	 * this ManagerFactory, with the VariableStore replaced by the given VariableStore.
	 * 
	 * @param newVarStore
	 *            The new VariableStore to be stored in the replacement
	 * @return A replacement ManagerFactory which contains the given VariableStore
	 */
	public ManagerFactory createReplacement(WriteableVariableStore newVarStore)
	{
		return new ManagerFactory(opLib, varLib, functionLib, newVarStore,
			siFactory);
	}
}
