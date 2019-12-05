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

/**
 * A BasicCalculation is a calculation performed on two objects of the same
 * format. This could be two Number objects or two Boolean objects, etc.
 *
 * @param <T> The format of object processed by this BasicCalculation
 */
public interface BasicCalculation<T> extends CalculationInfo<T>
{

    /**
     * "Processes" (or runs) the BasicCalculation in order to determine the
     * appropriate result of the BasicCalculation.
     * <p>
     * There is no requirement that a BasicCalculation take into account the
     * input value (it can be a "set").
     * <p>
     * The BasicCalculation should treat the input as an Immutable object (it
     * does not gain ownership of that parameter).
     * <p>
     * Note that the two parameters may be order dependent in some
     * BasicCalculation objects, so classes calling a BasicCalculation should
     * carefully consider the order of arguments to a BasicCalculation.
     *
     * @param previousValue The first input value used (if necessary) to determine the
     *                      appropriate result of this BasicCalculation. Would be
     *                      considered the "previous" value for this BasicCalculation.
     * @param argument      The second input value used (if necessary) to determine the
     *                      appropriate result of this BasicCalculation. Would be
     *                      considered the argument for this BasicCalculation.
     * @return The resulting value of the BasicCalculation
     */
    T process(T previousValue, T argument);
}
