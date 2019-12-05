/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
package plugin.bonustokens;

import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.enumeration.Load;

/**
 * Handles the BONUS:MOVEADD token.
 **/
public final class MoveAdd extends BonusObj
{
    private static final String[] BONUS_TAGS =
            {Load.LIGHT.toString(), Load.MEDIUM.toString(), Load.HEAVY.toString(), Load.OVERLOAD.toString()};

    @Override
    protected boolean parseToken(LoadContext context, final String token)
    {
        for (int i = 0;i < BONUS_TAGS.length;++i)
        {
            if (BONUS_TAGS[i].equals(token))
            {
                addBonusInfo(i);

                return true;
            }
        }

        if (token.startsWith(Constants.LST_TYPE_EQUAL))
        {
            addBonusInfo(token.replace('=', '.'));
        } else
        {
            addBonusInfo(token);
        }

        return true;
    }

    @Override
    protected String unparseToken(final Object obj)
    {
        if (obj instanceof Integer)
        {
            return BONUS_TAGS[(Integer) obj];
        }

        return (String) obj;
    }

    /**
     * Return the bonus tag handled by this class.
     *
     * @return The bonus handled by this class.
     */
    @Override
    public String getBonusHandled()
    {
        return "MOVEADD";
    }
}
