/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.base.formula.base.DynamicDependency;
import pcgen.base.formula.base.DynamicManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;
import pcgen.base.util.FormatManager;

/**
 * An DynamicSolverManager manages a series of Solver objects in order to manage
 * dependencies between those Solver objects and ensure that any Solver which needs to be
 * processed to update a value is processed "aggressively" (as soon as a dependency has
 * calculated a new value). It can also handle dynamic dependencies (see
 * DynamicDependency.java)
 * 
 * One of the primary characteristic of the DynamicSolverManager is also that callers will
 * consider items as represented by a given "VariableID", whereas the DynamicSolverManager
 * will build and manage the associated Solver for that VariableID.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DynamicSolverManager implements SolverSystem
{

	/**
	 * The SolverStrategy used to determine how VariableIDs are updated.
	 */
	private final SolverStrategy strategy;
	
	/**
	 * The FormulaManager used by this DynamicSolverManager.
	 */
	private final FormulaManager formulaManager;

	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * DynamicSolverManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * The "summarized" results of the calculation of each Solver.
	 */
	private final WriteableVariableStore resultStore;

	/**
	 * A mathematical graph used to store dependencies between VariableIDs. Since there is
	 * a 1:1 relationship with the Solver used for a VariableID, this implicitly stores
	 * the dependencies between the Solvers that are part of this DynamicSolverManager.
	 */
	private final DirectionalSetMapGraph<VariableID<?>, DefaultDirectionalGraphEdge<VariableID<?>>> dependencies =
			new DirectionalSetMapGraph<>();

	/**
	 * A mathematical graph used to store dynamic dependencies. This links from a
	 * VariableID to a DynamicEdge. The DynamicEdge contains the information indicating
	 * the "dynamic" DefaultDirectionalGraphEdge that was injected into the dependency
	 * graph (graph).
	 */
	private final DirectionalSetMapGraph<Object, DynamicEdge> dynamic =
			new DirectionalSetMapGraph<>();

	/**
	 * The solverManager to be used to manage the defaults and values of the variables in
	 * this AggressiveSolverFactory.
	 */
	private final SolverManager solverManager;

	/**
	 * Constructs a new DynamicSolverManager which will use the given FormulaMananger and
	 * store results in the given VariableStore.
	 * 
	 * It is assumed that the WriteableVariableStore provided to this DynamicSolverManager
	 * will not be shared as a Writeable object to any other Object. (So for purposes of
	 * ownership, the ownership of that WriteableVariableStore transfers to this
	 * DynamicSolverManager. It can be shared to other locations as a (readable)
	 * VariableStore, as necessary.)
	 * 
	 * @param manager
	 *            The FormulaManager to be used by any Solver in this DynamicSolverManager
	 * @param managerFactory
	 *            The ManagerFactory to be used to generate visitor managers in this
	 *            DynamicSolverManager
	 * @param solverManager
	 *            The SolverManager used to manage the defaults and values of variables
	 * @param resultStore
	 *            The WriteableVariableStore used to store results of the calculations of
	 *            the Solver objects within this DynamicSolverManager.
	 */
	public DynamicSolverManager(FormulaManager manager, ManagerFactory managerFactory,
		SolverManager solverManager, WriteableVariableStore resultStore)
	{
		this.strategy = new AggressiveStrategy(this::processForChildren,
			solverManager::processSolver);
		this.formulaManager = Objects.requireNonNull(manager);
		this.managerFactory = Objects.requireNonNull(managerFactory);
		this.solverManager = Objects.requireNonNull(solverManager);
		this.resultStore = Objects.requireNonNull(resultStore);
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
				getDepManager(varID, Objects.requireNonNull(source));
		modifier.captureDependencies(dependencyManager);
		addDirectDependencies(varID, dependencyManager);
		addDynamicDependencies(varID, dependencyManager.get(DependencyManager.DYNAMIC));
		solverManager.addModifier(varID, modifier, source);
		strategy.processModsUpdated(varID);
	}

	private <T> void addDirectDependencies(VariableID<T> varID,
		DependencyManager dependencyManager)
	{
		//Should always exist based on what called this method
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		for (VariableID<?> depID : potentialVariables.get().getVariables())
		{
			dependencies.addNode(varID);
			solverManager.initialize(depID);
			/*
			 * Better to use depID here rather than Solver: (1) No order of operations
			 * risk (2) Process can still write to cache knowing ID
			 */
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
			DefaultDirectionalGraphEdge<VariableID<?>> edge =
					new DefaultDirectionalGraphEdge<>(depID, varID);
			dependencies.addEdge(edge);
		}
	}

	private <T> void addDynamicDependencies(VariableID<T> varID,
		DynamicManager dynamicManager)
	{
		for (DynamicDependency dependency : dynamicManager.getDependencies())
		{
			VariableID<?> controlVar = dependency.getControlVar();
			VarScoped controlVarValue = (VarScoped) resultStore.get(controlVar);
			if (controlVarValue == null)
			{
				throw new IllegalArgumentException(
					"Cannot initialize Dynamic Edge of format "
						+ controlVar.getFormatManager()
						+ " because no default was provided for that format");
			}
			List<VariableID<?>> inputs = dependency.generateSources(
				formulaManager.getFactory(),
				formulaManager.getScopeInstanceFactory(), controlVarValue);
			for (VariableID<?> input : inputs)
			{
				@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
				DefaultDirectionalGraphEdge<VariableID<?>> edge =
						new DefaultDirectionalGraphEdge<>(input, varID);
				dependencies.addEdge(edge);
				@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
				DynamicEdge dynamicEdge =
						new DynamicEdge(controlVar, edge, dependency);
				dynamic.addEdge(dynamicEdge);
			}
		}
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
				getDepManager(varID, Objects.requireNonNull(source));
		solverManager.captureAllDependencies(varID, dependencyManager);
		//Note, this removes all dynamic edges as a side effect :/
		removeIfMissing(varID, dependencyManager.get(DependencyManager.VARIABLES));
		DynamicManager dynamicManager = dependencyManager.get(DependencyManager.DYNAMIC);
		updateDynamic(dynamicManager);
		addDynamicDependencies(varID, dynamicManager);
		strategy.processModsUpdated(varID);
	}

	private void removeIfMissing(VariableID<?> varID,
		Optional<VariableList> potentialVariables)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> edges =
				dependencies.getAdjacentEdges(varID);
		if (edges == null)
		{
			return;
		}
		List<VariableID<?>> dependentVarIDs = potentialVariables.get().getVariables();
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : edges)
		{
			if ((edge.getNodeAt(1) == varID)
				&& !dependentVarIDs.contains(edge.getNodeAt(0)))
			{
				dependencies.removeEdge(edge);
			}
		}
	}

	private void updateDynamic(DynamicManager dynamicManager)
	{
		for (DynamicDependency dependency : dynamicManager.getDependencies())
		{
			VariableID<?> controlVar = dependency.getControlVar();
			for (DynamicEdge edge : dynamic.getAdjacentEdges(controlVar))
			{
				if (!dependencies.containsEdge(edge.getTargetEdge()))
				{
					dynamic.removeEdge(edge);
				}
			}
		}
	}

	private <T> DependencyManager getDepManager(VariableID<T> varID,
		ScopeInstance source)
	{
		DependencyManager dependencyManager = managerFactory
			.generateDependencyManager(formulaManager, source);
		dependencyManager = dependencyManager.getWith(
			DependencyManager.ASSERTED, Optional.of(varID.getFormatManager()));
		dependencyManager = dependencyManager.getWith(DependencyManager.DYNAMIC,
			new DynamicManager());
		return managerFactory.withVariables(dependencyManager);
	}

	private void resolveDynamic(VariableID<?> varID, Consumer<VariableID<?>> consumer)
	{
		if (!dynamic.containsNode(varID))
		{
			return;
		}
		VarScoped vs = (VarScoped) resultStore.get(varID);
		ScopeInstanceFactory siFactory = formulaManager.getScopeInstanceFactory();
		VariableLibrary varLibrary = formulaManager.getFactory();
		for (DynamicEdge edge : dynamic.getAdjacentEdges(varID))
		{
			DefaultDirectionalGraphEdge<VariableID<?>> target = edge.getTargetEdge();
			DynamicEdge newEdge = edge.createReplacement(varLibrary, siFactory, vs,
				target.getNodeAt(1));
			DefaultDirectionalGraphEdge<VariableID<?>> newTarget =
					newEdge.getTargetEdge();
			dynamic.removeEdge(edge);
			if (!dynamic.hasAdjacentEdge(varID))
			{
				dynamic.removeNode(varID);
			}
			dynamic.removeNode(target);
			dependencies.removeEdge(target);
			dependencies.addEdge(newTarget);
			dynamic.addEdge(newEdge);
			consumer.accept(newTarget.getNodeAt(1));
		}
	}

	private void processForChildren(VariableID<?> varID,
		Consumer<VariableID<?>> consumer)
	{
		resolveDynamic(varID, consumer);
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
	public DynamicSolverManager createReplacement(WriteableVariableStore newVarStore)
	{
		DynamicSolverManager replacement = new DynamicSolverManager(
			formulaManager.getWith(FormulaManager.RESULTS, newVarStore), managerFactory,
			solverManager.createReplacement(newVarStore), newVarStore);
		for (VariableID<?> varID : dependencies.getNodeList())
		{
			replacement.dependencies.addNode(varID);
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : dependencies.getEdgeList())
		{
			replacement.dependencies.addEdge(edge);
		}
		for (Object node : dynamic.getNodeList())
		{
			replacement.dynamic.addNode(node);
		}
		for (DynamicEdge edge : dynamic.getEdgeList())
		{
			replacement.dynamic.addEdge(edge);
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
