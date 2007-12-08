/*
 * StatListTest.java
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
 *
 * Created on 08/12/2007
 *
 * $Id$
 */

package pcgen.core;

import org.junit.Test;

import pcgen.AbstractCharacterTestCase;
import pcgen.util.TestHelper;

/**
 * <code>StatListTest</code> checks the function of the 
 * StatList class. 
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class StatListTest extends AbstractCharacterTestCase
{
	PCTemplate locker;
	PCTemplate unlocker;
	Ability bonus;

	/* (non-Javadoc)
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		locker = new PCTemplate();
		locker.setName("locker");
		locker.addVariable(-9, "LOCK.STR", "12");
		unlocker = new PCTemplate();
		unlocker.setName("unlocker");
		unlocker.addVariable(-9, "UNLOCK.STR", "");
		bonus = TestHelper.makeAbility("Bonus", "FEAT", "General.Fighter");
		bonus.addBonusList("STAT|STR|7|TYPE=Enhancement");

		PlayerCharacter pc = getCharacter();
		setPCStat(pc, "STR", 6);
	}

	/**
	 * Test method for {@link pcgen.core.StatList#getBaseStatFor(java.lang.String)}.
	 */
	@Test
	public void testGetBaseStatFor()
	{
		PlayerCharacter pc = getCharacter();
		assertEquals("Starting STR should be 6", 6, pc.getStatList().getBaseStatFor("STR"));

		// Bonus should not affect base stat
		pc.addAbility(AbilityCategory.FEAT, bonus, null);
		pc.calcActiveBonuses();
		assertEquals("Stat should still be locked", 6, pc.getStatList().getBaseStatFor("STR"));
		
		pc.addTemplate(locker);
		assertEquals("Stat should now be locked", 12, pc.getStatList().getBaseStatFor("STR"));

		pc.addTemplate(unlocker);
		assertEquals("Stat should now be unlocked", 6, pc.getStatList().getBaseStatFor("STR"));
	}

	/**
	 * Test method for {@link pcgen.core.StatList#getTotalStatFor(java.lang.String)}.
	 */
	@Test
	public void testGetTotalStatFor()
	{
		PlayerCharacter pc = getCharacter();
		assertEquals("Starting STR should be 6", 6, pc.getStatList().getTotalStatFor("STR"));

		// Bonus should affect total stat
		pc.addAbility(AbilityCategory.FEAT, bonus, null);
		pc.calcActiveBonuses();
		assertEquals("Stat should have bonus", 13, pc.getStatList().getTotalStatFor("STR"));
		
		pc.addTemplate(locker);
		assertEquals("Stat should now be locked", 12, pc.getStatList().getTotalStatFor("STR"));

		pc.addTemplate(unlocker);
		assertEquals("Stat should now be unlocked", 13, pc.getStatList().getTotalStatFor("STR"));
	}

}
