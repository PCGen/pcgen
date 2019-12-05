/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.gui2.prefs;

import javax.swing.JPanel;

/**
 * The abstract class {@code PCGenPrefsPanel} defines the
 * interface for a panel in the Preferences dialog.
 */
public abstract class PCGenPrefsPanel extends JPanel
{

    /**
     * Returns the title of the panel.
     */
    public abstract String getTitle();

    /**
     * Initializes the panel's values based on the current preferences.
     */
    public abstract void applyOptionValuesToControls();

    /**
     * Updates the current preferences based on the panel's values.
     */
    public abstract void setOptionsBasedOnControls();

    @Override
    public String toString()
    {
        return getTitle();
    }
}
