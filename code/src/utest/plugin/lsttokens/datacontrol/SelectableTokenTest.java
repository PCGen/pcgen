/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.datacontrol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public class SelectableTokenTest
{

    private static SelectableToken token = new SelectableToken();
    private static CampaignSourceEntry testCampaign;
    private LoadContext context;
    private ContentDefinition cd;


    @BeforeAll
    public static void classSetUp()
    {
        testCampaign =
                new CampaignSourceEntry(new Campaign(), TestURI.getURI());
    }

    @BeforeEach
    public void setUp()
    {
        TokenRegistration.clearTokens();
        TokenRegistration.register(token);
        resetContext();
    }

    @AfterEach
    public void tearDown()
    {
        TokenRegistration.clearTokens();
        context = null;
        cd = null;
    }

    @AfterAll
    public static void classTearDown()
    {
        token = null;
        testCampaign = null;
    }

    protected void resetContext()
    {
        URI testURI = testCampaign.getURI();
        context =
                new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                        new ConsolidatedListCommitStrategy());
        context.setSourceURI(testURI);
        context.setExtractURI(testURI);
        cd = new FactDefinition();
    }

    @Test
    public void testInvalidInputNullString()
    {
        assertFalse(token.parseToken(context, cd, null).passed());
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(token.parseToken(context, cd, "").passed());
    }

    @Test
    public void testValidStringYes()
    {
        assertNull(cd.getSelectable());
        assertTrue(token.parseToken(context, cd, "YES").passed());
        assertNotNull(cd.getSelectable());
        assertTrue(cd.getSelectable());
        String[] unparsed = token.unparse(context, cd);
        assertArrayEquals(new String[]{"YES"}, unparsed);
    }

    @Test
    public void testValidStringNo()
    {
        assertNull(cd.getSelectable());
        assertTrue(token.parseToken(context, cd, "NO").passed());
        assertNotNull(cd.getSelectable());
        assertFalse(cd.getSelectable());
        String[] unparsed = token.unparse(context, cd);
        assertArrayEquals(new String[]{"NO"}, unparsed);
    }

}
