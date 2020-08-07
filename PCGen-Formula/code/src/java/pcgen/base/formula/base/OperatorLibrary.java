/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

import java.util.Optional;

import pcgen.base.formula.parse.Operator;
import pcgen.base.util.FormatManager;

/**
 * A OperatorLibrary is a container for OperatorAction objects. These define the actual
 * behavior of an operator like + or - in a formula.
 */
public interface OperatorLibrary
{

	/**
	 * Adds an OperatorAction to the FunctionLibrary.
	 * 
	 * OperatorLibrary does not define the behavior if an object attempts to add null or
	 * attempts to add an OperatorAction with a null name. An exception may be thrown
	 * (implementation dependent).
	 * 
	 * @param action
	 *            The OperatorAction to be added to the OperatorLibrary
	 */
	public void addAction(OperatorAction action);

	/**
	 * Perform an evaluation of the given Operator with the two given objects as
	 * arguments.
	 * 
	 * The actual OperatorAction to be performed is the first OperatorAction added to the
	 * OperatorLibrary which can operate on the two arguments. This "operation test" is
	 * performed by checking the results of the processAbstract method on the classes of
	 * the two given objects.
	 * 
	 * If no OperatorAction has been added to the OperatorLibrary that can process the
	 * given arguments, an IllegalStateException is returned. (The user should have
	 * checked with processAbstract)
	 * 
	 * The return value of evaluate is part of a contract with processAbstract.
	 * If processAbstract returns a non-empty value, then this method should
	 * return a non-null value. If processAbstract returns empty, then this
	 * method should throw an Exception.
	 * 
	 * @param operator
	 *            The Operator to be evaluated
	 * @param o1
	 *            The first argument to the operation
	 * @param o2
	 *            The second argument to the operation
	 * @param asserted
	 *            An Optional FormatManager indicating the asserted format for the
	 *            variables. This is not guaranteed to perform a deep evaluation; though
	 *            some operators may perform a sanity check. Some operators may not
	 *            require this argument; for others the Operator may not be used if no
	 *            assertion is provided.
	 * @return The result of the operation, if this OperatorLibrary has an OperatorAction
	 *         for the given Operator and arguments
	 * @throws IllegalStateException
	 *             if this OperatorLibrary did not have an OperatorAction for the given
	 *             Operator and arguments
	 */
	public Object evaluate(Operator operator, Object o1, Object o2,
		Optional<FormatManager<?>> asserted);

	/**
	 * Processes an "abstract" version of the operation, performing a prediction of the
	 * returned Class rather than on actual objects.
	 * 
	 * Note that an OperatorAction (and thus by extension OperatorLibrary) provides a
	 * prediction of the returned Class, not the actual class. The returned Class from
	 * this method is guaranteed to be assignable from the actual result. In other words,
	 * this may return a FormatManager for Number.class, whereas evaluate may return an
	 * Integer or Double.
	 * 
	 * The return value of processAbstract is part of a contract with evaluate. If this
	 * method returns a non-empty value, then evaluate should return a non-null value. If
	 * this method returns empty, then evaluate should throw an exception.
	 * 
	 * @param operator
	 *            The Operator to be evaluated
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
	 * @return An Optional FormatManager for the data format of the result of the operation if this
	 *         OperatorLibrary has an OperatorAction for the given Operator arguments
	 */
	public Optional<FormatManager<?>> processAbstract(Operator operator, Class<?> format1,
		Class<?> format2, Optional<FormatManager<?>> asserted);

	/**
	 * Adds a UnaryAction to the FunctionLibrary.
	 * 
	 * OperatorLibrary does not define the behavior if an object attempts to add null or
	 * attempts to add an UnaryAction with a null name. An exception may be thrown
	 * (implementation dependent).
	 * 
	 * @param action
	 *            The UnaryAction to be added to the OperatorLibrary
	 */
	public void addAction(UnaryAction action);

	/**
	 * Perform an evaluation of the given Operator with the given object as an argument.
	 * 
	 * The actual UnaryAction to be performed is the first UnaryAction added to the
	 * OperatorLibrary which can operate on the argument. This "operation test" is
	 * performed by checking the results of the processAbstract method on the class of the
	 * given object.
	 * 
	 * If no UnaryAction has been added to the OperatorLibrary that can process the given
	 * argument, an IllegalStateException is returned. (The user should have checked with
	 * processAbstract)
	 * 
	 * The return value of evaluate is part of a contract with processAbstract.
	 * If processAbstract returns a non-empty value, then this method should
	 * return a non-null value. If processAbstract returns empty, then this
	 * method should throw an Exception.
	 * 
	 * @param operator
	 *            The Operator to be evaluated
	 * @param argument
	 *            The argument to the operation
	 * @return The result of the operation, if this OperatorLibrary has a UnaryAction for
	 *         the given Operator and argument
	 * @throws IllegalStateException
	 *             if this OperatorLibrary did not have a UnaryAction for the given
	 *             Operator and argument
	 */
	public Object evaluate(Operator operator, Object argument);

	/**
	 * Processes an "abstract" version of the operation, performing a prediction of the
	 * returned Class rather than on an actual object.
	 * 
	 * Note that a UnaryAction (and thus by extension OperatorLibrary) provides a
	 * prediction of the returned Class, not the actual class. The returned Class from
	 * this method is guaranteed to be assignable from the actual result. In other words,
	 * this may return a FormatManager for Number.class, whereas evaluate may return an
	 * Integer or Double.
	 * 
	 * The return value of processAbstract is part of a contract with evaluate. If this
	 * method returns a non-empty value, then evaluate should return a non-null value. If
	 * this method returns empty, then evaluate should throw an exception.
	 * 
	 * @param operator
	 *            The Operator to be evaluated
	 * @param format
	 *            The class (data format) of the argument to the abstract operation
	 * @return An Optional FormatManager for the data format of the result of the operation if this
	 *         OperatorLibrary has an OperatorAction for the given Operator arguments
	 */
	public Optional<FormatManager<?>> processAbstract(Operator operator, Class<?> format);
}
