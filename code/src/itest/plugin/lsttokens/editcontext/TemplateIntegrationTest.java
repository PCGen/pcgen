/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.TemplateLst;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class TemplateIntegrationTest extends
        AbstractListIntegrationTestCase<CDOMObject, PCTemplate>
{

    private static TemplateLst token = new TemplateLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public Class<PCTemplate> getTargetClass()
    {
        return PCTemplate.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return true;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public boolean isPrereqLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Test
    public void testRoundRobinAddRemove() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP1.REMOVE");
        commit(modCampaign, tc, "TestWP2.REMOVE");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinMergeRemove() throws PersistenceLayerException
    {
        if (isMerge())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP1.REMOVE");
            commit(testCampaign, tc, "TestWP2.REMOVE");
            tc = new TestContext();
            tc.putText(testCampaign.getURI(), "TestWP1.REMOVE"
                    + getJoinCharacter() + "TestWP2.REMOVE");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinRemoveSame() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP1.REMOVE");
        commit(modCampaign, tc, "TestWP1.REMOVE");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginalRemove()
            throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "TestWP2.REMOVE");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoModRemove() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP2.REMOVE");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddChoose() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP3");
        construct(secondaryContext, "TestWP4");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "CHOOSE:TestWP1|TestWP2");
        commit(modCampaign, tc, "CHOOSE:TestWP3|TestWP4");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinChooseSame() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP3");
        construct(secondaryContext, "TestWP4");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "CHOOSE:TestWP3|TestWP4");
        commit(modCampaign, tc, "CHOOSE:TestWP3|TestWP4");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginalChoose()
            throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP3");
        construct(secondaryContext, "TestWP4");
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "CHOOSE:TestWP3|TestWP4");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoModChoose() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP3");
        construct(secondaryContext, "TestWP4");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "CHOOSE:TestWP3|TestWP4");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddchoice() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP3");
        construct(secondaryContext, "TestWP4");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "CHOOSE:TestWP1|TestWP2");
        commit(modCampaign, tc, "ADDCHOICE:TestWP3|TestWP4");
        completeRoundRobin(tc);
    }

}
