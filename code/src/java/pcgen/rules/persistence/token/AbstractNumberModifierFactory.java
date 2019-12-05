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
package pcgen.rules.persistence.token;

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaCalculation;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ProcessCalculation;

public abstract class AbstractNumberModifierFactory<T> implements ModifierFactory<T>, BasicCalculation<T>
{

    @Override
    public FormulaModifier<T> getModifier(String instructions,
            FormatManager<T> formatManager)
    {
        try
        {
            return getFixedModifier(formatManager, instructions);
        } catch (NumberFormatException e)
        {
            final NEPFormula<T> f = FormulaFactory.getNEPFormulaFor(formatManager, instructions);
            NEPCalculation<T> calc = new FormulaCalculation<>(f, this);
            return new CalculationModifier<>(calc, formatManager);
        }
    }

    @Override
    public FormulaModifier<T> getFixedModifier(FormatManager<T> formatManager, String instructions)
    {
        T n = formatManager.convert(instructions);
        NEPCalculation<T> calc = new ProcessCalculation<>(n, this, formatManager);
        return new CalculationModifier<>(calc, formatManager);
    }
}
