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

import java.util.Collection;
import java.util.List;

import pcgen.base.formula.base.ImplementedScope;

/**
 * A ScopeManager is a storage location for ImplementedScope used to track the children of
 * ImplementedScope objects.
 * 
 * Note: If a complete set of ImplementedScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 * 
 * Relationship between two ImplementedScope objects extends to this form of ambiguity: When
 * in a known scope, a variable name should be unique (regardless of being in the local
 * scope or implied from a parent scope).
 * 
 * If a variable name is defined for an existing parent or child (both recursively) of a
 * ImplementedScope, then adding that variable name to that ImplementedScope should be prohibited.
 * Otherwise, ambiguity (1) above would be violated.
 */
public interface ScopeManager
{
	/**
	 * Returns the ImplementedScope with the given name. If there was no ImplementedScope
	 * with the given name registered with this ScopeManager, then null
	 * will be returned.
	 * 
	 * @param name
	 *            The name of the ImplementedScope that should be returned
	 * @return The ImplementedScope with the given name
	 */
	public ImplementedScope getImplementedScope(String name);

	/**
	 * Returns a list of the child ImplementedScope objects registered with this
	 * ScopeManager for the given ImplementedScope.
	 * 
	 * @param scope
	 *            The ImplementedScope for which this ScopeManager should return the
	 *            registered children ImplementedScope objects.
	 * @return A list of the child ImplementedScope objects registered with this
	 *         ScopeManager for the given ImplementedScope
	 */
	public List<? extends ImplementedScope> getChildScopes(ImplementedScope scope);

	/**
	 * Returns a Collection of the ImplementedScope objects that have been registered with this
	 * ScopeManager.
	 * 
	 * @return A Collection of the ImplementedScope objects that have been registered with this
	 *         ScopeManager
	 */
	public Collection<? extends ImplementedScope> getImplementedScopes();

	/**
	 * Returns true if the ImplementedScope has been registered with this ScopeManager.
	 * 
	 * @return true if the ImplementedScope has been registered with this ScopeManager;
	 *         false otherwise
	 */
	public boolean recognizesScope(ImplementedScope scope);
	
	/**
	 * Returns true if two scopes are related. They are related if the presence of a
	 * matching variable name would produce an ambiguity (as described in the description
	 * of this interface).
	 * 
	 * @param firstScope
	 *            The first scope to be checked
	 * @param secondScope
	 *            The second scope to be checked
	 * @return true if the two ImplementedScope objects are related; false otherwise
	 */
	public boolean isRelated(ImplementedScope firstScope, ImplementedScope secondScope);
}
