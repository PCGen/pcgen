/*
 * FilterButtonGroupPanel.java
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
package pcgen.gui2.filter;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * This component is used to represent a set of button filters that are mutually exclusive.
 * Only one of the button filters added to this component will be active at a single time.
 * The behavior is identical to that of a ButtonGroup which contains the buttons with the exception
 * that it is possible to deselect all of the buttons.
 * @param <C> The context that this filter is defined under
 * @param <E> The element type that will be filtered
 */
public class FilterButtonGroupPanel<C, E> extends JPanel
		implements DisplayableFilter<C, E>, ItemListener
{

	private List<FilterButton<C, E>> buttons = new ArrayList<>();
	private FilterHandler filterHandler;

	public FilterButtonGroupPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public void addFilterButton(FilterButton<C, E> button)
	{
		add(button);
		buttons.add(button);
		button.addItemListener(this);
		button.setFilterHandler(filterHandler);
	}

	public void removeFilterButton(FilterButton<C, E> button)
	{
		remove(button);
		buttons.remove(button);
		button.removeItemListener(this);
		button.setFilterHandler(null);
	}

	@Override
	public Component getFilterComponent()
	{
		return this;
	}

	@Override
	public void setFilterHandler(FilterHandler handler)
	{
		this.filterHandler = handler;
		for (FilterButton<C, E> filterButton : buttons)
		{
			filterButton.setFilterHandler(handler);
		}
	}

	@Override
	public boolean accept(C context, E element)
	{
		for (FilterButton<C, E> filterButton : buttons)
		{
			if (!filterButton.accept(context, element))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getStateChange() == ItemEvent.SELECTED)
		{
			for (FilterButton<C, E> filterButton : buttons)
			{
				if (filterButton == e.getItemSelectable())
				{
					continue;
				}
				filterButton.setSelected(false);
			}
		}
	}

}
