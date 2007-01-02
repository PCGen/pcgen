/*
 * PreEquipPrimaryTest.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.core.prereq;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * <code>PreEquipPrimaryTest</code> tests that the PREEQUIPPRIMARY tag is
 * working correctly.
 *
 * Last Editor: $Author: $
 * Last Edited: $Date$
 *
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 */
public class PreEquipPrimaryTest extends AbstractCharacterTestCase
{

	/*
	 * Class under test for int passes(Prerequisite, PlayerCharacter)
	 */
	public void testPassesPrerequisitePlayerCharacter()
	{
		final PlayerCharacter character = getCharacter();

		final Equipment dagger = new Equipment();
		dagger.setName("Dagger");

		character.addEquipment(dagger);
		dagger.setIsEquipped(true, character);
		dagger.setLocation(Equipment.EQUIPPED_PRIMARY);

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("equipprimary");
		prereq.setKey("DAGGER");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		dagger.setName("Dagger (Masterwork)");

		assertFalse("Should be an exact match only", PrereqHandler.passes(
			prereq, character, null));

		prereq.setKey("DAGGER%");

		assertTrue("Should allow wildcard match", PrereqHandler.passes(prereq,
			character, null));
	}

	/**
	 * Test equipment type tests
	 * @throws Exception
	 */
	public void testType() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Equipment longsword = new Equipment();
		longsword.setName("Longsword");

		character.addEquipment(longsword);
		longsword.setIsEquipped(true, character);
		longsword.setLocation(Equipment.EQUIPPED_PRIMARY);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("equipprimary");
		prereq.setKey("TYPE=Slashing");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Equipment has no type", PrereqHandler.passes(prereq,
			character, null));

		longsword.typeList().add("SLASHING");

		assertTrue("Equipment is slashing", PrereqHandler.passes(prereq,
			character, null));

		prereq.setKey("TYPE.Armor");

		assertFalse("Equipment is not armor", PrereqHandler.passes(prereq,
			character, null));
	}

	/**
	 * Test wield category tests
	 * @throws Exception
	 */
	public void testWield() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		final Race race = new Race();
		race.setName("Test Race");
		race.setSize("M");

		character.setRace(race);

		final Equipment longsword = new Equipment();
		longsword.setName("Dagger");

		character.addEquipment(longsword);
		longsword.setIsEquipped(true, character);
		longsword.setLocation(Equipment.EQUIPPED_PRIMARY);

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("equipprimary");
		prereq.setKey("WIELDCATEGORY=OneHanded");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		// Test 3.0 Style
		longsword.setSize("M", true);

		assertTrue("Weapon is M therefore OneHanded", PrereqHandler.passes(
			prereq, character, null));

		longsword.setSize("L", true);

		assertFalse("Weapon is L therefore TwoHanded", PrereqHandler.passes(
			prereq, character, null));

		// Test 3.5 style
		longsword.setWield("TwoHanded");

		assertFalse("Weapon is TwoHanded", PrereqHandler.passes(prereq,
			character, null));

		longsword.setWield("OneHanded");

		assertTrue("Weapon is OneHanded", PrereqHandler.passes(prereq,
			character, null));

	}
}
