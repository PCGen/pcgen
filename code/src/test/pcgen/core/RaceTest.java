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
import pcgen.core.analysis.RaceStat;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;

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
		assertEquals("Template has not been unlocked", false, RaceStat.isUnlocked(str, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has not been unlocked", false, RaceStat.isUnlocked(str, race));
		race.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", true, RaceStat.isUnlocked(str, race));
	}
	
	/**
	 * Test the isNonAbility method of Race.
	 */
	public void testIsNonAbility()
	{
		Race race = new Race();
		race.setName("Test Race");
		assertEquals("Template has not been locked to a nonability", false, RaceStat.isNonAbility(str, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has been locked to an ability", false, RaceStat.isNonAbility(str, race));
		race.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(10)));
		assertEquals("Template has been locked to a nonability", true, RaceStat.isNonAbility(str, race));
		race.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", false, RaceStat.isNonAbility(str, race));
	}
}
