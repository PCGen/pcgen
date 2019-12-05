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

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.DrLst;
import plugin.lsttokens.SabLst;
import plugin.lsttokens.SrLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.template.CrToken;
import plugin.lsttokens.template.RepeatlevelToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RepeatLevelIntegrationTest extends
        AbstractIntegrationTestCase<PCTemplate>
{

    private static RepeatlevelToken token = new RepeatlevelToken();
    private static CDOMTokenLoader<PCTemplate> loader =
            new CDOMTokenLoader<>();

    @BeforeAll
    public static void ltClassSetUp() throws PersistenceLayerException
    {
        TokenRegistration.register(new PreLevelParser());
        TokenRegistration.register(new PreLevelWriter());
        TokenRegistration.register(new CrToken());
        TokenRegistration.register(new DrLst());
        TokenRegistration.register(new SrLst());
        TokenRegistration.register(new SabLst());
    }

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

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "5|0|10:5:SAB:Sample Spec Abil");
        commit(modCampaign, tc, "1|2|20:3:CR:-1");
        completeRoundRobin(tc);
    }
}
