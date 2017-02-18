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
 */
package pcgen.gui2.tools; // hm.binkley.gui;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jetbrains.annotations.Contract;

/**
 * {@code Utilities}.
 *
 *
 * @see SwingConstants
 */
public final class Utilities
{

    /** Up direction. */
    public static final int UP = 14;
    /** Beginning (far left) direction. */
    public static final int BEGINNING = 16;
    /** End (far right) direction. */
    public static final int END = 17;
    /** Icons for New item. */
    static final ImageIcon NEW_ICON = Icons.New16.getImageIcon();
    /** Icons for Close item. */
    static final ImageIcon CLOSE_ICON = Icons.Close16.getImageIcon();
    /** Icons for Locked item. */
    static final ImageIcon LOCK_ICON = Icons.Bookmarks16.getImageIcon();
    /** Icons for Up item. */
    static final ImageIcon UP_ICON = Icons.Up16.getImageIcon();
    /** Icons for Left item. */
    static final ImageIcon LEFT_ICON = Icons.Back16.getImageIcon();
    /** Icons for Down item. */
    static final ImageIcon DOWN_ICON = Icons.Down16.getImageIcon();
    /** Icons for Right item. */
    static final ImageIcon RIGHT_ICON = Icons.Forward16.getImageIcon();
    /** Icons for Top item. */
    static final ImageIcon TOP_ICON = Icons.UUp16.getImageIcon();
    /** Icons for Beginning item. */
    static final ImageIcon BEGINNING_ICON = Icons.BBack16.getImageIcon();
    /** Icons for Bottom item. */
    static final ImageIcon BOTTOM_ICON = Icons.DDown16.getImageIcon();
    /** Icons for End item. */
    static final ImageIcon END_ICON = Icons.FForward16.getImageIcon();

    private Utilities()
    {
    }

    /**
     * Work around bug in W32; it returns false even on right-mouse
     * clicks.
     *
     * @param e {@code MouseEvent}, the event
     *
     * @return {@code boolean}, the condition
     */
    @Contract(pure = true)
    static boolean isRightMouseButton(MouseEvent e)
    {
        return e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e);
    }

    /**
     * {@code isShiftLeftMouseButton} detects SHIFT-BUTTON1
     * events for flipping pane shortcuts.
     *
     * @param e {@code MouseEvent}, the event
     *
     * @return {@code boolean}, the condition
     */
    @Contract(pure = true)
    static boolean isShiftLeftMouseButton(InputEvent e)
    {
        return ((e.getModifiers() & InputEvent.BUTTON1_MASK) ==
                InputEvent.BUTTON1_MASK) && e.isShiftDown();
    }

}
