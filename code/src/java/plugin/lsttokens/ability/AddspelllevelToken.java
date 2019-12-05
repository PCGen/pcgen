/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Delta;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AddspelllevelToken extends AbstractNonEmptyToken<Ability> implements CDOMPrimaryToken<Ability>
{

    @Override
    public String getTokenName()
    {
        return "ADDSPELLLEVEL";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Ability ability, String value)
    {
        try
        {
            context.getObjectContext().put(ability, IntegerKey.ADD_SPELL_LEVEL, Delta.parseInt(value));
            return ParseResult.SUCCESS;
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expected an integer.  Tag must be of the form: " + getTokenName() + ":<int>");
        }
    }

    @Override
    public String[] unparse(LoadContext context, Ability ability)
    {
        Integer lvl = context.getObjectContext().getInteger(ability, IntegerKey.ADD_SPELL_LEVEL);
        if (lvl == null)
        {
            return null;
        }
        return new String[]{lvl.toString()};
    }

    @Override
    public Class<Ability> getTokenClass()
    {
        return Ability.class;
    }

}
