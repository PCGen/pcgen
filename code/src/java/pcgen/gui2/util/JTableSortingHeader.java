/*
 * JTableSortingHeader.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on Feb 16, 2008, 8:27:21 PM
 */
package pcgen.gui2.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pcgen.gui2.tools.Icons;

/**
 *
 * @author Connor Petty<mistercpp2000@gmail.com>
 */
public class JTableSortingHeader extends JTableHeader implements MouseListener
{

	private static final long serialVersionUID = -2459707147524421794L;
	private static final Icon ASCENDING_ICON = Icons.Down16.getImageIcon();
	private static final Icon DESCENDING_ICON = Icons.Up16.getImageIcon();
	private static final ButtonModel defaultModel = new DefaultButtonModel();
	private final ButtonModel usedModel = new DefaultButtonModel();

	public JTableSortingHeader(JTableEx table)
	{
		super(table.getColumnModel());
		this.table = table;
		addMouseListener(this);
	}

	@Override
	protected TableCellRenderer createDefaultRenderer()
	{
		return new SortingHeaderRenderer();
	}

	@Override
	public JTableEx getTable()
	{
		return (JTableEx) super.getTable();
	}

	public TableColumn getTrackedColumn()
	{
		TableColumnModel model = getColumnModel();
		Point mousepos = getMousePosition();
		if (mousepos != null)
		{
			return model.getColumn(model.getColumnIndexAtX(mousepos.x));
		}
		return null;
	}

	public class SortingHeaderRenderer extends JButton implements TableCellRenderer
	{

		public SortingHeaderRenderer()
		{
			setHorizontalTextPosition(LEADING);
			this.setMargin(new Insets(0, 0, 0, 0));
		}

		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean isSelected,
													   boolean hasFocus,
													   int row,
													   int column)
		{
			TableColumn draggedColumn = getDraggedColumn();
			if (draggedColumn != null && draggedColumn.getHeaderValue() == value)
			{
				setModel(usedModel);
			}
			else
			{
				setModel(defaultModel);
			}
			Icon icon = null;
			TableColumn currentColumn = table.getColumn(value);
			List<? extends SortingPriority> list = getTable().getSortingPriority();
			if (!list.isEmpty())
			{
				SortingPriority order = list.get(0);
				if (order.getColumn() == currentColumn.getModelIndex())
				{
					switch (order.getMode())
					{
						case ASCENDING:
							icon = ASCENDING_ICON;
							break;
						case DESCENDING:
							icon = DESCENDING_ICON;
							break;
					}
				}
			}
			setIcon(icon);
			setText(value.toString());
			return this;
		}

	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e) && getCursor() == Cursor.getDefaultCursor()
				&& getTrackedColumn() != null)
		{
			getTable().toggleSort(getTrackedColumn().getModelIndex());
			usedModel.setPressed(false);
			repaint();
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			usedModel.setPressed(true);
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			usedModel.setPressed(false);
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		usedModel.setRollover(true);
	}

	public void mouseExited(MouseEvent e)
	{
		usedModel.setRollover(false);
	}

}
