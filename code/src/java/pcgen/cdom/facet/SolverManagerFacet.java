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
import pcgen.rules.context.VariableContext;

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
		VariableContext varContext = loadContextFacet.get(id.getDatasetID()).get().getVariableContext();
		ScopeInstance scope = resolveScope(id, vm, thisValue, varContext);
		VariableID<T> varID = (VariableID<T>) varContext.getVariableID(scope, vm.getVarName());
		SolverManager sm = get(id);
		sm.addModifier(varID, modifier, source);
		sm.processSolver(varID);
		return true;
	}

	/**
	 * Removes a Modifier from the PC.
	 */
	public <T> void removeModifier(CharID id, VarModifier<T> vm, VarScoped thisValue, Modifier<T> modifier,
		ScopeInstance source)
	{
		VariableContext varContext = loadContextFacet.get(id.getDatasetID()).get().getVariableContext();
		ScopeInstance scope = resolveScope(id, vm, thisValue, varContext);
		VariableID<T> varID = (VariableID<T>) varContext.getVariableID(scope, vm.getVarName());
		SolverManager sm = get(id);
		sm.removeModifier(varID, modifier, source);
		sm.processSolver(varID);
	}

	private <T> ScopeInstance resolveScope(CharID id, VarModifier<T> vm, VarScoped thisValue,
		VariableContext varContext)
	{
		if (vm.getLegalScope().isGlobal()
			|| varContext.isLegalVariableID(scopeFacet.getGlobalScope(id).getImplementedScope(), vm.getVarName()))
		{
			return scopeFacet.getGlobalScope(id);
		}
		return scopeFacet.get(id, vm.getFullLegalScopeName(), thisValue);
	}

	public void setScopeFacet(ScopeFacet scopeFacet)
	{
		this.scopeFacet = scopeFacet;
	}
}
