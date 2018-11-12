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

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.solver.testsupport.AbstractModifier;

public class SolverFactoryTest extends TestCase
{
	private SolverFactory factory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		factory = new SolverFactory();
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
			factory.getDefault(Number.class);
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
		catch (IllegalArgumentException e)
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
		AbstractModifier<Number> setNumber = AbstractModifier.setNumber(9, 100);
		try
		{
			factory.addSolverFormat(null, setNumber);
			fail("Should not be able to set Solver for null format");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			factory.addSolverFormat(Number.class, null);
			fail("Should not be able to set Solver for null default");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		//But this is safe
		factory.addSolverFormat(Number.class, AbstractModifier.setNumber(108, 28));
		assertEquals(108, factory.getDefault(Number.class));
		try
		{
			factory.addSolverFormat(Number.class, AbstractModifier.setNumber(111, 228));
			fail("You can't reset a default to a different value");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		//But you can set it to the same thing
		factory.addSolverFormat(Number.class, AbstractModifier.setNumber(108, 28));
		assertTrue(factory.validateDefaults().get());
	}

	@Test
	public void testIllegalAddSolverFormatAdding()
	{
		factory.addSolverFormat(Number.class, AbstractModifier.add(9, 5));
		assertFalse("Should not be able to set Solver for adding modifier",
			factory.validateDefaults().get());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testIllegalAddSolverFormatGenerics()
	{
		AbstractModifier<Number> setNumber = AbstractModifier.setNumber(9, 100);
		//intentionally break generics
		factory.addSolverFormat(String.class, (Modifier) setNumber);
		assertFalse("Should not be able to add Format with mismatch",
			factory.validateDefaults().get());
	}

	@Test
	public void testIllegalAddSolverFormatDouble()
	{
		factory.addSolverFormat(Number.class, AbstractModifier.setNumber(108, 28));
		try
		{
			factory.addSolverFormat(Number.class, AbstractModifier.setNumber(9, 5));
			fail("Should not be able to set Default a second time");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
}
