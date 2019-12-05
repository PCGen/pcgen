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
package pcgen.io.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code EquipSetMigrationTest} verifies the EquipSetMigration
 * class is functioning correctly.
 */
public class EquipSetMigrationTest extends AbstractCharacterTestCase
{
    private final int[] preOrderedVer = {6, 0, 1};
    private final int[] postOrderedVer = {6, 1, 3};

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TestHelper.makeEquipment("Item One\tTYPE:Goods\tSIZE:M");
        TestHelper.makeEquipment("Item Two\tTYPE:Goods\tSIZE:M");
        TestHelper.makeEquipment("Item Three\tTYPE:Goods\tSIZE:M");
        TestHelper.makeEquipment("Backpack\tTYPE:Goods.CONTAINER\tSIZE:M\tCONTAINS:UNLIM");

    }

    /**
     * Test method for {@link pcgen.io.migration.EquipSetMigration#migrateEquipSets(pcgen.core.PlayerCharacter, int[])}.
     */
    @Test
    public void testMigrateEquipSetsNoEquip()
    {
        PlayerCharacter pc = getCharacter();
        EquipSetMigration.migrateEquipSets(pc, preOrderedVer);
        assertEquals(0, pc.getDisplay().getEquipSet().size(), "No equipsets");
    }

    /**
     * Test method for {@link pcgen.io.migration.EquipSetMigration#migrateEquipSets(pcgen.core.PlayerCharacter, int[])}.
     */
    @Test
    public void testMigrateEquipSetsSimpleEquipSet()
    {
        PlayerCharacter pc = getCharacter();
        EquipSet eSet = new EquipSet("0.1", "Battle gear");
        pc.addEquipSet(eSet);
        pc.setCalcEquipSetId("0.1");

        Equipment eqItemOne = EquipmentList.getEquipmentFromName("Item One", pc);
        eqItemOne.setOutputIndex(1);
        Equipment eqItemTwo = EquipmentList.getEquipmentFromName("Item Two", pc);
        eqItemTwo.setOutputIndex(2);
        Equipment eqItemThree = EquipmentList.getEquipmentFromName("Item Three", pc);
        eqItemThree.setOutputIndex(3);

        equipItem(pc, eqItemOne, 1.0f,
                "Carried", "0.1.1");
        equipItem(pc, eqItemTwo, 1.0f,
                "Carried", "0.1.6");
        equipItem(pc, eqItemThree, 1.0f,
                "Carried", "0.1.4");

        EquipSetMigration.migrateEquipSets(pc, preOrderedVer);
        List<EquipSet> migratedList = (List<EquipSet>) pc.getDisplay()
                .getEquipSet();
        assertEquals(4, migratedList.size(), "Unexpected number of equipsets");
        verifyEquipSet(migratedList.get(0), "0.1", null);
        verifyEquipSet(migratedList.get(1), "0.1.01", eqItemOne);
        verifyEquipSet(migratedList.get(2), "0.1.02", eqItemTwo);
        verifyEquipSet(migratedList.get(3), "0.1.03", eqItemThree);
    }

    /**
     * Test method for {@link pcgen.io.migration.EquipSetMigration#renumberEquipmentSets(pcgen.core.PlayerCharacter)}.
     */
    @Test
    public void testRenumberEquipmentSetsNestedEquipSet()
    {
        PlayerCharacter pc = getCharacter();
        EquipSet eSet = new EquipSet("0.1", "Battle gear");
        pc.addEquipSet(eSet);
        pc.setCalcEquipSetId("0.1");

        Equipment eqItemOne = EquipmentList.getEquipmentFromName("Item One", pc);
        eqItemOne.setOutputIndex(4);
        Equipment eqBackpack = EquipmentList.getEquipmentFromName("Backpack", pc);
        eqBackpack.setOutputIndex(1);
        Equipment eqItemTwo = EquipmentList.getEquipmentFromName("Item Two", pc);
        eqItemTwo.setOutputIndex(3);
        Equipment eqItemThree = EquipmentList.getEquipmentFromName("Item Three", pc);
        eqItemThree.setOutputIndex(2);

        equipItem(pc, eqItemOne, 1.0f, "Carried", "0.1.1");
        equipItem(pc, eqBackpack, 1.0f, "Carried", "0.1.2");
        equipItem(pc, eqItemTwo, 1.0f, "Carried", "0.1.2.1");
        equipItem(pc, eqItemThree, 1.0f, "Carried", "0.1.2.2");

        EquipSetMigration.renumberEquipmentSets(pc);
        List<EquipSet> migratedList = (List<EquipSet>) pc.getDisplay()
                .getEquipSet();
        assertEquals(5, migratedList.size(), "Unexpected number of equipsets");
        verifyEquipSet(migratedList.get(0), "0.1", null);
        verifyEquipSet(migratedList.get(1), "0.1.02", eqItemOne);
        verifyEquipSet(migratedList.get(2), "0.1.01", eqBackpack);
        verifyEquipSet(migratedList.get(3), "0.1.01.02", eqItemTwo);
        verifyEquipSet(migratedList.get(4), "0.1.01.01", eqItemThree);
    }

    /**
     * Test method for {@link pcgen.io.migration.EquipSetMigration#renumberEquipmentSets(pcgen.core.PlayerCharacter)}.
     */
    @Test
    public void testRenumberEquipmentSetsMultipleEquipSet()
    {
        PlayerCharacter pc = getCharacter();
        EquipSet eSet = new EquipSet("0.1", "Battle gear");
        pc.addEquipSet(eSet);
        pc.setCalcEquipSetId("0.1");
        eSet = new EquipSet("0.3", "Around Town");
        pc.addEquipSet(eSet);

        Equipment eqItemOne = EquipmentList.getEquipmentFromName("Item One", pc);
        eqItemOne.setOutputIndex(1);
        Equipment eqItemTwo = EquipmentList.getEquipmentFromName("Item Two", pc);
        eqItemTwo.setOutputIndex(2);
        Equipment eqItemThree = EquipmentList.getEquipmentFromName("Item Three", pc);
        eqItemThree.setOutputIndex(3);
        Equipment eqBackpack = EquipmentList.getEquipmentFromName("Backpack", pc);
        eqBackpack.setOutputIndex(4);

        equipItem(pc, eqItemOne, 1.0f,
                "Carried", "0.1.1");
        equipItem(pc, eqItemTwo, 1.0f,
                "Carried", "0.1.6");
        equipItem(pc, eqItemThree, 1.0f,
                "Carried", "0.1.4");

        equipItem(pc, eqItemOne, 1.0f,
                "Carried", "0.3.6");
        equipItem(pc, eqItemTwo, 1.0f,
                "Backpack", "0.3.4.1");
        equipItem(pc, eqItemThree, 1.0f,
                "Backpack", "0.3.4.2");
        equipItem(pc, eqBackpack, 1.0f, "Carried", "0.3.4");

        EquipSetMigration.renumberEquipmentSets(pc);
        List<EquipSet> migratedList = (List<EquipSet>) pc.getDisplay()
                .getEquipSet();
        assertEquals(9, migratedList.size(), "Unexpected number of equipsets");
        verifyEquipSet(migratedList.get(0), "0.1", null);
        verifyEquipSet(migratedList.get(2), "0.1.01", eqItemOne);
        verifyEquipSet(migratedList.get(3), "0.1.02", eqItemTwo);
        verifyEquipSet(migratedList.get(4), "0.1.03", eqItemThree);
        verifyEquipSet(migratedList.get(1), "0.3", null);
        verifyEquipSet(migratedList.get(5), "0.3.01", eqItemOne);
        verifyEquipSet(migratedList.get(6), "0.3.02.01", eqItemTwo);
        verifyEquipSet(migratedList.get(7), "0.3.02.02", eqItemThree);
        verifyEquipSet(migratedList.get(8), "0.3.02", eqBackpack);
    }

    /**
     * Test method for {@link pcgen.io.migration.EquipSetMigration#migrateEquipSets(pcgen.core.PlayerCharacter, int[])}.
     */
    @Test
    public void testMigrateEquipSetsNoMigrate()
    {
        PlayerCharacter pc = getCharacter();
        EquipSet eSet = new EquipSet("0.1", "Battle gear");
        pc.addEquipSet(eSet);
        pc.setCalcEquipSetId("0.1");

        Equipment eqItemOne = EquipmentList.getEquipmentFromName("Item One", pc);
        eqItemOne.setOutputIndex(1);
        Equipment eqItemTwo = EquipmentList.getEquipmentFromName("Item Two", pc);
        eqItemTwo.setOutputIndex(2);
        Equipment eqItemThree = EquipmentList.getEquipmentFromName("Item Three", pc);
        eqItemThree.setOutputIndex(3);

        equipItem(pc, eqItemOne, 1.0f,
                "Carried", "0.1.1");
        equipItem(pc, eqItemTwo, 1.0f,
                "Carried", "0.1.6");
        equipItem(pc, eqItemThree, 1.0f,
                "Carried", "0.1.4");

        EquipSetMigration.migrateEquipSets(pc, postOrderedVer);
        List<EquipSet> migratedList = (List<EquipSet>) pc.getDisplay()
                .getEquipSet();
        assertEquals(4, migratedList.size(), "Unexpected number of equipsets");
        verifyEquipSet(migratedList.get(0), "0.1", null);
        verifyEquipSet(migratedList.get(1), "0.1.1", eqItemOne);
        verifyEquipSet(migratedList.get(2), "0.1.6", eqItemTwo);
        verifyEquipSet(migratedList.get(3), "0.1.4", eqItemThree);
    }

    private static void verifyEquipSet(EquipSet equipSet, String expectedIdPath, Equipment expectedItem)
    {
        assertEquals(expectedItem, equipSet.getItem(), "Unexpected item");
        assertEquals(expectedIdPath, equipSet.getIdPath(), "Unexpected path");
    }

    /**
     * Add the equipment item to the equipset.
     *
     * @param pc   The character owning the set
     * @param item The item of equipment
     * @param qty  The number to be placed in the location.
     * @param id   The set to add the item to
     * @return The new EquipSet object for the item.
     */
    private static EquipSet equipItem(PlayerCharacter pc,
            Equipment item, float qty, String locName, String id)
    {
        EquipSet newSet = new EquipSet(id, locName, item.getName(), item);
        item.setQty(qty);
        newSet.setQty(1.0f);
        pc.addEquipSet(newSet);
        return newSet;
    }

}
