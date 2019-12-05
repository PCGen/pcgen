/*
 * ResizableEquipTypeToken.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Type;
import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * {@code ResizableEquipTypeToken} parses the list of equipment
 * types designated as able to be automatically resized.
 */
public class ResizableEquipTypeToken implements GameModeLstToken
{

    @Override
    public String getTokenName()
    {
        return "RESIZABLEEQUIPTYPE";
    }

    @Override
    public boolean parse(GameMode gameMode, String value, URI source)
    {
        List<Type> typelist = new ArrayList<>();
        final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE, false);

        while (aTok.hasMoreTokens())
        {
            final String aString = aTok.nextToken();
            typelist.add(Type.getConstant(aString));
        }
        gameMode.setResizableTypeList(typelist);
        return true;
    }
}
