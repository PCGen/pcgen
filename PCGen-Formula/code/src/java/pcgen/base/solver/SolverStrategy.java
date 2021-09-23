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

import pcgen.base.formula.base.DependencyConsumer;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableSolver;

/**
 * A SolverStrategy is the analyzer of the dependencies in a SolverSystem, and decides the
 * method used to calculate the results of the VariableIDs defined in that system.
 */
public interface SolverStrategy
{

	/**
	 * Notifies the SolverStrategy that the given VariableID has been updated (and any
	 * dependent items may need to be addressed).
	 * 
	 * @param varID
	 *            The VariableID where the value has been updated
	 */
	public void processValueUpdated(VariableID<?> varID);

	/**
	 * Notifies the SolverStrategy that the process to calculate the result of a given
	 * VariableID has been updated.
	 * 
	 * @param varID
	 *            The VariableID where the inputs have been updated
	 */
	public void processModsUpdated(VariableID<?> varID);

	/**
	 * Generates a Replacement SolverStrategy with the given arguments.
	 * 
	 * @param depManager
	 *            The new DependencyConsumer for the replacement SolverStrategy
	 * @param solver
	 *            The new VariableSolver used to solve a given VariableID
	 * @return A Replacement SolverStrategy with the given arguments
	 */
	public SolverStrategy generateReplacement(DependencyConsumer depManager,
		VariableSolver solver);

}
