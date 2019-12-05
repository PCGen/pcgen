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

import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.util.FormatManager;

/**
 * An RemoteWrappingLibrary is a FunctionLibrary that contains two things: a Default
 * FunctionLibrary for most functions, and specific values contained by the source() or
 * target() functions.
 */
public class RemoteWrappingLibrary implements FunctionLibrary
{
    /**
     * The underlying FunctionLibrary of this RemoteWrappingLibrary.
     */
    private final FunctionLibrary functionLibrary;

    /**
     * The value of the source() function.
     */
    private final VarScoped sourceValue;

    /**
     * The value of the target() function.
     */
    private final VarScoped targetValue;

    /**
     * The FormatManager indicating the format of the return value for the source()
     * function of ThisWrappingModifier.
     */
    private final FormatManager<?> sourceFormatManager;

    /**
     * The FormatManager indicating the format of the return value for the target()
     * function of ThisWrappingModifier.
     */
    private final FormatManager<?> targetFormatManager;

    /**
     * Constructs a new RemoteWrappingLibrary with the given underlying FunctionLibrary
     * and values to be used when the target() or source() functions are called.
     *
     * @param functionLibrary     The underlying FunctionLibrary of this RemoteWrappingLibrary
     * @param sourceValue         The value of the source() function
     * @param sourceFormatManager The FormatManager indicating the format of the return value for the
     *                            source() function
     * @param targetValue         The value of the target() function
     * @param targetFormatManager The FormatManager indicating the format of the return value for the
     *                            target() function
     */
    public RemoteWrappingLibrary(FunctionLibrary functionLibrary, VarScoped sourceValue,
            FormatManager<?> sourceFormatManager, VarScoped targetValue, FormatManager<?> targetFormatManager)
    {
        this.functionLibrary = Objects.requireNonNull(functionLibrary);
        this.sourceValue = Objects.requireNonNull(sourceValue);
        this.sourceFormatManager = Objects.requireNonNull(sourceFormatManager);
        this.targetValue = Objects.requireNonNull(targetValue);
        this.targetFormatManager = Objects.requireNonNull(targetFormatManager);
    }

    @Override
    public FormulaFunction getFunction(String functionName)
    {
        if (functionName.equalsIgnoreCase("source"))
        {
            return new DefinedFunction("source", sourceValue, sourceFormatManager);
        }
        if (functionName.equalsIgnoreCase("target"))
        {
            return new DefinedFunction("target", targetValue, targetFormatManager);
        }
        return functionLibrary.getFunction(functionName);
    }
}
