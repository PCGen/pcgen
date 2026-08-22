/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
package pcgen.cdom.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests group ("TYPE=") resolution in {@link AbstractReferenceManufacturer}
 * (the private resolveGroupReferences path, reached via resolveReferences).
 *
 * These pin the input -> group-membership behaviour that the isType(Type)
 * fast-path optimization must preserve: an object joins a TYPE group iff it is
 * of ALL tokens in that group's key.
 */
class AbstractReferenceManufacturerGroupResolutionTest
{

	private SimpleReferenceManufacturer<Skill> manufacturer;

	@BeforeEach
	void setUp()
	{
		manufacturer = new SimpleReferenceManufacturer<>(
				new CDOMFactory<>(BasicClassIdentity.getIdentity(Skill.class)));
	}

	/**
	 * Constructs a Skill in the manufacturer with the given types.
	 */
	private Skill makeSkill(String key, String... types)
	{
		Skill skill = manufacturer.constructObject(key);
		for (String type : types)
		{
			skill.addToListFor(ListKey.TYPE, Type.getConstant(type));
		}
		return skill;
	}

	@Test
	void singleTokenGroupContainsOnlyMatchingObjects()
	{
		Skill weapon = makeSkill("Weapon", "Weapon");
		Skill armor = makeSkill("Armor", "Armor");

		CDOMGroupRef<Skill> weaponGroup = manufacturer.getTypeReference("Weapon");

		assertTrue(manufacturer.resolveReferences(null));

		assertTrue(weaponGroup.contains(weapon));
		assertFalse(weaponGroup.contains(armor));
		assertEquals(Set.of(weapon), Set.copyOf(weaponGroup.getContainedObjects()));
	}

	@Test
	void multiTokenGroupRequiresAllTokens()
	{
		Skill both = makeSkill("Both", "Weapon", "Melee");
		Skill weaponOnly = makeSkill("WeaponOnly", "Weapon");
		Skill meleeOnly = makeSkill("MeleeOnly", "Melee");

		// TYPE=Weapon.Melee -> only objects that are BOTH Weapon and Melee
		CDOMGroupRef<Skill> group = manufacturer.getTypeReference("Weapon", "Melee");

		assertTrue(manufacturer.resolveReferences(null));

		assertTrue(group.contains(both));
		assertFalse(group.contains(weaponOnly));
		assertFalse(group.contains(meleeOnly));
		assertEquals(Set.of(both), Set.copyOf(group.getContainedObjects()));
	}

	@Test
	void matchingIsCaseInsensitive()
	{
		// Object declared with one casing, group requested with another.
		Skill skill = makeSkill("Mixed", "Weapon");

		CDOMGroupRef<Skill> group = manufacturer.getTypeReference("wEaPoN");

		assertTrue(manufacturer.resolveReferences(null));

		assertTrue(group.contains(skill),
				"TYPE matching must be case-insensitive (Type is interned case-insensitively)");
	}

	@Test
	void groupWithNoMatchesIsFlaggedUnconstructed()
	{
		makeSkill("Weapon", "Weapon");

		CDOMGroupRef<Skill> group = manufacturer.getTypeReference("Nonexistent");

		// A TYPE= reference that matches nothing is a dangling reference:
		// resolution reports failure and the group gets no objects.
		assertFalse(manufacturer.resolveReferences(null),
				"A TYPE group matching zero objects must be reported as unconstructed");
		assertEquals(0, group.getObjectCount());
	}

	@Test
	void objectQualifiesForEveryGroupItMatches()
	{
		Skill both = makeSkill("Both", "Weapon", "Melee");

		CDOMGroupRef<Skill> weaponGroup = manufacturer.getTypeReference("Weapon");
		CDOMGroupRef<Skill> meleeGroup = manufacturer.getTypeReference("Melee");
		CDOMGroupRef<Skill> comboGroup = manufacturer.getTypeReference("Weapon", "Melee");

		assertTrue(manufacturer.resolveReferences(null));

		assertTrue(weaponGroup.contains(both));
		assertTrue(meleeGroup.contains(both));
		assertTrue(comboGroup.contains(both));
	}
}
