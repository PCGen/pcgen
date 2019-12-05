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
package pcgen.gui2.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;

public abstract class AbstractListMenu<E> extends JMenu implements ListListener<E>
{

    private ListFacade<E> listModel;
    private int oldSize = 0;
    private int offset = 0;

    protected AbstractListMenu(Action action)
    {
        this(action, null);
    }

    private AbstractListMenu(Action action, ListFacade<E> listModel)
    {
        super(action);
        setListModel(listModel);
    }

    @Override
    public void elementAdded(ListEvent<E> e)
    {
        rebuildListMenu();
    }

    @Override
    public void elementRemoved(ListEvent<E> e)
    {
        rebuildListMenu();
    }

    @Override
    public void elementsChanged(ListEvent<E> e)
    {
        rebuildListMenu();
    }

    @Override
    public void elementModified(ListEvent<E> e)
    {
    }

    @Override
    public Point getToolTipLocation(MouseEvent event)
    {
        Dimension size = getSize();
        double oneRowUpHeight = (size.getHeight() * -1) - 5;
        return new Point((int) size.getWidth(), (int) oneRowUpHeight);
    }

    private void rebuildListMenu()
    {
        for (int i = 0;i < oldSize;i++)
        {
            remove(offset);
        }
        oldSize = listModel.getSize();
        for (int i = 0;i < oldSize;i++)
        {
            add(createMenuItem(listModel.getElementAt(i), i), i + offset);
        }
        checkEnabled();
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public void setListModel(ListFacade<E> listModel)
    {
        ListFacade<E> oldModel = this.listModel;
        if (oldModel != null)
        {
            oldModel.removeListListener(this);
            for (int x = 0;x < oldSize;x++)
            {
                remove(offset);
            }
        }
        this.listModel = listModel;
        if (listModel != null)
        {
            oldSize = listModel.getSize();
            for (int x = 0;x < oldSize;x++)
            {
                add(createMenuItem(listModel.getElementAt(x), x), x + offset);
            }
            listModel.addListListener(this);
        }
        checkEnabled();
    }

    /**
     * Create a new dynamic menu item. The menu can optionally have a number at the
     * start of the menu item to allow quick selection.
     *
     * @param item  The item to create a menu for.
     * @param index The 0 based index of the items position in the dynamic item list.
     * @return A menu item.
     */
    protected abstract JMenuItem createMenuItem(E item, int index);

    private void checkEnabled()
    {
        setEnabled(getMenuComponentCount() != 0);
    }

    protected static final class CheckBoxMenuItem extends JCheckBoxMenuItem
    {

        private final Object item;

        public CheckBoxMenuItem(Object item, boolean selected, ItemListener listener)
        {
            super(Objects.requireNonNull(item).toString());
            this.item = Objects.requireNonNull(item);
            setSelected(selected);
            addItemListener(Objects.requireNonNull(listener));
        }

        @Override
        public Object[] getSelectedObjects()
        {
            return new Object[]{item};
        }
    }

}
