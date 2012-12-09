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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.fact.WeightFacet;

public class WeightFacetTest extends TestCase
{
	private CharID id;
	private CharID altid;
	private WeightFacet facet = new WeightFacet();

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		id = new CharID();
		altid = new CharID();
	}

	@Test
	public void testWeightUnsetZero()
	{
		assertEquals(0, facet.getWeight(id));
	}

	@Test
	public void testWeightSetGet()
	{
		facet.setWeight(id, 250);
		assertEquals(250, facet.getWeight(id));
		facet.setWeight(id, 225);
		assertEquals(225, facet.getWeight(id));
	}

	@Test
	public void testWeightSetZeroValid()
	{
		facet.setWeight(id, 0);
		assertEquals(0, facet.getWeight(id));
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
	public void testWeightDiffPC()
	{
		facet.setWeight(id, 250);
		assertEquals(0, facet.getWeight(altid));
	}

	@Test
	public void testRemoveWeight()
	{
		facet.setWeight(id, 25);
		assertEquals(25, facet.getWeight(id));
		facet.removeWeight(id);
		assertEquals(0, facet.getWeight(id));
	}

	@Test
	public void testRemoveAltWeight()
	{
		facet.setWeight(id, 25);
		assertEquals(25, facet.getWeight(id));
		facet.removeWeight(altid);
		assertEquals(25, facet.getWeight(id));
	}

}
