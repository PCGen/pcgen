/*
 * Copyright (c) Thomas Parker, 2014
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

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.VarScopedFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.local.DefinedWrappingModifier;
import pcgen.cdom.formula.local.ModifierDecoration;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;

/**
 * ModifierFacet checks each item added to a PlayerCharacter to see if it has
 * MODIFY: entries on the object, and if so, adds them to the Solver system.
 */
public class ModifierFacet implements DataFacetChangeListener<CharID, PCGenScoped>
{
    private ScopeFacet scopeFacet;

    private VarScopedFacet varScopedFacet;

    private SolverManagerFacet solverManagerFacet;

    private LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCGenScoped> dfce)
    {
        CharID id = dfce.getCharID();
        PCGenScoped obj = dfce.getCDOMObject();
        VarModifier<?>[] modifiers = obj.getModifierArray();
        if (modifiers.length > 0)
        {
            ScopeInstance inst = scopeFacet.get(id, obj);
            for (VarModifier<?> vm : modifiers)
            {
                processAddition(id, obj, vm, inst);
            }
        }
    }

    private <T> void processAddition(CharID id, VarScoped obj, VarModifier<T> vm, ScopeInstance inst)
    {
        solverManagerFacet.addModifier(id, vm, obj, getModifier(id, inst, vm.getModifier(), obj), inst);
    }

    private <T> Modifier<T> getModifier(CharID id, ScopeInstance source, FormulaModifier<T> modifier,
            VarScoped thisValue)
    {
        PCGenScope legalScope = (PCGenScope) source.getLegalScope();
        LoadContext context = loadContextFacet.get(id.getDatasetID()).get();
        Modifier<T> returnValue;
        Optional<FormatManager<?>> formatManager = legalScope.getFormatManager(context);
        if (formatManager.isPresent())
        {
            returnValue = new DefinedWrappingModifier<>(modifier, "this",
                    thisValue, formatManager.get());
        } else
        {
            returnValue = new ModifierDecoration<>(modifier);
        }
        return returnValue;
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCGenScoped> dfce)
    {
        CharID id = dfce.getCharID();
        PCGenScoped obj = dfce.getCDOMObject();
        VarModifier<?>[] modifiers = obj.getModifierArray();
        if (modifiers.length > 0)
        {
            ScopeInstance inst = scopeFacet.get(id, obj);
            for (VarModifier<?> vm : modifiers)
            {
                processRemoval(id, obj, vm, inst);
            }
        }
    }

    private <T> void processRemoval(CharID id, VarScoped obj, VarModifier<T> vm, ScopeInstance inst)
    {
        solverManagerFacet.removeModifier(id, vm, obj, getModifier(id, inst, vm.getModifier(), obj), inst);
    }

    public void setScopeFacet(ScopeFacet scopeFacet)
    {
        this.scopeFacet = scopeFacet;
    }

    public void setSolverManagerFacet(SolverManagerFacet solverManagerFacet)
    {
        this.solverManagerFacet = solverManagerFacet;
    }

    public void setVarScopedFacet(VarScopedFacet varScopedFacet)
    {
        this.varScopedFacet = varScopedFacet;
    }

    /**
     * Initializes the connections for ModifierFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the ModifierFacet.
     */
    public void init()
    {
        varScopedFacet.addDataFacetChangeListener(this);
    }
}
