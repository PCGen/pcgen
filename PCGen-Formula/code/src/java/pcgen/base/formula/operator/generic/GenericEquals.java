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
package pcgen.base.formula.operator.generic;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * GenericEquals performs an equality comparison on two values.
 */
public class GenericEquals implements OperatorAction
{

	/**
	 * Indicates that GenericEquals Performs a comparison for logical equality.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.EQ;
	}

	/**
	 * Performs Abstract Evaluation, checking that the two arguments are
	 * of matching classes and returns BooleanManager.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted)
	{
		if (format1.isArray() || format2.isArray())
		{
			return Optional.empty();
		}
		if (format1.equals(format2))
		{
			return Optional.of(FormatUtilities.BOOLEAN_MANAGER);
		}
		return Optional.empty();
	}

	/**
	 * Performs a logical equality comparison on the given arguments.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(Object left, Object right)
	{
		return left.equals(Objects.requireNonNull(right));
	}

}
