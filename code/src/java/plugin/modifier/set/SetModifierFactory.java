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

import pcgen.base.calculation.AbstractPCGenModifier;
import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.rules.persistence.token.AbstractFixedSetModifierFactory;

/**
 * A SetModifierFactory is a ModifierFactory that returns a specific value
 * (independent of the input) when a Modifier produced by this
 * SetModifierFactory is processed.
 * 
 * @param <T>
 *            The Class of object contained in the arrays processed by this
 *            SetModifierFactory
 */
public class SetModifierFactory<T> extends AbstractFixedSetModifierFactory<T[]>
{

	@SuppressWarnings("rawtypes")
	private static final Class ARRAY_CLASS = Object[].class;

	@Override
	public String getIdentification()
	{
		return "SET";
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<T[]> getVariableFormat()
	{
		return ARRAY_CLASS;
	}

	@Override
	public PCGenModifier<T[]> getModifier(String instructions,
		ManagerFactory managerFactory, FormulaManager ignored, LegalScope varScope,
		FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new SetIndirectArrayModifier(formatManager, indirect);
	}

	@Override
	public PCGenModifier<T[]> getFixedModifier(
		FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toSet = fmtManager.convert(instructions);
		return new SetDirectArrayModifier(fmtManager, toSet);
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
			T[] toSet)
		{
			super(formatManager);
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
			Indirect<T[]> toSet)
		{
			super(formatManager);
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
	abstract class SetArrayModifier extends AbstractPCGenModifier<T[]>
	{

		private final FormatManager<T[]> fmtManager;

		SetArrayModifier(FormatManager<T[]> formatManager)
		{
			this.fmtManager = formatManager;
		}

		@Override
		public long getPriority()
		{
			return ((long) getUserPriority() << 32);
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
		public FormatManager<T[]> getVariableFormat()
		{
			return fmtManager;
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
