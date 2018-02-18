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
package pcgen.base.formula.base;

/**
 * A FunctionLibrary is a container for FormulaFunction objects.
 */
@FunctionalInterface
public interface FunctionLibrary
{

	/**
	 * Returns the FormulaFunction with the given name.
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to get a
	 * FormulaFunction with the name null. An exception may be thrown.
	 * 
	 * "null" is a legal return value if there is no FormulaFunction in the
	 * FunctionLibrary for the given name.
	 * 
	 * @param functionName
	 *            The name of the FormulaFunction to be returned.
	 * @return The FormulaFunction with the given name.
	 */
	public FormulaFunction getFunction(String functionName);

}
