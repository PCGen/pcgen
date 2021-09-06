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
import pcgen.base.formula.base.VarScoped;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

/**
 * A RemoteWrappingModifier wraps a FormulaModifier and projects it to the formula system
 * as a Modifier, while also inserting any new formula system functions supported by the
 * given java.util.function.Function.
 * 
 * @param <T>
 *            The format that this RemoteWrappingModifier acts upon
 */
public class RemoteWrappingModifier<T> implements Modifier<T>
{
	/**
	 * The underlying FormulaModifier.
	 */
	private final FormulaModifier<T> modifier;

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
	 * function of RemoteWrappingModifier.
	 */
	private final FormatManager<?> sourceFormatManager;

	/**
	 * The FormatManager indicating the format of the return value for the target()
	 * function of RemoteWrappingModifier.
	 */
	private final FormatManager<?> targetFormatManager;

	/**
	 * Constructs a new RemoteWrappingModifier for the given FormulaModifier and Function.
	 * 
	 * @param modifier
	 *            The underlying FormulaModifier
	 * @param sourceValue
	 *            The value of the source() function
	 * @param sourceFormatManager
	 *            The FormatManager indicating the format of the return value for the
	 *            source() function
	 * @param targetValue
	 *            The value of the target() function
	 * @param targetFormatManager
	 *            The FormatManager indicating the format of the return value for the
	 *            target() function
	 */
	public RemoteWrappingModifier(FormulaModifier<T> modifier, VarScoped sourceValue,
		FormatManager<?> sourceFormatManager, VarScoped targetValue, FormatManager<?> targetFormatManager)
	{
		this.modifier = Objects.requireNonNull(modifier);
		this.sourceValue = Objects.requireNonNull(sourceValue);
		this.sourceFormatManager = Objects.requireNonNull(sourceFormatManager);
		this.targetValue = Objects.requireNonNull(targetValue);
		this.targetFormatManager = Objects.requireNonNull(targetFormatManager);
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
		functionManager = new RemoteWrappingLibrary(functionManager, sourceValue, sourceFormatManager, targetValue,
			targetFormatManager);
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
		return (31 * modifier.hashCode() + sourceValue.hashCode()) * 31 + targetValue.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RemoteWrappingModifier<?> other)
		{
			return modifier.equals(other.modifier) && sourceValue.equals(other.sourceValue)
				&& sourceFormatManager.equals(other.sourceFormatManager) && targetValue.equals(other.targetValue)
				&& targetFormatManager.equals(other.targetFormatManager);
		}
		return false;
	}
}
