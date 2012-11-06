/*
 * Created on Sep 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pcgen.gui2.util;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * The Class <code>ScrollablePanel</code> provides a panel which can be 
 * dynamically built up and displayed in a JScrollPane.  
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author Devon Jones
 * @version $Revision$
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getPreferredSize()
	{
		Dimension preferredSize = super.getPreferredSize();
		// Add a bit of padding to account for squashing in the Java LAF
		preferredSize.height += 20;
		return preferredSize;
	}

}
