/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.core.Equipment;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.util.AbstractListFacade;

public class EquipmentListFacadeImpl extends AbstractListFacade<EquipmentFacade> implements EquipmentListFacade
{

    private ArrayList<EquipmentFacade> equipmentList;
    private Map<EquipmentFacade, Integer> quantityMap;

    public EquipmentListFacadeImpl()
    {
        equipmentList = new ArrayList<>();
        quantityMap = new HashMap<>();
    }

    public EquipmentListFacadeImpl(List<Equipment> list)
    {
        equipmentList = new ArrayList<>(list);
        quantityMap = new HashMap<>();
        for (Equipment equipment : list)
        {
            quantityMap.put(equipment, equipment.getQty().intValue());
        }
    }

    /**
     * Refresh the equipment list facade with the supplied data.
     *
     * @param list The new list of equipment.
     */
    public void refresh(List<Equipment> list)
    {
        equipmentList = new ArrayList<>(list);
        quantityMap = new HashMap<>();
        for (Equipment equipment : list)
        {
            quantityMap.put(equipment, equipment.getQty().intValue());
        }
        fireElementsChanged(this);
    }

    @Override
    public void addEquipmentListListener(EquipmentListListener listener)
    {
        listenerList.add(EquipmentListListener.class, listener);
    }

    @Override
    public void removeEquipmentListListener(EquipmentListListener listener)
    {
        listenerList.remove(EquipmentListListener.class, listener);
    }

    private void fireQuantityChangedEvent(Object source, EquipmentFacade equipment, int index)
    {
        Object[] listeners = listenerList.getListenerList();
        EquipmentListEvent e = null;
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == EquipmentListListener.class)
            {
                if (e == null)
                {
                    e = new EquipmentListEvent(source, equipment, index);
                }
                ((EquipmentListListener) listeners[i + 1]).quantityChanged(e);
            }
        }
    }

    public void addElement(EquipmentFacade equipment, int quantity)
    {
        equipmentList.add(equipment);
        quantityMap.put(equipment, quantity);
        fireElementAdded(this, equipment, equipmentList.size() - 1);
    }

    public void removeElement(EquipmentFacade equipment)
    {
        int index = equipmentList.indexOf(equipment);
        if (index != -1)
        {
            equipmentList.remove(equipment);
            quantityMap.remove(equipment);
            fireElementRemoved(this, equipment, index);
        }
    }

    @Override
    public int getQuantity(EquipmentFacade equipment)
    {
        if (quantityMap.containsKey(equipment))
        {
            return quantityMap.get(equipment);
        }
        return 0;
    }

    public void setQuantity(EquipmentFacade equipment, int quantity)
    {
        quantityMap.put(equipment, quantity);
        fireQuantityChangedEvent(this, equipment, equipmentList.indexOf(equipment));
    }

    @Override
    public EquipmentFacade getElementAt(int index)
    {
        return equipmentList.get(index);
    }

    @Override
    public int getSize()
    {
        return equipmentList.size();
    }

    @Override
    public void modifyElement(EquipmentFacade equipment)
    {
        fireElementModified(this, equipment, equipmentList.indexOf(equipment));
    }
}
