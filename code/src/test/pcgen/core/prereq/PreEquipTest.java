/*
 * PreEquipTest.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 22-Nov-2004
 */
package pcgen.core.prereq;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 */
public class PreEquipTest extends AbstractCharacterTestCase {

    /*
     * Class under test for int passes(Prerequisite, PlayerCharacter)
     */
    public void testPassesPrerequisitePlayerCharacter() {
		final PlayerCharacter character = getCharacter();

		final Equipment longsword = new Equipment();
        longsword.setName("Longsword");

        character.addEquipment(longsword);
        longsword.setIsEquipped(true, character);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("equip");
        prereq.setKey("LONGSWORD%");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.EQ);


		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

    }

}
