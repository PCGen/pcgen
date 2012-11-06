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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.AbstractListFacet;
import pcgen.cdom.facet.DataFacetChangeEvent;
import pcgen.cdom.facet.DataFacetChangeListener;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

public abstract class AbstractListFacetTest<T> extends TestCase
{
	private CharID id;
	private CharID altid;

	private Listener listener = new Listener();

	private class Listener implements DataFacetChangeListener<T>
	{

		public int addEventCount;
		public int removeEventCount;

        @Override
		public void dataAdded(DataFacetChangeEvent<T> dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent<T> dfce)
		{
			removeEventCount++;
		}

	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		id = new CharID();
		altid = new CharID();
		getFacet().addDataFacetChangeListener(listener);
	}

	private void assertEventCount(int a, int r)
	{
		assertEquals(a, listener.addEventCount);
		assertEquals(r, listener.removeEventCount);
	}

	@Test
	public void testTemplateUnsetZeroCount()
	{
		assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testTemplateUnsetEmpty()
	{
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testTemplateUnsetEmptySet()
	{
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTemplateAddNull()
	{
		try
		{
			getFacet().add(id, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddSingleGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, getFacet().getCount(altid));
		assertTrue(getFacet().isEmpty(altid));
		assertNotNull(getFacet().getSet(altid));
		assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testTemplateAddSingleTwiceGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddMultGet()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		T t2 = getObject();
		getFacet().add(id, t2);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testTemplateContains()
	{
		T t1 = getObject();
		assertFalse(getFacet().contains(id, t1));
		getFacet().add(id, t1);
		assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1);
		assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testTemplateAddAllNull()
	{
		try
		{
			getFacet().addAll(id, null);
			fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddAllUseless()
	{
		getFacet().addAll(id, new ArrayList<T>());
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddAll()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<T>();
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
		assertEventCount(2, 0);
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
	public void testTemplateAddAllTwice()
	{
		T t1 = getObject();
		List<T> pct = new ArrayList<T>();
		pct.add(t1);
		pct.add(t1);
		getFacet().addAll(id, pct);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddAllNullInList()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<T>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		try
		{
			getFacet().addAll(id, pct);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
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
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateRemoveUseless()
	{
		try
		{
			getFacet().remove(id, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddSingleRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		getFacet().remove(id, t1);
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testTemplateAddUselessRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getObject());
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddSingleTwiceRemove()
	{
		T t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only requires one Remove (internally a Set, not List)
		getFacet().remove(id, t1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
		// Second has no effect
		getFacet().remove(id, t1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testTemplateAddMultRemove()
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
		assertEventCount(2, 1);
	}

	@Test
	public void testTemplateRemoveAllNull()
	{
		getFacet().add(id, getObject());
		try
		{
			getFacet().removeAll(id, null);
			fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
	}

	@Test
	public void testTemplateRemoveAllUseless()
	{
		getFacet().removeAll(id, new ArrayList<T>());
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateRemoveAllList()
	{
		T t1 = getObject();
		T t2 = getObject();
		T t3 = getObject();
		List<T> pct = new ArrayList<T>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		getFacet().add(id, t3);
		pct.add(t1);
		pct.add(t3);
		assertEventCount(3, 0);
		getFacet().removeAll(id, pct);
		assertEventCount(3, 2);
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
	public void testTemplateRemoveAllTwice()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<T>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		getFacet().removeAll(id, pct);
		assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAllNullInList()
	{
		T t1 = getObject();
		T t2 = getObject();
		List<T> pct = new ArrayList<T>();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		assertEventCount(2, 0);
		try
		{
			getFacet().removeAll(id, pct);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		assertEventCount(2, 1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<T> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAll()
	{
		assertNotNull(getFacet().removeAll(id));
		assertTrue(getFacet().removeAll(id).isEmpty());
		T t1 = getObject();
		T t2 = getObject();
		getFacet().add(id, t1);
		getFacet().add(id, t2);
		assertEventCount(2, 0);
		Collection<T> setoftwo = getFacet().removeAll(id);
		assertEventCount(2, 2);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
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
		List<T> pct = new ArrayList<T>();
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

	protected abstract T getObject();

	protected abstract AbstractListFacet<T> getFacet();


	public static void addBonus(String name, Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz, name);
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

}
