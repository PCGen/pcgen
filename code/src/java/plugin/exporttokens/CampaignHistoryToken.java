/*
 * CampaignHistoryToken.java
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

import java.text.NumberFormat;
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.core.ChronicleEntry;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * CampaignHistoryToken allows the character's campaign history entries to
 * be output.
 *
 * <pre>
 * CAMPAIGNHISTORY.v.x
 * CAMPAIGNHISTORY.v.x.CAMPAIGN
 * CAMPAIGNHISTORY.v.x.ADVENTURE
 * CAMPAIGNHISTORY.v.x.PARTY
 * CAMPAIGNHISTORY.v.x.DATE
 * CAMPAIGNHISTORY.v.x.XP
 * CAMPAIGNHISTORY.v.x.GM
 * CAMPAIGNHISTORY.v.x.TEXT
 * </pre>
 */
public class CampaignHistoryToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "CAMPAIGNHISTORY";

    private static enum Visibility
    {
        ALL, HIDDEN, VISIBLE
    }

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        Visibility visibility = Visibility.VISIBLE;
        String entryIndex = aTok.nextToken();
        if (!StringUtils.isNumeric(entryIndex))
        {
            if (entryIndex.equals("ALL"))
            {
                visibility = Visibility.ALL;
            } else if (entryIndex.equals("HIDDEN"))
            {
                visibility = Visibility.HIDDEN;
            } else if (!entryIndex.equals("VISIBLE"))
            {
                Logging.log(Logging.LST_ERROR, "Invalid visibility entry '" + entryIndex
                        + "'. Should be one of ALL, VISIBLE or HIDDEN. Token was " + tokenSource);
                return "";
            }

            entryIndex = aTok.nextToken();
        }

        if (!StringUtils.isNumeric(entryIndex))
        {
            Logging.log(Logging.LST_ERROR,
                    "Invalid position entry '" + entryIndex + "', it should be a number. Token was " + tokenSource);
            return "";
        }

        int index = Integer.parseInt(entryIndex);
        ChronicleEntry entry = getTargetChronicleEntry(index, visibility, pc.getDisplay());
        if (entry == null)
        {
            return "";
        }
        String token = (aTok.hasMoreTokens()) ? aTok.nextToken() : "TEXT";
        String value = getChronicleValue(entry, token.toUpperCase());
        if (value == null)
        {
            Logging.log(Logging.LST_ERROR, "Invalid property '" + token + "'. Token was " + tokenSource);
            return "";
        }
        return value;
    }

    private ChronicleEntry getTargetChronicleEntry(int targetIndex, Visibility visibility, CharacterDisplay display)
    {
        Collection<ChronicleEntry> entries = display.getChronicleEntries();
        int i = 0;
        for (ChronicleEntry chronicleEntry : entries)
        {
            if ((chronicleEntry.isOutputEntry() && visibility == Visibility.HIDDEN)
                    || (!chronicleEntry.isOutputEntry() && visibility == Visibility.VISIBLE))
            {
                continue;
            }

            if (i == targetIndex)
            {
                return chronicleEntry;
            }
            i++;
        }

        return null;
    }

    /**
     * @param entry
     * @param token
     * @return String Chronicle Value or NULL
     */
    private String getChronicleValue(ChronicleEntry entry, String token)
    {

        if (token.equals("TEXT"))
        {
            return entry.getChronicle();
        }
        if (token.equals("CAMPAIGN"))
        {
            return entry.getCampaign();
        }
        if (token.equals("ADVENTURE"))
        {
            return entry.getAdventure();
        }
        if (token.equals("PARTY"))
        {
            return entry.getParty();
        }
        if (token.equals("DATE"))
        {
            return entry.getDate();
        }
        if (token.equals("XP"))
        {
            NumberFormat fmt = NumberFormat.getNumberInstance();
            return fmt.format(entry.getXpField());
        }
        if (token.equals("GM"))
        {
            return entry.getGmField();
        }

        // Anything else is an error
        return null;
    }

}
