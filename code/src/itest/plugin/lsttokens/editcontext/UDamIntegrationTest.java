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
import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.UdamLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class UDamIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{
    private static UdamLst token = new UdamLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Ability> getCDOMClass()
    {
        return Ability.class;
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

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "1,2,3,4,5,6,7,8,9");
        commit(modCampaign, tc, "1,2,3,4*form,5*form,6,7*form,8,9");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "1,2,3,4,5,6,7,8,9");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "1,2,3,4,5,6,7,8,9");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearBase() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
        commit(modCampaign, tc, "1,2,3,4*form,5*form,6,7*form,8,9");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearMod() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "1,2,3,4,5,6,7,8,9");
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearBoth() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR);
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    protected Ability construct(LoadContext context, String name)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(name);
        context.getReferenceContext().importObject(a);
        return a;
    }
}
