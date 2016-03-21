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
package pcgen.base.calculation;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.calculation.testsupport.BasicCalc;
import pcgen.base.calculation.testsupport.NepCalc;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.operator.number.NumberAdd;

public class ArrayComponentModifierTest extends TestCase
{
	private BasicCalculation basic = getBasicCalc(new NumberAdd(), 6);
	private NEPCalculation calc = getNEPCalc(33);

	@Test
	public void testConstructor()
	{
		try
		{
			new ArrayComponentModifier(5, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new ArrayComponentModifier(-5, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetUserPriority()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals(5, acm.getUserPriority());
	}

	@Test
	public void testGetInherentPriority()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals(6, acm.getInherentPriority());
	}

	@Test
	public void testGetVariableFormat()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals(new Number[]{}.getClass(), acm.getVariableFormat());
	}

	@Test
	public void testGetIdentification()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals("Basic (component)", acm.getIdentification());
	}

	@Test
	public void testGetInstructions()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		assertEquals("To [5]: +33", acm.toString());
	}

	@Test
	public void testProcess()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		Number[] array = new Number[]{1, 2, 3, 4, 5, 6, 7};
		Object[] result = acm.process(array, null, null);
		array[5] = array[5].intValue() + 33;
		assertTrue(Arrays.deepEquals(array, result));
	}


	@Test
	public void testProcessOutOfBounds()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		ArrayComponentModifier acm = new ArrayComponentModifier(5, cm);
		Number[] array = new Number[]{1, 2, 3, 4};
		//Should be no effect
		Object[] result = acm.process(array, null, null);
		assertTrue(Arrays.deepEquals(array, result));
	}

//	@Test
//	public void testEquals()
//	{
//		CalculationModifier cm = new CalculationModifier(calc, 5);
//		NEPCalculation calc2 = getNEPCalc(33);
//		CalculationModifier cm2 = new CalculationModifier(calc2, 5);
//		assertTrue(cm.equals(cm2));
//		assertEquals(cm.hashCode(), cm2.hashCode());
//		CalculationModifier cm3 = new CalculationModifier(calc, 3);
//		assertFalse(cm.equals(cm3));
//		assertFalse(cm.equals(new Object()));
//	}
//

	private AbstractNEPCalculation getNEPCalc(final Number n)
	{
		return new NepCalc(basic, n);
	}

	private BasicCalculation getBasicCalc(final OperatorAction oa,
		final int inherentPriority)
	{
		return new BasicCalc(oa);
	}

}
