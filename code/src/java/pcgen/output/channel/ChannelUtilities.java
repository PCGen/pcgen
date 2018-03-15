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

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableLibraryFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.formula.VariableChannel;

/**
 * ChannelUtilities are a class for setting up communication channels from the
 * core to other objects via get(), set(...) and events.
 */
public final class ChannelUtilities
{
	private static final VariableLibraryFacet VARLIB_FACET =
			FacetLibrary.getFacet(VariableLibraryFacet.class);
	private static final ScopeFacet SCOPE_FACET =
			FacetLibrary.getFacet(ScopeFacet.class);
	private static final VariableStoreFacet RESULT_FACET =
			FacetLibrary.getFacet(VariableStoreFacet.class);
	private static final SolverManagerFacet MGR_FACET =
			FacetLibrary.getFacet(SolverManagerFacet.class);

	private ChannelUtilities()
	{
		//Do not instantiate Utility Class
	}

	/**
	 * Retrieves a Channel for the given CharID, owning object, and name of the
	 * channel.
	 * 
	 * @param id
	 *            The CharID identifying the PlayerCharacter on which the
	 *            Channel resides
	 * @param owner
	 *            The owning object of the Channel
	 * @param name
	 *            The name of the channel
	 * @return A Channel for the given CharID, owning object, and name of the
	 *         channel
	 */
	public static VariableChannel<?> getChannel(CharID id, VarScoped owner,
		String name)
	{
		ScopeInstanceFactory instFactory = SCOPE_FACET.get(id);
		ScopeInstance scopeInst = instFactory.get(owner.getLocalScopeName(), owner);
		return getChannel(id, scopeInst, name);
	}

	/**
	 * Retrieves a (Global) Channel for the given CharID and name of the
	 * channel.
	 * 
	 * @param id
	 *            The CharID identifying the PlayerCharacter on which the
	 *            Channel resides
	 * @param name
	 *            The name of the channel
	 * @return A Channel for the given CharID and name of the channel
	 */
	public static VariableChannel<?> getGlobalChannel(CharID id, String name)
	{
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		return getChannel(id, globalInstance, name);
	}

	private static VariableChannel<?> getChannel(CharID id,
		ScopeInstance scopeInst, String name)
	{
		String varName = createVarName(name);
		VariableID<?> varID = VARLIB_FACET.getVariableID(id.getDatasetID(),
			scopeInst, varName);
		return VariableChannel.construct(MGR_FACET.get(id),
			RESULT_FACET.get(id), varID);
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
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		VariableID<?> varID = VARLIB_FACET.getVariableID(id.getDatasetID(),
			globalInstance, createVarName(channelName));
		return RESULT_FACET.getValue(id, varID);
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
		ScopeInstance globalInstance = SCOPE_FACET.getGlobalScope(id);
		VariableID<?> varID = VARLIB_FACET.getVariableID(id.getDatasetID(),
			globalInstance, createVarName(channelName));
		processSet(id, varID, value);
	}

	private static <T> void processSet(CharID id, VariableID<T> varID, Object value)
	{
		RESULT_FACET.get(id).put(varID, (T) value);
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

}
