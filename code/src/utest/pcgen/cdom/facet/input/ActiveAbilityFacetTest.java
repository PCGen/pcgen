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
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

public class ActiveAbilityFacetTest extends TestCase
{
	protected CharID id = CharID.getID();
	protected CharID altid = CharID.getID();

	private ActiveAbilityFacet facet = new ActiveAbilityFacet();
	private AbilityCategory class1;
	private AbilityCategory class2;
	private AbilityCategory class3;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		class1 = new AbilityCategory();
		class1.setName("Cl1");
		class2 = new AbilityCategory();
		class2.setName("Cl2");
		class3 = new AbilityCategory();
		class3.setName("Cl3");
	}

	@Test
	public void testAddNullID()
	{
		try
		{
			getFacet().add(null, class1, Nature.AUTOMATIC, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	public void testAddNullCategory()
	{
		try
		{
			getFacet().add(id, null, Nature.AUTOMATIC, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullNature()
	{
		try
		{
			getFacet().add(id, class1, null, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddNullAbility()
	{
		try
		{
			getFacet().add(id, class1, Nature.AUTOMATIC, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testAddContains()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, getObject()));
	}

	@Test
	public void testEmpty()
	{
		//Also, please don't throw an exception...
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, getObject()));
	}

	@Test
	public void testAddTwoNatures()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));

		//Second add doesn't change anything
		getFacet().add(id, class1, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
	}

	@Test
	public void testAddTwoClasses()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertFalse(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class2, Nature.VIRTUAL, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));

		getFacet().add(id, class2, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class2, Nature.VIRTUAL, a1));
		assertFalse(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(altid, class2, Nature.VIRTUAL, a1));
	}

	@Test
	public void testAddMultGet()
	{
		Ability a1 = getObject();
		Ability a2 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a2));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));

		//Second add
		getFacet().add(id, class1, Nature.AUTOMATIC, a2);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a2));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		//No cross pollution
		assertFalse(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
	}

	@Test
	public void testRemoveNullID()
	{
		try
		{
			getFacet().remove(null, class1, Nature.AUTOMATIC, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullClass()
	{
		try
		{
			getFacet().remove(id, null, Nature.AUTOMATIC, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullNature()
	{
		try
		{
			getFacet().remove(id, class1, null, getObject());
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveNullAbility()
	{
		try
		{
			getFacet().remove(id, class1, Nature.AUTOMATIC, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// Yep!
		}
	}

	@Test
	public void testRemoveUseless()
	{
		//Just don't throw an exception
		getFacet().remove(id, class1, Nature.AUTOMATIC, getObject());
	}

	@Test
	public void testRemoveUselessNature()
	{
		getFacet().add(id, class1, Nature.AUTOMATIC, getObject());
		//Just don't throw an exception
		getFacet().remove(id, class1, Nature.AUTOMATIC, getObject());
	}

	@Test
	public void testRemoveUselessAbility()
	{
		Ability a1 = getObject();
		Ability a2 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		getFacet().add(id, class2, Nature.AUTOMATIC, a2);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a2));
		assertTrue(getFacet().contains(id, class2, Nature.AUTOMATIC, a2));
		//No cross pollution
		getFacet().remove(id, class1, Nature.AUTOMATIC, a2);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class2, Nature.AUTOMATIC, a2));
	}

	@Test
	public void testRemoveUselessClass()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		getFacet().add(id, class2, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		//No cross pollution
		getFacet().remove(id, class3, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class2, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class2, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
	}

	public void testRemoveDiffNature()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class1, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
	}

	@Test
	public void testAddSingleRemove()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
	}

	@Test
	public void testAddSingleTwiceRemove()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		//Was added twice, but no sources, removed once works
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
	}

	@Test
	public void testAddMultNatureRemove()
	{
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		getFacet().add(id, class1, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
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
		Ability a1 = getObject();
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		getFacet().add(id, class1, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertFalse(getFacet().contains(id, class2, Nature.VIRTUAL, a1));
		getFacet().add(id, class2, Nature.VIRTUAL, a1);
		assertTrue(getFacet().contains(id, class2, Nature.VIRTUAL, a1));
		getFacet().copyContents(id, altid);

		//prove the copy
		assertTrue(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class2, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(altid, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(id, class2, Nature.VIRTUAL, a1));

		//prove independence (remove from id)
		getFacet().remove(id, class1, Nature.AUTOMATIC, a1);
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(altid, class1, Nature.VIRTUAL, a1));

		//prove independence (remove from altid)
		getFacet().remove(altid, class1, Nature.VIRTUAL, a1);
		assertFalse(getFacet().contains(id, class1, Nature.AUTOMATIC, a1));
		assertTrue(getFacet().contains(id, class1, Nature.VIRTUAL, a1));
		assertTrue(getFacet().contains(altid, class1, Nature.AUTOMATIC, a1));
		assertFalse(getFacet().contains(altid, class1, Nature.VIRTUAL, a1));
	}

	protected ActiveAbilityFacet getFacet()
	{
		return facet;
	}

	private int n = 0;

	protected Ability getObject()
	{
		Ability t = new Ability();
		t.setName("Ability" + n++);
		return t;
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
