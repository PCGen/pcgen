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
 * IfFunction returns different values based on a given calculation. It follows
 * the common form for an if function: if (conditional, return_if_true,
 * return_if_false).
 * 
 * In the case of this implementation, conditional is a Boolean.
 */
public class IfFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "IF";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		FunctionUtilities.validateArgCount(this, args, 3);
		//Boolean conditional node
		Node conditionalNode = args[0];
		FunctionUtilities.ensureMatchingFormat(visitor, semantics, conditionalNode,
			FormatUtilities.BOOLEAN_MANAGER);
		
		//If True node
		FormatManager<?> tFormat =
				(FormatManager<?>) args[1].jjtAccept(visitor, semantics);

		//If False node
		FormatManager<?> fFormat =
				(FormatManager<?>) args[2].jjtAccept(visitor, semantics);

		//Check for Mismatch in formats between True and False results
		if (!tFormat.equals(fFormat))
		{
			throw new SemanticsFailureException("Parse Error: Invalid Value Format: "
				+ fFormat + " found in " + conditionalNode.getClass().getName()
				+ " found in location requiring a " + tFormat
				+ " (class cannot be evaluated)");
		}
		return tFormat;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Boolean b = (Boolean) args[0].jjtAccept(visitor, manager.getWith(
			EvaluationManager.ASSERTED, Optional.of(FormatUtilities.BOOLEAN_MANAGER)));
		/*
		 * Note no attempt to cast or interpret the return values since we do
		 * not know if they are Boolean or Double (see allowArgs)
		 */
		final int which = (b) ? 1 : 2;
		return args[which].jjtAccept(visitor, manager);
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		/*
		 * Technically this is conservative, since if arg1 is static it could be
		 * evaluated to determine if arg2 or arg3 is the one that will always be
		 * used... but that is not a corner case we will spend time on right
		 * now...
		 */
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
		args[0].jjtAccept(visitor, manager.getWith(DependencyManager.ASSERTED,
			Optional.of(FormatUtilities.BOOLEAN_MANAGER)));
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> tFormat =
				(Optional<FormatManager<?>>) args[1].jjtAccept(visitor, manager);
		args[2].jjtAccept(visitor, manager);
		return tFormat;
	}
}
