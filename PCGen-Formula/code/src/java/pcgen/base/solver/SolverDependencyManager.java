/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import java.util.function.Consumer;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;

/**
 * A SolverDependencyManager manages the dependencies in a SolverSystem.
 */
public interface SolverDependencyManager
{
	/**
	 * Adds a node (for potential dependencies) to the SolverDependencyManager.
	 * @param varID The VariableID indicating the variable to be added to the SolverDependencyManager
	 */
	public void addNode(VariableID<?> varID);

	/**
	 * Inserts a set of dependencies for the given Modifier.
	 * 
	 * @param <T>
	 *            The format of the given VariableID and Modifier
	 * @param varID
	 *            The VariableID for which dependencies are being added
	 * @param modifier
	 *            The Modifier which will be analyzed to determine dependencies
	 * @param source
	 *            The source of the modifier, to determine variables in scope
	 */
	public <T> void insertDependency(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source);

	/**
	 * Removes a set of dependencies if they are missing from the list provided by the
	 * given Consumer.
	 * 
	 * @param varID
	 *            The VariableID to be checked for dependencies
	 * @param consumer
	 *            The DependencyManager Consumer that will identify the active
	 *            dependencies (so missing ones can be removed)
	 */
	public void removeDependencyIfMissing(VariableID<?> varID,
		Consumer<DependencyManager> consumer);

	/**
	 * Processes a Consumer for the children of the given VariableID.
	 * @param varID The VariableID for which the children will be passed to the given Consumer
	 * @param consumer The Consumer to receive each of the children of the given VariableID
	 */
	public void processForChildren(VariableID<?> varID,
		Consumer<VariableID<?>> consumer);

	/**
	 * Creates a replacement SolverDependencyManager with a new VariableStore and
	 * notification target.
	 * 
	 * @param newVarStore
	 *            The WriteableVariableStore to be placed in the new
	 *            SolverDependencyManager
	 * @param newNotificationTarget
	 *            The notification target to be placed in the new SolverDependencyManager
	 * @return A replacement SolverDependencyManager with a new VariableStore and
	 *         notification target
	 */
	public SolverDependencyManager createReplacement(
		WriteableVariableStore newVarStore,
		Consumer<VariableID<?>> newNotificationTarget);
}
