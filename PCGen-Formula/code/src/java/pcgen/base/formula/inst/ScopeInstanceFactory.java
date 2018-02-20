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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.util.DoubleKeyMapToList;

/**
 * A ScopeInstanceFactory is a factory used to instantiate ScopeInstance objects
 * given a parent ScopeInstance and LegalScope in which to instantiate the
 * ScopeInstance.
 */
public class ScopeInstanceFactory
{

	/**
	 * Contains the map of parent ScopeInstance objects and LegalScope objects
	 * to the instantiated "children" ScopeInstance objects within the provided
	 * LegalScope.
	 */
	private DoubleKeyMapToList<ScopeInstance, LegalScope, ScopeInstance> scopeInstances =
			new DoubleKeyMapToList<ScopeInstance, LegalScope, ScopeInstance>();

	/**
	 * Contains a map from the owning VarScoped object to the ScopeInstance for
	 * that object.
	 */
	private Map<VarScoped, ScopeInstance> objectToInstanceCache =
			new HashMap<VarScoped, ScopeInstance>();

	/**
	 * Contains a map from a format to the global ScopeInstance for that format.
	 */
	private Map<String, ScopeInstance> globals =
			new HashMap<String, ScopeInstance>();

	/**
	 * The LegalScopeLibrary used to indicate the LegalScope objects for this
	 * ScopeInstanceFactory.
	 */
	private final LegalScopeLibrary library;

	/**
	 * Construct a new ScopeInstanceFactory with the underlying
	 * LegalScopeLibrary.
	 * 
	 * @param library
	 *            The LegalScopeLibrary indicating the legal scopes for this
	 *            ScopeInstanceFactory
	 */
	public ScopeInstanceFactory(LegalScopeLibrary library)
	{
		this.library = Objects.requireNonNull(library);
	}

	/**
	 * Returns the "global" ScopeInstance object for the given LegalScope.
	 * 
	 * @param scopeName
	 *            The name of the LegalScope for which the "global"
	 *            ScopeInstance should be returned.
	 * @return The "global" ScopeInstance object for the given LegalScope
	 */
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
		if (legalScope.getParentScope() != null)
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

	/**
	 * Returns the ScopeInstance within the given LegalScope and considering the
	 * given VarScoped object. If the LegalScope is not the scope for the given
	 * VarScoped object, then ancestors of the VarScoped object will be checked
	 * until one matches the given LegalScope. A new ScopeInstance will be
	 * created if one does not already exist.
	 * 
	 * @param scopeName
	 *            The name of the LegalScope for which the ScopeInstance should
	 *            be returned
	 * @param obj
	 *            The Object where analysis should start in order to determine
	 *            the appropriate ScopeInstance to be returned.
	 * @return The ScopeInstance within the given LegalScope and considering the
	 *         given VarScoped object
	 * @throws IllegalArgumentException
	 *             if the given LegalScope is not a scope for the given
	 *             VarScoped object or an ancestor of the VarScoped object (as
	 *             determined by getVariableParent())
	 */
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
	 * this ScopeInstanceFactory.
	 */
	private ScopeInstance getMessaged(LegalScope instScope, VarScoped current,
		VarScoped original)
	{
		//null current means we expect the LegalScope to be a form of global (no parent)
		if (current == null)
		{
			if (instScope.getParentScope() == null)
			{
				//Is Global
				return getGlobalInstance(instScope);
			}
			if (original == null)
			{
				throw new IllegalArgumentException(
					"Requested ScopeInstance for null (Global) object, "
						+ "but with LegalScope that was not Global: "
						+ instScope.getName());
			}
			else
			{
				throw new IllegalArgumentException(
					"Requested ScopeInstance for "
						+ original.getClass().getName() + " "
						+ original.getKeyName() + " but in an uncompatible "
						+ "LegalScope: " + instScope.getName());
			}
		}
		VarScoped parentObj = current.getVariableParent();
		String localScopeName = current.getLocalScopeName();
		if (localScopeName == null)
		{
			/*
			 * Some object may not have a local scope, so just get the parent
			 * and check the local variable scope of the parent.
			 */
			return getMessaged(instScope, parentObj, original);
		}
		LegalScope currentScope = library.getScope(localScopeName);
		if (!currentScope.equals(instScope))
		{
			/*
			 * We could be in a sub-scope and the LegalScope is really for a
			 * parent. If the scopes don't match, fall "up" until a matching
			 * object or failure
			 */
			return getMessaged(instScope, parentObj, original);
		}
		//At this point, it really *is* for current
		ScopeInstance inst = objectToInstanceCache.get(current);
		if (inst == null)
		{
			//Need to build the scope...
			ScopeInstance parentInstance =
					getMessaged(instScope.getParentScope(), parentObj,
						parentObj);
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
	 * ScopeInstanceFactory has built a ScopeInstance.
	 * 
	 * @return A Collection of the VarScoped objects for which this
	 *         ScopeInstanceFactory has built a ScopeInstance
	 */
	public Collection<VarScoped> getInstancedObjects()
	{
		return Collections
			.unmodifiableCollection(objectToInstanceCache.keySet());
	}

	/**
	 * Returns the LegalScope for the given legal scope name, using the
	 * LegalScopeLibrary underlying this ScopeInstanceFactory to resolve the
	 * name.
	 * 
	 * @param s
	 *            The scope name to be used to find the LegalScope in the
	 *            LegalScopeLibrary underlying this ScopeInstanceFactory
	 * @return The LegalScope for the given legal scope name
	 */
	public LegalScope getScope(String s)
	{
		return library.getScope(s);
	}
}
