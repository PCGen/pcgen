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
package pcgen.base.formula.factory;

import java.util.Objects;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.base.WriteableVariableStore;

/**
 * A ManagerFactory is an object designed to produce the various manager objects used by
 * the visitors to a Formula. This provides a common construction location for typical
 * SolverSystem object structures.
 */
public class SimpleManagerFactory implements ManagerFactory
{
	/**
	 * The ScopeImplementer this ManagerFactory will use to construct Managers.
	 */
	private final ScopeImplementer scopeLib;

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
	 * @param scopeLib
	 *            The ScopeImplementer this ManagerFactory will use to construct Managers
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
	public SimpleManagerFactory(ScopeImplementer scopeLib, OperatorLibrary opLib,
		VariableLibrary varLib, FunctionLibrary functionLib,
		VariableStore varStore, ScopeInstanceFactory siFactory)
	{
		this.scopeLib = Objects.requireNonNull(scopeLib);
		this.opLib = Objects.requireNonNull(opLib);
		this.varLib = Objects.requireNonNull(varLib);
		this.functionLib = Objects.requireNonNull(functionLib);
		this.varStore = Objects.requireNonNull(varStore);
		this.siFactory = Objects.requireNonNull(siFactory);
	}

	@Override
	public DependencyManager generateDependencyManager()
	{
		DependencyManager depManager = new DependencyManager();
		depManager = depManager.getWith(DependencyManager.FUNCTION, functionLib);
		depManager = depManager.getWith(DependencyManager.SCOPELIB, scopeLib);
		depManager = depManager.getWith(DependencyManager.VARLIB, varLib);
		depManager = depManager.getWith(DependencyManager.SIFACTORY, siFactory);
		return depManager.getWith(DependencyManager.OPLIB, opLib);
	}

	@Override
	public FormulaSemantics generateFormulaSemantics(ImplementedScope scope)
	{
		FormulaSemantics semantics = new FormulaSemantics();
		semantics = semantics.getWith(FormulaSemantics.FUNCTION, functionLib);
		semantics = semantics.getWith(FormulaSemantics.OPLIB, opLib);
		semantics = semantics.getWith(FormulaSemantics.SCOPELIB, scopeLib);
		semantics = semantics.getWith(FormulaSemantics.VARLIB, varLib);
		semantics = semantics.getWith(FormulaSemantics.SIFACTORY, siFactory);
		return semantics.getWith(FormulaSemantics.SCOPE, scope);
	}

	@Override
	public EvaluationManager generateEvaluationManager()
	{
		EvaluationManager manager = new EvaluationManager();
		manager = manager.getWith(EvaluationManager.FUNCTION, functionLib);
		manager = manager.getWith(EvaluationManager.OPLIB, opLib);
		manager = manager.getWith(EvaluationManager.SCOPELIB, scopeLib);
		manager = manager.getWith(EvaluationManager.VARLIB, varLib);
		manager = manager.getWith(EvaluationManager.SIFACTORY, siFactory);
		manager = manager.getWith(EvaluationManager.RESULTS, varStore);
		return manager;
	}

	@Override
	public SimpleManagerFactory createReplacement(WriteableVariableStore newVarStore)
	{
		return new SimpleManagerFactory(scopeLib, opLib, varLib, functionLib,
			newVarStore, siFactory);
	}
}
