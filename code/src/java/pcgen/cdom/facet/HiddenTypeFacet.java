/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.facet;

import java.util.List;

import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;

public class HiddenTypeFacet extends AbstractScopeFacet<DataSetID, Class<?>, Type> implements DataSetInitializedFacet
{
    private DataSetInitializationFacet datasetInitializationFacet;

    @Override
    public void initialize(LoadContext context)
    {
        for (Campaign c : context.getLoadedCampaigns())
        {
            loadCampaignHiddenTypes(context.getDataSetID(), c);
        }
    }

    private void loadCampaignHiddenTypes(DataSetID id, Campaign c)
    {
        loadHiddenTypes(id, ListKey.HIDDEN_Equipment, Equipment.class, c);
        loadHiddenTypes(id, ListKey.HIDDEN_Ability, Ability.class, c);
        loadHiddenTypes(id, ListKey.HIDDEN_Skill, Skill.class, c);
        for (Campaign subCamp : c.getSubCampaigns())
        {
            loadCampaignHiddenTypes(id, subCamp);
        }
    }

    private void loadHiddenTypes(DataSetID id, ListKey<String> listKey, Class<?> cl, Campaign c)
    {
        List<String> hiddentypes = c.getSafeListFor(listKey);
        for (String s : hiddentypes)
        {
            add(id, cl, Type.getConstant(s), c);
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
