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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractSingleSourceListFacetTest<CT, ST>
{
	private CharID id;
	private CharID altid;
	private TestFacetListener<CT> listener;

	@BeforeEach
	void setUp()
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		listener = new TestFacetListener<CT>();
		getFacet().addDataFacetChangeListener(listener);
	}

	@AfterEach
	public void tearDown()
	{
		id = null;
		altid = null;
		listener = null;
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
		ST oneSource = developSource();
		assertDoesNotThrow(() -> getFacet().removeAll(id, oneSource));
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
		ST source1 = developSource();
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		assertThrows(NullPointerException.class,
				() -> getFacet().add(null, getTypeObj(), source1));
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testObjAddNull()
	{
		ST source1 = developSource();
		assertThrows(NullPointerException.class, () -> getFacet().add(id, null, source1));
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testObjAddNullSource()
	{
		CT t1 = getTypeObj();
		assertThrows(NullPointerException.class, () -> getFacet().add(id, t1, null));
		listener.assertEventCount(0, 0);
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testObjAddSingleGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
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
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleTwiceTwoSourceGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source2);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testObjAddMultGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		listener.assertEventCount(1, 0);
		CT t2 = getTypeObj();
		getFacet().add(id, t2, source1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		listener.assertEventCount(2, 0);
	}

	@Test
	public void testObjContains()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		assertFalse(getFacet().contains(id, t1));
		getFacet().add(id, t1, source1);
		assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1, source1);
		assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testObjAddAllNull()
	{
		ST source1 = developSource();
		assertThrows(NullPointerException.class, () -> getFacet().addAll(id, null, source1));
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testObjAddAllUseless()
	{
		ST source1 = developSource();
		getFacet().addAll(id, new ArrayList<>(), source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(0, 0);
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
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		listener.assertEventCount(2, 0);
		// Prove independence
		pct.remove(t2);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
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
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		listener.assertEventCount(2, 0);
		CT t3 = getTypeObj();
		ST source2 = developSource();
		List<CT> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		assertEquals(3, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofthree = getFacet().getSet(id);
		assertNotNull(setofthree);
		assertEquals(3, setofthree.size());
		assertTrue(setofthree.contains(t1));
		assertTrue(setofthree.contains(t2));
		assertTrue(setofthree.contains(t3));
		listener.assertEventCount(3, 0);
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
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		listener.assertEventCount(1, 0);
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
		assertThrows(NullPointerException.class, () -> getFacet().addAll(id, pct, source1));
		/*
		 * TODO This should be zero, one or two???
		 */
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(1, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testObjRemoveUseless()
	{
		ST source1 = developSource();
		getFacet().remove(id, null, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testObjRemoveUselessSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		ST source2 = developSource();
		getFacet().remove(id, t1, source2);
		// No change (wrong source)
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Remove
		getFacet().remove(id, t1, source1);
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
		listener.assertEventCount(1, 1);
	}

	@Test
	public void testObjAddUselessRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getTypeObj(), source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testObjAddSingleTwiceRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1, source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
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
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		listener.assertEventCount(2, 1);
	}

	@Test
	public void testObjRemoveAllNull()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertThrows(NullPointerException.class, () -> getFacet().removeAll(id, null, source1));
	}

	@Test
	public void testObjRemoveAllUseless()
	{
		ST source1 = developSource();
		getFacet().removeAll(id, new ArrayList<>(), source1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(0, 0);
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
		listener.assertEventCount(3, 0);
		getFacet().removeAll(id, pct, source1);
		listener.assertEventCount(3, 1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t2));
		assertTrue(setoftwo.contains(t3));
		// Prove independence
		pct.remove(t1);
		Set<CT> set = getFacet().getSet(id);
		assertNotNull(set);
		assertEquals(2, set.size());
		assertTrue(set.contains(t2));
		assertTrue(set.contains(t3));
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
		listener.assertEventCount(3, 0);
		getFacet().removeAll(id, developSource());
		listener.assertEventCount(3, 0);
		getFacet().removeAll(id, source1);
		listener.assertEventCount(3, 2);
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(setofone.contains(t3));
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
		listener.assertEventCount(2, 0);
		getFacet().removeAll(id, pct, source1);
		listener.assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
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
		listener.assertEventCount(2, 0);
		getFacet().removeAll(id, pct, source1);
		listener.assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testObjRemoveAll()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		CT t2 = getTypeObj();
		ST source3 = developSource();
		assertNotNull(getFacet().removeAll(id));
		assertTrue(getFacet().removeAll(id).isEmpty());
		getFacet().add(id, t1, source1);
		getFacet().add(id, t1, source2);
		getFacet().add(id, t2, source3);
		listener.assertEventCount(2, 0);
		Map<CT, ST> map = getFacet().removeAll(id);
		listener.assertEventCount(2, 2);
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
		ST source1 = developSource();
		CT t2 = getTypeObj();
		getFacet().add(id, t1, source1);
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
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
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
		getFacet().remove(id, t1, source1);
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

		getFacet().remove(altid, t2, source1);
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
	public void testObjGetSetSource()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		List<? extends CT> origset = getFacet().getSet(id, source1);
		assertNotNull(origset);
		assertTrue(origset.isEmpty());
		CT t2 = getTypeObj();
		List<CT> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct, source1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<? extends CT> setoftwo = getFacet().getSet(id, source1);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		listener.assertEventCount(2, 0);
		CT t3 = getTypeObj();
		ST source2 = developSource();
		List<CT> pct2 = new ArrayList<>();
		pct2.add(t1);
		pct2.add(t3);
		getFacet().addAll(id, pct2, source2);
		assertEquals(3, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		setoftwo = getFacet().getSet(id, source1);
		assertNotNull(setoftwo);
		assertEquals(1, setoftwo.size());
		assertTrue(setoftwo.contains(t2));
		setoftwo = getFacet().getSet(id, source2);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t3));
		listener.assertEventCount(3, 0);
		getFacet().remove(id, t3, source2);
		List<? extends CT> setofone = getFacet().getSet(id, source2);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		getFacet().remove(id, t1, source2);
		List<? extends CT> emptyset = getFacet().getSet(id, source2);
		assertNotNull(emptyset);
		assertEquals(0, emptyset.size());
	}

	@Test
	public void testObjAddSingleTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
	}

	@Test
	public void testObjAddTwoSourcesTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source2);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
	}

	@Test
	public void testObjGetSource()
	{
		CT t1 = getTypeObj();
		assertNull(getFacet().getSource(id, t1));
		ST source1 = developSource();
		ST source2 = developSource();
		getFacet().add(id, t1, source1);
		assertEquals(source1, getFacet().getSource(id, t1));
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (but twice on that source)
		getFacet().add(id, t1, source2);
		assertEquals(source2, getFacet().getSource(id, t1));
		listener.assertEventCount(1, 0);
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
		assertNull(getFacet().getSource(id, t1));
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		listener.assertEventCount(1, 1);
		assertNull(getFacet().getSource(id, t1));
	}

	protected abstract CT getTypeObj();

	protected abstract AbstractSingleSourceListFacet<CT, ST> getFacet();

	protected abstract ST developSource();
}
