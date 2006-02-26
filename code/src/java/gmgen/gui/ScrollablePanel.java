/*
 * Created on Sep 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gmgen.gui;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * @author djones4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScrollablePanel extends JPanel implements Scrollable {
	int scrollNum;
	//
	// Scrollable methods
	//
	
	/**
	 * Constructor
	 * @param scrollNum
	 */
	public ScrollablePanel(int scrollNum) {
		super();
		this.scrollNum = scrollNum;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * Returns height of a row
	 * @param visibleRect
	 * @param orientation
	 * @param direction
	 * @return int
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return scrollNum;
	}

	/**
	 * returns the height of the visible rect (so it scrolls by one screenfull).
	 * @param visibleRect
	 * @param orientation
	 * @param direction
	 * @return int
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return scrollNum;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
