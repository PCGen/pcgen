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

import pcgen.base.format.NumberManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;
import plugin.modifier.testsupport.EvalManagerUtilities;

public class MinNumberModifierTest extends TestCase
{
	private LegalScope varScope = new SimpleLegalScope(null, "Global");
	FormatManager<Number> numManager = new NumberManager();


	@Test
	public void testInvalidConstruction()
	{
		try
		{
			MinModifierFactory m = new MinModifierFactory();
			m.getModifier(100, null, null, null, null);
			fail("Expected MaxModifier with null compare value to fail");
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
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-3), modifier.process(-2, -3));
	}

	@Test
	public void testProcessNegative2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-4), modifier.process(-4, -2));
	}

	@Test
	public void testProcessPositive1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(2), modifier.process(2, 3));
	}

	@Test
	public void testProcessPositive2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(3), modifier.process(4, 3));
	}

	@Test
	public void testProcessZero1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(0, 3));
	}

	@Test
	public void testProcessZero2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(4, 0));
	}

	@Test
	public void testProcessZero3()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-3), modifier.process(0, -3));
	}

	@Test
	public void testProcessZero4()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-4), modifier.process(-4,0));
	}

	@Test
	public void testProcessMixed1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-7), modifier.process(5,-7));
	}

	@Test
	public void testProcessMixed2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Integer.valueOf(-4), modifier.process(-4,3));
	}

	@Test
	public void testProcessDoubleNegative1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-3.4), modifier.process(-2.3, -3.4));
	}

	@Test
	public void testProcessDoubleNegative2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-4.3), modifier.process(-4.3, -2.4));
	}

	@Test
	public void testProcessDoublePositive1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(2.6), modifier.process(2.6, 3.5));
	}

	@Test
	public void testProcessDoublePositive2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(3.1), modifier.process(4.4, 3.1));
	}

	@Test
	public void testProcessDoubleZero1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(0.0, 3.1));
	}

	@Test
	public void testProcessDoubleZero2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(4.2, 0.0));
	}

	@Test
	public void testProcessDoubleZero3()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-3.4), modifier.process(0.0, -3.4));
	}

	@Test
	public void testProcessDoubleZero4()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-4.3), modifier.process(-4.3,0.0));
	}

	@Test
	public void testProcessDoubleMixed1()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-7.2), modifier.process(5.3,-7.2));
	}

	@Test
	public void testProcessDoubleMixed2()
	{
		MinModifierFactory modifier = new MinModifierFactory();
		assertEquals(Double.valueOf(-4.2), modifier.process(-4.2,3.1));
	}

	@Test
	public void testGetModifier()
	{
		MinModifierFactory factory = new MinModifierFactory();
		Modifier<Number> modifier =
				factory.getModifier(35, "6.5", null, varScope, numManager);
		assertEquals((35l<<32)+factory.getInherentPriority(), modifier.getPriority());
		assertEquals(Number.class, modifier.getVariableFormat());
		assertEquals(4.3, modifier.process(EvalManagerUtilities.getInputEM(4.3)));
		assertEquals(6.5, modifier.process(EvalManagerUtilities.getInputEM(9.3)));
	}
}
