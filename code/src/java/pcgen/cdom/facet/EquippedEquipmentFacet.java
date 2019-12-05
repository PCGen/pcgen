/*
 * Copyright (c) Thomas Parker, 2009.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractDataFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.core.Equipment;
import pcgen.output.publish.OutputDB;

/**
 * EquippedEquipmentFacet is a Facet that tracks the Equipment that is Equipped
 * by a Player Character.
 */
public class EquippedEquipmentFacet extends AbstractDataFacet<CharID, Equipment> implements SetFacet<CharID, Equipment>
{
    private EquipmentFacet equipmentFacet;

    /**
     * Triggered ("manually") when the equipped equipment on a Player Character
     * has changed. Evaluates all Equipment available to the Player Character
     * and places the Equipped Equipment into this EquippedEquipmentFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           equipped Equipment should be updated
     */
    public void reset(CharID id)
    {
        Set<Equipment> oldEquipped = (Set<Equipment>) removeCache(id);
        Set<Equipment> currentEquipment = equipmentFacet.getSet(id);
        Set<Equipment> newEquipped = Collections.newSetFromMap(new IdentityHashMap<>());
        setCache(id, newEquipped);
        if (oldEquipped != null)
        {
            // Delete items that the PC no longer has at all
            for (Equipment e : oldEquipped)
            {
                if (!currentEquipment.contains(e))
                {
                    fireDataFacetChangeEvent(id, e, DataFacetChangeEvent.DATA_REMOVED);
                }
            }
        }
        for (Equipment e : currentEquipment)
        {
            if (e.isEquipped())
            {
                newEquipped.add(e);
                // If not old, it's added
                if (oldEquipped == null || !oldEquipped.contains(e))
                {
                    fireDataFacetChangeEvent(id, e, DataFacetChangeEvent.DATA_ADDED);
                }
            } else
            {
                // If old, it's removed
                if (oldEquipped != null && oldEquipped.contains(e))
                {
                    fireDataFacetChangeEvent(id, e, DataFacetChangeEvent.DATA_REMOVED);
                }
            }
        }
    }

    /**
     * Returns a non-null copy of the Set of Equipment in this
     * EquippedEquipmentFacet for the Player Character represented by the given
     * CharID. This method returns an empty set if no objects are in this
     * EquippedEquipmentFacet for the Player Character identified by the given
     * CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned Set is
     * transferred to the class calling this method. Modification of the
     * returned Set will not modify this EquippedEquipmentFacet and modification
     * of this EquippedEquipmentFacet will not modify the returned Set.
     * Modifications to the returned Set will also not modify any future or
     * previous objects returned by this (or other) methods on
     * EquippedEquipmentFacet. If you wish to modify the information stored in
     * this EquippedEquipmentFacet, you must use the add*() and remove*()
     * methods of EquippedEquipmentFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this EquippedEquipmentFacet should be returned
     * @return A non-null copy of the Set of Equipment in this
     * EquippedEquipmentFacet for the Player Character represented by
     * the given CharID
     */
    @Override
    public Set<Equipment> getSet(CharID id)
    {
        Set<Equipment> set = (Set<Equipment>) getCache(id);
        if (set == null)
        {
            return Collections.emptySet();
        }
        Set<Equipment> returnEquipped = Collections.newSetFromMap(new IdentityHashMap<>());
        returnEquipped.addAll(set);
        return returnEquipped;
    }

    /**
     * Returns the count of the number of Equipment objects in this
     * EquippedEquipmentFacet for the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           count of the number of items in this EquippedEquipmentFacet
     *           should be returned
     * @return The count of the number of items in this EquippedEquipmentFacet
     * for the Player Character represented by the given CharID
     */
    @Override
    public int getCount(CharID id)
    {
        Set<Equipment> set = (Set<Equipment>) getCache(id);
        return (set == null) ? 0 : set.size();
    }

    public void setEquipmentFacet(EquipmentFacet equipmentFacet)
    {
        this.equipmentFacet = equipmentFacet;
    }

    /**
     * Copies the contents of the EquippedEquipmentFacet from one Player
     * Character to another Player Character, based on the given CharIDs
     * representing those Player Characters.
     * <p>
     * This is a method in EquippedEquipmentFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Map is internal information to EquippedEquipmentFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the EquippedEquipmentFacet of
     * one Player Character will only impact the Player Character where the
     * EquippedEquipmentFacet was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Set<Equipment> set = (Set<Equipment>) getCache(source);
        if (set != null)
        {
            Set<Equipment> newEquipped = Collections.newSetFromMap(new IdentityHashMap<>());
            newEquipped.addAll(set);
            setCache(copy, newEquipped);
        }
    }

    /**
     * Remove all entries for a single character.
     *
     * @param id The CharID representing the Player Character.
     */
    public void removeAll(CharID id)
    {
        removeCache(id);
    }

    public void init()
    {
        OutputDB.register("equipment.equipped", this);
    }
}
