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
package pcgen.base.formula.inst;

import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.function.AbsFunction;
import pcgen.base.formula.function.CeilFunction;
import pcgen.base.formula.function.FloorFunction;
import pcgen.base.formula.function.GetOptionalFunction;
import pcgen.base.formula.function.IfFunction;
import pcgen.base.formula.function.IsEmptyFunction;
import pcgen.base.formula.function.IsPresentFunction;
import pcgen.base.formula.function.LengthFunction;
import pcgen.base.formula.function.MaxFunction;
import pcgen.base.formula.function.MinFunction;
import pcgen.base.formula.function.RoundFunction;
import pcgen.base.formula.function.SliceFunction;
import pcgen.base.formula.function.ValueFunction;
import pcgen.base.formula.operator.array.ArrayAdd;
import pcgen.base.formula.operator.array.ArrayEquals;
import pcgen.base.formula.operator.array.ArrayNotEqual;
import pcgen.base.formula.operator.array.ArraySubtract;
import pcgen.base.formula.operator.array.ArraySubtractInstance;
import pcgen.base.formula.operator.bool.BooleanAnd;
import pcgen.base.formula.operator.bool.BooleanNot;
import pcgen.base.formula.operator.bool.BooleanOr;
import pcgen.base.formula.operator.generic.GenericEquals;
import pcgen.base.formula.operator.generic.GenericNotEqual;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.formula.operator.number.NumberDivide;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberExp;
import pcgen.base.formula.operator.number.NumberGreaterThan;
import pcgen.base.formula.operator.number.NumberGreaterThanOrEqualTo;
import pcgen.base.formula.operator.number.NumberLessThan;
import pcgen.base.formula.operator.number.NumberLessThanOrEqualTo;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.operator.number.NumberMultiply;
import pcgen.base.formula.operator.number.NumberNotEqual;
import pcgen.base.formula.operator.number.NumberRemainder;
import pcgen.base.formula.operator.number.NumberSubtract;
import pcgen.base.formula.operator.string.StringAdd;

/**
 * FormulaUtilities are a general set of utilities for dealing with Formulas.
 * This generally assists with loading libraries with "built-in" capabilities.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public final class FormulaUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private FormulaUtilities()
	{
	}

	/**
	 * Load the "built-in" functions into the given WriteableFunctionLibrary.
	 * 
	 * @param functionLib
	 *            The WriteableFunctionLibrary to which the built in functions should be
	 *            added.
	 * @return The given FunctionLibrary
	 */
	public static WriteableFunctionLibrary loadBuiltInFunctions(
		WriteableFunctionLibrary functionLib)
	{
		functionLib.addFunction(new AbsFunction());
		functionLib.addFunction(new CeilFunction());
		functionLib.addFunction(new FloorFunction());
		functionLib.addFunction(new GetOptionalFunction());
		functionLib.addFunction(new IfFunction());
		functionLib.addFunction(new IsEmptyFunction());
		functionLib.addFunction(new IsPresentFunction());
		functionLib.addFunction(new LengthFunction());
		functionLib.addFunction(new MaxFunction());
		functionLib.addFunction(new MinFunction());
		functionLib.addFunction(new RoundFunction());
		functionLib.addFunction(new SliceFunction());
		functionLib.addFunction(new ValueFunction());
		return functionLib;
	}

	/**
	 * Load the "built-in" operators into the given OperatorLibrary.
	 * 
	 * @param opLib
	 *            The OperatorLibrary to which the built in operators should be added.
	 * @return The given OperatorLibrary
	 */
	public static OperatorLibrary loadBuiltInOperators(OperatorLibrary opLib)
	{
		opLib.addAction(new GenericEquals());
		opLib.addAction(new GenericNotEqual());
		opLib.addAction(new ArrayAdd());
		opLib.addAction(new ArrayEquals());
		opLib.addAction(new ArrayNotEqual());
		opLib.addAction(new ArraySubtract());
		opLib.addAction(new ArraySubtractInstance());
		opLib.addAction(new BooleanAnd());
		opLib.addAction(new BooleanNot());
		opLib.addAction(new BooleanOr());
		opLib.addAction(new NumberAdd());
		opLib.addAction(new NumberDivide());
		opLib.addAction(new NumberEquals());
		opLib.addAction(new NumberExp());
		opLib.addAction(new NumberGreaterThan());
		opLib.addAction(new NumberGreaterThanOrEqualTo());
		opLib.addAction(new NumberLessThan());
		opLib.addAction(new NumberLessThanOrEqualTo());
		opLib.addAction(new NumberMinus());
		opLib.addAction(new NumberMultiply());
		opLib.addAction(new NumberNotEqual());
		opLib.addAction(new NumberRemainder());
		opLib.addAction(new NumberSubtract());
		opLib.addAction(new StringAdd());
		return opLib;
	}

}
