/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;
import pcgen.core.LevelInfo;
import pcgen.core.XPTable;

import org.junit.jupiter.api.Test;

public class XPTableFacetTest extends AbstractItemFacetTest<XPTable>
{

    private final XPTableFacet facet = new XPTableFacet();

    @Override
    protected AbstractItemFacet<CharID, XPTable> getFacet()
    {
        return facet;
    }

    private int n = 0;

    @Override
    protected XPTable getItem()
    {
        XPTable xpt = new XPTable();
        xpt.setName("XPTable" + n++);
        return xpt;
    }

    @Test
    public void testGetLevelInfoNegLevel()
    {
        XPTable t1 = getItem();
        CharID id = getCharID();
        facet.set(id, t1);
        assertNull(facet.getLevelInfo(id, -1));
    }

    @Test
    public void testGetLevelInfoZeroLevel()
    {
        XPTable t1 = getItem();
        CharID id = getCharID();
        facet.set(id, t1);
        assertNull(facet.getLevelInfo(id, 0));
    }

    @Test
    public void testGetLevelInfoNoTable()
    {
        CharID id = getCharID();
        assertNull(facet.getLevelInfo(id, 1));
    }

    @Test
    public void testGetLevelInfoEmptyTable()
    {
        XPTable t1 = getItem();
        CharID id = getCharID();
        facet.set(id, t1);
        assertNull(facet.getLevelInfo(id, 1));
    }

    @Test
    public void testGetLevelInfo()
    {
        XPTable t1 = getItem();
        LevelInfo li = new LevelInfo();
        t1.addLevelInfo("1", li);
        CharID id = getCharID();
        facet.set(id, t1);
        LevelInfo xpt = facet.getLevelInfo(id, 1);
        assertNotNull(xpt);
        assertSame(li, xpt);
    }

    @Test
    public void testGetLevelInfoWrongLevel()
    {
        XPTable t1 = getItem();
        LevelInfo li = new LevelInfo();
        t1.addLevelInfo("2", li);
        CharID id = getCharID();
        facet.set(id, t1);
        LevelInfo xpt = facet.getLevelInfo(id, 1);
        assertNull(xpt);
    }

    @Test
    public void testGetLevelInfoLevel()
    {
        XPTable t1 = getItem();
        LevelInfo li = new LevelInfo();
        t1.addLevelInfo("LEVEL", li);
        CharID id = getCharID();
        facet.set(id, t1);
        LevelInfo xpt = facet.getLevelInfo(id, 1);
        assertNotNull(xpt);
        assertSame(li, xpt);
    }


}
