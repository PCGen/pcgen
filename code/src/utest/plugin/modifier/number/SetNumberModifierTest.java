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
import pcgen.base.solver.SplitFormulaSetup;
import pcgen.base.solver.SplitFormulaSetup.IndividualSetup;
import pcgen.base.util.FormatManager;
import pcgen.base.util.FormatManagerLibrary;

public class SetNumberModifierTest extends TestCase
{

	private LegalScope varScope = new SimpleLegalScope(null, "Global");
	FormatManager<Number> numManager =
			FormatManagerLibrary.getFormatManager(Number.class);

	@Test
	public void testInvalidConstruction()
	{
		try
		{
			SetModifierFactory m = new SetModifierFactory();
			m.getModifier(100, null, null, null, null);
			fail("Expected SetModifier with null set value to fail");
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
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(-3), modifier.process(-2, -3));
	}

	@Test
	public void testProcessNegative2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(-2), modifier.process(-4, -2));
	}

	@Test
	public void testProcessPositive1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(3), modifier.process(2, 3));
	}

	@Test
	public void testProcessPositive2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(3), modifier.process(4, 3));
	}

	@Test
	public void testProcessZero1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(3), modifier.process(0, 3));
	}

	@Test
	public void testProcessZero2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(4, 0));
	}

	@Test
	public void testProcessZero3()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(-3), modifier.process(0, -3));
	}

	@Test
	public void testProcessZero4()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(0), modifier.process(-4,0));
	}

	@Test
	public void testProcessMixed1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(-7), modifier.process(5,-7));
	}

	@Test
	public void testProcessMixed2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Integer.valueOf(3), modifier.process(-4,3));
	}

	@Test
	public void testProcessDoubleNegative1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(-3.4), modifier.process(-2.3, -3.4));
	}

	@Test
	public void testProcessDoubleNegative2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(-2.4), modifier.process(-4.3, -2.4));
	}

	@Test
	public void testProcessDoublePositive1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(3.5), modifier.process(2.6, 3.5));
	}

	@Test
	public void testProcessDoublePositive2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(3.1), modifier.process(4.4, 3.1));
	}

	@Test
	public void testProcessDoubleZero1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(3.1), modifier.process(0.0, 3.1));
	}

	@Test
	public void testProcessDoubleZero2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(4.2, 0.0));
	}

	@Test
	public void testProcessDoubleZero3()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(-3.4), modifier.process(0.0, -3.4));
	}

	@Test
	public void testProcessDoubleZero4()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(0.0), modifier.process(-4.3,0.0));
	}

	@Test
	public void testProcessDoubleMixed1()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(-7.2), modifier.process(5.3,-7.2));
	}

	@Test
	public void testProcessDoubleMixed2()
	{
		SetModifierFactory modifier = new SetModifierFactory();
		assertEquals(Double.valueOf(3.1), modifier.process(-4.2,3.1));
	}

	@Test
	public void testGetModifier()
	{
		SetModifierFactory factory = new SetModifierFactory();
		Modifier<Number> modifier =
				factory.getModifier(35, "6.5", null, varScope, numManager);
		assertEquals(factory.getInherentPriority(), modifier.getInherentPriority());
		assertEquals(35, modifier.getUserPriority());
		assertEquals(Number.class, modifier.getVariableFormat());
		assertEquals(6.5, modifier.process(4.3, null));
	}

	@Test
	public void testGetFormulaModifier()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		setup.getLegalScopeLibrary().registerScope(varScope);
		IndividualSetup iSetup = setup.getIndividualSetup("Global");
		SetModifierFactory factory = new SetModifierFactory();
		Modifier<Number> modifier =
				factory.getModifier(35, "6+5", iSetup.getFormulaManager(), varScope, numManager);
		assertEquals(factory.getInherentPriority(), modifier.getInherentPriority());
		assertEquals(35, modifier.getUserPriority());
		assertEquals(Number.class, modifier.getVariableFormat());
		assertEquals(11, modifier.process(4.3, iSetup.getScopeInfo()));
	}
}
