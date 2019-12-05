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
package plugin.lsttokens.pcclass;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken extends AbstractNonEmptyToken<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "MONNONSKILLHD";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
    {
        BonusObj bon = Bonus.newBonus(context, "MONNONSKILLHD|NUMBER|" + value);
        if (bon == null)
        {
            return new ParseResult.Fail(getTokenName() + " was given invalid bonus value: " + value);
        }
        bon.setTokenSource(getTokenName());
        context.getObjectContext().addToList(pcc, ListKey.BONUS, bon);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass obj)
    {
        Changes<BonusObj> changes = context.getObjectContext().getListChanges(obj, ListKey.BONUS);
        if (changes == null || changes.isEmpty())
        {
            // Empty indicates no token present
            return null;
        }
        // CONSIDER need to deal with removed...
        Collection<BonusObj> added = changes.getAdded();
        String tokenName = getTokenName();
        Set<String> bonusSet = new TreeSet<>();
        for (BonusObj bonus : added)
        {
            if (tokenName.equals(bonus.getTokenSource()))
            {
                StringBuilder sb = new StringBuilder();
                sb.append(bonus.getValue());
                if (bonus.hasPrerequisites())
                {
                    sb.append('|');
                    sb.append(getPrerequisiteString(context, bonus.getPrerequisiteList()));
                }
                bonusSet.add(sb.toString());
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
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
