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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.GlobalSkillCostFacet;
import pcgen.cdom.facet.input.GlobalAddedSkillCostFacet;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.CskillLst;
import plugin.lsttokens.choose.SkillToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalCSkillTest extends AbstractContentTokenTest
{

    private static CskillLst token = new CskillLst();
    private static SkillToken CHOOSE_SKILL_TOKEN = new SkillToken();
    private GlobalAddedSkillCostFacet globalAddedSkillCostFacet;
    private GlobalSkillCostFacet globalSkillCostFacet;
    private Skill granted;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        globalSkillCostFacet =
                FacetLibrary.getFacet(GlobalSkillCostFacet.class);
        globalAddedSkillCostFacet =
                FacetLibrary.getFacet(GlobalAddedSkillCostFacet.class);
        granted = create(Skill.class, "Granted");
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
    }

    @Test
    public void testList()
    {
        PCTemplate source = create(PCTemplate.class, "Source");
        ParseResult result = token.parseToken(context, source, "LIST");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_SKILL_TOKEN.parseToken(context, source, "Granted");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        PCClass wizard = create(PCClass.class, "Wizard");
        finishLoad();
        pc.incrementClassLevel(1, wizard);
        assertFalse(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
        assertEquals(SkillCost.CROSS_CLASS, pc.getSkillCostForClass(granted, wizard));
        templateInputFacet.directAdd(id, source, granted);
        pc.calcActiveBonuses();
        assertEquals(SkillCost.CLASS, pc.getSkillCostForClass(granted, wizard));
        assertTrue(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
        templateInputFacet.remove(id, source);
        pc.calcActiveBonuses();
        assertFalse(globalAddedSkillCostFacet.contains(id, SkillCost.CLASS, granted));
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @Override
    protected boolean containsExpected()
    {
        return globalSkillCostFacet.contains(id, SkillCost.CLASS, granted);
    }

    @Override
    protected int targetFacetCount()
    {
        return globalSkillCostFacet.contains(id, SkillCost.CLASS, granted) ? 1
                : 0;
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }
}
