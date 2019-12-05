/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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

package pcgen.util.enumeration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.spell.Spell;

public enum ProhibitedSpellType
{

    ALIGNMENT("Alignment")
            {
                @Override
                public Collection<String> getCheckList(Spell s)
                {
                    return s.getSafeListFor(ListKey.SPELL_DESCRIPTOR);
                }

                @Override
                public int getRequiredCount(Collection<String> l)
                {
                    return l.size();
                }
            },

    DESCRIPTOR("Descriptor")
            {
                @Override
                public Collection<String> getCheckList(Spell s)
                {
                    return s.getSafeListFor(ListKey.SPELL_DESCRIPTOR);
                }

                @Override
                public int getRequiredCount(Collection<String> l)
                {
                    return l.size();
                }
            },

    SCHOOL("School")
            {
                @Override
                public Collection<String> getCheckList(Spell s)
                {
                    /*
                     * Long method for now
                     * TODO Clean up
                     */
                    List<String> list = new ArrayList<>();
                    for (SpellSchool ss : s.getSafeListFor(ListKey.SPELL_SCHOOL))
                    {
                        list.add(ss.toString());
                    }
                    return list;
                }

                @Override
                public int getRequiredCount(Collection<String> l)
                {
                    return l.size();
                }
            },

    SUBSCHOOL("SubSchool")
            {
                @Override
                public Collection<String> getCheckList(Spell s)
                {
                    return s.getSafeListFor(ListKey.SPELL_SUBSCHOOL);
                }

                @Override
                public int getRequiredCount(Collection<String> l)
                {
                    return l.size();
                }
            },

    SPELL("Spell")
            {
                @Override
                public Collection<String> getCheckList(Spell s)
                {
                    return Collections.singletonList(s.getKeyName());
                }

                @Override
                public int getRequiredCount(Collection<String> l)
                {
                    return 1;
                }
            };

    private final String text;

    ProhibitedSpellType(String s)
    {
        text = s;
    }

    public abstract Collection<String> getCheckList(Spell s);

    public abstract int getRequiredCount(Collection<String> l);

    @Override
    public String toString()
    {
        return text;
    }
}
