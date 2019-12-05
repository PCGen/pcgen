/*
 * Copyright (c) Thomas Parker, 2012.
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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.meta.CorePerspective;
import pcgen.cdom.meta.CorePerspectiveDB;
import pcgen.cdom.meta.FacetBehavior;
import pcgen.cdom.meta.PerspectiveLocation;
import pcgen.core.Language;

/**
 * FreeLanguageFacet is a Facet that tracks the Languages that have been granted
 * to a Player Character for free.
 */
public class FreeLanguageFacet extends AbstractSourcedListFacet<CharID, Language> implements PerspectiveLocation
{

    private LanguageFacet languageFacet;

    public void setLanguageFacet(LanguageFacet languageFacet)
    {
        this.languageFacet = languageFacet;
    }

    public void init()
    {
        addDataFacetChangeListener(languageFacet);
        CorePerspectiveDB.register(CorePerspective.LANGUAGE, FacetBehavior.INPUT, this);
    }

    @Override
    public String getIdentity()
    {
        return "CHOOSE:LANGAUTO";
    }
}
