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
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;

/**
 * An DynamicSolverDependencyManager manages a series of VariableIDs in order to manage
 * dependencies between those VariableIDs. It can also handle dynamic dependencies (see
 * DynamicDependency.java)
 */
public class DynamicSolverDependencyManager implements SolverDependencyManager
{
	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * DynamicSolverDependencyManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * The Consumer to be notified when a new VariableID is detected. Note that this can
	 * be a redundant call, so must not create unnecessary side effects.
	 */
	private final Consumer<VariableID<?>> notificationTarget;

	/**
	 * The "summarized" results of the calculation of each Solver.
	 */
	private final VariableStore resultStore;

	/**
	 * A mathematical graph used to store dependencies between VariableIDs. Since there is
	 * a 1:1 relationship with the Solver used for a VariableID, this implicitly stores
	 * the dependencies between the Solvers that are part of this DynamicSolverDependencyManager.
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
	 * Constructs a new DynamicSolverDependencyManager which will use the given
	 * FormulaMananger and store results in the given VariableStore.
	 * 
	 * It is assumed that the WriteableVariableStore provided to this
	 * DynamicSolverDependencyManager will not be shared as a Writeable object to any
	 * other Object. (So for purposes of ownership, the ownership of that
	 * WriteableVariableStore transfers to this DynamicSolverDependencyManager. It can be
	 * shared to other locations as a (readable) VariableStore, as necessary.)
	 * 
	 * @param managerFactory
	 *            The ManagerFactory to be used to generate visitor managers in this
	 *            DynamicSolverDependencyManager
	 * @param notificationTarget
	 *            The Consumer to be notified when a VariableID providing a dependency is
	 *            detected
	 * @param resultStore
	 *            The VariableStore used to store results in the SolverSystem where this
	 *            DynamicSolverDependencyManager is managing the dependencies
	 */
	public DynamicSolverDependencyManager(ManagerFactory managerFactory,
		Consumer<VariableID<?>> notificationTarget, VariableStore resultStore)
	{
		this.managerFactory = Objects.requireNonNull(managerFactory);
		this.notificationTarget = Objects.requireNonNull(notificationTarget);
		this.resultStore = Objects.requireNonNull(resultStore);
	}

	@Override
	public void addNode(VariableID<?> varID)
	{
		dependencies.addNode(varID);
	}

	@Override
	public <T> void insertDependency(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		DependencyManager dependencyManager = getDepManager(varID);
		dependencyManager =
				dependencyManager.getWith(DependencyManager.INSTANCE, source);
		modifier.captureDependencies(dependencyManager);
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		insert(varID, potentialVariables.get().getVariables());
		DynamicManager dynamicManager =
				dependencyManager.get(DependencyManager.DYNAMIC);
		addDynamicDependencies(varID, dynamicManager);
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
			List<VariableID<?>> inputs =
					dependency.generateSources(controlVarValue);
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
	public void removeDependencyIfMissing(VariableID<?> varID,
		Consumer<DependencyManager> varGatheringTarget)
	{
		DependencyManager dependencyManager = getDepManager(varID);
		varGatheringTarget.accept(dependencyManager);
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		//Note, this removes all dynamic edges as a side effect :/
		removeIfMissing(varID, potentialVariables.get().getVariables());
		DynamicManager dynamicManager = dependencyManager.get(DependencyManager.DYNAMIC);
		updateDynamic(dynamicManager);
		addDynamicDependencies(varID, dynamicManager);
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

	private void insert(VariableID<?> varID,
		List<VariableID<?>> dependentVarIDs)
	{
		for (VariableID<?> depID : dependentVarIDs)
		{
			//Note: This add can be redundant
			dependencies.addNode(varID);
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
			DefaultDirectionalGraphEdge<VariableID<?>> edge =
					new DefaultDirectionalGraphEdge<VariableID<?>>(depID, varID);
			dependencies.addEdge(edge);
			notificationTarget.accept(depID);
		}
	}

	private void removeIfMissing(VariableID<?> varID,
		List<VariableID<?>> dependentVarIDs)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> edges =
				dependencies.getAdjacentEdges(varID);
		if (edges == null)
		{
			return;
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : edges)
		{
			if ((edge.getNodeAt(1) == varID)
				&& !dependentVarIDs.contains(edge.getNodeAt(0)))
			{
				dependencies.removeEdge(edge);
			}
		}
	}

	private <T> DependencyManager getDepManager(VariableID<T> varID)
	{
		DependencyManager dependencyManager = managerFactory
			.generateDependencyManager();
		dependencyManager = dependencyManager.getWith(
			DependencyManager.ASSERTED, Optional.of(varID.getFormatManager()));
		dependencyManager = dependencyManager.getWith(DependencyManager.DYNAMIC,
			new DynamicManager());
		return managerFactory.withVariables(dependencyManager);
	}

	@Override
	public void processForChildren(VariableID<?> varID,
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
	public DynamicSolverDependencyManager createReplacement(
		WriteableVariableStore newVarStore,
		Consumer<VariableID<?>> newNotificationTarget)
	{
		DynamicSolverDependencyManager replacement =
				new DynamicSolverDependencyManager(
					managerFactory, newNotificationTarget, newVarStore);
		for (VariableID<?> varID : dependencies.getNodeList())
		{
			replacement.dependencies.addNode(varID);
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : dependencies
			.getEdgeList())
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

	private void resolveDynamic(VariableID<?> varID,
		Consumer<VariableID<?>> consumer)
	{
		if (!dynamic.containsNode(varID))
		{
			return;
		}
		VarScoped vs = (VarScoped) resultStore.get(varID);
		for (DynamicEdge edge : dynamic.getAdjacentEdges(varID))
		{
			DefaultDirectionalGraphEdge<VariableID<?>> target =
					edge.getTargetEdge();
			DynamicEdge newEdge =
					edge.createReplacement(vs, target.getNodeAt(1));
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
}
