/*
 * Copyright (c) Thomas Parker, 2010.
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
import pcgen.cdom.facet.model.ShieldProfProviderFacet;
import pcgen.cdom.helper.ProfProvider;
import pcgen.cdom.helper.SimpleShieldProfProvider;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.ShieldProf;

/**
 * AutoListShieldProfFacet is a Facet that tracks the ShieldProfs that have been
 * granted to a Player Character by AUTO:SHIELDPROF|%LIST and converts them to
 * ProfProvider objects.
 */
public class AutoListShieldProfFacet extends AbstractItemConvertingFacet<ShieldProf, ProfProvider<ShieldProf>>
        implements PerspectiveLocation
{

    private ShieldProfProviderFacet shieldProfProviderFacet;

    public void setShieldProfProviderFacet(ShieldProfProviderFacet shieldProfProviderFacet)
    {
        this.shieldProfProviderFacet = shieldProfProviderFacet;
    }

    public void init()
    {
        addDataFacetChangeListener(shieldProfProviderFacet);
        CorePerspectiveDB.register(CorePerspective.SHIELDPROF, FacetBehavior.INPUT, this);
    }

    /**
     * Converts an ArmorProf into a {@code ProfProvider<ArmorProf>}. This is required
     * because the %LIST (which listens to a CHOOSE:ARMORPROF) is given an
     * ArmorProf object, rather than a ProfProvider object. This therefore wraps
     * the ArmorProf object into a ProfProvider, since the master list of
     * proficiencies for a Player Character are stored as ProfProviders.
     *
     * @param ap The ShieldProf that was granted to a Player Character
     * @return A new ProfProvider that wraps the given ShieldProf into a
     * ProfProvider
     */
    @Override
    protected ProfProvider<ShieldProf> convert(ShieldProf ap)
    {
        return new SimpleShieldProfProvider(ap);
    }

    @Override
    public String getIdentity()
    {
        return "AUTO:SHIELDPROF|%LIST";
    }
}
