/**
 * TextFieldListener.java
 * Copyright James Dempsey, 2011
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
 * Created on 10/10/2011 2:28:48 PM
 *
 * $Id$
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
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
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
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e)
	{
		textChanged(textField.getText());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e)
	{
		textChanged(textField.getText());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		textChanged(textField.getText());
	}

	protected abstract void textChanged(String text);
}
