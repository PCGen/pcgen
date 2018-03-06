/*
 * Copyright 2016-18 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

import java.util.Collection;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * FormulaModifier is a Modifier that has additional characteristics to support PCGen
 * 
 * NOTE: This INTENTIONALLY does NOT extend Modifier<T> even though the methods are
 * capable of doing that... it is specifically designed to make people STOP and decide how
 * an item should respond as a Modifier... e.g. does it need to implement this()?
 * 
 * @param <T>
 *            The format that this FormulaModifier acts upon
 */
public interface FormulaModifier<T>
{

	/**
	 * Adds an Association to this FormulaModifier.
	 * 
	 * @param assocInstructions
	 *            The instructions of the Association to be added to this FormulaModifier
	 * 
	 * @throws IllegalArgumentException
	 *             if the given instructions are not valid or are not a supported
	 *             Association for this FormulaModifier
	 */
	public void addAssociation(String assocInstructions);

	/**
	 * Returns a Collection of the instructions (String format) for the Associations on
	 * this FormulaModifier.
	 * 
	 * Ownership of the returned Collection should be transferred to the calling object,
	 * and no reference to the underlying contents of the FormulaModifier should be
	 * maintained. (There should be no way for the FormulaModifier to alter this Collection
	 * after it is returned and no method for the returned Collection to modify the
	 * FormulaModifier)
	 * 
	 * @return the instructions (String format) for the Associations on this FormulaModifier
	 */
	public Collection<String> getAssociationInstructions();

	/**
	 * Add object references to this FormulaModifier. These are captured solely as
	 * dependency management.
	 * 
	 * @param collection
	 *            The Collection of Indirect objects that this FormulaModifier references.
	 */
	public void addReferences(Collection<Indirect<?>> collection);

	/**
	 * "Processes" (or runs) the FormulaModifier in order to determine the appropriate
	 * result of the FormulaModifier.
	 * 
	 * @param manager
	 *            The EvaluationManager that is used (if necessary) to process a Formula
	 *            that is contained by this FormulaModifier
	 * @return The resulting value of the FormulaModifier
	 */
	public T process(EvaluationManager manager);

	/**
	 * Loads the dependencies for the FormulaModifier into the given DependencyManager.
	 * 
	 * The DependencyManager may not be altered if there are no dependencies for this
	 * FormulaModifier.
	 * 
	 * @param fdm
	 *            The DependencyManager to be notified of dependencies for this
	 *            FormulaModifier
	 */
	public void getDependencies(DependencyManager fdm);

	/**
	 * Returns the priority of this FormulaModifier. This is defined by the developer, and
	 * is intended to set the order of operations for a Modifier when processed by a
	 * Solver.
	 * 
	 * A lower priority is acted upon first.
	 * 
	 * For example, a calculation that performs Multiplication would want to have a lower
	 * priority (acting first) than a calculation that performs addition (since
	 * multiplication before addition is the natural order of operations in mathematics)
	 * 
	 * @return The priority of this calculation
	 */
	public long getPriority();

	/**
	 * Returns the FormatManager for the the Format (Class) of the object upon which this
	 * FormulaModifier can operate. May have an underlying parent class if the
	 * FormulaModifier can act upon various related classes such as java.lang.Number.
	 * 
	 * @return The FormatManager of the Format (class) of the object upon which this
	 *         FormulaModifier can operate
	 */
	public FormatManager<T> getVariableFormat();

	/**
	 * Returns a String identifying the FormulaModifier. May be "ADD" for a
	 * FormulaModifier that performs Addition.
	 * 
	 * @return A String identifying the behavior of the FormulaModifier
	 */
	public String getIdentification();

	/**
	 * Returns a String identifying the formula used for FormulaModifier. May be "3" for a
	 * FormulaModifier that performs Addition of 3.
	 * 
	 * @return A String identifying the formula used for FormulaModifier
	 */
	public String getInstructions();
}
