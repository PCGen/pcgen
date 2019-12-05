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
package plugin.lsttokens.editcontext.testsupport;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Loadable;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.output.publish.OutputDB;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import util.TestURI;

public abstract class AbstractIntegrationTestCase<T extends ConcretePrereqObject & Loadable>
{
    protected LoadContext primaryContext;
    protected LoadContext secondaryContext;
    protected T primaryProf;
    protected T secondaryProf;
    protected String prefix = "";

    protected static CampaignSourceEntry testCampaign;
    protected static CampaignSourceEntry modCampaign;

    public abstract CDOMLoader<T> getLoader();

    public abstract CDOMPrimaryToken<? super T> getToken();

    @BeforeAll
    public static void classSetUp() throws URISyntaxException
    {
        OutputDB.reset();
        testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
        modCampaign = new CampaignSourceEntry(new Campaign(), new URI(
                "file:/Test%20Case%20Modifier"));
    }

    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        // Yea, this causes warnings...
        TokenRegistration.register(getToken());
        primaryContext = new EditorLoadContext();
        secondaryContext = new EditorLoadContext();
        primaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
        secondaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
        primaryProf = construct(primaryContext, "TestObj");
        secondaryProf = construct(secondaryContext, "TestObj");
    }

    protected T construct(LoadContext context, String name)
    {
        return context.getReferenceContext().constructCDOMObject(getCDOMClass(),
                name);
    }

    public abstract Class<? extends T> getCDOMClass();

    protected static void addBonus(Class<? extends BonusObj> clazz)
    {
        try
        {
            TokenLibrary.addBonusClass(clazz);
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    protected void verifyCleanStart()
    {
        // Default is not to write out anything
        assertNull(getToken().unparse(primaryContext, primaryProf));
        assertNull(getToken().unparse(secondaryContext, secondaryProf));
        // Ensure the graphs are the same at the start
        assertEquals(primaryProf, secondaryProf, "The graphs are not the same at test start"
        );
        // Ensure the graphs are the same at the start
        assertTrue(primaryContext
                .getListContext().masterListsEqual(
                        secondaryContext.getListContext()), "The graphs are not the same at test start");
    }

    protected void commit(CampaignSourceEntry campaign, TestContext tc,
            String... str) throws PersistenceLayerException
    {
        String unparsedBuilt =
                Arrays.stream(str)
                        .map(s -> getToken().getTokenName() + ':' + s + '\t')
                        .collect(Collectors.joining());
        URI uri = campaign.getURI();
        primaryContext.setSourceURI(uri);
        assertTrue(getLoader().parseLine(primaryContext,
                primaryProf, unparsedBuilt, campaign.getURI()
        ), "Parsing of " + unparsedBuilt
                + " failed unexpectedly");
        tc.putText(uri, str);
        tc.putCampaign(uri, campaign);
    }

    protected void emptyCommit(CampaignSourceEntry campaign, TestContext tc)
            throws PersistenceLayerException
    {
        URI uri = campaign.getURI();
        primaryContext.setSourceURI(uri);
        getLoader().parseLine(primaryContext, primaryProf, null,
                campaign.getURI());
        tc.putText(uri, (String[]) null);
        tc.putCampaign(uri, campaign);
    }

    public void completeRoundRobin(TestContext tc)
            throws PersistenceLayerException
    {
        for (URI uri : tc.getURIs())
        {
            List<String> str = tc.getText(uri);
            primaryContext.setExtractURI(uri);
            // Get back the appropriate token:
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            if (str == null)
            {
                assertNull(unparsed, "Expecting empty unparsed");
                getLoader().parseLine(secondaryContext, secondaryProf, null,
                        uri);
                continue;
            }
            assertArrayEquals(str.toArray(), unparsed);

            // Do round Robin
            String unparsedBuilt = Arrays.stream(unparsed)
                    .map(s -> getToken().getTokenName() + ':' + s + '\t')
                    .collect(Collectors.joining());
            secondaryContext.setSourceURI(uri);
            getLoader().parseLine(secondaryContext, secondaryProf,
                    unparsedBuilt, uri);
        }

        // Ensure the objects are the same
        assertEquals(primaryProf, secondaryProf, "Re parse of unparsed string gave a different value"
        );

        // Ensure the graphs are the same
        assertTrue(
                primaryContext.getListContext().masterListsEqual(
                        secondaryContext.getListContext()),
                "Re parse of unparsed string gave a different graph"
        );

        // And that it comes back out the same again
        for (URI uri : tc.getURIs())
        {
            List<String> str = tc.getText(uri);
            secondaryContext.setExtractURI(uri);
            // Get back the appropriate token:
            String[] unparsed = getToken().unparse(secondaryContext,
                    secondaryProf);
            if (str == null)
            {
                assertNull(unparsed);
                continue;
            }
            assertArrayEquals(str.toArray(), unparsed);
        }

        assertTrue(primaryContext.getReferenceContext().validate(null), "First parse context was not valid");
        assertTrue(
                secondaryContext.getReferenceContext().validate(null),
                "Unprased/reparsed context was not valid"
        );
        int expectedPrimaryMessageCount = 0;
        assertEquals(
                expectedPrimaryMessageCount, primaryContext.getWriteMessageCount(),
                "First parse and unparse/reparse had different number of messages"
        );
        assertEquals(0, secondaryContext.getWriteMessageCount(),
                "Unexpected messages in unparse/reparse"
        );
    }
}
