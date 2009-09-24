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
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.PCTemplate;

public class TemplateFacetTest extends TestCase
{
	private CharID id;
	private CharID altid;
	private TemplateFacet facet = new TemplateFacet();

	private Listener listener = new Listener();

	private class Listener implements DataFacetChangeListener<PCTemplate>
	{

		public int addEventCount;
		public int removeEventCount;

		public void dataAdded(DataFacetChangeEvent<PCTemplate> dfce)
		{
			addEventCount++;
		}

		public void dataRemoved(DataFacetChangeEvent<PCTemplate> dfce)
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
		try
		{
			facet.add(id, null);
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
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
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
	public void testTemplateAddSingleTwiceGet()
	{
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		facet.add(id, t1);
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
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		PCTemplate t2 = new PCTemplate();
		facet.add(id, t2);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testTemplateContains()
	{
		PCTemplate t1 = new PCTemplate();
		assertFalse(facet.contains(id, t1));
		facet.add(id, t1);
		assertTrue(facet.contains(id, t1));
		facet.remove(id, t1);
		assertFalse(facet.contains(id, t1));
	}

	@Test
	public void testTemplateAddAllNull()
	{
		try
		{
			facet.addAll(id, null);
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
		facet.addAll(id, new ArrayList<PCTemplate>());
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateAddAll()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, pct);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setoftwo = facet.getSet(id);
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
	public void testTemplateAddAllTwice()
	{
		PCTemplate t1 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		pct.add(t1);
		pct.add(t1);
		facet.addAll(id, pct);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testTemplateAddAllNullInList()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		try
		{
			facet.addAll(id, pct);
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
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
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
			facet.remove(id, null);
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
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		facet.remove(id, t1);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testTemplateAddUselessRemove()
	{
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		facet.remove(id, new PCTemplate());
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
		PCTemplate t1 = new PCTemplate();
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set
		facet.add(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Only requires one Remove (internally a Set, not List)
		facet.remove(id, t1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
		// Second has no effect
		facet.remove(id, t1);
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testTemplateAddMultRemove()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		facet.add(id, t1);
		facet.add(id, t2);
		facet.remove(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testTemplateRemoveAllNull()
	{
		facet.add(id, new PCTemplate());
		try
		{
			facet.removeAll(id, null);
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
		facet.removeAll(id, new ArrayList<PCTemplate>());
		testTemplateUnsetZeroCount();
		testTemplateUnsetEmpty();
		testTemplateUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTemplateRemoveAllList()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		PCTemplate t3 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		facet.add(id, t1);
		facet.add(id, t2);
		facet.add(id, t3);
		pct.add(t1);
		pct.add(t3);
		assertEventCount(3, 0);
		facet.removeAll(id, pct);
		assertEventCount(3, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		// Prove independence
		pct.remove(t1);
		setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAllTwice()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		facet.add(id, t1);
		facet.add(id, t2);
		pct.add(t1);
		pct.add(t1);
		assertEventCount(2, 0);
		facet.removeAll(id, pct);
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAllNullInList()
	{
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
		facet.add(id, t1);
		facet.add(id, t2);
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		assertEventCount(2, 0);
		try
		{
			facet.removeAll(id, pct);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		assertEventCount(2, 1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCTemplate> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testTemplateRemoveAll()
	{
		assertNotNull(facet.removeAll(id));
		assertTrue(facet.removeAll(id).isEmpty());
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		facet.add(id, t1);
		facet.add(id, t2);
		assertEventCount(2, 0);
		Set<PCTemplate> setoftwo = facet.removeAll(id);
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
		PCTemplate t1 = new PCTemplate();
		PCTemplate t2 = new PCTemplate();
		facet.add(id, t1);
		Set<PCTemplate> set = facet.getSet(id);
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
		List<PCTemplate> pct = new ArrayList<PCTemplate>();
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
			set.retainAll(new ArrayList<PCTemplate>());
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
		facet.add(id, t1);
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

}
