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

import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.util.CaseInsensitiveMap;

/**
 * SimpleFunctionLibrary is a simple implementation of the FunctionLibrary interface.
 * 
 * This triggers exceptions if a null FormulaFunction or FormulaFunction with a null name
 * is added to the FunctionLibrary.
 * 
 * Note also that SimpleFunctionLibrary treats FormulaFunction names as case-insensitive.
 * Therefore, "Ceil" and "ceil" are identical functions.
 */
public class SimpleFunctionLibrary implements WriteableFunctionLibrary
{

	/**
	 * Stores the FormulaFunction objects in this FunctionLibrary.
	 */
	private final CaseInsensitiveMap<FormulaFunction> functionMap =
			new CaseInsensitiveMap<FormulaFunction>();

	/**
	 * Adds a FormulaFunction to the SimpleFunctionLibrary.
	 * 
	 * A null FormulaFunction or a FormulaFunction which returns null from
	 * getFunctionName() will both trigger an exception.
	 * 
	 * It is important that this method only be called once per FormulaFunction name. If
	 * there is an attempt to add a second FormulaFunction with a name already matching a
	 * FormulaFunction within the SimpleFunctionLibrary, then an exception will be thrown.
	 */
	@Override
	public void addFunction(FormulaFunction function)
	{
		String functionName = function.getFunctionName();
		Objects.requireNonNull(functionName, "Cannot add Function with null name");
		if (functionMap.containsKey(functionName))
		{
			throw new IllegalArgumentException(
				"Cannot load two functions of name: " + functionName);
		}
		functionMap.put(functionName, function);
	}

	/**
	 * Returns the FormulaFunction with the given name (evaluated on a case-insensitive
	 * basis).
	 * 
	 * Per the contractual requirement of FunctionLibrary, will return null if no
	 * FormulaFunction with the given name is in the SimpleFunctionLibrary.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public FormulaFunction getFunction(String functionName)
	{
		return functionMap.get(functionName);
	}
}
