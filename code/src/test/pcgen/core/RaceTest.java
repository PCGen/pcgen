/*
 * RaceTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 8/12/2007
 *
 * $Id$
 */
package pcgen.core;

import pcgen.AbstractCharacterTestCase;

/**
 * <code>RaceTest</code> tests the function of the Race class.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class RaceTest extends AbstractCharacterTestCase
{
	
	/**
	 * Test the isUnlocked method of Race.
	 */
	public void testIsUnlocked()
	{
		Race race = new Race();
		race.setName("Test Race");
		int index = getCharacter().getStatList().getIndexOfStatFor("STR");
		assertEquals("Template has not been unlocked", false, race.isUnlocked(index));
		race.addVariable(-9, "LOCK.STR", "12");
		assertEquals("Template has not been unlocked", false, race.isUnlocked(index));
		race.addVariable(-9, "UNLOCK.STR", "");
		assertEquals("Template has been unlocked", true, race.isUnlocked(index));
	}
	
	/**
	 * Test the isNonAbility method of Race.
	 */
	public void testIsNonAbility()
	{
		Race race = new Race();
		race.setName("Test Race");
		int index = getCharacter().getStatList().getIndexOfStatFor("STR");
		assertEquals("Template has not been locked to a nonability", false, race.isNonAbility(index));
		race.addVariable(-9, "LOCK.STR", "12");
		assertEquals("Template has been locked to an ability", false, race.isNonAbility(index));
		race.addVariable(-9, "LOCK.STR", "10");
		assertEquals("Template has been locked to a nonability", true, race.isNonAbility(index));
		race.addVariable(-9, "UNLOCK.STR", "");
		assertEquals("Template has been unlocked", false, race.isNonAbility(index));
	}
}
