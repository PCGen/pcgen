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
package pcgen.base.formula.operator.bool;

import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.UnaryAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * BooleanNot performs negation on a Boolean value.
 */
public class BooleanNot implements UnaryAction
{

	/**
	 * Indicates that BooleanNot Performs Negation.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.NOT;
	}

	@Override
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format)
	{
		if (FormatUtilities.BOOLEAN_CLASS.isAssignableFrom(format))
		{
			return Optional.of(FormatUtilities.BOOLEAN_MANAGER);
		}
		return Optional.empty();
	}

	@Override
	public Object evaluate(Object argument)
	{
		return !((Boolean) argument);
	}

}
