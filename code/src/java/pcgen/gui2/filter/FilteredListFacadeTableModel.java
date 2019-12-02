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
package pcgen.gui2.filter;

import java.util.Comparator;

import javax.swing.table.AbstractTableModel;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.SortedListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.util.table.Row;
import pcgen.gui2.util.table.SortableTableModel;

public abstract class FilteredListFacadeTableModel<E> extends AbstractTableModel
		implements SortableTableModel, ListListener<E>
{

	protected final SortedListFacade<E> sortedList;
	private final FilteredListFacade<CharacterFacade, E> filteredList;
	protected final CharacterFacade character;

	public FilteredListFacadeTableModel()
	{
		this(null);
	}

	public FilteredListFacadeTableModel(CharacterFacade character)
	{
		this.character = character;
		this.filteredList = new FilteredListFacade<>();
		filteredList.setContext(character);
		this.sortedList = new SortedListFacade<>((Comparator<Object>) (o1, o2) -> 0);
		sortedList.addListListener(this);
		sortedList.setDelegate(filteredList);
	}

	protected void setDelegate(ListFacade<E> delegate)
	{
		filteredList.setDelegate(delegate);
	}

	public void setFilter(Filter<? super CharacterFacade, ? super E> filter)
	{
		filteredList.setFilter(filter);
	}

	public void refilter()
	{
		filteredList.refilter();
	}

	@Override
	public int getRowCount()
	{
		return sortedList.getSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return getValueAt(sortedList.getElementAt(rowIndex), columnIndex);
	}

	protected abstract Object getValueAt(E element, int column);

	@Override
	public void sortModel(Comparator<Row> comparator)
	{
		if (comparator == null)
		{
			return;
		}
		sortedList.setComparator(new RowComparator(comparator));
	}

	@Override
	public void elementAdded(ListEvent<E> e)
	{
		fireTableRowsInserted(e.getIndex(), e.getIndex());
	}

	@Override
	public void elementRemoved(ListEvent<E> e)
	{
		fireTableRowsDeleted(e.getIndex(), e.getIndex());
	}

	@Override
	public void elementsChanged(ListEvent<E> e)
	{
		fireTableDataChanged();
	}

	@Override
	public void elementModified(ListEvent<E> e)
	{
		fireTableRowsUpdated(e.getIndex(), e.getIndex());
	}

	private class ElementRow implements Row
	{

		private final E element;

		public ElementRow(E element)
		{
			this.element = element;
		}

		@Override
		public Object getValueAt(int column)
		{
			return FilteredListFacadeTableModel.this.getValueAt(element, column);
		}

	}

	private class RowComparator implements Comparator<E>
	{

		private final Comparator<Row> comp;

		public RowComparator(Comparator<Row> comparator)
		{
			super();
			this.comp = comparator;
		}

		@Override
		public int compare(E o1, E o2)
		{
			return comp.compare(new ElementRow(o1), new ElementRow(o2));
		}

	}

}
