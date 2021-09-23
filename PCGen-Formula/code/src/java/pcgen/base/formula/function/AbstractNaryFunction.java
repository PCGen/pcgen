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
 * AbstractNaryFunction centralizes common behaviors for Functions that take a
 * variable number of Number arguments, with a minimum of two arguments.
 * 
 * It is important to understand that this is designed to work for functions
 * that use two values at a time, and when more than two are present, the
 * AbstractNaryFunction is associative (the order in which the arguments are processed does
 * not matter). This is true of functions like calculating a maximum value
 * (where the order is not significant as long as all values are processed).
 * 
 * This is implemented as a repeated check between two values in order to
 * process the variable number of arguments to the function.
 */
public abstract class AbstractNaryFunction implements FormulaFunction
{

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount < 2)
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected at least 2, got "
				+ args.length + " " + Arrays.asList(args));
		}
		for (Node n : args)
		{
			FunctionUtilities.ensureMatchingFormat(visitor, semantics, n,
				FormatUtilities.NUMBER_MANAGER);
		}
		return FormatUtilities.NUMBER_MANAGER;
	}

	@Override
	public final Number evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		int argCount = args.length;
		Number solution = (Number) args[0].jjtAccept(visitor, manager);
		//May be N args, so just loop until done
		for (int i = 1; i < argCount; i++)
		{
			Number next = (Number) args[i].jjtAccept(visitor, manager);
			solution = evaluate(solution, next);
		}
		return solution;
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		for (Node n : args)
		{
			Boolean result = (Boolean) n.jjtAccept(visitor, null);
			if (!result.booleanValue())
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		for (Node n : args)
		{
			n.jjtAccept(visitor, manager);
		}
		return Optional.of(FormatUtilities.NUMBER_MANAGER);
	}

	/**
	 * This method must be implemented by classes that extend
	 * AbstractNaryFunction. It performs the evaluation on the given numeric
	 * values.
	 * 
	 * @param n1
	 *            The first input value for the AbstractNaryFunction
	 * @param n2
	 *            the second input value for the AbstractNaryFunction
	 * @return The value calculated from the input values after applying the
	 *         AbstractNaryFunction
	 */
	protected abstract Number evaluate(Number n1, Number n2);
}
