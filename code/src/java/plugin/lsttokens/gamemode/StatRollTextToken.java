/*
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
package plugin.lsttokens.gamemode;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * {@code StatRollTextToken}
 */
public class StatRollTextToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "STATROLLTEXT";
    }

    //
    // STATROLLTEXT:<stat_val>,<display_text>
    //
    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        final StringTokenizer tok = new StringTokenizer(value, "\t");
        if (tok.countTokens() == 2)
        {
            try
            {
                final int statValue = Integer.parseInt(tok.nextToken());
                gameMode.addStatDisplayText(statValue, tok.nextToken());
                return true;
            } catch (NumberFormatException exc)
            {
                // returns false
            }
        }
        return false;
    }
}
