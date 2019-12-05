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
package pcgen.cdom.testsupport;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractAssociationFacet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractAssociationFacetTest<CT, ST>
{
    private CharID id;
    private CharID altid;

    @BeforeEach
    void setUp()
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
    }

    @Test
    public void testObjUnsetZeroCount()
    {
        assertEquals(0, getFacet().getCount(id));
    }

    @Test
    public void testObjUnsetEmpty()
    {
        assertTrue(getFacet().isEmpty(id));
    }

    @Test
    public void testRemoveAllUnsetEmpty()
    {
        // Not particularly a test, just make sure it doesn't throw an exception
        getFacet().removeAll(id);
    }

    @Test
    public void testObjUnsetEmptySet()
    {
        assertNotNull(getFacet().getSet(id));
        assertTrue(getFacet().getSet(id).isEmpty());
    }

    @Test
    public void testTypeAddNullID()
    {
        ST source1 = developSource(getTypeObj());
        try
        {
            getFacet().set(null, getTypeObj(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjAddNull()
    {
        ST source1 = developSource(getTypeObj());
        try
        {
            getFacet().set(id, null, source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjAddNullSource()
    {
        CT t1 = getTypeObj();
        try
        {
            getFacet().set(id, t1, null);
            fail();
        } catch (NullPointerException e)
        {
            // OK
        }
        assertEquals(0, getFacet().getCount(id));
        assertTrue(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertTrue(getFacet().getSet(id).isEmpty());
    }

    @Test
    public void testObjAddSingleGet()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // No cross-pollution
        assertEquals(0, getFacet().getCount(altid));
        assertTrue(getFacet().isEmpty(altid));
        assertNotNull(getFacet().getSet(altid));
        assertTrue(getFacet().getSet(altid).isEmpty());
    }

    @Test
    public void testObjAddSingleSourceTwiceGet()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Add same, still only once in set (and only one event)
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());
    }

    @Test
    public void testObjAddSingleTwiceTwoSourceGet()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        ST source2 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Add same, still only once in set (and only one event)
        getFacet().set(id, t1, source2);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());
    }

    @Test
    public void testObjAddMultGet()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<CT> setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertEquals(t1, setofone.iterator().next());
        CT t2 = getTypeObj();
        getFacet().set(id, t2, source1);
        assertEquals(2, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<CT> setoftwo = getFacet().getSet(id);
        assertNotNull(setoftwo);
        assertEquals(2, setoftwo.size());
        assertTrue(setoftwo.contains(t1));
        assertTrue(setoftwo.contains(t2));
    }

    @Test
    public void testObjContains()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        assertFalse(getFacet().contains(id, t1));
        getFacet().set(id, t1, source1);
        assertTrue(getFacet().contains(id, t1));
        getFacet().remove(id, t1);
        assertFalse(getFacet().contains(id, t1));
    }

    @Test
    public void testObjRemoveUseless()
    {
        getFacet().remove(id, null);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjAddSingleRemove()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Remove
        getFacet().remove(id, t1);
        assertEquals(0, getFacet().getCount(id));
        assertTrue(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertTrue(getFacet().getSet(id).isEmpty());
    }

    @Test
    public void testObjAddUselessRemove()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Useless Remove
        getFacet().remove(id, getTypeObj());
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());
    }

    @Test
    public void testObjAddSingleTwiceRemove()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Add same, still only once in set (but twice on that source)
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Only one Remove required to clear (source Set not source List)
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();

        // Second remove useless
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjAddMultRemove()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        CT t2 = getTypeObj();
        getFacet().set(id, t1, source1);
        getFacet().set(id, t2, source1);
        getFacet().remove(id, t1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<CT> setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertTrue(setofone.contains(t2));
    }

    @Test
    public void testObjRemoveNullInnocent()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        getFacet().remove(id, null);
    }

    @Test
    public void testObjRemoveAll()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        ST source2 = developSource(t1);
        CT t2 = getTypeObj();
        ST source3 = developSource(t2);
        assertNotNull(getFacet().removeAll(id));
        assertTrue(getFacet().removeAll(id).isEmpty());
        getFacet().set(id, t1, source1);
        getFacet().set(id, t1, source2);
        getFacet().set(id, t2, source3);
        Map<CT, ST> map = getFacet().removeAll(id);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertTrue(map.containsKey(t1));
        assertTrue(map.containsKey(t2));
        assertNotNull(map.get(t1));
        assertNotNull(map.get(t2));
        assertEquals(source2, map.get(t1));
        assertEquals(source3, map.get(t2));
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testGetSetIndependence()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        CT t2 = getTypeObj();
        getFacet().set(id, t1, source1);
        Set<CT> set = getFacet().getSet(id);
        try
        {
            set.add(t2);
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
        try
        {
            set.remove(t1);
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
        List<CT> pct = new ArrayList<>();
        pct.add(t1);
        pct.add(t2);
        try
        {
            set.addAll(pct);
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
        try
        {
            set.removeAll(pct);
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
        try
        {
            set.retainAll(new ArrayList<CT>());
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
        getFacet().set(id, t1, source1);
        try
        {
            set.clear();
            // If we can modify, then make sure it's independent of the
            // getFacet()
            assertEquals(1, getFacet().getCount(id));
            assertFalse(getFacet().isEmpty(id));
            assertNotNull(getFacet().getSet(id));
            assertEquals(1, getFacet().getSet(id).size());
            assertEquals(t1, getFacet().getSet(id).iterator().next());
        } catch (UnsupportedOperationException e)
        {
            // This is ok too
        }
    }

    @Test
    public void testCopyContentsNone()
    {
        getFacet().copyContents(altid, id);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testCopyContents()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        CT t2 = getTypeObj();
        getFacet().set(id, t1, source1);
        getFacet().set(id, t2, source1);
        assertEquals(2, getFacet().getCount(id));
        assertEquals(0, getFacet().getCount(altid));
        getFacet().copyContents(id, altid);
        assertEquals(2, getFacet().getCount(altid));
        assertFalse(getFacet().isEmpty(altid));
        Set<CT> setoftwo = getFacet().getSet(altid);
        assertNotNull(setoftwo);
        assertEquals(2, setoftwo.size());
        assertTrue(setoftwo.contains(t1));
        assertTrue(setoftwo.contains(t2));
        // Prove independence (remove from id)
        getFacet().remove(id, t1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<CT> setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertTrue(setofone.contains(t2));

        assertEquals(2, getFacet().getCount(altid));
        assertFalse(getFacet().isEmpty(altid));
        setoftwo = getFacet().getSet(altid);
        assertNotNull(setoftwo);
        assertEquals(2, setoftwo.size());
        assertTrue(setoftwo.contains(t1));
        assertTrue(setoftwo.contains(t2));
        // Prove Independence (remove from altid)

        getFacet().remove(altid, t2);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertTrue(setofone.contains(t2));

        assertEquals(1, getFacet().getCount(altid));
        assertFalse(getFacet().isEmpty(altid));
        setofone = getFacet().getSet(altid);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertTrue(setofone.contains(t1));
    }

    @Test
    public void testObjAddSingleTwiceRemoveUnsourced()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Add same, still only once in set (but twice on that source)
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Only one Remove required to clear (source Set not source List)
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();

        // Second remove useless
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjAddTwoSourcesTwiceRemoveUnsourced()
    {
        CT t1 = getTypeObj();
        ST source1 = developSource(t1);
        ST source2 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Add same, still only once in set (but twice on that source)
        getFacet().set(id, t1, source2);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(t1, getFacet().getSet(id).iterator().next());

        // Only one Remove required to clear (source Set not source List)
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();

        // Second remove useless
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
    }

    @Test
    public void testObjGetSource()
    {
        CT t1 = getTypeObj();
        assertNull(getFacet().get(id, t1));
        ST source1 = developSource(t1);
        ST source2 = developSource(t1);
        getFacet().set(id, t1, source1);
        assertEquals(source1, getFacet().get(id, t1));

        // Add same, still only once in set (but twice on that source)
        getFacet().set(id, t1, source2);
        assertEquals(source2, getFacet().get(id, t1));

        // Only one Remove required to clear (source Set not source List)
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
        assertNull(getFacet().get(id, t1));
        // Second remove useless
        getFacet().remove(id, t1);
        testObjUnsetZeroCount();
        testObjUnsetEmpty();
        testObjUnsetEmptySet();
        assertNull(getFacet().get(id, t1));
    }

    protected abstract CT getTypeObj();

    protected abstract AbstractAssociationFacet<CharID, CT, ST> getFacet();

    protected abstract ST developSource(CT obj);

}
