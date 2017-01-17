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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractSingleSourceListFacetTest<CT, ST> extends
		TestCase
{
	private CharID id;
	private CharID altid;

	private Listener listener = new Listener();
	ST oneSource = developSource();

	private class Listener implements DataFacetChangeListener<CharID, CT>
	{

		public int addEventCount;
		public int removeEventCount;

        @Override
		public void dataAdded(DataFacetChangeEvent<CharID, CT> dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent<CharID, CT> dfce)
		{
			removeEventCount++;
		}

	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		getFacet().addDataFacetChangeListener(listener);
	}

	private void assertEventCount(int a, int r)
	{
		Assert.assertEquals(a, listener.addEventCount);
		Assert.assertEquals(r, listener.removeEventCount);
	}

	@Test
	public void testObjUnsetZeroCount()
	{
		Assert.assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testObjUnsetEmpty()
	{
		Assert.assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testRemoveAllUnsetEmpty()
	{
		// Not particularly a test, just make sure it doesn't throw an exception
		getFacet().removeAll(id, oneSource);
	}

	@Test
	public void testObjUnsetEmptySet()
	{
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTypeAddNullID()
	{
		ST source1 = developSource();
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		try
		{
			getFacet().add(null, getTypeObj(), source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testObjAddNull()
	{
		ST source1 = developSource();
		try
		{
			getFacet().add(id, null, source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testObjAddNullSource()
	{
		CT t1 = getTypeObj();
		try
		{
			getFacet().add(id, t1, null);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertEventCount(0, 0);
		Assert.assertEquals(0, getFacet().getCount(id));
		Assert.assertTrue(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testObjAddSingleGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		Assert.assertEquals(0, getFacet().getCount(altid));
		Assert.assertTrue(getFacet().isEmpty(altid));
		Assert.assertNotNull(getFacet().getSet(altid));
		Assert.assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testObjAddSingleSourceTwiceGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleTwiceTwoSourceGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testObjAddMultGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		CT t2 = getTypeObj();
		getFacet().add(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testObjContains()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		Assert.assertFalse(getFacet().contains(id, t1));
		getFacet().add(id, t1, source1);
		Assert.assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1, source1);
		Assert.assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testObjAddAllNull()
	{
		ST source1 = developSource();
		try
		{
			getFacet().addAll(id, null, source1);
			Assert.fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
		assertEventCount(0, 0);
	}

	@Test
	public void testObjAddAllUseless()
	{
		ST source1 = developSource();
		getFacet().addAll(id, new ArrayList<>(), source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testObjAddAll()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		// Prove independence
		pct.remove(t2);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
	}

	@Test
	public void testObjAddAllSecondSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		CT t3 = getTypeObj();
		ST source2 = developSource();
		List<CT> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofthree = getFacet().getSet(id);
		Assert.assertNotNull(setofthree);
		Assert.assertEquals(3, setofthree.size());
		Assert.assertTrue(setofthree.contains(t1));
		Assert.assertTrue(setofthree.contains(t2));
		Assert.assertTrue(setofthree.contains(t3));
		assertEventCount(3, 0);
	}

	@Test
	public void testObjAddAllTwice()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t1);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testObjAddAllNullInList()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		try
		{
			getFacet().addAll(id, pct, source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		/*
		 * TODO This should be zero, one or two???
		 */
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(1, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testObjRemoveUseless()
	{
		ST source1 = developSource();
		getFacet().remove(id, null, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testObjRemoveUselessSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		ST source2 = developSource();
		getFacet().remove(id, t1, source2);
		// No change (wrong source)
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		getFacet().remove(id, t1, source1);
		Assert.assertEquals(0, getFacet().getCount(id));
		Assert.assertTrue(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testObjAddUselessRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getTypeObj(), source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleTwiceRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testObjAddMultRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().remove(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testObjRemoveAllNull()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		try
		{
			getFacet().removeAll(id, null, source1);
			Assert.fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
	}

	@Test
	public void testObjRemoveAllUseless()
	{
		ST source1 = developSource();
		getFacet().removeAll(id, new ArrayList<>(), source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testObjRemoveAllList()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		ST source2 = developSource();
		CT t3 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().add(id, t3, source1);
		getFacet().add(id, t3, source2);
		pct.add(t1);
		pct.add(t3);
		assertEventCount(3, 0);
		getFacet().removeAll(id, pct, source1);
		assertEventCount(3, 1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t2));
		Assert.assertTrue(setoftwo.contains(t3));
		// Prove independence
		pct.remove(t1);
		setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t2));
		Assert.assertTrue(setoftwo.contains(t3));
	}

	@Test
	public void testObjRemoveAllSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		ST source2 = developSource();
		CT t3 = getTypeObj();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().add(id, t3, source1);
		getFacet().add(id, t3, source2);
		assertEventCount(3, 0);
		getFacet().removeAll(id, developSource());
		assertEventCount(3, 0);
		getFacet().removeAll(id, source1);
		assertEventCount(3, 2);
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertTrue(setofone.contains(t3));
	}

	@Test
	public void testObjRemoveAllTwice()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		getFacet().removeAll(id, pct, source1);
		assertEventCount(2, 1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
	}

	@Test
	public void testObjRemoveAllNullInList()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		pct.add(t1);
		pct.add(null);
		assertEventCount(2, 0);
		getFacet().removeAll(id, pct, source1);
		assertEventCount(2, 1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
	}

	@Test
	public void testObjRemoveAll()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		CT t2 = getTypeObj();
		ST source3 = developSource();
		Assert.assertNotNull(getFacet().removeAll(id));
		Assert.assertTrue(getFacet().removeAll(id).isEmpty());
		getFacet().add(id, t1, source1);
		getFacet().add(id, t1, source2);
		getFacet().add(id, t2, source3);
		assertEventCount(2, 0);
		Map<CT, ST> map = getFacet().removeAll(id);
		assertEventCount(2, 2);
		Assert.assertNotNull(map);
		Assert.assertEquals(2, map.size());
		Assert.assertTrue(map.containsKey(t1));
		Assert.assertTrue(map.containsKey(t2));
		Assert.assertNotNull(map.get(t1));
		Assert.assertNotNull(map.get(t2));
		Assert.assertEquals(source2, map.get(t1));
		Assert.assertEquals(source3, map.get(t2));
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		CT t2 = getTypeObj();
		getFacet().add(id, t1, source1);
		Set<CT> set = getFacet().getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
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
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<CT>());
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		getFacet().add(id, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
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
		ST source1 = developSource();
		CT t2 = getTypeObj();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertEquals(0, getFacet().getCount(altid));
		getFacet().copyContents(id, altid);
		Assert.assertEquals(2, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		Set<CT> setoftwo = getFacet().getSet(altid);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		getFacet().remove(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));

		Assert.assertEquals(2, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		setoftwo = getFacet().getSet(altid);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		// Prove Independence (remove from altid)

		getFacet().remove(altid, t2, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));

		Assert.assertEquals(1, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		setofone = getFacet().getSet(altid);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
	}

	@Test
	public void testObjGetSetSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		List<? extends CT> origset = getFacet().getSet(id, source1);
		Assert.assertNotNull(origset);
		Assert.assertTrue(origset.isEmpty());
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<? extends CT> setoftwo = getFacet().getSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		CT t3 = getTypeObj();
		ST source2 = developSource();
		List<CT> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(1, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t2));
		setoftwo = getFacet().getSet(id, source2);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t3));
		assertEventCount(3, 0);
		getFacet().remove(id, t3, source2);
		List<? extends CT> setofone = getFacet().getSet(id, source2);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
		getFacet().remove(id, t1, source2);
		List<? extends CT> emptyset = getFacet().getSet(id, source2);
		Assert.assertNotNull(emptyset);
		Assert.assertEquals(0, emptyset.size());
	}

	@Test
	public void testObjAddSingleTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testObjAddTwoSourcesTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testObjGetSource()
	{
		CT t1 = getTypeObj();
		Assert.assertNull(getFacet().getSource(id, t1));
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(source1, getFacet().getSource(id, t1));
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source2);
		Assert.assertEquals(source2, getFacet().getSource(id, t1));
		assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
		Assert.assertNull(getFacet().getSource(id, t1));
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		assertEventCount(1, 1);
		Assert.assertNull(getFacet().getSource(id, t1));
	}

	protected abstract CT getTypeObj();

	protected abstract AbstractSingleSourceListFacet<CT, ST> getFacet();

	protected abstract ST developSource();

}
