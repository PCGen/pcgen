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

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.inst.SimpleVariableStore;
import pcgen.base.util.DoubleKeyMapToList;

/**
 * A MonitorableVariableStore is a WriteableVariableStore that allows
 * VariableListener objects to listen for changes to the variables within this
 * VariableStore.
 */
public class MonitorableVariableStore extends SimpleVariableStore
{

    /**
     * The listeners, identified by priority and which VariableID they are listening to.
     */
    private final DoubleKeyMapToList<Integer, VariableID<?>, VariableListener<?>> listenerList =
            new DoubleKeyMapToList<>(TreeMap.class, HashMap.class);

    /**
     * Adds a VariableListener for the given VariableID.
     *
     * @param varID    The VariableID for which the given VariableListener wishes to
     *                 receive VariableChangeEvents
     * @param listener The VariableListener that will listen for changes to the given
     *                 VariableID
     */
    public <T> void addVariableListener(VariableID<T> varID, VariableListener<? super T> listener)
    {
        addVariableListener(0, varID, listener);
    }

    /**
     * Adds a VariableListener for the given VariableID with the given priority .
     *
     * @param priority The lower the priority the earlier in the list the new
     *                 listener will get advised of the change.
     * @param varID    The VariableID for which the given VariableListener wishes to
     *                 receive VariableChangeEvents
     * @param listener The VariableListener that will listen for changes to the given
     *                 VariableID
     */
    public <T> void addVariableListener(int priority, VariableID<T> varID, VariableListener<? super T> listener)
    {
        listenerList.addToListFor(priority, varID, listener);
    }

    /**
     * Removes a VariableListener from the given VariableID.
     *
     * @param varID    The VariableID for which the given VariableListener is to be
     *                 removed from the listener list
     * @param listener The VariableListener to be removed from the listener list for
     *                 the given VariableID
     */
    public <T> void removeVariableListener(VariableID<T> varID, VariableListener<? super T> listener)
    {
        removeVariableListener(0, varID, listener);
    }

    /**
     * Removes a VariableListener so that it will no longer receive VariableChangeEvents
     * from this MonitorableVariableStore for the given VariableID. This will remove the
     * VariableListener from the given priority.
     * <p>
     * Note that if the given VariableListener has been registered under a
     * different priority, it will still receive events at that priority level.
     *
     * @param priority The priority of the listener to be removed
     * @param varID    The VariableID for which the given VariableListener is to be removed
     *                 from the listener list
     * @param listener The VariableListener to be removed from the listener list for the given
     *                 VariableID
     */
    public <T> void removeVariableListener(int priority, VariableID<T> varID, VariableListener<? super T> listener)
    {
        listenerList.removeFromListFor(0, varID, listener);
    }

    @Override
    public <T> T put(VariableID<T> varID, T value)
    {
        T old = super.put(varID, value);
        if (!value.equals(old))
        {
            fireVariableChanged(varID, old, value);
        }
        return old;
    }

    /**
     * Fires a VariableChangeEvent to the VariableListeners subscribed to the
     * given VariableID.
     *
     * @param varID The VariableID indicating which variable changed
     * @param old   The previous value of the variable prior to the change
     * @param value The (current and) new value of the variable after the change
     */
    public <T> void fireVariableChanged(VariableID<T> varID, T old, T value)
    {
        for (Integer priority : listenerList.getKeySet())
        {
            List<VariableListener<?>> listeners = listenerList.getListFor(priority, varID);
            VariableChangeEvent vcEvent = null;
            if (listeners != null)
            {
                for (VariableListener<?> listener : listeners)
                {
                    // Lazily create event
                    if (vcEvent == null)
                    {
                        vcEvent = new VariableChangeEvent<>(this, varID, old, value);
                    }
                    listener.variableChanged(vcEvent);
                }
            }
        }
    }

}
