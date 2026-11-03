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

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.util.FormatManager;

/**
 * A NEPFormula is a formula that is part of the "Native Equation Parser" for
 * PCGen.
 * 
 * @param <T>
 *            The Class of object returned by this NEPFormula
 */
public interface NEPFormula<T>
{

	/**
	 * Resolves the NEPFormula in the context of the given EvaluationManager. The
	 * given EvaluationManager must contain information about variable values,
	 * available functions, and other characteristics required for the formula
	 * to produce a value.
	 * 
	 * If variables and formulas required by the NEPFormula are not available in
	 * the given EvaluationManager, behavior is not guaranteed and NEPFormula or
	 * other methods called within this method reserve the right to throw an
	 * Exception or otherwise not fail gracefully. (The precise behavior is
	 * likely defined by the EvaluationManager).
	 * 
	 * Note in the case of a valid formula that the format (but not the exact
	 * class) of the return value is guaranteed by the NEPFormula. The Class may
	 * extend the format contained by the NEPFormula. The exact class returned
	 * is defined by the EvaluationManager, which can therefore implement the
	 * appropriate processing (precision in the case of numbers) desired for the
	 * given calculation.
	 * 
	 * @param manager
	 *            The EvaluationManager for the context of the formula
	 * @return The value calculated for the NEPFormula
	 */
	public T resolve(EvaluationManager manager);

	/**
	 * Processes the FormulaSemantics for the NEPFormula.
	 * 
	 * The given FormulaSemantics must contain information about variable
	 * values, available functions, and other characteristics required for the
	 * formula to establish the list of variables contained within the
	 * NEPFormula. These must be valid within the context of the format
	 * of the NEPFormula as returned by getFormatManager().
	 * 
	 * @param semantics
	 *            The FormulaSemantics object used to contain and store semantic
	 *            information about the NEPFormula
	 * @throws SemanticsException
	 *             if there is an error indicating the formula is not valid
	 */
	public void isValid(FormulaSemantics semantics) throws SemanticsException;

	/**
	 * Determines the dependencies for this formula, including the VariableID
	 * objects representing the variables within the NEPFormula.
	 * 
	 * The given DependencyManager must contain information about variable
	 * values, available functions, and other characteristics required for the
	 * formula to establish the list of variables contained within the
	 * NEPFormula.
	 * 
	 * The given DependencyManager will be loaded with the dependency
	 * information.
	 * 
	 * @param depManager
	 *            The DependencyManager to be used to capture the dependencies
	 */
	public void captureDependencies(DependencyManager depManager);
	
	/**
	 * The FormatManager in which the NEPFormula is processed (and indicating the type of
	 * value it will return if Evaluate is called).
	 * 
	 * @return The FormatManager in which the NEPFormula is processed
	 */
	public FormatManager<T> getFormatManager();
}
