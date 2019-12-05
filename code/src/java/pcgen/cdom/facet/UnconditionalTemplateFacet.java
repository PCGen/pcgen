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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

/**
 * UnconditionalTemplateFacet is a Facet that tracks the PCTemplates that have
 * been granted to a Player Character.
 */
public class UnconditionalTemplateFacet extends AbstractListFacet<CharID, PCTemplate>
{
    private TemplateFacet templateFacet;

    public void init()
    {
        addDataFacetChangeListener(templateFacet);
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

}
