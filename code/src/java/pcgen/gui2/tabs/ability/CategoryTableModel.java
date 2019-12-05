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
package pcgen.gui2.tabs.ability;

import javax.swing.JTable;

import pcgen.core.AbilityCategory;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ChangeEvent;
import pcgen.facade.util.event.ChangeListener;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilteredListFacadeTableModel;

/**
 * The model for the bottom left table showing the ability categories and
 * their pool points.
 */
public class CategoryTableModel extends FilteredListFacadeTableModel<AbilityCategory> implements ChangeListener
{

    boolean installed = false;
    private final JTable categoryTable;

    public CategoryTableModel(CharacterFacade character, ListFacade<AbilityCategory> categories,
            Filter<CharacterFacade, AbilityCategory> filter, JTable theCategoryTable)
    {
        super(character);
        this.categoryTable = theCategoryTable;
        setDelegate(categories);
        setFilter(filter);
    }

    public AbilityCategory getCategory(int index)
    {
        return sortedList.getElementAt(index);
    }

    @Override
    public int getColumnCount()
    {
        return 3;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (columnIndex == 2)
        {
            return sortedList.getElementAt(rowIndex).allowPoolMod();
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        character.setRemainingSelection(sortedList.getElementAt(rowIndex), (Integer) aValue);
    }

    @Override
    public String getColumnName(int column)
    {
        switch (column)
        {
            case 0:
                return "Category";
            case 1:
                return "Total";
            case 2:
                return "Remaining";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == 0)
        {
            return Object.class;
        }
        return Integer.class;
    }

    @Override
    protected Object getValueAt(AbilityCategory category, int column)
    {
        switch (column)
        {
            case 0:
                return category;
            case 1:
                return character.getTotalSelections(category);
            case 2:
                return character.getRemainingSelections(category);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void ItemChanged(ChangeEvent event)
    {
        AbilityCategory facade = null;
        if (installed)
        {
            int selectedRow = categoryTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < sortedList.getSize())
            {
                facade = sortedList.getElementAt(selectedRow);
            }
        }
        Object data = event.getSource();
        refilter();
        for (int i = 0;i < getRowCount();i++)
        {
            AbilityCategory rowCat = getCategory(i);
            if (rowCat == data)
            {
                fireTableRowsUpdated(i, i);
            }
        }

        if (facade != null)
        {
            for (int i = 0;i < sortedList.getSize();i++)
            {
                if (facade == sortedList.getElementAt(i))
                {
                    categoryTable.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    /**
     * Engage this class instance with the current UI.
     */
    public void install()
    {
        character.addAbilityCatSelectionListener(this);
        installed = true;
    }

    /**
     * Detach this class instance from the current UI.
     */
    public void uninstall()
    {
        installed = false;
        character.removeAbilityCatSelectionListener(this);
    }
}
