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
package gmgen.gui; // hm.binkley.gui;

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
 * @version $Revision$
 *
 * @see SwingConstants
 */
final class Utilities implements SwingConstants
{

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
