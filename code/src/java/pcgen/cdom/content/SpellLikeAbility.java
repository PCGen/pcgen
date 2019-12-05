/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.core.spell.Spell;

public class SpellLikeAbility extends ConcretePrereqObject implements QualifyingObject
{
    private final Spell spell;
    private final Formula castTimes; // times the spell is in this list
    private final String castTimeUnit; // the timeunit the times is for (day, week etc)
    private final String fixedCasterLevel;
    private final String dc;
    private final String book;
    private final String qualifiedKey;

    public SpellLikeAbility(Spell sp, Formula times, String timeunit, String spellbook, String fixedLevel,
            String fixedDC, String sourceident)
    {
        spell = sp;
        castTimes = times;
        castTimeUnit = timeunit;
        book = spellbook;
        fixedCasterLevel = fixedLevel;
        dc = fixedDC;
        qualifiedKey = sourceident;
    }

    public Spell getSpell()
    {
        return spell;
    }

    public Formula getCastTimes()
    {
        return castTimes;
    }

    public String getCastTimeUnit()
    {
        return castTimeUnit;
    }

    public String getFixedCasterLevel()
    {
        return fixedCasterLevel;
    }

    public String getDC()
    {
        return dc;
    }

    public String getSpellBook()
    {
        return book;
    }

    public String getQualifiedKey()
    {
        return qualifiedKey;
    }

}
