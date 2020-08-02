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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import pcgen.base.formula.base.DefinedScope;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.graph.inst.DefaultDirectionalGraphEdge;
import pcgen.base.graph.inst.DirectionalSetMapGraph;
import pcgen.base.logging.Logging;
import pcgen.base.logging.Severity;

/**
 * A ScopeManagerInst is a storage location for DefinedScope used to track the children of
 * DefinedScope objects.
 * 
 * Note: If a complete set of DefinedScope objects is not loaded (meaning some of the
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

	private static final Object GLOBAL_PARENT = new Object();

	private DirectionalSetMapGraph<Object, DefaultDirectionalGraphEdge<Object>> scopeRelationships =
			new DirectionalSetMapGraph<>();

	private HashMap<DefinedScope, Object> virtualNodes = new HashMap<>();

	private HashMap<String, ImplementedScope> scopeCache = new HashMap<>();

	/*
	 * Note the use of DefinedScope rather than pure Strings allows two local scopes
	 * to both be called "Foo" as long as there is no relationship between the parent
	 * scopes.
	 */

	/**
	 * Create a new ScopeManagerInst
	 */
	public ScopeManagerInst()
	{
		scopeRelationships.addNode(GLOBAL_PARENT);
	}

	/**
	 * Registers a DefinedScope with this DefinedScopeManager.
	 * 
	 * @param scope
	 *            The DefinedScope to be registered with this DefinedScopeManager
	 * @throws IllegalArgumentException
	 *             if the name of the given DefinedScope is null or if there is a previous
	 *             DefinedScope registered with the same name as the given DefinedScope
	 */
	public void registerScope(DefinedScope scope)
	{
		registerScope(Optional.empty(), scope);
	}

	/**
	 * Registers a DefinedScope with this DefinedScopeManager. The given parent will be
	 * the parent scope of the DefinedScope to be registered.
	 * 
	 * @param parent
	 *            The parent DefinedScope of the given scope to be registered
	 * @param scope
	 *            The DefinedScope to be registered with this DefinedScopeManager
	 * @throws IllegalArgumentException
	 *             if the name of the given DefinedScope is null or if there is a previous
	 *             DefinedScope registered with the same name as the given DefinedScope or
	 *             if the parent DefinedScope has not yet been registered with this
	 *             ScopeManagerInst
	 */
	public void registerScope(DefinedScope parent, DefinedScope scope)
	{
		registerScope(Optional.of(parent), scope);
	}

	private void registerScope(Optional<DefinedScope> parent,
		DefinedScope scope)
	{
		String name = scope.getName();
		Objects.requireNonNull(name, "DefinedScope must have a name");
		//TODO Make Period a parameter?
		if (name.indexOf('.') != -1)
		{
			throw new IllegalArgumentException(
				"DefinedScope name must not contain a period '.'");
		}
		Object parentVirtualNode;
		if (parent.isPresent())
		{
			if (!recognizesScope(parent.get()))
			{
				throw new IllegalArgumentException(
					"Attempted to register Scope " + scope.getName()
						+ " before parent scope " + parent.get().getName()
						+ " was registered");
			}
			parentVirtualNode = virtualNodes.get(parent.get());
		}
		else
		{
			parentVirtualNode = GLOBAL_PARENT;
		}
		Stream<Object> children = getDownScopeStream(parentVirtualNode);
		Optional<DefinedScope> duplicate =
				children.map(child -> (DefinedScope) child)
					.filter(child -> child.getName().equals(name)).findFirst();
		if (duplicate.isPresent())
		{
			DefinedScope dupe = duplicate.get();
			String message = parent.isPresent()
				? " for parent " + parent.get().getName() : " as global scope";
			if (scope.equals(dupe))
			{
				Logging.log(Severity.WARNING,
					() -> "Duplicate attempt to register scope " + name
						+ message);
				return;
			}
			else
			{
				throw new IllegalArgumentException(
					"Attempted to register a second sub-scope named " + name
						+ message);
			}

		}
		//connect up
		scopeRelationships.addNode(scope);
		scopeRelationships.addEdge(
			new DefaultDirectionalGraphEdge<>(parentVirtualNode, scope));

		//connect down
		Object virtualNode = getVirtualNode(scope);
		scopeRelationships.addNode(virtualNode);
		scopeRelationships
			.addEdge(new DefaultDirectionalGraphEdge<>(scope, virtualNode));
	}

	private Object getVirtualNode(DefinedScope scope)
	{
		Object o = virtualNodes.get(scope);
		if (o == null)
		{
			o = new Object();
			virtualNodes.put(scope, o);
		}
		return o;
	}

	@Override
	public boolean isRelated(DefinedScope scope1, DefinedScope scope2)
	{
		Collection<Object> descendents1 = getDescendents(scope1);
		descendents1.retainAll(getDescendents(scope2));
		return !descendents1.isEmpty();
	}

	private Collection<Object> getDescendents(DefinedScope scope)
	{
		Collection<Object> descendents = new HashSet<Object>();
		accumDescendents(scope, descendents);
		return descendents;
	}

	private void accumDescendents(Object obj, Collection<Object> descendents)
	{
		Stream<Object> children = getDownScopeStream(obj);
		children.filter(child -> descendents.add(child))
			.forEach(child -> accumDescendents(child, descendents));
	}

	private Stream<Object> getDownScopeStream(Object node)
	{
		Set<DefaultDirectionalGraphEdge<Object>> edges = scopeRelationships
			.getAdjacentEdges(Objects.requireNonNull(node));
		return edges.stream().filter(edge -> edge.isSource(node))
			.map(edge -> edge.getNodeAt(1));
	}

	@Override
	public ImplementedScope getImplementedScope(String name)
	{
		ImplementedScope cached = scopeCache.get(name);
		if (cached != null)
		{
			return cached;
		}
		int dotLoc = name.lastIndexOf('.');
		Optional<ImplementedScope> parent;
		Object parentVirtualNode;

		if (dotLoc == -1)
		{
			parent = Optional.empty();
			parentVirtualNode = GLOBAL_PARENT;
		}
		else
		{
			ImplementedScope implScope =
					getImplementedScope(name.substring(0, dotLoc));
			parent = Optional.of(implScope);
			parentVirtualNode = virtualNodes.get(implScope.getDefinedScope());
		}
		String localName = name.substring(dotLoc + 1);
		Optional<DefinedScope> localDefined =
				getDownScopeStream(parentVirtualNode)
					.map(child -> (DefinedScope) child)
					.filter(scope -> localName.equals(scope.getName()))
					.findFirst();
		if (localDefined.isEmpty())
		{
			String message = parent.isPresent()
				? " for parent " + parent.get().getName() : " as global scope";
			throw new IllegalArgumentException(
				"Cannot find DefinedScope " + localName + message);
		}
		SimpleImplementedScope scope =
				new SimpleImplementedScope(parent, localDefined.get());
		scopeCache.put(name, scope);
		return scope;
	}

	public boolean recognizesScope(DefinedScope scope)
	{
		return scopeRelationships.containsNode(scope);
	}

	@Override
	public boolean recognizesScope(ImplementedScope scope)
	{
		return scopeCache.containsValue(scope);
	}

	public void addPseudoRelationship(DefinedScope underlying,
		DefinedScope expressed)
	{
		scopeRelationships.addEdge(new DefaultDirectionalGraphEdge<>(expressed,
			virtualNodes.get(underlying)));
		//TODO Need to make sure this doesn't retroactively make things related :/
	}
}
