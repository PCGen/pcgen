/*
 * Copyright (c) Thomas Parker, 2015.
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

import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.formula.MonitorableVariableStore;

/**
 * This Facet stores the VariableStore (where the results of calculations in the
 * new formula system are stored) for each PlayerCharacter (identified by their
 * CharID).
 */
public class VariableStoreFacet extends AbstractItemFacet<CharID, MonitorableVariableStore>
{

    /**
     * The global LoadContextFacet used to get VariableIDs
     */
    private final LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    public <T> T getValue(CharID id, VariableID<T> varID)
    {
        T value = get(id).get(varID);
        if (value == null)
        {
            return loadContextFacet.get(id.getDatasetID()).get().getVariableContext()
                    .getDefaultValue(varID.getFormatManager());
        }
        return value;
    }

    @Override
    public void copyContents(CharID source, CharID copy)
    {
        MonitorableVariableStore obj = get(source);
        if (obj != null)
        {
            MonitorableVariableStore replacement = new MonitorableVariableStore();
            replacement.importFrom(get(source));
            setCache(copy, replacement);
        }
    }
}
