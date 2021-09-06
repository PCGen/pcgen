/*
 * Copyright 2014-18 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

import java.util.Objects;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.util.FormatManager;

/**
 * A CalculationModifier is a Modifier that is a wrapper around a NEPCalculation. A
 * CalculationModifier also contains the user priority of the Modifier.
 * 
 * @see pcgen.base.calculation.NEPCalculation
 * 
 * @param <T>
 *            The format that this CalculationModifier acts upon
 */
public final class CalculationModifier<T> extends AbstractPCGenModifier<T>
{

	/**
	 * The NEPCalculation to be performed by this CalculationModifier.
	 */
	private final NEPCalculation<T> toDo;

	private final FormatManager<T> formatManager;

	/**
	 * Constructs a new CalculationModifier from the given NEPCalculation.
	 * 
	 * The intent is that a solver would process the Modifier with the lowest
	 * user priority first.
	 * 
	 * @param calc
	 *            The NEPCalculation to be performed by this CalculationModifier when it
	 *            is processed
	 * @throws IllegalArgumentException
	 *             if the given NEPCalculation is null
	 */
	public CalculationModifier(NEPCalculation<T> calc, FormatManager<T> fmtManager)
	{
		Objects.requireNonNull(calc, "Calculation cannot be null");
		Objects.requireNonNull(fmtManager, "FormatManager cannot be null");
		toDo = calc;
		formatManager = fmtManager;
	}

	@Override
	public long getPriority()
	{
		return ((long) getUserPriority() << 32) + toDo.getInherentPriority();
	}

	@Override
	public T process(EvaluationManager manager)
	{
		return toDo.process(manager);
	}

	@Override
	public void getDependencies(DependencyManager manager)
	{
		toDo.getDependencies(manager);
	}

	@Override
	public String getInstructions()
	{
		return toDo.getInstructions();
	}

	@Override
	public FormatManager<T> getVariableFormat()
	{
		return formatManager;
	}

	@Override
	public String getIdentification()
	{
		return toDo.getIdentification();
	}

	@Override
	public int hashCode()
	{
		return getUserPriority() ^ toDo.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CalculationModifier<?> other)
		{
			return (other.getUserPriority() == getUserPriority()) && other.toDo.equals(toDo);
		}
		return false;
	}

	/**
	 * Returns true if the given FormatManager is equal to the FormatManager for this
	 * CalculationModifier.
	 * 
	 * @param fm
	 *            The FormatManager to check if it is equal to the FormatManager for this
	 *            CalculationModifier
	 * @return true if the given FormatManager is equal to the FormatManager for this
	 *         CalculationModifier; false otherwise
	 */
	public boolean isCompatible(FormatManager<?> fm)
	{
		return formatManager.equals(fm);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": " + toDo + " ["
			+ formatManager.getIdentifierType() + "]";
	}

	@Override
	public void isValid(FormulaSemantics semantics) throws SemanticsException
	{
		toDo.isValid(semantics);
	}
}
