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
package pcgen.gui2.tabs.skill;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.ClassListener;
import pcgen.facade.core.CharacterLevelsFacade.SkillPointListener;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.table.TableCellUtilities;

/**
 * A model to back the table of character levels and the skill points
 * associated with them.
 */
public class SkillPointTableModel extends AbstractTableModel
        implements ListListener<CharacterLevelFacade>, ClassListener, SkillPointListener
{

    private CharacterLevelsFacade levels;

    public SkillPointTableModel(CharacterFacade character)
    {
        this.levels = character.getCharacterLevelsFacade();
        levels.addListListener(this);
        levels.addClassListener(this);
        levels.addSkillPointListener(this);
    }

    public static void initializeTable(JTable table)
    {
        table.setAutoCreateColumnsFromModel(false);
        JTableHeader header = table.getTableHeader();
        TableColumnModel columns = new DefaultTableColumnModel();
        TableCellRenderer headerRenderer = header.getDefaultRenderer();
        columns.addColumn(Utilities.createTableColumn(0, "in_level", headerRenderer, false));
        columns.addColumn(Utilities.createTableColumn(1, "in_class", headerRenderer, true));
        TableColumn remainCol = Utilities.createTableColumn(2, "in_iskRemain", headerRenderer, false);
        remainCol.setCellRenderer(new BoldNumberRenderer());
        columns.addColumn(remainCol);
        columns.addColumn(Utilities.createTableColumn(3, "in_gained", headerRenderer, false));
        table.setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
        table.setColumnModel(columns);
        table.setFocusable(false);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
    }

    @Override
    public int getRowCount()
    {
        return levels.getSize();
    }

    @Override
    public int getColumnCount()
    {
        return 4;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case 1:
                return Object.class;
            case 0:
            case 2:
            case 3:
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            return rowIndex + 1;
        }
        CharacterLevelFacade level = levels.getElementAt(rowIndex);
        switch (columnIndex)
        {
            case 1:
                return levels.getClassTaken(level);
            case 2:
                return levels.getRemainingSkillPoints(level);
            case 3:
                return levels.getGainedSkillPoints(level);
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return columnIndex == 3;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        CharacterLevelFacade level = levels.getElementAt(rowIndex);
        levels.setGainedSkillPoints(level, (Integer) aValue);
    }

    @Override
    public void elementAdded(ListEvent<CharacterLevelFacade> e)
    {
        fireTableRowsInserted(e.getIndex(), e.getIndex());
    }

    @Override
    public void elementRemoved(ListEvent<CharacterLevelFacade> e)
    {
        fireTableRowsDeleted(e.getIndex(), e.getIndex());
    }

    @Override
    public void elementsChanged(ListEvent<CharacterLevelFacade> e)
    {
        fireTableDataChanged();
    }

    @Override
    public void elementModified(ListEvent<CharacterLevelFacade> e)
    {
        fireTableRowsUpdated(e.getIndex(), e.getIndex());
    }

    @Override
    public void skillPointsChanged(CharacterLevelEvent e)
    {
        levelChanged(e);
    }

    @Override
    public void classChanged(CharacterLevelEvent e)
    {
        levelChanged(e);
    }

    private void levelChanged(CharacterLevelEvent e)
    {
        int firstRow = e.getBaseLevelIndex();
        int lastRow = e.affectsHigherLevels() ? levels.getSize() - 1 : firstRow;
        fireTableRowsUpdated(firstRow, lastRow);
    }

    /**
     * The Class {@code BoldNumberRenderer} displays a right aligned
     * read-only column containing a bolded number.
     */
    private static class BoldNumberRenderer extends DefaultTableCellRenderer
    {

        /**
         * Create a new BoldNumberRenderer instance.
         */
        public BoldNumberRenderer()
        {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            comp.setFont(FontManipulation.bold(table.getFont()));
            return this;
        }

    }

}
