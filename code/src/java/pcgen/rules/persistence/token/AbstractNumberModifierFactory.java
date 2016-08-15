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
package pcgen.rules.persistence.token;

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaCalculation;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ProcessCalculation;

public abstract class AbstractNumberModifierFactory<T> implements
		ModifierFactory<T>, BasicCalculation<T>
{

	/**
	 * @see pcgen.rules.persistence.token.ModifierFactory#getModifier(int,
	 *      java.lang.String, pcgen.base.formula.manager.FormulaManager,
	 *      pcgen.base.formula.base.LegalScope, pcgen.base.format.FormatManager)
	 */
	@Override
	public PCGenModifier<T> getModifier(int userPriority, String instructions,
		FormulaManager formulaManager, LegalScope varScope,
		FormatManager<T> formatManager)
	{
		try
		{
			return getFixedModifier(userPriority, formatManager, instructions);
		}
		catch (NumberFormatException e)
		{
			final NEPFormula<T> f =
					FormulaFactory.getValidFormula(instructions,
						formulaManager, varScope, formatManager);
			NEPCalculation<T> calc = new FormulaCalculation<T>(f, this);
			return new CalculationModifier<T>(calc, userPriority);
		}
	}

	@Override
	public PCGenModifier<T> getFixedModifier(int userPriority,
		FormatManager<T> fmtManager, String instructions)
	{
		T n = fmtManager.convert(instructions);
		NEPCalculation<T> calc = new ProcessCalculation<T>(n, this, fmtManager);
		return new CalculationModifier<T>(calc, userPriority);
	}
}
