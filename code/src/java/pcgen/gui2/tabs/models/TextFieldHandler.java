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
package pcgen.gui2.tabs.models;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.util.ManagedField;

/**
 * Watches both the ReferenceFacade and a JTextField and updates both for any changes.
 */
public abstract class TextFieldHandler implements DocumentListener, ReferenceListener<String>, ManagedField
{

    /**
     * The JTextField to be monitored for changes when this is installed.
     */
    private JTextField textField;

    /**
     * The ReferenceFacade to be monitored for changes when this is installed.
     */
    private ReferenceFacade<String> referenceFacade;

    /**
     * Constructs a new TextFieldChannelHandler monitoring the given JTextField and
     * ReferenceFacade.
     *
     * @param textField       The JTextField to be monitored for changes when this is installed
     * @param referenceFacade The ReferenceFacade to be monitored for changes when this is
     *                        installed
     */
    public TextFieldHandler(JTextField textField, ReferenceFacade<String> referenceFacade)
    {
        this.textField = textField;
        this.referenceFacade = referenceFacade;
    }

    @Override
    public JTextField getTextField()
    {
        return textField;
    }

    /**
     * Attach the handler to the screen field. e.g. When the character is
     * made active.
     */
    @Override
    public void install()
    {
        textField.setText(referenceFacade.get());
        textField.getDocument().addDocumentListener(this);
        referenceFacade.addReferenceListener(this);
    }

    /**
     * Detach the handler from the on screen field. e.g. when the
     * character is no longer being displayed.
     */
    @Override
    public void uninstall()
    {
        textField.getDocument().removeDocumentListener(this);
        referenceFacade.removeReferenceListener(this);
    }

    @Override
    public void referenceChanged(ReferenceEvent<String> e)
    {
        if (!textField.getText().equals(e.getNewReference()))
        {
            //This is done to prevent an event feedback loop since
            //setText modifies the document we are listening to.
            textField.getDocument().removeDocumentListener(this);
            textField.setText(e.getNewReference());
            textField.getDocument().addDocumentListener(this);
        }
    }

    protected abstract void textChanged(String text);

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

}
