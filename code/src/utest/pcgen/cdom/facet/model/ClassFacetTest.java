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
package pcgen.cdom.facet.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassInfo;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassFacetTest
{
	private CharID id;
	private CharID altid;
	private ClassFacet facet = new ClassFacet();

	private ClassListener classListener = new ClassListener();

	private static class ClassListener implements DataFacetChangeListener<CharID, PCClass>,
			ClassLevelChangeListener
	{

		public int addEventCount;
		public int addEventObjectCount;
		public int removeEventCount;
		public int levelEventCount;
		public ClassLevelChangeEvent lastLevelEvent;
		public ClassLevelObjectChangeEvent lastLevelObjectEvent;

        @Override
		public void dataAdded(DataFacetChangeEvent<CharID, PCClass> dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent<CharID, PCClass> dfce)
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
        	lastLevelObjectEvent = lce;
			addEventObjectCount++;
		}

	}

	@BeforeEach
	public void setUp() {
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet.addDataFacetChangeListener(classListener);
		facet.addLevelChangeListener(classListener);
	}

	@AfterEach
	public void tearDown()
	{
		id = null;
		altid = null;
		facet = null;
		classListener = null;
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
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
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
		catch (NullPointerException e)
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
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// No cross-pollution
		assertEquals(0, facet.getCount(altid));
		assertTrue(facet.isEmpty(altid));
		assertNotNull(facet.getSet(altid));
		assertTrue(facet.getSet(altid).isEmpty());
		assertEquals(0, facet.getLevel(altid, t1));
	}

	@Test
	public void testPCClassAddSingleTwiceGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Add same, still only once in set (and only one event)
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set (only one add data event, doesn't
		// desroy level)
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
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
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		ClassLevelChangeEvent event = classListener.lastLevelEvent;
		assertSame(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(0, event.getOldLevel());
		assertEquals(2, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 3);
		assertEquals(3, facet.getLevel(id, t1));
		assertEventCount(1, 0, 2);
		event = classListener.lastLevelEvent;
		assertSame(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(2, event.getOldLevel());
		assertEquals(3, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 1);
		assertEquals(1, facet.getLevel(id, t1));
		assertEventCount(1, 0, 3);
		event = classListener.lastLevelEvent;
		assertSame(id, event.getCharID());
		assertEquals(t1, event.getPCClass());
		assertEquals(3, event.getOldLevel());
		assertEquals(1, event.getNewLevel());
	}

	@Test
	public void testPCClassAddMultGet()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		PCClass t2 = new PCClass();
		t2.setName("OtherClass");
		facet.addClass(id, t2);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0, 1);
		facet.setLevel(id, t2, 3);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(2, facet.getSet(id).size());
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
		catch (NullPointerException e)
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
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Remove
		facet.removeClass(id, t1);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertTrue(facet.getSet(id).isEmpty());
		assertEquals(0, facet.getLevel(id, t1));
		// Add one remove event, and one level change event (2->0)
		assertEventCount(1, 1, 2);
	}

	@Test
	public void testPCClassAddUselessRemove()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Set Level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Useless Remove
		PCClass other = new PCClass();
		other.setName("OtherClass");
		facet.removeClass(id, other);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
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
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
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
		t1.setName("MyClass");
		PCClass t2 = new PCClass();
		t2.setName("OtherClass");
		facet.addClass(id, t1);
		facet.addClass(id, t2);
		facet.removeClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1, 0);
		facet.removeClass(id, t2);
		assertEventCount(2, 2, 0);
		assertEquals(0, facet.getCount(id));
		assertTrue(facet.isEmpty(id));
		Set<PCClass> emptyset = facet.getSet(id);
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
		t1.setName("MyClass");
		PCClass t2 = new PCClass();
		t2.setName("OtherClass");
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
		Set<PCClass> set = facet.getSet(id);
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
		List<PCClass> pct = new ArrayList<>();
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
			set.retainAll(new ArrayList<PCClass>());
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
		facet.addClass(id, t1);
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
	public void testPCClassSetLevelNegative()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
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
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, null, 2);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testPCClassSetLevelUncontainedClass()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		assertNotNull(facet.getSet(id));
		assertEquals(1, facet.getSet(id).size());
		assertEquals(t1, facet.getSet(id).iterator().next());
		assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			PCClass other = new PCClass();
			other.setName("OtherClass");
			facet.setLevel(id, other, 2);
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
		try
		{
			PCClassLevel pcl = new PCClassLevel();
			assertFalse(facet.setClassLevel(id, null, pcl));
			fail();
		}
		catch (NullPointerException e)
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
		try
		{
			PCClass t1 = new PCClass();
			assertFalse(facet.setClassLevel(id, t1, null));
			fail();
		}
		catch (NullPointerException e)
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
		try
		{
			PCClassLevel pcl = new PCClassLevel();
			PCClass t1 = new PCClass();
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
		//INTENTIONALLY commented out to show what is "bad"
		//pcl.put(IntegerKey.LEVEL, 4);
		try
		{
			PCClassLevel pcl = new PCClassLevel();
			PCClass t1 = new PCClass();
			facet.setClassLevel(id, t1, pcl);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//yep!
		} catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelOtherAdded()
	{
		PCClass cl = new PCClass();
		cl.setName("MyClass");
		facet.addClass(id, cl);
		PCClass t1 = new PCClass();
		t1.setName("OtherClass");
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 4);
		try
		{
			assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (IllegalArgumentException e)
		{
			//Yep okay too!
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevel()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 3);
		PCClassLevel old = facet.getClassLevel(id, t1, 3);
		try
		{
			assertTrue(facet.setClassLevel(id, t1, pcl));
			ClassLevelObjectChangeEvent event = classListener.lastLevelObjectEvent;
			assertSame(id, event.getCharID());
			assertEquals(t1, event.getPCClass());
			assertEquals(old, event.getOldLevel());
			assertEquals(pcl, event.getNewLevel());
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
		assertEquals(pcl, facet.getClassLevel(id, t1, 3));
	}

	@Test
	public void testSetClassLevelNoClass()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		PCClass diff = new PCClass();
		diff.setName("OtherClass");
		facet.addClass(id, diff);
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 3);
		try
		{
			assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			facet.getClassLevel(id, t1, 3);
			fail("This should not work: PC does not have this class");
		}
		catch (IllegalArgumentException e)
		{
			//Yep :)
		}
	}

	@Test
	public void testSetClassLevelUseless()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(IntegerKey.LEVEL, 3);
		PCClassLevel old = facet.getClassLevel(id, t1, 3);
		assertEquals(0, classListener.addEventObjectCount);
		try
		{
			assertTrue(facet.setClassLevel(id, t1, pcl));
			ClassLevelObjectChangeEvent event = classListener.lastLevelObjectEvent;
			assertSame(id, event.getCharID());
			assertEquals(t1, event.getPCClass());
			assertEquals(old, event.getOldLevel());
			assertEquals(pcl, event.getNewLevel());
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
		assertEquals(1, classListener.addEventObjectCount);
		//Now useless but still returns true
		try
		{
			assertTrue(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			fail(e.getMessage());
		}
		//Guarantee no new event
		assertEquals(1, classListener.addEventObjectCount);
		assertEquals(pcl, facet.getClassLevel(id, t1, 3));
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
		cl.setName("MyClass");
		facet.addClass(id, new PCClass());
		try
		{
			assertNull(facet.getClassLevel(id, cl, 1));
		}
		catch (IllegalArgumentException e)
		{
			//Yep okay too!
		}
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
		catch (NullPointerException e)
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
	@Test

	public void testEmptyCopyContents()
	{
		facet.copyContents(id, altid);
		assertNull(facet.removeAllClasses(altid));
	}

	@Test
	public void testPCClassReplaceUseless()
	{
		PCClass t1 = new PCClass();
		PCClass t2 = new PCClass();
		facet.replaceClass(id, t1, t2);
		assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassReplace()
	{
		PCClass t1 = new PCClass();
		t1.setName("Base");
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(2, facet.getLevel(id, t1));
		PCClass t2 = new PCClass();
		t2.setName("Other");
		facet.replaceClass(id, t1, t2);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t2, setofone.iterator().next());
		//TODO This test needs some help, as 
//		assertEquals(0, facet.getLevel(id, t1));
//		assertEquals(2, facet.getLevel(id, t2));
//		//TODO figure out what this is??
//		assertEventCount(2, 1, 0);
	}

	@Test
	public void testPCClassReplaceWithExtra()
	{
		PCClass t1 = new PCClass();
		t1.setName("Base");
		facet.addClass(id, t1);
		assertEquals(1, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		assertEquals(2, facet.getLevel(id, t1));
		PCClass t2 = new PCClass();
		t2.setName("Innocent");
		facet.addClass(id, t2);
		PCClass t3 = new PCClass();
		t3.setName("Other");
		facet.replaceClass(id, t1, t3);
		assertEquals(2, facet.getCount(id));
		assertFalse(facet.isEmpty(id));
		Set<PCClass> setoftwo = facet.getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t2));
		assertTrue(setoftwo.contains(t3));
		//TODO This test needs some help, as 
//		assertEquals(0, facet.getLevel(id, t1));
//		assertEquals(2, facet.getLevel(id, t2));
//		//TODO figure out what this is??
//		assertEventCount(2, 1, 0);
	}

}
