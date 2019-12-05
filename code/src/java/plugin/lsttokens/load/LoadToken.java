/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
package plugin.lsttokens.load;

import java.math.BigDecimal;

import pcgen.core.system.LoadInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * {@code LoadToken}
 */
public class LoadToken extends AbstractTokenWithSeparator<LoadInfo> implements CDOMPrimaryToken<LoadInfo>
{

    @Override
    public String getTokenName()
    {
        return "LOAD";
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, LoadInfo info, String value)
    {
        int pipeLoc = value.indexOf('|');
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(getTokenName() + " requires a pipe, found : " + value);
        }
        if (pipeLoc != value.lastIndexOf('|'))
        {
            return new ParseResult.Fail(getTokenName() + " requires only one pipe, found : " + value);
        }
        String strengthString = value.substring(0, pipeLoc);

        int strength;
        try
        {
            strength = Integer.parseInt(strengthString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expected an Integer strength value : " + strengthString + " in value: " + value);
        }
        String loadString = value.substring(pipeLoc + 1);
        try
        {
            BigDecimal load = new BigDecimal(loadString);
            if (load.compareTo(BigDecimal.ZERO) < 0)
            {
                return new ParseResult.Fail(
                        getTokenName() + " requires a non-negative load value, found : " + loadString);
            }
            info.addLoadScoreValue(strength, load);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " misunderstood load value : " + loadString + " in value: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    public String[] unparse(LoadContext context, LoadInfo info)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<LoadInfo> getTokenClass()
    {
        return LoadInfo.class;
    }
}
