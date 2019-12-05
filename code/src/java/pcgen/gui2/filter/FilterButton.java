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
package pcgen.gui2.filter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import pcgen.gui2.UIPropertyContext;
import pcgen.system.PropertyContext;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a simple filter represented as a toggle button. When the button is selected
 * (i.e. pressed) the filter assigned to this button will become active. When deselected the filter
 * will become inactive. Selecting and deselecting the button will trigger its FilterHandler
 * to refilter its contents.
 */
public class FilterButton<C, E> extends JToggleButton implements DisplayableFilter<C, E>, ActionListener
{

    private FilterHandler filterHandler;
    private Filter<C, E> filter;
    private final PropertyContext filterContext;

    public FilterButton(String prefKey)
    {
        this(prefKey, false);
    }

    public FilterButton(String prefKey, boolean defaultSelectedState)
    {
        if (StringUtils.isEmpty(prefKey))
        {
            throw new NullPointerException("prefKey cannot be null");
        }
        addActionListener(this);
        PropertyContext baseContext = UIPropertyContext.createContext("filterPrefs");
        filterContext = baseContext.createChildContext(prefKey);
        setSelected(filterContext.initBoolean("active", defaultSelectedState));
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
    }

    public void setFilter(Filter<C, E> filter)
    {
        this.filter = filter;
    }

    @Override
    public boolean accept(C context, E element)
    {
        //if this button is not selected treat it as if
        //this filter always accepts
        return !isEnabled() || !isSelected() || filter.accept(context, element);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        filterHandler.refilter();
        filterContext.setBoolean("active", isSelected());
    }

}
