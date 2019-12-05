/**
 * Copyright 2007 (C) Andrew Wilson <nuance@sourceforge.net>
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * <p>
 * $Author$
 * $Date$
 * $Revision$
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.Type;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.Test;


class AbilityUtilitiesTest extends AbstractCharacterTestCase
{
    /**
     * Test method for 'pcgen.core.AbilityUtilities.removeChoicesFromName(String)'
     */
    @Test
    public void testRemoveChoicesFromName()
    {
        assertEquals(
                "Bare Thing",
                AbilityUtilities.removeChoicesFromName("Bare Thing (Mad cow)"), "Choice is removed from name correctly"
        );
    }

    /**
     * Test method for 'pcgen.core.AbilityUtilities.getUndecoratedName(String, ArrayList)'
     */
    @Test
    public void testGetUndecoratedName()
    {
        final List<String> specifics = new ArrayList<>();
        specifics.add("quxx");

        final String name = "foo (bar, baz)";
        assertEquals(
                "foo",
                AbilityUtilities.getUndecoratedName(name, specifics), "Got correct undecorated name"
        );
        assertEquals(2, specifics.size(), "Size of extracted decoration");
        assertEquals("bar", specifics.get(0), "First extracted decoration is correct");
        assertEquals("baz", specifics.get(1), "Second extracted decoration is correct");
    }

    /**
     * Verify that getAllAbilities is working correctly
     */
    @Test
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
        context.getReferenceContext().getManufacturerId(typeChild);
        context.getReferenceContext().validate(null);
        context.getReferenceContext().resolveReferences(null);

        Collection<Ability> allAbilities = context.getReferenceContext().getManufacturerId(parent).getAllObjects();
        assertTrue(allAbilities.contains(fencing), "Parent missing ability 'fencing'");
        assertTrue(allAbilities.contains(reading), "Parent missing ability 'reading'");
        assertEquals(2, allAbilities.size(), "Incorrect number of abilities found for parent");

        allAbilities = context.getReferenceContext().getManufacturerId(typeChild).getAllObjects();
        assertTrue(allAbilities.contains(fencing), "TypeChild missing ability fencing");
        assertFalse(allAbilities.contains(reading), "TypeChild shouldn't have ability 'reading'");
        assertEquals(1, allAbilities.size(), "Incorrect number of abilities found for TypeChild");

    }
}
