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
import pcgen.rules.persistence.TokenUtilities;

/**
 * Handles the BONUS:SKILLRANK token.
 */
public final class SkillRank extends BonusObj
{
    private static final Class<pcgen.core.Skill> SKILL_CLASS = pcgen.core.Skill.class;

    @Override
    protected boolean parseToken(LoadContext context, final String token)
    {
        if (token.startsWith(Constants.LST_TYPE_EQUAL))
        {
            addBonusInfo(token.replace('=', '.'));
        } else
        {
            addBonusInfo(token);
        }

        if (!token.equals("LIST") && !token.equals("%LIST") && !token.equals("%CHOICE") && !token.equals("%VAR"))
        {
            //This is done entirely for the side effects
            context.forgetMeNot(TokenUtilities.getReference(context, SKILL_CLASS, token));
        }

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
        return "SKILLRANK";
    }
}
