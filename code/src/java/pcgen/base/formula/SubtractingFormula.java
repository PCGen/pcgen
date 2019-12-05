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
 * A SubtractingFormula represents a 'deferred calculation' of sorts, designed
 * to be stored and capable of subtracting a predetermined value from a given
 * input number.
 * <p>
 * A SubtractingFormula will always return an Integer
 */
public class SubtractingFormula implements ReferenceFormula<Integer>
{

    /**
     * The value to be subtracted from the input value
     */
    private final int sub;

    /**
     * Creates a new SubtractingFormula with the given int as the value to be
     * subtracted from the input to the resolve method
     *
     * @param decrement the int to be subtracted when this SubtractingFormula is used
     */
    public SubtractingFormula(int decrement)
    {
        sub = decrement;
    }

    /**
     * Executes this SubtractingFormula, subtracting the number provided in the
     * constructor of this SubtractingFormula from the given Number. Only one
     * input Number is permitted.
     * <p>
     * This method is value-semantic and will not modify or maintain a reference
     * to the given Array of Numbers.
     *
     * @param numbers The array of Numbers used to resolve the value of this
     *                SubtractingFormula (array length must be 1 and the Number must
     *                be non-null)
     * @return the result of the subtraction
     * @throws IllegalArgumentException if more than one Number is provided as an argument
     * @throws NullPointerException     if the Number provided is null
     */
    @Override
    public Integer resolve(Number... numbers)
    {
        if (numbers == null || numbers.length != 1)
        {
            throw new IllegalArgumentException("SubtractingFormula only has one back-reference");
        }
        return numbers[0].intValue() - sub;
    }

    @Override
    public String toString()
    {
        if (sub >= 0)
        {
            return "-" + sub;
        } else
        {
            return "+" + -sub;
        }
    }

    @Override
    public int hashCode()
    {
        return sub;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof SubtractingFormula && ((SubtractingFormula) obj).sub == sub;
    }
}
