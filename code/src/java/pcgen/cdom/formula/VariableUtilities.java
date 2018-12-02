/*
 * Copyright (c) Thomas Parker, 2018.
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

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.util.CControl;
import pcgen.core.PlayerCharacter;
import pcgen.output.channel.ChannelUtilities;
import pcgen.rules.context.VariableContext;

/**
 * VariableUtilities are a class for monitoring events on variables.
 */
public class VariableUtilities
{

	/**
	 * The LoadContextFacet.
	 */
	private static final LoadContextFacet LOAD_CONTEXT_FACET =
			FacetLibrary.getFacet(LoadContextFacet.class);

	/**
	 * The ScopeFacet.
	 */
	private static final ScopeFacet SCOPE_FACET = FacetLibrary.getFacet(ScopeFacet.class);

	/**
	 * The VariableStoreFacet.
	 */
	private static final VariableStoreFacet RESULT_FACET = FacetLibrary.getFacet(VariableStoreFacet.class);
	
	/**
	 * Returns the VariableID for a variable on the PlayerCharacter represented by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter that the variable is on
	 * @param variableName
	 *            The name of the variable for which the VariableID should be returned
	 * @return The VariableID for the variable with the given name on the PlayerCharacter
	 *         represented by the given CharID
	 */
	public static <T> VariableID<T> getGlobalVariableID(CharID id, String variableName)
	{
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		VariableContext varContext =
				LOAD_CONTEXT_FACET.get(id.getDatasetID()).get().getVariableContext();
		VariableID<T> varID =
				(VariableID<T>) varContext.getVariableID(globalInstance, variableName);
		return varID;
	}

	/**
	 * Defines a listener that should react to a change in a variable.
	 * 
	 * @param id
	 *            The CharID on which the variable should be watched
	 * @param variableName
	 *            The name of the variable to be watched
	 * @param listener
	 *            The listener to receive an event when the value of the variable changes
	 */
	public static <T> void addListenerToVariable(CharID id, String variableName,
		VariableListener<T> listener)
	{
		VariableID<T> varID = VariableUtilities.getGlobalVariableID(id, variableName);
		RESULT_FACET.get(id).addVariableListener(0, varID, listener);
	}

	/**
	 * Removes a listener that should no longer see changes in a variable.
	 * 
	 * @param id
	 *            The CharID on which the variable should be no longer be watched
	 * @param listener
	 *            The listener to be removed from receiving an event when the value of the
	 *            variable changes
	 * @param variableName
	 *            The name of the variable to no longer be watched
	 */
	public static <T> void removeListenerFromVariable(CharID id,
		VariableListener<T> listener, String variableName)
	{
		VariableID<T> varID = VariableUtilities.getGlobalVariableID(id, variableName);
		RESULT_FACET.get(id).removeVariableListener(0, varID, listener);
	}

	/**
	 * Sets up the given Code Control so that if the value on the channel changes, the PC
	 * is categorized as Dirty.
	 * 
	 * @param pc
	 *            The PlayerCharacter on which the channel should be watched
	 * @param codeControl
	 *            The name of the channel to be watched
	 */
	public static void setDirtyOnChannelChange(PlayerCharacter pc,
		CControl codeControl)
	{
		String variableName =
				ChannelUtilities.createVarName(pc.getControl(codeControl));
		VariableUtilities.addListenerToVariable(pc.getCharID(), variableName,
			x -> pc.setDirty(true));
	}

	/**
	 * Sets up the given DataFacetChangeListener to receive a DataFacetChangeEvent when
	 * the value of the given channel on the given PC changes. This provides compatibility
	 * for facets that wish to listen to the new variable system.
	 * 
	 * Note that this currently supports Item-based channels, not lists
	 * 
	 * @param pc
	 *            The PlayerCharacter on which the channel should be watched
	 * @param codeControl
	 *            The name of the channel to be watched
	 * @param listener
	 *            The listener to receive an event when the value of the channel changes
	 */
	public static void watchChannel(PlayerCharacter pc, CControl codeControl,
		DataFacetChangeListener<CharID, ?> listener)
	{
		watchChannel(pc.getCharID(), pc.getControl(codeControl), listener, 0);
	}

	/**
	 * Sets up the given DataFacetChangeListener to receive a DataFacetChangeEvent when
	 * the value of a channel changes. This provides compatibility for facets that wish to
	 * listen to the new variable system.
	 * 
	 * Note that this currently supports Item-based channels, not lists
	 * 
	 * @param id
	 *            The CharID on which the channel should be watched
	 * @param channelName
	 *            The name of the channel to be watched
	 * @param listener
	 *            The listener to receive an event when the value of the channel changes
	 * @param priority
	 *            The priority of the listener for receiving changes (The lower the
	 *            priority the earlier in the list the new listener will get advised of
	 *            the change)
	 */
	public static <T> void watchChannel(CharID id, String channelName,
		DataFacetChangeListener<CharID, T> listener, int priority)
	{
		VariableID<T> varID =
				ChannelUtilities.getChannelVariableID(id, channelName);
		RESULT_FACET.get(id).addVariableListener(priority, varID,
			e -> fireVariableChangedEvent(id, e, listener));
	}

	private static <T> void fireVariableChangedEvent(CharID id,
		VariableChangeEvent<T> vcEvent,
		DataFacetChangeListener<CharID, T> listener)
	{
		T oldValue = vcEvent.getOldValue();
		if (oldValue != null)
		{
			DataFacetChangeEvent<CharID, T> removeEvent =
					new DataFacetChangeEvent<>(id, oldValue, RESULT_FACET,
						DataFacetChangeEvent.DATA_REMOVED);
			listener.dataRemoved(removeEvent);
		}
		DataFacetChangeEvent<CharID, T> addEvent =
				new DataFacetChangeEvent<>(id, vcEvent.getNewValue(),
					RESULT_FACET, DataFacetChangeEvent.DATA_ADDED);
		listener.dataAdded(addEvent);
	}
}
