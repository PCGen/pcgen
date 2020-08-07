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
package pcgen.base.formula.inst;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;

/**
 * A SimpleScopeInstanceFactory is a factory used to instantiate ScopeInstance objects
 * given a parent ScopeInstance and ImplementedScope in which to instantiate the
 * ScopeInstance.
 */
public class SimpleScopeInstanceFactory implements ScopeInstanceFactory
{

	/**
	 * Contains a map from the owning VarScoped object to the ScopeInstance for
	 * that object.
	 */
	private final Map<VarScoped, ScopeInstance> objectToInstanceCache =
			new HashMap<>();

	/**
	 * Contains a map from a format to the global ScopeInstance for that format.
	 */
	private final Map<String, ScopeInstance> globals = new HashMap<>();

	/**
	 * The ScopeManager used to indicate the ImplementedScope objects for this
	 * SimpleScopeInstanceFactory.
	 */
	private final ScopeImplementer manager;

	/**
	 * Construct a new SimpleScopeInstanceFactory with the underlying
	 * ScopeManager.
	 * 
	 * @param manager
	 *            The ScopeManager indicating the legal scopes for this
	 *            SimpleScopeInstanceFactory
	 */
	public SimpleScopeInstanceFactory(ScopeImplementer manager)
	{
		this.manager = Objects.requireNonNull(manager);
	}

	@Override
	public ScopeInstance getGlobalInstance(String scopeName)
	{
		ImplementedScope scope = manager.getImplementedScope(scopeName);
		if (scope == null)
		{
			throw new IllegalArgumentException(
				"Cannot find Scope named: " + scopeName);
		}
		return getGlobalInstance(scope);
	}

	/*
	 * This is private so we know the ImplementedScope came from the contained
	 * ScopeManager.
	 */
	private ScopeInstance getGlobalInstance(ImplementedScope scope)
	{
		if (scope.getParentScope().isPresent())
		{
			throw new IllegalArgumentException(
				"Cannot build Global Scope for a ImplementedScope that has a parent");
		}
		String name = scope.getName();
		ScopeInstance inst = globals.get(name);
		if (inst == null)
		{
			inst = new SimpleScopeInstance(Optional.empty(), scope,
				new GlobalVarScoped(scope.getName()));
			globals.put(name, inst);
		}
		return inst;
	}

	@Override
	public ScopeInstance get(String scopeName, Optional<VarScoped> obj)
	{
		ImplementedScope scope = manager.getImplementedScope(scopeName);
		return getMessaged(scope, obj, obj);
	}

	/**
	 * Actually processes the result of a get, while preserving the original
	 * VarScoped object to make any message better for the end user.
	 * 
	 * Private so that we know the ImplementedScope came from the ScopeManager of
	 * this SimpleScopeInstanceFactory.
	 */
	private ScopeInstance getMessaged(ImplementedScope instScope, Optional<VarScoped> current,
		Optional<VarScoped> original)
	{
		Optional<? extends ImplementedScope> potentialParentScope = instScope.getParentScope();
		//Empty is Global object
		if (current.isEmpty())
		{
			//is instScope Global?
			if (potentialParentScope.isEmpty())
			{
				return getGlobalInstance(instScope);
			}
			if (original.isEmpty())
			{
				//Started with a global assertion
				throw new IllegalArgumentException(
					"Requested ScopeInstance for Global object, "
						+ "but with ImplementedScope that was not Global: "
						+ instScope.getName());
			}
			else
			{
				//Reached, but did not start with, a global assertion
				throw new IllegalArgumentException(
					"Requested ScopeInstance for " + original.getClass().getName() + " "
						+ original.get().getKeyName() + " and reached a global parent, "
						+ "but have only reached Scope: " + instScope.getName());
			}
		}
		VarScoped currentVarScoped = current.get();
		Optional<VarScoped> parentObj = currentVarScoped.getVariableParent();
		Optional<String> scopeName = currentVarScoped.getScopeName();
		if (scopeName.isEmpty())
		{
			/*
			 * Some object may not have a scope, so fall up: get the parent and
			 * check the local variable scope of the parent.
			 */
			return getMessaged(instScope, parentObj, original);
		}
		ImplementedScope currentScope = manager.getImplementedScope(scopeName.get());
		if (!currentScope.equals(instScope))
		{
			/*
			 * We could be in a sub-scope and the instScope is really for a parent. If the
			 * scopes don't match, fall "up" until a matching object or failure.
			 */
			return getMessaged(instScope, parentObj, original);
		}
		//At this point, it really *is* for currentVarScoped
		ScopeInstance inst = objectToInstanceCache.get(currentVarScoped);
		if (inst == null)
		{
			//Need to build the scope...
			ScopeInstance parentInstance =
					getMessaged(potentialParentScope.get(), parentObj, original);
			inst = new SimpleScopeInstance(Optional.of(parentInstance),
				currentScope, original.get());
			objectToInstanceCache.put(currentVarScoped, inst);
		}
		return inst;
	}

	/**
	 * Returns a Collection of the VarScoped objects for which this
	 * SimpleScopeInstanceFactory has built a ScopeInstance.
	 * 
	 * @return A Collection of the VarScoped objects for which this
	 *         SimpleScopeInstanceFactory has built a ScopeInstance
	 */
	public Collection<VarScoped> getInstancedObjects()
	{
		return Collections
			.unmodifiableCollection(objectToInstanceCache.keySet());
	}
}
