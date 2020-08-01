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

import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStrategy;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
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
		return visitRelational(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return visitRelational(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return visitRelational(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return visitOperatorNode(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTUnaryMinus node, Object data)
	{
		return visitUnaryNode(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTUnaryNot node, Object data)
	{
		return visitUnaryNode(node, data);
	}

	/**
	 * Only requires a recursive check, has no dependencies itself.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		return visitOperatorNode(node, data);
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
		//We assume semantics passed
		return Optional.of(FormatUtilities.NUMBER_MANAGER);
	}

	/**
	 * Processes a FormulaFunction encountered in the formula.
	 * 
	 * This will decode what FormulaFunction is being called, using the
	 * FunctionLibrary, and then call getDependencies() on the Function, relying
	 * on the behavior of that method (as defined in the contract of the
	 * FormulaFunction interface) to load the DependencyManager.
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
			FormulaFunction function = library.getFunction(name);
			Node[] args = VisitorUtilities.accumulateArguments(argNode);
			return function.getDependencies(this, manager, args);
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			return getVariableFormat(manager, name).getComponentManager();
		}
		throw new IllegalStateException(
				"Evaluation called on invalid Function (failed semantics?)");
	}

	/**
	 * Processes a Variable. Since this means the formula is dependent on that
	 * variable, this loads the appropriate VariableID into the
	 * DependencyManager.
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		return Optional.of(visitVariable(node.getText(), (DependencyManager) data));
	}

	/**
	 * Adds a dependency on a specific variable to the given DependencyManager
	 * 
	 * @param varName
	 *            The variable name to be added as a dependency
	 * @param manager
	 *            The DependencyManager used to process the visit to the variable
	 * @return The format for the given Variable, in the scope as described by the
	 *         DependencyManager
	 */
	public FormatManager<?> visitVariable(String varName, DependencyManager manager)
	{
		Optional<VariableStrategy> varStrategy =
				manager.get(DependencyManager.VARSTRATEGY);
		if (varStrategy.isPresent())
		{
			varStrategy.get().addVariable(manager, varName);
		}
		return getVariableFormat(manager, varName);
	}

	/**
	 * Returns the format for a given Variable name, in the scope as described by the
	 * DependencyManager.
	 * 
	 * @param manager
	 *            The DependencyManager used to determine the context of analysis of the
	 *            given variable name
	 * @param varName
	 *            The variable name for which the format should be returned
	 * @return The format for the given Variable, in the scope as described by the
	 *         DependencyManager
	 */
	public FormatManager<?> getVariableFormat(DependencyManager manager, String varName)
	{
		VariableLibrary varLib = manager.get(DependencyManager.FMANAGER).getFactory();
		//Fall back to INSTANCE if necessary
		LegalScope legalScope = manager.get(DependencyManager.SCOPE).orElseGet(
			() -> manager.get(DependencyManager.INSTANCE).getLegalScope());
		return varLib.getVariableFormat(legalScope, varName);
	}

	/**
	 * This type of node is ONLY encountered as part of a an array call (which
	 * is parsed like a function). Since the FormulaFunction should have "consumed"
	 * these elements and not called back into DependencyVisitor, reaching this
	 * node in DependencyVisitor indicates either an error in the implementation
	 * of the formula or a tree structure problem in the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the FormulaFunction
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Brackets)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * FormulaFunction should have "consumed" these elements and not called back into
	 * DependencyVisitor, reaching this node in DependencyVisitor indicates
	 * either an error in the implementation of the formula or a tree structure
	 * problem in the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the FormulaFunction
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Parenthesis)");
	}

	/**
	 * A Quoted String, thus returns a StringManager, or if the appropriate format if one
	 * is asserted.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		DependencyManager manager = (DependencyManager) data;
		Optional<FormatManager<?>> asserted = manager.get(DependencyManager.ASSERTED);
		if (!asserted.isPresent())
		{
			return Optional.of(FormatUtilities.STRING_MANAGER);
		}
		FormatManager<?> assertedFormat = asserted.get();
		if (!assertedFormat.isDirect())
		{
			manager.get(DependencyManager.INDIRECTS)
				.ifPresent(indManager -> indManager
					.add(assertedFormat.convertIndirect(node.getText())));
		}
		return asserted;
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
	 * Processes an Operator node.
	 * 
	 * @param node
	 *            The node to be checked for dependencies
	 * @return A FormatManager object, which will indicate the format returned
	 *         by the Operator.
	 */
	private Optional<FormatManager<?>> visitOperatorNode(SimpleNode node, Object data)
	{
		DependencyManager manager = (DependencyManager) data;
		Node child1 = node.jjtGetChild(0);
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> format1 = (Optional<FormatManager<?>>) child1.jjtAccept(this, data);
		Node child2 = node.jjtGetChild(1);
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> format2 = (Optional<FormatManager<?>>) child2.jjtAccept(this, data);
		OperatorLibrary opLib = manager.get(DependencyManager.OPLIB);
		Operator op = node.getOperator();
		return opLib.processAbstract(op, format1.get().getManagedClass(),
			format2.get().getManagedClass(), manager.get(DependencyManager.ASSERTED));
	}

	private Optional<FormatManager<?>> visitUnaryNode(SimpleNode node, Object data)
	{
		Node child = node.jjtGetChild(0);
		@SuppressWarnings("unchecked")
		Optional<FormatManager<?>> format = (Optional<FormatManager<?>>) child.jjtAccept(this, data);
		DependencyManager manager = (DependencyManager) data;
		OperatorLibrary opLib = manager.get(DependencyManager.OPLIB);
		Operator op = node.getOperator();
		return opLib.processAbstract(op, format.get().getManagedClass());
	}

	/**
	 * Processes a relational node for dependencies.
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
		node.childrenAccept(this, data);
		return Optional.of(FormatUtilities.BOOLEAN_MANAGER);
	}
}
