/*
 * JTreeTableMouseAdapter.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author  Aaron Divinsky <boomer70@yahoo.com>
 * Created on Oct 11, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.utils;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <code>JTreeTableMouseAdapter</code> handles mouse events for a JTreeTable.
 * A ClickHandler is called when a mouse event needs to be processed.
 *
 * @author  Aaron Divinsky <boomer70@yahoo.com>
 * @version $Revision$
 **/
public class JTreeTableMouseAdapter extends MouseAdapter
{
	private JTreeTable owner;
	private boolean allowControlClicks = false;
	private ClickHandler handler;

	/**
	 * Constructor
	 *
	 * @param anOwner The JTreeTable that this adapter is attached to
	 * @param aHandler A Handler to get click events in
	 * @param controlClicks Should control clicks do multiselecting
	 */
	public JTreeTableMouseAdapter(final JTreeTable anOwner,
		final ClickHandler aHandler, final boolean controlClicks)
	{
		owner = anOwner;
		handler = aHandler;
		allowControlClicks = controlClicks;
	}

	/**
	 * Handles processing of mouse events and dispatching them to the
	 * ClickHandler.
	 * @param e The MouseEvent
	 */
	public void mousePressed(MouseEvent e)
	{
		JTree atree = owner.getTree();
		final int selRow = atree.getClosestRowForLocation(e.getX(), e.getY());
		Rectangle bounds = atree.getRowBounds(selRow);
		if (!(e.getY() >= bounds.y && e.getY() <= (bounds.y + bounds.height)))
		{
			return;
		}

		if (selRow != -1)
		{
			final TreePath mlSelPath = atree.getPathForRow(selRow);
			if (mlSelPath == null)
			{
				return;
			}
			if (e.getClickCount() >= 1)
			{
				if (allowControlClicks && e.isControlDown())
				{
					if (atree.isPathSelected(mlSelPath))
					{
						atree.removeSelectionPath(mlSelPath);
					}
					else if (!atree.isPathSelected(mlSelPath))
					{
						atree.addSelectionPath(mlSelPath);
					}
				}
				else
				{
					atree.setSelectionPath(mlSelPath);
				}
				handler.singleClickEvent();
			}
			if (e.getClickCount() == 2)
			{
				PObjectNode po = (PObjectNode) mlSelPath.getLastPathComponent();
				Object item = po.getItem();
				if (!handler.isSelectable(item))
				{
					// This behaviour is handled by the default handler for
					// the tree.  If we try and handle it as well we will
					// cancel each other out.
					if (atree.getPathForLocation(e.getX(), e.getY()) == null)
					{
						// If this is a String we are sitting on a grouping
						// (i.e. a TYPE).  Make the children visible.
						if (atree.isExpanded(mlSelPath))
						{
							atree.collapsePath(mlSelPath);
						}
						else
						{
							atree.expandPath(mlSelPath);
						}
					}
				}
				else
				{
					handler.doubleClickEvent();
				}
			}
		}
	}
}
