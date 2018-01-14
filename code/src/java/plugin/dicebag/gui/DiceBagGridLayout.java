/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.dicebag.gui;

import java.awt.Container;
import java.awt.GridLayout;

/**
 * <p>This is an extended grid bag layout manager which extends/reduces the grid
 * size based on a specified minimum/maximum width or height.</p>
 * <p>The caller can specify whether the manager should give manage by columns,
 * in which case the min/max values represent minimum and maximum column widths, or by
 * rows, in which case the values represent column heights.</p>
 */
public class DiceBagGridLayout extends GridLayout
{
	/**
	 * <p>Indicates management by column width.</p>
	 */
	public static final int MANAGE_BY_COLUMNS = 1;

	/**
	 * <p>Indicates management by row height.</p>
	 */
	public static final int MANAGE_BY_ROWS = 0;

	/**
	 * <p>Indicates what to manage by (rows or columns).</p>
	 */
	private int m_manageBy = 1;

	/**
	 * <p>Maximum size values.</p>
	 */
	private int m_maxSize = 0;

	/**
	 * <p>Minimum size value.</p>
	 */
	private int m_minSize = 0;

	/**
	 * <p>Default constructor.  Uses a default of {@code MANAGE_BY_ROWS}, a minimum
	 * size of 50, and a maximum size of 200.</p>
	 *
	 * @see java.awt.GridLayout#GridLayout()
	 */
	public DiceBagGridLayout()
	{
		super();
		m_manageBy = MANAGE_BY_ROWS;
		m_minSize = 50;
		m_maxSize = 200;
	}

	/**
	 * <p>Initializes the object with the specified data.</p>
	 *
	 * @param rows     Initial number of rows.
	 * @param cols     Initial number of columns.
	 * @param manageBy Either {@code MANAGE_BY_ROWS} or <code>MANAGE_BY_COLUMNS</code>.
	 * @param minSize  Minimum size, expressed in pixels.
	 * @param maxSize  Maximum size, expressed in pixels.
	 *
	 * @see java.awt.GridLayout#GridLayout(int rows, int cols)
	 */
	public DiceBagGridLayout(int rows, int cols, int manageBy, int minSize,
		int maxSize)
	{
		super(rows, cols);
		m_manageBy = manageBy;
		m_minSize = minSize;
		m_maxSize = maxSize;
	}

	/**
	 * <p>Initializes the object with the specified data.</p>
	 *
	 * @param rows     Initial number of rows.
	 * @param cols     Initial number of columns.
	 * @param hgap     Horizontal gap
	 * @param vgap     Vertical gap
	 * @param manageBy Either {@code MANAGE_BY_ROWS} or <code>MANAGE_BY_COLUMNS</code>.
	 * @param minSize  Minimum size, expressed in pixels.
	 * @param maxSize  Maximum size, expressed in pixels.
	 *
	 * @see java.awt.GridLayout#GridLayout(int rows, int cols, int hgap, int vgap)
	 */
	public DiceBagGridLayout(int rows, int cols, int hgap, int vgap,
		int manageBy, int minSize, int maxSize)
	{
		super(rows, cols, hgap, vgap);
		m_manageBy = manageBy;
		m_minSize = minSize;
		m_maxSize = maxSize;
	}

	/**
	 * <p>This method computes the correct number of rows or columns based
	 * on the current size of the {@code parent} and the <code>m_manageBy</code>
	 * value, using an algorithm similar to {@code getMinimumLayoutSize()}.  It then
	 * sets the new number of rows or columns and calls the {@code super}'s implementation.</p>
	 *
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 *
	 * @param parent Container -- parent.
	 */
    @Override
	public void layoutContainer(Container parent)
	{
		if (m_manageBy == MANAGE_BY_COLUMNS)
		{
			int minWCols =
					(int) Math.floor((parent.getWidth() - getHgap()
						- parent.getInsets().left - parent.getInsets().right)
						/ (m_minSize + getHgap()));
			int maxWCols =
					(int) Math.floor((parent.getWidth() - getHgap()
						- parent.getInsets().left - parent.getInsets().right)
						/ (m_maxSize + getHgap()));

			if ((minWCols < getColumns()) && (minWCols > 0))
			{
				setColumns(minWCols);
			}
			else if ((maxWCols > getColumns())
				&& (maxWCols <= parent.getComponentCount()))
			{
				setColumns(maxWCols);
			}
		}
		else if (m_manageBy == MANAGE_BY_ROWS)
		{
			int minWRows =
					(int) Math.floor((parent.getHeight() - getVgap()
						- parent.getInsets().top - parent.getInsets().bottom)
						/ (m_minSize + getVgap()));
			int maxWRows =
					(int) Math.floor((parent.getHeight() - getVgap()
						- parent.getInsets().top - parent.getInsets().bottom)
						/ (m_maxSize + getVgap()));

			if ((minWRows < getRows()) && (minWRows > 0))
			{
				setRows(minWRows);
			}
			else if ((maxWRows > getRows())
				&& (maxWRows <= parent.getComponentCount()))
			{
				setRows(maxWRows);
			}
		}

		super.layoutContainer(parent);
	}
}
