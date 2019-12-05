/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext.skill;

import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.skill.ClassesToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClassesIntegrationTest extends AbstractIntegrationTestCase<Skill>
{

    private static ClassesToken token = new ClassesToken();
    private static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<>();

    @BeforeAll
    public static void ltClassSetUp() throws PersistenceLayerException
    {
        TokenRegistration.register(new PreClassParser());
    }

    @Override
    public Class<Skill> getCDOMClass()
    {
        return Skill.class;
    }

    @Override
    public CDOMLoader<Skill> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Skill> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Cleric");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Cleric");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Wizard");
        commit(modCampaign, tc, "Cleric|Sorcerer");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddNot() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Wizard");
        commit(modCampaign, tc, "ALL|!Sorcerer");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinOverridePre() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "ALL|!Sorcerer|!Wizard");
        commit(modCampaign, tc, "Sorcerer");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Sorcerer");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Sorcerer|Wizard");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
