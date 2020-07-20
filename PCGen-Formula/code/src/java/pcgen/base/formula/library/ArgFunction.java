/*
 * Copyright 2015-8 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.library;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * ArgFunction is a one-argument function designed to delegate to a value
 * provided as an argument to a GenericFunction.
 * 
 * This is indirectly used within a GenericFunction in order to reference values
 * passed into that function.
 */
public final class ArgFunction implements FormulaFunction
{
	/**
	 * The name for this Function.
	 */
	public static final String FUNCTION_NAME = "ARG";

	/**
	 * The "arguments" provided to the GenericFunction.
	 */
	private final Node[] masterArgs;

	/**
	 * Constructs a new ArgFunction to delegate to the appropriate values.
	 * 
	 * @param masterArgs
	 *            The "arguments" provided to the GenericFunction that is being
	 *            processed
	 */
	private ArgFunction(Node[] masterArgs)
	{
		this.masterArgs = Objects.requireNonNull(masterArgs);
	}

	@Override
	public String getFunctionName()
	{
		return FUNCTION_NAME;
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		return (Boolean) visitor.visit((SimpleNode) masterArgs[argNum], null);
	}

	/**
	 * Must be a single, numeric, integer argument (and within the range of
	 * legal items in the master arguments provided when ArgFunction was
	 * constructed) to be valid.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			throw new SemanticsFailureException("Function " + FUNCTION_NAME
				+ " received incorrect # of arguments, expected: 0 got " + args.length
				+ " " + Arrays.asList(args));
		}
		Node node = args[0];
		if (!(node instanceof ASTNum))
		{
			throw new SemanticsFailureException("Parse Error: Function " + FUNCTION_NAME
				+ " received invalid argument format," + " expected: ASTNum got "
				+ node.getClass().getName() + ": " + node);
		}
		String nodeText = ((ASTNum) node).getText();
		try
		{
			int argNum = Integer.parseInt(nodeText);
			if ((argNum < 0) || (argNum >= masterArgs.length))
			{
				throw new SemanticsFailureException("Function " + FUNCTION_NAME
					+ " received incorrect # of arguments, expected: " + (argNum + 1)
					+ " got " + masterArgs.length + " " + Arrays.asList(masterArgs));
			}
			assertArgs(semantics, argNum);
			Node n = masterArgs[argNum];
			return (FormatManager<?>) n.jjtAccept(visitor, semantics);
		}
		catch (NumberFormatException e)
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Class: " + node.getClass().getName()
					+ " found in operable location (class cannot be evaluated)", e);
		}
	}

	private void assertArgs(FormulaSemantics semantics, int argNum)
	{
		Optional<ArgumentDependencyManager> argManager =
				semantics.get(ArgumentDependencyManager.KEY);
		//If not present, they didn't ask, their own fault
		if (argManager.isPresent())
		{
			argManager.get().addArgument(argNum);
		}
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		return visitor.visit((SimpleNode) masterArgs[argNum], manager);
	}

	/**
	 * Captures dependencies for this ArgFunction. This will load an
	 * ArgumentDependencyManager if and only if one is present within the
	 * DependencyManager.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		Optional<ArgumentDependencyManager> argManager =
				manager.get(ArgumentDependencyManager.KEY);
		if (argManager.isPresent())
		{
			argManager.get().addArgument(argNum);
		}
		else
		{
			manager.get(DependencyManager.LOG)
				.add("Encountered ARG Function, "
					+ "but DependencyManager did not have an ArgumentDependencyManager, "
					+ "so unable to log the dependency");
		}
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> result = (Optional<FormatManager<?>>) visitor
			.visit((SimpleNode) masterArgs[argNum], manager);
		return result;
	}

	/**
	 * Returns a FunctionLibrary which contains the arg(x) function.
	 * 
	 * @param functionLibrary
	 *            The underlying FunctionLibrary handling all other functions
	 * @param args
	 *            the "arguments" provided to the GenericFunction that is being processed
	 * @return A FunctionLibrary which contains the arg(x) function.
	 */
	public static FunctionLibrary getWithArgs(FunctionLibrary functionLibrary, Node[] args)
	{
		return lookupName -> FUNCTION_NAME.equalsIgnoreCase(lookupName)
			? new ArgFunction(args) : functionLibrary.getFunction(lookupName);
	}
}
