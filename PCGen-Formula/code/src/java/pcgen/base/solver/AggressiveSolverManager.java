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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;
import pcgen.base.logging.Logging;
import pcgen.base.logging.Severity;
import pcgen.base.util.FormatManager;

/**
 * An AggressiveSolverManager manages a series of variables objects in order to manage
 * dependencies between those variables and ensure that any variable which needs to be
 * processed to update a value is processed "aggressively" (as soon as a dependency has
 * calculated a new value).
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AggressiveSolverManager implements SolverSystem
{

	/**
	 * The SolverStrategy used to determine how VariableIDs are updated.
	 */
	private final SolverStrategy strategy;
	
	/**
	 * The FormulaManager used by this AggressiveSolverManager.
	 */
	private final FormulaManager formulaManager;

	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * AggressiveSolverManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * A mathematical graph used to store dependencies between VariableIDs. Since there is
	 * a 1:1 relationship with the Solver used for a VariableID, this implicitly stores
	 * the dependencies between the Solvers that are part of this AggressiveSolverManager.
	 */
	private final DirectionalSetMapGraph<VariableID<?>, DefaultDirectionalGraphEdge<VariableID<?>>> dependencies =
			new DirectionalSetMapGraph<>();

	/**
	 * The solverManager to be used to manage the defaults and values of the variables in
	 * this AggressiveSolverFactory.
	 */
	private final SolverManager solverManager;

	/**
	 * Constructs a new AggressiveSolverManager which will use the given FormulaMananger
	 * and store results in the given VariableStore.
	 * 
	 * It is assumed that the WriteableVariableStore provided to this
	 * AggressiveSolverManager will not be shared as a Writeable object to any other
	 * Object. (So for purposes of ownership, the ownership of that WriteableVariableStore
	 * transfers to this AggressiveSolverManager. It can be shared to other locations as a
	 * (readable) VariableStore, as necessary.)
	 * 
	 * @param manager
	 *            The FormulaManager to be used by any Solver in this
	 *            AggressiveSolverManager
	 * @param managerFactory
	 *            The ManagerFactory to be used to generate visitor managers in this
	 *            AggressiveSolverManager
	 * @param solverManager
	 *            The SolverManager used to manage the defaults and values of variables
	 */
	public AggressiveSolverManager(FormulaManager manager, ManagerFactory managerFactory,
		SolverManager solverManager)
	{
		this.strategy = new AggressiveStrategy(this::processForChildren,
			solverManager::processSolver);
		this.formulaManager = Objects.requireNonNull(manager);
		this.managerFactory = Objects.requireNonNull(managerFactory);
		this.solverManager = Objects.requireNonNull(solverManager);
	}

	@Override
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		Objects.requireNonNull(varID);
		Objects.requireNonNull(modifier);
		Objects.requireNonNull(source);

		if (solverManager.initialize(varID))
		{
			dependencies.addNode(varID);
		}

		DependencyManager dependencyManager =
				managerFactory.generateDependencyManager(formulaManager, source);
		dependencyManager = dependencyManager.getWith(DependencyManager.ASSERTED,
			Optional.of(varID.getFormatManager()));
		dependencyManager = managerFactory.withVariables(dependencyManager);
		modifier.captureDependencies(dependencyManager);
		//Should always exist based on where this method was called from
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		for (VariableID<?> depID : potentialVariables.get().getVariables())
		{
			dependencies.addNode(depID);
			solverManager.initialize(depID);
			/*
			 * Better to use depID here rather than Solver: (1) No order of operations
			 * risk (2) Process can still write to cache knowing ID
			 */
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
			DefaultDirectionalGraphEdge<VariableID<?>> edge =
					new DefaultDirectionalGraphEdge<VariableID<?>>(depID, varID);
			dependencies.addEdge(edge);
		}
		solverManager.addModifier(varID, modifier, source);
		strategy.processModsUpdated(varID);
	}

	@Override
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		Objects.requireNonNull(varID);
		Objects.requireNonNull(modifier);
		Objects.requireNonNull(source);
		solverManager.removeModifier(varID, modifier, source);
		DependencyManager dependencyManager =
				managerFactory.generateDependencyManager(formulaManager, source);
		dependencyManager = managerFactory.withVariables(dependencyManager);
		dependencyManager = dependencyManager.getWith(DependencyManager.ASSERTED,
			Optional.of(varID.getFormatManager()));
		solverManager.captureAllDependencies(varID, dependencyManager);
		processDependencies(varID, dependencyManager);
		strategy.processModsUpdated(varID);
	}

	/**
	 * Process Dependencies to be removed for the given VariableID stored in the given
	 * DependencyManager.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which dependencies will be removed
	 * @param dependencyManager
	 *            The DependencyManager containing the dependencies of the given
	 *            VariableID
	 */
	private <T> void processDependencies(VariableID<T> varID,
		DependencyManager dependencyManager)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> edges =
				dependencies.getAdjacentEdges(varID);
		if (edges == null)
		{
			return;
		}
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		List<VariableID<?>> dependentVarIDs = potentialVariables.get().getVariables();
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : edges)
		{
			if (edge.getNodeAt(1) == varID)
			{
				VariableID<?> depID = edge.getNodeAt(0);
				if (dependentVarIDs.contains(depID))
				{
					dependencies.removeEdge(edge);
					dependentVarIDs.remove(depID);
				}
			}
		}
		if (!dependentVarIDs.isEmpty())
		{
			Logging.log(Severity.ERROR,
				() -> "Unable to find matching edges for all Solver Dependencies: "
					+ dependentVarIDs);
		}
	}

	private void processForChildren(VariableID<?> varID,
		Consumer<VariableID<?>> consumer)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> adjacentEdges =
				dependencies.getAdjacentEdges(varID);
		if (adjacentEdges != null)
		{
			for (DefaultDirectionalGraphEdge<VariableID<?>> edge : adjacentEdges)
			{
				if (edge.getNodeAt(0).equals(varID))
				{
					consumer.accept(edge.getNodeAt(1));
				}
			}
		}
	}

	@Override
	public <T> List<ProcessStep<T>> diagnose(VariableID<T> varID)
	{
		return solverManager.diagnose(varID);
	}

	@Override
	public <T> T getDefaultValue(FormatManager<T> formatManager)
	{
		return solverManager.getDefault(formatManager);
	}

	@Override
	public AggressiveSolverManager createReplacement(WriteableVariableStore newVarStore)
	{
		AggressiveSolverManager replacement = new AggressiveSolverManager(
			formulaManager.getWith(FormulaManager.RESULTS, newVarStore),
			managerFactory, solverManager.createReplacement(newVarStore));
		for (VariableID<?> varID : dependencies.getNodeList())
		{
			replacement.dependencies.addNode(varID);
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : dependencies.getEdgeList())
		{
			replacement.dependencies.addEdge(edge);
		}
		return replacement;
	}

	@Override
	public <T> T solve(NEPFormula<T> formula)
	{
		EvaluationManager evalManager =
				managerFactory.generateEvaluationManager(formulaManager);
		return formula.resolve(evalManager);
	}
}
