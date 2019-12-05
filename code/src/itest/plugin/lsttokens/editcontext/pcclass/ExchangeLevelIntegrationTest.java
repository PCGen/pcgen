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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.ExchangelevelToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ExchangeLevelIntegrationTest extends
        AbstractIntegrationTestCase<PCClass>
{

    private static ExchangelevelToken token = new ExchangelevelToken();
    private static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        prefix = "CLASS:";
    }

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    public Class<PCClass> getTargetClass()
    {
        return PCClass.class;
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Bard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Bard");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Paladin|11|10|1");
        commit(modCampaign, tc, "Bard|5|10|1");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Paladin|5|10|1");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Paladin|10|10|0");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

}
