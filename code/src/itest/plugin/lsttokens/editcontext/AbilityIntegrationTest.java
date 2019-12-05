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

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AbilityLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class AbilityIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{
    private static final CDOMPrimaryToken<CDOMObject> token = new AbilityLst();
    private static final CDOMLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Dummy");
        primaryContext.getReferenceContext().importObject(a);
        a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Dummy");
        secondaryContext.getReferenceContext().importObject(a);
    }

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
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|NORMAL|Abil1");
        commit(modCampaign, tc, "FEAT|VIRTUAL|TYPE=TestType");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|Abil1|Abil2");
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinMixed() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2|Abil1");
        commit(modCampaign, tc, "FEAT|AUTOMATIC|.CLEAR.Abil1|Abil2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "FEAT|VIRTUAL|Abil1|Abil2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|Abil1|Abil2");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSetDotClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoResetDotClear()
            throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinMixedClearDot() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR");
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinMixedDotClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR.Abil2");
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSetClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoResetClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearOrder() throws PersistenceLayerException
    {
        verifyCleanStart();
        construct(primaryContext, "Abil1");
        construct(secondaryContext, "Abil1");
        construct(primaryContext, "Abil2");
        construct(secondaryContext, "Abil2");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "FEAT|VIRTUAL|.CLEAR",
                "FEAT|VIRTUAL|Abil1|Abil2");
        commit(modCampaign, tc, "FEAT|VIRTUAL|.CLEAR");
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
