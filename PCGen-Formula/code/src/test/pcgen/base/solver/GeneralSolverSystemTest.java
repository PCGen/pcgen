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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pcgen.base.testsupport.AbstractFormulaTestCase;

public class GeneralSolverSystemTest extends AbstractFormulaTestCase
{
	@Test
	public void testIllegalConstruction()
	{
		SimpleSolverManager newSolver = new SimpleSolverManager(
			getFormulaManager().getFactory()::isLegalVariableID,
			getFormulaManager(), getManagerFactory(), getValueStore(),
			getVariableStore());
		SolverDependencyManager dm = new StaticSolverDependencyManager(getFormulaManager(),
			getManagerFactory(), newSolver::initialize);
		SolverStrategy strategy =
				new AggressiveStrategy(dm::processForChildren, newSolver::processSolver);
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(null, dm, strategy));
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(newSolver, null, strategy));
		assertThrows(NullPointerException.class, () -> new GeneralSolverSystem(newSolver, dm, null));
	}
}
