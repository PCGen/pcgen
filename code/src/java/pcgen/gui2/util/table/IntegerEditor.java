/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pcgen.gui2.util.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Implements a cell editor that uses a formatted text field
 * to edit Integer values.
 */
public class IntegerEditor extends DefaultCellEditor
{

    private final JFormattedTextField ftf;
    private final NumberFormat integerFormat;
    private final Integer minimum;
    private final Integer maximum;

    public IntegerEditor(int min, int max)
    {
        super(new JFormattedTextField());
        ftf = (JFormattedTextField) getComponent();
        minimum = min;
        maximum = max;

        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getIntegerInstance();
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);
        intFormatter.setMinimum(minimum);
        intFormatter.setMaximum(maximum);

        ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        ftf.setValue(minimum);
        ftf.setHorizontalAlignment(SwingConstants.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        ftf.getActionMap().put("check", new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!ftf.isEditValid())
                { //The text is invalid.
                    if (userSaysRevert())
                    { //reverted
                        ftf.postActionEvent(); //inform the editor
                    }
                } else
                {
                    try
                    { //The text is valid,
                        ftf.commitEdit(); //so use it.
                        ftf.postActionEvent(); //stop editing
                    } catch (java.text.ParseException exc)
                    {
                    }
                }
            }

        });
    }

    //Override to invoke setValue on the formatted text field.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        JFormattedTextField textField =
                (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        textField.setValue(value);
        return textField;
    }

    //Override to ensure that the value remains an Integer.
    @Override
    public Object getCellEditorValue()
    {
        JFormattedTextField textField = (JFormattedTextField) getComponent();
        Object o = textField.getValue();
        if (o instanceof Integer)
        {
            return o;
        } else if (o instanceof Number)
        {
            return ((Number) o).intValue();
        } else
        {
            try
            {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc)
            {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }

    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version
    //of this method so that everything gets cleaned up.
    @Override
    public boolean stopCellEditing()
    {
        JFormattedTextField textField = (JFormattedTextField) getComponent();
        if (textField.isEditValid())
        {
            try
            {
                textField.commitEdit();
            } catch (java.text.ParseException exc)
            {
            }

        } else
        { //text is invalid
            if (!userSaysRevert())
            { //user wants to edit
                return false; //don't let the editor go away
            }
        }
        return super.stopCellEditing();
    }

    /**
     * Lets the user know that the text they entered is
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false,
     * indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert()
    {
        ftf.selectAll();
        Object[] options = {"Edit", "Revert"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(ftf),
                "The value must be an integer between " + minimum + " and " + maximum + ".\n"
                        + "You can either continue editing " + "or revert to the last valid value.",
                "Invalid Text Entered", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);

        if (answer == 1)
        { //Revert!
            ftf.setValue(ftf.getValue());
            return true;
        }
        return false;
    }

    /**
     * @return The optimal size for this cell editor.
     */
    public Dimension getPreferredSize()
    {
        Component comp = getComponent();
        if (comp != null)
        {
            return comp.getPreferredSize();
        }

        return new Dimension(40, 25);
    }
}
