/*
 * @(#)AbstractTreeTableModel.java    1.2 98/10/27
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
 */
package pcgen.gui.utils;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * An abstract implementation of the TreeTableModel interface, handling the list
 * of listeners.
 * @author Philip Milne
 * @version 1.2 10/27/98
 * Modified 6/1/2002 Scott Ellsworth added implemenation of getChildren(Object, int) which assumes parent fulfils the PObjectNode
 */
public abstract class AbstractTreeTableModel implements TreeTableModel
{
	private final EventListenerList listenerList = new EventListenerList();
	private Object root;
	private String qFilter = null;

	/**
	 * Constructor
	 * @param root
	 */
	public AbstractTreeTableModel(Object root)
	{
		this.root = root;
	}

	/**
     * This is not called in the JTree's default mode: use a naive implementation.
     * 
	 * @param parent 
	 * @param child 
     * @return index of the child or -1 
	 */
	public final int getIndexOfChild(Object parent, Object child)
	{
		for (int i = 0; i < getChildCount(parent); i++)
		{
			if (getChild(parent, i).equals(child))
			{
				return i;
			}
		}

		return -1;
	}

    /**
     * Returns true if its a leaf node
     * 
     * @param node 
     * @return true if its a leaf node
     */
	public final boolean isLeaf(Object node)
	{
		return getChildCount(node) == 0;
	}

	//
	// Default implmentations for methods in the TreeModel interface.
	//
    
    /**
     * Get the root node
     * @return root
     */
	public Object getRoot()
	{
		return root;
	}

	/**
     * Add a listener to the tree model
     * 
     * @param l
	 */
    public final void addTreeModelListener(TreeModelListener l)
	{
		listenerList.add(TreeModelListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 * @param source
	 * @param path
	 * @see EventListenerList
	 */
	public final void fireTreeNodesChanged(Object source, TreePath path)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i].equals(TreeModelListener.class))
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path);
				}

				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

    /**
     * Remove the listener for this tree model
     * 
     * @param l
     */
	public final void removeTreeModelListener(TreeModelListener l)
	{
		listenerList.remove(TreeModelListener.class, l);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public final void valueForPathChanged(TreePath path, Object newValue)
	{
	    // TODO This method currently does nothing?
	}

	/**
	 * Make the column with the Tree in it editable
	 * Making this column editable causes the JTable to forward mouse
	 * and keyboard events in the Tree column to the underlying JTree.
	 * @param node
	 * @param column
	 * @return true if cell editable
	 */
	public boolean isCellEditable(Object node, int column)
	{
		return getColumnClass(column).equals(TreeTableModel.class);
	}

	/**
	 * Left to be implemented in the subclass in original
	 * Currently done via PObjectNode
	 * @param parent
	 * @param index
	 * @return child
	 */
	public final Object getChild(Object parent, int index)
	{
		return PObjectNode.getChild(parent, index);
	}

	/**
	 * Left to be implemented in the subclass in original
	 * Currently done via PObjectNode
	 * @param parent
	 * @return child count
	 **/
	public final int getChildCount(Object parent)
	{
		return PObjectNode.getChildCount(parent);
	}

	//
	// Default impelmentations for methods in the TreeTableModel interface.
	//
    
    /**
     * Get the column class
     * 
     * @param column
     * @return Object.class
     */
	public Class<?> getColumnClass(int column)
	{
		return Object.class;
	}

	/**
	 * Sets the root
	 * @param root
	 */
	public void setRoot(Object root)
	{
		this.root = root;
	}

    /**
     * Does nothing
     * 
     * @param aValue 
     * @param node 
     * @param column 
     */
	public void setValueAt(Object aValue, Object node, int column)
	{
	    // TODO This method currently does nothing
	}

	/**
	 * This should be called instead of updateUI
	 * updateUI is for changes to the UI (such as metalUF to systemLF)
	 */
	public void updateTree()
	{
		fireTreeNodesChanged(root, new TreePath(root));
	}

	/**
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
     * 
	 * @see EventListenerList
	 */
	protected final void fireTreeStructureChanged(Object source, TreePath path)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i].equals(TreeModelListener.class))
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path);
				}

				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	// Left to be implemented in the subclass:

	/*
	 *   public int getColumnCount()
	 *   public String getColumnName(Object node, int column)
	 *   public Object getValueAt(Object node, int column)
	 */

	/**
	 * Get the QuickFilter
	 * @return QuickFilter
	 */
	public String getQFilter()
	{
		return qFilter;
	}

	/**
	 * Set theQuickFilter
	 * @param quickFilter
	 */
	public void setQFilter(String quickFilter) 
	{
		if(quickFilter != null) {
			this.qFilter = quickFilter.toLowerCase();
		}
		else {
			this.qFilter = null;
		}
	}

	/**
	 * Clear the QuickFilter
	 */
	public void clearQFilter() 
	{
		this.qFilter = null;
	}
}
