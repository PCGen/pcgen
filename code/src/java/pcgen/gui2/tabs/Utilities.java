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
package pcgen.gui2.tabs;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.gui2.util.SignIcon;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.system.LanguageBundle;

public final class Utilities
{

    private Utilities()
    {
    }

    public static JButton createSignButton(Sign sign)
    {
        JButton button = new JButton();
        button.setMargin(new Insets(0, 8, 0, 8));
        button.setIcon(new SignIcon(sign));
        return button;
    }

    public static TableColumn createTableColumn(int index, String headerValue, TableCellRenderer headerRenderer,
            boolean resizable)
    {
        TableColumn column = new TableColumn(index);
        if (headerValue.startsWith("in_"))
        {
            column.setHeaderValue(LanguageBundle.getString(headerValue));
        } else
        {
            column.setHeaderValue(headerValue);
        }
        column.setHeaderRenderer(headerRenderer);
        if (!resizable)
        {
            column.sizeWidthToFit();
            column.setMaxWidth(column.getMaxWidth() + 10);
            column.setPreferredWidth(column.getPreferredWidth() + 10);
        }
        column.setResizable(resizable);
        return column;
    }

}
