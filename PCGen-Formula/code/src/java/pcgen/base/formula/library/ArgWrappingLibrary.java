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

import java.util.Objects;

import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.parse.Node;

/**
 * An ArgWrappingLibrary is a FunctionLibrary that contains two things: a
 * Default FunctionLibrary for most functions, and a specific values contained
 * by the arg(n) function.
 */
public class ArgWrappingLibrary implements FunctionLibrary
{
	/**
	 * The underlying FunctionLibrary of this ArgWrappingLibrary.
	 */
	private final FunctionLibrary functionLibrary;

	/**
	 * The arguments available to the arg(n) function.
	 */
	private final Node[] args;

	/**
	 * Constructs a new ArgWrappingLibrary with the given underlying
	 * FunctionLibrary and results to be used when the arg(n) function is
	 * called.
	 * 
	 * @param functionLibrary
	 *            The underlying FunctionLibrary of this ArgWrappingLibrary
	 * @param args
	 *            The arguments available to the arg(n) function
	 */
	public ArgWrappingLibrary(FunctionLibrary functionLibrary, Node[] args)
	{
		this.functionLibrary = Objects.requireNonNull(functionLibrary);
		this.args = Objects.requireNonNull(args);
	}

	/**
	 * Unsupported, since this is designed to be a dynamic FunctionLibrary.
	 */
	@Override
	public void addFunction(Function function)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Function getFunction(String functionName)
	{
		if (functionName.equalsIgnoreCase("arg"))
		{
			return new ArgFunction(args);
		}
		return functionLibrary.getFunction(functionName);
	}
}
