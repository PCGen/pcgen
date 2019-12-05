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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import pcgen.cdom.content.fact.FactDefinition;
import pcgen.core.Campaign;
import pcgen.core.Domain;
import pcgen.core.Skill;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public class FactDefTokenTest
{

    private static FactDefToken token = new FactDefToken();
    private static CampaignSourceEntry testCampaign;

    private LoadContext context;
    private FactDefinition fd;

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
        fd = null;
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
        fd = new FactDefinition();
    }

    @Test
    public void testInvalidInputNullString()
    {
        assertFalse(token.parseToken(context, fd, null).passed());
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(token.parseToken(context, fd, "").passed());
    }

    @Test
    public void testInvalidInputNoPipe()
    {
        assertFalse(token.parseToken(context, fd, "SKILL").passed());
    }

    @Test
    public void testInvalidInputTrailingPipe()
    {
        assertFalse(token.parseToken(context, fd, "SKILL|").passed());
    }

    @Test
    public void testInvalidInputLeadingPipe()
    {
        assertFalse(token.parseToken(context, fd, "|Possibility").passed());
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(token.parseToken(context, fd, "SKILL||Possibility").passed());
    }

    @Test
    public void testInvalidInputDoublePipe2()
    {
        assertFalse(token.parseToken(context, fd, "SKILL|Possibility|Exception").passed());
    }

    @Test
    public void testValidStringString()
    {
        assertNull(fd.getFactName());
        assertNull(fd.getUsableLocation());
        assertTrue(token.parseToken(context, fd, "SKILL|Possibility").passed());
        assertNotNull(fd.getFactName());
        assertNotNull(fd.getUsableLocation());
        assertEquals("Possibility", fd.getFactName());
        Assertions.assertSame(Skill.class, fd.getUsableLocation());
        String[] unparsed = token.unparse(context, fd);
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals("SKILL|Possibility", unparsed[0]);
    }

    @Test
    public void testValidStringNo()
    {
        assertNull(fd.getFactName());
        assertNull(fd.getUsableLocation());
        assertTrue(token.parseToken(context, fd, "DOMAIN|Caster").passed());
        assertNotNull(fd.getFactName());
        assertNotNull(fd.getUsableLocation());
        assertEquals("Caster", fd.getFactName());
        Assertions.assertSame(Domain.class, fd.getUsableLocation());
        String[] unparsed = token.unparse(context, fd);
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals("DOMAIN|Caster", unparsed[0]);
    }

}
