/*
 * GamemodekeyToken.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * {@code GamemodekeyToken} parses the GAMEMODEKEY token.
 */
public class GamemodekeyToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "GAMEMODEKEY";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        gameMode.setName(value.replace('|', ' '));
        return true;
    }
}
