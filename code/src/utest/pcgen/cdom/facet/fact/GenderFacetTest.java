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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


class GenderFacetTest
{
    /*
     * NOTE: This is not literal unit testing - it is leveraging the existing
     * TemplateFacet framework. This class trusts that TemplateFacetTest has
     * fully vetted TemplateFacet. PLEASE ensure all tests there are working
     * before investigating tests here.
     */
    private CharID id;
    private CharID altid;
    private GenderFacet facet;
    private TemplateFacet tfacet = new TemplateFacet();

    @BeforeEach
    public void setUp()
    {
        facet = new GenderFacet();
        facet.setTemplateFacet(tfacet);
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
    }

    @AfterEach
    public void tearDown()
    {
        id = null;
        altid = null;
        facet = null;
        tfacet = null;
    }

    @Test
    public void testGenderUnsetNull()
    {
        assertEquals(Gender.getDefaultValue(), facet.getGender(id));
    }

    @Test
    public void testWithNothingInTemplates()
    {
        tfacet.add(id, new PCTemplate(), this);
        assertEquals(Gender.getDefaultValue(), facet.getGender(id));
    }

    @Test
    public void testAvoidPollution()
    {
        PCTemplate pct = new PCTemplate();
        pct.put(ObjectKey.GENDER_LOCK, Gender.Neuter);
        tfacet.add(id, pct, this);
        assertEquals(Gender.getDefaultValue(), facet.getGender(altid));
    }

    @Test
    public void testGenderSet()
    {
        assertTrue(facet.canSetGender(id));
        facet.set(id, Gender.Female);
        assertTrue(facet.canSetGender(id));
        assertEquals(Gender.Female, facet.getGender(id));
        facet.remove(id);
        assertTrue(facet.canSetGender(id));
        assertEquals(Gender.getDefaultValue(), facet.getGender(id));
    }

    @Test
    public void testGenderLocked()
    {
        assertTrue(facet.canSetGender(id));
        PCTemplate pct = new PCTemplate();
        pct.put(ObjectKey.GENDER_LOCK, Gender.Female);
        tfacet.add(id, pct, this);
        assertFalse(facet.canSetGender(id));
        assertEquals(Gender.Female, facet.getGender(id));
        tfacet.remove(id, pct, this);
        assertTrue(facet.canSetGender(id));
        assertEquals(Gender.getDefaultValue(), facet.getGender(id));
    }

    @Test
    public void testGenderSetLockDominates()
    {
        facet.set(id, Gender.Female);
        assertEquals(Gender.Female, facet.getGender(id));
        PCTemplate pct = new PCTemplate();
        pct.put(ObjectKey.GENDER_LOCK, Gender.Neuter);
        tfacet.add(id, pct, this);
        assertEquals(Gender.Neuter, facet.getGender(id));
        tfacet.remove(id, pct, this);
        assertEquals(Gender.Female, facet.getGender(id));
    }

    @Test
    public void testMultipleGenderSetSecondDominatesGender()
    {
        PCTemplate pct = new PCTemplate();
        pct.setName("PCT");
        pct.put(ObjectKey.GENDER_LOCK, Gender.Neuter);
        tfacet.add(id, pct, this);
        assertEquals(Gender.Neuter, facet.getGender(id));
        PCTemplate pct2 = new PCTemplate();
        pct2.setName("Other");
        pct2.put(ObjectKey.GENDER_LOCK, Gender.Female);
        tfacet.add(id, pct2, this);
        assertEquals(Gender.Female, facet.getGender(id));
        tfacet.remove(id, pct, this);
        assertEquals(Gender.Female, facet.getGender(id));
        tfacet.add(id, pct, this);
        assertEquals(Gender.Neuter, facet.getGender(id));
        tfacet.remove(id, pct, this);
        assertEquals(Gender.Female, facet.getGender(id));
        tfacet.remove(id, pct2, this);
        assertEquals(Gender.getDefaultValue(), facet.getGender(id));
    }

}
