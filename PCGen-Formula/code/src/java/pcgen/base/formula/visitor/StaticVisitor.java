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

import java.util.Objects;

import pcgen.base.formula.base.FunctionLibrary;
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
 * StaticVisitor visits a formula in tree form to determine if the formula is
 * static.
 * 
 * A static formula does not use variables in calculating the result, and does
 * not use a FormulaFunction where the returned value will depend on the context
 * in which the FormulaFunction is called.
 * 
 * As an example, "2+3" is a static formula while "2+T" is not. "max(4,6)" is
 * also a static formula (and one that uses a function).
 * 
 * As an observation: in many cases, a static formula will likely be a singular
 * value or will be a calculation that cannot be entered as a single value with
 * sufficient precision (such as "2/3").
 * 
 * Note that the contract of StaticVisitor is a conservative contract. This
 * means that a formula for which StaticVisitor returns TRUE is guaranteed to be
 * static, while a formula for which StaticVisitor returns FALSE MAY or MAY NOT
 * be static. In most cases, FALSE will indicate a non-static formula, but there
 * are corner cases that may elude StaticVisitor, such as: "if(4&lt;6,5,T)".
 * Detecting that this is static would require evaluation of the "4&lt;6"
 * portion, and the contract of static detection on a Formula does not require
 * such evaluation.
 * 
 * StaticVisitor does not accumulate results, since it is only detecting a
 * Boolean value. Therefore, the data parameter to the methods is ignored.
 * Rather, a "fast fail" implementation will return FALSE as soon as it is
 * detected.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class StaticVisitor implements FormulaParserVisitor
{

	/*
	 * Implementation note: As a result of the "fast fail" behavior of returning
	 * FALSE as soon as it is detected, a method in StaticVisitor that needs to
	 * analyze the children of a node must perform one of two operations when
	 * any node is passed StaticVisitor as the first argument to .jjtAccept().
	 * 
	 * If only one child node is being passed the StaticVisitor, the method
	 * should directly return the contents of the call to .jjtAccept()
	 * 
	 * If more than one child node is being processed, then the return value of
	 * .jjtAccept() must be checked, and if FALSE, then the method should
	 * immediately return FALSE.
	 */

	/**
	 * The FunctionLibrary to be used to get functions when they are encountered
	 * in a formula.
	 */
	private final FunctionLibrary library;

	/**
	 * Constructs a new StaticVisitor that will use the given FunctionLibrary.
	 * 
	 * @param fl
	 *            The FunctionLibrary to be used to get functions when they are
	 *            encountered in a formula
	 */
	public StaticVisitor(FunctionLibrary fl)
	{
		this.library = Objects.requireNonNull(fl);
	}

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this StaticVisitor being called.
	 * 
	 *
	 */
	@Override
	public Object visit(SimpleNode node, Object data)
	{
		//Delegate to the appropriate class
		return node.jjtAccept(this, null);
	}

	/**
	 * Processes the child of this node (this assumes the node has only one
	 * child - so this is making an assumption about formula validity. To ensure
	 * a formula will meet this assumption, SemanticsVisitor can be used prior
	 * to using StaticVisitor)
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return singleChildStatic(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Processes the child of this node (this assumes the node has only one
	 * child - so this is making an assumption about formula validity. To ensure
	 * a formula will meet this assumption, SemanticsVisitor can be used prior
	 * to using StaticVisitor)
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return singleChildStatic(node);
	}

	/**
	 * Processes the child of this node (this assumes the node has only one
	 * child - so this is making an assumption about formula validity. To ensure
	 * a formula will meet this assumption, SemanticsVisitor can be used prior
	 * to using StaticVisitor)
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return singleChildStatic(node);
	}

	/**
	 * Checks all of the children of this node to determine if they are static.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return checkAllChildren(node);
	}

	/**
	 * Processes the child of this node (this assumes the node has only one
	 * child - so this is making an assumption about formula validity. To ensure
	 * a formula will meet this assumption, SemanticsVisitor can be used prior
	 * to using StaticVisitor)
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return singleChildStatic(node);
	}

	/**
	 * Numbers are always static. :)
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		return Boolean.TRUE;
	}

	/**
	 * Indicates a FormulaFunction was encountered in the formula. This will decode what
	 * FormulaFunction is being called, using the FunctionLibrary, and then call
	 * isStatic() on the Function, relying on the behavior of that method (as defined in
	 * the contract of the FormulaFunction interface) to determine if the FormulaFunction
	 * represents a static value.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		ASTPCGenSingleWord fnode = (ASTPCGenSingleWord) node.jjtGetChild(0);
		Node argNode = node.jjtGetChild(1);
		if (argNode instanceof ASTFParen)
		{
			String name = fnode.getText();
			Node[] args = VisitorUtilities.accumulateArguments(argNode);
			return library.getFunction(name).isStatic(this, args);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			//Array access (defensive)
			return Boolean.FALSE;
		}
		else
		{
			throw new IllegalStateException(
				"Invalid Formula (unrecognized node: " + argNode + ")");
		}
	}

	/**
	 * Variables are always NOT static. :(
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		return Boolean.FALSE;
	}

	/**
	 * This type of node is ONLY encountered as part of a FormulaFunction. Since the
	 * FormulaFunction should have "consumed" these elements and not called back into
	 * StaticVisitor, reaching this node in StaticVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the FormulaFunction
		throw new IllegalStateException(
			"Static Check called on invalid Formula (reached brackets)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * FormulaFunction should have "consumed" these elements and not called back
	 * into StaticVisitor, reaching this node in StaticVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Static Check called on invalid Formula (reached Function Parens)");
	}

	/**
	 * Strings are always static :)
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		return Boolean.TRUE;
	}

	/**
	 * Checks all the children of the given node to determine if the children
	 * are static.
	 * 
	 * @param node
	 *            The node for which the children should be checked to determine
	 *            if each is static
	 * @return TRUE if all children of this node are static; FALSE otherwise
	 */
	private Object checkAllChildren(Node node)
	{
		int childCount = node.jjtGetNumChildren();
		for (int i = 0; i < childCount; i++)
		{
			Node child = node.jjtGetChild(i);
			Boolean result = (Boolean) child.jjtAccept(this, null);
			//Fail fast, per implementation note above
			if (!result.booleanValue())
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * Checks the (single) child of the given node to determine if the child is
	 * static.
	 * 
	 * @param node
	 *            The node for which the first child should be checked to
	 *            determine if it is static
	 * @return TRUE if the first child of this node is static; FALSE otherwise
	 */
	private Object singleChildStatic(SimpleNode node)
	{
		return node.jjtGetChild(0).jjtAccept(this, null);
	}

	/**
	 * Returns the underlying FunctionLibrary for this StaticVisitor.
	 * 
	 * @return the underlying FunctionLibrary for this StaticVisitor
	 */
	public FunctionLibrary getLibrary()
	{
		return library;
	}

}
