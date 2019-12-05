/*
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
package pcgen.gui2.util.treeview;

import java.util.List;

public interface DataView<E>
{

    /**
     * Note: If any of the returned data is editable, make sure that the returned
     * list is editable as well.
     *
     * @return editable list
     */
    Object getData(E element, int column);

    void setData(Object value, E element, int column);

    List<? extends DataViewColumn> getDataColumns();

    /**
     * @return The key that will be used to save any preferences for this data view.
     */
    String getPrefsKey();
}
