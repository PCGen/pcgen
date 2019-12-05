/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.spell;

import java.util.StringJoiner;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.SpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.InfoFacade;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * {@code Spell} represents a magic spell from the games rules.
 */
@SuppressWarnings("serial")
public final class Spell extends PObject implements InfoFacade, Ungranted
{
    public static final CDOMReference<SpellList> SPELLS;

    static
    {
        SpellList wpl = new SpellList();
        wpl.setName("*Spells");
        SPELLS = CDOMDirectSingleRef.getRef(wpl);
    }

    @Override
    public String getPCCText()
    {
        StringJoiner txt = new StringJoiner("\t");
        txt.add(getDisplayName());
        Globals.getContext().unparse(this).forEach(txt::add);
        txt.add(PrerequisiteWriter.prereqsToString(this));
        return txt.toString();
    }

    /**
     * Tests to see if two Spell objects are equal.
     *
     * @param obj Spell to compare to.
     * @return <tt>true</tt> if the Spells are the same.
     */
    @Override
    public boolean equals(final Object obj)
    {
        return obj instanceof Spell && getKeyName().equalsIgnoreCase(((Spell) obj).getKeyName());
    }

    /**
     * Need something consistent with equals - this causes conflicts with the same name
     * but that's ok, it's only a hashcode.
     */
    @Override
    public int hashCode()
    {
        return getKeyName().hashCode();
    }

    public boolean isAllowed(Type t)
    {
        boolean allowed = containsInList(ListKey.ITEM, t);
        boolean prohibited = Type.POTION.equals(t) || containsInList(ListKey.PROHIBITED_ITEM, t);
        return allowed || !prohibited;
    }

    @Override
    public String toString()
    {
        if (SettingsHandler.guiUsesOutputNameSpells())
        {
            return getOutputName();
        }

        return getDisplayName();
    }
}
