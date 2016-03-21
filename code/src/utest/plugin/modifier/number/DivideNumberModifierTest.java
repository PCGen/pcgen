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
import pcgen.base.format.NumberManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.util.FormatManager;

public class DivideNumberModifierTest extends TestCase
{

	private LegalScope varScope = new SimpleLegalScope(null, "Global");
	FormatManager<Number> numManager = new NumberManager();

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			DivideModifierFactory m = new DivideModifierFactory();
			m.getModifier(100, null, null, null, null);
			fail("Expected DivideModifierFactory with null divide value to fail");
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
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(-2), modifier.process(6, -3));
	}

	@Test
	public void testProcessNegative2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(-4), modifier.process(8, -2));
	}

	@Test
	public void testProcessPositive1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(2), modifier.process(6, 3));
	}

	@Test
	public void testProcessPositive2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(4), modifier.process(12, 3));
	}

	@Test
	public void testProcessZero1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(0, 3));
	}

	@Test
	public void testProcessZero2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.POSITIVE_INFINITY, modifier.process(4, 0));
	}

	@Test
	public void testProcessZero3()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(0, -3));
	}

	@Test
	public void testProcessZero4()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.NEGATIVE_INFINITY, modifier.process(-4, 0));
	}

	@Test
	public void testProcessMixed1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(5), modifier.process(-35, -7));
	}

	@Test
	public void testProcessMixed2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Integer.valueOf(-4), modifier.process(-12, 3));
	}

	@Test
	public void testProcessDoubleNegative1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(-2.1), modifier.process(3.57, -1.7));
	}

	@Test
	public void testProcessDoubleNegative2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(-2.6), modifier.process(4.16, -1.6));
	}

	@Test
	public void testProcessDoublePositive1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(2.6), modifier.process(9.1, 3.5));
	}

	@Test
	public void testProcessDoublePositive2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(1.9), modifier.process(5.89, 3.1));
	}

	@Test
	public void testProcessDoubleZero1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(0.0, 3.1));
	}

	@Test
	public void testProcessDoubleZero2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.POSITIVE_INFINITY, modifier.process(4.2, 0.0));
	}

	@Test
	public void testProcessDoubleZero3()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(-0.0), modifier.process(0.0, -3.4));
	}

	@Test
	public void testProcessDoubleZero4()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.NEGATIVE_INFINITY, modifier.process(-4.3, 0.0));
	}

	@Test
	public void testProcessDoubleMixed1()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(5.3), modifier.process(-38.16, -7.2));
	}

	@Test
	public void testProcessDoubleMixed2()
	{
		DivideModifierFactory modifier = new DivideModifierFactory();
		assertEquals(Double.valueOf(-2.2), modifier.process(-3.08, 1.4));
	}

	@Test
	public void testGetModifier()
	{
		DivideModifierFactory factory = new DivideModifierFactory();
		Modifier<Number> modifier =
				factory.getModifier(35, "4.3", null, varScope, numManager);
		assertEquals(factory.getInherentPriority(),
			modifier.getInherentPriority());
		assertEquals(35, modifier.getUserPriority());
		assertEquals(Number.class, modifier.getVariableFormat());
		assertEquals(3.2, modifier.process(13.76, null));
	}
}
