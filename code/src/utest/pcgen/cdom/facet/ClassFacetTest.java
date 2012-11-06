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
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.ClassFacet.ClassInfo;
import pcgen.cdom.facet.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;

public class ClassFacetTest extends TestCase
{
	private CharID id = new CharID();
	private CharID altid = new CharID();
	private ClassFacet facet = new ClassFacet();

	private ClassListener classListener = new ClassListener();

	private static class ClassListener implements DataFacetChangeListener<PCClass>,
			ClassLevelChangeListener
	{

		public int addEventCount;
		public int removeEventCount;
		public int levelEventCount;
		public ClassLevelChangeEvent lastLevelEvent;

        @Override
		public void dataAdded(DataFacetChangeEvent<PCClass> dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent<PCClass> dfce)
		{
			removeEventCount++;
		}

        @Override
		public void levelChanged(ClassLevelChangeEvent lce)
		{
			levelEventCount++;
			lastLevelEvent = lce;
		}

        @Override
		public void levelObjectChanged(ClassLevelObjectChangeEvent lce)
		{
		}

	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		facet.addDataFacetChangeListener(classListener);
		facet.addLevelChangeListener(classListener);
	}

	private void assertEventCount(int a, int r, int l)
	{
		assertEquals(a, classListener.addEventCount);
		assertEquals(r, classListener.removeEventCount);
		assertEquals(l, classListener.levelEventCount);
	}

	@Test
	public void testPCClassUnsetZeroCount()
	{
		assertEquals(0, facet.getCount(id));
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetZeroLevel()
	{
		assertEquals(0, facet.getLevel(id, new PCClass()));
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassNullZeroLevel()
	{
		assertEquals(0, facet.getLevel(id, null));
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetEmpty()
	{
		assertTrue(facet.isEmpty(id));
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetEmptySet()
	{
		assertNotNull(facet.getClassSet(id));
		assertTrue(facet.getClassSet(id).isEmpty());
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddNull()
	{
		try
		{
			facet.addClass(id, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEventCount(0, 0, 0);
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddSingleGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// No cross-pollution
		assertEquals(0, facet.getCount(altid));
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.getClassSet(altid));
		assertTrue(facet.getClassSet(altid).isEmpty());
		assertEquals(0, facet.getLevel(altid, t1));
	}

	@Test
	public void testPCClassAddSingleTwiceGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Add same, still only once in set (and only one event)
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set (only one add data event, doesn't
		// desroy level)
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
	}

	@Test
	public void testPCClassSetEvent()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		ClassLevelChangeEvent event = classListener.lastLevelEvent;
		assertEquals(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(0, event.getOldLevel());
		assertEquals(2, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 3);
		assertEquals(3, facet.getLevel(id, t1));
		assertEventCount(1, 0, 2);
		event = classListener.lastLevelEvent;
		assertEquals(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(2, event.getOldLevel());
		assertEquals(3, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 1);
		assertEquals(1, facet.getLevel(id, t1));
		assertEventCount(1, 0, 3);
		event = classListener.lastLevelEvent;
		assertEquals(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(3, event.getOldLevel());
		assertEquals(1, event.getNewLevel());
	}

	@Test
	public void testPCClassAddMultGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getClassSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		PCClass t2 = new PCClass();
		facet.addClass(id, t2);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setoftwo = facet.getClassSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0, 1);
		facet.setLevel(id, t2, 3);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(2, facet.getClassSet(id).size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEquals(3, facet.getLevel(id, t2));
		assertEventCount(2, 0, 2);
	}

	@Test
	public void testPCClassContains()
	{
		PCClass t1 = new PCClass();
		assertFalse(facet.contains(id, t1));
		facet.addClass(id, t1);
		assertTrue(facet.contains(id, t1));
		facet.removeClass(id, t1);
		assertFalse(facet.contains(id, t1));
	}

	@Test
	public void testPCClassRemoveUseless()
	{
		try
		{
			facet.removeClass(id, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEventCount(0, 0, 0);
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddSingleRemove()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Remove
		facet.removeClass(id, t1);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertTrue(facet.getClassSet(id).isEmpty());
		assertEquals(0, facet.getLevel(id, t1));
		// Add one remove event, and one level change event (2->0)
		assertEventCount(1, 1, 2);
	}

	@Test
	public void testPCClassAddUselessRemove()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Set Level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Useless Remove
		facet.removeClass(id, new PCClass());
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
	}

	@Test
	public void testPCClassAddSingleTwiceRemoveClass()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Only requires one Remove (internally a Set, not List)
		facet.removeClass(id, t1);
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 1, 2);
		// Second has no effect
		facet.removeClass(id, t1);
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 1, 2);
	}

	@Test
	public void testPCClassAddMultRemove()
	{
		PCClass t1 = new PCClass();
		PCClass t2 = new PCClass();
		facet.addClass(id, t1);
		facet.addClass(id, t2);
		facet.removeClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getClassSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1, 0);
		facet.removeClass(id, t2);
		assertEventCount(2, 2, 0);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		Set<PCClass> emptyset = facet.getClassSet(id);
		assertNotNull(emptyset);
		assertEquals(0, emptyset.size());
	}

	@Test
	public void testPCClassRemoveAllSymmetry()
	{
		PCClass t1 = new PCClass();
		assertNull(facet.removeAllClasses(id));
		facet.addClass(id, t1);
		facet.removeClass(id, t1);
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassRemoveAll()
	{
		assertNull(facet.removeAllClasses(id));
		PCClass t1 = new PCClass();
		PCClass t2 = new PCClass();
		facet.addClass(id, t1);
		facet.setLevel(id, t1, 3);
		facet.addClass(id, t2);
		facet.setLevel(id, t2, 5);
		assertEventCount(2, 0, 2);
		ClassInfo ci = facet.removeAllClasses(id);
		Set<PCClass> setoftwo = ci.getClassSet();
		assertEventCount(2, 2, 4);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		PCClass t1 = new PCClass();
		PCClass t2 = new PCClass();
		facet.addClass(id, t1);
		Set<PCClass> set = facet.getClassSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
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
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		List<PCClass> pct = new ArrayList<PCClass>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
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
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<PCClass>());
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		facet.addClass(id, t1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, facet.getCount(id));
			assertFalse(facet.isEmpty(id));
			assertNotNull(facet.getClassSet(id));
			assertEquals(1, facet.getClassSet(id).size());
			assertEquals(t1, facet.getClassSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
	}

	@Test
	public void testPCClassSetLevelNegative()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, t1, -2);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testPCClassSetLevelNullClass()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, null, 2);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testPCClassSetLevelUncontainedClass()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getClassSet(id));
		assertEquals(1, facet.getClassSet(id).size());
		assertEquals(t1, facet.getClassSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, new PCClass(), 2);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testSetClassLevelNull()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		PCClassLevel pcl = new PCClassLevel();
		try
		{
			assertFalse(facet.setClassLevel(id, null, pcl));
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelNullLevel()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		PCClass t1 = new PCClass();
		try
		{
			assertFalse(facet.setClassLevel(id, t1, null));
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelNotAdded()
	{
		PCClass t1 = new PCClass();
		PCClassLevel pcl = new PCClassLevel();
		try
		{
			assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testSetClassLevelBadLevel()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		PCClass t1 = new PCClass();
		PCClassLevel pcl = new PCClassLevel();
		//INTENTIONALLY commented out to show what is "bad"
		//pcl.put(IntegerKey.LEVEL, 4);
		try
		{
			facet.setClassLevel(id, t1, pcl);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//yep!
		}
		catch (NullPointerException e)
		{
			//okay too!
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelOtherAdded()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		PCClass t1 = new PCClass();
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 4);
		try
		{
			assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelNotSet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 3);
		try
		{
			assertTrue(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetClassLevelUnset()
	{
		PCClass cl = new PCClass();
		assertNull(facet.getClassLevel(id, cl, 1));
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testGetClassLevelOtherSet()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, new PCClass());
		assertNull(facet.getClassLevel(id, cl, 1));
	}

	@Test
	public void testGetClassLevelNullClass()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		try
		{
			facet.getClassLevel(id, null, 1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Yep!
		}
	}

	@Test
	public void testGetClassLevelNegativeLevel()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		try
		{
			facet.getClassLevel(id, cl, -3);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep
		}
	}

	@Test
	public void testGetClassLevelDefaultAdded()
	{
		PCClass cl = new PCClass();
		facet.addClass(id, cl);
		PCClassLevel pcl = facet.getClassLevel(id, cl, 1);
		assertEquals(Integer.valueOf(1), pcl.get(IntegerKey.LEVEL));
	}

	@Test
	public void testGetClassLevelAdded()
	{
		PCClass cl = new PCClass();
		PCClassLevel ocl = cl.getOriginalClassLevel(2);
		ocl.put(IntegerKey.HIT_DIE, 4);
		facet.addClass(id, cl);
		PCClassLevel pcl = facet.getClassLevel(id, cl, 2);
		assertEquals(Integer.valueOf(2), pcl.get(IntegerKey.LEVEL));
		assertEquals(Integer.valueOf(4), pcl.get(IntegerKey.HIT_DIE));
	}

	@Test
	public void testCopyContents()
	{
		PCClass cl = new PCClass();
		PCClassLevel ocl = cl.getOriginalClassLevel(2);
		ocl.put(IntegerKey.HIT_DIE, 4);
		facet.addClass(id, cl);
		facet.setLevel(id, cl, 3);
		facet.copyContents(id, altid);
		PCClassLevel pcl = facet.getClassLevel(altid, cl, 2);
		assertEquals(Integer.valueOf(2), pcl.get(IntegerKey.LEVEL));
		assertEquals(Integer.valueOf(4), pcl.get(IntegerKey.HIT_DIE));
		assertEquals(3, facet.getLevel(altid, cl));
	}
}
