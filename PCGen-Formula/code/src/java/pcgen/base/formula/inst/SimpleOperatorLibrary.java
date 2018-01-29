/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.UnaryAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;
import pcgen.base.util.HashMapToList;

/**
 * SimpleOperatorLibrary is a simple implementation of the OperatorLibrary
 * interface.
 */
public class SimpleOperatorLibrary implements OperatorLibrary
{

	/**
	 * HashMapToList from the Operators to the available OperatorActions for the
	 * Operator.
	 */
	private final HashMapToList<Operator, OperatorAction> operatorMTL =
			new HashMapToList<Operator, OperatorAction>();

	/**
	 * HashMapToList from the Operators to the available UnaryActions for the
	 * Operator.
	 */
	private final HashMapToList<Operator, UnaryAction> unaryMTL =
			new HashMapToList<Operator, UnaryAction>();

	@Override
	public void addAction(OperatorAction action)
	{
		operatorMTL.addToListFor(action.getOperator(), action);
	}

	@Override
	public void addAction(UnaryAction action)
	{
		unaryMTL.addToListFor(action.getOperator(), action);
	}

	@Override
	public Object evaluate(Operator operator, Object o)
	{
		List<UnaryAction> actionList = unaryMTL.getListFor(operator);
		if (actionList != null)
		{
			for (UnaryAction action : actionList)
			{
				/*
				 * null indicates the UnaryAction can't evaluate these, but we
				 * should try another in list (don't unconditionally fail
				 * because another UnaryAction might work)
				 */
				if (action.abstractEvaluate(o.getClass()) != null)
				{
					return action.evaluate(o);
				}
			}
		}
		throw new IllegalStateException(
			"Evaluate called on invalid Unary Operator: " + operator.getSymbol()
				+ " cannot process " + o.getClass().getSimpleName());
	}

	@Override
	public FormatManager<?> processAbstract(Operator operator, Class<?> format)
	{
		List<UnaryAction> actionList = unaryMTL.getListFor(operator);
		if (actionList != null)
		{
			for (UnaryAction action : actionList)
			{
				FormatManager<?> result = action.abstractEvaluate(format);
				/*
				 * null indicates the UnaryAction can't evaluate these, but try
				 * another (don't unconditionally return result because another
				 * UnaryAction might work)
				 */
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}

	@Override
	public Object evaluate(Operator operator, Object o1, Object o2,
		Optional<FormatManager<?>> asserted)
	{
		List<OperatorAction> actionList = operatorMTL.getListFor(operator);
		if (actionList != null)
		{
			for (OperatorAction action : actionList)
			{
				/*
				 * null indicates the OperatorAction can't evaluate these, but
				 * we should try another in list (don't unconditionally fail
				 * because another OperatorAction might work)
				 */
				if (action.abstractEvaluate(o1.getClass(),
					o2.getClass(), asserted) != null)
				{
					return action.evaluate(o1, o2);
				}
			}
		}
		throw new IllegalStateException(
			"Evaluate called on invalid Operator: " + operator.getSymbol()
				+ " cannot process " + o1.getClass().getSimpleName() + " and "
				+ o2.getClass().getSimpleName());
	}

	@Override
	public FormatManager<?> processAbstract(Operator operator, Class<?> format1,
		Class<?> format2, Optional<FormatManager<?>> asserted)
	{
		List<OperatorAction> actionList = operatorMTL.getListFor(operator);
		if (actionList != null)
		{
			for (OperatorAction action : actionList)
			{
				FormatManager<?> result =
						action.abstractEvaluate(format1, format2, asserted);
				/*
				 * null indicates the OperatorAction can't evaluate these, but
				 * try another (don't unconditionally return result because
				 * another OperatorAction might work)
				 */
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}

}
