/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.lang.ref.WeakReference;
import java.util.Objects;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.helper.ReferenceDependency;

/**
 * PCGenManagerFactory is a ManagerFactory responsible for ensuring the Managers are
 * constructed with sufficient support to work with the formula functions provided in
 * PCGen.
 */
public class PCGenManagerFactory implements ManagerFactory
{
	private final WeakReference<LoadContext> context;
	private final ScopeImplementer scopeLib;
	private final OperatorLibrary opLib;
	private final VariableLibrary varLib;
	private final FunctionLibrary functionLib;
	private final VariableStore varStore;
	private final ScopeInstanceFactory siFactory;

	/**
	 * Constructs a new PCGenManagerFactory with the provided components.
	 *
	 * @param context
	 *            The LoadContext for this ManagerFactory
	 * @param scopeLib
	 *            The ScopeImplementer
	 * @param opLib
	 *            The OperatorLibrary
	 * @param varLib
	 *            The VariableLibrary
	 * @param functionLib
	 *            The FunctionLibrary
	 * @param varStore
	 *            The VariableStore
	 * @param siFactory
	 *            The ScopeInstanceFactory
	 */
	public PCGenManagerFactory(LoadContext context, ScopeImplementer scopeLib,
		OperatorLibrary opLib, VariableLibrary varLib, FunctionLibrary functionLib,
		VariableStore varStore, ScopeInstanceFactory siFactory)
	{
		this.context = new WeakReference<>(Objects.requireNonNull(context));
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
		depManager = depManager.getWith(DependencyManager.OPLIB, opLib);
		depManager = depManager.getWith(ManagerKey.CONTEXT, context.get());
		return depManager.getWith(ManagerKey.REFERENCES, new ReferenceDependency());
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
		semantics = semantics.getWith(FormulaSemantics.SCOPE, scope);
		return semantics.getWith(ManagerKey.CONTEXT, context.get());
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
		return manager.getWith(ManagerKey.CONTEXT, context.get());
	}

	@Override
	public PCGenManagerFactory createReplacement(WriteableVariableStore newVarStore)
	{
		return new PCGenManagerFactory(context.get(), scopeLib, opLib, varLib,
			functionLib, newVarStore, siFactory);
	}
}
