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
package plugin.modifier.number;

import pcgen.base.lang.NumberUtilities;
import pcgen.rules.persistence.token.AbstractNumberModifierFactory;

/**
 * An MaxModifierFactory is a {@code ModifierFactory<Number>} that returns the lower of
 * the input or a defined maximum value (provided during construction of this
 * MaxModifierFactory) when a Modifier produced by this MaxModifierFactory is
 * processed.
 */
public class MaxModifierFactory extends AbstractNumberModifierFactory<Number>
{

	/**
	 * Identifies that the Modifier objects built by this MaxModifierFactory act
	 * upon java.lang.Number objects.
	 * 
	 * @see pcgen.base.calculation.CalculationInfo#getVariableFormat()
	 */
	@Override
	public Class<Number> getVariableFormat()
	{
		return Number.class;
	}

	/**
	 * Returns the higher of the two input values, used by Modifiers produced by
	 * this MaxModifierFactory
	 * 
	 * @see pcgen.base.calculation.BasicCalculation#process(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public Number process(Number previousValue, Number argument)
	{
		return NumberUtilities.max(previousValue, argument);
	}

	/**
	 * Returns the inherent priority of an MaxModifier. This is used if two
	 * Modifiers have the same User Priority. Lower values are processed first.
	 * 
	 * @see pcgen.base.calculation.CalculationInfo#getInherentPriority()
	 */
	@Override
	public int getInherentPriority()
	{
		return 4;
	}

	/**
	 * Returns an Identifier for this type of Modifier
	 * 
	 * @see pcgen.base.calculation.CalculationInfo#getIdentification()
	 */
	@Override
	public String getIdentification()
	{
		return "MAX";
	}
}
