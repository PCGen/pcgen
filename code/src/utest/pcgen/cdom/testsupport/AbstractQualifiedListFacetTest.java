/*
 * Copyright (c) 2009-2010 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.testsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.base.QualifiedActor;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

public abstract class AbstractQualifiedListFacetTest<T extends QualifyingObject>
		extends TestCase
{
	protected CharID id;
	protected CharID altid;

	private Listener listener = new Listener();
	protected Object oneSource = new Object();

	private class Listener implements DataFacetChangeListener<CharID, T>
	{

		public int addEventCount;
		public int removeEventCount;

		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, T> dfce)
		{
			addEventCount++;
		}

		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, T> dfce)
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

	protected void assertEventCount(int a, int r)
	{
		Assert.assertEquals(a, listener.addEventCount);
		Assert.assertEquals(r, listener.removeEventCount);
	}

	@Test
	public void testTypeUnsetZeroCount()
	{
		Assert.assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testTypeUnsetEmpty()
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
	public void testTypeUnsetEmptySet()
	{
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTypeAddNullID()
	{
		Object source1 = new Object();
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		try
		{
			getFacet().add(null, getObject(), source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddNull()
	{
		Object source1 = new Object();
		try
		{
			getFacet().add(id, null, source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddNullSource()
	{
		T t1 = getObject();
		getFacet().add(id, t1, null);
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
	public void testTypeAddSingleGet()
	{
		Object source1 = new Object();
		T t1 = getObject();
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
	public void testTypeAddSingleSourceTwiceGet()
	{
		Object source1 = new Object();
		T t1 = getObject();
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
	public void testTypeAddSingleTwiceTwoSourceGet()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		T t1 = getObject();
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
	public void testTypeAddMultGet()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		T t2 = getAltObject();
		getFacet().add(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testTypeContains()
	{
		Object source1 = new Object();
		T t1 = getObject();
		Assert.assertFalse(getFacet().contains(id, t1));
		getFacet().add(id, t1, source1);
		Assert.assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1, source1);
		Assert.assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testTypeAddAllNull()
	{
		Object source1 = new Object();
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
	public void testTypeAddAllUseless()
	{
		Object source1 = new Object();
		getFacet().addAll(id, new ArrayList<>(), source1);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddAll()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setoftwo = getFacet().getSet(id);
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
	public void testTypeAddAllSecondSource()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		T t3 = getThirdObject();
		List<T> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofthree = getFacet().getSet(id);
		Assert.assertNotNull(setofthree);
		Assert.assertEquals(3, setofthree.size());
		Assert.assertTrue(setofthree.contains(t1));
		Assert.assertTrue(setofthree.contains(t2));
		Assert.assertTrue(setofthree.contains(t3));
		assertEventCount(3, 0);
	}

	@Test
	public void testTypeAddAllTwice()
	{
		Object source1 = new Object();
		T t1 = getObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t1);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddAllNullInList()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<>();
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
		Set<T> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(1, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeRemoveUseless()
	{
		Object source1 = new Object();
		getFacet().remove(id, null, source1);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeRemoveUselessSource()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
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
	public void testTypeAddSingleRemove()
	{
		Object source1 = new Object();
		T t1 = getObject();
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
	public void testTypeAddUselessRemove()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getAltObject(), source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddSingleTwiceRemove()
	{
		Object source1 = new Object();
		T t1 = getObject();
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
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1, source1);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testTypeAddMultRemove()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().remove(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testTypeRemoveAllNull()
	{
		Object source1 = new Object();
		getFacet().add(id, getObject(), source1);
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
	public void testTypeRemoveAllUseless()
	{
		Object source1 = new Object();
		getFacet().removeAll(id, new ArrayList<>(), source1);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeRemoveAllList()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		T t3 = getThirdObject();
		List<T> pct = new ArrayList<>();
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
		Set<T> setoftwo = getFacet().getSet(id);
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
	public void testTypeRemoveAllSource()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		T t3 = getThirdObject();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().add(id, t3, source1);
		getFacet().add(id, t3, source2);
		assertEventCount(3, 0);
		getFacet().removeAll(id, new Object());
		assertEventCount(3, 0);
		getFacet().removeAll(id, source1);
		assertEventCount(3, 2);
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertTrue(setofone.contains(t3));
	}

	@Test
	public void testTypeRemoveAllTwice()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		getFacet().removeAll(id, pct, source1);
		assertEventCount(2, 1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTypeRemoveAllNullInList()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		pct.add(t1);
		pct.add(null);
		assertEventCount(2, 0);
		try
		{
			getFacet().removeAll(id, pct, source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		// CONSIDER This was old behavior - prior to null being illegal
		// assertEventCount(2, 1);
		// assertEquals(1, getFacet().getCount(id));
		// assertFalse(getFacet().isEmpty(id));
		// Set<T> setofone = getFacet().getSet(id);
		// assertNotNull(setofone);
		// assertEquals(1, setofone.size());
		// assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTypeRemoveAll()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Assert.assertNotNull(getFacet().removeAll(id));
		Assert.assertTrue(getFacet().removeAll(id).isEmpty());
		T t1 = getObject();
		T t2 = getAltObject();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t1, source2);
		getFacet().add(id, t2, source2);
		assertEventCount(2, 0);
		Map<T, Set<Object>> map = getFacet().removeAll(id);
		assertEventCount(2, 2);
		Assert.assertNotNull(map);
		Assert.assertEquals(2, map.size());
		Assert.assertTrue(map.containsKey(t1));
		Assert.assertTrue(map.containsKey(t2));
		Assert.assertNotNull(map.get(t1));
		Assert.assertNotNull(map.get(t2));
		Assert.assertEquals(2, map.get(t1).size());
		Assert.assertEquals(1, map.get(t2).size());
		Assert.assertTrue(map.get(t1).contains(source1));
		Assert.assertTrue(map.get(t1).contains(source2));
		Assert.assertTrue(map.get(t2).contains(source2));
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1, source1);
		Set<T> set = getFacet().getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
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
			// If we can modify, then make sure it's independent of the facet
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
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
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
			// If we can modify, then make sure it's independent of the facet
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
			set.retainAll(new ArrayList<T>());
			// If we can modify, then make sure it's independent of the facet
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
			// If we can modify, then make sure it's independent of the facet
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
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
	}

	@Test
	public void testCopyContents()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getAltObject();
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertEquals(0, getFacet().getCount(altid));
		getFacet().copyContents(id, altid);
		Assert.assertEquals(2, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		Set<T> setoftwo = getFacet().getSet(altid);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		getFacet().remove(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<T> setofone = getFacet().getSet(id);
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
	public void testTypeGetSetSource()
	{
		Object source1 = new Object();
		List<? extends T> origset = getFacet().getSet(id, source1);
		Assert.assertNotNull(origset);
		Assert.assertTrue(origset.isEmpty());
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<? extends T> setoftwo = getFacet().getSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		T t3 = getThirdObject();
		List<T> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		setoftwo = getFacet().getSet(id, source2);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t3));
		assertEventCount(3, 0);
		getFacet().remove(id, t3, source2);
		List<? extends T> setofone = getFacet().getSet(id, source2);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
		getFacet().remove(id, t1, source2);
		List<? extends T> emptyset = getFacet().getSet(id, source2);
		Assert.assertNotNull(emptyset);
		Assert.assertEquals(0, emptyset.size());
	}

	abstract protected AbstractQualifiedListFacet<T> getFacet();

	abstract protected T getObject();

	protected T getAltObject()
	{
		return getObject();
	}

	protected T getThirdObject()
	{
		return getObject();
	}

	public static void addBonus(Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testTypeAddSingleGetQualified()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getQualifiedSet(id));
		Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
		Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		Assert.assertEquals(0, getFacet().getCount(altid));
		Assert.assertTrue(getFacet().isEmpty(altid));
		Assert.assertNotNull(getFacet().getQualifiedSet(altid));
		Assert.assertTrue(getFacet().getQualifiedSet(altid).isEmpty());
	}

	@Test
	public void testTypeAddSingleSourceTwiceGetQualified()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getQualifiedSet(id));
		Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
		Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getQualifiedSet(id));
		Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
		Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddSingleTwiceTwoSourceGetQualified()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getQualifiedSet(id));
		Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
		Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getQualifiedSet(id));
		Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
		Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddMultGetQualified()
	{
		Object source1 = new Object();
		T t1 = getObject();
		getFacet().add(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getQualifiedSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		T t2 = getAltObject();
		getFacet().add(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<T> setoftwo = getFacet().getQualifiedSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testGetSetQualifiedIndependence()
	{
		Object source1 = new Object();
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1, source1);
		Collection<T> set = getFacet().getQualifiedSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<T>());
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		getFacet().add(id, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getQualifiedSet(id));
			Assert.assertEquals(1, getFacet().getQualifiedSet(id).size());
			Assert.assertEquals(t1, getFacet().getQualifiedSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
	}

	@Test
	public void testTypeGetQualifiedSetSource()
	{
		Object source1 = new Object();
		Collection<? extends T> origset =
				getFacet().getQualifiedSet(id, source1);
		Assert.assertNotNull(origset);
		Assert.assertTrue(origset.isEmpty());
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<? extends T> setoftwo =
				getFacet().getQualifiedSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		T t3 = getThirdObject();
		List<T> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getQualifiedSet(id, source1);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		setoftwo = getFacet().getQualifiedSet(id, source2);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t3));
		assertEventCount(3, 0);
		getFacet().remove(id, t3, source2);
		Collection<? extends T> setofone =
				getFacet().getQualifiedSet(id, source2);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
		getFacet().remove(id, t1, source2);
		Collection<? extends T> emptyset =
				getFacet().getQualifiedSet(id, source2);
		Assert.assertNotNull(emptyset);
		Assert.assertEquals(0, emptyset.size());
	}

	@Test
	public void testActOnQualifiedSet()
	{
		Object source1 = "Source1";
		QualifiedActor<T, T> echo = new QualifiedActor<T, T>()
		{
			@Override
			public T act(T object, Object source)
			{
				return object;
			}
		};
		Collection<? extends T> origset =
				getFacet().actOnQualifiedSet(id, echo);
		Assert.assertNotNull(origset);
		Assert.assertTrue(origset.isEmpty());
		T t1 = getObject();
		T t2 = getAltObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Collection<? extends T> setoftwo =
				getFacet().actOnQualifiedSet(id, echo);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = "Source2";
		T t3 = getThirdObject();
		List<T> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		Assert.assertEquals(3, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		List<T> setoffour = getFacet().actOnQualifiedSet(id, echo);
		Assert.assertNotNull(setoffour);
		Assert.assertEquals(4, setoffour.size());
		Assert.assertTrue(setoffour.contains(t1));
		//two if T1
		Assert.assertTrue(setoffour.indexOf(t1) != setoffour.lastIndexOf(t1));
		Assert.assertTrue(setoffour.contains(t2));
		Assert.assertTrue(setoffour.contains(t3));
		assertEventCount(3, 0);
		QualifiedActor<T, String> sourcedep = new QualifiedActor<T, String>()
		{
			@Override
			public String act(T object, Object source)
			{
				return object.toString() + ":" + source.toString();
			}
		};
		List<String> stringset = getFacet().actOnQualifiedSet(id, sourcedep);
		Assert.assertNotNull(stringset);
		Assert.assertEquals(4, stringset.size());
		Assert.assertTrue(stringset.contains(t1.toString() + ":" + source1.toString()));
		Assert.assertTrue(stringset.contains(t1.toString() + ":" + source2.toString()));
		Assert.assertTrue(stringset.contains(t2.toString() + ":" + source1.toString()));
		Assert.assertTrue(stringset.contains(t3.toString() + ":" + source2.toString()));
		assertEventCount(3, 0);
	}

}
