/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.SkillRankFacet.SkillRankChangeEvent;
import pcgen.cdom.facet.SkillRankFacet.SkillRankChangeListener;
import pcgen.core.PCClass;
import pcgen.core.Skill;

public class SkillRankFacetTest
{
	private CharID id;
	private CharID altid;
	private SkillRankFacet facet = new SkillRankFacet();
	private Skill s1, s2, s3;
	private PCClass cl1, cl2;

	private SRListener listener = new SRListener();

	private static class SRListener implements SkillRankChangeListener
	{

		public int rankEventCount;
		public SkillRankChangeEvent lastRankEvent;

		@Override
		public void rankChanged(SkillRankChangeEvent lce)
		{
			rankEventCount++;
			lastRankEvent = lce;
		}

	}

	@Before
	public void setUp() throws Exception
	{
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		facet.addSkillRankChangeListener(listener);
		s1 = new Skill();
		s1.setName("S1");
		s2 = new Skill();
		s2.setName("S2");
		s3 = new Skill();
		s3.setName("S3");
		cl1 = new PCClass();
		cl1.setName("Cl1");
		cl2 = new PCClass();
		cl2.setName("Cl2");
	}

	private void assertEventCount(int l)
	{
		Assert.assertEquals(l, listener.rankEventCount);
	}

	@Test
	public void testRankUnsetZero()
	{
		Assert.assertEquals(0.0f, facet.getRank(id, s1));
	}

	@Test
	public void testUnsetEmpty()
	{
		Assert.assertNull(facet.get(id, s1, cl1));
	}

	@Test
	public void testAddCharIDNull()
	{
		try
		{
			facet.set(null, s1, cl1, 4.0);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
	}

	@Test
	public void testAddSkillNull()
	{
		try
		{
			facet.set(id, null, cl1, 4.0);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
	}

	@Test
	public void testAddClassNull()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, null, 4.0);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.get(id, s1, null).floatValue());
		Assert.assertEquals(4.0f, facet.getRank(id, s1));
	}

	@Test
	public void testAddSingleGet()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, cl1, 4.0);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.get(id, s1, cl1).floatValue());
		Assert.assertEquals(4.0f, facet.getRank(id, s1));
	}

	@Test
	public void testAddTwiceGet()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, cl1, 4.0);
		facet.set(id, s1, cl2, 2.0);
		assertEventCount(2);
		Assert.assertEquals(4.0f, facet.get(id, s1, cl1).floatValue());
		Assert.assertEquals(2.0f, facet.get(id, s1, cl2).floatValue());
		Assert.assertEquals(6.0f, facet.getRank(id, s1));
	}

	@Test
	public void testAddRankEvent()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, cl1, 4.0);
		assertEventCount(1);
		SkillRankChangeEvent event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(0.0f, event.getOldRank());
		Assert.assertEquals(4.0f, event.getNewRank());
		//More Ranks
		facet.set(id, s1, cl2, 2.0);
		event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(4.0f, event.getOldRank());
		Assert.assertEquals(6.0f, event.getNewRank());
	}

	@Test
	public void testRemoveRankEvent()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, cl1, 4);
		assertEventCount(1);
		SkillRankChangeEvent event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(0.0f, event.getOldRank());
		Assert.assertEquals(4.0f, event.getNewRank());
		//Remove
		facet.remove(id, s1, cl1);
		event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(4.0f, event.getOldRank());
		Assert.assertEquals(0.0f, event.getNewRank());
		Assert.assertEquals(0.0f, facet.getRank(id, s1));
	}

	@Test
	public void testRemoveNonEvent()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.remove(id, s1, cl1);
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
	}

	@Test
	public void testRemoveUseless()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, cl1, 4);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.getRank(id, s1));
		facet.remove(id, s1, cl2);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.getRank(id, s1));
	}

	@Test
	public void testRemoveUselessTwo()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s2, cl1, 4);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.getRank(id, s2));
		facet.remove(id, s1, cl2);
		assertEventCount(1);
		Assert.assertEquals(4.0f, facet.getRank(id, s2));
	}

	@Test
	public void testRemoveCharIDNull()
	{
		try
		{
			facet.remove(null, s1, cl1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
	}

	@Test
	public void testRemoveSkillNull()
	{
		try
		{
			facet.remove(id, null, cl1);
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
	}

	@Test
	public void testRemoveClassNull()
	{
		testRankUnsetZero();
		testUnsetEmpty();
		assertEventCount(0);
		facet.set(id, s1, null, 4);
		assertEventCount(1);
		SkillRankChangeEvent event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(0.0f, event.getOldRank());
		Assert.assertEquals(4.0f, event.getNewRank());
		//Remove
		facet.remove(id, s1, null);
		event = listener.lastRankEvent;
		Assert.assertEquals(s1, event.getSkill());
		Assert.assertEquals(4.0f, event.getOldRank());
		Assert.assertEquals(0.0f, event.getNewRank());
		Assert.assertEquals(0.0f, facet.getRank(id, s1));
	}

	/*
	 * TODO What about negative NamedValue??
	 */

	@Test
	public void testGetSetIndependence()
	{
		//TODO Not possible with NamedValue being mutable :(
	}

	@Test
	public void testCopyContents()
	{
		facet.set(id, s1, cl1, 4);
		facet.set(id, s2, cl2, 2);
		Assert.assertEquals(0.0f, facet.getRank(altid, s1));
		Assert.assertEquals(0.0f, facet.getRank(altid, s2));
		facet.copyContents(id, altid);
		Assert.assertEquals(4.0f, facet.getRank(altid, s1));
		Assert.assertEquals(2.0f, facet.getRank(altid, s2));
	}

	@Test
	public void testEmptyCopyContents()
	{
		facet.copyContents(id, altid);
		Assert.assertEquals(0.0f, facet.getRank(altid, s1));
	}
}
