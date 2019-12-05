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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public abstract class AbstractTextPropertyIntegrationTestCase<T extends CDOMObject>
        extends AbstractIntegrationTestCase<T>
{

    @BeforeAll
    public static void localClassSetUp() throws PersistenceLayerException
    {
        TokenRegistration.register(new PreLevelParser());
        TokenRegistration.register(new PreClassParser());
        TokenRegistration.register(new PreLevelWriter());
        TokenRegistration.register(new PreClassWriter());
    }


    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Languedoc-Roussillon");
        commit(modCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinIdentical() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Niederösterreich");
        commit(modCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Rheinhessen|VarOne|VarTwo");
        commit(modCampaign, tc,
                "Rheinhessen|VarOne|VarTwo|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc,
                "Rheinhessen|VarOne|VarTwo|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
        commit(modCampaign, tc, "Rheinhessen");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinTrickRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc,
                "Rheinhessen(% of % or %)|VarOne|VarTwo|VarThree");
        commit(modCampaign, tc, "Rheinhessen");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Rheinhessen (% of %)|VarOne|VarTwo|PRELEVEL:MIN=5");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Rheinhessen|VarOne|VarTwo|!PRELEVEL:MIN=5");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }


    @Test
    public void testRoundRobinDotClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "Rheinhessen");
            commit(modCampaign, tc, getClearString());
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearSet() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, getClearString(), "Rheinhessen");
            completeRoundRobin(tc);
        }
    }

    protected abstract boolean isClearLegal();

    @Test
    public void testRoundRobinNoOriginalClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            emptyCommit(testCampaign, tc);
            commit(modCampaign, tc, getClearString());
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
            commit(testCampaign, tc, getClearString());
            emptyCommit(modCampaign, tc);
            completeRoundRobin(tc);
        }
    }

    protected String getClearString()
    {
        return Constants.LST_DOT_CLEAR;
    }
}
