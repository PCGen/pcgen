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
import plugin.lsttokens.editcontext.testsupport.AbstractIntegerIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.pcclass.MaxlevelToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class MaxLevelIntegrationTest extends
        AbstractIntegerIntegrationTestCase<PCClass>
{

    private static MaxlevelToken token = new MaxlevelToken();
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

    @Test
    public void testRoundRobinSimpleNo() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "NOLIMIT");
        commit(modCampaign, tc, "15");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "6");
        commit(modCampaign, tc, "NOLIMIT");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoLimitSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "NOLIMIT");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoLimitReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "NOLIMIT");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    public boolean isNegativeAllowed()
    {
        return false;
    }

    @Override
    public boolean isPositiveAllowed()
    {
        return true;
    }

    @Override
    public boolean isZeroAllowed()
    {
        return false;
    }

    @Override
    public boolean doesOverwrite()
    {
        return true;
    }

    @Override
    protected boolean isClearAllowed()
    {
        return false;
    }
}
