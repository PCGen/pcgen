/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.function;

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.util.FormatManager;

/**
 * FunctionUtilities are supporting methods for FormulaFunction objects.
 */
public class FunctionUtilities
{

	/**
	 * Ensures the given ASTNum is a positive integer value. Throws a
	 * SemanticsFailureException if the value is not a positive integer.
	 * 
	 * @param node
	 *            The ASTNum to be checked to ensure it is a positive integer.
	 */
	public static void ensurePositiveInteger(ASTNum node)
	{
		int value = convertToInteger(node);
		if (value < 0)
		{
			throw new SemanticsFailureException("Parse Error: Invalid Value: "
				+ node.getClass().getName() + " contained " + node.getText()
				+ " but a positive integer was expected (class cannot be evaluated)");
		}
	}

	/**
	 * Returns the value of the given ASTNum if it is an integer value. Throws a
	 * SemanticsFailureException if the value is not an integer.
	 * 
	 * @param node
	 *            The ASTNum to be converted to an integer.
	 * @return The integer value of the ASTNum, if it is an integer
	 */
	public static int convertToInteger(ASTNum node)
	{
		try
		{
			return Integer.parseInt(node.getText());
		}
		catch (NumberFormatException e)
		{
			throw new SemanticsFailureException("Parse Error: Invalid Value: "
				+ node.getClass().getName() + " contained " + node.getText()
				+ " but an integer was expected (class cannot be evaluated)",
				e);
		}
	}

	/**
	 * Validates the given array of Nodes contains the expected number of entries; throws
	 * a SemanticsFailureException if not.
	 * 
	 * @param function
	 *            The function for which the arguments are being tested
	 * @param args
	 *            The arguments to the function
	 * @param expected
	 *            The expected number of arguments to the function
	 */
	public static void validateArgCount(FormulaFunction function, Node[] args,
		int expected)
	{
		if (args.length != expected)
		{
			throw new SemanticsFailureException("Function "
				+ function.getFunctionName()
				+ " received incorrect # of arguments, expected: " + expected
				+ " got " + args.length + " " + Arrays.asList(args));
		}
	}

	/**
	 * Ensures the two given Format entries match. Throws a SemanticsFailureException if
	 * the formats do not match.
	 * 
	 * @param node
	 *            The Node for which the Format is being checked
	 * @param nodeFormat
	 *            The Format of the given Node
	 * @param expected
	 *            The expected Format of the given node.
	 */
	public static void ensureMatchingFormat(Node node,
		FormatManager<?> nodeFormat, FormatManager<?> expected)
	{
		if (!nodeFormat.equals(expected))
		{
			throw new SemanticsFailureException(
				"Parse Error: Invalid Value Format: " + nodeFormat
					+ " found in " + node.getClass().getName()
					+ " found in location requiring a" + " "
					+ expected.getIdentifierType()
					+ " (class cannot be evaluated)");
		}
	}

	/**
	 * Ensures that the value of the given Node matches a given format.
	 * 
	 * Note that this provides for the implied flexibility of the formula system, meaning
	 * if this method is told a Number is expected, a string value containing a number
	 * WILL be legal (e.g. "1" is legal).
	 * 
	 * @param visitor
	 *            The SemanticsVisitor used to evaluate nodes
	 * @param semantics
	 *            The FormulaSemantics used to support evaluation of nodes
	 * @param node
	 *            The node to be evaluated
	 * @param expected
	 *            The expected Format of the node
	 */
	public static void ensureMatchingFormat(SemanticsVisitor visitor,
		FormulaSemantics semantics, Node node, FormatManager<?> expected)
	{
		FormatManager<?> format =
				(FormatManager<?>) node.jjtAccept(visitor, semantics
					.getWith(FormulaSemantics.ASSERTED, Optional.of(expected)));
		ensureMatchingFormat(node, format, expected);
	}
	
	/**
	 * Ensures the given node (located at the given argument number) is a Quoted String.
	 * If not, throws a SemanticsFailureException.
	 * 
	 * @param node
	 *            The node to be checked
	 * @param arg
	 *            The location of the node (for error reporting)
	 */
	public static void ensureQuotedString(Node node, int arg)
	{
		if (!(node instanceof ASTQuotString))
		{
			//Error
			throw new SemanticsFailureException(
				"Parse Error: Invalid argument #" + arg + ": is a "
					+ node.getClass().getName()
					+ ", Must be a (Static) String");
		}
	}

	/**
	 * Returns the quoted string within the given node if the node is an ASTQuotString. If
	 * not, throws a SemanticsFailureException.
	 * 
	 * @param node
	 *            The node to be checked
	 * @param arg
	 *            The location of the node (for error reporting)
	 * @return The text value of the node
	 */
	public static String getQuotedString(Node node, int arg)
	{
		if (node instanceof ASTQuotString)
		{
			return ((ASTQuotString) node).getText();
		}
		throw new SemanticsFailureException(
			"Parse Error: Invalid argument #" + arg + ": is a "
				+ node.getClass().getName() + ", Must be a (Static) String");
	}

}
