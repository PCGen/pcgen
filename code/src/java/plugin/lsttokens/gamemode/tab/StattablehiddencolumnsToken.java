/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.gamemode.tab;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.TabInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Tab;

public class StattablehiddencolumnsToken extends AbstractTokenWithSeparator<TabInfo>
        implements CDOMPrimaryToken<TabInfo>
{

    @Override
    public String getTokenName()
    {
        return "STATTABLEHIDDENCOLUMNS";
    }

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, TabInfo ti, String value)
    {
        if (!Tab.SUMMARY.equals(ti.getTab()))
        {
            return new ParseResult.Fail(getTokenName() + " may only be used on the " + Tab.SUMMARY + " Tab");
        }
        ti.clearHiddenColumns();

        StringTokenizer st = new StringTokenizer(value, Constants.COMMA);
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            try
            {
                ti.hideColumn(Integer.parseInt(token));
            } catch (NumberFormatException nfe)
            {
                return new ParseResult.Fail(getTokenName() + " misunderstood Integer: " + token + " in " + value);
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, TabInfo ti)
    {
        if (!Tab.SUMMARY.equals(ti.getTab()))
        {
            return null;
        }
        Collection<Integer> columns = ti.getHiddenColumns();
        TreeSet<Integer> set = new TreeSet<>(columns);
        return new String[]{StringUtil.join(set, Constants.COMMA)};
    }

    @Override
    public Class<TabInfo> getTokenClass()
    {
        return TabInfo.class;
    }

    @Override
    protected char separator()
    {
        return ',';
    }
}
