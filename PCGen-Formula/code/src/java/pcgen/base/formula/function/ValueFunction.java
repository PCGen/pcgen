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
 * ValueFunction returns the input value to the evaluation
 */
public class ValueFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "VALUE";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return Boolean.FALSE;
	}

	/**
	 * Must be zero-argument to be valid.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		FunctionUtilities.validateArgCount(this, args, 0);
		Optional<FormatManager<?>> inputFormat =
				semantics.get(FormulaSemantics.INPUT_FORMAT);
		if (!inputFormat.isPresent())
		{
			throw new SemanticsFailureException("Function value()"
				+ " unable to proceed without a known INPUT_FORMAT");
		}
		return inputFormat.get();
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		return manager.get(EvaluationManager.INPUT);
	}

	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		return manager.get(DependencyManager.INPUT_FORMAT);
	}

}
