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

import javax.swing.ComboBoxModel;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.util.FacadeListModel;

public abstract class CharacterComboBoxModel<E> extends FacadeListModel<E>
        implements ComboBoxModel<E>, ReferenceListener<E>
{

    private ReferenceFacade<E> reference = null;
    protected Object selectedItem = null;

    public CharacterComboBoxModel()
    {
    }

    public CharacterComboBoxModel(ListFacade<E> list, ReferenceFacade<E> ref)
    {
        setListFacade(list);
        setReference(ref);
    }

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
            setSelectedItem0(reference.get());
        }
    }

    @Override
    public Object getSelectedItem()
    {
        return selectedItem;
    }

    private void setSelectedItem0(Object item)
    {
        selectedItem = item;
        fireContentsChanged(this, -1, -1);
    }

    @Override
    public void referenceChanged(ReferenceEvent<E> e)
    {
        setSelectedItem0(e.getNewReference());
    }
}
