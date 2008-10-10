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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability.Nature;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;

/**
 * Equipment Modifer Test 
 */
@SuppressWarnings("nls")
public class EquipmentModifierTest extends PCGenTestCase
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
	 * @see pcgen.PCGenTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		try
		{
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		}
		catch (Exception e)
		{
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
		final BonusObj aBonus =
				Bonus.newBonus("WEAPON|DAMAGE|((%CHOICE)MIN(STR))");
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
		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus =
				Bonus.newBonus("WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");

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
		final EquipmentModifier eqMod = new EquipmentModifier();
		final BonusObj aBonus =
				Bonus.newBonus("WEAPON|TOHIT|-2|PREVARGT:%CHOICE,STR");

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
	
	/**
	 * Test the processing of an ability tag associated with an EqMod.
	 */
	public void testAbility()
	{
		final EquipmentModifier eqMod = new EquipmentModifier();
		final Ability ability = new Ability();
		ability.setCategory(AbilityCategory.FEAT.getAbilityCategory());
		ability.setName("EqModTest");
		eqMod.addAbility(AbilityCategory.FEAT, Nature.VIRTUAL,
			new QualifiedObject<String>(ability.getKeyName(),
				new ArrayList<Prerequisite>()));
		List<String> keys =
				eqMod
					.getAbilityKeys(null, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertEquals("Added ability should be only one in the list",
			"EqModTest", keys.get(0));
		assertEquals("Added ability should be only one in the list", 1, keys
			.size());
	}
}
