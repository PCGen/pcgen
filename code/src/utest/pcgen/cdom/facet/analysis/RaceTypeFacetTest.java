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
package pcgen.cdom.facet.analysis;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RaceTypeFacetTest
{
    private static final RaceType LAST_RACE_TYPE = RaceType
            .getConstant("TestLastRaceType");
    private static final RaceType RACE_TYPE_TOO = RaceType
            .getConstant("TestRaceTypeToo");
    private static final RaceType TEST_RACE_TYPE = RaceType
            .getConstant("TestRaceType");
    /*
     * NOTE: This is not literal unit testing - it is leveraging the existing
     * RaceFacet and TemplateFacet frameworks. This class trusts that
     * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
     * TemplateFacet. PLEASE ensure all tests there are working before
     * investigating tests here.
     */
    private CharID id;
    private CharID altid;
    private RaceTypeFacet facet;
    private RaceFacet rfacet = new RaceFacet();
    private TemplateFacet tfacet = new TemplateFacet();
    private CompanionModFacet cfacet = new CompanionModFacet();

    @BeforeEach
    public void setUp()
    {
        facet = new RaceTypeFacet();
        facet.setRaceFacet(rfacet);
        facet.setTemplateFacet(tfacet);
        facet.setCompanionModFacet(cfacet);
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
        rfacet = null;
        tfacet = null;
        cfacet = null;
    }

    @Test
    public void testRaceTypeUnsetNull()
    {
        assertNull(facet.getRaceType(id));
    }

    @Test
    public void testWithNothingInRace()
    {
        rfacet.set(id, new Race());
        assertNull(facet.getRaceType(id));
    }

    @Test
    public void testAvoidPollution()
    {
        Race r = new Race();
        r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        rfacet.set(id, r);
        assertNull(facet.getRaceType(altid));
    }

    @Test
    public void testGetFromRace()
    {
        Race r = new Race();
        r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        rfacet.set(id, r);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        rfacet.remove(id);
        assertNull(facet.getRaceType(id));
    }

    @Test
    public void testGetFromCMod()
    {
        rfacet.set(id, new Race());
        CompanionMod c = new CompanionMod();
        c.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        cfacet.add(id, c);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        cfacet.remove(id, c);
        assertNull(facet.getRaceType(id));
    }

    @Test
    public void testGetFromTemplate()
    {
        rfacet.set(id, new Race());
        PCTemplate t = new PCTemplate();
        t.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        tfacet.add(id, t, this);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        tfacet.remove(id, t, this);
        assertNull(facet.getRaceType(id));
    }

    @Test
    public void testGetFromCModOverridesRace()
    {
        Race r = new Race();
        r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        rfacet.set(id, r);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        CompanionMod c = new CompanionMod();
        c.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
        cfacet.add(id, c);
        assertSame(RACE_TYPE_TOO, facet.getRaceType(id));
        cfacet.remove(id, c);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
    }

    @Test
    public void testGetFromTemplateOverridesRaceandCMod()
    {
        Race r = new Race();
        r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        rfacet.set(id, r);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        CompanionMod c = new CompanionMod();
        c.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
        cfacet.add(id, c);
        assertSame(RACE_TYPE_TOO, facet.getRaceType(id));
        PCTemplate t = new PCTemplate();
        t.put(ObjectKey.RACETYPE, LAST_RACE_TYPE);
        tfacet.add(id, t, this);
        assertSame(LAST_RACE_TYPE, facet.getRaceType(id));
        tfacet.remove(id, t, this);
        assertSame(RACE_TYPE_TOO, facet.getRaceType(id));
        cfacet.remove(id, c);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
    }

    @Test
    public void testGetFromTemplateSecondOverrides()
    {
        Race r = new Race();
        r.put(ObjectKey.RACETYPE, TEST_RACE_TYPE);
        rfacet.set(id, r);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
        PCTemplate t = new PCTemplate();
        t.setName("PCT");
        t.put(ObjectKey.RACETYPE, RACE_TYPE_TOO);
        tfacet.add(id, t, this);
        assertSame(RACE_TYPE_TOO, facet.getRaceType(id));
        PCTemplate t2 = new PCTemplate();
        t2.setName("Other");
        t2.put(ObjectKey.RACETYPE, LAST_RACE_TYPE);
        tfacet.add(id, t2, this);
        assertSame(LAST_RACE_TYPE, facet.getRaceType(id));
        tfacet.remove(id, t, this);
        assertSame(LAST_RACE_TYPE, facet.getRaceType(id));
        tfacet.add(id, t, this);
        assertSame(RACE_TYPE_TOO, facet.getRaceType(id));
        tfacet.remove(id, t, this);
        assertSame(LAST_RACE_TYPE, facet.getRaceType(id));
        tfacet.remove(id, t2, this);
        assertSame(TEST_RACE_TYPE, facet.getRaceType(id));
    }
}
