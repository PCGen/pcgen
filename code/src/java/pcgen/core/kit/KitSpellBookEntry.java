/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;

/**
 * Deals with a SpellBook Entry for Kits
 */
public class KitSpellBookEntry
{
    private final String bookName;
    private final Spell spell;
    private final int theCount;
    private final List<CDOMSingleRef<Ability>> theModifierList;

    private PCClass theClass = null;

    /**
     * @param aBookName
     * @param sp
     * @param modifiers
     * @param copies
     */
    public KitSpellBookEntry(final String aBookName, final Spell sp, final List<CDOMSingleRef<Ability>> modifiers,
            int copies)
    {
        bookName = aBookName;
        spell = sp;
        if (modifiers != null && !modifiers.isEmpty())
        {
            theModifierList = new ArrayList<>();
            theModifierList.addAll(modifiers);
        } else
        {
            theModifierList = null;
        }
        theCount = copies;
    }

    /**
     * Get the spell book name
     *
     * @return the spell book name
     */
    public String getBookName()
    {
        return bookName;
    }

    /**
     * Get the spell
     *
     * @return spell
     */
    public Spell getSpell()
    {
        return spell;
    }

    /**
     * Get the modifiers
     *
     * @return the modifiers
     */
    public List<CDOMSingleRef<Ability>> getModifiers()
    {
        List<CDOMSingleRef<Ability>> ret = theModifierList;
        if (ret == null)
        {
            ret = new ArrayList<>();
        }
        return Collections.unmodifiableList(ret);
    }

    /**
     * Get the number of copies
     *
     * @return the number of copies
     */
    public int getCopies()
    {
        return theCount;
    }

    /**
     * Set the PC Class
     *
     * @param aClass
     */
    public void setPCClass(final PCClass aClass)
    {
        theClass = aClass;
    }

    /**
     * Get the class of the PC
     *
     * @return the class of the PC
     */
    public PCClass getPCClass()
    {
        return theClass;
    }

    /**
     * TODO Fix this
     *
     * @return String
     */
    @Override
    public String toString()
    {
        return spell.getDisplayName();
    }
}
