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
package pcgen.base.formula.library;

import java.util.Arrays;
import java.util.Objects;

import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * GenericFunction can perform a varied calculation based on a pre-defined
 * Formula and given a set of arguments.
 * 
 * This effectively serves the purpose of providing the ability to predefine
 * macros. For example a function called d20Mod could be created, where the
 * Function loaded here is "floor((arg(1)-10)/2)". The value for arg(1) is the
 * first argument to the d20Mod function when it is actually called in data. So
 * it would be called as something like "d20Mod(14)". The resulting effect is a
 * calculation of "floor((14-10)/2)"
 */
public class GenericFunction implements Function
{

	/**
	 * The name for this function (how the user refers to this function).
	 */
	private final String functionName;

	/**
	 * The root node of the tree representing the calculation of this
	 * GenericFunction.
	 * 
	 * Note that while this object is private, it is intended that this object
	 * will escape from the GenericFunction instance (This is because the method
	 * of evaluating or processing a GenericFunction uses a visitor pattern on
	 * the tree of objects). Given that this root object and the resulting tree
	 * is shared, a GenericFunction is not immutable; it is up to the behavior
	 * of the visitor to ensure that it treats the GenericFunction in an
	 * appropriate fashion.
	 */
	private final SimpleNode root;

	/**
	 * Constructs a new GenericFunction with the given Function name and root
	 * node of the tree representing the calculation of this GenericFunction.
	 * 
	 * The Formula defined by the given Root node will be operated upon when
	 * this GenericFunction is called, with any arguments provided to the
	 * GenericFunction loaded into values stored in the arg(n) function
	 * available to the Formula with the root at the given node.
	 * 
	 * @param name
	 *            The Function name for this GenericFunction
	 * @param root
	 *            The root node of the tree representing the calculation of this
	 *            GenericFunction
	 */
	public GenericFunction(String name, SimpleNode root)
	{
		functionName = Objects.requireNonNull(name);
		this.root = Objects.requireNonNull(root);
	}

	/**
	 * Returns the function name for this function. This is how it is called by
	 * a user in a formula.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getFunctionName()
	{
		return functionName;
	}

	/**
	 * Checks if the given arguments are valid using the given SemanticsVisitor.
	 * This will validate that the number (and format) of given arguments
	 * matches the number of arguments required by the formula provided at
	 * construction.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final FormatManager<?> allowArgs(SemanticsVisitor visitor,
		Node[] args, FormulaSemantics semantics)
	{
		FormulaManager formulaManager = semantics.get(FormulaSemantics.FMANAGER);
		FunctionLibrary withArgs = new ArgWrappingLibrary(
			formulaManager.get(FormulaManager.FUNCTION), args);
		FormulaManager subFormulaMgr =
				formulaManager.getWith(FormulaManager.FUNCTION, withArgs);

		//Need to save original to handle "embedded" GenericFunction objects properly
		ArgumentDependencyManager myArgs = new ArgumentDependencyManager();
		FormulaSemantics subSemantics =
				semantics.getWith(ArgumentDependencyManager.KEY, myArgs);
		subSemantics = subSemantics.getWith(FormulaSemantics.FMANAGER, subFormulaMgr);
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> result = (FormatManager<?>) visitor.visit(root, subSemantics);

		int maxArg = myArgs.getMaximumArgument() + 1;
		if (maxArg != args.length)
		{
			semantics.setInvalid("Function " + functionName
				+ " required: " + maxArg + " arguments, but was provided "
				+ args.length + " " + Arrays.asList(args));
			return null;
		}
		return result;
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor.
	 * 
	 * This method assumes the arguments are valid values. See evaluate on the
	 * Function interface for important assumptions made when this method is
	 * called.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		FormulaManager formulaManager = manager.get(EvaluationManager.FMANAGER);
		FunctionLibrary withArgs =
				new ArgWrappingLibrary(formulaManager.get(FormulaManager.FUNCTION), args);
		FormulaManager subFormulaMgr =
				formulaManager.getWith(FormulaManager.FUNCTION, withArgs);
		return visitor.visit(root,
			manager.getWith(EvaluationManager.FMANAGER, subFormulaMgr));
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
		FunctionLibrary argLibrary = new ArgWrappingLibrary(visitor.getLibrary(), args);
		StaticVisitor subVisitor = new StaticVisitor(argLibrary);
		return (Boolean) subVisitor.visit(root, null);
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
	public FormatManager<?> getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		FormulaManager formulaManager = manager.get(DependencyManager.FMANAGER);
		FunctionLibrary withArgs = new ArgWrappingLibrary(
			formulaManager.get(FormulaManager.FUNCTION), args);
		FormulaManager subFtn = formulaManager.getWith(FormulaManager.FUNCTION, withArgs);
		return (FormatManager<?>) visitor.visit(root,
			manager.getWith(DependencyManager.FMANAGER, subFtn));
	}
}
