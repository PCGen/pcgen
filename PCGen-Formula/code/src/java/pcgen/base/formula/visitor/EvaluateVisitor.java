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
package pcgen.base.formula.visitor;

import java.lang.reflect.Array;

import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.parse.ASTArithmetic;
import pcgen.base.formula.parse.ASTEquality;
import pcgen.base.formula.parse.ASTExpon;
import pcgen.base.formula.parse.ASTFParen;
import pcgen.base.formula.parse.ASTGeometric;
import pcgen.base.formula.parse.ASTLogical;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.ASTPCGenBracket;
import pcgen.base.formula.parse.ASTPCGenLookup;
import pcgen.base.formula.parse.ASTPCGenSingleWord;
import pcgen.base.formula.parse.ASTParen;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.ASTRelational;
import pcgen.base.formula.parse.ASTRoot;
import pcgen.base.formula.parse.ASTUnaryMinus;
import pcgen.base.formula.parse.ASTUnaryNot;
import pcgen.base.formula.parse.FormulaParserVisitor;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.util.FormatManager;

/**
 * EvaluateVisitor visits a formula in tree form in order to solve the formula -
 * It calculates the numeric value as a result of substituting the variables and
 * evaluating the functions contained in the formula.
 * 
 * EvaluateVisitor returns but does not accumulate results, since it is only
 * processing items that are present in the formula. The data parameter to the
 * visit methods should be an EvaluationManager that contains the necessary
 * information to resolve items in the formula.
 * 
 * EvaluateVisitor enforces no contract that it will validate a formula, but
 * reserves the right to do so. As a result, the behavior of EvaluationVisitor
 * is not defined if SemanticsVisitor returned a FormulaSemantics that indicated
 * isValid() was false.
 * 
 * Also, a user of EvaluateVisitor should ensure that DependencyVisitor has been
 * called and successfully processed to ensure that evaluation will run without
 * an Exception.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class EvaluateVisitor implements FormulaParserVisitor
{
	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this EvaluateVisitor being called.
	 * 
	 *
	 */
	@Override
	public Object visit(SimpleNode node, Object data)
	{
		//Delegate to the appropriate class
		return node.jjtAccept(this, data);
	}

	/**
	 * Processes the (single) child of this node, as a root is simply a
	 * structural placeholder.
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Evaluates the node, based on the Operator in the node.
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return evaluateRelational(node, data);
	}

	/**
	 * Evaluates the node, based on the Operator in the node.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return evaluateRelational(node, data);
	}

	/**
	 * Evaluates the node, based on the Operator in the node.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return evaluateRelational(node, data);
	}

	/**
	 * Evaluates the node, based on the Operator in the node.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return evaluateOperatorNode(node, data);
	}

	/**
	 * Evaluates the node, based on the Operator in the node.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return evaluateOperatorNode(node, data);
	}

	/**
	 * Evaluates the node, which is a unary negation.
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return evaluateUnaryNode(node, data);
	}

	/**
	 * Evaluates the node, which is a unary negation.
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return evaluateUnaryNode(node, data);
	}

	/**
	 * Evaluates the exponential node.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return evaluateOperatorNode(node, data);
	}

	/**
	 * Processes the (single) child of this node, as grouping parenthesis are
	 * logically present only to define order of operations (now implicit in the
	 * tree structure).
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Returns the contents of the node, which is a numeric value.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		String nodeText = node.getText();
		try
		{
			return Integer.valueOf(nodeText);
		}
		catch (NumberFormatException e)
		{
			return Double.valueOf(nodeText);
		}
	}

	/**
	 * Processes a function encountered in the formula.
	 * 
	 * This will decode what function is being called, using the
	 * FunctionLibrary, and then call evaluate() on the Function, relying on the
	 * behavior of that method (as defined in the contract of the Function
	 * interface) to calculate the return value.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		EvaluationManager manager = (EvaluationManager) data;
		ASTPCGenSingleWord fnode = (ASTPCGenSingleWord) node.jjtGetChild(0);
		String name = fnode.getText();
		Node argNode = node.jjtGetChild(1);
		Node[] args = VisitorUtilities.accumulateArguments(argNode);
		if (argNode instanceof ASTFParen)
		{
			FunctionLibrary ftnLib = manager.get(EvaluationManager.FMANAGER)
				.get(FormulaManager.FUNCTION);
			Function function = ftnLib.getFunction(name);
			return function.evaluate(this, args, manager);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			int index = (Integer) visit((SimpleNode) args[0], data);
			return Array.get(visitVariable(name, manager), index);
		}
		throw new IllegalStateException("Invalid Formula (unrecognized node: "
			+ argNode + ")");
	}

	/**
	 * Processes a variable within the formula. This relies on the
	 * VariableIDFactory and the ScopeInstance to precisely determine the
	 * VariableID and then fetch the value for that VariableID from the
	 * VariableStore (cache).
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		return visitVariable(node.getText(), (EvaluationManager) data);
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * EvaluateVisitor, reaching this node in EvaluateVisitor indicates either
	 * an error in the implementation of the formula or a tree structure problem
	 * in the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Brackets)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * EvaluateVisitor, reaching this node in EvaluateVisitor indicates either
	 * an error in the implementation of the formula or a tree structure problem
	 * in the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Parenthesis)");
	}

	/**
	 * Evaluates a Quoted String (so obviously returns a String).
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		EvaluationManager manager = (EvaluationManager) data;
		FormatManager<?> asserted = manager.get(EvaluationManager.ASSERTED);
		//The quotes are stripped by the parser
		try
		{
			return asserted.convert(node.getText());
		}
		catch (IllegalArgumentException e)
		{
			//Give up and return a String
			return node.getText();
		}
	}

	/**
	 * Evaluates an operator node. Must have 2 children and a node that contains
	 * an Operator.
	 * 
	 * @param node
	 *            The node that contains an Operator and has exactly 2 children.
	 * @param data
	 *            The EvaluationManager used in evaluation
	 * @return The result of the operation acting on the 2 children
	 */
	private Object evaluateOperatorNode(SimpleNode node, Object data)
	{
		Object child1result = node.jjtGetChild(0).jjtAccept(this, data);
		Object child2result = node.jjtGetChild(1).jjtAccept(this, data);
		EvaluationManager manager = (EvaluationManager) data;
		OperatorLibrary opLib =
				manager.get(EvaluationManager.FMANAGER).getOperatorLibrary();
		return opLib.evaluate(node.getOperator(), child1result, child2result);
	}

	/**
	 * Evaluates an operator node. Must have 1 child and a node that contains a
	 * Unary Operator.
	 * 
	 * @param node
	 *            The node that contains a Unary Operator and has exactly 1
	 *            child.
	 * @param data
	 *            The EvaluationManager used in evaluation
	 * @return The result of the operation acting on the child
	 */
	private Object evaluateUnaryNode(SimpleNode node, Object data)
	{
		Object result = node.jjtGetChild(0).jjtAccept(this, data);
		EvaluationManager manager = (EvaluationManager) data;
		OperatorLibrary opLib =
				manager.get(EvaluationManager.FMANAGER).getOperatorLibrary();
		return opLib.evaluate(node.getOperator(), result);
	}

	/**
	 * Evaluates a single child node. Effectively extracts the child and then
	 * performs a double-dispatch to get back into one of the methods on this
	 * EvaluateVisitor.
	 * 
	 * @param node
	 *            The node for which the (single) child will be evaluated
	 * @param data
	 *            The EvaluationManager used in evaluation
	 * @return The result of the evaluation on the child of the given node
	 */
	private Object evaluateSingleChild(SimpleNode node, Object data)
	{
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	/**
	 * Evaluates a relational node. Must have 2 children and a node that
	 * contains an Operator.
	 * 
	 * @param node
	 *            The node that contains an Operator and has exactly 2 children.
	 * @param data
	 *            The EvaluationManager used in evaluation
	 * @return The result of the operation acting on the 2 children
	 */
	private Object evaluateRelational(SimpleNode node, Object data)
	{
		EvaluationManager manager = (EvaluationManager) data;
		//Pass in null since we can't assert what each side of the logical expression is
		return evaluateOperatorNode(node, manager.getWith(EvaluationManager.ASSERTED, null));
	}

	/**
	 * Returns the value for a specific variable.
	 * 
	 * @param varName
	 *            The name of the variable to be evaluated
	 * @param manager
	 *            The EvaluationManager used in evaluation
	 * @return the value for the given specific variable
	 */
	public Object visitVariable(String varName, EvaluationManager manager)
	{
		FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
		VariableLibrary varLibrary = fm.getFactory();
		ScopeInstance scopeInst = manager.get(EvaluationManager.INSTANCE);
		FormatManager<?> formatManager =
				varLibrary
					.getVariableFormat(scopeInst.getLegalScope(), varName);
		if (formatManager != null)
		{
			VariableID<?> id = varLibrary.getVariableID(scopeInst, varName);
			VariableStore resolver = fm.get(FormulaManager.RESULTS);
			if (resolver.containsKey(id))
			{
				return resolver.get(id);
			}
		}
		FormatManager<?> asserted = manager.get(EvaluationManager.ASSERTED);
		if (asserted == null)
		{
			System.out
				.println("Evaluation called on invalid variable: '" + varName
					+ "', no asserted format available to determine default, "
					+ "assuming zero (number)");
			return 0;
		}
		System.out.println("Evaluation called on invalid variable: '" + varName
			+ "', assuming default for " + asserted.getManagedClass().getSimpleName());
		return fm.getDefault(asserted.getManagedClass());
	}

}
