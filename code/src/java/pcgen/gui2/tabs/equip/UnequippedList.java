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
 */
package pcgen.gui2.tabs.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentListFacade.EquipmentListListener;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.util.AbstractListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.util.Logging;

public class UnequippedList extends AbstractListFacade<EquipmentFacade> implements EquipmentListFacade,
        EquipmentListListener, ListListener<EquipmentFacade>, ReferenceListener<EquipmentSetFacade>
{

    private final EquipmentListFacade purchasedList;
    private final List<EquipmentFacade> equipmentList;
    private final Map<EquipmentFacade, Integer> quantityMap;
    private EquipmentListFacade equippedList;
    private EquipmentFacade lastRemoved;

    public UnequippedList(CharacterFacade character)
    {
        this.purchasedList = character.getPurchasedEquipment();
        equipmentList = new ArrayList<>();
        quantityMap = new HashMap<>();
        ReferenceFacade<EquipmentSetFacade> ref = character.getEquipmentSetRef();
        equippedList = ref.get().getEquippedItems();
        for (EquipmentFacade equipment : purchasedList)
        {
            int quantity = purchasedList.getQuantity(equipment) - equippedList.getQuantity(equipment);
            if (quantity > 0)
            {
                equipmentList.add(equipment);
                quantityMap.put(equipment, quantity);
            }
        }
        purchasedList.addListListener(this);
        purchasedList.addEquipmentListListener(this);
        equippedList.addListListener(this);
        equippedList.addEquipmentListListener(this);
        ref.addReferenceListener(this);
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
    public int getQuantity(EquipmentFacade equipment)
    {
        if (quantityMap.containsKey(equipment))
        {
            return quantityMap.get(equipment);
        }
        return 0;
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

    @Override
    public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
    {
        equippedList.removeListListener(this);
        equippedList.removeEquipmentListListener(this);
        equippedList = e.getNewReference().getEquippedItems();
        equippedList.addListListener(this);
        equippedList.addEquipmentListListener(this);
        elementsChanged(null);
    }

    private void addEquipment(EquipmentFacade equipment, int quantity)
    {
        equipmentList.add(equipment);
        quantityMap.put(equipment, quantity);
        fireElementAdded(this, equipment, equipmentList.size() - 1);
    }

    private void setQuantity(EquipmentFacade equipment, int quantity)
    {
        quantityMap.put(equipment, quantity);
        fireQuantityChangedEvent(this, equipment, quantity);
    }

    private void removeEquipment(EquipmentFacade equipment)
    {
        int index = equipmentList.indexOf(equipment);
        if (index >= 0)
        {
            equipmentList.remove(index);
            quantityMap.remove(equipment);
            fireElementRemoved(this, equipment, index);
        }
    }

    @Override
    public void elementAdded(ListEvent<EquipmentFacade> e)
    {
        EquipmentFacade equipment = e.getElement();
        if (e.getSource() == purchasedList)
        {
            lastRemoved = null;
            addEquipment(equipment, purchasedList.getQuantity(equipment));
        } else
        {
            int quantity = purchasedList.getQuantity(equipment) - equippedList.getQuantity(equipment);
            if (quantity > 0)
            {
                setQuantity(equipment, quantity);
            } else
            {
                removeEquipment(equipment);
            }
        }
    }

    @Override
    public void elementRemoved(ListEvent<EquipmentFacade> e)
    {
        EquipmentFacade equipment = e.getElement();
        if (e.getSource() == purchasedList)
        {
            removeEquipment(equipment);
            // We remember this item has been removed as we could be getting a
            // message to say it has been unequipped shortly.
            lastRemoved = equipment;
        } else
        {
            if (equipment.equals(lastRemoved))
            {
                if (Logging.isDebugMode())
                {
                    Logging.debugPrint("Ignoring unequip of item just removed: " + equipment);
                }
                return;
            }
            int quantity = purchasedList.getQuantity(equipment) - equippedList.getQuantity(equipment);
            if (quantityMap.containsKey(equipment))
            {
                setQuantity(equipment, quantity);
            } else
            {
                addEquipment(equipment, quantity);
            }
        }
    }

    @Override
    public void elementsChanged(ListEvent<EquipmentFacade> e)
    {
        equipmentList.clear();
        quantityMap.clear();
        for (EquipmentFacade equipment : purchasedList)
        {
            int quantity = purchasedList.getQuantity(equipment) - equippedList.getQuantity(equipment);
            if (quantity > 0)
            {
                equipmentList.add(equipment);
                quantityMap.put(equipment, quantity);
            }
        }
        fireElementsChanged(this);
    }

    @Override
    public void elementModified(ListEvent<EquipmentFacade> e)
    {
    }

    @Override
    public void quantityChanged(EquipmentListEvent e)
    {
        EquipmentFacade equipment = e.getEquipment();
        int quantity = purchasedList.getQuantity(equipment) - equippedList.getQuantity(equipment);
        if (quantity > 0)
        {
            if (quantityMap.containsKey(equipment))
            {
                setQuantity(equipment, quantity);
            } else
            {
                addEquipment(equipment, quantity);
            }
        } else
        {
            removeEquipment(equipment);
        }
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

    @Override
    public void modifyElement(EquipmentFacade equipment)
    {
        // Ignored.
    }

}
