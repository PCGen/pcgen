/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * VariableSolver is a FunctionalInterface that can solve a variable represented by a
 * VariableID.
 */
@FunctionalInterface
public interface VariableSolver
{

	/**
	 * Solves the given VariableID. Returns true if the value of the VariableID changed;
	 * false otherwise.
	 * 
	 * @param startingID
	 *            The VariableID that this VariableSolver should solve
	 * @return true if the value of the VariableID changed when it was solved; false
	 *         otherwise
	 */
	public boolean solve(VariableID<?> startingID);

}
