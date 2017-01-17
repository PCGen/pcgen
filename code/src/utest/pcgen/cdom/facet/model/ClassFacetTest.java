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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassFacet.ClassInfo;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;

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

	@Before
	public void setUp() throws Exception
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet.addDataFacetChangeListener(classListener);
		facet.addLevelChangeListener(classListener);
	}

	private void assertEventCount(int a, int r, int l)
	{
		Assert.assertEquals(a, classListener.addEventCount);
		Assert.assertEquals(r, classListener.removeEventCount);
		Assert.assertEquals(l, classListener.levelEventCount);
	}

	@Test
	public void testPCClassUnsetZeroCount()
	{
		Assert.assertEquals(0, facet.getCount(id));
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetZeroLevel()
	{
		Assert.assertEquals(0, facet.getLevel(id, new PCClass()));
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassNullZeroLevel()
	{
		Assert.assertEquals(0, facet.getLevel(id, null));
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetEmpty()
	{
		Assert.assertTrue(facet.isEmpty(id));
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassUnsetEmptySet()
	{
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertTrue(facet.getSet(id).isEmpty());
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddNull()
	{
		try
		{
			facet.addClass(id, null);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEventCount(0, 0, 0);
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddSingleGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// No cross-pollution
		Assert.assertEquals(0, facet.getCount(altid));
		Assert.assertTrue(facet.isEmpty(altid));
		Assert.assertNotNull(facet.getSet(altid));
		Assert.assertTrue(facet.getSet(altid).isEmpty());
		Assert.assertEquals(0, facet.getLevel(altid, t1));
	}

	@Test
	public void testPCClassAddSingleTwiceGet()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Add same, still only once in set (and only one event)
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set (only one add data event, doesn't
		// desroy level)
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
	}

	@Test
	public void testPCClassSetEvent()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set the level
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		ClassLevelChangeEvent event = classListener.lastLevelEvent;
		Assert.assertEquals(id, event.getCharID());
		Assert.assertEquals(t1, event.getPCClass());
		Assert.assertEquals(0, event.getOldLevel());
		Assert.assertEquals(2, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 3);
		Assert.assertEquals(3, facet.getLevel(id, t1));
		assertEventCount(1, 0, 2);
		event = classListener.lastLevelEvent;
		Assert.assertEquals(id, event.getCharID());
		Assert.assertEquals(t1, event.getPCClass());
		Assert.assertEquals(2, event.getOldLevel());
		Assert.assertEquals(3, event.getNewLevel());
		//Set another level
		facet.setLevel(id, t1, 1);
		Assert.assertEquals(1, facet.getLevel(id, t1));
		assertEventCount(1, 0, 3);
		event = classListener.lastLevelEvent;
		Assert.assertEquals(id, event.getCharID());
		Assert.assertEquals(t1, event.getPCClass());
		Assert.assertEquals(3, event.getOldLevel());
		Assert.assertEquals(1, event.getNewLevel());
	}

	@Test
	public void testPCClassAddMultGet()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		PCClass t2 = new PCClass();
		t2.setName("OtherClass");
		facet.addClass(id, t2);
		Assert.assertEquals(2, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setoftwo = facet.getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0, 1);
		facet.setLevel(id, t2, 3);
		Assert.assertEquals(2, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(2, facet.getSet(id).size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		Assert.assertEquals(3, facet.getLevel(id, t2));
		assertEventCount(2, 0, 2);
	}

	@Test
	public void testPCClassContains()
	{
		PCClass t1 = new PCClass();
		Assert.assertFalse(facet.contains(id, t1));
		facet.addClass(id, t1);
		Assert.assertTrue(facet.contains(id, t1));
		facet.removeClass(id, t1);
		Assert.assertFalse(facet.contains(id, t1));
	}

	@Test
	public void testPCClassRemoveUseless()
	{
		try
		{
			facet.removeClass(id, null);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Expected
		}
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		assertEventCount(0, 0, 0);
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassAddSingleRemove()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Remove
		facet.removeClass(id, t1);
		Assert.assertEquals(0, facet.getCount(id));
		Assert.assertTrue(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertTrue(facet.getSet(id).isEmpty());
		Assert.assertEquals(0, facet.getLevel(id, t1));
		// Add one remove event, and one level change event (2->0)
		assertEventCount(1, 1, 2);
	}

	@Test
	public void testPCClassAddUselessRemove()
	{
		PCClass t1 = new PCClass();
		t1.setName("MyClass");
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Set Level
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Useless Remove
		PCClass other = new PCClass();
		other.setName("OtherClass");
		facet.removeClass(id, other);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
	}

	@Test
	public void testPCClassAddSingleTwiceRemoveClass()
	{
		PCClass t1 = new PCClass();
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		assertEventCount(1, 0, 0);
		// Now set level
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Add same, still only once in set
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(2, facet.getLevel(id, t1));
		assertEventCount(1, 0, 1);
		// Only requires one Remove (internally a Set, not List)
		facet.removeClass(id, t1);
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		Assert.assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 1, 2);
		// Second has no effect
		facet.removeClass(id, t1);
		testPCClassUnsetZeroCount();
		testPCClassUnsetEmpty();
		testPCClassUnsetEmptySet();
		Assert.assertEquals(0, facet.getLevel(id, t1));
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
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
		assertEventCount(2, 1, 0);
		facet.removeClass(id, t2);
		assertEventCount(2, 2, 0);
		Assert.assertEquals(0, facet.getCount(id));
		Assert.assertTrue(facet.isEmpty(id));
		Set<PCClass> emptyset = facet.getSet(id);
		Assert.assertNotNull(emptyset);
		Assert.assertEquals(0, emptyset.size());
	}

	@Test
	public void testPCClassRemoveAllSymmetry()
	{
		PCClass t1 = new PCClass();
		Assert.assertNull(facet.removeAllClasses(id));
		facet.addClass(id, t1);
		facet.removeClass(id, t1);
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassRemoveAll()
	{
		Assert.assertNull(facet.removeAllClasses(id));
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
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
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
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
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
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<PCClass>());
			// If we can modify, then make sure it's independent of the facet
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
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
			Assert.assertEquals(1, facet.getCount(id));
			Assert.assertFalse(facet.isEmpty(id));
			Assert.assertNotNull(facet.getSet(id));
			Assert.assertEquals(1, facet.getSet(id).size());
			Assert.assertEquals(t1, facet.getSet(id).iterator().next());
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
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, t1, -2);
			Assert.fail();
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
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			facet.setLevel(id, null, 2);
			Assert.fail();
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
		t1.setName("MyClass");
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Assert.assertNotNull(facet.getSet(id));
		Assert.assertEquals(1, facet.getSet(id).size());
		Assert.assertEquals(t1, facet.getSet(id).iterator().next());
		Assert.assertEquals(0, facet.getLevel(id, t1));
		assertEventCount(1, 0, 0);
		try
		{
			PCClass other = new PCClass();
			other.setName("OtherClass");
			facet.setLevel(id, other, 2);
			Assert.fail();
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
			Assert.assertFalse(facet.setClassLevel(id, null, pcl));
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
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
			Assert.assertFalse(facet.setClassLevel(id, t1, null));
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetClassLevelNotAdded()
	{
		PCClass t1 = new PCClass();
		PCClassLevel pcl = new PCClassLevel();
		try
		{
			Assert.assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertNull(facet.removeAllClasses(id));
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
			Assert.fail();
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
			Assert.fail(e.getMessage());
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
			Assert.assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (IllegalArgumentException e)
		{
			//Yep okay too!
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
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
			Assert.assertTrue(facet.setClassLevel(id, t1, pcl));
			ClassLevelObjectChangeEvent event = classListener.lastLevelObjectEvent;
			Assert.assertEquals(id, event.getCharID());
			Assert.assertEquals(t1, event.getPCClass());
			Assert.assertEquals(old, event.getOldLevel());
			Assert.assertEquals(pcl, event.getNewLevel());
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(pcl, facet.getClassLevel(id, t1, 3));
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
			Assert.assertFalse(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
		try
		{
			facet.getClassLevel(id, t1, 3);
			Assert.fail("This should not work: PC does not have this class");
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
		Assert.assertEquals(0, classListener.addEventObjectCount);
		try
		{
			Assert.assertTrue(facet.setClassLevel(id, t1, pcl));
			ClassLevelObjectChangeEvent event = classListener.lastLevelObjectEvent;
			Assert.assertEquals(id, event.getCharID());
			Assert.assertEquals(t1, event.getPCClass());
			Assert.assertEquals(old, event.getOldLevel());
			Assert.assertEquals(pcl, event.getNewLevel());
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(1, classListener.addEventObjectCount);
		//Now useless but still returns true
		try
		{
			Assert.assertTrue(facet.setClassLevel(id, t1, pcl));
		}
		catch (CloneNotSupportedException e)
		{
			Assert.fail(e.getMessage());
		}
		//Guarantee no new event
		Assert.assertEquals(1, classListener.addEventObjectCount);
		Assert.assertEquals(pcl, facet.getClassLevel(id, t1, 3));
	}

	@Test
	public void testGetClassLevelUnset()
	{
		PCClass cl = new PCClass();
		Assert.assertNull(facet.getClassLevel(id, cl, 1));
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testGetClassLevelOtherSet()
	{
		PCClass cl = new PCClass();
		cl.setName("MyClass");
		facet.addClass(id, new PCClass());
		try
		{
			Assert.assertNull(facet.getClassLevel(id, cl, 1));
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
			Assert.fail();
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
			Assert.fail();
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
		Assert.assertEquals(Integer.valueOf(1), pcl.get(IntegerKey.LEVEL));
	}

	@Test
	public void testGetClassLevelAdded()
	{
		PCClass cl = new PCClass();
		PCClassLevel ocl = cl.getOriginalClassLevel(2);
		ocl.put(IntegerKey.HIT_DIE, 4);
		facet.addClass(id, cl);
		PCClassLevel pcl = facet.getClassLevel(id, cl, 2);
		Assert.assertEquals(Integer.valueOf(2), pcl.get(IntegerKey.LEVEL));
		Assert.assertEquals(Integer.valueOf(4), pcl.get(IntegerKey.HIT_DIE));
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
		Assert.assertEquals(Integer.valueOf(2), pcl.get(IntegerKey.LEVEL));
		Assert.assertEquals(Integer.valueOf(4), pcl.get(IntegerKey.HIT_DIE));
		Assert.assertEquals(3, facet.getLevel(altid, cl));
	}
	@Test

	public void testEmptyCopyContents()
	{
		facet.copyContents(id, altid);
		Assert.assertNull(facet.removeAllClasses(altid));
	}

	@Test
	public void testPCClassReplaceUseless()
	{
		PCClass t1 = new PCClass();
		PCClass t2 = new PCClass();
		facet.replaceClass(id, t1, t2);
		Assert.assertNull(facet.removeAllClasses(id));
	}

	@Test
	public void testPCClassReplace()
	{
		PCClass t1 = new PCClass();
		t1.setName("Base");
		facet.addClass(id, t1);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(2, facet.getLevel(id, t1));
		PCClass t2 = new PCClass();
		t2.setName("Other");
		facet.replaceClass(id, t1, t2);
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		setofone = facet.getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t2, setofone.iterator().next());
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
		Assert.assertEquals(1, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setofone = facet.getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0, 0);
		facet.setLevel(id, t1, 2);
		Assert.assertEquals(2, facet.getLevel(id, t1));
		PCClass t2 = new PCClass();
		t2.setName("Innocent");
		facet.addClass(id, t2);
		PCClass t3 = new PCClass();
		t3.setName("Other");
		facet.replaceClass(id, t1, t3);
		Assert.assertEquals(2, facet.getCount(id));
		Assert.assertFalse(facet.isEmpty(id));
		Set<PCClass> setoftwo = facet.getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t2));
		Assert.assertTrue(setoftwo.contains(t3));
		//TODO This test needs some help, as 
//		assertEquals(0, facet.getLevel(id, t1));
//		assertEquals(2, facet.getLevel(id, t2));
//		//TODO figure out what this is??
//		assertEventCount(2, 1, 0);
	}

}
