/*
 * Copyright (c) 2019 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.gamemode.ageset;

import java.util.Set;
import java.util.TreeSet;

import pcgen.core.AgeSet;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Implements the BONUS token for AgeSet objects.
 */
public class BonusToken implements CDOMPrimaryToken<AgeSet>
{
    @Override
    public String getTokenName()
    {
        return "BONUS";
    }

    @Override
    public ParseResult parseToken(LoadContext context, AgeSet obj, String value)
    {
        BonusObj bon = Bonus.newBonus(context, value);
        if (bon == null)
        {
            return new ParseResult.Fail(
                    getTokenName() + " was given invalid bonus: " + value);
        }
        bon.setTokenSource(getTokenName());
        obj.addBonus(bon);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AgeSet obj)
    {
        String tokenName = getTokenName();
        Set<String> bonusSet = new TreeSet<>();
        for (BonusObj bonus : obj.getBonuses())
        {
            if (tokenName.equals(bonus.getTokenSource()))
            {
                String bonusString = bonus.getLSTformat();
                bonusSet.add(bonusString);
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
    public Class<AgeSet> getTokenClass()
    {
        return AgeSet.class;
    }
}
