/*
 * Copyright (c) Thomas Parker, 2018.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.model;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;

/**
 * ActiveEqHeadFacet is a Facet that tracks the Equipment objects that have been equipped
 * by a Player Character. This then grabs the EquipmentHead objects.
 */
public class ActiveEqHeadFacet extends AbstractSourcedListFacet<CharID, EquipmentHead>
        implements DataFacetChangeListener<CharID, Equipment>
{
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        CharID id = dfce.getCharID();
        Equipment equipment = dfce.getCDOMObject();
        for (EquipmentHead head : equipment.getEquipmentHeads())
        {
            add(id, head, equipment);
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        CharID id = dfce.getCharID();
        Equipment equipment = dfce.getCDOMObject();
        for (EquipmentHead head : equipment.getEquipmentHeads())
        {
            remove(id, head, equipment);
        }
    }
}
