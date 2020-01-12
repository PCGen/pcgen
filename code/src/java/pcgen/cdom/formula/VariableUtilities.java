/*
 * Copyright (c) Thomas Parker, 2018-9.
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

import java.util.Optional;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.VariableContext;

/**
 * VariableUtilities are a class for monitoring events on variables.
 */
public final class VariableUtilities
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

	private VariableUtilities()
	{
	}

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
        return (VariableID<T>) varContext.getVariableID(globalInstance, variableName);
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
	 * Forwards a VariableChangeEvent to a DataFacetChangeListener as a
	 * DataFacetChangeEvent.
	 * 
	 * @param id
	 *            The CharID on which the change is occurring
	 * @param vcEvent
	 *            the VariableChangeEvent
	 * @param listener
	 *            The DataFacetChangeListener
	 */
	public static <T> void forwardVariableChangeToDFCL(CharID id,
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

	/**
	 * Returns a VariableID for a local variable on the PlayerCharacter represented by the
	 * given CharID and on the object represented by the given ScopeInstance.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter that the variable is
	 *            contained within
	 * @param scopeInst
	 *            The ScopeInstance representing the object that the variable is on
	 * @param name
	 *            The name of the variable for which the VariableID should be returned
	 * @return The VariableID for the variable with the given name on the local object
	 *         represented by the given ScopeInstance
	 */
	public static VariableID<?> getLocalVariableID(CharID id,
		ScopeInstance scopeInst, String name)
	{
		LoadContext loadContext = LOAD_CONTEXT_FACET.get(id.getDatasetID()).get();
		return loadContext.getVariableContext().getVariableID(scopeInst, name);
	}

	/**
	 * Returns a VariableID for a local variable on the PlayerCharacter represented by the
	 * given CharID and on the given object.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter that the variable is
	 *            contained within
	 * @param owner
	 *            The owning VarScoped object that the variable is on
	 * @param name
	 *            The name of the variable for which the VariableID should be returned
	 * @return The VariableID for the variable with the given name on the given object
	 */
	public static VariableID<?> getLocalVariableID(CharID id, VarScoped owner,
		String name)
	{
		ScopeInstanceFactory instFactory = SCOPE_FACET.get(id);
		Optional<String> localScopeName = owner.getLocalScopeName();
		ScopeInstance scopeInst =
				instFactory.get(localScopeName.get(), Optional.of(owner));
		return VariableUtilities.getLocalVariableID(id, scopeInst, name);
	}
}
