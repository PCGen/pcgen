/*
 * Copyright (c) Thomas Parker, 2018.
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

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.output.channel.ChannelUtilities;

/**
 * A VariableChannelFactoryInst is a VariableChannelFactory that will ensure that a
 * VariableChannel is only produced once per VariableID.
 */
public class VariableChannelFactoryInst implements VariableChannelFactory
{
	/**
	 * The LoadContextFacet for VariableID construction.
	 */
	private static final LoadContextFacet LOAD_CONTEXT_FACET = FacetLibrary.getFacet(LoadContextFacet.class);

	/**
	 * The ScopeFacet for VariableID construction.
	 */
	private static final ScopeFacet SCOPE_FACET = FacetLibrary.getFacet(ScopeFacet.class);

	/**
	 * The SolverManagerFacet for VariableID construction.
	 */
	private static final SolverManagerFacet MGR_FACET = FacetLibrary.getFacet(SolverManagerFacet.class);

	/**
	 * The VariableStoreFacet for VariableID construction.
	 */
	private static final VariableStoreFacet RESULT_FACET = FacetLibrary.getFacet(VariableStoreFacet.class);

	/**
	 * The VariableChannel objects produced by this VariableChannelFactoryInst.
	 */
	private Map<VariableID<?>, VariableChannel<?>> channels = new HashMap<>();

	@Override
	public VariableChannel<?> getChannel(CharID id, VarScoped owner, String name)
	{
		ScopeInstanceFactory instFactory = SCOPE_FACET.get(id);
		ScopeInstance scopeInst = instFactory.get(owner.getLocalScopeName(), owner);
		return getChannel(id, scopeInst, name);
	}

	@Override
	public VariableChannel<?> getGlobalChannel(CharID id, String name)
	{
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		return getChannel(id, globalInstance, name);
	}

	@Override
	public void disconnect(VariableChannel<?> variableChannel)
	{
		channels.remove(variableChannel.getVariableID());
		variableChannel.disconnect();
	}

	private VariableChannel<?> getChannel(CharID id, ScopeInstance scopeInst, String name)
	{
		String varName = ChannelUtilities.createVarName(name);
		VariableID<?> varID =
				LOAD_CONTEXT_FACET.get(id.getDatasetID()).get().getVariableContext().getVariableID(scopeInst, varName);
		return getChannel(id, varID);
	}

	/**
	 * Gets a VariableChannel linked to the given VariableID on the PlayerCharacter
	 * represented by the given CharID.
	 * 
	 * As a note on object cleanup: The returned VariableChannel is a listener to the
	 * given MonitorableVariableStore. Should use of this VariableChannel be no longer
	 * necessary, then the disconnect() method should be called in order to disconnect the
	 * VariableChannel from the WriteableVariableStore.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter for which a VariableChannel
	 *            should be returned
	 * @param varID
	 *            The VariableID indicating to which Variable this VariableChannel is
	 *            providing an interface
	 * @return A VariableChannel linked to the given VariableID on the PlayerCharacter
	 *         represented by the given CharID
	 */
	private <T> VariableChannel<T> getChannel(CharID id, VariableID<T> varID)
	{
		@SuppressWarnings("unchecked")
		VariableChannel<T> ref = (VariableChannel<T>) channels.get(varID);
		if (ref == null)
		{
			MonitorableVariableStore varStore = RESULT_FACET.get(id);
			ref = new VariableChannel<>(MGR_FACET.get(id), varStore, varID);
			channels.put(varID, ref);
			varStore.addVariableListener(varID, ref);
		}
		return ref;
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
	 */
	public static <T> void watchChannel(CharID id, String channelName,
		DataFacetChangeListener<CharID, T> listener)
	{
		watchChannel(id, channelName, listener, 0);
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
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		VariableID<T> varID = (VariableID<T>) LOAD_CONTEXT_FACET.get(id.getDatasetID())
			.get().getVariableContext()
			.getVariableID(globalInstance, ChannelUtilities.createVarName(channelName));
		RESULT_FACET.get(id).addVariableListener(priority, varID, new VariableListener<T>()
		{
			@Override
			public void variableChanged(VariableChangeEvent<T> vcEvent)
			{
				DataFacetChangeEvent<CharID, T> removeEvent =
						new DataFacetChangeEvent<>(id, vcEvent.getOldValue(),
							RESULT_FACET, DataFacetChangeEvent.DATA_REMOVED);
				listener.dataRemoved(removeEvent);
				DataFacetChangeEvent<CharID, T> addEvent = new DataFacetChangeEvent<>(id,
					vcEvent.getNewValue(), RESULT_FACET, DataFacetChangeEvent.DATA_ADDED);
				listener.dataAdded(addEvent);
			}
		});
	}
}
