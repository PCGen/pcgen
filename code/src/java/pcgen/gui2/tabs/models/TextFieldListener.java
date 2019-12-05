/**
 * Copyright James Dempsey, 2011
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.tabs.models;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * The Class {@code TextFieldListener} is a convenience class for
 * processing a change in the value of a text field. It is only one way
 * though and does not update the text field if the underlying value
 * changes.
 */
public abstract class TextFieldListener implements DocumentListener
{

    private final JTextComponent textField;

    /**
     * @param textField
     */
    public TextFieldListener(JTextComponent textField)
    {
        this.textField = textField;
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        textChanged(textField.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        textChanged(textField.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        textChanged(textField.getText());
    }

    protected abstract void textChanged(String text);
}
