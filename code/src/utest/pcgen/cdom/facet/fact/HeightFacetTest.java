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

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeightFacetTest extends AbstractItemFacetTest<Integer>
{
	private CharID id;
	private HeightFacet facet = new HeightFacet();

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
	}

	@Override
	@AfterEach
	public void tearDown()
	{
		id = null;
		facet = null;
		super.tearDown();
	}

	@Test
	public void testHeightUnsetZero()
	{
		assertEquals(0, facet.getHeight(id));
	}

	@Test
	public void testHeightSetZeroValid()
	{
		facet.setHeight(id, 0);
		assertEquals(0, facet.getHeight(id));
	}

	@Test
	public void testHeightSetNegative()
	{
		facet.setHeight(id, -250);
		/*
		 * TODO Some form of error here?
		 */
	}

	@Test
	public void testRemoveHeight()
	{
		facet.setHeight(id, 25);
		assertEquals(25, facet.getHeight(id));
		facet.removeHeight(id);
		assertEquals(0, facet.getHeight(id));
	}

	@Override
	protected AbstractItemFacet<CharID, Integer> getFacet()
	{
		return facet;
	}

	private int n = 1;

	@Override
	protected Integer getItem()
	{
		return n++;
	}
}
