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

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InitiativeFacetTest
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
    private InitiativeFacet facet;
    private Map<CharID, Double> bonusInfo;

    @BeforeEach
    public void setUp() throws Exception
    {
        DataSetID cid = DataSetID.getID();
        id = CharID.getID(cid);
        altid = CharID.getID(cid);
        facet = getMockFacet();
        facet.setFormulaResolvingFacet(new FormulaResolvingFacet());
        bonusInfo = new HashMap<>();
    }

    @AfterEach
    public void tearDown()
    {
        id = null;
        altid = null;
        facet = null;
        bonusInfo = null;
    }

    @Test
    public void testReachUnset()
    {
        assertEquals(2, facet.getInitiative(id));
    }

    @Test
    public void testGetWithBonus()
    {
        assertEquals(2, facet.getInitiative(id));
        bonusInfo.put(altid, 4.0);
        // No pollution
        assertEquals(2, facet.getInitiative(id));
        bonusInfo.put(id, 6.0);
        assertEquals(8, facet.getInitiative(id));
        bonusInfo.clear();
        assertEquals(2, facet.getInitiative(id));
    }

    public InitiativeFacet getMockFacet() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException
    {
        InitiativeFacet f = new InitiativeFacet();
        Field field = InitiativeFacet.class.getDeclaredField("bonusCheckingFacet");
        field.setAccessible(true);
        BonusCheckingFacet fakeFacet = new BonusCheckingFacet()
        {

            @Override
            public double getBonus(CharID cid, String bonusType,
                    String bonusName)
            {
                if ("COMBAT".equals(bonusType)
                        && "Initiative".equals(bonusName))
                {
                    Double d = bonusInfo.get(cid);
                    return d == null ? 0 : d;
                }
                return 0;
            }

        };
        field.set(f, fakeFacet);
        Field field2 = InitiativeFacet.class.getDeclaredField("initcomp");
        field2.setAccessible(true);
        field2.set(f, FormulaFactory.getFormulaFor("2"));
        return f;
    }
}
