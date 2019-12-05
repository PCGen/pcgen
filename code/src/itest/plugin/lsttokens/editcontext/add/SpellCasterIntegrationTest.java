/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTSkill or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.editcontext.add;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.add.SpellCasterToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;

public class SpellCasterIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{

    private static SpellCasterToken ft = new SpellCasterToken();
    private static AddLst token = new AddLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(ft);
    }

    @Test
    public void testRoundRobinAdd() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SPELLCASTER|Arcane");
        commit(modCampaign, tc, "SPELLCASTER|Divine");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddSame() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SPELLCASTER|Divine");
        commit(modCampaign, tc, "SPELLCASTER|Divine");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginal() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "SPELLCASTER|Divine");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoMod() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SPELLCASTER|Divine");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddAny() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SPELLCASTER|Divine");
        commit(modCampaign, tc, "SPELLCASTER|ANY");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginalAny() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "SPELLCASTER|ANY");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoModAny() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SPELLCASTER|ANY");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
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
}
