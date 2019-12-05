/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.modifier.dynamic;

import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.content.IndirectCalculation;
import pcgen.cdom.inst.Dynamic;
import pcgen.rules.persistence.token.AbstractSetModifierFactory;

/**
 * An SetModifier is a {@code Modifier<Dyanmic>} that returns a specific value
 * (independent of the input) when the Modifier is processed.
 */
public class SetModifierFactory extends AbstractSetModifierFactory<Dynamic>
{

    @Override
    public FormulaModifier<Dynamic> getModifier(String instructions,
            FormatManager<Dynamic> formatManager)
    {
        if (!getVariableFormat().isAssignableFrom(formatManager.getManagedClass()))
        {
            throw new IllegalArgumentException(
                    "FormatManager must manage " + getVariableFormat().getName() + " or a child of that class");
        }
        Indirect<Dynamic> n = formatManager.convertIndirect(instructions);
        NEPCalculation<Dynamic> calc = new IndirectCalculation<>(n, this);
        return new CalculationModifier<>(calc, formatManager);
    }

    @Override
    public Class<Dynamic> getVariableFormat()
    {
        return Dynamic.class;
    }
}
