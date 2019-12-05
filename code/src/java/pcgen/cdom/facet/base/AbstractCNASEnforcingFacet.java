/*
 * Copyright (c) Thomas Parker, 2010.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.CNAbilitySelectionUtilities;

public class AbstractCNASEnforcingFacet extends AbstractDataFacet<CharID, CNAbilitySelection>
        implements DataFacetChangeListener<CharID, CNAbilitySelection>
{
    public boolean isEmpty(CharID id)
    {
        List<List<SourcedCNAS>> list = getList(id);
        return (list == null) || list.isEmpty();
    }

    public boolean add(CharID id, CNAbilitySelection cnas, Object source)
    {
        Objects.requireNonNull(cnas, "Attempt to add null to list");
        Objects.requireNonNull(source, "Attempt to add object with null source to list");
        List<List<SourcedCNAS>> list = getConstructingList(id);
        for (List<SourcedCNAS> slist : list)
        {
            CNAbilitySelection main = slist.get(0).cnas;
            if (!CNAbilitySelectionUtilities.canCoExist(main, cnas))
            {
                slist.add(new SourcedCNAS(cnas, source));
                return false;
            }
        }
        List<SourcedCNAS> newList = new ArrayList<>(1);
        newList.add(new SourcedCNAS(cnas, source));
        list.add(newList);
        fireDataFacetChangeEvent(id, cnas, DataFacetChangeEvent.DATA_ADDED);
        return true;
    }

    public boolean remove(CharID id, CNAbilitySelection cnas, Object source)
    {
        Objects.requireNonNull(cnas, "Attempt to remove null from list");
        Objects.requireNonNull(source, "Attempt to remove object with null source from list");
        List<List<SourcedCNAS>> list = getList(id);
        if (list == null)
        {
            return false;
        }
        for (Iterator<List<SourcedCNAS>> listIT = list.iterator();listIT.hasNext();)
        {
            List<SourcedCNAS> array = listIT.next();
            int length = array.size();
            //Iterate backwards, so that we remove harmless items first
            for (int j = length - 1;j >= 0;j--)
            {
                SourcedCNAS sc = array.get(j);
                if (cnas.equals(sc.cnas) && source.equals(sc.source))
                {
                    //fix the array here
                    if ((j == 0) && (length == 1))
                    {
                        //There is no alternative, remove the array from the list;
                        listIT.remove();
                        fireDataFacetChangeEvent(id, cnas, DataFacetChangeEvent.DATA_REMOVED);
                        return true;
                    } else
                    {
                        array.remove(j);
                        CNAbilitySelection newPrimary = array.get(0).cnas;
                        //Only fire if the CNAS differs to avoid churn
                        if (!cnas.equals(newPrimary) && (j == 0))
                        {
                            fireDataFacetChangeEvent(id, cnas, DataFacetChangeEvent.DATA_REMOVED);
                            fireDataFacetChangeEvent(id, newPrimary, DataFacetChangeEvent.DATA_ADDED);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public Collection<CNAbilitySelection> getSet(CharID id)
    {
        List<List<SourcedCNAS>> list = getList(id);
        if (list == null)
        {
            return Collections.emptyList();
        }
        List<CNAbilitySelection> returnList = new ArrayList<>();
        for (List<SourcedCNAS> array : list)
        {
            returnList.add(array.get(0).cnas);
        }
        return returnList;
    }

    @SuppressWarnings("unchecked")
    protected List<List<SourcedCNAS>> getList(CharID id)
    {
        return (List<List<SourcedCNAS>>) this.getCache(id);
    }

    private List<List<SourcedCNAS>> getConstructingList(CharID id)
    {
        List<List<SourcedCNAS>> list = getList(id);
        if (list == null)
        {
            list = new ArrayList<>();
            setCache(id, list);
        }
        return list;
    }

    @Override
    public void copyContents(CharID source, CharID copy)
    {
        List<List<SourcedCNAS>> list = getList(source);
        if (list != null)
        {
            List<List<SourcedCNAS>> constructingList = getConstructingList(copy);
            for (List<SourcedCNAS> orig : list)
            {
                List<SourcedCNAS> newCnasList = new ArrayList<>(orig);
                constructingList.add(newCnasList);
            }
        }
    }

    public int getCount(CharID id)
    {
        List<List<SourcedCNAS>> list = getList(id);
        return (list == null) ? 0 : list.size();
    }

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
    }

    protected static class SourcedCNAS
    {
        public final CNAbilitySelection cnas;
        public final Object source;

        public SourcedCNAS(CNAbilitySelection cnas, Object source)
        {
            this.cnas = cnas;
            this.source = source;
        }

        @Override
        public String toString()
        {
            return cnas + " (src: " + source + ")";
        }

        @Override
        public int hashCode()
        {
            return source.hashCode() ^ cnas.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (o instanceof SourcedCNAS)
            {
                SourcedCNAS other = (SourcedCNAS) o;
                return cnas.equals(other.cnas) && source.equals(other.source);
            }
            return false;
        }

    }
}
