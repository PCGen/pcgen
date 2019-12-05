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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

import org.junit.jupiter.api.Test;

public abstract class AbstractExtractingFacetTest<S, T> extends
        AbstractSourcedListFacetTest<T>
{

    protected abstract DataFacetChangeListener<CharID, S> getListener();

    @Test
    public void testExtTypeAddNullSource()
    {
        Object source = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // No cross-pollution
        assertEquals(0, getFacet().getCount(altid));
        assertTrue(getFacet().isEmpty(altid));
        assertNotNull(getFacet().getSet(altid));
        assertTrue(getFacet().getSet(altid).isEmpty());
    }

    @Test
    public void testExtTypeAddSingleGet()
    {
        Object source = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // No cross-pollution
        assertEquals(0, getFacet().getCount(altid));
        assertTrue(getFacet().isEmpty(altid));
        assertNotNull(getFacet().getSet(altid));
        assertTrue(getFacet().getSet(altid).isEmpty());
    }

    @Test
    public void testExtTypeAddSingleSourceTwiceGet()
    {
        Object source = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Add same, still only once in set (and only one event)
        dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
    }

    @Test
    public void testExtTypeAddSingleTwiceTwoSourceGet()
    {
        Object source = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Add same, still only once in set (and only one event)
        Object source2 = new Object();
        dfce =
                new DataFacetChangeEvent<>(id, t1, source2,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
    }

    @Test
    public void testExtTypeAddMultGet()
    {
        Object source = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<T> setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, setofone.iterator().next());
        assertEventCount(1, 0);
        dfce =
                new DataFacetChangeEvent<>(id, getContainingObject(1), source,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(2, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<T> setoftwo = getFacet().getSet(id);
        assertNotNull(setoftwo);
        assertEquals(2, setoftwo.size());
        assertTrue(setoftwo.contains(getTargetObject(0)));
        assertTrue(setoftwo.contains(getTargetObject(1)));
        assertEventCount(2, 0);
    }

    @Test
    public void testExtTypeContains()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        T tgt1 = getTargetObject(0);
        assertFalse(getFacet().contains(id, tgt1));
        getListener().dataAdded(dfce);
        assertTrue(getFacet().contains(id, tgt1));
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        assertFalse(getFacet().contains(id, tgt1));
    }

    @Test
    public void testExtTypeRemoveUselessSource()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        S t2 = getContainingObject(1);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        dfce =
                new DataFacetChangeEvent<>(id, t2, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        // No change (wrong source)
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
    }

    @Test
    public void testExtTypeAddSingleRemove()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Remove
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        assertEquals(0, getFacet().getCount(id));
        assertTrue(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertTrue(getFacet().getSet(id).isEmpty());
        assertEventCount(1, 1);
    }

    @Test
    public void testExtTypeAddUselessRemove()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Useless Remove
        dfce =
                new DataFacetChangeEvent<>(id, getContainingObject(1),
                        source1, DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
    }

    @Test
    public void testExtTypeAddSingleTwiceRemove()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        T tgt1 = getTargetObject(0);
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Add same, still only once in set (but twice on that source)
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        assertNotNull(getFacet().getSet(id));
        assertEquals(1, getFacet().getSet(id).size());
        assertEquals(tgt1, getFacet().getSet(id).iterator().next());
        assertEventCount(1, 0);
        // Only one Remove required to clear (source Set not source List)
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        testTypeUnsetZeroCount();
        testTypeUnsetEmpty();
        testTypeUnsetEmptySet();
        assertEventCount(1, 1);
        // Second remove useless
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        testTypeUnsetZeroCount();
        testTypeUnsetEmpty();
        testTypeUnsetEmptySet();
        assertEventCount(1, 1);
    }

    @Test
    public void testExtTypeAddMultRemove()
    {
        Object source1 = new Object();
        S t1 = getContainingObject(0);
        DataFacetChangeEvent<CharID, S> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        S t2 = getContainingObject(1);
        dfce =
                new DataFacetChangeEvent<>(id, t2, source1,
                        DataFacetChangeEvent.DATA_ADDED);
        getListener().dataAdded(dfce);
        dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
        getListener().dataRemoved(dfce);
        assertEquals(1, getFacet().getCount(id));
        assertFalse(getFacet().isEmpty(id));
        Set<T> setofone = getFacet().getSet(id);
        assertNotNull(setofone);
        assertEquals(1, setofone.size());
        assertTrue(setofone.contains(getTargetObject(1)));
        assertEventCount(2, 1);
    }

    protected abstract S getContainingObject(int i);

    protected abstract T getTargetObject(int i);

}
