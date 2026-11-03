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
package pcgen.base.formula.analysis;

import java.util.Optional;

import pcgen.base.util.TypedKey;

/**
 * An ArgumentDependencyManager is a class to capture uses of arg(x) in a
 * Formula. These are captured by the numeric value ("x" in the previous
 * example).
 */
public class ArgumentDependencyManager
{
	/**
	 * A TypedKey for use in storing an ArgumentDependencyManager.
	 */
	public static final TypedKey<Optional<ArgumentDependencyManager>> KEY =
			new TypedKey<>(Optional.empty());

	/**
	 * The maximum argument number encountered by this
	 * ArgumentDependencyManager. -1 indicates no arguments have been
	 * encountered.
	 */
	private int maxArgument = -1;

	/**
	 * Adds a called argument (by number) to the list of dependencies for a
	 * Formula.
	 * 
	 * @param argNumber
	 *            The number of the argument called through the arg function
	 */
	public void addArgument(int argNumber)
	{
		maxArgument = Math.max(maxArgument, argNumber);
	}

	/**
	 * Returns the maximum number of arguments required by arg(n) calls within a
	 * function. This will match the largest integer value n used in arg calls.
	 * If no arg(n) function was used, this will return -1.
	 * 
	 * @return the maximum number of arguments required by arg(n) calls within a
	 *         function; -1 if no arg function was called
	 */
	public int getMaximumArgument()
	{
		return maxArgument;
	}
}
