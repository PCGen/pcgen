/*
 * Copyright (c) Thomas Parker, 2009.
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
import java.util.Set;
import java.util.function.Consumer;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractAssociationFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.VarScopedFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.local.DefinedWrappingModifier;
import pcgen.cdom.formula.local.ModifierDecoration;
import pcgen.cdom.formula.local.RemoteWrappingModifier;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;

/**
 * RemoteModifierFacet is a Facet that tracks remove Modifiers that have been
 * granted to a Player Character by looking for MODIFYOTHER: entries on
 * CDOMObjects added to/removed from the Player Character.
 */
public class RemoteModifierFacet extends AbstractAssociationFacet<CharID, RemoteModifier<?>, PCGenScoped>
        implements DataFacetChangeListener<CharID, PCGenScoped>
{

    private ScopeFacet scopeFacet;

    private VarScopedFacet varScopedFacet;

    private SolverManagerFacet solverManagerFacet;

    private LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCGenScoped> dfce)
    {
        CharID id = dfce.getCharID();
        PCGenScoped addedObject = dfce.getCDOMObject();
        /*
         * If addedObject can have local variables, find what may have been modified by
         * previous objects.
         */
        for (RemoteModifier<?> remoteModifier : getSet(id))
        {
            PCGenScoped modSource = get(id, remoteModifier);
            processAdd(id, remoteModifier, addedObject, modSource);
        }
        /*
         * Look at what addedObject can modify on others.
         */
        RemoteModifier<?>[] remoteModifierArray = addedObject.getRemoteModifierArray();
        if (remoteModifierArray.length > 0)
        {
            Set<PCGenScoped> targets = varScopedFacet.getSet(id);
            for (RemoteModifier<?> remoteModifier : remoteModifierArray)
            {
                set(id, remoteModifier, addedObject);
                //Apply to existing as necessary
                for (PCGenScoped obj : targets)
                {
                    processAdd(id, remoteModifier, obj, addedObject);
                }
            }
        }
    }

    private <MT> void processAdd(CharID id, RemoteModifier<MT> remoteModifier,
            PCGenScoped targetObject, PCGenScoped modSource)
    {
        ScopeInstance modSourceInstance = scopeFacet.get(id, modSource);
        VarModifier<MT> varModifier = remoteModifier.getVarModifier();
        FormulaModifier<MT> formulaModifier = varModifier.getModifier();
        Consumer<PCGenScoped> consumer = target -> solverManagerFacet.addModifier(id,
                varModifier, target, getModifier(id, formulaModifier, modSource,
                        modSourceInstance, target, scopeFacet.get(id, target)),
                scopeFacet.get(id, target));
        remoteModifier.getGrouping().process(targetObject, consumer);
    }

    private <T> Modifier<T> getModifier(CharID id, FormulaModifier<T> modifier, VarScoped source,
            ScopeInstance sourceInstance, VarScoped target, ScopeInstance targetInstance)
    {
        PCGenScope sourceScope = (PCGenScope) sourceInstance.getLegalScope();
        LoadContext context = loadContextFacet.get(id.getDatasetID()).get();
        Optional<FormatManager<?>> sourceFormatManager = sourceScope.getFormatManager(context);
        PCGenScope targetScope = (PCGenScope) targetInstance.getLegalScope();
        Optional<FormatManager<?>> targetFormatManager = targetScope.getFormatManager(context);
        Modifier<T> returnValue;
        if (sourceFormatManager.isPresent() && targetFormatManager.isPresent())
        {
            returnValue = new RemoteWrappingModifier<>(modifier, source,
                    sourceFormatManager.get(), target, targetFormatManager.get());
        } else if (sourceFormatManager.isPresent())
        {
            returnValue = new DefinedWrappingModifier<>(modifier, "source",
                    source, sourceFormatManager.get());
        } else if (sourceFormatManager.isPresent())
        {
            returnValue = new DefinedWrappingModifier<>(modifier, "target",
                    target, targetFormatManager.get());
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
        PCGenScoped addedObject = dfce.getCDOMObject();
        /*
         * If addedObject can have local variables, find what had been modified by
         * previous objects.
         */
        for (RemoteModifier<?> remoteModifier : getSet(id))
        {
            PCGenScoped modSource = get(id, remoteModifier);
            processRemove(id, remoteModifier, addedObject, modSource);
        }
        /*
         * Look at what addedObject can modify on others.
         */
        RemoteModifier<?>[] remoteModifierArray = addedObject.getRemoteModifierArray();
        if (remoteModifierArray.length > 0)
        {
            Set<? extends VarScoped> targets = varScopedFacet.getSet(id);
            for (RemoteModifier<?> remoteModifier : remoteModifierArray)
            {
                remove(id, remoteModifier);
                //RemoveFrom existing as necessary
                for (VarScoped obj : targets)
                {
                    processRemove(id, remoteModifier, obj, addedObject);
                }
            }
        }
    }

    private <MT> void processRemove(CharID id, RemoteModifier<MT> remoteModifier,
            VarScoped targetObject, PCGenScoped modSource)
    {
        ScopeInstance modSourceInstance = scopeFacet.get(id, modSource);
        VarModifier<MT> varModifier = remoteModifier.getVarModifier();
        FormulaModifier<MT> formulaModifier = varModifier.getModifier();
        Consumer<PCGenScoped> consumer = target -> solverManagerFacet.removeModifier(id,
                varModifier, target, getModifier(id, formulaModifier, modSource,
                        modSourceInstance, target, scopeFacet.get(id, target)),
                scopeFacet.get(id, target));
        remoteModifier.getGrouping().process(modSource, consumer);
    }

    public void setScopeFacet(ScopeFacet scopeFacet)
    {
        this.scopeFacet = scopeFacet;
    }

    public void setVarScopedFacet(VarScopedFacet varScopedFacet)
    {
        this.varScopedFacet = varScopedFacet;
    }

    public void setSolverManagerFacet(SolverManagerFacet solverManagerFacet)
    {
        this.solverManagerFacet = solverManagerFacet;
    }

    /**
     * Initializes the connections for RemoteModifierFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the RemoteModifierFacet.
     */
    public void init()
    {
        varScopedFacet.addDataFacetChangeListener(this);
    }

    /*
     * In Ability: MODIFYOTHER:PC.EQUIPMENT|GROUP=Martial|EqCritRange|ADD|1
     *
     * In Global: MODIFYOTHER:PC.EQUIPMENT.PART|ALL|CritRange|SOLVE|value()+EqCritRange
     * |PRIORITY=10000
     *
     * effectively we are solving APPLYTO:EQUIPMENT|PC[GROUP=Martial]|...
     *
     * EQUIPMENT ends up in the Modifier as stDef
     *
     * EqCritRange|ADD|1 also ends up in the VarModifier (as the varName and the
     * Modifier itself)
     *
     * type is getGroupClass()
     *
     *
     *
     * GROUP=Martial is static
     *
     * PC is dynamic and attached to a Facet
     *
     * list(PC) && list(GROUP=Martial)
     *
     * But what happens when something lands on the list?
     *
     * Then we need to circle around & load local vars...
     *
     * When New member of a certain VarID (e.g. Applied Equipment) Then take
     * that member and get the VariableStore Look up in the database all
     * VarModifiers to be applied merge VariableStore and VarModifiers...
     */
}
