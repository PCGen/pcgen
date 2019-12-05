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

import java.util.List;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.ProcessStep;
import pcgen.base.solver.SolverManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;

/**
 * This stores the SolverManager for each PlayerCharacter.
 */
public class SolverManagerFacet extends AbstractItemFacet<CharID, SolverManager>
{
    /**
     * The global LoadContextFacet used to get VariableIDs
     */
    private final LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    private ScopeFacet scopeFacet;

    public <T> List<ProcessStep<T>> diagnose(CharID id, VariableID<T> varID)
    {
        return get(id).diagnose(varID);
    }

    public <T> boolean addModifier(CharID id, VarModifier<T> vm, VarScoped thisValue, Modifier<T> modifier,
            ScopeInstance source)
    {
        ScopeInstance scope = scopeFacet.get(id, vm.getFullLegalScopeName(), thisValue);
        VariableID<T> varID = (VariableID<T>) loadContextFacet.get(id.getDatasetID()).get().getVariableContext()
                .getVariableID(scope, vm.getVarName());
        return get(id).addModifierAndSolve(varID, modifier, source);
    }

    /**
     * Removes a Modifier from the PC.
     */
    public <T> void removeModifier(CharID id, VarModifier<T> vm, VarScoped thisValue, Modifier<T> modifier,
            ScopeInstance source)
    {
        ScopeInstance scope = scopeFacet.get(id, vm.getFullLegalScopeName(), thisValue);
        VariableID<T> varID = (VariableID<T>) loadContextFacet.get(id.getDatasetID()).get().getVariableContext()
                .getVariableID(scope, vm.getVarName());
        get(id).removeModifier(varID, modifier, source);
    }

    public void setScopeFacet(ScopeFacet scopeFacet)
    {
        this.scopeFacet = scopeFacet;
    }
}
