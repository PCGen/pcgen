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
package pcgen.base.formula.base;

import pcgen.base.formula.parse.Operator;

public interface UnaryAction
{

	/**
	 * Returns the Operator that this UnaryAction represents.
	 * 
	 * @return The Operator that this UnaryAction represents
	 */
	public Operator getOperator();

	/**
	 * Processes an "abstract" version of the operation, performing a prediction
	 * of the returned Class rather than on an actual object.
	 * 
	 * If the OperatorAction cannot perform an action on the object of the given
	 * class, then the OperatorAction will return null from this method. An
	 * exception should not be thrown to indicate incompatibility.
	 * 
	 * Note that this provides a prediction of the returned Class, not the
	 * actual class. However, the returned Class from this method is guaranteed
	 * to be assignable from than the actual result. In other words, this may
	 * return Number.class, whereas evaluate may return an Integer or Double.
	 * 
	 * The return value of abstractEvaluate is part of a contract with evaluate.
	 * If this method returns a non-null value, then evaluate should return a
	 * non-null value. If this method returns null, then evaluate should throw
	 * an exception.
	 * 
	 * @param format
	 *            The class (data format) of the argument to the abstract
	 *            operation
	 * @return The class (data format) of the result of the operation if this
	 *         OperatorAction can process objects of the given classes; null
	 *         otherwise
	 */
	public Class<?> abstractEvaluate(Class<?> format);

	/**
	 * Perform an evaluation with the given object as an argument and returns a
	 * non-null result of the evaluation.
	 * 
	 * This method requires that abstractEvaluate called on the class of the
	 * given argument would not return null. In other words, if abstractEvaluate
	 * would have returned null when called with the class of the given
	 * argument, then evaluate should throw an Exception.
	 * 
	 * The return value of evaluate is part of a contract with abstractEvaluate.
	 * If abstractEvaluate returns a non-null value, then this method should
	 * return a non-null value. If abstractEvaluate returns null, then this
	 * method should throw an Exception.
	 * 
	 * @param o
	 *            The argument to the operation
	 * @return The result of the operation
	 */
	public Object evaluate(Object o);

}
