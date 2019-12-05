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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.ListSkillCostFacet;
import pcgen.cdom.facet.input.MonsterCSkillFacet;
import pcgen.cdom.facet.input.RaceInputFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.choose.SkillToken;
import plugin.lsttokens.pcclass.HdToken;
import plugin.lsttokens.pcclass.IsmonsterToken;
import plugin.lsttokens.race.MoncskillToken;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class RaceMonCSkillTest extends AbstractTokenModelTest
{

    private static MoncskillToken token = new MoncskillToken();
    private static SkillToken CHOOSE_SKILL_TOKEN = new SkillToken();
    protected RaceInputFacet raceInputFacet = FacetLibrary
            .getFacet(RaceInputFacet.class);

    private MonsterCSkillFacet mcsFacet;
    private ListSkillCostFacet lscFacet;
    private Skill sk;
    private PCClass dragon;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mcsFacet = FacetLibrary.getFacet(MonsterCSkillFacet.class);
        lscFacet = FacetLibrary.getFacet(ListSkillCostFacet.class);
        sk = context.getReferenceContext().constructCDOMObject(Skill.class, "MySkill");
        dragon = context.getReferenceContext().constructCDOMObject(PCClass.class, "Dragon");
        dragon.addToListFor(ListKey.TYPE, Type.MONSTER);
        new HdToken().parseToken(context, dragon, "8");
        new IsmonsterToken().parseToken(context, dragon, "YES");
        TokenRegistration.register(CHOOSE_SKILL_TOKEN);
        ChooserFactory.setDelegate(new MockUIDelegate());
    }

    @Test
    public void testDirect()
    {
        Race source = create(Race.class, "Source");
        ParseResult result = token.parseToken(context, source, "MySkill");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        raceFacet.directSet(id, source, getAssoc());
        ClassSkillList dragonCSL =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(
                        ClassSkillList.class, "Dragon");
        assertTrue(lscFacet.contains(id, dragonCSL, SkillCost.CLASS, sk));
        assertEquals(SkillCost.CROSS_CLASS, pc.getSkillCostForClass(sk, dragon));
        pc.incrementClassLevel(1, dragon);
        assertEquals(SkillCost.CLASS, pc.getSkillCostForClass(sk, dragon));
        raceFacet.remove(id);
        assertFalse(lscFacet.contains(id, dragonCSL, SkillCost.CLASS, sk));
    }

    @Test
    public void testList()
    {
        Race source = create(Race.class, "Source");
        ParseResult result = token.parseToken(context, source, "LIST");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        result = CHOOSE_SKILL_TOKEN.parseToken(context, source, "MySkill");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(0, mcsFacet.getCount(id));
        assertEquals(SkillCost.CROSS_CLASS, pc.getSkillCostForClass(sk, dragon));
        raceInputFacet.set(id, source);
        assertTrue(mcsFacet.contains(id, sk));
        assertEquals(1, mcsFacet.getCount(id));
        pc.incrementClassLevel(1, dragon);
        pc.setDirty(true);
        assertEquals(SkillCost.CLASS, pc.getSkillCostForClass(sk, dragon));
        raceInputFacet.remove(id);
        assertEquals(0, mcsFacet.getCount(id));
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }
}
