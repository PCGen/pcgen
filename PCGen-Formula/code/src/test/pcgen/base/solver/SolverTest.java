/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.calculation.ArrayComponentModifier;
import pcgen.base.calculation.Modifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.solver.testsupport.AbstractModifier;

public class SolverTest extends TestCase
{
	private ScopeInformation si;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SplitFormulaSetup sfs = new SplitFormulaSetup();
		sfs.getLegalScopeLibrary().registerScope(
			new SimpleLegalScope(null, "Global"));
		si = new IndividualSetup(sfs, "Global").getScopeInfo();
	}

	@Test
	public void testIllegalConstruction()
	{
		Modifier<Number> mod = AbstractModifier.add(1, 100);
		try
		{
			new Solver<Number>(mod, si);
			fail("Default Modifier was not static");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		mod = AbstractModifier.setNumber(6, 0);
		try
		{
			new Solver<Number>(mod, null);
			fail("null Scope Info must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new Solver<Number>(null, si);
			fail("null default must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

	}

	@Test
	public void testIllegalAdd()
	{
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		mod = AbstractModifier.add(1, 100);
		try
		{
			solver.addModifier(null, new Object());
			fail("Null modifier must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			solver.addModifier(mod, null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		Modifier<String> badm = AbstractModifier.setString();
		try
		{
			//have to be bad about generics to even get this to be set up to fail
			Modifier m = badm;
			solver.addModifier(m, new Object());
			fail("wrong type must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			//have to be bad about generics to even get this to be set up to fail
			Modifier m = new Modifier(){

				@Override
				public Object process(Object input, ScopeInformation scopeInfo, Object owner)
				{
					return 3;
				}

				@Override
				public void getDependencies(DependencyManager fdm)
				{
				}

				@Override
				public String getInstructions()
				{
					return "3";
				}

				@Override
				public String getIdentification()
				{
					return "SET";
				}

				@Override
				public Class getVariableFormat()
				{
					return Number.class;
				}

				@Override
				public int getInherentPriority()
				{
					//bad (intentional)
					return -1;
				}

				@Override
				public int getUserPriority()
				{
					return 0;
				}};
			solver.addModifier(m, new Object());
			fail("wrong type must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

	}

	@Test
	public void testIllegalRemove()
	{
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		mod = AbstractModifier.add(1, 100);
		try
		{
			solver.removeModifier(null, new Object());
			fail("Null modifier must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			solver.removeModifier(mod, null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testIllegalRemoveFromSource()
	{
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		try
		{
			solver.removeFromSource(null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testHarmless()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.removeModifier(addm, this);
		assertEquals(Integer.valueOf(6), solver.process());
	}

	@Test
	public void testRemoveFromSource()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		//harmless
		solver.removeFromSource(this);
		assertEquals(Integer.valueOf(6), solver.process());
		//now do real stuff
		solver.addModifier(addm, this);
		solver.addModifier(multm, new Object());
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(9), solver.process());
		solver.removeFromSource(this);
		assertEquals(Integer.valueOf(12), solver.process());
		//Harmless
		solver.removeFromSource(new Object());
		assertEquals(Integer.valueOf(12), solver.process());
	}

	@Test
	public void testProcessSamePriority()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(9), solver.process());
		solver.removeModifier(addm, this);
		assertEquals(Integer.valueOf(8), solver.process());
	}

	@Test
	public void testProcessUserPriority1()
	{
		//Will be ignored due to later set
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 200);
		Modifier<Number> multm = AbstractModifier.multiply(2, 300);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(8), solver.process());
	}

	@Test
	public void testProcessUserPriority2()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 300);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		assertEquals(Integer.valueOf(14), solver.process());
	}

	@Test
	public void testDiagnose()
	{
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number> setm = AbstractModifier.setNumber(4, 100);
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number> mod = AbstractModifier.setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		List<ProcessStep<Number>> list = solver.diagnose();
		assertNotNull(list);
		assertEquals(1, list.size());
		ProcessStep<Number> step = list.get(0);
		assertEquals("DefaultValue for Number", step.getSourceInfo());
		assertEquals(6, step.getResult());
		assertEquals(mod, step.getModifier());
		solver.addModifier(addm, this);
		//Shouldn't be related (verify list is not reference semantic)
		assertEquals(1, list.size());
		Object multSrc = new Object();
		solver.addModifier(multm, multSrc);
		solver.addModifier(setm, this);
		list = solver.diagnose();
		assertEquals(4, list.size());
		step = list.get(0);
		assertEquals("DefaultValue for Number", step.getSourceInfo());
		assertEquals(6, step.getResult());
		assertEquals(mod, step.getModifier());
		step = list.get(1);
		assertEquals(this, step.getSource());
		assertEquals(4, step.getResult());
		assertEquals(setm, step.getModifier());
		step = list.get(2);
		assertEquals(multSrc, step.getSource());
		assertEquals(8, step.getResult());
		assertEquals(multm, step.getModifier());
		step = list.get(3);
		assertEquals(this, step.getSource());
		assertEquals(9, step.getResult());
		assertEquals(addm, step.getModifier());

	}

	@Test
	public void testArrayMod()
	{
		Solver<Number[]> solver =
				new Solver<Number[]>(AbstractModifier.setEmptyArray(0), si);
		assertTrue(Arrays.equals(new Number[]{}, solver.process()));
		Modifier<Number[]> add1 = AbstractModifier.addToArray(1, 10);
		solver.addModifier(add1, this);
		assertTrue(Arrays.equals(new Number[]{1}, solver.process()));
		Modifier<Number[]> add2 = AbstractModifier.addToArray(2, 11);
		solver.addModifier(add2, this);
		assertTrue(Arrays.equals(new Number[]{1, 2}, solver.process()));
		Modifier<Number[]> add3 = AbstractModifier.addToArray(3, 12);
		solver.addModifier(add3, this);
		assertTrue(Arrays.equals(new Number[]{1, 2, 3}, solver.process()));
		Modifier<Number> addm = AbstractModifier.add(1, 100);
		Modifier<Number[]> addTo1 = new ArrayComponentModifier<>(0, addm);
		solver.addModifier(addTo1, this);
		assertTrue(Arrays.equals(new Number[]{2, 2, 3}, solver.process()));
		Modifier<Number> multm = AbstractModifier.multiply(2, 100);
		Modifier<Number[]> multTo2 = new ArrayComponentModifier<>(1, multm);
		solver.addModifier(multTo2, this);
		assertTrue(Arrays.equals(new Number[]{2, 4, 3}, solver.process()));
		Modifier<Number> setm = AbstractModifier.setNumber(7, 100);
		Modifier<Number[]> setTo3 = new ArrayComponentModifier<>(2, setm);
		solver.addModifier(setTo3, this);
		assertTrue(Arrays.equals(new Number[]{2, 4, 7}, solver.process()));
		solver.removeModifier(add1, this);
		assertTrue(Arrays.equals(new Number[]{3, 6}, solver.process()));
	}
}
