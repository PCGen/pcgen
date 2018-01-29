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

import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.ArrayUtilities;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Tuple;

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
	public FormatManager<?> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted)
	{
		if (!asserted.isPresent())
		{
			return null;
		}
		FormatManager<?> formatManager = asserted.get();
		if (!formatManager.getManagedClass().isArray() || !format1.isArray())
		{
			return null;
		}
		FormatManager<?> assertedComponent = formatManager.getComponentManager();
		Class<?> firstComponent = format1.getComponentType();
		Class<?> secondComponent =
				format2.isArray() ? format2.getComponentType() : format2;
		if (!assertedComponent.getManagedClass().isAssignableFrom(firstComponent)
			|| !assertedComponent.getManagedClass().isAssignableFrom(secondComponent))
		{
			return null;
		}
		return formatManager;
	}

	@Override
	public Object evaluate(Object left, Object right)
	{
		Object[] removes =
				right.getClass().isArray() ? (Object[]) right : new Object[]{right};
		Tuple<List<Object>, List<Object>> diff =
				ArrayUtilities.calculateDifference((Object[]) left, removes);
		List<Object> resultList = diff.getSecond();
		return resultList.toArray(new Object[]{resultList.size()});
	}
}
