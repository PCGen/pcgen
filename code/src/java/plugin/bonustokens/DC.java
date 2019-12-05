/*
 * DC.java
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

/**
 * Handles the BONUS:DC token.
 */
public final class DC extends BonusObj
{
    private static final String[] BONUS_TAGS = {"FEATBONUS", "ALLSPELLS"};

    /*
     * When parsing the token, valid tokens are either a bonusTag[]
     * or:
     *    CLASS.<ClassName>
     *    DESCRIPTOR.<Description>
     *    DOMAIN.<DomainName>
     *    SCHOOL.<SchoolName>
     *    SUBSCHOOL.<SubSchoolName>
     *    TYPE.<CasterType>
     *    SPELL.<SpellName>
     */

    @Override
    protected boolean parseToken(LoadContext context, final String token)
    {
        for (int i = 0;i < BONUS_TAGS.length;++i)
        {
            if (token.equals(BONUS_TAGS[i]))
            {
                addBonusInfo(i);

                return true;
            }
        }

        boolean valid = token.startsWith(Constants.LST_CLASS);
        valid = valid || token.startsWith("DESCRIPTOR");
        valid = valid || token.startsWith("DOMAIN");
        valid = valid || token.startsWith("SCHOOL");
        valid = valid || token.startsWith("SUBSCHOOL");
        valid = valid || token.startsWith("TYPE");
        valid = valid || token.startsWith("SPELL");

        if (valid)
        {
            addBonusInfo(token);

            return true;
        }
        return false;
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
        return "DC";
    }
}
