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

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

public abstract class AbstractItemFacetTest<T> extends TestCase
{
	private CharID id;
	private CharID altid;

	private Listener listener = new Listener();

	public class Listener implements DataFacetChangeListener<CharID, T>
	{

		public int addEventCount;
		public int removeEventCount;

        @Override
		public void dataAdded(DataFacetChangeEvent<CharID, T> dfce)
		{
			addEventCount++;
		}

        @Override
		public void dataRemoved(DataFacetChangeEvent<CharID, T> dfce)
		{
			removeEventCount++;
		}

	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		getFacet().addDataFacetChangeListener(listener);
	}

	private void assertEventCount(int a, int r)
	{
		Assert.assertEquals(a, listener.addEventCount);
		Assert.assertEquals(r, listener.removeEventCount);
	}

	@Test
	public void testItemUnsetEmpty()
	{
		Assert.assertNull(getFacet().get(id));
		Assert.assertTrue(getFacet().matches(id, null));
	}

	public void testListeners()
	{
		Listener newL = new Listener();
		// Useless Removal
		getFacet().removeDataFacetChangeListener(newL);
		getFacet().addDataFacetChangeListener(newL);
		testItemSetGet();
		// Remove added first
		getFacet().removeDataFacetChangeListener(listener);
		// Note assert event count works because no new events added
		testItemSetGet();
		Assert.assertEquals(2, newL.addEventCount);
		Assert.assertEquals(1, newL.removeEventCount);
		Listener thirdL = new Listener();
		getFacet().addDataFacetChangeListener(thirdL);
		Listener fourthL = new Listener();
		getFacet().addDataFacetChangeListener(fourthL);
		// Note assert event count works because no new events added
		testItemSetGet();
		Assert.assertEquals(3, newL.addEventCount);
		Assert.assertEquals(2, newL.removeEventCount);
		Assert.assertEquals(1, thirdL.addEventCount);
		Assert.assertEquals(1, thirdL.removeEventCount);
		Assert.assertEquals(1, fourthL.addEventCount);
		Assert.assertEquals(1, fourthL.removeEventCount);
		// Remove middle
		getFacet().removeDataFacetChangeListener(thirdL);
		// Note assert event count works because no new events added
		testItemSetGet();
		Assert.assertEquals(4, newL.addEventCount);
		Assert.assertEquals(3, newL.removeEventCount);
		Assert.assertEquals(1, thirdL.addEventCount);
		Assert.assertEquals(1, thirdL.removeEventCount);
		Assert.assertEquals(2, fourthL.addEventCount);
		Assert.assertEquals(2, fourthL.removeEventCount);
		// Remove added last
		getFacet().removeDataFacetChangeListener(fourthL);
		// Note assert event count works because no new events added
		testItemSetGet();
		Assert.assertEquals(5, newL.addEventCount);
		Assert.assertEquals(4, newL.removeEventCount);
		Assert.assertEquals(1, thirdL.addEventCount);
		Assert.assertEquals(1, thirdL.removeEventCount);
		Assert.assertEquals(2, fourthL.addEventCount);
		Assert.assertEquals(2, fourthL.removeEventCount);
		// Remove only
		getFacet().removeDataFacetChangeListener(newL);
		// Note assert event count works because no new events added
		testItemSetGet();
		Assert.assertEquals(5, newL.addEventCount);
		Assert.assertEquals(4, newL.removeEventCount);
		Assert.assertEquals(1, thirdL.addEventCount);
		Assert.assertEquals(1, thirdL.removeEventCount);
		Assert.assertEquals(2, fourthL.addEventCount);
		Assert.assertEquals(2, fourthL.removeEventCount);
	}

	@Test
	public void testItemSetNull()
	{
		try
		{
			getFacet().set(id, null);
			/*
			 * For now, this won't fail. This is a simplification to allow easy
			 * cloning in PlayerCharacter (making this fail results in an issue
			 * with get having to detect null and take no action). Ideal
			 * long-term solution is probably to have AbstractItemFact implement
			 * copyContents as other Facets
			 */
			// fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testItemUnsetEmpty();
		assertEventCount(0, 0);
	}

	@Test
	public void testItemSetNullID()
	{
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		T t1 = getItem();
		try
		{
			getFacet().set(null, t1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testItemUnsetEmpty();
		assertEventCount(0, 0);
	}

	@Test
	public void testItemSetGet()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		Assert.assertEquals(t1, getFacet().get(id));
		assertEventCount(1, 0);
		// No cross-pollution
		Assert.assertNull(getFacet().get(altid));
	}

	@Test
	public void testItemSetTwiceGet()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		Assert.assertEquals(t1, getFacet().get(id));
		assertEventCount(1, 0);
		// Set same, still only set (and only one event)
		getFacet().set(id, t1);
		Assert.assertEquals(t1, getFacet().get(id));
		assertEventCount(1, 0);
	}

	@Test
	public void testItemSetMultGetRemove()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		Assert.assertEquals(t1, getFacet().get(id));
		assertEventCount(1, 0);
		T t2 = getItem();
		getFacet().set(id, t2);
		Assert.assertEquals(t2, getFacet().get(id));
		assertEventCount(2, 1);
		// Remove
		getFacet().remove(id);
		Assert.assertNull(getFacet().get(id));
		assertEventCount(2, 2);
		// But only one remove event
		getFacet().remove(id);
		Assert.assertNull(getFacet().get(id));
		assertEventCount(2, 2);
	}

	@Test
	public void testItemMatches()
	{
		T t1 = getItem();
		Assert.assertFalse(getFacet().matches(id, t1));
		getFacet().set(id, t1);
		Assert.assertTrue(getFacet().matches(id, t1));
		getFacet().remove(id);
		Assert.assertFalse(getFacet().matches(id, t1));
		Assert.assertNull(getFacet().get(id));
		Assert.assertTrue(getFacet().matches(id, null));
	}

	@Test
	public void testCopyContentsNone()
	{
		getFacet().copyContents(altid, id);
		Assert.assertNull(getFacet().get(id));
		Assert.assertTrue(getFacet().matches(id, null));
	}

	@Test
	public void testCopyContentsOne()
	{
		T t1 = getItem();
		T t2 = getItem();
		getFacet().set(id, t1);
		getFacet().copyContents(id, altid);
		Assert.assertEquals(t1, getFacet().get(altid));
		// Prove independence (remove from id)
		getFacet().set(id, t2);
		Assert.assertEquals(t1, getFacet().get(altid));
	}

	@Test
	public void testCopyContentsTwo()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		getFacet().copyContents(id, altid);
		Assert.assertEquals(t1, getFacet().get(altid));
		// Prove Independence (remove from altid)
		getFacet().remove(altid);
		Assert.assertEquals(t1, getFacet().get(id));
		Assert.assertNull(getFacet().get(altid));
		Assert.assertTrue(getFacet().matches(altid, null));
	}

	protected abstract AbstractItemFacet<CharID, T> getFacet();

	protected abstract T getItem();
	
	protected CharID getCharID()
	{
		return id;
	}
}
