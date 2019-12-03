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
package pcgen.cdom.facet.analysis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCClass;
import pcgen.core.Skill;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalSkillCostFacetTest
{
	protected CharID id;
	protected CharID altid;

	private LocalSkillCostFacet facet = new LocalSkillCostFacet();
	private PCClass class1;
	private PCClass class2;

	@BeforeEach
	public void setUp() {
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		class1 = new PCClass();
		class1.setName("Cl1");
		class2 = new PCClass();
		class2.setName("Cl2");
	}

	@AfterEach
	public void tearDown()
	{
		id = null;
		altid = null;
		facet = null;
		class1 = null;
		class2  = null;
	}

	@Test
	public void testAddNullID()
	{
		try
		{
			addCost(null, class1, getObject(), SkillCost.CLASS);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullClass()
	{
		try
		{
			addCost(id, null, getObject(), SkillCost.CLASS);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullSkill()
	{
		try
		{
			addCost(id, class1, null, SkillCost.CLASS);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullCost()
	{
		try
		{
			addCost(id, class1, getObject(), null);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddContains()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS,
				getObject()
		));
	}

	@Test
	public void testEmpty()
	{
		Skill t1 = getObject();
		for (SkillCost sc : SkillCost.values())
		{
			assertFalse(getFacet().contains(id, class1, sc, t1));
		}
	}

	@Test
	public void testAddTwoSources()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));

		//Second add doesn't change anything
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(ObjectKey.PARENT, class1);
		DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(id, pcl, new Object(),
                        DataFacetChangeEvent.DATA_ADDED);
		ListKey<CDOMReference<Skill>> lk = ListKey.LOCALCSKILL;
		pcl.addToListFor(lk, CDOMDirectSingleRef.getRef(t1));
		getFacet().dataAdded(dfce);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
	}

	@Test
	public void testAddTwoClasses()
	{
		Skill t1 = getObject();
		Skill t2 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		addCost(id, class2, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(altid, class2, SkillCost.CLASS, t2));
	}

	@Test
	public void testAddMultGet()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));

		Skill t2 = getAltObject();
		//Second add doesn't change anything
		addCost(id, class1, t2, SkillCost.CROSS_CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t2));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, SkillCost.CROSS_CLASS,
				t2
		));
	}

	@Test
	public void testRemoveNullID()
	{
		try
		{
			removeCosts(null, class1);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddBadSource()
	{
		Skill t1 = getObject();
		PCClassLevel pcl = new PCClassLevel();
		DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(id, pcl, new Object(),
                        DataFacetChangeEvent.DATA_ADDED);
		ListKey<CDOMReference<Skill>> lk = ListKey.LOCALCSKILL;
		pcl.addToListFor(lk, CDOMDirectSingleRef.getRef(t1));
		try
		{
			getFacet().dataAdded(dfce);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullSource()
	{
		try
		{
			removeCosts(id, null);
			fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveUseless()
	{
		//Just don't throw an exception
		removeCosts(id, class1);
	}

	@Test
	public void testRemoveUselessSource()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));

		PCClass source2 = new PCClass();
		removeCosts(id, source2);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
	}

	@Test
	public void testRemoveSecondSource()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		PCClassLevel pcl = new PCClassLevel();
		pcl.put(ObjectKey.PARENT, class1);
		DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(id, pcl, new Object(),
                        DataFacetChangeEvent.DATA_ADDED);
		ListKey<CDOMReference<Skill>> lk = ListKey.LOCALCSKILL;
		pcl.addToListFor(lk, CDOMDirectSingleRef.getRef(t1));
		getFacet().dataAdded(dfce);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		removeCosts(id, pcl);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
	}

	@Test
	public void testAddSingleRemove()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		removeCosts(id, class1);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
	}

	@Test
	public void testAddSingleTwiceRemove()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		removeCosts(id, class1);
		//Was added twice, but remove all from a source
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
	}

	@Test
	public void testAddMultCostRemove()
	{
		Skill t1 = getObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		addCost(id, class1, t1, SkillCost.CROSS_CLASS);
		/*
		 * Note behavior here that it returns what is in the database, it does
		 * NOT attempt to "measure" SkillCost objects
		 */
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
		removeCosts(id, class1);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
	}

	@Test
	public void testAddMultSkillRemove()
	{
		Skill t1 = getObject();
		Skill t2 = getAltObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		removeCosts(id, class1);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
	}

	@Test
	public void testCopyContentsNone()
	{
		//Just no exceptions...
		getFacet().copyContents(altid, id);
	}

	@Test
	public void testCopyContentsOne()
	{
		Skill t1 = getObject();
		Skill t2 = getAltObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		addCost(id, class2, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		getFacet().copyContents(id, altid);

		//prove the copy
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));

		//prove independence (remove from id)
		removeCosts(id, class1);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t2));
	}

	@Test
	public void testCopyContentsTwo()
	{
		Skill t1 = getObject();
		Skill t2 = getAltObject();
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t1, SkillCost.CLASS);
		assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		addCost(id, class1, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		addCost(id, class2, t2, SkillCost.CLASS);
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		getFacet().copyContents(id, altid);

		//prove the copy
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t2));
		assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t2));

		//prove independence (remove from altid)
		removeCosts(altid, class1);
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
		assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
		assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t2));
	}

	protected LocalSkillCostFacet getFacet()
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

	private void addCost(CharID cid, PCClass cl, Skill skill, SkillCost sc)
	{
		DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(cid, cl, new Object(),
                        DataFacetChangeEvent.DATA_ADDED);
		ListKey<CDOMReference<Skill>> lk;
		if (sc.equals(SkillCost.CLASS))
		{
			lk = ListKey.LOCALCSKILL;
		}
		else if (sc.equals(SkillCost.CROSS_CLASS))
		{
			lk = ListKey.LOCALCCSKILL;
		}
		else
		{
			fail("Cannot use " + sc);
			//useless except to indicate lk is never used
			return;
		}
		cl.removeListFor(ListKey.LOCALCSKILL);
		cl.removeListFor(ListKey.LOCALCCSKILL);
		cl.addToListFor(lk, CDOMDirectSingleRef.getRef(skill));
		getFacet().dataAdded(dfce);
	}

	private void removeCosts(CharID cid, CDOMObject cl)
	{
		DataFacetChangeEvent<CharID, CDOMObject> dfce =
                new DataFacetChangeEvent<>(cid, cl, new Object(),
                        DataFacetChangeEvent.DATA_REMOVED);
		getFacet().dataRemoved(dfce);
	}
}
