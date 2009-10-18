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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Race;

public class RaceFacetTest extends TestCase
{
	private CharID id;
	private CharID altid;
	private RaceFacet facet = new RaceFacet();

	private Listener listener = new Listener();

	private class Listener implements DataFacetChangeListener<Race>
	{

		public int addEventCount;
		public int removeEventCount;

		public void dataAdded(DataFacetChangeEvent<Race> dfce)
		{
			addEventCount++;
		}

		public void dataRemoved(DataFacetChangeEvent<Race> dfce)
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
	public void testRaceUnsetEmpty()
	{
		assertNull(facet.get(id));
	}

	@Test
	public void testRaceSetNull()
	{
		try
		{
			facet.set(id, null);
			/*
			 * For now, this won't fail. This is a simplification to allow easy
			 * cloning in PlayerCharacter (making this fail results in an issue
			 * with get having to detect null and take no action). Ideal
			 * long-term solution is probably to have AbstractItemFact implement
			 * copyContents as other Facets
			 */
			//fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testRaceUnsetEmpty();
		assertEventCount(0, 0);
	}

	@Test
	public void testRaceSetGet()
	{
		Race t1 = new Race();
		facet.set(id, t1);
		assertEquals(t1, facet.get(id));
		assertEventCount(1, 0);
		// No cross-pollution
		assertNull(facet.get(altid));
	}

	@Test
	public void testRaceSetTwiceGet()
	{
		Race t1 = new Race();
		facet.set(id, t1);
		assertEquals(t1, facet.get(id));
		assertEventCount(1, 0);
		// Set same, still only set (and only one event)
		facet.set(id, t1);
		assertEquals(t1, facet.get(id));
		assertEventCount(1, 0);
	}

	@Test
	public void testRaceSetMultGetRemove()
	{
		Race t1 = new Race();
		facet.set(id, t1);
		assertEquals(t1, facet.get(id));
		assertEventCount(1, 0);
		Race t2 = new Race();
		facet.set(id, t2);
		assertEquals(t2, facet.get(id));
		assertEventCount(2, 1);
		// Remove
		facet.remove(id);
		assertNull(facet.get(id));
		assertEventCount(2, 2);
		// But only one remove event
		facet.remove(id);
		assertNull(facet.get(id));
		assertEventCount(2, 2);
	}

	@Test
	public void testRaceMatches()
	{
		Race t1 = new Race();
		assertFalse(facet.matches(id, t1));
		facet.set(id, t1);
		assertTrue(facet.matches(id, t1));
		facet.remove(id);
		assertFalse(facet.matches(id, t1));
	}
}
