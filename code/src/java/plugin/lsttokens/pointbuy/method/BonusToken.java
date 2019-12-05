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
package plugin.lsttokens.pointbuy.method;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.core.PointBuyMethod;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * {@code BonusToken}
 */
public class BonusToken extends AbstractNonEmptyToken<PointBuyMethod> implements CDOMPrimaryToken<PointBuyMethod>
{

    @Override
    public String getTokenName()
    {
        return "BONUS";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PointBuyMethod pbm, String value)
    {
        BonusObj bon = Bonus.newBonus(context, value);
        if (bon == null)
        {
            return new ParseResult.Fail(getTokenName() + " was given invalid bonus: " + value);
        }
        bon.setTokenSource(getTokenName());
        pbm.addBonus(bon);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PointBuyMethod pbm)
    {
        Collection<BonusObj> added = pbm.getBonuses();
        String tokenName = getTokenName();
        Set<String> bonusSet = new TreeSet<>();
        for (BonusObj bonus : added)
        {
            if (tokenName.equals(bonus.getTokenSource()))
            {
                bonusSet.add(bonus.toString());
            }
        }
        if (bonusSet.isEmpty())
        {
            // This is okay - just no BONUSes from this token
            return null;
        }
        return bonusSet.toArray(new String[0]);
    }

    @Override
    public Class<PointBuyMethod> getTokenClass()
    {
        return PointBuyMethod.class;
    }
}
