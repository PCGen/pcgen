/*
 * Copyright (c) Thomas Parker, 2010-14.
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

import pcgen.cdom.facet.base.AbstractItemConvertingFacet;
import pcgen.cdom.facet.model.ArmorProfProviderFacet;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.helper.SimpleArmorProfProvider;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.ArmorProf;

/**
 * AutoListArmorProfFacet is a Facet that tracks the ArmorProfs that have been
 * granted to a Player Character by AUTO:ARMORPROF|%LIST and converts them to
 * ProfProvider objects.
 */
public class AutoListArmorProfFacet extends AbstractItemConvertingFacet<ArmorProf, ProfProvider<ArmorProf>>
        implements PerspectiveLocation
{

    private ArmorProfProviderFacet armorProfProviderFacet;

    public void setArmorProfProviderFacet(ArmorProfProviderFacet armorProfProviderFacet)
    {
        this.armorProfProviderFacet = armorProfProviderFacet;
    }

    public void init()
    {
        addDataFacetChangeListener(armorProfProviderFacet);
        CorePerspectiveDB.register(CorePerspective.ARMORPROF, FacetBehavior.INPUT, this);
    }

    /**
     * Converts an ArmorProf into a {@code ProfProvider<ArmorProf>}. This is required
     * because the %LIST (which listens to a CHOOSE:ARMORPROF) is given an
     * ArmorProf object, rather than a ProfProvider object. This therefore wraps
     * the ArmorProf object into a ProfProvider, since the master list of
     * proficiencies for a Player Character are stored as ProfProviders.
     *
     * @param ap The ArmorProf that was granted to a Player Character
     * @return A new ProfProvider that wraps the given ArmorProf into a
     * ProfProvider
     */
    @Override
    protected ProfProvider<ArmorProf> convert(ArmorProf ap)
    {
        return new SimpleArmorProfProvider(ap);
    }

    @Override
    public String getIdentity()
    {
        return "AUTO:ARMORPROF|%LIST";
    }

}
