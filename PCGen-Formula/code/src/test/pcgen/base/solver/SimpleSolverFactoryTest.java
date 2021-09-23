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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;

public class SimpleSolverFactoryTest
{
	@Test
	public void testAddSolverFormat()
	{
		SupplierValueStore svs = new SupplierValueStore();
		svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 108);
		assertTrue(svs.validateDefaults().get());
		//you can use in an array
		//TODO Stuff
//		SolverManager factory = new SimpleSolverManager(svs, new SimpleVariableStore());
//		Solver<Number[]> solver = factory.getSolver(TestUtilities.NUMBER_ARRAY_MANAGER);
//		assertEquals(0, solver.process(new EvaluationManager()).length);
	}
}
