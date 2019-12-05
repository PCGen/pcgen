/*
 * Copyright 2002-2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 * USA
 */
package pcgen.gui2.tools;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Convenience methods from various sources.
 */
public final class Utility
{

    private static final KeyStroke ESCAPE_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    /**
     * An action map key for the user requesting a dialog close via the ESC key.
     */
    private static final String DISPATCH_WINDOW_CLOSING_ACTION_MAP_KEY = "pcgen:WINDOW_CLOSING"; //$NON-NLS-1$

    private Utility()
    {
    }

    /**
     * Set up GridBag Constraints.
     *
     * @param gbc The gridbagconstraints to set up
     * @param gx  cols from left (left-most col for multi-column cell)
     * @param gy  rows from top (top-most row for multi-row cell)
     * @param gw  cols wide
     * @param gh  rows high
     * @param wx  weight of x, I typically put in percentile,
     *            only need to specify this once for each column, other values in same column are 0.0
     * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
     */
    public static void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
    {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }

    /**
     * Set up GridBag Constraints.
     *
     * @param gbc    The gridbagconstraints to set up
     * @param gx     cols from left (left-most col for multi-column cell)
     * @param gy     rows from top (top-most row for multi-row cell)
     * @param gw     cols wide
     * @param gh     rows high
     * @param wx     weight of x, I typically put in percentile, only need to specify this once for each column,
     *               other values in same column are 0.0
     * @param wy     weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
     * @param fill   How should the component be resized if smaller than the space.
     * @param anchor Where should the component be placed if smaller than the space.
     */
    public static void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy,
            int fill, int anchor)
    {
        buildConstraints(gbc, gx, gy, gw, gh, wx, wy);
        gbc.fill = fill;
        gbc.anchor = anchor;
    }

    /**
     * Set up GridBag Constraints in a relative pattern. Components must be
     * added in order row by row.
     *
     * @param gbc    The gridbagconstraints to set up
     * @param gw     cols wide
     * @param gh     rows high
     * @param wx     weight of x, I typically put in percentile, only need to specify this once for each column,
     *               other values in same column are 0.0
     * @param wy     weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
     * @param fill   How should the component be resized if smaller than the space.
     * @param anchor Where should the component be placed if smaller than the space.
     */
    public static void buildRelativeConstraints(GridBagConstraints gbc, int gw, int gh, double wx, double wy, int fill,
            int anchor)
    {
        buildConstraints(gbc, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, gw, gh, wx, wy);
        gbc.fill = fill;
        gbc.anchor = anchor;
    }

    /**
     * Set up GridBag Constraints in a relative pattern. Components must be
     * added in order row by row.
     *
     * @param gbc The gridbagconstraints to set up
     * @param gw  cols wide
     * @param gh  rows high
     * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column,
     *            other values in same column are 0.0
     * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
     */
    public static void buildRelativeConstraints(GridBagConstraints gbc, int gw, int gh, double wx, double wy)
    {
        buildConstraints(gbc, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, gw, gh, wx, wy);
    }


    /**
     * Add a keyboard shortcut to allow ESC to close the dialog.
     *
     * @param dialog The dialog to be updated.
     */
    public static void installEscapeCloseOperation(final JDialog dialog)
    {
        JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESCAPE_STROKE, DISPATCH_WINDOW_CLOSING_ACTION_MAP_KEY);
        Action dispatchClosing = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        };
        root.getActionMap().put(DISPATCH_WINDOW_CLOSING_ACTION_MAP_KEY, dispatchClosing);
    }

    /**
     * Adjust the crop rectangle to fit within the image it is cropping. Also
     * ensure the area is square.
     *
     * @param image    The image being cropped
     * @param cropRect The rectangle defining the cropping area. This may be updated.
     */
    public static void adjustRectToFitImage(RenderedImage image, Rectangle cropRect)
    {
        // Make sure the rectangle is not too big
        if (cropRect.width > image.getWidth())
        {
            cropRect.width = image.getWidth();
        }
        if (cropRect.height > image.getHeight())
        {
            cropRect.height = image.getHeight();
        }

        // Make it square
        int dimension = Math.min(cropRect.width, cropRect.height);
        cropRect.setSize(dimension, dimension);

        // Now adjust the origin point so the box is within the image
        if ((cropRect.x + cropRect.width) > image.getWidth())
        {
            cropRect.x = image.getWidth() - cropRect.width;
        }
        if ((cropRect.y + cropRect.height) > image.getHeight())
        {
            cropRect.y = image.getHeight() - cropRect.height;
        }
    }

    /**
     * Trim a string from the left to fit within the specified width.
     *
     * @param fm       The font the text will be rendered in.
     * @param str      The string to trimmed.
     * @param maxWidth The maximum width that the string is allowed to be.
     * @return String The trimmed string.
     */
    public static String shortenString(FontMetrics fm, String str, int maxWidth)
    {
        for (int i = str.length();i > 0;i -= 5)
        {
            String shortedString = "..." + str.substring(str.length() - i);

            int width = fm.stringWidth(shortedString);
            //System.out.println("testing '"+foo+"' = "+width);
            if (width < maxWidth)
            {
                return shortedString;
            }
        }
        return "";
    }

    /**
     * Get the tabbed pane for a component
     *
     * @param c
     * @return the tabbed pane for a component
     */
    @Contract("null -> null")
    public static @Nullable JTabbedPane getTabbedPaneFor(Component c)
    {
        if (c == null)
        {
            return null;
        }
        if (c instanceof JTabbedPane)
        {
            return (JTabbedPane) c;
        }
        return getTabbedPaneFor(c.getParent());
    }

    /**
     * {@code isShiftLeftMouseButton} detects SHIFT-BUTTON1
     * events for flipping pane shortcuts.
     *
     * @param e {@code MouseEvent}, the event
     * @return {@code boolean}, the condition
     */
    @Contract(pure = true)
    static boolean isShiftLeftMouseButton(InputEvent e)
    {
        return ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) && e.isShiftDown();
    }
}
