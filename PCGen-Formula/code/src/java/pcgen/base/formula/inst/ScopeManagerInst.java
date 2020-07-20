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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.HashMapToList;

/**
 * A ScopeManagerInst is a storage location for LegalScope used to track the children of
 * LegalScope objects.
 * 
 * Note: If a complete set of LegalScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 */
public class ScopeManagerInst implements LegalScopeManager
{

	/**
	 * The Map of LegalScope objects to their list of child LegalScope objects.
	 */
	private final HashMapToList<LegalScope, LegalScope> scopeChildren =
			new HashMapToList<LegalScope, LegalScope>();

	/**
	 * A set of the LegalScope objects loaded in this ScopeManagerInst. Note that this is
	 * distinct from the keys of scopeChildren, since only parents are loaded as keys.
	 */
	private final CaseInsensitiveMap<LegalScope> scopes = new CaseInsensitiveMap<LegalScope>();

	/**
	 * Registers a LegalScope with this LegalScopeManager.
	 * 
	 * @param scope
	 *            The LegalScope to be registered with this LegalScopeManager
	 * @throws IllegalArgumentException
	 *             if the name of the given LegalScope is null or if there is a previous
	 *             LegalScope registered with the same name as the given LegalScope
	 */
	public void registerScope(LegalScope scope)
	{
		String name = scope.getName();
		Objects.requireNonNull(name, "LegalScope must have a name");
		if (name.indexOf('.') != -1)
		{
			throw new IllegalArgumentException(
				"LegalScope name must not contain a period '.'");
		}
		Optional<? extends LegalScope> parent = scope.getParentScope();
		if (parent.isPresent() && !recognizesScope(parent.get()))
		{
			throw new IllegalArgumentException(
				"Attempted to register Scope " + scope.getName() + " before parent scope "
					+ parent.get().getName() + " was registered");
		}
		String fullName = LegalScope.getFullName(scope);
		LegalScope current = scopes.get(fullName);
		if ((current != null) && !current.equals(scope))
		{
			throw new IllegalArgumentException("A Scope with name fully qualified name "
				+ fullName + " is already registered");
		}
		if (parent.isPresent())
		{
			scopeChildren.addToListFor(parent.get(), scope);
		}
		scopes.put(fullName, scope);
	}

	@Override
	public List<LegalScope> getChildScopes(LegalScope scope)
	{
		return scopeChildren.getListFor(Objects.requireNonNull(scope));
	}

	@Override
	public LegalScope getScope(String name)
	{
		return scopes.get(name);
	}

	@Override
	public Collection<LegalScope> getLegalScopes()
	{
		return Collections.unmodifiableCollection(scopes.values());
	}

	@Override
	public boolean recognizesScope(LegalScope legalScope)
	{
		return scopes.values().contains(legalScope);
	}

	@Override
	public boolean isRelated(LegalScope scope1, LegalScope scope2)
	{
		Collection<LegalScope> descendents1 = getDescendents(scope1);
		descendents1.retainAll(getDescendents(scope2));
		return !descendents1.isEmpty();
	}

	private Collection<LegalScope> getDescendents(LegalScope scope)
	{
		Collection<LegalScope> descendents = new HashSet<LegalScope>();
		descendents.add(scope);
		accumulateDescendents(scope, descendents);
		return descendents;
	}

	private void accumulateDescendents(LegalScope scope,
		Collection<LegalScope> descendents)
	{
		scopeChildren.getSafeListFor(scope).stream().filter(descendents::add)
			.forEach(child -> accumulateDescendents(child, descendents));
	}
}
