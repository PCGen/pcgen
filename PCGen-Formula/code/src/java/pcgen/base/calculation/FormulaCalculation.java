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
package pcgen.base.calculation;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.formula.library.ValueWrappingLibrary;

/**
 * A FormulaCalculation is an AbstractNEPCalculation that uses a NEPFormula for
 * the calculation of the result. This is then processed by a BasicCalculation
 * to get the final result.
 * 
 * This would be used in a place where a modification of this form was
 * performed: MODIFY:SomeVariable|ADD|MyVar+AnotherVar
 * 
 * The "MyVar+AnotherVar" would be the contents of the NEPFormula and the
 * BasicCalculation would perform an ADD.
 * 
 * @param <T>
 *            The format of objects on which this FormulaCalculation operates
 */
public final class FormulaCalculation<T> extends AbstractNEPCalculation<T>
{
	/**
	 * The underlying NEPFormula for which the result will be passed in to the
	 * BasicCalculation of this FormulaCalculation when it is processed.
	 */
	private final NEPFormula<T> formula;

	/**
	 * Constructs a new FormulaCalculation from the given object and
	 * BasicCalculation.
	 * 
	 * @param formula
	 *            The underlying NEPFormula for which the result will be passed
	 *            in to the BasicCalculation of this FormulaCalculation when it
	 *            is processed
	 * @param calc
	 *            The BasicCalculation which defines the operation to be
	 *            performed when this this FormulaCalculation is processed
	 */
	public FormulaCalculation(NEPFormula<T> formula, BasicCalculation<T> calc)
	{
		super(calc);
		if (formula == null)
		{
			throw new IllegalArgumentException("NEPFormula cannot be null");
		}
		this.formula = formula;
	}

	/**
	 * Processes the NEPFormula given at construction, and then passes the
	 * previous value and the result of the NEPFormula to the BasicCalculation
	 * provided at construction.
	 * 
	 * @see pcgen.base.calculation.NEPCalculation#process(java.lang.Object,
	 *      pcgen.base.formula.inst.ScopeInformation)
	 */
	@Override
	public T process(final T input, ScopeInformation scopeInfo)
	{
		FormulaManager fManager = scopeInfo.getFormulaManager();
		FunctionLibrary valueLibrary =
				new ValueWrappingLibrary(fManager.getLibrary(), input);
		FormulaManager withValue = fManager.swapFunctionLibrary(valueLibrary);
		ScopeInformation stepInfo =
				new ScopeInformation(withValue, scopeInfo.getScope());
		T resolved = formula.resolve(stepInfo);
		return getBasicCalculation().process(input, resolved);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getDependencies(ScopeInformation scopeInfo,
		DependencyManager fdm)
	{
		formula.getDependencies(scopeInfo, fdm);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInstructions()
	{
		return formula.toString();
	}
}
