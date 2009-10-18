/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;

public class SourcedEquipmentFacetTest extends TestCase
{
	private CharID id = new CharID();
	private CharID altid = new CharID();
	private SourcedEquipmentFacet facet = new SourcedEquipmentFacet();

	private Listener listener = new Listener();

	private class Listener implements DataFacetChangeListener<Equipment>
	{

		public int addEventCount;
		public int removeEventCount;

		public void dataAdded(DataFacetChangeEvent dfce)
		{
			addEventCount++;
		}

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
	public void testTemplateUnsetZeroCount()
	{
		assertEquals(0, facet.getCount(id));
	}

	@Test
	public void testTemplateUnsetEmpty()
	{
		assertTrue(facet.isEmpty(id));
	}

	@Test
	public void testTemplateUnsetEmptySet()
	{
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
	}

	@Test
	public void testTemplateAddNull()
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
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddNullSource()
	{
		Equipment t1 = new Equipment();
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
	public void testTemplateAddSingleGet()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
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
	public void testTemplateAddSingleSourceTwiceGet()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
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
	public void testTemplateAddSingleTwiceTwoSourceGet()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Equipment t1 = new Equipment();
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
	public void testTemplateAddMultGet()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		Equipment t2 = new Equipment();
		facet.add(id, t2, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testTemplateContains()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		assertFalse(facet.contains(id, t1));
		facet.add(id, t1, source1);
		assertTrue(facet.contains(id, t1));
		facet.remove(id, t1, source1);
		assertFalse(facet.contains(id, t1));
	}

	@Test
	public void testTemplateAddAllNull()
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
	public void testTemplateAddAllUseless()
	{
		Object source1 = new Object();
		facet.addAll(id, new ArrayList<Equipment>(), source1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddAll()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setoftwo = facet.getSet(id);
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
	public void testTemplateAddAllSecondSource()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct, source1);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		Equipment t3 = new Equipment();
		List<Equipment> pct2 = new ArrayList<Equipment>();
		pct2.add(t1);
		pct2.add(t3);
		facet.addAll(id, pct2, source2);
		assertEquals(3, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofthree = facet.getSet(id);
		assertNotNull(setofthree);
		assertEquals(3, setofthree.size());
		assertTrue(setofthree.contains(t1));
		assertTrue(setofthree.contains(t2));
		assertTrue(setofthree.contains(t3));
		assertEventCount(3, 0);
	}

	@Test
	public void testTemplateAddAllTwice()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
		pct.add(t1);
		pct.add(t1);
		facet.addAll(id, pct, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddAllNullInList()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
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
		Set<Equipment> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(1, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateRemoveUseless()
	{
		Object source1 = new Object();
		facet.remove(id, null, source1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateRemoveUselessSource()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
		facet.remove(id, t1, source2);
		//No change (wrong source)
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddSingleRemove()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
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
	public void testTemplateAddUselessRemove()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		facet.add(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		facet.remove(id, new Equipment(), source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddSingleTwiceRemove()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
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
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
		// Second remove useless
		facet.remove(id, t1, source1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testTemplateAddMultRemove()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		facet.remove(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testTemplateRemoveAllNull()
	{
		Object source1 = new Object();
		facet.add(id, new Equipment(), source1);
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
	public void testTemplateRemoveAllUseless()
	{
		Object source1 = new Object();
		facet.removeAll(id, new ArrayList<Equipment>(), source1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateRemoveAllList()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		Equipment t3 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
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
		Set<Equipment> setoftwo = facet.getSet(id);
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
	public void testTemplateRemoveAllTwice()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		facet.removeAll(id, pct, source1);
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAllNullInList()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		List<Equipment> pct = new ArrayList<Equipment>();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		pct.add(t1);
		pct.add(null);
		assertEventCount(2, 0);
		facet.removeAll(id, pct, source1);
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAll()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		assertNotNull(facet.removeAll(id));
		assertTrue(facet.removeAll(id).isEmpty());
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		facet.add(id, t1, source1);
		facet.add(id, t1, source2);
		facet.add(id, t2, source2);
		assertEventCount(2, 0);
		Map<Equipment, Set<Object>> map = facet.removeAll(id);
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
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		facet.add(id, t1, source1);
		Set<Equipment> set = facet.getSet(id);
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
		List<Equipment> pct = new ArrayList<Equipment>();
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
			set.retainAll(new ArrayList<Equipment>());
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
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
	}
	
	@Test
	public void testCopyContents()
	{		
		Object source1 = new Object();
		Equipment t1 = new Equipment();
		Equipment t2 = new Equipment();
		facet.add(id, t1, source1);
		facet.add(id, t2, source1);
		assertEquals(2, facet.getCount(id));
		assertEquals(0, facet.getCount(altid));
		facet.copyContents(id, altid);
		assertEquals(2, facet.getCount(altid));
		assertFalse(facet.isEmpty(altid));
		Set<Equipment> setoftwo = facet.getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		facet.remove(id, t1, source1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<Equipment> setofone = facet.getSet(id);
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
		//Prove Independence (remove from altid)

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

}
