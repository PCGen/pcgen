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

import java.util.List;
import java.util.Objects;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;

/**
 * A GeneralSolverSystem manages the components to resolve the value of VariableIDs. This
 * includes dependencies between those Solver objects and ensures that any VariableID
 * which needs to be processed is appropriately managed.
 */
public class GeneralSolverSystem implements SolverSystem
{

	/**
	 * The SolverDependencyManager used to track dependencies between VariableIDs.
	 */
	private final SolverDependencyManager depManager;

	/**
	 * The SolverFactory to be used to construct the Solver objects that are members of
	 * this AggressiveSolverFactory.
	 */
	private final SolverManager solverFactory;
	
	/**
	 * The SolverStrategy used to determine how VariableIDs are updated.
	 */
	private final SolverStrategy strategy;
	
	/**
	 * Constructs a new GeneralSolverSystem from the given arguments.
	 * 
	 * @param solverManager
	 *            The SolverFactory used to store Defaults and solve variables
	 * @param depManager
	 *            The SolverDependencyManager to be manage dependencies in this
	 *            GeneralSolverSystem
	 * @param strategy
	 *            The SolverStrategy used to determine how variables are solved in this
	 *            GeneralSolverSystem
	 */
	public GeneralSolverSystem(SolverManager solverManager,
		SolverDependencyManager depManager, SolverStrategy strategy)
	{
		this.solverFactory = Objects.requireNonNull(solverManager);
		this.depManager = Objects.requireNonNull(depManager);
		this.strategy = Objects.requireNonNull(strategy);
	}

	@Override
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		depManager.insertDependency(varID, modifier, source);
		solverFactory.addModifier(varID, modifier, source);
		strategy.processModsUpdated(varID);
	}

	@Override
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		solverFactory.removeModifier(varID, modifier, source);
		depManager.removeDependencyIfMissing(varID,
			fdm -> solverFactory.captureAllDependencies(varID, fdm));
		strategy.processModsUpdated(varID);
	}

	@Override
	public <T> List<ProcessStep<T>> diagnose(VariableID<T> varID)
	{
		return solverFactory.diagnose(varID);
	}

	@Override
	public <T> T getDefaultValue(FormatManager<T> formatManager)
	{
		return solverFactory.getDefault(formatManager);
	}

	@Override
	public GeneralSolverSystem createReplacement(WriteableVariableStore newVarStore)
	{
		SolverManager newSolver = solverFactory.createReplacement(newVarStore);
		SolverDependencyManager newDepManager = depManager.createReplacement(newVarStore);
		SolverStrategy newStrategy = strategy.generateReplacement(
			newDepManager::processForChildren, newSolver::processSolver);
		return new GeneralSolverSystem(newSolver, newDepManager,
			newStrategy);
	}

	@Override
	public <T> T solve(NEPFormula<T> formula)
	{
		return solverFactory.solve(formula);
	}
}
