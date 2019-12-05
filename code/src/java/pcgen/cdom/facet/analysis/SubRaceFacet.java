/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet.analysis;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

/**
 * SubRaceFacet is a Facet that returns information about the SubRace of a
 * Player Character
 */
public class SubRaceFacet
{
    private TemplateFacet templateFacet;

    /**
     * Returns the SubRace of the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           SubRace will be returned
     * @return The Character SubRace of the Player Character represented by the
     * given CharID
     */
    public String getSubRace(CharID id)
    {
        String subRace = null;

        for (PCTemplate template : templateFacet.getSet(id))
        {
            final String tempSubRace = getTemplateSubRace(template);

            if (tempSubRace != null)
            {
                subRace = tempSubRace;
            }
        }

        return subRace;
    }

    private String getTemplateSubRace(PCTemplate template)
    {
        /*
         * TODO This should be type safe to return a SubRace
         */
        SubRace sr = template.get(ObjectKey.SUBRACE);
        if (sr == null)
        {
            if (template.getSafe(ObjectKey.USETEMPLATENAMEFORSUBRACE))
            {
                return template.getDisplayName();
            }
            return null;
        }
        return sr.toString();
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }
}
