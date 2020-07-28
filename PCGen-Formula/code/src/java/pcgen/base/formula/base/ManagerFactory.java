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
	 * Construct a new ManagerFactory with the given arguments.
	 * 
	 * @param opLib
	 *            The OperatorLibrary this ManagerFactory will use to construct Managers
	 */
	public ManagerFactory(OperatorLibrary opLib)
	{
		this.opLib = Objects.requireNonNull(opLib);
	}

	/**
	 * Generates an initialized DependencyManager with the given arguments.
	 * 
	 * @param formulaManager
	 *            The FormulaManager to be contained in the DependencyManager
	 * @param scopeInst
	 *            The ScopeInstance to be contained in the DependencyManager
	 * @return An initialized DependencyManager with the given arguments
	 */
	public DependencyManager generateDependencyManager(
		FormulaManager formulaManager, ScopeInstance scopeInst)
	{
		DependencyManager fdm = new DependencyManager(formulaManager);
		fdm = fdm.getWith(DependencyManager.OPLIB, opLib);
		return fdm.getWith(DependencyManager.INSTANCE, scopeInst);
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
	 * @param manager
	 *            The FormulaManager referenced when a Formula is processed with this
	 *            FormulaSemantics
	 * @param scope
	 *            The ImplementedScope when a Formula is processed with this FormulaSemantics
	 * @return An initialized FormulaSemantics object with the appropriate keys set to the
	 *         given parameters
	 */
	public FormulaSemantics generateFormulaSemantics(
		FormulaManager manager, ImplementedScope scope)
	{
		FormulaSemantics semantics = new FormulaSemantics();
		semantics = semantics.getWith(FormulaSemantics.FMANAGER, manager);
		semantics = semantics.getWith(FormulaSemantics.OPLIB, opLib);
		return semantics.getWith(FormulaSemantics.SCOPE, scope);
	}

	/**
	 * Generates a new EvaluationManager initialized with the given parameters.
	 * 
	 * @param formulaManager
	 *            The FormulaManager used to evaluate formulas processed by this
	 *            EvaluationManager
	 * @return A new EvaluationManager initialized with the given parameters
	 */
	public EvaluationManager generateEvaluationManager(
		FormulaManager formulaManager)
	{
		EvaluationManager manager = new EvaluationManager();
		manager = manager.getWith(EvaluationManager.OPLIB, opLib);
		return manager.getWith(EvaluationManager.FMANAGER, formulaManager);
	}

}
