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
package pcgen.base.formula.base;

/**
 * A WriteableFunctionLibrary is a container for FormulaFunction objects that allows
 * additional Functions to be added to the library.
 */
public interface WriteableFunctionLibrary extends FunctionLibrary
{

	/**
	 * Adds a FormulaFunction to the FunctionLibrary.
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to add null or
	 * attempts to add a FormulaFunction with a null name. An exception may be thrown
	 * (implementation dependent).
	 * 
	 * @param function
	 *            The FormulaFunction to be added to the FunctionLibrary
	 */
	public void addFunction(FormulaFunction function);

}
