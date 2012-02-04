/*
 * CategoryTableModel.java
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
 * Created on Apr 9, 2011, 5:42:00 PM
 */
package pcgen.gui2.tabs.ability;

import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.event.ChangeEvent;
import pcgen.core.facade.event.ChangeListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilteredListFacadeTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CategoryTableModel extends FilteredListFacadeTableModel<AbilityCategoryFacade> implements ChangeListener
{

	public CategoryTableModel(CharacterFacade character,
							  ListFacade<AbilityCategoryFacade> categories,
							  Filter<CharacterFacade, AbilityCategoryFacade> filter)
	{
		super(character);
		setDelegate(categories);
		setFilter(filter);
	}

	public AbilityCategoryFacade getCategory(int index)
	{
		return sortedList.getElementAt(index);
	}

	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if (columnIndex == 1)
		{
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		character.setRemainingSelection(sortedList.getElementAt(rowIndex),
										(Integer) aValue);
	}

	@Override
	public String getColumnName(int column)
	{
		if (column == 0)
		{
			return "Category";
		}
		return "Remaining";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if (columnIndex == 1)
		{
			return Integer.class;
		}
		return Object.class;
	}

	@Override
	protected Object getValueAt(AbilityCategoryFacade category, int column)
	{
		switch (column)
		{
			case 0:
				return category;
			case 1:
				return character.getRemainingSelections(category);
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ItemChanged(ChangeEvent event)
	{
		Object data = event.getSource();
		refilter();
		for (int i = 0; i < getRowCount(); i++)
		{
			AbilityCategoryFacade rowCat = getCategory(i);
			if (rowCat == data)
			{
				fireTableRowsUpdated(i, i);
			}
			
		}
	}

	/**
	 * Engage this class instance with the current UI. 
	 */
	public void install()
	{
		character.addAbilityCatSelectionListener(this);
	}

	/**
	 * Detach this class instance from the current UI. 
	 */
	public void uninstall()
	{
		character.removeAbilityCatSelectionListener(this);
	}
}
