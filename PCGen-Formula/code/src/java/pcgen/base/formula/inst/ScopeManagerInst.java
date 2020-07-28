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

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.HashMapToList;

/**
 * A ScopeManagerInst is a storage location for ImplementedScope used to track the children of
 * ImplementedScope objects.
 * 
 * Note: If a complete set of ImplementedScope objects is not loaded (meaning some of the
 * parents are not themselves loaded), then certain behaviors (like getScope) are not
 * guaranteed to properly behave.
 * 
 * Relationship between two DefinedScope objects extends to two forms of ambiguity: (1) When
 * in a known scope, a variable name should be unique (regardless of being in the local
 * scope or implied from a parent scope) (2) When in an unknown child scope, the presence
 * of a variable should imply the full parent scope hierarchy. Said another way, in any
 * given context of interpreting a variable name, there should always be one and only one
 * possible interpretation. Given potential overlaps, this can be a subtle problem. The
 * following defines some rules for when a relationship exists between two scopes, and
 * thus they can't share a variable.
 * 
 * If a variable name is defined for an existing parent or child (both recursively) of a
 * DefinedScope, then adding that variable name to that DefinedScope should be prohibited.
 * Otherwise, ambiguity (1) above would be violated.
 * 
 * If a variable name is defined for any relative of a peer scope, it should also fail.
 * A.C and B.C are peer scopes of C, therefore, if a variable is defined in A.C, then it
 * can never be defined in B, B.C, or B.D or it can be considered ambiguous. This
 * prohibition follows from ambiguity (2) above.
 * 
 * Note that this latter rule also creates interesting dependencies between global scopes.
 * If a variable E exists in scopes F and G, and there are peer subscopes F.H and G.H,
 * then any variable defined in F cannot be defined in G. This is because when that
 * variable name is encountered, in an object defined by H, that object should be able to
 * determine whether it is being interpreted in F.H or G.H. This also follows from (2)
 * above.
 */
public class ScopeManagerInst implements ScopeManager
{

	/**
	 * The Map of ImplementedScope objects to their list of child ImplementedScope objects.
	 */
	private final HashMapToList<ImplementedScope, ImplementedScope> scopeChildren =
			new HashMapToList<ImplementedScope, ImplementedScope>();

	/**
	 * A set of the ImplementedScope objects loaded in this ScopeManagerInst. Note that this is
	 * distinct from the keys of scopeChildren, since only parents are loaded as keys.
	 */
	private final CaseInsensitiveMap<ImplementedScope> scopes = new CaseInsensitiveMap<ImplementedScope>();

	/**
	 * Registers a ImplementedScope with this ScopeManager.
	 * 
	 * @param scope
	 *            The ImplementedScope to be registered with this ScopeManager
	 * @throws IllegalArgumentException
	 *             if the name of the given ImplementedScope is null or if there is a previous
	 *             ImplementedScope registered with the same name as the given ImplementedScope
	 */
	public void registerScope(ImplementedScope scope)
	{
		String name = scope.getName();
		Objects.requireNonNull(name, "ImplementedScope must have a name");
		if (name.indexOf('.') != -1)
		{
			throw new IllegalArgumentException(
				"ImplementedScope name must not contain a period '.'");
		}
		Optional<? extends ImplementedScope> parent = scope.getParentScope();
		if (parent.isPresent() && !recognizesScope(parent.get()))
		{
			throw new IllegalArgumentException(
				"Attempted to register Scope " + scope.getName() + " before parent scope "
					+ parent.get().getName() + " was registered");
		}
		String fullName = ImplementedScope.getFullName(scope);
		ImplementedScope current = scopes.get(fullName);
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
	public List<ImplementedScope> getChildScopes(ImplementedScope scope)
	{
		return scopeChildren.getListFor(Objects.requireNonNull(scope));
	}

	@Override
	public ImplementedScope getImplementedScope(String name)
	{
		return scopes.get(name);
	}

	@Override
	public Collection<ImplementedScope> getImplementedScopes()
	{
		return Collections.unmodifiableCollection(scopes.values());
	}

	@Override
	public boolean recognizesScope(ImplementedScope scope)
	{
		return scopes.values().contains(scope);
	}

	@Override
	public boolean isRelated(ImplementedScope scope1, ImplementedScope scope2)
	{
		Collection<ImplementedScope> descendents1 = getDescendents(scope1);
		descendents1.retainAll(getDescendents(scope2));
		return !descendents1.isEmpty();
	}

	private Collection<ImplementedScope> getDescendents(ImplementedScope scope)
	{
		Collection<ImplementedScope> descendents = new HashSet<ImplementedScope>();
		descendents.add(scope);
		accumulateDescendents(scope, descendents);
		return descendents;
	}

	private void accumulateDescendents(ImplementedScope scope,
		Collection<ImplementedScope> descendents)
	{
		scopeChildren.getSafeListFor(scope).stream().filter(descendents::add)
			.forEach(child -> accumulateDescendents(child, descendents));
	}
}
