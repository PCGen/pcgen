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
import java.util.Objects;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeImplementer;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.util.DoubleKeyMap;

/**
 * A SimpleScopeInstanceFactory is a factory used to instantiate ScopeInstance objects
 * given a parent ScopeInstance and ImplementedScope in which to instantiate the
 * ScopeInstance.
 */
public class SimpleScopeInstanceFactory implements ScopeInstanceFactory
{

	/**
	 * Contains a map from the owning VarScoped object and ImplementedScope to the
	 * ScopeInstance.
	 */
	private final DoubleKeyMap<VarScoped, ImplementedScope, ScopeInstance> objectToInstanceCache =
			new DoubleKeyMap<>();

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
	public ScopeInstance get(String scopeName, VarScoped varScoped)
	{
		ImplementedScope scope = manager.getImplementedScope(scopeName);
		VarScoped activeVarScoped = varScoped.getProviderFor(scope);
		return getActiveInstance(scope, activeVarScoped);

	}

	private ScopeInstance getActiveInstance(ImplementedScope scope,
		VarScoped activeVarScoped)
	{
		ScopeInstance inst = objectToInstanceCache.get(activeVarScoped, scope);
		if (inst == null)
		{
			//Need to build the scope...
			inst = new SimpleScopeInstance(scope, activeVarScoped);
			objectToInstanceCache.put(activeVarScoped, scope, inst);
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
			.unmodifiableCollection(objectToInstanceCache.getKeySet());
	}
}
