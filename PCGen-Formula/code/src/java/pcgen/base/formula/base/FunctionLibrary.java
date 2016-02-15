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
 * A FunctionLibrary is a container for Function objects. There are two forms of
 * Functions:
 * 
 * (Paren) Function: A paren function is a function that uses parenthesis () to
 * contain the arguments to the function. An example of this is
 * var("CL=Fighter")
 * 
 * "var" is the function, "CL=Fighter" is the (one) argument to the function
 * 
 * Bracket Function: A bracket function is a "built in" value that can be used
 * in a function. An example of this include (but are certainly not limited to)
 * COUNT[SKILLS] and COUNT[STATS]
 * 
 * Apologies to those outside the USA since the name of () and [] varies by
 * region.
 */
public interface FunctionLibrary
{

	/**
	 * Adds a function to the FunctionLibrary. This is used for functions that
	 * are followed by parenthesis ()
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to add
	 * null or attempts to add a function with a null name. An exception may be
	 * thrown (implementation dependent).
	 * 
	 * @param function
	 *            The function to be added to the FunctionLibrary
	 */
	public void addFunction(Function function);

	/**
	 * Returns the Function with the given function name. This will return a
	 * function that was added to the FunctionLibrary via the addFunction()
	 * method.
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to get
	 * a function with the name null. An exception may be thrown.
	 * 
	 * "null" is a legal return value if there is no function in the
	 * FunctionLibrary for the given function name.
	 * 
	 * @param functionName
	 *            The name of the Function to be returned.
	 * @return The Function with the given function name.
	 */
	public Function getFunction(String functionName);

	/**
	 * Adds a bracket function to the FunctionLibrary. This is used for
	 * functions that are followed by brackets []
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to add
	 * null or attempts to add a function with a null name. An exception may be
	 * thrown (implementation dependent).
	 * 
	 * @param function
	 *            The bracket function to be added to the FunctionLibrary
	 */
	public void addBracketFunction(Function function);

	/**
	 * Returns the Function with the given bracket function name. This will
	 * return a function that was added to the FunctionLibrary via the
	 * addBracketFunction() method.
	 * 
	 * FunctionLibrary does not define the behavior if an object attempts to get
	 * a bracket function with the name null. An exception may be thrown.
	 * 
	 * "null" is a legal return value if there is no bracket function in the
	 * FunctionLibrary for the given bracket function name.
	 * 
	 * @param functionName
	 *            The name of the bracket Function to be returned.
	 * @return The Function with the given bracket function name.
	 */
	public Function getBracketFunction(String functionName);

}
