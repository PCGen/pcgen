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
 * An ImplementedScopeManager can identify if it recognizes an ImplementedScope as being
 * part of a SolverSystem.
 * 
 * This is necessary as a VariableID is partially defined by the ImplementedScope; thus an
 * ImplementedScope is specific to a SolverSystem (and this provides that defensive
 * programming interface)
 */
@FunctionalInterface
public interface ImplementedScopeManager
{
	/**
	 * Returns true if the given ImplementedScope is recognized; false otherwise
	 * 
	 * @param scope
	 *            The ImplementedScope to be checked
	 * @return true if the given ImplementedScope is recognized; false otherwise
	 */
	public boolean recognizesScope(ImplementedScope scope);

}
