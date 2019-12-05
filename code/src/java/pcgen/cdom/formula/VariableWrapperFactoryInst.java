/*
 * Copyright (c) Thomas Parker, 2018-9.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.formula;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableStoreFacet;

/**
 * A VariableWrapperFactoryInst is a VariableWrapperFactory that will ensure that a
 * VariableWrapper is only produced once per VariableID.
 */
public class VariableWrapperFactoryInst implements VariableWrapperFactory
{
    /**
     * The SolverManagerFacet for VariableID construction.
     */
    private static final SolverManagerFacet MGR_FACET = FacetLibrary.getFacet(SolverManagerFacet.class);

    /**
     * The VariableStoreFacet for VariableID construction.
     */
    private static final VariableStoreFacet RESULT_FACET = FacetLibrary.getFacet(VariableStoreFacet.class);

    /**
     * The VariableWrapper objects produced by this VariableWrapperFactoryInst.
     */
    private Map<VariableID<?>, VariableWrapper<?>> wrappers = new HashMap<>();

    @Override
    public VariableWrapper<?> getWrapper(CharID id, VarScoped owner, String name)
    {
        return getWrapper(id, VariableUtilities.getLocalVariableID(id, owner, name));
    }

    @Override
    public VariableWrapper<?> getGlobalWrapper(CharID id, String name)
    {
        return getWrapper(id, VariableUtilities.getGlobalVariableID(id, name));
    }

    @Override
    public void disconnect(VariableWrapper<?> variableWrapper)
    {
        wrappers.remove(variableWrapper.getVariableID());
        variableWrapper.disconnect();
    }

    /**
     * Gets a VariableWrapper linked to the given VariableID on the PlayerCharacter
     * represented by the given CharID.
     * <p>
     * As a note on object cleanup: The returned VariableWrapper is a listener to the
     * given MonitorableVariableStore. Should use of this VariableWrapper be no longer
     * necessary, then the disconnect() method should be called in order to disconnect the
     * VariableWrapper from the WriteableVariableStore.
     *
     * @param id    The CharID representing the PlayerCharacter for which a VariableWrapper
     *              should be returned
     * @param varID The VariableID indicating to which Variable this VariableWrapper is
     *              providing an interface
     * @return A VariableWrapper linked to the given VariableID on the PlayerCharacter
     * represented by the given CharID
     */
    private <T> VariableWrapper<T> getWrapper(CharID id, VariableID<T> varID)
    {
        @SuppressWarnings("unchecked")
        VariableWrapper<T> ref = (VariableWrapper<T>) wrappers.get(varID);
        if (ref == null)
        {
            MonitorableVariableStore varStore = RESULT_FACET.get(id);
            ref = VariableWrapper.construct(MGR_FACET.get(id), varStore, varID);
            wrappers.put(varID, ref);
            varStore.addVariableListener(varID, ref);
        }
        return ref;
    }

}
