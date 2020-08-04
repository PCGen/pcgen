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

import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ImplementedScopeManager;
import pcgen.base.formula.base.RelationshipManager;
import pcgen.base.formula.base.ScopeImplementer;

/**
 * A ImplementedScopeLibrary is a storage location for ImplementedScope used to track the
 * children of ImplementedScope objects.
 * 
 * Note: If a complete set of initialized ImplementedScope objects is not loaded (meaning
 * some of the parents are not themselves loaded), then certain behaviors (like isRelated)
 * are not guaranteed to properly behave.
 */
public class ImplementedScopeLibrary implements ScopeImplementer,
		RelationshipManager, ImplementedScopeManager
{

	/**
	 * The cache of ImplementedScope objects, stored by their fully qualified name.
	 */
	private final Map<String, ImplementedScope> scopeCache = new HashMap<>();
	
	/**
	 * Adds an ImplementedScope to this ImplementedScopeLibrary.
	 * 
	 * @param scope
	 *            The ImplementedScope to be added to this ImplementedScopeLibrary
	 */
	public void addScope(ImplementedScope scope)
	{
		scopeCache.put(ImplementedScope.getFullName(scope), scope);
	}

	@Override
	public boolean recognizesScope(ImplementedScope scope)
	{
		return scopeCache.containsValue(scope);
	}

	@Override
	public ImplementedScope getImplementedScope(String scopeName)
	{
		return scopeCache.get(scopeName);
	}

	@Override
	public boolean isRelated(ImplementedScope scope1, ImplementedScope scope2)
	{
		return scope1.drawsFrom().contains(scope2)
			|| scope2.drawsFrom().contains(scope1);
	}

}
