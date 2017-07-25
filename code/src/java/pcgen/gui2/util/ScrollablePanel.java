/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

/*
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pcgen.gui2.util;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * The Class {@code ScrollablePanel} provides a panel which can be
 * dynamically built up and displayed in a JScrollPane.  
 *
 * 
 */
public class ScrollablePanel extends JPanel implements Scrollable
{
	int scrollNum;

	//
	// Scrollable methods
	//

	/**
	 * Constructor
	 * @param scrollNum
	 */
	public ScrollablePanel(int scrollNum)
	{
		super();
		this.scrollNum = scrollNum;
	}

    @Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	/**
	 * Returns height of a row
	 * @param visibleRect
	 * @param orientation
	 * @param direction
	 * @return int
	 */
    @Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
		int orientation, int direction)
	{
		return scrollNum;
	}

	/**
	 * returns the height of the visible rect (so it scrolls by one screenfull).
	 * @param visibleRect
	 * @param orientation
	 * @param direction
	 * @return int
	 */
    @Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
		int orientation, int direction)
	{
		return scrollNum;
	}

    @Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

    @Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension preferredSize = super.getPreferredSize();
		// Add a bit of padding to account for squashing in the Java LAF
		preferredSize.height += 20;
		return preferredSize;
	}

}
