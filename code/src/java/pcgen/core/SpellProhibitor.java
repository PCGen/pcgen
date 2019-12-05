/*
 * Copyright 2005 (c) Stefan Raderamcher <radermacher@netcologne.de>
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
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.spell.Spell;
import pcgen.util.enumeration.ProhibitedSpellType;

public class SpellProhibitor extends ConcretePrereqObject
{

    private ProhibitedSpellType type = null;
    private List<String> valueList = null;

    public ProhibitedSpellType getType()
    {
        return type;
    }

    public List<String> getValueList()
    {
        return valueList;
    }

    public void setType(ProhibitedSpellType prohibitedType)
    {
        type = prohibitedType;
    }

    public void addValue(String value)
    {
        if (valueList == null)
        {
            valueList = new ArrayList<>();
        }
        valueList.add(value);
    }

    public boolean isProhibited(Spell s, PlayerCharacter aPC, CDOMObject owner)
    {
        /*
         * Note the rule is only "Prohibit Cleric/Druid spells based on
         * Alignment" - thus this Globals check is only relevant to the
         * Alignment type
         */
        if (type == ProhibitedSpellType.ALIGNMENT && !Globals.checkRule(RuleConstants.PROHIBITSPELLS))
        {
            return false;
        }

        if (!qualifies(aPC, owner))
        {
            return false;
        }

        int hits = 0;
        for (String typeDesc : type.getCheckList(s))
        {
            for (String prohib : valueList)
            {
                if (prohib.equalsIgnoreCase(typeDesc))
                {
                    hits++;
                }
            }
        }
        return hits == type.getRequiredCount(valueList);
    }

    @Override
    public int hashCode()
    {
        return type.hashCode() ^ valueList.size();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SpellProhibitor))
        {
            return false;
        }
        SpellProhibitor other = (SpellProhibitor) o;
        if ((type == null && other.type == null) || (type != null && type == other.type))
        {
            return (other.valueList == null && valueList == null)
                    || valueList != null && valueList.equals(other.valueList);
        }
        return false;
    }

}
