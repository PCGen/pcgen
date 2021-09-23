/*
 * Copyright (c) Thomas Parker, 2016-20.
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
package pcgen.base.formula.inst;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

import pcgen.base.formula.base.VariableID;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.EnhancedWeakReference;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;

/**
 * A MonitorableVariableStore is a WriteableVariableStore that allows VariableListener
 * objects to listen for changes to the variables within this VariableStore.
 */
public class MonitorableVariableStore extends SimpleVariableStore
{

	/**
	 * The listeners, identified by priority and which VariableID they are listening to.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private final DoubleKeyMapToList<VariableID<?>, Integer,
					EnhancedWeakReference<VariableListener<?>>> listenerList =
			new DoubleKeyMapToList<>(HashMap.class, TreeMap.class);

	/**
	 * The general listeners, identified by priority.
	 */
	private final MapToList<Integer, EnhancedWeakReference<VariableListener<?>>> generalListenerList =
			new TreeMapToList<>();

	/**
	 * Adds a VariableListener for the given VariableID.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which the given VariableListener wishes to receive
	 *            VariableChangeEvents
	 * @param listener
	 *            The VariableListener that will listen for changes to the given
	 *            VariableID
	 */
	public <T> void addVariableListener(VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		addVariableListener(0, varID, listener);
	}

	/**
	 * Adds a VariableListener for the given VariableID with the given priority .
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param priority
	 *            The lower the priority the earlier in the list the new listener will get
	 *            advised of the change.
	 * @param varID
	 *            The VariableID for which the given VariableListener wishes to receive
	 *            VariableChangeEvents
	 * @param listener
	 *            The VariableListener that will listen for changes to the given
	 *            VariableID
	 */
	public <T> void addVariableListener(int priority, VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		listenerList.addToListFor(varID, priority,
			new EnhancedWeakReference<>(listener));
	}

	/**
	 * Removes a VariableListener from the given VariableID.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID for which the given VariableListener is to be removed
	 *            from the listener list
	 * @param listener
	 *            The VariableListener to be removed from the listener list for the given
	 *            VariableID
	 */
	public <T> void removeVariableListener(VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		removeVariableListener(0, varID, listener);
	}

	/**
	 * Removes a VariableListener so that it will no longer receive VariableChangeEvents
	 * from this MonitorableVariableStore for the given VariableID. This will remove the
	 * VariableListener from the given priority.
	 * 
	 * Note that if the given VariableListener has been registered under a different
	 * priority, it will still receive events at that priority level.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param priority
	 *            The priority of the listener to be removed
	 * @param varID
	 *            The VariableID for which the given VariableListener is to be removed
	 *            from the listener list
	 * @param listener
	 *            The VariableListener to be removed from the listener list for the given
	 *            VariableID
	 */
	public <T> void removeVariableListener(int priority, VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		if (!listenerList.containsListFor(varID, priority))
		{
			return;
		}
		listenerList.getListFor(varID, priority).stream()
			.filter(reference -> reference.consumeIfEmpty(() -> listenerList
				.removeFromListFor(varID, priority, reference)))
			.filter(reference -> reference.get().equals(listener))
			.findFirst()
			.ifPresent(reference -> listenerList.removeFromListFor(varID,
				priority, reference));
	}

	/**
	 * Adds a VariableListener for all VariableIDs
	 * 
	 * @param listener
	 *            The VariableListener that will listen for changes
	 */
	public void addGeneralListener(VariableListener<Object> listener)
	{
		addGeneralListener(0, listener);
	}

	/**
	 * Adds a VariableListener for all VariableIDs with the given priority.
	 * 
	 * @param priority
	 *            The lower the priority the earlier in the list the new listener will get
	 *            advised of the change.
	 * @param listener
	 *            The VariableListener that will listen for changes
	 */
	public void addGeneralListener(int priority,
		VariableListener<Object> listener)
	{
		generalListenerList.addToListFor(priority,
			new EnhancedWeakReference<>(listener));
	}

	/**
	 * Removes a VariableListener that is listening to all VariableIDs.
	 * 
	 * @param listener
	 *            The VariableListener to be removed from the general listener list
	 */
	public void removeGeneralListener(VariableListener<Object> listener)
	{
		removeGeneralListener(0, listener);
	}

	/**
	 * Removes a VariableListener that is listening to all VariableIDs. This will remove
	 * the VariableListener from the given priority.
	 * 
	 * Note that if the given VariableListener has been registered under a different
	 * priority, it will still receive events at that priority level.
	 * 
	 * @param priority
	 *            The priority of the listener to be removed
	 * @param listener
	 *            The VariableListener to be removed from the listener list
	 */
	public void removeGeneralListener(int priority,
		VariableListener<Object> listener)
	{
		if (!generalListenerList.containsListFor(priority))
		{
			return;
		}
		generalListenerList.getListFor(priority).stream()
			.filter(
				reference -> reference.consumeIfEmpty(() -> generalListenerList
					.removeFromListFor(priority, reference)))
			.filter(reference -> reference.get().equals(listener))
			.findFirst()
			.ifPresent(reference -> generalListenerList
				.removeFromListFor(priority, reference));
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
	 * Fires a VariableChangeEvent to the VariableListeners subscribed to the given
	 * VariableID.
	 * 
	 * @param <T>
	 *            The format (class) of object contained by the given VariableID
	 * @param varID
	 *            The VariableID indicating which variable changed
	 * @param old
	 *            The previous value of the variable prior to the change
	 * @param value
	 *            The (current and) new value of the variable after the change
	 */
	@SuppressWarnings("unchecked")
	public <T> void fireVariableChanged(VariableID<T> varID, T old, T value)
	{
		//Note: Generics are enforced by addVariableListener
		@SuppressWarnings("rawtypes")
		VariableChangeEvent vcEvent =
				new VariableChangeEvent<>(this, varID, old, value);
		for (Integer priority : listenerList.getSecondaryKeySet(varID))
		{
			listenerList.getListFor(varID, priority).stream()
				.filter(reference -> reference.consumeIfEmpty(() -> listenerList
					.removeFromListFor(varID, priority, reference)))
				.map(reference -> reference.get())
				.filter(Objects::nonNull) //Defensive corner case - "timing attack" from GC
				.forEach(listener -> listener.variableChanged(vcEvent));
		}
		for (Integer priority : generalListenerList.getKeySet())
		{
			generalListenerList.getListFor(priority).stream()
				.filter(reference -> reference.consumeIfEmpty(() -> generalListenerList
					.removeFromListFor(priority, reference)))
				.map(reference -> reference.get())
				.filter(Objects::nonNull) //Defensive corner case - "timing attack" from GC
				.forEach(listener -> listener.variableChanged(vcEvent));
		}
	}
}
