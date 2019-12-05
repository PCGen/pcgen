/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.gui2.tabs.models;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.util.FacadeListModel;

/**
 * The Class {@code DeferredCharacterComboBoxModel} is a model for a combo
 * box that holds off setting the value until focus is lost. This gets around a bug
 * with the keyboard navigation of JComboBox where each key press selects the
 * highlighted entry. This model should be used where costly or permanent actions
 * are taken when an item is selected (e.h Race).
 * <p>
 * Note: This class needs to be added as a FocusListener of the target JComboBox
 * for selection to work.
 *
 * @param <E> The type of object being managed, generally a Facade
 */
@SuppressWarnings("serial")
public abstract class DeferredCharacterComboBoxModel<E> extends FacadeListModel<E>
        implements ComboBoxModel<E>, ReferenceListener<E>, FocusListener
{

    private ReferenceFacade<E> reference = null;
    protected Object selectedItem = null;

    public DeferredCharacterComboBoxModel(ListFacade<E> list, ReferenceFacade<E> ref)
    {
        setListFacade(list);
        setReference(ref);
    }

    /**
     * Set the reference to the selected object that we should listen for external changes to.
     *
     * @param ref The reference.
     */
    public void setReference(ReferenceFacade<E> ref)
    {
        if (reference != null)
        {
            reference.removeReferenceListener(this);
        }
        reference = ref;
        if (reference != null)
        {
            reference.addReferenceListener(this);
            setSelectedItem(reference.get());
        }
    }

    @Override
    public Object getSelectedItem()
    {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(Object item)
    {
        selectedItem = item;
        fireContentsChanged(this, -1, -1);
    }

    @Override
    public void referenceChanged(ReferenceEvent<E> e)
    {
        setSelectedItem(e.getNewReference());
    }

    /**
     * Now that the user has finished updating the combo box, save the value
     * they selected. This should be implemented as appropriate for each child
     * of this class.
     *
     * @param item The item that the user selected.
     */
    public abstract void commitSelectedItem(Object item);

    @Override
    public void focusGained(FocusEvent e)
    {
        // Ignored
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        // Temporary focus lost means something like the drop-down has
        // got focus
        if (e.isTemporary())
        {
            return;
        }

        SwingUtilities.invokeLater(() -> commitSelectedItem(selectedItem));
    }
}
