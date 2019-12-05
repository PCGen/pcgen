/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;

public class AbstractScopeFacetConsolidator<IDT extends PCGenIdentifier, S, D> extends AbstractListFacet<CharID, D>
        implements ScopeFacetChangeListener<CharID, S, D>
{

    @Override
    public void dataAdded(ScopeFacetChangeEvent<CharID, S, D> dfce)
    {
        add(dfce.getCharID(), dfce.getCDOMObject());
    }

    @Override
    public void dataRemoved(ScopeFacetChangeEvent<CharID, S, D> dfce)
    {
        remove(dfce.getCharID(), dfce.getCDOMObject());
    }

}
