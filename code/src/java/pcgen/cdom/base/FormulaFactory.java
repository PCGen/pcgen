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
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * FormulaFactory is a utility class which creates Formula objects based on the
 * input provided
 */
public final class FormulaFactory
{

	/**
	 * A Formula for the integer constant ZERO. This is done in order to
	 * minimize memory usage in the many cases where a default Formula of ZERO
	 * is required.
	 */
	public static final Formula ZERO = new IntegerFormula(Integer.valueOf(0));

	/**
	 * A Formula for the integer constant ONE. This is done in order to minimize
	 * memory usage in the many cases where a default Formula of ONE is
	 * required.
	 */
	public static final Formula ONE = new IntegerFormula(Integer.valueOf(1));

	private FormulaFactory()
	{
		// Can't instantiate Utility Class
	}

	/**
	 * Returns a Formula for the given String.
	 * 
	 * @param formulaString
	 *            The String to be converted to a Formula
	 * @return A Formula for the given String.
	 * @throws IllegalArgumentException
	 *             if the given String is null or empty
	 */
	public static Formula getFormulaFor(String formulaString)
	{
		if (formulaString == null || formulaString.length() == 0)
		{
			throw new IllegalArgumentException("Formula cannot be empty");
		}
		try
		{
			return getFormulaFor(Integer.valueOf(formulaString));
		}
		catch (NumberFormatException e)
		{
			// Okay, just not an integer
			try
			{
				return getFormulaFor(Double.valueOf(formulaString));
			}
			catch (NumberFormatException e2)
			{
				// Okay, just not a double
				return new JEPFormula(formulaString);
			}
		}
	}

	/**
	 * Returns a Formula for the given String, using "old" formula system
	 * 
	 * @param formulaString
	 *            The String to be converted to a Formula
	 * @return A Formula for the given String.
	 * @throws IllegalArgumentException
	 *             if the given String is null or empty
	 */
	public static Formula getJEPFormulaFor(String formulaString)
	{
		if (formulaString == null || formulaString.length() == 0)
		{
			throw new IllegalArgumentException("Formula cannot be empty");
		}
		try
		{
			return getFormulaFor(Integer.valueOf(formulaString));
		}
		catch (NumberFormatException e)
		{
			// Okay, just not an integer
			try
			{
				return getFormulaFor(Double.valueOf(formulaString));
			}
			catch (NumberFormatException e2)
			{
				// Okay, just not a double
				return new JEPFormula(formulaString);
			}
		}
	}

	/**
	 * Returns a Formula for the given Integer.
	 * 
	 * @param integer
	 *            The int to be converted to a Formula
	 * @return A Formula for the given Integer.
	 * @throws IllegalArgumentException
	 *             if the given Integer is null
	 */
	public static Formula getFormulaFor(Integer integer)
	{
		return new IntegerFormula(integer);
	}

	/**
	 * Returns a Formula for the given Integer.
	 * 
	 * @param dbl
	 *            The double to be converted to a Formula
	 * @return A Formula for the given Double.
	 * @throws IllegalArgumentException
	 *             if the given Double is null
	 */
	public static Formula getFormulaFor(Double dbl)
	{
		return new DoubleFormula(dbl);
	}

	/**
	 * IntegerFormula is a fixed-value formula for a specific Integer.
	 */
	private static class IntegerFormula implements Formula
	{

		/**
		 * The integer value of this IntegerFormula
		 */
		private final Integer integer;

		/**
		 * Creates a new IntegerFormula from the given Integer.
		 * 
		 * @param intValue
		 *            The Integer value of this IntegerFormula.
		 * @throws IllegalArgumentException
		 *             if the given Integer is null
		 */
		public IntegerFormula(Integer intValue)
		{
			if (intValue == null)
			{
				throw new IllegalArgumentException(
						"Cannot create an IntegerFormula with a null Integer");
			}
			integer = intValue;
		}

		/**
		 * Resolves this IntegerFormula, returning the Integer in this
		 * IntegerFormula.
		 * 
		 * @return the Integer in this IntegerFormula.
		 */
		@Override
		public Integer resolve(PlayerCharacter pc, String source)
		{
			return integer;
		}

		/**
		 * Resolves this IntegerFormula, returning the Integer in this
		 * IntegerFormula.
		 * 
		 * @return the Integer in this IntegerFormula.
		 */
		@Override
		public Integer resolve(Equipment equipment, boolean primary,
				PlayerCharacter pc, String source)
		{
			return integer;
		}

		/**
		 * Returns a String representation of this IntegerFormula.
		 */
		@Override
		public String toString()
		{
			return integer.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this IntegerFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return integer.intValue();
		}

		/**
		 * Returns true if this IntegerFormula is equal to the given Object.
		 * Equality is defined as being another IntegerFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof IntegerFormula)
					&& ((IntegerFormula) obj).integer.equals(integer);
		}

		/**
		 * Returns true as an IntegerFormula has an underlying integer (static)
		 * value
		 */
		@Override
		public boolean isStatic()
		{
			return true;
		}

		/**
		 * Returns true as an IntegerFormula is a valid Formula.
		 */
		@Override
		public boolean isValid()
		{
			return true;
		}

		/**
		 * Resolves this IntegerFormula, returning the Integer in this
		 * IntegerFormula.
		 * 
		 * @return the Integer in this IntegerFormula.
		 */
		@Override
		public Integer resolveStatic()
		{
			return integer;
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
		private final Double dbl;

		/**
		 * Creates a new DoubleFormula from the given Double.
		 * 
		 * @param dblValue
		 *            The Double value of this DoubleFormula.
		 * @throws IllegalArgumentException
		 *             if the given Double is null
		 */
		public DoubleFormula(Double dblValue)
		{
			if (dblValue == null)
			{
				throw new IllegalArgumentException(
						"Cannot create an DoubleFormula with a null Double");
			}
			dbl = dblValue;
		}

		/**
		 * Resolves this DoubleFormula, returning the Double in this
		 * DoubleFormula.
		 * 
		 * @return the Double in this DoubleFormula.
		 */
		@Override
		public Double resolve(PlayerCharacter pc, String source)
		{
			return dbl;
		}

		/**
		 * Resolves this DoubleFormula, returning the Double in this
		 * DoubleFormula.
		 * 
		 * @return the Double in this DoubleFormula.
		 */
		@Override
		public Double resolve(Equipment equipment, boolean primary,
				PlayerCharacter pc, String string)
		{
			return dbl;
		}

		/**
		 * Returns a String representation of this DoubleFormula.
		 */
		@Override
		public String toString()
		{
			return dbl.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this DoubleFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return dbl.intValue();
		}

		/**
		 * Returns true if this DoubleFormula is equal to the given Object.
		 * Equality is defined as being another DoubleFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof DoubleFormula)
					&& ((DoubleFormula) obj).dbl.equals(dbl);
		}

		/**
		 * Returns true as an DoubleFormula has an underlying double (static)
		 * value
		 */
		@Override
		public boolean isStatic()
		{
			return true;
		}

		/**
		 * Returns true as an DoubleFormula is a valid Formula.
		 */
		@Override
		public boolean isValid()
		{
			return true;
		}

		/**
		 * Resolves this DoubleFormula, returning the Double in this
		 * DoubleFormula.
		 * 
		 * @return the Double in this DoubleFormula.
		 */
		@Override
		public Double resolveStatic()
		{
			return dbl;
		}
	}
}
