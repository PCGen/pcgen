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
package pcgen.base.formula.inst;

import java.util.Objects;

import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.function.DefinedFunction;
import pcgen.base.util.FormatManager;

/**
 * An DefinedWrappingLibrary is a FunctionLibrary that contains two things: a Default
 * FunctionLibrary for most functions, and a specific value contained by the a function of
 * a specified name.
 */
public class DefinedWrappingLibrary implements FunctionLibrary
{
	/**
	 * The underlying FunctionLibrary of this DefinedWrappingLibrary.
	 */
	private final FunctionLibrary functionLibrary;

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
	 * function of DefinedWrappingLibrary.
	 */
	private final FormatManager<?> formatManager;

	/**
	 * Constructs a new DefinedWrappingLibrary with the given underlying FunctionLibrary
	 * and value to be used when the defined function is called.
	 * 
	 * @param functionLibrary
	 *            The underlying FunctionLibrary of this DefinedWrappingLibrary
	 * @param definedName
	 *            The name of the defined function
	 * @param definedValue
	 *            The value of the defined function
	 * @param formatManager
	 *            The FormatManager indicating the format of the return value for this
	 *            DefinedWrappingLibrary
	 */
	public DefinedWrappingLibrary(FunctionLibrary functionLibrary,
		String definedName, Object definedValue, FormatManager<?> formatManager)
	{
		this.functionLibrary = Objects.requireNonNull(functionLibrary);
		this.definedName = Objects.requireNonNull(definedName);
		this.definedValue = Objects.requireNonNull(definedValue);
		this.formatManager = Objects.requireNonNull(formatManager);
	}

	@Override
	public FormulaFunction getFunction(String functionName)
	{
		if (functionName.equalsIgnoreCase(definedName))
		{
			return new DefinedFunction(definedName, definedValue,
				formatManager);
		}
		return functionLibrary.getFunction(functionName);
	}
}
