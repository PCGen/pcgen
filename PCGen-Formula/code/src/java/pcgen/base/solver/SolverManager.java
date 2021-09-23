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

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;

/**
 * A SolverManager manages solving the variable represented by a VariableID and the
 * associated Modifiers.
 */
public interface SolverManager
{
	/**
	 * Adds the given Modifier to the variable identified by the given VariableID. The
	 * Modifier is associated to the given source.
	 * 
	 * @param <T>
	 *            The format of object contained in the variable identified by the given
	 *            VariableID
	 * @param varID
	 *            The VariableID identifying the variable the given Modifier should
	 *            influence
	 * @param modifier
	 *            The Modifier that should be added to the variable identified by the
	 *            given VariableID
	 * @param source
	 *            The source of the Modifier
	 */
	public <T> void addModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source);

	/**
	 * Removes the given Modifier from the variable identified by the given VariableID,
	 * provided the Modifier is associated with the given source.
	 * 
	 * @param <T>
	 *            The format of object contained in the variable identified by the given
	 *            VariableID
	 * @param varID
	 *            The VariableID identifying the variable the given Modifier is associated
	 *            with
	 * @param modifier
	 *            The Modifier that should be removed from the variable identified by the
	 *            given VariableID
	 * @param source
	 *            The source of the Modifier
	 */
	public <T> void removeModifier(VariableID<T> varID, Modifier<T> modifier,
		ScopeInstance source);

	/**
	 * Loads the dependencies for all Modifiers associated with the given VariableID into
	 * the given DependencyManager.
	 * 
	 * The DependencyManager may not be altered if there are no dependencies for the
	 * variable represented by the given VariableID. this Modifier.
	 * 
	 * @param varID
	 *            The VariableID identifying the variable to be analyzed for dependencies
	 * @param dependencyManager
	 *            The DependencyManager to be notified of dependencies for this Modifier
	 */
	public void captureAllDependencies(VariableID<?> varID,
		DependencyManager dependencyManager);

	/**
	 * Processes a single Solver represented by the given VariableID. Returns true if the
	 * value of the variable calculated by the Solver has changed due to this processing.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which the given Solver should be processed.
	 * 
	 * @return true if the value of the variable calculated by the Solver has changed due
	 *         to this processing; false otherwise
	 */
	public <T> boolean processSolver(VariableID<T> varID);

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
	 * Returns the default value for a given FormatManager.
	 * 
	 * @param <T>
	 *            The format (class) of object for which the default value should be
	 *            returned
	 * @param formatManager
	 *            The FormatManager for which the default value should be returned
	 * @return The default value for the given FormatManager
	 */
	public <T> T getDefault(FormatManager<T> formatManager);

	/**
	 * Directly solves a given NEPFormula using the information in this SolverManager.
	 * 
	 * @param <T>
	 *            The format (class) of the value returned by the given NEPFormula
	 * @param formula
	 *            The NEPFormula to be solved using the information in this SolverManager
	 * @return The result of evaluating the given formula based on information in this
	 *         SolverManager
	 */
	public <T> T solve(NEPFormula<T> formula);

	/**
	 * Creates a replacement SolverManager for this SolverManager. The replacement will
	 * have the given VariableStore as the destination for calculations. Any underlying
	 * system of calculation in the resulting SolverManager must be independent of this
	 * SolverManager. For example, an addition of a Modifier to either SolverManager must
	 * not modify the other.
	 * 
	 * @param newVarStore
	 *            The WriteableVariableStore for the new SolverManager
	 * @return A SolverManager sharing the same default values, existing values, but with
	 *         independent calculation pathways from this SolverManager
	 */
	public SolverManager createReplacement(WriteableVariableStore newVarStore);

}
