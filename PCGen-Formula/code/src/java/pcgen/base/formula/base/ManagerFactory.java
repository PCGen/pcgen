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
 * the visitors to a Formula. This is an interface to allow extension of these behaviors
 * by more advanced formula processing systems.
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
	 * Generates an initialized DependencyManager with the given arguments.
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
	 * Generates an initialized DependencyManager with the given arguments.
	 * 
	 * @return An initialized DependencyManager with the given arguments
	 */
	public DependencyManager generateDependencyManager()
	{
		DependencyManager fdm = new DependencyManager();
		fdm = fdm.getWith(DependencyManager.VARLIB, varLib);
		fdm = fdm.getWith(DependencyManager.FUNCTION, functionLib);
		return fdm.getWith(DependencyManager.OPLIB, opLib);
	}

	/**
	 * Generates a DependencyManager with additional contents to handle variable
	 * dependencies.
	 * 
	 * @param fdm
	 *            The DependencyManager from which the returned DependencyManager should
	 *            be derived
	 * @return The DependencyManager with contents to handle variables
	 */
	public DependencyManager withVariables(DependencyManager fdm)
	{
		return fdm
			.getWith(DependencyManager.VARSTRATEGY, Optional.of(new StaticStrategy()))
			.getWith(DependencyManager.VARIABLES, Optional.of(new VariableList()));
	}

	/**
	 * Constructs and initializes a new FormulaSemantics object with the appropriate keys
	 * set to the given parameters.
	 * 
	 * @param scope
	 *            The ImplementedScope when a Formula is processed with this FormulaSemantics
	 * @return An initialized FormulaSemantics object with the appropriate keys set to the
	 *         given parameters
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
	 * Generates a new EvaluationManager initialized with the given parameters.
	 * 
	 * @return A new EvaluationManager initialized with the given parameters
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
	 * Creates a replacement ManagerFactory which contains the given VariableStore.
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
