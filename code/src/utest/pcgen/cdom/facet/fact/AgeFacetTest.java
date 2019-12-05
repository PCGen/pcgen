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
package pcgen.cdom.facet.fact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;

import org.junit.jupiter.api.Test;

public class AgeFacetTest extends AbstractItemFacetTest<Integer>
{

    private final AgeFacet facet = new AgeFacet();

    private final CharID id = CharID.getID(DataSetID.getID());

    @Override
    protected AbstractItemFacet<CharID, Integer> getFacet()
    {
        return facet;
    }

    private int n = 0;

    @Override
    protected Integer getItem()
    {
        return n++;
    }

    @Test
    public void testItemSetMultGetAge()
    {
        assertEquals(0, facet.getAge(id));
        getFacet().set(id, 4);
        assertEquals(4, facet.getAge(id));
        getFacet().set(id, 2);
        assertEquals(2, facet.getAge(id));
        // Remove
        getFacet().remove(id);
        assertEquals(0, facet.getAge(id));
    }
}
