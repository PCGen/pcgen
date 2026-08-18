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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;

import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui2.util.treeview.TreeViewTableModel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Verifies that clicking a {@link JTreeTable} row can expand or collapse it:
 * the tree column editor must re-dispatch the mouse event to the embedded
 * {@link JTree} with the button and extended modifiers intact, so BasicTreeUI
 * recognises the click and toggles the node. Covers both a single click and a
 * double click on the row.
 */
class JTreeTableEditorEventForwardingTest
{

	@ParameterizedTest
	@ValueSource(ints = {1, 2})
	void editorForwardsClickToTreeIntact(int clickCount)
	{
		TreeViewTableModel<String> model = new TreeViewTableModel<>(new SingleColumnDataView());
		model.setSelectedTreeView(new FlatTreeView());

		JTreeTable table = new JTreeTable(model);
		// Give the table geometry so columnAtPoint()/getCellRect() resolve.
		table.setSize(200, 100);

		JTree tree = table.getTree();
		AtomicReference<MouseEvent> forwarded = new AtomicReference<>();
		tree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				forwarded.set(e);
			}
		});

		TableCellEditor editor = table.getCellEditor(0, 0);

		// A plain left-button press: extended modifiers carry BUTTON1_DOWN_MASK,
		// and the button is BUTTON1. Both must reach the tree for it to toggle.
		MouseEvent press = new MouseEvent(table, MouseEvent.MOUSE_PRESSED, 0L,
			MouseEvent.BUTTON1_DOWN_MASK, 10, 5, clickCount, false, MouseEvent.BUTTON1);

		editor.isCellEditable(press);

		MouseEvent seen = forwarded.get();
		assertNotNull(seen, "editor should re-dispatch a mouse event to the tree");
		assertAll(
			() -> assertEquals(MouseEvent.BUTTON1_DOWN_MASK, seen.getModifiersEx(),
				"extended modifiers must reach the tree"),
			() -> assertEquals(MouseEvent.BUTTON1, seen.getButton(),
				"button must reach the tree"),
			() -> assertEquals(press.getID(), seen.getID(), "event id must be preserved"),
			() -> assertEquals(clickCount, seen.getClickCount(), "click count must be preserved")
		);
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
			return "JTreeTableEditorEventForwardingTest";
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
