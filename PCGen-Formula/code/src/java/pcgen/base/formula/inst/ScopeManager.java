/*
 * Copyright 2015-20 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import pcgen.base.formula.base.ImplementedScope;

/**
 * A ScopeManager is a storage location for DefinedScope used to track the children of
 * DefinedScope objects.
 * 
 * Note: If a complete set of DefinedScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 */
public interface ScopeManager
{
	/**
	 * Returns true if the ImplementedScope is recognized by this ScopeManager.
	 * 
	 * @param implScope the ImplementedScope to be checked
	 * @return true if the ImplementedScope is recognized by this ScopeManager;
	 *         false otherwise
	 */
	public boolean recognizesScope(ImplementedScope implScope);

	/**
	 * Returns the ImplementedScope with the given fully resolved name.
	 * 
	 * @param name
	 *            The name of the ImplementedScope to be returned
	 * @return The ImplementedScope with the given fully resolved name
	 */
	public ImplementedScope getImplementedScope(String name);
}
