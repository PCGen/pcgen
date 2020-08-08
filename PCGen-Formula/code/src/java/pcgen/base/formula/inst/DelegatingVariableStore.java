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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import pcgen.base.formula.base.VariableID;

/**
 * A DelegatingVariableStore is a WriteableVariableStore that allows VariableListener
 * objects to listen for changes to the variables within this VariableStore and has
 * another MonitorableVariableStore providing alternative resolution for items.
 */
public class DelegatingVariableStore extends MonitorableVariableStore
{

	/**
	 * The underlying MonitorableVariableStore used by this DelegatingVariableStore.
	 */
	private final MonitorableVariableStore backgroundStore;

	/**
	 * Constructs a new DelegatingVariableStore with the given underlying
	 * MonitorableVariableStore.
	 * 
	 * @param backgroundStore
	 *            The underlying MonitorableVariableStore for this DelegatingVariableStore
	 */
	public DelegatingVariableStore(MonitorableVariableStore backgroundStore)
	{
		this.backgroundStore = Objects.requireNonNull(backgroundStore);
	}

	@Override
	public <T> void addVariableListener(VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		super.addVariableListener(varID, listener);
		backgroundStore.addVariableListener(varID, listener);
	}

	@Override
	public <T> void addVariableListener(int priority, VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		super.addVariableListener(priority, varID, listener);
		backgroundStore.addVariableListener(priority, varID, listener);
	}

	@Override
	public <T> void removeVariableListener(VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		super.removeVariableListener(varID, listener);
		backgroundStore.removeVariableListener(varID, listener);
	}

	@Override
	public <T> void removeVariableListener(int priority, VariableID<T> varID,
		VariableListener<? super T> listener)
	{
		super.removeVariableListener(priority, varID, listener);
		backgroundStore.removeVariableListener(priority, varID, listener);
	}

	@Override
	public <T> T get(VariableID<T> varID)
	{
		T result = super.get(varID);
		return (result == null) ? backgroundStore.get(varID) : result;
	}

	@Override
	public boolean containsVariable(VariableID<?> varID)
	{
		return super.containsVariable(varID) || backgroundStore.containsVariable(varID);
	}

	@Override
	public Collection<VariableID<?>> getVariables()
	{
		List<VariableID<?>> variables = new ArrayList<>();
		variables.addAll(super.getVariables());
		variables.addAll(backgroundStore.getVariables());
		return variables;
	}
}
