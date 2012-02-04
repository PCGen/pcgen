/*
 * AbstractListMenu.java
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
 * Created on Aug 18, 2008, 1:56:12 PM
 */
package pcgen.gui2.util;

import java.awt.event.ItemListener;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class AbstractListMenu<E> extends JMenu implements ListListener<E>
{

	private ListFacade<E> listModel;
	private int oldSize = 0;

	public AbstractListMenu(Action action)
	{
		this(action, null);
	}

	public AbstractListMenu(Action action, ListFacade<E> listModel)
	{
		super(action);
		setListModel(listModel);
	}

	public void elementAdded(ListEvent<E> e)
	{
		add(createMenuItem(e.getElement()), e.getIndex());
		oldSize++;
		checkEnabled();
	}

	public void elementRemoved(ListEvent<E> e)
	{
		remove(e.getIndex());
		oldSize--;
		checkEnabled();
	}

	public void elementsChanged(ListEvent<E> e)
	{
		for (int i = 0; i < oldSize; i++)
		{
			remove(0);
		}
		oldSize = listModel.getSize();
		for (int i = 0; i < oldSize; i++)
		{
			add(createMenuItem(listModel.getElementAt(i)), i);
		}
	}

	public void setListModel(ListFacade<E> listModel)
	{
		ListFacade<E> oldModel = this.listModel;
		if (oldModel != null)
		{
			oldModel.removeListListener(this);
			for (int x = 0; x < oldSize; x++)
			{
				remove(0);
			}
		}
		this.listModel = listModel;
		if (listModel != null)
		{
			oldSize = listModel.getSize();
			for (int x = 0; x < oldSize; x++)
			{
				add(createMenuItem(listModel.getElementAt(x)), x);
			}
			listModel.addListListener(this);
		}
		checkEnabled();
	}

	protected abstract JMenuItem createMenuItem(E item);

	protected void checkEnabled()
	{
		setEnabled(getMenuComponentCount() != 0);
	}

	protected static class CheckBoxMenuItem extends JCheckBoxMenuItem
	{

		private final Object item;

		public CheckBoxMenuItem(Object item, boolean selected,
				ItemListener listener)
		{
			this.item = item;
			setSelected(selected);
			addItemListener(listener);
		}

		@Override
		public String getText()
		{
			return item.toString();
		}

		@Override
		public Object[] getSelectedObjects()
		{
			return new Object[]
					{
						item
					};
		}

	}

}
