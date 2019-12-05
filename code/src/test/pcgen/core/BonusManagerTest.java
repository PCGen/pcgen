/*
 * Copyright James Dempsey, 2013
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


import static org.junit.Assert.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.Test;

/**
 * Test class for BonusManager.
 */
public class BonusManagerTest extends AbstractCharacterTestCase
{

    /**
     * Validate that the setActiveBonusStack method will correctly calculate the value of
     * a positive bonus followed by a negative bonus to a non-stackable value.
     */
    @Test
    public void testStackingPositiveFirst()
    {
        PCTemplate testObj = TestHelper.makeTemplate("PostiveFirst");
        LoadContext context = Globals.getContext();
        final BonusObj posBonus = Bonus.newBonus(context, "COMBAT|AC|5|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, posBonus);
        final BonusObj negBonus = Bonus.newBonus(context, "COMBAT|AC|-2|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, negBonus);

        PlayerCharacter pc = getCharacter();
        pc.addTemplate(testObj);
        // Run the check a few times to ensure no randomness issues
        for (int i = 0;i < 10;i++)
        {
            pc.calcActiveBonuses();
            assertEquals("Incorrect bonus total", 3.0, pc.getTotalBonusTo("COMBAT", "AC"), 0.0001);
        }
    }

    /**
     * Validate that the setActiveBonusStack method will correctly calculate the value of
     * a negative bonus followed by a positive bonus to a non-stackable value.
     */
    @Test
    public void testStackingNegativeFirst()
    {
        PCTemplate testObj = TestHelper.makeTemplate("PostiveFirst");
        LoadContext context = Globals.getContext();
        final BonusObj negBonus = Bonus.newBonus(context, "COMBAT|AC|-2|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, negBonus);
        final BonusObj posBonus = Bonus.newBonus(context, "COMBAT|AC|5|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, posBonus);

        PlayerCharacter pc = getCharacter();
        pc.addTemplate(testObj);

        // Run the check a few times to ensure no randomness issues
        for (int i = 0;i < 10;i++)
        {
            pc.calcActiveBonuses();
            assertEquals("Incorrect bonus total", 3.0, pc.getTotalBonusTo("COMBAT", "AC"), 0.0001);
        }
    }

    /**
     * Validate that the setActiveBonusStack method will correctly calculate the value of
     * a positive bonus, then a negative bonus followed by a positive bonus to a
     * non-stackable value.
     */
    @Test
    public void testStackingPosNegPos()
    {
        PCTemplate testObj = TestHelper.makeTemplate("PosNegPos");
        LoadContext context = Globals.getContext();
        final BonusObj posBonus = Bonus.newBonus(context, "COMBAT|AC|5|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, posBonus);
        final BonusObj negBonus = Bonus.newBonus(context, "COMBAT|AC|-2|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, negBonus);
        final BonusObj posBonus2 = Bonus.newBonus(context, "COMBAT|AC|4|TYPE=Armor");
        testObj.addToListFor(ListKey.BONUS, posBonus2);

        PlayerCharacter pc = getCharacter();
        pc.addTemplate(testObj);

        // Run the check a few times to ensure no randomness issues
        for (int i = 0;i < 10;i++)
        {
            pc.calcActiveBonuses();
            assertEquals("Incorrect bonus total", 3.0, pc.getTotalBonusTo("COMBAT", "AC"), 0.0001);
        }
    }

}
