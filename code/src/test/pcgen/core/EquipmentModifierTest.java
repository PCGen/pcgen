/*
 * EquipmentModifierTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 09-Jan-2004
 *
 * Current Ver: $Revision$
 *
 *
 *
 */
package pcgen.core;

import java.util.List;

import junit.framework.TestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Equipment Modifer Test 
 */
@SuppressWarnings("nls")
public class EquipmentModifierTest extends TestCase
{

	/**
	 * Main
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(EquipmentModifierTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(EquipmentModifierTest.class);
	}

	/**
	 * Constructs a new <code>EquipmentModifierTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentModifierTest()
	{
		// Do Nothing
	}

	/**
	 * Starts the system plugins.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TestHelper.loadPlugins();
	}

	/**
	 * Constructs a new <code>EquipmentModifierTest</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentModifierTest(final String name)
	{
		super(name);
	}

	/**
	 * Test +13
	 */
	public void test885958A()
	{
		LoadContext context = Globals.getContext();

		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus =
				Bonus.newBonus(context, "WEAPON|DAMAGE|((%CHOICE)MIN(STR))");
		eqMod.addToListFor(ListKey.BONUS, aBonus);

		final Equipment e = new Equipment();
		e.addAssociation(eqMod, "+13");
		
		for (BonusObj bonusObj : eqMod.getBonusList(e))
		{
			assertEquals("((+13)MIN(STR))", bonusObj.getValue());
		}
		assertEquals("((%CHOICE)MIN(STR))", aBonus.getValue());
	}

	/**
	 * Test -2 and +13
	 */
	public void test885958B()
	{
		LoadContext context = Globals.getContext();

		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus =
				Bonus.newBonus(context, "WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");

		final Equipment e = new Equipment();
		
		e.addAssociation(eqMod, "+13");
		eqMod.addToListFor(ListKey.BONUS, aBonus);

		for (BonusObj bonusObj : eqMod.getBonusList(e))
		{
			assertEquals("-2", bonusObj.getValue());

			final Prerequisite prereq = bonusObj.getPrerequisiteList().get(0);
			assertEquals("+13", prereq.getKey());
			assertEquals("STR", prereq.getOperand());
		}
		assertEquals("-2", aBonus.getValue());
		final Prerequisite prereq = aBonus.getPrerequisiteList().get(0);
		assertEquals("%CHOICE", prereq.getKey());
	}

	/**
	 * Test the expansion of the %CHOICE in a prereq for a bonus. Note as the
	 * options for the choice are processed in reverse order, we have to check the
	 * values in reverse order.
	 */
	public void testChoice()
	{
		LoadContext context = Globals.getContext();

		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus =
				Bonus.newBonus(context, "WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");

		final Equipment e = new Equipment();

		e.addAssociation(eqMod, "+1");
		e.addAssociation(eqMod, "+2");
		eqMod.addToListFor(ListKey.BONUS, aBonus);

		final List<BonusObj> list = eqMod.getBonusList(e);
		for (int j = list.size() - 1; j > 0; j--)
		{
			final BonusObj bonusObj = list.get(j);
			assertEquals("-2", bonusObj.getValue());

			final Prerequisite prereq = bonusObj.getPrerequisiteList().get(0);
			assertEquals("+" + (j+1), prereq.getKey());
			assertEquals("STR", prereq.getOperand());
		}
		assertEquals("-2", aBonus.getValue());
		final Prerequisite prereq = aBonus.getPrerequisiteList().get(0);
		assertEquals("%CHOICE", prereq.getKey());
	}
}
