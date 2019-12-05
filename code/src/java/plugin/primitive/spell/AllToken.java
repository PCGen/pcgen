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
 * AllToken is a Primitive that filters Spells based on restrictions, but without a limit
 * of what PCClass or Domain can cast a spell.
 */
public class AllToken extends AbstractRestrictedSpellPrimitive
{
    @Override
    public boolean initialize(LoadContext context, Class<Spell> cl, String value, String args)
    {
        if (value != null)
        {
            return false;
        }
        return initialize(context, args);
    }

    @Override
    public String getTokenName()
    {
        return "ALL";
    }

    @Override
    public boolean allow(PlayerCharacter pc, Spell spell)
    {
        HashMapToList<CDOMList<Spell>, Integer> levelInfo = pc.getSpellLevelInfo(spell);
        for (CDOMList<Spell> spellList : levelInfo.getKeySet())
        {
            for (Integer level : levelInfo.getListFor(spellList))
            {
                if (allow(pc, level, "ANY", spell, null))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return hasRestriction() ? GroupingState.ANY : GroupingState.ALLOWS_NONE;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof AllToken)
        {
            AllToken other = (AllToken) obj;
            return equalsRestrictedPrimitive(other);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return -47;
    }

    @Override
    public CharSequence getPrimitiveLST()
    {
        return getTokenName() + getRestrictionLST();
    }

}
