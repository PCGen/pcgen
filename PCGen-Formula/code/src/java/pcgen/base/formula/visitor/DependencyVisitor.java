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

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
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

/**
 * A DependencyVisitor captures the dependencies that exist in a Formula.
 * Usually this will consist of the variables that the Formula refers to, but
 * user-defined Functions may define additional dependencies if they are
 * supported by the DependencyManager.
 * 
 * DependencyVisitor enforces no contract that it will validate a formula, but
 * reserves the right to do so. As a result, the behavior of DependencyVisitor
 * is not defined if SemanticsVisitor returned a FormulaSemantics that indicated
 * isValid() was false.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DependencyVisitor implements FormulaParserVisitor
{

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this DependencyVisitor being called.
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
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return checkAllChildren(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return evaluateSingleChild(node, data);
	}

	/**
	 * Has no dependencies.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		return data;
	}

	/**
	 * Processes a function encountered in the formula.
	 * 
	 * This will decode what function is being called, using the
	 * FunctionLibrary, and then call getDependencies() on the Function, relying
	 * on the behavior of that method (as defined in the contract of the
	 * Function interface) to load the DependencyManager.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		DependencyManager manager = (DependencyManager) data;
		ASTPCGenSingleWord fnode = (ASTPCGenSingleWord) node.jjtGetChild(0);
		String name = fnode.getText();
		Node argNode = node.jjtGetChild(1);
		if (argNode instanceof ASTFParen)
		{
			FormulaManager formulaManager = manager.get(DependencyManager.FMANAGER);
			FunctionLibrary library = formulaManager.get(FormulaManager.FUNCTION);
			Function function = library.getFunction(name);
			Node[] args = VisitorUtilities.accumulateArguments(argNode);
			function.getDependencies(this, manager, args);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			visitVariable(name, manager);
		}
		else
		{
			throw new IllegalStateException(
				"Invalid Formula (unrecognized node: " + argNode + ")");
		}
		return data;
	}

	/**
	 * Processes a Variable. Since this means the formula is dependent on that
	 * variable, this loads the appropriate VariableID into the
	 * DependencyManager.
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		visitVariable(node.getText(), (DependencyManager) data);
		return data;
	}

	/**
	 * Adds a dependency on a specific variable to the given DependencyManager
	 * 
	 * @param varName
	 *            The variable name to be added as a dependency
	 */
	public void visitVariable(String varName, DependencyManager manager)
	{
		VariableID<?> id = getVariableID(varName, manager);
		if (id != null)
		{
			manager.addVariable(id);
		}
	}

	/**
	 * Returns the VariableID for the given variable name using the information provided
	 * in the given DependencyManager.
	 * 
	 * @return the VariableID for the given variable name using the information provided
	 *         in the given DependencyManager
	 */
	public VariableID<?> getVariableID(String varName, DependencyManager manager)
	{
		VariableLibrary varLib =
				manager.get(DependencyManager.FMANAGER).getFactory();
		return varLib.getVariableID(manager.get(DependencyManager.INSTANCE),
			varName);
	}

	/**
	 * This type of node is ONLY encountered as part of a an array call (which
	 * is parsed like a function). Since the function should have "consumed"
	 * these elements and not called back into DependencyVisitor, reaching this
	 * node in DependencyVisitor indicates either an error in the implementation
	 * of the formula or a tree structure problem in the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Brackets)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * DependencyVisitor, reaching this node in DependencyVisitor indicates
	 * either an error in the implementation of the formula or a tree structure
	 * problem in the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Parenthesis)");
	}

	/**
	 * Has no dependencies.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		return data;
	}

	/**
	 * Processes a single child node of the given node. Performs a
	 * double-dispatch in order to reach another method on this
	 * DependencyVisitor for the child.
	 */
	private Object evaluateSingleChild(Node node, Object data)
	{
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	/**
	 * Processes all child nodes of the given node. For each, performs a
	 * double-dispatch in order to reach another method on this
	 * DependencyVisitor for the child.
	 */
	private Object checkAllChildren(SimpleNode node, Object data)
	{
		int childCount = node.jjtGetNumChildren();
		for (int i = 0; i < childCount; i++)
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}
}
