/*
 * Copyright 2007 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code StatListTest} checks the function of the
 * StatList class. 
 */
public class StatListTest extends AbstractCharacterTestCase
{
	PCTemplate locker;
	PCTemplate unlocker;
	Ability bonus;
	Ability lockedBonus;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter pc = getCharacter();
		LoadContext context = Globals.getContext();

		locker = new PCTemplate();
		locker.setName("locker");
		CDOMDirectSingleRef<PCStat> strRef = CDOMDirectSingleRef.getRef(str);
		locker.addToListFor(ListKey.STAT_LOCKS, new StatLock(strRef, FormulaFactory.getFormulaFor(12)));
		unlocker = new PCTemplate();
		unlocker.setName("unlocker");
		unlocker.addToListFor(ListKey.UNLOCKED_STATS, strRef);
		bonus = TestHelper.makeAbility("Bonus", BuildUtilities.getFeatCat(), "General.Fighter");
		BonusObj aBonus = Bonus.newBonus(context, "STAT|STR|7|TYPE=Enhancement");
		if (aBonus != null)
		{
			bonus.addToListFor(ListKey.BONUS, aBonus);
		}
		lockedBonus = TestHelper.makeAbility("LockedBonus", BuildUtilities.getFeatCat(), "General.Fighter");
		aBonus = Bonus.newBonus(context, "LOCKEDSTAT|STR|3|TYPE=Morale");
		if (aBonus != null)
		{
			lockedBonus.addToListFor(ListKey.BONUS, aBonus);
		}

		setPCStat(pc, str, 6);
	}

	/**
	 * Test method for {@link pcgen.core.PlayerCharacter#getBaseStatFor(PCStat)}.
	 */
	@Test
	public void testGetBaseStatFor()
	{
		PlayerCharacter pc = getCharacter();
		assertEquals(6, pc.getBaseStatFor(str), "Starting STR should be 6");

		// Bonus should not affect base stat
		addAbility(BuildUtilities.getFeatCat(), bonus);
		pc.calcActiveBonuses();
		assertEquals(6, pc.getBaseStatFor(str), "Stat should still be base");
		
		pc.addTemplate(locker);
		assertEquals(12, pc.getBaseStatFor(str), "Stat should now be locked");

		addAbility(BuildUtilities.getFeatCat(), lockedBonus);
		pc.calcActiveBonuses();
		assertEquals(12, pc.getBaseStatFor(str), "Stat should still be locked");
		
		pc.addTemplate(unlocker);
		assertEquals(6, pc.getBaseStatFor(str), "Stat should now be unlocked");
	}

	/**
	 * Test method for {@link pcgen.core.PlayerCharacter#getTotalStatFor(PCStat)}.
	 */
	@Test
	public void testGetTotalStatFor()
	{
		PlayerCharacter pc = getCharacter();
		assertEquals(6, pc.getTotalStatFor(str), "Starting STR should be 6");

		// Bonus should affect total stat
		addAbility(BuildUtilities.getFeatCat(), bonus);
		pc.calcActiveBonuses();
		assertEquals(13, pc.getTotalStatFor(str), "Stat should have bonus");
		
		pc.addTemplate(locker);
		assertEquals(12, pc.getTotalStatFor(str), "Stat should now be locked");

		addAbility(BuildUtilities.getFeatCat(), lockedBonus);
		pc.calcActiveBonuses();
		assertEquals(15, pc.getTotalStatFor(str), "Stat should be locked but bonused");

		pc.addTemplate(unlocker);
		assertEquals(13, pc.getTotalStatFor(str), "Stat should now be unlocked");
	}
	
	/**
	 * Test out the output of stats where a min value is in place. 
	 */
	@Test
	public void testMinValueStat()
	{
		PlayerCharacter pc = getCharacter();
		assertEquals(6, pc.getTotalStatFor(str), "Starting STR should be 6");
		assertEquals(-2, pc.getStatModFor(str), "Starting STR mod");

		// With template lock
		PCTemplate statMinValTemplate = new PCTemplate();
		statMinValTemplate.setName("minval");
		statMinValTemplate.addToListFor(ListKey.STAT_MINVALUE, new StatLock(
			CDOMDirectSingleRef.getRef(str), FormulaFactory.getFormulaFor(8)));
		pc.addTemplate(statMinValTemplate);
		assertEquals(8, pc.getTotalStatFor(str), "STR now has minimum value");
		assertEquals(-1, pc.getStatModFor(str), "Starting STR mod");
		pc.removeTemplate(statMinValTemplate);
		assertEquals(6, pc.getTotalStatFor(str), "STR no longer has minimum value");
		assertEquals(-2, pc.getStatModFor(str), "Starting STR mod");
	}

}
