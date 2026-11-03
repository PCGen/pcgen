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
package pcgen.base.formula.base;

import java.util.Optional;

import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * An OperatorAction is used to process an operator (e.g. + or ==) in a Formula.
 * 
 * This interface is an abstract definition of the operation to be performed
 * (evaluate always takes Object) as the formula system can process multiple
 * formats of variables.
 * 
 * It is a conscious decision to have evaluate take Object, Object as parameters
 * and do casting. Given the abstract definition, it is possible for more than
 * one OperatorAction to act upon a given operator. This is generally done with
 * different classes being processed by an OperatorAction. This "selection" of
 * the appropriate OperatorAction is often performed by an OperatorLibrary, and
 * is typically done by using the abstractEvaluate method of the OperatorAction
 * objects.
 * 
 * The reason for Object is easier definition and use, without any material
 * penalty. The cast must occur *either way* (here or EvaluationVisitor) based
 * on how the visitation works. The difference is whether the system makes
 * multiple types of operators and thus adds fields to SimpleNode to hold the
 * different types (or worse - holds them in once place and does instanceof
 * checks). Since that has limited value (doesn't avoid operations, just changes
 * where they reside), it doesn't seem worthwhile and all Operators are
 * centralized by using Object, Object.
 * 
 * The Operators that perform mathematical functions should attempt to perform
 * Integer math if that is sensible for the given situation, but the system is
 * conservative and does not strictly guarantee Integer math in every legal
 * situation. For example, it may not perform Integer division due to risk of
 * rounding, even if the two incoming values divide into an integer.
 */
public interface OperatorAction
{

	/**
	 * Returns the Operator that this OperatorAction represents.
	 * 
	 * @return The Operator that this OperatorAction represents
	 */
	public Operator getOperator();

	/**
	 * Processes an "abstract" version of the operation, performing a prediction of the
	 * format of the returned Class rather than on actual objects.
	 * 
	 * If the OperatorAction cannot perform an action on objects of the given classes,
	 * then the OperatorAction must return Optional.empty() from this method. An exception
	 * should not be thrown to indicate incompatibility.
	 * 
	 * Note that this provides a prediction of the returned format, not the actual class.
	 * However, the returned FormatManager from this method is guaranteed to be
	 * appropriate for the actual result. In other words, this may return a FormatManager
	 * for Number.class, whereas evaluate may return an Integer or Double.
	 * 
	 * The return value of abstractEvaluate is part of a contract with evaluate. If this
	 * method returns a non-empty value, then evaluate should return a non-null value. If
	 * this method returns empty, then evaluate should throw an exception.
	 * 
	 * @param format1
	 *            The class (data format) of the first argument to the abstract operation
	 * @param format2
	 *            The class (data format) of the second argument to the abstract operation
	 * @param asserted
	 *            The Optional FormatManager indicating the asserted format for the
	 *            variables. This is not guaranteed to perform a deep evaluation; though
	 *            some operators may perform a sanity check. Some operators may not
	 *            require this argument; for others the Operator may not be used if no
	 *            assertion is provided.
	 * @return An Optional FormatManager for the data format of the result of the
	 *         operation if this OperatorAction can process objects of the given classes
	 */
	public Optional<FormatManager<?>> abstractEvaluate(Class<?> format1, Class<?> format2,
		Optional<FormatManager<?>> asserted);

	/**
	 * Perform an evaluation with the two given objects as arguments and returns
	 * a non-null result of the evaluation.
	 * 
	 * The return value of evaluate is part of a contract with abstractEvaluate.
	 * If abstractEvaluate returns a non-empty value, then this method should
	 * return a non-null value. If abstractEvaluate returns empty, then this
	 * method should throw an Exception.
	 * 
	 * @param left
	 *            The first argument to the operation
	 * @param right
	 *            The second argument to the operation
	 * @return The result of the operation
	 */
	public Object evaluate(Object left, Object right);

}
