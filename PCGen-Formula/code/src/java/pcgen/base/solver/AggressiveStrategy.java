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

import java.util.Objects;
import java.util.Stack;

import pcgen.base.formula.base.DependencyConsumer;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableSolver;

/**
 * An AggressiveStrategy is a SolverStrategy that will immediately calculate the result of
 * a VariableID when any incoming dependency potentially changes the value.
 */
public class AggressiveStrategy implements SolverStrategy
{
	/**
	 * The Stack, used during processing, to identify what items are being processed and
	 * to detect loops.
	 */
	private final Stack<VariableID<?>> varStack = new Stack<>();

	/**
	 * The VariableSolver used to solve a given VariableID.  Must return true if the value changed.
	 */
	private final VariableSolver solveProcessor;

	/**
	 * The DependencyConsumer used to process an item on all dependents of a VariableID.
	 */
	private final DependencyConsumer depConsumer;

	/**
	 * Constructs a new AggressiveStrategy with the given arguments.
	 * 
	 * @param depConsumer
	 *            The DependencyConsumer used to process an item on all dependents of a
	 *            VariableID
	 * @param solveProcessor
	 *            The VariableSolver used to solve a given VariableID
	 */
	public AggressiveStrategy(DependencyConsumer depConsumer,
		VariableSolver solveProcessor)
	{
		this.depConsumer = Objects.requireNonNull(depConsumer);
		this.solveProcessor = Objects.requireNonNull(solveProcessor);
	}

	@Override
	public void processModsUpdated(VariableID<?> varID)
	{
		solveFromNode(varID);
	}

	/**
	 * Triggers Solvers to be called, recursively through the dependencies, from the given
	 * VariableID.
	 * 
	 * @param varID
	 *            The VariableID as a starting point for triggering Solvers to be
	 *            processed
	 */
	private boolean solveFromNode(VariableID<?> varID)
	{
		boolean changed = false;
//		boolean loopIfChanged = varStack.contains(varID);
//		try
//		{
//			varStack.push(varID);
			changed = solveProcessor.solve(varID);
//			if (changed)
//			{
//				if (loopIfChanged)
//				{
//					throw new IllegalStateException(
//						"Infinite Loop in Variable Processing: " + varStack);
//				}
//				/*
//				 * Only necessary if the answer changes. The problem is that this is not
//				 * doing them in order of a topological sort - it is completely random...
//				 * so things may be processed twice :/
//				 */
//				processValueUpdated(varID);
//			}
//		}
//		finally
//		{
//			varStack.pop();
//		}
		return changed;
	}

	/**
	 * Solves children of (any VariableID dependent upon) the given VariableID.
	 * 
	 * @param varID
	 *            The VariableID for which the children should be solved
	 */
	@Override
	public void processValueUpdated(VariableID<?> varID)
	{
		if (varStack.contains(varID))
		{
			throw new IllegalStateException(
				"Infinite Loop in Variable Processing: " + varStack);
		}
		try
		{
			varStack.push(varID);
			depConsumer.processForDependents(varID, this::solveFromNode);
		}
		finally
		{
			varStack.pop();
		}
	}

	@Override
	public AggressiveStrategy generateReplacement(
		DependencyConsumer newDepConsumer,
		VariableSolver newSolver)
	{
		return new AggressiveStrategy(newDepConsumer, newSolver);
	}

}
