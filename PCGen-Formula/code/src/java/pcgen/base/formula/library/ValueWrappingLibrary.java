/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.library;

import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;

/**
 * A ValueWrappingLibrary is a FunctionLibrary that contains two things: a
 * Default FunctionLibrary for most functions, and a specific value contained by
 * the function "value".
 */
public class ValueWrappingLibrary implements FunctionLibrary
{
	/**
	 * The underlying FunctionLibrary of this ValueWrappingLibrary.
	 */
	private final FunctionLibrary functionLibrary;

	/**
	 * The result of calling the value() function.
	 */
	private final Object valueResult;

	/**
	 * Constructs a new ValueWrappingLibrary with the given underlying
	 * FunctionLibrary and result to be used when the value() function is
	 * called.
	 * 
	 * @param functionLibrary
	 *            The underlying FunctionLibrary of this ValueWrappingLibrary
	 * @param valueResult
	 *            The value to be returned when the value() function is called
	 */
	public ValueWrappingLibrary(FunctionLibrary functionLibrary,
		Object valueResult)
	{
		this.functionLibrary = functionLibrary;
		this.valueResult = valueResult;
	}

	/**
	 * Unsupported, since this is designed to be a dynamic FunctionLibrary.
	 */
	@Override
	public void addFunction(Function function)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function getFunction(String functionName)
	{
		if (functionName.equalsIgnoreCase("value"))
		{
			return new ValueFunction(valueResult);
		}
		return functionLibrary.getFunction(functionName);
	}

	/**
	 * Unsupported, since this is designed to be a dynamic FunctionLibrary.
	 */
	@Override
	public void addBracketFunction(Function function)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function getBracketFunction(String functionName)
	{
		return functionLibrary.getBracketFunction(functionName);
	}
}
