/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.tempbonus;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class AnyPCToken extends AbstractTokenWithSeparator<CDOMObject> implements CDOMSecondaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "ANYPC";
    }

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        final String v = value.replaceAll(Pattern.quote("<this>"), obj.getKeyName());
        BonusObj bon = Bonus.newBonus(context, v);
        if (bon == null)
        {
            return new ParseResult.Fail(getFullTokenName() + " was given invalid type: " + value);
        }
        bon.setTokenSource(getFullTokenName());
        context.getObjectContext().addToList(obj, ListKey.BONUS_ANYPC, bon);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<BonusObj> changes = context.getObjectContext().getListChanges(obj, ListKey.BONUS_ANYPC);
        Collection<BonusObj> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token (and no global clear, so nothing to do)
            return null;
        }
        Set<String> bonusSet = new TreeSet<>();
        for (BonusObj bonus : added)
        {
            bonusSet.add(bonus.getLSTformat());
        }
        return bonusSet.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public String getParentToken()
    {
        return "TEMPBONUS";
    }

    private String getFullTokenName()
    {
        return "TEMPBONUS:ANYPC";
    }

    @Override
    protected char separator()
    {
        return '|';
    }
}
