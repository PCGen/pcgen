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
package pcgen.base.solver;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsException;
import pcgen.base.util.FormatManager;

/**
 * A Modifier is designed to change an input (of a given format) to another
 * object of that format.
 * 
 * There is no requirement that a modifier take into account the input value (it
 * can be a "set").
 * 
 * Note that a Modifier is NOT intended to have side effects as it processes an
 * item.
 * 
 * @param <T>
 *            The format that this Modifier acts upon
 */
public interface Modifier<T>
{
	/**
	 * "Processes" (or runs) the Modifier in order to determine the appropriate
	 * result of the Modifier.
	 * 
	 * @param manager
	 *            The EvaluationManager that is used (if necessary) to process a
	 *            Formula that is contained by this Modifier
	 * @return The resulting value of the Modifier
	 */
	public T process(EvaluationManager manager);

	/**
	 * Loads the dependencies for the Modifier into the given DependencyManager.
	 * 
	 * The DependencyManager may not be altered if there are no dependencies for
	 * this Modifier.
	 * 
	 * @param depManager
	 *            The DependencyManager to be notified of dependencies for this
	 *            Modifier
	 */
	public void captureDependencies(DependencyManager depManager);

	/**
	 * Processes this Modifier to determine if it is valid within the rules provided by
	 * the given FormulaSemantics.
	 * 
	 * @param semantics
	 *            The FormulaSemantics holding information about the context in which the
	 *            Modifier is to be resolved
	 * @throws SemanticsException
	 *             If there is a Semantics issue with the Modifier
	 */
	public void isValid(FormulaSemantics semantics) throws SemanticsException;

	/**
	 * Returns the priority of this Modifier. This is defined by the developer,
	 * and is intended to set the order of operations for a Modifier when
	 * processed by a Solver.
	 * 
	 * A lower priority is acted upon first.
	 * 
	 * For example, a calculation that performs Multiplication would want to
	 * have a lower priority (acting first) than a calculation that performs
	 * addition (since multiplication before addition is the natural order of
	 * operations in mathematics)
	 * 
	 * @return The priority of this calculation
	 */
	public long getPriority();

	/**
	 * Returns the FormatManager for the the Format (Class) of the object upon which this
	 * Modifier can operate. May have an underlying parent class if the Modifier can act
	 * upon various related classes such as java.lang.Number.
	 * 
	 * @return The FormatManager of the Format (class) of the object upon which this
	 *         Modifier can operate
	 */
	public FormatManager<T> getVariableFormat();

	/**
	 * Returns a String identifying the Modifier. May be "ADD" for a Modifier
	 * that performs Addition.
	 * 
	 * @return A String identifying the behavior of the Modifier
	 */
	public String getIdentification();

	/**
	 * Returns a String identifying the formula used for Modifier. May be "3"
	 * for a Modifier that performs Addition of 3.
	 * 
	 * @return A String identifying the formula used for Modifier
	 */
	public String getInstructions();
}
