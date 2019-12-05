/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.filter;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class FilterUtilities
{

    private FilterUtilities()
    {
    }

    /**
     * This creates a new panel and puts the filterbar on the northern part
     * of the panel and adds the table to the center with a new scroll bar.
     * This method also has the side effect of setting the table's filter
     * to that of the filterbar.
     *
     * @param <C>
     * @param <E>
     * @param table     a FilteredTreeViewTable
     * @param filterBar a FilterBar
     * @return a JPanel containing the table and filterbar
     */
    public static <C, E> JPanel configureFilteredTreeViewPane(FilteredTreeViewTable<C, E> table,
            FilterBar<C, E> filterBar)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        table.setDisplayableFilter(filterBar);
        return panel;
    }

}
