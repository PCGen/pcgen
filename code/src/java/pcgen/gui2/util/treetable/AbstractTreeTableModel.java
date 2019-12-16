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
package pcgen.gui2.util.treetable;

import javax.swing.tree.DefaultTreeModel;

import pcgen.util.Logging;

/**
 * An abstract implementation of the TreeTableModel interface, handling the list
 * of listeners.
 */
public abstract class AbstractTreeTableModel extends DefaultTreeModel implements TreeTableModel
{

	protected AbstractTreeTableModel()
	{
		this(null);
	}

	public AbstractTreeTableModel(TreeTableNode root)
	{
		super(root);
	}

	/** By default, make the column with the Tree in it the only editable one. 
	*  Making this column editable causes the JTable to forward mouse 
	*  and keyboard events in the Tree column to the underlying JTree. 
	*/
	@Override
	public boolean isCellEditable(Object node, int column)
	{
		return getColumnClass(column) == TreeTableNode.class;
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		if (column == 0)
		{
			return TreeTableNode.class;
		}
		return Object.class;
	}

	/**
	 *  Returns a default name for the column using spreadsheet conventions:
	 *  A, B, C, ... Z, AA, AB, etc.  If {@code column} cannot be found,
	 *  returns an empty string.
	 *
	 * @param column  the column being queried
	 * @return a string containing the default name of {@code column}
	 */
	@Override
	public String getColumnName(int column)
	{
		StringBuilder result = new StringBuilder();
		for (; column >= 0; column = column / 26 - 1)
		{
			result.insert(0, (char) ((char) (column % 26) + 'A'));
		}
		return result.toString();
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column)
	{
		if (node == null)
		{
			Logging.log(Logging.WARNING, "Ignored attempt to set value of a null node. " + aValue + " col " + column);
			return;
		}
		TreeTableNode aNode = (TreeTableNode) node;
		aNode.setValueAt(aValue, column);
		nodeChanged(aNode);
	}

	@Override
	public Object getValueAt(Object node, int column)
	{
		return node == null ? null : ((TreeTableNode) node).getValueAt(column);
	}
}
