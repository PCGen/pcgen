/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.core.spell.Spell;

public class AvailableSpell extends ConcretePrereqObject implements QualifyingObject
{

    private final CDOMList<Spell> spelllist;
    private final Spell spell;
    private final int level;

    public AvailableSpell(CDOMList<Spell> spelllist, Spell spell, int level)
    {
        this.spelllist = spelllist;
        this.spell = spell;
        this.level = level;
    }

    public CDOMList<Spell> getSpelllist()
    {
        return spelllist;
    }

    public Spell getSpell()
    {
        return spell;
    }

    public int getLevel()
    {
        return level;
    }

}
