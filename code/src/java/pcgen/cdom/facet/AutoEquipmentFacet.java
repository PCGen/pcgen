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
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.core.QualifiedObject;

/**
 * AutoEquipmentFacet is a Facet that tracks the Automatic Equipment objects
 * (those granted by AUTO:EQUIP) that are contained in a Player Character.
 */
public class AutoEquipmentFacet extends AbstractQualifiedListFacet<QualifiedObject<CDOMReference<Equipment>>>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds Equipment granted to a Player Character by AUTO:EQUIP.
     * <p>
     * Triggered when one of the Facets to which AutoEquipmentFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        List<QualifiedObject<CDOMReference<Equipment>>> list = cdo.getSafeListFor(ListKey.EQUIPMENT);
        if (list != null)
        {
            addAll(dfce.getCharID(), list, cdo);
        }
    }

    /**
     * Removes Equipment granted to a Player Character by AUTO:EQUIP from the
     * Player Character when the granting object is removed from the Player
     * Character.
     * <p>
     * Triggered when one of the Facets to which AutoEquipmentFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    /**
     * Returns a List of Equipment granted to the Player Character by all
     * AUTO:EQUIPMENT tokens on objects added to the Player Character.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this AutoEquipmentFacet and modification of
     * this AutoEquipmentFacet will not modify the returned Collection.
     * Modifications to the returned List will also not modify any future or
     * previous objects returned by this (or other) methods on
     * AutoEquipmentFacet. If you wish to modify the information stored in this
     * AutoEquipmentFacet, you must use the add*() and remove*() methods of
     * AutoEquipmentFacet.
     *
     * @param id The CharID identifying the Player Character for which the list
     *           of all equipment granted by AUTO:EQUIP will be returned.
     * @return The List of Equipment granted by the Player Character by all
     * AUTO:EQUIP tokens on objects added to the Player Character.
     */
    public List<Equipment> getAutoEquipment(CharID id)
    {
        List<Equipment> list = new ArrayList<>();
        for (QualifiedObject<CDOMReference<Equipment>> qo : getQualifiedSet(id))
        {
            Collection<Equipment> equipList = qo.getRawObject().getContainedObjects();
            for (Equipment e : equipList)
            {
                e = e.clone();
                e.setQty(1);
                e.setAutomatic(true);
                list.add(e);
            }
        }
        return list;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for AutoEquipmentFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the AutoEquipmentFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
