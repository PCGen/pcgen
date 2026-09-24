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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import pcgen.facade.util.DefaultListFacade;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui2.util.treeview.TreeViewTableModel;

import org.junit.jupiter.api.Test;

/**
 * The tree column editor must recognise when a forwarded click falls on a node's
 * expand/collapse control so it can toggle the node itself, since the platform L&amp;F does
 * not always interpret the synthetic forwarded event as a control hit (the Spells-tab
 * "triangle needs a double-click" defect). {@code expandControlPathForClick} is the pure
 * decision behind that fix: it returns the branch path to toggle for a control-strip click,
 * and {@code null} for clicks on content, on leaves, or off any row.
 */
class JTreeTableExpandControlHitTest
{

	private static JTreeTable buildTable()
	{
		TreeViewTableModel<String> model = new TreeViewTableModel<>(new SingleColumnDataView());
		model.setSelectedTreeView(new TwoLevelTreeView());
		model.setDataModel(new DefaultListFacade<>(List.of("Alpha", "Beta")));
		JTreeTable table = new JTreeTable(model);
		table.setSize(300, 200);
		table.doLayout();
		return table;
	}

	@Test
	void clickLeftOfBranchContentIsExpandControlHit()
	{
		JTreeTable table = buildTable();
		JTree tree = table.getTree();
		TreePath branch = tree.getPathForRow(0);
		tree.collapsePath(branch);
		Rectangle b = tree.getPathBounds(branch);

		// A point in the control strip: left of the node's content, on the node's row.
		TreePath hit = JTreeTable.expandControlPathForClick(tree, b.x - 6, b.y + b.height / 2);

		assertEquals(branch, hit, "a click left of a branch node's content is an expand-control hit");
	}

	@Test
	void clickOnBranchContentIsNotExpandControlHit()
	{
		JTreeTable table = buildTable();
		JTree tree = table.getTree();
		TreePath branch = tree.getPathForRow(0);
		tree.collapsePath(branch);
		Rectangle b = tree.getPathBounds(branch);

		// A point inside the node's content bounds must NOT be treated as a control hit
		// (content clicks keep their normal selection / double-click-to-toggle behaviour).
		TreePath hit = JTreeTable.expandControlPathForClick(tree, b.x + b.width / 2, b.y + b.height / 2);

		assertNull(hit, "a click on the node's content is not an expand-control hit");
	}

	@Test
	void clickBelowEveryRowIsNotExpandControlHit()
	{
		JTreeTable table = buildTable();
		JTree tree = table.getTree();
		TreePath branch = tree.getPathForRow(0);
		tree.collapsePath(branch);

		// Far below the last visible row: no node there, so nothing to toggle.
		TreePath hit = JTreeTable.expandControlPathForClick(tree, 5, 10_000);

		assertNull(hit, "a click below all rows is not an expand-control hit");
	}

	@Test
	void editorSingleControlClickTogglesNode()
	{
		JTreeTable table = buildTable();
		JTree tree = table.getTree();
		TreePath branch = tree.getPathForRow(0);
		tree.collapsePath(branch);
		Rectangle b = tree.getPathBounds(branch);
		int x = Math.max(0, b.x - 6);
		int y = b.y + b.height / 2;

		// A single left-button click on the disclosure control, forwarded through the editor,
		// must leave the node expanded - regardless of whether the L&F handled the click itself.
		table.getCellEditor(0, 0).isCellEditable(control(table, x, y));
		assertTrue(tree.isExpanded(branch), "single control click should expand the collapsed node");

		// A second single control click collapses it again.
		table.getCellEditor(0, 0).isCellEditable(control(table, x, y));
		assertFalse(tree.isExpanded(branch), "a further single control click should collapse it");
	}

	private static MouseEvent control(JTreeTable table, int x, int y)
	{
		return new MouseEvent(table, MouseEvent.MOUSE_PRESSED, 0L,
			MouseEvent.BUTTON1_DOWN_MASK, x, y, 1, false, MouseEvent.BUTTON1);
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
			return "JTreeTableExpandControlHitTest";
		}
	}

	/** Puts every element under a shared parent node so there is a branch to expand. */
	private static final class TwoLevelTreeView implements TreeView<String>
	{
		@Override
		public String getViewName()
		{
			return "Grouped";
		}

		@Override
		public List<TreeViewPath<String>> getPaths(String pobj)
		{
			return List.of(new TreeViewPath<>(pobj, "Group"));
		}
	}
}
