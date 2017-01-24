/*
 * PreEquipTwoWeaponTest.java
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
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;

/**
 * <code>PreEquipTwoWeaponTest</code> tests that the PREEQUIPTWOWEAPON tag is
 * working correctly.
 *
 *
 */
public class PreEquipTwoWeaponTest extends AbstractCharacterTestCase
{

	/*
	 * Class under test for int passes(Prerequisite, PlayerCharacter)
	 */
	public void testPassesPrerequisitePlayerCharacter()
	{
		final PlayerCharacter character = getCharacter();

		final Equipment longsword = new Equipment();
		longsword.setName("Longsword");

		character.addEquipment(longsword);
		longsword.setIsEquipped(true, character);
		longsword.setLocation(EquipmentLocation.EQUIPPED_TWO_HANDS);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		final Prerequisite prereq = new Prerequisite();
		prereq.setKind("equiptwoweapon");
		prereq.setKey("LONGSWORD");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		final boolean passes = PrereqHandler.passes(prereq, character, null);
		assertTrue(passes);

		longsword.setName("Longsword (Large/Masterwork)");

		assertFalse("Should be an exact match only", PrereqHandler.passes(
			prereq, character, null));

		prereq.setKey("LONGSWORD (LARGE%");

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
		longsword.setLocation(EquipmentLocation.EQUIPPED_TWO_HANDS);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("equiptwoweapon");
		prereq.setKey("TYPE=Weapon");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		assertFalse("Equipment has no type", PrereqHandler.passes(prereq,
			character, null));

		longsword.addType(Type.WEAPON);

		assertTrue("Equipment is weapon", PrereqHandler.passes(prereq,
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
		CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		race.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));

		character.setRace(race);
		LoadContext context = Globals.getContext();

		final Equipment longsword = new Equipment();
		longsword.setName("Longsword");

		character.addEquipment(longsword);
		longsword.setIsEquipped(true, character);
		longsword.setLocation(EquipmentLocation.EQUIPPED_TWO_HANDS);
		character.doAfavorForAunitTestThatIgnoresEquippingRules();

		Prerequisite prereq = new Prerequisite();
		prereq.setKind("equiptwoweapon");
		prereq.setKey("WIELDCATEGORY=OneHanded");
		prereq.setOperand("1");
		prereq.setOperator(PrerequisiteOperator.EQ);

		// Test 3.0 Style
		longsword.put(ObjectKey.SIZE, mediumRef);
		longsword.put(ObjectKey.BASESIZE, mediumRef);

		assertTrue("Weapon is M therefore OneHanded", PrereqHandler.passes(
			prereq, character, null));

		longsword.put(ObjectKey.SIZE, largeRef);
		longsword.put(ObjectKey.BASESIZE, largeRef);

		assertFalse("Weapon is L therefore TwoHanded", PrereqHandler.passes(
			prereq, character, null));

		// Test 3.5 style
		longsword.put(ObjectKey.SIZE, mediumRef);
		longsword.put(ObjectKey.BASESIZE, mediumRef);
		longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "TwoHanded"));

		assertFalse("Weapon is TwoHanded", PrereqHandler.passes(prereq,
			character, null));

		longsword.put(ObjectKey.WIELD, context.getReferenceContext().silentlyGetConstructedCDOMObject(
				WieldCategory.class, "OneHanded"));

		assertTrue("Weapon is OneHanded", PrereqHandler.passes(prereq,
			character, null));

	}
}
