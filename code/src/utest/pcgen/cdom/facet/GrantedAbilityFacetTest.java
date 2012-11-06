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
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;

public class GrantedAbilityFacetTest extends TestCase
{
	private CharID id = new CharID();
	private CharID altid = new CharID();
	private GrantedAbilityFacet facet = new GrantedAbilityFacet();

	private Listener listener = new Listener();
	Object oneSource = new Object();

	private static class Listener implements DataFacetChangeListener<Ability>
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
	public void testAbilityUnsetEmpty()
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
	public void testAbilityUnsetEmptySet()
	{
		assertNotNull(facet.getCategories(id));
		assertTrue(facet.getCategories(id).isEmpty());
	}

	/*
	 * public void add(CharID id, Category<Ability> cat, Nature nat, Ability
	 * obj, Object source)
	 */
	@Test
	public void testAbilityAddNull()
	{
		Object source1 = new Object();
		Ability a = new Ability();
		try
		{
			facet.add(id, null, Nature.NORMAL, a, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(0, 0);
		try
		{
			facet.add(id, AbilityCategory.FEAT, null, a, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(0, 0);
		try
		{
			facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, null, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testAbilityAddNullSource()
	{
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, null);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, facet.getCategories(altid).size());
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertTrue(facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL)
				.isEmpty());
	}

	@Test
	public void testAbilityAddSingleGet()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, facet.getCategories(altid).size());
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertTrue(facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL)
				.isEmpty());
	}

	@Test
	public void testAbilityAddSingleSourceTwiceGet()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityAddSingleTwiceTwoSourceGet()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source2);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL)
				.iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityAddMultGet()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT,
				Nature.VIRTUAL);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getCategories(id));
		assertEquals(1, facet.getCategories(id).size());
		assertEquals(AbilityCategory.FEAT, facet.getCategories(id).iterator()
				.next());
		assertEquals(1, facet.getNatures(id, AbilityCategory.FEAT).size());
		assertEquals(Nature.VIRTUAL, facet.getNatures(id, AbilityCategory.FEAT)
				.iterator().next());
		Set<Ability> setoftwo = facet.get(id, AbilityCategory.FEAT,
				Nature.VIRTUAL);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testAbilityContains()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		assertFalse(facet
				.contains(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1));
		assertFalse(facet.contains(id, AbilityCategory.FEAT, Nature.NORMAL, t1));
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertTrue(facet.contains(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1));
		assertFalse(facet.contains(id, AbilityCategory.FEAT, Nature.NORMAL, t1));
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet
				.contains(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1));
		assertFalse(facet.contains(id, AbilityCategory.FEAT, Nature.NORMAL, t1));
	}

	@Test
	public void testAbilityAddAllNull()
	{
		Object source1 = new Object();
		try
		{
			facet.addAll(id, null, Nature.VIRTUAL, new ArrayList<Ability>(),
					source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		try
		{
			facet.addAll(id, AbilityCategory.FEAT, null,
					new ArrayList<Ability>(), source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		try
		{
			facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, null,
					source1);
			fail();
		}
		catch (NullPointerException e)
		{
			// Expected
		}
		assertEventCount(0, 0);
	}

	@Test
	public void testAbilityAddAllUseless()
	{
		Object source1 = new Object();
		facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL,
				new ArrayList<Ability>(), source1);
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testAbilityAddAll()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		List<Ability> pct = new ArrayList<Ability>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, pct, source1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setoftwo = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		// Prove independence
		pct.remove(t2);
		assertFalse(facet.isEmpty(id));
		setoftwo = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
	}

	@Test
	public void testAbilityAddAllSecondSource()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		List<Ability> pct = new ArrayList<Ability>();
		pct.add(t1);
		pct.add(t2);
		facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, pct, source1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setoftwo = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
		Object source2 = new Object();
		Ability t3 = new Ability();
		List<Ability> pct2 = new ArrayList<Ability>();
		pct2.add(t1);
		pct2.add(t3);
		facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, pct2, source2);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofthree = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofthree);
		assertEquals(3, setofthree.size());
		assertTrue(setofthree.contains(t1));
		assertTrue(setofthree.contains(t2));
		assertTrue(setofthree.contains(t3));
		assertEventCount(3, 0);
	}

	@Test
	public void testAbilityAddAllTwice()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		List<Ability> pct = new ArrayList<Ability>();
		pct.add(t1);
		pct.add(t1);
		facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, pct, source1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityAddAllNullInList()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		List<Ability> pct = new ArrayList<Ability>();
		pct.add(t1);
		pct.add(null);
		pct.add(t2);
		try
		{
			facet.addAll(id, AbilityCategory.FEAT, Nature.VIRTUAL, pct, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		/*
		 * TODO This should be zero, one or two???
		 */
		assertFalse(facet.isEmpty(id));
		Set<Ability> setoftwo = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(1, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityRemoveUseless()
	{
		Object source1 = new Object();
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, null, source1);
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testAbilityRemoveUselessSource()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source2);
		// No change (wrong source)
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityAddSingleRemove()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		// Remove
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertTrue(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertTrue(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testAbilityAddUselessRemove()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, new Ability(), source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testAbilityAddSingleTwiceRemove()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		// Add same, now twice in list (but twice on that source)
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		// Only one Remove does not clear
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
		assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		assertEventCount(1, 0);
		// Second remove actually removes
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
		assertEventCount(1, 1);
	}

	@Test
	public void testAbilityAddMultRemove()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testAbilityRemoveAllList()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		Ability t3 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t3, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t3, source2);
		assertEventCount(3, 0);
		facet.removeAll(id, source1);
		assertEventCount(3, 2);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t3));
		// Prove independence
		try
		{
			setofone.remove(t3);
			setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
			assertNotNull(setofone);
			assertEquals(1, setofone.size());
			assertTrue(setofone.contains(t3));
		}
		catch (UnsupportedOperationException e)
		{
			//ok
		}
	}

	@Test
	public void testAbilityRemoveAllSource()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		Ability t3 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t3, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t3, source2);
		assertEventCount(3, 0);
		facet.removeAll(id, new Object());
		assertEventCount(3, 0);
		facet.removeAll(id, source1);
		assertEventCount(3, 2);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t3));
	}

	@Test
	public void testAbilityRemoveAllTwice()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source2);
		assertEventCount(2, 0);
		facet.removeAll(id, source1);
		facet.removeAll(id, source1);
		assertEventCount(2, 1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
	}

	@Test
	public void testAbilityRemoveAll()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		assertNotNull(facet.removeAll(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertTrue(facet.removeAll(id, AbilityCategory.FEAT, Nature.VIRTUAL).isEmpty());
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source2);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source2);
		assertEventCount(2, 0);
		Map<Ability, List<Object>> map = facet.removeAll(id, AbilityCategory.FEAT, Nature.VIRTUAL);
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
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
	}

	@Test
	public void testAbilityRemoveAllClean()
	{
		Object source1 = new Object();
		assertNotNull(facet.removeAll(id, AbilityCategory.FEAT, Nature.VIRTUAL));
		assertTrue(facet.removeAll(id, AbilityCategory.FEAT, Nature.VIRTUAL).isEmpty());
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		assertEventCount(2, 0);
		facet.removeAll(id, source1);
		assertEventCount(2, 2);
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		Set<Ability> set = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		List<Ability> pct = new ArrayList<Ability>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<Ability>());
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL));
			assertEquals(1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).size());
			assertEquals(t1, facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL).iterator().next());
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
		testAbilityUnsetEmpty();
		testAbilityUnsetEmptySet();
	}

	@Test
	public void testCopyContents()
	{
		Object source1 = new Object();
		Ability t1 = new Ability();
		Ability t2 = new Ability();
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		facet.add(id, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		facet.copyContents(id, altid);
		assertFalse(facet.isEmpty(altid));
		Set<Ability> setoftwo = facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		facet.remove(id, AbilityCategory.FEAT, Nature.VIRTUAL, t1, source1);
		assertFalse(facet.isEmpty(id));
		Set<Ability> setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertFalse(facet.isEmpty(altid));
		setoftwo = facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove Independence (remove from altid)

		facet.remove(altid, AbilityCategory.FEAT, Nature.VIRTUAL, t2, source1);
		assertFalse(facet.isEmpty(id));
		setofone = facet.get(id, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertFalse(facet.isEmpty(altid));
		setofone = facet.get(altid, AbilityCategory.FEAT, Nature.VIRTUAL);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
	}

}
