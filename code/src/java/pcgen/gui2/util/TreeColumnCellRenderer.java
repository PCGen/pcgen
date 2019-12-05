/*
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.util;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * The parent class for all TreeCellRenderers used in JTreeTable. The whole point of this class is
 * to solve a very particular quirk in how tree cells are rendered vs how table cells are rendered.
 * Without forcefully setting the cell's background prior to rendering it will use some default
 * color (usually white) instead of the table's background color to render the tree cell. As a
 * consequence you will see an outer background (the table cell's background) and an inner
 * background (the tree cell's background) on top of it which don't match. We solve this problem by
 * setting the tree's background color before rendering the tree cell to that of the rendering table
 * cell. Then in this class we assign this tree cell's background color to the one that was set for
 * the tree.
 */
public class TreeColumnCellRenderer extends DefaultTreeCellRenderer
{

    protected TreeColumnCellRenderer()
    {
        setClosedIcon(null);
        setLeafIcon(null);
        setOpenIcon(null);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus)
    {
        setBackgroundNonSelectionColor(tree.getBackground());
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
