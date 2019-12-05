/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.core.PlayerCharacter;

/**
 * An OptionBound represents a range of values between two Formulas. This range
 * for an OptionBound is inclusive.
 * <p>
 * The name is derived from the bounds used in a kit to apply various options
 * (typically from a random value selection)
 */
public class OptionBound
{
    /**
     * The Formula defining the lower bound (inclusive) for this OptionBound.
     * May be null to represent no lower bound.
     */
    private final Formula minOption;

    /**
     * The Formula defining the upper bound (inclusive) for this OptionBound.
     * May be null to represent no upper bound.
     */
    private final Formula maxOption;

    /**
     * Constructs a new OptionBound which bounds the range of values between the
     * two given Formulas.
     *
     * @param min The Formula defining the lower bound (inclusive) for this
     *            OptionBound. May be null to represent no lower bound.
     * @param max The Formula defining the upper bound (inclusive) for this
     *            OptionBound. May be null to represent no upper bound.
     */
    public OptionBound(Formula min, Formula max)
    {
        minOption = min;
        maxOption = max;
    }

    /**
     * Returns true if this OptionBound includes the given value when the
     * Formulas in this OptionBound are resolved relative to the given
     * PlayerCharacter.
     *
     * @param pc    The PlayerCharacter to be used when resolving the bounding
     *              Formulas for this OptionBound.
     * @param value The value to be checked to determine if it falls within the
     *              range provided by this OptionBound.
     * @return true if the given value is within the bounds of this OptionBound
     * (inclusive) when the bounds are resolved within the context of
     * the given PlayerCharacter; false otherwise.
     */
    public boolean isOption(PlayerCharacter pc, int value)
    {
        return (minOption == null || minOption.resolve(pc, "").intValue() <= value)
                && (maxOption == null || maxOption.resolve(pc, "").intValue() >= value);
    }

    /**
     * Returns the Formula defining the lower bound (inclusive) for this
     * OptionBound. May be null to represent no lower bound.
     *
     * @return The Formula defining the lower bound (inclusive) for this
     * OptionBound.
     */
    public Formula getOptionMin()
    {
        return minOption;
    }

    /**
     * Returns the Formula defining the upper bound (inclusive) for this
     * OptionBound. May be null to represent no upper bound.
     *
     * @return The Formula defining the lower bound (inclusive) for this
     * OptionBound.
     */
    public Formula getOptionMax()
    {
        return maxOption;
    }
}
