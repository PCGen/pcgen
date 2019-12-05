/*
 * Copyright (c) 2010-14 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;

/**
 * The Class {@code MasterSkillFacet} caches a copy of all class skill
 * lists. This allows faster checking of whether skills are class skills for a
 * character class. Note this is a "global" facet in that it does not have
 * method that depend on CharID (they are not character specific).
 */
public class MasterSkillFacet extends AbstractScopeFacet<DataSetID, ClassSkillList, Skill>
        implements DataSetInitializedFacet
{

    private DataSetInitializationFacet datasetInitializationFacet;

    @Override
    public synchronized void initialize(LoadContext context)
    {
        DataSetID dsID = context.getDataSetID();
        if (getCache(dsID) == null)
        {
            MasterListInterface masterLists = SettingsHandler.getGame().getMasterLists();
            for (CDOMReference ref : masterLists.getActiveLists())
            {
                Collection objects = masterLists.getObjects(ref);
                for (Object cl : ref.getContainedObjects())
                {
                    if (cl instanceof ClassSkillList)
                    {
                        addAll(dsID, (ClassSkillList) cl, objects, cl);
                    }
                }
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
