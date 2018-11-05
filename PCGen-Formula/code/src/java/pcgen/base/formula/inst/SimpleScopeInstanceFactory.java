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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.util.DoubleKeyMapToList;

/**
 * A SimpleScopeInstanceFactory is a factory used to instantiate ScopeInstance objects
 * given a parent ScopeInstance and LegalScope in which to instantiate the
 * ScopeInstance.
 */
public class SimpleScopeInstanceFactory implements ScopeInstanceFactory
{

	/**
	 * Contains the map of parent ScopeInstance objects and LegalScope objects
	 * to the instantiated "children" ScopeInstance objects within the provided
	 * LegalScope.
	 */
	private final DoubleKeyMapToList<ScopeInstance, LegalScope, ScopeInstance> scopeInstances =
			new DoubleKeyMapToList<ScopeInstance, LegalScope, ScopeInstance>();

	/**
	 * Contains a map from the owning VarScoped object to the ScopeInstance for
	 * that object.
	 */
	private final Map<VarScoped, ScopeInstance> objectToInstanceCache =
			new HashMap<VarScoped, ScopeInstance>();

	/**
	 * Contains a map from a format to the global ScopeInstance for that format.
	 */
	private final Map<String, ScopeInstance> globals =
			new HashMap<String, ScopeInstance>();

	/**
	 * The LegalScopeLibrary used to indicate the LegalScope objects for this
	 * SimpleScopeInstanceFactory.
	 */
	private final LegalScopeLibrary library;

	/**
	 * Construct a new SimpleScopeInstanceFactory with the underlying
	 * LegalScopeLibrary.
	 * 
	 * @param library
	 *            The LegalScopeLibrary indicating the legal scopes for this
	 *            SimpleScopeInstanceFactory
	 */
	public SimpleScopeInstanceFactory(LegalScopeLibrary library)
	{
		this.library = Objects.requireNonNull(library);
	}

	@Override
	public ScopeInstance getGlobalInstance(String scopeName)
	{
		LegalScope legalScope = library.getScope(scopeName);
		if (legalScope == null)
		{
			throw new IllegalArgumentException(
				"Cannot find Scope named: " + scopeName);
		}
		return getGlobalInstance(legalScope);
	}

	/*
	 * This is private so we know the LegalScope came from the contained
	 * LegalScopeLibrary.
	 */
	private ScopeInstance getGlobalInstance(LegalScope legalScope)
	{
		if (legalScope.getParentScope().isPresent())
		{
			throw new IllegalArgumentException(
				"Cannot build Global Scope for a LegalScope that has a parent");
		}
		String name = legalScope.getName();
		ScopeInstance inst = globals.get(name);
		if (inst == null)
		{
			inst = new SimpleScopeInstance(null, legalScope,
				new GlobalVarScoped(legalScope.getName()));
			globals.put(name, inst);
		}
		return inst;
	}

	@Override
	public ScopeInstance get(String scopeName, VarScoped obj)
	{
		LegalScope scope = library.getScope(scopeName);
		if (scope == null)
		{
			throw new IllegalArgumentException(
				"Scope with name " + scopeName + " not found");
		}
		return getMessaged(scope, obj, obj);
	}

	/**
	 * Actually processes the result of a get, while preserving the original
	 * VarScoped object to make any message better for the end user.
	 * 
	 * Private so that we know the LegalScope came from the LegalScopeLibrary of
	 * this SimpleScopeInstanceFactory.
	 */
	private ScopeInstance getMessaged(LegalScope instScope, VarScoped current,
		VarScoped original)
	{
		Optional<? extends LegalScope> potentialParentScope = instScope.getParentScope();
		//null is Global object
		if (current == null)
		{
			//is instScope Global?
			if (!potentialParentScope.isPresent())
			{
				return getGlobalInstance(instScope);
			}
			if (original == null)
			{
				//Started with a global assertion
				throw new IllegalArgumentException(
					"Requested ScopeInstance for null (Global) object, "
						+ "but with LegalScope that was not Global: "
						+ instScope.getName());
			}
			else
			{
				//Reached, but did not start with, a global assertion
				throw new IllegalArgumentException(
					"Requested ScopeInstance for " + original.getClass().getName() + " "
						+ original.getKeyName() + " and reached a global parent, "
						+ "but have only reached Scope: " + instScope.getName());
			}
		}
		VarScoped parentObj = current.getVariableParent();
		String localScopeName = current.getLocalScopeName();
		if (localScopeName == null)
		{
			/*
			 * Some object may not have a local scope, so fall up: get the parent and
			 * check the local variable scope of the parent.
			 */
			return getMessaged(instScope, parentObj, original);
		}
		LegalScope currentScope = library.getScope(localScopeName);
		if (!currentScope.equals(instScope))
		{
			/*
			 * We could be in a sub-scope and the instScope is really for a parent. If the
			 * scopes don't match, fall "up" until a matching object or failure.
			 */
			return getMessaged(instScope, parentObj, original);
		}
		//At this point, it really *is* for current
		ScopeInstance inst = objectToInstanceCache.get(current);
		if (inst == null)
		{
			//Need to build the scope...
			ScopeInstance parentInstance =
					getMessaged(potentialParentScope.get(), parentObj, original);
			inst = constructInstance(parentInstance, currentScope, original);
			objectToInstanceCache.put(current, inst);
		}
		return inst;
	}

	/*
	 * Private due to lack of checking and ensuring LegalScope is from the
	 * embedded LegalScopeLibrary.
	 */
	private ScopeInstance constructInstance(ScopeInstance parent,
		LegalScope scope, VarScoped representing)
	{
		SimpleScopeInstance inst =
				new SimpleScopeInstance(parent, scope, representing);
		scopeInstances.addToListFor(parent, scope, inst);
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

	@Override
	public LegalScope getScope(String s)
	{
		return library.getScope(s);
	}
}
