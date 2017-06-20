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
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.NEPFormula;
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
	public static final Formula ZERO = new NumberFormula(0);

	/**
	 * A Formula for the integer constant ONE. This is done in order to minimize
	 * memory usage in the many cases where a default Formula of ONE is
	 * required.
	 */
	public static final Formula ONE = new NumberFormula(1);

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
		if (formulaString == null || formulaString.isEmpty())
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
	private static final class NumberFormula implements Formula
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
		private NumberFormula(Number intValue)
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
	private static final class SimpleFormula<T> implements NEPFormula<T>
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
		private SimpleFormula(T val)
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
		 */
		@Override
		public boolean equals(Object obj)
		{
			return (obj instanceof SimpleFormula)
				&& ((SimpleFormula<?>) obj).value.equals(value);
		}

		@Override
		public void getDependencies(DependencyManager fdm)
		{
			//None
		}

		@Override
		public T resolve(EvaluationManager evalManager)
		{
			return value;
		}

		@Override
		public void isValid(FormatManager<T> formatManager,
			FormulaSemantics semantics)
		{
			Class<?> expectedFormat = formatManager.getManagedClass();
			if (!expectedFormat.isAssignableFrom(value.getClass()))
			{
				semantics.setInvalid("Parse Error: Invalid Value Format: "
					+ value.getClass() + " found in location requiring a "
					+ expectedFormat + " (class cannot be evaluated)");
			}
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
		if (expression == null || expression.isEmpty())
		{
			throw new IllegalArgumentException("Formula cannot be empty");
		}
		try
		{
			return new SimpleFormula<>(fmtManager.convert(expression));
		}
		catch (IllegalArgumentException e)
		{
			// Okay, not simple :P
			return new ComplexNEPFormula<>(expression);
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
	 * @param managerFactory
	 *            The ManagerFactory to be used for building the FormulaSemantics
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
		ManagerFactory managerFactory, FormulaManager formulaManager, LegalScope varScope,
		FormatManager<T> formatManager)
	{
		NEPFormula<T> formula = getNEPFormulaFor(formatManager, expression);
		FormulaSemantics semantics = managerFactory.generateFormulaSemantics(
			formulaManager, varScope, formatManager.getManagedClass());
		formula.isValid(formatManager, semantics);
		if (!semantics.isValid())
		{
			throw new IllegalArgumentException("Cannot create a Formula from: "
				+ expression + ", due to: " + semantics.getReport()
				+ " with format " + formatManager.getIdentifierType());
		}
		return formula;
	}
}
