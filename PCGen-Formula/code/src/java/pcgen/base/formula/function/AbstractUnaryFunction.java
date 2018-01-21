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
package pcgen.base.formula.function;

import java.util.Arrays;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * AbstractUnaryFunction centralizes common behaviors for Functions that return
 * a Number and only take one argument.
 */
public abstract class AbstractUnaryFunction implements Function
{

	/**
	 * Checks if the given arguments are valid using the given SemanticsVisitor.
	 * Only one argument is allowed, and it must be a valid formula value
	 * (number, variable, another function, etc.)
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1 got "
				+ args.length + " " + Arrays.asList(args));
			return null;
		}
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format =
				(FormatManager<?>) args[0].jjtAccept(visitor, semantics);
		if (!semantics.isValid())
		{
			return null;
		}
		if (!format.equals(FormatUtilities.NUMBER_MANAGER))
		{
			semantics.setInvalid("Parse Error: Invalid Value Format: " + format
				+ " found in " + args[0].getClass().getName()
				+ " found in location requiring a"
				+ " Number (class cannot be evaluated)");
			return null;
		}
		return format;
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor. Only one
	 * argument is allowed, and it must be a valid numeric value.
	 * 
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value. See evaluate on the Function interface for important
	 * assumptions made when this method is called.
	 * 
	 * Actual processing is delegated to evaluate(Number)
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final Number evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		return evaluate((Number) args[0].jjtAccept(visitor, manager));
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value in a formula. See isStatic on the Function interface for
	 * important assumptions made when this method is called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
	}

	/**
	 * Captures dependencies of this function. This includes Variables (in the
	 * form of VariableIDs), but is not limited to those as the only possible
	 * dependency.
	 * 
	 * Consistent with the contract of the Function interface, this list
	 * recursively includes all of the contents of items within this function
	 * (if this function calls another function, etc. all variables in the tree
	 * below this function are included).
	 * 
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value in a formula. See getDependencies on the Function interface
	 * for important assumptions made when this method is called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public FormatManager<?> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		return (FormatManager<?>) args[0].jjtAccept(visitor, manager);
	}

	/**
	 * This method must be implemented by classes that extend
	 * AbstractUnaryFunction. It performs the evaluation on the given numeric
	 * value.
	 * 
	 * The contract for the AbstractUnaryFunction interface guarantees that the
	 * provided value will not be null, and the returned value may not be null.
	 * 
	 * @param n
	 *            The input value for the AbstractUnaryFunction
	 * @return The value calculated from the input value after applying the
	 *         AbstractUnaryFunction
	 */
	protected abstract Number evaluate(Number n);

}
