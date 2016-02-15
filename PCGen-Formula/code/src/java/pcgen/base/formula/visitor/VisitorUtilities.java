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

import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.parse.ASTFParen;
import pcgen.base.formula.parse.ASTPCGenBracket;
import pcgen.base.formula.parse.ASTPCGenLookup;
import pcgen.base.formula.parse.ASTPCGenSingleWord;
import pcgen.base.formula.parse.Node;

/**
 * VisitorUtilities are a set of common behaviors used among a number of
 * visitors to do processing of a formula.
 */
public final class VisitorUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private VisitorUtilities()
	{
	}

	/**
	 * Returns a Function from the given FunctionLibrary based on the given
	 * node.
	 * 
	 * @param library
	 *            The FunctionLibrary containing the Function to be returned
	 * @param node
	 *            The node which contains the function (this includes the
	 *            function name and arguments)
	 * @return The Function from the given FunctionLibrary based on the
	 *         information in the given node
	 */
	public static Function getFunction(FunctionLibrary library,
		ASTPCGenLookup node)
	{
		ASTPCGenSingleWord fnode = (ASTPCGenSingleWord) node.jjtGetChild(0);
		Node argNode = node.jjtGetChild(1);
		String ftnName = fnode.getText();
		Function function;
		if (argNode instanceof ASTFParen)
		{
			function = library.getFunction(ftnName);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			function = library.getBracketFunction(ftnName);
		}
		else
		{
			throw new IllegalStateException(
				"Processing called on invalid Formula (function " + ftnName
					+ " not recognized)");
		}
		return function;
	}

	/**
	 * Returns an array of Node that represent the children (arguments) of the
	 * given node.
	 * 
	 * @param argNode
	 *            The "argument" node which contains the children to be placed
	 *            into an array
	 * @return an array of Node that represent the children (arguments) of the
	 *         given node
	 */
	public static Node[] accumulateArguments(Node argNode)
	{
		int argLength = argNode.jjtGetNumChildren();
		Node[] args = new Node[argLength];
		for (int i = 0; i < argLength; i++)
		{
			args[i] = argNode.jjtGetChild(i);
		}
		return args;
	}

}
