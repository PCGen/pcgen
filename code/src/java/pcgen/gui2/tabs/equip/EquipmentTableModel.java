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

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentListFacade.EquipmentListEvent;
import pcgen.facade.core.EquipmentListFacade.EquipmentListListener;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.filter.FilteredListFacadeTableModel;

/**
 * The model for the Equip View table. It shows a flat list of the equipment which
 * is either equipped, unequipped or of all gear owned. Each instance relates to
 * one type of view.
 */
public class EquipmentTableModel
        extends FilteredListFacadeTableModel<EquipmentFacade>
        implements EquipmentListListener, ReferenceListener<EquipmentSetFacade>
{

    protected EquipmentListFacade equipmentList = null;
    protected EquipmentSetFacade equipmentSet = null;

    public EquipmentTableModel(CharacterFacade character)
    {
        super(character);
        ReferenceFacade<EquipmentSetFacade> ref = character.getEquipmentSetRef();
        ref.addReferenceListener(this);
        setEquipmentList(ref.get().getEquippedItems());
        setEquipmentSet(ref.get());
    }

    @Override
    public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
    {
        setEquipmentList(e.getNewReference().getEquippedItems());
        setEquipmentSet(e.getNewReference());
    }

    protected void setEquipmentList(EquipmentListFacade equipmentList)
    {
        EquipmentListFacade oldList = this.equipmentList;
        if (oldList != null)
        {
            oldList.removeEquipmentListListener(this);
        }
        this.equipmentList = equipmentList;
        if (equipmentList != null)
        {
            equipmentList.addEquipmentListListener(this);
        }
        super.setDelegate(equipmentList);
    }

    protected void setEquipmentSet(EquipmentSetFacade equipmentSet)
    {
        this.equipmentSet = equipmentSet;
    }

    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return Object.class;
            case 1:
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return Float.class;
            case 5:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public void quantityChanged(EquipmentListEvent e)
    {
        for (int i = 0;i < sortedList.getSize();i++)
        {
            if (e.getEquipment() == sortedList.getElementAt(i))
            {
                fireTableCellUpdated(i, 3);
                return;
            }
        }
    }

    public EquipmentFacade getValue(int index)
    {
        return sortedList.getElementAt(index);
    }

    @Override
    protected Object getValueAt(EquipmentFacade equipment, int column)
    {
        switch (column)
        {
            case 0:
                return equipment;
            case 1:
                return equipment.getTypes()[0];
            case 2:
                return equipmentSet.getLocation(equipment);
            case 3:
                return equipmentList.getQuantity(equipment);
            case 4:
                return character.getInfoFactory().getWeight(equipment);
            case 5:
                return character.getInfoFactory().getDescription(equipment);
            default:
                return null;
        }
    }

    @Override
    public void elementModified(ListEvent<EquipmentFacade> e)
    {
    }

}
