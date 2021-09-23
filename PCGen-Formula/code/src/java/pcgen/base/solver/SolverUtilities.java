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

import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.inst.MonitorableVariableStore;
import pcgen.base.util.ValueStore;

/**
 * SolverUtilities are utilities related to Solvers and SolverSystems.
 */
public final class SolverUtilities
{

	private SolverUtilities()
	{
		//Do not construct utility class
	}

	/**
	 * Builds a new GeneralSolverSystem with an Aggressive SolverStrategy and a Static
	 * SolverDependencyManager.
	 * 
	 * @param varLib
	 *            The VariableLibrary used to set up the SolverSystem
	 * @param managerFactory
	 *            The ManagerFactory used to set up the SolverSystem
	 * @param valueStore
	 *            The ValueStore used to set up the SolverSystem
	 * @param resultStore
	 *            The MonitorableVariableStore used to set up the SolverSystem
	 * @return The new GeneralSolverSystem
	 */
	public static GeneralSolverSystem buildStaticSolverSystem(
		VariableLibrary varLib, ManagerFactory managerFactory,
		ValueStore valueStore, MonitorableVariableStore resultStore)
	{
		SimpleSolverManager newSolver =
				new SimpleSolverManager(varLib::isLegalVariableID,
					managerFactory, valueStore, resultStore);
		SolverDependencyManager dm = new StaticSolverDependencyManager(
			managerFactory);
		SolverStrategy strategy =
				new AggressiveStrategy(dm::processForChildren, newSolver::processSolver);
		resultStore.addGeneralListener(event -> strategy.processValueUpdated(event.getVarID()));
		return new GeneralSolverSystem(newSolver, dm, strategy);
	}

	/**
	 * Builds a new GeneralSolverSystem with an Aggressive SolverStrategy and a Dynamic
	 * SolverDependencyManager.
	 * 
	 * @param varLib
	 *            The VariableLibrary used to set up the SolverSystem
	 * @param managerFactory
	 *            The ManagerFactory used to set up the SolverSystem
	 * @param valueStore
	 *            The ValueStore used to set up the SolverSystem
	 * @param resultStore
	 *            The MonitorableVariableStore used to set up the SolverSystem
	 * @return The new GeneralSolverSystem
	 */
	public static GeneralSolverSystem buildDynamicSolverSystem(
		VariableLibrary varLib, ManagerFactory managerFactory,
		ValueStore valueStore, MonitorableVariableStore resultStore)
	{
		SimpleSolverManager newSolver =
				new SimpleSolverManager(varLib::isLegalVariableID,
					managerFactory, valueStore, resultStore);
		SolverDependencyManager dm = new DynamicSolverDependencyManager(
			managerFactory, resultStore);
		SolverStrategy strategy =
				new AggressiveStrategy(dm::processForChildren, newSolver::processSolver);
		resultStore.addGeneralListener(event -> strategy.processValueUpdated(event.getVarID()));
		return new GeneralSolverSystem(newSolver, dm, strategy);
	}
}
