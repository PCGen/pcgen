/*
 * Copyright (c) 2009-2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet.input;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

public class GlobalAddedSkillCostFacetTest extends TestCase
{
	protected CharID id = CharID.getID();
	protected CharID altid = CharID.getID();

	private GlobalAddedSkillCostFacet facet = new GlobalAddedSkillCostFacet();
	private PCTemplate source1 = new PCTemplate();

	@Test
	public void testAddNullID()
	{
		try
		{
			getFacet().add(null, getObject(), SkillCost.CLASS, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullSkill()
	{
		try
		{
			getFacet().add(id, null, SkillCost.CLASS, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullCost()
	{
		try
		{
			getFacet().add(id, getObject(), null, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullSource()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, null);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t1, SkillCost.CLASS));
	}

	@Test
	public void testAddContains()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, getObject(), SkillCost.CLASS));
	}

	@Test
	public void testEmpty()
	{
		Skill t1 = getObject();
		for (SkillCost sc : SkillCost.values())
		{
			assertFalse(getFacet().contains(id, t1, sc));
		}
	}

	@Test
	public void testAddTwoSources()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t1, SkillCost.CLASS));

		PCClass source2 = new PCClass();
		//Second add doesn't change anything
		getFacet().add(id, t1, SkillCost.CLASS, source2);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t1, SkillCost.CLASS));
	}

	@Test
	public void testAddMultGet()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t1, SkillCost.CLASS));

		Skill t2 = getAltObject();
		//Second add doesn't change anything
		getFacet().add(id, t2, SkillCost.CROSS_CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CROSS_CLASS));
		assertFalse(getFacet().contains(id, t2, SkillCost.CLASS));
		//No cross pollution
		assertFalse(getFacet().contains(altid, t2, SkillCost.CROSS_CLASS));
	}

	@Test
	public void testRemoveNullID()
	{
		try
		{
			getFacet().remove(null, getObject(), SkillCost.CLASS, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullSkill()
	{
		try
		{
			getFacet().remove(id, null, SkillCost.CLASS, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullCost()
	{
		try
		{
			getFacet().remove(id, getObject(), null, source1);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testTypeRemoveUseless()
	{
		//Just don't throw an exception
		getFacet().remove(id, getAltObject(), SkillCost.CLASS, source1);
	}

	@Test
	public void testTypeRemoveUselessSkill()
	{
		Skill t1 = getObject();
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		//Just don't throw an exception
		getFacet().remove(id, getAltObject(), SkillCost.CLASS, source1);
	}

	@Test
	public void testTypeRemoveUselessCost()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(id, t1, SkillCost.CROSS_CLASS));
		//No cross pollution
		getFacet().remove(id, t1, SkillCost.CROSS_CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
	}

	@Test
	public void testTypeRemoveUselessSource()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));

		PCClass source2 = new PCClass();
		getFacet().remove(id, t1, SkillCost.CLASS, source2);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
	}

	public void testTypeRemoveDiffCost()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().remove(id, t1, SkillCost.CROSS_CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
	}

	@Test
	public void testTypeAddSingleRemove()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().remove(id, t1, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
	}

	@Test
	public void testTypeAddSingleTwiceRemove()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().remove(id, t1, SkillCost.CLASS, source1);
		//Was added twice, but sources are a SET, removed once works
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
	}

	@Test
	public void testTypeAddMultRemove()
	{
		Skill t1 = getObject();
		Skill t2 = getAltObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t2, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t2, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
		getFacet().remove(id, t1, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
	}

	@Test
	public void testCopyContentsNone()
	{
		//Just no exceptions...
		getFacet().copyContents(altid, id);
	}

	@Test
	public void testCopyContents()
	{
		Skill t1 = getObject();
		Skill t2 = getAltObject();
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t1, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t2, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		getFacet().add(id, t2, SkillCost.CLASS, source1);
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
		getFacet().copyContents(id, altid);

		//prove the copy
		assertTrue(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
		assertTrue(getFacet().contains(altid, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(altid, t2, SkillCost.CLASS));

		//prove independence (remove from id)
		getFacet().remove(id, t1, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
		assertTrue(getFacet().contains(altid, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(altid, t2, SkillCost.CLASS));

		//prove independence (remove from altid)
		getFacet().remove(altid, t2, SkillCost.CLASS, source1);
		assertFalse(getFacet().contains(id, t1, SkillCost.CLASS));
		assertTrue(getFacet().contains(id, t2, SkillCost.CLASS));
		assertTrue(getFacet().contains(altid, t1, SkillCost.CLASS));
		assertFalse(getFacet().contains(altid, t2, SkillCost.CLASS));
	}

	protected GlobalAddedSkillCostFacet getFacet()
	{
		return facet;
	}

	private int n = 0;

	protected Skill getObject()
	{
		Skill t = new Skill();
		t.setName("Skill" + n++);
		return t;
	}

	protected Skill getAltObject()
	{
		return getObject();
	}

	public static void addBonus(String name, Class<? extends BonusObj> clazz)
	{
		try
		{
			TokenLibrary.addBonusClass(clazz, name);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

}
