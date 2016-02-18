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
package pcgen.base.calculation;

/**
 * A Modifier is designed to change an input (of a given format) to another
 * object of that format.
 * 
 * There is no requirement that a modifier take into account the input value (it
 * can be a "set").
 * 
 * Note that a Modifier is NOT intended to have side effects as it processes an
 * item.
 * 
 * @param <T>
 *            The format that this Modifier acts upon
 */
public interface Modifier<T> extends NEPCalculation<T>
{
	/**
	 * Returns the user priority of this Modifier. This is considered by a
	 * Solver when there are multiple Modifiers to be applied.
	 * 
	 * A lower priority is acted upon first.
	 * 
	 * The user priority is considered before the inherent priority of the
	 * Modifier.
	 * 
	 * @return The user priority of this Modifier.
	 */
	public int getUserPriority();

}
