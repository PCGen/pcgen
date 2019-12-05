/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.cdom.base.FormulaFactory;

/**
 * SpellResistance represents the SpellResistance provided to a PlayerCharacter
 * by a given object.
 */
public class SpellResistance extends ConcretePrereqObject
{

    /**
     * The special case of no spell resistance. This is "cached" in order to
     * provide reuse of this case during runtime.
     */
    public static final SpellResistance NONE = new SpellResistance(FormulaFactory.ZERO);

    /**
     * The Formula representing the reduction provided by this SpellResistance.
     */
    private final Formula reduction;

    /**
     * Constructs a new SpellResistance with the given Formula as the reduction
     * provided by this SpellResistance.
     *
     * @param aReduction The reduction provided by this SpellResistance.
     */
    public SpellResistance(Formula aReduction)
    {
        reduction = aReduction;
    }

    /**
     * Returns the reduction provided by this SpellResistance.
     *
     * @return The reduction provided by this SpellResistance.
     */
    public Formula getReduction()
    {
        return reduction;
    }

    @Override
    public String toString()
    {
        return reduction.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof SpellResistance)
        {
            SpellResistance othSR = (SpellResistance) other;
            return reduction.equals(othSR.reduction);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return reduction.hashCode();
    }

    /**
     * Returns a format for this SpellResistance that is suitable for storage in
     * an LST file.
     *
     * @return A format for this SpellResistance that is suitable for storage in
     * an LST file.
     */
    public String getLSTformat()
    {
        return reduction.toString();
    }
}
