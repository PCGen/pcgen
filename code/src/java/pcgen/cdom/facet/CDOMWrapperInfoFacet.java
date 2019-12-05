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

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSubAssociationFacet;
import pcgen.core.Ability;
import pcgen.core.ArmorProf;
import pcgen.core.Campaign;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.ShieldProf;
import pcgen.core.SizeAdjustment;
import pcgen.core.WeaponProf;
import pcgen.output.actor.DescriptionActor;
import pcgen.output.actor.DisplayNameActor;
import pcgen.output.actor.EqTypeActor;
import pcgen.output.actor.InfoActor;
import pcgen.output.actor.IsVisibleToActor;
import pcgen.output.actor.KeyActor;
import pcgen.output.actor.OutputNameActor;
import pcgen.output.actor.SourceActor;
import pcgen.output.actor.TypeActor;
import pcgen.output.base.OutputActor;
import pcgen.rules.context.LoadContext;

/**
 * This Facet stores the Actors usable in Freemarker for a given DataSetID.
 */
public class CDOMWrapperInfoFacet extends AbstractSubAssociationFacet<DataSetID, Class<?>, String, OutputActor<?>>
        implements DataSetInitializedFacet
{

    private DataSetInitializationFacet datasetInitializationFacet;

    @Override
    public void initialize(LoadContext context)
    {
        initialize(context.getDataSetID());
    }

    /**
     * Initializes this facet for the given DataSetID.
     *
     * @param dsID The DataSetID for which this facet should be initialized
     */
    public void initialize(DataSetID dsID)
    {
        set(dsID, CDOMObject.class, "source", new SourceActor());
        set(dsID, CDOMObject.class, "info", new InfoActor());
        set(dsID, CDOMObject.class, "key", new KeyActor());
        set(dsID, CDOMObject.class, "displayname", new DisplayNameActor());
        set(dsID, CDOMObject.class, "type", new TypeActor());
        set(dsID, PObject.class, "desc", new DescriptionActor(ListKey.DESCRIPTION));
        set(dsID, PObject.class, "benefit", new DescriptionActor(ListKey.BENEFIT));
        OutputNameActor outputNameActor = new OutputNameActor();
        set(dsID, Ability.class, "outputname", outputNameActor);
        set(dsID, ArmorProf.class, "outputname", outputNameActor);
        set(dsID, Campaign.class, "outputname", outputNameActor);
        set(dsID, Deity.class, "outputname", outputNameActor);
        set(dsID, Domain.class, "outputname", outputNameActor);
        set(dsID, Equipment.class, "outputname", outputNameActor);
        set(dsID, Kit.class, "outputname", outputNameActor);
        set(dsID, Language.class, "outputname", outputNameActor);
        set(dsID, PCAlignment.class, "outputname", outputNameActor);
        set(dsID, PCCheck.class, "outputname", outputNameActor);
        set(dsID, PCClass.class, "outputname", outputNameActor);
        set(dsID, PCStat.class, "outputname", outputNameActor);
        set(dsID, PCTemplate.class, "outputname", outputNameActor);
        set(dsID, Race.class, "outputname", outputNameActor);
        set(dsID, ShieldProf.class, "outputname", outputNameActor);
        set(dsID, SizeAdjustment.class, "outputname", outputNameActor);
        set(dsID, WeaponProf.class, "outputname", outputNameActor);
        set(dsID, Equipment.class, "type", new EqTypeActor());
        set(dsID, CDOMObject.class, "visibleto", new IsVisibleToActor());
    }

    /**
     * Returns the Actor for the given DataSetID, Identity (class) and key.
     *
     * @param dsID     The DataSetID to identify the actor to be returned
     * @param identity The Identity (class) for which the actor should be returned
     * @param key      The key of the actor to be returned
     * @return the Actor for the given DataSetID, Identity (class) and key
     */
    public <T> OutputActor<? super T> getActor(DataSetID dsID, Class<T> identity, String key)
    {
        @SuppressWarnings("unchecked")
        OutputActor<T> actor = (OutputActor<T>) get(dsID, identity, key);
        if (actor == null)
        {
            Class<? super T> parent = identity.getSuperclass();
            if (parent == null)
            {
                //If we got to Object, then there is nothing left to do
                return null;
            }
            return getActor(dsID, parent, key);
        }
        return actor;
    }

    @Override
    protected Map<String, OutputActor<?>> getSubComponentMap()
    {
        return new HashMap<>();
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
