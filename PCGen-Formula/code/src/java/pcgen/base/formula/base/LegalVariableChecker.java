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
 * LegalVariableChecker is a FunctionalInterface that can check if an ImplementedScope and
 * variable name represent a legal variable.
 */
@FunctionalInterface
public interface LegalVariableChecker
{

	/**
	 * Returns true if the given ImplementedScope and variable name are a legal combination.
	 * 
	 * @param scope
	 *            The ImplementedScope to be used to determine if the given combination is legal
	 * @param varName
	 *            The variable name to be used to determine if the given combination is
	 *            legal
	 * 
	 * @return true if the given ImplementedScope and variable name are a legal combination;
	 *         false otherwise
	 */
	public boolean isLegalVariable(ImplementedScope scope, String varName);

}
