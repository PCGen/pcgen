/*
 * Copyright 2010 (C) Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.summary;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.ClassListener;
import pcgen.facade.core.CharacterLevelsFacade.HitPointListener;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.table.TableCellUtilities;

public class ClassLevelTableModel extends AbstractTableModel implements ListListener<CharacterLevelFacade>,
        ItemListener, PropertyChangeListener, HitPointListener, ClassListener
{

    private CharacterLevelsFacade levels;
    private Map<String, Integer> finalLevelMap;
    private CharacterFacade character;
    private Editor editor = new Editor();
    private Editor renderer = new Editor();
    private JComboBox classComboBox;
    private JTable classTable;

    public ClassLevelTableModel(CharacterFacade character, JTable table, JComboBox comboBox)
    {
        super();
        this.character = character;
        this.levels = character.getCharacterLevelsFacade();
        this.finalLevelMap = new HashMap<>();
        resetLevelMap();
        levels.addListListener(this);
        levels.addClassListener(this);
        levels.addHitPointListener(this);
        this.classComboBox = comboBox;
        this.classTable = table;
    }

    public static void initializeTable(JTable classLevelTable)
    {
        JTableHeader tableHeader = classLevelTable.getTableHeader();
        tableHeader.setResizingAllowed(false);
        tableHeader.setReorderingAllowed(false);
        TableColumnModel columnModel = new DefaultTableColumnModel();
        TableCellRenderer headerRenderer = tableHeader.getDefaultRenderer();
        columnModel.addColumn(Utilities.createTableColumn(0, "Level", headerRenderer, false));
        columnModel.addColumn(Utilities.createTableColumn(1, "HP", headerRenderer, false));
        columnModel.addColumn(Utilities.createTableColumn(2, "Class (All Levels In Class)", headerRenderer, true));
        classLevelTable.setColumnModel(columnModel);
        classLevelTable.setAutoCreateColumnsFromModel(false);
        classLevelTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        classLevelTable.setFocusable(false);
        classLevelTable.setCellSelectionEnabled(false);
        classLevelTable.setRowHeight(20);
    }

    public void install()
    {
        classTable.setModel(this);
        classTable.setDefaultRenderer(Object.class, renderer);
        classTable.setDefaultRenderer(Integer.class, new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
        classTable.setDefaultEditor(Object.class, editor);
        classComboBox.addItemListener(this);
        classComboBox.addPropertyChangeListener("model", this);
    }

    public void uninstall()
    {
        classComboBox.removeItemListener(this);
        classComboBox.removePropertyChangeListener("model", this);
    }

    private void resetLevelMap()
    {
        finalLevelMap.clear();
        for (int i = levels.getSize() - 1;i >= 0;i--)
        {
            String c = levels.getClassTaken(levels.getElementAt(i)).getKeyName();
            if (!finalLevelMap.containsKey(c))
            {
                finalLevelMap.put(c, i);
            }
        }
    }

    @Override
    public int getRowCount()
    {
        return levels.getSize() + 1;
    }

    @Override
    public int getColumnCount()
    {
        return 3;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return rowIndex >= levels.getSize() - 1 && columnIndex == 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
            case 1:
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (rowIndex == levels.getSize())
        {
            return null;
        }
        switch (columnIndex)
        {
            case 0:
                return rowIndex + 1;
            case 1:
                return levels.getHPGained(levels.getElementAt(rowIndex));
            case 2:
                PCClass c = levels.getClassTaken(levels.getElementAt(rowIndex));
                String classKey = c.getKeyName();
                if (finalLevelMap.get(classKey) == rowIndex)
                {
                    return c + " (" + character.getClassLevel(c) + ")";
                }
                return c.toString();
            default:
                return null;
        }
    }

    @Override
    public void elementAdded(ListEvent<CharacterLevelFacade> e)
    {
        editor.cancelCellEditing();
        int i = e.getIndex();
        String c = levels.getClassTaken(levels.getElementAt(i)).getKeyName();
        finalLevelMap.put(c, i);
        // Do a full refresh as the previous max class level row may be affected
        fireTableDataChanged();
    }

    @Override
    public void elementRemoved(ListEvent<CharacterLevelFacade> e)
    {
        editor.cancelCellEditing();
        resetLevelMap();
        // Do a full refresh as the new max class level row may be affected
        fireTableDataChanged();
    }

    @Override
    public void elementsChanged(ListEvent<CharacterLevelFacade> e)
    {
        editor.cancelCellEditing();
        resetLevelMap();
        fireTableDataChanged();
    }

    @Override
    public void elementModified(ListEvent<CharacterLevelFacade> e)
    {
        // Do a full refresh as the new max class level row may be affected
        fireTableDataChanged();
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            fireTableRowsUpdated(levels.getSize(), levels.getSize());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        fireTableRowsUpdated(levels.getSize(), levels.getSize());
    }

    @Override
    public void classChanged(CharacterLevelEvent e)
    {
        levelChanged(e);
    }

    @Override
    public void hitPointsChanged(CharacterLevelEvent e)
    {
        levelChanged(e);
    }

    private void levelChanged(CharacterLevelEvent e)
    {
        int firstRow = e.getBaseLevelIndex();
        int lastRow = e.affectsHigherLevels() ? levels.getSize() - 1 : firstRow;
        fireTableRowsUpdated(firstRow, lastRow);
    }

    private class Editor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer, ActionListener
    {

        private JPanel cellPanel = new JPanel();
        private JLabel cellLabel = new JLabel();
        private JButton addLevelButton = Utilities.createSignButton(Sign.Plus);
        private JButton removeLevelButton = Utilities.createSignButton(Sign.Minus);

        public Editor()
        {
            super();
            cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.X_AXIS));
            cellPanel.setOpaque(true);
            addLevelButton.setFocusable(false);
            removeLevelButton.setFocusable(false);
            addLevelButton.addActionListener(this);
            removeLevelButton.addActionListener(this);
        }

        @Override
        public Object getCellEditorValue()
        {
            return null;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            return getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column)
        {
            cellPanel.removeAll();
            TableCellUtilities.setToRowBackground(cellPanel, table, row);
            cellLabel.setForeground(table.getForeground());
            cellLabel.setFont(table.getFont());
            if (row == levels.getSize() - 1)
            {
                cellLabel.setText(value.toString());
                cellPanel.add(cellLabel);
                cellPanel.add(Box.createHorizontalGlue());
                cellPanel.add(removeLevelButton);
            } else if (row == levels.getSize())
            {
                cellLabel.setText("Add Level");
                cellPanel.add(Box.createHorizontalGlue());
                cellPanel.add(cellLabel);
                cellPanel.add(Box.createHorizontalStrut(3));
                addLevelButton.setEnabled(classComboBox.getSelectedItem() != null);
                cellPanel.add(addLevelButton);
            } else
            {
                cellLabel.setText(value.toString());
                cellPanel.add(cellLabel);
            }
            return cellPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == addLevelButton)
            {
                PCClass c = (PCClass) classComboBox.getSelectedItem();
                if (c != null)
                {
                    character.addCharacterLevels(new PCClass[]{c});
                }
            } else
            {
                character.removeCharacterLevels(1);
            }
            cancelCellEditing();
        }

    }

}
