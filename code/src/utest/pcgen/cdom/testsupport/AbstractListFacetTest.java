/*
 * Copyright (c) 2009-2010 Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractListFacetTest<T>
{
	protected CharID id;
	protected CharID altid;
	protected TestFacetListener<T> listener;

	@BeforeEach
	public void setUp()
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		listener = new TestFacetListener<T>();
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
	public void testListUnsetZeroCount()
	{
		assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testListUnsetEmpty()
	{
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testListUnsetEmptySet()
	{
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTypeAddNullID()
	{
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		assertThrows(NullPointerException.class, () -> getFacet().add(null, getObject()));
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testAddNullItem()
	{
		assertThrows(NullPointerException.class, () -> getFacet().add(id, null));
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testAddSingleGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
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
	public void testAddSingleTwiceGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testAddMultGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		listener.assertEventCount(1, 0);
		T t2 = getObject();
		getFacet().add(id, t2);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		listener.assertEventCount(2, 0);
	}

	@Test
	public void testContains()
	{
		T t1 = getObject();
		assertFalse(getFacet().contains(id, t1));
		getFacet().add(id, t1);
		assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1);
		assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testAddAllNull()
	{
		assertThrows(NullPointerException.class, () -> getFacet().addAll(id, null));
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testAddAllUseless()
	{
		getFacet().addAll(id, new ArrayList<>());
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testAddAll()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		getFacet().addAll(id, pct);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setoftwo = getFacet().getSet(id);
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
	public void testAddAllTwice()
	{
		T t1 = getObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t1);
		getFacet().addAll(id, pct);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testAddAllNullInList()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		assertThrows(NullPointerException.class, () -> getFacet().addAll(id, pct));
		/*
		 * TODO This should be zero, one or two??? Not a huge fan of only 1, but
		 * not sure what the better solution is to keep facet code from becoming
		 * too complex. Either way, this situation is bad, so the stack trace
		 * genereted by the IllegalArgumentException will get attention :)
		 */
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testRemoveUseless()
	{
		assertThrows(NullPointerException.class, () -> getFacet().remove(id, null));
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testAddSingleRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Remove
		getFacet().remove(id, t1);
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
		listener.assertEventCount(1, 1);
	}

	@Test
	public void testAddUselessRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getObject());
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
	}

	@Test
	public void testAddSingleTwiceRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, still only once in set
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Only requires one Remove (internally a Set, not List)
		getFacet().remove(id, t1);
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(1, 1);
		// Second has no effect
		getFacet().remove(id, t1);
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(1, 1);
	}

	@Test
	public void testAddMultRemove()
	{
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		getFacet().remove(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		listener.assertEventCount(2, 1);
	}

	@Test
	public void testRemoveAllNull()
	{
		getFacet().add(id, getObject());
		assertThrows(NullPointerException.class, () -> getFacet().removeAll(id, null));
	}

	@Test
	public void testRemoveAllUseless()
	{
		getFacet().removeAll(id, new ArrayList<>());
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(0, 0);
	}

	@Test
	public void testRemoveAllList()
	{
		T t1 = getObject();
		T t2 = getObject();
		T t3 = getObject();
		List<T> pct = new ArrayList<>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		getFacet().add(id, t3);
		pct.add(t1);
		pct.add(t3);
		listener.assertEventCount(3, 0);
		getFacet().removeAll(id, pct);
		listener.assertEventCount(3, 2);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		// Prove independence
		pct.remove(t1);
		setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testRemoveAllTwice()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		pct.add(t1);
		pct.add(t1);
		listener.assertEventCount(2, 0);
		getFacet().removeAll(id, pct);
		listener.assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testRemoveAllNullInList()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		listener.assertEventCount(2, 0);
		assertThrows(NullPointerException.class, () -> getFacet().removeAll(id, pct));
		listener.assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testRemoveAll()
	{
		assertNotNull(getFacet().removeAll(id));
		assertTrue(getFacet().removeAll(id).isEmpty());
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		listener.assertEventCount(2, 0);
		Collection<T> setoftwo = getFacet().removeAll(id);
		listener.assertEventCount(2, 2);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1);
		Collection<T> set = getFacet().getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
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
			// If we can modify, then make sure it's independent of the facet
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
		List<T> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
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
			// If we can modify, then make sure it's independent of the facet
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
			set.retainAll(new ArrayList<T>());
			// If we can modify, then make sure it's independent of the facet
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
		getFacet().add(id, t1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
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
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
	}

	@Test
	public void testCopyContents()
	{
		T t1 = getObject();
		T t2 = getAltObject();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		assertEquals(2, getFacet().getCount(id));
		assertEquals(0, getFacet().getCount(altid));
		getFacet().copyContents(id, altid);
		assertEquals(2, getFacet().getCount(altid));
		assertFalse(getFacet().isEmpty(altid));
		Collection<T> setoftwo = getFacet().getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		getFacet().remove(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
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

	protected T getAltObject()
	{
		return getObject();
	}

	protected abstract T getObject();

	protected abstract AbstractListFacet<CharID, T> getFacet();


	public static void addBonus(Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

}
