/*
 * Copyright (c) Thomas Parker, 2016-9.
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

import java.util.Objects;

import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.SolverManager;
import pcgen.facade.util.AbstractReferenceFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 * A VariableWrapper provides a common mechanism for reading from a variable by a system
 * external to the PCGen core.
 *
 * @param <T> The Format of the information contained in this VariableWrapper
 */
public final class VariableWrapper<T> extends AbstractReferenceFacade<T>
        implements VariableListener<T>, ReferenceFacade<T>
{

    /**
     * The underlying SolverManager that solves the given VariableID
     */
    private final SolverManager manager;

    /**
     * The VariableID indicating to which Variable this VariableWrapper is providing an
     * interface.
     */
    private final VariableID<T> varID;

    /**
     * The MonitorableVariableStore that the results of the calculations by the
     * SolverManager are placed in.
     */
    private final MonitorableVariableStore varStore;

    /**
     * Constructs a new VariableWrapper with the given SolverManager,
     * MonitorableVariableStore, and VariableID indicating the contents of the Channel.
     *
     * @param manager  The underlying SolverManager that solves the given VariableID
     * @param varStore The MonitorableVariableStore that the results of the calculations by the
     *                 SolverManager are placed in
     * @param varID    The VariableID indicating to which Variable this VariableWrapper is
     *                 providing an interface
     */
    private VariableWrapper(SolverManager manager,
            MonitorableVariableStore varStore, VariableID<T> varID)
    {
        this.manager = Objects.requireNonNull(manager);
        this.varStore = Objects.requireNonNull(varStore);
        this.varID = Objects.requireNonNull(varID);
    }

    @Override
    public void variableChanged(VariableChangeEvent<T> event)
    {
        fireReferenceChangedEvent(event.getSource(), event.getOldValue(),
                event.getNewValue());
    }

    @Override
    public T get()
    {
        T value = varStore.get(varID);
        if (value == null)
        {
            return manager.getDefaultValue(varID.getFormatManager());
        }
        return value;
    }

    /**
     * Disconnects the VariableWrapper from the WriteableVariableStore. This is necessary
     * before a VariableWrapper is disposed of, so that it does not cause a memory leak.
     * <p>
     * Note, if disconnect() is called and the VariableWrapper continues to be used, then
     * it is effectively a WriteableVariableStore, not a MonitorableVariableStore. It will
     * no longer send any ReferenceEvents if the underlying VariableID changes.
     */
    public void disconnect()
    {
        varStore.removeVariableListener(varID, this);
    }

    /**
     * Returns the VariableID for this VariableWrapper.
     * <p>
     * As a note on object cleanup: The returned VariableWrapper is a listener to the
     * given MonitorableVariableStore. Should use of this VariableWrapper be no longer
     * necessary, then the disconnect() method of the VariableWrapper should be called in
     * order to disconnect the VariableWrapper from the WriteableVariableStore.
     *
     * @param manager  The underlying SolverManager that solves the given VariableID
     * @param varStore The MonitorableVariableStore that the results of the calculations by the
     *                 SolverManager are placed in
     * @param varID    The VariableID indicating to which Variable this VariableWrapper is
     *                 providing an interface
     * @return A new VariableWrapper linked as a listener to the given
     * MonitorableVariableStore for the given VariableID
     */
    public static <T> VariableWrapper<T> construct(SolverManager manager,
            MonitorableVariableStore varStore, VariableID<T> varID)
    {
        VariableWrapper<T> ref =
                new VariableWrapper<>(manager, varStore, varID);
        varStore.addVariableListener(varID, ref);
        return ref;
    }

    /**
     * Returns the VariableID for this VariableWrapper.
     *
     * @return The VariableID for this VariableWrapper
     */
    public VariableID<?> getVariableID()
    {
        return varID;
    }
}
