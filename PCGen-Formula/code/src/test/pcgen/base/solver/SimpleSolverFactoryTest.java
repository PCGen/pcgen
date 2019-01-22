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

import java.util.function.Supplier;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.util.FormatManager;

public class SimpleSolverFactoryTest extends TestCase
{
	private final FormatManager<Number[]> NAF =
			new ArrayFormatManager<>(FormatUtilities.NUMBER_MANAGER, '\n', ',');

	private SolverFactory factory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SupplierValueStore valueStore = new SupplierValueStore();
		factory = new SimpleSolverFactory(valueStore);
	}

	@Test
	public void testIllegalGetDefault()
	{
		try
		{
			factory.getDefault(null);
			fail("Should not be able to get Default for null");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		try
		{
			factory.getDefault(FormatUtilities.NUMBER_MANAGER);
			fail("Should not be able to get Default when none was set");
		}
		catch (NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testIllegalGetSolver()
	{
		try
		{
			factory.getSolver(new NumberManager());
			fail("Should not be able to get Solver when no default was set");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			factory.getSolver(null);
			fail("Should not be able to get Solver for null FormatManager");
		}
		catch (NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testAddSolverFormat()
	{
		try
		{
			factory.addSolverFormat(null, () -> 9);
			fail("Should not be able to set Solver for null format");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, null);
			fail("Should not be able to set Solver for null default");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		//But this is safe
		Supplier<? extends Number> default108 = () -> 108;
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertEquals(108, factory.getDefault(FormatUtilities.NUMBER_MANAGER));
		try
		{
			factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 111);
			fail("You can't reset a default to a different value");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		//But you can set it to the same thing (maybe?)
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertTrue(factory.validateDefaults().get());
		//and you can use in an array
		Solver<Number[]> solver = factory.getSolver(NAF);
		assertEquals(0, solver.process(new EvaluationManager()).length);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testIllegalAddSolverFormatGenerics()
	{
		Supplier<Number> setNumber = () -> 9;
		//intentionally break generics
		factory.addSolverFormat(FormatUtilities.STRING_MANAGER, (Supplier) setNumber);
		assertFalse("Should not be able to add Format with mismatch",
			factory.validateDefaults().get());
	}

	@Test
	public void testIllegalAddSolverFormatDouble()
	{
		factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 108);
		try
		{
			factory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 9);
			fail("Should not be able to set Default a second time");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
}
