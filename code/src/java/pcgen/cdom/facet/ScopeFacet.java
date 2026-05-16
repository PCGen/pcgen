/*
 * Copyright (c) Thomas Parker, 2015.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.GlobalPCVarScoped;

/**
 * ScopeFacet stores the relationship from a Character, LegalScope, and
 * CDOMObject to the ScopeInstance for that object.
 */
public class ScopeFacet extends AbstractItemFacet<CharID, ScopeInstanceFactory>
{
	private final Map<CharID, GlobalPCVarScoped> globalVarScopedMap = new HashMap<>();

	@Override
	public boolean set(CharID id, ScopeInstanceFactory factory)
	{
		boolean result = super.set(id, factory);
		GlobalPCVarScoped globalVS = new GlobalPCVarScoped(GlobalPCScope.GLOBAL_SCOPE_NAME);
		globalVarScopedMap.put(id, globalVS);
		return result;
	}

	@Override
	public ScopeInstanceFactory remove(CharID id)
	{
		globalVarScopedMap.remove(id);
		return super.remove(id);
	}

	/**
	 * Returns the Global ScopeInstance for the PlayerCharacter represented by
	 * the given CharID.
	 *
	 * @param id
	 *            The CharID representing the PlayerCharacter for which the
	 *            Global ScopeInstance should be returned
	 * @return The Global ScopeInstance for the PlayerCharacter represented by
	 *         the given CharID
	 */
	public ScopeInstance getGlobalScope(CharID id)
	{
		return get(id).get(GlobalPCScope.GLOBAL_SCOPE_NAME, globalVarScopedMap.get(id));
	}

	/**
	 * Returns the ScopeInstance (within the given scope name and
	 * PlayerCharacter represented by the given CharID) for the given VarScoped
	 * object.
	 *
	 * @param id
	 *            The CharID representing the PlayerCharacter within which the
	 *            returned ScopeInstance exists
	 * @param scopeName
	 *            The scope name within which the returned ScopeInstance exists
	 * @param scopedObject
	 *            The VarScoped object for which the ScopeInstance object should
	 *            be returned
	 * @return The ScopeInstance for the CharID representing the PlayerCharacter
	 *         and the given scope name and VarScoped objects
	 */
	public ScopeInstance get(CharID id, String scopeName, VarScoped scopedObject)
	{
		return get(id).get(scopeName, scopedObject);
	}

	/**
	 * Returns the ScopeInstance for the given VarScoped object within the PC identified
	 * by the given CharID. Uses the global scope name since VarScoped.getProviderFor
	 * will walk the hierarchy.
	 *
	 * @param id
	 *            The CharID representing the PlayerCharacter
	 * @param vs
	 *            The VarScoped object for which the ScopeInstance should be returned
	 * @return The ScopeInstance for the given VarScoped object
	 */
	public ScopeInstance get(CharID id, VarScoped vs)
	{
		return get(id).get(GlobalPCScope.GLOBAL_SCOPE_NAME, vs);
	}
}
