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

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
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
	private static final Class ARRAY_CLASS = Object[].class;

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

	@Override
	public PCGenModifier<T[]> getModifier(int userPriority, String instructions,
		ManagerFactory managerFactory, FormulaManager ignored, LegalScope varScope,
		FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new SetIndirectArrayModifier(formatManager, userPriority,
			indirect);
	}

	@Override
	public PCGenModifier<T[]> getFixedModifier(int userPriority,
		FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toSet = fmtManager.convert(instructions);
		return new SetDirectArrayModifier(fmtManager, userPriority, toSet);
	}

	/**
	 * A SetDirectArrayModifier is a PCGenModifier that contains a set of objects 
	 * to be used by the Modifier.
	 */
	private final class SetDirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private T[] toSet;

		private SetDirectArrayModifier(FormatManager<T[]> formatManager,
		                               int userPriority, T[] toSet)
		{
			super(formatManager, userPriority);
			this.toSet = toSet;
		}

		@Override
		public String getInstructions()
		{
			return getFormatManager().unconvert(toSet);
		}

		@Override
		protected T[] getArray()
		{
			return toSet;
		}

	}

	/**
	 * A SetIndirectArrayModifier is a PCGenModifier that contains a set of Indirect objects
	 * to be resolved and used by the Modifier when executed.
	 */
	private final class SetIndirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private Indirect<T[]> toSet;

		private SetIndirectArrayModifier(FormatManager<T[]> formatManager,
			int userPriority, Indirect<T[]> toSet)
		{
			super(formatManager, userPriority);
			this.toSet = toSet;
		}

		@Override
		public String getInstructions()
		{
			return toSet.getUnconverted();
		}

		@Override
		protected T[] getArray()
		{
			return toSet.get();
		}

	}

	/**
	 * The Modifier that implements SET for Set objects
	 */
	abstract class SetArrayModifier implements PCGenModifier<T[]>
	{

		/**
		 * The user priority of this SetModifier
		 */
		private final int userPriority;

		private final FormatManager<T[]> fmtManager;

		SetArrayModifier(FormatManager<T[]> formatManager,
		                 int userPriority)
		{
			this.fmtManager = formatManager;
			this.userPriority = userPriority;
		}

		@Override
		public int getUserPriority()
		{
			return userPriority;
		}

		@Override
		public long getPriority()
		{
			return ((long) userPriority << 32);
		}

		@Override
		public T[] process(EvaluationManager evalManager)
		{
			return getArray();
		}

		/**
		 * Returns the array to be set during process().
		 * 
		 * @return The array to be set during process()
		 */
		protected abstract T[] getArray();

		@Override
		public Class<T[]> getVariableFormat()
		{
			return fmtManager.getManagedClass();
		}

		@Override
		@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
		public void getDependencies(DependencyManager fdm)
		{
		}

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
