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
package pcgen.base.formula.base;


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
public interface FormulaManager
{

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	public VariableLibrary getFactory();

	/**
	 * Returns the VariableStore used to hold variables values for items
	 * processed through this FormulaManager.
	 * 
	 * @return The VariableStore used to hold variables values for items
	 *         processed through this FormulaManager
	 */
	public VariableStore getResolver();

	/**
	 * Returns the FunctionLibrary used to store valid functions in this
	 * FormulaManager.
	 * 
	 * @return The FunctionLibrary used to store valid functions in this
	 *         FormulaManager
	 */
	public FunctionLibrary getLibrary();

	/**
	 * Returns the OperatorLibrary used to store valid operations in this
	 * FormulaManager.
	 * 
	 * @return The OperatorLibrary used to store valid operations in this
	 *         FormulaManager
	 */
	public OperatorLibrary getOperatorLibrary();

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
	public FormulaManager swapFunctionLibrary(FunctionLibrary ftnLib);

}
