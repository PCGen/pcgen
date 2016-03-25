/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.operator.string;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;

/**
 * StringAdd performs concatenation on two String values.
 */
public class StringAdd implements OperatorAction
{

	/**
	 * Cache of the String class.
	 */
	private static final Class<String> STRING_CLASS = String.class;

	/**
	 * Indicates that StringAdd Performs Concatenation of Strings.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.ADD;
	}

	/**
	 * Performs Abstract Evaluation, checking that the two arguments are
	 * String.class and returns String.class.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> abstractEvaluate(Class<?> format1, Class<?> format2)
	{
		if (STRING_CLASS.isAssignableFrom(format1)
			&& STRING_CLASS.isAssignableFrom(format2))
		{
			return STRING_CLASS;
		}
		return null;
	}

	/**
	 * Performs concatenation on the given arguments.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(Object left, Object right)
	{
		if ((left == null) || (right == null))
		{
			throw new NullPointerException(
				"Object to evaluate cannot be null: " + left + " + " + right);
		}
		return (String) left + (String) right;
	}

}
