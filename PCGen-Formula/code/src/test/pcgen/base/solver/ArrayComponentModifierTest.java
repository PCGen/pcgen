/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.solver.testsupport.AbstractModifier;

public class ArrayComponentModifierTest extends TestCase
{
	@Test
	public void testConstructor()
	{
		try
		{
			new ArrayComponentModifier(5, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new ArrayComponentModifier(-5, null);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testGetUserPriority()
	{
		Modifier cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals((100L << 32), acm.getPriority());
	}

	@Test
	public void testGetVariableFormat()
	{
		Modifier cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals(Number[].class, acm.getVariableFormat());
	}

	@Test
	public void testGetIdentification()
	{
		Modifier cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals("Set (component)", acm.getIdentification());
	}

	@Test
	public void testGetInstructions()
	{
		Modifier cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals("To [5]: +3", acm.toString());
	}

	@Test
	public void testProcess()
	{
		Modifier cm = AbstractModifier.setNumber(6, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		EvaluationManager manager = new EvaluationManager();
		manager.set(EvaluationManager.INPUT, array);
		Object[] result = acm.process(manager);
		assertFalse(array == result);
		array[5] = 6;
		assertTrue(Arrays.deepEquals(array, result));
	}


	@Test
	public void testProcessOutOfBounds()
	{
		Modifier cm = AbstractModifier.setNumber(77, 100);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		Number[] array = {1, 2, 3, 4};
		//Should be no effect
		EvaluationManager manager = new EvaluationManager();
		manager.set(EvaluationManager.INPUT, array);
		Object[] result = acm.process(manager);
		assertTrue(Arrays.deepEquals(array, result));
	}
}
