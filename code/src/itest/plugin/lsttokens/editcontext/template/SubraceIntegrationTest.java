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

import pcgen.cdom.enumeration.SubRace;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.SubraceToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class SubraceIntegrationTest extends
        AbstractTypeSafeIntegrationTestCase<PCTemplate>
{

    private static SubraceToken token = new SubraceToken();
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
    public Object getConstant(String string)
    {
        return SubRace.getConstant(string);
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Test
    public void testRoundRobinSpecialCaseOne() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "YES");
        commit(modCampaign, tc, "Yarra Valley");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialCaseTwo() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Yarra Valley");
        commit(modCampaign, tc, "YES");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "YES");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "YES");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

}
