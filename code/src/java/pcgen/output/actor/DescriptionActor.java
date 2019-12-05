/*
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 *
 */
package pcgen.output.actor;

import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.helper.SpringHelper;
import pcgen.core.Description;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.output.base.OutputActor;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A DescriptionActor is designed to process an interpolation and convert the
 * a list of descriptions from a PObject into a TemplateModel.
 * <p>
 * Note that the actual name of the interpolation is stored externally to this
 * Actor (in CDOMObjectWrapperInfo to be precise)
 */
public class DescriptionActor implements OutputActor<PObject>
{

    private final ListKey<Description> listKey;

    /**
     * Constructs a new DescriptionActor with the given ListKey as the ListKey used to
     * fetch the Description from the CDOMObject.
     *
     * @param listKey the ListKey used to fetch the Description from the CDOMObject
     */
    public DescriptionActor(ListKey<Description> listKey)
    {
        this.listKey = listKey;
    }

    @Override
    public TemplateModel process(CharID id, PObject d) throws TemplateModelException
    {
        List<Description> theBenefits = d.getListFor(listKey);
        if (theBenefits == null)
        {
            return FacetLibrary.getFacet(ObjectWrapperFacet.class).wrap(id, Constants.EMPTY_STRING);
        }
        PlayerCharacterTrackingFacet charStore = SpringHelper.getBean(PlayerCharacterTrackingFacet.class);
        PlayerCharacter aPC = charStore.getPC(id);
        final StringBuilder buf = new StringBuilder(250);
        boolean needSpace = false;
        for (final Description desc : theBenefits)
        {
            final String str = desc.getDescription(aPC, Collections.singletonList(d));
            if (!str.isEmpty())
            {
                if (needSpace)
                {
                    buf.append(' ');
                }
                buf.append(str);
                needSpace = true;
            }
        }
        return FacetLibrary.getFacet(ObjectWrapperFacet.class).wrap(id, buf.toString());
    }

}
