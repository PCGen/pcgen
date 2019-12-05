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
package pcgen.cdom.facet.fact;

import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.output.publish.OutputDB;

/**
 * GenderFacet is a Facet that tracks the Gender of a Player Character.
 */
public class GenderFacet extends AbstractItemFacet<CharID, Gender> implements ItemFacet<CharID, Gender>
{

    private TemplateFacet templateFacet;

    /**
     * Returns the Gender for the Player Character represented by the given
     * CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           Gender should be returned
     * @return The Gender for the Player Character represented by the given
     * CharID
     */
    public Gender getGender(CharID id)
    {
        Gender g = findTemplateGender(id);
        if (g == null)
        {
            g = get(id);
        }
        return g == null ? Gender.getDefaultValue() : g;
    }

    /**
     * Returns true if the Gender can be set for the Player Character
     * represented by the given CharID. Returns false if the Gender of the
     * Player Character is currently controlled by a Gender Lock applied by a
     * PCTemplate.
     *
     * @param id The CharID representing the Player Character to query to see
     *           if the Gender can be set for that Player Character
     * @return true if the Gender can be set for the Player Character
     * represented by the given CharID; false otherwise
     */
    public boolean canSetGender(CharID id)
    {
        return findTemplateGender(id) == null;
    }

    /**
     * Returns the Gender if the Gender has been locked by a PCTemplate
     * possessed by the Player Character represented by the given CharID. null
     * will be returned if the Player Character does not possess a PCTemplate or
     * if the PCTemplates possessed by the Player Character do not exert a
     * Gender Lock on the Player Character.
     *
     * @param id The CharID representing the Player Character to check if a
     *           Gender Lock exists on that Player Character
     * @return A Gender, if the Gender has been locked by a PCTemplate possessed
     * by the Player Character represented by the given CharID; null
     * otherwise
     */
    private Gender findTemplateGender(CharID id)
    {
        Gender g = null;

        for (PCTemplate template : templateFacet.getSet(id))
        {
            Gender lock = template.get(ObjectKey.GENDER_LOCK);
            if (lock != null)
            {
                g = lock;
            }
        }

        return g;
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    /**
     * Initializes the connections for GenderFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the GenderFacet.
     */
    public void init()
    {
        OutputDB.register("gender", this);
    }
}
