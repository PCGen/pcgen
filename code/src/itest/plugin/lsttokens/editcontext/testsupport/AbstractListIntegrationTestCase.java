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
package plugin.lsttokens.editcontext.testsupport;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractListIntegrationTestCase<T extends CDOMObject, TC extends Loadable>
        extends AbstractIntegrationTestCase<T>
{

    public abstract Class<TC> getTargetClass();

    public abstract boolean isAllLegal();

    public abstract boolean isTypeLegal();

    public abstract boolean isPrereqLegal();

    public abstract boolean isClearLegal();

    public String getClearString()
    {
        return isClearAll() ? Constants.LST_DOT_CLEAR_ALL : Constants.LST_DOT_CLEAR;
    }

    public boolean isClearAll()
    {
        return false;
    }

    public abstract boolean isClearDotLegal();

    public abstract char getJoinCharacter();

    public boolean isMerge()
    {
        return true;
    }

    public String getPrefix()
    {
        return "";
    }

    public String getTypePrefix()
    {
        return "";
    }

    PreClassParser preclass = new PreClassParser();
    PreClassWriter preclasswriter = new PreClassWriter();
    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getPrefix() + "TestWP1");
        commit(modCampaign, tc, getPrefix() + "TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinMerge() throws PersistenceLayerException
    {
        if (isMerge())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1");
            commit(testCampaign, tc, getPrefix() + "TestWP2");
            tc = new TestContext();
            tc.putText(testCampaign.getURI(), getPrefix()
                    + "TestWP1" + getJoinCharacter() + "TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinAddSame() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getPrefix() + "TestWP1");
        commit(modCampaign, tc, getPrefix() + "TestWP1");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddType() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1");
            commit(modCampaign, tc, getPrefix() + getTypePrefix()
                    + "TYPE=TestType");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinAddAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1");
            commit(modCampaign, tc, getPrefix() + getAllString());
            completeRoundRobin(tc);
        }
    }

    protected String getAllString()
    {
        return "ALL";
    }

    @Test
    public void testRoundRobinAddPrereq() throws PersistenceLayerException
    {
        if (isPrereqLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1");
            commit(modCampaign, tc, getPrefix() + "TestWP1|PRERACE:1,Human");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinRemovePrereq() throws PersistenceLayerException
    {
        if (isPrereqLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1|PRERACE:1,Human");
            commit(modCampaign, tc, getPrefix() + "TestWP1");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinStartType() throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + getTypePrefix()
                    + "TYPE=TestAltType.TestThirdType.TestType");
            commit(modCampaign, tc, getPrefix() + "TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinStartAll() throws PersistenceLayerException
    {
        if (isAllLegal())
        {
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + getAllString());
            commit(modCampaign, tc, getPrefix() + "TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP2");
            commit(modCampaign, tc, getPrefix() + getClearString());
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDot() throws PersistenceLayerException
    {
        if (isClearDotLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1"
                    + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, getPrefix() + ".CLEAR.TestWP1");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDotType()
            throws PersistenceLayerException
    {
        if (isClearDotLegal() && isTypeLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1"
                    + getJoinCharacter() + getTypePrefix() + "TYPE=TestType");
            commit(modCampaign, tc, getPrefix() + ".CLEAR." + getTypePrefix()
                    + "TYPE=TestType");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDotAll() throws PersistenceLayerException
    {
        if (isClearDotLegal() && isAllLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + getAllString());
            commit(modCampaign, tc, getPrefix() + ".CLEAR." + getAllString());
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDotComplete()
            throws PersistenceLayerException
    {
        if (isClearDotLegal())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP2");
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + "TestWP1"
                    + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, getPrefix() + ".CLEAR.TestWP1"
                    + getJoinCharacter() + ".CLEAR.TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinNoOriginal() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, getPrefix() + "TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoMod() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getPrefix() + "TestWP2");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginalClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            emptyCommit(testCampaign, tc);
            commit(modCampaign, tc, getPrefix() + getClearString());
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinNoModClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getPrefix() + getClearString());
            emptyCommit(modCampaign, tc);
            completeRoundRobin(tc);
        }
    }
}
