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

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegerIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.HitdieToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class HitDieIntegrationTest extends
        AbstractIntegerIntegrationTestCase<PCTemplate>
{

    private static HitdieToken token = new HitdieToken();
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
    public boolean isNegativeAllowed()
    {
        return false;
    }

    @Override
    public boolean doesOverwrite()
    {
        return true;
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

    @Test
    public void testRoundRobinSpecialCaseOne() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "4|CLASS=Fighter");
        commit(modCampaign, tc, "5|CLASS=Wizard");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialCaseTwo() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Wizard");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "6|CLASS=Wizard");
        commit(modCampaign, tc, "4|CLASS=Fighter");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "4|CLASS=Fighter");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "4|CLASS=Fighter");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    protected boolean isClearAllowed()
    {
        return false;
    }
}
