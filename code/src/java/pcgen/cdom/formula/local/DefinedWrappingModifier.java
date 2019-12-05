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
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

/**
 * A DefinedWrappingModifier wraps a FormulaModifier and projects it to the formula system
 * as a Modifier, while also inserting any new formula system functions supported by the
 * given java.util.function.Function.
 *
 * @param <T> The format that this DefinedWrappingModifier acts upon
 */
public class DefinedWrappingModifier<T> implements Modifier<T>
{
    /**
     * The underlying FormulaModifier.
     */
    private final FormulaModifier<T> modifier;

    /**
     * The name of the defined function.
     */
    private final String definedName;

    /**
     * The value of the defined function.
     */
    private final Object definedValue;

    /**
     * The FormatManager indicating the format of the return value for the defined
     * function of DefinedWrappingModifier.
     */
    private final FormatManager<?> formatManager;

    /**
     * Constructs a new DefinedWrappingModifier for the given FormulaModifier and
     * Function.
     *
     * @param modifier      The underlying FormulaModifier
     * @param definedName   The name of the defined function
     * @param definedValue  The value of the defined function
     * @param formatManager The FormatManager indicating the format of the return value for this
     *                      DefinedWrappingModifier
     */
    public DefinedWrappingModifier(FormulaModifier<T> modifier, String definedName, Object definedValue,
            FormatManager<?> formatManager)
    {
        this.modifier = Objects.requireNonNull(modifier);
        this.definedName = Objects.requireNonNull(definedName);
        this.definedValue = Objects.requireNonNull(definedValue);
        this.formatManager = Objects.requireNonNull(formatManager);
    }

    @Override
    public T process(EvaluationManager manager)
    {
        FormulaManager formulaManager = decorateFormulaManager(manager.get(EvaluationManager.FMANAGER));
        return modifier.process(manager.getWith(EvaluationManager.FMANAGER, formulaManager));
    }

    @Override
    public void getDependencies(DependencyManager fdm)
    {
        FormulaManager formulaManager = decorateFormulaManager(fdm.get(DependencyManager.FMANAGER));
        modifier.getDependencies(fdm.getWith(DependencyManager.FMANAGER, formulaManager));
    }

    private FormulaManager decorateFormulaManager(FormulaManager formulaManager)
    {
        FunctionLibrary functionManager = formulaManager.get(FormulaManager.FUNCTION);
        functionManager = new DefinedWrappingLibrary(functionManager, definedName, definedValue, formatManager);
        return formulaManager.getWith(FormulaManager.FUNCTION, functionManager);
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
        return (31 * modifier.hashCode()) + definedValue.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof DefinedWrappingModifier)
        {
            DefinedWrappingModifier<?> other = (DefinedWrappingModifier<?>) obj;
            return modifier.equals(other.modifier) && definedValue.equals(other.definedValue)
                    && definedName.equals(other.definedName) && formatManager.equals(other.formatManager);
        }
        return false;
    }
}
