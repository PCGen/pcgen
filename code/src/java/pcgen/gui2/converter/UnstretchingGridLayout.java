/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.converter;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UnstretchingGridLayout extends GridLayout
{
	private transient Lock instanceLock = new ReentrantLock();

	public UnstretchingGridLayout(int arg0, int arg1)
	{
		super(arg0, arg1);
	}

	@Override
	public void layoutContainer(Container parent)
	{
		try
		{
			instanceLock.lock();

			int componentCount = parent.getComponentCount();
			if (componentCount == 0)
			{
				return;
			}

			int rowCount = getRows();
			int columnCount = getColumns();
			if (rowCount > 0)
			{
				columnCount = ((componentCount + rowCount) - 1) / rowCount;
			}
			else
			{
				rowCount = ((componentCount + columnCount) - 1) / columnCount;
			}

			/*
			 * First loop is to determine the maximum row height and column
			 * width necessary for proper layout.
			 */
			int[] rowHeight = new int[rowCount];
			int[] columnWidth = new int[columnCount];
			for (int i = 0; i < componentCount; i++)
			{
				int row = i / columnCount;
				int column = i % columnCount;
				Component component = parent.getComponent(i);
				Dimension d = component.getPreferredSize();
				rowHeight[row] = Math.max(rowHeight[row], d.height);
				columnWidth[column] = Math.max(columnWidth[column], d.width);
			}
			// Avoid fetching the orientation & gaps/gutters in the loop
			boolean leftToRight = parent.getComponentOrientation().isLeftToRight();
			int xGutter = getHgap();
			int yGutter = getVgap();

			Insets insets = parent.getInsets();
			int yLoc = insets.top;
			/*
			 * This loop actually does the placement. Note that it seems easier
			 * to do this with two loops (one for rows and one for columns) in
			 * order to simplify the calculation of the x and y location of the
			 * component. Is there an algorithm that would obliviate the need
			 * for the gate on componentCount and can avoid having to sum
			 * multiple values from the row* and column* arrays?
			 */
			for (int row = 0; row < rowHeight.length; row++)
			{
				int xLoc = insets.left;
				for (int column = 0; column < columnCount; column++)
				{
					int i = (row * columnCount) + column;
					if (i < componentCount)
					{
						Component component = parent.getComponent(i);
						Dimension d = component.getPreferredSize();
						int x = leftToRight ? xLoc : (xLoc - d.width);
						component.setBounds(x, yLoc, d.width, d.height);
					}
					if (leftToRight)
					{
						xLoc += columnWidth[column] + xGutter;
					}
					else
					{
						xLoc -= columnWidth[column] + xGutter;
					}
				}
				yLoc += rowHeight[row] + yGutter;
			}
		} finally
		{
			instanceLock.unlock();
		}
	}
}
