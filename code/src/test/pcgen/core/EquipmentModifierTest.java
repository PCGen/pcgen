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
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import gmgen.pluginmgr.PluginLoader;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.PCGenTestCase;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;

import java.util.Iterator;
import java.util.List;

/**
 * Equipment Modifer Test 
 */
public class EquipmentModifierTest extends PCGenTestCase {
	
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

	public void setUp() throws Exception {
		try {
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		}
		catch(Exception e) {
			// TODO Deal with exception
		}
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
		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus = Bonus.newBonus("WEAPON|DAMAGE|((%CHOICE)MIN(STR))");
		eqMod.addBonusList(aBonus);
		eqMod.addAssociated("+13");

		final List list = eqMod.getBonusList();
		for (Iterator iter = list.iterator(); iter.hasNext();)
		{
			final BonusObj bonusObj = (BonusObj) iter.next();
			assertEquals("((+13)MIN(STR))", bonusObj.getValue());
		}
		assertEquals("((%CHOICE)MIN(STR))", aBonus.getValue());
	}

	/**
	 * Test -2 and +13
	 */
	public void test885958B()
	{
		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus = Bonus.newBonus(
			"WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");
		eqMod.addAssociated("+13");
		eqMod.addBonusList(aBonus);

		final List list = eqMod.getBonusList();
		for (Iterator iter = list.iterator(); iter.hasNext();)
		{
			final BonusObj bonusObj = (BonusObj) iter.next();
			assertEquals("-2", bonusObj.getValue());

			final Prerequisite prereq = bonusObj.getPrereqList().get(0);
			assertEquals("+13", prereq.getKey());
			assertEquals("STR", prereq.getOperand());
		}
		assertEquals("-2", aBonus.getValue());
		final Prerequisite prereq = aBonus.getPrereqList().get(0);
		assertEquals("%CHOICE", prereq.getKey());
	}

	/**
	 * Test the expansion of the %CHOICE in a prereq for a bonus. Note as the
	 * options for the choice are processed in reverse order, we have to check the
	 * values in reverse order.
	 */
	public void testChoice()
	{
		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus = Bonus.newBonus(
			"WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");
		eqMod.addAssociated("+1");
		eqMod.addAssociated("+2");
		eqMod.addBonusList(aBonus);

		final List list = eqMod.getBonusList();
		int i = 1;
		for (int j = list.size() - 1; j > 0; j--)
		{
			final BonusObj bonusObj = (BonusObj) list.get(j);
			assertEquals("-2", bonusObj.getValue());

			final Prerequisite prereq = bonusObj.getPrereqList().get(0);
			assertEquals("+" + i, prereq.getKey());
			assertEquals("STR", prereq.getOperand());
			i++;
		}
		assertEquals("-2", aBonus.getValue());
		final Prerequisite prereq = aBonus.getPrereqList().get(0);
		assertEquals("%CHOICE", prereq.getKey());
	}
}
