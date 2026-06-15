/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
package pcgen.gui2.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui2.util.treeview.TreeViewTableModel;

import org.junit.jupiter.api.Test;

/**
 * Regression test for issue #6797: JTreeTable.getEditingRow() must honor
 * Swing's "no cell is being edited" sentinel (editingColumn == -1).
 *
 * <p>The bug: macOS accessibility (and Swing's own selection-changed path
 * during tree expansion) calls JTable.getColumnClass() with editingColumn
 * == -1, flowing into TreeViewTableModel.getDataColumn(-1) ->
 * datacolumns.get(-2) -> ArrayIndexOutOfBoundsException.
 */
class JTreeTableEditingRowTest
{

	@Test
	void getEditingRowDoesNotThrowWhenNotEditing()
	{
		TreeViewTableModel<String> model = new TreeViewTableModel<>(new SingleColumnDataView());
		model.setSelectedTreeView(new FlatTreeView());

		JTreeTable table = new JTreeTable(model);

		// editingColumn defaults to -1 (Swing sentinel: no cell is being edited).
		// JTable.getEditingRow() returns -1 in that state; PCGen's override
		// previously crashed because it indexed the model with editingColumn.
		assertDoesNotThrow(table::getEditingRow);
		assertEquals(-1, table.getEditingRow());
	}

	private static final class SingleColumnDataView implements DataView<String>
	{
		@Override
		public Object getData(String element, int column)
		{
			return element;
		}

		@Override
		public void setData(Object value, String element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return List.of(new DefaultDataViewColumn("Value", String.class));
		}

		@Override
		public String getPrefsKey()
		{
			return "JTreeTableEditingRowTest";
		}
	}

	private static final class FlatTreeView implements TreeView<String>
	{
		@Override
		public String getViewName()
		{
			return "Flat";
		}

		@Override
		public List<TreeViewPath<String>> getPaths(String pobj)
		{
			return List.of(new TreeViewPath<>(pobj));
		}
	}
}
