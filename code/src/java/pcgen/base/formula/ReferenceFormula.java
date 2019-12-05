/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.formula;

/**
 * Represents a Formula that can have arguments provided to drive resolution of
 * the formula.
 * <p>
 * A ReferenceFormula may have restrictions on the legal number of arguments to
 * the resolve method.
 *
 * @param <T> The type of Object returned by the Formula (it is likely, but not
 *            required, that this is a Number of some form)
 */
@FunctionalInterface
public interface ReferenceFormula<T>
{

    /**
     * Executes this ReferenceFormula
     *
     * @param numbers the inputs to the formula
     * @return the result of the calculation
     */
    T resolve(Number... numbers);

}
