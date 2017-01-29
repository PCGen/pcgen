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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

public abstract class AbstractConsolidatingFacetTest<ST, T> extends
		AbstractSourcedListFacetTest<T>
{

	protected abstract DataFacetChangeListener<CharID, ST> getListener();

	protected abstract ST getSourceObject();

	protected abstract T getConverted(ST t1);

	protected boolean sourcedFromEvent()
	{
		return true;
	}

	@Test
	public void testConTypeAddNullSource()
	{
		Object source = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		Assert.assertEquals(0, getFacet().getCount(altid));
		Assert.assertTrue(getFacet().isEmpty(altid));
		Assert.assertNotNull(getFacet().getSet(altid));
		Assert.assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testConTypeAddSingleGet()
	{
		Object source = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		Assert.assertEquals(0, getFacet().getCount(altid));
		Assert.assertTrue(getFacet().isEmpty(altid));
		Assert.assertNotNull(getFacet().getSet(altid));
		Assert.assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testConTypeAddSingleSourceTwiceGet()
	{
		Object source = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testConTypeAddSingleTwiceTwoSourceGet()
	{
		Object source = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		Object source2 = new Object();
		dfce =
                new DataFacetChangeEvent<>(id, t1, source2,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testConTypeAddMultGet()
	{
		Object source = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(getConverted(t1), setofone.iterator().next());
		assertEventCount(1, 0);
		ST t2 = getSourceObject();
		dfce =
                new DataFacetChangeEvent<>(id, t2, source,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(getConverted(t1)));
		Assert.assertTrue(setoftwo.contains(getConverted(t2)));
		assertEventCount(2, 0);
	}

	@Test
	public void testConTypeContains()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		T obj1 = getConverted(t1);
		Assert.assertFalse(getFacet().contains(id, obj1));
		getListener().dataAdded(dfce);
		Assert.assertTrue(getFacet().contains(id, obj1));
		dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
		getListener().dataRemoved(dfce);
		Assert.assertFalse(getFacet().contains(id, obj1));
	}

	@Test
	public void testConTypeRemoveUselessSource()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
		dfce =
                new DataFacetChangeEvent<>(id, t1, source2,
                        DataFacetChangeEvent.DATA_REMOVED);
		getListener().dataRemoved(dfce);
		if (sourcedFromEvent())
		{
			// No change (wrong source)
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
			assertEventCount(1, 0);
		}
		else
		{
			Assert.assertEquals(0, getFacet().getCount(id));
			Assert.assertTrue(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(0, getFacet().getSet(id).size());
			assertEventCount(1, 1);
		}
	}

	@Test
	public void testConTypeAddSingleRemove()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
		getListener().dataRemoved(dfce);
		Assert.assertEquals(0, getFacet().getCount(id));
		Assert.assertTrue(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testConTypeAddUselessRemove()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		dfce =
                new DataFacetChangeEvent<>(id, getSourceObject(), source1,
                        DataFacetChangeEvent.DATA_REMOVED);
		getListener().dataRemoved(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testConTypeAddSingleTwiceRemove()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(getConverted(t1), getFacet().getSet(id).iterator().next());
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
	public void testConTypeAddMultRemove()
	{
		Object source1 = new Object();
		ST t1 = getSourceObject();
		DataFacetChangeEvent<CharID, ST> dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		ST t2 = getSourceObject();
		dfce =
                new DataFacetChangeEvent<>(id, t2, source1,
                        DataFacetChangeEvent.DATA_ADDED);
		getListener().dataAdded(dfce);
		dfce =
                new DataFacetChangeEvent<>(id, t1, source1,
                        DataFacetChangeEvent.DATA_REMOVED);
		getListener().dataRemoved(dfce);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(getConverted(t2)));
		assertEventCount(2, 1);
	}

}
