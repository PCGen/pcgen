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
 *
 *
 */
package pcgen.persistence.lst;

import java.util.Arrays;
import java.util.List;

import pcgen.cdom.content.TabInfo;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * TabLoader loads TAB lines from the miscinfo.lst file for a game mode.
 */
public class TabLoader extends SimpleLoader<TabInfo>
{

    private List<String> deprecatedTabNames =
            Arrays.asList("ABILITIES", "CAMPAIGNS", "RACE", "GEAR", "RESOURCES", "NATURALWEAPONS", "SOURCEINFO");

    public TabLoader()
    {
        super(TabInfo.class);
    }

    @Override
    protected String processFirstToken(LoadContext context, String token)
    {
        if (!Tab.exists(token))
        {
            if (deprecatedTabNames.contains(token.toUpperCase()))
            {
                Logging.deprecationPrint("TAB:" + token + " has been deprecated and is now ignored.", context);
            } else
            {
                Logging.errorPrint("TAB:" + token + " is not valid.", context);
            }
            return null;
        }

        return super.processFirstToken(context, token);
    }

}
