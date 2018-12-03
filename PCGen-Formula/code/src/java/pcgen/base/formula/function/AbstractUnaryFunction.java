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
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
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
public abstract class AbstractUnaryFunction implements FormulaFunction
{

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1 got " + args.length
				+ " " + Arrays.asList(args));
		}
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format =
				(FormatManager<?>) args[0].jjtAccept(visitor, semantics);
		if (!format.equals(FormatUtilities.NUMBER_MANAGER))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + format + " found in "
					+ args[0].getClass().getName() + " found in location requiring a"
					+ " Number (class cannot be evaluated)");
		}
		return FormatUtilities.NUMBER_MANAGER;
	}

	@Override
	public final Number evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		return evaluate((Number) args[0].jjtAccept(visitor, manager));
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		return (Optional<FormatManager<?>>) args[0].jjtAccept(visitor, manager);
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
