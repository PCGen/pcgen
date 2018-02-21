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

import java.util.Objects;

import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.util.CaseInsensitiveMap;

/**
 * SimpleFunctionLibrary is a simple implementation of the FunctionLibrary
 * interface.
 * 
 * This triggers exceptions if a null function or function with a null name is
 * added to the FunctionLibrary.
 * 
 * Note also that SimpleFunctionLibrary treats function names as
 * case-insensitive. Therefore, "Ceil" and "ceil" are identical functions.
 */
public class SimpleFunctionLibrary implements FunctionLibrary
{

	/**
	 * Stores the "paren functions" in this FunctionLibrary.
	 * 
	 * These are () functions for world-wide clarity :D
	 */
	private final CaseInsensitiveMap<Function> parenMap =
			new CaseInsensitiveMap<Function>();

	/**
	 * Adds a "paren" function to the SimpleFunctionLibrary.
	 * 
	 * A null Function or a function which returns null from getFunctionName()
	 * will both trigger an exception.
	 * 
	 * It is important that this method only be called once per Function name.
	 * If there is an attempt to add a second function with a name already
	 * matching a "paren" Function within the SimpleFunctionLibrary, then an
	 * exception will be thrown.
	 */
	@Override
	public void addFunction(Function function)
	{
		String functionName = function.getFunctionName();
		Objects.requireNonNull(functionName, "Cannot add Function with null name");
		if (parenMap.containsKey(functionName))
		{
			throw new IllegalArgumentException(
				"Cannot load two functions of name: " + functionName);
		}
		parenMap.put(functionName, function);
	}

	/**
	 * Returns the "paren" Function with the given function name (evaluated on a
	 * case-insensitive basis).
	 * 
	 * Per the contractual requirement of FunctionLibrary, will return null if
	 * no "paren" Function with the given function name is in the
	 * SimpleFunctionLibrary.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Function getFunction(String functionName)
	{
		return parenMap.get(functionName);
	}
}
