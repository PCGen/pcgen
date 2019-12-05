/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.input;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.inst.Dynamic;

/**
 * DynamicWatchingFacet is a Facet that determines all Dynamic Objects that have been
 * granted to a Player Character.
 */
public class DynamicWatchingFacet extends AbstractSourcedListFacet<CharID, Dynamic>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();
        List<CDOMReference<Dynamic>> granted = cdo.getListFor(ListKey.GRANTED);
        if (granted == null)
        {
            return;
        }
        for (CDOMReference<Dynamic> reference : granted)
        {
            for (Dynamic dynamic : reference.getContainedObjects())
            {
                add(id, dynamic, cdo);
            }
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();
        List<CDOMReference<Dynamic>> granted = cdo.getListFor(ListKey.GRANTED);
        if (granted == null)
        {
            return;
        }
        for (CDOMReference<Dynamic> reference : granted)
        {
            for (Dynamic dynamic : reference.getContainedObjects())
            {
                remove(id, dynamic, cdo);
            }
        }
    }
}
