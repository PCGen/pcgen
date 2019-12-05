/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.util.FormatManager;

/**
 * An AbstractFixedSetModifierFactory is a ModifierToken/BasicCalculation that returns a
 * specific value (independent of the input) when the AbstractFixedSetModifierFactory is
 * processed. This should be used for formats which are fixed (e.g. Number, String), not
 * those that are referenced (Skill, Language, et al).
 *
 * @param <T> The format of the object handled by this AbstractFixedSetModifierFactory
 */
public abstract class AbstractFixedSetModifierFactory<T> extends AbstractSetModifierFactory<T>
{

    @Override
    public FormulaModifier<T> getModifier(String instructions, FormatManager<T> formatManager)
    {
        if (!getVariableFormat().isAssignableFrom(formatManager.getManagedClass()))
        {
            throw new IllegalArgumentException("FormatManager must manage " + getVariableFormat().getName());
        }
        return getFixedModifier(formatManager, instructions);
    }

}
