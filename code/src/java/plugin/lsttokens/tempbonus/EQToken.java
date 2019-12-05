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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.EquipBonus;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class EQToken extends AbstractTokenWithSeparator<CDOMObject> implements CDOMSecondaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "EQ";
    }

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    "Expected " + getFullTokenName() + ":<type>|<conditions>|BONUS but did not find a second pipe");
        }
        String constraints = value.substring(0, pipeLoc);
        String bonus = value.substring(pipeLoc + 1);
        final String v = bonus.replaceAll(Pattern.quote("<this>"), obj.getKeyName());
        BonusObj bon = Bonus.newBonus(context, v);
        if (bon == null)
        {
            return new ParseResult.Fail(getFullTokenName() + " was given invalid type: " + bonus);
        }
        bon.setTokenSource(getFullTokenName());
        EquipBonus eb = new EquipBonus(bon, constraints);
        context.getObjectContext().addToList(obj, ListKey.BONUS_EQUIP, eb);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<EquipBonus> changes = context.getObjectContext().getListChanges(obj, ListKey.BONUS_EQUIP);
        Collection<EquipBonus> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token (and no global clear, so nothing to do)
            return null;
        }
        Set<String> bonusSet = new TreeSet<>();
        for (EquipBonus eb : added)
        {
            String bonusText = eb.bonus.getLSTformat();
            bonusSet.add(eb.conditions + Constants.PIPE + bonusText);
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
        return "TEMPBONUS:EQ";
    }

    @Override
    protected char separator()
    {
        return '|';
    }
}
