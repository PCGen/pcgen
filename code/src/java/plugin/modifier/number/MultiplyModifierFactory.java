/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.modifier.number;

import pcgen.base.lang.NumberUtilities;
import pcgen.rules.persistence.token.AbstractNumberModifierFactory;

/**
 * An MultiplyModifierFactory is a {@code ModifierFactory<Number>} that multiplies a
 * specific value (provided during construction of this MultiplyModifierFactory)
 * with the input when a Modifier produced by this MultiplyModifierFactory is
 * processed.
 */
public class MultiplyModifierFactory extends AbstractNumberModifierFactory<Number>
{

    /**
     * Identifies that the Modifier objects built by this
     * MultiplyModifierFactory act upon java.lang.Number objects.
     *
     * @return The Format (Number.class) of object upon which Modifiers built by this
     * MultiplyModifierFactory can operate
     */
    @Override
    public Class<Number> getVariableFormat()
    {
        return Number.class;
    }

    /**
     * Returns the product of the two input values, used by Modifiers produced
     * by this MultiplyModifierFactory
     *
     * @param previousValue The first input value used to determine the appropriate result of a
     *                      Modifier produced by this ModifierFactory.
     * @param argument      The second input value used to determine the appropriate result of a
     *                      Modifier produced by this ModifierFactory.
     * @return The resulting value of the Modifier when processed
     */
    @Override
    public Number process(Number previousValue, Number argument)
    {
        return NumberUtilities.multiply(previousValue, argument);
    }

    @Override
    public int getInherentPriority()
    {
        return 1;
    }

    @Override
    public String getIdentification()
    {
        return "MULTIPLY";
    }
}
