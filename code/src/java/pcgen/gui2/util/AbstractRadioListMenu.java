/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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

import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import pcgen.facade.util.event.ListEvent;

public abstract class AbstractRadioListMenu<E> extends AbstractListMenu<E> implements ItemListener
{

	private final ButtonGroup group = new ButtonGroup();
	private Map<E, RadioMenuItem> menuMap = new HashMap<>();
	private E selectedItem = null;

	public AbstractRadioListMenu(Action action)
	{
		super(action);
	}

	@Override
	protected JMenuItem createMenuItem(E item, int index)
	{
		RadioMenuItem menuItem = new RadioMenuItem(item, item == selectedItem, this);
		group.add(menuItem);
		menuMap.put(item, menuItem);
		return menuItem;
	}

	@Override
	public void elementRemoved(ListEvent<E> e)
	{
		group.remove(getItem(e.getIndex()));
		menuMap.remove(e.getElement());
		super.elementRemoved(e);
	}

	public void setSelectedItem(E item)
	{
		JMenuItem menuItem = menuMap.get(item);
		if (menuItem != null)
		{
			menuItem.setSelected(true);
		}
	}

	/**
	 * Update the menu so that no entries are selected. 
	 */
	public void clearSelection()
	{
		group.clearSelection();
	}

	private static class RadioMenuItem extends JRadioButtonMenuItem
	{

		private final Object item;

		public RadioMenuItem(Object item, boolean selected, ItemListener listener)
		{
			super(item.toString(), selected);
			this.item = item;
			addItemListener(listener);
		}

		@Override
		public Object[] getSelectedObjects()
		{
			return new Object[]{item};
		}

	}

}
