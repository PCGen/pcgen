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
 * An AddingFormula represents a 'deferred calculation' of sorts, designed to be
 * stored and capable of adding a predetermined value to a given input number.
 * <p>
 * An AddingFormula will always return an Integer
 */
public class AddingFormula implements ReferenceFormula<Integer>
{

    /**
     * The value to be added to the value input into the resolve method
     */
    private final int add;

    /**
     * Creates a new AddingFormula with the given int as the value to be added
     * to the input to the resolve method
     *
     * @param adder the int to be added when this AddingFormula is used
     */
    public AddingFormula(int adder)
    {
        add = adder;
    }

    /**
     * Executes this AddingFormula, adding the number provided in the
     * constructor of this AddingFormula to the given Number. Only one input
     * Number is permitted.
     * <p>
     * This method is value-semantic and will not modify or maintain a reference
     * to the given Array of Numbers.
     *
     * @param numbers The array of Numbers used to resolve the value of this
     *                AddingFormula (array length must be 1 and the Number must be
     *                non-null)
     * @return the result of the addition
     * @throws IllegalArgumentException if more than one Number is provided as an argument
     * @throws NullPointerException     if the Number provided is null
     */
    @Override
    public Integer resolve(Number... numbers)
    {
        if (numbers == null || numbers.length != 1)
        {
            throw new IllegalArgumentException("AddingFormula only has one back-reference");
        }
        return numbers[0].intValue() + add;
    }

    @Override
    public String toString()
    {
        if (add >= 0)
        {
            return "+" + add;
        } else
        {
            return Integer.toString(add);
        }
    }

    @Override
    public int hashCode()
    {
        return add;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof AddingFormula && ((AddingFormula) obj).add == add;
    }
}
