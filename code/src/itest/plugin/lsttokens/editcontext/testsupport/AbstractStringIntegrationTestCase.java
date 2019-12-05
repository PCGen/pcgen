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

import org.junit.jupiter.api.Test;

public abstract class AbstractStringIntegrationTestCase<T extends CDOMObject>
        extends AbstractIntegrationTestCase<T>
{

    public abstract boolean isClearLegal();

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
    public void testRoundRobinOverwrite() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Languedoc-Roussillon");
        commit(testCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSame() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Finger Lakes");
        commit(modCampaign, tc, "Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinDotClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "Finger Lakes");
            commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
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
            commit(testCampaign, tc, Constants.LST_DOT_CLEAR, "Finger Lakes");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Yarra Valley");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
