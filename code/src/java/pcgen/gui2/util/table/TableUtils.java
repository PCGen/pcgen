/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.util.table;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.TableColumn;

public final class TableUtils
{

    private TableUtils()
    {
    }

    public static JTable createDefaultTable()
    {
        JTable table = new JTable();
        table.setFillsViewportHeight(true);
        return table;
    }

    /**
     * This configures a scroll pane such that a set of checkboxes will reside along the
     * scroll pane's row header.
     *
     * @param table
     * @param rowheaderTable
     * @return the toggle button selection pane
     */
    public static JScrollPane createCheckBoxSelectionPane(JTable table, JTable rowheaderTable)
    {
        return createToggleButtonSelectionPane(table, rowheaderTable, new JCheckBox());
    }

    public static JScrollPane createRadioBoxSelectionPane(JTable table, JTable rowheaderTable)
    {
        rowheaderTable.setDefaultEditor(Boolean.class, new TableCellUtilities.RadioButtonEditor());
        return createToggleButtonSelectionPane(table, rowheaderTable, new JRadioButton());
    }

    private static JScrollPane createToggleButtonSelectionPane(JTable table, JTable rowheaderTable,
            JToggleButton button)
    {
        rowheaderTable.setAutoCreateColumnsFromModel(false);
        // force the tables to share models
        rowheaderTable.setModel(table.getModel());
        rowheaderTable.setSelectionModel(table.getSelectionModel());
        rowheaderTable.setRowHeight(table.getRowHeight());
        rowheaderTable.setIntercellSpacing(table.getIntercellSpacing());
        rowheaderTable.setShowGrid(false);
        rowheaderTable.setFocusable(false);

        TableColumn column = new TableColumn(-1);
        column.setHeaderValue(new Object());
        column.setCellRenderer(new TableCellUtilities.ToggleButtonRenderer(button));
        rowheaderTable.addColumn(column);
        rowheaderTable.setPreferredScrollableViewportSize(new Dimension(20, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setRowHeaderView(rowheaderTable);
        return scrollPane;
    }

}
