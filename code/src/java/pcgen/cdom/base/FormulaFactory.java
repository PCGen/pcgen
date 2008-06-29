/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.base.formula.Formula;
import pcgen.core.PlayerCharacter;

/**
 * FormulaFactory is a utility class which creates Formula objects based on the
 * input provided
 */
public final class FormulaFactory
{

	private FormulaFactory()
	{
		// Can't instantiate Utility Class
	}

	/**
	 * Returns a Formula for the given String.
	 * 
	 * @param s
	 *            The String to be converted to a Formula
	 * @return A Formula for the given String.
	 * @throws IllegalArgumentException
	 *             if the given String is null or empty
	 */
	public static Formula getFormulaFor(String s)
	{
		if (s == null || s.length() == 0)
		{
			throw new IllegalArgumentException("Formula cannot be empty");
		}
		try
		{
			return getFormulaFor(Integer.valueOf(s));
		}
		catch (NumberFormatException e)
		{
			// Okay, just not an integer
			try
			{
				return getFormulaFor(Double.valueOf(s));
			}
			catch (NumberFormatException e2)
			{
				// Okay, just not a double
				return new JEPFormula(s);
			}
		}
	}

	/**
	 * Returns a Formula for the given Integer.
	 * 
	 * @param i
	 *            The int to be converted to a Formula
	 * @return A Formula for the given Integer.
	 * @throws IllegalArgumentException
	 *             if the given Integer is null
	 */
	public static Formula getFormulaFor(Integer i)
	{
		return new IntegerFormula(i);
	}

	/**
	 * Returns a Formula for the given Integer.
	 * 
	 * @param e
	 *            The double to be converted to a Formula
	 * @return A Formula for the given Double.
	 * @throws IllegalArgumentException
	 *             if the given Double is null
	 */
	public static Formula getFormulaFor(Double d)
	{
		return new DoubleFormula(d);
	}

	/**
	 * IntegerFormula is a fixed-value formula for a specific Integer.
	 */
	private static class IntegerFormula implements Formula
	{

		/**
		 * The integer value of this IntegerFormula
		 */
		private final Integer i;

		/**
		 * Creates a new IntegerFormula from the given Integer.
		 * 
		 * @param in
		 *            The Integer value of this IntegerFormula.
		 * @throws IllegalArgumentException
		 *             if the given Integer is null
		 */
		public IntegerFormula(Integer in)
		{
			if (in == null)
			{
				throw new IllegalArgumentException(
						"Cannot create an IntegerFormula with a null Integer");
			}
			i = in;
		}

		/**
		 * Resolves this IntegerFormula, returning the Integer in this
		 * IntegerFormula.
		 * 
		 * @return the Integer in this IntegerFormula.
		 */
		public Integer resolve(PlayerCharacter pc, String source)
		{
			return i;
		}

		/**
		 * Returns a String representation of this IntegerFormula.
		 */
		@Override
		public String toString()
		{
			return i.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this IntegerFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return i;
		}

		/**
		 * Returns true if this IntegerFormula is equal to the given Object.
		 * Equality is defined as being another IntegerFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o)
		{
			return (o instanceof IntegerFormula)
					&& ((IntegerFormula) o).i.equals(i);
		}

		public boolean isStatic()
		{
			return true;
		}
	}

	/**
	 * DoubleFormula is a fixed-value formula for a specific Double.
	 */
	private static class DoubleFormula implements Formula
	{

		/**
		 * The Double value of this DoubleFormula
		 */
		private final Double i;

		/**
		 * Creates a new DoubleFormula from the given Double.
		 * 
		 * @param in
		 *            The Double value of this DoubleFormula.
		 * @throws IllegalArgumentException
		 *             if the given Double is null
		 */
		public DoubleFormula(Double in)
		{
			if (in == null)
			{
				throw new IllegalArgumentException(
						"Cannot create an DoubleFormula with a null Double");
			}
			i = in;
		}

		/**
		 * Resolves this DoubleFormula, returning the Double in this
		 * DoubleFormula.
		 * 
		 * @return the Double in this DoubleFormula.
		 */
		public Double resolve(PlayerCharacter pc, String source)
		{
			return i;
		}

		/**
		 * Returns a String representation of this DoubleFormula.
		 */
		@Override
		public String toString()
		{
			return i.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this DoubleFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return i.intValue();
		}

		/**
		 * Returns true if this DoubleFormula is equal to the given Object.
		 * Equality is defined as being another DoubleFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o)
		{
			return (o instanceof DoubleFormula)
					&& ((DoubleFormula) o).i.equals(i);
		}

		public boolean isStatic()
		{
			return true;
		}
	}
}
