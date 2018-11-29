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
import pcgen.base.formula.parse.FormulaParserTreeConstants;
import pcgen.base.formula.parse.FormulaParserVisitor;
import pcgen.base.formula.parse.Operator;
import pcgen.base.formula.parse.SimpleNode;

/**
 * FullDumpVisitor is a FormulaParserVisitor that dumps an extended version of
 * the formula to standard error. This includes not just the parsed syntax of
 * the formula, but also information about the node types.
 * 
 * This is intended as a debugging behavior.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class FullDumpVisitor implements FormulaParserVisitor
{

	/**
	 * An embedded ReconstructionVisitor used to reconstruct the formula, as the
	 * reconstructed version is provided as part of the debugging output.
	 */
	private final ReconstructionVisitor reconVisitor =
			new ReconstructionVisitor();

	@Override
	public Object visit(SimpleNode node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTParen node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTNum node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTFParen node, Object data)
	{
		return dump(node, data);
	}

	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		return dump(node, data);
	}

	/**
	 * Dump out the tree of nodes produced by the parser. Each node gives its
	 * type, the operation that it represents and a reconstruction of the
	 * subtree of nodes beneath it. For leaf nodes, they just print out their
	 * type and their text.
	 * 
	 * @param node
	 *            The starting node for the dump
	 * @param data
	 *            The string that will be printed at the start of the line. Each
	 *            recursive call adds another space and this shows the tree
	 *            structure through indentation.
	 * @return null (assists other methods in this class)
	 */
	@SuppressWarnings("PMD.SystemPrintln")
	private Object dump(SimpleNode node, Object data)
	{
		System.err.print(data);
		System.err.print(FormulaParserTreeConstants.jjtNodeName[node.getId()]);
		Operator operator = node.getOperator();
		if (operator != null)
		{
			System.err.print(" ");
			System.err.print(operator.getSymbol());
		}
		Object rvr = node.jjtAccept(reconVisitor, new StringBuilder(1000));
		System.err.print(": ");
		System.err.println(rvr);
		node.childrenAccept(this, data + " ");
		return null;
	}
}
