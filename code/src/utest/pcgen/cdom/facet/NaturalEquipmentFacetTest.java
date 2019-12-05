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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractConsolidatingFacetTest;
import pcgen.core.Equipment;

public class NaturalEquipmentFacetTest extends
        AbstractConsolidatingFacetTest<Equipment, Equipment>
{
    private static int n = 0;

    private NaturalEquipmentFacet facet = new NaturalEquipmentFacet();

    @Override
    protected AbstractSourcedListFacet<CharID, Equipment> getFacet()
    {
        return facet;
    }

    @Override
    protected Equipment getObject()
    {
        Equipment eq = new Equipment();
        eq.setName("EQ" + n++);
        eq.addType(Type.NATURAL);
        return eq;
    }

    @Override
    protected DataFacetChangeListener<CharID, Equipment> getListener()
    {
        return facet;
    }

    @Override
    protected Equipment getSourceObject()
    {
        return getObject();
    }

    @Override
    protected Equipment getConverted(Equipment e)
    {
        return e;
    }
}
