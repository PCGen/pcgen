/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.HashMapToList;

/**
 * A LegalScopeLibrary is a storage location for LegalScope used to track the
 * children of LegalScope objects.
 * 
 * Note: If a complete set of LegalScope objects is not loaded (meaning some of
 * the parents are not themselves loaded), then certain behaviors (like
 * getScope) are not guaranteed to properly behave.
 */
public class LegalScopeLibrary
{

	//Enforce only one scope has no parent (a root exists?)

	/**
	 * The Map of LegalScope objects to their list of child LegalScope objects.
	 */
	private HashMapToList<LegalScope, LegalScope> scopeChildren =
			new HashMapToList<LegalScope, LegalScope>();

	/**
	 * A set of the LegalScope objects loaded in this LegalScopeLibrary. Note
	 * that this is distinct from the keys of scopeChildren, since only parents
	 * are loaded as keys.
	 */
	private CaseInsensitiveMap<LegalScope> scopes =
			new CaseInsensitiveMap<LegalScope>();

	/**
	 * Registers a LegalScope with this LegalScopeLibrary.
	 * 
	 * @param scope
	 *            The LegalScope to be registered with this LegalScopeLibrary
	 * @throws IllegalArgumentException
	 *             if the name of the given LegalScope is null or if there is a
	 *             previous LegalScope registered with the same name as the
	 *             given LegalScope
	 */
	public void registerScope(LegalScope scope)
	{
		String name = scope.getName();
		if (name == null)
		{
			throw new IllegalArgumentException("LegalScope must have a name");
		}
		if (name.indexOf('.') != -1)
		{
			throw new IllegalArgumentException("LegalScope name must not contain a period '.'");
		}
		String fullName = LegalScope.getFullName(scope);
		LegalScope current = scopes.get(fullName);
		if ((current != null) && !current.equals(scope))
		{
			throw new IllegalArgumentException("A Scope with name fully qualified name "
				+ fullName + " is already registered");
		}
		scopeChildren.addToListFor(scope.getParentScope(), scope);
		scopes.put(fullName, scope);
	}

	/**
	 * Returns a list of the child LegalScope objects registered with this
	 * LegalScopeLibrary for the given LegalScope.
	 * 
	 * @param scope
	 *            The LegalScope for which this LegalScopeLibrary should return
	 *            the registered children LegalScope objects.
	 * @return A list of the child LegalScope objects registered with this
	 *         LegalScopeLibrary for the given LegalScope
	 */
	public List<LegalScope> getChildScopes(LegalScope scope)
	{
		return scopeChildren.getListFor(Objects.requireNonNull(scope));
	}

	/**
	 * Returns the LegalScope with the given name. If there was no LegalScope
	 * with the given name registered with this LegalScopeLibrary, then null
	 * will be returned.
	 * 
	 * @param name
	 *            The name of the LegalScope that should be returned
	 * @return The LegalScope with the given name.
	 */
	public LegalScope getScope(String name)
	{
		return scopes.get(name);
	}

	/**
	 * Returns a Collection of the LegalScope objects that have been registered
	 * with this LegalScopeLibrary.
	 * 
	 * @return A Collection of the LegalScope objects that have been registered
	 *         with this LegalScopeLibrary
	 */
	public Collection<LegalScope> getLegalScopes()
	{
		return Collections.unmodifiableCollection(scopes.values());
	}
}
