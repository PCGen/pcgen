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
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.DomainInputFacet;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.skill.ClassesToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class SkillClassesTest extends AbstractTokenModelTest
{

    private static ClassesToken token = new ClassesToken();
    private Skill sk;
    private PCClass dragon;
    protected DomainInputFacet domainInputFacet = FacetLibrary
            .getFacet(DomainInputFacet.class);

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        sk = context.getReferenceContext().constructCDOMObject(Skill.class, "MySkill");
        dragon = context.getReferenceContext().constructCDOMObject(PCClass.class, "Dragon");
        dragon.addToListFor(ListKey.TYPE, Type.MONSTER);
        ChooserFactory.setDelegate(new MockUIDelegate());
    }

    @Test
    public void testDirect()
    {
        ParseResult result = token.parseToken(context, sk, "Dragon");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        pc.incrementClassLevel(1, dragon);
        assertEquals(SkillCost.CLASS, pc.getSkillCostForClass(sk, dragon));
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }
}
