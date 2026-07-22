/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.choiceset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

import org.junit.jupiter.api.Test;

/**
 * Regression test for the SpotBugs EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC finding
 * that previously affected {@link SpellCasterChoiceSet}. Before the fix the
 * subclass overrode {@code equals} to require {@code SpellCasterChoiceSet} on
 * the other side, which broke symmetry with {@link ChoiceSet#equals(Object)}.
 * The fix removes that override and inherits the parent equality.
 */
class SpellCasterChoiceSetEqualsTest
{

	/**
	 * Minimal stub PrimitiveChoiceSet identified by name; equals compares names.
	 * Suitable for use as both the parent ChoiceSet's pcs and the subclass's
	 * typePCS so that both ChoiceSets share the same {@code pcs} reference.
	 */
	private static PrimitiveChoiceSet<PCClass> stubPcs(String name)
	{
		return new PrimitiveChoiceSet<>()
		{
			@Override
			public Class<PCClass> getChoiceClass()
			{
				return PCClass.class;
			}

			@Override
			public String getLSTformat(boolean useAny)
			{
				return name;
			}

			@Override
			public Set<PCClass> getSet(PlayerCharacter pc)
			{
				return Collections.emptySet();
			}

			@Override
			public GroupingState getGroupingState()
			{
				return GroupingState.ANY;
			}

			@Override
			public boolean equals(Object o)
			{
				return (o instanceof PrimitiveChoiceSet<?>) && name.equals(((PrimitiveChoiceSet<?>) o).getLSTformat(true));
			}

			@Override
			public int hashCode()
			{
				return name.hashCode();
			}
		};
	}

	@Test
	void parentAndSpellCasterAreSymmetricWhenSharingNameAndPcs()
	{
		PrimitiveChoiceSet<PCClass> sharedPcs = stubPcs("CASTERS");

		// Parent ChoiceSet, name + pcs only — the identity ChoiceSet.equals checks.
		ChoiceSet<PCClass> plainSet = new ChoiceSet<>("SPELLCASTER", sharedPcs);

		// SpellCasterChoiceSet that passes the same pcs to super(...) so the
		// parent-visible identity matches.
		SpellCasterChoiceSet casterSet =
			new SpellCasterChoiceSet(null, List.of(), sharedPcs, null);

		// Symmetry of equals: both directions must agree (used to fail).
		assertEquals(plainSet.equals(casterSet), casterSet.equals(plainSet),
			"ChoiceSet.equals(SpellCasterChoiceSet) must equal SpellCasterChoiceSet.equals(ChoiceSet)");

		// And the parent-defined identity says they are equal.
		assertTrue(plainSet.equals(casterSet), "parent considers them equal (same setName + pcs)");
		assertTrue(casterSet.equals(plainSet), "subclass must agree (symmetry)");

		// Objects.equals goes through the receiver-side equals; verify both routes.
		assertEquals(Objects.equals(plainSet, casterSet), Objects.equals(casterSet, plainSet));

		// hashCode contract: equal objects must hash the same.
		assertEquals(plainSet.hashCode(), casterSet.hashCode(),
			"equal ChoiceSet/SpellCasterChoiceSet must hash equally");
	}
}
