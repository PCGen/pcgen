/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.operator.array;

import java.util.Optional;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.ArrayUtilities;
import pcgen.base.util.FormatManager;

/**
 * ArrayAdd performs concatenation of two items that are array-compatible. This could be
 * two arrays, an array and an item (in any order), or two items.
 */
public class ArrayAdd implements OperatorAction
{

	@Override
	public Operator getOperator()
	{
		return Operator.ADD;
	}

	@Override
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted)
	{
		if (!asserted.isPresent())
		{
			return Optional.empty();
		}
		FormatManager<?> formatManager = asserted.get();
		if (!formatManager.getManagedClass().isArray())
		{
			return Optional.empty();
		}
		Optional<FormatManager<?>> assertedComponent = formatManager.getComponentManager();
		if (assertedComponent.isEmpty())
		{
			return Optional.empty();
		}
		if (!isCompatible(assertedComponent, format1)
			|| !isCompatible(assertedComponent, format2))
		{
			return Optional.empty();
		}
		return Optional.of(formatManager);
	}

	private boolean isCompatible(Optional<FormatManager<?>> assertedComponent, Class<?> format)
	{
		Class<?> component = format.isArray() ? format.getComponentType() : format;
		return assertedComponent.get().getManagedClass().isAssignableFrom(component);
	}

	@Override
	public Object evaluate(Object left, Object right)
	{
		if (left.getClass().isArray() && right.getClass().isArray())
		{
			return ArrayUtilities.mergeArray(Object.class, (Object[]) left,
				(Object[]) right);
		}
		if (left.getClass().isArray())
		{
			Object[] array = (Object[]) left;
			int arrayLength = array.length;
			Object[] returnArray =
					ArrayUtilities.buildOfClass(Object.class).apply(arrayLength + 1);
			System.arraycopy(array, 0, returnArray, 0, arrayLength);
			returnArray[arrayLength] = right;
			return returnArray;
		}
		if (right.getClass().isArray())
		{
			Object[] array = (Object[]) right;
			int arrayLength = array.length;
			Object[] returnArray =
					ArrayUtilities.buildOfClass(Object.class).apply(arrayLength + 1);
			//Preserve order
			returnArray[0] = left;
			System.arraycopy(array, 0, returnArray, 1, arrayLength);
			return returnArray;
		}
		return new Object[]{left, right};
	}

}
