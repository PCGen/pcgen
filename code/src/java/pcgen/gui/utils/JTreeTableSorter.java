/*
 * JTreeTableSorter.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on March 6, 2001, 1:57 PM
 */
package pcgen.gui.utils;

import pcgen.util.Logging;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.14 $
 */
public final class JTreeTableSorter
{
	private AbstractTreeTableModel tableModel;
	private JTreeTable tableView;
	private PObjectNode root;
	private boolean isAscending;
	private int prevAscending = 0;
	private int prevCol = 0;

	/**
	 * Constructor
	 * @param table
	 * @param obj
	 * @param model
	 */
	public JTreeTableSorter(JTreeTable table, PObjectNode obj, AbstractTreeTableModel model)
	{
		tableModel = model;

		final AbstractTreeTableModel tmodel = model;
		root = obj;
		tableView = table;
		table.setColumnSelectionAllowed(false);

		MouseAdapter listMouseListener = new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					final TableColumnModel columnModel = tableView.getColumnModel();
					final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
					final int column = tableView.convertColumnIndexToModel(viewColumn);

					if ((e.getClickCount() == 1) && (column > -1))
					{
						if (tmodel.getColumnClass(column).isAssignableFrom(Integer.class))
						{
							// JTreeTableSorter.this.mode = 1;
						}
						else if (tmodel.getColumnClass(column).isAssignableFrom(Float.class))
						{
							// JTreeTableSorter.this.mode = 2;
						}
						else
						{
							// JTreeTableSorter.this.mode = 0;
						}

						sortNodeOnColumn(JTreeTableSorter.this.root, column, e.getModifiers() & InputEvent.SHIFT_MASK);
						JTreeTableSorter.this.prevCol = column;
						JTreeTableSorter.this.prevAscending = e.getModifiers() & InputEvent.SHIFT_MASK;
						updateSortModel();
					}
				}
			};

		JTableHeader th = table.getTableHeader();
		th.addMouseListener(listMouseListener);
	}

	/**
	 * Set the root object
	 * @param obj
	 */
	public void setRoot(PObjectNode obj)
	{
		root = obj;
	}

	/**
	 * Sort the node on the column 
	 * @return a PObject
	 */
	public PObjectNode sortNodeOnColumn()
	{
		return sortNodeOnColumn(root, prevCol, prevAscending);
	}

	private PObjectNode sortNodeOnColumn(PObjectNode node, int col, int ascending)
	{
		List master = node.getChildren();

		if (master == null)
		{
			return node;
		}

		// Inefficient!  XXX
		List itemsToSort = new ArrayList(master.size());

		for (int i = 0; i < master.size(); i++)
		{
			PObjectNode pi = (PObjectNode) master.get(i);
			List sortItem = new ArrayList(2);

			sortItem.add(pi);

			Object pir = null;

			try
			{
				pir = tableModel.getValueAt(pi, col);
			}
			catch (Exception exc)
			{
				Logging.errorPrint("", exc);
			}

			if (pir == null)
			{
				continue;
			}

			if (pir instanceof String)
			{
				// color coding is done before a pipe |, ignore that for sorting purposes.
				pir = pir.toString().substring(pir.toString().lastIndexOf("|") + 1);
			}

			sortItem.add(pir);
			itemsToSort.add(sortItem);
		}

		isAscending = ascending == 0;
		Collections.sort(itemsToSort,
		    new Comparator()
			{
				public int compare(Object obj1, Object obj2)
				{
					final Object o1 = ((ArrayList) obj1).get(1);
					final Object o2 = ((ArrayList) obj2).get(1);
					int iRet = -1;

					//Globals.debugPrint("obj1:" + o1.getClass().getName() + ":" + o1 + "  obj2:" + o2.getClass().getName() + ":" + o2);
					if (o1 instanceof Integer)
					{
						iRet = ((Integer) o1).compareTo((Integer) o2);
					}
					else if (o1 instanceof String)
					{
						String s1 = stripHTML((String)o1);
						String s2 = stripHTML((String)o2);
						iRet = s1.compareToIgnoreCase(s2);
					}
					else if (o1 instanceof Float)
					{
						iRet = ((Float) o1).compareTo((Float) o2);
					}
					else if (o1 instanceof BigDecimal)
					{
						iRet = ((BigDecimal) o1).compareTo((BigDecimal) o2);
					}
					else
					{
						Logging.errorPrint("JTreeTableSorter: unknown compare class: " + o1.getClass().getName());
					}

					if (!isAscending)
					{
						iRet *= -1;
					}

					return iRet;
				}

		        private String stripHTML(String string) {
					int index = string.indexOf('<');
					int endIndex = string.indexOf('>');
					while (index != -1 && endIndex != -1) {
						string = string.substring(0,index) + string.substring(endIndex+1);
						index = string.indexOf('<');
						endIndex = string.indexOf('>');
					}
					return string;
		        }
			});

		for (int i = 0; i < itemsToSort.size(); i++)
		{
			final List sortItem = (ArrayList) itemsToSort.get(i);
			master.set(i, sortItem.get(0));
		}

		for (int i = 0; i < master.size(); i++)
		{
			PObjectNode nodule = (PObjectNode) master.get(i);

			// if it doesn't have any children, continue
			if (nodule.isLeaf())
			{
				continue;
			}

			// else sort its children
			sortNodeOnColumn(nodule, col, ascending);
		}

		return node;
	}

	private void updateSortModel()
	{
		List pathList = tableView.getExpandedPaths();
		tableModel.updateTree();
		tableView.updateUI();
		tableView.expandPathList(pathList);
	}
}
