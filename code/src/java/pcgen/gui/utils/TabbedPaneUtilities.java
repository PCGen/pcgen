/*
 * Copyright (c) 2005 Tom Parker thpr@sourceforge.net
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
 *
 * Created on Jun 10, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.gui.utils;

import javax.swing.JTabbedPane;
import java.awt.Component;

/**
 * @author Thomas Parker <thpr@sourceforge.net>
 *
 * This is a utility class, serving as the location for static methods for use
 * with JTabbedPanes.
 */
public final class TabbedPaneUtilities
{
    private TabbedPaneUtilities()
    {
        //prevent construction of utility class
    }

    /**
     * Get the tabbed pane for a component
     * @param c
     * @return the tabbed pane for a component
     */
    public static JTabbedPane getTabbedPaneFor(Component c)
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

}
