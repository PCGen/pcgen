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
package pcgen.base.calculation;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;

/**
 * A NEPCalculation is designed to be passed into a CalculationModifier. This
 * interface is much akin to a complicated closure (see Java 8), since it is
 * really designed for that single use.
 * 
 * There is no requirement that a NEPCalculation take into account the input
 * value (it can be a "set").
 * 
 * Note that a NEPCalculation is NOT intended to have side effects as it
 * processes an item.
 * 
 * @param <T>
 *            The format of object upon which this NEPCalculation can operate
 */
public interface NEPCalculation<T> extends CalculationInfo<T>
{
	/**
	 * "Processes" (or runs) the NEPCalculation in order to determine the
	 * appropriate result of the NEPCalculation.
	 * 
	 * There is no requirement that a NEPCalculation take into account the input
	 * value (it can be a "set").
	 * 
	 * The NEPCalculation should treat the input as an Immutable object (it does
	 * not gain ownership of that parameter).
	 * 
	 * @param evalManager
	 *            The EvaluationManager used to determine the appropriate result
	 *            of this NEPCalculation
	 * @return The resulting value of the NEPCalculation
	 */
	public T process(EvaluationManager evalManager);

	/**
	 * Loads the dependencies for the NEPCalculation into the given
	 * DependencyManager.
	 * 
	 * The DependencyManager may not be altered if there are no dependencies for
	 * this NEPCalculation.
	 * 
	 * @param fdm
	 *            The DependencyManager to be notified of dependencies for this
	 *            NEPCalculation
	 */
	public void getDependencies(DependencyManager fdm);

	/**
	 * Returns a String identifying the formula used for calculation. May be "3"
	 * for a calculation that performs Addition of 3.
	 * 
	 * @return A String identifying the formula used for calculation
	 */
	public String getInstructions();
}
