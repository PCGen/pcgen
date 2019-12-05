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
package pcgen.cdom.facet.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.util.enumeration.Load;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class UnencumberedLoadFacetTest extends
        AbstractExtractingFacetTest<CDOMObject, Load>
{

    private UnencumberedLoadFacet facet = new UnencumberedLoadFacet();
    private Load[] target;
    private CDOMObject[] source;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        CDOMObject cdo1 = new PCTemplate();
        cdo1.setName("Template1");
        CDOMObject cdo2 = new Race();
        cdo2.setName("Race1");
        Load st1 = Load.HEAVY;
        Load st2 = Load.MEDIUM;
        cdo1.put(ObjectKey.UNENCUMBERED_LOAD, st1);
        cdo2.put(ObjectKey.UNENCUMBERED_LOAD, st2);
        source = new CDOMObject[]{cdo1, cdo2};
        target = new Load[]{st1, st2};
    }

    @Override
    protected AbstractSourcedListFacet<CharID, Load> getFacet()
    {
        return facet;
    }

    @Override
    protected Load getObject()
    {
        return Load.HEAVY;
    }

    @Override
    protected Load getAltObject()
    {
        return Load.MEDIUM;
    }

    @Override
    protected Load getThirdObject()
    {
        return Load.LIGHT;
    }

    @Override
    protected CDOMObject getContainingObject(int i)
    {
        return source[i];
    }

    @Override
    protected DataFacetChangeListener<CharID, CDOMObject> getListener()
    {
        return facet;
    }

    @Override
    protected Load getTargetObject(int i)
    {
        return target[i];
    }

    @Test
    public void testMultipleLoad()
    {
        assertEquals(Load.LIGHT, facet.getBestLoad(id));
        Object source1 = new Object();
        DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(id, source[1], source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(Load.MEDIUM, facet.getBestLoad(id));
        dfce =
                new DataFacetChangeEvent<>(id, source[0], source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(Load.HEAVY, facet.getBestLoad(id));
    }

    @Test
    public void testIgnoreLoad()
    {
        assertTrue(facet.ignoreLoad(id, Load.LIGHT));
        assertFalse(facet.ignoreLoad(id, Load.MEDIUM));
        Object source1 = new Object();
        DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(id, source[1], source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertTrue(facet.ignoreLoad(id, Load.LIGHT));
        assertTrue(facet.ignoreLoad(id, Load.MEDIUM));
        assertFalse(facet.ignoreLoad(id, Load.HEAVY));
        dfce =
                new DataFacetChangeEvent<>(id, source[0], source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertTrue(facet.ignoreLoad(id, Load.LIGHT));
        assertTrue(facet.ignoreLoad(id, Load.MEDIUM));
        assertTrue(facet.ignoreLoad(id, Load.HEAVY));
    }
}
