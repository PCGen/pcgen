/*
 * Copyright (c) Thomas Parker, 2018
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.formula;

import java.util.Objects;

import pcgen.base.util.ArrayUtilities;
import pcgen.facade.util.AbstractListFacade;
import pcgen.facade.util.WriteableListFacade;

/**
 * A ListChannelAdapter is an Adapter that presents a ListFacade interface to the UI,
 * while maintaining interaction to a single variable channel composed of an Array.
 * 
 * @param <T>
 *            The type of object in the list/underlying array
 */
public class ListChannelAdapter<T> extends AbstractListFacade<T>
		implements WriteableListFacade<T>
{

	/**
	 * The underlying variable channel, that is an array variable.
	 */
	private final VariableChannel<T[]> variableChannel;

	/**
	 * Constructs a new ListChannelAdapter for the given underlying VariableChannel.
	 * 
	 * @param underlyingChannel
	 *            The underlying VariableChannel for this ListChannelAdapter
	 */
	public ListChannelAdapter(VariableChannel<T[]> underlyingChannel)
	{
		this.variableChannel = Objects.requireNonNull(underlyingChannel);
	}

	@Override
	public T getElementAt(int index)
	{
		return variableChannel.get()[index];
	}

	@Override
	public int getSize()
	{
		return variableChannel.get().length;
	}

	@Override
	public void addElement(T element)
	{
		Class<?> variableFormat =
				variableChannel.getVariableID().getVariableFormat();
		variableChannel.set(ArrayUtilities.addOnCopy(variableChannel.get(),
			element, (Class<T>) variableFormat.getComponentType()));
		fireElementAdded(this, element, getSize());
	}

	@Override
	public void addElement(int index, T element)
	{
		Class<?> variableFormat =
				variableChannel.getVariableID().getVariableFormat();
		variableChannel.set(ArrayUtilities.addOnCopy(variableChannel.get(),
			index, element, (Class<T>) variableFormat.getComponentType()));
		fireElementAdded(this, element, index);
	}

	@Override
	public boolean removeElement(T element)
	{
		T[] oldArray = variableChannel.get();
		int foundLoc = -1;
		int newSize = oldArray.length - 1;
		for (int i = 0; i <= newSize; i++)
		{
			if (oldArray[i] == element)
			{
				foundLoc = i;
				break;
			}
		}
		if (foundLoc == -1)
		{
			return false;
		}
		removeElement(foundLoc);
		return true;
	}

	@Override
	public void removeElement(int index)
	{
		T[] array = variableChannel.get();
		T element = array[index];
		variableChannel.set(ArrayUtilities.removeOnCopy(array, index));
		fireElementRemoved(this, element, index);
	}

}
