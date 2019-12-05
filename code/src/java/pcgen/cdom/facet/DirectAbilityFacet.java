/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractCNASEnforcingFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.helper.CNAbilitySelection;

/**
 * DirectAbilityFacet is a Facet that tracks the CategorizedAbilitySelection
 * that have been granted to a Player Character.
 */
public class DirectAbilityFacet extends AbstractCNASEnforcingFacet
{

    public void removeAll(CharID id, Object source)
    {
        Objects.requireNonNull(source, "Attempt to remove object with null source from list");
        List<List<SourcedCNAS>> list = getList(id);
        if (list == null)
        {
            return;
        }
        List<CNAbilitySelection> removed = new ArrayList<>();
        List<CNAbilitySelection> added = new ArrayList<>();
        for (Iterator<List<SourcedCNAS>> listIT = list.iterator();listIT.hasNext();)
        {
            List<SourcedCNAS> array = listIT.next();
            int length = array.size();
            //Iterate backwards, so that we remove harmless items first
            for (int j = length - 1;j >= 0;j--)
            {
                SourcedCNAS sc = array.get(j);
                if (source.equals(sc.source))
                {
                    //fix the array here
                    array.remove(j);
                    boolean needRemove = true;
                    if (!array.isEmpty())
                    {
                        CNAbilitySelection newPrimary = array.get(0).cnas;
                        //Only fire if the CNAS differs to avoid churn
                        if (!sc.cnas.equals(newPrimary) && (j == 0))
                        {
                            added.add(newPrimary);
                        } else
                        {
                            needRemove = false;
                        }
                    }
                    if (needRemove)
                    {
                        removed.add(sc.cnas);
                    }
                }
            }
            if (array.isEmpty())
            {
                listIT.remove();
            }
        }
        for (CNAbilitySelection cnas : removed)
        {
            fireDataFacetChangeEvent(id, cnas, DataFacetChangeEvent.DATA_REMOVED);
        }
        for (CNAbilitySelection cnas : added)
        {
            fireDataFacetChangeEvent(id, cnas, DataFacetChangeEvent.DATA_ADDED);
        }
    }
}
