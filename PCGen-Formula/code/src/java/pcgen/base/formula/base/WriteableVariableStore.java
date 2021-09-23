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

/**
 * A WriteableVariableStore is a storage location for the values of Variables. In
 * this case, it can be written, so the exposure of this interface should be
 * limited to calculating objects, not consuming objects.
 * 
 * Neither a null key or null value are allowed.
 */
public interface WriteableVariableStore extends VariableStore
{
	/**
	 * Adds the given non-null value to this WriteableVariableStore for the
	 * given (non-null) VariableID. Returns the previous value stored in this
	 * WriteableVariableStore for the given VariableID.
	 * 
	 * The returned value may be null if the VariableID was not previously
	 * stored in this WriteableVariableStore. The VariableStore must throw an
	 * exception if the given VariableID or value are null.
	 * 
	 * @param <T>
	 *            The format of variable to be stored within this
	 *            WriteableVariableStore
	 * @param varID
	 *            The VariableID used to index the value to be stored in this
	 *            WriteableVariableStore
	 * @param value
	 *            The value to be stored in this WriteableVariableStore
	 * @return The previous value stored in this WriteableVariableStore for the
	 *         given VariableID
	 */
	public <T> T put(VariableID<T> varID, T value);
	
	/**
	 * Imports the VariableIDs and values from the given VariableStore to this
	 * WriteableVariableStore. If a value for any VariableID already exists in this
	 * WriteableVariableStore, the value is overwritten by the value in the given
	 * VariableStore.
	 * 
	 * @param varStore
	 *            The VariableStore from which the VariableIDs and values should be
	 *            imported
	 */
	public void importFrom(VariableStore varStore);

}
