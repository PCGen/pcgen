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
package pcgen.cdom.facet.fact;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;

public class HeightFacetTest extends AbstractItemFacetTest<Integer>
{
	private CharID id;
	private final HeightFacet facet = new HeightFacet();

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
	}

	@Test
	public void testHeightUnsetZero()
	{
		assertEquals(0, (int)facet.get(id));
	}

	@Test
	public void testHeightSetZeroValid()
	{
		facet.set(id, 0);
		assertEquals(0, (int)facet.get(id));
	}

	@Test
	public void testHeightSetNegative()
	{
		facet.set(id, -250);
		/*
		 * TODO Some form of error here?
		 */
	}

	@Test
	public void testRemoveHeight()
	{
		facet.set(id, 25);
		assertEquals(25, (int)facet.get(id));
		facet.remove(id);
		assertEquals(0, (int)facet.get(id));
	}

	@Override
	protected AbstractItemFacet<CharID, Integer> getFacet()
	{
		return facet;
	}

	private int n = 112324;

	@Override
	protected Integer getItem()
	{
		return n++;
	}
}
