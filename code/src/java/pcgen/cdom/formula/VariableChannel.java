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

import java.util.Objects;

import javax.swing.event.EventListenerList;

import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.SolverManager;
import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 * A VariableChannel provides a common mechanism for reading and writing to a
 * variable from a system external to the PCGen core.
 * 
 * @param <T>
 *            The Format of the information contained in this VariableChannel
 */
public final class VariableChannel<T> implements VariableListener<T>, WriteableReferenceFacade<T>
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
	 * The list of listeners that listen to this VariableChannel for
	 * ReferenceEvents.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructs a new VariableChannel with the given SolverManager,
	 * MonitorableVariableStore, and VariableID indicating the contents of the
	 * Channel.
	 * 
	 * @param manager
	 *            The underlying SolverManager that solves the given
	 *            VariableID
	 * @param varStore
	 *            The MonitorableVariableStore that the results of the
	 *            calculations by the SolverManager are placed in
	 * @param varID
	 *            The VariableID indicating to which Variable this
	 *            VariableChannel is providing an interface
	 */
	public VariableChannel(SolverManager manager, MonitorableVariableStore varStore, VariableID<T> varID)
	{
		this.manager = Objects.requireNonNull(manager);
		this.varStore = Objects.requireNonNull(varStore);
		this.varID = Objects.requireNonNull(varID);
	}

	@Override
	public void variableChanged(VariableChangeEvent<T> event)
	{
		fireReferenceChangedEvent(event.getSource(), event.getOldValue(), event.getNewValue());
	}

	@Override
	public T get()
	{
		T value = varStore.get(varID);
		if (value == null)
		{
			return manager.getDefaultValue(varID.getVariableFormat());
		}
		return value;
	}

	@Override
	public void set(T object)
	{
		varStore.put(varID, object);
		manager.solveChildren(varID);
	}

	/**
	 * Disconnects the VariableChannel from the WriteableVariableStore. This is
	 * necessary before a VariableChannel is disposed of, so that it does not
	 * cause a memory leak.
	 * 
	 * Note, if disconnect() is called and the VariableChannel continues to be
	 * used, then it is effectively a WriteableVariableStore, not a
	 * MonitorableVariableStore. It will no longer send any ReferenceEvents if
	 * the underlying VariableID changes.
	 */
	public void disconnect()
	{
		varStore.removeVariableListener(varID, this);
	}

	@Override
	public void addReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.add(ReferenceListener.class, listener);
	}

	@Override
	public void removeReferenceListener(ReferenceListener<? super T> listener)
	{
		listenerList.remove(ReferenceListener.class, listener);
	}

	private void fireReferenceChangedEvent(Object source, T oldValue, T newValue)
	{
		ReferenceListener[] listeners = listenerList.getListeners(ReferenceListener.class);
		ReferenceEvent<T> e = null;
		for (int i = listeners.length - 1; i >= 0; i--)
		{
			if (e == null)
			{
				e = new ReferenceEvent<>(source, oldValue, newValue);
			}
			listeners[i].referenceChanged(e);
		}
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

}
