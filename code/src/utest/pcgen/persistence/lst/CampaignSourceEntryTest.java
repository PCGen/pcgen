/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CampaignSourceEntryTest checks processing functions of the
 * CampaignSourceEntry class.
 */
class CampaignSourceEntryTest
{

    private URI sourceUri;

    @BeforeEach
    void setUp() throws Exception
    {
        sourceUri = new URI("file://CampaignSourceEntryTest");
    }

    /**
     * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(String, URI, String)}
     */
    @Test
    public void testParseSuffix()
    {
        String value = "Full tag contents goes here";

        List<String> result =
                CampaignSourceEntry.parseSuffix("", sourceUri, value);
        assertTrue(result.isEmpty(), "Empty string should give empty list");

        String firstPrereq = "PREx:1,foo";
        result = CampaignSourceEntry.parseSuffix(firstPrereq, sourceUri, value);
        assertEquals(firstPrereq, result.get(0), "Single value should match");
        assertEquals(1, result.size(), "Incorrect number of tags");

        String secondPrereq = "PREy:1,bar";
        result =
                CampaignSourceEntry.parseSuffix(firstPrereq + "|"
                        + secondPrereq, sourceUri, value);
        assertEquals(firstPrereq, result.get(0), "First value should match");
        assertEquals(secondPrereq, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");

        String includeNoBar =
                "(INCLUDE:CATEGORY=Class Ability,Monkey See,Monkey Do)";
        result =
                CampaignSourceEntry.parseSuffix(includeNoBar, sourceUri, value);
        assertEquals(includeNoBar, result.get(0), "Single value should match");
        assertEquals(1, result.size(), "Incorrect number of tags");

        result =
                CampaignSourceEntry.parseSuffix(includeNoBar + "|"
                        + secondPrereq, sourceUri, value);
        assertEquals(includeNoBar, result.get(0), "First value should match");
        assertEquals(secondPrereq, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");

        String includeWithBar = "(INCLUDE:Happy Elf|Dower Dwarf)";
        result =
                CampaignSourceEntry.parseSuffix(includeWithBar + "|"
                        + secondPrereq, sourceUri, value);
        assertEquals(includeWithBar, result.get(0), "First value should match");
        assertEquals(secondPrereq, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");

        result =
                CampaignSourceEntry.parseSuffix(firstPrereq + "|"
                        + includeWithBar, sourceUri, value);
        assertEquals(firstPrereq, result.get(0), "First value should match");
        assertEquals(includeWithBar, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");

        result =
                CampaignSourceEntry.parseSuffix(firstPrereq + "|"
                        + includeWithBar + "|" + secondPrereq, sourceUri, value);
        assertEquals(firstPrereq, result.get(0), "First value should match");
        assertEquals(includeWithBar, result.get(1), "Second value should match");
        assertEquals(secondPrereq, result.get(2), "Third value should match");
        assertEquals(3, result.size(), "Incorrect number of tags");
    }

    /**
     * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(String, URI, String)}
     */
    @Test
    public void testParseSuffixInvalid()
    {
        String value = "Full tag contents goes here";

        List<String> result =
                CampaignSourceEntry.parseSuffix("", sourceUri, value);
        assertTrue(result.isEmpty(), "Empty string should give empty list");

        String includeNoBar =
                "(INCLUDE:CATEGORY=Class Ability,Monkey See,Monkey Do";
        result =
                CampaignSourceEntry.parseSuffix(includeNoBar, sourceUri, value);
        assertNull(result, "Bad data should give null result");

        String firstPrereq = "PREx:1,foo";
        result =
                CampaignSourceEntry.parseSuffix(includeNoBar + "|"
                        + firstPrereq, sourceUri, value);
        assertNull(result, "Bad data should give null result");

        result =
                CampaignSourceEntry.parseSuffix(firstPrereq + "|"
                        + includeNoBar, sourceUri, value);
        assertNull(result, "Bad data should give null result");
    }

    /**
     * Test method for {@link pcgen.persistence.lst.CampaignSourceEntry#parseSuffix(String, URI, String)}
     */
    @Test
    public void testParseSuffixInlineBracket()
    {
        String value = "Full tag contents goes here";

        String includeWithBracket = "(INCLUDE:Bluff(Lie))";
        List<String> result =
                CampaignSourceEntry.parseSuffix(includeWithBracket, sourceUri,
                        value);
        assertEquals(includeWithBracket,
                result.get(0), "First value should match"
        );
        assertEquals(1, result.size(), "Incorrect number of tags");

        String includeWithBar = "(INCLUDE:Happy Elf|Dower Dwarf)";
        result =
                CampaignSourceEntry.parseSuffix(includeWithBracket + "|"
                        + includeWithBar, sourceUri, value);
        assertEquals(includeWithBracket,
                result.get(0), "First value should match"
        );
        assertEquals(includeWithBar, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");

        String includeWithBarBracket = "(INCLUDE:Bluff(Lie)|Perception)";
        result =
                CampaignSourceEntry.parseSuffix(includeWithBarBracket + "|"
                        + includeWithBar, sourceUri, value);
        assertEquals(includeWithBarBracket,
                result.get(0), "First value should match"
        );
        assertEquals(includeWithBar, result.get(1), "Second value should match");
        assertEquals(2, result.size(), "Incorrect number of tags");
    }
}
