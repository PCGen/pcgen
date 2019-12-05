/*
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class CachedDataView<E> implements DataView<E>
{

    private Map<E, List<Object>> dataCache = new WeakHashMap<>();

    @Override
    public final Object getData(E element, int column)
    {
        List<Object> data = dataCache.get(element);
        if (data == null)
        {
            List<? extends DataViewColumn> columns = getDataColumns();
            data = new ArrayList<>(columns.size());
            for (int i = 0;i < columns.size();i++)
            {
                data.add(getDataInternal(element, i));
            }
            dataCache.put(element, data);
        }
        return data.get(column);
    }

    protected abstract Object getDataInternal(E element, int column);

    @Override
    public void setData(Object value, E element, int column)
    {
        dataCache.remove(element);
    }
}
