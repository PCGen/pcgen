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

public abstract class AbstractTypeSafeListIntegrationTestCase<T extends CDOMObject>
        extends AbstractIntegrationTestCase<T>
{

    protected abstract boolean requiresPreconstruction();

    public abstract Object getConstant(String string);

    public abstract char getJoinCharacter();

    public abstract boolean isClearLegal();

    public abstract boolean isClearDotLegal();

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Languedoc-Roussillon");
            getConstant("Finger Lakes");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Languedoc-Roussillon");
        commit(modCampaign, tc, "Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAppend() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Languedoc-Roussillon");
            getConstant("Finger Lakes");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Languedoc-Roussillon");
        commit(testCampaign, tc, "Finger Lakes");
        tc = new TestContext();
        tc.putText(testCampaign.getURI(), "Languedoc-Roussillon"
                + getJoinCharacter() + "Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("Languedoc-Roussillon");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "Languedoc-Roussillon");
            commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
            tc = new TestContext();
            tc.putText(testCampaign.getURI(), (String[]) null);
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinAddSame() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Finger Lakes");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Finger Lakes");
        commit(modCampaign, tc, "Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginal() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("TestWP2");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoMod() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("TestWP2");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP2");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinDotClearJoinedOne()
            throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP1");
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, "TestWP1");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearJoinedTwo()
            throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP1");
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP1");
            commit(modCampaign, tc, Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearSecond() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP2");
            commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearFirst() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
            commit(modCampaign, tc, "TestWP2");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDoubleDotClear() throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
            commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinUselessDotClear()
            throws PersistenceLayerException
    {
        if (isClearLegal())
        {
            verifyCleanStart();
            TestContext tc = new TestContext();
            emptyCommit(testCampaign, tc);
            commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDot() throws PersistenceLayerException
    {
        if (isClearDotLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP1");
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP1" + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, ".CLEAR.TestWP1");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinUselessDotClearDot()
            throws PersistenceLayerException
    {
        if (isClearDotLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP1");
                getConstant("TestWP2");
                getConstant("TestWP3");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP1" + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, ".CLEAR.TestWP3");
            completeRoundRobin(tc);
        }
    }

    @Test
    public void testRoundRobinDotClearDotAll() throws PersistenceLayerException
    {
        if (isClearDotLegal())
        {
            if (requiresPreconstruction())
            {
                getConstant("TestWP1");
                getConstant("TestWP2");
            }
            verifyCleanStart();
            TestContext tc = new TestContext();
            commit(testCampaign, tc, "TestWP1" + getJoinCharacter() + "TestWP2");
            commit(modCampaign, tc, ".CLEAR.TestWP1" + getJoinCharacter()
                    + ".CLEAR.TestWP2");
            completeRoundRobin(tc);
        }
    }

}
