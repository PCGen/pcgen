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
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AddingFormula represents a 'deferred calculation' of sorts, designed to be
 * stored and capable of adding a predetermined value to a given input number.
 * 
 * A AddingFormula will always return an Integer
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
	 * @param adder
	 *            the int to be added when this AddingFormula is used
	 */
	public AddingFormula(int adder)
	{
		add = adder;
	}

	/**
	 * Executes this AddingFormula, adding the number provided in the
	 * constructor of this AddingFormula to the given Number. Only one input
	 * Number is permitted.
	 * 
	 * @return the result of the addition
	 * @throws IllegalArgumentException
	 *             if more than one Number is provided as an argument
	 * @see pcgen.base.formula.ReferenceFormula#resolve(java.lang.Number[])
	 */
	public Integer resolve(Number... nums)
	{
		if (nums == null || nums.length != 1)
		{
			throw new IllegalArgumentException(
				"AddingFormula only has one backreference");
		}
		return Integer.valueOf(nums[0].intValue() + add);
	}

	/**
	 * Returns a String representation of this AddingFormula
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (add >= 0)
		{
			return "+" + add;
		}
		else
		{
			return Integer.toString(add);
		}
	}

	/**
	 * Consistent-with-equals hashCode method
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return add;
	}

	/**
	 * Returns true if this AddingFormula is equal to the given Object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof AddingFormula && ((AddingFormula) o).add == add;
	}
}
