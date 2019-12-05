/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import java.util.Collection;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STARTFEATS Token
 */
public class StartfeatsToken extends AbstractToken implements CDOMPrimaryToken<Race>
{

    @Override
    public String getTokenName()
    {
        return "STARTFEATS";
    }

    @Override
    public ParseResult parseToken(LoadContext context, Race race, String value)
    {
        int bonusValue;

        try
        {
            bonusValue = Integer.parseInt(value);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    "Error encountered in " + getTokenName() + " was expecting value to be an integer, found: " + value);
        }

        BonusObj bon = Bonus.newBonus(context, "FEAT|POOL|" + bonusValue);
        if (bon == null)
        {
            return new ParseResult.Fail("Internal Error: " + getTokenName() + " had invalid bonus");
        }
        Prerequisite prereq = getPrerequisite("PREMULT:1,[PREHD:MIN=1],[PRELEVEL:MIN=1]");
        if (prereq == null)
        {
            return new ParseResult.Fail("Internal Error: " + getTokenName() + " had invalid prerequisite");
        }
        bon.addPrerequisite(prereq);
        bon.setTokenSource(getTokenName());
        context.getObjectContext().addToList(race, ListKey.BONUS, bon);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        Changes<BonusObj> changes = context.getObjectContext().getListChanges(race, ListKey.BONUS);
        if (changes == null || changes.isEmpty())
        {
            // Empty indicates no token present
            return null;
        }
        // CONSIDER need to deal with removed...
        Collection<BonusObj> added = changes.getAdded();
        String tokenName = getTokenName();
        Collection<String> bonusSet = new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
        for (BonusObj bonus : added)
        {
            if (tokenName.equals(bonus.getTokenSource()))
            {
                bonusSet.add(bonus.getValue());
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
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }

}
