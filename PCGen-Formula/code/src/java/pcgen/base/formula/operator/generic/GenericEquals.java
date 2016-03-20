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

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;

/**
 * BooleanEquals performs an equality comparison on two Boolean values.
 */
public class GenericEquals implements OperatorAction
{

	/**
	 * Cache of the Boolean class.
	 */
	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;

	/**
	 * Indicates that BooleanEquals Performs a comparison for logical equality.
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
	 * Boolean.class and returns Boolean.class.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> abstractEvaluate(Class<?> format1, Class<?> format2)
	{
		if (format1.equals(format2))
		{
			return BOOLEAN_CLASS;
		}
		return null;
	}

	/**
	 * Performs a logical equality comparison on the given arguments.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(Object l, Object r)
	{
		if (r == null)
		{
			throw new NullPointerException("object in equality cannot be null");
		}
		return l.equals(r);
	}

}
