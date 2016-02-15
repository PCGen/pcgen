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
package plugin.modifier.number;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.util.FormatManager;
import pcgen.base.util.FormatManagerLibrary;

public class MultiplyNumberModifierTest extends TestCase
{

	private LegalScope varScope = new SimpleLegalScope(null, "Global");
	FormatManager<Number> numManager =
			FormatManagerLibrary.getFormatManager(Number.class);

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			MultiplyModifierFactory m = new MultiplyModifierFactory();
			m.getModifier(100, null, null, null, null);
			fail("Expected MultiplyModifier with null multiply value to fail");
		}
		catch (IllegalArgumentException e)
		{
			//Yep!
		}
		catch (NullPointerException e)
		{
			//Yep! okay too!
		}
	}

	@Test
	public void testProcessNegative1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(6), modifier.process(-2, -3));
	}

	@Test
	public void testProcessNegative2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(8), modifier.process(-4, -2));
	}

	@Test
	public void testProcessPositive1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(6), modifier.process(2, 3));
	}

	@Test
	public void testProcessPositive2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(12), modifier.process(4, 3));
	}

	@Test
	public void testProcessZero1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(0, 3));
	}

	@Test
	public void testProcessZero2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(4, 0));
	}

	@Test
	public void testProcessZero3()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(0, -3));
	}

	@Test
	public void testProcessZero4()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(-4,0));
	}

	@Test
	public void testProcessMixed1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(-35), modifier.process(5,-7));
	}

	@Test
	public void testProcessMixed2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Integer.valueOf(-12), modifier.process(-4,3));
	}

	@Test
	public void testProcessDoubleNegative1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(3.57), modifier.process(-2.1, -1.7));
	}

	@Test
	public void testProcessDoubleNegative2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(4.16), modifier.process(-2.6, -1.6));
	}

	@Test
	public void testProcessDoublePositive1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(9.1), modifier.process(2.6, 3.5));
	}

	@Test
	public void testProcessDoublePositive2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(5.89), modifier.process(1.9, 3.1));
	}

	@Test
	public void testProcessDoubleZero1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(0.0, 3.1));
	}

	@Test
	public void testProcessDoubleZero2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(4.2, 0.0));
	}

	@Test
	public void testProcessDoubleZero3()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(-0.0), modifier.process(0.0, -3.4));
	}

	@Test
	public void testProcessDoubleZero4()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(-0.0), modifier.process(-4.3,0.0));
	}

	@Test
	public void testProcessDoubleMixed1()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(-38.16), modifier.process(5.3,-7.2));
	}

	@Test
	public void testProcessDoubleMixed2()
	{
		MultiplyModifierFactory modifier = new MultiplyModifierFactory();
		assertEquals(Double.valueOf(-3.08), modifier.process(-2.2,1.4));
	}

	@Test
	public void testGetModifier()
	{
		MultiplyModifierFactory factory = new MultiplyModifierFactory();
		Modifier<Number> modifier =
				factory.getModifier(35, "6.5", null, varScope, numManager);
		assertEquals(factory.getInherentPriority(), modifier.getInherentPriority());
		assertEquals(35, modifier.getUserPriority());
		assertEquals(Number.class, modifier.getVariableFormat());
		assertEquals(27.95, modifier.process(4.3, null));
	}
}
