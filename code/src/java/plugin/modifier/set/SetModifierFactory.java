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
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
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
	@SuppressWarnings("unchecked")
	public Class<T[]> getVariableFormat()
	{
		return ARRAY_CLASS;
	}

	@Override
	public FormulaModifier<T[]> getModifier(String instructions, FormatManager<T[]> formatManager)
	{
		Indirect<T[]> indirect = formatManager.convertIndirect(instructions);
		return new SetIndirectArrayModifier(formatManager, indirect);
	}

	@Override
	public FormulaModifier<T[]> getFixedModifier(FormatManager<T[]> fmtManager, String instructions)
	{
		T[] toSet = fmtManager.convert(instructions);
		return new SetDirectArrayModifier(fmtManager, toSet);
	}

	/**
	 * A SetDirectArrayModifier is a FormulaModifier that contains a set of objects 
	 * to be used by the Modifier.
	 */
	private final class SetDirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private T[] toSet;

		private SetDirectArrayModifier(FormatManager<T[]> formatManager, T[] toSet)
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

		@Override
		public void getDependencies(DependencyManager fdm)
		{
			//Since this already knows the toSet objects, it has no dependencies
		}

		@Override
		public void isValid(FormulaSemantics semantics) {
			/*
			 * Since this is direct (already has the object), it has no semantic issues
			 * (barring someone violating Generics)
			 */
		}
	}

	/**
	 * A SetIndirectArrayModifier is a FormulaModifier that contains a set of Indirect objects
	 * to be resolved and used by the Modifier when executed.
	 */
	private final class SetIndirectArrayModifier extends SetArrayModifier
	{
		/**
		 * The objects to be set to the active set when this SetModifier is
		 * processed
		 */
		private Indirect<T[]> toSet;

		private SetIndirectArrayModifier(FormatManager<T[]> formatManager, Indirect<T[]> toSet)
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

		@Override
		public void getDependencies(DependencyManager fdm)
		{
			//CONSIDER: How does DependencyManager want to know about Indirect?
		}

		@Override
		public void isValid(FormulaSemantics semantics) {
			/*
			 * Since this is direct (already has a reference to the object), it has no
			 * semantic issues (barring someone violating Generics)
			 */
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
