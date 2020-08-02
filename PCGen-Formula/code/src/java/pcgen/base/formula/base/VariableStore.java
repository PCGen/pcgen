/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

import java.util.Collection;

/**
 * A VariableStore is a storage location for the values of Variables.
 * 
 * Note: A VariableStore MAY have behavior that varies from Map (which has
 * similar methods). The reason is that a VariableStore may calculate values as
 * required or may have them cached. That is not defined by the Formula parsing
 * system. As a result, it is advised to reference the specific implementation
 * of VariableStore to understand whether one should unconditionally use
 * containsKey() before get().
 * 
 * Neither a null key or null value are allowed.
 */
public interface VariableStore
{

	/**
	 * Returns the value in this VariableStore for the given VariableID.
	 * 
	 * It is necessary to check the implementation to determine if containsKey
	 * must be called before get().
	 * 
	 * Will return null if there is no value stored for the given VariableID.
	 * 
	 * While a null id is allowed as a parameter, the result will be
	 * unconditionally null for a return value, as the null id is not allowed in
	 * a VariableStore.
	 * 
	 * Note the possible performance characteristics for this method referenced
	 * in the class description.
	 * 
	 * @param <T>
	 *            The format of variable to be retrieved from this VariableStore
	 * @param varID
	 *            The VariableID for which the stored value should be returned
	 * @return Returns the value in this VariableStore for the given VariableID
	 */
	public <T> T get(VariableID<T> varID);

	/**
	 * Returns true if this VariableStore contains a value for the given
	 * VariableID; false otherwise.
	 * 
	 * If this method returns true, then it is guaranteed that the get method
	 * will not return null.
	 * 
	 * @param varID
	 *            The VariableID for which the VariableStore will identify if it
	 *            contains a value
	 * @return true if this VariableStore contains a value for the given
	 *         VariableID; false otherwise
	 */
	public boolean containsVariable(VariableID<?> varID);

	/**
	 * Returns a Collection of the VariableID objects in this VariableStore.
	 * 
	 * @return A Collection of the VariableID objects in this VariableStore.
	 */
	public Collection<VariableID<?>> getVariables();

}
