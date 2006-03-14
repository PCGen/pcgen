/*
 * ResizeColumnListener.java
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
 * Created on Mar 21, 2002 5:45 PM
 */
package pcgen.gui.utils;

import pcgen.core.Globals;

import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author  jaymecox@netscape.net
 * @version    $Revision$
 */
public final class ResizeColumnListener implements PropertyChangeListener
{
	private JTableEx table;
	private String colFrom;
	private boolean colChange = false;
	private boolean mouseIsDown = true;
	private int colIndex;

	public ResizeColumnListener(JTableEx fromTable, String fromString, int fromIndex)
	{
		table = fromTable;
		colFrom = fromString;
		colIndex = fromIndex;
		mouseIsDown = false;
		colChange = false;
		table.getTableHeader().addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					mouseIsDown = true;
				}

				public void mouseReleased(MouseEvent evt)
				{
					mouseIsDown = false;

					if (colChange)
					{
						TableColumn col = table.getColumnModel().getColumn(colIndex);
						int colWidth = col.getWidth();
						Globals.setCustColumnWidth(colFrom, colIndex, colWidth);
						colChange = false;
					}
				}
			});
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals("width") && mouseIsDown)
		{
			colChange = true;
		}
	}
}
