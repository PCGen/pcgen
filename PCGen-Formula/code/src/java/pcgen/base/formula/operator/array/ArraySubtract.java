/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * ArraySubtract performs a difference on two Arrays or an Array and an individual item.
 */
public class ArraySubtract implements OperatorAction
{

	@Override
	public Operator getOperator()
	{
		return Operator.SUB;
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
		if (!formatManager.getManagedClass().isArray() || !format1.isArray())
		{
			return Optional.empty();
		}
		Optional<FormatManager<?>> assertedComponent = formatManager.getComponentManager();
		if (assertedComponent.isEmpty())
		{
			return Optional.empty();
		}
		Class<?> firstComponent = format1.getComponentType();
		Class<?> secondComponent =
				format2.isArray() ? format2.getComponentType() : format2;
		if (!assertedComponent.get().getManagedClass().isAssignableFrom(firstComponent)
			|| !assertedComponent.get().getManagedClass().isAssignableFrom(secondComponent))
		{
			return Optional.empty();
		}
		return Optional.of(formatManager);
	}

	@Override
	public Object evaluate(Object left, Object right)
	{
		if (!left.getClass().isArray())
		{
			throw new IllegalArgumentException(
				"First argument to ArraySubtract must be an array");
		}
		List<Object> toRemoveList = right.getClass().isArray()
			? Arrays.asList((Object[]) right) : Collections.singletonList(right);
		List<Object> originalList = new ArrayList<>(Arrays.asList((Object[]) left));
		//WARNING: This is NOT .removeAll since we are respecting quantity
		for (Object o : toRemoveList)
		{
			originalList.remove(o);
		}
		return originalList.toArray(new Object[]{originalList.size()});
	}
}
