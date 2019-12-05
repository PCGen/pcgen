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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.base.calculation.AbstractNEPCalculation;
import pcgen.base.calculation.BasicCalculation;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.util.FormatManager;

/**
 * A ProcessCalculation is an AbstractNEPCalculation that uses a simple object
 * for the calculation (such as adding a specific Integer).
 *
 * @param <T> The format of object on which this ProcessCalculation operates
 */
public final class ProcessCalculation<T> extends AbstractNEPCalculation<T>
{
    /**
     * The underlying object to be passed in to the BasicCalculation of this
     * ProcessCalculation when it is processed.
     */
    private final T obj;

    /**
     * The FormatManager to manage objects that this ProcessCalculation operates
     * upon.
     */
    private final FormatManager<T> formatManager;

    /**
     * Constructs a new ProcessCalculation from the given object and
     * BasicCalculation.
     *
     * @param object     The underlying object to be passed into the given
     *                   BasicCalculation when this ProcessCalculation is processed
     * @param calc       The BasicCalculation which defines the operation to be
     *                   performed when this ProcessCalculation is processed
     * @param fmtManager The FormatManager for the calculation this can perform
     */
    public ProcessCalculation(T object, BasicCalculation<T> calc, FormatManager<T> fmtManager)
    {
        super(calc);
        this.obj = Objects.requireNonNull(object);
        this.formatManager = Objects.requireNonNull(fmtManager);
    }

    @Override
    public T process(EvaluationManager evalManager)
    {
        @SuppressWarnings("unchecked")
        T input = evalManager == null ? null : (T) evalManager.get(EvaluationManager.INPUT);
        return getBasicCalculation().process(input, obj);
    }

    @Override
    public String getInstructions()
    {
        return formatManager.unconvert(obj);
    }

    @Override
    public int hashCode()
    {
        return obj.hashCode() ^ getBasicCalculation().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof ProcessCalculation)
        {
            ProcessCalculation<?> other = (ProcessCalculation<?>) o;
            return other.getBasicCalculation().equals(getBasicCalculation()) && other.obj.equals(obj);
        }
        return false;
    }

    @Override
    public void getDependencies(DependencyManager fdm)
    {
        //Since this is direct (already has the object), it has no dependencies
    }

    @Override
    public void isValid(FormulaSemantics semantics)
    {
        /*
         * Since this is direct (already has the object), it has no semantic issues
         * (barring someone violating Generics)
         */
    }
}
