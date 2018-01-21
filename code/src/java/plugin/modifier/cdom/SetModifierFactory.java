/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.modifier.cdom;

import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.IndirectCalculation;
import pcgen.rules.persistence.token.AbstractSetModifierFactory;

/**
 * An SetModifier is a {@code Modifier<CDOMObject>} that returns a specific value
 * (independent of the input) when the Modifier is processed.
 */
public class SetModifierFactory extends AbstractSetModifierFactory<CDOMObject>
{

	@Override
	public PCGenModifier<CDOMObject> getModifier(String instructions,
		ManagerFactory managerFactory, FormulaManager ignored, LegalScope varScope,
		FormatManager<CDOMObject> formatManager)
	{
		if (!getVariableFormat().isAssignableFrom(formatManager.getManagedClass()))
		{
			throw new IllegalArgumentException("FormatManager must manage "
				+ getVariableFormat().getName() + " or a child of that class");
		}
		Indirect<CDOMObject> n = formatManager.convertIndirect(instructions);
		NEPCalculation<CDOMObject> calc = new IndirectCalculation<>(n, this);
		return new CalculationModifier<>(calc, formatManager);
	}

	/**
	 * Identifies that this SetModifier acts upon CDOMObject objects.
	 * 
	 * @see pcgen.base.calculation.CalculationInfo#getVariableFormat()
	 */
	@Override
	public Class<CDOMObject> getVariableFormat()
	{
		return CDOMObject.class;
	}
}
