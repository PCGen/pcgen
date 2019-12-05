/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package tokencontent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.QualifyFacet;
import pcgen.cdom.list.CompanionList;
import pcgen.core.Campaign;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.QualifyToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalQualifyTest extends AbstractContentTokenTest
{

    private static QualifyToken token = new QualifyToken();
    private QualifyFacet qualifyFacet;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        qualifyFacet = FacetLibrary.getFacet(QualifyFacet.class);
        create(Race.class, "Dwarf");
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @Override
    protected boolean containsExpected()
    {
        /*
         * TODO This indicates that FollowerOptionFacet is not really pure
         * content - it is doing filtering as well
         */
        Race dwarf =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(Race.class,
                        "Dwarf");
        return qualifyFacet.grantsQualify(id, dwarf);
    }

    @Override
    protected int targetFacetCount()
    {
        return qualifyFacet.getCount(id);
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }

    @Override
    @Test
    public void testFromAlignment()
    {
        PCAlignment source = create(PCAlignment.class, "Source");
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }

    @Override
    @Test
    public void testFromCampaign()
    {
        Campaign source = create(Campaign.class, "Source");
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }

    @Override
    @Test
    public void testFromCompanionMod()
    {
        CompanionList cat = create(CompanionList.class, "Category");
        context.getReferenceContext().importObject(cat);
        CompanionMod source = cat.newInstance();
        cat.setKeyName("Source");
        context.getReferenceContext().importObject(source);
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }

    @Override
    @Test
    public void testFromEqMod()
    {
        EquipmentModifier source = create(EquipmentModifier.class, "Source");
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }

    @Override
    @Test
    public void testFromCheck()
    {
        PCCheck source = create(PCCheck.class, "Source");
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }

    @Override
    @Test
    public void testFromStat()
    {
        PCStat source = create(PCStat.class, "Source");
        ParseResult result = token.parseToken(context, source, "RACE|Dwarf");
        assertFalse(result.passed());
    }
}
