/*
 * Copyright (c) Thomas Parker, 2016.
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
package pcgen.cdom.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.SolverManager;
import pcgen.facade.util.AbstractReferenceFacade;
import pcgen.facade.util.VetoableReferenceFacade;

import org.apache.commons.collections4.CollectionUtils;

/**
 * A VariableChannel provides a common mechanism for reading and writing to a
 * variable from a system external to the PCGen core.
 *
 * @param <T> The Format of the information contained in this VariableChannel
 */
public final class VariableChannel<T> extends AbstractReferenceFacade<T>
        implements VetoableReferenceFacade<T>
{

    /**
     * The underlying SolverManager that solves the given VariableID
     */
    private final SolverManager manager;

    /**
     * The VariableID indicating to which Variable this VariableChannel is
     * providing an interface.
     */
    private final VariableID<T> varID;

    /**
     * The MonitorableVariableStore that the results of the calculations by the
     * SolverManager are placed in.
     */
    private final MonitorableVariableStore varStore;

    /**
     * The private Listener, so that VariableChannel does not need to implement
     * VariableListener itself. (prevents exposure of internal behavior)
     */
    private final Listener varListener = new Listener();

    /**
     * The list of functions allowed to veto changes to this variable channel.
     */
    private List<BiFunction<T, T, Boolean>> vetoList = null;

    /**
     * Constructs a new VariableChannel with the given SolverManager,
     * MonitorableVariableStore, and VariableID indicating the contents of the
     * Channel.
     *
     * @param manager  The underlying SolverManager that solves the given
     *                 VariableID
     * @param varStore The MonitorableVariableStore that the results of the
     *                 calculations by the SolverManager are placed in
     * @param varID    The VariableID indicating to which Variable this
     *                 VariableChannel is providing an interface
     */
    private VariableChannel(SolverManager manager, MonitorableVariableStore varStore,
            VariableID<T> varID)
    {
        this.manager = Objects.requireNonNull(manager);
        this.varStore = Objects.requireNonNull(varStore);
        this.varID = Objects.requireNonNull(varID);
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

    @Override
    public void set(T object)
    {
        if (!checkForVeto(object))
        {
            varStore.put(varID, object);
            manager.solveChildren(varID);
        }
    }

    private boolean checkForVeto(T proposedValue)
    {
        T oldValue = varStore.get(varID);
        return CollectionUtils.emptyIfNull(vetoList)
                .stream()
                .filter(f -> f.apply(oldValue, proposedValue))
                .findAny()
                .isPresent();
    }

    /**
     * Disconnects the VariableChannel from the WriteableVariableStore. This is
     * necessary before a VariableChannel is disposed of, so that it does not
     * cause a memory leak.
     * <p>
     * Note, if disconnect() is called and the VariableChannel continues to be
     * used, then it is effectively a WriteableVariableStore, not a
     * MonitorableVariableStore. It will no longer send any ReferenceEvents if
     * the underlying VariableID changes.
     */
    public void disconnect()
    {
        varStore.removeVariableListener(varID, varListener);
    }

    /**
     * Returns the VariableID for this VariableChannel.
     * <p>
     * As a note on object cleanup: The returned VariableChannel is a listener
     * to the given MonitorableVariableStore. Should use of this VariableChannel
     * be no longer necessary, then the disconnect() method of the
     * VariableChannel should be called in order to disconnect the
     * VariableChannel from the WriteableVariableStore.
     *
     * @param manager  The underlying SolverManager that solves the given
     *                 VariableID
     * @param varStore The MonitorableVariableStore that the results of the
     *                 calculations by the SolverManager are placed in
     * @param varID    The VariableID indicating to which Variable this
     *                 VariableChannel is providing an interface
     * @return A new VariableChannel linked as a listener to the given
     * MonitorableVariableStore for the given VariableID
     */
    public static <T> VariableChannel<T> construct(SolverManager manager,
            MonitorableVariableStore varStore, VariableID<T> varID)
    {
        VariableChannel<T> ref =
                new VariableChannel<>(manager, varStore, varID);
        varStore.addVariableListener(varID, ref.varListener);
        return ref;
    }

    /**
     * Returns the VariableID for this VariableChannel.
     *
     * @return The VariableID for this VariableChannel
     */
    public VariableID<?> getVariableID()
    {
        return varID;
    }

    @Override
    public void addVetoToChannel(BiFunction<T, T, Boolean> function)
    {
        if (vetoList == null)
        {
            vetoList = new ArrayList<>(2);
        }
        vetoList.add(Objects.requireNonNull(function));
    }

    private class Listener implements VariableListener<T>
    {
        @Override
        public void variableChanged(VariableChangeEvent<T> event)
        {
            fireReferenceChangedEvent(event.getSource(), event.getOldValue(), event.getNewValue());
        }
    }
}
