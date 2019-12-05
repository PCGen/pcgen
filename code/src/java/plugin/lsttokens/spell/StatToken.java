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
package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STAT Token
 */
public class StatToken extends AbstractNonEmptyToken<Spell> implements CDOMPrimaryToken<Spell>
{

    private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

    @Override
    public String getTokenName()
    {
        return "STAT";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, Spell spell, String value)
    {
        CDOMSingleRef<PCStat> pcs = context.getReferenceContext().getCDOMReference(PCSTAT_CLASS, value);
        if (pcs == null)
        {
            return new ParseResult.Fail("Invalid Stat Abbreviation in Token " + getTokenName() + ": " + value);
        }
        context.getObjectContext().put(spell, ObjectKey.SPELL_STAT, pcs);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Spell spell)
    {
        CDOMSingleRef<PCStat> pcs = context.getObjectContext().getObject(spell, ObjectKey.SPELL_STAT);
        if (pcs == null)
        {
            return null;
        }
        return new String[]{pcs.getLSTformat(false)};
    }

    @Override
    public Class<Spell> getTokenClass()
    {
        return Spell.class;
    }
}
