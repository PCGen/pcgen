/**
 * Copyright 2007 (C) Andrew Wilson <nuance@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * $Author$
 * $Date$
 * $Revision$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.Type;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;


public class AbilityUtilitiesTest extends AbstractCharacterTestCase
{

	/**
	 * Run the test
	 * @param args don't need args apparently
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(AbilityUtilitiesTest.class);
	}

	/**
	 * Test method for 'pcgen.core.AbilityUtilities.removeChoicesFromName(String)'
	 */
	public void testRemoveChoicesFromName()
	{
		is(AbilityUtilities.removeChoicesFromName("Bare Thing (Mad cow)"),
			strEq("Bare Thing"), "Choice is removed from name correctly");
	}

	/**
	 * Test method for 'pcgen.core.AbilityUtilities.getUndecoratedName(String, ArrayList)'
	 */
	public void testGetUndecoratedName()
	{
		final List<String> specifics = new ArrayList<>();
		specifics.add("quxx");

		final String name = "foo (bar, baz)";
		is(AbilityUtilities.getUndecoratedName(name, specifics),
			strEq("foo"), "Got correct undecorated name");
		is(specifics.size(), eq(2), "First extracted decoration is correct");
		is(specifics.get(0), strEq("bar"),
			"First extracted decoration is correct");
		is(specifics.get(1), strEq("baz"),
			"Second extracted decoration is correct");
	}
	
	/**
	 * Verify that getAllAbilities is working correctly
	 */
	public void testGetAllAbilities()
	{		
		LoadContext context = Globals.getContext();
		AbilityCategory parent = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "parent");
		AbilityCategory typeChild = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "typeChild");
		typeChild.setAbilityCategory(parent.getAbilityCatRef());
		typeChild.addAbilityType(Type.getConstant("Sport"));
		
		Ability fencing = TestHelper.makeAbility("fencing", parent, "sport");
		Ability reading = TestHelper.makeAbility("reading", parent, "interest");
		//Throwaway is required to create it...
		context.getReferenceContext().getManufacturer(Ability.class, typeChild);
		context.getReferenceContext().validate(null);
		context.getReferenceContext().resolveReferences(null);

		Collection<Ability> allAbilities = context.getReferenceContext().getManufacturer(Ability.class, parent).getAllObjects();
		assertTrue("Parent missing ability 'fencing'", allAbilities.contains(fencing));
		assertTrue("Parent missing ability 'reading'", allAbilities.contains(reading));
		assertEquals("Incorrect number of abilities found for parent", 2, allAbilities.size());
		
		allAbilities = context.getReferenceContext().getManufacturer(Ability.class, typeChild).getAllObjects();
		assertTrue("TypeChild missing ability fencing", allAbilities.contains(fencing));
		assertFalse("TypeChild shouldn't have ability 'reading'", allAbilities.contains(reading));
		assertEquals("Incorrect number of abilities found for TypeChild", 1, allAbilities.size());
		
	}
}
