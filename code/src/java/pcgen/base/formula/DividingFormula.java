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
 * A DividingFormula represents a 'deferred calculation' of sorts, designed to
 * be stored and capable of dividing a given input number by a predetermined
 * value
 * <p>
 * A DividingFormula will always return an Integer
 */
public class DividingFormula implements ReferenceFormula<Integer>
{

    /**
     * The value the input into the resolve method will be divided by
     */
    private final int denominator;

    /**
     * Creates a new DividingFormula with the given int as the value to divide
     * the input to the resolve method by
     *
     * @param denom denominator, the int to be divide the input by when this DividingFormula is
     *              used
     * @throws IllegalArgumentException if the given int is zero
     */
    public DividingFormula(int denom)
    {
        if (denom == 0)
        {
            throw new IllegalArgumentException("Cannot build a DividingFormula that divides by Zero - "
                    + "will always cause an ArithmeticException when resolved");
        }
        denominator = denom;
    }

    /**
     * Executes this DividingFormula, dividing the number provided by the
     * integer given in the constructor of this DividingFormula. Only one input
     * Number is permitted.
     * <p>
     * This method is value-semantic and will not modify or maintain a reference
     * to the given Array of Numbers.
     *
     * @param numbers The array of Numbers used to resolve the value of this
     *                DividingFormula (array length must be 1 and the Number must be
     *                non-null)
     * @return the result of the division
     * @throws IllegalArgumentException if more than one Number is provided as an argument
     * @throws NullPointerException     if the Number provided is null
     */
    @Override
    public Integer resolve(Number... numbers)
    {
        if (numbers == null || numbers.length != 1)
        {
            throw new IllegalArgumentException("DividingFormula only has one back-reference");
        }
        /*
         * Note that there is NOT an order of operations issue here with
         * rounding, and rounding first results in a faster & more accurate
         * calculation.
         */
        return numbers[0].intValue() / denominator;
    }

    @Override
    public String toString()
    {
        return "/" + denominator;
    }

    @Override
    public int hashCode()
    {
        return denominator;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof DividingFormula && ((DividingFormula) obj).denominator == denominator;
    }
}
