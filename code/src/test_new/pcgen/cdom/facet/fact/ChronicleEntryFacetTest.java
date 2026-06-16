/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet.fact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.testsupport.AbstractListFacetTest;
import pcgen.core.ChronicleEntry;

public class ChronicleEntryFacetTest extends
		AbstractListFacetTest<ChronicleEntry>
{

	private final ChronicleEntryFacet facet = new ChronicleEntryFacet();

	@Override
	protected AbstractListFacet<CharID, ChronicleEntry> getFacet()
	{
		return facet;
	}

	private int n = 1;

	@Override
	protected ChronicleEntry getObject()
	{
		ChronicleEntry ce = new ChronicleEntry();
		ce.setChronicle("Chr: " + n++);
		return ce;
	}

	@Override
	public void testAddSingleTwiceGet()
	{
		ChronicleEntry t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, now there twice (LIST not SET)
		getFacet().add(id, t1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(2, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(2, 0);
	}

	@Override
	public void testAddAllTwice()
	{
		ChronicleEntry t1 = getObject();
		List<ChronicleEntry> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t1);
		getFacet().addAll(id, pct);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<ChronicleEntry> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(2, setofone.size());
		assertTrue(setofone.contains(t1));
		listener.assertEventCount(2, 0);
	}

	@Override
	public void testAddSingleTwiceRemove()
	{
		ChronicleEntry t1 = getObject();
		getFacet().add(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(1, 0);
		// Add same, now twice in list
		getFacet().add(id, t1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(2, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(2, 0);
		// Only requires one Remove (internally a Set, not List)
		getFacet().remove(id, t1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		listener.assertEventCount(2, 1);
		// Second has no effect
		getFacet().remove(id, t1);
		testListUnsetZeroCount();
		testListUnsetEmpty();
		testListUnsetEmptySet();
		listener.assertEventCount(2, 2);
	}

}
