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

package plugin.lsttokens.kit.skill;

import java.math.BigDecimal;

import pcgen.core.kit.KitSkill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * RANK token
 */
public class RankToken extends AbstractNonEmptyToken<KitSkill> implements CDOMPrimaryToken<KitSkill>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "RANK";
    }

    @Override
    public Class<KitSkill> getTokenClass()
    {
        return KitSkill.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, KitSkill kitSkill, String value)
    {
        try
        {
            BigDecimal rank = new BigDecimal(value);
            if (rank.compareTo(BigDecimal.ZERO) < 0)
            {
                return new ParseResult.Fail(getTokenName() + " must be a positive number: " + value);
            }
            kitSkill.setRank(rank);
            return ParseResult.SUCCESS;
        } catch (NumberFormatException e)
        {
            return new ParseResult.Fail(getTokenName() + " expected a number: " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, KitSkill kitSkill)
    {
        BigDecimal bd = kitSkill.getRank();
        if (bd == null)
        {
            return null;
        }
        return new String[]{bd.toString()};
    }
}
