/**
 * @(#)JTreeTable.java    1.2 98/10/27
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 **/
package pcgen.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.character.SpellInfo;
import pcgen.core.utils.MessageType;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.system.LanguageBundle;

/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 **/
public final class JTreeTable extends JTableEx implements KeyListener
{
	static final long serialVersionUID = -3571248405124682593L;

	// 3 sec delay before reset of search word
	private TimedKeyBuffer keyBuffer = new TimedKeyBuffer(3000);

	/** A subclass of JTree. */
	private TreeTableCellRenderer tree;

	/**
	 * Constructor
	 * @param treeTableModel
	 */
	public JTreeTable(TreeTableModel treeTableModel)
	{
		super();

		/*
		 JTreeTable's event handling assumes bad things about
		 mouse pressed/released that are not true on MacOS X.
		 For example, one gets NPEs thrown when the mouse is
		 hit because the event manager is waiting for released
		 and one never gets the release.
		 It turns out that the MetalLAF handles this happily and
		 thus we can use that to get appropriate line styles,
		 without knackering Mac support.
		 Fix done by LeeAnn Rucker, formerly at Apple for Javasoft.
		 Added to pcgen by Scott Ellsworth
		 */
		UIManager.put("TreeTableUI", "javax.swing.plaf.metal.MetalTreeUI"); //$NON-NLS-1$ //$NON-NLS-2$
		UIManager.put("Tree.leftChildIndent", Integer.valueOf(3)); //$NON-NLS-1$
		UIManager.put("Tree.rightChildIndent", Integer.valueOf(8)); //$NON-NLS-1$

		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel);
		addKeyListener(this);

		// Install a tableModel representing the visible rows in tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper =
				new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

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
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? (-1)
			: editingRow;
	}

	/**
	 * returns a (sorted) List of expanded Tree paths
	 * @return expanded paths
	 **/
	public List<String> getExpandedPaths()
	{
		if (tree == null)
		{
			return null;
		}

		List<String> ret = new ArrayList<String>(tree.getRowCount());

		for (int i = 0; i < tree.getRowCount(); i++)
		{
			if (tree.isExpanded(i))
			{
				ret.add(tree.getPathForRow(i).toString());
			}
		}

		Collections.sort(ret, Collections.reverseOrder());

		return ret;
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

	/**
	 * This function starts a recursive search of all PObjectNodes
	 * of this JTreeTable, expanding all occurances of PObjects
	 * with a given name
	 * @param name
	 **/
	public void expandByPObjectName(String name)
	{
		expandByPObjectName((PObjectNode) this.getTree().getModel().getRoot(),
			name);
	}

	/**
	 * Expand a List of paths
	 * @param aList
	 **/
	public void expandPathList(List<String> aList)
	{
		if (aList == null)
		{
			return;
		}

		for (String path : aList)
		{
			for (int iRow = 0; iRow < getRowCount(); iRow++)
			{
				TreePath iPath = tree.getPathForRow(iRow);

				if ((iPath != null) && iPath.toString().equals(path))
				{
					tree.makeVisible(iPath);
					tree.expandPath(iPath);
				}
			}
		}
	}

	/**
	 * Proceses non-unicode keys such as action keys. If the user hits
	 * Escape, then the key buffer is cleared.
	 *
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 **/
	public void keyPressed(KeyEvent ke)
	{
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			keyBuffer.clearBuffer();
		}
	}

	//
	// KeyListener implementation to support quick select via keyboard
	//

	/**
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 **/
	public void keyReleased(@SuppressWarnings("unused")
	KeyEvent ke)
	{
		// TODO This method currently does nothing?
	}

	/**
	 * Processes unicode key entry. The keys are added to a timed buffer to
	 * build up the search string. A search is then made for a node in the
	 * current level which has matching name. Only alpha-numeric characters
	 * along with a few symbols will be accepted. No search will take place
	 * if focus is on an editable cell.
	 *
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 **/
	public void keyTyped(KeyEvent ke)
	{
		// Need to filter out any escaped characters as they
		// are most likely command shortcuts
		if ((ke.getModifiers() != 0)
			&& (ke.getModifiers() != InputEvent.SHIFT_MASK))
		{
			return;
		}

		// Filter out non-standard characters
		char keyChar = ke.getKeyChar();

		if (!Character.isLetterOrDigit(keyChar)
			&& !Character.isWhitespace(keyChar)
			&& !((keyChar == '-') || (keyChar == '+') || (keyChar == '(')
				|| (keyChar == ')') || (keyChar == '.') || (keyChar == ',')
				|| (keyChar == ':') || (keyChar == ';')))
		{
			return;
		}

		// If the current column is editable, then a keypress will be
		// to start editing, and not for us
		if ((getSelectedRow() >= 0) && (getSelectedColumn() >= 0)
			&& isCellEditable(getSelectedRow(), getSelectedColumn())
			&& (getColumnClass(getSelectedColumn()) != TreeTableModel.class))
		{
			return;
		}

		// Build the buffer
		keyBuffer.addChar(ke.getKeyChar());

		String buffer = keyBuffer.getString();

		// Grab the parent of the current node
		TreePath treePath = tree.getSelectionPath();
		PObjectNode current = ((PObjectNode) treePath.getLastPathComponent());
		PObjectNode parent = current.getParent();

		// Check for a expand/contract command
		if (buffer.length() == 1)
		{
			if (!current.isLeaf()
				&& ((keyChar == '+') || (keyChar == '-') || (keyChar == ' ')))
			{
				keyBuffer.clearBuffer();

				switch (keyChar)
				{
					case '+':

						//expand the node
						tree.expandPath(treePath);
						tree.setSelectionPath(treePath);

						return;

					case '-':

						// Collapse the node
						tree.collapsePath(treePath);
						tree.setSelectionPath(treePath);

						return;

					case ' ':

						// toggle the node's state
						if (tree.isCollapsed(treePath))
						{
							tree.expandPath(treePath);
						}
						else
						{
							tree.collapsePath(treePath);
						}

						tree.setSelectionPath(treePath);

						return;

					default:
						break;
				}
			}
		}

		// Only search if the current node is not a match
		String nodeName = current.getNodeName();
		if ((nodeName == null)
			|| !nodeName.regionMatches(true, 0, buffer, 0, buffer.length()))
		{
			// Find a node at the current level that matches the buffer
			searchSingleLevel(parent, buffer, true);
		}
	}

	/**
	 * Forwards the <code>scrollRectToVisible()</code> message to the
	 * <code>JComponent</code>'s parent. Components that can service
	 * the request, such as <code>JViewport</code>,
	 * override this method and perform the scrolling.
	 *
	 * @param aRect the visible <code>Rectangle</code>
	 * @see javax.swing.JViewport
	 */
	@Override
	public void scrollRectToVisible(Rectangle aRect)
	{
		Container parent;
		int dx = getX();
		int dy = getY();

		for (parent = getParent(); !(parent == null)
			&& !(parent instanceof JComponent)
			&& !(parent instanceof CellRendererPane); parent =
				parent.getParent())
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
	 * Search for a tree path
	 * @param name
	 * @param expand
	 * @return tree path
	 */
	public TreePath search(String name, boolean expand)
	{
		final PObjectNode rootNode = (PObjectNode) tree.getModel().getRoot();

		return search(rootNode, name, expand);
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
		LookAndFeel.installColorsAndFont(this,
			"Tree.background", "Tree.foreground", "Tree.font"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
	}

	/**
	 * This function recursively searches all PObjectNodes
	 * of this JTreeTable, expanding all occurances of PObjects
	 * with a given name
	 * The initial call should always come from expandByPObjectName(String)
	 * @param root
	 * @param name
	 */
	private void expandByPObjectName(PObjectNode root, String name)
	{
		List<PObjectNode> p1 = root.getChildren();

		if (p1 == null)
		{
			return;
		}

		for (int counter = 0; counter < p1.size(); counter++)
		{
			PObjectNode node = p1.get(counter);

			//recurse for all this node's subnodes...
			if (!node.isLeaf())
			{
				expandByPObjectName(node, name);
			}

			//...but look at all the terminal nodes (actual PObjects)
			else
			{
				final Object theObj = node.getItem();

				if (theObj instanceof CDOMObject)
				{
					if (((CDOMObject) theObj).getDisplayName().equals(name))
					{
						//expand that node
						List<PObjectNode> path = new ArrayList<PObjectNode>();
						PObjectNode pon = node;

						while (pon.getParent() != null)
						{
							//pop this entry onto the "front" of the list since it's a parent
							path.add(0, pon.getParent());
							pon = pon.getParent();
						}

						this.getTree().expandPath(new TreePath(path.toArray()));

						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
				else if (theObj instanceof SpellInfo)
				{
					if (theObj.toString().equals(name))
					{
						//expand that node
						List<PObjectNode> path = new ArrayList<PObjectNode>();
						PObjectNode pon = node;

						while (pon.getParent() != null)
						{
							//pop this entry onto the "front" of the list since it's a parent
							path.add(0, pon.getParent());
							pon = pon.getParent();
						}

						this.getTree().expandPath(new TreePath(path.toArray()));

						//would like to .scrollPathToVisible, but it doesn't seem to work
					}
				}
			}
		}
	}

	/**
	 * Makes sure all the path components in path are expanded (except
	 * for the last path component) and scrolls so that the
	 * node identified by the path is displayed. Only works when this
	 * <code>JTree</code> is contained in a <code>JScrollPane</code>.
	 *
	 * @param path  the <code>TreePath</code> identifying the node to
	 *         bring into view
	 */
	private void scrollPathToVisible(TreePath path)
	{
		if (path != null)
		{
			tree.makeVisible(path);

			Rectangle bounds = tree.getPathBounds(path);

			if (bounds != null)
			{
				scrollRectToVisible(bounds);
			}
		}
	}

	private TreePath search(PObjectNode root, String name, boolean expand)
	{
		int nameLength = name.length();
		List<PObjectNode> p1 = root.getChildren();

		if (p1 != null)
		{
			for (int counter = 0; counter < p1.size(); counter++)
			{
				PObjectNode node = p1.get(counter);

				//recurse for all this node's subnodes...
				if (!node.isLeaf())
				{
					TreePath tp = search(node, name, expand);

					if (tp != null)
					{
						return tp;
					}
				}

				//...but look at all the terminal nodes (actual PObjects)
				else
				{
					String aString = node.getNodeName();
					if (Constants.EMPTY_STRING.equals(aString))
					{
						aString = node.toString();
						if (aString.indexOf(Constants.PIPE) != -1)
						{
							aString =
									aString.substring(aString
										.lastIndexOf(Constants.PIPE) + 1);
						}
					}

					if (aString.regionMatches(true, 0, name, 0, nameLength))
					{
						//expand that node
						List<PObjectNode> path = new ArrayList<PObjectNode>();
						PObjectNode pon = node;

						while (pon.getParent() != null)
						{
							path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
							pon = pon.getParent();
						}

						TreePath tpath = new TreePath(path.toArray());

						if (expand)
						{
							tree.expandPath(tpath);
						}

						path.add(node);
						tpath = new TreePath(path.toArray());

						if (expand)
						{
							scrollPathToVisible(tpath);
							tree.setSelectionPath(tpath);
						}

						return tpath;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Searches the direct children of the supplied node for one with a
	 * name starting with the supplied name. If a node is found, it is
	 * optionally selected.
	 *
	 * @param root The PObjectNode whose children are to be searched
	 * @param name The partial name to look for.
	 * @param select Should we select the node (true) or just pass it back (false)
	 * @return TreePath The path to the first matchign node.
	 *                   Null if thee is no match.
	 */
	private TreePath searchSingleLevel(PObjectNode root, String name,
		boolean select)
	{
		int nameLength = name.length();
		List<PObjectNode> p1 = root.getChildren();

		if (p1 != null)
		{
			for (int counter = 0; counter < p1.size(); counter++)
			{
				PObjectNode node = p1.get(counter);

				// Fetch the name of the node so that we can do a comparison
				String aString = node.getNodeName();

				// Check for a match.
				if (aString.regionMatches(true, 0, name, 0, nameLength))
				{
					//select that node
					List<PObjectNode> path = new ArrayList<PObjectNode>();
					PObjectNode pon = node;

					while (pon.getParent() != null)
					{
						path.add(0, pon.getParent()); //pop this entry onto the "front" of the list since it's a parent
						pon = pon.getParent();
					}

					path.add(node);

					TreePath tpath = new TreePath(path.toArray());

					if (select)
					{
						scrollPathToVisible(tpath);
						tree.setSelectionPath(tpath);
					}

					return tpath;
				}
			}
		}

		return null;
	}

	/**
	 * A TreeCellRenderer that displays a JTree.
	 **/
	final class TreeTableCellRenderer extends JTree implements
			TableCellRenderer
	{
		// Last table/tree row asked to render
		private int visibleRow;
                private DefaultTableCellRenderer tableCellRenderer;

		TreeTableCellRenderer(TreeModel model)
		{
			super(model);
                        this.tableCellRenderer = new DefaultTableCellRenderer()
                        {

                            @Override
                            public void setBounds(int x, int y, int width,
                                                   int height)
                            {
                                super.setBounds(x, y, width, height);
                                TreeTableCellRenderer.this.setBounds(x, y, width,
                                                                     height);
                            }

                            @Override
                            public void paint(final Graphics g)
                            {
                                TreeTableCellRenderer.this.paint(g);
                                paintBorder(g);
                            }

                        };
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
		public void setBounds(int x, @SuppressWarnings("unused")
		int y, int w, @SuppressWarnings("unused")
		int h)
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
			if (aRowHeight > 0)
			{
				super.setRowHeight(aRowHeight);

				if ((JTreeTable.this != null)
					&& (JTreeTable.this.getRowHeight() != aRowHeight))
				{
					JTreeTable.this
						.setRowHeight(JTreeTable.this.getRowHeight());
				}
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
		public Component getTableCellRendererComponent(JTable table,
			@SuppressWarnings("unused")
			Object value, boolean isSelected, @SuppressWarnings("unused")
			boolean hasFocus, int row, @SuppressWarnings("unused")
			int column)
		{
			if (isSelected)
			{
				this.setBackground(table.getSelectionBackground());
			}
			else
			{
				this.setBackground(table.getBackground());
			}

			visibleRow = row;

			return tableCellRenderer.getTableCellRendererComponent(table, value,
                                                                   isSelected,
                                                                   hasFocus, row,
                                                                   column);
		}

		/**
		 * Fix to bad event handling on MacOS X
		 * @return UI Class ID
		 **/
		@Override
		public String getUIClassID()
		{
			return "TreeTableUI"; //$NON-NLS-1$
		}

		/**
		 * Sublcassed to translate the graphics such
		 * that the last visible row will be drawn at 0,0.
		 * @param g
		 **/
		@Override
		public void paint(final Graphics g)
		{
			int offset = -visibleRow * JTreeTable.this.getRowHeight();
                        g.translate(0, offset);
                        try
                        {
                            super.paint(g);
                        }
                        catch (Exception e)
                        {
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

			if (tcr instanceof DefaultTreeCellRenderer)
			{
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				dtcr.setTextSelectionColor(UIManager
					.getColor("Table.selectionForeground")); //$NON-NLS-1$
				dtcr.setBackgroundSelectionColor(UIManager
					.getColor("Table.selectionBackground")); //$NON-NLS-1$
			}
		}
	}

	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
	 * to listen for changes in the ListSelectionModel it maintains. Once
	 * a change in the ListSelectionModel happens, the paths are updated
	 * in the DefaultTreeSelectionModel.
	 **/
	private final class ListToTreeSelectionModelWrapper extends
			DefaultTreeSelectionModel
	{
		static final long serialVersionUID = -3571248405124682593L;

		// Set to true when we are updating the ListSelectionModel
		private boolean updatingListSelectionModel;

		private ListToTreeSelectionModelWrapper()
		{
			super();
			getListSelectionModel().addListSelectionListener(
				createListSelectionListener());
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
		 * If <code>updatingListSelectionModel</code> is false,
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

					for (int i = 0; i < sRows.length; i++)
					{
						if (tree.getPathForRow(sRows[i]) != null)
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

					for (int i = 0; i < sRows.length; i++)
					{
						TreePath tp = tree.getPathForRow(sRows[i]);

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
			/**
			 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
			 */
			public void valueChanged(@SuppressWarnings("unused")
			ListSelectionEvent e)
			{
				updateSelectedPathsFromSelectedRows();
			}
		}
	}

	private static final class TimedKeyBuffer
	{
		private String keyBuffer;
		private int timeToWait;
		private long lastMSecs;

		/**
		 * Constructor
		 * @param msecs
		 */
		public TimedKeyBuffer(int msecs)
		{
			timeToWait = msecs;
			lastMSecs = 0;
		}

		/**
		 * Get the buffer
		 * @return buffer
		 */
		public String getString()
		{
			if (System.currentTimeMillis() > (lastMSecs + timeToWait))
			{
				return Constants.EMPTY_STRING;
			}
			return keyBuffer;
		}

		/**
		 * Add a char to the buffer
		 * @param character
		 */
		public void addChar(char character)
		{
			if (System.currentTimeMillis() > (lastMSecs + timeToWait))
			{
				keyBuffer = Constants.EMPTY_STRING;
			}

			keyBuffer += String.valueOf(Character.toLowerCase(character));
			lastMSecs = System.currentTimeMillis();
		}

		/**
		 * Clear the buffer
		 */
		public void clearBuffer()
		{
			keyBuffer = Constants.EMPTY_STRING;
		}
	}

	/**
	 * TreeTableCellEditor implementation.
	 * Component returned is the JTree.
	 **/
	private final class TreeTableCellEditor extends AbstractCellEditor
			implements TableCellEditor
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
					if (getColumnClass(counter) == TreeTableModel.class)
					{
						MouseEvent me = (MouseEvent) e;
						MouseEvent newME =
								new MouseEvent(tree, me.getID(), me.getWhen(),
									me.getModifiers(), me.getX(), me.getY(), me
										.getClickCount(), me.isPopupTrigger());
						tree.dispatchEvent(newME);

						break;
					}
				}
			}

			return false;
		}

		/**
		 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
		 */
		public Component getTableCellEditorComponent(
			@SuppressWarnings("unused")
			JTable table, @SuppressWarnings("unused")
			Object value, @SuppressWarnings("unused")
			boolean isSelected, @SuppressWarnings("unused")
			int r, @SuppressWarnings("unused")
			int c)
		{
			return tree;
		}
	}

	/**
	 * search the tree
	 * @param lastSearch
	 * @return result
	 */
	public String searchTree(final String lastSearch)
	{
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue =
				ii.showInputDialog(null,
					LanguageBundle.getString("TreeTable.Prompt.Search"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.INFORMATION, null,
					lastSearch);

		String aString = ((String) selectedValue);
		if (aString != null)
		{
			if (aString.length() != 0)
			{
				if (this.search(aString, true) != null)
				{
					this.requestFocus();
				}
			}
		}
		else
		{
			aString = Constants.EMPTY_STRING;
		}
		return aString;
	}

	/**
	 * Associates a popup menu with the tree table.
	 * 
	 * <p>This handles showing the popup based on a right click and also handles
	 * any menu accelerators.
	 * 
	 * @param aPopupMenu Menu to associate.
	 */
	public void addPopupMenu(final JPopupMenu aPopupMenu)
	{
		addMouseListener(new PopupListener(this, aPopupMenu));
	}

	private class PopupListener extends MouseAdapter
	{
		private JPopupMenu theMenu;
		private JTree theTree;

		private PopupListener(final JTreeTable treeTable, final JPopupMenu aMenu)
		{
			theTree = treeTable.getTree();
			theMenu = aMenu;

			//			KeyListener myKeyListener = new KeyListener()
			//				{
			//					public void keyTyped(KeyEvent e)
			//					{
			//						dispatchEvent(e);
			//					}
			//
			//					// Walk through the list of accelerators
			//					// to see if the user has pressed a sequence
			//					// used by the popup. This would not
			//					// happen unless the popup was showing
			//					//
			//					public void keyPressed(KeyEvent e)
			//					{
			//						final int keyCode = e.getKeyCode();
			//
			//						if (keyCode != KeyEvent.VK_UNDEFINED)
			//						{
			//							final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
			//
			//							for (int i = 0; i < theMenu.getComponentCount(); ++i)
			//							{
			//								final Component menuComponent = theMenu.getComponent(i);
			//
			//								if (menuComponent instanceof JMenuItem)
			//								{
			//									KeyStroke ks = ((JMenuItem) menuComponent).getAccelerator();
			//
			//									if ((ks != null) && keyStroke.equals(ks))
			//									{
			//										((JMenuItem) menuComponent).doClick(2);
			//
			//										return;
			//									}
			//								}
			//							}
			//						}
			//
			//						dispatchEvent(e);
			//					}
			//
			//					public void keyReleased(KeyEvent e)
			//					{
			//						dispatchEvent(e);
			//					}
			//				};
			//
			//			treeTable.addKeyListener(myKeyListener);
		}

		/**
		 * Overridden to potential show the popup menu.
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		/**
		 * Overridden to potentially show the popup menu.
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				final TreePath selPath =
						theTree.getClosestPathForLocation(evt.getX(), evt
							.getY());

				if (selPath == null)
				{
					return;
				}

				if (theTree.isSelectionEmpty())
				{
					theTree.setSelectionPath(selPath);
					theMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else if (!theTree.isPathSelected(selPath))
				{
					theTree.setSelectionPath(selPath);
					theMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					theTree.addSelectionPath(selPath);
					theMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}
}
