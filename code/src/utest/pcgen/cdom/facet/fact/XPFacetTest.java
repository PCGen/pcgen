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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.LevelTableFacet;

public class XPFacetTest extends TestCase
{
	private CharID id;
	private CharID altid;
	private XPFacet facet;
	private int adjustment;
	private Map<Integer, Integer> minXP;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet = getMockFacet();
		minXP = new HashMap<>();
		minXP.put(1, 0);
		minXP.put(2, 1000);
	}

	@Test
	public void testEarnedXPUnsetZero()
	{
		assertEquals(0, facet.getEarnedXP(id));
	}

	@Test
	public void testEarnedXPSetGet()
	{
		facet.setEarnedXP(id, 250);
		assertEquals(250, facet.getEarnedXP(id));
		facet.setEarnedXP(id, 2500);
		assertEquals(2500, facet.getEarnedXP(id));
	}

	@Test
	public void testEarnedXPSetZeroValid()
	{
		facet.setEarnedXP(id, 0);
		assertEquals(0, facet.getEarnedXP(id));
	}

	@Test
	public void testEarnedXPSetNegative()
	{
		facet.setEarnedXP(id, -250);
		/*
		 * TODO Some form of error here?
		 */
	}

	@Test
	public void testEarnedXPDiffPC()
	{
		facet.setEarnedXP(id, 2500);
		assertEquals(0, facet.getEarnedXP(altid));
	}

	@Test
	public void testGetTotalXPUnsetZero()
	{
		assertEquals(0, facet.getEarnedXP(id));
		assertEquals(0, facet.getXP(id));
	}

	@Test
	public void testGetTotalXP()
	{
		facet.setEarnedXP(id, 2500);
		assertEquals(2500, facet.getXP(id));
	}

	@Test
	public void testSetXP()
	{
		facet.setXP(id, 2500);
		assertEquals(2500, facet.getEarnedXP(id));
		assertEquals(2500, facet.getXP(id));
	}

	@Test
	public void testSetXPZeroValid()
	{
		facet.setXP(id, 0);
		assertEquals(0, facet.getEarnedXP(id));
		assertEquals(0, facet.getXP(id));
	}

	@Test
	public void testSetXPNegativeInvalid()
	{
		facet.setXP(id, -100);
		/*
		 * TODO there is some form of error here?
		 */
	}

	@Test
	public void testSetXPWithLA()
	{
		adjustment = 3; // Means equivalent of level 4, since PC's start at
						// Level 1
		minXP.put(4, 2000);
		facet.setXP(id, 2500);
		assertEquals(500, facet.getEarnedXP(id));
		assertEquals(2500, facet.getXP(id));
	}

	@Test
	public void testSetXPNexativeEarnedXPWithLA()
	{
		adjustment = 2;
		minXP.put(3, 2000);
		facet.setXP(id, 1500);
		assertEquals(0, facet.getEarnedXP(id));
		assertEquals(2000, facet.getXP(id));
		/*
		 * TODO Need to detect this error!
		 */
	}

	@Test
	public void testSetXPZeroEarnedXPWithLA()
	{
		adjustment = 2;
		minXP.put(3, 2000);
		facet.setXP(id, 2000);
		assertEquals(0, facet.getEarnedXP(id));
		assertEquals(2000, facet.getXP(id));
	}

	public XPFacet getMockFacet() throws SecurityException,
			IllegalArgumentException
	{
		XPFacet f = new XPFacet();
		LevelFacet fakeFacet = new LevelFacet()
		{

			@Override
			public int getLevelAdjustment(CharID cid)
			{
				return adjustment;
			}

		};
		f.setLevelFacet(fakeFacet);

		LevelTableFacet fakeTableFacet = new LevelTableFacet()
		{

			@Override
			public int minXPForLevel(int level, CharID cid)
			{
				return minXP.get(level);
			}

		};
		f.setLevelTableFacet(fakeTableFacet);
		return f;
	}
}
