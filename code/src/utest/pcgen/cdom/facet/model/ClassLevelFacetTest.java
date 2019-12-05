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
package pcgen.cdom.facet.model;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.testsupport.AbstractSourcedListFacetTest;

public class ClassLevelFacetTest extends
        AbstractSourcedListFacetTest<PCClassLevel>
{

    private ClassLevelFacet facet = new ClassLevelFacet();

    @Override
    protected AbstractSourcedListFacet<CharID, PCClassLevel> getFacet()
    {
        return facet;
    }

    public static int n = 0;

    @Override
    protected PCClassLevel getObject()
    {
        PCClassLevel wp = new PCClassLevel();
        wp.setName("WP" + n++);
        return wp;
    }

}
