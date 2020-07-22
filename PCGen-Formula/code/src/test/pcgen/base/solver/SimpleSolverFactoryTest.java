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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.testsupport.TestUtilities;

public class SimpleSolverFactoryTest
{
	@Test
	public void testIllegalGetDefault()
	{
		SolverFactory factory = new SimpleSolverFactory(new SupplierValueStore());
		assertThrows(NullPointerException.class, () -> factory.getDefault(null));
		assertThrows(NullPointerException.class, () -> factory.getDefault(FormatUtilities.NUMBER_MANAGER));
	}

	@Test
	public void testIllegalGetSolver()
	{
		SolverFactory factory = new SimpleSolverFactory(new SupplierValueStore());
		assertThrows(NullPointerException.class, () -> factory.getSolver(new NumberManager()));
		assertThrows(NullPointerException.class, () -> factory.getSolver(null));
	}

	@Test
	public void testAddSolverFormat()
	{
		SolverFactory factory = new SimpleSolverFactory(new SupplierValueStore());
		assertThrows(NullPointerException.class, () -> factory.addSolverFormat(null, () -> 9));
		assertThrows(NullPointerException.class, () -> factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, null));
		//But this is safe
		Supplier<? extends Number> default108 = () -> 108;
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertEquals(108, factory.getDefault(FormatUtilities.NUMBER_MANAGER));
		assertThrows(IllegalArgumentException.class, () -> factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 111));
		//But you can set it to the same thing (maybe?)
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertTrue(factory.validateDefaults().get());
		//and you can use in an array
		Solver<Number[]> solver = factory.getSolver(TestUtilities.NUMBER_ARRAY_MANAGER);
		assertEquals(0, solver.process(new EvaluationManager()).length);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testIllegalAddSolverFormatGenerics()
	{
		SolverFactory factory = new SimpleSolverFactory(new SupplierValueStore());
		Supplier<Number> setNumber = () -> 9;
		//intentionally break generics
		factory.addSolverFormat(FormatUtilities.STRING_MANAGER, (Supplier) setNumber);
		assertFalse(factory.validateDefaults().get(),
			"Should not be able to add Format with mismatch");
	}

	@Test
	public void testIllegalAddSolverFormatDouble()
	{
		SolverFactory factory = new SimpleSolverFactory(new SupplierValueStore());
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 108);
		assertThrows(IllegalArgumentException.class, () -> factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 9));
	}
}
