/*
 * Utilities.java
 *
 * Copyright 2002, 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Created on August 18th, 2002.
 */
package pcgen.gui.panes; // hm.binkley.gui;

import pcgen.gui.utils.IconUtilitities;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * <code>Utilities</code>.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1.17 $
 *
 * @see SwingConstants
 */
final class Utilities implements SwingConstants
{
	/** Up direction. */
	public static final int UP = 14;

	/** Down direction. */
	public static final int DOWN = 15;

	/** Beginning (far left) direction. */
	public static final int BEGINNING = 16;

	/** End (far right) direction. */
	public static final int END = 17;

	/** Icon for New item. */
	public static final ImageIcon NEW_ICON = getImageIcon("resources/New16.gif");

	/** Icon for Close item. */
	public static final ImageIcon CLOSE_ICON = getImageIcon("resources/Close16.gif");

	/** Icon for Center item. */
	public static final ImageIcon CENTER_ICON = getImageIcon("resources/Stop16.gif");

	/** Icon for Flip item. */
	public static final ImageIcon FLIP_ICON = getImageIcon("resources/Refresh16.gif");

	/** Icon for Reset item. */
	public static final ImageIcon RESET_ICON = getImageIcon("resources/Redo16.gif");

	/** Icon for Locked item. */
	public static final ImageIcon LOCK_ICON = getImageIcon("resources/Bookmarks16.gif");

	/** Icon for Join item. */
	public static final ImageIcon JOIN_ICON = getImageIcon("resources/Pause16.gif");

	/** Icon for Up item. */
	public static final ImageIcon UP_ICON = getImageIcon("resources/Up16.gif");

	/** Icon for Left item. */
	public static final ImageIcon LEFT_ICON = getImageIcon("resources/Back16.gif");

	/** Icon for Down item. */
	public static final ImageIcon DOWN_ICON = getImageIcon("resources/Down16.gif");

	/** Icon for Right item. */
	public static final ImageIcon RIGHT_ICON = getImageIcon("resources/Forward16.gif");

	/** Icon for Top item. */
	public static final ImageIcon TOP_ICON = getImageIcon("resources/UUp16.gif");

	/** Icon for Beginning item. */
	public static final ImageIcon BEGINNING_ICON = getImageIcon("resources/BBack16.gif");

	/** Icon for Bottom item. */
	public static final ImageIcon BOTTOM_ICON = getImageIcon("resources/DDown16.gif");

	/** Icon for End item. */
	public static final ImageIcon END_ICON = getImageIcon("resources/FForward16.gif");

	private Utilities()
	{
		super();
	}

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location.
	 *
	 * @param location <code>String</code>, the path to the
	 * <code>IconImage> source
	 *
	 * @return <code>ImageIcon</code>, the icon or <code>null</code>
	 * on failure
	 */
	static ImageIcon getImageIcon(String location)
	{
		return getImageIcon(location, null);
	}

	/**
	 * Work around bug in W32; it returns false even on right-mouse
	 * clicks.
	 *
	 * @param e <code>MouseEvent</code>, the event
	 *
	 * @return <code>boolean</code>, the condition
	 */
	static boolean isRightMouseButton(MouseEvent e)
	{
		return e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e);
	}

	/**
	 * <code>isShiftLeftMouseButton</code> detects SHIFT-BUTTON1
	 * events for flipping pane shortcuts.
	 *
	 * @param e <code>MouseEvent</code>, the event
	 *
	 * @return <code>boolean</code>, the condition
	 */
	static boolean isShiftLeftMouseButton(MouseEvent e)
	{
		return ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) && e.isShiftDown();
	}

	/**
	 * Fetch an <code>ImageIcon</code> relative to the calling
	 * location and using a description.
	 *
	 * @param location <code>String</code>, the path to the
	 * <code>IconImage> source
	 * @param description <code>String</code>, the description
	 *
	 * @return <code>ImageIcon</code>, the icon or <code>null</code>
	 * on failure
	 */
	private static ImageIcon getImageIcon(String location, String description)
	{
		String prefix = "resources/";

		if (location.startsWith(prefix))
		{
			location = location.substring(prefix.length());
		}

		return IconUtilitities.getImageIcon(location, description);
	}
}
