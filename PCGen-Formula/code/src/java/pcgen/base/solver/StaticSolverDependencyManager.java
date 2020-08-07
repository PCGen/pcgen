/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableList;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;

/**
 * A StaticSolverDependencyManager is a SolverDependencyManager that recognizes only
 * static dependencies.
 */
public final class StaticSolverDependencyManager
		implements SolverDependencyManager
{
	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * StaticSolverDependencyManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * A mathematical graph used to store dependencies between VariableIDs. Since there is
	 * a 1:1 relationship with the Solver used for a VariableID, this implicitly stores
	 * the dependencies between the Solvers that are part of this StaticSolverDependencyManager.
	 */
	private final DirectionalSetMapGraph<VariableID<?>, DefaultDirectionalGraphEdge<VariableID<?>>> dependencies =
			new DirectionalSetMapGraph<>();

	/**
	 * Create a new StaticSolverDependencyManager with the given arguments.
	 * 
	 * @param managerFactory
	 *            The ManagerFactory used to construct DependencyManager objects
	 */
	public StaticSolverDependencyManager(ManagerFactory managerFactory)
	{
		this.managerFactory = Objects.requireNonNull(managerFactory);
	}

	@Override
	public <T> void insertDependency(VariableID<T> varID,
		Modifier<T> modifier, ScopeInstance source)
	{
		DependencyManager dependencyManager = getDepManager(varID);
		dependencyManager =
				dependencyManager.getWith(DependencyManager.INSTANCE, source);
		modifier.captureDependencies(dependencyManager);
		VariableList variableList =
				dependencyManager.get(DependencyManager.VARIABLES).get();
		insert(varID, variableList.getVariables());
	}

	@Override
	public void removeDependencyIfMissing(VariableID<?> varID,
		Consumer<DependencyManager> varGatheringTarget)
	{
		DependencyManager dependencyManager = getDepManager(varID);
		varGatheringTarget.accept(dependencyManager);
		VariableList variableList =
				dependencyManager.get(DependencyManager.VARIABLES).get();
		removeIfMissing(varID, variableList.getVariables());
	}

	private void insert(VariableID<?> varID,
		List<VariableID<?>> dependentVarIDs)
	{
		for (VariableID<?> depID : dependentVarIDs)
		{
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
			DefaultDirectionalGraphEdge<VariableID<?>> edge =
					new DefaultDirectionalGraphEdge<VariableID<?>>(depID, varID);
			dependencies.addEdge(edge);
		}
	}

	private void removeIfMissing(VariableID<?> varID,
		List<VariableID<?>> dependentVarIDs)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> adjacentEdges =
				dependencies.getAdjacentEdges(varID);
		if (adjacentEdges != null)
		{
			for (DefaultDirectionalGraphEdge<VariableID<?>> edge : adjacentEdges)
			{
				if ((edge.getNodeAt(1) == varID)
					&& !dependentVarIDs.contains(edge.getNodeAt(0)))
				{
					dependencies.removeEdge(edge);
				}
			}
		}
	}

	private <T> DependencyManager getDepManager(VariableID<T> varID)
	{
		DependencyManager dependencyManager = managerFactory
			.generateDependencyManager();
		dependencyManager = dependencyManager.getWith(
			DependencyManager.ASSERTED, Optional.of(varID.getFormatManager()));
		return managerFactory.withVariables(dependencyManager);
	}

	@Override
	public void processForChildren(VariableID<?> varID,
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
	public StaticSolverDependencyManager createReplacement(
		WriteableVariableStore newVarStore)
	{
		StaticSolverDependencyManager replacement =
				new StaticSolverDependencyManager(managerFactory);
		for (VariableID<?> varID : dependencies.getNodeList())
		{
			replacement.dependencies.addNode(varID);
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : dependencies
			.getEdgeList())
		{
			replacement.dependencies.addEdge(edge);
		}
		return replacement;
	}

}
