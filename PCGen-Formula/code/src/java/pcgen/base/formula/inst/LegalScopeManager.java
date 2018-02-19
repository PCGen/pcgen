/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.base.formula.base.LegalScopeLibrary;

/**
 * A LegalScopeManager is a storage location for LegalScope used to track the children of
 * LegalScope objects.
 * 
 * Note: If a complete set of LegalScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 */
public interface LegalScopeManager extends LegalScopeLibrary
{
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
	 * @return true if the LegalScope has been registered with this LegalScopeManager;
	 *         false otherwise
	 */
	public boolean recognizesScope(LegalScope legalScope);
}
