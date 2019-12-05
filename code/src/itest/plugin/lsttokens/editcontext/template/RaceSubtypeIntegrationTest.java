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

import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.RacesubtypeToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class RaceSubtypeIntegrationTest extends
        AbstractTypeSafeListIntegrationTestCase<PCTemplate>
{

    private static RacesubtypeToken token = new RacesubtypeToken();
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
        return RaceSubType.getConstant(string);
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
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

    @Test
    public void testValidRemoveInputSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Rheinhessen");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testValidRemoveInputSpace() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Finger Lakes");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testValidRemoveInputHyphen() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Languedoc-Roussillon");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testValidRemoveInputList() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Niederösterreich"
                + getJoinCharacter() + ".REMOVE.Finger Lakes");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testValidInputMultRemoveList() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Niederösterreich"
                + getJoinCharacter() + ".REMOVE.Finger Lakes");
        commit(modCampaign, tc, ".REMOVE.Languedoc-Roussillon"
                + getJoinCharacter() + ".REMOVE.Rheinhessen");
        completeRoundRobin(tc);
    }

    @Test
    public void testMixRoundRobinThree() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, ".REMOVE.Finger Lakes" + getJoinCharacter()
                + "Niederösterreich");
        commit(modCampaign, tc, ".REMOVE.Niederösterreich" + getJoinCharacter()
                + "Finger Lakes");
        completeRoundRobin(tc);
    }
}
