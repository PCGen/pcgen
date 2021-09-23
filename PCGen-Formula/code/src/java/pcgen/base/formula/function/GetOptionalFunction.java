/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.logging.Logging;
import pcgen.base.logging.Severity;
import pcgen.base.util.FormatManager;

/**
 * GetOptionalFunction retrieves the underlying object to an Optional.
 */
public class GetOptionalFunction implements FormulaFunction
{

	@Override
	public String getFunctionName()
	{
		return "GETOPTIONAL";
	}

	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 1)
		{
			throw new SemanticsFailureException("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 1 got "
				+ argCount + " " + Arrays.asList(args));
		}
		Node node = args[0];
		FormatManager<?> format = (FormatManager<?>) node.jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
		if (!format.getManagedClass().equals(Optional.class))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: "
					+ format.getIdentifierType() + " found in "
					+ node.getClass().getName()
					+ " found in location requiring an "
					+ "OPTIONAL (class cannot be evaluated)");
		}
		return format.getComponentManager().get();
	}

	@Override
	public final Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		Optional<?> optional = (Optional<?>) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, Optional.empty()));
		if (optional.isEmpty())
		{
			Optional<FormatManager<?>> asserted =
					manager.get(EvaluationManager.ASSERTED);
			if (asserted.isEmpty())
			{
				Logging.log(Severity.WARNING,
					() -> "Evaluation called on invalid formula: '"
						+ "<Optional Not Present>"
						+ "', no asserted format available to determine default, "
						+ "assuming zero (number)");
				return 0;
			}
			@SuppressWarnings("unchecked")
			FormatManager<Optional<?>> optionalFormat =
					(FormatManager<Optional<?>>) asserted.get();
			Optional<FormatManager<?>> underlying =
					optionalFormat.getComponentManager();
			Class<?> managedClass = asserted.get().getManagedClass();
			Logging.log(Severity.WARNING,
				() -> "Evaluation called on invalid formula: '"
					+ "<Optional Not Present>" + "', assuming default for "
					+ managedClass.getSimpleName());
			VariableLibrary varLib = manager.get(EvaluationManager.VARLIB);
			return varLib.getDefault((FormatManager<?>) underlying.get());
		}
		return optional.get();
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
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> formatManager =
				(Optional<FormatManager<?>>) args[0].jjtAccept(visitor, manager
					.getWith(DependencyManager.ASSERTED, Optional.empty()));
		return formatManager;
	}
}
