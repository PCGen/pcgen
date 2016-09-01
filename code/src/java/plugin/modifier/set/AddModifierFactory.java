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
import java.util.HashSet;
import java.util.Set;

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.solver.Modifier;
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
	private static final Class ARRAY_CLASS = new Object[0].getClass();

	/**
	 * Identifies that this AddModifier acts upon java.util.Set objects.
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<T[]> getVariableFormat()
	{
		return ARRAY_CLASS;
	}

	/**
	 * Returns an Identifier for this type of Modifier
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentification()
	{
		return "ADD";
	}

	/**
	 * @see pcgen.rules.persistence.token.ModifierFactory#getModifier(int,
	 *      java.lang.String, pcgen.base.formula.manager.FormulaManager,
	 *      pcgen.base.formula.base.LegalScope, pcgen.base.format.FormatManager)
	 */
	@Override
	public PCGenModifier<T[]> getModifier(int userPriority, String instructions,
		FormulaManager ignored, LegalScope varScope,
		FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new AddIndirectArrayModifier(formatManager, userPriority,
			indirect);
	}

	@Override
	public Modifier<T[]> getFixedModifier(int userPriority,
		FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toAdd = fmtManager.convert(instructions);
		return new AddDirectArrayModifier(fmtManager, userPriority, toAdd);
	}

	public class AddDirectArrayModifier extends AddArrayModifier
	{
		/**
		 * The objects to be added to the active set when this AddModifier is
		 * processed
		 */
		private T[] toAdd;

		public AddDirectArrayModifier(FormatManager<T[]> formatManager,
			int userPriority, T[] toAdd)
		{
			super(formatManager, userPriority);
			this.toAdd = toAdd;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getInstructions()
		{
			return getFormatManager().unconvert(toAdd);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected T[] getArray()
		{
			return toAdd;
		}

	}

	public class AddIndirectArrayModifier extends AddArrayModifier
	{
		/**
		 * The objects to be added to the active set when this AddModifier is
		 * processed
		 */
		private Indirect<T[]> toAdd;

		public AddIndirectArrayModifier(FormatManager<T[]> formatManager,
			int userPriority, Indirect<T[]> toAdd)
		{
			super(formatManager, userPriority);
			this.toAdd = toAdd;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getInstructions()
		{
			return toAdd.getUnconverted();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected T[] getArray()
		{
			return toAdd.get();
		}

	}

	/**
	 * The Modifier that implements ADD for Set objects
	 */
	public abstract class AddArrayModifier implements PCGenModifier<T[]>
	{

		/**
		 * The user priority of this AddModifier
		 */
		private final int userPriority;

		private final FormatManager<T[]> fmtManager;

		public AddArrayModifier(FormatManager<T[]> formatManager,
			int userPriority)
		{
			this.fmtManager = formatManager;
			this.userPriority = userPriority;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getUserPriority()
		{
			return userPriority;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getPriority()
		{
			return (userPriority << 32) + 3;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T[] process(EvaluationManager evalManager)
		{
			@SuppressWarnings("unchecked")
			T[] input = (T[]) evalManager.peek(EvaluationManager.INPUT);
			Set<T> newSet = new HashSet<>();
			for (T o : input)
			{
				newSet.add(o);
			}
			for (T o : getArray())
			{
				newSet.add(o);
			}
			Class<?> component =
					fmtManager.getManagedClass().getComponentType();
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Class<T[]> getVariableFormat()
		{
			return fmtManager.getManagedClass();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
		public void getDependencies(DependencyManager fdm)
		{
		}

		/**
		 * {@inheritDoc}
		 */
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
