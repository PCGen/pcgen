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
		if (actionList == null)
		{
			throw new IllegalStateException(
				"Evaluate called on invalid Unary Operator: "
					+ operator.getSymbol() + " cannot process "
					+ o.getClass().getSimpleName());
		}
		return actionList.stream()
				.filter(
					action -> action.abstractEvaluate(o.getClass()).isPresent())
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
					"Evaluate called on invalid Unary Operator: "
							+ operator.getSymbol() + " cannot process "
							+ o.getClass().getSimpleName()))
				.evaluate(o);
	}

	@Override
	public Optional<FormatManager<?>> processAbstract(Operator operator, Class<?> format)
	{
		List<UnaryAction> actionList = unaryMTL.getListFor(operator);
		if (actionList == null)
		{
			return Optional.empty();
		}
		//Return the first action that works
		return actionList.stream()
				.map(action -> action.abstractEvaluate(format))
				.filter(o -> o.isPresent())
				.findFirst()
				.orElse(Optional.empty());
	}

	@Override
	public Object evaluate(Operator operator, Object left, Object right,
		Optional<FormatManager<?>> asserted)
	{
		List<OperatorAction> actionList = operatorMTL.getListFor(operator);
		if (actionList == null)
		{
			throw new IllegalStateException(
				"Evaluate called on invalid Operator: " + operator.getSymbol()
				+ " cannot process " + left.getClass().getSimpleName() + " and "
				+ right.getClass().getSimpleName());
		}
		return actionList.stream()
				.filter(action -> action
					.abstractEvaluate(left.getClass(), right.getClass(), asserted)
					.isPresent())
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
					"Evaluate called on invalid Operator: "
							+ operator.getSymbol() + " cannot process "
							+ left.getClass().getSimpleName() + " and "
							+ right.getClass().getSimpleName()))
				.evaluate(left, right);
	}

	@Override
	public Optional<FormatManager<?>> processAbstract(Operator operator, Class<?> format1,
		Class<?> format2, Optional<FormatManager<?>> asserted)
	{
		List<OperatorAction> actionList = operatorMTL.getListFor(operator);
		if (actionList == null)
		{
			return Optional.empty();
		}
		//Return the first action that works
		return actionList.stream()
				.map(action -> action.abstractEvaluate(format1, format2, asserted))
				.filter(o -> o.isPresent())
				.findFirst()
				.orElse(Optional.empty());
	}

}
