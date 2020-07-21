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
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * LengthFunction returns a length of an array.
 */
public class LengthFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "LENGTH";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1, got "
				+ args.length + " " + Arrays.asList(args));
		}
		Node arrayNode = args[0];
		FormatManager<?> arrayFormat = (FormatManager<?>) arrayNode.jjtAccept(
			visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
		if (!arrayFormat.getManagedClass().isArray())
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + arrayFormat
					+ " found in " + arrayNode.getClass().getName()
					+ " found in location requiring an array (class cannot be evaluated)");
		}

		return FormatUtilities.NUMBER_MANAGER;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		EvaluationManager nonAssertingManager =
				manager.getWith(EvaluationManager.ASSERTED, Optional.empty());
		Object result = args[0].jjtAccept(visitor, nonAssertingManager);
		return ((Object[]) result).length;
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
		args[0].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, Optional.empty()));
		return Optional.of(FormatUtilities.NUMBER_MANAGER);
	}

}
