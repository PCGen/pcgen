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

public class GlobalAddedSkillCostFacetTest
{
    private CharID id;
    private CharID altid;

    private GlobalAddedSkillCostFacet facet = new GlobalAddedSkillCostFacet();
    private PCTemplate source1 = new PCTemplate();

    @BeforeEach
    public void setUp()
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
        source1 = new PCTemplate();
        source1.setName("T1");
    }

    @AfterEach
    public void tearDown()
    {
        id = null;
        altid = null;
        facet = null;
        source1 = null;
    }

    @Test
    public void testAddNullID()
    {
        try
        {
            getFacet().add(null, SkillCost.CLASS, getObject(), source1);
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
            getFacet().add(id, SkillCost.CLASS, null, source1);
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
            getFacet().add(id, null, getObject(), source1);
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
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, null);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddContains()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CLASS, getObject()));
    }

    @Test
    public void testEmpty()
    {
        Skill t1 = getObject();
        for (SkillCost sc : SkillCost.values())
        {
            assertFalse(getFacet().contains(id, sc, t1));
        }
    }

    @Test
    public void testAddTwoSources()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t1));

        PCClass source2 = new PCClass();
        //Second add doesn't change anything
        getFacet().add(id, SkillCost.CLASS, t1, source2);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t1));
    }

    @Test
    public void testAddMultGet()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t1));

        Skill t2 = getAltObject();
        //Second add doesn't change anything
        getFacet().add(id, SkillCost.CROSS_CLASS, t2, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CROSS_CLASS, t2));
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t2));
        //No cross pollution
        assertFalse(getFacet().contains(altid, SkillCost.CROSS_CLASS, t2));
    }

    @Test
    public void testRemoveNullID()
    {
        try
        {
            getFacet().remove(null, SkillCost.CLASS, getObject(), source1);
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
            getFacet().remove(id, SkillCost.CLASS, null, source1);
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
            getFacet().remove(id, null, getObject(), source1);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testTypeRemoveUseless()
    {
        //Just don't throw an exception
        getFacet().remove(id, SkillCost.CLASS, getAltObject(), source1);
    }

    @Test
    public void testTypeRemoveUselessSkill()
    {
        Skill t1 = getObject();
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        //Just don't throw an exception
        getFacet().remove(id, SkillCost.CLASS, getAltObject(), source1);
    }

    @Test
    public void testTypeRemoveUselessCost()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(id, SkillCost.CROSS_CLASS, t1));
        //No cross pollution
        getFacet().remove(id, SkillCost.CROSS_CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
    }

    @Test
    public void testTypeRemoveUselessSource()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));

        PCClass source2 = new PCClass();
        getFacet().remove(id, SkillCost.CLASS, t1, source2);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
    }

    @Test
    public void testTypeRemoveDiffCost()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().remove(id, SkillCost.CROSS_CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
    }

    @Test
    public void testTypeAddSingleRemove()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().remove(id, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
    }

    @Test
    public void testTypeAddSingleTwiceRemove()
    {
        Skill t1 = getObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().remove(id, SkillCost.CLASS, t1, source1);
        //Was added twice, but sources are a SET, removed once works
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
    }

    @Test
    public void testTypeAddMultRemove()
    {
        Skill t1 = getObject();
        Skill t2 = getAltObject();
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t2, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
        getFacet().remove(id, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
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
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        getFacet().add(id, SkillCost.CLASS, t2, source1);
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
        getFacet().copyContents(id, altid);

        //prove the copy
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(altid, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(altid, SkillCost.CLASS, t2));

        //prove independence (remove from id)
        getFacet().remove(id, SkillCost.CLASS, t1, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(altid, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(altid, SkillCost.CLASS, t2));

        //prove independence (remove from altid)
        getFacet().remove(altid, SkillCost.CLASS, t2, source1);
        assertFalse(getFacet().contains(id, SkillCost.CLASS, t1));
        assertTrue(getFacet().contains(id, SkillCost.CLASS, t2));
        assertTrue(getFacet().contains(altid, SkillCost.CLASS, t1));
        assertFalse(getFacet().contains(altid, SkillCost.CLASS, t2));
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
