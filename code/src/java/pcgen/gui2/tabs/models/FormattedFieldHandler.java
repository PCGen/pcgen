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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;

import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.util.ManagedField;

public abstract class FormattedFieldHandler implements PropertyChangeListener,
        ReferenceListener<Integer>, ManagedField
{

    private JFormattedTextField field;
    private ReferenceFacade<Integer> ref;

    public FormattedFieldHandler(JFormattedTextField field, ReferenceFacade<Integer> ref)
    {
        this.field = field;
        this.ref = ref;
    }

    @Override
    public JFormattedTextField getTextField()
    {
        return field;
    }

    /**
     * Attach the handler to the screen field. e.g. When the character is
     * made active.
     */
    @Override
    public void install()
    {
        field.setValue(ref.get());
        field.addPropertyChangeListener(this);
        ref.addReferenceListener(this);
    }

    /**
     * Detach the handler from the on screen field. e.g. when the
     * character is no longer being displayed.
     */
    @Override
    public void uninstall()
    {
        field.removePropertyChangeListener(this);
        ref.removeReferenceListener(this);
    }

    @Override
    public void referenceChanged(ReferenceEvent<Integer> e)
    {
        int newVal = e.getNewReference();
        int oldVal = ((Number) field.getValue()).intValue();
        if (oldVal != newVal)
        {
            field.setValue(newVal);
        }
    }

    protected abstract void valueChanged(int value);

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        valueChanged(((Number) field.getValue()).intValue());
    }

}
