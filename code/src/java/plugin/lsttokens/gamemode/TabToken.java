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

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.TabLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * {@code TabToken}
 */
public class TabToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "TAB";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        try
        {
            TabLoader tabLoader = new TabLoader();
            LoadContext modeContext = gameMode.getModeContext();
            modeContext.setSourceURI(source);
            tabLoader.parseLine(modeContext, value, source);
            return true;
        } catch (Exception e)
        {
            Logging.errorPrint(e.getMessage(), e);
            return false;
        }
    }
}
