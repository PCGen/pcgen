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
package plugin.lsttokens.editcontext.race;

import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.race.MonsterclassToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class MonsterClassIntegrationTest extends
        AbstractIntegrationTestCase<Race>
{

    private static MonsterclassToken token = new MonsterclassToken();
    private static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<Race> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Race> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Wizard:4");
        commit(modCampaign, tc, "Sorcerer:3");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Sorcerer:3");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Sorcerer");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Sorcerer:2");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
