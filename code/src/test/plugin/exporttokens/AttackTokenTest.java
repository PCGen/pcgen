/*
 * AttackTokenTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package plugin.exporttokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.rules.context.LoadContext;

/**
 * <code>ACTokenTest</code> tests the function of the AC token and 
 * thus the calculations of armor class.  
 *
 *
 */
public class AttackTokenTest extends AbstractCharacterTestCase
{
	PCClass myClass = new PCClass();

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(AttackTokenTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();
		setPCStat(character, str, 14);
		str.removeListFor(ListKey.BONUS);
		LoadContext context = Globals.getContext();
		final BonusObj aBonus = Bonus.newBonus(context, "COMBAT|TOHIT.Melee|STR|TYPE=Ability");
		
		if (aBonus != null)
		{
			str.addToListFor(ListKey.BONUS, aBonus);
		}
//		// Ignoring max dex
//		stat.addBonusList("COMBAT|AC|DEX|TYPE=Ability");

		EquipSet def = new EquipSet("0.1", "Default");
		character.addEquipSet(def);
		character.setCalcEquipmentList();

		character.calcActiveBonuses();

		myClass.setName("My Class");
		myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
		final BonusObj babClassBonus = Bonus.newBonus(context, "COMBAT|BASEAB|CL+5");
		myClass.getOriginalClassLevel(1).addToListFor(ListKey.BONUS, babClassBonus);
		Globals.getContext().getReferenceContext().importObject(myClass);

	}

	/*
	 * @see TestCase#tearDown()
	 */
    @Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test the character's attack calcs with no bonus.
	 * @throws Exception
	 */
	public void testBase() throws Exception
	{
		assertEquals("Total melee attack no bonus", "+2", new AttackToken()
			.getToken("ATTACK.MELEE.TOTAL", getCharacter(), null));

		assertEquals("Total melee attack no bonus short", "+2",
			new AttackToken().getToken("ATTACK.MELEE.TOTAL.SHORT",
				getCharacter(), null));
	}

	/**
	 * Test the character's attack calcs with a bonus.
	 * @throws Exception
	 */
	public void testIterative() throws Exception
	{
		getCharacter().incrementClassLevel(1, myClass, true);
		getCharacter().calcActiveBonuses();

		assertEquals("Total melee attack no bonus", "+8/+3", new AttackToken()
			.getToken("ATTACK.MELEE.TOTAL", getCharacter(), null));

		assertEquals("Total melee attack no bonus short", "+8",
			new AttackToken().getToken("ATTACK.MELEE.TOTAL.SHORT",
				getCharacter(), null));
	}

}
