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

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.fact.MoneyFacet;

public class MoneyFacetTest extends TestCase
{
	private static final BigDecimal BD225 = new BigDecimal(225);
	private static final BigDecimal BD250 = new BigDecimal(250);
	private static final BigDecimal BD0 = new BigDecimal(0);
	private static final BigDecimal BDMinus250 = new BigDecimal(-250);
	private CharID id;
	private CharID altid;
	private MoneyFacet facet = new MoneyFacet();

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		id = CharID.getID();
		altid = CharID.getID();
	}

	@Test
	public void testWeightUnsetZero()
	{
		assertEquals(BD0, facet.getGold(id));
	}

	@Test
	public void testWeightSetGet()
	{
		facet.setGold(id, BD250);
		assertEquals(BD250, facet.getGold(id));
		facet.setGold(id, BD225);
		assertEquals(BD225, facet.getGold(id));
	}

	@Test
	public void testWeightSetZeroValid()
	{
		facet.setGold(id, BD0);
		assertEquals(BD0, facet.getGold(id));
	}

	@Test
	public void testWeightSetNegative()
	{
		facet.setGold(id, BDMinus250);
		/*
		 * TODO Some form of error here?
		 */
	}

	@Test
	public void testWeightDiffPC()
	{
		facet.setGold(id, BD250);
		assertEquals(BD0, facet.getGold(altid));
	}

	@Test
	public void testRemoveWeight()
	{
		facet.setGold(id, BD225);
		assertEquals(BD225, facet.getGold(id));
		facet.adjustGold(id, 200);
		assertEquals(new BigDecimal("425.00"), facet.getGold(id));
	}

	@Test
	public void testRemoveAltWeight()
	{
		facet.setGold(id, BD225);
		assertEquals(BD225, facet.getGold(id));
		facet.adjustGold(altid, 45);
		assertEquals(BD225, facet.getGold(id));
	}

}
