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

public interface DisplayableFilter<C, E> extends Filter<C, E>
{

    /**
     * returns a Component that will be used to display
     * this filter
     *
     * @return a swing Component
     */
    Component getFilterComponent();

    /**
     * sets the handler that will be used to toggle refiltering of a list
     * when a change occurs to this filter
     *
     * @param handler
     */
    void setFilterHandler(FilterHandler handler);

}
