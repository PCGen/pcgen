/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.function;

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.ASTUnaryMinus;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * SliceFunction returns a slice of an array.
 */
public class SliceFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "SLICE";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		int argCount = args.length;
		if ((argCount < 2) || (argCount > 3))
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 2 or 3, got "
				+ args.length + " " + Arrays.asList(args));
		}
		//Array node
		Node arrayNode = args[0];
		FormatManager<?> arrayFormat =
				ensureArray(visitor, semantics, arrayNode);

		//Start node
		FormatManager<?> startFormat = (FormatManager<?>) args[1]
			.jjtAccept(visitor, semantics.getWith(FormulaSemantics.ASSERTED,
				Optional.of(FormatUtilities.NUMBER_MANAGER)));

		if (!FormatUtilities.NUMBER_MANAGER.equals(startFormat))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + arrayFormat
					+ " found in " + args[1].getClass().getName()
					+ " found in start location, requires a"
					+ " Number (class cannot be evaluated)");
		}

		if (args[1] instanceof ASTUnaryMinus)
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value: " + args[1]
					+ " found in start location, requires a"
					+ " positive Number (class cannot be evaluated)");
		}

		if (args[1] instanceof ASTNum)
		{
			FunctionUtilities.ensurePositiveInteger((ASTNum) args[1]);
		}

		if (argCount == 3)
		{
			//Optional end node
			processEndNode(visitor, semantics, args, arrayFormat);
		}

		return arrayFormat;
	}

	private void processEndNode(SemanticsVisitor visitor,
		FormulaSemantics semantics, Node[] args, FormatManager<?> arrayFormat)
	{
		FormatManager<?> endFormat = (FormatManager<?>) args[2]
			.jjtAccept(visitor, semantics.getWith(FormulaSemantics.ASSERTED,
				Optional.of(FormatUtilities.NUMBER_MANAGER)));

		if (!FormatUtilities.NUMBER_MANAGER.equals(endFormat))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + arrayFormat
					+ " found in " + args[2].getClass().getName()
					+ " found in end location, requires a"
					+ " Number (class cannot be evaluated)");
		}
		
		if (args[2] instanceof ASTNum)
		{
			FunctionUtilities.ensurePositiveInteger((ASTNum) args[2]);
		}

		if ((args[1] instanceof ASTNum) && (args[2] instanceof ASTNum))
		{
			int start = FunctionUtilities.convertToInteger((ASTNum) args[1]);
			int end = FunctionUtilities.convertToInteger((ASTNum) args[2]);
			if (start == end)
			{
				throw new SemanticsFailureException(
					"Parse Error: Wasted Effort: Start and End values "
						+ "of slice produce empty array");
			}
			else if (start > end)
			{
				throw new SemanticsFailureException(
					"Parse Error: Invalid Slice: Start: " + start + " End: " + end 
						+ " means slice has no content (class cannot be evaluated)");
			}
		}
	}

	private static FormatManager<?> ensureArray(SemanticsVisitor visitor,
		FormulaSemantics semantics, Node node)
	{
		FormatManager<?> arrayFormat =
				(FormatManager<?>) node.jjtAccept(visitor,
					semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
		if (!arrayFormat.getManagedClass().isArray())
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + arrayFormat
					+ " found in " + node.getClass().getName()
					+ " found in location requiring an array (class cannot be evaluated)");
		}
		return arrayFormat;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Object[] original = (Object[]) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, Optional.empty()));
		Number start = (Number) args[1].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED,
				Optional.of(FormatUtilities.NUMBER_MANAGER)));
		Number end;
		if (args.length == 2)
		{
			end = original.length;
		}
		else
		{
			end = (Number) args[2].jjtAccept(visitor,
				manager.getWith(EvaluationManager.ASSERTED,
					Optional.of(FormatUtilities.NUMBER_MANAGER)));
		}
		
		/*
		 * TODO There are 3 possible issues at this point: (1) From and To are not
		 * integers [we choose to "cast" below to force this] (2) From may be less than zero
		 * (3) To may be less than From. At this time, #2 and #3 are not dealt with
		 * here... we have a general problem of "how do we communicate runtime issues back
		 * up the stack from our library (which doesn't depend on any logging utilities).
		 */

		int from = start.intValue();
		int to = end.intValue();

		return Arrays.copyOfRange(original, from, to);
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
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> returnFormat =
				(Optional<FormatManager<?>>) args[0].jjtAccept(visitor, manager
					.getWith(DependencyManager.ASSERTED, Optional.empty()));
		args[1].jjtAccept(visitor, manager);
		if (args.length == 3)
		{
			args[2].jjtAccept(visitor, manager);
		}
		return returnFormat;
	}

}
