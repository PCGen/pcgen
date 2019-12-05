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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.core.character.EquipSet;

/**
 * EquipSetFacet is a Facet that tracks the EquipSets for a Player Character.
 */
public class EquipSetFacet extends AbstractListFacet<CharID, EquipSet>
        implements DataFacetChangeListener<CharID, Equipment>
{

    /**
     * Remove an EqSet from the PC's Equipped Equipment.
     *
     * @param id   The identifier of the PC
     * @param eSet The EquipSet to remove.
     * @return true if the object was removed.
     */
    public boolean delEquipSet(CharID id, EquipSet eSet)
    {
        Collection<EquipSet> componentSet = getCachedSet(id);
        if (componentSet == null)
        {
            return false;
        }

        boolean found = false;
        final String pid = eSet.getIdPath();

        // first remove this EquipSet
        componentSet.remove(eSet);

        // now find and remove all it's children
        for (Iterator<EquipSet> e = componentSet.iterator();e.hasNext();)
        {
            final EquipSet es = e.next();
            final String abParentId = es.getParentIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
            final String abPid = pid + Constants.EQUIP_SET_PATH_SEPARATOR;

            if (abParentId.startsWith(abPid))
            {
                e.remove();
                found = true;
            }
        }
        return found;
    }

    /**
     * Search all of the PC equipment sets and replace all instances of oldItem with newItem.
     *
     * @param id      The ID of the PC.
     * @param oldItem the item to search for.
     * @param newItem The replacement item
     */
    public void updateEquipSetItem(CharID id, Equipment oldItem, Equipment newItem)
    {
        if (isEmpty(id))
        {
            return;
        }

        final List<EquipSet> tmpList = new ArrayList<>();

        // find all oldItem EquipSet's
        for (EquipSet es : getSet(id))
        {
            final Equipment eqI = es.getItem();

            if ((eqI != null) && oldItem.equals(eqI))
            {
                tmpList.add(es);
            }
        }

        for (EquipSet es : tmpList)
        {
            es.setValue(newItem.getName());
            es.setItem(newItem);
        }
    }

    /**
     * Search all the PCs Equipment sets for instances of eq and delete them.
     *
     * @param id The ID of the PC
     * @param eq The equipment to delete.
     */
    public void delEquipSetItem(CharID id, Equipment eq)
    {
        if (isEmpty(id))
        {
            return;
        }

        final List<EquipSet> tmpList = new ArrayList<>();

        // now find and remove equipment from all EquipSet's
        for (EquipSet es : getSet(id))
        {
            final Equipment eqI = es.getItem();

            if ((eqI != null) && eq.equals(eqI))
            {
                tmpList.add(es);
            }
        }

        for (EquipSet es : tmpList)
        {
            delEquipSet(id, es);
        }
    }

    /**
     * Get an EqSet by its path.
     *
     * @param id   The ID of the PC
     * @param path The path of the EqSet.
     * @return The requested EqSet or null if not found.
     */
    public EquipSet getEquipSetByIdPath(CharID id, String path)
    {
        for (EquipSet eSet : getSet(id))
        {
            if (eSet.getIdPath().equals(path))
            {
                return eSet;
            }
        }

        return null;
    }

    /**
     * Get an EqSet by its name.
     *
     * @param id   The ID of the PC
     * @param name The name of the EqSet.
     * @return The requested EqSet or null if not found.
     */
    public EquipSet getEquipSetByName(CharID id, String name)
    {
        for (EquipSet eSet : getSet(id))
        {
            if (eSet.getName().equals(name))
            {
                return eSet;
            }
        }

        return null;
    }

    /**
     * Search the PCs equipment sets rooted on idPath and return a count of the items
     * named "name".
     *
     * @param id     The ID of the PC
     * @param idPath The
     * @param name   The name of the EqSet.
     * @return The count fo the number of instances of the named equipSet on the given path.
     */
    public Float getEquipSetCount(CharID id, String idPath, String name)
    {
        float count = 0;
        for (EquipSet eSet : getSet(id))
        {
            final String esID = eSet.getIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
            final String abID = idPath + Constants.EQUIP_SET_PATH_SEPARATOR;
            if (esID.startsWith(abID))
            {
                if (eSet.getValue().equals(name))
                {
                    count += eSet.getQty();
                }
            }
        }
        return count;
    }

    /**
     * Search the equipment sets rooted at {@code set}, and return the number
     * of items of {@code set} in the set.
     *
     * @param id  The ID of the PC
     * @param set The root of an Equipment Set
     * @param eq  The equipment to search for.
     * @return The number of items equipped.
     */
    public Float getEquippedQuantity(CharID id, EquipSet set, Equipment eq)
    {
        final String rPath = set.getIdPath();

        for (EquipSet es : getSet(id))
        {
            String esIdPath = es.getIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;
            String rIdPath = rPath + Constants.EQUIP_SET_PATH_SEPARATOR;

            if (!esIdPath.startsWith(rIdPath))
            {
                continue;
            }

            if (eq.getName().equals(es.getValue()))
            {
                return es.getQty();
            }
        }

        return (float) 0;
    }

    /**
     * Notify the facet's listeners that data has been added
     *
     * @param dfce The data facet change event.
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        //Ignore
    }

    /**
     * Notify the facet's listeners that data has been removed.
     *
     * @param dfce The data facet change event.
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        delEquipSetItem(dfce.getCharID(), dfce.getCDOMObject());
    }
}
