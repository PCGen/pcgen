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
package plugin.lsttokens.gamemode.basedice;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.BaseDice;
import pcgen.core.RollInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class UpToken extends AbstractTokenWithSeparator<BaseDice> implements CDOMPrimaryToken<BaseDice>
{

    @Override
    public String getTokenName()
    {
        return "UP";
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, BaseDice bd, String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.COMMA);
        while (st.hasMoreTokens())
        {
            String roll = st.nextToken();
            try
            {
                bd.addToUpList(new RollInfo(roll));
            } catch (IllegalArgumentException e)
            {
                return new ParseResult.Fail("Invalid Roll provided: " + roll + " in " + value);
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    public String[] unparse(LoadContext context, BaseDice bd)
    {
        return new String[]{StringUtil.join(bd.getUpSteps(), Constants.COMMA)};
    }

    @Override
    public Class<BaseDice> getTokenClass()
    {
        return BaseDice.class;
    }
}
