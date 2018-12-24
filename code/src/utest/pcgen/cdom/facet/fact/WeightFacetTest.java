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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.testsupport.AbstractItemFacetTest;

public class WeightFacetTest extends AbstractItemFacetTest<Integer>
{
	private CharID id;
	private WeightFacet facet = new WeightFacet();

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
	}

	@Test
	public void testWeightUnsetZero()
	{
		assertThat(facet.getWeight(id), is(0));
	}

	@Test
	public void testWeightSetZeroValid()
	{
		facet.setWeight(id, 0);
		assertThat(facet.getWeight(id), is(0));
	}

	@Test
	public void testWeightSetNegative()
	{
		facet.setWeight(id, -250);
		/*
		 * TODO Some form of error here?
		 */
	}

	@Test
	public void testRemoveWeight()
	{
		facet.setWeight(id, 25);
		assertThat(facet.getWeight(id), is(25));
		facet.removeWeight(id);
		assertThat(facet.getWeight(id), is(0));
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
