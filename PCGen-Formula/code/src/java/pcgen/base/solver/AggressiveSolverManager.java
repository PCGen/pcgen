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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

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
 * An AggressiveSolverManager manages a series of Solver objects in order to manage
 * dependencies between those Solver objects and ensure that any Solver which needs to be
 * processed to update a value is processed "aggressively" (as soon as a dependency has
 * calculated a new value).
 * 
 * One of the primary characteristic of the AggressiveSolverManager is also that callers
 * will consider items as represented by a given "VariableID", whereas the
 * AggressiveSolverManager will build and manage the associated Solver for that
 * VariableID.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AggressiveSolverManager implements SolverSystem
{

	/**
	 * The FormulaManager used by the Solver members of this AggressiveSolverManager.
	 */
	private final FormulaManager formulaManager;

	/**
	 * The ManagerFactory to be used to generate visitor managers in this
	 * AggressiveSolverManager.
	 */
	private final ManagerFactory managerFactory;

	/**
	 * The relationship from each VariableID to the Solver calculating the value of the
	 * VariableID.
	 */
	private final Map<VariableID<?>, Solver<?>> scopedChannels =
			new HashMap<VariableID<?>, Solver<?>>();

	/**
	 * The "summarized" results of the calculation of each Solver.
	 */
	private final WriteableVariableStore resultStore;

	/**
	 * A mathematical graph used to store dependencies between VariableIDs. Since there is
	 * a 1:1 relationship with the Solver used for a VariableID, this implicitly stores
	 * the dependencies between the Solvers that are part of this AggressiveSolverManager.
	 */
	private final DirectionalSetMapGraph<VariableID<?>, DefaultDirectionalGraphEdge<VariableID<?>>> dependencies =
			new DirectionalSetMapGraph<>();

	/**
	 * The SolverFactory to be used to construct the Solver objects that are members of
	 * this AggressiveSolverFactory.
	 */
	private final SolverFactory solverFactory;

	/**
	 * The Stack, used during processing, to identify what items are being processed and
	 * to detect loops.
	 */
	private final Stack<VariableID<?>> varStack = new Stack<>();

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
	 * @param solverFactory
	 *            The SolverFactory used to store Defaults and build Solver objects
	 * @param resultStore
	 *            The WriteableVariableStore used to store results of the calculations of
	 *            the Solver objects within this AggressiveSolverManager.
	 */
	public AggressiveSolverManager(FormulaManager manager, ManagerFactory managerFactory,
		SolverFactory solverFactory, WriteableVariableStore resultStore)
	{
		this.formulaManager = Objects.requireNonNull(manager);
		this.managerFactory = Objects.requireNonNull(managerFactory);
		this.solverFactory = Objects.requireNonNull(solverFactory);
		this.resultStore = Objects.requireNonNull(resultStore);
	}

	/*
	 * Note: This creates a "local" scoped channel that only exists for the item in
	 * question (item is "in" the VariableID). The key here being that there is the
	 * ability to have a local variable (e.g. Equipment variable).
	 */
	@Override
	public <T> void createChannel(VariableID<T> varID)
	{
		Solver<?> currentSolver = scopedChannels.get(Objects.requireNonNull(varID));
		if (currentSolver != null)
		{
			throw new IllegalArgumentException(
				"Attempt to recreate local channel: " + varID);
		}
		unconditionallyBuildSolver(varID);
		solveFromNode(varID);
	}

	@Override
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		addModifierAndSolve(varID, modifier, source);
	}

	@Override
	public <T> boolean addModifierAndSolve(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		Objects.requireNonNull(varID);
		Objects.requireNonNull(modifier);
		Objects.requireNonNull(source);

		if (!formulaManager.getFactory()
			.isLegalVariableID(varID.getScope().getLegalScope(), varID.getName()))
		{
			/*
			 * The above check allows the implicit create below for only items within the
			 * VariableLibrary
			 */
			throw new IllegalArgumentException("Request to add Modifier to Solver for "
				+ varID + " but that channel was never defined");
		}
		//Note: This cast is enforced by the solver during addModifier
		@SuppressWarnings("unchecked")
		Solver<T> solver = (Solver<T>) scopedChannels.get(varID);
		if (solver == null)
		{
			//CONSIDER This build is implicit - do we want explicit or implicit?
			solver = unconditionallyBuildSolver(varID);
		}
		/*
		 * Now build new edges of things this solver will be dependent upon...
		 */
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
			ensureSolverExists(depID);
			/*
			 * Better to use depID here rather than Solver: (1) No order of operations
			 * risk (2) Process can still write to cache knowing ID
			 */
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
			DefaultDirectionalGraphEdge<VariableID<?>> edge =
					new DefaultDirectionalGraphEdge<VariableID<?>>(depID, varID);
			dependencies.addEdge(edge);
		}
		//Cast above effectively enforced here
		solver.addModifier(modifier, source);
		/*
		 * Solve this solver and anything that requires it (recursively)
		 */
		return solveFromNode(varID);
	}

	private void ensureSolverExists(VariableID<?> varID)
	{
		if (scopedChannels.get(varID) == null)
		{
			unconditionallyBuildSolver(varID);
			solveFromNode(varID);
		}
	}

	private <T> Solver<T> unconditionallyBuildSolver(VariableID<T> varID)
	{
		FormatManager<T> formatManager = varID.getFormatManager();
		Solver<T> solver = solverFactory.getSolver(formatManager);
		scopedChannels.put(varID, solver);
		dependencies.addNode(varID);
		return solver;
	}

	@Override
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source)
	{
		Objects.requireNonNull(varID);
		Objects.requireNonNull(modifier);
		Objects.requireNonNull(source);
		//Note: This cast is enforced by the solver during addModifier
		@SuppressWarnings("unchecked")
		Solver<T> solver = (Solver<T>) scopedChannels.get(varID);
		if (solver == null)
		{
			throw new IllegalArgumentException("Request to remove Modifier to Solver for "
				+ varID + " but that channel was never defined");
		}
		DependencyManager dependencyManager =
				managerFactory.generateDependencyManager(formulaManager, source);
		dependencyManager = managerFactory.withVariables(dependencyManager);
		dependencyManager = dependencyManager.getWith(DependencyManager.ASSERTED,
			Optional.of(varID.getFormatManager()));
		modifier.captureDependencies(dependencyManager);
		processDependencies(varID, dependencyManager);
		//Cast above effectively enforced here
		solver.removeModifier(modifier, source);
		solveFromNode(varID);
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
		Optional<VariableList> potentialVariables =
				dependencyManager.get(DependencyManager.VARIABLES);
		List<VariableID<?>> dependentVarIDs = potentialVariables.get().getVariables();
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> edges =
				dependencies.getAdjacentEdges(varID);
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

	/**
	 * Triggers Solvers to be called, recursively through the dependencies, from the given
	 * VariableID.
	 * 
	 * @param varID
	 *            The VariableID as a starting point for triggering Solvers to be
	 *            processed
	 * @return true if the variable identified by the given VariableID changed; false
	 *         otherwise
	 */
	public boolean solveFromNode(VariableID<?> varID)
	{
		boolean changed = false;
		boolean warning = varStack.contains(varID);
		try
		{
			varStack.push(varID);
			changed = processSolver(varID);
			if (changed)
			{
				if (warning)
				{
					throw new IllegalStateException(
						"Infinite Loop in Variable Processing: " + varStack);
				}
				/*
				 * Only necessary if the answer changes. The problem is that this is not
				 * doing them in order of a topological sort - it is completely random...
				 * so things may be processed twice :/
				 */
				solveChildren(varID);
			}
		}
		finally
		{
			varStack.pop();
		}
		return changed;
	}

	@Override
	public void solveChildren(VariableID<?> varID)
	{
		Set<DefaultDirectionalGraphEdge<VariableID<?>>> adjacentEdges =
				dependencies.getAdjacentEdges(varID);
		if (adjacentEdges != null)
		{
			for (DefaultDirectionalGraphEdge<VariableID<?>> edge : adjacentEdges)
			{
				if (edge.getNodeAt(0).equals(varID))
				{
					solveFromNode(edge.getNodeAt(1));
				}
			}
		}
	}

	/**
	 * Processes a single Solver represented by the given VariableID. Returns true if the
	 * value of the Variable calculated by the Solver has changed due to this processing.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which the given Solver should be processed.
	 * 
	 * @return true if the value of the Variable calculated by the Solver has changed due
	 *         to this processing; false otherwise
	 */
	private <T> boolean processSolver(VariableID<T> varID)
	{
		@SuppressWarnings("unchecked")
		Solver<T> solver = (Solver<T>) scopedChannels.get(varID);
		/*
		 * Solver should "never" be null here, so we accept risk of NPE, since it's always
		 * a code bug
		 */
		EvaluationManager evalManager =
				managerFactory.generateEvaluationManager(formulaManager);
		T newValue = solver.process(evalManager);
		Object oldValue = resultStore.put(varID, newValue);
		return !newValue.equals(oldValue);
	}

	@Override
	public <T> List<ProcessStep<T>> diagnose(VariableID<T> varID)
	{
		@SuppressWarnings("unchecked")
		Solver<T> solver = (Solver<T>) scopedChannels.get(varID);
		if (solver == null)
		{
			throw new IllegalArgumentException("Request to diagnose VariableID " + varID
				+ " but that channel was never defined");
		}
		EvaluationManager evalManager =
				managerFactory.generateEvaluationManager(formulaManager);
		return solver.diagnose(evalManager);
	}

	@Override
	public <T> T getDefaultValue(FormatManager<T> formatManager)
	{
		return solverFactory.getDefault(formatManager);
	}

	@Override
	public AggressiveSolverManager createReplacement(WriteableVariableStore newVarStore)
	{
		newVarStore.importFrom(resultStore);
		AggressiveSolverManager replacement = new AggressiveSolverManager(
			formulaManager.getWith(FormulaManager.RESULTS, newVarStore), managerFactory,
			solverFactory, newVarStore);
		for (VariableID<?> varID : dependencies.getNodeList())
		{
			replacement.dependencies.addNode(varID);
		}
		for (DefaultDirectionalGraphEdge<VariableID<?>> edge : dependencies.getEdgeList())
		{
			replacement.dependencies.addEdge(edge);
		}
		for (Entry<VariableID<?>, Solver<?>> entry : scopedChannels.entrySet())
		{
			replacement.scopedChannels.put(entry.getKey(), entry.getValue().createReplacement());
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
