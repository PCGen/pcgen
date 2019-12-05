/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.spell;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractRestrictedSpellPrimitive;

/**
 * SpellTypeToken handles the restriction of a spell choice to a spell from a type of
 * spell (e.g. Arcane/Divine).
 */
public class SpellTypeToken extends AbstractRestrictedSpellPrimitive
{
    private String spelltype;

    @Override
    public boolean initialize(LoadContext context, Class<Spell> cl, String value, String args)
    {
        if (value == null)
        {
            return false;
        }
        spelltype = value;
        return initialize(context, args);
    }

    @Override
    public String getTokenName()
    {
        return "SPELLTYPE";
    }

    @Override
    public boolean allow(PlayerCharacter pc, Spell spell)
    {
        HashMapToList<CDOMList<Spell>, Integer> levelInfo = pc.getSpellLevelInfo(spell);
        String source = "SPELLTYPE:" + spelltype;
        for (CDOMList<Spell> spellList : levelInfo.getKeySet())
        {
            if (spellList.isType(spelltype))
            {
                for (Integer level : levelInfo.getListFor(spellList))
                {
                    if (allow(pc, level, source, spell, null))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SpellTypeToken)
        {
            SpellTypeToken other = (SpellTypeToken) obj;
            if (spelltype == null)
            {
                return other.spelltype == null;
            }
            return spelltype.equals(other.spelltype) && equalsRestrictedPrimitive(other);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return spelltype == null ? -23 : spelltype.hashCode();
    }

    @Override
    public CharSequence getPrimitiveLST()
    {
        return new StringBuilder().append(getTokenName()).append('=').append(spelltype).append(getRestrictionLST());
    }

}
