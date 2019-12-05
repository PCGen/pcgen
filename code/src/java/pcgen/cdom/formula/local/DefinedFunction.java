/*
 * Copyright (c) Thomas Parker, 2018.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.formula.local;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * A DefinedFunction is a zero-argument, pre-defined return value FormulaFunction for the
 * new formula system.
 * <p>
 * This is usually dynamically generated, and is used to support functions like this().
 */
public class DefinedFunction implements FormulaFunction
{
    /**
     * The name of this DefinedFunction.
     */
    private final String name;

    /**
     * The return value for this DefinedFunction.
     */
    private final Object definedValue;

    /**
     * The FormatManager indicating the format of the return value for this
     * DefinedFunction.
     */
    private final FormatManager<?> formatManager;

    /**
     * Constructs a new DefinedFunction with the given name and return value.
     *
     * @param name          The name of this DefinedFunction
     * @param definedValue  The return value for this DefinedFunction
     * @param formatManager The FormatManager indicating the format of the return value for this
     *                      DefinedFunction
     */
    public DefinedFunction(String name, Object definedValue, FormatManager<?> formatManager)
    {
        this.name = Objects.requireNonNull(name);
        this.definedValue = Objects.requireNonNull(definedValue);
        this.formatManager = Objects.requireNonNull(formatManager);
    }

    @Override
    public String getFunctionName()
    {
        return name;
    }

    @Override
    public Boolean isStatic(StaticVisitor visitor, Node[] args)
    {
        return true;
    }

    @Override
    public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
    {
        if (args.length == 0)
        {
            return formatManager;
        }
        throw new SemanticsFailureException("Function " + name
                + "() received incorrect # of arguments, expected: 0 got " + args.length + " " + Arrays.asList(args));
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
    {
        return definedValue;
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager manager, Node[] args)
    {
        return Optional.of(formatManager);
    }

}
