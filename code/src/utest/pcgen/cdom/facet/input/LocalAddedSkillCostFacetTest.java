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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.persistence.TokenLibrary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalAddedSkillCostFacetTest
{
    private CharID id;
    private CharID altid;

    private LocalAddedSkillCostFacet facet = new LocalAddedSkillCostFacet();
    private PCTemplate source1 = new PCTemplate();
    private PCClass class1;
    private PCClass class2;
    private PCClass class3;

    @BeforeEach
    public void setUp()
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
        class1 = new PCClass();
        class1.setName("Cl1");
        class2 = new PCClass();
        class2.setName("Cl2");
        class3 = new PCClass();
        class3.setName("Cl3");
    }

    @AfterEach
    public void tearDown()
    {
        id = null;
        altid = null;
        facet = null;
        source1 = null;
        class1 = null;
        class2 = null;
        class3 = null;
    }

    @Test
    public void testAddNullID()
    {
        try
        {
            getFacet().add(null, class1, SkillCost.CLASS, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testAddNullClass()
    {
        try
        {
            getFacet().add(id, null, SkillCost.CLASS, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testAddNullSkill()
    {
        try
        {
            getFacet().add(id, class1, SkillCost.CLASS, null, source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testAddNullCost()
    {
        try
        {
            getFacet().add(id, class1, null, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testAddNullSource()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, null);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, getObject()));
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddContains()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, getObject()));
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
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));

        PCClass source2 = new PCClass();
        //Second add doesn't change anything
        getFacet().add(id, class1, SkillCost.CLASS, t1, source2);
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
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t2));
        //No cross pollution
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));

        getFacet().add(id, class2, SkillCost.CLASS, t2, source1);
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
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t1));

        Skill t2 = getAltObject();
        //Second add doesn't change anything
        getFacet().add(id, class1, SkillCost.CROSS_CLASS, t2, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t2));
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        //No cross pollution
        assertFalse(getFacet().contains(altid, class1, SkillCost.CROSS_CLASS, t2));
    }

    @Test
    public void testRemoveNullID()
    {
        try
        {
            getFacet().remove(null, class1, SkillCost.CLASS, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testRemoveNullClass()
    {
        try
        {
            getFacet().remove(id, null, SkillCost.CLASS, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testRemoveNullSkill()
    {
        try
        {
            getFacet().remove(id, class1, SkillCost.CLASS, null, source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testRemoveNullCost()
    {
        try
        {
            getFacet().remove(id, class1, null, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testRemoveUseless()
    {
        //Just don't throw an exception
        getFacet().remove(id, class1, SkillCost.CLASS, getAltObject(), source1);
    }

    @Test
    public void testRemoveUselessSkill()
    {
        Skill t1 = getObject();
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        //Just don't throw an exception
        getFacet().remove(id, class1, SkillCost.CLASS, getAltObject(), source1);
    }

    @Test
    public void testRemoveUselessCost()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        getFacet().add(id, class2, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        //No cross pollution
        getFacet().remove(id, class1, SkillCost.CROSS_CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t1));
    }

    @Test
    public void testRemoveUselessClass()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        getFacet().add(id, class2, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        //No cross pollution
        getFacet().remove(id, class3, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        getFacet().remove(id, class2, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testRemoveUselessSource()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));

        PCClass source2 = new PCClass();
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source2);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testRemoveSecondSource()
    {
        Skill t1 = getObject();
        PCClass source2 = new PCClass();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        getFacet().add(id, class1, SkillCost.CLASS, t1, source2);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testRemoveDiffCost()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CROSS_CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddSingleRemove()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddSingleTwiceRemove()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        //Was added twice, but sources are a SET, removed once works
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddMultCostRemove()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        getFacet().add(id, class1, SkillCost.CROSS_CLASS, t1, source1);
        /*
         * Note behavior here that it returns what is in the database, it does
         * NOT attempt to "measure" SkillCost objects
         */
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CROSS_CLASS, t1));
    }

    @Test
    public void testAddMultSourceRemove()
    {
        Skill t1 = getObject();
        Skill t2 = getAltObject();
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t2, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
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
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        getFacet().add(id, class1, SkillCost.CLASS, t2, source1);
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertFalse(getFacet().contains(id, class2, SkillCost.CLASS, t2));
        getFacet().add(id, class2, SkillCost.CLASS, t2, source1);
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
        getFacet().remove(id, class1, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t2));

        //prove independence (remove from altid)
        getFacet().remove(altid, class1, SkillCost.CLASS, t2, source1);
        assertFalse(getFacet().contains(id, class1, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, class1, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(altid, class1, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(altid, class1, SkillCost.CLASS, t2));
    }

    protected LocalAddedSkillCostFacet getFacet()
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

    public static void addBonus(Class<? extends BonusObj> clazz)
    {
        try
        {
            TokenLibrary.addBonusClass(clazz);
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

}
