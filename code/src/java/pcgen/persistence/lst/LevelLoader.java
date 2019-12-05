/*
 * Copyright 2002 (C) James Dempsey
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
 *
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.LevelInfo;
import pcgen.core.XPTable;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * {@code LevelLoader} loads up the level system file
 * by processing each line passed to it.
 **/
public final class LevelLoader
{
    /**
     * Creates a new instance of LevelLoader
     */
    private LevelLoader()
    {
        // Empty Constructor
    }

    /**
     * Parse the line from the level.lst file, populating the
     * levelInfo object with the info found.
     *
     * @param gameMode  the game mode
     * @param inputLine The line to be parsed
     * @param lineNum   The number of the line being parsed.
     */
    public static String parseLine(GameMode gameMode, String inputLine, int lineNum, URI source, String xpTable)
    {
        if (gameMode == null)
        {
            return "";
        }

        // Deal with the start of a new XPTable definition
        if (inputLine.startsWith("XPTABLE:"))
        {
            String value = inputLine.substring(8);
            if (value.indexOf('\t') >= 0)
            {
                value = value.substring(0, value.indexOf('\t'));
            }
            value = value.trim();
            if (value.equals(""))
            {
                Logging.errorPrint("Error parsing level line \"" + inputLine + "\": empty XPTABLE value.");
            } else
            {
                gameMode.addXPTableName(value);
                return value;
            }
        }

        // Provide a default fallback table name for backwards compatibility
        if (xpTable.equals(""))
        {
            xpTable = "Default";
            gameMode.addXPTableName(xpTable);
        }

        final LevelInfo levelInfo = new LevelInfo();
        final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

        Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(LevelLstToken.class);
        while (colToken.hasMoreTokens())
        {
            final String colString = colToken.nextToken().trim();

            final int idxColon = colString.indexOf(':');
            String key = "";
            try
            {
                key = colString.substring(0, idxColon);
            } catch (StringIndexOutOfBoundsException e)
            {
                // TODO Handle Exception
            }
            LevelLstToken token = (LevelLstToken) tokenMap.get(key);

            if (token != null)
            {
                final String value = colString.substring(idxColon + 1);
                LstUtils.deprecationCheck(token, levelInfo.getLevelString(), source, value);
                if (!token.parse(levelInfo, value))
                {
                    Logging.errorPrint("LevelLoader got invalid " + key + " value of '" + value + "' in '" + inputLine
                            + "' at line " + lineNum + " of " + source + ". Token ignored.");
                }
            } else
            {
                Logging.errorPrint(
                        "LevelLoader got unexpected token of '" + colString + "' at line " + lineNum + ". Token ignored.");
            }
        }
        if (validateLevelInfo(gameMode, xpTable, levelInfo, inputLine, lineNum, source))
        {
            gameMode.addLevelInfo(xpTable, levelInfo);
        }
        return xpTable;
    }

    private static boolean validateLevelInfo(GameMode gameMode, String xpTable, LevelInfo levelInfo, String inputLine,
            int lineNum, URI source)
    {
        String level = levelInfo.getLevelString();
        if (level == null)
        {
            Logging.errorPrint("LevelLoader got empty level value in '" + inputLine + "' at line " + lineNum + " of "
                    + source + ". Line ignored.");
            return false;
        }
        XPTable existingTable = gameMode.getLevelInfo(xpTable);
        if (existingTable == null)
        {
            // No data on this table held yet, so it has to be right
            return true;
        }

        // Not a number so just check for a duplicate
        if (existingTable.getLevelInfo(level) != null)
        {
            Logging.errorPrint("LevelLoader got duplicate level value of '" + level + "' in '" + inputLine
                    + "' at line " + lineNum + " of " + source + ". Line ignored.");
            return false;
        }
        if (!isNumeric(level))
        {
            // Not a number so must be good now
            return true;
        }

        if (!existingTable.validateSequence(level))
        {
            Logging.errorPrint("LevelLoader got out of sequence level value of '" + level + "' in '" + inputLine
                    + "' at line " + lineNum + " of " + source + ". Line ignored.");

        }
        return true;
    }

    private static boolean isNumeric(String level)
    {
        try
        {
            Integer.parseInt(level);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }
}
