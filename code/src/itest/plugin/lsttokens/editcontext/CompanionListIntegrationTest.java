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
import pcgen.core.Domain;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.CompanionListLst;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class CompanionListIntegrationTest extends
        AbstractListIntegrationTestCase<CDOMObject, Race>
{

    private static CompanionListLst token = new CompanionListLst();
    private static CDOMTokenLoader<CDOMObject> loader =
            new CDOMTokenLoader<>();

    @Override
    public String getPrefix()
    {
        return "Familiar|";
    }

    @Override
    public Class<Domain> getCDOMClass()
    {
        return Domain.class;
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

    @Override
    public Class<Race> getTargetClass()
    {
        return Race.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public boolean isPrereqLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    protected String getAllString()
    {
        return "ANY";
    }

    @Test
    public void testRoundRobinDiffAdjustment() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, getPrefix() + "RACETYPE=Align|FOLLOWERADJUSTMENT:-2");
        commit(modCampaign, tc, getPrefix() + "RACETYPE=Alien|FOLLOWERADJUSTMENT:-5");
        completeRoundRobin(tc);
    }
}
