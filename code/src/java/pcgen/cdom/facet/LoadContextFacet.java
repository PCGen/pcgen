/*
 * Copyright (c) Thomas Parker, 2018.
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
package pcgen.cdom.facet;

import java.lang.ref.WeakReference;

import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.output.factory.GlobalVarModelFactory;
import pcgen.output.publish.OutputDB;
import pcgen.rules.context.LoadContext;

/**
 * This stores the LoadContext for each Data Load (DataSetID).
 */
public class LoadContextFacet extends AbstractItemFacet<DataSetID, WeakReference<LoadContext>>
        implements DataSetInitializedFacet
{

    private DataSetInitializationFacet datasetInitializationFacet;

    @Override
    public synchronized void initialize(LoadContext context)
    {
        set(context.getDataSetID(), new WeakReference<>(context));
    }

    /**
     * Initialize (register this facet as a DataSet initialized facet)
     */
    public void init()
    {
        datasetInitializationFacet.addDataSetInitializedFacet(this);
        OutputDB.registerModelFactory("val", new GlobalVarModelFactory());
    }

    public void setDataSetInitializationFacet(DataSetInitializationFacet datasetInitializationFacet)
    {
        this.datasetInitializationFacet = datasetInitializationFacet;
    }
}
