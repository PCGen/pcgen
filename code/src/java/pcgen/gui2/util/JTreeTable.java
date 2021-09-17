/**
 * (#)JTreeTable.java    1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 *
 *
 **/
package pcgen.gui2.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pcgen.gui2.util.table.Row;
import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.TreeTableModel;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.util.Logging;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 **/
public class JTreeTable extends JTableEx
{

	private static final long serialVersionUID = -3571248405124682593L;
	/** A subclass of JTree. */
	private TreeTableCellRenderer tree;
	private TreeTableModelAdapter adapter;

	public JTreeTable()
	{
		this(null);
	}

	/**
	 * Constructor
	 * @param treeTableModel
	 */
	public JTreeTable(TreeTableModel treeTableModel)
	{
		tree = new TreeTableCellRenderer();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		adapter = new TreeTableModelAdapter(tree);
		setTreeTableModel(treeTableModel);
		super.setModel(adapter);
		// Force the JTable and JTree to share row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableNode.class, tree);
		setDefaultEditor(TreeTableNode.class, new TreeTableCellEditor());

		// No grid.
		setShowGrid(false);

		// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match the table
		if (tree.getRowHeight() < 1)
		{
			// Metal looks better like this.
			setRowHeight(18);
		}
		else
		{
			// If the UI has specified a rowHeight,
			// we'd better all be using the same one!
			setRowHeight(tree.getRowHeight());
		}

	}

	public TreeTableModel getTreeTableModel()
	{
		return (TreeTableModel) tree.getModel();
	}

	public void setTreeTableModel(TreeTableModel model)
	{
		tree.setModel(model);
		adapter.setTreeTableModel(model);
	}

	/**
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 * @return editing row
	 **/
	@Override
	public int getEditingRow()
	{
		return (getColumnClass(editingColumn) == TreeTableNode.class) ? (-1) : editingRow;
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 * @param aRowHeight
	 **/
	@Override
	public void setRowHeight(int aRowHeight)
	{
		super.setRowHeight(aRowHeight);

		if ((tree != null) && (tree.getRowHeight() != aRowHeight))
		{
			tree.setRowHeight(getRowHeight());
		}
	}

	/**
	 * Returns the tree that is being shared between the model.
	 * @return JTree
	 **/
	public JTree getTree()
	{
		return tree;
	}

	public TreeCellRenderer getTreeCellRenderer()
	{
		return tree.getCellRenderer();
	}

	public void setTreeCellRenderer(TreeColumnCellRenderer renderer)
	{
		tree.setCellRenderer(renderer);
	}

	/**
	 * Forwards the {@code scrollRectToVisible()} message to the
	 * {@code JComponent}'s parent. Components that can service
	 * the request, such as {@code JViewport},
	 * override this method and perform the scrolling.
	 *
	 * @param aRect the visible {@code Rectangle}
	 * @see javax.swing.JViewport
	 */
	@Override
	public void scrollRectToVisible(Rectangle aRect)
	{
		Container parent;
		int dx = getX();
		int dy = getY();

		for (parent = getParent(); (parent != null) && !(parent instanceof JComponent)
			&& !(parent instanceof CellRendererPane); parent = parent.getParent())
		{
			final Rectangle bounds = parent.getBounds();

			dx += bounds.x;
			dy += bounds.y;
		}

		if ((parent != null) && !(parent instanceof CellRendererPane))
		{
			aRect.x += dx;
			aRect.y += dy;

			((JComponent) parent).scrollRectToVisible(aRect);
			aRect.x -= dx;
			aRect.y -= dy;
		}
	}

	/**
	 * Overridden to message super and forward the method to the tree.
	 * Since the tree is not actually in the component hieachy it will
	 * never receive this unless we forward it in this manner.
	 **/
	@Override
	public void updateUI()
	{
		super.updateUI();

		if (tree != null)
		{
			tree.updateUI();
		}

		// Use the tree's default foreground and background
		// colors in the table
		LookAndFeel.installColorsAndFont(this, "Tree.background", //$NON-NLS-1$
			"Tree.foreground", //$NON-NLS-1$
			"Tree.font"); //$NON-NLS-1$
	}

	/**
	 * This is a wrapper class takes a TreeTableModel and implements
	 * the table model interface. The implementation is trivial, with
	 * all of the event dispatching support provided by the superclass:
	 * the AbstractTableModel.
	 *
	 */
	private static class TreeTableModelAdapter extends AbstractTableModel
			implements SortableTableModel, TreeModelListener, TreeExpansionListener
	{

		private JTree tree;
		private TreeTableModel treeTableModel;

		/**
		 * Constructor
		 * @param tree
		 */
		TreeTableModelAdapter(JTree tree)
		{
			this.tree = tree;
			tree.addTreeExpansionListener(this);
		}

		/**
		 * Install a TreeModelListener that can update the table when
		 * tree changes. We use delayedFireTableDataChanged as we can
		 * not be guaranteed the tree will have finished processing
		 * the event before us.
		 **/
		public void setTreeTableModel(TreeTableModel model)
		{
			if (treeTableModel != null)
			{
				treeTableModel.removeTreeModelListener(this);
			}
			treeTableModel = model;
			if (treeTableModel != null)
			{
				treeTableModel.addTreeModelListener(this);
			}
			fireTableStructureChanged();
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			if (treeTableModel == null)
			{
				return false;
			}
			return treeTableModel.isCellEditable(nodeForRow(row), column);
		}

		@Override
		public Class<?> getColumnClass(int column)
		{
			if (treeTableModel == null)
			{
				return Object.class;
			}
			return treeTableModel.getColumnClass(column);
		}

		// Wrappers, implementing TableModel interface.
		@Override
		public int getColumnCount()
		{
			if (treeTableModel == null)
			{
				return 0;
			}
			return treeTableModel.getColumnCount();
		}

		@Override
		public String getColumnName(int column)
		{
			if (treeTableModel == null)
			{
				return null;
			}
			return treeTableModel.getColumnName(column);
		}

		@Override
		public int getRowCount()
		{
			return tree.getRowCount();
		}

		@Override
		public void setValueAt(Object value, int row, int column)
		{
			if (treeTableModel == null)
			{
				return;
			}
			treeTableModel.setValueAt(value, nodeForRow(row), column);
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			if (treeTableModel == null)
			{
				return null;
			}
			return treeTableModel.getValueAt(nodeForRow(row), column);
		}

		private Object nodeForRow(int row)
		{
			TreePath treePath = tree.getPathForRow(row);
			if (treePath != null)
			{
				return treePath.getLastPathComponent();
			}
			return null;
		}

		@Override
		public void sortModel(Comparator<Row> comparator)
		{
			if (treeTableModel == null || !(treeTableModel instanceof SortableTreeTableModel model))
			{
				return;
			}
			Enumeration<TreePath> paths = tree.getExpandedDescendants(new TreePath(model.getRoot()));
			TreePath[] selectionPaths = tree.getSelectionPaths();
			model.sortModel(comparator);
			if (paths != null)
			{
				while (paths.hasMoreElements())
				{
					tree.expandPath(paths.nextElement());
				}
			}
			tree.setSelectionPaths(selectionPaths);
		}

		@Override
		public void treeNodesChanged(TreeModelEvent e)
		{
			TreePath parentPath = e.getTreePath();
			int leadingRow = Integer.MAX_VALUE;
			int trailingRow = -1;
			if (e.getChildren() != null)
			{
				for (Object node : e.getChildren())
				{
					TreePath childPath = parentPath.pathByAddingChild(node);
					int row = tree.getRowForPath(childPath);
					leadingRow = Math.min(leadingRow, row);
					trailingRow = Math.max(trailingRow, row);
				}
			}
			fireTableRowsUpdated(leadingRow, trailingRow);
		}

		/**
		 * This is used to when handling event cascading to
		 * prevent inconsistencies when updating the table.
		 * It is necessary when responding to tree model events
		 * that may have other listeners.
		 * By firing a new event later we ensure that all listeners
		 * have had a chance to update the tree's state.
		 */
		private void fireDelayedTableDataChanged()
		{
			SwingUtilities.invokeLater(this::fireTableDataChangedPreservingSelection);
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e)
		{
			fireDelayedTableDataChanged();
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e)
		{
			fireDelayedTableDataChanged();
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e)
		{
			//			fireTableStructureChanged();
			fireDelayedTableDataChanged();
		}
		// Don't use fireTableRowsInserted() here;
		// the selection model would get updated twice.

		@Override
		public void treeExpanded(TreeExpansionEvent event)
		{
			fireTableDataChangedPreservingSelection();
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event)
		{
			fireTableDataChangedPreservingSelection();
		}

		private void fireTableDataChangedPreservingSelection()
		{
			TreeSelectionModel selModel = tree.getSelectionModel();
			TreePath leadSelectionPath = selModel.getLeadSelectionPath();
			fireTableDataChanged();
			if (leadSelectionPath != null)
			{
				selModel.setSelectionPath(leadSelectionPath);
			}
		}

	}

	/**
	 * A TreeCellRenderer that displays a JTree.
	 **/
	final class TreeTableCellRenderer extends JTree implements TableCellRenderer
	{
		// Last table/tree row asked to render

		private int visibleRow;
		private DefaultTableCellRenderer tableCellRenderer;

		public TreeTableCellRenderer()
		{
			this.tableCellRenderer = new DefaultTableCellRenderer()
			{

				@Override
				public void setBounds(int x, int y, int width, int height)
				{
					super.setBounds(x, y, width, height);
					TreeTableCellRenderer.this.setBounds(x, y, width, height);
				}

				@Override
				public void paint(final Graphics g)
				{
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());
					TreeTableCellRenderer.this.paint(g);
					paintBorder(g);
				}

				@Override
				protected void setValue(Object value)
				{
					super.setValue(value);
					setToolTipText(getText());
				}

			};
			this.setCellRenderer(new TreeColumnCellRenderer());
			this.setOpaque(false);
		}

		/**
		 * This is overridden to set the height
		 * to match that of the JTable.
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 **/
		@Override
		public void setBounds(int x, @SuppressWarnings("unused") int y, int w, @SuppressWarnings("unused") int h)
		{
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		/**
		 * Sets the row height of the tree and forwards
		 * the row height to the table.
		 * @param aRowHeight
		 **/
		@Override
		public void setRowHeight(int aRowHeight)
		{
			super.setRowHeight(aRowHeight);
			if ((aRowHeight > 0) && (JTreeTable.this.getRowHeight() != aRowHeight))
			{
				JTreeTable.this.setRowHeight(aRowHeight);
			}
		}

		/**
		 * TreeCellRenderer method.
		 * Overridden to update the visible row.
		 * @param table
		 * @param value
		 * @param isSelected
		 * @param hasFocus
		 * @param row
		 * @param column
		 * @return Component
		 **/
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			visibleRow = row;

			Component comp =
					tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setBackground(comp.getBackground());
			return comp;
		}

		/**
		 * Sublcassed to translate the graphics such
		 * that the last visible row will be drawn at 0,0.
		 * @param g
		 **/
		@Override
		public void paint(final Graphics g)
		{
			Rectangle rect = JTreeTable.this.getCellRect(visibleRow, 0, true);
			int offset = -rect.y;
			g.translate(0, offset);
			try
			{
				super.paint(g);
			}
			catch (Exception e)
			{
				Logging.errorPrint("Paint Exception", e);
				// TODO Handle this?
			}
			finally
			{
				g.translate(0, -offset);
			}
		}

		/**
		 * updateUI is overridden to set the colors
		 * of the Trees renderer to match that of the table.
		 **/
		@Override
		public void updateUI()
		{
			super.updateUI();

			// Make the tree's cell renderer use the
			// table's cell selection colors.
			TreeCellRenderer tcr = getCellRenderer();

			if (tcr instanceof DefaultTreeCellRenderer dtcr)
			{
				dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground")); //$NON-NLS-1$
				dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground")); //$NON-NLS-1$
			}
		}

	}

	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	 * to listen for changes in the ListSelectionModel it maintains. Once
	 * a change in the ListSelectionModel happens, the paths are updated
	 * in the DefaultTreeSelectionModel.
	 **/
	private final class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	{

		static final long serialVersionUID = -3571248405124682593L;
		// Set to true when we are updating the ListSelectionModel
		private boolean updatingListSelectionModel;

		private ListToTreeSelectionModelWrapper()
		{
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		/**
		 * This is overridden to set updatingListSelectionModel
		 * and message super. This is the only place
		 * DefaultTreeSelectionModel alters the ListSelectionModel.
		 **/
		@Override
		public void resetRowSelection()
		{
			if (!updatingListSelectionModel)
			{
				updatingListSelectionModel = true;

				try
				{
					super.resetRowSelection();
				}
				finally
				{
					updatingListSelectionModel = false;
				}
			}

			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * Returns the list selection model.
		 * ListToTreeSelectionModelWrapper listens for changes
		 * to this model and updates the selected paths accordingly.
		 * @return ListSelectionModel
		 **/
		private ListSelectionModel getListSelectionModel()
		{
			return listSelectionModel;
		}

		/**
		 * Creates and returns an instance of ListSelectionHandler.
		 * @return ListSelectionListener
		 **/
		private ListSelectionListener createListSelectionListener()
		{
			return new ListSelectionHandler();
		}

		/**
		 * If {@code updatingListSelectionModel} is false,
		 * this will reset the selected paths from the selected
		 * rows in the list selection model.
		 **/
		private void updateSelectedPathsFromSelectedRows()
		{
			if (!updatingListSelectionModel)
			{
				updatingListSelectionModel = true;

				try
				{
					int[] sRows = getSelectedRows();

					if ((sRows == null) || (sRows.length == 0))
					{
						return;
					}

					int count = 0;

                    for (int row : sRows)
                    {
                        if (tree.getPathForRow(row) != null)
                        {
                            count++;
                        }
                    }

					if (count == 0)
					{
						return;
					}

					TreePath[] tps = new TreePath[count];
					count = 0;

                    for (int sRow : sRows)
                    {
                        TreePath tp = tree.getPathForRow(sRow);

                        if (tp != null)
                        {
                            tps[count++] = tp;
                        }
                    }

					// don't ned a clear as we are
					// using setSelectionPaths()
					//clearSelection();
					setSelectionPaths(tps);
				}
				finally
				{
					updatingListSelectionModel = false;
				}
			}
		}

		/**
		 * Class responsible for calling
		 * updateSelectedPathsFromSelectedRows when the
		 * selection of the list changse.
		 **/
		final class ListSelectionHandler implements ListSelectionListener
		{

			@Override
			public void valueChanged(@SuppressWarnings("unused") ListSelectionEvent e)
			{
				updateSelectedPathsFromSelectedRows();
			}

		}

	}

	/**
	 * TreeTableCellEditor implementation.
	 * Component returned is the JTree.
	 **/
	private final class TreeTableCellEditor implements TableCellEditor
	{

		/**
		 * Overridden to return false, and if the event is a mouse event
		 * it is forwarded to the tree.<p>
		 * The behavior for this is debatable, and should really be offered
		 * as a property. By returning false, all keyboard actions are
		 * implemented in terms of the table. By returning true, the
		 * tree would get a chance to do something with the keyboard
		 * events. For the most part this is ok. But for certain keys,
		 * such as left/right, the tree will expand/collapse where as
		 * the table focus should really move to a different column. Page
		 * up/down should also be implemented in terms of the table.
		 * By returning false this also has the added benefit that clicking
		 * outside of the bounds of the tree node, but still in the tree
		 * column will select the row, whereas if this returned true
		 * that wouldn't be the case.
		 * <p>By returning false we are also enforcing the policy that
		 * the tree will never be editable (at least by a key sequence).
		 * @param e
		 * @return true if cell editable
		 */
		@Override
		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent)
			{
				for (int counter = getColumnCount() - 1; counter >= 0; counter--)
				{
					if (getColumnClass(counter) == TreeTableNode.class)
					{
						MouseEvent me = (MouseEvent) e;
						int column = JTreeTable.this.columnAtPoint(me.getPoint());
						Rectangle cell = JTreeTable.this.getCellRect(0, column, true);
						MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX(),
							me.getY(), me.getClickCount(), me.isPopupTrigger());
						//we translate the event into the tree's coordinate system
						newME.translatePoint(-cell.x, 0);
						tree.dispatchEvent(newME);

						break;
					}
				}
			}

			return false;
		}

		@Override
		public Component getTableCellEditorComponent(@SuppressWarnings("unused") JTable table,
			@SuppressWarnings("unused") Object value, @SuppressWarnings("unused") boolean isSelected,
			@SuppressWarnings("unused") int r, @SuppressWarnings("unused") int c)
		{
			return tree;
		}

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent)
		{
			return false;
		}

		@Override
		public boolean stopCellEditing()
		{
			return true;
		}

		@Override
		public void cancelCellEditing()
		{
		}

		@Override
		public void addCellEditorListener(CellEditorListener l)
		{
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l)
		{
		}
	}

}
