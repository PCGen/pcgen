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

import java.util.Optional;

import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * A UnaryAction is used to process a unary operator (e.g. ! or -) in a Formula.
 * 
 * This interface is an abstract definition of the operation to be performed
 * (evaluate always takes Object) as the formula system can process multiple
 * formats of variables.
 * 
 * It is a conscious decision to have evaluate take Object, Object as parameters
 * and do casting. Given the abstract definition, it is possible for more than
 * one UnaryAction to act upon a given operator. This is generally done with
 * different classes being processed by a UnaryAction. This "selection" of the
 * appropriate UnaryAction is often performed by an OperatorLibrary, and is
 * typically done by using the abstractEvaluate method of the UnaryAction
 * objects.
 * 
 * The reason for Object is easier definition and use, without any material
 * penalty. The cast must occur *either way* (here or EvaluationVisitor) based
 * on how the visitation works. The difference is whether the system makes
 * multiple types of operators and thus adds fields to SimpleNode to hold the
 * different types (or worse - holds them in once place and does instanceof
 * checks). Since that has limited value (doesn't avoid operations, just changes
 * where they reside), it doesn't seem worthwhile and all Operators are
 * centralized by using Object as the class of the parameter.
 */
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
	 * If the UnaryAction cannot perform an action on the object of the given
	 * class, then the UnaryAction must return Optional.empty() from this method.
	 * An exception should not be thrown to indicate incompatibility.
	 * 
	 * Note that this provides a prediction of the returned Class, not the
	 * actual class. However, the returned Class from this method is guaranteed
	 * to be assignable from than the actual result. In other words, this may
	 * return Number.class, whereas evaluate may return an Integer or Double.
	 * 
	 * The return value of abstractEvaluate is part of a contract with evaluate.
	 * If this method returns a non-empty value, then evaluate should return a
	 * non-null value. If this method returns empty, then evaluate should throw
	 * an exception.
	 * 
	 * @param format
	 *            The class (data format) of the argument to the abstract
	 *            operation
	 * @return An Optional FormatManager for the data format of the result of the
	 *         operation if this UnaryAction can process objects of the given
	 *         class
	 */
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format);

	/**
	 * Perform an evaluation with the given object as an argument and returns a
	 * non-null result of the evaluation.
	 * 
	 * The return value of evaluate is part of a contract with abstractEvaluate.
	 * If abstractEvaluate returns a non-empty value, then this method should
	 * return a non-null value. If abstractEvaluate returns empty, then this
	 * method should throw an Exception.
	 * 
	 * @param o
	 *            The argument to the operation
	 * @return The result of the operation
	 */
	public Object evaluate(Object o);

}
