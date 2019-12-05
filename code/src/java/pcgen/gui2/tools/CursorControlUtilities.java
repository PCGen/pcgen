/*
 * Copyright (c) 2006, 2009.
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
 *
 */
package pcgen.gui2.tools;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

public final class CursorControlUtilities
{

    private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

    private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    private static final MouseAdapter CLICK_CONSUMER = new MouseAdapter()
    {
        // Empty class (designed to consume all clicks with no effect)
    };

    private CursorControlUtilities()
    {
        // Utility class cannot be instantiated
    }

    public static void startWaitCursor(JComponent component)
    {
        RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
        root.getGlassPane().setCursor(WAIT_CURSOR);
        root.getGlassPane().addMouseListener(CLICK_CONSUMER);
        root.getGlassPane().setVisible(true);
        root.getRootPane().validate();
    }

    public static void stopWaitCursor(JComponent component)
    {
        RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
        root.getGlassPane().setCursor(DEFAULT_CURSOR);
        root.getGlassPane().removeMouseListener(CLICK_CONSUMER);
        root.getGlassPane().setVisible(false);
        root.getRootPane().validate();
    }
}
