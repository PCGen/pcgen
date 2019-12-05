/*
 * Copyright (c) Thomas Parker, 2014.
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

import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;

/**
 * EquipmentTypeFacet is a Facet that calculates the TYPE items that have been
 * added to a Equipment in a dataset.
 */
public class EquipmentTypeFacet extends AbstractListFacet<DataSetID, Type> implements DataSetInitializedFacet
{

    private DataSetInitializationFacet datasetInitializationFacet;

    @Override
    public void initialize(LoadContext context)
    {
        DataSetID id = context.getDataSetID();
        for (Equipment e : context.getReferenceContext().getConstructedCDOMObjects(Equipment.class))
        {
            for (Type t : e.getTrueTypeList(false))
            {
                add(id, t);
            }
        }
    }

    public void setDataSetInitializationFacet(DataSetInitializationFacet datasetInitializationFacet)
    {
        this.datasetInitializationFacet = datasetInitializationFacet;
    }

    public void init()
    {
        datasetInitializationFacet.addDataSetInitializedFacet(this);
    }
}
