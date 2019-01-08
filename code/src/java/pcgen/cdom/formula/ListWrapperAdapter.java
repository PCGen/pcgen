/*
 * Copyright (c) Thomas Parker, 2018-9
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pcgen.facade.util.AbstractListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 * A ListWrapperAdapter is an Adapter that presents a ListFacade interface to the UI,
 * while maintaining interaction to a single variable composed of an Array.
 * 
 * @param <T>
 *            The type of object in the list/underlying array
 */
public class ListWrapperAdapter<T> extends AbstractListFacade<T>
		implements ListFacade<T>, ReferenceListener<T[]>
{
	/**
	 * The underlying variable channel, that is an array variable.
	 */
	private final VariableWrapper<T[]> variableWrapper;

	/**
	 * Constructs a new ListWrapperAdapter for the given underlying VariableChannel.
	 * 
	 * @param underlyingWrapper
	 *            The underlying VariableChannel for this ListWrapperAdapter
	 */
	public ListWrapperAdapter(VariableWrapper<T[]> underlyingWrapper)
	{
		this.variableWrapper = Objects.requireNonNull(underlyingWrapper);
		variableWrapper.addReferenceListener(this);
	}

	@Override
	public T getElementAt(int index)
	{
		return variableWrapper.get()[index];
	}

	@Override
	public int getSize()
	{
		return variableWrapper.get().length;
	}

	@Override
	public void referenceChanged(ReferenceEvent<T[]> e)
	{
		T[] oldArray = e.getOldReference();
		List<T> oldList = Arrays.asList(oldArray);
		T[] newArray = e.getNewReference();
		List<T> newList = Arrays.asList(newArray);
		for (int i = oldArray.length; i >= 0 ; i--)
		{
			if (!newList.contains(oldArray[i]))
			{
				fireElementRemoved(this, oldArray[i], i);
			}
		}
		for (int i = 0; i < newArray.length; i++)
		{
			if (!oldList.contains(newArray[i]))
			{
				fireElementAdded(this, newArray[i], i);
			}
		}
	}
}
