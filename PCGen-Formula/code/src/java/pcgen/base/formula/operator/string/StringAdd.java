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

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * StringAdd performs concatenation on two String values.
 */
public class StringAdd implements OperatorAction
{

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
	 * String.class and returns StringManager.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted)
	{
		if (FormatUtilities.STRING_CLASS.isAssignableFrom(format1)
			&& FormatUtilities.STRING_CLASS.isAssignableFrom(format2))
		{
			return Optional.of(FormatUtilities.STRING_MANAGER);
		}
		return Optional.empty();
	}

	/**
	 * Performs concatenation on the given arguments.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(Object left, Object right)
	{
		/*
		 * DO NOT attempt to change this to have A.toString() + B and have
		 * .toString implied on B. That will not catch errors where the items
		 * are not Strings.  Please leave the casting as indicated.
		 * 
		 * See: StringAddTest.testEvaluateMismatch unit test
		 */
		return (String) Objects.requireNonNull(left)
			+ (String) Objects.requireNonNull(right);
	}

}
