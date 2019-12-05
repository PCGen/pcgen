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

import pcgen.base.format.StringManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.FactLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;

public class FactIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{

    private static final String PROP_1 = "Property";
    private static final StringManager STRING_MGR = new StringManager();
    private static FactLst token = new FactLst();
    private static CDOMTokenLoader<CDOMObject> loader =
            new CDOMTokenLoader<>();
    private FactDefinition cd;

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        TokenRegistration.clearTokens();
        super.setUp();
        cd = new FactDefinition();
        cd.setDisplayName(PROP_1);
        cd.setFormatManager(STRING_MGR);
        cd.setName(PROP_1);
        cd.setFactName(PROP_1);
        cd.setUsableLocation(PCTemplate.class);
        cd.setVisibility(Visibility.DEFAULT);
        primaryContext.getReferenceContext().importObject(cd);
        SourceFileLoader.processFactDefinitions(primaryContext);
        secondaryContext.getReferenceContext().importObject(cd);
        SourceFileLoader.processFactDefinitions(secondaryContext);
    }

    private String getClearString()
    {
        return Constants.LST_DOT_CLEAR;
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

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, PROP_1 + "|Languedoc-Roussillon");
        commit(modCampaign, tc, PROP_1 + "|Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinOverwrite() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, PROP_1 + "|Languedoc-Roussillon");
        commit(testCampaign, tc, PROP_1 + "|Finger Lakes");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSame() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, PROP_1 + "|Niederösterreich");
        commit(modCampaign, tc, PROP_1 + "|Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, PROP_1 + "|Niederösterreich");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, PROP_1 + "|Yarra Valley");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoOriginalClear()
            throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, PROP_1 + "|" + getClearString());
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoModClear() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, PROP_1 + "|" + getClearString());
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
