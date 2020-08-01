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

import pcgen.base.formula.base.LegalScope;

/**
 * A LegalScopeManager is a storage location for LegalScope used to track the children of
 * LegalScope objects.
 * 
 * Note: If a complete set of LegalScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 * 
 * Relationship between two LegalScope objects extends to this form of ambiguity: When
 * in a known scope, a variable name should be unique (regardless of being in the local
 * scope or implied from a parent scope).
 * 
 * If a variable name is defined for an existing parent or child (both recursively) of a
 * LegalScope, then adding that variable name to that LegalScope should be prohibited.
 * Otherwise, ambiguity (1) above would be violated.
 */
public interface LegalScopeManager
{
	/**
	 * Returns the LegalScope with the given name. If there was no LegalScope
	 * with the given name registered with this LegalScopeManager, then null
	 * will be returned.
	 * 
	 * @param name
	 *            The name of the LegalScope that should be returned
	 * @return The LegalScope with the given name
	 */
	public LegalScope getScope(String name);

	/**
	 * Returns a list of the child LegalScope objects registered with this
	 * LegalScopeManager for the given LegalScope.
	 * 
	 * @param scope
	 *            The LegalScope for which this LegalScopeManager should return the
	 *            registered children LegalScope objects.
	 * @return A list of the child LegalScope objects registered with this
	 *         LegalScopeManager for the given LegalScope
	 */
	public List<? extends LegalScope> getChildScopes(LegalScope scope);

	/**
	 * Returns a Collection of the LegalScope objects that have been registered with this
	 * LegalScopeManager.
	 * 
	 * @return A Collection of the LegalScope objects that have been registered with this
	 *         LegalScopeManager
	 */
	public Collection<? extends LegalScope> getLegalScopes();

	/**
	 * Returns true if the LegalScope has been registered with this LegalScopeManager.
	 * 
	 * @param legalScope
	 *            The LegalScope to be checked to see if it is recognized by this
	 *            LegalScopeManager
	 * @return true if the LegalScope has been registered with this LegalScopeManager;
	 *         false otherwise
	 */
	public boolean recognizesScope(LegalScope legalScope);
	
	/**
	 * Returns true if two scopes are related. They are related if the presence of a
	 * matching variable name would produce an ambiguity (as described in the description
	 * of this interface).
	 * 
	 * @param firstScope
	 *            The first scope to be checked
	 * @param secondScope
	 *            The second scope to be checked
	 * @return true if the two LegalScope objects are related; false otherwise
	 */
	public boolean isRelated(LegalScope firstScope, LegalScope secondScope);
}
