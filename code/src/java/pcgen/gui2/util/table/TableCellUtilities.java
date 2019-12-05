/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public final class TableCellUtilities
{

    private static final TableCellRenderer DUMMY_RENDERER = new DefaultTableCellRenderer();

    private TableCellUtilities()
    {
    }

    public static void setToRowBackground(Component c, JTable table, int row)
    {
        Component tableCellRendererComponent =
                DUMMY_RENDERER.getTableCellRendererComponent(table, null, false, false, row, 0);
        c.setBackground(tableCellRendererComponent.getBackground());
    }

    public static class RadioButtonEditor extends AbstractCellEditor implements ActionListener, TableCellEditor
    {

        private final JRadioButton button;

        RadioButtonEditor()
        {
            this.button = new JRadioButton();
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.addActionListener(this);
        }

        @Override
        public Object getCellEditorValue()
        {
            return button.isSelected();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column)
        {
            boolean selected = false;
            if (value instanceof Boolean)
            {
                selected = (Boolean) value;
            } else if (value instanceof String)
            {
                selected = value.equals("true");
            }
            button.setSelected(selected);
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            stopCellEditing();
        }

    }

    public static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor, ChangeListener
    {

        protected final JSpinner spinner;

        public SpinnerEditor(SpinnerModel model)
        {
            this(new JSpinner(model));
        }

        private SpinnerEditor(JSpinner spinner)
        {
            this.spinner = spinner;
            spinner.addChangeListener(this);
        }

        @Override
        public Object getCellEditorValue()
        {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column)
        {
            spinner.setValue(value);
            return spinner;
        }

        @Override
        public void stateChanged(ChangeEvent e)
        {
            stopCellEditing();
        }

        @Override
        public boolean stopCellEditing()
        {
            try
            {
                spinner.commitEdit();
            } catch (ParseException ex)
            {
                return false;
            }
            return super.stopCellEditing();
        }

    }

    public static final class ToggleButtonRenderer extends JComponent implements TableCellRenderer
    {

        private final JToggleButton button;
        private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

        ToggleButtonRenderer(JToggleButton button)
        {
            this.button = button;
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setBorderPainted(true);
            setLayout(new GridLayout(1, 1));
            add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value == null)
            {
                return renderer;
            }
            setBackground(renderer.getBackground());
            button.setSelected((Boolean) value);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    public static class SpinnerRenderer extends DefaultTableCellRenderer
    {

        private final JSpinner spinner;

        public SpinnerRenderer()
        {
            this(new JSpinner());
        }

        public SpinnerRenderer(JSpinner spinner)
        {
            this.spinner = spinner;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            if (value == null)
            {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            spinner.setValue(value);
            spinner.setEnabled(table.isCellEditable(row, column));
            return spinner;
        }

    }

    /**
     * Align the cell text in a column
     */
    public static final class AlignRenderer extends DefaultTableCellRenderer
    {

        /**
         * align is one of:
         * SwingConstants.LEFT
         * SwingConstants.CENTER
         * X.RIGHT
         **/
        private int align = SwingConstants.LEFT;
        private final boolean showTooltips;

        /**
         * Create a new instance of AlignRenderer without tool tips.
         *
         * @param anInt The alignment constant, from SwingConstants.
         */
        public AlignRenderer(int anInt)
        {
            this(anInt, false);
        }

        /**
         * Create a new instance of AlignRenderer
         *
         * @param anInt    The alignment constant, from SwingConstants.
         * @param tooltips Should we show tool tips?
         */
        public AlignRenderer(int anInt, boolean tooltips)
        {
            super();
            align = anInt;
            this.showTooltips = tooltips;
            setHorizontalAlignment(align);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setEnabled((table == null) || table.isEnabled());
            setHorizontalAlignment(align);
            if (showTooltips)
            {
                setToolTipText(String.valueOf(value));
            }
            return this;
        }

    }

}
