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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractItemFacetTest<T>
{
	private CharID id;
	private CharID altid;

	private Listener listener = new Listener();

	public class Listener implements DataFacetChangeListener<CharID, T>
	{

		private int addEventCount;
		private int removeEventCount;

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

	@Before
	public void setUp() throws Exception
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		getFacet().addDataFacetChangeListener(listener);
	}

	private void assertEventCount(int a, int r)
	{
		assertThat(listener.addEventCount, is(a));
		assertThat(listener.removeEventCount, is(r));
	}

	@Test
	public void testItemUnsetEmpty()
	{
		assertThat(getFacet().get(id), nullValue());
		assertThat(getFacet().matches(id, null), is(true));
	}

	@Test
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
		assertThat(newL.addEventCount, is(2));
		assertThat(newL.removeEventCount, is(1));
		Listener thirdL = new Listener();
		getFacet().addDataFacetChangeListener(thirdL);
		Listener fourthL = new Listener();
		getFacet().addDataFacetChangeListener(fourthL);
		// Note assert event count works because no new events added
		testItemSetGet();
		assertThat(newL.addEventCount, is(3));
		assertThat(newL.removeEventCount, is(2));
		assertThat(thirdL.addEventCount, is(1));
		assertThat(thirdL.removeEventCount, is(1));
		assertThat(fourthL.addEventCount, is(1));
		assertThat(fourthL.removeEventCount, is(1));
		// Remove middle
		getFacet().removeDataFacetChangeListener(thirdL);
		// Note assert event count works because no new events added
		testItemSetGet();
		assertThat(newL.addEventCount, is(4));
		assertThat(newL.removeEventCount, is(3));
		assertThat(thirdL.addEventCount, is(1));
		assertThat(thirdL.removeEventCount, is(1));
		assertThat(fourthL.addEventCount, is(2));
		assertThat(fourthL.removeEventCount, is(2));
		// Remove added last
		getFacet().removeDataFacetChangeListener(fourthL);
		// Note assert event count works because no new events added
		testItemSetGet();
		assertThat(newL.addEventCount, is(5));
		assertThat(newL.removeEventCount, is(4));
		assertThat(thirdL.addEventCount, is(1));
		assertThat(thirdL.removeEventCount, is(1));
		assertThat(fourthL.addEventCount, is(2));
		assertThat(fourthL.removeEventCount, is(2));
		// Remove only
		getFacet().removeDataFacetChangeListener(newL);
		// Note assert event count works because no new events added
		testItemSetGet();
		assertThat(newL.addEventCount, is(5));
		assertThat(newL.removeEventCount, is(4));
		assertThat(thirdL.addEventCount, is(1));
		assertThat(thirdL.removeEventCount, is(1));
		assertThat(fourthL.addEventCount, is(2));
		assertThat(fourthL.removeEventCount, is(2));
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
		catch (IllegalArgumentException ignored)
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
		catch (NullPointerException e)
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
		assertThat(getFacet().get(id), is(t1));
		assertEventCount(1, 0);
		// No cross-pollution
		assertThat(getFacet().get(altid), nullValue());
	}

	@Test
	public void testItemSetTwiceGet()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		assertThat(getFacet().get(id), is(t1));
		assertEventCount(1, 0);
		// Set same, still only set (and only one event)
		getFacet().set(id, t1);
		assertThat(getFacet().get(id), is(t1));
		assertEventCount(1, 0);
	}

	@Test
	public void testItemSetMultGetRemove()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		assertThat(getFacet().get(id), is(t1));
		assertEventCount(1, 0);
		T t2 = getItem();
		getFacet().set(id, t2);
		assertThat(getFacet().get(id), is(t2));
		assertEventCount(2, 1);
		// Remove
		getFacet().remove(id);
		assertThat(getFacet().get(id), nullValue());
		assertEventCount(2, 2);
		// But only one remove event
		getFacet().remove(id);
		assertThat(getFacet().get(id), nullValue());
		assertEventCount(2, 2);
	}

	@Test
	public void testItemMatches()
	{
		T t1 = getItem();
		assertThat(getFacet().matches(id, t1), is(false));
		getFacet().set(id, t1);
		assertThat(getFacet().matches(id, t1), is(true));
		getFacet().remove(id);
		assertThat(getFacet().matches(id, t1), is(false));
		assertThat(getFacet().get(id), nullValue());
		assertThat(getFacet().matches(id, null), is(true));
	}

	@Test
	public void testCopyContentsNone()
	{
		getFacet().copyContents(altid, id);
		assertThat(getFacet().get(id), nullValue());
		assertThat(getFacet().matches(id, null), is(true));
	}

	@Test
	public void testCopyContentsOne()
	{
		T t1 = getItem();
		T t2 = getItem();
		getFacet().set(id, t1);
		getFacet().copyContents(id, altid);
		assertThat(getFacet().get(altid), is(t1));
		// Prove independence (remove from id)
		getFacet().set(id, t2);
		assertThat(getFacet().get(altid), is(t1));
	}

	@Test
	public void testCopyContentsTwo()
	{
		T t1 = getItem();
		getFacet().set(id, t1);
		getFacet().copyContents(id, altid);
		assertThat(getFacet().get(altid), is(t1));
		// Prove Independence (remove from altid)
		getFacet().remove(altid);
		assertThat(getFacet().get(id), is(t1));
		assertThat(getFacet().get(altid), nullValue());
		assertThat(getFacet().matches(altid, null), is(true));
	}

	protected abstract AbstractItemFacet<CharID, T> getFacet();

	protected abstract T getItem();
	
	protected CharID getCharID()
	{
		return id;
	}
}
