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
package plugin.lsttokens.editcontext.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.spell.ItemToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ItemIntegrationTest extends
        AbstractTypeSafeListIntegrationTestCase<Spell>
{

    private static ItemToken token = new ItemToken();
    private static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Spell> getCDOMClass()
    {
        return Spell.class;
    }

    @Override
    public CDOMLoader<Spell> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Spell> getToken()
    {
        return token;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Test
    public void testRoundRobinProhibitedSimple()
            throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "[Languedoc-Roussillon]");
        commit(modCampaign, tc, "[Niederösterreich]");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinProhibitedSame() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "[Finger Lakes]");
        commit(modCampaign, tc, "[Finger Lakes]");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemoveAdd() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "[Finger Lakes]");
        commit(modCampaign, tc, "Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Finger Lakes");
        commit(modCampaign, tc, "[Finger Lakes]");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinProhibitedNoSet()
            throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "[Niederösterreich]");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinProhibitedNoReset()
            throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "[Yarra Valley]");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    public Object getConstant(String string)
    {
        return string;
    }

    @Override
    public char getJoinCharacter()
    {
        return ',';
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }
}
