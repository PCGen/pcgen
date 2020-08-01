/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * IsEmptyFunction calculates the a Boolean based on whether a given array is empty.
 */
public class IsEmptyFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "ISEMPTY";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		FunctionUtilities.validateArgCount(this, args, 1);
		Node node = args[0];
		FormatManager<?> format = (FormatManager<?>) node.jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
		if (!format.getManagedClass().isArray())
		{
			throw new SemanticsFailureException("Parse Error: Invalid Value Format: "
				+ format.getIdentifierType() + " found in " + node.getClass().getName()
				+ " found in location requiring an "
				+ "ARRAY (class cannot be evaluated)");
		}
		return FormatUtilities.BOOLEAN_MANAGER;
	}

	@Override
	public final Boolean evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Object[] solution = (Object[]) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, Optional.empty()));
		return (solution.length == 0);
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
	}

	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		args[0].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, Optional.empty()));
		return Optional.of(FormatUtilities.BOOLEAN_MANAGER);
	}
}
