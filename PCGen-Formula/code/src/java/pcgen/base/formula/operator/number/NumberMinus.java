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
package pcgen.base.formula.operator.number;

import pcgen.base.formula.base.UnaryAction;
import pcgen.base.formula.parse.Operator;

/**
 * NumberAdd performs negation of a Number value.
 */
public class NumberMinus implements UnaryAction
{

	/**
	 * Cache of the Number class.
	 */
	private static final Class<Number> NUMBER_CLASS = Number.class;

	/**
	 * Indicates that NumberMinus Performs Negation.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.MINUS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> abstractEvaluate(Class<?> format)
	{
		if (NUMBER_CLASS.isAssignableFrom(format))
		{
			return NUMBER_CLASS;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(Object object)
	{
		if (object instanceof Integer)
		{
			return -(Integer) object;
		}
		return -((Number) object).doubleValue();
	}

}
