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
import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.SpellsLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpellsIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{
    private static SpellsLst token = new SpellsLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    PreClassParser preclass = new PreClassParser();
    PreClassWriter preclasswriter = new PreClassWriter();
    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
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
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SpellBook|TIMES=3|Fireball");
        commit(modCampaign, tc, "SpellBook|TIMES=1|Fireball|PRERACE:1,Human");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "SpellBook|TIMES=1|Fireball,CL+5");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(
                testCampaign,
                tc,
                "SpellBook|TIMES=2|TIMEUNIT=Week|CASTERLEVEL=15|Fireball,CL+5|Lightning Bolt,25|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR_ALL,
                "SpellBook|TIMES=3|Fireball");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearBase() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
        commit(modCampaign, tc, "SpellBook|TIMES=1|Fireball,CL+5");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearMod() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SpellBook|TIMES=1|Fireball,CL+5");
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinClearBoth() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSetClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoResetClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, Constants.LST_DOT_CLEAR_ALL);
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
