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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReachFacetTest
{
    /*
     * NOTE: This is not literal unit testing - it is leveraging the existing
     * RaceFacet and TemplateFacet frameworks. This class trusts that
     * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
     * TemplateFacet. PLEASE ensure all tests there are working before
     * investigating tests here.
     */
    private CharID id;
    private CharID altid;
    private ReachFacet facet;
    private RaceFacet rfacet = new RaceFacet();
    private TemplateFacet tfacet = new TemplateFacet();
    private Map<CharID, Double> bonusInfo;

    @BeforeEach
    public void setUp() throws Exception
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
        facet = getMockFacet();
        facet.setRaceFacet(rfacet);
        facet.setTemplateFacet(tfacet);
        bonusInfo = new HashMap<>();
    }

    @AfterEach
    public void tearDown()
    {
        id = null;
        altid = null;
        facet = null;
        rfacet = null;
        tfacet = null;
        bonusInfo = null;
    }

    @Test
    public void testReachUnsetZero()
    {
        assertEquals(0, facet.getReach(id));
    }

    @Test
    public void testWithNothingInRaceDefaultsTo5()
    {
        rfacet.set(id, new Race());
        assertEquals(5, facet.getReach(id));
        rfacet.remove(id);
        assertEquals(0, facet.getReach(id));
    }

    @Test
    public void testAvoidPollution()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        assertEquals(0, facet.getReach(altid));
    }

    @Test
    public void testGetFromRace()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetFromTemplateLowerOverridesDefault()
    {
        rfacet.set(id, new Race());
        PCTemplate t = new PCTemplate();
        t.put(IntegerKey.REACH, 3);
        tfacet.add(id, t, this);
        assertEquals(3, facet.getReach(id));
        tfacet.remove(id, t, this);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetFromTemplateHigherOverridesDefault()
    {
        rfacet.set(id, new Race());
        PCTemplate t = new PCTemplate();
        t.put(IntegerKey.REACH, 7);
        tfacet.add(id, t, this);
        assertEquals(7, facet.getReach(id));
        tfacet.remove(id, t, this);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetFromTemplateLowerOverridesRace()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        PCTemplate t = new PCTemplate();
        t.put(IntegerKey.REACH, 3);
        tfacet.add(id, t, this);
        assertEquals(3, facet.getReach(id));
        tfacet.remove(id, t, this);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetFromTemplateHigherOverridesRace()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        PCTemplate t = new PCTemplate();
        t.put(IntegerKey.REACH, 8);
        tfacet.add(id, t, this);
        assertEquals(8, facet.getReach(id));
        tfacet.remove(id, t, this);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetFromTemplateSecondOverrides()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        PCTemplate t = new PCTemplate();
        t.setName("PCT");
        t.put(IntegerKey.REACH, 8);
        tfacet.add(id, t, this);
        PCTemplate t2 = new PCTemplate();
        t2.setName("Other");
        t2.put(IntegerKey.REACH, 7);
        tfacet.add(id, t2, this);
        assertEquals(7, facet.getReach(id));
        tfacet.remove(id, t2, this);
        assertEquals(8, facet.getReach(id));
        tfacet.remove(id, t, this);
        assertEquals(5, facet.getReach(id));
    }

    @Test
    public void testGetWithBonus()
    {
        Race r = new Race();
        r.put(IntegerKey.REACH, 5);
        rfacet.set(id, r);
        assertEquals(5, facet.getReach(id));
        bonusInfo.put(altid, 4.0);
        //No pollution
        assertEquals(5, facet.getReach(id));
        bonusInfo.put(id, 6.0);
        assertEquals(11, facet.getReach(id));
        PCTemplate t = new PCTemplate();
        t.setName("PCT");
        t.put(IntegerKey.REACH, 8);
        tfacet.add(id, t, this);
        assertEquals(14, facet.getReach(id));
        PCTemplate t2 = new PCTemplate();
        t2.setName("Other");
        t2.put(IntegerKey.REACH, 7);
        tfacet.add(id, t2, this);
        assertEquals(13, facet.getReach(id));
        tfacet.remove(id, t2, this);
        assertEquals(14, facet.getReach(id));
        bonusInfo.clear();
        assertEquals(8, facet.getReach(id));
    }

    public ReachFacet getMockFacet() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException
    {
        ReachFacet f = new ReachFacet();
        Field field = ReachFacet.class.getDeclaredField("bonusCheckingFacet");
        field.setAccessible(true);
        BonusCheckingFacet fakeFacet = new BonusCheckingFacet()
        {

            @Override
            public double getBonus(CharID cid, String bonusType, String bonusName)
            {
                if ("COMBAT".equals(bonusType) && "REACH".equals(bonusName))
                {
                    Double d = bonusInfo.get(cid);
                    return d == null ? 0 : d;
                }
                return 0;
            }


        };
        field.set(f, fakeFacet);
        return f;
    }
}
