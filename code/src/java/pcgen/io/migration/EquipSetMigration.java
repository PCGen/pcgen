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
 *
 *
 */
package pcgen.io.migration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;
import pcgen.core.utils.CoreUtility;

/**
 * The Class {@code EquipSetMigration} updates a character's equipment
 * sets to match newer requirements.
 */
public final class EquipSetMigration
{

    private static EquipSetOutputOrderComparator comparator = new EquipSetOutputOrderComparator();

    private EquipSetMigration()
    {
    }

    /**
     * Update the character's equipment sets, if required.
     *
     * @param pc     The character being updated.
     * @param pcgVer The version of PCGen in which the character was created.
     */
    public static void migrateEquipSets(PlayerCharacter pc, int[] pcgVer)
    {
        if (CoreUtility.compareVersions(pcgVer, new int[]{6, 1, 3}) < 0)
        {
            renumberEquipmentSets(pc);
        }
    }

    /**
     * Renumber the equipment sets so that they are sortable in current output
     * order. For each equipment set, change the format of the idpaths from
     * 0.1.1.1 to 0.1.01.01 and renumber items in a container in accordance with
     * their output order
     *
     * @param pc The character to have equipment sets renumbered.
     */
    static void renumberEquipmentSets(PlayerCharacter pc)
    {
        Collection<EquipSet> allEquipSets = pc.getDisplay().getEquipSet();
        List<EquipSet> sortedChildrenEs = getSortedChildren(allEquipSets, "0");
        for (EquipSet equipSet : sortedChildrenEs)
        {
            List<EquipSet> children = getSortedChildren(allEquipSets, equipSet.getIdPath());
            renumberChildren(children, allEquipSets, equipSet.getIdPath());
        }
    }

    /**
     * Retrieve a list of the equipment sets underthe parent id path in id path order.
     *
     * @param allEquipSets The set of all equipment sets.
     * @param parentIdPath The id path of the top of the tree we want to retrieve.
     * @return The sorted list of child equipment sets.
     */
    private static List<EquipSet> getSortedChildren(Collection<EquipSet> allEquipSets, String parentIdPath)
    {
        List<EquipSet> children = new ArrayList<>();
        for (EquipSet equipSet : allEquipSets)
        {
            if (equipSet.getParentIdPath().equals(parentIdPath))
            {
                children.add(equipSet);
            }
        }

        children.sort(comparator);
        return children;
    }

    /**
     * Renumber the equipment sets in order with a potential new parent path
     * also. This is a recursive function.
     *
     * @param targets       The ordered list of equipment sets to be renumbered.
     * @param allEquipSets  The collection of all of the character's equipment sets.
     * @param newParentPath The new path of the parent.
     */
    private static void renumberChildren(List<EquipSet> targets, Collection<EquipSet> allEquipSets,
            String newParentPath)
    {
        if (targets.isEmpty())
        {
            return;
        }

        int index = 1;
        NumberFormat format = new DecimalFormat("00");
        for (EquipSet equipSet : targets)
        {
            String oldIdPath = equipSet.getIdPath();
            equipSet.setIdPath(newParentPath + "." + format.format(index++));
            List<EquipSet> children = getSortedChildren(allEquipSets, oldIdPath);
            renumberChildren(children, allEquipSets, equipSet.getIdPath());
        }
    }

    /**
     * Comparator to order the equipment sets in output order.
     */
    private static class EquipSetOutputOrderComparator implements Comparator<EquipSet>
    {

        @Override
        public int compare(EquipSet arg0, EquipSet arg1)
        {
            Equipment equip0 = arg0.getItem();
            Equipment equip1 = arg1.getItem();

            int equipOutputOrder0 = equip0 == null ? 99999 : equip0.getOutputIndex();
            int equipOutputOrder1 = equip1 == null ? 99999 : equip1.getOutputIndex();
            if (equipOutputOrder0 != equipOutputOrder1)
            {
                return Integer.compare(equipOutputOrder0, equipOutputOrder1);
            }

            String sortKey0 = getSortKey(equip0);
            String sortKey1 = getSortKey(equip1);
            if (!sortKey0.equals(sortKey1))
            {
                return sortKey0.compareTo(sortKey1);
            }

            return arg0.getIdPath().compareTo(arg1.getIdPath());
        }

        private String getSortKey(Equipment equip)
        {
            if (equip == null)
            {
                return "zzzzzzzz";
            }

            String key = equip.get(StringKey.SORT_KEY);
            if (key == null)
            {
                key = equip.getDisplayName();
            }

            return key;
        }
    }

}
