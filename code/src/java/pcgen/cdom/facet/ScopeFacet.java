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

import java.util.Optional;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.formula.scope.GlobalPCScope;

/**
 * ScopeFacet stores the relationship from a Character, LegalScope, and
 * CDOMObject to the ScopeInstance for that object.
 */
public class ScopeFacet extends AbstractItemFacet<CharID, ScopeInstanceFactory>
{
    /**
     * Returns the Global ScopeInstance for the PlayerCharacter represented by
     * the given CharID.
     *
     * @param id The CharID representing the PlayerCharacter for which the
     *           Global ScopeInstance should be returned
     * @return The Global ScopeInstance for the PlayerCharacter represented by
     * the given CharID
     */
    public ScopeInstance getGlobalScope(CharID id)
    {
        return get(id).getGlobalInstance(GlobalPCScope.GLOBAL_SCOPE_NAME);
    }

    /**
     * Returns the ScopeInstance (within the given LegalScope and
     * PlayerCharacter represented by the given CharID) for the given VarScoped
     * object.
     *
     * @param id             The CharID representing the PlayerCharacter within which the
     *                       returned ScopeInstance exists
     * @param legalScopeName The LegalScope name within which the returned ScopeInstance
     *                       exists
     * @param scopedObject   The VarScoped object for which the ScopeInstance object should
     *                       be returned
     * @return The ScopeInstance for the CharID representing the PlayerCharacter
     * and the given LegalScope and VarScoped objects
     */
    public ScopeInstance get(CharID id, String legalScopeName, VarScoped scopedObject)
    {
        return get(id).get(legalScopeName, Optional.of(scopedObject));
    }

    public ScopeInstance get(CharID id, VarScoped vs)
    {
        Optional<String> localName = vs.getLocalScopeName();
        VarScoped active = vs;
        while (localName.isEmpty())
        {
            Optional<VarScoped> parent = active.getVariableParent();
            if (parent.isEmpty())
            {
                return getGlobalScope(id);
            }
            active = parent.get();
            localName = active.getLocalScopeName();
        }
        return get(id, localName.get(), vs);
    }
}
