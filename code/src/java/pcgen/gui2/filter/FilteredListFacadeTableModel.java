/*
 * FilteredListFacadeTableModel.java
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
 * Created on Jan 31, 2011, 9:55:02 PM
 */
package pcgen.gui2.filter;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.SortedListFacade;
import pcgen.gui2.util.table.SortableTableModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class FilteredListFacadeTableModel<E> extends AbstractTableModel implements SortableTableModel, ListListener<E>
{

	protected final SortedListFacade<E> sortedList;
	private final FilteredListFacade filteredList;
	protected final CharacterFacade character;

	public FilteredListFacadeTableModel()
	{
		this(null);
	}

	public FilteredListFacadeTableModel(CharacterFacade character)
	{
		this.character = character;
		this.filteredList = new FilteredListFacade();
		filteredList.setContext(character);
		this.sortedList = new SortedListFacade<E>(new Comparator<Object>()
		{

			public int compare(Object o1, Object o2)
			{
				return 0;
			}

		});
		sortedList.addListListener(this);
		sortedList.setDelegate(filteredList);
	}

	protected void setDelegate(ListFacade<E> delegate)
	{
		filteredList.setDelegate(delegate);
	}

	public void setFilter(Filter<CharacterFacade, E> filter)
	{
		filteredList.setFilter(filter);
	}

	public void refilter()
	{
		filteredList.refilter();
	}

	public int getRowCount()
	{
		return sortedList.getSize();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return getValueAt(sortedList.getElementAt(rowIndex), columnIndex);
	}

	protected abstract Object getValueAt(E element, int column);

	public void sortModel(Comparator<List<?>> comparator)
	{
		if (comparator == null)
		{
			return;
		}
		sortedList.setComparator(new RowComparator(comparator));
	}

	public void elementAdded(ListEvent<E> e)
	{
		fireTableRowsInserted(e.getIndex(), e.getIndex());
	}

	public void elementRemoved(ListEvent<E> e)
	{
		fireTableRowsDeleted(e.getIndex(), e.getIndex());
	}

	public void elementsChanged(ListEvent<E> e)
	{
		fireTableDataChanged();
	}

	private class RowList extends AbstractList
	{

		private final E element;

		public RowList(E element)
		{
			super();
			this.element = element;
		}

		@Override
		public Object get(int index)
		{
			return getValueAt(element, index);
		}

		@Override
		public int size()
		{
			return getColumnCount();
		}

	}

	private class RowComparator implements Comparator<E>
	{

		private Comparator<List<?>> comp;

		public RowComparator(Comparator<List<?>> comparator)
		{
			super();
			this.comp = comparator;
		}

		public int compare(E o1, E o2)
		{
			return comp.compare(new RowList(o1), new RowList(o2));
		}

	}

}
