/*
 * JTableEx.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on June 27, 2001, 20:36 PM
 */
package pcgen.gui.utils;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

/**
 *  <code>JTableEx</code> extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision$
 */
public class JTableEx extends JTable
{
	static final long serialVersionUID = 514835142307946415L;

	/**
	 * Constructor
	 */
	public JTableEx()
	{
		this(null, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 */
	public JTableEx(TableModel tm)
	{
		this(tm, null, null);
	}

	/**
	 * Constructor
	 * @param tm
	 * @param tcm
	 */
	public JTableEx(TableModel tm, TableColumnModel tcm)
	{
		this(tm, tcm, null);
	}

	private JTableEx(TableModel tm, TableColumnModel tcm, ListSelectionModel lsm)
	{
		super(tm, tcm, lsm);

		setDefaultRenderer(BigDecimal.class, new AlignCellRenderer(
			SwingConstants.RIGHT));
		setDefaultRenderer(Float.class, new AlignCellRenderer(
			SwingConstants.RIGHT));
		setDefaultRenderer(Integer.class, new AlignCellRenderer(
			SwingConstants.RIGHT));
	}

	/**
	 * Calculate 'optimal' width (for example, minimum to show full text)
	 * for the columns in the columns list
	 * 
	 * @param columns
	 **/
	public final void setOptimalColumnWidths(int[] columns)
	{
		final JTableHeader header = getTableHeader();
		final TableCellRenderer defaultHeaderRenderer =
				((header != null) ? header.getDefaultRenderer() : null);
		final TableColumnModel aColumnModel = getColumnModel();

		if (aColumnModel == null)
		{
			return;
		}

		final int columncount = aColumnModel.getColumnCount();

		if ((columns.length <= 0) || (columncount < columns.length)
			|| (columncount < columns[columns.length - 1]))
		{
			Logging
				.errorPrint("Bad parameters passed to setOptimalColumnWidth.");

			return;
		}

		final TableModel data = getModel();

		final int rowCount = data.getRowCount();
		int totalWidth = 0;

		for (int i = 0; i < columns.length; i++)
		{
			try
			{
				final TableColumn column = aColumnModel.getColumn(columns[i]);

				if (column == null)
				{
					continue;
				}

				final int columnIndex = column.getModelIndex();
				int width = -1;

				//
				// Get the width of the header cell
				//
				TableCellRenderer h = column.getHeaderRenderer();

				if (h == null)
				{
					h = defaultHeaderRenderer;
				}

				if (h != null) // Not explicitly impossible
				{
					final Object value = column.getHeaderValue();

					if (value != null)
					{
						final Component c =
								h.getTableCellRendererComponent(this, value,
									false, false, -1, i);

						if (c != null)
						{
							width = c.getPreferredSize().width;
						}
					}
				}

				//
				// Cycle through entire column to get the largest cell
				//
				TableCellRenderer r = column.getCellRenderer();

				if (r == null)
				{
					r =
							this.getDefaultRenderer(data
								.getColumnClass(columnIndex));
				}

				if (r != null)
				{
					for (int row = rowCount - 1; row >= 0; --row)
					{
						final Object value = data.getValueAt(row, columnIndex);

						if (value != null)
						{
							final Component c =
									r.getTableCellRendererComponent(this,
										value, false, false, row, columnIndex);

							if (c != null)
							{
								width =
										Math.max(width,
											c.getPreferredSize().width);
							}
						}
					}
				}

				if (width >= 0)
				{
					column.setPreferredWidth(width + 5); //It seems to get it just a bit too small.
				}

				totalWidth += column.getPreferredWidth();
			}
			catch (Exception e)
			{
				Logging.errorPrint("Exception JTableEx.setOptimalColumnWidths:"
					+ i + ":" + columns.length + ":" + columncount
					+ Constants.LINE_SEPARATOR + "Exception type:"
					+ e.getClass().getName() + Constants.LINE_SEPARATOR
					+ "Message:" + e.getMessage());
			}
		}

		totalWidth += (columncount * aColumnModel.getColumnMargin());

		final Dimension size = getPreferredScrollableViewportSize();
		size.width = totalWidth;

		setPreferredScrollableViewportSize(size);
		sizeColumnsToFit(-1);

		if (header != null)
		{
			header.repaint();
		}
	}

    @Override
	public final String getToolTipText(MouseEvent event)
	{
		if (SettingsHandler.isToolTipTextShown())
		{
			final int row = rowAtPoint(event.getPoint());
			final int col = columnAtPoint(event.getPoint());

			//Did we get the event from something that was *over* the table (e.g. a listbox's menu)?
			if ((row < 0) || (col < 0))
			{
				return null;
			}

			final Object o = getValueAt(row, col);

			if ((o == null) || "".equals(o.toString()))
			{
				return null;
			}
			return wrap(o.toString());
		}
		return null;
	}

	/**
	 * set horizontal alignment of column
	 * and attach a new cell renderer
	 * @param col
	 * @param alignment
	 **/
	public void setColAlign(int col, int alignment)
	{
		getColumnModel().getColumn(col).setCellRenderer(
			new AlignCellRenderer(alignment));
	}

	/*
	 * fixes a bug which caused the
	 * JTableHeaderUI not to be updated
	 * correctly on initialization
	 *
	 * author: Thomas Behr 13-03-02
	 */
    @Override
	public void updateUI()
	{
		super.updateUI();
		getTableHeader().updateUI();
	}

	/**
	 * If text is longer than 20 chars, show t
	 * @param argText
	 * @return String
	 **/
	private static String wrap(String argText)
	{
		String text = argText.substring(argText.lastIndexOf("|") + 1);
		int textLength = text.length();
		StringBuffer wrapped = new StringBuffer(textLength);
		final int length = 70;

		while (textLength > length)
		{
			// XXX correct the line below for Linux
			if (text.indexOf('\\') >= 0)
			{
				break;
			}

			int pos;
			int lastBreak = -1;
			boolean bInHtmlTag = false;
			int displayedCount = 0;

			for (pos = 0; pos < textLength; pos++)
			{
				if (displayedCount >= length)
				{
					break;
				}

				switch (text.charAt(pos))
				{
					case ' ':

						if (!bInHtmlTag)
						{
							lastBreak = pos;
							displayedCount += 1;
						}

						break;

					case '<':
						bInHtmlTag = true;

						break;

					case '>':
						bInHtmlTag = false;

						break;

					default:

						if (!bInHtmlTag)
						{
							displayedCount += 1;
						}

						break;
				}
			}

			if (displayedCount < length)
			{
				lastBreak = textLength;
			}

			if (lastBreak == -1)
			{
				lastBreak = length;
			}

			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}

			wrapped.append(text.substring(0, lastBreak));
			text = text.substring(lastBreak).trim();
			textLength = text.length();
		}

		if (text.length() != 0)
		{
			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}

			wrapped.append(text);
		}

		if (!wrapped.toString().startsWith("<html>"))
		{
			wrapped.insert(0, "<html>");
			wrapped.append("</html>");
		}

		return wrapped.toString();
	}

	/**
	 * Align the cell text in a column
	 **/
	public static final class AlignCellRenderer extends
			DefaultTableCellRenderer
	{
		/**
		 * align is one of:
		 * SwingConstants.LEFT
		 * SwingConstants.CENTER
		 * SwingConstants.RIGHT
		 **/
		private int align = SwingConstants.LEFT;

		/**
		 * Align the cell renderer
		 * @param anInt
		 */
		public AlignCellRenderer(int anInt)
		{
			super();
			align = anInt;
			setHorizontalAlignment(align);
		}

        @Override
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
			setEnabled((table == null) || table.isEnabled());

			setHorizontalAlignment(align);

			return this;
		}
	}
}
