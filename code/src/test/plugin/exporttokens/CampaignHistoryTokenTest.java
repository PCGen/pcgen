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
package plugin.exporttokens;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.ChronicleEntry;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CampaignHistoryTokenTest validates the functions of the
 * CampaignHistoryToken class.
 */
public class CampaignHistoryTokenTest extends AbstractCharacterTestCase
{

    private ChronicleEntry visibleEntry;
    private ChronicleEntry hiddenEntry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        PlayerCharacter character = getCharacter();

        visibleEntry = TestHelper.buildChronicleEntry(true, "Kingmaker", "17Dec2012", "Vic",
                "Ruling council", "Finns folly", 150,
                "A ruin is conquered in the forest and a new town is founded.");

        hiddenEntry = TestHelper.buildChronicleEntry(false, "Campaign", "Date", "GM", "Party",
                "Adventure", 1390, "Chronicle");

        character.addChronicleEntry(visibleEntry);
        character.addChronicleEntry(hiddenEntry);
    }

    @Test
    public void testFieldChoice() throws IOException
    {
        FileAccess.setCurrentOutputFilter("xml");
        PlayerCharacter character = getCharacter();
        assertEquals("Field Campaign", visibleEntry.getCampaign(),
                evaluateToken("CAMPAIGNHISTORY.0.CAMPAIGN", character));
        assertEquals("Field ADVENTURE", visibleEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.0.ADVENture", character));
        assertEquals("Field PARTY", visibleEntry.getParty(),
                evaluateToken("CAMPAIGNHISTORY.0.PARTY", character));
        assertEquals("Field DATE", visibleEntry.getDate(),
                evaluateToken("CAMPAIGNHISTORY.0.DATE", character));
        assertEquals("Field XP", visibleEntry.getXpField(),
                Integer.parseInt(evaluateToken("CAMPAIGNHISTORY.0.XP", character)));
        assertEquals("Field GM", visibleEntry.getGmField(),
                evaluateToken("CAMPAIGNHISTORY.0.GM", character));
        assertEquals("Field Text", visibleEntry.getChronicle(),
                evaluateToken("CAMPAIGNHISTORY.0.TEXT", character));
        assertEquals("Default field", visibleEntry.getChronicle(),
                evaluateToken("CAMPAIGNHISTORY.0", character));

        assertEquals("Invalid field", "",
                evaluateToken("CAMPAIGNHISTORY.0.LALALA", character));
    }


    @Test
    public void testVisibility() throws IOException
    {
        FileAccess.setCurrentOutputFilter("xml");
        PlayerCharacter character = getCharacter();
        assertEquals("Default visibility", visibleEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.0.ADVENTURE", character));
        assertEquals("Default visibility", "",
                evaluateToken("CAMPAIGNHISTORY.1.ADVENTURE", character));

        assertEquals("Hidden visibility", hiddenEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.HIDDEN.0.ADVENTURE", character));
        assertEquals("Hidden visibility", "",
                evaluateToken("CAMPAIGNHISTORY.HIDDEN.1.ADVENTURE", character));

        assertEquals("All visibility", visibleEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.ALL.0.ADVENTURE", character));
        assertEquals("All visibility", hiddenEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.ALL.1.ADVENTURE", character));

        assertEquals("Visible visibility", visibleEntry.getAdventure(),
                evaluateToken("CAMPAIGNHISTORY.VISIBLE.0.ADVENTURE", character));
        assertEquals("Visible visibility", "",
                evaluateToken("CAMPAIGNHISTORY.VISIBLE.1.ADVENTURE", character));

        assertEquals("Invalid visibility", "",
                evaluateToken("CAMPAIGNHISTORY.LALALA.0.ADVENTURE", character));
    }


    private String evaluateToken(String token, PlayerCharacter pc)
            throws IOException
    {
        StringWriter retWriter = new StringWriter();
        BufferedWriter bufWriter = new BufferedWriter(retWriter);
        ExportHandler export = ExportHandler.createExportHandler(new File(""));
        export.replaceTokenSkipMath(pc, token, bufWriter);
        retWriter.flush();

        bufWriter.flush();

        return retWriter.toString();
    }

}
