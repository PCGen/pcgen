/*
 * Copyright (c) Thomas Parker, 2016.
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

import java.util.EventObject;

import pcgen.base.formula.base.VariableID;

/**
 * A VariableChangeEvent is an event indicating a change took place on a Variable (as
 * represented by a VariableID).
 * 
 * @param <T>
 *            The Format of the underlying Variable for which the VariableChangeEvent is
 *            indicating a change
 */
public class VariableChangeEvent<T> extends EventObject
{

	/**
	 * The VariableID indicating which variable changed for this VariableChangeEvent.
	 */
	private final VariableID<T> varID;

	/**
	 * The previous value of the variable prior to the change being indicated by this
	 * VariableChangeEvent.
	 */
	private final T oldValue;

	/**
	 * The (current and) new value of the variable after the change being indicated by
	 * this VariableChangeEvent.
	 */
	private final T newValue;

	/**
	 * Constructs a new VariableChangeEvent with the given Source, VariableID and old/new
	 * values.
	 * 
	 * @param source
	 *            The source object of the VariableChangeEvent
	 * @param varID
	 *            The VariableID indicating which variable changed for this
	 *            VariableChangeEvent
	 * @param old
	 *            The previous value of the variable prior to the change being indicated
	 *            by this VariableChangeEvent
	 * @param value
	 *            The (current and) new value of the variable after the change being
	 *            indicated by this VariableChangeEvent
	 */
	public VariableChangeEvent(Object source, VariableID<T> varID, T old,
		T value)
	{
		super(source);
		this.varID = varID;
		this.oldValue = old;
		this.newValue = value;
	}

	/**
	 * Returns the VariableID indicating the variable that was changed for this
	 * VariableChangeEvent.
	 * 
	 * @return The VariableID indicating the variable that was changed for this
	 *         VariableChangeEvent
	 */
	public VariableID<T> getVarID()
	{
		return varID;
	}

	/**
	 * Returns the previous value of the variable prior to the change being indicated by
	 * this VariableChangeEvent.
	 * 
	 * @return The previous value of the variable prior to the change being indicated by
	 *         this VariableChangeEvent
	 */
	public T getOldValue()
	{
		return oldValue;
	}

	/**
	 * Returns the (current and) new value of the variable after the change being
	 * indicated by this VariableChangeEvent.
	 * 
	 * @return The (current and) new value of the variable after the change being
	 *         indicated by this VariableChangeEvent
	 */
	public T getNewValue()
	{
		return newValue;
	}
}
