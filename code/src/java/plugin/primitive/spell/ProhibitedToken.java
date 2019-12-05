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

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

/**
 * ProhibitedToken is a Primitive that includes spells that have been prohibited for a PC.
 */
public class ProhibitedToken implements PrimitiveToken<Spell>, PrimitiveFilter<Spell>
{
    private static final Class<Spell> SPELL_CLASS = Spell.class;
    private boolean prohibited;
    private CDOMReference<Spell> allSpells;

    @Override
    public boolean initialize(LoadContext context, Class<Spell> cl, String value, String args)
    {
        if (args != null)
        {
            return false;
        }
        if ("YES".equals(value))
        {
            prohibited = true;
        } else if ("NO".equals(value))
        {
            prohibited = false;
        } else
        {
            Logging.errorPrint("Did not understand Prohibited value: " + value);
            return false;
        }
        allSpells = context.getReferenceContext().getCDOMAllReference(SPELL_CLASS);
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "PROHIBITED";
    }

    @Override
    public Class<Spell> getReferenceClass()
    {
        return SPELL_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return getTokenName() + '=' + (prohibited ? "YES" : "NO");
    }

    @Override
    public boolean allow(PlayerCharacter pc, Spell spell)
    {
        for (PCClass cl : pc.getDisplay().getClassSet())
        {
            if (prohibited == SpellCountCalc.isProhibited(spell, cl, pc))
            {
                return false;
            }
        }
        return true;
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
        if (obj instanceof ProhibitedToken)
        {
            ProhibitedToken other = (ProhibitedToken) obj;
            return prohibited == other.prohibited;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return prohibited ? 1345 : 999234;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Spell, R> c)
    {
        return c.convert(allSpells, this);
    }
}
