/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula.testsupport;

import java.lang.reflect.Array;

import pcgen.TestConstants;
import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaCalculation;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.lang.NumberUtilities;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

public abstract class AbstractModifier<T> implements Modifier<T>
{

	private final FormatManager<T> format;
	private final int priority;
	private final int inherent;

	public AbstractModifier(int inherent, FormatManager<T> cl)
	{
		this(inherent, cl, 100);
	}

	public AbstractModifier(int inherent, FormatManager<T> cl, int priority)
	{
		format = cl;
		this.priority = priority;
		this.inherent = inherent;
	}

	@Override
	public void getDependencies(DependencyManager fdm)
	{
	}

	@Override
	public String getIdentification()
	{
		return "Set";
	}

	@Override
	public FormatManager<T> getVariableFormat()
	{
		return format;
	}

	@Override
	public long getPriority()
	{
		return ((long) priority << 32) + inherent;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbstractModifier)
		{
			AbstractModifier<?> am = (AbstractModifier<?>) o;
			return format.equals(am.format) && inherent == am.inherent
				&& priority == am.priority;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return format.hashCode() + (31 * inherent) + (31 * priority);
	}

	public static FormulaModifier<Number> getAddModifier(String modInstructions,
		FormatManager<Number> formatManager)
	{
		NEPFormula<Number> f =
				new ComplexNEPFormula<>(modInstructions, formatManager);
		NEPCalculation<Number> calc = new FormulaCalculation<>(f,
			new plugin.modifier.number.AddModifierFactory());
		return new CalculationModifier<>(calc, formatManager);
	}

	public static AbstractModifier<Number[]> addToArray(final int value,
		int priority)
	{
		return new AbstractModifier<Number[]>(0, TestConstants.NUMBER_ARR_FORMAT, priority)
		{
			@Override
			public Number[] process(EvaluationManager manager)
			{
				Number[] input = (Number[]) manager.get(EvaluationManager.INPUT);
				Number[] newArray =
						(Number[]) Array.newInstance(FormatUtilities.NUMBER_MANAGER.getManagedClass(),
							input.length + 1);
				System.arraycopy(input, 0, newArray, 0, input.length);
				newArray[newArray.length - 1] = value;
				return newArray;
			}

			@Override
			public String getInstructions()
			{
				return "append " + Integer.toString(value);
			}
		};
	}

	public static AbstractModifier<Number[]> setEmptyArray(int priority)
	{
		return new AbstractModifier<Number[]>(0, TestConstants.NUMBER_ARR_FORMAT, priority)
		{
			@Override
			public Number[] process(EvaluationManager manager)
			{
				return new Number[]{};
			}

			@Override
			public String getInstructions()
			{
				return "[]";
			}
		};
	}

	public static AbstractModifier<Number> setNumber(final int value,
		int priority)
	{
		return new PrivateSetNumber(0, FormatUtilities.NUMBER_MANAGER, priority, value);
	}

	public static AbstractModifier<String> setString(String s)
	{
		return new AbstractModifier<String>(0, FormatUtilities.STRING_MANAGER)
		{
			@Override
			public String process(EvaluationManager manager)
			{
				return s;
			}

			@Override
			public String getInstructions()
			{
				return s;
			}
		};
	}

	public static AbstractModifier<Number> multiply(final int value,
		int priority)
	{
		return new AbstractModifier<Number>(1, FormatUtilities.NUMBER_MANAGER, priority)
		{
			@Override
			public Number process(EvaluationManager manager)
			{
				return NumberUtilities.multiply((Number) manager.get(EvaluationManager.INPUT), value);
			}

			@Override
			public String getInstructions()
			{
				return "*" + Integer.toString(value);
			}
		};
	}

	public static AbstractModifier<Number> add(final int value, int priority)
	{
		return new AbstractModifier<Number>(2, FormatUtilities.NUMBER_MANAGER, priority)
		{
			@Override
			public Number process(EvaluationManager manager)
			{
				return NumberUtilities.add((Number) manager.get(EvaluationManager.INPUT), value);
			}

			@Override
			public String getInstructions()
			{
				return "+" + Integer.toString(value);
			}
		};
	}

	public static AbstractModifier<Number> add(final ComplexNEPFormula<?> value, int priority)
	{
		return new AbstractModifier<Number>(2, FormatUtilities.NUMBER_MANAGER, priority)
		{
			@Override
			public Number process(EvaluationManager manager)
			{
				Number result = (Number) value.resolve(manager);
				return NumberUtilities.add((Number) manager.get(EvaluationManager.INPUT), result);
			}

			@Override
			public void getDependencies(DependencyManager fdm)
			{
				value.getDependencies(fdm);
			}

			@Override
			public String getInstructions()
			{
				return "*" + value;
			}
		};
	}

	public static <T> AbstractModifier<T> setObject(FormatManager<T> fmt, final T value, int priority)
	{
		return new AbstractModifier<T>(2, fmt, priority)
		{
			@Override
			public T process(EvaluationManager manager)
			{
				return value;
			}

			@Override
			public String getInstructions()
			{
				return value.toString();
			}
		};
	}

	private static final class PrivateSetNumber extends AbstractModifier<Number>
	{
		private final int value;

		private PrivateSetNumber(int inherent, FormatManager<Number> cl, int priority,
			int value)
		{
			super(inherent, cl, priority);
			this.value = value;
		}

		@Override
		public Number process(EvaluationManager manager)
		{
			return value;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof PrivateSetNumber)
			{
				PrivateSetNumber psn = (PrivateSetNumber) o;
				return super.equals(o) && (psn.value == value);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode() + (31 * value);
		}

		@Override
		public String getInstructions()
		{
			return Integer.toString(value);
		}
	}
}
