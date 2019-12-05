/*
 * Copyright (c) Thomas Parker, 2014.
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
package pcgen.cdom.facet.event;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Skill;

/**
 * AssociationChangeEvent is an event sent to a AssociationChangeListener when a
 * Association Bonus value changes on a Player Character.
 */
public class AssociationChangeEvent
{

    /**
     * The CharID identifying the Player Character on which the Bonus value
     * change took place.
     */
    private final CharID charID;

    /**
     * The Skill for which the Association Bonus value changed on the Player
     * Character.
     */
    private final Skill skill;

    /**
     * The previous value of the Bonus value
     */
    private final Number oldVal;

    /**
     * The new value of the Bonus value
     */
    private final Number newVal;

    private final Object source;

    /**
     * Constructs a new AssociationChangeEvent indicating a Bonus value change
     * took place on the Player Character identified by the given CharId. The
     * Bonus name, type, old value, and new value are provided.
     *
     * @param id       The CharID indicating the Player Character on which the Bonus
     *                 value change took place
     * @param sk       The Skill for the Association Bonus value that changed
     * @param oldValue The previous value of the Bonus value
     * @param newValue The new value of the Bonus value
     * @param src      The source object for this AssociationChangeEvent
     */
    public AssociationChangeEvent(CharID id, Skill sk, Number oldValue, Number newValue, Object src)
    {
        charID = id;
        skill = sk;
        oldVal = oldValue;
        newVal = newValue;
        source = src;
    }

    public CharID getCharID()
    {
        return charID;
    }

    public Skill getSkill()
    {
        return skill;
    }

    public Number getOldVal()
    {
        return oldVal;
    }

    public Number getNewVal()
    {
        return newVal;
    }

    public Object getSource()
    {
        return source;
    }
}
