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

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;

/**
 * A SolverManager manages a series of Solver objects in order to manage dependencies
 * between those Solver objects.
 * 
 * One of the primary characteristic of the SolverManager is also that callers will
 * consider items as represented by a given "VariableID", whereas the SolverManager will
 * build and manage any associated structures to correctly resolve the VariableID.
 */
public interface SolverManager
{

	/**
	 * Defines a new Variable that requires solving in this SolverManager. The Variable,
	 * identified by the given VariableID, will be of the format of the given Class.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID used to identify the Solver to be built
	 * @throws IllegalArgumentException
	 *             if any of the parameters is null
	 */
	public <T> void createChannel(VariableID<T> varID);

	/**
	 * Adds a Modifier (with the given source object) to the Solver identified by the
	 * given VariableID.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which a Modifier should be added to the responsible
	 *            Solver
	 * @param modifier
	 *            The Modifier to be added to the Solver for the given VariableID
	 * @param source
	 *            The source of the Modifier to be added to the Solver
	 * @throws IllegalArgumentException
	 *             if any of the parameters is null
	 */
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier, ScopeInstance source);

	/**
	 * Removes a Modifier (with the given source object) from the Solver identified by the
	 * given VariableID.
	 * 
	 * For this to have any effect, the combination of Modifier and source must be the
	 * same (as defined by .equals() equality) as a combination provided to the
	 * addModifier method for the given VariableID.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which a Modifier should be removed from the
	 *            responsible Solver
	 * @param modifier
	 *            The Modifier to be removed from the Solver identified by the given
	 *            VariableID
	 * @param source
	 *            The source object for the Modifier to be removed from the Solver
	 *            identified by the given VariableID
	 * @throws IllegalArgumentException
	 *             if any of the parameters is null
	 */
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source);

	/**
	 * Provides a List of ProcessStep objects identifying how the current value of the
	 * variable identified by the given VariableID has been calculated.
	 * 
	 * The ProcessStep objects are provided in the order of operations, with the first
	 * object in the list being the first step in the derivation.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which the List of ProcessStep objects should be
	 *            returned.
	 * @return The List of ProcessStep objects identifying how the current value of the
	 *         variable identified by the given VariableID has been calculated
	 */
	public <T> List<ProcessStep<T>> diagnose(VariableID<T> varID);

	/**
	 * Returns the Default Value (in the underlying SolverFactory) for the given Variable
	 * Format.
	 * 
	 * @param varFormat
	 *            The variable format for which the default value should be returned
	 * @param <T>
	 *            The format for which the default value should be returned
	 * @return The Default Value for the given Variable Format.
	 */
	public <T> T getDefaultValue(Class<T> varFormat);

	/**
	 * Triggers Solvers to be called, recursively through the dependencies, from the
	 * children of the given VariableID.
	 * 
	 * @param varID
	 *            The VariableID for which the children will be used as a starting point
	 *            for triggering Solvers to be processed
	 */
	public void solveChildren(VariableID<?> varID);
}
