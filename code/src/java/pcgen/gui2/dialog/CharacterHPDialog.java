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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import pcgen.core.PCClass;
import pcgen.core.RollingMethods;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.facade.core.CharacterLevelsFacade.HitPointListener;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.table.IntegerEditor;

import org.apache.commons.lang3.math.NumberUtils;

public final class CharacterHPDialog extends JDialog implements ActionListener
{

    private final CharacterFacade character;
    private final CharacterLevelsFacade levels;
    private final JLabel totalHp;
    private final HPTableModel tableModel;

    private CharacterHPDialog(Frame frame, CharacterFacade character)
    {
        super(frame, true);
        this.character = character;
        this.levels = character.getCharacterLevelsFacade();
        this.totalHp = new JLabel();
        this.tableModel = new HPTableModel();
        initComponents();
        pack();
    }

    public static void showHPDialog(Component parent, CharacterFacade character)
    {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        CharacterHPDialog dialog = new CharacterHPDialog(frame, character);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void initComponents()
    {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        JTable table = new JTable(tableModel)
        {

            @Override
            public TableCellEditor getCellEditor(int row, int column)
            {
                if (column == 5)
                {//TODO: the max roll should be calculated in a different manner
                    String hd = levels.getClassTaken(levels.getElementAt(row)).getHD();
                    int max = NumberUtils.toInt(hd);
                    return new IntegerEditor(1, max);
                } else
                {
                    return super.getCellEditor(row, column);
                }
            }

        };
        table.setDefaultRenderer(JButton.class, new Renderer());
        table.setDefaultEditor(JButton.class, new Editor());
        table.setCellSelectionEnabled(false);
        table.setRowHeight(new IntegerEditor(1, 10).getPreferredSize().height);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        pane.add(scrollPane, BorderLayout.CENTER);

        Box box = Box.createHorizontalBox();
        box.add(new JLabel("Total Hp:"));
        box.add(Box.createHorizontalStrut(3));

        final ReferenceListener<Integer> hpListener = e -> totalHp.setText(e.getNewReference().toString());
        ReferenceFacade<Integer> hpRef = character.getTotalHPRef();
        totalHp.setText(hpRef.get().toString());
        hpRef.addReferenceListener(hpListener);
        box.add(totalHp);
        box.add(Box.createHorizontalStrut(5));

        JButton button = new JButton("Reroll All");
        button.setActionCommand("Reroll");
        button.addActionListener(this);
        box.add(button);

        box.add(Box.createHorizontalGlue());
        button = new JButton("Close");
        button.setActionCommand("Close");
        button.addActionListener(this);
        box.add(button);
        pane.add(box, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosed(WindowEvent e)
            {
                //Make sure to remove the listeners so that the garbage collector can
                //dispose of this dialog and prevent a memory leak
                levels.removeHitPointListener(tableModel);
                character.getTotalHPRef().removeReferenceListener(hpListener);
            }

        });

        Utility.installEscapeCloseOperation(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Reroll"))
        {
            for (int l = 0;l < levels.getSize();l++)
            {
                CharacterLevelFacade level = levels.getElementAt(l);
                int i = Integer.parseInt(levels.getClassTaken(level).getHD());
                int rolled = RollingMethods.roll(i);
                levels.setHPRolled(level, rolled);
            }
            return;
        }
        dispose();
    }

    private class HPTableModel extends AbstractTableModel implements HitPointListener
    {

        public HPTableModel()
        {
            levels.addHitPointListener(this);
        }

        @Override
        public int getRowCount()
        {
            return levels.getSize();
        }

        @Override
        public int getColumnCount()
        {
            return 7;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            switch (columnIndex)
            {
                case 5:
                case 6:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                case 3:
                case 4:
                case 5:
                    return Integer.class;
                case 6:
                    return JButton.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0:
                    return "Level";
                case 1:
                    return "Class";
                case 2:
                    return "Sides";
                case 3:
                    return "Total";
                case 4:
                    return "Adj";
                case 5:
                    return "Rolled";
                default:
                    return "Reroll";
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            CharacterLevelFacade level = levels.getElementAt(rowIndex);
            PCClass c = levels.getClassTaken(level);
            switch (columnIndex)
            {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return c;
                case 2:
                    return c.getHD();
                case 3:
                    return levels.getHPGained(level);
                case 4:
                    return levels.getHPGained(level) - levels.getHPRolled(level);
                case 5:
                    return levels.getHPRolled(level);
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            CharacterLevelFacade level = levels.getElementAt(rowIndex);
            levels.setHPRolled(level, (Integer) aValue);
        }

        @Override
        public void hitPointsChanged(CharacterLevelEvent e)
        {
            fireTableRowsUpdated(e.getBaseLevelIndex(), e.getBaseLevelIndex());
        }

    }

    private static class Renderer implements TableCellRenderer
    {

        private final JButton button = new JButton();

        public Renderer()
        {
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setText("Reroll");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            return button;
        }

    }

    private class Editor extends AbstractCellEditor implements TableCellEditor, ActionListener
    {

        private final JButton button = new JButton();
        private int editingRow;

        public Editor()
        {
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setText("Reroll");
            button.addActionListener(this);
        }

        @Override
        public Object getCellEditorValue()
        {
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column)
        {
            editingRow = row;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            CharacterLevelFacade level = levels.getElementAt(editingRow);
            int i = Integer.parseInt(levels.getClassTaken(level).getHD());
            int rolled = RollingMethods.roll(i);
            levels.setHPRolled(level, rolled);
            cancelCellEditing();
        }

    }

}
