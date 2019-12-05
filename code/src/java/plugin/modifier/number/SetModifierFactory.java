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

import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaCalculation;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.FormulaFactory;
import pcgen.rules.persistence.token.AbstractFixedSetModifierFactory;

/**
 * A SetModifierFactory is a {@code ModifierFactory<Number>} that returns a specific
 * value (independent of the input) when a Modifier produced by this
 * SetModifierFactory is processed.
 */
public class SetModifierFactory extends AbstractFixedSetModifierFactory<Number>
{

    /**
     * Identifies that the Modifier objects built by this SetModifierFactory act
     * upon java.lang.Number objects.
     *
     * @return The Format (Number.class) of object upon which Modifiers built by this
     * SetModifierFactory can operate
     */
    @Override
    public Class<Number> getVariableFormat()
    {
        return Number.class;
    }

    @Override
    public FormulaModifier<Number> getModifier(String instructions,
            FormatManager<Number> formatManager)
    {
        if (!formatManager.getManagedClass().equals(getVariableFormat()))
        {
            throw new IllegalArgumentException("FormatManager must manage " + getVariableFormat().getName());
        }
        try
        {
            return getFixedModifier(formatManager, instructions);
        } catch (NumberFormatException e)
        {
            final NEPFormula<Number> f = FormulaFactory.getNEPFormulaFor(formatManager, instructions);
            NEPCalculation<Number> calc = new FormulaCalculation<>(f, this);
            return new CalculationModifier<>(calc, formatManager);
        }
    }

}
