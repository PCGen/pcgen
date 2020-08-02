/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;

/**
 * A SimpleSolverManager manages the Solvers for VariableIDs.
 * 
 * One of the primary characteristic of the SimpleSolverManager is that callers will
 * consider items as represented by a given "VariableID", whereas the SolverManager will
 * build and manage the associated Solver for that VariableID.
 */
public class SimpleSolverManager implements SolverManager
{

	/**
	 * The FormulaManager used by the Solver members of this SimpleSolverManager.
	 */
	private final FormulaManager formulaManager;
	
	/**
	 * The Function used to check validity of a variable name.
	 */
	private final BiFunction<ImplementedScope, String, Boolean> varCheck;

	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * SimpleSolverManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * The relationship from each VariableID to the Solver calculating the value of the
	 * VariableID.
	 */
	private final Map<VariableID<?>, Solver<?>> scopedChannels =
			new HashMap<VariableID<?>, Solver<?>>();

	/**
	 * The IndirectValueStore containing the relationship between a format of Solver and
	 * the default value for that format of Solver.
	 */
	private final SupplierValueStore valueStore;

	/**
	 * The "summarized" results of the calculation of each Solver.
	 */
	private final WriteableVariableStore resultStore;

	/**
	 * Constructs a new SimpleSolverManager using the given IndirectValueStore as the
	 * underlying ValueStore.
	 * 
	 * It is assumed that the WriteableVariableStore provided to this SimpleSolverManager
	 * will not be shared as a Writeable object to any other Object. (So for purposes of
	 * ownership, the ownership of that WriteableVariableStore transfers to this
	 * SimpleSolverManager. It can be shared to other locations as a (readable)
	 * VariableStore, as necessary.)
	 * 
	 * @param varCheck
	 *            The Function used to check validity of a variable name
	 * @param manager
	 *            The FormulaManager used by the Solver members of this
	 *            SimpleSolverManager
	 * @param managerFactory
	 *            The to be used to generate visitor managers in this SimpleSolverManager
	 * @param valueStore
	 *            The IndirectValueStore containing the relationship between a format of a
	 *            variable and the default value for that format of a variable
	 * @param resultStore
	 *            The place where results from a calculation should be stored
	 */
	public SimpleSolverManager(BiFunction<ImplementedScope, String, Boolean> varCheck,
		FormulaManager manager, ManagerFactory managerFactory,
		SupplierValueStore valueStore, WriteableVariableStore resultStore)
	{
		this.varCheck = Objects.requireNonNull(varCheck);
		this.formulaManager = Objects.requireNonNull(manager);
		this.managerFactory = Objects.requireNonNull(managerFactory);
		this.valueStore = Objects.requireNonNull(valueStore);
		this.resultStore = Objects.requireNonNull(resultStore);
	}

	/**
	 * Checks if the given VariableID represents a legal variable for this
	 * SimpleSolverManager. A VariableID is illegal if the variable name, scope, and
	 * format within the VariableID have not been asserted as legal to the VariableLibrary
	 * within the FormatManager of this SimpleSolverManager.
	 * 
	 * @param varID
	 *            The VariableID to be checked
	 * @throws IllegalArgumentException
	 *             if the VariableID is not a legal VariableID for this
	 *             SimpleSolverManager
	 */
	private void checkLegal(VariableID<?> varID)
	{
		if (!varCheck.apply(varID.getScope().getImplementedScope(), varID.getName()))
		{
			throw new IllegalArgumentException(
				"Request to add Modifier to Solver for " + varID
					+ " but that channel was never defined");
		}
	}

	@Override
	public boolean initialize(VariableID<?> varID)
	{
		Solver<?> currentSolver =
				scopedChannels.get(Objects.requireNonNull(varID));
		if (currentSolver == null)
		{
			scopedChannels.put(varID, getNewSolver(varID));
			processSolver(varID);
		}
		return (currentSolver == null);
	}

	@Override
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		getBuiltSolver(varID)
			.orElseThrow(() -> new IllegalArgumentException(
				"Request to add Modifier to Solver for " + varID
					+ " but that channel was never defined"))
			.addModifier(modifier, source);
	}

	@Override
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		getBuiltSolver(varID)
			.ifPresent(s -> s.removeModifier(modifier, source));
	}

	@Override
	public void captureAllDependencies(VariableID<?> varID,
		DependencyManager dependencyManager)
	{
		getBuiltSolver(varID)
			.ifPresent(s -> s.captureDependencies(dependencyManager));
	}

	@Override
	public <T> boolean processSolver(VariableID<T> varID)
	{
		T newValue = getBuiltSolver(varID)
			.map(s -> s.process(
				managerFactory.generateEvaluationManager(formulaManager)))
			.orElse(getDefault(varID.getFormatManager()));
		Object oldValue = resultStore.put(varID, newValue);
		return !newValue.equals(oldValue);
	}

	@Override
	public <T> List<ProcessStep<T>> diagnose(VariableID<T> varID)
	{
		return getBuiltSolver(varID)
			.map(s -> s.diagnose(
				managerFactory.generateEvaluationManager(formulaManager)))
			.orElse(Collections.emptyList());
	}

	@Override
	public SolverManager createReplacement(WriteableVariableStore newVarStore)
	{
		newVarStore.importFrom(resultStore);
		SimpleSolverManager replacement = new SimpleSolverManager(varCheck, 
			formulaManager.getWith(FormulaManager.RESULTS, newVarStore),
			managerFactory, valueStore, newVarStore);
		for (Entry<VariableID<?>, Solver<?>> entry : scopedChannels.entrySet())
		{
			createSolverReplacement(replacement.scopedChannels, entry.getKey(),
				entry.getValue());
		}
		return replacement;
	}

	private <T> void createSolverReplacement(
		Map<VariableID<?>, Solver<?>> replacement, VariableID<T> varID,
		Solver<?> solver)
	{
		@SuppressWarnings("unchecked")
		Solver<T> tsSolver = (Solver<T>) solver;
		replacement.put(varID, tsSolver.createReplacement());
	}

	private <T> Solver<T> getNewSolver(VariableID<T> varID)
	{
		checkLegal(varID);
		return new Solver<T>(varID.getFormatManager(),
			varID.getFormatManager().initializeFrom(valueStore));
	}

	private <T> Optional<Solver<T>> getBuiltSolver(VariableID<T> varID)
	{
		checkLegal(varID);
		@SuppressWarnings("unchecked")
		Solver<T> solver =
				(Solver<T>) scopedChannels.get(Objects.requireNonNull(varID));
		return Optional.ofNullable(solver);
	}

	@Override
	public <T> T solve(NEPFormula<T> formula)
	{
		EvaluationManager evalManager =
				managerFactory.generateEvaluationManager(formulaManager);
		return formula.resolve(evalManager);
	}

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return formatManager.initializeFrom(valueStore);
	}
}
