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

public abstract class AbstractItemIntegrationTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractIntegrationTestCase<T>
{

    public abstract Class<TC> getTargetClass();

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        construct(primaryContext, getFirstConstant());
        construct(primaryContext, getSecondConstant());
        construct(secondaryContext, getFirstConstant());
        construct(secondaryContext, getSecondConstant());
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getFirstConstant());
        commit(modCampaign, tc, getSecondConstant());
        completeRoundRobin(tc);
    }

    protected String getSecondConstant()
    {
        return "TestWP2";
    }

    protected String getFirstConstant()
    {
        return "TestWP1";
    }

    @Test
    public void testRoundRobinOverwrite() throws PersistenceLayerException
    {
        construct(primaryContext, getFirstConstant());
        construct(primaryContext, getSecondConstant());
        construct(secondaryContext, getFirstConstant());
        construct(secondaryContext, getSecondConstant());
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getFirstConstant());
        commit(testCampaign, tc, getSecondConstant());
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddSame() throws PersistenceLayerException
    {
        construct(primaryContext, getFirstConstant());
        construct(secondaryContext, getFirstConstant());
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getFirstConstant());
        commit(modCampaign, tc, getFirstConstant());
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginal() throws PersistenceLayerException
    {
        construct(primaryContext, getSecondConstant());
        construct(secondaryContext, getSecondConstant());
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, getSecondConstant());
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoMod() throws PersistenceLayerException
    {
        construct(primaryContext, getSecondConstant());
        construct(secondaryContext, getSecondConstant());
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getSecondConstant());
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
