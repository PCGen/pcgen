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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.QualifiedObject;

public class HasDeityWeaponProfFacetTest extends TestCase
{
	private CharID id = new CharID();
	private CharID altid = new CharID();
	private HasDeityWeaponProfFacet facet = new HasDeityWeaponProfFacet();

	private Listener listener = new Listener();
	Object oneSource = new Object();

	private static class Listener implements DataFacetChangeListener<QualifiedObject<Boolean>>
	{

		public int addEventCount;
		public int removeEventCount;

        @Override
		public void dataAdded(DataFacetChangeEvent dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent dfce)
		{
			removeEventCount++;
		}

	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		facet.addDataFacetChangeListener(listener);
	}

	private void assertEventCount(int a, int r)
	{
		assertEquals(a, listener.addEventCount);
		assertEquals(r, listener.removeEventCount);
	}

	@Test
	public void testQualifiedUnsetZeroCount()
	{
		assertEquals(0, facet.getCount(id));
	}

	@Test
	public void testQualifiedUnsetEmpty()
	{
		assertTrue(facet.isEmpty(id));
	}

	@Test
	public void testRemoveAllUnsetEmpty()
	{
		// Not particularly a test, just make sure it doesn't throw an exception
		facet.removeAll(id, oneSource);
	}

	@Test
	public void testQualifiedUnsetEmptySet()
	{
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
	}

	@Test
	public void testQualifiedAddNull()
	{
		Object source1 = new Object();
		try
		{
			facet.add(id, null, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testQualifiedAddNullSource()
	{
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, null);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, facet.getCount(altid));
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.getSet(altid));
		assertTrue(facet.getSet(altid).isEmpty());
	}

	@Test
	public void testQualifiedAddSingleGet()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, facet.getCount(altid));
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.getSet(altid));
		assertTrue(facet.getSet(altid).isEmpty());
	}

	@Test
	public void testQualifiedAddSingleSourceTwiceGet()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedAddSingleTwiceTwoSourceGet()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		facet.add(id, t1, source2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedAddMultGet()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t2, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testQualifiedContains()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		assertFalse(facet.contains(id, t1));
		facet.add(id, t1, source1);
		assertTrue(facet.contains(id, t1));
		facet.remove(id, t1, source1);
		assertFalse(facet.contains(id, t1));
	}

	@Test
	public void testQualifiedAddAllNull()
	{
		Object source1 = new Object();
		try
		{
			facet.addAll(id, null, source1);
			fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
		assertEventCount(0, 0);
	}

	@Test
	public void testQualifiedAddAllUseless()
	{
		Object source1 = new Object();
		facet.addAll(id, new ArrayList<QualifiedObject<Boolean>>(), source1);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testQualifiedAddAll()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		// Prove independence
		pct.remove(t2);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
	}

	@Test
	public void testQualifiedAddAllSecondSource()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		QualifiedObject<Boolean> t3 = new QualifiedObject<Boolean>(Boolean.TRUE);
		List<QualifiedObject<Boolean>> pct2 = new ArrayList<QualifiedObject<Boolean>>();
		pct2.add(t1);
		pct2.add(t3);
		facet.addAll(id, pct2, source2);
		assertEquals(3, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofthree = facet.getSet(id);
		assertNotNull(setofthree);
		assertEquals(3, setofthree.size());
		assertTrue(setofthree.contains(t1));
		assertTrue(setofthree.contains(t2));
		assertTrue(setofthree.contains(t3));
		assertEventCount(3, 0);
	}

	@Test
	public void testQualifiedAddAllTwice()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t1);
		facet.addAll(id, pct, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedAddAllNullInList()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		try
		{
			facet.addAll(id, pct, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		/*
		 * TODO This should be zero, one or two???
		 */
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(1, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedRemoveUseless()
	{
		Object source1 = new Object();
		facet.remove(id, null, source1);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testQualifiedRemoveUselessSource()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
		facet.remove(id, t1, source2);
		// No change (wrong source)
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedAddSingleRemove()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		facet.remove(id, t1, source1);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testQualifiedAddUselessRemove()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		facet.remove(id, new QualifiedObject<Boolean>(Boolean.FALSE), source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testQualifiedAddSingleTwiceRemove()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		facet.remove(id, t1, source1);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		facet.remove(id, t1, source1);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testQualifiedAddMultRemove()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		facet.remove(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testQualifiedRemoveAllNull()
	{
		Object source1 = new Object();
		facet.add(id, new QualifiedObject<Boolean>(Boolean.TRUE), source1);
		try
		{
			facet.removeAll(id, null, source1);
			fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
	}

	@Test
	public void testQualifiedRemoveAllUseless()
	{
		Object source1 = new Object();
		facet.removeAll(id, new ArrayList<QualifiedObject<Boolean>>(), source1);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testQualifiedRemoveAllList()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		QualifiedObject<Boolean> t3 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		facet.add(id, t3, source1);
		facet.add(id, t3, source2);
		pct.add(t1);
		pct.add(t3);
		assertEventCount(3, 0);
		facet.removeAll(id, pct, source1);
		assertEventCount(3, 1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t2));
		assertTrue(setoftwo.contains(t3));
		// Prove independence
		pct.remove(t1);
		setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t2));
		assertTrue(setoftwo.contains(t3));
	}

	@Test
	public void testQualifiedRemoveAllSource()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		QualifiedObject<Boolean> t3 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		facet.add(id, t3, source1);
		facet.add(id, t3, source2);
		assertEventCount(3, 0);
		facet.removeAll(id, new Object());
		assertEventCount(3, 0);
		facet.removeAll(id, source1);
		assertEventCount(3, 2);
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertTrue(setofone.contains(t3));
	}

	@Test
	public void testQualifiedRemoveAllTwice()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		facet.removeAll(id, pct, source1);
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testQualifiedRemoveAllNullInList()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		pct.add(t1);
		pct.add(null);
		assertEventCount(2, 0);
		facet.removeAll(id, pct, source1);
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testQualifiedRemoveAll()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		assertNotNull(facet.removeAll(id));
		assertTrue(facet.removeAll(id).isEmpty());
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t1, source1);
		facet.add(id, t1, source2);
		facet.add(id, t2, source2);
		assertEventCount(2, 0);
		Map<QualifiedObject<Boolean>, Set<Object>> map = facet.removeAll(id);
		assertEventCount(2, 2);
		assertNotNull(map);
		assertEquals(2, map.size());
		assertTrue(map.containsKey(t1));
		assertTrue(map.containsKey(t2));
		assertNotNull(map.get(t1));
		assertNotNull(map.get(t2));
		assertEquals(2, map.get(t1).size());
		assertEquals(1, map.get(t2).size());
		assertTrue(map.get(t1).contains(source1));
		assertTrue(map.get(t1).contains(source2));
		assertTrue(map.get(t2).contains(source2));
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t1, source1);
		Set<QualifiedObject<Boolean>> set = facet.getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<QualifiedObject<Boolean>>());
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		facet.add(id, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getSet(id));
			assertEquals(1, facet.getSet(id).size());
			assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
	}

	@Test
	public void testCopyContentsNone()
	{
		facet.copyContents(altid, id);
		testQualifiedUnsetZeroCount();
		testQualifiedUnsetEmpty();
		testQualifiedUnsetEmptySet();
	}

	@Test
	public void testCopyContents()
	{
		Object source1 = new Object();
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		assertEquals(2, facet.getCount(id));
		assertEquals(0, facet.getCount(altid));
		facet.copyContents(id, altid);
		assertEquals(2, facet.getCount(altid));
		assertFalse(facet.isEmpty(altid));
		Set<QualifiedObject<Boolean>> setoftwo = facet.getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		facet.remove(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<QualifiedObject<Boolean>> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertEquals(2, facet.getCount(altid));
		assertFalse(facet.isEmpty(altid));
		setoftwo = facet.getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove Independence (remove from altid)

		facet.remove(altid, t2, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertEquals(1, facet.getCount(altid));
		assertFalse(facet.isEmpty(altid));
		setofone = facet.getSet(altid);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
	}

	@Test
	public void testQualifiedGetSetSource()
	{
		Object source1 = new Object();
		List<? extends QualifiedObject<Boolean>> origset = facet.getSet(id, source1);
		assertNotNull(origset);
		assertTrue(origset.isEmpty());
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Collection<? extends QualifiedObject<Boolean>> setoftwo = facet.getSet(id, source1);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		QualifiedObject<Boolean> t3 = new QualifiedObject<Boolean>(Boolean.TRUE);
		List<QualifiedObject<Boolean>> pct2 = new ArrayList<QualifiedObject<Boolean>>();
		pct2.add(t1);
		pct2.add(t3);
		facet.addAll(id, pct2, source2);
		assertEquals(3, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		setoftwo = facet.getSet(id, source1);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		setoftwo = facet.getSet(id, source2);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t3));
		assertEventCount(3, 0);
		facet.remove(id, t3, source2);
		List<? extends QualifiedObject<Boolean>> setofone = facet.getSet(id, source2);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		facet.remove(id, t1, source2);
		List<? extends QualifiedObject<Boolean>> emptyset = facet.getSet(id, source2);
		assertNotNull(emptyset);
		assertEquals(0, emptyset.size());
	}


	@Test
	public void testgetQualifiedSet()
	{
		Object source1 = new Object();
		Collection<QualifiedObject<Boolean>> emptyset = facet.getQualifiedSet(id);
		assertNotNull(emptyset);
		assertTrue(emptyset.isEmpty());
		QualifiedObject<Boolean> t1 = new QualifiedObject<Boolean>(Boolean.TRUE);
		QualifiedObject<Boolean> t2 = new QualifiedObject<Boolean>(Boolean.FALSE);
		List<QualifiedObject<Boolean>> pct = new ArrayList<QualifiedObject<Boolean>>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Collection<? extends QualifiedObject<Boolean>> setoftwo = facet.getQualifiedSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		QualifiedObject<Boolean> t3 = new QualifiedObject<Boolean>(null);
		List<QualifiedObject<Boolean>> pct2 = new ArrayList<QualifiedObject<Boolean>>();
		pct2.add(t1);
		pct2.add(t3);
		facet.addAll(id, pct2, source2);
		assertEquals(3, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		setoftwo = facet.getQualifiedSet(id);
		assertNotNull(setoftwo);
		assertEquals(3, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertTrue(setoftwo.contains(t3));
		assertEventCount(3, 0);
		facet.remove(id, t3, source2);
		setoftwo = facet.getQualifiedSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		facet.remove(id, t1, source2);
		setoftwo = facet.getQualifiedSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
	}

}
