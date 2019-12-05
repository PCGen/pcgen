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
package pcgen.cdom.facet.input;

import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.DataSetInitializationFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;

public class MasterUsableSkillFacet extends AbstractSourcedListFacet<DataSetID, Skill>
        implements DataSetInitializedFacet
{

    private DataSetInitializationFacet dataSetInitializationFacet;

    @Override
    public synchronized void initialize(LoadContext context)
    {
        DataSetID id = context.getDataSetID();
        if (getCache(id) == null)
        {
            for (Skill sk : context.getReferenceContext().getConstructedCDOMObjects(Skill.class))
            {
                if (!sk.getSafe(ObjectKey.EXCLUSIVE) && sk.getSafe(ObjectKey.USE_UNTRAINED))
                {
                    add(id, sk, sk);
                }
            }
        }
    }

    public void setDataSetInitializationFacet(DataSetInitializationFacet dataSetInitializationFacet)
    {
        this.dataSetInitializationFacet = dataSetInitializationFacet;
    }

    public void init()
    {
        dataSetInitializationFacet.addDataSetInitializedFacet(this);
    }
}
