/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.prereq.Prerequisite;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.parser.PreAbilityParser;

import org.junit.jupiter.api.Test;


/**
 * The Class {@code DataSetTest} check that the Dataset class is functioning
 * correctly.
 */
class DataSetTest
{

    @Test
    public final void testGetEquipmentLocationsAll()
    {
        // if getEquipmentLocations is done first, the defaultonly test fails.
        getEquipmentLocationsDefaultOnly();
        getEquipmentLocations();
    }

    /**
     * Test method for {@link pcgen.core.DataSet#getEquipmentLocations()}. Validate
     * that the default locations are added.
     */
    public static void getEquipmentLocationsDefaultOnly()
    {
        DataSet dataset = new DataSet(Globals.getContext(), SettingsHandler.getGame(), new DefaultListFacade<>());
        ListFacade<BodyStructure> locations = dataset.getEquipmentLocations();
        assertNotNull(locations, "Body Structure should not be null");
        assertTrue(
                checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_EQUIPPED),
                "Expected to find Equipped"
        );
        assertTrue(
                checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_CARRIED),
                "Expected to find Carried"
        );
        assertTrue(
                checkBodyStructurePresent(locations, Constants.EQUIP_LOCATION_NOTCARRIED),
                "Expected to find Not Carried"
        );
        assertEquals(3, locations.getSize(), "Incorrect size of body structures list");
    }

    /**
     * Test method for {@link pcgen.core.DataSet#getEquipmentLocations()}. Validate
     * that known body structures get added.
     */
    public static void getEquipmentLocations()
    {
        final String structName = "TestStruct";
        SystemCollections.addToBodyStructureList(structName, SettingsHandler.getGame().getName());
        DataSet dataset =
                new DataSet(Globals.getContext(), SettingsHandler.getGame(),
                        new DefaultListFacade<>());
        ListFacade<BodyStructure> locations =
                dataset.getEquipmentLocations();
        assertNotNull(locations, "Body Structure should not be null");
        // TODO i18n this. It should be the same value as structname, not the localized value.
        assertTrue(
                checkBodyStructurePresent(locations, "Teststruct"),
                "Expected to find added body structure '" + structName + "'"
        );
        assertTrue(checkBodyStructurePresent(
                locations, Constants.EQUIP_LOCATION_EQUIPPED), "Expected to find Equipped");
        assertTrue(checkBodyStructurePresent(
                locations, Constants.EQUIP_LOCATION_CARRIED), "Expected to find Carried");
        assertTrue(checkBodyStructurePresent(
                locations, Constants.EQUIP_LOCATION_NOTCARRIED), "Expected to find Not Carried");
        assertEquals(4, locations
                .getSize(), "Incorrect size of body structures list");
    }

    /**
     * Verify the getPrereqAbilities method is functioning correctly.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testGetPrereqAbilities() throws PersistenceLayerException
    {
        Ability acrobatics = TestHelper.makeAbility("Acrobatics", BuildUtilities.getFeatCat(), "general");
        Ability dodge = TestHelper.makeAbility("Dodge", BuildUtilities.getFeatCat(), "general");
        Ability mobility = TestHelper.makeAbility("Mobility", BuildUtilities.getFeatCat(), "general");
        Ability springAttack = TestHelper.makeAbility("Spring Attack", BuildUtilities.getFeatCat(), "general");
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY=FEAT,KEY_Dodge",
                        false, false);
        mobility.addPrerequisite(prereq);
        prereq =
                parser.parse("ability", "2,CATEGORY=FEAT,KEY_Dodge,KEY_Mobility",
                        false, false);
        springAttack.addPrerequisite(prereq);

        DataSet dataset =
                new DataSet(Globals.getContext(), SettingsHandler.getGame(),
                        new DefaultListFacade<>());
        List<AbilityFacade> abilities = dataset.getPrereqAbilities(acrobatics);
        assertEquals(0, abilities.size(), "Acrobatics prereq should be empty");
        abilities = dataset.getPrereqAbilities(dodge);
        assertEquals(0, abilities.size(), "Dodge prereq should be empty");
        abilities = dataset.getPrereqAbilities(mobility);
        assertEquals(1, abilities.size(), "Mobility prereq should not be empty");
        assertEquals(dodge, abilities.get(0), "Mobility prereq should be dodge");
        abilities = dataset.getPrereqAbilities(springAttack);
        assertEquals(2, abilities.size(), "Spring Attack prereq should not be empty");
        assertEquals(dodge, abilities.get(0), "Spring Attack prereq should be dodge");
        assertEquals(mobility, abilities.get(1), "Spring Attack prereq should be mobility");
    }

    /**
     * @param locations
     * @param name
     */
    private static boolean checkBodyStructurePresent(
            ListFacade<BodyStructure> locations, String name)
    {
        boolean foundRec = false;
        for (BodyStructure equipmentLocFacade : locations)
        {
            if (equipmentLocFacade.toString().equals(name))
            {
                foundRec = true;
            }
        }

        return foundRec;
    }

}
