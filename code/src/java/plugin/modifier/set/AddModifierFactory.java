/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.modifier.set;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pcgen.base.calculation.AbstractPCGenModifier;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.rules.persistence.token.ModifierFactory;

/**
 * An AddModifierFactory is a ModifierFactory that adds a specific set of items
 * to the input set when a Modifier produced by this AddModifierFactory is
 * processed.
 * 
 * @param <T>
 *            The Class of object contained in the arrays processed by this
 *            AddModifierFactory
 */
public class AddModifierFactory<T> implements ModifierFactory<T[]>
{

	@SuppressWarnings("rawtypes")
	private static final Class ARRAY_CLASS = Object[].class;

	@SuppressWarnings("unchecked")
	@Override
	public Class<T[]> getVariableFormat()
	{
		return ARRAY_CLASS;
	}

	@Override
	public String getIdentification()
	{
		return "ADD";
	}

	@Override
	public FormulaModifier<T[]> getModifier(String instructions, FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new AddIndirectArrayModifier(formatManager, indirect);
	}

	@Override
	public FormulaModifier<T[]> getFixedModifier(FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toAdd = fmtManager.convert(instructions);
		return new AddDirectArrayModifier(fmtManager, toAdd);
	}

	/**
	 * An AddDirectArrayModifier is a FormulaModifier that contains a set of objects to be
	 * used by the Modifier when executed.
	 */
	private final class AddDirectArrayModifier extends AddArrayModifier
	{
		/**
		 * The objects to be added to the active set when this AddModifier is
		 * processed
		 */
		private T[] toAdd;

		private AddDirectArrayModifier(FormatManager<T[]> formatManager, T[] toAdd)
		{
			super(formatManager);
			this.toAdd = toAdd;
		}

		@Override
		public String getInstructions()
		{
			return getFormatManager().unconvert(toAdd);
		}

		@Override
		protected T[] getArray()
		{
			return toAdd;
		}

		@Override
		public void getDependencies(DependencyManager fdm)
		{
			//Since this already knows the toSet objects, it has no dependencies
		}

		@Override
		public void isValid(FormulaSemantics semantics)
		{
			/*
			 * Since this is direct (already has the object), it has no semantic issues
			 * (barring someone violating Generics)
			 */
		}
	}

	/**
	 * An AddIndirectArrayModifier is a FormulaModifier that contains a set of Indirect
	 * objects to be resolved and used by the Modifier when executed.
	 */
	private final class AddIndirectArrayModifier extends AddArrayModifier
	{
		/**
		 * The objects to be added to the active set when this AddModifier is
		 * processed
		 */
		private Indirect<T[]> toAdd;

		private AddIndirectArrayModifier(FormatManager<T[]> formatManager, Indirect<T[]> toAdd)
		{
			super(formatManager);
			this.toAdd = toAdd;
		}

		@Override
		public String getInstructions()
		{
			return toAdd.getUnconverted();
		}

		@Override
		protected T[] getArray()
		{
			return toAdd.get();
		}

		@Override
		public void getDependencies(DependencyManager fdm)
		{
			//CONSIDER: How does DependencyManager want to know about Indirect?
		}

		@Override
		public void isValid(FormulaSemantics semantics)
		{
			/*
			 * Since this is direct (already has a reference to the object), it has no
			 * semantic issues (barring someone violating Generics)
			 */
		}
	}

	/**
	 * The Modifier that implements ADD for Set objects
	 */
	private abstract class AddArrayModifier extends AbstractPCGenModifier<T[]>
	{

		private final FormatManager<T[]> fmtManager;

		AddArrayModifier(FormatManager<T[]> formatManager)
		{
			this.fmtManager = formatManager;
		}

		@Override
		public long getPriority()
		{
			return ((long) getUserPriority() << 32) + 3;
		}

		@Override
		public T[] process(EvaluationManager evalManager)
		{
			@SuppressWarnings("unchecked")
			T[] input = (T[]) evalManager.get(EvaluationManager.INPUT);
			Set<T> newSet = new HashSet<>();
			Collections.addAll(newSet, input);
			Collections.addAll(newSet, getArray());
			Class<?> component = fmtManager.getManagedClass().getComponentType();
			@SuppressWarnings("unchecked")
			T[] newArray = (T[]) Array.newInstance(component, newSet.size());
			return newSet.toArray(newArray);
		}

		/**
		 * Returns the array to be added during process().
		 * 
		 * @return The array to be added during process()
		 */
		protected abstract T[] getArray();

		@Override
		public FormatManager<T[]> getVariableFormat()
		{
			return fmtManager;
		}

		@Override
		public String getIdentification()
		{
			return AddModifierFactory.this.getIdentification();
		}

		/**
		 * Returns the FormatManager for this AddArrayModifier.
		 * 
		 * @return The FormatManager for this AddArrayModifier
		 */
		public FormatManager<T[]> getFormatManager()
		{
			return fmtManager;
		}
	}
}
