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
package pcgen.output.channel;

import java.util.Objects;

import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.formula.VariableListener;
import pcgen.cdom.formula.VariableUtilities;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * ChannelUtilities are a class for setting up communication channels from the
 * core to other objects via get(), set(...) and events.
 */
public final class ChannelUtilities
{
	/**
	 * The VariableStoreFacet
	 */
	private static final VariableStoreFacet RESULT_FACET = FacetLibrary.getFacet(VariableStoreFacet.class);
	
	/**
	 * The SolverManagerFacet
	 */
	private static final SolverManagerFacet SOLVER_MANAGER_FACET = FacetLibrary.getFacet(SolverManagerFacet.class);

	private ChannelUtilities()
	{
		//Do not instantiate Utility Class
	}

	/**
	 * Reads a Global Channel with the given Channel name.
	 * 
	 * @param id
	 *            The CharID representing the PC on which the channel should be read
	 * @param channelName
	 *            The name of the channel to be read
	 * @return The value of the channel with the given name on the PC represented by the
	 *         given CharID
	 */
	public static Object readGlobalChannel(CharID id, String channelName)
	{
		return RESULT_FACET.getValue(id, getChannelVariableID(id, channelName));
	}

	/**
	 * Reads a global channel where the name is determined by the given Code Control
	 * (CControl) object.
	 * 
	 * @param id
	 *            The CharID representing the PC on which the channel should be read
	 * @param control
	 *            The Code Control which should be used to determine the channel name
	 * @return The value of the channel identified by the given Code Control on the PC
	 *         represented by the given CharID
	 */
	public static Object readControlledChannel(CharID id, CControl control)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(), control);
		return ChannelUtilities.readGlobalChannel(id, channelName);
	}

	/**
	 * Sets the value of a Global Channel with the given Channel name.
	 * 
	 * @param id
	 *            The CharID representing the PC on which the channel should be set
	 * @param channelName
	 *            The name of the channel to be set
	 * @param value
	 *            The value to which the channel should be set, for the channel with the
	 *            given name on the PC represented by the given CharID
	 */
	public static void setGlobalChannel(CharID id, String channelName, Object value)
	{
		VariableID<Object> varID = getChannelVariableID(id, channelName);
		RESULT_FACET.get(id).put(varID, value);
		SOLVER_MANAGER_FACET.get(id).solveChildren(varID);
	}

	/**
	 * Sets a global channel where the name is determined by the given Code Control
	 * (CControl) object.
	 * 
	 * @param id
	 *            The CharID representing the PC on which the channel should be set
	 * @param control
	 *            The Code Control which should be used to determine the channel name
	 * @param value
	 *            The value to which the channel should be set, for the channel identified
	 *            by the given Code Control, on the PC represented by the given CharID
	 */
	public static void setControlledChannel(CharID id, CControl control, Object value)
	{
		String playerNameChannel = ControlUtilities.getControlToken(Globals.getContext(),
			control);
		setGlobalChannel(id, playerNameChannel, value);
	}

	/**
	 * Creates a channel variable Name from the given channel name.
	 * 
	 * @param channelName
	 *            The Channel name from which the channel variable name should be created
	 * @return The channel variable name
	 */
	public static String createVarName(String channelName)
	{
		return "CHANNEL*" + Objects.requireNonNull(channelName);
	}

	/**
	 * Defines a listener that should react to a change in a channel.
	 * 
	 * @param id
	 *            The CharID on which the channel should be watched
	 * @param channelName
	 *            The name of the channel to be watched
	 * @param listener
	 *            The listener to receive an event when the value of the channel changes
	 */
	public static <T> void addListenerToChannel(CharID id, String channelName,
		VariableListener<T> listener)
	{
		String variableName = createVarName(channelName);
		VariableUtilities.addListenerToVariable(id, variableName, listener);
	}

	/**
	 * Removes a listener that should no longer see changes in a channel.
	 * 
	 * @param id
	 *            The CharID on which the channel should be no longer be watched
	 * @param channelName
	 *            The name of the channel to no longer be watched
	 * @param listener
	 *            The listener to be removed from receiving an event when the value of the channel changes
	 */
	public static <T> void removeListenerFromChannel(CharID id, String channelName,
		VariableListener<T> listener)
	{
		String variableName = createVarName(channelName);
		VariableUtilities.removeListenerFromVariable(id, listener, variableName);
	}

	/**
	 * Returns the VariableID for a channel on the PlayerCharacter represented by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter that the channel is on
	 * @param channelName
	 *            The name of the channel for which the VariableID should be returned
	 * @return The VariableID for the channel with the given name on the PlayerCharacter
	 *         represented by the given CharID
	 */
	private static <T> VariableID<T> getChannelVariableID(CharID id, String channelName)
	{
		String variableName = createVarName(channelName);
		return VariableUtilities.getGlobalVariableID(id, variableName);
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
		ChannelUtilities.watchChannel(pc.getCharID(), pc.getControl(codeControl), listener, 0);
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
	 * @param priority
	 *            The priority of the listener for receiving changes (The lower the
	 *            priority the earlier in the list the new listener will get advised of
	 *            the change)
	 */
	public static void watchChannel(PlayerCharacter pc, CControl codeControl,
		DataFacetChangeListener<CharID, ?> listener, int priority)
	{
		ChannelUtilities.watchChannel(pc.getCharID(), pc.getControl(codeControl), listener, priority);
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
				getChannelVariableID(id, channelName);
		RESULT_FACET.get(id).addVariableListener(priority, varID,
			e -> VariableUtilities.forwardVariableChangeToDFCL(id, e, listener));
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
		addListenerToChannel(pc, codeControl, x -> pc.setDirty(true));
	}

	/**
	 * Adds a listener to the channel on the given PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter on which the listener will be added
	 * @param control
	 *            The CodeControl indicating the channel on which the listener will be
	 *            added
	 * @param listener
	 *            the VariableListener to be added to listen to changes to the channel
	 */
	public static void addListenerToChannel(PlayerCharacter pc, CControl control,
		VariableListener<?> listener)
	{
		String varName = createVarName(pc.getControl(control));
		VariableUtilities.addListenerToVariable(pc.getCharID(), varName,
			listener);
	}
}
