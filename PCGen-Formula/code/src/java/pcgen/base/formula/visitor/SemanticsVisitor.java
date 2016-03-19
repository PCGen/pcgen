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

import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
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
	 * A cache of the Number class.
	 */
	private static final Class<Number> NUMBER_CLASS = Number.class;

	/**
	 * A cache of the String class.
	 */
	private static final Class<String> STRING_CLASS = String.class;

	/**
	 * The FormulaManager used to get information about functions and other key
	 * parameters of a Formula.
	 */
	private final FormulaManager fm;

	/**
	 * The LegalScope in which the formula resides.
	 */
	private final LegalScope legalScope;

	/**
	 * Constructs a new SemanticsVisitor with the given FormulaManager and
	 * LegalScope.
	 * 
	 * @param fm
	 *            The FormulaManager used to get information about functions and
	 *            other key parameters of a Formula
	 * @param legalScope
	 *            The LegalScope used to validate if variables used in the
	 *            formula are valid
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null
	 */
	public SemanticsVisitor(FormulaManager fm, LegalScope legalScope)
	{
		if (fm == null)
		{
			throw new IllegalArgumentException("FormulaManager cannot be null");
		}
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		this.fm = fm;
		this.legalScope = legalScope;
	}

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this SemanticsVisitor being called.
	 * 
	 * @see pcgen.base.formula.parse.FormulaParserVisitor#visit(pcgen.base.formula.parse.SimpleNode,
	 *      java.lang.Object)
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
		return visitOperatorNode(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return visitOperatorNode(node, data);
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
		FormulaSemantics semantics = (FormulaSemantics) data;
		if (node.getOperator() == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Object of type " + node.getClass()
					+ " expected to have an operator, none was found");
			return semantics;
		}
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
			node.jjtGetChild(i).jjtAccept(this, semantics);
			//Consistent with the "fail fast" behavior in the implementation note
			if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID)
				.isValid())
			{
				return semantics;
			}
			/*
			 * Note: We only implement ^ for Number.class today. This is a
			 * "known" limitation, but would be nice to escape. However, this
			 * means we can't shortcut the item in evaluate... (see
			 * EvaluationVisitor)
			 */
			Class<?> format =
					semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
			if (!format.equals(NUMBER_CLASS))
			{
				FormulaSemanticsUtilities.setInvalid(semantics,
					"Parse Error: Invalid Value Format: " + format
						+ " found in "
						+ node.jjtGetChild(i).getClass().getName()
						+ " found in location requiring a"
						+ " Number (class cannot be evaluated)");
			}
		}
		return semantics;
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
			FormulaSemanticsUtilities.setInvalid(semantics,
				getInvalidCountReport(node, 0));
			return semantics;
		}
		try
		{
			Double.parseDouble(node.getText());
			semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
				NUMBER_CLASS);
		}
		catch (NumberFormatException e)
		{
			FormulaSemanticsUtilities.setInvalid(semantics, node.getClass()
				+ " had invalid number: " + node.getText());
		}
		return semantics;
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
			FormulaSemanticsUtilities.setInvalid(semantics,
				getInvalidCountReport(node, 2));
			return semantics;
		}
		Node firstChild = node.jjtGetChild(0);

		if (!(firstChild instanceof ASTPCGenSingleWord))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Function " + "Formula Name"
					+ " received invalid argument format,"
					+ " expected: ASTPCGenSingleWord got "
					+ firstChild.getClass().getName() + ": " + firstChild);
			return semantics;
		}

		/*
		 * Validate the function contents (remember it can have other complex
		 * structures inside of it)
		 */
		ASTPCGenSingleWord ftnNode = (ASTPCGenSingleWord) firstChild;
		String ftnName = ftnNode.getText();
		Node argNode = node.jjtGetChild(1);
		Function function;
		String functionForm;
		FunctionLibrary library = fm.getLibrary();
		if (argNode instanceof ASTFParen)
		{
			function = library.getFunction(ftnName);
			functionForm = "()";
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			function = library.getBracketFunction(ftnName);
			functionForm = "[]";
		}
		else
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Function Formula Arguments"
					+ " received invalid argument format, "
					+ "expected: ASTFParen, got "
					+ firstChild.getClass().getName() + ": " + firstChild);
			return semantics;
		}
		if (function == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics, "Function: "
				+ ftnName + " was not found (called as: " + ftnName
				+ functionForm + ")");
			return semantics;
		}
		//Extract arguments from the grouping to give them to the function
		int argLength = argNode.jjtGetNumChildren();
		Node[] args = new Node[argLength];
		for (int i = 0; i < argLength; i++)
		{
			args[i] = argNode.jjtGetChild(i);
		}
		function.allowArgs(this, args, semantics);
		return semantics;
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
			FormulaSemanticsUtilities.setInvalid(semantics,
				getInvalidCountReport(node, 0));
			return semantics;
		}
		String varName = node.getText();
		FormatManager<?> formatManager =
				fm.getFactory().getVariableFormat(legalScope, varName);
		if (formatManager != null)
		{
			semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
				formatManager.getManagedClass());
		}
		else
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Variable: " + varName + " was not found in scope "
					+ getLegalScope().getName());
		}
		return semantics;
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
		FormulaSemanticsUtilities.setInvalid(semantics,
			"Parse Error: Invalid Class: " + node.getClass().getName()
				+ " found in operable location (class cannot be evaluated)");
		return semantics;
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
		FormulaSemanticsUtilities.setInvalid(semantics,
			"Parse Error: Invalid Class: " + node.getClass().getName()
				+ " found in operable location (class cannot be evaluated)");
		return semantics;
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * SemanticsVisitor, reaching this node in SemanticsVisitor indicates either
	 * an error in the implementation of the formula or a tree structure problem
	 * in the formula.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT, STRING_CLASS);
		return semantics;
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2 valid
	 * children, and a non-null Operator.
	 * 
	 * @param node
	 *            The node to be validated to ensure it has valid children and a
	 *            non-null Operator
	 * @return A FormulaSemantics object, which will indicate isValid() true if
	 *         this operator has 2 valid children and a non-null Operator.
	 *         Otherwise, the FormulaSemantics will indicate isValid() false
	 */
	private Object visitOperatorNode(SimpleNode node, Object data)
	{
		final FormulaSemantics semantics = (FormulaSemantics) data;
		Operator op = node.getOperator();
		if (op == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Object of type " + node.getClass()
					+ " expected to have an operator, none was found");
			return semantics;
		}
		if (node.jjtGetNumChildren() != 2)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				getInvalidCountReport(node, 2));
			return semantics;
		}
		Node child1 = node.jjtGetChild(0);
		child1.jjtAccept(this, data);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return semantics;
		}
		//Need to capture now
		@SuppressWarnings("PMD.PrematureDeclaration")
		Class<?> format1 =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);

		Node child2 = node.jjtGetChild(1);
		child2.jjtAccept(this, semantics);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return semantics;
		}
		Class<?> format2 =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
		Class<?> returnedFormat =
				fm.getOperatorLibrary().processAbstract(op, format1, format2);
		//null response means the library couldn't find an appropriate operator
		if (returnedFormat == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Operator " + op.getSymbol()
					+ " cannot process children: " + format1.getSimpleName()
					+ " and " + format2.getSimpleName() + " found in "
					+ node.getClass().getName());
			return semantics;
		}
		semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT, returnedFormat);
		return semantics;
	}


	private Object visitUnaryNode(SimpleNode node, Object data)
	{
		FormulaSemantics semantics = (FormulaSemantics) data;
		Operator op = node.getOperator();
		if (op == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Object of type " + node.getClass()
					+ " expected to have an operator, none was found");
			return semantics;
		}
		semantics = (FormulaSemantics) singleChildValid(node, data);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return semantics;
		}
		Class<?> format =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
		Class<?> returnedFormat =
				fm.getOperatorLibrary().processAbstract(op, format);
		//null response means the library couldn't find an appropriate operator
		if (returnedFormat == null)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Operator " + op.getSymbol()
					+ " cannot process child: " + format.getSimpleName()
					+ " found in " + node.getClass().getName());
			return semantics;
		}
		semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT, returnedFormat);
		return semantics;
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
	 * @return A FormulaSemantics object, which will indicate isValid() true if
	 *         this operator has a single, valid child; Otherwise, the
	 *         FormulaSemantics will indicate isValid() false
	 */
	private Object singleChildValid(SimpleNode node, Object data)
	{
		if (node.jjtGetNumChildren() != 1)
		{
			FormulaSemantics semantics = (FormulaSemantics) data;
			FormulaSemanticsUtilities.setInvalid(semantics,
				getInvalidCountReport(node, 1));
			return semantics;
		}
		Node child = node.jjtGetChild(0);
		return child.jjtAccept(this, data);
	}

	/**
	 * Returns the LegalScope in which this SemanticsVisitor is operating.
	 * 
	 * @return the LegalScope in which this SemanticsVisitor is operating
	 */
	public LegalScope getLegalScope()
	{
		return legalScope;
	}

	/**
	 * Returns the underlying FormulaManager for this SemanticsVisitor.
	 * 
	 * @return the underlying FormulaManager for this SemanticsVisitor
	 */
	public FormulaManager getFormulaManager()
	{
		return fm;
	}

	/**
	 * Generates an invalid argument count report based on the given node and
	 * expected argument count.
	 */
	private String getInvalidCountReport(Node node, int expectedCount)
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
}
