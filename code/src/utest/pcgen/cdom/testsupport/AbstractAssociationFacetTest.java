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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractAssociationFacet;

public abstract class AbstractAssociationFacetTest<CT, ST> extends
		TestCase
{
	private CharID id;
	private CharID altid;

	ST oneSource = developSource(getTypeObj());

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
	}

	@Test
	public void testObjUnsetZeroCount()
	{
		Assert.assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testObjUnsetEmpty()
	{
		Assert.assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testRemoveAllUnsetEmpty()
	{
		// Not particularly a test, just make sure it doesn't throw an exception
		getFacet().removeAll(id);
	}

	@Test
	public void testObjUnsetEmptySet()
	{
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTypeAddNullID()
	{
		ST source1 = developSource(getTypeObj());
		try
		{
			getFacet().set(null, getTypeObj(), source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjAddNull()
	{
		ST source1 = developSource(getTypeObj());
		try
		{
			getFacet().set(id, null, source1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjAddNullSource()
	{
		CT t1 = getTypeObj();
		try
		{
			getFacet().set(id, t1, null);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Assert.assertEquals(0, getFacet().getCount(id));
		Assert.assertTrue(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testObjAddSingleGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// No cross-pollution
		Assert.assertEquals(0, getFacet().getCount(altid));
		Assert.assertTrue(getFacet().isEmpty(altid));
		Assert.assertNotNull(getFacet().getSet(altid));
		Assert.assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testObjAddSingleSourceTwiceGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Add same, still only once in set (and only one event)
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
	}

	@Test
	public void testObjAddSingleTwiceTwoSourceGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		ST source2 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Add same, still only once in set (and only one event)
		getFacet().set(id, t1, source2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
	}

	@Test
	public void testObjAddMultGet()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertEquals(t1, setofone.iterator().next());
		CT t2 = getTypeObj();
		getFacet().set(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setoftwo = getFacet().getSet(id);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
	}

	@Test
	public void testObjContains()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		Assert.assertFalse(getFacet().contains(id, t1));
		getFacet().set(id, t1, source1);
		Assert.assertTrue(getFacet().contains(id, t1));
		getFacet().remove(id, t1);
		Assert.assertFalse(getFacet().contains(id, t1));
	}

	@Test
	public void testObjRemoveUseless()
	{
		ST source1 = developSource(getTypeObj());
		getFacet().remove(id, null);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjAddSingleRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Remove
		getFacet().remove(id, t1);
		Assert.assertEquals(0, getFacet().getCount(id));
		Assert.assertTrue(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testObjAddUselessRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Useless Remove
		getFacet().remove(id, getTypeObj());
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
	}

	@Test
	public void testObjAddSingleTwiceRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Add same, still only once in set (but twice on that source)
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjAddMultRemove()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		CT t2 = getTypeObj();
		getFacet().set(id, t1, source1);
		getFacet().set(id, t2, source1);
		getFacet().remove(id, t1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));
	}

	@Test
	public void testObjRemoveNullInnocent()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		getFacet().remove(id, null);
	}

	@Test
	public void testObjRemoveAll()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		ST source2 = developSource(t1);
		CT t2 = getTypeObj();
		ST source3 = developSource(t2);
		Assert.assertNotNull(getFacet().removeAll(id));
		Assert.assertTrue(getFacet().removeAll(id).isEmpty());
		getFacet().set(id, t1, source1);
		getFacet().set(id, t1, source2);
		getFacet().set(id, t2, source3);
		Map<CT, ST> map = getFacet().removeAll(id);
		Assert.assertNotNull(map);
		Assert.assertEquals(2, map.size());
		Assert.assertTrue(map.containsKey(t1));
		Assert.assertTrue(map.containsKey(t2));
		Assert.assertNotNull(map.get(t1));
		Assert.assertNotNull(map.get(t2));
		Assert.assertEquals(source2, map.get(t1));
		Assert.assertEquals(source3, map.get(t2));
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testGetSetIndependence()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		CT t2 = getTypeObj();
		getFacet().set(id, t1, source1);
		Set<CT> set = getFacet().getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
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
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		getFacet().set(id, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the
			// getFacet()
			Assert.assertEquals(1, getFacet().getCount(id));
			Assert.assertFalse(getFacet().isEmpty(id));
			Assert.assertNotNull(getFacet().getSet(id));
			Assert.assertEquals(1, getFacet().getSet(id).size());
			Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
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
		ST source1 = developSource(t1);
		CT t2 = getTypeObj();
		getFacet().set(id, t1, source1);
		getFacet().set(id, t2, source1);
		Assert.assertEquals(2, getFacet().getCount(id));
		Assert.assertEquals(0, getFacet().getCount(altid));
		getFacet().copyContents(id, altid);
		Assert.assertEquals(2, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		Set<CT> setoftwo = getFacet().getSet(altid);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		getFacet().remove(id, t1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Set<CT> setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));

		Assert.assertEquals(2, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		setoftwo = getFacet().getSet(altid);
		Assert.assertNotNull(setoftwo);
		Assert.assertEquals(2, setoftwo.size());
		Assert.assertTrue(setoftwo.contains(t1));
		Assert.assertTrue(setoftwo.contains(t2));
		// Prove Independence (remove from altid)

		getFacet().remove(altid, t2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		setofone = getFacet().getSet(id);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t2));

		Assert.assertEquals(1, getFacet().getCount(altid));
		Assert.assertFalse(getFacet().isEmpty(altid));
		setofone = getFacet().getSet(altid);
		Assert.assertNotNull(setofone);
		Assert.assertEquals(1, setofone.size());
		Assert.assertTrue(setofone.contains(t1));
	}

	@Test
	public void testObjAddSingleTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Add same, still only once in set (but twice on that source)
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjAddTwoSourcesTwiceRemoveUnsourced()
	{
		CT t1 = getTypeObj();
		ST source1 = developSource(t1);
		ST source2 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Add same, still only once in set (but twice on that source)
		getFacet().set(id, t1, source2);
		Assert.assertEquals(1, getFacet().getCount(id));
		Assert.assertFalse(getFacet().isEmpty(id));
		Assert.assertNotNull(getFacet().getSet(id));
		Assert.assertEquals(1, getFacet().getSet(id).size());
		Assert.assertEquals(t1, getFacet().getSet(id).iterator().next());
		
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
	}

	@Test
	public void testObjGetSource()
	{
		CT t1 = getTypeObj();
		Assert.assertNull(getFacet().get(id, t1));
		ST source1 = developSource(t1);
		ST source2 = developSource(t1);
		getFacet().set(id, t1, source1);
		Assert.assertEquals(source1, getFacet().get(id, t1));
		
		// Add same, still only once in set (but twice on that source)
		getFacet().set(id, t1, source2);
		Assert.assertEquals(source2, getFacet().get(id, t1));
		
		// Only one Remove required to clear (source Set not source List)
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		Assert.assertNull(getFacet().get(id, t1));
		// Second remove useless
		getFacet().remove(id, t1);
		testObjUnsetZeroCount();
		testObjUnsetEmpty();
		testObjUnsetEmptySet();
		Assert.assertNull(getFacet().get(id, t1));
	}

	protected abstract CT getTypeObj();

	protected abstract AbstractAssociationFacet<CharID, CT, ST> getFacet();

	protected abstract ST developSource(CT obj);

}
