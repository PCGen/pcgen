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

import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

/**
 * IfFunction returns different values based on a given calculation. It follows
 * the common form for an if function: if (conditional, return_if_true,
 * return_if_false).
 * 
 * In the case of this implementation, conditional is a Boolean.
 */
public class IfFunction implements Function
{

	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;

	/**
	 * Returns the function name for this function. This is how it is called by
	 * a user in a formula.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getFunctionName()
	{
		return "IF";
	}

	/**
	 * Checks if the given arguments are valid using the given SemanticsVisitor.
	 * Three arguments are required, and each must be a valid formula value
	 * (number, variable, another function, etc.).
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final void allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 3)
		{
			FormulaSemanticsUtilities.setInvalid(semantics, "Function "
				+ getFunctionName()
				+ " received incorrect # of arguments, expected: 3 got "
				+ args.length + " " + Arrays.asList(args));
			return;
		}
		//Boolean conditional node
		Node conditionalNode = args[0];
		SemanticsVisitor booleanVisitor;
		if (BOOLEAN_CLASS.equals(visitor.getAssertedFormat()))
		{
			booleanVisitor = visitor;
		}
		else
		{
			booleanVisitor = new SemanticsVisitor(visitor.getFormulaManager(),
				visitor.getLegalScope(), BOOLEAN_CLASS);
		}
		conditionalNode.jjtAccept(booleanVisitor, semantics);
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return;
		}
		Class<?> format =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
		if (!format.equals(BOOLEAN_CLASS))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Invalid Value Format: " + format + " found in "
					+ conditionalNode.getClass().getName()
					+ " found in location requiring a"
					+ " Boolean (class cannot be evaluated)");
			return;
		}

		//If True node
		Node trueNode = args[1];
		trueNode.jjtAccept(visitor, semantics);
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return;
		}
		/*
		 * Format is arbitrary - but capture now - just need True and False to
		 * match, see below
		 */
		@SuppressWarnings("PMD.PrematureDeclaration")
		Class<?> tFormat =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);

		//If False node
		Node falseNode = args[2];
		falseNode.jjtAccept(visitor, semantics);
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return;
		}

		//Check for Mismatch in formats between True and False results
		Class<?> fFormat =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
		if (!tFormat.equals(fFormat))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Invalid Value Format: " + fFormat + " found in "
					+ conditionalNode.getClass().getName()
					+ " found in location requiring a " + tFormat
					+ " (class cannot be evaluated)");
		}
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor.
	 * 
	 * This method assumes there are three arguments, and the arguments are
	 * valid values. See evaluate on the Function interface for important
	 * assumptions made when this method is called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		Class<?> assertedFormat)
	{
		Boolean b = (Boolean) args[0].jjtAccept(visitor, BOOLEAN_CLASS);
		/*
		 * Note no attempt to cast or interpret the return values since we do
		 * not know if they are Boolean or Double (see allowArgs)
		 */
		if (b.booleanValue())
		{
			return args[1].jjtAccept(visitor, assertedFormat);
		}
		else
		{
			return args[2].jjtAccept(visitor, assertedFormat);
		}
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes the arguments are valid values in a formula. See
	 * isStatic on the Function interface for important assumptions made when
	 * this method is called.
	 * 
	 * {@inheritDoc}
	 */
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

	/**
	 * Captures dependencies of the IF function. This includes Variables (in the
	 * form of VariableIDs), but is not limited to those as the only possible
	 * dependency.
	 * 
	 * Consistent with the contract of the Function interface, this list
	 * recursively includes all of the contents of items within this function
	 * (if this function calls another function, etc. all variables in the tree
	 * below this function are included)
	 * 
	 * This method assumes the arguments are valid values in a formula. See
	 * getVariables on the Function interface for important assumptions made
	 * when this method is called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		manager.push(DependencyManager.ASSERTED, Boolean.class);
		args[0].jjtAccept(visitor, manager);
		manager.pop(DependencyManager.ASSERTED);
		args[1].jjtAccept(visitor, manager);
		args[2].jjtAccept(visitor, manager);
	}
}
