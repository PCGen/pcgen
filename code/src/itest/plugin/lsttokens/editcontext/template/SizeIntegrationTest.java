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
package plugin.lsttokens.editcontext.template;

import java.net.URISyntaxException;

import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.SizeToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SizeIntegrationTest extends
        AbstractIntegrationTestCase<PCTemplate>
{

    private static SizeToken token = new SizeToken();
    private static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        SizeAdjustment ps = BuildUtilities.createSize("Small", 0);
        primaryContext.getReferenceContext().importObject(ps);
        SizeAdjustment pm = BuildUtilities.createSize("Medium", 1);
        primaryContext.getReferenceContext().importObject(pm);
        SizeAdjustment ss = BuildUtilities.createSize("Small", 0);
        secondaryContext.getReferenceContext().importObject(ss);
        SizeAdjustment sm = BuildUtilities.createSize("Medium", 1);
        secondaryContext.getReferenceContext().importObject(sm);
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "M");
        commit(modCampaign, tc, "S");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "S");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "M");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialCaseOne() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Formula");
        commit(modCampaign, tc, "M");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialCaseTwo() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "S");
        commit(modCampaign, tc, "Formula");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Formula");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Formula");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

}
