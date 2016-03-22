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

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.rules.persistence.token.AbstractSetModifierFactory;

/**
 * A SetModifierFactory is a ModifierFactory that returns a specific value
 * (independent of the input) when a Modifier produced by this
 * SetModifierFactory is processed.
 * 
 * @param <T>
 *            The Class of object contained in the arrays processed by this
 *            SetModifierFactory
 */
public class SetModifierFactory<T> extends AbstractSetModifierFactory<T[]>
{

	@SuppressWarnings("rawtypes")
	private static final Class ARRAY_CLASS = new Object[0].getClass();

	/**
	 * @see pcgen.rules.persistence.token.ModifierFactory#getIdentification()
	 */
	@Override
	public String getIdentification()
	{
		return "SET";
	}

	/**
	 * @see pcgen.rules.persistence.token.ModifierFactory#getVariableFormat()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<T[]> getVariableFormat()
	{
		return ARRAY_CLASS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Modifier<T[]> getModifier(int userPriority, String instructions,
		FormulaManager ignored, LegalScope varScope,
		FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new SetIndirectArrayModifier(formatManager, userPriority,
			indirect);
	}

	@Override
	public Modifier<T[]> getFixedModifier(int userPriority,
		FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toSet = fmtManager.convert(instructions);
		return new SetDirectArrayModifier(fmtManager, userPriority, toSet);
	}

	public class SetDirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private T[] toSet;

		public SetDirectArrayModifier(FormatManager<T[]> formatManager,
			int userPriority, T[] toSet)
		{
			super(formatManager, userPriority);
			this.toSet = toSet;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getInstructions()
		{
			return getFormatManager().unconvert(toSet);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected T[] getArray()
		{
			return toSet;
		}

	}

	public class SetIndirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private Indirect<T[]> toSet;

		public SetIndirectArrayModifier(FormatManager<T[]> formatManager,
			int userPriority, Indirect<T[]> toSet)
		{
			super(formatManager, userPriority);
			this.toSet = toSet;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getInstructions()
		{
			return toSet.getUnconverted();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected T[] getArray()
		{
			return toSet.get();
		}

	}

	/**
	 * The Modifier that implements SET for Set objects
	 */
	public abstract class SetArrayModifier implements Modifier<T[]>
	{

		/**
		 * The user priority of this SetModifier
		 */
		private final int userPriority;

		private final FormatManager<T[]> fmtManager;

		public SetArrayModifier(FormatManager<T[]> formatManager,
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
		@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
		public int getInherentPriority()
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T[] process(T[] input, ScopeInformation scopeInfo, Object source)
		{
			return getArray();
		}

		/**
		 * Returns the array to be set during process().
		 * 
		 * @return The array to be set during process()
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
		public void getDependencies(ScopeInformation scopeInfo,
			DependencyManager fdm, Class<?> assertedFormat)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getIdentification()
		{
			return SetModifierFactory.this.getIdentification();
		}

		/**
		 * Returns the FormatManager for this SetArrayModifier.
		 * 
		 * @return The FormatManager for this SetArrayModifier
		 */
		public FormatManager<T[]> getFormatManager()
		{
			return fmtManager;
		}

	}
}
