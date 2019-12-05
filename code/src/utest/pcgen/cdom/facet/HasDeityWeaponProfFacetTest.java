/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.facet;

import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.testsupport.AbstractQualifiedListFacetTest;
import pcgen.core.QualifiedObject;

public class HasDeityWeaponProfFacetTest extends
        AbstractQualifiedListFacetTest<QualifiedObject<Boolean>>
{
    private final AbstractQualifiedListFacet<QualifiedObject<Boolean>> facet = new HasDeityWeaponProfFacet();

    @Override
    protected AbstractQualifiedListFacet<QualifiedObject<Boolean>> getFacet()
    {
        return facet;
    }

    @Override
    protected QualifiedObject<Boolean> getObject()
    {
        return new QualifiedObject<>(Boolean.TRUE);
    }

    @Override
    protected QualifiedObject<Boolean> getAltObject()
    {
        return new QualifiedObject<>(Boolean.FALSE);
    }

}
