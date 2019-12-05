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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with MONSKILL Token
 */
public class MonskillToken extends AbstractNonEmptyToken<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "MONSKILL";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
    {
        BonusObj bon = Bonus.newBonus(context, "MONSKILLPTS|NUMBER|" + value);
        if (bon == null)
        {
            return new ParseResult.Fail(getTokenName() + " was given invalid bonus value: " + value);
        }
        Prerequisite prereq = getPrerequisite("PRELEVELMAX:1");
        if (prereq == null)
        {
            return new ParseResult.Fail("Internal Error: " + getTokenName() + " had invalid prerequisite");
        }
        bon.addPrerequisite(prereq);
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
                List<Prerequisite> prereqList = new ArrayList<>(bonus.getPrerequisiteList());
                Prerequisite prereq = getPrerequisite("PRELEVELMAX:1");
                prereqList.remove(prereq);
                if (!prereqList.isEmpty())
                {
                    sb.append('|');
                    sb.append(getPrerequisiteString(context, prereqList));
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
