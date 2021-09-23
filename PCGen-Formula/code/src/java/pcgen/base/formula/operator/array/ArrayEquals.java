/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * ArrayEquals performs an equality comparison on two Arrays. Note that this includes
 * CONTENT AND ORDER.
 */
public class ArrayEquals implements OperatorAction
{

	@Override
	public Operator getOperator()
	{
		return Operator.EQ;
	}

	@Override
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted)
	{
		if (format1.isArray() && format2.isArray() && format1.equals(format2))
		{
			return Optional.of(FormatUtilities.BOOLEAN_MANAGER);
		}
		return Optional.empty();
	}

	@Override
	public Object evaluate(Object left, Object right)
	{
		Object first = Objects.requireNonNull(left);
		Object second = Objects.requireNonNull(right);
		return Arrays.deepEquals((Object[]) first, (Object[]) second);
	}

}
