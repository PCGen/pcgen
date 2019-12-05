/*
 * ProhibitedListToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

//PROHIBITEDLIST
public class ProhibitedListToken extends AbstractExportToken
{
    public static final String TOKENNAME = "PROHIBITEDLIST";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        return getProhibitedListToken(tokenSource, display);
    }

    public static String getProhibitedListToken(String tokenSource, CharacterDisplay display)
    {
        int k = tokenSource.lastIndexOf(',');

        String jointext;
        if (k >= 0)
        {
            jointext = tokenSource.substring(k + 1);
        } else
        {
            jointext = ",";
        }

        Set<String> set = new TreeSet<>();
        for (PCClass pcClass : display.getClassSet())
        {
            if (display.getLevel(pcClass) > 0)
            {
                for (SpellProhibitor sp : pcClass.getSafeListFor(ListKey.PROHIBITED_SPELLS))
                {
                    set.addAll(sp.getValueList());
                }

                Collection<? extends SpellProhibitor> prohibList = display.getProhibitedSchools(pcClass);
                if (prohibList != null)
                {
                    for (SpellProhibitor sp : prohibList)
                    {
                        set.addAll(sp.getValueList());
                    }
                }
            }
        }

        return StringUtil.join(set, jointext);
    }
}
