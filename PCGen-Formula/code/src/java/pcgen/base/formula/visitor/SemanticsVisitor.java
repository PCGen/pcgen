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

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.OperatorLibrary;
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
import pcgen.base.formula.parse.Operator;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.util.FormatManager;

/**
 * SemanticsVisitor visits a formula in tree form to determine if the formula is
 * valid and if so, how the formula behaves. As a note, this is checking for
 * structural validity.
 * 
 * "Structural Validity" includes checking items such as: (a) Ensure Formula
 * arguments are a legal form (b) Ensure formula arguments are possibly legal
 * values (see below for clarification of "possibly legal") (c) Ensure there are
 * no nodes in the tree that can be reached but are non-sensical when reached
 * (d) Ensure nodes of the tree identified as numerical can actually be parsed
 * into numbers.
 * 
 * Note that SemanticsVisitor will not produce an error in a situations which is
 * "possibly legal": Specifically, where an String referred to in a formula may
 * or may not be valid at a later time. For details, see the Function interface
 * and examples provided there (and the different requirements on allowArgs() -
 * called by SemanticsVisitor - and getDependencies() - not called by
 * SemanticsVisitor)
 * 
 * The objects to be passed in as data to each of the methods on the
 * FormulaParserVisitor interface of SemanticsVisitor is a FormulaSemantics
 * object. The objects that each of the methods on the FormulaParserVistor
 * interface of SemanticsVisitor returns implement the FormatManager interface.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class SemanticsVisitor implements FormulaParserVisitor
{
	/*
	 * Implementation note: As a result of the "fast fail" behavior of returning
	 * an invalid FormulaVaility as soon as it is detected, a method in
	 * SemanticsVisitor that needs to analyze the children of a node must
	 * perform one of two operations when any node is passed SemanticsVisitor as
	 * the first argument to .jjtAccept().
	 * 
	 * If only one child node is being passed the SemanticsVisitor, the method
	 * should directly return the contents of the call to .jjtAccept()
	 * 
	 * If more than one child node is being processed, then the return value of
	 * .jjtAccept() must be checked, and if the FormulaSemantics returns FALSE
	 * from the isValid() method, then the method in SemanticsVisitor should
	 * immediately return that "invalid" FormulaSemantics.
	 */

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this SemanticsVisitor being called.
	 */
	@Override
	public Object visit(SimpleNode node, Object data)
	{
		//Delegate to the appropriate class
		return node.jjtAccept(this, data);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return singleChildValid(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return visitRelational(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return visitRelational(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return visitRelational(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return visitUnaryNode(node, data);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return visitUnaryNode(node, data);
	}

	/**
	 * Processes a variable argument Operator node. Enforces that the node 2 or
	 * more children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return singleChildValid(node, data);
	}

	/**
	 * Processes a numeric node. This ensures that the node has no children and
	 * that it can be parsed as a numeric value.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		if (node.jjtGetNumChildren() != 0)
		{
			semantics.setInvalid(getInvalidCountReport(node, 0));
			return null;
		}
		try
		{
			Double.parseDouble(node.getText());
			return FormatUtilities.NUMBER_MANAGER;
		}
		catch (NumberFormatException e)
		{
			semantics.setInvalid(node.getClass() + " had invalid number: "
				+ node.getText());
			return null;
		}
	}

	/**
	 * Processes a function encountered in the formula. This will validate the
	 * structure of the nodes making up the Function within the formula, decode
	 * what function is being called, using the FunctionLibrary, and then call
	 * allowArgs() on the Function, relying on the behavior of that method (as
	 * defined in the contract of the Function interface) to determine if the
	 * function represents a valid call to the Function.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		//Two children are function name and the grouping (parens/brackets)
		if (node.jjtGetNumChildren() != 2)
		{
			semantics.setInvalid(getInvalidCountReport(node, 2));
			return null;
		}
		Node firstChild = node.jjtGetChild(0);

		if (!(firstChild instanceof ASTPCGenSingleWord))
		{
			semantics.setInvalid("Parse Error: Formula "
				+ " received invalid node format within Lookup,"
				+ " expected: ASTPCGenSingleWord got "
				+ firstChild.getClass().getName() + ": " + firstChild);
			return null;
		}

		/*
		 * Validate the function contents (remember it can have other complex
		 * structures inside of it)
		 */
		ASTPCGenSingleWord ftnNode = (ASTPCGenSingleWord) firstChild;
		String name = ftnNode.getText();
		Node argNode = node.jjtGetChild(1);
		if (argNode instanceof ASTFParen)
		{
			FunctionLibrary library = semantics.get(FormulaSemantics.FMANAGER)
				.get(FormulaManager.FUNCTION);
			Function function = library.getFunction(name);
			if (function == null)
			{
				semantics.setInvalid("Function: " + name
					+ " was not found (called as: " + name + "(...))");
				return null;
			}
			//Extract arguments from the grouping to give them to the function
			Node[] args = VisitorUtilities.accumulateArguments(argNode);
			return function.allowArgs(this, args, semantics);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			return processArray(name, (SimpleNode) argNode, semantics);
		}
		else
		{
			semantics.setInvalid("Parse Error: Function Formula Arguments"
				+ " received invalid argument format, "
				+ "expected: ASTFParen or ASTPCGenBracket, got "
				+ firstChild.getClass().getName() + ": " + firstChild);
			return null;
		}
	}

	private FormatManager<?> processArray(String name, SimpleNode argNode,
		FormulaSemantics semantics)
	{
		FormatManager<?> argFormat =
				(FormatManager<?>) singleChildValid(argNode, semantics);
		if (!FormatUtilities.NUMBER_MANAGER.equals(argFormat))
		{
			semantics.setInvalid("Argument to array: "
				+ ((SimpleNode) argNode.jjtGetChild(0)).getText()
				+ " must resolve to a number");
			return null;
		}
		FormatManager<?> formatManager = getVariableFormat(semantics, name);
		if (formatManager == null)
		{
			semantics.setInvalid("Variable: " + name
				+ " was not found in scope "
				+ semantics.get(FormulaSemantics.SCOPE).getName());
			return null;
		}
		FormatManager<?> componentMgr = formatManager.getComponentManager();
		if (componentMgr == null)
		{
			semantics.setInvalid("Variable: " + name
				+ " was not an array in scope "
				+ semantics.get(FormulaSemantics.SCOPE).getName());
			return null;
		}
		return componentMgr;
	}

	/**
	 * Visits a variable name within the formula. This will validate with the
	 * VariableIDFactory that the variable usage is valid within the scope
	 * recognized by this SemanticsVisitor.
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		if (node.jjtGetNumChildren() != 0)
		{
			semantics.setInvalid(getInvalidCountReport(node, 0));
			return null;
		}
		String varName = node.getText();
		FormatManager<?> formatManager = getVariableFormat(semantics, varName);
		if (formatManager == null)
		{
			semantics.setInvalid("Variable: " + varName
				+ " was not found in scope "
				+ semantics.get(FormulaSemantics.SCOPE).getName());
			return null;
		}
		return formatManager;
	}

	/**
	 * Returns the format for a given Variable name, in the scope as described
	 * by the FormulaSemantics.
	 * 
	 * @param semantics
	 *            The FormulaSemantics used to determine the context of analysis
	 *            of the given variable name
	 * @param varName
	 *            The variable name for which the format should be returned
	 * @return The format for the given Variable, in the scope as described by
	 *         the FormulaSemantics
	 */
	public FormatManager<?> getVariableFormat(FormulaSemantics semantics,
		String varName)
	{
		VariableLibrary varLib =
				semantics.get(FormulaSemantics.FMANAGER).getFactory();
		LegalScope legalScope = semantics.get(FormulaSemantics.SCOPE);
		return varLib.getVariableFormat(legalScope, varName);
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * SemanticsVisitor, reaching this node in SemanticsVisitor indicates either
	 * an error in the implementation of the formula or a tree structure problem
	 * in the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the function
		FormulaSemantics semantics = (FormulaSemantics) data;
		semantics.setInvalid("Parse Error: Invalid Class: "
			+ node.getClass().getName()
			+ " found in operable location (class cannot be evaluated)");
		return null;
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * SemanticsVisitor, reaching this node in SemanticsVisitor indicates either
	 * an error in the implementation of the formula or a tree structure problem
	 * in the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		FormulaSemantics semantics = (FormulaSemantics) data;
		semantics.setInvalid("Parse Error: Invalid Class: "
			+ node.getClass().getName()
			+ " found in operable location (class cannot be evaluated)");
		return null;
	}

	/**
	 * A Quoted String. This will check to see if there is an asserted format. If so, it
	 * will attempt to return the FormatManager for that asserted format. If not, it will
	 * return a StringManager.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		Optional<FormatManager<?>> asserted = semantics.get(FormulaSemantics.ASSERTED);
		if (!asserted.isPresent())
		{
			return FormatUtilities.STRING_MANAGER;
		}
		FormatManager<?> assertedFormat = asserted.get();
		try
		{
			assertedFormat.convertIndirect(node.getText());
			return assertedFormat;
		}
		catch (IllegalArgumentException e)
		{
			semantics.setInvalid(
				"Invalid " + assertedFormat.getIdentifierType() + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2 valid
	 * children, and a non-null Operator.
	 * 
	 * @param node
	 *            The node to be validated to ensure it has valid children and a
	 *            non-null Operator
	 * @return A FormatManager object, which will indicate the format returned
	 *         by the Operator.
	 */
	private FormatManager<?> visitOperatorNode(SimpleNode node, Object data)
	{
		final FormulaSemantics semantics = (FormulaSemantics) data;
		Operator op = node.getOperator();
		if (op == null)
		{
			semantics.setInvalid("Parse Error: Object of type "
				+ node.getClass()
				+ " expected to have an operator, none was found");
			return null;
		}
		if (node.jjtGetNumChildren() != 2)
		{
			semantics.setInvalid(getInvalidCountReport(node, 2));
			return null;
		}
		Node child1 = node.jjtGetChild(0);
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format1 = (FormatManager<?>) child1.jjtAccept(this, data);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.isValid())
		{
			return null;
		}

		Node child2 = node.jjtGetChild(1);
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format2 = (FormatManager<?>) child2.jjtAccept(this, data);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.isValid())
		{
			return null;
		}
		OperatorLibrary opLib =
				semantics.get(FormulaSemantics.FMANAGER).getOperatorLibrary();
		FormatManager<?> returnedFormat =
				opLib.processAbstract(op, format1.getManagedClass(),
					format2.getManagedClass());
		//null response means the library couldn't find an appropriate operator
		if (returnedFormat == null)
		{
			semantics.setInvalid("Parse Error: Operator " + op.getSymbol()
				+ " cannot process children: " + format1.getIdentifierType()
				+ " and " + format2.getIdentifierType() + " found in "
				+ node.getClass().getName());
			return null;
		}
		return returnedFormat;
	}

	private FormatManager<?> visitUnaryNode(SimpleNode node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		Operator op = node.getOperator();
		if (op == null)
		{
			semantics.setInvalid("Parse Error: Object of type "
				+ node.getClass()
				+ " expected to have an operator, none was found");
			return null;
		}
		@SuppressWarnings("PMD.PrematureDeclaration")
		FormatManager<?> format = (FormatManager<?>) singleChildValid(node, data);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.isValid())
		{
			return null;
		}
		OperatorLibrary opLib =
				semantics.get(FormulaSemantics.FMANAGER).getOperatorLibrary();
		FormatManager<?> returnedFormat = opLib.processAbstract(op, format.getManagedClass());
		//null response means the library couldn't find an appropriate operator
		if (returnedFormat == null)
		{
			semantics.setInvalid("Parse Error: Operator " + op.getSymbol()
				+ " cannot process child: " + format.getIdentifierType()
				+ " found in " + node.getClass().getName());
			return null;
		}
		return returnedFormat;
	}

	/**
	 * Processes a node enforcing that the given node has a single child and
	 * enforcing that the child is valid.
	 * 
	 * @param node
	 *            The node to be validated to ensure it has a single, valid
	 *            child.
	 * @param data
	 *            The incoming FormulaSemantics object (as Object to assist
	 *            other methods in this class)
	 * @return A FormatManager object, which will indicate the format returned
	 *         by the Node.
	 */
	private Object singleChildValid(SimpleNode node, Object data)
	{
		if (node.jjtGetNumChildren() != 1)
		{
			FormulaSemantics semantics = (FormulaSemantics) data;
			semantics.setInvalid(getInvalidCountReport(node, 1));
			return null;
		}
		Node child = node.jjtGetChild(0);
		return child.jjtAccept(this, data);
	}

	/**
	 * Generates an invalid argument count report based on the given node and
	 * expected argument count.
	 */
	private String getInvalidCountReport(SimpleNode node, int expectedCount)
	{
		int argLength = node.jjtGetNumChildren();
		Node[] args = new Node[argLength];
		for (int i = 0; i < argLength; i++)
		{
			args[i] = node.jjtGetChild(i);
		}
		return "Parse Error: Item of type " + node.getClass().getName()
			+ " had incorrect children from parse. Expected " + expectedCount
			+ " got " + args.length + " " + Arrays.asList(args);
	}

	/**
	 * Processes a relational node enforcing that the given node has two
	 * children and enforcing that the child is valid.
	 * 
	 * Expect this to return Boolean.class :)
	 * 
	 * @param node
	 *            The node to be validated to ensure it has two valid children
	 *            that can be compared.
	 * @param data
	 *            The incoming FormulaSemantics object (as Object to assist
	 *            other methods in this class)
	 * @return A FormatManager object, which will indicate the format returned
	 *         by the Operator.
	 */
	private Object visitRelational(SimpleNode node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		//null assertion since we can't assert what each side of the logical expression is
		return visitOperatorNode(node,
			semantics.getWith(FormulaSemantics.ASSERTED, Optional.empty()));
	}

}
