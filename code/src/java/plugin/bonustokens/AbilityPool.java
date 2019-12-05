/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Handles the BONUS:ABILITYPOOL token.
 */
public final class AbilityPool extends BonusObj
{
    @Override
    protected boolean parseToken(LoadContext context, final String token)
    {
        if (token == null)
        {
            Logging.errorPrint("Malformed BONUS:ABILITYPOOL Requires Ability Category");
        }
        addBonusInfo(token);
        return true;
    }

    @Override
    protected String unparseToken(final Object obj)
    {
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
        return "ABILITYPOOL";
    }
}
