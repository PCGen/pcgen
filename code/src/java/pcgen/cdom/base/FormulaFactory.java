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
import pcgen.base.formula.analysis.FormulaFormat;
import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.util.FormatManager;
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
	public static final Formula ZERO = new NumberFormula(Integer.valueOf(0));

	/**
	 * A Formula for the integer constant ONE. This is done in order to minimize
	 * memory usage in the many cases where a default Formula of ONE is
	 * required.
	 */
	public static final Formula ONE = new NumberFormula(Integer.valueOf(1));

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
	 * Returns a Formula for the given Number.
	 * 
	 * @param number
	 *            The int to be converted to a Formula
	 * @return A Formula for the given Number.
	 * @throws IllegalArgumentException
	 *             if the given Number is null
	 */
	public static Formula getFormulaFor(Number number)
	{
		return new NumberFormula(number);
	}

	/**
	 * NumberFormula is a fixed-value formula for a specific Integer.
	 */
	private static class NumberFormula implements Formula
	{

		/**
		 * The Number value of this NumberFormula
		 */
		private final Number number;

		/**
		 * Creates a new NumberFormula from the given Number.
		 * 
		 * @param intValue
		 *            The Number value of this NumberFormula.
		 * @throws IllegalArgumentException
		 *             if the given Number is null
		 */
		public NumberFormula(Number intValue)
		{
			if (intValue == null)
			{
				throw new IllegalArgumentException(
					"Cannot create an NumberFormula with a null Number");
			}
			number = intValue;
		}

		/**
		 * Resolves this NumberFormula, returning the Number in this
		 * NumberFormula.
		 * 
		 * @return the Number in this NumberFormula.
		 */
		@Override
		public Number resolve(PlayerCharacter pc, String source)
		{
			return number;
		}

		/**
		 * Resolves this NumberFormula, returning the Number in this
		 * NumberFormula.
		 * 
		 * @return the Number in this NumberFormula.
		 */
		@Override
		public Number resolve(Equipment equipment, boolean primary,
			PlayerCharacter pc, String source)
		{
			return number;
		}

		/**
		 * Returns a String representation of this NumberFormula.
		 */
		@Override
		public String toString()
		{
			return number.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this NumberFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return number.intValue();
		}

		/**
		 * Returns true if this NumberFormula is equal to the given Object.
		 * Equality is defined as being another NumberFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof NumberFormula)
				&& ((NumberFormula) obj).number.equals(number);
		}

		/**
		 * Returns true as an NumberFormula has an underlying Number (static)
		 * value
		 */
		@Override
		public boolean isStatic()
		{
			return true;
		}

		/**
		 * Returns true as an NumberFormula is a valid Formula.
		 */
		@Override
		public boolean isValid()
		{
			return true;
		}

		/**
		 * Resolves this NumberFormula, returning the Number in this
		 * NumberFormula.
		 * 
		 * @return the Integer in this NumberFormula.
		 */
		@Override
		public Number resolveStatic()
		{
			return number;
		}
	}

	/**
	 * SimpleFormula is a fixed-value formula for a specific value.
	 */
	private static class SimpleFormula<T> implements NEPFormula<T>
	{

		/**
		 * The value of this SimpleFormula
		 */
		private final T value;

		/**
		 * Creates a new SimpleFormula from the given value.
		 * 
		 * @param val
		 *            The value of this SimpleFormula.
		 * @throws IllegalArgumentException
		 *             if the given value is null
		 */
		public SimpleFormula(T val)
		{
			if (val == null)
			{
				throw new IllegalArgumentException(
					"Cannot create an SimpleFormula with a null value");
			}
			value = val;
		}

		/**
		 * Returns a String representation of this SimpleFormula.
		 */
		@Override
		public String toString()
		{
			return value.toString();
		}

		/**
		 * Returns the consistent-with-equals hashCode for this SimpleFormula
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}

		/**
		 * Returns true if this SimpleFormula is equal to the given Object.
		 * Equality is defined as being another SimpleFormula object with equal
		 * value.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof SimpleFormula)
				&& ((SimpleFormula<?>) obj).value.equals(value);
		}

		/**
		 * @see pcgen.base.calculation.NEPCalculation#getDependencies(pcgen.base.formula.manager.ScopeInformation,
		 *      pcgen.base.formula.dependency.DependencyManager)
		 */
		@Override
		public void getDependencies(ScopeInformation scopeInfo,
			DependencyManager arg1)
		{
			//None
		}

		/**
		 * @see pcgen.base.formula.NEPFormula#resolve(pcgen.base.formula.manager.ScopeInformation)
		 */
		@Override
		public T resolve(ScopeInformation scopeInfo)
		{
			return value;
		}

		/**
		 * @see pcgen.base.formula.inst.NEPFormula#isValid(pcgen.base.formula.manager.FormulaManager,
		 *      pcgen.base.formula.base.LegalScope,
		 *      pcgen.base.format.FormatManager)
		 */
		@Override
		public FormulaSemantics isValid(FormulaManager fm, LegalScope varScope,
			FormatManager<T> formatManager)
		{
			FormulaSemantics semantics =
					FormulaSemanticsUtilities.getInitializedSemantics();
			semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
				new FormulaFormat(formatManager.getManagedClass()));
			return semantics;
		}
	}

	/**
	 * Returns a "New Equation Parser" formula for the given String when
	 * interpreted by the given FormatManager.
	 * 
	 * Due to the type implied by the construction of ComplexNEPFormula, this
	 * should remain private and external users should be encouraged to use
	 * getValidFormula (or create a new NEPFormula themselves and check it for
	 * validity).
	 * 
	 * @param fmtManager
	 *            The FormulaManager to be used to interpret a "simple" formula
	 * @param expression
	 *            The expression to be interpreted by the formula parser
	 * @return The NEPFormula representing the given expression
	 */
	private static <T> NEPFormula<T> getNEPFormulaFor(
		FormatManager<T> fmtManager, String expression)
	{
		if (expression == null || expression.length() == 0)
		{
			throw new IllegalArgumentException("Formula cannot be empty");
		}
		try
		{
			return new SimpleFormula<T>(fmtManager.convert(expression));
		}
		catch (IllegalArgumentException e)
		{
			// Okay, not simple :P
			return new ComplexNEPFormula<T>(expression);
		}
	}

	/**
	 * Returns a "valid" NEPFormula for the given expression.
	 * 
	 * If the given expression does not represent a valid formula, then this
	 * will throw an IllegalArgumentException.
	 * 
	 * If the given expression does not return an object of the type in the
	 * given FormatManager, then this will throw an IllegalArgumentException.
	 * 
	 * @param expression
	 *            The String representation of the formula to be converted to a
	 *            NEPFormula
	 * @param formulaManager
	 *            The FormulaManager to be used for validating the NEPExpression
	 * @param varScope
	 *            The LegalScope in which the NEPFormula is established and
	 *            checked
	 * @param formatManager
	 *            The FormatManager in which the NEPFormula is established and
	 *            checked
	 * @return a "valid" NEPFormula for the given expression
	 */
	public static <T> NEPFormula<T> getValidFormula(String expression,
		FormulaManager formulaManager, LegalScope varScope,
		FormatManager<T> formatManager)
	{
		Class<T> varClass = formatManager.getManagedClass();
		NEPFormula<T> formula = getNEPFormulaFor(formatManager, expression);
		FormulaSemantics semantics =
				formula.isValid(formulaManager, varScope, formatManager);
		if (semantics.getInfo(FormulaSemanticsUtilities.SEM_VALID).isValid())
		{
			Class<?> formulaClass =
					semantics.getInfo(FormulaSemanticsUtilities.SEM_FORMAT)
						.getFormat();
			if (formulaClass.equals(varClass))
			{
				return formula;
			}
			throw new IllegalArgumentException("Formula: " + expression
				+ " returned: " + formulaClass.getCanonicalName() + " but "
				+ varClass.getCanonicalName() + " was required");
		}
		throw new IllegalArgumentException("Cannot create a Formula from: "
			+ expression
			+ ", due to: "
			+ semantics.getInfo(FormulaSemanticsUtilities.SEM_REPORT)
				.getReport());
	}
}
