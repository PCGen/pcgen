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
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractTypeSafeIntegrationTestCase<T extends CDOMObject>
        extends AbstractIntegrationTestCase<T>
{

    protected abstract boolean requiresPreconstruction();

    public abstract Object getConstant(String string);

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
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
    public void testRoundRobinOverwrite() throws PersistenceLayerException
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
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSame() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Niederösterreich");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Niederösterreich");
        commit(modCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Niederösterreich");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Yarra Valley");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Yarra Valley");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
