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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.calculation.testsupport.BasicCalc;
import pcgen.base.calculation.testsupport.NepCalc;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.operator.number.NumberAdd;

public class CalculationModifierTest extends TestCase
{
	private BasicCalculation basic = getBasicCalc(new NumberAdd(), 6);
	private NEPCalculation calc = getNEPCalc(33);

	@Test
	public void testConstructor()
	{
		try
		{
			new CalculationModifier(null, 5);
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
		assertEquals(5, cm.getUserPriority());
	}

	@Test
	public void testGetInherentPriority()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		assertEquals(6, cm.getInherentPriority());
	}

	@Test
	public void testGetVariableFormat()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		assertEquals(Number.class, cm.getVariableFormat());
	}

	@Test
	public void testGetIdentification()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		assertEquals("Basic", cm.getIdentification());
	}

	@Test
	public void testGetInstructions()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		assertEquals("33", cm.getInstructions());
	}

	@Test
	public void testProcess()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		assertEquals(42, cm.process(9, null));
	}

	@Test
	public void testEquals()
	{
		CalculationModifier cm = new CalculationModifier(calc, 5);
		NEPCalculation calc2 = getNEPCalc(33);
		CalculationModifier cm2 = new CalculationModifier(calc2, 5);
		assertTrue(cm.equals(cm2));
		assertEquals(cm.hashCode(), cm2.hashCode());
		CalculationModifier cm3 = new CalculationModifier(calc, 3);
		assertFalse(cm.equals(cm3));
		assertFalse(cm.equals(new Object()));
	}

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
