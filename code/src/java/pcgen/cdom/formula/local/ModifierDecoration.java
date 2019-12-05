/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula.local;

import java.util.Objects;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

/**
 * A ModifierDecoration wraps a FormulaModifier and projects it to the formula system as a
 * Modifier.
 *
 * @param <T> The format that this ModifierDecoration acts upon
 */
public class ModifierDecoration<T> implements Modifier<T>
{
    /**
     * The underlying FormulaModifier.
     */
    private final FormulaModifier<T> modifier;

    /**
     * Constructs a new ModifierDecoration from the given FormulaModifier.
     *
     * @param modifier The FormulaModifier which this FormulaModifier is wrapping
     */
    public ModifierDecoration(FormulaModifier<T> modifier)
    {
        this.modifier = Objects.requireNonNull(modifier);
    }

    @Override
    public T process(EvaluationManager manager)
    {
        return modifier.process(manager);
    }

    @Override
    public void getDependencies(DependencyManager fdm)
    {
        modifier.getDependencies(fdm);
    }

    @Override
    public long getPriority()
    {
        return modifier.getPriority();
    }

    @Override
    public FormatManager<T> getVariableFormat()
    {
        return modifier.getVariableFormat();
    }

    @Override
    public String getIdentification()
    {
        return modifier.getIdentification();
    }

    @Override
    public String getInstructions()
    {
        return modifier.getInstructions();
    }

    @Override
    public int hashCode()
    {
        return modifier.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof ModifierDecoration) && modifier.equals(((ModifierDecoration<?>) obj).modifier);
    }
}
