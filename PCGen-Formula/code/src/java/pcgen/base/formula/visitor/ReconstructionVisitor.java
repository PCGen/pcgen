/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * ReconstructionVisitor reconstructs a logically-identical version of the
 * formula in the visited tree.
 * 
 * Note that this does not necessarily represent a perfect reconstruction of the
 * original formula. Specifically, the parser ignores certain whitespace, and
 * the ignored whitespace is not reproduced (rather it was discarded when the
 * tree was built). The logical operations are all maintained, the only
 * difference should be in spacing.
 * 
 * All calls to methods on a ReconstructionVisitor MUST pass in a non-null
 * StringBuilder as the "data" parameter to the method call. Failure to do this
 * will result in a ClassCastException or a NullPointerException. The
 * StringBuilder is then returned to the calling object as the return result of
 * the method called on ReconstructionVisitor.
 * 
 * When the StringBuilder is passed into a method, it is assumed that ownership
 * of the StringBuilder has been transferred to the ReconstructionVisitor. This
 * means the ReconstructionVisitor intends to modify the StringBuilder, and has
 * no need to worry about other threads accessing the StringBuilder. When
 * ReconstructionVisitor has finished the reconstruction, it will return
 * ownership of the StringBuilder to the calling object.
 * 
 * ReconstructionVisitor maintains the StringBuilder as passed in - meaning that
 * if the StringBuilder already has contents, those contents are
 * preserved/ignored by ReconstructionVisitor.
 * 
 * Reconstruction is accomplished by accumulating the result into the given
 * StringBuilder. As a result of this accumulation, if there is a non-standard
 * exit from ReconstructionVisitor (such as an Exception), then the contents of
 * the StringBuilder are not guaranteed to be a complete reconstruction of the
 * formula represented in the tree. Only if the StringBuilder is successfully
 * returned should the contents of the StringBuilder be trusted to represent the
 * complete formula.
 * 
 * WARNING: If you have modified a formula tree, you should ONLY write out a
 * formula into a persistent location if you KNOW is valid. You should use the
 * SemanticsVisitor to check this if necessary. ReconstructionVisitor makes no
 * attempt to ensure that the tree is valid, so even if the tree structure is
 * not a valid formula, ReconstructionVisitor will still happily produce a
 * String which if read, will not parse without an error.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ReconstructionVisitor implements FormulaParserVisitor
{

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this ReconstructionVisitor being called.
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
	 * Since the root node does not represent any content, this solely processes
	 * the children of the root node.
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return processChildren(node, data);
	}

	/**
	 * Processes the logical node (which is an operator node).
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the equality node (which is an operator node).
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the relational node (which is an operator node).
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the arithmetic node (which is an operator node).
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the geometric node (which is an operator node).
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the unary node. Since this represents unary minus sign
	 * (effectively negation), it writes out a minus sign followed by the
	 * children (presumed to be one, but not validated here)
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return processUnary(node, data);
	}

	/**
	 * Processes the unary node. Since this represents unary not sign
	 * (effectively negation), it writes out an exclamation point followed by
	 * the children (presumed to be one, but not validated here)
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append('!');
		return processChildren(node, data);
	}

	/**
	 * Processes the exponential node (which is an operator node).
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return processOperator(node, data);
	}

	/**
	 * Processes the parenthesis node. These are grouping parenthesis, so the
	 * parenthesis are written surrounding the children (presumably one, but not
	 * validated here)
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append('(');
		processChildren(node, data);
		sb.append(')');
		return data;
	}

	/**
	 * Processes a numeric node, so involves writing the text of the node.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append(node.getText());
		return data;
	}

	/**
	 * Processes a lookup node, which is a virtual node used for organizing a
	 * formula tree. It does not represent any content in the formula, so this
	 * only needs to write the children.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		return processChildren(node, data);
	}

	/**
	 * Processes a word node, so involves writing the text of the node.
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append(node.getText());
		return data;
	}

	/**
	 * Processes the FormulaFunction bracket node. These are brackets following a
	 * bracket FormulaFunction name (though the name was already written by a word
	 * node), so the brackets are written surrounding the children. It is
	 * presumed there is only one child, so no separator is necessary.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append('[');
		//No need for separator, only 1 child was legal
		processChildren(node, data);
		sb.append(']');
		return data;
	}

	/**
	 * Processes the FormulaFunction parenthesis node. These are parenthesis following
	 * a FormulaFunction name (though the name was already written by a word node), so
	 * the parenthesis are written surrounding the children. Since the children
	 * are arguments to a function, they are separated by commas.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append('(');
		//separator as a paren FormulaFunction can have multiple arguments
		processChildrenWithSeparator(node, data, ",");
		sb.append(')');
		return data;
	}

	/**
	 * Processes the quoted string node, so it writes a quote followed by the
	 * text, followed by a closing quote.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append('"').append(node.getText()).append('"');
		return data;
	}

	/**
	 * Process the children of the given node to write the contents of the
	 * children nodes.
	 * 
	 * @param node
	 *            The node for which the children should be processed to
	 *            reconstruct the contents
	 * @param data
	 *            The StringBuilder in which the results are being written
	 * @return The StringBuilder in which the results are being written
	 */
	private Object processChildren(SimpleNode node, Object data)
	{
		node.childrenAccept(this, data);
		return data;
	}

	/**
	 * Process the children of the given node to write the contents of the
	 * children nodes, separated by the given separator.
	 * 
	 * @param node
	 *            The node for which the children should be processed to
	 *            reconstruct the contents
	 * @param data
	 *            The StringBuilder in which the results are being written
	 * @param separator
	 *            The separator to be written in between the contents of each
	 *            child node
	 * @return The StringBuilder in which the results are being written
	 */
	private Object processChildrenWithSeparator(SimpleNode node, Object data,
		String separator)
	{
		StringBuilder sb = (StringBuilder) data;
		int numberOfChildren = node.jjtGetNumChildren();
		for (int i = 0; i < numberOfChildren; i++)
		{
			if (i != 0)
			{
				sb.append(separator);
			}
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
		}
		return data;
	}

	/**
	 * Writes an operator node. Effectively this writes the children, each
	 * separated by the symbol for the node's Operator.
	 */
	private Object processOperator(SimpleNode node, Object data)
	{
		return processChildrenWithSeparator(node, data, node.getOperator()
			.getSymbol());
	}
	
	/**
	 * Writes a unary operator node. Effectively this writes the single child,
	 * prefixed with the symbol for the node's Operator.
	 */
	private Object processUnary(SimpleNode node, Object data)
	{
		StringBuilder sb = (StringBuilder) data;
		sb.append(node.getOperator().getSymbol());
		return processChildren(node, data);
	}
}
