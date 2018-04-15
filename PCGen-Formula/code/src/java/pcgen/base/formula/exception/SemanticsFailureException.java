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
package pcgen.base.formula.exception;

/**
 * A SemanticsFailureException is an Exception thrown while a FormulaSemantics is
 * processing to indicate a failure in the semantics rules of a formula.
 * 
 * This could mean an assertion of a Number was made, but no variable with an appropriate
 * name of format Number existed.
 */
public class SemanticsFailureException extends RuntimeException
{

	/**
	 * Constructs a new SemanticsFailureException with the given message and underlying
	 * cause.
	 * 
	 * @param message
	 *            The message indicating the semantics failure
	 * @param cause
	 *            The underlying Throwable causing a semantics failure
	 */
	public SemanticsFailureException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Constructs a new SemanticsFailureException with the given message.
	 * 
	 * @param message
	 *            The message indicating the semantics failure
	 */
	public SemanticsFailureException(String message)
	{
		super(message);
	}

}
