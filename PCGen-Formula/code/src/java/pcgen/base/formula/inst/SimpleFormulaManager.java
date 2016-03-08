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
package pcgen.base.formula.inst;

import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.util.FormatManager;

/**
 * A FormulaManager exists as compound object to simplify those things that
 * require context to be resolved (legal functions, variables). This provides a
 * convenient, single location for consolidation of these capabilities (and thus
 * keeps the number of parameters that have to be passed around to a reasonable
 * level).
 * 
 * This is also an object used to "cache" the SemanticsVisitor (since the
 * visitor needs to know some of the contents in the FormulaManager, it can be
 * lazily instantiated but then effectively cached as long as that
 * FormulaManager is reused - especially valuable for things like the global
 * context which in the future we can create once for the PC and never have to
 * recreate...)
 */
public class SimpleFormulaManager implements FormulaManager
{
	/**
	 * The FunctionLibrary used to store valid functions in this FormulaManager.
	 */
	private final FunctionLibrary ftnLibrary;

	/**
	 * The OperatorLibrary used to store valid operators in this FormulaManager.
	 */
	private final OperatorLibrary opLibrary;

	/**
	 * The VariableLibrary used to get VariableIDs.
	 */
	private final VariableLibrary varLibrary;

	/**
	 * The active VariableStore used to cache results of items processed through
	 * this FormulaManager (thus serves as a storage location for variable
	 * values).
	 */
	private final VariableStore results;

	/**
	 * Constructs a new FormulaManager from the provided FunctionLibrary,
	 * OperatorLibrary, VariableLibrary, and VariableStore.
	 * 
	 * @param ftnLibrary
	 *            The FunctionLibrary used to store valid functions in this
	 *            FormulaManager
	 * @param opLibrary
	 *            The OperatorLibrary used to store valid operators in this
	 *            FormulaManager
	 * @param varLibrary
	 *            The VariableLibrary used to get VariableIDs
	 * @param resultStore
	 *            The VariableStore used to hold variables values for items
	 *            processed through this FormulaManager
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	public SimpleFormulaManager(FunctionLibrary ftnLibrary,
		OperatorLibrary opLibrary, VariableLibrary varLibrary,
		VariableStore resultStore)
	{
		if (ftnLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null FunctionLibrary");
		}
		if (opLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null OperatorLibrary");
		}
		if (varLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableLibrary");
		}
		if (resultStore == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableStore");
		}
		this.ftnLibrary = ftnLibrary;
		this.opLibrary = opLibrary;
		this.varLibrary = varLibrary;
		this.results = resultStore;
	}

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	@Override
	public VariableLibrary getFactory()
	{
		return varLibrary;
	}

	/**
	 * Returns the VariableStore used to hold variables values for items
	 * processed through this FormulaManager.
	 * 
	 * @return The VariableStore used to hold variables values for items
	 *         processed through this FormulaManager
	 */
	@Override
	public VariableStore getResolver()
	{
		return results;
	}

	/**
	 * Returns the FunctionLibrary used to store valid functions in this
	 * FormulaManager.
	 * 
	 * @return The FunctionLibrary used to store valid functions in this
	 *         FormulaManager
	 */
	@Override
	public FunctionLibrary getLibrary()
	{
		return ftnLibrary;
	}

	/**
	 * Returns the OperatorLibrary used to store valid operations in this
	 * FormulaManager.
	 * 
	 * @return The OperatorLibrary used to store valid operations in this
	 *         FormulaManager
	 */
	@Override
	public OperatorLibrary getOperatorLibrary()
	{
		return opLibrary;
	}

	/**
	 * Returns the FormulaSemantics for the formula starting with with the given
	 * SimpleNode as the root of the parsed tree of the formula.
	 * 
	 * @param root
	 *            The starting node in a parsed tree of a formula, to be used
	 *            for the semantics evaluation
	 * @param legalScope
	 *            The LegalScope used to check for validity of variables used
	 *            within the formula
	 * @param formatManager
	 *            The FormatManager used to check for validity of variables used
	 *            within the formula
	 * @return The FormulaSemantics for the formula starting with with the given
	 *         SimpleNode as the root of the parsed tree of the formula
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	@Override
	public FormulaSemantics isValid(SimpleNode root, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		if (root == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null root");
		}
		if (formatManager == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null FormatManager");
		}
		SemanticsVisitor semanticsVisitor =
				new SemanticsVisitor(this, legalScope);
		FormulaSemantics semantics =
				FormulaSemanticsUtilities.getInitializedSemantics();
		semanticsVisitor.visit(root, semantics);
		if (!semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			return semantics;
		}
		Class<?> nsFormat = formatManager.getManagedClass();
		Class<?> formulaFormat =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT);
		if (!nsFormat.isAssignableFrom(formulaFormat))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Invalid Value Format: " + formulaFormat
					+ " found in " + root.getClass().getName()
					+ " found in location requiring a " + nsFormat
					+ " (class cannot be evaluated)");
		}
		return semantics;
	}

	/**
	 * Returns a new FormulaManager, with similar features to this
	 * FormulaManager, but with the FunctionLibrary swapped for the given
	 * FunctionLibrary.
	 * 
	 * @param ftnLib
	 *            The FunctionLibrary to be included in the returned
	 *            FormulaManager
	 * @return a new FormulaManager, with similar features to this
	 *         FormulaManager, but with the FunctionLibrary swapped for the
	 *         given FunctionLibrary
	 */
	@Override
	public FormulaManager swapFunctionLibrary(FunctionLibrary ftnLib)
	{
		return new SimpleFormulaManager(ftnLib, opLibrary, varLibrary, results);
	}

}
