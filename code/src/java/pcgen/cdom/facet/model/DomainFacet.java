/*
 * Copyright (c) Thomas Parker, 2009-14.
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
package pcgen.cdom.facet.model;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.Domain;
import pcgen.output.publish.OutputDB;

/**
 * DomainFacet is a Facet that tracks the Domains possessed by a Player
 * Character.
 */
public class DomainFacet extends AbstractSingleSourceListFacet<Domain, ClassSource>
        implements PerspectiveLocation, SetFacet<CharID, Domain>
{
    public void init()
    {
        CorePerspectiveDB.register(CorePerspective.DOMAIN, FacetBehavior.MODEL, this);
        OutputDB.register("domains", this);
    }

    @Override
    public String getIdentity()
    {
        return "Character Domains";
    }

}
